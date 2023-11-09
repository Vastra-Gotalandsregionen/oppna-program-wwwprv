package se.vgregion.portal.wwwprv.model.jpa;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Patrik Bergström
 */
@Entity
@Table(name = "vgr_dataprivata_user")
public class DataPrivataUser {

    @Id
    private Long liferayUserId;

    public DataPrivataUser() {
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "vgr_dataprivata_user_supplier")
    private Set<Supplier> suppliers = new HashSet<>();

    public DataPrivataUser(Long liferayUserId) {
        this.liferayUserId = liferayUserId;
    }

    public Long getLiferayUserId() {
        return liferayUserId;
    }

    public void setLiferayUserId(Long id) {
        this.liferayUserId = id;
    }

    public Set<Supplier> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(Set<Supplier> supplier) {
        this.suppliers = supplier;
    }
}
