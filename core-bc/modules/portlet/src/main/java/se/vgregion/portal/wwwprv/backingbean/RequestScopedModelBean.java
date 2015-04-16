package se.vgregion.portal.wwwprv.backingbean;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se.vgregion.portal.wwwprv.model.UserContainer;
import se.vgregion.portal.wwwprv.model.jpa.Supplier;

import java.util.Map;

/**
 * @author Patrik Bergström
 */
@Component
@Scope("request")
public class RequestScopedModelBean {

    private Map<UserContainer, Map<Supplier, Boolean>> userWithSuppliersHelper;

    public Map<UserContainer, Map<Supplier, Boolean>> getUserWithSuppliersHelper() {
        return userWithSuppliersHelper;
    }

    public void setUserWithSuppliersHelper(Map<UserContainer, Map<Supplier, Boolean>> userWithSuppliersHelper) {
        this.userWithSuppliersHelper = userWithSuppliersHelper;
    }
}
