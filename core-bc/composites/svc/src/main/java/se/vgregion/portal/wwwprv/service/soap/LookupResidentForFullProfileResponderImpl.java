package se.vgregion.portal.wwwprv.service.soap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import se.riv.population.residentmaster.lookupresidentforfullprofile.v1.rivtabp21.LookupResidentForFullProfileResponderInterface;
import se.riv.population.residentmaster.lookupresidentforfullprofileresponder.v1.LookupResidentForFullProfileResponseType;
import se.riv.population.residentmaster.lookupresidentforfullprofileresponder.v1.LookupResidentForFullProfileType;
import se.riv.population.residentmaster.lookupresidentforfullprofileresponder.v1.ObjectFactory;

import javax.xml.bind.JAXBElement;

public class LookupResidentForFullProfileResponderImpl extends WebServiceGatewaySupport implements LookupResidentForFullProfileResponderInterface {

    private static final Logger log = LoggerFactory.getLogger(LookupResidentForFullProfileResponderImpl.class);
    private String url;

    @Override
    public LookupResidentForFullProfileResponseType lookupResidentForFullProfile(String s,
                                                                                 LookupResidentForFullProfileType lookupResidentForFullProfileType) {

        JAXBElement<LookupResidentForFullProfileType> jaxbElement = new ObjectFactory()
                .createLookupResidentForFullProfile(lookupResidentForFullProfileType);

        Object object = getWebServiceTemplate()
                .marshalSendAndReceive(url, jaxbElement);

        JAXBElement<LookupResidentForFullProfileResponseType> response =
                (JAXBElement<LookupResidentForFullProfileResponseType>) object;

        return response.getValue();
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
