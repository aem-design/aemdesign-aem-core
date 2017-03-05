<%@ page import="org.apache.sling.api.resource.ValueMap" %>
<%@ page import="static org.apache.commons.lang3.StringUtils.isEmpty" %>
<%@ page import="org.apache.sling.commons.json.JSONException" %>
<%@ page import="org.apache.sling.commons.json.io.JSONWriter" %>
<%@ page import="java.io.StringWriter" %>
<%@ page session="false" %>
<%!
    /**
     * check if string is equals to "on"
     * @param source
     * @return
     */
    public final static boolean isOn(String source) {
        return "on".equals(source);
    }

    /**
     * check if string is equals to "yes"
     * @param source
     * @return
     */
    public final static boolean isYes(String source) {
        return "yes".equals(source);
    }

    /**
     * check if string is NOT equals to "on"
     * @param source
     * @return
     */
    public final static boolean isNotOn(String source) {
        return !"on".equals(source);
    }

    /**
     * check if string is NOT equals to "yes"
     * @param source
     * @return
     */
    public final static boolean isNotYes(String source) {
        return !"yes".equals(source);
    }

    /**
     * get value from value map
     * @param source
     * @param Name
     * @return
     */
    public final static String getValue(ValueMap source, String Name) {
        if (source == null || isEmpty(Name)) { //quick fail
            return null;
        }
        if (source.containsKey(Name)) {
            return source.get(Name, "");
        }

        return null;
    }

    /**
     * check if object is null
     * @param source
     * @return
     */
    public final static Boolean isNull(Object source) {
        return source == null;
    }

    /**
     * check if object is NOT null
     * @param source
     * @return
     */
    public final static Boolean isNotNull(Object source) {
        return source != null;
    }

    /**
     * converts an object to json string
     * @param object
     * @return
     */
    public String toJson(Object[][] object) {
        StringWriter sw = new StringWriter();
        JSONWriter w = new JSONWriter(sw);
        w.setTidy(true);
        try {
            w.array();
            for (int i = 0; i < object.length; i++) {
                w.object();

                if (object[i].length > 2) {
                    Object value = object[i][1];
                    String valueString;

                    if (value.getClass().isArray()) {
                        valueString = StringUtils.join((Object[]) value, ",");
                    } else {
                        valueString = value.toString();
                    }

                    w.key(object[i][0].toString()).value(valueString);
                } else if (object[i].length == 1 ) {
                    if (object[i].getClass().isArray()) {
                        w.array();

                        for (int y = 0; y < object[i].length; y++) {
                            Object value = object[i][y];
                            String valueString;

                            if (value.getClass().isArray()) {
                                valueString = StringUtils.join((Object[]) value, ",");
                            } else {
                                valueString = value.toString();
                            }

                            w.value(valueString);
                        }
                        w.endArray();
                    } else if (object[i].getClass().isEnum()) {
                        w.array();
                        for (Object s : object[i]) {
                            w.value(s);
                        }
                        w.endArray();
                    }
                }
                w.endObject();
            }
            w.endArray();
        } catch (JSONException jex) {

        }
        return sw.toString();
    }

%>