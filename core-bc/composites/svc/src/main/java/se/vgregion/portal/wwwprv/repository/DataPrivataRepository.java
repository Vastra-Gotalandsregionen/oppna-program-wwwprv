package se.vgregion.portal.wwwprv.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import se.vgregion.portal.wwwprv.model.jpa.DataPrivataUser;
import se.vgregion.portal.wwwprv.model.jpa.Supplier;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * @author Patrik Bergström
 */
@Repository
@SuppressWarnings("unchecked")
public class DataPrivataRepository {

    @PersistenceContext
    private EntityManager entityManager;


    public DataPrivataUser getUserById(Long userId) {
        return entityManager.find(DataPrivataUser.class, userId);
    }

    @Transactional
    public void persistNewUser(DataPrivataUser dataPrivataUser) {
        entityManager.persist(dataPrivataUser);
    }

    public List<Supplier> getAllSuppliers() {
        return entityManager.createQuery("select s from Supplier s").getResultList();
    }
    @Transactional
    public void persistNewSupplier(Supplier supplierToAdd) {
        entityManager.persist(supplierToAdd);
    }

    public Supplier findSupplierById(Integer primaryKey) {
        return entityManager.find(Supplier.class, primaryKey);
    }

    @Transactional
    public void saveUser(DataPrivataUser dataPrivataUser) {
        entityManager.merge(dataPrivataUser);
    }

    @Transactional
    public void remove(Supplier supplier) {
        entityManager.remove(entityManager.getReference(Supplier.class, supplier.getId()));
    }

    @Transactional
    public void saveSupplier(Supplier supplier) {
        entityManager.merge(supplier);
    }
}
