<%@page import="com.day.cq.wcm.api.WCMMode,
    com.day.cq.wcm.api.components.DropTarget" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/i18n.jsp" %>
<%

    //no lambada is available so this is the best that can be done
    Object[][] componentFields = {
            {"path", StringUtils.EMPTY}
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

    String target = _properties.get("path", String.class);
    Resource targetResource = resourceResolver.resolve(target);

    request.setAttribute(INHERITED_RESOURCE, targetResource);

    String defDecor =_componentContext.getDefaultDecorationTagName();

    if (targetResource != null) {

        boolean needToCloseDiv = false;
        // Use request attributes to guard against reference loops
        String path = _resource.getPath();
        String key = "apps.aemdesign.components.content.reference:" + path;

        try {
            if (request.getAttribute(key) == null || Boolean.FALSE.equals(request.getAttribute(key))) {
                request.setAttribute(key, Boolean.TRUE);
            } else {
                throw new IllegalStateException("Reference loop: " + path);
            }

            //drop target css class = dd prefix + name of the drop target in the edit config
            String ddClassName = DropTarget.CSS_CLASS_PREFIX + "paragraph";

            // Include the target paragraph by path
            if (target != null) {
                if (CURRENT_WCMMODE == WCMMode.EDIT || CURRENT_WCMMODE == WCMMode.DESIGN) {
                    %><div style="display:inline;" class="<%= ddClassName %>"><%
                    needToCloseDiv = true;
                }

                disableEditMode(_componentContext, IncludeOptions.getOptions(request, true), _slingRequest);
                %><sling:include path="<%= target %>"/><%

                if (CURRENT_WCMMODE == WCMMode.EDIT || CURRENT_WCMMODE == WCMMode.DESIGN) {
                %></div><%
                    needToCloseDiv = false;
                }
            } else if (CURRENT_WCMMODE == WCMMode.EDIT) {
                %><p><img src="/libs/cq/ui/resources/0.gif" class="cq-reference-placeholder <%= ddClassName %>" alt="" /></p><%
            }

        } catch (Exception e) {
            // Log errors always
            _log.error(MessageFormat.format("Reference component error: {0}",_properties.get("path", String.class).toString()), e);

            // Display errors only in edit mode
            if (CURRENT_WCMMODE == WCMMode.EDIT) {
                %><p>Reference error: <%= xssAPI.encodeForHTML(e.toString()) %></p><%
            }
        } finally {
            if (needToCloseDiv) {
                if (CURRENT_WCMMODE == WCMMode.EDIT | CURRENT_WCMMODE == WCMMode.DESIGN) {
                %></div><%
                }
            }
            enableEditMode(CURRENT_WCMMODE, _componentContext, defDecor, IncludeOptions.getOptions(request, true), _slingRequest);
            request.setAttribute(key,Boolean.FALSE);

        }

    } else {
        %><p>Reference not found: <%= target %></p><%
    }

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<div ${componentProperties.componentAttributes}>
    <c:choose>
        <c:when test="${componentProperties.variant eq 'default'}">
            <%@ include file="variant.default.jsp" %>
        </c:when>
        <c:when test="${componentProperties.variant eq 'render'}">
            <%@ include file="variant.render.jsp" %>
        </c:when>
        <c:otherwise>
            <%@ include file="variant.empty.jsp" %>
        </c:otherwise>
    </c:choose>
</div>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>