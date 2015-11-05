
import se.riv.population.residentmaster.lookupresidentforfullprofile.v1.rivtabp21.LookupResidentForFullProfileResponderInterface;
import se.riv.population.residentmaster.lookupresidentforfullprofile.v1.rivtabp21.LookupResidentForFullProfileResponderService;
import se.riv.population.residentmaster.lookupresidentforfullprofileresponder.v1.LookUpSpecificationType;
import se.riv.population.residentmaster.lookupresidentforfullprofileresponder.v1.LookupResidentForFullProfileResponseType;
import se.riv.population.residentmaster.lookupresidentforfullprofileresponder.v1.LookupResidentForFullProfileType;
import se.riv.population.residentmaster.v1.JaNejTYPE;
import se.riv.population.residentmaster.v1.ResidentType;
import se.riv.population.residentmaster.v1.SvenskAdressTYPE;

import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * @author Patrik Bergström
 */
public class Test {

    /*public static void main(String[] args) throws MalformedURLException {

        String logicalAddress = "logicalAddress";

        String historiskTidpunkt = "1900-01-01"; // Kanske ska vara en tidpunkt per person?
        JaNejTYPE sekretessmarkering = null; // Frivilligt?
        String senasteAndringFolkbokforing = null;
        String[] personNummers = new String[]{"19121212-1212", "20101010-1010"};

        LookupResidentForFullProfileResponseType fullProfileResponse = lookupResidentForFullProfile(
                logicalAddress,
                historiskTidpunkt,
                sekretessmarkering,
                senasteAndringFolkbokforing,
                personNummers);

        List<ResidentType> residents = fullProfileResponse.getResident();

        for (ResidentType resident : residents) {
            SvenskAdressTYPE folkbokforingsadress = resident.getPersonpost().getFolkbokforingsadress();

            String kommunKod = folkbokforingsadress.getKommunKod();
            String lanKod = folkbokforingsadress.getLanKod();
            String nyckelKod = folkbokforingsadress.getSCBNyckelkod();

            LookupDistrictInformationResponseType districtResponse = lookupDistrictInformation(
                    logicalAddress,
                    kommunKod,
                    lanKod,
                    nyckelKod);

            String namndId = districtResponse.getNamnd().getNamndId();

            System.out.println(namndId);
        }

    }

    private static LookupDistrictInformationResponseType lookupDistrictInformation(String logicalAddress, String kommunKod, String lanKod, String nyckelKod) throws MalformedURLException {
        Service service = LookupDistrictInformationResponderService.create(new URL("localhost:8888"),
                LookupDistrictInformationResponderService.SERVICE);

        LookupDistrictInformationResponderInterface port = service.getPort(
                LookupDistrictInformationResponderInterface.class);

        LookupDistrictInformationType lookupDistrictInformationType = new ObjectFactory()
                .createLookupDistrictInformationType();

        lookupDistrictInformationType.setKommunKod(kommunKod);
        lookupDistrictInformationType.setLanKod(lanKod);
        lookupDistrictInformationType.setNyckelKod(nyckelKod);

        return port.lookupDistrictInformation(logicalAddress, lookupDistrictInformationType);
    }

    private static LookupResidentForFullProfileResponseType lookupResidentForFullProfile(
            String logicalAddress,
            String historiskTidpunkt,
            JaNejTYPE sekretessmarkering,
            String senasteAndringFolkbokforing,
            String... personNummers) throws MalformedURLException {

        Service lookupResidentForFullProfileResponderService = LookupResidentForFullProfileResponderService.create(
                new URL("localhost:8888"), LookupResidentForFullProfileResponderService.SERVICE);

        LookupResidentForFullProfileResponderInterface port = lookupResidentForFullProfileResponderService.getPort(
                LookupResidentForFullProfileResponderInterface.class);

        riv.population.residentmaster.lookupresidentforfullprofileresponder._1.ObjectFactory objectFactory =
                new riv.population.residentmaster.lookupresidentforfullprofileresponder._1.ObjectFactory();

        LookUpSpecificationType lookUpSpecification = objectFactory.createLookUpSpecificationType();

        lookUpSpecification.setHistoriskTidpunkt(historiskTidpunkt);
        lookUpSpecification.setSekretessmarkering(sekretessmarkering);
        lookUpSpecification.setSenasteAndringFolkbokforing(senasteAndringFolkbokforing);

        LookupResidentForFullProfileType lookUpSpecificationForFullProfile = objectFactory.createLookupResidentForFullProfileType();

        lookUpSpecificationForFullProfile.setLookUpSpecification(lookUpSpecification);

        for (String personNummer : personNummers) {
            lookUpSpecificationForFullProfile.getPersonId().add(personNummer);
        }

        LookupResidentForFullProfileResponseType response = port.lookupResidentForFullProfile(logicalAddress,
                lookUpSpecificationForFullProfile);

        return response;
    }*/
}
