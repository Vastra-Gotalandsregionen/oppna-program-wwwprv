package se.vgregion.portal.wwwprv.backingbean;

import com.liferay.faces.util.portal.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se.vgregion.portal.wwwprv.model.Tree;
import se.vgregion.portal.wwwprv.model.UserContainer;
import se.vgregion.portal.wwwprv.model.jpa.DataPrivataUser;
import se.vgregion.portal.wwwprv.model.jpa.Supplier;
import se.vgregion.portal.wwwprv.service.DataPrivataService;
import se.vgregion.portal.wwwprv.service.LiferayService;
import se.vgregion.portal.wwwprv.service.LiferayServiceException;
import se.vgregion.portal.wwwprv.util.SharedUploadFolder;
import se.vgregion.portal.wwwprv.util.SupplierComparator;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Patrik Bergström
 */

@Component
@Scope("session")
public class AdminBackingBean {

    @Autowired
    private LiferayService liferayService;

    @Autowired
    private DataPrivataService dataPrivataService;

    private Supplier supplierToAdd;
    private Supplier currentSupplier;
    private String supplierMessage;
    private String userMessage;
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private Map<Long, Boolean> usersSupplierChooserExpanded = new HashMap<>();
    private Tree<String> tree;

    public AdminBackingBean() {
    }

    public AdminBackingBean(LiferayService liferayService, DataPrivataService dataPrivataService) {
        this.liferayService = liferayService;
        this.dataPrivataService = dataPrivataService;
    }

    @PostConstruct
    public void init() {
        tree = dataPrivataService.retrieveRemoteFileTree();
    }

    public TreeNode getFileTree() {
        TreeNode target = new DefaultTreeNode(tree.getRoot().getData());

        Tree.Node<String> source = tree.getRoot();

        target.getChildren().addAll(getFileTree(source.getChildren()));

        return target;
    }

    private Collection<? extends TreeNode> getFileTree(List<Tree.Node<String>> nodes) {
        if (nodes == null || nodes.size() == 0) {
            return new ArrayList<>();
        } else {
            Collection<TreeNode> nodesToAdd = new ArrayList<>();
            for (Tree.Node node : nodes) {
                TreeNode treeNode = new DefaultTreeNode(node.getData());
                treeNode.getChildren().addAll(getFileTree(node.getChildren()));
                nodesToAdd.add(treeNode);
            }

            return nodesToAdd;
        }
    }

