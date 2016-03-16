package se.vgregion.portal.wwwprv.service;

import se.riv.population.residentmaster.extended.v1.AdministrativIndelningType;
import se.riv.population.residentmaster.extended.v1.ExtendedResidentType;
import se.riv.population.residentmaster.lookupresidentforextendedprofileresponder.v1.LookupResidentForExtendedProfileResponseType;
import se.riv.population.residentmaster.v1.SvenskAdressTYPE;
import se.vgregion.portal.wwwprv.table.Column;
import se.vgregion.portal.wwwprv.table.Table;
import se.vgregion.portal.wwwprv.table.Tupel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by clalu4 on 2016-03-14.
 * Should mirror the meaning of Filspec_Unilabs_Lab__Nämndf_ver2.doc.
 */
public class UnilabsLab implements DistrictDistribution {

    protected PopulationService service;

    protected String originalFileName;

    protected LookupResidentForExtendedProfileResponseType data;

    public UnilabsLab(PopulationService service, String originalFileName) {
        this.service = service;
        this.originalFileName = originalFileName;
    }

    @Override
    public String process(String input) {
        Table table = new Table(input);

        List<String> personalNumbers = new ArrayList<>();

        for (Tupel tupel : table.getTupels()) {
            personalNumbers.add(tupel.get("Personnr").value());
        }

        data = service.lookup(personalNumbers);

        String nowDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String nowTime = new SimpleDateFormat("HH:mm:ss").format(new Date());

        table.insert(new Column("Ursprungligt_filnamn", 0, originalFileName.length()));
        table.insert(new Column("Körningsdatum", 1, 10));
        table.insert(new Column("Klockslag_körningsdatum", 2, 9)); //
        // The original columns are here...
        table.insert(new Column("Just_Nämnd", 11, 2));
        table.insert(new Column("Just_länkommm", 12, 4));
        table.insert(new Column("Just_sdn", 13, 2));
        table.insert(new Column("Just_nyckelkod", 14, 6));

        for (Tupel tupel : table.getTupels()) {
            String personalNumber = tupel.get("Personnr").value();
            String date = tupel.get("Provdatum").value();
            ExtendedResidentType info = getLatestResidentInfo(personalNumber, date);
            tupel.get("Ursprungligt_filnamn").set(originalFileName);
            tupel.get("Körningsdatum").set(nowDate);
            tupel.get("Klockslag_körningsdatum").set(nowTime);
            if (info != null) {
                AdministrativIndelningType folkbok = info.getFolkbokforingsaddressIndelning();
                if (folkbok != null) {
                    tupel.get("Just_Nämnd").set(folkbok.getHalsoSjukvardsNamndKod());
                    tupel.get("Just_länkommm").set(folkbok.getPrimaromradeKod());
                    // Ask Pia about above...
                    tupel.get("Just_sdn").set(folkbok.getStadsdelsnamndKod());
                    SvenskAdressTYPE adress = info.getPersonpost().getFolkbokforingsadress();
                    if (adress != null) {
                        tupel.get("Just_nyckelkod").set(adress.getSCBNyckelkod());
                    }
                }
                // Ask Pia about above...
            }
        }

        return table.toString();
    }

    protected ExtendedResidentType getLatestResidentInfo(String forPersonNumber, String justAfterThisTextDate) {
        TreeMap<String, ExtendedResidentType> tree = toTextDateMapped(getInfo(forPersonNumber));
        String floorKey = tree.floorKey(justAfterThisTextDate);
        if (floorKey == null) {
            return null;
        }
        return tree.get(floorKey);
    }

    private TreeMap<String, ExtendedResidentType> toTextDateMapped(List<ExtendedResidentType> info) {
        TreeMap<String, ExtendedResidentType> result = new TreeMap<>();
        int nullCount = 0;
        for (ExtendedResidentType resident : info) {
            String latestChange = resident.getSenasteAndringFolkbokforing();
            if (latestChange == null) {
                latestChange = "18" + String.format("%02d", nullCount++);
                // Pretend this entry dates back to 1800+. Probably this is the first entry for the person... right?
            }
            result.put(latestChange, resident);
        }
        return result;
    }

    private List<ExtendedResidentType> getInfo(String byPersonalNumber) {
        List<ExtendedResidentType> result = new ArrayList<>();
        for (ExtendedResidentType resident : data.getResident()) {
            if (resident.getPersonpost().getPersonId().equals(byPersonalNumber)) {
                result.add(resident);
            }
        }
        return result;
    }

}
