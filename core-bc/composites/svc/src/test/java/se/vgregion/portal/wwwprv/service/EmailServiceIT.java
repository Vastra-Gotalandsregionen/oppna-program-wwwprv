package se.vgregion.portal.wwwprv.service;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.vgregion.portal.wwwprv.model.jpa.Supplier;

/**
 * @author Patrik Bergström
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-test.xml")
public class EmailServiceIT {

    @Autowired
    private EmailService service;

    @Test
    @Ignore
    public void testNotifyNewUpload() throws Exception {

        Supplier supplier = new Supplier();
        supplier.setSharedUploadFolder((short) 1);
        service.notifyNewUpload("TestFil.txt", supplier, "Test Testsson");
    }
}