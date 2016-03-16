package se.vgregion.portal.wwwprv.table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clalu4 on 2016-03-14.
 * Holds meta information about one of the columns inside a Table.
 */
public class Column implements Comparable<Column> {

    private String name;

    private int index;

    private int charLength;

    private boolean rightAligned;

    /**
     * Default constructor.
     */
    public Column() {
        super();
    }

    /**
     * Constructs an instance.
     *
     * @param name       name or label of the column.
     * @param index      order inside the table of the column.
     * @param charLength preferred max length of the values represented by this column.
     */
    public Column(String name, int index, int charLength) {
        super();
        this.name = name;
        this.index = index;
        this.charLength = charLength;

        if (name.length() > charLength) {
            this.charLength = name.length();
        }
    }

    /**
     * Constructs a list of columns from a string. It locates text divided only på spaces and makes columns from those.
     * See the description of Table to understand basic Column configuration in this regard.
     *
     * @param heading the text to convert to a series of Columns.
     * @return the columns result of this operation.
     */
    public static List<Column> toColumns(String heading) {
        heading += " ";
        List<Column> result = new ArrayList<>();
        int latestStart = 0;
        String[] heads = heading.split("(?<=\\s)(?=\\S)");
        int count = 0;
        for (String head : heads) {
            Column column = new Column(head.trim(), count++, head.length() - 1);
            result.add(column);
        }

        return result;
    }

    /**
     * Depicts a Column as text.
     *
     * @return a text with name and index and character length.
     */
    @Override
    public String toString() {
        return name + "(" + index + ", " + charLength + ")";
    }

    @Override
    public int compareTo(Column o) {
        if (o == null) {
            return -1;
        }
        return index - o.index;
    }

    /**
     * Getter for charLength.
     *
     * @return charLength
     */
    public int getCharLength() {
        return charLength;
    }

    /**
     * Getter for index.
     *
     * @return index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Getter for name.
     *
     * @return name.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for index.
     *
     * @param index new value.
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Getter for rightAligned.
     *
     * @return rightAligned.
     */
    public boolean isRightAligned() {
        return rightAligned;
    }

    /**
     * Setter for rightAligned.
     *
     * @param rightAligned new value.
     */
    public void setRightAligned(boolean rightAligned) {
        this.rightAligned = rightAligned;
    }

}
