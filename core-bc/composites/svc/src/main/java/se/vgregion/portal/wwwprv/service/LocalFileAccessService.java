package se.vgregion.portal.wwwprv.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import se.vgregion.portal.wwwprv.model.Node;
import se.vgregion.portal.wwwprv.model.jpa.Supplier;
import se.vgregion.portal.wwwprv.util.Callback;
import se.vgregion.portal.wwwprv.util.Notifiable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Patrik Björk
 */
public class LocalFileAccessService implements FileAccessService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalFileAccessService.class);

    @Value("${localStoragePath}")
    private String localStoragePath;

    private ExecutorService backgroundExecutor = Executors.newFixedThreadPool(20, new CustomizableThreadFactory() {
        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = super.newThread(runnable);

            thread.setDaemon(true);

            return thread;
        }
    });



    public LocalFileAccessService() {
        super();
    }

    @Override
    public Future<?> uploadFileInBackground(final String fileName, final Supplier supplier, final InputStream inputStream, final long fileSize,
                                            final String namndFordelningDirectory, final String uploader, final Notifiable notifiable,
                                            final Callback callback) {

        Future<?> submit = backgroundExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    LOGGER.info("Uploading using " + LocalFileAccessService.class.getName());
                    //final String save2dir = localStoragePath + File.separator;
                    final Path save2dir = Paths.get(localStoragePath);
                    //final String save2path = save2dir + fileName;
                    final Path save2path = Paths.get(localStoragePath + File.separator + fileName);
                    try (OutputStream os = new FileOutputStream(save2path.toString())) {
                        byte[] buf = new byte[4096];

                        int n;
                        long accumulatedBytes = 0;
                        int numberRoundsSoFar = 0;

                        while ((n = inputStream.read(buf)) != -1) {
                            accumulatedBytes += n;
                            os.write(buf, 0, n);

                            Thread.sleep(1);

                            if (++numberRoundsSoFar % 10 == 0) {
                                notifiable.notifyPercentage((int) ((100f * (float) accumulatedBytes) / (float) fileSize));
                            }
                        }

                        notifiable.notifyPercentage(100);
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    }

                    try {
                        Files.createDirectories(save2dir);
                        if (!Files.exists(save2path)) {
                            Files.createFile(save2path);
                        }
                        FileInputStream fis = new FileInputStream(save2path.toString());
                        FileOutputStream fos = new FileOutputStream(save2path + ".board-distributed");

                        // Do some distributions here....

                        //distributionService.runMakeDistributionFileContent(fis, fileName, supplier.getEnhetsKod(), fos);
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        throw new RuntimeException(e);
                    }

                    callback.callback();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        });

        // Since we run this as a background job the user is notified all is done pretty much immediately.
        notifiable.notifyPercentage(100);

        return submit;
    }

    @Override
    public Node<String> retrieveRemoteFileTree(String url) {
        return new MockFileAccessService().retrieveRemoteFileTree(url);
    }

    public String getLocalStoragePath() {
        return localStoragePath;
    }

    public void setLocalStoragePath(String localStoragePath) {
        this.localStoragePath = localStoragePath;
        File file = new File(localStoragePath);

        if (!file.exists()) {
            file.mkdirs();
        }
    }
}
