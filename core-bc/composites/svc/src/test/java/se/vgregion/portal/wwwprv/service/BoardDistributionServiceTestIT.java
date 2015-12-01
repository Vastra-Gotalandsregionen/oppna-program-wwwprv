package se.vgregion.portal.wwwprv.service;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.vgregion.portal.wwwprv.util.Text;

import static org.junit.Assert.*;

/**
 * Created by clalu4 on 2015-11-30.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:population-test.xml")
public class BoardDistributionServiceTestIT {

    @Autowired
    BoardDistributionService distributionService;

    @Ignore
    @Test
    public void makeDistributionFileContent() throws Exception {
        assertNotNull(distributionService);
        Text testContent = new Text(getClass().getResourceAsStream("Unilabs_S50MA50_201510_Lab_20151023_1252.in"));
        Text result = distributionService.makeDistributionFileContent(testContent, "Unilabs_S50MA50_201510_Lab_20151023_1252.in", "Unilabs_S50MA50");
        assertNotNull(result);
        // Text expexted = new Text(getClass().getResourceAsStream("Facit/Unilabs_S50MA50_2015107_nämndfördelning_20151102_lev_20151023.txt.rpt"));
        // assertEquals(expexted, result);
    }

}