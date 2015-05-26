package se.vgregion.portal.wwwprv.jsf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.vgregion.portal.wwwprv.model.jpa.Supplier;
import se.vgregion.portal.wwwprv.service.DataPrivataService;
import se.vgregion.portal.wwwprv.util.SharedUploadFolder;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * @author Patrik Bergström
 */
@Component
public class SharedUploadFolderConverter implements Converter {

    @Override
    public Short getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || "".equals(value) || "Välj...".equals(value)) {
            return null;
        }

        return SharedUploadFolder.getSharedUploadFolder(value).getIndex();
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || "".equals(value)) {
            return null;
        }

        return SharedUploadFolder.getSharedUploadFolder((Short) value).getLabel();
    }
}
