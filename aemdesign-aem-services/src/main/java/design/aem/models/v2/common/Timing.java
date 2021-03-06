package design.aem.models.v2.common;

import com.google.gson.Gson;
import design.aem.components.ComponentProperties;
import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;
import org.apache.sling.api.request.RequestProgressTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static design.aem.utils.components.ComponentsUtil.*;

public class Timing extends BaseComponent {
    protected static final Logger LOGGER = LoggerFactory.getLogger(Timing.class);

    private static final String TIMER_END = "TIMER_END";

    private static String basename(String path) {
        String result = path;
        int pos = path.lastIndexOf('/');
        if (pos > 0) {
            result = result.substring(pos + 1);
        }
        return result;
    }

    private static boolean accept(String line) {
        boolean result = line.contains(TIMER_END);
        result &= !line.contains(",resolveServlet(");
        result &= !line.contains("ResourceResolution");
        result &= !line.contains("ServletResolution");
        return result;
    }

    public static byte[] compress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return new byte[]{};
        }

        final String charsetName = "UTF-8";
        ByteArrayOutputStream obj = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(obj);
        gzip.write(str.getBytes(charsetName));
        gzip.close();

        return obj.toByteArray();
    }

    public static String decompress(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
        BufferedReader bf = new BufferedReader(new InputStreamReader(gis, StandardCharsets.UTF_8));

        String line;
        StringBuilder output = new StringBuilder();
        while ((line = bf.readLine()) != null) {
            output.append(line);
        }

        return output.toString();
    }

    @SuppressWarnings("squid:S1604")
    protected void ready() {
        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE);

        // Convert RequestProgressTracker TIMER_END messages to timings and operation names
        RequestProgressTracker t = getRequest().getRequestProgressTracker();
        ArrayList<ChartBar> chartData = new ArrayList<>();
        int maxTime = 0;
        Iterator<String> messages = t.getMessages();
        if (messages != null) {
            while (messages.hasNext()) {
                String line = messages.next();
                if (accept(line)) {
                    ChartBar b = new ChartBar(line);
                    chartData.add(b);
                    maxTime = b.end;
                }
            }
        }
        long millis = maxTime;

        long hours = TimeUnit.MICROSECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMicros(hours);
        long minutes = TimeUnit.MICROSECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMicros(minutes);
        long seconds = TimeUnit.MICROSECONDS.toSeconds(millis);
        millis -= TimeUnit.SECONDS.toMicros(seconds);


        String title = basename(getRequest().getPathInfo()) + " (" + String.format("%02d:%02d:%02d.%d", hours, minutes, seconds, millis) + ")";

        // Sort data according to numeric start time
        Collections.sort(chartData, new Comparator<ChartBar>() {
            public int compare(ChartBar a, ChartBar b) {
                if (a.start > b.start) {
                    return 1;
                } else if (a.start < b.start) {
                    return -1;
                }
                return 0;
            }
        });


        String url = "http://aem.design/component-timing/#data=";
        byte[] encoded = new byte[0];
        try {

            Map<String, Object> dataObject = new HashMap<>();

            dataObject.put("title", title);

            ArrayList<Object> data = new ArrayList<>();

            ArrayList<Object> dataInfo = new ArrayList<>();

            dataInfo.add("name");
            Map<String, String> roleTooltip = new HashMap<>();
            roleTooltip.put("role", "tooltip");
            dataInfo.add(roleTooltip);
            dataInfo.add("start");
            dataInfo.add("elapsed");

            data.add(dataInfo);


            for (ChartBar d : chartData) {
                ArrayList<Object> bar = new ArrayList<>();
                bar.add(d.name);
                bar.add(d.fullname);
                bar.add(d.start);
                bar.add(d.elapsed);

                data.add(bar);
            }

            dataObject.put("data", data);

            Gson gson = new Gson();

            String jsonString = gson.toJson(dataObject);

            byte[] compressed = compress(jsonString);
            encoded = Base64.getEncoder().encode(compressed);

            componentProperties.put("url", url);
            componentProperties.put("encodedData", new String(encoded));
            componentProperties.put("jsonString", jsonString);

        } catch (Exception ex) {
            LOGGER.error("ready {}", ex);
        }
    }

    @Override
    protected void setFields() {
        setComponentFields(new Object[][]{
            {FIELD_VARIANT, DEFAULT_VARIANT},
        });
    }

    static class ChartBar {
        private static final String ELLIPSIS = "...";
        private static final int MAX_LABEL_LENGTH = 25;
        String input;
        String name;
        String fullname;
        int start;
        int end;
        int elapsed;

        ChartBar(String line) {
            try {
                input = line.trim();
                end = Integer.valueOf(scan(' '));
                scan('{');
                elapsed = Integer.valueOf(scan(','));
                start = end - elapsed;
                fullname = cutBeforeLast(scan('}'), '#');
                name = shortForm(fullname);
            } catch (NumberFormatException ex) {
                name = fullname = ex.toString();
            }
        }

        private static String cutBeforeLast(String str, char separator) {
            int pos = str.lastIndexOf(separator);
            if (pos > 0) {
                str = str.substring(0, pos);
            }
            return str;
        }

        private static String shortForm(String str) {
            String result = basename(str);
            if (result.length() > MAX_LABEL_LENGTH) {
                result = result.substring(0, MAX_LABEL_LENGTH - ELLIPSIS.length()) + ELLIPSIS;
            }
            return result;
        }

        /**
         * Remove chars up to separator in this.input, and return result.
         *
         * @param separator separator to stop at
         * @return string up to separator
         */
        private String scan(char separator) {
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                if (c == separator) {
                    break;
                }
                sb.append(c);
            }
            input = input.substring(sb.length() + 1);
            return sb.toString().trim();
        }
    }

    public interface Getter {
        String get(ChartBar t);
    }
}
