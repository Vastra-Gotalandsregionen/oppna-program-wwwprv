package se.vgregion.portal.wwwprv.util;

public class ColumnNames {

    public final static Extended EXTENDED = new Extended();

    public final static Simpler SIMPLER = new Simpler();

    public static class Extended {
        public static final String korn_datum = "korn_datum", filnamn = "filnamn",
                avtalskod = "avtalskod", BesoksDatum = "BesoksDatum", personnr = "personnr", Namn = "Namn", Betalare = "Betalare", Beställare = "Beställare", Analyskod = "Analyskod", AnalysNamn = "AnalysNamn",
                Cap_pris = "Cap_pris", PatLan = "PatLan",
                PatKommun = "PatKommun", SDN = "SDN",
                NyckelKod = "NyckelKod", Namnd = "Namnd";
    }

    public static class Simpler {
        public static final String Datum = "Datum", Pnr = "Pnr", Namn = "Namn", Betalare = "Betalare", Beställare = "Beställare", Analys¤kod = "Analys.kod", Analysnamn = "Analysnamn", Pris = "Pris";
    }

}