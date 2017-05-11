package design.aem.impl.filters;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.sling.SlingFilter;
import org.apache.felix.scr.annotations.sling.SlingFilterScope;
import org.apache.sling.api.SlingHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;

/**
* Simple servlet filter component that logs incoming requests.
*/
@SlingFilter(generateComponent = false, generateService = true, order = -700, scope = SlingFilterScope.REQUEST)
@Component(immediate = true, metatype = false)
public class LoggingFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        // because this is a Sling filter, we can be assured the the request
        // is an instance of SlingHttpServletRequest
        SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
        logger.debug("request for {}, with selector {}",
                slingRequest.getRequestPathInfo().getResourcePath(),
                slingRequest.getRequestPathInfo().getSelectorString());

        chain.doFilter(request, response);
    }

    public void destroy() {
    }

}
