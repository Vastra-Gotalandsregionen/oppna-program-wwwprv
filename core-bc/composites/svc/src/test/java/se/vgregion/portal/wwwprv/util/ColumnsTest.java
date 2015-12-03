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
public class ColumnsTest {

    Columns columns;

    private final String heading = "Datum    Pnr            Namn                      Betalare     Beställare   Analys.kod Analysnamn                     Pris";
    //private final String lastRowInTestFile =  "20151005 196708282584   TESTSSON, TEST            S50MA55      PL5025                  P-ASAT                              8.74";

    @Before
    public void setUp() {
        columns = new Columns(heading);
    }

    @Test
    public void isLast() {
        assertTrue(columns.isLast("Pris"));
    }

}