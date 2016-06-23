package se.vgregion.portal.wwwprv.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import se.vgregion.portal.wwwprv.model.Node;
import se.vgregion.portal.wwwprv.model.jpa.DataPrivataUser;
import se.vgregion.portal.wwwprv.model.jpa.FileUpload;
import se.vgregion.portal.wwwprv.model.jpa.GlobalSetting;
import se.vgregion.portal.wwwprv.model.jpa.Supplier;
import se.vgregion.portal.wwwprv.util.Callback;
import se.vgregion.portal.wwwprv.util.Notifiable;
import se.vgregion.portal.wwwprv.util.SharedUploadFolder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Patrik Bergström
 */
@Repository
@SuppressWarnings("unchecked")
public class DataPrivataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataPrivataService.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private EmailService emailService;

    @Autowired
    private FileAccessService fileAccessService;

    public DataPrivataService() {
    }

    public DataPrivataService(EmailService emailService, FileAccessService fileAccessService) {
        this.emailService = emailService;
        this.fileAccessService = fileAccessService;
    }

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

    public Supplier getSupplier(String supplierCode) {
        Query query = entityManager.createQuery("select s from Supplier s where s.enhetsKod = :enhetsKod");

        query.setParameter("enhetsKod", supplierCode);

        Supplier result = (Supplier) query.getSingleResult();

        return result;
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
    public void saveFileUpload(String supplierCode, String baseName, String datePart, String suffix, String userName,
                               InputStream inputStream, long fileSize, Notifiable notifiable, Callback callback)
            throws DistrictDistributionException {

        FileUpload fileUpload = new FileUpload(supplierCode, baseName, datePart, suffix, userName, fileSize);

        fileUpload.setUploaded(new Date());
        fileUpload.setUploader(userName);

        entityManager.persist(fileUpload);

        fileAccessService.uploadFileInBackground(fileUpload.getFullFileName(), getSupplier(supplierCode), inputStream,
                fileSize, getNamndFordelningDirectory(), userName, notifiable, callback);

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

    public static void verifyFileName(String fileName, Supplier chosenSupplier) throws IllegalArgumentException {
        Short sharedUploadFolder = chosenSupplier.getSharedUploadFolder();

        String enhetsKod = chosenSupplier.getEnhetsKod();

        if (sharedUploadFolder.equals(SharedUploadFolder.MARS_SHARED_FOLDER.getIndex())) {

            if (!fileName.toLowerCase().startsWith(enhetsKod.toLowerCase())) {
                LOGGER.error("Filename \"" + fileName.toLowerCase() + "\" doesn't start with \""
                        + enhetsKod.toLowerCase() + "\".");

                throw new IllegalArgumentException("Filen " + fileName + " börjar inte med " + enhetsKod + ".");
            }

        } else if (sharedUploadFolder.equals(SharedUploadFolder.AVESINA_SHARED_FOLDER.getIndex())) {

            // The date part is not exactly perfect but fair enough with fair readabiltiy.
            if (!fileName.toLowerCase().matches(enhetsKod.toLowerCase() + "_[0-9]{2}[0-1][0-9][0-3][0-9]\\.in")) {
                throw new IllegalArgumentException("Filen måste vara på formen " + enhetsKod + "_yymmdd.in");
            }

        } else {
            throw new IllegalArgumentException("Tekniskt fel. Ingen destination är konfigurerad.");
        }
    }

    public String possiblyChangeSuffix(String suffixIncludingDot, Supplier chosenSupplier) {
        Short sharedUploadFolder = chosenSupplier.getSharedUploadFolder();

        if (sharedUploadFolder == null) {
            throw new IllegalArgumentException("Tekniskt fel. Ingen destination är konfigurerad.");
        }

        if (sharedUploadFolder.equals(SharedUploadFolder.MARS_SHARED_FOLDER.getIndex())) {
            return ".in";
        }

        // Unchanged.
        return suffixIncludingDot;
    }

    public Node<String> retrieveRemoteFileTree() {

        Node<String> aggregatedTree = new Node<>();
        List<String> serverList = getServerList();

        for (String serverUrl : serverList) {
            Node<String> stringTree = fileAccessService.retrieveRemoteFileTree(serverUrl);

            Node<String> newNode = new Node<>(serverUrl);

            aggregatedTree.getChildren().add(newNode);

            newNode.getChildren().addAll(stringTree.getChildren());
        }

        return aggregatedTree;
    }

    public String getNamndFordelningDirectory() {
        GlobalSetting globalSetting = entityManager.find(GlobalSetting.class, "namnd-fordelnings-directory");

        if (globalSetting == null) {
            globalSetting = new GlobalSetting("namnd-fordelnings-directory", "");
            entityManager.persist(globalSetting);
        }

        return globalSetting.getValue();
    }

    @Transactional
    public void saveNamndFordelningDirectory(String value) {
        GlobalSetting globalSetting = new GlobalSetting("namnd-fordelnings-directory", value);

        entityManager.merge(globalSetting);
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public void saveServerList(String commaSeparatedServerList) {
        GlobalSetting globalSetting = new GlobalSetting("server-list", commaSeparatedServerList);

        entityManager.merge(globalSetting);
    }

    public List<String> getServerList() {
        GlobalSetting globalSetting = entityManager.find(GlobalSetting.class, "server-list");

        if (globalSetting == null) {
            globalSetting = new GlobalSetting("server-list", "");
            entityManager.persist(globalSetting);
        }

        String[] servers;

        String value = globalSetting.getValue();

        if (value != null && value.length() > 0) {
            servers = value.split(",");
        } else {
            servers = new String[0];
        }

        List<String> serverList = new ArrayList<>();

        for (String server : servers) {
            serverList.add(server.trim());
        }

        return serverList;
    }
}
