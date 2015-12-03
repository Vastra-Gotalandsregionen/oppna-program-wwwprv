package se.vgregion.portal.wwwprv.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by clalu4 on 2015-11-24.
 * Meta information about columns in a text-table where the fields are defined by their postions inside each rows
 * text.
 */
public class Columns {

    private final String heading;

    /**
     * Holds specifics about each column in the table.
     */
    public final List<Column> list;

    /**
     * Is sort of an index to use for looking up columns by their name. All Column-objects here is presumed to also
     * exist inside the list property.
     */
    public final Map<String, Column> map;

    /**
     * Creates an instance, parses the provided input to create initial Column-objects inside the list and map
     * properties.
     * @param heading input that is supposed to match the first row inside a text-table - names and implict the
     *                location - begin/end of each column.
     */
    public Columns(String heading) {
        this.heading = heading;
        list = toColumns(heading);
        map = new HashMap<>();
        for (Column column : list) {
            map.put(column.getName(), column);
        }
    }

    private List<Column> toColumns(String heading) {
        heading += " ";
        List<Column> result = new ArrayList<>();
        int latestStart = 0;
        String[] heads = heading.split("(?<=\\s)(?=\\S)");

        for (String head : heads) {
            Column column = new Column(head.trim(), latestStart, (latestStart += head.length()) - 1);
            result.add(column);
        }

        return result;
    }

    /**
     * Information about each column inside a text-table. Name (heading in the file often) and span inside each row
     * where the value resides.
     */
    public class Column {

        private final String name;
        private final int begin;
        private final int end;

        /**
         * Creates an instance.
         * @param name initial value of this.
         * @param begin initial value of this.
         * @param end initial value of this.
         */
        public Column(String name, int begin, int end) {
            this.name = name;
            this.begin = begin;
            this.end = end;
        }

        /**
         * Getter.
         * @return the name.
         */
        public String getName() {
            return name;
        }

        /**
         * Getter.
         * @return the begin.
         */
        public int getBegin() {
            return begin;
        }

        /**
         * Getter.
         * @return the end.
         */
        public int getEnd() {
            return end;
        }

        /**
         * Equality is checked by comparing the names of two objects.
         * @param obj presumably another Column. If not the false is returned.
         * @return true on equality between names of the two Column-objects.
         */
        @Override
        public boolean equals(Object obj) {
            if(obj instanceof Column) {
                return ((Column) obj).name.equals(name);
            }
            return false;
        }
    }

    boolean isLast(Columns.Column column) {
        return list.size() - 1 == list.indexOf(column);
    }

    boolean isLast(String key) {
        Columns.Column column = map.get(key);
        return isLast(column);
    }

}
