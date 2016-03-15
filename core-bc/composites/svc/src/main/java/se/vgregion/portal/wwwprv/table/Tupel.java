package se.vgregion.portal.wwwprv.table;

import java.util.HashMap;
import java.util.List;

/**
 * Created by clalu4 on 2016-03-14.
 */
public class Tupel extends HashMap<String, Cell> {

    private final List<Column> columns;

    @Override
    public Cell get(Object key) {
        if (key instanceof Column) {
            key = ((Column) key).getName();
        }
        Cell r = super.get(key);
        if (r == null) {
            r = new Cell();
            r.setColumn(getColumnByName((String) key));
            put((String) key, r);
        }
        return super.get(key);
    }

    private Column getColumnByName(String name) {
        for (Column column : columns) {
            if (name.equals(column.getName())) {
                return column;
            }
        }
        return null;
    }

    public Tupel(List<Column> columns, String line) {
        this.columns = columns;
        int offset = 0;
        for (Column column : columns) {
            int start = offset;
            int end = offset + column.getCharLength();
            start = Math.min(start, line.length());
            end = Math.min(end, line.length());
            String part = line.substring(start, end);
            get(column).set(part);
            offset += column.getCharLength() + 1;
        }
    }

    public List<Column> getColumns() {
        return columns;
    }

}
