package se.vgregion.portal.wwwprv.service.soap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.transport.http.HttpUrlConnectionMessageSender;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;

public class HttpsWebServiceMessageSenderImpl extends HttpUrlConnectionMessageSender {

    @Autowired
    private ConvenientSslContextFactory sslContextFactory;

    @Override
    protected void prepareConnection(HttpURLConnection connection) throws IOException {

        if (connection instanceof HttpsURLConnection) {
            try {
                ((HttpsURLConnection) connection).setSSLSocketFactory(sslContextFactory.createSslContext()
                                                                              .getSocketFactory());
            } catch (Exception e) {
                throw new IOException(e);
            }
        }

        super.prepareConnection(connection);
    }
}
