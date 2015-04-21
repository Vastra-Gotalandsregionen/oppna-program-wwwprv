package se.vgregion.portal.wwwprv.backingbean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se.vgregion.portal.wwwprv.model.jpa.DataPrivataUser;
import se.vgregion.portal.wwwprv.model.jpa.Supplier;
import se.vgregion.portal.wwwprv.repository.DataPrivataRepository;
import se.vgregion.portal.wwwprv.util.SupplierComparator;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Patrik Bergström
 */

@Component
@Scope("request")
public class UploadRequestScopeBackingBean {

    @Autowired
    private DataPrivataRepository repository;

    private Set<Supplier> usersSuppliers;

    @PostConstruct
    public void init() {
        DataPrivataUser dataPrivataUser = repository.getUserById(Long.valueOf(UtilBean.getUserId()));

        usersSuppliers = dataPrivataUser.getSuppliers();
    }

    public List<Supplier> getUsersSuppliers() {
        if (usersSuppliers == null) {
            init();
        }

        List<Supplier> suppliers = new ArrayList<>(usersSuppliers);

        Collections.sort(suppliers, new SupplierComparator());

        return suppliers;
    }

}