    public List<UserContainer> getAllUsers() {
        ThemeDisplay themeDisplay = (ThemeDisplay) ((PortletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest()).getAttribute(WebKeys.THEME_DISPLAY);

        long companyId = themeDisplay.getCompanyId();

        try {
            return liferayService.getUploaderUsers(companyId);
        } catch (LiferayServiceException e) {
            FacesContext.getCurrentInstance().addMessage("usersMessage", new FacesMessage(e.getLocalizedMessage()));
            return null;
        }
    }

    public List<Short> getAllSharedUploadFolders() {
        SharedUploadFolder[] values = SharedUploadFolder.values();

        List<Short> toReturn = new ArrayList<>();

        for (SharedUploadFolder value : values) {
            toReturn.add(value.getIndex());
        }

        return toReturn;
    }

    public String getLabel(Short index) {
        return SharedUploadFolder.getSharedUploadFolder(index).getLabel();
    }

    public void addSupplier() {

        dataPrivataService.persistNewSupplier(supplierToAdd);

        String message = "Lyckades lägga till " + supplierToAdd.getEnhetsKod() + ".";

        setSupplierMessage(message);

        supplierToAdd = new Supplier();
    }

    public void saveSupplier(Supplier supplier) {

        dataPrivataService.saveSupplier(supplier);

        String message = "Lyckades spara " + supplier.getEnhetsKod() + ".";

        setSupplierMessage(message);
    }

    public void removeSupplier(Supplier supplier) {
        try {
            dataPrivataService.remove(supplier);

            String message = "Lyckades ta bort " + supplier.getEnhetsKod() + ".";

            setSupplierMessage(message);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

    }

    public void saveUser(UserContainer userContainer) {
        dataPrivataService.saveUser(userContainer.getDataPrivataUser());

        String message = "Lyckades spara till " + userContainer.getLiferayUser().getFullName() + ".";

        setUserMessage(message);
    }

    public List<Supplier> getAllSuppliers() {
        List<Supplier> allSuppliers = dataPrivataService.getAllSuppliers();
        Collections.sort(allSuppliers, new SupplierComparator());
        return allSuppliers;
    }

    public Supplier getSupplierToAdd() {
        if (supplierToAdd == null) {
            supplierToAdd = new Supplier();
        }
        return supplierToAdd;
    }

    public Map<UserContainer, Map<Supplier, Boolean>> getUserWithSuppliersHelper() {
        // By this way we only do initUserWithSuppliersMap() once for each request, since the bean we find is request
        // scope.
        RequestScopedModelBean bean = UtilBean.findBean("requestScopedModelBean");

        Map<UserContainer, Map<Supplier, Boolean>> userWithSuppliersHelper = bean.getUserWithSuppliersHelper();

        if (userWithSuppliersHelper == null) {
            userWithSuppliersHelper = initUserWithSuppliersMap();
            bean.setUserWithSuppliersHelper(userWithSuppliersHelper);
        }

        return userWithSuppliersHelper;
    }

    private Map<UserContainer, Map<Supplier, Boolean>> initUserWithSuppliersMap() {
        Map<UserContainer, Map<Supplier, Boolean>> userWithSuppliersHelper = new HashMap<>();

        List<UserContainer> allUsers = getAllUsers();

        for (UserContainer user : allUsers) {
            userWithSuppliersHelper.put(user, new HashMap<Supplier, Boolean>());

            List<Supplier> allSuppliers = getAllSuppliers();

            for (Supplier supplier : allSuppliers) {
                userWithSuppliersHelper.get(user).put(supplier, user.getDataPrivataUser().getSuppliers().contains(supplier));
            }
        }
        return userWithSuppliersHelper;
    }

    public void toggleSupplier(UserContainer userContainer, Supplier supplier) {
        DataPrivataUser dataPrivataUser = userContainer.getDataPrivataUser();
        if (dataPrivataUser.getSuppliers().contains(supplier)) {
            dataPrivataUser.getSuppliers().remove(supplier);
        } else {
            dataPrivataUser.getSuppliers().add(supplier);
        }

        dataPrivataService.saveUser(dataPrivataUser);

        FacesContext context = FacesContext.getCurrentInstance();

        context.addMessage(null, new FacesMessage("Sparat!"));
    }

    public void toggleSupplierChooser() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map map = context.getExternalContext().getRequestParameterMap();
        String userId = (String) map.get("userId");
        String isExpanded = (String) map.get("isExpanded");

        usersSupplierChooserExpanded.put(Long.parseLong(userId), Boolean.parseBoolean(isExpanded));
    }

    public boolean showSupplierChooser(UserContainer userContainer) {
        Long userId = userContainer.getDataPrivataUser().getLiferayUserId();
        return usersSupplierChooserExpanded.containsKey(userId) && usersSupplierChooserExpanded.get(userId) == true;
    }

    public void setSupplierMessage(String supplierMessage) {
        this.supplierMessage = supplierMessage;
    }

    public String getSupplierMessage() {

        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                supplierMessage = null;
            }
        }, 2, TimeUnit.SECONDS);

        return supplierMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getUserMessage() {
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                userMessage = null;
            }
        }, 2, TimeUnit.SECONDS);

        return userMessage;
    }

    public Supplier getCurrentSupplier() {
        return currentSupplier;
    }

    public void setCurrentSupplier(Supplier currentSupplier) {
        this.currentSupplier = currentSupplier;
    }
}
