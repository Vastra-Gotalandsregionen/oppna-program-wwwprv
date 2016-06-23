package se.vgregion.portal.wwwprv.service;

import se.vgregion.portal.wwwprv.model.Node;
import se.vgregion.portal.wwwprv.model.jpa.Supplier;
import se.vgregion.portal.wwwprv.util.Callback;
import se.vgregion.portal.wwwprv.util.Notifiable;

import java.io.InputStream;
import java.util.concurrent.Future;

/**
 * @author Patrik Björk
 */
public interface FileAccessService {

    Future<?> uploadFileInBackground(String fileName, Supplier supplier, InputStream inputStream, long fileSize,
                                     String namndFordelningDirectory, String uploader, Notifiable notifiable,
                                     Callback callback);

    Node<String> retrieveRemoteFileTree(String url);
}
