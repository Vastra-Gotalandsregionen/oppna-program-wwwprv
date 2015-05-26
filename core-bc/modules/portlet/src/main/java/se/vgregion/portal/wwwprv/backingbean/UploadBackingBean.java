package se.vgregion.portal.wwwprv.backingbean;

import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se.vgregion.portal.wwwprv.model.jpa.FileUpload;
import se.vgregion.portal.wwwprv.model.jpa.Supplier;
import se.vgregion.portal.wwwprv.service.DataPrivataService;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Patrik Bergström
 */

@Component
@Scope("session")
public class UploadBackingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadBackingBean.class);

    @Autowired
    private DataPrivataService dataPrivataService;

    private UploadedFile file;

    private String uploadedFileName;
    private Supplier chosenSupplier;
    private Boolean showFileUpload;
    private boolean currentlyDuplicateFileWorkflow = false;
    private File tempFile;
    private File baseUploadDirectory = new File(System.getProperty("user.home") + "/.wwwprv");
    private File uploadDirectory;
    private List<FileUpload> uploadedFileList;
    private FileUpload tempFileUpload;

    @PostConstruct
    public void init() {
        List<Supplier> usersSuppliers = getUsersSuppliers();

        if (usersSuppliers.size() == 1) {
            setChosenSupplier(usersSuppliers.get(0));
        }
    }

    public void fileUploadListener(FileUploadEvent event) throws IOException {
        String originalFileName = event.getFile().getFileName();

        int lastDot = originalFileName.lastIndexOf(".");

        String baseFileName = null;
        String lastPartIncludingDot = null;
        if (lastDot > -1) {
            baseFileName = originalFileName.substring(0, lastDot);
            lastPartIncludingDot = originalFileName.substring(lastDot, originalFileName.length());
        } else {
            baseFileName = originalFileName;
            lastPartIncludingDot = "";
        }

        if (!uploadDirectory.exists()) {
            boolean success = uploadDirectory.mkdirs();

            if (!success) {
                throw new RuntimeException("Couldn't create upload directory.");
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("_yyyyMMdd_HHmm");

        String datePart = sdf.format(new Date());

        String newFileName = baseFileName + datePart + lastPartIncludingDot;

        File newFile = new File(uploadDirectory, newFileName);

        String userName = getUserName();

        if (dataPrivataService.isFileAlreadyUploaded(baseFileName, lastPartIncludingDot, chosenSupplier)) {
            tempFileUpload = new FileUpload(chosenSupplier.getEnhetsKod(), baseFileName, datePart, lastPartIncludingDot, userName);

            tempFile = new File(System.getProperty("java.io.tmpdir"), newFileName);
            newFile = tempFile;
            currentlyDuplicateFileWorkflow = true;
        }

        try (FileOutputStream fos = new FileOutputStream(newFile); BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            InputStream is = event.getFile().getInputstream();

            IOUtils.copy(is, bos);

        }

        try (FileInputStream fis = new FileInputStream(newFile);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            if (!currentlyDuplicateFileWorkflow) {
                dataPrivataService.saveFileUpload(chosenSupplier.getEnhetsKod(), baseFileName, datePart, lastPartIncludingDot, userName, bis);
            }

            if (!currentlyDuplicateFileWorkflow) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Uppladdning lyckades. Filen fick namnet " + newFileName));
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ett tekniskt fel inträffade.", "Ett tekniskt fel inträffade."));
        }

        updateUploadedFileList();
    }

    private String getUserName() {
        PortletRequest request = (PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        Map<String, String> userInfo = (Map<String, String>) request.getAttribute(PortletRequest.USER_INFO);
        String info;
        if (userInfo != null) {
            info = userInfo.get(PortletRequest.P3PUserInfos.USER_LOGIN_ID.toString());
        } else {
            return null;
        }
        return info;
    }

    public void moveTempFileToUploadDirectory() throws IOException {
        File target = new File(uploadDirectory, tempFile.getName());
        Files.move(tempFile.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);

        try (FileInputStream fis = new FileInputStream(target);
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            dataPrivataService.saveFileUpload(tempFileUpload.getSupplierCode(), tempFileUpload.getBaseName(),
                    tempFileUpload.getDatePart(), tempFileUpload.getSuffix(), getUserName(), bis);

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Filen " + tempFile.getName() + " skapades."));

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ett tekniskt fel inträffade.", "Ett tekniskt fel inträffade."));
        }

        tempFile = null;
        tempFileUpload = null;
        currentlyDuplicateFileWorkflow = false;

        updateUploadedFileList();
    }

    public void abortMoveTempFileToUploadDirectory() throws IOException {
        tempFile = null;
        tempFileUpload = null;
        currentlyDuplicateFileWorkflow = false;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Operationen avbröts."));
    }

    public boolean isCurrentlyDuplicateFileWorkflow() {
        return currentlyDuplicateFileWorkflow;
    }

    public File getTempFile() {
        return tempFile;
    }

    public FileUpload getTempFileUpload() {
        return tempFileUpload;
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

    public UploadRequestScopeBackingBean getUploadRequestBean() {
        return UtilBean.findBean("uploadRequestScopeBackingBean");
    }

    public Supplier getChosenSupplier() {
        return chosenSupplier;
    }

    public void setChosenSupplier(Supplier chosenSupplier) {
        this.chosenSupplier = chosenSupplier;

        if (chosenSupplier != null) {
            showFileUpload = true;
            uploadDirectory = new File(baseUploadDirectory, chosenSupplier.getEnhetsKod());

            updateUploadedFileList();
        } else {
            showFileUpload = false;
            uploadedFileList = null;
        }
    }

    private void updateUploadedFileList() {
        List<FileUpload> allFileUploads = dataPrivataService.getAllFileUploads();

        Iterator<FileUpload> iterator = allFileUploads.iterator();

        while (iterator.hasNext()) {
            if (!iterator.next().getSupplierCode().equals(chosenSupplier.getEnhetsKod())) {
                iterator.remove();
            }
        }

        uploadedFileList = allFileUploads;
    }

    public List<FileUpload> getUploadedFileList() {
        return uploadedFileList;
    }

    public List<Supplier> getUsersSuppliers() {
        return getUploadRequestBean().getUsersSuppliers();
    }

    public Boolean getShowFileUpload() {
        return showFileUpload;
    }
}
