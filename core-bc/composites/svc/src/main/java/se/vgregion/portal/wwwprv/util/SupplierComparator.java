package se.vgregion.portal.wwwprv.util;

import se.vgregion.portal.wwwprv.model.jpa.Supplier;

import java.util.Comparator;

/**
 * @author Patrik Bergström
 */
public class SupplierComparator implements Comparator<Supplier> {
    @Override
    public int compare(Supplier o1, Supplier o2) {
        return o1.getEnhetsKod().toLowerCase().compareTo(o2.getEnhetsKod().toLowerCase());
    }
}
