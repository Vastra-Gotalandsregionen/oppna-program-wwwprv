package se.vgregion.portal.wwwprv.service;

import org.apache.commons.io.output.NullOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import se.vgregion.portal.wwwprv.model.Node;
import se.vgregion.portal.wwwprv.model.jpa.Supplier;
import se.vgregion.portal.wwwprv.util.Callback;
import se.vgregion.portal.wwwprv.util.Notifiable;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Patrik Björk
 */
public class MockFileAccessService implements FileAccessService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockFileAccessService.class);

    private ExecutorService backgroundExecutor = Executors.newFixedThreadPool(20, new CustomizableThreadFactory() {
        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = super.newThread(runnable);

            thread.setDaemon(true);

            return thread;
        }
    });

    @Override
    public Future<?> uploadFileInBackground(String fileName, final Supplier supplier, final InputStream inputStream, final long fileSize,
                                            final String namndFordelningDirectory, final String uploader, final Notifiable notifiable,
                                            final Callback callback) {

        Future<?> submit = backgroundExecutor.submit(new Runnable() {
            @Override
            public void run() {
                LOGGER.info("Uploading using " + MockFileAccessService.class.getName());

                try (OutputStream os = new NullOutputStream();) {

                    byte[] buf = new byte[4096];

                    int n;
                    long accumulatedBytes = 0;
                    int numberRoundsSoFar = 0;

                    while ((n = inputStream.read(buf)) != -1) {
                        accumulatedBytes += n;
                        os.write(buf, 0, n);

                        Thread.sleep(500);

                        if (++numberRoundsSoFar % 10 == 0) {
                            notifiable.notifyPercentage((int) ((100f * (float) accumulatedBytes) / (float) fileSize));
                        }
                    }

                    notifiable.notifyPercentage(100);
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
    public Node<String> retrieveRemoteFileTree(String dummyUrl) {

        Node<String> level1 = new Node<>(dummyUrl);

        Node<String> dir1 = new Node<>("Mapp1/");
        level1.add(dir1);
        level1.add(new Node<String>("Mapp2/"));
        level1.add(new Node<String>("Mapp3/"));

        Node<String> dir12 = new Node<String>("mapp1-2/");
        List<Node<String>> level2 = dir1.getChildren();
        level2.add(dir12);
        level2.add(new Node<String>("mapp1-3/"));

        List<Node<String>> level3 = dir12.getChildren();
        level3.add(new Node<String>("mapp1-3-1/"));
        level3.add(new Node<String>("mapp1-3-2/"));

        return level1;
    }

}
