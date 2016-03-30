package se.vgregion.portal.wwwprv.table;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by clalu4 on 2016-03-14.
 */
public class TableTest {

    @Test
    public void padLeft() {
        String s = Table.padLeft("123", 10);
        assertEquals(10, s.length());
        assertTrue(s.startsWith("123"));
    }

    @Test
    public void padRight() {
        String s = Table.padRight("123", 10);
        assertEquals(10, s.length());
        assertTrue(s.endsWith("123"));
    }

    @Test
    public void main() {
        Table table = Table.newTableFromSpaceDelimInput(testText());
    }

    public static String testText() {
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

    @Test
    public void insertColumn() {
        Table table = Table.newTableFromSpaceDelimInput(testText());

        int counter = 0;
        for (Column column : table.getColumns()) {
            assertEquals(counter++, column.getIndex());
        }

        table.insert(new Column("zero", 0, 2));
        //System.out.println(table);

        table.insert(new Column("middle", table.getColumns().size() / 2, 20));
        //System.out.println(table);

        table.insert(new Column("last", table.getColumns().size(), 10));
        //System.out.println(table);

        counter = 0;
        for (Column column : table.getColumns()) {
            assertEquals(column + " With index " + counter, counter++, column.getIndex());
        }
    }

    @Test
    public void testToString() {
        Table table = Table.newTableFromSpaceDelimInput(testText());
        String r = table.toString();
        System.out.println(r);
    }

}