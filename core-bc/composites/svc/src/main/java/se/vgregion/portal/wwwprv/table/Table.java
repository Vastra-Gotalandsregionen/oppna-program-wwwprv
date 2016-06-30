package se.vgregion.portal.wwwprv.table;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by clalu4 on 2016-03-14.
 * A representation of a textual table. It holds both the data, as rows or Tupels, ant the meta-data connected to those
 * as Columns.
 * It can be constructed with a text where the first row defines the columns, their name och width. A column starts
 * at the char where the name of the column starts and ends one char before the beginning of the next column (or the
 * end of the row).
 *
 */
public class Table {

    private final List<Column> columns;

    private final List<Tupel> tupels = new ArrayList<>();

    /**
     * Constructs an empty table. No columns and no data.
     */
    private Table() {
        super();
        columns = new ArrayList<>();
    }

    /**
     * Constructs a new table.
     * @param text  This text will result in a table with three columns with the width of 8 for the two first and 7 for the last one.
     * <pre>
     *     Column1  Column2  Column3
     *     Value1   Value2   Value3
     *     Value4   Value5   Value6
     * </pre>
     * Three tupel will be created.
     */
    private Table(String text) {
        super();
        List<String> lines = Arrays.asList(text.split(("\\n")));
        columns = Column.toColumnsSplitBySpaces(lines.get(0));

        if (lines.size() <= 1) {
            return;
        }

        List<String> sub = lines.subList(1, lines.size());

        for (String line : sub) {
            Tupel tupel = new Tupel(columns, line);
            tupels.add(tupel);
        }
    }

    public static Table newEmptyTable() {
        return new Table();
    }

    public static Table newTableFromSpaceDelimInput(String text) {
        List<String> lines = Arrays.asList(text.split(("\\n")));
        List<Column> columns = Column.toColumnsSplitBySpaces(lines.get(0));

        return newTable(lines, columns);
    }

    public static Table newTableFromSemiColonDelimInput(String text) {
        List<String> lines = Arrays.asList(text.split(("\\n")));
        List<Column> columns = Column.toColumnsSplitBySemicolon(lines.get(0));

        return newTable(lines, columns);
    }

    private static Table newTable(List<String> lines, List<Column> columns) {
        Table table = new Table();

        table.columns.addAll(columns);

        if (lines.size() <= 1) {
            return table;
        }

        List<String> sub = lines.subList(1, lines.size());

        for (String line : sub) {
            Tupel tupel = new Tupel(table.columns, line);
            table.tupels.add(tupel);
        }

        return table;
    }

    /**
     * Gets the data in this table. If the content of this list is modified then the content of the table also will. Its
     * one and the same - no defencive copying. Use the result of this to access and modify the data.
     * @return a list of tupels.
     */
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

    /**
     * Inserts a column in the table. This is the preferred way of adding a column after the table have been
     * instantiated.
     * @param column the column to insert into the collective of columns.
     */
    public Column insert(final Column column) {
        columns.add(column.getIndex(), column);
        int counter = 0;
        for (Column c : columns) {
            c.setIndex(counter++);
        }
        Collections.sort(columns);
        return column;
    }

    /**
     * Creates a text-mass that reflects all the column-names and values separated by a provided text.
     * @param withThisDelimiter what to put between each value/column-name as field separator.
     * @return the text 'showing' the table as... well text.
     */
    public String toString(String withThisDelimiter) {
        StringBuilder sb = new StringBuilder();
        List<String> list = new ArrayList<>();
        for (Column column : columns) {
            list.add(column.getName());
        }
        sb.append(StringUtils.join(list, withThisDelimiter));
        sb.append("\n");

        for (Tupel tupel : tupels) {
            list = new ArrayList<>();
            for (Column column : columns) {
                list.add((tupel.get(column).value().trim()));
            }
            sb.append(StringUtils.join(list, withThisDelimiter));
            sb.append("\n");
        }
        return sb.toString();
    }

    public String toExcelCsvText() {
        String withThisDelimiter = ";";
        StringBuilder sb = new StringBuilder();
        List<String> list = new ArrayList<>();
        for (Column column : columns) {
            list.add(column.getName());
        }
        sb.append(StringUtils.join(list, withThisDelimiter));
        sb.append("\n");

        for (Tupel tupel : tupels) {
            list = new ArrayList<>();
            for (Column column : columns) {
                list.add(toSafeExcel(tupel.get(column).value().trim()));
            }
            sb.append(StringUtils.join(list, withThisDelimiter));
            sb.append("\n");
        }
        return sb.toString();
    }

    static String toSafeExcel(String text) {
        if (text == null || "null".equals(text)) {
            return "";
        }
        if (NumberUtils.isNumber(text)) {
            text = "=\"" + text + '"';
        }
        text = text.replaceAll(Pattern.quote("\""), "\"\"");
        text = '"' + text + '"';
        return text;
    }

    /**
     * Prints the table with fixed sized column width.
     * @return Text display of the table.
     */
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

    /**
     * Getter for the columns. Adding columns to this list can have some side-effects. It could cause the printout
     * (toString()) of the object to show the columns in an unforseen order. Use the insert(Column)-method for this
     * instead.
     * Removing one of the columns would be more appropriate usage.
     *
     * @return the columns of this table.
     */
    public List<Column> getColumns() {
        return columns;
    }

    public Column getColumnByName(String name) {
        return Column.getColumnByName(columns, name);
    }

}
