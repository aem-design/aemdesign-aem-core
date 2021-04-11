<%@page session="false"
        contentType="text/html"
        pageEncoding="utf-8"
        import="org.apache.commons.lang3.StringEscapeUtils,
            com.day.cq.replication.Agent,
            com.day.cq.replication.AgentManager,
            com.day.cq.replication.AgentConfig,
            com.day.cq.replication.ReplicationActionType,
            com.day.cq.replication.ReplicationOptions,
            com.day.cq.replication.ReplicationListener,
            com.day.cq.replication.ReplicationAction,
            com.day.cq.replication.ReplicationResult,
            java.io.PrintWriter,
            com.day.cq.replication.Replicator, com.day.cq.replication.ReplicationLog" %><%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %><%
%><cq:defineObjects />
<%@include file="/libs/cq/replication/components/dateutils/dateutils.jsp" %>
<%

    String agentName = currentPage.getName();
    Replicator repl = sling.getService(Replicator.class);
    AgentManager agentMgr = sling.getService(AgentManager.class);
    Agent agent = agentName == null ? null : agentMgr.getAgents().get(agentName);
    AgentConfig cfg = agent == null ? null : agent.getConfiguration();

    if (cfg == null || !cfg.getConfigPath().startsWith(currentNode.getPath())) {
        // agent not active
        agent = null;
    }
    String title = agent == null ? null : agent.getConfiguration().getName();
    if (title == null) {
        title = agentName;
    }
%><html><head>
    <style type="text/css">
        code {
            font-family:lucida console, courier new, monospace;
            font-size:12px;
            white-space:nowrap;
        }
        .error {
            color: red;
            font-weight: bold;
        }
    </style>
    <title>AEM Replication | Testing <%= StringEscapeUtils.escapeHtml4(title) %></title>
</head>
<body bgcolor="white"><%
    if (agent == null) {
        %>no such agent: <%= StringEscapeUtils.escapeHtml4(agentName) %><br><%
    } else {
        %><h1>Replication test to <%= xssAPI.encodeForHTML(agent.getConfiguration().getTransportURI()) %></h1><code><%
        ReplicationOptions opts = new ReplicationOptions();
        opts.setDesiredAgentIDs(agentName);
        opts.setSynchronous(true);
        TestListener test = new TestListener(new PrintWriter(out));
        opts.setListener(test);
        repl.replicate(currentNode.getSession(), ReplicationActionType.TEST, "/content", opts);
        %></code><%
    }
%></body></html><%!

    private static class TestListener implements ReplicationListener {

        private final PrintWriter out;

        private TestListener(PrintWriter out) {
            this.out = out;
        }

        public void onStart(Agent agent, ReplicationAction action) {
            //messages are logged directly from backend
        }

        public void onMessage(ReplicationLog.Level level, String message) {
            print(message);
        }

        public void onEnd(Agent agent, ReplicationAction action, ReplicationResult result) {
            boolean s = result.isSuccess() && result.getCode() == 200;
            out.printf("<hr size=\"1\">Replication test <strong>%s</strong><br>", s ? "succeeded" : "failed");
            if (!result.isSuccess()) {
                out.printf("<span class=\"error\">%s</span>", result.getMessage());
            }
            out.flush();
        }

        public void onError(Agent agent, ReplicationAction action, Exception error) {
            print("Error while replicating: " + error.toString());
        }

        private void print(String msg) {
            String date = REPLICATION_DATE_DEFAULT.format(System.currentTimeMillis());
            out.printf("%s - %s<br>", date, StringEscapeUtils.escapeXml(msg));
            out.flush();
        }
    }
%>
