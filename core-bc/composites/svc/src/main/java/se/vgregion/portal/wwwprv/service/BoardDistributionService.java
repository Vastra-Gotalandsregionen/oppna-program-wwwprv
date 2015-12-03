package se.vgregion.portal.wwwprv.service;

import org.springframework.beans.factory.annotation.Autowired;
import se.riv.population.residentmaster.extended.v1.AdministrativIndelningType;
import se.riv.population.residentmaster.extended.v1.ExtendedResidentType;
import se.riv.population.residentmaster.lookupresidentforextendedprofileresponder.v1.LookupResidentForExtendedProfileResponseType;
import se.riv.population.residentmaster.v1.SvenskAdressTYPE;
import se.vgregion.portal.wwwprv.util.Text;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import static se.vgregion.portal.wwwprv.util.ColumnNames.EXTENDED;
import static se.vgregion.portal.wwwprv.util.ColumnNames.SIMPLER;

/**
 * Created by clalu4 on 2015-11-30.
 * Business logic to produce a board distributed text-table (as a file or stream). It will take input in the form of a
 * mass of text containing a table with information about health care. As output it will produce another text-table
 * where the previous values (from the input) have been complemented with data about where each patient lives (region)
 * and what administrative board he/she belongs to.
 * In order to do this it will access external data from resident databases. The usage of this service are, typically,
 * like some kind of batch job - not something that will happen immediately when the user loads data inside the system.
 * For that purpose the {@link #runMakeDistributionFileContent(InputStream, String, String, OutputStream)} is
 * provided - it will run the functionality inside another thread.
 */
public class BoardDistributionService {

    @Autowired
    private PopulationService populationService;

    private static final ThreadLocal<Thread> currentDistributionFileContentWorker = new InheritableThreadLocal<>();

    public BoardDistributionService() {
        super();
    }

    public BoardDistributionService(PopulationService populationService) {
        this();
        this.populationService = populationService;
    }

    /**
     * Merges input from the method call with data from a populace database.
     *
     * @param fromInputFileAsLines the table of data which is to be decorated with data from the residential database.
     * @param withOriginalFileName the name of the file used by the client operator when initializing this process.
     * @param fromSupplier         supplier code, for instance "Unilabs_RTG", "Unilabs_Lab", "Unilabs_RTG_Hisingen",
     *                             "STMGBG15SVK", "skasmam", "pvofmam", "Privmam", "GAMBRO15OVK", "HALLAND15", "testar",
     *                             "LUN15OV", "LUN15SV" or "Unilabs_S50MA50".
     * @return a merger of the provided data and the data fetched, by each patient personal number, from the population
     * database.
     */
    public synchronized Text makeDistributionFileContent(Text fromInputFileAsLines, String withOriginalFileName, String fromSupplier) {
        assert fromInputFileAsLines != null;
        assert withOriginalFileName != null;
        assert fromSupplier != null;

        Text result = new Text(getClass().getResourceAsStream(fromSupplier + ".header-template"));

        final Date now = new Date();
        List<String> personalNumbers = new ArrayList<>();
        for (Text.Line from : fromInputFileAsLines.lines.subList(1, fromInputFileAsLines.lines.size() - 1)) {
            Text.Line nl = result.appendLine();

            nl.set(EXTENDED.korn_datum, now);
            nl.set(EXTENDED.filnamn, withOriginalFileName);
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

    /**
     * Se explanation of class and of {@link #makeDistributionFileContent(Text, String, String)}.
     * This method reads and writes streams inside a separate thread. This can be obtained by this methods return value
     * or by the static field getter {@link static getCurrentDistributionFileContentWorker()}
     *
     * @param withInput            corresponds to the fromInputFileAsLines parameter in
     *                             {@link #makeDistributionFileContent(Text, String, String)}. This stream is read and then closed.
     * @param andOriginalFileName  corresponds to the withOriginalFileName parameter in
     *                             {@link #makeDistributionFileContent(Text, String, String)}.
     * @param fromSupplier         corresponds to the fromSupplier parameter in
     *                             {@link #makeDistributionFileContent(Text, String, String)}.
     * @param writeResultsIntoHere the result of the process is written to this stream. The result of
     *                             {@link #makeDistributionFileContent(Text, String, String)} is put inside this, then the stream is flushed and closed.
     * @return the thread that is used for running the process (start have been called on this).
     */
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

    /**
     * Gets the current / latest thread that is / where used to run the method runMakeDistributionFileContent.
     *
     * @return thread that is used.
     */
    public static ThreadLocal<Thread> getCurrentDistributionFileContentWorker() {
        return currentDistributionFileContentWorker;
    }

}
