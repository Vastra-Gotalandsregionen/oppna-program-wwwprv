package se.vgregion.portal.wwwprv.table;

import java.util.HashMap;
import java.util.List;

/**
 * Created by clalu4 on 2016-03-14.
 * Represents a row in the Table object.
 */
public class Tupel extends HashMap<String, Cell> {

    private final List<Column> columns;

    /**
     * The get method returns a new instance, inserted into the Tupel, if no item already is present for the key.
     * @param key a string or Column that represents a cell value inside this map.
     * @return a Cell. Never a null. a unmapped value results in a put of a new Cell object and then this new value
     * is returned.
     */
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

    Column getColumnByName(String name) {
        return Column.getColumnByName(columns, name);
    }

    /**
     * Makes a new instance.
     * @param columns Columns that might be used inside this object.
     * @param line the values represented by this text.
     */
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

    /**
     * Getter for columns.
     * @return columns.
     */
    public List<Column> getColumns() {
        return columns;
    }

}
