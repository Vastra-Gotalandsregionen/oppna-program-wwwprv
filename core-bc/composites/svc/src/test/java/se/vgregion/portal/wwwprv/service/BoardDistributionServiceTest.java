package se.vgregion.portal.wwwprv.service;

import com.google.gson.Gson;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.io.output.NullOutputStream;
import org.junit.Assert;
import org.junit.Test;
import se.riv.population.residentmaster.lookupresidentforextendedprofileresponder.v1.LookupResidentForExtendedProfileResponseType;
import se.vgregion.portal.wwwprv.util.Text;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by clalu4 on 2015-12-01.
 */
public class BoardDistributionServiceTest {

    @Test
    public void runMakeDistributionFileContent() throws Exception {
        //Text testContent = new Text(Testing.getUnilabsSourceStream());
        BoardDistributionService service = new BoardDistributionService(new PopulationService() {
            @Override
            public LookupResidentForExtendedProfileResponseType lookup(List<String> bySocialSecurityNumbers) {
                try {
                    return new Gson().fromJson(Testing.getLookupDumpText(), LookupResidentForExtendedProfileResponseType.class);
                } catch (Exception e) {

                    throw new RuntimeException(e);
                }
            }
        });

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream input = Testing.getUnilabsSourceStream();
        Thread thread = service.runMakeDistributionFileContent(input, "Unilabs_S50MA50_201510_Lab_20151023_1252", "Unilabs_S50MA50", baos);
        thread.join();

        String result = new String(baos.toByteArray());
        String expexted = Testing.getUnilabsFacitText();
        //System.out.println(result);

        /* Assert.assertEquals(new Text(Arrays.asList(expexted.split(Pattern.quote("\n")))).getAllValueFromColumn("personnr"),
                new Text(Arrays.asList(result.trim().split(Pattern.quote("\n")))).getAllValueFromColumn("personnr"));*/

        List<String> expextedPnr = new Text(Arrays.asList(expexted.split(Pattern.quote("\n")))).getAllValueFromColumn("personnr");
        List<String> resultPnr = new Text(Arrays.asList(result.trim().split(Pattern.quote("\n")))).getAllValueFromColumn("personnr");

        /*
        expextedPnr.removeAll(resultPnr);
        System.out.println(expextedPnr);
        */
    }

}