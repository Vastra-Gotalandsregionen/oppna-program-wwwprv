package se.vgregion.portal.wwwprv.jsf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.vgregion.portal.wwwprv.model.jpa.Supplier;
import se.vgregion.portal.wwwprv.service.DataPrivataService;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * @author Patrik Bergström
 */
@Component
public class SupplierConverter implements Converter {

    @Autowired
    private DataPrivataService dataPrivataService;

    @Override
    public Supplier getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || "".equals(value)) {
            return null;
        }
        return dataPrivataService.findSupplierById(Integer.parseInt(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || "".equals(value)) {
            return "";
        }
        return ((Supplier) value).getId() + "";
    }
}
