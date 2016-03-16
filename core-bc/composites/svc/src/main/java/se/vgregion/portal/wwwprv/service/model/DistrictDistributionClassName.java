package se.vgregion.portal.wwwprv.service.model;

import se.vgregion.portal.wwwprv.service.UnilabsLab;

/**
 * @author Patrik Björk
 */
public enum DistrictDistributionClassName {
    UNILABS_LAB(UnilabsLab.class.getCanonicalName());
//    UNILABS_RONTGEN(UnilabsRontgen.class.getCanonicalName());

    private final String canonicalName;

    DistrictDistributionClassName(String canonicalName) {
        this.canonicalName = canonicalName;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public DistrictDistributionClassName getByCanonicalName(String canonicalName) {
        for (DistrictDistributionClassName name : DistrictDistributionClassName.values()) {
            if (name.getCanonicalName().equals(canonicalName)) {
                return name;
            }
        }

        throw new RuntimeException(canonicalName + " wasn't found.");
    }
}
