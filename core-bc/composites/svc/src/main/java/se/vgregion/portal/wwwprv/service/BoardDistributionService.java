package se.vgregion.portal.wwwprv.service;

import org.springframework.beans.factory.annotation.Autowired;
import se.riv.population.residentmaster.extended.v1.AdministrativIndelningType;
import se.riv.population.residentmaster.extended.v1.ExtendedResidentType;
import se.riv.population.residentmaster.lookupresidentforextendedprofileresponder.v1.LookupResidentForExtendedProfileResponseType;
import se.riv.population.residentmaster.v1.SvenskAdressTYPE;
import se.vgregion.portal.wwwprv.util.Text;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import static se.vgregion.portal.wwwprv.util.ColumnNames.EXTENDED;
import static se.vgregion.portal.wwwprv.util.ColumnNames.SIMPLER;

/**
 * Created by clalu4 on 2015-11-30.
 */
public class BoardDistributionService {

    @Autowired
    private PopulationService populationService;

    private static final ThreadLocal<Thread> currentDistributionFileContentWorker = new InheritableThreadLocal<>();

    public synchronized Text makeDistributionFileContent(Text fromInputFileAsLines, String withOriginalFileName, String fromSupplier) {
        assert fromInputFileAsLines != null;
        assert withOriginalFileName != null;
        assert fromSupplier != null;

        Text result = new Text(getClass().getResourceAsStream(fromSupplier + ".header-template"));

        final Date now = new Date();
        List<String> personalNumbers = new ArrayList<>();
        for (Text.Line from : fromInputFileAsLines.lines.subList(1, fromInputFileAsLines.lines.size() - 1)) {
            Text.Line nl = result.apendLine();
            //nl.set(firstOldColumnInResult.getName(), from.getContent());
            nl.set(EXTENDED.korn_datum, now);
            nl.set(EXTENDED.filnamn, withOriginalFileName);

            //nl.set(EXTENDED.avtalskod, from.get());
            nl.set(EXTENDED.BesoksDatum, from.get(SIMPLER.Datum));
            nl.set(EXTENDED.personnr, from.get(SIMPLER.Pnr));
            nl.set(EXTENDED.Namn, from.get(SIMPLER.Namn));
            nl.set(EXTENDED.Betalare, from.get(SIMPLER.Betalare));
            nl.set(EXTENDED.Beställare, from.get(SIMPLER.Beställare));
            nl.set(EXTENDED.Analyskod, from.get(SIMPLER.Analys¤kod));
            nl.set(EXTENDED.AnalysNamn, from.get(SIMPLER.Analysnamn));
            nl.set(EXTENDED.Cap_pris, from.get(SIMPLER.Pris).replace('.', ','));

            personalNumbers.add(from.get(SIMPLER.Pnr).trim());
        }

        LookupResidentForExtendedProfileResponseType populationInf = populationService.lookup(personalNumbers);

        BagMap numberKeyd = new BagMap();

        for (ExtendedResidentType resident : populationInf.getResident()) {
            String pn = resident.getPersonpost().getPersonId();
            numberKeyd.get(pn).add(resident);
        }

        for (Text.Line line : result.lines.subList(2, result.lines.size() - 1)) {
            String pn = line.get(EXTENDED.personnr);
            if (numberKeyd.get(pn).isEmpty()) {
                System.out.println("Did not find " + pn);
            } else {
                ExtendedResidentType item = numberKeyd.get(pn).first();
                SvenskAdressTYPE fba = item.getPersonpost().getFolkbokforingsadress();

                if (fba != null) {
                    line.set(EXTENDED.PatLan, fba.getLanKod());
                    line.set(EXTENDED.PatKommun, fba.getKommunKod());
                    line.set(EXTENDED.NyckelKod, fba.getSCBNyckelkod());
                }

                AdministrativIndelningType folkBokforingsAdressIndelning = item.getFolkbokforingsaddressIndelning();
                if (folkBokforingsAdressIndelning != null) {
                    line.set(EXTENDED.SDN, item.getFolkbokforingsaddressIndelning().getStadsdelsnamndKod());
                    line.set(EXTENDED.Namnd, item.getFolkbokforingsaddressIndelning().getHalsoSjukvardsNamndKod());
                }
            }
        }


        // PatLan     PatKommun  SDN        NyckelKod  Namnd


        return result;
    }


    private class BagMap extends HashMap<String, NavigableSet<ExtendedResidentType>> {
        @Override
        public NavigableSet<ExtendedResidentType> get(Object key) {
            NavigableSet<ExtendedResidentType> result = super.get(key);
            if (result == null) {
                Comparator<? super ExtendedResidentType> fo = new Comparator<ExtendedResidentType>() {
                    @Override
                    public int compare(ExtendedResidentType o1, ExtendedResidentType o2) {
                        if (o1 == null) o1 = new ExtendedResidentType();
                        if (o2 == null) o2 = new ExtendedResidentType();
                        return -whenNullThenBlank(o1.getSenasteAndringFolkbokforing()).compareTo(whenNullThenBlank(o2.getSenasteAndringFolkbokforing()));
                    }
                };
                put((String) key, result = new TreeSet<ExtendedResidentType>(fo));
            }
            return result;
        }
    }

    private String whenNullThenBlank(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }


    public synchronized Thread runMakeDistributionFileContent(final InputStream withInput, final String andOriginalFileName, final String fromSupplier, final OutputStream writeResultsIntoHere) {
        Runnable r = new Runnable() {
            public void run() {
                try {
                    Text source = new Text(withInput);
                    Text result = makeDistributionFileContent(source, andOriginalFileName, fromSupplier);
                    for (String row : result.toRawLines()) {
                        writeResultsIntoHere.write(row.getBytes());
                        writeResultsIntoHere.write("\n".getBytes());
                    }
                    writeResultsIntoHere.flush();
                    writeResultsIntoHere.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Thread thread = new Thread(r);
        getCurrentDistributionFileContentWorker().set(thread);
        thread.start();
        return thread;
    }

    public synchronized void makeDistributionFileContent(final InputStream withInput, final String andOriginalFileName, final String fromSupplier, final OutputStream writeResultsIntoHere) {
        /*Runnable runnable = new Runnable() {
            public void run() {*/
        //System.out.println("I'm running");
                Text source = new Text(withInput);
                Text result = makeDistributionFileContent(source, andOriginalFileName, fromSupplier);
                BufferedOutputStream bof = new BufferedOutputStream(writeResultsIntoHere);
                try {
                    for (String row : result.toRawLines()) {
                        bof.write(row.getBytes());
                    }
                    writeResultsIntoHere.flush();
                    writeResultsIntoHere.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                //System.out.println("I'm done running");
           /* }
        };
        Thread thread = new Thread(runnable);
        thread.start();*/
    }

    /**
     * Gets the current / latest thread that is / where used to run the method runMakeDistributionFileContent.
     * @return thread that is used.
     */
    public static ThreadLocal<Thread> getCurrentDistributionFileContentWorker() {
        return currentDistributionFileContentWorker;
    }

}
