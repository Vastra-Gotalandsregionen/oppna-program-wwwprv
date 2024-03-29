package se.vgregion.portal.wwwprv.service;

import jcifs.CIFSContext;
import jcifs.context.SingletonContext;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileFilter;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;
import se.vgregion.portal.wwwprv.model.Node;
import se.vgregion.portal.wwwprv.model.jpa.Supplier;
import se.vgregion.portal.wwwprv.util.Callback;
import se.vgregion.portal.wwwprv.util.Notifiable;

import javax.annotation.PreDestroy;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Patrik Bergström
 */
@Service
public class RemoteFileAccessService implements FileAccessService {

    public static final String namndFordeladFileNameSuffix = "_Namndfordelad.csv";
    public static final String folderPrefix = "Vardgiv_";

    private final int depthLimit = 1;

    @Value("${shared.folder.username}")
    private String user;

    @Value("${shared.folder.password}")
    private String password;

    @Value("${shared.folder.domain}")
    private String domain;

    @Autowired
    private ExtendedPopulationService extendedPopulationService;

    @Autowired
    private FullPopulationService fullPopulationService;

    @Autowired
    private EmailService emailService;

    static {
        System.setProperty("jcifs.encoding", "ISO-8859-1");
//        System.setProperty("jcifs.smb.client.minVersion", "2.1");
//        System.setProperty("jcifs.smb.client.enableSMB2", "true");
    }

