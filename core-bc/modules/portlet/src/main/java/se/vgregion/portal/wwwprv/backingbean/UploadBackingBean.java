package se.vgregion.portal.wwwprv.backingbean;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se.vgregion.portal.wwwprv.service.LiferayService;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * @author Patrik Bergström
 */

@Component
@Scope("session")
public class UploadBackingBean {

    private UploadedFile file;

    private String uploadedFileName;


    public void upload() {
        if (file != null) {
            uploadedFileName = file.getFileName();
        }
    }

    public void fileUploadListener(FileUploadEvent event) {
        System.out.println(event);
        event.getFile().getFileName();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Uppladdning lyckades. Filen fick namnet xxx"));
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public String getUploadedFileName() {
        return uploadedFileName;
    }
}
