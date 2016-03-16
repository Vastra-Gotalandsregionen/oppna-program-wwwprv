package se.vgregion.portal.wwwprv.service;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.vgregion.portal.wwwprv.model.Node;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertTrue;

/**
 * @author Patrik Björk
 */
public class RemoteFileAccessServiceIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteFileAccessServiceIT.class);

    private final String SHARED_FOLDER_SERVER = "VGFS0233";

    @Test
    public void testRetrieveRemoteFileTree() throws Exception {
        Properties properties = loadProperties();

        RemoteFileAccessService remoteFileAccessService = new RemoteFileAccessService(
                properties.getProperty("shared.folder.username"),
                null, // todo These parameters should be removed. I'll wait until the merge to avoid conflicts.
                properties.getProperty("shared.folder.password"),
                null,
                null,
                null
        );

        Node<String> tree = remoteFileAccessService.retrieveRemoteFileTree(SHARED_FOLDER_SERVER);

        assertTrue(tree.getChildren() != null && tree.getChildren().size() > 0);

        print(tree);
    }

    private Properties loadProperties() throws IOException {
        Properties properties = new Properties();

        File propertyFile = new File(System.getProperty("user.home") + "/.hotell/wwwprv/secret.properties");

        try (FileInputStream fis = new FileInputStream(propertyFile)) {
            properties.load(fis);
        } catch (Exception e) {
            LOGGER.error("Failed to load properties. Make sure to place a secret.properties file in" +
                    " ${user.home)/.hotell/wwwprv/");
        }

        return properties;
    }

    private void print(Node<String> tree) {
        StringBuilder sb = new StringBuilder();

        sb.insert(0, tree.getData());

        Node<String> parent = tree.getParent();
        while (parent != null) {
            sb.insert(0, parent.getData());
            parent = parent.getParent();
        }

        LOGGER.info(sb.toString());

        if (tree.getChildren() != null && tree.getChildren().size() > 0) {
            for (Node<String> stringNode : tree.getChildren()) {
                print(stringNode);
            }
        }
    }
}