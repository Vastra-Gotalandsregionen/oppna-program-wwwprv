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
        // System.out.println(resultFileContent());
        // System.out.println(inputFileContent());

        String originalFileName = "original_file_name.text";
        UnilabsLab unilabsLab = new UnilabsLab(populationService, originalFileName);

        String output = unilabsLab.process(inputFileContent().toString());
        Table outputTable = new Table(output);
        System.out.println(output);

        String expectedHeading = "korn_datum              filnamn                                            avtalskod BesoksDatum personnr      Namn                           Betalare Bestallare Analyskod  AnalysNamn                     Cap_pris               PatLan     PatKommun  SDN        NyckelKod  Namnd\n";
        Table havingExpectedHeading = new Table(expectedHeading);

        Set<String> columnNames = new HashSet<>();
        for (Column c : outputTable.getColumns()) {
            columnNames.add(c.getName());
        }

    }


    public Table inputFileContent() {
        String def = "" +
                "key                      format\n" +
                "Provdatum                A(8)  \n" +
                "Personnr                 A(12) \n" + // changed A(10) -> A(12)
                "Namn                     A(30) \n" +
                "Betalare                 A(2)  \n" +
                "Beställare               A(10) \n" +
                "Analyskod                A(10) \n" +
                "Analysnamn               A(20) \n" +
                "Pris                     A(7)  \n";

        Table table = new Table(def);


        Table result = new Table("Line");

        int i = 0;
        for (Tupel tupel : table.getTupels()) {
            result.insert(new Column(tupel.get("key").value().trim(), i++, toInt(tupel.get("format").value())));
        }

        for (String s : DummyPersonalNumbers.get()) {
            if (s.startsWith("18")) {
                // Remove those dummies born 18-1899...
                continue;
            }
            //s = s.substring(2);
            Tupel tupel = new Tupel(result.getColumns(), "");
            tupel.get("Personnr").set(s);
            tupel.get("Provdatum").set("20160101");
            result.getTupels().add(tupel);
        }


        return result;
    }

    public Table resultFileContent() {
        String def = "" +
                "key                      format\n" +
                "Ursprungligt_filnamn           \n" +
                "Körningsdatum            A(10) \n" +
                "Klockslag körningsdatum        \n" +
                "Provdatum                A(8)  \n" +
                "Personnr                 A(12) \n" + // changed A(10) -> A(12)
                "Namn                     A(30) \n" +
                "Betalare                 A(2)  \n" +
                "Beställare               A(10) \n" +
                "Analyskod                A(10) \n" +
                "Analysnamn               A(20) \n" +
                "Pris                     A(7)  \n" +
                "Just_Nämnd               A(2)  \n" +
                "Just_länkommm            A(4)  \n" +
                "Just_sdn                 A(2)  \n" +
                "Just_nyckelkod           A(6)  \n";

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