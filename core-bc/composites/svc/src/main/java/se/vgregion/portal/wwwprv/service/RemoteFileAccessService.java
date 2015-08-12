package se.vgregion.portal.wwwprv.service;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.vgregion.portal.wwwprv.model.jpa.Supplier;
import se.vgregion.portal.wwwprv.util.Notifiable;
import se.vgregion.portal.wwwprv.util.SharedUploadFolder;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

    @Value("${mars.folder1.url}")
    private String url1;

    @Value("${mars.folder2.url}")
    private String url2;

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteFileAccessService.class);

    @Override
    public void uploadFile(String fileName, Supplier supplier, final InputStream inputStream, long fileSize, Notifiable notifiable) {

        String url;
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
        }
    }

}
