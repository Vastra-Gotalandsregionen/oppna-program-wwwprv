package se.vgregion.portal.wwwprv.backingbean;

import com.liferay.faces.util.portal.WebKeys;
import com.liferay.portal.model.User;
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

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
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

    private Supplier supplierToAdd;
    private String supplierMessage;
    private String userMessage;
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

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
//        FacesContext.getCurrentInstance().addMessage(":theForm:usersMessage", new FacesMessage("Lyckades spara till " + userContainer.getLiferayUser().getFullName() + "."));

        String message = "Lyckades spara till " + userContainer.getLiferayUser().getFullName() + ".";

        setUserMessage(message);
    }

    public List<Supplier> getAllSuppliers() {
        List<Supplier> allSuppliers = repository.getAllSuppliers();
        Collections.sort(allSuppliers, new Comparator<Supplier>() {
            @Override
            public int compare(Supplier o1, Supplier o2) {
                return o1.getEnhetsKod().toLowerCase().compareTo(o2.getEnhetsKod().toLowerCase());
            }
        });
        return allSuppliers;
    }

    public Supplier getSupplierToAdd() {
        if (supplierToAdd == null) {
            supplierToAdd = new Supplier();
        }
        return supplierToAdd;
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
