package se.vgregion.portal.wwwprv.service;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.vgregion.portal.wwwprv.model.jpa.Supplier;
import se.vgregion.portal.wwwprv.util.SharedUploadFolder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

/**
 * @author Patrik Bergström
 */
@Service
public class RemoteFileAccessService {

    @Value("${mars.folder1.user}")
    private String user1;

    @Value("${mars.folder2.user}")
    private String user2;

    @Value("${mars.folder1.password}")
    private String password1;

    @Value("${mars.folder2.password}")
    private String password2;

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteFileAccessService.class);

    public void uploadFile(String fileName, Supplier supplier, InputStream inputStream) {

        String url;
        NtlmPasswordAuthentication auth;
        if (SharedUploadFolder.getSharedUploadFolder(supplier.getSharedUploadFolder()).equals(SharedUploadFolder.MARS_SHARED_FOLDER)) {
            url = "smb://mars.vgregion.se/inbox/DATA_PRIVATA/Försystem/Infiler/";
            auth = new NtlmPasswordAuthentication("MARS", user1, password1);
        } else if (SharedUploadFolder.getSharedUploadFolder(supplier.getSharedUploadFolder()).equals(SharedUploadFolder.AVESINA_SHARED_FOLDER)) {
            url = "smb://mars.vgregion.se/inbox/DATA_PRIVATA/Försystem/InfilWeb/Avesina_old/";
            auth = new NtlmPasswordAuthentication("MARS", user2, password2);
        } else {
            throw new RuntimeException("Failed to decide which upload url to use.");
        }

        try {
            SmbFile dir = new SmbFile(url, auth);

            SmbFile newFile = new SmbFile(dir, fileName);

            newFile.createNewFile();

            try (OutputStream outputStream = newFile.getOutputStream()) {
                IOUtils.copy(inputStream, outputStream);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
