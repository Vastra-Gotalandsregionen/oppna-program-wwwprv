package se.vgregion.portal.wwwprv.model.jpa;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Patrik Bergström
 */
@Entity
@Table(name = "vgr_dataprivata_user")
public class DataPrivataUser {

    @Id
    private Long id;

    public DataPrivataUser() {
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE) // Hibernate specific
    private Supplier supplier;

    public DataPrivataUser(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
}
