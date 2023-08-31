package se.vgregion.portal.wwwprv.backingbean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se.vgregion.portal.wwwprv.model.jpa.FileUpload;
import se.vgregion.portal.wwwprv.service.DataPrivataService;
import se.vgregion.portal.wwwprv.service.LiferayService;

import java.util.List;

/**
 * @author Patrik Bergström
 */

@Component
@Scope("request")
public class ListFilesBackingBean {

    @Autowired
    private DataPrivataService dataPrivataService;

    private List<FileUpload> allFileUploads;

    public List<FileUpload> getAllFileUploads() {
        if (allFileUploads == null) {
            allFileUploads = dataPrivataService.getAllFileUploads();
        }

        return allFileUploads;
    }
}
