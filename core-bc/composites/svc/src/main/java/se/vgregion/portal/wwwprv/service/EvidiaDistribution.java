package se.vgregion.portal.wwwprv.service;

import se.riv.population.residentmaster.lookupresidentforfullprofileresponder.v1.LookupResidentForFullProfileResponseType;
import se.riv.population.residentmaster.v1.ResidentType;

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
        String[] rows = input.split("\\n");
        System.out.println("Processar " + rows.length + " rader.");
        StringBuilder sb = new StringBuilder();
        for (String row : rows) {
            row = row.trim();
            String[] cells = row.split(Pattern.quote(" "));
            LookupResidentForFullProfileResponseType personInf = service.lookup(cells[1]);
            if (!personInf.getResident().isEmpty()) {
                ResidentType first = personInf.getResident().get(0);
                String lk = first.getPersonpost().getFolkbokforingsadress().getLanKod();
                if (row.length() > 108) {
                    row += new String(new char[row.length() - 108]).replace("\0", " ");
                }
                row += " " + lk;
            }
            sb.append(row);
            sb.append("\n");
        }
        return sb.toString();
    }

}
