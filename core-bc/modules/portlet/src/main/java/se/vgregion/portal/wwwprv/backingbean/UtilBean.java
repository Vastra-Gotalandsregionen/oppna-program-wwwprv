package se.vgregion.portal.wwwprv.backingbean;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se.vgregion.portal.wwwprv.model.UserContainer;
import se.vgregion.portal.wwwprv.model.jpa.Supplier;

import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Patrik Bergström
 */
@Component
@Scope("request")
public class UtilBean {

    public static String chosenSupplierString(UserContainer userContainer) {
        Set<Supplier> suppliers = userContainer.getDataPrivataUser().getSuppliers();

        if (suppliers == null || suppliers.size() == 0) {
            return "Klicka för att välja";
        }

        List<String> names = new ArrayList<>();

        for (Supplier supplier : suppliers) {
            names.add(supplier.getEnhetsNamn());
        }

        Collections.sort(names);

        return StringUtils.join(names, ", ");
    }

    @SuppressWarnings("unchecked")
    public static <T> T findBean(String beanName) {
        FacesContext context = FacesContext.getCurrentInstance();
        return (T) context.getApplication().evaluateExpressionGet(context, "#{" + beanName + "}", Object.class);
    }

    public static String getUserId() {
        return ((PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest())
                .getRemoteUser();
    }

}
