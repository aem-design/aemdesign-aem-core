<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="org.apache.commons.lang3.time.DateFormatUtils" %>
<%@ page import="org.apache.commons.lang3.time.DateUtils" %>
<%@ page import="javax.jcr.Node" %>
<%@ page import="javax.jcr.RepositoryException" %>
<%@ page import="java.text.ParseException" %>
<%@ page import="java.text.ParsePosition" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="static org.apache.commons.lang3.StringUtils.join" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Date" %>
<%@ include file="/apps/aemdesign/global/logging.jsp" %>
<%!

    final static SimpleDateFormat[] SLING_FORMATS = new SimpleDateFormat[]{
            new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z"),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"),
            new SimpleDateFormat("yyyy-MM-dd"),
            new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"),
            new SimpleDateFormat("dd.MM.yyyy")};

    /**
     * Parse a date/time using an array of DateFormat objects
     *
     * @param sDate    the date string
     * @return the parsed date, or null if not parseable
     */
    private static boolean isDate(String sDate) {
        return isDate(sDate,SLING_FORMATS);
    }
    /**
     * Parse a date/time using an array of DateFormat objects
     *
     * @param sDate    the date string
     * @param formats  the array of DateFormat objects to try, one by one
     * @return the parsed date, or null if not parseable
     */
    private static boolean isDate(String sDate, SimpleDateFormat[] formats) {
        for (int i = 0; i < formats.length; i++) {
            try {
                formats[i].setLenient(false);
                Date d = formats[i].parse(sDate, new ParsePosition(0));
                if (d != null) return true;
            }
            catch (NumberFormatException ex) {}
            catch (Exception ex) {}
        }
        return false;
    }
    private static SimpleDateFormat getDateFormat(String sDate, SimpleDateFormat[] formats) {
        for (int i = 0; i < formats.length; i++) {
            try {
                formats[i].setLenient(false);
                Date d = formats[i].parse(sDate, new ParsePosition(0));
                if (d != null) return formats[i];
            }
            catch (NumberFormatException ex) {}
            catch (Exception ex) {}
        }
        return null;
    }

    /**
     * Translate a date from yyyy-mm-dd into dd MMM yyyy. If it cannot parse
     * it, it will simply return the original value. If there is timezone information
     * available, it will strip it out.
     *
     * @param date is the date to parse
     * @return is the formatted date result.
     */
    public String getFormattedDate(String date) {

        // null? return null.
        if (date == null) {
            return null;
        }

        date = date.trim();
        String orgDate = date;

        // contains spaces? is probably already formatted.
        if (date.indexOf(' ') != -1) {
            return date;
        }

        // has time zone information? no one cares.
        if (date.indexOf('T') != -1) {
            date = date.substring(0, date.indexOf('T'));
        }

        SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outFormat = new SimpleDateFormat("dd MMM yyyy");
        try {
            Date dateObj = inFormat.parse(date);
            return outFormat.format(dateObj);
        }
        catch (ParseException pEx) {
            // couldn't parse date, just return our input
            return orgDate;
        }

    }

	/**
		
	*/
	public String getFormattedDate(String date, String format) {
	
	    // null? return null.
	    if (date == null) {
	        return null;
	    }
	
	    date = date.trim();
	    String orgDate = date;
	
	    // contains spaces? is probably already formatted.
	    if (date.indexOf(' ') != -1) {
	        return date;
	    }
	
	    SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
	    SimpleDateFormat outFormat = new SimpleDateFormat(format);
	    try {
	        Date dateObj = inFormat.parse(date);
	        return outFormat.format(dateObj);
	    }
	    catch (ParseException pEx) {
	        // couldn't parse date, just return our input
	        return orgDate;
	    }
	
	}

    /**

     */
    public String getFormattedDateWithDefauls(String date, SimpleDateFormat[] inFormats, SimpleDateFormat outFormat, String defaultDate) {
        SimpleDateFormat dateFormat = getDateFormat(date, inFormats);
        if (dateFormat!=null) {
            try {
                Date dateObj = dateFormat.parse(date);
                return outFormat.format(dateObj);
            }
            catch (Exception Ex) {
                // couldn't parse date, just return default
            }
        }
        return defaultDate;
    }

    /**
     * This function provides safe access for date properties, returning null if repository errors occur.
     * @param node The event details node to get the information from.
     * @param key The field name for the date to be retrieved.
     * @return The date stored under the provided key.
     */
    protected Calendar getDateProperty(Node node, String key) {
        try {
            return node.hasProperty(key) ? node.getProperty(key).getDate() : null;
        } catch (RepositoryException e) {
            return null;
        }
    }

    /**
     * Proxy method for making date formatting more agreeable to debugging. If an IllegalArgumentException occurs
     * during the format, it's wrapped in a RuntimeException with the full format pattern.
     * @param date The date to format.
     * @param format The pattern to format the date with.
     * @return The date in string form, formatted with the provided pattern.
     * @throws RuntimeException
     */
    protected String formatDate(Calendar date, String format) throws RuntimeException {
        getLogger().debug("Formatting date '{}' with format '{}'.", date, format);

        if (date == null || StringUtils.isBlank(format)) {
            getLogger().warn("Unable to format date '{}' with format '{}'.", date, format);
            return null; // Not ideal, but may prevent errors from popping up.
        }

        try {
            //SimpleDateFormat dateFormat = new SimpleDateFormat(format);

            String dateString = DateFormatUtils.format(date, format);

            //String dateString = dateFormat.format(date);
            getLogger().debug("Successfully formatted date from '{}' into '{}'.", date, dateString);
            return dateString;
        } catch (Exception e) {
            throw new RuntimeException("Failed to format date using pattern: " + format, e);
        }
    }

    /**
     * Parses a date string into a Calendar using the provided formats, and throws a RuntimeException if something goes
     * wrong, with relevant and useful information to assist debugging.
     * @param dateString The string-formatted date to parse.
     * @param formats The formats to attempt while parsing.
     * @return The parsed date as a Calendar instance.
     */
    protected Calendar parseDate(String dateString, String... formats) {
        getLogger().debug("Parsing stored date '{}' with formats {}", dateString, formats);

        try {
            Date date = DateUtils.parseDate(dateString, formats);
            Calendar calendar = DateUtils.toCalendar(date);
            getLogger().debug("Successfully parsed '{}' into '{}'", dateString, calendar);
            return calendar;
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unable to parse stored date '%s' with formats [%s].",
                    dateString,
                    StringUtils.join(formats, ',')
            ), e);
        }
    }

    /**
  	* Method returns a short formated string for the given date object, it returns the date in "dd MMM YYYY" format.
  	*
  	*/
  	public String getShortFormattedDate(Date thedate){
  	
  	        // null? return null.
  	        if (thedate == null) {
  	            return null;
  	        }
  	       
  	       SimpleDateFormat outFormat = new SimpleDateFormat("dd MMM yyyy");
  	       return outFormat.format(thedate); 
  	}
  	
  	/**
  	* Method returns a full formated string for the given date object, it returns the date in "dd MMM YYYY" format.
  	*
  	*/
	public String getFullFormattedDate(Date thedate){
	
	        // null? return null.
	        if (thedate == null) {
	            return null;
	        }
	        
	        SimpleDateFormat outFormat = new SimpleDateFormat("d MMM yyyy, h:mmaaa");
	        return outFormat.format(thedate); 
	 }

%>