package se.vgregion.portal.wwwprv.service;

import riv.population.residentmaster._1.PersonpostTYPE;
import riv.population.residentmaster._1.ResidentType;
import riv.population.residentmaster._1.SvenskAdressTYPE;
import se.riv.population.residentmaster.lookupresidentforfullprofileresponder.v1.LookupResidentForFullProfileResponseType;
import se.vgregion.portal.wwwprv.table.Cell;
import se.vgregion.portal.wwwprv.table.Column;
import se.vgregion.portal.wwwprv.table.Table;
import se.vgregion.portal.wwwprv.table.Tupel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by clalu4 on 2016-03-14.
 * Should mirror the meaning of Filspec_Unilabs_Lab__Nämndf_ver2.doc.
 */
public class EvidiaDistribution implements DistrictDistribution {

    protected FullPopulationService service;

    /**
     * Constructor.
     *
     * @param service the source of extra data to use when complementing the input data.
     */
    public EvidiaDistribution(FullPopulationService service) {
        this.service = service;
    }

    /**
     * Complements the data hold in the text-table that is feed into the method.
     * Seven extra columns are inserted.
     * <p>
     * Three of them are inserted before the original content.
     * 'korn_datum' - the date of the execution of this method. Format are yyyy-MM-dd.
     * 'klockslag' - time of day when the execution is made.
     * 'filnamn' is the name for the file that is provided in the constructor to this class earlier.
     * <p>
     * Four of the columns are added after the original columns.
     * 'Pat_Nämnd' - the patient board that the patient belonged to at the moment of hes/shes visit to the medical
     * center that this row depicts.
     * 'PatLan_+_kommun' - the patients lan and kommun as one field (the code for each entity).
     * 'PAT_SDN' the sdn for the patient.
     * 'Pat_nyckelkod' the key code for the patient.
     *
     * @param input a text forming at 'text-table' - a table with fixed length columns and headings that holds the data.
     * @return a table with column-values separated by semicolons.
     */
    @Override
    public String process(String input) {
        input = "Datum    Personnummer Namn                           N1 Kod1       Kod2 Kod3  C1 Text              Pris    LK\n" + input;
        /*String[] rows = input.split("\\n");
        System.out.println("Processar " + rows.length + " rader.");*/

        Table table = Table.newTableFromSpaceDelimInput(input);

        String nowDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        /*table.insert(new Column("Datum", 0, 8));
        table.insert(new Column("Personnummer", 1, 12));
        table.insert(new Column("Namn", 2, 30));
        table.insert(new Column("Namn", 3, 30));
        table.insert(new Column("N1", 4, 2));
        table.insert(new Column("Kod", 5, 10));
        table.insert(new Column("A", 6, 4));
        table.insert(new Column("N2", 7, 4));
        table.insert(new Column("C", 8, 2));
        table.insert(new Column("Text", 9, 17));
        table.insert(new Column("Pris", 10, 7));
        table.insert(new Column("LK", 11, 2));*/

        int rowCursor = 0;

        Column kod1Prefix = new Column("Kp1", table.getColumnByName("Kod1").getIndex(), 10);
        table.insert(kod1Prefix);

        for (Tupel tupel : table.getTupels()) {
            rowCursor++;
            try {
                Cell kod1 = tupel.get("Kod1");
                String[] parts = kod1.value().split(Pattern.quote("-"));
                if (parts.length == 2) {
                    kod1.set(parts[1]);
                    tupel.get("Kp1").set(parts[0]);
                }

                LookupResidentForFullProfileResponseType personInf = service.lookup(tupel.get("Personnummer").value());
                String lk = null;
                if (!personInf.getResident().isEmpty()) {
                    ResidentType first = personInf.getResident().get(0);
                    PersonpostTYPE pp = first.getPersonpost();
                    if (pp != null) {
                        SvenskAdressTYPE fba = pp.getFolkbokforingsadress();
                        if (fba != null) {
                            lk = fba.getLanKod();
                        }
                    }
                }
                if (lk == null) lk = "";
                tupel.get("LK").set(lk);
            } catch (Exception e) {
                throw new RuntimeException("EvidiaDistribution misslyckades vid rad " + rowCursor + ". Kontrollera personnummer, mm. Samma felaktiga nummer kan förekomma flera gånger i filen.", e);
            }
        }
        return table.toExcelCsvText();
    }

    static String padTextLeft(String that, int totalLength, char ch) {
        if (that.length() < totalLength) {
            that += new String(new char[totalLength - that.length()]).replace('\0', ch);
        }
        return that;
    }

}
