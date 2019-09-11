package se.vgregion.portal.wwwprv.mock;

import se.riv.population.residentmaster.lookupresidentforextendedprofile.v1.rivtabp21.LookupResidentForExtendedProfileResponderInterface;
import se.riv.population.residentmaster.lookupresidentforextendedprofileresponder.v1.LookupResidentForExtendedProfileResponseType;
import se.riv.population.residentmaster.lookupresidentforextendedprofileresponder.v1.LookupResidentForExtendedProfileType;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;

@WebService(targetNamespace = "urn:riv:population:residentmaster:LookupResidentForExtendedProfile:1:rivtabp21")
public class MockLookupResidentForExtendedProfileResponderInterface implements LookupResidentForExtendedProfileResponderInterface {

    public static void main(String[] args) throws InterruptedException {
        Endpoint.publish("http://localhost:8081", new MockLookupResidentForExtendedProfileResponderInterface());
        Thread.sleep(1000000000);
    }

    @Override
    public LookupResidentForExtendedProfileResponseType lookupResidentForExtendedProfile(
            String s, LookupResidentForExtendedProfileType lookupResidentForExtendedProfileType) {

        return new LookupResidentForExtendedProfileResponseType();
    }
}
