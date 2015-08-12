package se.vgregion.portal.wwwprv.backingbean;

import org.junit.Test;
import se.vgregion.portal.wwwprv.model.jpa.Supplier;
import se.vgregion.portal.wwwprv.util.SharedUploadFolder;

import static org.junit.Assert.*;

/**
 * @author Patrik Björk
 */
public class UploadBackingBeanTest {

    @Test
    public void preProcessFileName() throws Exception {
        Supplier supplier = new Supplier();
        supplier.setSharedUploadFolder(SharedUploadFolder.MARS_SHARED_FOLDER.getIndex());

        String fileName = "asdf.in.txt";
        String result = UploadBackingBean.preProcessFileName(fileName, supplier);
        assertEquals("ASDF.IN", result);

        fileName = "asdf.intxt";
        result = UploadBackingBean.preProcessFileName(fileName, supplier);
        assertEquals("ASDF.INTXT", result);

        fileName = "asdf.fdsa.in.txt";
        result = UploadBackingBean.preProcessFileName(fileName, supplier);
        assertEquals("ASDF.FDSA.IN", result);

        fileName = "c:\\mapp\\asdf.fdsa.in.txt";
        result = UploadBackingBean.preProcessFileName(fileName, supplier);
        assertEquals("ASDF.FDSA.IN", result);

    }
}