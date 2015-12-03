package se.vgregion.portal.wwwprv.util;

/**
 * Storage class for file header constants.
 */
public class ColumnNames {

    /**
     * Consists of constants naming the columns for the file that have received information about board distribution.
     */
    public final static Extended EXTENDED = new Extended();

    /**
     * Consists of constants naming the columns for the file that is uploaded by the client of this application.
     * The name SIMPLER refers to the fact that the data not yet have been complemented with board distribution
     * information.
     */
    public final static Simpler SIMPLER = new Simpler();

    /**
     * Names the columns of the file that have been provided with board distribution information.
     */
    public static class Extended {
        public static final String korn_datum = "korn_datum", filnamn = "filnamn",
                avtalskod = "avtalskod", BesoksDatum = "BesoksDatum", personnr = "personnr", Namn = "Namn", Betalare = "Betalare", Beställare = "Beställare", Analyskod = "Analyskod", AnalysNamn = "AnalysNamn",
                Cap_pris = "Cap_pris", PatLan = "PatLan",
                PatKommun = "PatKommun", SDN = "SDN",
                NyckelKod = "NyckelKod", Namnd = "Namnd";
    }

    /**
     * Names the columns of the file that is loaded by the user.
     */
    public static class Simpler {
        public static final String Datum = "Datum", Pnr = "Pnr", Namn = "Namn", Betalare = "Betalare", Beställare = "Beställare", Analys¤kod = "Analys.kod", Analysnamn = "Analysnamn", Pris = "Pris";
    }

}