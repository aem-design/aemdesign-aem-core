package design.aem.impl.commands;

import design.aem.CommandHandler;
import design.aem.CommandService;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.servlets.post.HtmlResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SlingServlet(
        resourceTypes = "aemdesign/api",
        description = "API Commands Servlet",
        methods = "POST",
        paths = "/bin/nodeLockUnlock"
)
public class CommandServlet extends SlingAllMethodsServlet {

  private static final Logger log = LoggerFactory.getLogger(CommandServlet.class);

  @Reference
  private CommandService commandService;

  @Override
  protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
    String cmd = request.getRequestParameter("cmd").getString();

    CommandHandler commandHandler = commandService.getCommandHandler(cmd);

    if (commandHandler != null) {
      try {
        Object ret = commandHandler.performCommand(cmd, request, response);

        if (ret != null) {
          if (ret instanceof HtmlResponse) {
            ((HtmlResponse) ret).send(response, true);
          }
        }
      } catch (ServletException | IOException ex) {
        throw ex;
      } catch (Exception ex) {
        throw new ServletException("Command " + cmd + " processing caused an exception", ex);
      }
    } else {
      log.warn("API Command not supported: {}", cmd);
      response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
  }
}
