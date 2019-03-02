<%@include file="/apps/aemdesign/global/global.jsp"%><%
%><%@page session="false"
          import="com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.ds.DataSource,
                  com.adobe.granite.ui.components.ds.SimpleDataSource,
                  com.adobe.granite.ui.components.ds.ValueMapResource,
                  org.apache.commons.lang3.StringUtils,
                  org.apache.sling.api.resource.Resource,
                  org.apache.sling.api.resource.ResourceMetadata,
                  org.apache.sling.api.wrappers.ValueMapDecorator"%>
<%@ page import="java.util.*" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    /**
     A datasource returning tag key-value pairs that is suitable to be used for select or autocomplete (or compatible) components.

     @datasource
     @name Tags
     @location /libs/cq/gui/components/common/datasources/tags

     @path {String[]} [namespaces] The namespaces of the tags to return. Only the tag which namespace equals to one of this values is returned. If this property is not specified, all tags are returned.
     @variant {String[]} [namespaces] variant name with following values:
            tagname returns tag name, no reference to Tags
            pathvalue returns tag path, output full path to tag, central to update, will need content migration when path to Tag storage change
            tagidvalue return tag tagid, safest to use with Tag Manager and central to update
            valuelist return tag value, NOTE only use this for drop-downs that value values that don't use Expression Language
     */
    Config dsCfg = new Config(_resource.getChild("datasource"));
    String componentPath = dsCfg.get("component", "");

    Resource componentResource = _resourceResolver.resolve(componentPath);

    String badgePattern = "badge.(.*).html";
    Pattern pattern = Pattern.compile(badgePattern);

    List<Resource> badgeList = new ArrayList<Resource>();

    Iterable<Resource> componentResourceChildren = componentResource.getChildren();
    for (Resource nextChild : componentResourceChildren) {
        String childName = nextChild.getName();

        Matcher matcher = pattern.matcher(childName);
        boolean matches = matcher.matches();

        if (matches) {

            String itemName = matcher.group(1);
            String itemValue = itemName.replace("default", "");
            String itemTitle = StringUtils.capitalize(StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(itemName), ", "));
            itemTitle = replaceLast(itemTitle,", "," and ");

            ValueMapDecorator badgeItem = new ValueMapDecorator(new HashMap<String, Object>());

            badgeItem.put("text",itemTitle);
            badgeItem.put("value",itemValue);

            badgeList.add(new ValueMapResource(_resourceResolver, new ResourceMetadata(), "nt:unstructured", badgeItem));

        }
    }

    // sort by text
    badgeList.sort(new Comparator<Resource>() {
        @Override
        public int compare(Resource resource1, Resource resource2) {
            try {
                return resource1.getValueMap().get("text","").compareTo(resource2.getValueMap().get("text",""));
            } catch (Exception ex) {
                return -1;
            }
        }
    });

    DataSource ds = new SimpleDataSource(badgeList.iterator());
    request.setAttribute(DataSource.class.getName(), ds);
%><%!

    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)"+regex+"(?!.*?"+regex+")", replacement);
    }
%>
