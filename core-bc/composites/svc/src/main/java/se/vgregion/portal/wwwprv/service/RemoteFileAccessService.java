package se.vgregion.portal.wwwprv.service;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.vgregion.portal.wwwprv.model.Node;
import se.vgregion.portal.wwwprv.model.jpa.Supplier;
import se.vgregion.portal.wwwprv.util.Notifiable;
import se.vgregion.portal.wwwprv.util.SharedUploadFolder;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Patrik Bergström
 */
@Service
public class RemoteFileAccessService implements FileAccessService {

    @Value("${mars.folder1.user}")
    private String user1;

    @Value("${mars.folder2.user}")
    private String user2;

    @Value("${mars.folder1.password}")
    private String password1;

    @Value("${mars.folder2.password}")
    private String password2;

    /*@Value("${mars.folder1.url}")
    private String url1;

    @Value("${mars.folder2.url}")
    private String url2;*/

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteFileAccessService.class);

    public RemoteFileAccessService() {
    }

    public RemoteFileAccessService(String user1, String user2, String password1, String password2, String url1, String url2) {
        this.user1 = user1;
        this.user2 = user2;
        this.password1 = password1;
        this.password2 = password2;
//        this.url1 = url1;
//        this.url2 = url2;
    }

    @Override
    public void uploadFile(String fileName, Supplier supplier, final InputStream inputStream, long fileSize, Notifiable notifiable) {

        /*String url;
        NtlmPasswordAuthentication auth;
        if (SharedUploadFolder.getSharedUploadFolder(supplier.getSharedUploadFolder()).equals(SharedUploadFolder.MARS_SHARED_FOLDER)) {
            url = url1;
            auth = new NtlmPasswordAuthentication("MARS", user1, password1);
        } else if (SharedUploadFolder.getSharedUploadFolder(supplier.getSharedUploadFolder()).equals(SharedUploadFolder.AVESINA_SHARED_FOLDER)) {
            url = url2;
            auth = new NtlmPasswordAuthentication("MARS", user2, password2);
        } else {
            throw new RuntimeException("Failed to decide which upload url to use.");
        }

        try {
            SmbFile dir = new SmbFile(url, auth);

            try {
                LOGGER.info("Uploading to samba share: dir=" + dir.getCanonicalPath() + ", fileName=" + fileName);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }

            SmbFile newFile = new SmbFile(dir, fileName);

            newFile.createNewFile();

            try (OutputStream outputStream = newFile.getOutputStream();
                 BufferedOutputStream bos = new BufferedOutputStream(outputStream)) {

                byte[] buf = new byte[2048];

                int n;
                long accumulatedBytes = 0;
                int numberRoundsSoFar = 0;

                while ((n = inputStream.read(buf)) != -1) {
                    accumulatedBytes += n;
                    bos.write(buf, 0, n);

                    if (++numberRoundsSoFar % 10 == 0) {
                        notifiable.notifyPercentage((int) ((100f * (float) accumulatedBytes) / (float) fileSize));
                    }
                }

                notifiable.notifyPercentage(100);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }

    @Override
    public Node<String> retrieveRemoteFileTree(String host) {

        String url = "smb://" + host;
        try {

            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("VGREGION", user1, password1);

            SmbFile smbRoot = new SmbFile(url, auth);

            if (!smbRoot.isDirectory()) {
                throw new RuntimeException("Root is expected to be a directory.");
            }

            String[] split = url.split("/");
            Node<String> tree = new Node<>(split[split.length - 1] + "/");

            Node<String> root = tree;

            buildDirectoryTree(root, smbRoot);

            return tree;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void buildDirectoryTree(Node<String> node, SmbFile smbRoot) {
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
            if (!"Access is denied.".equals(message)) {
                LOGGER.error(message, e);
            }
        }

        if (directories == null || directories.length == 0) {
            return;
        } else {
            for (SmbFile directory : directories) {
                Node<String> newNode = new Node<>(directory.getName());
                buildDirectoryTree(newNode, directory);
                newNode.setParent(node);
                node.getChildren().add(newNode);
            }
        }
    }
}
