<%@ page import="com.day.cq.search.Query"%><%@ page import="com.day.cq.search.PredicateConverter"%><%@ page import="com.day.cq.search.QueryBuilder"%><%@ page import="com.day.cq.search.result.SearchResult"%><%@ page import="com.day.cq.search.result.Hit"%><%@page contentType="application/json" %>
<%@include file="/apps/aemdesign/global/global.jsp" %>
<%@include file="/apps/aemdesign/global/components.jsp" %>
<%
    ComponentProperties componentProperties = new ComponentProperties();

    List<Map<String, String>> results = new ArrayList<Map<String, String>>();

    Map<String, String> optionConditions = new HashMap<String, String>();

    optionConditions.put("group.1_path", "/apps/aemdesign/components/forms/actions");
    optionConditions.put("group.1_path.flat", "true");
    optionConditions.put("group.2_path", "/libs/foundation/components/form/actions");
    optionConditions.put("group.2_path.flat", "true");
    optionConditions.put("group.p.or", "true");
    optionConditions.put("property", "sling:resourceType");
    optionConditions.put("property.value", "foundation/components/form/action");
    optionConditions.put("p.limit", "-1");
    optionConditions.put("p.hits", "selective");
    optionConditions.put("p.properties", "jcr:path jcr:title hint");

    QueryBuilder queryBuilder = _sling.getService(QueryBuilder.class);

    javax.jcr.Session jcrSession = _slingRequest.getResourceResolver().adaptTo(javax.jcr.Session.class);

    Query query = queryBuilder.createQuery(PredicateConverter.createPredicates(optionConditions), jcrSession);

    SearchResult result = query.getResult();

    _log.info("Total result count : "    + result.getTotalMatches());

    try {
        // iterating over the results
        for (Hit hit : result.getHits()) {
            Map<String, String> option = new HashMap<String, String>();
            String optionsTextField = hit.getProperties().get("jcr:title", StringUtils.EMPTY);
            option.put("text", StringEscapeUtils.escapeEcmaScript(optionsTextField));

            String optionsValueField = hit.getPath();
            if (optionsValueField.startsWith("/") && (optionsValueField.charAt(5) == '/')){
                optionsValueField = optionsValueField.substring(6);
            }
            option.put("value", StringEscapeUtils.escapeEcmaScript(optionsValueField));

            if (hit.getProperties().containsKey("hint")){
                String qtip = hit.getProperties().get("hint", String.class);
                option.put("qtip", StringEscapeUtils.escapeEcmaScript(qtip));
            }
            results.add(option);

        }
    } catch (Exception e) {
        _log.error("Failed to " + e.getMessage(), e);
    }

    componentProperties.put("results", results);


%>

<c:set var="componentProperties" value="<%= componentProperties %>"/>

{"states":[
    <c:forEach var="item" items="${componentProperties.results}" varStatus="loop">
    {
        "text": "${item.text}",
        "value": "${item.value}" ${not empty item.qtip ? "," : ""}
        <c:if test="${not empty item.qtip}">
            "qtip": "${item.qtip}"
        </c:if>
    } ${not loop.last ? ',' : ''}
    </c:forEach>
]}
