package se.vgregion.portal.wwwprv.table;

/**
 * Created by clalu4 on 2016-03-14.
 */
public class Cell {

    private String content = "";

    private Column column;

    public String value() {
        if (content == null) {
            return "";
        }
        return content;
    }

    public void set(String content) {
        this.content = content;
    }

    @Override
    public int hashCode() {
        return content.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Cell) {
            return ((Cell) obj).content.equals(content);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "C(" + content + ")";
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    public Column getColumn() {
        return column;
    }
}
