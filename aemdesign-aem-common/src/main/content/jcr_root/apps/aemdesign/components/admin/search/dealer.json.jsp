<%@page session="false"%><%--

  Compiles a JSON-formatted list of child tags

--%><%
%><%@ page import="com.day.cq.tagging.Tag,
                   org.apache.commons.lang3.StringUtils,
                   org.apache.sling.api.resource.Resource,
                   org.apache.sling.api.resource.ValueMap,
                   org.apache.sling.commons.json.io.JSONWriter"%>
<%@ page import="javax.jcr.Node" %>
<%@ page import="javax.jcr.Session" %>
<%@ page import="com.day.cq.wcm.foundation.Search" %>
<%@ page import="javax.jcr.PropertyIterator" %>
<%@ page import="javax.jcr.Property" %>
<%@ page import="org.apache.sling.commons.json.JSONObject" %>
<%@ page import="org.apache.sling.commons.json.JSONArray" %>
<%@ page import="org.apache.sling.api.resource.ResourceResolver" %>
<%@ page import="java.util.*" %>
<%@ page import="org.w3c.dom.traversal.NodeIterator" %>
<%@ page import="org.apache.sling.commons.json.JSONException" %>
<%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0"%><%
%><sling:defineObjects/><%
%><%@include file="/libs/foundation/global.jsp"%>
<%

    response.setContentType("application/json");
    response.setCharacterEncoding("utf-8");


    final String dealerJCRPath = "/content/aemdesign/en/dealers";
    final String dealerAddressSlingPath = "aemdesign/components/data/dealerAddressDetails";
    final String dealerDetailsPath = "jcr:content/par/dealerdetails";
    final String stateTagPath = "/etc/tags/states";
    final String tyreSearch = "isTyre";


    boolean skip = false;

    Session currentSession = resource.getResourceResolver().adaptTo( Session.class);


    Node parent = currentSession.getNode(dealerJCRPath);


    JSONArray dealers = new JSONArray();


    String[] filters = slingRequest.getRequestPathInfo().getSelectors();
    boolean isPostcodeFilter = false;


 //Default listings if the latitude/longitude of the postcode is 0 or NaN
    HashMap<String, LatLong> latLongs = new HashMap<String, LatLong>();
    latLongs.put("ACT", new LatLong("-35.240765", "149.061491"));
    latLongs.put("NSW", new LatLong("-33.888969","151.1531"));
    latLongs.put("VIC", new LatLong("-37.799968","144.998631"));
    latLongs.put("QLD", new LatLong("-27.448191","153.042662"));
    latLongs.put("SA", new LatLong("-34.930134","138.604181"));
    latLongs.put("WA", new LatLong("-31.969329","115.892501"));
    latLongs.put("TAS", new LatLong("-42.878291", "147.324042"));
    latLongs.put( "NT", new LatLong("-12.451877", "130.841015"));

    setFilters(filters);

    for(String filter: filters)
    {
        try
        {
            if(Integer.parseInt(filter) > 0)
            {
                isPostcodeFilter =  true;
                setPostcodeEntry(filter);
            }
        }
        catch(Exception e)
        {

        }
    }
    String state = "";
    String longitude = "";
    String latitude = "";
    java.util.List<Dealer> dealerList = new java.util.ArrayList<Dealer>();
    java.util.List<Dealer> dealerListWithoutOwner = new java.util.ArrayList<Dealer>();
    boolean errorTrapped = false;
    if(isPostcodeFilter) {


        //Get postcode entry
        String postcodeEntry = getPostcodeEntry();

        Iterator<Node> stateTags = currentSession.getNode(stateTagPath).getNodes();
        boolean found = false;
        while (stateTags.hasNext() && !found) {
            //Find Postcode
            Iterator<Node> postcodeContainerFolder = stateTags.next().getNodes();
            while (postcodeContainerFolder.hasNext() && !found) {
                Iterator<Node> postcodeTags = postcodeContainerFolder.next().getNodes();
                while (postcodeTags.hasNext() && !found) {
                    Node postcodeTag = postcodeTags.next();
                    if(postcodeTag.getName().equals(postcodeEntry))
                    {
                        latitude = postcodeTag.getProperty("latitude").getValue().getString();
                        longitude = postcodeTag.getProperty("longitude").getValue().getString();
                        state  = postcodeTag.getParent().getParent().getName();
                        found = true;
                    }
                }
            }
        }

        if(!found)
        {
            //throw new Exception("Invalid Postcode");
            JSONObject json = new JSONObject();
            json.put("Error", "Invalid Postcode");
            dealers.put(json);
            errorTrapped = true;
        }
    }

    if (!errorTrapped) {
        state = isPostcodeFilter ? state : getStateEntry();
        Iterator<Node> children = parent.getNode(state).getNodes();
        while (children.hasNext()) {
            Node child = children.next();

            if (isPage(child)) {

                if (hasNode(child, dealerDetailsPath)) {
                    Dealer dealer = new Dealer(child.getNode(dealerDetailsPath), resourceResolver);

                    dealer.addAddressDetails(child.getNodes(), dealerAddressSlingPath);

                    if (isPostcodeFilter && checkLatitudeLongitudeForNotBlank(latitude, longitude)) {
                        //No Latitude or longitude provided


                        if(latitude.equals("NaN") || longitude.equals("NaN") || latitude.equals("0") || longitude.equals("0"))
                        {
                            if(latLongs.containsKey(state.toUpperCase()))
                            {
                                LatLong latlongValues = latLongs.get(state.toUpperCase());
                                latitude = latlongValues.getLatitude();
                                longitude = latlongValues.getLongitude();
                            }
                            else
                            {
                                latitude = "0";
                                longitude = "0";
                            }
                        }
                        dealer.calculateDistance(latitude, longitude);
                    }
                    dealerList.add(dealer);
                }
            }
        }


        if (isPostcodeFilter) {
            if (getFilterLength() < 3) {
                //Return the dealers for the postcode
                String postcode = getPostcodeEntry();

                for (Dealer d : dealerList) {
                    if (d.getOwnedPostcodes().contains(postcode)) {
                        dealers.put(d.createJSONObject(true));

                    } else {
                        dealerListWithoutOwner.add(d);
                    }
                }


                //return the other five
                if (dealers.length() < 5) {
                    List<Dealer> dealerListWithout = sortList(dealerListWithoutOwner, latitude, longitude);

                    for (int i = 0; i < dealerListWithout.size() && i < 5; i++) {
                        dealers.put(dealerListWithout.get(i).createJSONObject(false));
                    }
                }
            } else {
                //Postcode Tyre Search
                if (filters[2].equals(tyreSearch)) {
                    String postcode = getPostcodeEntry();
                    for (Dealer d : dealerList) {
                        if (d.getOwnedPostcodes().contains(postcode) && d.isTyreDealer()) {
                            dealers.put(d.createJSONObject(true));
                        } else {
                            dealerListWithoutOwner.add(d);
                        }
                    }

                    // return the 5 closest to the postcode's latitude/longitude
                    if (dealers.length() < 5) {
                        List<Dealer> dealerListWithout = sortList(dealerListWithoutOwner, latitude, longitude);

                        int count = 0;
                        for (int i = 0; i < dealerListWithout.size() && count < 5; i++) {
                            if (dealerListWithout.get(i).isTyreDealer()) {
                                dealers.put(dealerListWithout.get(i).createJSONObject(false));
                                count++;
                            }
                        }
                    }
                }
                if (dealers.length() == 0) {
                    JSONObject json = new JSONObject();
                    json.put("Error", "No Tyre Dealers found in "  + filters[1]);
                    dealers.put(json);
                }
            }
        } else {
            if (getFilterLength() < 3) {
                //Return normal state search
                Collections.sort(dealerList, new Comparator<Dealer>(){
                    public int compare(Dealer one, Dealer two) {
                        return one.getPropertyValue("suburb").compareTo(two.getPropertyValue("suburb"));
                    }
                });

                for (Dealer d : dealerList) {
                    if (d.getState().contains(state)) {
                        dealers.put(d.createJSONObject(false));
                    }
                }
            } else {
                //Return tyre dealers
                if (filters[2].equals(tyreSearch)) {

                    Collections.sort(dealerList, new Comparator<Dealer>(){
                        public int compare(Dealer one, Dealer two) {
                            return one.getPropertyValue("suburb").compareTo(two.getPropertyValue("suburb"));
                        }
                    });

                    for (Dealer d : dealerList) {
                        if (d.getState().contains(state) && d.isTyreDealer()) {
                            dealers.put(d.createJSONObject(false));
                        }
                    }
                }
            }
        }
    }



    final JSONWriter w = new JSONWriter(dealers.write(response.getWriter()));

