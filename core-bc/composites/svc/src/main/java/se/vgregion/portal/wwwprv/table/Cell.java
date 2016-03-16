package se.vgregion.portal.wwwprv.table;

/**
 * Created by clalu4 on 2016-03-14.
 * A value-wrapper for the values inside a Table.
 */
public class Cell {

    private String content = "";

    private Column column;

    /**
     * A getter, with-out the convenction. Difference is that it trims the value before returning it and also protects
     * against NPEs (returns "" instead of null).
     * @return
     */
    public String value() {
        if (content == null) {
            return "";
        }
        return content;
    }

    /**
     * Sets the wrapped value of this object.
     * @param content new value.
     */
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

    /**
     * Setter for column.
     * @param column column.
     */
    public void setColumn(Column column) {
        this.column = column;
    }

    /**
     * Getter for the column value.
     * @return column.
     */
    public Column getColumn() {
        return column;
    }

}
