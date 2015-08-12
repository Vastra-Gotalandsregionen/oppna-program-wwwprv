package se.vgregion.portal.wwwprv.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.vgregion.portal.wwwprv.model.jpa.Supplier;
import se.vgregion.portal.wwwprv.util.SharedUploadFolder;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @author Patrik Bergström
 */
@Service
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    @Value("${email.notification.to}")
    private String toEmail;

    @Value("${email.notification.cc-array}")
    private String[] ccEmails;

    @Value("${email.notification.environment}")
    private String environment;

    public void notifyNewUpload(String fullFileName, Supplier supplier, String userName) {

        // Sender's email ID needs to be mentioned
        String from = "dataprivata@vgregion.se";

        // Assuming you are sending email from localhost
        String host = "mail.vgregion.se";

        // Get system properties
        Properties properties = new Properties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", "25");

        // Get the default Session object.
        Session session = Session.getInstance(properties);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));

            for (String ccEmail : ccEmails) {
                message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccEmail));
            }

            // Set Subject: header field
            message.setSubject("Uppladdad fil: " + fullFileName + (environment != null && !"".equals(environment) ? " - " + environment : ""));

            // Now set the actual message
            message.setText(fullFileName + " har laddats upp till " + SharedUploadFolder.getSharedUploadFolder(supplier.getSharedUploadFolder()).getLabel() + " av " + userName);

            // Send message
            Transport.send(message);

        } catch (MessagingException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
