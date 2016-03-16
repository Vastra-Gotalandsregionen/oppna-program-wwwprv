package se.vgregion.portal.wwwprv.service;

import junit.framework.Assert;
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
public class UnilabsLabTest {

    @Autowired
    private PopulationService populationService;

    @Test
    public void main() {
        String originalFileName = "original_file_name.text";
        UnilabsLab unilabsLab = new UnilabsLab(populationService, originalFileName);

        String output = unilabsLab.process(inputFileContent().toString());
        System.out.println(output);
        Table outputTable = new Table(output);

        Table headings = new Table(getResultFileColumns());

        Set<String> columnNames = new HashSet<>();
        for (Tupel column : headings.getTupels()) {
            columnNames.add(column.get("key").value().trim());
        }

        for (Column column : outputTable.getColumns()) {
            Assert.assertTrue("Column " + column.getName() + " should have been inside.",
                    columnNames.contains(column.getName())
            );
        }

        System.out.println(new Table(output).toString(";"));
    }

    public String getInputFileColumns() {
        return "" +
                "key                    format\n" +
                "BesoksDatum             A(8)\n" +
                "personnr               A(12)\n" +
                "Namn                   A(30)\n" +
                "Betalare                A(2)\n" +
                "Bestallare             A(10)\n" +
                "Analyskod              A(10)\n" +
                "AnalysNamn             A(20)\n" +
                "pris                    A(7)\n";
    }

    public Table inputFileContent() {
        String def = getInputFileColumns();
        Table table = new Table(def);
        Table result = new Table();

        int i = 0;
        for (Tupel tupel : table.getTupels()) {
            result.insert(new Column(tupel.get("key").value().trim(), i++, toInt(tupel.get("format").value())));
        }

        for (String s : DummyPersonalNumbers.get()) {
            Tupel tupel = new Tupel(result.getColumns(), "");
            tupel.get("personnr").set(s);
            tupel.get("BesoksDatum").set("20160101");
            result.getTupels().add(tupel);
        }

        i = 0;
        for (Tupel tupel : result.getTupels()) {
            for (Column column : result.getColumns()) {
                if (!(column.getName().equals("personnr") || column.getName().equals("BesoksDatum"))) {
                    tupel.get(column.getName()).set(i + "");
                }
            }
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
                "Analyskod              A(10)\n" +
                "AnalysNamn             A(20)\n" +
                "pris                    A(7)\n" +
                "Pat_Nämnd               A(2)\n" +
                "PatLan_+_kommun       A(4)\n" +
                "Pat_SDN                 A(2)\n" +
                "Pat_nyckelkod           A(6)\n" + "\n";
    }

    public Table resultFileContent() {
        String def = getResultFileColumns();
        Table table = new Table(def);
        Table result = new Table("Line");

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