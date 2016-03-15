package se.vgregion.portal.wwwprv.service;

import org.junit.Test;
import org.mockito.Mockito;
import se.vgregion.portal.wwwprv.model.jpa.GlobalSetting;
import se.vgregion.portal.wwwprv.model.jpa.Supplier;
import se.vgregion.portal.wwwprv.util.SharedUploadFolder;

import javax.persistence.EntityManager;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Patrik Bergström
 */
public class DataPrivataServiceTest {

    @Test
    public void testVerifyFileNameOk() throws Exception {

        Supplier s1 = new Supplier();
        s1.setEnhetsKod("aSdF");
        s1.setSharedUploadFolder(SharedUploadFolder.MARS_SHARED_FOLDER.getIndex());

        DataPrivataService.verifyFileName("asdf_alefj.txt", s1);

        s1.setSharedUploadFolder(SharedUploadFolder.AVESINA_SHARED_FOLDER.getIndex());

        DataPrivataService.verifyFileName("asdf_151027.in", s1);

        // Next
        s1.setEnhetsKod("STMGBG15SVK");
        s1.setSharedUploadFolder(SharedUploadFolder.MARS_SHARED_FOLDER.getIndex());

        DataPrivataService.verifyFileName("STMGBG15SVK20150804.in", s1);
    }

    @Test
    public void testVerifyFileNameFail() throws Exception {

        Supplier s1 = new Supplier();
        s1.setEnhetsKod("aSdF");
        s1.setSharedUploadFolder(SharedUploadFolder.MARS_SHARED_FOLDER.getIndex());

        try {
            DataPrivataService.verifyFileName("fdsa_alefj.txt", s1);
            fail();
        } catch (IllegalArgumentException e) {

        }

        s1.setSharedUploadFolder(SharedUploadFolder.AVESINA_SHARED_FOLDER.getIndex());

        try {
            DataPrivataService.verifyFileName("asdf_alefj.txt", s1);
            fail();
        } catch (IllegalArgumentException e) {

        }

        try {
            DataPrivataService.verifyFileName("asdf_151027.txt", s1); // Should end with .in
            fail();
        } catch (IllegalArgumentException e) {

        }

        try {
            DataPrivataService.verifyFileName("asdf_151047.in", s1); // No 47th day in month
            fail();
        } catch (IllegalArgumentException e) {

        }

        try {
            DataPrivataService.verifyFileName("asdf_151027.inf", s1); // Should end with .in
            fail();
        } catch (IllegalArgumentException e) {

        }

    }

    @Test
    public void testGetServerList() {
        DataPrivataService service = new DataPrivataService();

        EntityManager mock = Mockito.mock(EntityManager.class);

        Mockito.when(mock.find(Mockito.eq(GlobalSetting.class), Mockito.eq("server-list"))).thenReturn(null);

        service.setEntityManager(mock);

        List<String> serverList = service.getServerList();

        assertTrue(serverList.size() == 0);
    }

}