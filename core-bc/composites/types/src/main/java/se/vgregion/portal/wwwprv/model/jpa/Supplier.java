package se.vgregion.portal.wwwprv.model.jpa;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;

/**
 * @author Patrik Bergström
 */
@Entity
@Table(name = "vgr_dataprivata_supplier")
public class Supplier implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String ansvarigtKansli;

    private String enhetsNamn;

    private String enhetsKod;

    private Short sharedUploadFolder;

    @ManyToMany(mappedBy = "suppliers", fetch = FetchType.EAGER)
    private Set<DataPrivataUser> dataPrivataUsers;

    public Supplier() {
    }

    public Integer getId() {
        return id;
    }

    public String getAnsvarigtKansli() {
        return ansvarigtKansli;
    }

    public void setAnsvarigtKansli(String ansvarigtKansli) {
        this.ansvarigtKansli = ansvarigtKansli;
    }

    public String getEnhetsNamn() {
        return enhetsNamn;
    }

    public void setEnhetsNamn(String enhetsNamn) {
        this.enhetsNamn = enhetsNamn;
    }

    public String getEnhetsKod() {
        return enhetsKod;
    }

    public void setEnhetsKod(String enhetsKod) {
        this.enhetsKod = enhetsKod;
    }

    public Set<DataPrivataUser> getDataPrivataUsers() {
        return dataPrivataUsers;
    }

    public void setDataPrivataUsers(Set<DataPrivataUser> dataPrivataUsers) {
        this.dataPrivataUsers = dataPrivataUsers;
    }

    public Short getSharedUploadFolder() {
        return sharedUploadFolder;
    }

    public void setSharedUploadFolder(Short sharedUploadFolder) {
        this.sharedUploadFolder = sharedUploadFolder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Supplier supplier = (Supplier) o;

        return !(id != null ? !id.equals(supplier.id) : supplier.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
