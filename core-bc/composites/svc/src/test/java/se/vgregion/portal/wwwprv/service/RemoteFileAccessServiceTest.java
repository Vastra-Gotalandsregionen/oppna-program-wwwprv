package se.vgregion.portal.wwwprv.service;

import org.junit.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Patrik Björk
 */
public class RemoteFileAccessServiceTest {

    @Test
    public void complementFileNameWithNamndfordelningPart() throws Exception {

        // Happy days
        String fileName = "UNILABS_LAB.INPUT_20160322_1319.IN";
        String newName = RemoteFileAccessService.complementFileNameWithNamndfordelningPart(fileName);
        assertEquals("UNILABS_LAB.INPUT_20160322_1319.IN" + RemoteFileAccessService.namndFordeladFileNameSuffix, newName);

        // No dot - add last.
        fileName = "UNILABS_LABINPUT_20160322_1319IN";
        newName = RemoteFileAccessService.complementFileNameWithNamndfordelningPart(fileName);
        assertEquals("UNILABS_LABINPUT_20160322_1319IN" + RemoteFileAccessService.namndFordeladFileNameSuffix, newName);

        // Empty - add last.
        fileName = "";
        newName = RemoteFileAccessService.complementFileNameWithNamndfordelningPart(fileName);
        assertEquals(RemoteFileAccessService.namndFordeladFileNameSuffix, newName);

        // Null - no change
        fileName = null;
        try {
            newName = RemoteFileAccessService.complementFileNameWithNamndfordelningPart(fileName);
            fail();
        } catch (NullPointerException e) {
            // Expected
        }
    }

    public static void main(String[] args) throws IOException {
        runSomeFilesAgainstGuessingCharacterTypes("/some/path/to/test/files");
    }

    public static void runSomeFilesAgainstGuessingCharacterTypes(String pathToDirWithFiles) throws IOException {
        Path dirOfFiles = Paths.get(pathToDirWithFiles);
        Files.list(dirOfFiles).forEach(
                file-> {
                    try {
                        String result = RemoteFileAccessService.guessCharacterEncoding(
                                Files.readAllBytes(file)
                        );
                        System.out.println(file.getFileName() + " " + result);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );


    }


    @Test
    public void guessCharacterEncoding() throws UnsupportedEncodingException {
        String result = RemoteFileAccessService.guessCharacterEncoding(getUtf8sample());
        assertEquals("UTF-8", result);

        result = RemoteFileAccessService.guessCharacterEncoding(getWindows1252sample());
        assertEquals("ISO-8859-1", result);
    }

    static byte[] getUtf8sample() {
         /*
            Datum    Pnr          Namn                      Lä Ko Betalare     Beställare   Unders.kod Undersökning                   Pris
            20210118 191212121212 TOLVAN0, TOLVSSON         14 81 38           PL1871       933000     ULJ Thyreoidea                       733
         */
        return new byte[]{
                -17, -69, -65, 68, 97, 116, 117, 109, 32, 32, 32, 32, 80, 110, 114, 32, 32, 32, 32, 32, 32, 32,
                32, 32, 32, 78, 97, 109, 110, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
                32, 32, 32, 32, 76, -61, -92, 32, 75, 111, 32, 66, 101, 116, 97, 108, 97, 114, 101, 32, 32, 32, 32, 32,
                66, 101, 115, 116, -61, -92, 108, 108, 97, 114, 101, 32, 32, 32, 85, 110, 100, 101, 114, 115, 46, 107,
                111, 100, 32, 85, 110, 100, 101, 114, 115, -61, -74, 107, 110, 105, 110, 103, 32, 32, 32, 32, 32, 32,
                32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 80, 114, 105, 115, 13, 10, 50, 48, 50, 49, 48, 49,
                49, 56, 32, 49, 57, 49, 50, 49, 50, 49, 50, 49, 50, 49, 50, 32, 84, 79, 76, 86, 65, 78, 48, 44, 32, 84,
                79, 76, 86, 83, 83, 79, 78, 32, 32, 32, 32, 32, 32, 32, 32, 32, 49, 52, 32, 56, 49, 32, 51, 56, 32, 32,
                32, 32, 32, 32, 32, 32, 32, 32, 32, 80, 76, 49, 56, 55, 49, 32, 32, 32, 32, 32, 32, 32, 57, 51, 51, 48,
                48, 48, 32, 32, 32, 32, 32, 85, 76, 74, 32, 84, 104, 121, 114, 101, 111, 105, 100, 101, 97, 32, 32, 32,
                32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 55, 51, 51, 13, 10
        };
    }

    static byte[] getWindows1252sample() {
        /*
            Datum    Pnr          Namn                      Lä Ko Betalare     Beställare   Unders.kod Undersökning                   Pris
            20201020 191212121212 TOLVAN000, TOLV           14 80 38           PL0600       840800     DT Buk med iv kontrast              1883
         */
        return new byte[]{
                        68, 97, 116, 117, 109, 32, 32, 32, 32, 80, 110, 114, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
                        78, 97, 109, 110, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
                        32, 32, 32, 76, -28, 32, 75, 111, 32, 66, 101, 116, 97, 108, 97, 114, 101, 32, 32, 32, 32, 32,
                        66, 101, 115, 116, -28, 108, 108, 97, 114, 101, 32, 32, 32, 85, 110, 100, 101, 114, 115, 46,
                        107, 111, 100, 32, 85, 110, 100, 101, 114, 115, -10, 107, 110, 105, 110, 103, 32, 32, 32, 32,
                        32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 80, 114, 105, 115, 32, 32, 32, 32,
                        32, 13, 10, 50, 48, 50, 48, 49, 48, 50, 48, 32, 49, 57, 49, 50, 49, 50, 49, 50, 49, 50, 49, 50,
                        32, 84, 79, 76, 86, 65, 78, 48, 48, 48, 44, 32, 84, 79, 76, 86, 32, 32, 32, 32, 32, 32, 32, 32,
                        32, 32, 32, 49, 52, 32, 56, 48, 32, 51, 56, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 80, 76,
                        48, 54, 48, 48, 32, 32, 32, 32, 32, 32, 32, 56, 52, 48, 56, 48, 48, 32, 32, 32, 32, 32, 68, 84,
                        32, 66, 117, 107, 32, 109, 101, 100, 32, 105, 118, 32, 107, 111, 110, 116, 114, 97, 115, 116,
                        32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 49, 56, 56, 51, 13, 10
                };
    }

}