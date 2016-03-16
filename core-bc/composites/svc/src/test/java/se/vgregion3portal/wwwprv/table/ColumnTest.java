package se.vgregion.portal.wwwprv.table;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

/**
 * Created by clalu4 on 2016-03-14.
 */
public class ColumnTest {


    @Test
    public void toColumns() {
        String testHeader = "one     two     three four";
        List<Column> columns = Column.toColumns(testHeader);
        System.out.println(testHeader);
        System.out.println(columns);

        Column one = columns.get(0);
        Assert.assertEquals(7, one.getCharLength());

        Column two = columns.get(1);
        Assert.assertEquals(7, two.getCharLength());

    }


    @Test
    public void compareTo() {
        String testHeader = "one     two     three four";
        List<Column> columns = Column.toColumns(testHeader);
        Collections.shuffle(columns);
        Collections.sort(columns);
        System.out.println(columns);
    }

}