    private ExecutorService backgroundExecutor = Executors.newFixedThreadPool(20, new CustomizableThreadFactory() {
        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = super.newThread(runnable);

            thread.setDaemon(true);

            return thread;
        }
    });

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteFileAccessService.class);

    public RemoteFileAccessService() {
    }

    public RemoteFileAccessService(String user, String password, String domain) {
        this.user = user;
        this.password = password;
        this.domain = domain;
    }

    @PreDestroy
    public void shutdown() {
        backgroundExecutor.shutdown();
    }

    @Override
    public Future<?> uploadFileInBackground(final String fileNameBase,
                                            final Supplier supplier,
                                            final InputStream inputStreamSource,
                                            final long fileSize,
                                            final String namndFordelningDirectory,
                                            final String uploader,
                                            final Notifiable notifiable,
                                            final Callback callback) {

        Future<?> submit = backgroundExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    uploadFile(fileNameBase, supplier, inputStreamSource, fileSize, namndFordelningDirectory, notifiable);
                    emailService.notifyNewUpload(fileNameBase, supplier, uploader);
                    callback.callback();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    emailService.notifyError(e);
                }
            }
        });

        // Since we run this as a background job the user is notified all is done pretty much immediately.
        notifiable.notifyPercentage(100);

        return submit;
    }

    private void uploadFile(String fileNameBase, Supplier supplier, InputStream inputStreamSource, long fileSize, String namndFordelningDirectory, Notifiable notifiable) throws DistrictDistributionException {
        List<String> uploadFoldersInOrder = new ArrayList<>(supplier.getUploadFolders());
        for (String uploadFolder : supplier.getUploadFolders()) {
            if (uploadFolder.equals(namndFordelningDirectory)) {
                // Remove and insert first.
                uploadFoldersInOrder.remove(uploadFolder);
                uploadFoldersInOrder.add(0, uploadFolder);
            }
        }

        try {
            byte[] fileContent = IOUtils.toByteArray(inputStreamSource);

            for (String uploadFolder : uploadFoldersInOrder) {
                // We set this for every iteration.
                String fileName = fileNameBase;

                CIFSContext cifsContext = SingletonContext.getInstance();

                NtlmPasswordAuthentication auth = getAuth(cifsContext);

                cifsContext = cifsContext.withCredentials(auth);

                SmbFile dir = new SmbFile("smb://" + uploadFolder, cifsContext);

                // Create subfolder if not existent
                String subDirName = folderPrefix + supplier.getEnhetsKod();
                SmbFile subDir = new SmbFile(dir, subDirName);

                if (!subDir.exists()) {
                    subDir.mkdirs();
                    LOGGER.info("Created directory: " + subDir.getCanonicalPath());
                }

                InputStream toUpload;
                if (uploadFolder.equals(namndFordelningDirectory)) {
                    fileName = complementFileNameWithNamndfordelningPart(fileName);
                    DistrictDistribution districtDistribution = getDistrictDistribution(fileName, supplier);
                    String charsetName = guessCharacterEncoding(fileContent);
                    String input = new String(fileContent, charsetName);
                    String processed = districtDistribution.process(input);
                    ByteArrayInputStream bais = new ByteArrayInputStream(processed.getBytes(charsetName));
                    toUpload = bais;
                } else {
                    toUpload = new ByteArrayInputStream(fileContent);
                }

                LOGGER.info("subDir = " + subDir.getCanonicalPath());

                SmbFile newFile = new SmbFile(dir, subDirName + "/" + fileName);

                LOGGER.info("newFile = " + newFile.getCanonicalPath());

                newFile.createNewFile();

                LOGGER.info("Uploading to samba share: dir=" + subDir.getCanonicalPath() + ", fileName=" + fileName);

                upload(fileSize, notifiable, toUpload, newFile);

                if (uploadFolder.equals(namndFordelningDirectory)) {
                    // Also upload the original file content.

                    // Original file name here.
                    SmbFile newFile2 = new SmbFile(dir, subDirName + "/" + fileNameBase);
                    newFile2.createNewFile();

                    upload(fileSize, notifiable, new ByteArrayInputStream(fileContent), newFile2);
                }

            }

        } catch (IOException
                | ClassNotFoundException
                | NoSuchMethodException |
                IllegalAccessException |
                InstantiationException |
                InvocationTargetException e) {

            String message = "Failure. FileNameBase=" + fileNameBase + ", Supplier="
                    + supplier != null ? supplier.getEnhetsKod() : ". ";

            throw new RuntimeException(message + e.getMessage(), e);
        }
    }

    static String guessCharacterEncoding(byte[] fromRawBytes) throws UnsupportedEncodingException {
        String[] charFormats = {"ISO-8859-1", "UTF-8"};
        NavigableMap<Long, String> ranking = new TreeMap<>();
        for (String charFormat : charFormats) {
            long occurrence = new String(fromRawBytes, charFormat).chars().filter(
                    c -> c == 'å' || c == 'ä' || c == 'ö' || c == 'Å' || c == 'Ä' || c == 'Ö'
            ).count();
            ranking.put(occurrence, charFormat);
        }
        String charsetName = ranking.lastEntry().getValue();
        return charsetName;
    }

    private void upload(float fileSize, Notifiable notifiable, InputStream toUpload, SmbFile newFile) throws IOException {
        try (OutputStream outputStream = newFile.getOutputStream();
             BufferedOutputStream bos = new BufferedOutputStream(outputStream)) {

            byte[] buf = new byte[2048];

            int n;
            long accumulatedBytes = 0;
            int numberRoundsSoFar = 0;

            while ((n = toUpload.read(buf)) != -1) {
                accumulatedBytes += n;
                bos.write(buf, 0, n);

                if (++numberRoundsSoFar % 10 == 0) {
                    notifiable.notifyPercentage((int) ((100f * (float) accumulatedBytes) / fileSize));
                }
            }

            notifiable.notifyPercentage(100);

        }
    }

    static String complementFileNameWithNamndfordelningPart(String fileName) {
        if (fileName == null) {
            throw new NullPointerException("Filename must not be null.");
        }

        return fileName + namndFordeladFileNameSuffix;
    }

    private DistrictDistribution getDistrictDistribution(String fileName, Supplier supplier) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (supplier.getEnhetsNamn().equals("Evidia")) {
            return new EvidiaDistribution(fullPopulationService);
        } else {
            return new UnilabsLab(extendedPopulationService, fileName);
        }
    }

    private NtlmPasswordAuthentication getAuth(CIFSContext cifsContext) {
        return new NtlmPasswordAuthentication(
                cifsContext,
                this.domain,
                this.user,
                this.password
        );
    }

    @Override
    public Node<String> retrieveRemoteFileTree(String host) {

        String url = "smb://" + host;
        try {

            CIFSContext cifsContext = SingletonContext.getInstance();

            NtlmPasswordAuthentication auth = getAuth(cifsContext);

            cifsContext = cifsContext.withCredentials(auth);

            SmbFile smbRoot = new SmbFile(url, cifsContext);

            if (!smbRoot.isDirectory()) {
                throw new RuntimeException("Root is expected to be a directory.");
            }

            String[] split = url.split("/");
            Node<String> tree = new Node<>(split[split.length - 1] + "/");

            Node<String> root = tree;

            buildDirectoryTree(root, smbRoot, depthLimit, 0);

            return tree;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void buildDirectoryTree(Node<String> node, SmbFile smbRoot) {
        buildDirectoryTree(node, smbRoot, null, null);
    }

    private void buildDirectoryTree(Node<String> node, SmbFile smbRoot, Integer depthLimit, Integer currentDepth) {

        if ((currentDepth != null && depthLimit != null) && currentDepth >= depthLimit) {
            return;
        }

        SmbFile[] directories = null;
        try {
            directories = smbRoot.listFiles(new SmbFileFilter() {
                @Override
                public boolean accept(SmbFile file) throws SmbException {
                    return file.isDirectory();
                }
            });

            List<SmbFile> list = Arrays.asList(directories);

            Collections.sort(list, new Comparator<SmbFile>() {
                @Override
                public int compare(SmbFile o1, SmbFile o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });

            directories = list.toArray(new SmbFile[0]);
        } catch (SmbException e) {
            String message = e.getMessage();
            if (!("Access is denied.".equals(message) || "Invalid operation for IPC service".equals(message))) {
                LOGGER.error(message, e);
            }
        } catch (RuntimeException e) {
            String message = e.getMessage();
            LOGGER.error(message, e);
        }

        if (directories == null || directories.length == 0) {
            return;
        } else {
            for (SmbFile directory : directories) {
                Node<String> newNode = new Node<>(directory.getName());
                buildDirectoryTree(newNode, directory, depthLimit, currentDepth != null ? currentDepth + 1 : null);
                newNode.setParent(node);
                node.getChildren().add(newNode);
            }
        }
    }
}
