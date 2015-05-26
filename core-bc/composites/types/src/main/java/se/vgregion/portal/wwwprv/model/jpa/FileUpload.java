package se.vgregion.portal.wwwprv.model.jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * @author Patrik Bergström
 */
@Entity
@Table(name = "vgr_dataprivata_fileupload")
public class FileUpload {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String supplierCode;

    private String baseName;

    private String datePart;

    private String suffix;

    private String uploader;

    @Temporal(TemporalType.TIMESTAMP)
    private Date uploaded;

    public String getSupplierCode() {
        return supplierCode;
    }

    public FileUpload() {
    }

    public FileUpload(String supplierCode, String baseName, String datePart, String suffix, String uploader) {
        this.supplierCode = supplierCode;
        this.baseName = baseName;
        this.datePart = datePart;
        this.suffix = suffix;
        this.uploader = uploader;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String fileName) {
        this.baseName = fileName;
    }

    public String getDatePart() {
        return datePart;
    }

    public void setDatePart(String datePart) {
        this.datePart = datePart;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Date getUploaded() {
        return uploaded;
    }

    public void setUploaded(Date uploaded) {
        this.uploaded = uploaded;
    }

    public String getFullFileName() {
        return baseName + datePart + suffix;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }
}
