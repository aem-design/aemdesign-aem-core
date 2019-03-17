package design.aem.models.v2.common;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.i18n.I18n;
import com.day.cq.wcm.api.Page;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.request.RequestProgressTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static design.aem.utils.components.CommonUtil.PN_REDIRECT_TARGET;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.DEFAULT_EXTENTION;
import static design.aem.utils.components.I18nUtil.getDefaultLabelIfEmpty;

public class Timing extends WCMUsePojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(Timing.class);

    private ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    private static final String TIMER_END = "TIMER_END";



    @Override
    public void activate() throws Exception {


        // }
        Object[][] componentFields = {
                {FIELD_VARIANT, DEFAULT_VARIANT},
        };

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE);


        // Convert RequestProgressTracker TIMER_END messages to timings and operation names
        RequestProgressTracker t = getRequest().getRequestProgressTracker();
        ArrayList<ChartBar> chartData = new ArrayList<ChartBar>();
        int maxTime = 0;
        for(Iterator<String> it = t.getMessages(); it.hasNext(); ) {
            String line = it.next();
            if(accept(line)) {
                ChartBar b = new ChartBar(line);
                chartData.add(b);
                maxTime = b.end;
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
                if(a.start > b.start) {
                    return 1;
                } else if(a.start < b.start) {
                    return -1;
                }
                return 0;
            }
        });



        /* uncomment the following to get more timing details in the page
        out.println("\nRaw RequestProgressTracker data:");
        StringBuilder mb = new StringBuilder();
        Iterator<String> it = t.getMessages();
        while(it.hasNext()) {
            mb.append(it.next());
        }
        out.print(mb.toString());
        out.println("\nChartData dump:");
        for(ChartBar d : chartData) {
            out.print(d.start);
            out.print(' ');
            out.print(d.fullname);
            out.print(" (");
            out.print(d.elapsed);
            out.println("ms)");
        }
        */


        String url = "http://aem.design/component-timing/#data=";
        byte[] encoded = new byte[0];
        try {

            Map<String,Object> dataObject = new HashMap<>();

            //data.put(data)
            dataObject.put("title",title);

            ArrayList<Object> data= new ArrayList<Object>();

            ArrayList<Object> dataInfo = new ArrayList<Object>();

            dataInfo.add("name");
            Map<String,String> roleTooltip = new HashMap<>();
            roleTooltip.put("role","tooltip");
            dataInfo.add(roleTooltip);
            dataInfo.add("start");
            dataInfo.add("elapsed");

            data.add(dataInfo);


            for(ChartBar d : chartData) {
                ArrayList<Object> bar = new ArrayList<Object>();
                bar.add(d.name);
                bar.add(d.fullname);
                bar.add(d.start);
                bar.add(d.elapsed);

//                dataInfo.add(bar);
                data.add(bar);
            }

            dataObject.put("data",data);

            Gson gson = new Gson();

            String jsonString = gson.toJson(dataObject);


//            w.object();
//
//                w.key("title").value(title);
//                w.key("data");
//                w.array();
//
//                    w.array();
//                    w.value("name");
//                    w.object();
//                        w.key("role").value("tooltip");
//                    w.endObject();
//                    w.value("start");
//                    w.value("elapsed");
//                    w.endArray();
//
//                    for(ChartBar d : chartData) {
//                        w.array();
//                        w.value(d.name);
//                        w.value(d.fullname);
//                        w.value(d.start);
//                        w.value(d.elapsed);
//                        w.endArray();
//                    }
//
//                w.endArray();
//
//            w.endObject();

            byte[] compressed = compress(jsonString);
            encoded = Base64.getEncoder().encode(compressed);

            componentProperties.put("url",url);
            componentProperties.put("encodedData",new String(encoded));
            componentProperties.put("jsonString",jsonString);

        } catch (Exception ex ) {
            LOGGER.error(ex.toString());
        }


    }


    static class ChartBar {
        String input;
        String name;
        String fullname;
        int start;
        int end;
        int elapsed;
        private static final String ELLIPSIS = "...";
        private static final int MAX_LABEL_LENGTH = 25;

        ChartBar(String line) {
            try {
                input = line.trim();
                end = Integer.valueOf(scan(' '));
                scan('{');
                elapsed = Integer.valueOf(scan(','));
                start = end - elapsed;
                fullname = cutBeforeLast(scan('}'), '#');
                name = shortForm(fullname);
            } catch(NumberFormatException ignored) {
                name = fullname = ignored.toString();
            }
        }

        /** Remove chars up to separator in this.input, and return result */
        private String scan(char separator) {
            final StringBuilder sb = new StringBuilder();
            for(int i=0; i < input.length(); i++) {
                char c = input.charAt(i);
                if(c == separator) {
                    break;
                }
                sb.append(c);
            }
            input = input.substring(sb.length() + 1);
            return sb.toString().trim();
        }

        private static String cutBeforeLast(String str, char separator) {
            int pos = str.lastIndexOf(separator);
            if(pos > 0) {
                str = str.substring(0, pos);
            }
            return str;
        }

        private static String shortForm(String str) {
            String result = basename(str);
            if(result.length() > MAX_LABEL_LENGTH) {
                result = result.substring(0, MAX_LABEL_LENGTH - ELLIPSIS.length()) + ELLIPSIS;
            }
            return result;
        }
    }

    static abstract class Getter {
        abstract String get(ChartBar t);
    }

    private static String basename(String path) {
        String result = path;
        int pos = path.lastIndexOf('/');
        if(pos > 0) {
            result = result.substring(pos + 1);
        }
        return result;
    }

    private static String stringList(List<ChartBar> data, String separator, Getter g) {
        StringBuilder result = new StringBuilder();
        for(ChartBar t : data) {
            if(result.length() > 0) {
                result.append(separator);
            }
            result.append(URLEncoder.encode(g.get(t)));
        }
        return result.toString();
    }

    private static boolean accept(String line) {
        boolean result = line.contains(TIMER_END);
        result &= !line.contains(",resolveServlet(");
        result &= !line.contains("ResourceResolution");
        result &= !line.contains("ServletResolution");
        return result;
    }

    public static byte[] compress(String str) throws Exception {
        if (str == null || str.length() == 0) {
            return null;
        }

        ByteArrayOutputStream obj=new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(obj);
        gzip.write(str.getBytes("UTF-8"));
        gzip.close();
        String outStr = obj.toString("UTF-8");

        return obj.toByteArray();
    }

    public static String decompress(byte[] bytes) throws Exception {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
        BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
        String outStr = "";
        String line;
        while ((line=bf.readLine())!=null) {
            outStr += line;
        }

        return outStr;
    }
}