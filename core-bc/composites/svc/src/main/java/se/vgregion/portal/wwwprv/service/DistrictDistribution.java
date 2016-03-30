package se.vgregion.portal.wwwprv.service;

/**
 * Created by clalu4 on 2016-03-15.
 * Interface for processing of batch files containing information about medical services performed for inhabitants of
 * VGR. This 'processing' adds additional information to the file about the events.
 */
public interface DistrictDistribution {

    /**
     * Main method of doint the file processing.
     * @param input a text forming at 'text-table' - a table with fixed length columns and headings that holds the data.
     * @return a csv-text that, logically, looks like the input but have been extended with some extra columns.
     * @throws DistrictDistributionException
     */
    String process(String input) throws DistrictDistributionException;

}
