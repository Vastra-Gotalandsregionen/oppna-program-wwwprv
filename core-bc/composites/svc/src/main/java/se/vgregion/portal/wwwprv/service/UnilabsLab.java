package se.vgregion.portal.wwwprv.service;

import se.riv.population.residentmaster.extended.v1.AdministrativIndelningType;
import se.riv.population.residentmaster.extended.v1.ExtendedResidentType;
import se.riv.population.residentmaster.v1.PersonpostTYPE;
import se.riv.population.residentmaster.v1.SvenskAdressTYPE;
import se.vgregion.portal.wwwprv.table.Column;
import se.vgregion.portal.wwwprv.table.Table;
import se.vgregion.portal.wwwprv.table.Tupel;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by clalu4 on 2016-03-14.
 * Should mirror the meaning of Filspec_Unilabs_Lab__Nämndf_ver2.doc.
 */
public class UnilabsLab implements DistrictDistribution {

    protected PopulationService service;

    protected String originalFileName;

    /**
     * Constructor.
     * @param service the source of extra data to use when complementing the input data.
     * @param originalFileName name of the file that the process should output to. This to be able to write this name
     *                         to the actual content.
     */
    public UnilabsLab(PopulationService service, String originalFileName) {
        this.service = service;
        this.originalFileName = originalFileName;
    }

    /**
     * Complements the data hold in the text-table that is feed into the method.
     * Seven extra columns are inserted.
     *
     * Three of them are inserted before the original content.
     * 'korn_datum' - the date of the execution of this method. Format are yyyy-MM-dd.
     * 'klockslag' - time of day when the execution is made.
     * 'filnamn' is the name for the file that is provided in the constructor to this class earlier.
     *
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
        Table table = Table.newTableFromSpaceDelimInput(input);

        String nowDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        table.insert(new Column("korn_datum", 0, 10));
        table.insert(new Column("klockslag", 1, 9)); //
        table.insert(new Column("filnamn", 2, originalFileName.length()));

        Column purchaser = table.getColumnByName("Bestallare");
        if (purchaser != null) {
            table.insert(new Column("Specialitet", purchaser.getIndex() + 1, 11));
            table.insert(new Column("Avtalskod", purchaser.getIndex() + 1, 9));
        }

        // The original columns are here...
        table.insert(new Column("Pat_Nämnd", table.getColumns().size(), 2));
        table.insert(new Column("PatLan_+_kommun", table.getColumns().size(), 4));
        table.insert(new Column("Pat_SDN", table.getColumns().size(), 2));
        table.insert(new Column("Pat_nyckelkod", table.getColumns().size(), 6));



        Column dateKey = table.getColumns().get(3);
        Column personalNumberKey = table.getColumns().get(4);

        for (Tupel tupel : table.getTupels()) {
            String pris = tupel.get("pris").value();
            pris = pris.replace('.', ',');
            tupel.get("pris").set(pris);

            String personalNumber = tupel.get(personalNumberKey).value().trim();
            String date = tupel.get(dateKey).value().trim();
            ExtendedResidentType info = getResidentialInfo(personalNumber, date);
            //ExtendedResidentType info = getLatestResidentInfo(personalNumber, date);
            tupel.get("korn_datum").set(nowDate);
            tupel.get("filnamn").set(originalFileName);
            String nowTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
            tupel.get("klockslag").set(nowTime);
            String purchaserValue = tupel.get("Bestallare").value().trim();

            if (!purchaserValue.isEmpty()) {
                if (purchaserValue.length() >= 6) {
                    tupel.get("Specialitet").set(purchaserValue.substring(0, 2));
                    tupel.get("Avtalskod").set(purchaserValue.substring(2));
                } else {
                    tupel.get("Avtalskod").set(purchaserValue);
                }
            }

            if (info != null) {
                AdministrativIndelningType folkbok = info.getFolkbokforingsaddressIndelning();
                PersonpostTYPE pp = info.getPersonpost();
                if (pp != null) {
                    SvenskAdressTYPE fba = pp.getFolkbokforingsadress();
                    if (fba != null){
                        tupel.get("PatLan_+_kommun").set(blank(fba.getLanKod()) + blank(fba.getKommunKod()));
                    }
                }
                if (folkbok != null) {
                    tupel.get("Pat_SDN").set(folkbok.getStadsdelsnamndKod());
                    SvenskAdressTYPE adress = info.getPersonpost().getFolkbokforingsadress();
                    if (adress != null) {
                        tupel.get("Pat_nyckelkod").set(adress.getSCBNyckelkod());
                    }
                }
                AdministrativIndelningType fbi = info.getFolkbokforingsaddressIndelning();
                if (fbi != null) {
                    tupel.get("Pat_Nämnd").set(fbi.getHalsoSjukvardsNamndKod());
                }
            }
        }

        return table.toExcelCsvText();
    }

    String blank(String s) {
        return s == null ? "" :s;
    }

    ExtendedResidentType getResidentialInfo(String forPersonalNumber, String fromDate) {
        return service.lookup(new PopulationService.Arg(forPersonalNumber, fromDate)).get(0);
    }

}
