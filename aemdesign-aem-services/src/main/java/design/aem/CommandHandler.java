package design.aem;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

import java.util.Collection;

public interface CommandHandler {
    Collection<String> getSupportedCommands();
    Object performCommand(String cmd, SlingHttpServletRequest request, SlingHttpServletResponse response) throws Exception;
}
