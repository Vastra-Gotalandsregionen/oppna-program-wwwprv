package se.vgregion.portal.wwwprv.service;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.vgregion.portal.wwwprv.model.jpa.Supplier;
import se.vgregion.portal.wwwprv.util.Callback;
import se.vgregion.portal.wwwprv.util.Notifiable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by clalu4 on 2015-11-30.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:population-test.xml")
public class LocalFileAccessServiceTestIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalFileAccessServiceTestIT.class);

    @Autowired
    LocalFileAccessService service;

    //@Ignore
    @Test
    public void uploadFile() throws InterruptedException, IOException, ExecutionException {
        assertNotNull(service);
        service.setLocalStoragePath(System.getProperty("user.home") + File.separator + ".hotell" + File.separator + "wwwprv" + File.separator + "junit-test-output");

        String fileName = "Unilabs_S50MA50_201510_Lab_20151023_1252.in";
        InputStream testContent = (getClass().getResourceAsStream(fileName));

        Notifiable notfyer = new Notifiable() {
            @Override
            public void notifyPercentage(int percentage) {

            }
        };

        Supplier unilabs = new Supplier();
        unilabs.setEnhetsKod("Unilabs_S50MA50");

        Future<?> future = service.uploadFileInBackground(fileName,
                unilabs,
                testContent,
                IOUtils.toByteArray(getClass().getResourceAsStream(fileName)).length,
                "namndfordelningsdir",
                "uploader",
                notfyer,
                new Callback() {
                    @Override
                    public void callback() {
                        LOGGER.info("Callback run.");
                    }
                });

        future.get();

        //BoardDistributionService.getCurrentDistributionFileContentWorker().get().join();

        File unprocessed = new File(service.getLocalStoragePath() + File.separator + fileName);
        assertTrue(unprocessed.exists());

        File processed = new File(service.getLocalStoragePath() + File.separator + fileName + ".board-distributed");
        assertTrue(processed.exists());
    }

}