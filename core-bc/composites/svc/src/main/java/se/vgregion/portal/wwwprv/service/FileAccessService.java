package se.vgregion.portal.wwwprv.service;

import se.vgregion.portal.wwwprv.model.Tree;
import se.vgregion.portal.wwwprv.model.jpa.Supplier;
import se.vgregion.portal.wwwprv.util.Notifiable;

import java.io.InputStream;

/**
 * @author Patrik Björk
 */
public interface FileAccessService {

    void uploadFile(String fileName, Supplier supplier, InputStream inputStream, long fileSize, Notifiable notifiable);

    Tree<String> retrieveRemoteFileTree();
}
