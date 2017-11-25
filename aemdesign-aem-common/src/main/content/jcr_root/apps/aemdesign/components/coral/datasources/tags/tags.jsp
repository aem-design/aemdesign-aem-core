<%@include file="/libs/granite/ui/global.jsp"%><%
%><%@page session="false"
          import="java.util.Arrays,
                  java.util.List,
                  java.util.Locale,
                  java.util.HashMap,
                  org.apache.commons.collections.Transformer,
                  org.apache.commons.collections.iterators.IteratorChain,
                  org.apache.commons.collections.iterators.TransformIterator,
                  org.apache.sling.api.resource.ResourceMetadata,
                  org.apache.sling.api.resource.ResourceResolver,
                  org.apache.sling.api.wrappers.ValueMapDecorator,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.ds.DataSource,
                  com.adobe.granite.ui.components.ds.SimpleDataSource,
                  com.adobe.granite.ui.components.ds.ValueMapResource,
                  com.day.cq.tagging.Tag,
                  com.day.cq.tagging.TagManager"%>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%
/**
    A datasource returning tag key-value pairs that is suitable to be used for select or autocomplete (or compatible) components.

    @datasource
    @name Tags
    @location /libs/cq/gui/components/common/datasources/tags

    @property {String[]} [namespaces] The namespaces of the tags to return. Only the tag which namespace equals to one of this values is returned. If this property is not specified, all tags are returned.

 */
Config dsCfg = new Config(resource.getChild("datasource"));
List<String> namespaces = Arrays.asList(dsCfg.get("namespaces", new String[0]));

String path = dsCfg.get("path","");
final String variant = dsCfg.get("variant","");

TagManager tm = resourceResolver.adaptTo(TagManager.class);

IteratorChain tags = new IteratorChain();

if (tm != null) {

    if (StringUtils.isNotEmpty(path)) {
        Tag tagPath = tm.resolve(path);
        if (tagPath != null) {
            tags.addIterator(tagPath.listAllSubTags());
        }
    } else {

        for (Tag ns : tm.getNamespaces()) {
            if (namespaces.size() == 0 || namespaces.contains(ns.getName())) {
                tags.addIterator(ns.listAllSubTags());
            }
        }
    }
}

final Locale locale = request.getLocale();
final ResourceResolver resolver = resourceResolver;

@SuppressWarnings("unchecked")
DataSource ds = new SimpleDataSource(new TransformIterator(tags, new Transformer() {
    public Object transform(Object o) {
        Tag tag = (Tag) o;

        String tagId = tag.getTagID();
        Resource childR = tag.adaptTo(Resource.class);
        ValueMap childVM = childR.adaptTo(ValueMap.class);

        ValueMap vm = new ValueMapDecorator(new HashMap<String, Object>());
        String value = tagId;
        String text = tag.getTitlePath(locale);

        if (childVM!=null) {

            if ("pathvalue".equals(variant)) {

                value = tag.getPath();
                text = tag.getTitle(locale);
            } else if ("valuelist".equals(variant)) {

                value = childVM.get("value", "");
                text = tag.getTitle(locale);
            }
        }


        vm.put("value", value);
        vm.put("text", text);

        return new ValueMapResource(resolver, new ResourceMetadata(), "nt:unstructured", vm);
    }
}));

request.setAttribute(DataSource.class.getName(), ds);
%>
