package se.vgregion.portal.wwwprv.backingbean;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import se.vgregion.portal.wwwprv.model.UserContainer;
import se.vgregion.portal.wwwprv.model.jpa.Supplier;

import javax.faces.component.html.HtmlCommandButton;
import java.util.Map;

/**
 * @author Patrik Bergström
 */
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestScopedModelBean {

    private HtmlCommandButton justToAssociateMessageWithSomething;
    private Map<UserContainer, Map<Supplier, Boolean>> userWithSuppliersHelper;

    public HtmlCommandButton getJustToAssociateMessageWithSomething() {
        return justToAssociateMessageWithSomething;
    }

    public void setJustToAssociateMessageWithSomething(HtmlCommandButton justToAssociateMessageWithSomething) {
        this.justToAssociateMessageWithSomething = justToAssociateMessageWithSomething;
    }

    public Map<UserContainer, Map<Supplier, Boolean>> getUserWithSuppliersHelper() {
        return userWithSuppliersHelper;
    }

    public void setUserWithSuppliersHelper(Map<UserContainer, Map<Supplier, Boolean>> userWithSuppliersHelper) {
        this.userWithSuppliersHelper = userWithSuppliersHelper;
    }
}