%>
<%!
    private class Dealer implements Comparable<Dealer>
    {
        private ResourceResolver resourceResovler;
        private String state;
        private Double latitude;
        private Double longitude;
        private String test;
        private Double distance;
        private boolean isTyreDealer;
        private String ownedPostcodes = "";
        private java.util.HashMap<String, String> dealerProperties = new HashMap<String, String>();
        private java.util.List<java.util.HashMap<String, String>> addresses = new ArrayList<HashMap<String, String>>();


        private Dealer(Node dealerDetails, ResourceResolver resourceResolver) throws Exception
        {
            try
            {
                this.resourceResovler = resourceResolver;
                this.latitude = Double.parseDouble(hasProperty(dealerDetails, "latitude"));
                this.longitude = Double.parseDouble(hasProperty(dealerDetails,"longitude"));
                this.state = hasProperty(dealerDetails, "state");
                this.isTyreDealer  = hasProperty(dealerDetails, "isTyre").equals("no")?false: true;
                addToPropertiesMap(dealerDetails, dealerProperties);

            }
            catch(Exception e)
            {
                throw e;
            }
        }

        private boolean isTyreDealer()
        {
            return isTyreDealer;
        }

        private String  getOwnedPostcodes()
        {
            return ownedPostcodes;
        }

        private String getState()
        {
            return state;
        }

        private Double getDistance()
        {
            return distance;
        }

        private JSONObject createJSONObject(boolean isOwner) throws Exception
        {
            try
            {
                //Get Dealer details
                JSONObject dealerDetails = iterateHashMap(dealerProperties);
                dealerDetails.put("IsAnOwner", isOwner);
                dealerDetails.put("Distance", distance);
                //dealerDetails.put("Test", test);

                //For loop around dealerAddressDetails HasMap
                JSONArray arrayDealerAddress = new JSONArray();
                for (HashMap<String, String> address: addresses)
                {
                    arrayDealerAddress.put(iterateHashMap(address));
                }
                dealerDetails.put("dealerAddresses", arrayDealerAddress);
                return dealerDetails;
            }
            catch(Exception e)
            {
                JSONObject json = new JSONObject();
                json.put("Error", e.getMessage());
                return json;
            }
        }

        private JSONObject iterateHashMap(HashMap<String, String> propertiesMap) throws JSONException {
            JSONObject json = new JSONObject();
            try
            {
                Iterator it = propertiesMap.entrySet().iterator();
                while (it.hasNext())
                {
                    java.util.Map.Entry pairs = (java.util.Map.Entry) it.next();
                    if(pairs.getKey().equals("state"))
                    {
                        json.put(pairs.getKey().toString(), pairs.getValue().toString().toUpperCase());
                    }
                    else
                    {
                        json.put(pairs.getKey().toString(), pairs.getValue().toString());
                    }
                }

            }
            catch(Exception e)
            {
                json.put("Error", e.getMessage());
            }
            return json;
        }

        private void addAddressDetails(Iterator<Node> nodes, String path)throws Exception
        {
            try
            {
                while (nodes.hasNext())
                {
                    Node child = nodes.next();
                    //looking at the components in each dealer
                    Iterator<Node> par = child.getNodes();
                    while(par.hasNext())
                    {

                        Iterator<Node> components = par.next().getNodes();

                        while(components.hasNext())
                        {
                            Node component = components.next();

                            //checking to see if the component is an address component
                            if(component.getProperty("sling:resourceType").getValue().getString().equals(path))
                            {
                                java.util.HashMap<String, String> dealerAddressProperties =
                                        new java.util.HashMap<String, String>();

                                addToPropertiesMap(component, dealerAddressProperties);

                                addresses.add(dealerAddressProperties);
                            }
                        }
                    }

                }
            }
            catch(Exception e)
            {
                throw e;
            }
        }

        private void addToPropertiesMap(Node node, java.util.HashMap<String, String> propertiesMap) throws Exception
        {
            try
            {
                PropertyIterator propertyIterator = node.getProperties();
                while (propertyIterator.hasNext()) {
                    Property p = propertyIterator.nextProperty();
                    //dealer details properties
                    String postcode = "";
                    if (!p.getName().toString().contains("jcr") && !p.getName().toString().contains("sling"))
                    {
                        if(p.getName().equals("postcodes") || p.getName().equals("postcode")) {
                            for (int i = 0; i < p.getValues().length; i++)
                            {
                                String postcodeTagPath = (p.getValues()[i].getString()).replace(":", "/");
                                Resource resolveTag = resourceResovler.resolve("/etc/tags/" + postcodeTagPath);


                                if (i == 0) {
                                    postcode = resolveTag.getName();
                                } else {
                                    postcode = postcode + ", " +  resolveTag.getName();
                                }
                            }
                            //Set the owned postcodes
                            if(p.getName().equals("postcodes"))
                            {
                                ownedPostcodes = postcode;
                            }
                            propertiesMap.put(p.getName(), postcode);
                        }
                        else
                        {
                            propertiesMap.put(p.getName().toString(), p.getValue().getString());
                        }
                    }
                }
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        private String hasProperty(Node node, String propertyName) throws Exception {
            try
            {
                if (node.hasProperty(propertyName))
                {
                    return node.getProperty(propertyName).getValue().getString();
                }
                else
                {
                    return "";
                }
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        private String getPropertyValue(String propertyName)
        {
            return dealerProperties.get(propertyName);
        }

        private void calculateDistance(String fromLatitude, String fromLongitude)
        {
            double lat1 = Double.parseDouble(fromLatitude);
            double lng1 = Double.parseDouble(fromLongitude);




            double radius = 6371; // Radius of the earth in km
            double dLat = deg2rad(latitude-lat1);  // deg2rad below
            double dLon = deg2rad(longitude-lng1);
            double a =
                    Math.sin(dLat/2) * Math.sin(dLat/2) +
                            Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(latitude)) *
                                    Math.sin(dLon/2) * Math.sin(dLon/2)
                    ;
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            distance = radius * c; // Distance in km



            /*double theta = lng1 - longitude;
            double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(latitude)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(latitude)) * Math.cos(deg2rad(theta));
            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515;

            distance = dist * 1.609344;*/
        }

        public int compareTo(Dealer d)
        {
            //TODO: confirm this is the correct means to determine order.
            return this.distance.compareTo(d.getDistance());
        }
    }
%>
<%!
    public class LatLong
    {
        private String lat;
        private String lon;

        private LatLong(String lat, String lon)
        {
            this.lat = lat;
            this.lon = lon;
        }

        public String getLatitude()
        {
            return lat;
        }

        public String getLongitude()
        {
            return lon;
        }
    }
%>


<%!

    //HELPER METHODS

    private String postcodeEntry;
    private String[] filters;

    private void setFilters(String[] filtering) {
        filters = filtering;
    }

    private void setPostcodeEntry(String postcodeFilter){
        postcodeEntry = postcodeFilter;
    }

    private String getPostcodeEntry()
    {
        return postcodeEntry;
    }

    private String getStateEntry()
    {
        return filters[1];
    }

    private int getFilterLength()
    {
        return filters.length;
    }

    private List<Dealer>  sortList(List<Dealer> dealerList, String latitude, String longitude)
    {
        if(checkLatitudeLongitudeForNotBlank(latitude, longitude))
        {
            for(Dealer d: dealerList)
            {
                d.calculateDistance(latitude, longitude);
            }
            Collections.sort(dealerList);
        }
        return dealerList;
    }


    private boolean checkLatitudeLongitudeForNotBlank(String latitude, String longitude)
    {
        if((!latitude.equals("") && !longitude.equals("")))
        {
            return true;
        }
        return false;
    }

    private boolean  isPage (Node child) throws Exception
    {
        try
        {
            return child.getProperty("jcr:primaryType").getValue().getString().equals("cq:Page");
        }
        catch(Exception e)
        {
            throw e;
        }
    }

    private boolean hasNode(Node node, String path)
    {
        try
        {
            return node.hasNode(path);
        }
        catch(Exception e)
        {
            return false;
        }

    }

    private double deg2rad(double deg)
    {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad)
    {
        return (rad * 180 / Math.PI);
    }


%>