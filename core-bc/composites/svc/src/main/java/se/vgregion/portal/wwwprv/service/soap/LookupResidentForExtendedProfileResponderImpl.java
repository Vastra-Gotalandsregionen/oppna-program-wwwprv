package se.vgregion.portal.wwwprv.service.soap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import se.riv.population.residentmaster.lookupresidentforextendedprofile.v1.rivtabp21.LookupResidentForExtendedProfileResponderInterface;
import se.riv.population.residentmaster.lookupresidentforextendedprofileresponder.v1.LookupResidentForExtendedProfileResponseType;
import se.riv.population.residentmaster.lookupresidentforextendedprofileresponder.v1.LookupResidentForExtendedProfileType;
import se.riv.population.residentmaster.lookupresidentforextendedprofileresponder.v1.ObjectFactory;

import javax.xml.bind.JAXBElement;

public class LookupResidentForExtendedProfileResponderImpl extends WebServiceGatewaySupport implements LookupResidentForExtendedProfileResponderInterface {

    private static final Logger log = LoggerFactory.getLogger(LookupResidentForExtendedProfileResponderImpl.class);
    private String url;

    @Override
    public LookupResidentForExtendedProfileResponseType lookupResidentForExtendedProfile(String s,
                                                                                         LookupResidentForExtendedProfileType lookupResidentForExtendedProfileType) {

        JAXBElement<LookupResidentForExtendedProfileType> jaxbElement = new ObjectFactory()
                .createLookupResidentForExtendedProfile(lookupResidentForExtendedProfileType);

        Object object = getWebServiceTemplate().marshalSendAndReceive(url, jaxbElement);

        JAXBElement<LookupResidentForExtendedProfileResponseType> response =
                (JAXBElement<LookupResidentForExtendedProfileResponseType>) object;

        return response.getValue();
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
