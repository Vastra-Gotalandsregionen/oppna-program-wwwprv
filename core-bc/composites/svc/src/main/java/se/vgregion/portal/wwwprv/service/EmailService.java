package se.vgregion.portal.wwwprv.service;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.vgregion.portal.wwwprv.model.jpa.Supplier;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
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

        String subject = "Uppladdad fil: " + fullFileName + getEnvironmentString();
        String text = fullFileName + " har laddats upp till "
                + StringUtils.join(supplier.getUploadFolders(), ", ") + " av " + userName;

        sendMessage(subject, text);
    }

    private String getEnvironmentString() {
        return environment != null && !"".equals(environment) ? " - " + environment : "";
    }

    public void notifyError(Exception e, String... args) {
        String subject = "Systemmeddelande - ett fel har inträffat - " + getEnvironmentString();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); PrintStream s = new PrintStream(out)) {
            if (args != null && args.length > 0) {
                String lineSeparator = System.getProperty("line.separator");
                for (String arg : args) {
                    out.write(arg.getBytes("UTF-8"));
                    out.write(lineSeparator.getBytes("UTF-8"));
                }
            }
            e.printStackTrace(s);

            sendMessage(subject, out.toString());
        } catch (IOException e1) {
            LOGGER.error(e1.getMessage(), e1);
        }

    }

    public void notifyErrorMessage(String message) {
        String subject = "Systemmeddelande - ett fel har inträffat - " + getEnvironmentString();

        sendMessage(subject, message);
    }

    private void sendMessage(String subject, String text) {
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
            message.setSubject(subject);

            // Now set the actual message
            message.setText(text);

            // Send message
            Transport.send(message);

        } catch (MessagingException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
