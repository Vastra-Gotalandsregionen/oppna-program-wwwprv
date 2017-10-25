package se.vgregion.portal.wwwprv.service;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(PopulationService.class);

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
    public List<ExtendedResidentType> lookup(Arg... bySocialSecurityNumbersAndStringDates) {
        List<ExtendedResidentType> result = new ArrayList<>();
        for (Arg arg : bySocialSecurityNumbersAndStringDates) {
            LookupResidentForExtendedProfileType callArg = new LookupResidentForExtendedProfileType();
            LookUpSpecificationType spec = new LookUpSpecificationType();
            callArg.setLookUpSpecification(spec);
            callArg.getPersonId().add(arg.personalNumber);
            // spec.setSenasteAndringFolkbokforing(arg.textDate);
            // 20120101. Förväntat format: yyyyMMddHHmmss
            if (arg.textDate != null) {
                arg.textDate = StringUtils.rightPad(arg.textDate, 14, '0');
            }
            spec.setHistoriskTidpunkt(arg.textDate);

            List<ExtendedResidentType> shouldJustBeOneOrNone = null;
            for (int i = 0; i < 5; i++) {
                try {
                    shouldJustBeOneOrNone = extendedProfileClient.lookupResidentForExtendedProfile("", callArg).getResident();
                    if (i > 0) {
                        LOGGER.info("Succeded with lookupResidentForExtendedProfile after " + (i + 1) + " tries.");
                    }
                    break;
                } catch (Exception e) {
                    if (i == 4) {
                        throw e;
                    } else {
                        LOGGER.error("Try number " + (i + 1) + ". Failed to lookupResidentForExtendedProfile. Will retry...");
                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException e1) {
                            LOGGER.error(e.getMessage(), e);
                        }
                    }
                }
            }
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

        /**
         * No args constructor.
         */
        public Arg() {
            super();
        }


        /**
         * Constructor that provides the actual data-content of the instance at the moment of creation.
         * @param personalNumber in swedish 'personnummer'.
         * @param textDate date of usage - of the personal number provided as first argument.
         */
        public Arg(String personalNumber, String textDate) {
            super();
            this.personalNumber = personalNumber;
            this.textDate = textDate;
        }

    }

}