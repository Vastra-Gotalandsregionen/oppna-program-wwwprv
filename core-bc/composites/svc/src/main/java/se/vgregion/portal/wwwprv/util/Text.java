package se.vgregion.portal.wwwprv.util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Holder class an
 */
public class Text {

    public final Columns columns;

    private static final SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:s.S");

    public final List<Line> lines = new ArrayList<Line>(){
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
    };

    public Text(List<String> raw) {
        this.columns = new Columns(raw.get(0));
        for (String text : raw) {
            Line line = new Line(text);
            lines.add(line);
        }
    }

    public Text() {
        this(new ArrayList<String>());
    }

    public Text(File file) {
        this(toLines(file));
    }

    public Text(InputStream is) {
        this(toLines(is));
    }

    public static List<String> toLines(InputStream file) {
        try {
            return toLinesImp(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void add(List<String> raw) {
        for (String text : raw) {
            Line line = new Line(text);
            lines.add(line);
        }
    }

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


    public static List<String> toLines(File file) {
        try {
            return toLinesImp(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void add(InputStream from) {
        try {
            addImp(from);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

        private void addImp (InputStream from)throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(from));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(new Line(line));
            }
            bufferedReader.close();
        }


        private static List<String> toLinesImp (File file)throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getPath()));
            List<String> result = new ArrayList<>();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                result.add(line);
            }
            bufferedReader.close();
            return result;
        }

    public Line newLine() {
        return new Line();
    }

    public Line apendLine() {
        Line result = new Line();
        lines.add(result);
        return result;
    }

    public Line apendLine(String content) {
        Line result = new Line(content);
        lines.add(result);
        return result;
    }

    public class Line {

        private String content;

        public Line(String content) {
            if (content == null) {
                content = "";
            }
            this.content = content;
        }

        public Line() {
            this("");
        }

        public String get(String key) {
            Columns.Column meta = guardValueLength(key);
            return content.substring(meta.getBegin(), meta.getEnd()).trim();
        }

        public void set(String key, String value) {
            if (value == null) {
                value = "";
            }
            Columns.Column meta = guardValueLength(key);
            content = pad(content, meta.getEnd());
            value = pad(value, meta.getEnd() - meta.getBegin());
            content = content.substring(0, meta.getBegin()) + value + content.substring(meta.getEnd());
        }

        public void set(String key, Date value) {
            String text = fullDateFormat.format(value);
            set(key, text);
        }

        public void set(String key, String formatPattern, Date value) {
            SimpleDateFormat sdf = new SimpleDateFormat(formatPattern);
            String text = sdf.format(value);
            set(key, text);
        }

        private Columns.Column guardValueLength(String key) {
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

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }


    public List<String> toRawLines() {
        List<String> result = new ArrayList<>();
        for (Line line : lines) {
            result.add(line.getContent());
        }
        return result;
    }

    protected void onItemAdded(int index, Line item) {

    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Text) {
            return ((Text) obj).toRawLines().equals(toRawLines());
        } else {
            return false;
        }
    }

}
