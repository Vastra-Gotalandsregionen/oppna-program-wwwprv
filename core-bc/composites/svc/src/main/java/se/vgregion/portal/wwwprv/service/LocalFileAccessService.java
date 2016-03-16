package se.vgregion.portal.wwwprv.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import se.vgregion.portal.wwwprv.model.Node;
import se.vgregion.portal.wwwprv.model.jpa.Supplier;
import se.vgregion.portal.wwwprv.util.Notifiable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Patrik Björk
 */
public class LocalFileAccessService implements FileAccessService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalFileAccessService.class);

    @Value("${localStoragePath}")
    private String localStoragePath;

    public LocalFileAccessService() {
        super();
    }

    @Override
    public void uploadFile(final String fileName, final Supplier supplier, InputStream inputStream, long fileSize,
                           String namndFordelningDirectory, Notifiable notifiable) {
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
