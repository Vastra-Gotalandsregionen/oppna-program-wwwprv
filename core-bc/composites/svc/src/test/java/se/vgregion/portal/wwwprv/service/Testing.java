package se.vgregion.portal.wwwprv.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

}
