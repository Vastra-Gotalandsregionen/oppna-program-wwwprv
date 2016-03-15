package se.vgregion.portal.wwwprv.table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clalu4 on 2016-03-14.
 */
public class Column implements Comparable<Column> {

    private String name;

    private int index;

    private int charLength;

    private boolean rightAligned;

    public Column() {
        super();
    }

    public Column(String name, int index, int charLength) {
        super();
        this.name = name;
        this.index = index;
        this.charLength = charLength;

        if (name.length() > charLength) {
            this.charLength = name.length();
        }
    }

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

    public int getCharLength() {
        return charLength;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isRightAligned() {
        return rightAligned;
    }

    public void setRightAligned(boolean rightAligned) {
        this.rightAligned = rightAligned;
    }
}
