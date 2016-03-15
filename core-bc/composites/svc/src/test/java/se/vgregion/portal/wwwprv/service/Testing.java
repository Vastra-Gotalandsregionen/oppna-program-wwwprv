package se.vgregion.portal.wwwprv.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by clalu4 on 2015-12-01.
 */
public class Testing {

    public static Path getUnilabsSourcePath() {
        Path path = Paths.get(new File("src\\test\\resources\\se\\vgregion\\portal\\wwwprv\\service".replace("\\", File.separator))
                .getAbsolutePath() + File.separator + "Unilabs_S50MA50_201510_Lab_20151023_1252.in");
        return path;
    }

    public static String getUnilabsSourceText() {
        try {
            return new String(Files.readAllBytes(getUnilabsSourcePath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static File getUnilabsSourceFile() {
        return new File(getUnilabsSourcePath().toString());
    }

    public static InputStream getUnilabsSourceStream() {
        try {
            return new FileInputStream(getUnilabsSourcePath().toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Path getUnilabsFacitPath() {
        Path path = Paths.get(new File("src\\test\\resources\\se\\vgregion\\portal\\wwwprv\\service\\Facit".replace("\\", File.separator))
                .getAbsolutePath() + File.separator + "Unilabs_S50MA50_2015107_nämndfördelning_20151102_lev_20151023.txt.rpt");
        return path;
    }

    public static String getUnilabsFacitText() {
        try {
            return new String(Files.readAllBytes(getUnilabsFacitPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Path getLookupDumpPath() {
        Path path = Paths.get(new File("src\\test\\resources\\se\\vgregion\\portal\\wwwprv".replace("\\", File.separator))
                .getAbsolutePath() + File.separator + "lookup.dump");
        return path;
    }

    public static String getLookupDumpText() {
        try {
            return new String(Files.readAllBytes(getLookupDumpPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String labFileText() {
        String heading = "Datum    Pnr            Namn                      Betalare     Beställare   Analys.kod Analysnamn                     Pris     \n";

        String[] numbers = new String[]{
                "191202119150", "193901059059", "189004129814", "189004119807", "195102262267", "199704102384",
                "199201142388", "199704102384", "199506262386", "199504152381", "189007209803", "189004149812",
                "195205131575", "195102031753", "195401072284", "199711192394", "200110302387", "199711302381",
                "200008072399", "199812262393", "200110172392", "200001082395", "199701032394", "200112152384",
                "199801222390", "200112152384", "196801029288", "194803022328", "193302129220", "199604222399",
                "199508232387", "199711232398", "199801152381", "199801042392", "196508122857", "194512267743",
                "199711172396", "198504199897", "197006121144", "198508199885", "196804159264", "199508232387",
                "199711232398", "196708282584"
        };

        String[] line = new String[]{"20151126 xxxxxxxxxxxx   HULTKRANTZ, ALVA          S50MA55      5024         XSAMH      S-AMH                             420.48",
                "20151119 xxxxxxxxxxxx   AL HANOTA, /LINA/DANIAL E S50MA55      5024         XSAMH      S-AMH                             420.48",
                "20151120 xxxxxxxxxxxx   ENGQUIST, /ULLA/ELISABETH S50MA55      5024                    P-Kobalamin                        86.65"};

        List<String> linel = new ArrayList<>(Arrays.asList(line));

        StringBuilder sb = new StringBuilder(heading);

        for (String number : numbers) {
            String template = linel.get((int) (Math.random() * linel.size()));
            sb.append(template.replace("xxxxxxxxxxxx", number));
            sb.append("\n");
        }

        return sb.toString();
    }

}
