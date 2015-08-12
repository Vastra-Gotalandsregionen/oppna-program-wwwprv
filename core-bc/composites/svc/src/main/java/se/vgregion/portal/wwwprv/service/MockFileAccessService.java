package se.vgregion.portal.wwwprv.service;

import org.apache.commons.io.output.NullOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.vgregion.portal.wwwprv.model.jpa.Supplier;
import se.vgregion.portal.wwwprv.util.Notifiable;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Patrik Björk
 */
public class MockFileAccessService implements FileAccessService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockFileAccessService.class);

    @Override
    public void uploadFile(String fileName, Supplier supplier, InputStream inputStream, long fileSize, Notifiable notifiable) {

        LOGGER.info("Uploading using " + MockFileAccessService.class.getName());

        try (OutputStream os = new NullOutputStream();) {

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
    }

}
