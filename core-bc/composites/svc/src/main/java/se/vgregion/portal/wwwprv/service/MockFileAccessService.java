package se.vgregion.portal.wwwprv.service;

import org.apache.commons.io.output.NullOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.vgregion.portal.wwwprv.model.Tree;
import se.vgregion.portal.wwwprv.model.jpa.Supplier;
import se.vgregion.portal.wwwprv.util.Notifiable;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Patrik Björk
 */
public class MockFileAccessService implements FileAccessService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockFileAccessService.class);

    @Override
    public void uploadFile(String fileName, Supplier supplier, InputStream inputStream, long fileSize, Notifiable notifiable) {

        LOGGER.info("Uploading using " + MockFileAccessService.class.getName());

        try (OutputStream os = new NullOutputStream();) {

            byte[] buf = new byte[4096];

            int n;
            long accumulatedBytes = 0;
            int numberRoundsSoFar = 0;

            while ((n = inputStream.read(buf)) != -1) {
                accumulatedBytes += n;
                os.write(buf, 0, n);

                Thread.sleep(1);

                if (++numberRoundsSoFar % 10 == 0) {
                    notifiable.notifyPercentage((int) ((100f * (float) accumulatedBytes) / (float) fileSize));
                }
            }

            notifiable.notifyPercentage(100);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public Tree<String> retrieveRemoteFileTree() {

        Tree<String> tree = new Tree<>("Rotmapp/");

        List<Tree.Node<String>> level1 = tree.getRoot().getChildren();

        Tree.Node<String> dir1 = new Tree.Node<>("Mapp1/");
        level1.add(dir1);
        level1.add(new Tree.Node<>("Mapp2/"));
        level1.add(new Tree.Node<>("Mapp3/"));

        Tree.Node<String> dir12 = new Tree.Node<>("mapp1-2/");
        List<Tree.Node<String>> level2 = dir1.getChildren();
        level2.add(dir12);
        level2.add(new Tree.Node<>("mapp1-3/"));

        List<Tree.Node<String>> level3 = dir12.getChildren();
        level3.add(new Tree.Node<>("mapp1-3-1/"));
        level3.add(new Tree.Node<>("mapp1-3-2/"));

        return tree;
    }

}
