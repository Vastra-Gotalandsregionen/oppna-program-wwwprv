package se.vgregion.portal.wwwprv.service;

import se.riv.population.residentmaster.extended.v1.AdministrativIndelningType;
import se.riv.population.residentmaster.extended.v1.ExtendedResidentType;
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

    //protected LookupResidentForExtendedProfileResponseType data;

    public UnilabsLab(PopulationService service, String originalFileName) {
        this.service = service;
        this.originalFileName = originalFileName;
    }

    @Override
    public String process(String input) {
        Table table = new Table(input);

        //List<String> personalNumbers = new ArrayList<>();
        /*
        for (Tupel tupel : table.getTupels()) {
            personalNumbers.add(tupel.get("personnr").value());
        }
        data = service.lookup(personalNumbers);*/

        String nowDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        table.insert(new Column("korn_datum", 0, 10));
        table.insert(new Column("klockslag", 1, 9)); //
        table.insert(new Column("filnamn", 2, originalFileName.length()));
        // The original columns are here...
        table.insert(new Column("Pat_Nämnd", table.getColumns().size(), 2));
        table.insert(new Column("PatLan_+_kommun", table.getColumns().size(), 4));
        table.insert(new Column("Pat_SDN", table.getColumns().size(), 2));
        table.insert(new Column("Pat_nyckelkod", table.getColumns().size(), 6));

        Column dateKey = table.getColumns().get(3);
        Column personalNumberKey = table.getColumns().get(4);

        for (Tupel tupel : table.getTupels()) {
            String personalNumber = tupel.get(personalNumberKey).value().trim();
            String date = tupel.get(dateKey).value().trim();
            ExtendedResidentType info = getResidentialInfo(personalNumber, date);
            //ExtendedResidentType info = getLatestResidentInfo(personalNumber, date);
            tupel.get("korn_datum").set(nowDate);
            tupel.get("filnamn").set(originalFileName);
            String nowTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
            tupel.get("klockslag").set(nowTime);
            if (info != null) {
                AdministrativIndelningType folkbok = info.getFolkbokforingsaddressIndelning();
                if (folkbok != null) {
                    tupel.get("Pat_Nämnd").set(folkbok.getHalsoSjukvardsNamndKod());
                    tupel.get("PatLan_+_kommun").set(folkbok.getPrimaromradeKod());
                    // Split this into län - kom
                    // Ask Pia about above...
                    tupel.get("Pat_SDN").set(folkbok.getStadsdelsnamndKod());
                    SvenskAdressTYPE adress = info.getPersonpost().getFolkbokforingsadress();
                    if (adress != null) {
                        tupel.get("Pat_nyckelkod").set(adress.getSCBNyckelkod());
                    }
                }
                // Ask Pia about above...
            }
        }

        return table.toString(";");
    }

    ExtendedResidentType getResidentialInfo(String forPersonalNumber, String fromDate) {
        return service.lookup(new PopulationService.Arg(forPersonalNumber, fromDate)).get(0);
    }

    /*
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
    }*/

}
