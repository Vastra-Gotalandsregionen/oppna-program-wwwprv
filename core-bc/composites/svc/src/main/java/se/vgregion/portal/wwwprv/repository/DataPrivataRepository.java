package se.vgregion.portal.wwwprv.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import se.vgregion.portal.wwwprv.model.jpa.DataPrivataUser;
import se.vgregion.portal.wwwprv.model.jpa.FileUpload;
import se.vgregion.portal.wwwprv.model.jpa.Supplier;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
        Supplier supplierReference = entityManager.getReference(Supplier.class, supplier.getId());

        // Remove bidirectionally
        Set<DataPrivataUser> dataPrivataUsers = supplierReference.getDataPrivataUsers();
        for (DataPrivataUser dataPrivataUser : dataPrivataUsers) {
            dataPrivataUser.getSuppliers().remove(supplierReference);
        }

        entityManager.remove(supplierReference);
    }

    @Transactional
    public void saveSupplier(Supplier supplier) {
        entityManager.merge(supplier);
    }

    @Transactional
    public void saveFileUpload(String supplierCode, String baseName, String datePart, String suffix) {
        FileUpload fileUpload = new FileUpload(supplierCode, baseName, datePart, suffix);

        fileUpload.setUploaded(new Date());

        entityManager.persist(fileUpload);
    }

    public List<FileUpload> getAllFileUploads() {
        return entityManager.createQuery("select f from FileUpload f order by f.uploaded desc").getResultList();
    }

    public boolean isFileAlreadyUploaded(String baseFileName, String suffix, Supplier supplier) {
        Query query = entityManager.createQuery(
                "select f from FileUpload f where f.baseName = :baseName and f.suffix = :suffix and f.supplierCode = :supplierCode");

        query.setParameter("baseName", baseFileName);
        query.setParameter("suffix", suffix);
        query.setParameter("supplierCode", supplier.getEnhetsKod());

        List resultList = query.getResultList();

        return resultList != null && resultList.size() > 0;
    }
}
