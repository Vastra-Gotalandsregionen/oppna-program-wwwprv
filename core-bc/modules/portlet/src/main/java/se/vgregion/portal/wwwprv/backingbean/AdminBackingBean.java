package se.vgregion.portal.wwwprv.backingbean;

import com.liferay.faces.util.portal.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se.vgregion.portal.wwwprv.model.UserContainer;
import se.vgregion.portal.wwwprv.model.jpa.DataPrivataUser;
import se.vgregion.portal.wwwprv.model.jpa.Supplier;
import se.vgregion.portal.wwwprv.repository.DataPrivataRepository;
import se.vgregion.portal.wwwprv.service.LiferayService;
import se.vgregion.portal.wwwprv.service.LiferayServiceException;
import se.vgregion.portal.wwwprv.util.SupplierComparator;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
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
    private DataPrivataRepository repository;

    @Autowired
    private RequestScopedModelBean requestScopedModelBean;

    private Supplier supplierToAdd;
    private String supplierMessage;
    private String userMessage;
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private Map<Long, Boolean> usersSupplierChooserExpanded = new HashMap<>();

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

    public void addSupplier() {

        repository.persistNewSupplier(supplierToAdd);

        String message = "Lyckades lägga till " + supplierToAdd.getEnhetsKod() + ".";

        setSupplierMessage(message);

        supplierToAdd = new Supplier();
    }

    public void saveSupplier(Supplier supplier) {

        repository.saveSupplier(supplier);

        String message = "Lyckades spara " + supplier.getEnhetsKod() + ".";

        setSupplierMessage(message);
    }

    public void removeSupplier(Supplier supplier) {
        try {
            repository.remove(supplier);

            String message = "Lyckades ta bort " + supplier.getEnhetsKod() + ".";

            setSupplierMessage(message);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

    }

    public void saveUser(UserContainer userContainer) {
        repository.saveUser(userContainer.getDataPrivataUser());

        String message = "Lyckades spara till " + userContainer.getLiferayUser().getFullName() + ".";

        setUserMessage(message);
    }

    public List<Supplier> getAllSuppliers() {
        List<Supplier> allSuppliers = repository.getAllSuppliers();
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

        repository.saveUser(dataPrivataUser);

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
}
