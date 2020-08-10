package design.aem.services.feed;

import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import java.io.IOException;
import java.util.Arrays;

public class FeedService extends SlingSafeMethodsServlet {
    private static final long serialVersionUID = 1L;

    protected transient PageManager pageManager;
    protected transient ContentPolicy policy;
    protected transient ContentPolicyManager policyManager;
    protected transient Node resourceNode;
    protected transient Resource resource;
    protected transient ResourceResolver resourceResolver;
    protected transient SlingHttpServletRequest request;

    @Override
    protected void doGet(
        final SlingHttpServletRequest request,
        final @Nonnull SlingHttpServletResponse response
    ) throws IOException {
        this.resource = request.getResource();
        this.resourceResolver = request.getResourceResolver();
        this.request = request;

        try {
            pageManager = resourceResolver.adaptTo(PageManager.class);
            policyManager = resourceResolver.adaptTo(ContentPolicyManager.class);
            resourceNode = resource.adaptTo(Node.class);

            if (policyManager != null) {
                policy = policyManager.getPolicy(resource, request);
            }

            if (resourceNode != null) {
                boolean feedEnabled = Boolean.TRUE.equals(resourceNode.getProperty("feedEnabled").getBoolean());

                if (!feedEnabled || !feedMatchesRequest()) {
                    response.sendError(501, "This feed doesn't appear to be enabled!");
                } else {
                    handleResponse(response);
                }
            } else {
                response.sendError(404, "Something unknown appears to have gone wrong while initialising the feed.");
            }
        } catch (Exception ex) {
            response.sendError(404);
        }
    }

    /**
     * Default selector for the feed.
     *
     * @return {@code null} when this selector hasn't been overridden
     */
    protected String feedSelector() {
        return null;
    }

    /**
     * Determine if the current request aligns with the current feed.
     *
     * @return {@code true} when all conditions match
     */
    protected boolean feedMatchesRequest() {
        return Arrays.asList(request.getRequestPathInfo().getSelectors()).contains(feedSelector());
    }

    /**
     * Attempt to retrieve the given {@code property} from the policy (if found).
     *
     * @param property Property to lookup
     * @param fallback A default value if no policy/property is found
     * @return {@code T} found value or {@code null}
     */
    protected <T> T getStyle(String property, T fallback) {
        return policy != null ? policy.getProperties().get(property, fallback) : fallback;
    }

    /**
     * Default handler when the feed hasn't been implemented correctly.
     *
     * @param response {@code SlingHttpServletResponse} instance
     * @throws IOException
     */
    protected void handleResponse(final SlingHttpServletResponse response) throws IOException {
        throw new RuntimeException("Response handler has not been implemented.");
    }
}
