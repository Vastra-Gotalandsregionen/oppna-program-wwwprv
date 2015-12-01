package se.vgregion.portal.wwwprv.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by clalu4 on 2015-11-24.
 */
public class Columns {

    private final String heading;

    public final List<Column> list;

    public final Map<String, Column> map;

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

    public class Column {
        private final String name;
        private final int begin;
        private final int end;

        public Column(String name, int begin, int end) {
            this.name = name;
            this.begin = begin;
            this.end = end;
        }

        public String getName() {
            return name;
        }

        public int getBegin() {
            return begin;
        }

        public int getEnd() {
            return end;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof Column) {
                return ((Column) obj).name.equals(name);
            }
            return false;
        }
    }

}
