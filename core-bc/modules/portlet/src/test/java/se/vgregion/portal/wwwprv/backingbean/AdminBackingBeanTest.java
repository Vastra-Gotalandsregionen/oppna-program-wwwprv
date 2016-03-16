package se.vgregion.portal.wwwprv.backingbean;

import org.junit.Test;
import org.mockito.Mockito;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.vgregion.portal.wwwprv.model.Node;
import se.vgregion.portal.wwwprv.model.jpa.GlobalSetting;
import se.vgregion.portal.wwwprv.service.DataPrivataService;
import se.vgregion.portal.wwwprv.service.MockFileAccessService;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * @author Patrik Björk
 */
public class AdminBackingBeanTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminBackingBeanTest.class);

    @Test
    public void testGetFileTree() throws Exception {
        
        // Setup
        MockFileAccessService mockFileAccessService = new MockFileAccessService();

        DataPrivataService dataPrivataService = new DataPrivataService(null, mockFileAccessService);

        EntityManager entityManager = Mockito.mock(EntityManager.class);
        when(entityManager.find(GlobalSetting.class, "namnd-fordelnings-directory"))
                .thenReturn(new GlobalSetting("namnd-fordelnings-directory", "mumbojumbo"));

        Mockito.when(entityManager.find(Mockito.eq(GlobalSetting.class), Mockito.eq("server-list"))).thenReturn(new GlobalSetting("server-list", "dummy"));

        dataPrivataService.setEntityManager(entityManager);
        
        AdminBackingBean adminBackingBean = new AdminBackingBean(null, dataPrivataService);

        // Fetch of the remote file tree is made here.
        adminBackingBean.init();
        
        // Verify
        TreeNode fileTree = adminBackingBean.getRemoteDirectoryTree(); // Primefaces TreeNode

        Node<String> correctTree = mockFileAccessService.retrieveRemoteFileTree("dummy");

        Node<String> correctTreeRoot = new Node<>();
        correctTreeRoot.add(correctTree);

        // Root level
        LOGGER.info("Root level: " + correctTreeRoot.getData() + " - " + fileTree.getData());
        assertEquals(correctTreeRoot.getData(), fileTree.getData());

        // We make it iteratively as opposed to recursive here since the recursive method in runtime is what we test.
        // First level
        List<TreeNode> toVerify = fileTree.getChildren();
        List<Node<String>> verifyAgainst = correctTreeRoot.getChildren();

        for (int i = 0; i < verifyAgainst.size(); i++) {

            LOGGER.info("First level: " + verifyAgainst.get(i).getData() + " - " + toVerify.get(i).getData());
            assertEquals(verifyAgainst.get(i).getData(), toVerify.get(i).getData());

            // Second level
            for (int j = 0; j < verifyAgainst.size(); j++) {
                List<TreeNode> toVerify1 = toVerify.get(j).getChildren();
                List<Node<String>> verifyAgainst1 = verifyAgainst.get(j).getChildren();

                for (int k = 0; k < verifyAgainst1.size(); k++) {
                    LOGGER.info("Second level: " + verifyAgainst1.get(k).getData() + " - " + toVerify1.get(k).getData());
                    assertEquals(verifyAgainst1.get(k).getData(), toVerify1.get(k).getData());
                }
            }
        }
    }
}