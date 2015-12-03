package se.vgregion.portal.wwwprv.util;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by clalu4 on 2015-12-01.
 */
public class TextTest {

    Text text;

    private final String firstRowInTestFile = "Datum    Pnr            Namn                      Betalare     Beställare   Analys.kod Analysnamn                     Pris";
    private final String lastRowInTestFile =  "20151005 196708282584   TESTSSON, TEST            S50MA55      PL5025                  P-ASAT                              8.74";

    @Before
    public void setUp() {
        text = new Text(getClass().getResourceAsStream("/se/vgregion/portal/wwwprv/service/Unilabs_S50MA50_201510_Lab_20151023_1252.in"));
    }

    @Test
    public void constructorZero() {
        Text text = new Text();
        assertNotNull(text);
    }

    @Test
    public void toRawLines() {
        List<String> raw = text.toRawLines();

        assertEquals(39, raw.size());
        assertEquals(firstRowInTestFile, raw.get(0));
        assertEquals(lastRowInTestFile, raw.get(raw.size() - 1));
    }

    @Test
    public void setAndGet() {
        Text.Line first = text.lines.get(0);

        first.set("Datum", "Datum2");
        String expected = "Datum2   Pnr            Namn                      Betalare     Beställare   Analys.kod Analysnamn                     Pris";
        assertEquals(expected, first.getContent());
        assertEquals("Datum2", first.get("Datum"));

        first.set("Datum", "Datum2");
        expected = "Datum2   Pnr            Namn                      Betalare     Beställare   Analys.kod Analysnamn                     Pris";
        assertEquals(expected, first.getContent());
        assertEquals("Datum2", first.get("Datum"));

        first.set("Pris", "Pris2");
        assertEquals("Pris2", first.get("Pris"));
        expected = "Datum2   Pnr            Namn                      Betalare     Beställare   Analys.kod Analysnamn                     Pris2";
        assertEquals(expected, first.getContent());
    }

}