package se.vgregion.portal.wwwprv.service;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.vgregion.portal.wwwprv.DummyPersonalNumbers;
import se.vgregion.portal.wwwprv.table.Column;
import se.vgregion.portal.wwwprv.table.Table;
import se.vgregion.portal.wwwprv.table.Tupel;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by clalu4 on 2016-03-14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:population-test.xml")
public class UnilabsLabIT {

    @Autowired
    private ExtendedPopulationService extendedPopulationService;

    @Test
    public void main() throws DistrictDistributionException {
        String originalFileName = "original_file_name.text";
        UnilabsLab unilabsLab = new UnilabsLab(extendedPopulationService, originalFileName);

        String otherPath = "C:\\Users\\clalu4\\Kod\\Diverse\\misc-vgr-database-work\\src\\main\\resources\\my\\misc\\vgr\\work\\dataprivata\\data\\that\\failed\\unpersonal.test.in";
        //String input = inputFileContent(getInputFileColumnsUnilabsLab()).toString();
        String input = inputFileContent(otherPath).toString();
        System.out.println(input);

        String output = unilabsLab.process(input);
        System.out.println(output);
        Table outputTable = Table.newTableFromSemiColonDelimInput(output);

        Table headings = Table.newTableFromSpaceDelimInput(getResultFileColumns());

        Set<String> columnNames = new HashSet<>();
        for (Tupel column : headings.getTupels()) {
            columnNames.add(column.get("key").value().trim());
        }

        for (Column column : outputTable.getColumns()) {
            Assert.assertTrue("Column " + column.getName() + " should have been inside.",
                    columnNames.contains(column.getName())
            );
        }

        //System.out.println(Table.newTableFromSemiColonDelimInput(output).toString(";"));
        System.out.println(output);

    }

    public String getInputFileColumnsUnilabsRontgen() {
        return "" +
                "key                    format\n" +
                "BesoksDatum            A(8)\n" +
                "personnr               A(12)\n" +
                "Namn                   A(30)\n" +
                "Lan                    A(2)\n" +
                "Kommun                 A(2)\n" +
                "Betalare               A(2)\n" +
                "Bestallare             A(10)\n" +
                "Analyskod              A(10)\n" +
                "AnalysNamn             A(20)\n" +
                "Pris                    A(7)\n";
    }

    public String getInputFileColumnsUnilabsLab() {
        return "" +
                "key                    format\n" +
                "BesoksDatum             A(8)\n" +
                "personnr               A(12)\n" +
                "Namn                   A(30)\n" +
                "Betalare                A(2)\n" +
                "Bestallare             A(10)\n" +
                "Analyskod              A(10)\n" +
                "AnalysNamn             A(20)\n" +
                "Pris                    A(7)\n";
    }

    public Table inputFileContent(String inputFileColumns) {
        //String def = getInputFileColumnsUnilabsLab();
        Table table = Table.newTableFromSpaceDelimInput(inputFileColumns);
        Table result = Table.newEmptyTable();

        int i = 0;
        for (Tupel tupel : table.getTupels()) {
            result.insert(new Column(tupel.get("key").value().trim(), i++, toInt(tupel.get("format").value())));
        }

        for (String s : DummyPersonalNumbers.get()) {
            Tupel tupel = new Tupel(result.getColumns(), "");
            tupel.get("personnr").set(s);
            tupel.get("BesoksDatum").set("20120101");
            result.getTupels().add(tupel);
        }

        i = 0;
        for (Tupel tupel : result.getTupels()) {
            for (Column column : result.getColumns()) {
                if (column.getName().equals("Pris")) {
                    tupel.get(column.getName()).set(i + "." + (i + 1)); // Decimal value
                } else if (!(column.getName().equals("personnr") || column.getName().equals("BesoksDatum"))) {
                    tupel.get(column.getName()).set(i + "");
                }
            }
            i++;
        }

        i = 0;
        for (Tupel tupel : result.getTupels()) {
            tupel.get("Bestallare").set(StringUtils.rightPad("" + i, 6, '0'));
            i++;
        }

        return result;
    }

    public String getResultFileColumns() {
        return "" +
                "key                   format\n" +
                "korn_datum                  \n" +
                "klockslag                   \n" +
                "filnamn                     \n" +
                "BesoksDatum             A(8)\n" +
                "personnr               A(12)\n" +
                "Namn                   A(30)\n" +
                "Betalare                A(2)\n" +
                "Bestallare             A(10)\n" +
                "Specialitet            A(11)\n" +
                "Avtalskod              A(9)\n" +
                "Analyskod              A(10)\n" +
                "AnalysNamn             A(20)\n" +
                "Pris                    A(7)\n" +
                "Pat_Nämnd               A(2)\n" +
                "PatLan_+_kommun       A(4)\n" +
                "Pat_SDN                 A(2)\n" +
                "Pat_nyckelkod           A(6)\n" + "\n";
    }

    public Table resultFileContent() {
        String def = getResultFileColumns();
        Table table = Table.newTableFromSpaceDelimInput(def);
        Table result = Table.newTableFromSpaceDelimInput("Line");

        int i = 0;
        for (Tupel tupel : table.getTupels()) {
            result.insert(new Column(tupel.get("key").value().trim(), i++, toInt(tupel.get("format").value())));
        }

        return result;
    }

    private int toInt(String s) {
        try {
            return Integer.parseInt(s.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

}