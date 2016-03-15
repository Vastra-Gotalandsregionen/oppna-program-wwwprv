package se.vgregion.portal.wwwprv.table;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by clalu4 on 2016-03-14.
 */
public class Table {

    private final List<Column> columns;

    private final List<Tupel> tupels = new ArrayList<>();

    public Table(String text) {
        super();
        List<String> lines = Arrays.asList(text.split(("\\n")));
        columns = Column.toColumns(lines.get(0));

        if (lines.size() <= 1) {
            return;
        }

        List<String> sub = lines.subList(1, lines.size() - 1);

        for (String line : sub) {
            Tupel tupel = new Tupel(columns, line);
            tupels.add(tupel);
        }
    }

    public List<Tupel> getTupels() {
        return tupels;
    }

    private TreeMap<Integer, Column> toIndexedColumns() {
        TreeMap<Integer, Column> index = new TreeMap<>();
        for (Column column : columns) {
            index.put(column.getIndex(), column);
        }
        return index;
    }

    public void insert(final Column column) {
        columns.add(column.getIndex(), column);
        int counter = 0;
        for (Column c : columns) {
            c.setIndex(counter++);
        }
        Collections.sort(columns);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Column last = columns.get(columns.size() - 1);
        for (Column column : columns) {
            sb.append(padLeft(column.getName(), column.getCharLength()));
            if (column != last) {
                sb.append(" ");
            }
        }
        sb.append("\n");

        for (Tupel tupel : tupels) {
            for (Column column : columns) {
                if (column.isRightAligned()) {
                    sb.append(padRight(tupel.get(column).value(), column.getCharLength()));
                } else {
                    sb.append(padLeft(tupel.get(column).value(), column.getCharLength()));
                }
                if (column != last) {
                    sb.append(" ");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    static String padLeft(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    static String padRight(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }

    public List<Column> getColumns() {
        return columns;
    }
}
