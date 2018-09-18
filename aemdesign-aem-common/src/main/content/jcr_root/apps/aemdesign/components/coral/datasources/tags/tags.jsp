<%@include file="/apps/aemdesign/global/global.jsp"%><%
%><%@page session="false"
          import="com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.ds.DataSource,
                  com.adobe.granite.ui.components.ds.SimpleDataSource,
                  com.adobe.granite.ui.components.ds.ValueMapResource,
                  com.day.cq.tagging.Tag,
                  com.day.cq.tagging.TagManager,
                  org.apache.commons.collections.Transformer,
                  org.apache.commons.collections.iterators.IteratorChain,
                  org.apache.commons.collections.iterators.TransformIterator,
                  org.apache.commons.lang3.StringUtils,
                  org.apache.sling.tenant.Tenant,
                  com.adobe.granite.ui.components.ExpressionHelper,
                  com.adobe.granite.ui.components.ExpressionCustomizer,
                  org.apache.sling.api.resource.Resource,
                  org.apache.sling.api.resource.ResourceMetadata,
                  org.apache.sling.api.resource.ResourceResolver,
                  org.apache.sling.api.wrappers.ValueMapDecorator,
                  java.util.Arrays,
                  java.util.HashMap"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%
    /**
     A datasource returning tag key-value pairs that is suitable to be used for select or autocomplete (or compatible) components.

     @datasource
     @name Tags
     @location /libs/cq/gui/components/common/datasources/tags

     @path {String[]} [namespaces] The namespaces of the tags to return. Only the tag which namespace equals to one of this values is returned. If this property is not specified, all tags are returned.
     @variant {String[]} [namespaces] variant name, pathvalue returns tag path, tagidvalue return tag tagid, valuelist return tag value

     */
    Config dsCfg = new Config(_resource.getChild("datasource"));
    List<String> namespaces = Arrays.asList(dsCfg.get("namespaces", new String[0]));


    ExpressionCustomizer expressionCustomizer = ExpressionCustomizer.from(request);

    TagManager tm = _resourceResolver.adaptTo(TagManager.class);

    Tenant tenant = _resourceResolver.adaptTo(Tenant.class);

    String requestSuffix = _slingRequest.getRequestPathInfo().getSuffix();

    if (tenant == null) {
        tenant = _resource.adaptTo(Tenant.class);

//        if (tenant != null) {
//            expressionCustomizer.setVariable("tenantId", tenant.getId());
//            expressionCustomizer.setVariable("tenant", tenant);
//        } else {
            String finalTenantId;
            if (isNotEmpty(requestSuffix)) {
                finalTenantId = resolveTenantIdFromPath(requestSuffix);
            } else {
                finalTenantId = resolveTenantIdFromPath(_resource.getPath());
            }
            if (isNotEmpty(finalTenantId)) {
                expressionCustomizer.setVariable("tenantId", finalTenantId);
            }

//        }
    }

    ExpressionHelper ex = cmp.getExpressionHelper();

    String path = ex.getString(dsCfg.get("path",""));

    final String variant = dsCfg.get("variant","");


    IteratorChain tags = new IteratorChain();

    if (tm != null) {

        if (StringUtils.isNotEmpty(path)) {
            Resource rs = _resourceResolver.resolve(path);
            if (rs != null) {
                Tag tagPath = rs.adaptTo(Tag.class);
                if (tagPath != null) {
                    tags.addIterator(tagPath.listAllSubTags());
                }
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
    final ResourceResolver resolver = _resourceResolver;

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
                } else if ("tagidvalue".equals(variant)) {

                    value = tag.getTagID();
                    text = tag.getTitle(locale);
                } else if ("tagname".equals(variant)) {

                    value = tag.getName();
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
