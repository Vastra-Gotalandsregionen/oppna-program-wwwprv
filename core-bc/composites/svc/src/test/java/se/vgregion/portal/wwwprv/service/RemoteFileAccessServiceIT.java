package se.vgregion.portal.wwwprv.service;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.vgregion.portal.wwwprv.model.Node;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import static org.junit.Assert.assertTrue;

/**
 * @author Patrik Björk
 */
public class RemoteFileAccessServiceIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteFileAccessServiceIT.class);

    String remoteDirList = "vgregion.se/app/DATAPRIVATA.INFILER/,vgregion.se/app/DATAPRIVATA.Halland_o_Koptvard/,vgregion.se/app/DATAPRIVATA.Integration/,vgregion.se/app/DATAPRIVATA.NAMNDFORDELNING/";

    @Test
    @Ignore // It takes a lot of time to run locally. Possibly because of network latency. The problem isn't as clear
    // in the runtime servers.
    public void testRetrieveRemoteFileTree() throws Exception {
        Properties properties = loadProperties();

        RemoteFileAccessService remoteFileAccessService = new RemoteFileAccessService(
                properties.getProperty("shared.folder.username"),
                properties.getProperty("shared.folder.password"),
                properties.getProperty("shared.folder.domain")
        );


        Arrays.asList(remoteDirList.split(",")).stream()
                .forEach(share -> {
                    Node<String> tree = remoteFileAccessService.retrieveRemoteFileTree(share);

                    assertTrue(share + " failed.", tree.getChildren() != null && tree.getChildren().size() >= 0);

                    print(tree);
                });
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