package se.vgregion.portal.wwwprv.service;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.riv.population.residentmaster.extended.v1.ExtendedResidentType;
import se.riv.population.residentmaster.lookupresidentforextendedprofileresponder.v1.LookupResidentForExtendedProfileResponseType;
import se.vgregion.portal.wwwprv.table.Table;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Claes Lundahl
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:population-test.xml")
public class PopulationServiceIT {

    ExecutorService executorService = Executors.newFixedThreadPool(24);

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
    private PopulationService populationService;

    @Ignore
    @Test
    public void oneCallLookup() throws Exception {
        long now = System.currentTimeMillis();
        try {
            LookupResidentForExtendedProfileResponseType result = populationService.lookup(ids);
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
                LookupResidentForExtendedProfileResponseType r = populationService.lookup(id);
                correctNumbers.add(id);
                if (!r.getResident().isEmpty()) {
                    System.out.print(" Y");
                } else {
                    System.out.print(" N");
                }
            } catch (Exception e) {
                System.out.print(" F:" + e.getMessage());
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

    volatile int counter = 0;

    @Test
    @Ignore
    public void unilabsLookup() throws IOException {

        FileInputStream input = new FileInputStream("/tmp/UNILABS_LAB_201907_20190823_1124.IN");
        Table table = Table.newTableFromSpaceDelimInput(IOUtils.toString(input));

        List<String> pnrs = table.getTupels().stream().map(tupel -> tupel.get(tupel.getColumns().get(1)).value()).collect(Collectors.toList());
//        CompletableFuture<List<ExtendedResidentType>>[] a = ;

        List<String> failed = new ArrayList<>();

        CompletableFuture<List<ExtendedResidentType>>[] collect = pnrs.stream().map(s -> {
            return CompletableFuture.supplyAsync(
                    () -> {
                        try {
                            System.out.println("Räknar... " + counter++);
                            return populationService.lookup(new PopulationService.Arg(s, "20190823000000"));
                        } catch (Exception e) {
                            failed.add(s);
//                            throw new RuntimeException(e);
                            return new ArrayList<>();
                        }
                    }, executorService);
        }).collect(Collectors.toList()).toArray(new CompletableFuture[0]);

        CompletableFuture<Void> allOf = CompletableFuture.allOf(collect);
        allOf.whenComplete((aVoid, throwable) -> System.out.println("Färdig...."));

        allOf.join();

        System.out.println(failed);

    }

}
