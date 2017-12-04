<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@ page import="java.text.MessageFormat" %>

<c:if test="${PRINT_COMPONENT_BADGE}">
    <c:if test="${CURRENT_WCMMODE == WCMMODE_EDIT || CURRENT_WCMMODE == WCMMODE_PREVIEW}">
    <details class="component badge badge-pill">
        <%
        String originLocation = "";
        String originVariant = "";
        String componentId = "";
        String originLocationLink = " Source: <a target=\"_blank\" href=\"{0}.html\">{1}</a>";

        try {
            Resource originalResource = (Resource) request.getAttribute(INHERITED_RESOURCE);
            if (originalResource != null) {
                String originPath = originalResource.getPath();
                String originName = originalResource.getName();
                ValueMap resourceProps = originalResource.getValueMap();
                if (resourceProps!=null) {
                    originVariant = resourceProps.get(FIELD_VARIANT,"variant not set");
                    componentId = resourceProps.get(FIELD_STYLE_COMPONENT_ID,"");
                }
                if (originPath.indexOf("/jcr") > 0) {
                    originPath = originPath.substring(0, originPath.indexOf("/jcr"));

                    Page originPage = _pageManager.getPage(originPath);
                    if (originPage != null) {
                        originName = originPage.getTitle();
                    } else {
                        originName = originPath;
                    }
                }
                originLocation = MessageFormat.format(originLocationLink, originPath, originName);
                request.removeAttribute(INHERITED_RESOURCE);
            } else {
                ValueMap resourceProps = _resource.getValueMap();
                if (resourceProps!=null) {
                    originVariant = resourceProps.get(FIELD_VARIANT,"variant not set");
                    componentId = resourceProps.get(FIELD_STYLE_COMPONENT_ID,"");

                    String referencePath = resourceProps.get(FIELD_REFERENCE_PATH,"");
                    if (isNotEmpty(referencePath)) {
                        Page originPage = _pageManager.getContainingPage(referencePath);
                        if (originPage != null) {
                            originLocation = MessageFormat.format(
                                    originLocationLink,
                                    originPage.getPath(),
                                    originPage.getName()
                                );
                        }
                    }
                }
            }

        } catch (Exception ex) {
            LOG.error(ex.toString());
        }
        %>
        <summary><%=_component.getTitle()%></summary>
        <%--<c:if test="${not empty componentProperties}">--%>
            <%--<c:forEach items="${componentProperties}" var="property" varStatus="status"> <small>${property.key}: ${property.value};</small></c:forEach>--%>
        <%--</c:if>--%>
        <small>(<%=_componentContext.getCell().getName()%> : <%=_resource.getName()%> #<%=componentId%> v:<%=originVariant%>)<%=originLocation%></small>
        <%

        if (INCLUDE_BADGE_VARIANT_CODE) {
            String componentVariant = DEFAULT_VARIANT;

            //print component variant template
            ComponentProperties componentBadgeProperties = (ComponentProperties) pageContext.getAttribute("componentProperties");
            if (componentBadgeProperties != null) {
                componentVariant = componentBadgeProperties.get(FIELD_VARIANT, DEFAULT_VARIANT);
            }

            String variantPath = String.format("%s/variant.%s.jsp", _component.getPath(), componentVariant);

            Resource variantResource = _resourceResolver.getResource(variantPath);
            if (variantResource != null) {
                String variantContents = getResourceContent(variantResource);
                out.write( _xssAPI.encodeForHTML(String.format("<div class=\"code variant %s\"><pre><code>%s</code></pre></div>", componentVariant, variantContents)) );
            }
        }
        %>
    </details>
    </c:if>
</c:if>
