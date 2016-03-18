package se.vgregion.portal.wwwprv.service;

import org.springframework.beans.factory.annotation.Autowired;
import se.riv.population.residentmaster.extended.v1.ExtendedResidentType;
import se.riv.population.residentmaster.lookupresidentforextendedprofile.v1.rivtabp21.LookupResidentForExtendedProfileResponderInterface;
import se.riv.population.residentmaster.lookupresidentforextendedprofileresponder.v1.LookupResidentForExtendedProfileResponseType;
import se.riv.population.residentmaster.lookupresidentforextendedprofileresponder.v1.LookupResidentForExtendedProfileType;
import se.riv.population.residentmaster.lookupresidentforfullprofileresponder.v1.LookUpSpecificationType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service to get residential information about patients in the region.
 */
public class PopulationService {

    @Autowired
    private LookupResidentForExtendedProfileResponderInterface extendedProfileClient;

    private static int maxSocialSecurityNumbersAlowedForWsCall = 1000;

    /**
     * Makes a WS-call to get information about a number of residents.
     *
     * @param bySocialSecurityNumbers a list of personal numbers to get information about.
     * @return an object graph describing where a person lives an additional information about what administrative
     * units that takes responsibility for the area where the person lives.
     */
    public LookupResidentForExtendedProfileResponseType lookup(List<String> bySocialSecurityNumbers) {
        bySocialSecurityNumbers = new ArrayList<>(bySocialSecurityNumbers);

        LookupResidentForExtendedProfileResponseType result = new LookupResidentForExtendedProfileResponseType();
        while (!bySocialSecurityNumbers.isEmpty()) {
            LookupResidentForExtendedProfileType arg = new LookupResidentForExtendedProfileType();

            LookUpSpecificationType spec = new LookUpSpecificationType();
            arg.setLookUpSpecification(spec);
            List<String> partial = bySocialSecurityNumbers.subList(0, Math.min(bySocialSecurityNumbers.size(), maxSocialSecurityNumbersAlowedForWsCall));
            arg.getPersonId().addAll(partial);

            result.getResident().addAll(extendedProfileClient.lookupResidentForExtendedProfile("", arg).getResident());
            partial.clear();
        }

        return result;
    }

    /**
     * see {@link #lookup(List<String>) lookup}
     *
     * @param bySocialSecurityNumbers se above...
     * @return se above...
     */
    public LookupResidentForExtendedProfileResponseType lookup(String... bySocialSecurityNumbers) {
        return lookup(Arrays.asList(bySocialSecurityNumbers));
    }

    /**
     * Makes a WS-call to get information about a number of residents.
     *
     * @param bySocialSecurityNumbersAndStringDates a list of personal numbers+dates to get information about.
     * @return an object graph describing where a person lives an additional information about what administrative
     * units that takes responsibility for the area where the person lives.
     */
    public List<ExtendedResidentType> lookup(Arg ... bySocialSecurityNumbersAndStringDates) {
        List<ExtendedResidentType> result = new ArrayList<>();
        for (Arg arg : bySocialSecurityNumbersAndStringDates) {
            LookupResidentForExtendedProfileType callArg = new LookupResidentForExtendedProfileType();
            LookUpSpecificationType spec = new LookUpSpecificationType();
            callArg.setLookUpSpecification(spec);
            callArg.getPersonId().add(arg.personalNumber);
            // spec.setSenasteAndringFolkbokforing(arg.textDate);
            spec.setHistoriskTidpunkt(arg.textDate);
            List<ExtendedResidentType> shouldJustBeOneOrNone = extendedProfileClient.lookupResidentForExtendedProfile("", callArg).getResident();
            if (shouldJustBeOneOrNone.size() > 1) {
                throw new RuntimeException();
            }
            if (shouldJustBeOneOrNone.isEmpty()) {
                result.add(null);
            } else {
                result.addAll(shouldJustBeOneOrNone);
            }
        }
        return result;
    }

    /**
     * Argument holder for usage with PopulationService.
     */
    public static class Arg {

        public String personalNumber;
        public String textDate;

        public Arg() {
            super();
        }

        public Arg(String personalNumber, String textDate) {
            super();
            this.personalNumber = personalNumber;
            this.textDate = textDate;
        }

    }

}