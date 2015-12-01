package se.vgregion.portal.wwwprv.service;

import org.apache.commons.collections.BeanMap;
import org.springframework.beans.factory.annotation.Autowired;
import se.riv.population.residentmaster.lookupresidentforextendedprofile.v1.rivtabp21.LookupResidentForExtendedProfileResponderInterface;
import se.riv.population.residentmaster.lookupresidentforextendedprofileresponder.v1.LookupResidentForExtendedProfileResponseType;
import se.riv.population.residentmaster.lookupresidentforextendedprofileresponder.v1.LookupResidentForExtendedProfileType;
import se.riv.population.residentmaster.lookupresidentforfullprofileresponder.v1.LookUpSpecificationType;

import java.util.Arrays;
import java.util.List;

public class PopulationService {

    @Autowired
    private LookupResidentForExtendedProfileResponderInterface extendedProfileClient;

    public LookupResidentForExtendedProfileResponseType lookup(List<String> bySocialSecurityNumbers) {
        LookupResidentForExtendedProfileType arg = new LookupResidentForExtendedProfileType();

        LookUpSpecificationType spec = new LookUpSpecificationType();
        arg.setLookUpSpecification(spec);

        arg.getPersonId().addAll(bySocialSecurityNumbers);
        return extendedProfileClient.lookupResidentForExtendedProfile("", arg);
    }

    public LookupResidentForExtendedProfileResponseType lookup(String... bySocialSecurityNumbers) {
        return lookup(Arrays.asList(bySocialSecurityNumbers));
    }

}