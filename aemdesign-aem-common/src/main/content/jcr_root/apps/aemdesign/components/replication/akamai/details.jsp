<%@page session="false"%>
<%@page contentType="text/html"
            pageEncoding="utf-8"
            import="com.day.cq.replication.Agent,
                    com.day.cq.replication.AgentConfig,
                    com.day.cq.replication.AgentManager,
                    com.day.cq.replication.ReplicationQueue,
                    com.day.cq.i18n.I18n,
                    org.apache.sling.api.resource.ValueMap,
                    javax.jcr.Node,
                    com.adobe.granite.crypto.CryptoSupport" %>
<%@ page import="javax.jcr.RepositoryException" %>
<%@ page import="com.adobe.granite.crypto.CryptoException" %>
<%@ page import="javax.jcr.Session" %>
<%
%><%@include file="/libs/foundation/global.jsp"%><%
    String id = currentPage.getName();
    I18n i18n = new I18n(slingRequest);

    AgentManager agentMgr = sling.getService(AgentManager.class);
    Agent agent = agentMgr.getAgents().get(id);
    AgentConfig cfg = agent == null ? null : agent.getConfiguration();
    ReplicationQueue queue = agent == null ? null : agent.getQueue();

    if (cfg == null || !cfg.getConfigPath().equals(currentNode.getPath())) {
        // agent not active
        agent = null;
    }

    ValueMap agentValues = resource.adaptTo(ValueMap.class);
    Node agentNode = resource.adaptTo(Node.class);

    if (agentValues != null) {
        String token = agentValues.get("token", "");
        String accesstoken = agentValues.get("accesstoken", "");
        String secret = agentValues.get("secret", "");
        CryptoSupport cryptoSupport = sling.getService(CryptoSupport.class);

        try {
          if(token.length()!=0 && !cryptoSupport.isProtected(token) ){
              log.error("Agent config [{}] has unprotected field {}, attempting to protect.", resource.getPath(), "token");
              agentNode.setProperty("token", cryptoSupport.protect(token));
          }
          if(accesstoken.length()!=0 && !cryptoSupport.isProtected(accesstoken) ){
              log.error("Agent config [{}] has unprotected field {}, attempting to protect.", resource.getPath(), "accesstoken");
              agentNode.setProperty("accesstoken", cryptoSupport.protect(accesstoken));
          }
          if(secret.length()!=0 && !cryptoSupport.isProtected(secret) ){
              log.error("Agent config [{}] has unprotected field {}, attempting to protect.", resource.getPath(), "secret");
              agentNode.setProperty("secret", cryptoSupport.protect(secret));
          }
          Session resourceSession = resource.getResourceResolver().adaptTo(Session.class);
          resourceSession.save();
        } catch (RepositoryException | CryptoException e) {
          e.printStackTrace();
        }

    }

    String uri = xssAPI.encodeForHTML(properties.get("transportUri", i18n.get("(not configured)")));
    String queueStr = i18n.get("Queue is <strong>not active</strong>");
            String queueCls = "cq-agent-queue";
            if (queue != null) {
                int num = queue.entries().size();
                if (queue.isPaused()) {
                    queueStr = i18n.get("Queue is <strong>paused</strong>");
                    queueCls += "-blocked";
                } else if (queue.isBlocked()) {
                    queueStr = i18n.get("Queue is <strong>blocked - {0} pending</strong>", "{0} is the number of pending items", num);
                    queueCls += "-blocked";
                } else {
                    if (num == 0) {
                        queueStr = i18n.get("Queue is <strong>idle</strong>");
                        queueCls += "-idle";
                    } else  {
                        queueStr = i18n.get("Queue is <strong>active - {0} pending</strong>", "{0} is the number of pending items", num);
                        queueCls += "-active";
                    }
                }
            } else {
                queueCls += "-inactive";
            }

            // get status
            String status;
            String message = i18n.get("Replicating to <strong>{0}</strong>", "{0} is an URL", uri);
            String globalIcnCls = "cq-agent-header";
            String statusIcnCls = "cq-agent-status";
            if (agent == null) {
                status = "not active";
                statusIcnCls += "-inactive";
                globalIcnCls += "-off";
            } else {
                try {
                    agent.checkValid();
                    if (agent.isEnabled()) {
                        status = i18n.get("Agent is <strong>enabled.</strong>");
                        globalIcnCls += "-on";
                        statusIcnCls += "-ok";
                    } else {
                        status = i18n.get("Agent is <strong>disabled.</strong>");
                        globalIcnCls += "-off";
                        statusIcnCls += "-disabled";
                    }
                } catch (IllegalArgumentException e) {
                    message = e.getMessage();
                    status = i18n.get("Agent is <strong>not valid.</strong>");
                    globalIcnCls += "-off";
                    statusIcnCls += "-invalid";
                }
            }

%><ul>
        <li><div class="li-bullet <%=statusIcnCls%>"><%= status %> <%= message %></div></li>
        <li><div class="li-bullet <%=queueCls%>"><%= queueStr %></div></li>
        <%
            if (cfg != null && cfg.isSpecific()) {
                %><li><%= i18n.get("Agent is ignored on normal replication") %></li><%
            }
            if (cfg != null && cfg.isTriggeredOnModification()) {
                %><li><%= i18n.get("Agent is triggered on modification") %></li><%
            }
            if (cfg != null && cfg.isTriggeredOnOffTime()) {
                %><li><%= i18n.get("Agent is triggered when on-/offtime reached") %></li><%
            }
            if (cfg != null && cfg.isTriggeredOnReceive()) {
                %><li><%= i18n.get("Agent is triggered when receiving replication events") %></li><%
            }
        %>
        <li><a href="<%= xssAPI.getValidHref(currentPage.getPath()) %>.log.html#end"><%= i18n.get("View log") %></a></li>
        <li><a href="javascript:test()"><%= i18n.get("Test Connection") %></a></li>
    </ul>
