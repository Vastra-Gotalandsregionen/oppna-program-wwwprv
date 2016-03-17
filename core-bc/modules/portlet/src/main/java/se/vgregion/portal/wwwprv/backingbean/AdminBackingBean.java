package se.vgregion.portal.wwwprv.backingbean;

import com.liferay.faces.util.portal.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import org.apache.commons.lang.StringUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se.vgregion.portal.wwwprv.model.Node;
import se.vgregion.portal.wwwprv.model.UserContainer;
import se.vgregion.portal.wwwprv.model.jpa.DataPrivataUser;
import se.vgregion.portal.wwwprv.model.jpa.Supplier;
import se.vgregion.portal.wwwprv.service.DataPrivataService;
import se.vgregion.portal.wwwprv.service.LiferayService;
import se.vgregion.portal.wwwprv.service.LiferayServiceException;
import se.vgregion.portal.wwwprv.service.model.DistrictDistributionClassName;
import se.vgregion.portal.wwwprv.util.SharedUploadFolder;
import se.vgregion.portal.wwwprv.util.SupplierComparator;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private String serverList;
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private Map<Long, Boolean> usersSupplierChooserExpanded = new HashMap<>();
    private TreeNode remoteDirectoryTree;
    private TreeNode[] selectedDirectoryNode;
    private TreeNode selectedNamndFordelningDirectory;
    private TreeNode namndFordelningRemoteDirectoryTree;

    @Autowired
    private RequestScopedModelBean requestScopedModelBean;

    public AdminBackingBean() {
    }

    public AdminBackingBean(LiferayService liferayService, DataPrivataService dataPrivataService) {
        this.liferayService = liferayService;
        this.dataPrivataService = dataPrivataService;
    }

    @PostConstruct
    public void init() {
        remoteDirectoryTree = transformTree(dataPrivataService.retrieveRemoteFileTree());

        // namndFordelningRemoteDirectoryTree
        namndFordelningRemoteDirectoryTree = transformTree(dataPrivataService.retrieveRemoteFileTree());

        List<String> serverList = dataPrivataService.getServerList();

        if (serverList != null && serverList.size() > 0) {
            this.serverList = StringUtils.join(serverList, ", ");
        }

        refreshNamndFordelningDirectory();
    }

    private void refreshNamndFordelningDirectory() {
        String namndFordelningDirectory = dataPrivataService.getNamndFordelningDirectory();

        traverseAndSetSelectedAndExpanded(namndFordelningRemoteDirectoryTree, namndFordelningDirectory);
    }

    private void traverseAndSetSelectedAndExpanded(TreeNode treeNode, String directory) {

        // Defaults.
        treeNode.setSelected(false);
        treeNode.setExpanded(false);

        if (getFullPath(treeNode).equals(directory)) {
            treeNode.setSelected(true);

            while (treeNode.getParent() != null) {
                treeNode.getParent().setExpanded(true);
                treeNode = treeNode.getParent();
            }
        } else {
            if (treeNode.getChildren().size() > 0) {
                for (TreeNode node : treeNode.getChildren()) {
                    traverseAndSetSelectedAndExpanded(node, directory);
                }
            }
        }
    }

    /**
     * Transforms from {@link Node} to {@link TreeNode}.
     * @return
     */
    public TreeNode transformTree(Node<String> tree) {
        TreeNode target = new DefaultTreeNode(tree.getData());

        Node<String> source = tree;

        target.getChildren().addAll(transformTree(source.getChildren()));

        return target;
    }

    private Collection<? extends TreeNode> transformTree(List<Node<String>> nodes) {
        if (nodes == null || nodes.size() == 0) {
            return new ArrayList<>();
        } else {
            Collection<TreeNode> nodesToAdd = new ArrayList<>();
            for (Node node : nodes) {
                TreeNode treeNode = new DefaultTreeNode(node.getData());
                treeNode.getChildren().addAll(transformTree(node.getChildren()));

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

    public void saveUploadFolders() {
        Set<String> selectedUploadFolders = new HashSet<>();

        for (TreeNode treeNode : selectedDirectoryNode) {
            selectedUploadFolders.add(getFullPath(treeNode));
        }

        currentSupplier.setUploadFolders(selectedUploadFolders);

        dataPrivataService.saveSupplier(currentSupplier);
    }

    public void saveNamndFordelningDirectory() {
        String fullPath = getFullPath(selectedNamndFordelningDirectory);

        dataPrivataService.saveNamndFordelningDirectory(fullPath);

        refreshNamndFordelningDirectory();

        FacesContext.getCurrentInstance()
                .addMessage(
                        getJustToAssociateMessageWithSomething().getClientId(),
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Sparat!", "Sparat!"));
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

    public void saveServerList() {
        dataPrivataService.saveServerList(serverList);
        init();

        FacesContext.getCurrentInstance()
                .addMessage(
                        null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Sparat!", "Sparat!"));
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

        traverseAndSetSelected(currentSupplier, remoteDirectoryTree);
        traverseAndSetExpanded(currentSupplier, remoteDirectoryTree);
    }

    private static void traverseAndSetExpanded(Supplier supplier, TreeNode treeNode) {
        Set<String> persistedUploadFolders = supplier.getUploadFolders();

        boolean match = anyPersistedUploadFolderStartsWith(persistedUploadFolders, getFullPath(treeNode));

        treeNode.setExpanded(match && treeNode.getChildren().size() > 0);

        if (treeNode.getChildren().size() > 0) {
            for (TreeNode node : treeNode.getChildren()) {
                traverseAndSetExpanded(supplier, node);
            }
        }
    }

    private static boolean anyPersistedUploadFolderStartsWith(Set<String> strings, String string) {
        for (String s : strings) {
            // Just starts with. We don't want to expand when equal.
            if (s.startsWith(string) && !s.equals(string)) {
                return true;
            }
        }

        return false;
    }

    private static void traverseAndSetSelected(Supplier supplier, TreeNode treeNode) {
        Set<String> persistedUploadFolders = supplier.getUploadFolders();

        boolean contains = persistedUploadFolders.contains(getFullPath(treeNode));
        treeNode.setSelected(contains);

        if (treeNode.getChildren().size() > 0) {
            for (TreeNode node : treeNode.getChildren()) {
                traverseAndSetSelected(supplier, node);
            }
        }
    }

    public TreeNode[] getSelectedDirectoryNode() {
        return selectedDirectoryNode;
    }

    public void setSelectedDirectoryNode(TreeNode[] selectedDirectoryNode) {
        this.selectedDirectoryNode = selectedDirectoryNode;
    }

    public TreeNode getRemoteDirectoryTree() {
        return remoteDirectoryTree;
    }

    public TreeNode getNamndFordelningRemoteDirectoryTree() {
        return namndFordelningRemoteDirectoryTree;
    }

    public void setNamndFordelningRemoteDirectoryTree(TreeNode namndFordelningRemoteDirectoryTree) {
        this.namndFordelningRemoteDirectoryTree = namndFordelningRemoteDirectoryTree;
    }

    public TreeNode getSelectedNamndFordelningDirectory() {
        return selectedNamndFordelningDirectory;
    }

    public void setSelectedNamndFordelningDirectory(TreeNode selectedNamndFordelningDirectory) {
        this.selectedNamndFordelningDirectory = selectedNamndFordelningDirectory;
    }

    public static String getFullPath(TreeNode treeNode) {
        StringBuilder sb = new StringBuilder();

        sb.append(treeNode.getData());

        while (treeNode.getParent() != null && StringUtils.isNotEmpty((String) treeNode.getParent().getData())) {
            sb.insert(0, treeNode.getParent().getData() + "/");

            treeNode = treeNode.getParent();
        }

        // Remove double slashes if existing.
        return sb.toString().replaceAll("//", "/");
    }

    public DistrictDistributionClassName[] getDistrictDistributionClassNameValues() {
        return DistrictDistributionClassName.values();
    }

    public HtmlCommandButton getJustToAssociateMessageWithSomething() {
        return requestScopedModelBean.getJustToAssociateMessageWithSomething();
    }

    public void setJustToAssociateMessageWithSomething(HtmlCommandButton testar) {
        requestScopedModelBean.setJustToAssociateMessageWithSomething(testar);
    }

    public HtmlCommandButton getSaveServerListButton() {
        return requestScopedModelBean.getSaveServerListButton();
    }

    public void setSaveServerListButton(HtmlCommandButton button) {
        requestScopedModelBean.setSaveServerListButton(button);
    }

    public String getServerList() {
        return serverList;
    }

    public void setServerList(String serverList) {
        this.serverList = serverList;
    }
}
