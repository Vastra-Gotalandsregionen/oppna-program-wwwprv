package se.vgregion.portal.wwwprv.util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a tabular text-file with position based field specification. Ie, the fields are at certain positions on
 * each row.
 * The first line in the table is expected to be a header row. That row is used in discovering what fields / columns
 * exists. This information is obtained when / if any content is provided when the Text object is created.
 */
public class Text {

    /**
     * Holds meta information about the fields inside each row.
     */
    public final Columns columns;

    private static final SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:s.S");

    /**
     * Actual data from the text-table, represented as Line-objects.
     */
    public final List<Line> lines = new ArrayList<Line>();/*{
        @Override
        public boolean add(Line o) {
            boolean r = super.add(o);
            onItemAdded(indexOf(o), o);
            return r;
        }

        @Override
        public void add(int index, Line element) {
            super.add(index, element);
            onItemAdded(index, element);
        }

        @Override
        public boolean addAll(Collection<? extends Line> c) {
            boolean r = false;
            for (Line l : c) {
                if (add(l)) {
                    r = true;
                }
            }
            return r;
        }

        @Override
        public boolean addAll(int index, Collection<? extends Line> c) {
            boolean r = false;
            for (Line l : c) {
                int size = size();
                add(index++, l);
                if (size != size())
                    r = true;
            }
            return r;
        }
    };*/

    /**
     * Creates instance from a list of texts.
     *
     * @param raw contains texts that represents a text-table
     */
    public Text(List<String> raw) {
        this.columns = new Columns(raw.get(0));
        for (String text : raw) {
            Line line = new Line(text);
            lines.add(line);
        }
    }

    /**
     * Creates a new, empty, instance.
     */
    public Text() {
        this(new ArrayList<String>());
    }

    /**
     * Creates instance from a file.
     *
     * @param file contains texts that represents a text-table
     */
    public Text(File file) {
        this(toLines(file));
    }

    /**
     * Creates instance from a stream.
     *
     * @param is contains texts that represents a text-table
     */
    public Text(InputStream is) {
        this(toLines(is));
    }

    /**
     * Utility method to load a input stream as a list of texts.
     *
     * @param file the input stream to load text lines from.
     * @return text from the provided stream.
     */
    public static List<String> toLines(InputStream file) {
        try {
            return toLinesImp(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*public void add(List<String> raw) {
        for (String text : raw) {
            Line line = new Line(text);
            lines.add(line);
        }
    }*/

    private static List<String> toLinesImp(InputStream file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file, "UTF8"));
        List<String> result = new ArrayList<>();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            result.add(line);
        }
        bufferedReader.close();
        return result;
    }

    /**
     * Utility method to load a file as a list of texts.
     *
     * @param file the file to load text lines from.
     * @return text from the provided file.
     */
    public static List<String> toLines(File file) {
        try {
            return toLinesImp(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*public void add(InputStream from) {
        try {
            addImp(from);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }*/

    /*private void addImp(InputStream from) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(from));
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(new Line(line));
        }
        bufferedReader.close();
    }*/


    private static List<String> toLinesImp(File file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getPath()));
        List<String> result = new ArrayList<>();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            result.add(line);
        }
        bufferedReader.close();
        return result;
    }

    /**
     * Creates a new Line object and adds it into itself at the end.
     *
     * @return a new line added inside the lines list.
     */
    public Line appendLine() {
        return appendLine("");
    }

    /**
     * Creates a new Line object and adds it into itself at the end.
     *
     * @param content initial values in the form of one single text row.
     * @return a new line added inside the lines list.
     */
    public Line appendLine(String content) {
        Line result = new Line(content);
        lines.add(result);
        return result;
    }

    /**
     * Represents each tupel / line in the text table.
     */
    public class Line {

        private String content;

        /**
         * Creates an instance with an initial content - fields for columns of this text-table.
         *
         * @param content
         */
        public Line(String content) {
            if (content == null) {
                content = "";
            }
            this.content = content;
        }

        /**
         * Creates an empty row.
         */
        public Line() {
            this("");
        }

        /**
         * Gets a value from a column position inside the row represented by this instance.
         *
         * @param key name of the column that represents the position to look for the value to return.
         * @return field value of this row, designated by the key param.
         */
        public String get(String key) {
            Columns.Column meta = getColumnAndGuaranteeValueLength(key);
            if (columns.isLast(meta)) {
                return content.substring(meta.getBegin()).trim();
            }
            return content.substring(meta.getBegin(), meta.getEnd()).trim();
        }

        /**
         * Puts a value inside the string content of this row.
         *
         * @param key   name of the column that describes where to put the data.
         * @param value actual value to put in the text-row holding all values for this post.
         */
        public void set(String key, String value) {
            if (value == null) {
                value = "";
            }
            Columns.Column meta = getColumnAndGuaranteeValueLength(key);
            content = pad(content, meta.getEnd());
            value = pad(value, meta.getEnd() - meta.getBegin());
            content = content.substring(0, meta.getBegin()) + value + content.substring(meta.getEnd());
        }

        /**
         * Special setter for date. It uses the pattern yyyy-MM-dd HH:mm:s.S when writing to the content row.
         *
         * @param key   name of the column to put the value inside.
         * @param value the date value itself - that will be converted to a textual representation when put inside the
         *              instance.
         */
        public void set(String key, Date value) {
            String text = fullDateFormat.format(value);
            set(key, text);
        }

        /**
         * Explicit setter for a date value with a pattern to use for conversion to textual representation.
         *
         * @param key           name of the column to put the value inside.
         * @param formatPattern pattern to use on the date before putting that, resulting, new value inside the line
         *                      representing this row in the text-table.
         * @param value         the value to set. Will be converted to text before being put inside the content of this row.
         */
        public void set(String key, String formatPattern, Date value) {
            SimpleDateFormat sdf = new SimpleDateFormat(formatPattern);
            String text = sdf.format(value);
            set(key, text);
        }

        private Columns.Column getColumnAndGuaranteeValueLength(String key) {
            Columns.Column meta = columns.map.get(key);

            if (meta == null) {
                throw new IllegalArgumentException("Your key: '" + key + "', all keys available: " + columns.map.keySet());
            }

            if (content.length() <= meta.getEnd()) {
                content = pad(content, meta.getEnd());
            }
            return meta;
        }

        private String pad(String that, int upToPosition) {
            for (int i = that.length(); i < upToPosition; i++) {
                that += " ";
            }
            return that;
        }

        /**
         * Gets entire content of this row.
         *
         * @return the row of positional spaced values hold by this instance.
         */
        public String getContent() {
            return content;
        }

        /**
         * Sets the content of this instance.
         *
         * @param content the row of positional spaced values hold by this instance.
         */
        public void setContent(String content) {
            this.content = content;
        }
    }

    /**
     * Converts the lines inside this object to a plain list of strings. Useful when the content is supposed to be
     * saved to a file or such.
     *
     * @return The strings representing all the lines in this instance.
     */
    public List<String> toRawLines() {
        List<String> result = new ArrayList<>();
        for (Line line : lines) {
            result.add(line.getContent());
        }
        return result;
    }

/*    protected void onItemAdded(int index, Line item) {

    }*/

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Text) {
            return ((Text) obj).toRawLines().equals(toRawLines());
        } else {
            return false;
        }
    }

    /**
     * Extract all value from a column.
     * @param key name of column to get all values from.
     * @return all values from that column.
     */
    public List<String> getAllValueFromColumn(String key) {
        List<String> result = new ArrayList<>();
        for (Line line : lines) {
            result.add(line.get(key));
        }
        return result;
    }

}
