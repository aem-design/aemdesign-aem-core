<%@page session="false"%>
<%@include file="/apps/aemdesign/global/global.jsp" %>
<%@ page import="org.apache.sling.api.request.RequestProgressTracker" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.concurrent.TimeUnit" %>
<%@ page import="java.util.zip.GZIPInputStream" %>
<%@ page import="java.util.zip.GZIPOutputStream" %>
<%@ page import="org.apache.sling.commons.json.io.JSONStringer" %>

<%!
private static final String TIMER_END = "TIMER_END";

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
    System.out.println("String length : " + str.length());
    ByteArrayOutputStream obj=new ByteArrayOutputStream();
    GZIPOutputStream gzip = new GZIPOutputStream(obj);
    gzip.write(str.getBytes("UTF-8"));
    gzip.close();
    String outStr = obj.toString("UTF-8");
    System.out.println("Output String length : " + outStr.length());
    return obj.toByteArray();
}

public static String decompress(byte[] bytes) throws Exception {
    if (bytes == null || bytes.length == 0) {
        return null;
    }
    System.out.println("Input String length : " + bytes.length);
    GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
    BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
    String outStr = "";
    String line;
    while ((line=bf.readLine())!=null) {
        outStr += line;
    }
    System.out.println("Output String lenght : " + outStr.length());
    return outStr;
}

%>

<%
// Convert RequestProgressTracker TIMER_END messages to timings and operation names
RequestProgressTracker t = slingRequest.getRequestProgressTracker();
ArrayList<ChartBar> chartData = new ArrayList<ChartBar>();
int maxTime = 0;
for(Iterator<String> it = t.getMessages() ; it.hasNext(); ) {
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


String title = basename(request.getPathInfo()) + " (" + String.format("%02d:%02d:%02d.%d", hours, minutes, seconds, millis) + ")";

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

%>

<%
// Dump data, in comments
out.println("<!--");

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

out.println("More detailed timing info is available by uncommenting some code in the aemdesign/components/common/timing.jsp component");
out.println("Timing chart URL:");

    String url = "http://aem.design/component-timing/#data=";
    byte[] encoded = new byte[0];
    try {

        final JSONStringer w = new JSONStringer();

        w.object();

            w.key("title").value(title);
            w.key("data");
            w.array();

                w.array();
                    w.value("name");
                    w.object();
                    w.key("role").value("tooltip");
                    w.endObject();
                    w.value("start");
                    w.value("elapsed");
                w.endArray();

                for(ChartBar d : chartData) {
                    w.array();
                        w.value(d.name);
                        w.value(d.fullname);
                        w.value(d.start);
                        w.value(d.elapsed);
                    w.endArray();
                }

            w.endArray();

        w.endObject();

//        out.println(  w.toString() );
//        out.println("len:" + w.toString().length());
//        out.println("after compress:");
        byte[] compressed = compress(w.toString());
        encoded = Base64.getEncoder().encode(compressed);
//        out.println("compress len:"+compressed.length);
//        out.println("base64 len:"+encoded.length);
//        out.println("base64:");
//        out.println(new String(encoded));
//        out.println("after decompress:");
//        String decomp = decompress(compressed);
//        out.println(decomp);
//        out.println(  url + new String(encoded) );

    } catch (Exception ex ) {
        out.println(ex.toString());
    }

    out.println("-->");
%>
<a class="timing hidden" href="<%=url + new String(encoded)%>">Component Timing</a>
