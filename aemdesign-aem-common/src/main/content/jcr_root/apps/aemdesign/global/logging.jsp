<%@ page import="org.slf4j.Logger" %><%!

    /** Local logging container. */
    private ThreadLocal<Logger> localLogger = new ThreadLocal<Logger>();

    /**
     * Utility method for retrieving the current thread's logger instance.
     * @return The current logging instance.
     */
    public synchronized Logger getLogger() {
        return localLogger.get();
    }

%><%
    localLogger.set((org.slf4j.Logger)pageContext.getAttribute("log"));
%>