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
 * Should mirror the meaning of Filspec_Unilabs_Rontgen_Nämndf_ver2.doc.
 * Den här är samma som den andra UnilabsLab?!
 */
public class UnilabsRontgen extends UnilabsLab {


    public UnilabsRontgen(PopulationService service, String originalFileName) {
        super(service, originalFileName);
    }
}
