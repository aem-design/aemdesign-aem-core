<%@include file="/libs/granite/ui/global.jsp"%><%
%><%@page session="false"
          import="com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.ds.DataSource,
                  com.adobe.granite.ui.components.ds.SimpleDataSource,
                  com.adobe.granite.ui.components.ds.ValueMapResource,
                  com.day.cq.i18n.I18n,
                  com.day.cq.wcm.foundation.forms.FormsManager,
                  org.apache.commons.collections.Transformer,
                  org.apache.commons.collections.iterators.IteratorChain,
                  org.apache.commons.collections.iterators.TransformIterator,
                  org.apache.sling.api.resource.ResourceMetadata,
                  org.apache.sling.api.resource.ResourceResolver,
                  org.apache.sling.api.resource.ValueMap,
                  org.apache.sling.api.wrappers.ValueMapDecorator,
                  java.util.Arrays,
                  java.util.HashMap,
                  java.util.List"%>
<%@ page import="java.util.Locale" %>
<%
/**
    A datasource returning tag key-value pairs that is suitable to be used for select or autocomplete (or compatible) components.
    replacement for com.day.cq.wcm.foundation.forms.impl.FormsListServlet

    @datasource
    @name Forms
    @location /libs/cq/gui/components/common/datasources/forms

    @variant {String[]} [namespaces] variant name

 */
Config dsCfg = new Config(resource.getChild("datasource"));

final String variant = dsCfg.get("variant","");

FormsManager formsManager = resourceResolver.adaptTo(FormsManager.class);

IteratorChain dataIterator = new IteratorChain();

if (formsManager != null) {

    if (variant == "constraints") {
        dataIterator.addIterator(formsManager.getConstraints());
    } else if (variant == "actions") {
        dataIterator.addIterator(formsManager.getActions());
    }

}


final ResourceResolver resolver = resourceResolver;
final I18n i18n = new I18n(request);

@SuppressWarnings("unchecked")
DataSource ds = new SimpleDataSource(new TransformIterator(dataIterator, new Transformer() {
    public Object transform(Object o) {
        ValueMap vm = new ValueMapDecorator(new HashMap<String, Object>());

        FormsManager.ComponentDescription desc = (FormsManager.ComponentDescription) o;

        vm.put("value", desc.getResourceType());
        vm.put("text", i18n.get(desc.getTitle()));

        return new ValueMapResource(resolver, new ResourceMetadata(), "nt:unstructured", vm);
    }
}));

request.setAttribute(DataSource.class.getName(), ds);
%>
