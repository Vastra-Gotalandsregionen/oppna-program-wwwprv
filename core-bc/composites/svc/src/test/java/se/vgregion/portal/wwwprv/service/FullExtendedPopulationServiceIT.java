package se.vgregion.portal.wwwprv.service;

import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.riv.population.residentmaster.lookupresidentforextendedprofileresponder.v1.LookupResidentForExtendedProfileResponseType;
import se.riv.population.residentmaster.lookupresidentforfullprofileresponder.v1.LookupResidentForFullProfileResponseType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Claes Lundahl
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:population-test.xml")
public class FullExtendedPopulationServiceIT {

    @Value("${population.service.password}")
    private String password;

    List<String> ids =
            Arrays.asList(
                    "191202119150", "193901059059", "189004129814", "189004119807", "195102262267", "199704102384",
                    "199201142388", "199704102384", "199506262386", "199504152381", "189007209803", "189004149812",
                    "195205131575", "195102031753", "195401072284", "199711192394", "200110302387", "199711302381",
                    "200008072399", "199812262393", "200110172392", "200001082395", "199701032394", "200112152384",
                    "199801222390", "200112152384", "196801029288", "194803022328", "193302129220", "199604222399",
                    "199508232387", "199711232398", "199801152381", "199801042392", "196508122857", "194512267743",
                    "199711172396", "198504199897", "197006121144", "198508199885", "196804159264", "199508232387",
                    "199711232398", "196708282584"
            );

    @Autowired
    private FullPopulationService populationService;
    @Autowired
    private ExtendedPopulationService extendedPopulationService;

    @Ignore
    @Test
    public void oneCallLookup() throws Exception {
        long now = System.currentTimeMillis();
        try {
            LookupResidentForFullProfileResponseType result = populationService.lookup(ids.subList(0, 1));
            LookupResidentForExtendedProfileResponseType lookup = extendedPopulationService.lookup(ids.subList(0, 1));


            System.out.print("result.getResident().isEmpty(): " + result.getResident().isEmpty());
        } catch (Exception e) {
            System.out.print("F: " + e.getMessage());
        }
        System.out.println(" " + (System.currentTimeMillis() - now));
    }

    //@Ignore
    @Test
    public void eachItemLookup() throws Exception {
        final List<String> correctNumbers = new ArrayList<>();

        for (String id : ids) {
            long now = System.currentTimeMillis();
            try {
                System.out.print(id);
                LookupResidentForFullProfileResponseType r = populationService.lookup(id);
                correctNumbers.add(id);
                if (!r.getResident().isEmpty()) {
                    System.out.print(" Y");
                } else {
                    System.out.print(" N");
                }
            } catch (Exception e) {
                System.out.print(" F:" + e.getMessage());
                e.printStackTrace();
                return;
            }
            System.out.println(" Tidsåtgång " + (System.currentTimeMillis() - now) + " ms.");
        }

        System.out.println("\nCorrect-Numbers\n \"" + StringUtils.join(correctNumbers, "\", \"") + "\"");
    }

    public static Path getLookupDump() {
        Path path = Paths.get(new File("src\\test\\resources\\se\\vgregion\\portal\\wwwprv".replace("\\", File.separator))
                .getAbsolutePath() + File.separator + "lookup.dump");
        return path;
    }

    @Test
    public void evidiaDistribution() throws IOException {
        EvidiaDistribution evidiaDistribution = new EvidiaDistribution(populationService);
        String input = "20220830 191212121212 12345678,1234                  38 TL-0240    AM10 000   MR Hjärna            1250,00"
                + "\n"
                + "20220830 197508191934 12345678,1234                  38 TL-0240    AM10 000   MR Hjärna            1250,00";

        input = Files.readAllLines(Paths.get("P:\\DATAPRIVATA.INFILER\\Vardgiv_Evidia","EVIDIA_202211_10029721_20221201_1648.IN"))
                .stream().collect(Collectors.joining("\n"));

        System.out.println(evidiaDistribution.process(input));
    }

}
