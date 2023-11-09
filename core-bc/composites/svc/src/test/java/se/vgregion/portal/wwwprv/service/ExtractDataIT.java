package se.vgregion.portal.wwwprv.service;

import org.apache.commons.collections.BeanMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import riv.population.residentmaster._1.*;
import se.riv.population.residentmaster.extended.v1.ExtendedResidentType;
import se.riv.population.residentmaster.lookupresidentforextendedprofileresponder.v1.LookupResidentForExtendedProfileResponseType;
import se.vgregion.portal.wwwprv.DummyPersonalNumbers;
import se.vgregion.portal.wwwprv.table.Column;
import se.vgregion.portal.wwwprv.table.Table;
import se.vgregion.portal.wwwprv.table.Tupel;

/**
 * Created by clalu4 on 2016-03-17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:population-test.xml")
public class ExtractDataIT {

    @Autowired
    private ExtendedPopulationService extendedPopulationService;

    @Test
    public void main() {
        LookupResidentForExtendedProfileResponseType data = extendedPopulationService.lookup(DummyPersonalNumbers.get());

        Table table = Table.newEmptyTable();
        table.insert(new Column("Senaste_Aandring", 0, 12));
        table.insert(new Column("Sekretess", 1, 2));

        for (ExtendedResidentType resident : data.getResident()) {
            Tupel item = new Tupel(table.getColumns(), "");
            NamnTYPE namn = resident.getPersonpost().getNamn();
            addValueAndOrColumns("namn:", namn, table, item);
            item.get("Senaste_Aandring").set(resident.getSenasteAndringFolkbokforing());
            item.get("Sekretess").set(resident.getSekretessmarkering() + "");
            addValueAndOrColumns("fbi:", resident.getFolkbokforingsaddressIndelning(), table, item);
            addValueAndOrColumns("pp:", resident.getPersonpost(), table, item);
            AvregistreringTYPE avreg = resident.getPersonpost().getAvregistrering();
            addValueAndOrColumns("avreg:", avreg, table, item);
            FodelseTYPE fodelse = resident.getPersonpost().getFodelse();
            addValueAndOrColumns("föd:", fodelse, table, item);
            SvenskAdressTYPE fadress = resident.getPersonpost().getFolkbokforingsadress();
            addValueAndOrColumns("fadress:", fadress, table, item);
            InvandringTYPE inv = resident.getPersonpost().getInvandring();
            addValueAndOrColumns("inv:", inv, table, item);
            RelationerTYPE rel = resident.getPersonpost().getRelationer();
            addValueAndOrColumns("rel:", rel, table, item);
            SvenskAdressTYPE sarskadress = resident.getPersonpost().getSarskildPostadress();
            addValueAndOrColumns("sarskadress:", sarskadress, table, item);
            UtlandsadressTYPE utlAdress = resident.getPersonpost().getUtlandsadress();
            addValueAndOrColumns("utlAdress:", utlAdress, table, item);

            table.getTupels().add(item);
        }

        System.out.println(table);
        System.out.println(table.toString(";"));
    }

    protected void addValueAndOrColumns(String prefix, Object bean, Table intoThis, Tupel onRow) {
        if (bean == null) {
            return;
        }
        BeanMap columnsFromThis = new BeanMap(bean);
        for (Object o : columnsFromThis.keySet()) {
            if (columnsFromThis.getType((String) o).equals(String.class)) {
                String cn = prefix + o;
                Column column = intoThis.getColumnByName(cn);
                if (column == null) {
                    column = intoThis.insert(new Column(cn, intoThis.getColumns().size(), 1));
                }
                String value = (String) columnsFromThis.get(o);
                if (value == null) {
                    value = "";
                }
                if (column.getCharLength() < value.length()) {
                    column.setCharLength(value.length());
                }
                onRow.get(column).set(value);
            }
        }
    }

}
