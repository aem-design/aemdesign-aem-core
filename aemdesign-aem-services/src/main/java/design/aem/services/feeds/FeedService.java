/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 AEM.Design
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package design.aem.services.feeds;

import com.day.cq.wcm.api.PageManager;
import design.aem.utils.components.ComponentsUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import java.io.IOException;
import java.util.Arrays;

import static design.aem.models.v2.lists.List.componentFieldsShared;
import static design.aem.models.v2.lists.List.componentFieldsSharedDefaults;

public class FeedService extends SlingSafeMethodsServlet {
    protected static final Logger LOGGER = LoggerFactory.getLogger(FeedService.class);

    private static final long serialVersionUID = 1L;

    protected transient ValueMap fieldDefaults;
    protected transient PageManager pageManager;
    protected transient ValueMap policy;
    protected transient Node resourceNode;
    protected transient Resource resource;
    protected transient ResourceResolver resourceResolver;
    protected transient SlingHttpServletRequest request;

    @Override
    protected void doGet(
        final @Nonnull SlingHttpServletRequest slingRequest,
        final @Nonnull SlingHttpServletResponse slingResponse
    ) throws IOException {
        try {
            resource = slingRequest.getResource();
            resourceResolver = slingRequest.getResourceResolver();
            request = slingRequest;
            pageManager = resourceResolver.adaptTo(PageManager.class);
            resourceNode = resource.adaptTo(Node.class);

            policy = ComponentsUtil.getContentPolicyProperties(resource, resourceResolver);

            if (resourceNode != null) {
                fieldDefaults = componentFieldsSharedDefaults(policy, resource.getPath());

                Object[][] componentFields = componentFieldsShared(fieldDefaults);

//                boolean feedEnabled = Boolean.TRUE.equals(componentProperties.get("feedEnabled", boolean.class));
//                String feedType = componentProperties.get("feedType", String.class);
//
//                if (!feedEnabled || !feedMatchesRequest(feedType)) {
//                    slingResponse.sendError(501, "This feed doesn't appear to be enabled!");
//                } else {
//                    handleResponse(slingResponse);
//                }

                boolean feedEnabled = Boolean.TRUE.equals(resourceNode.getProperty("feedEnabled").getBoolean());
                String feedType = resourceNode.getProperty("feedType").getString();

                if (!feedEnabled || !feedMatchesRequest(feedType)) {
                    slingResponse.sendError(501, "This feed doesn't appear to be enabled!");
                } else {
                    handleResponse(slingResponse);
                }
            } else {
                slingResponse.sendError(404, "Something unknown appears to have gone wrong while initialising the feed.");
            }
        } catch (Exception ex) {
            LOGGER.error("Unable to generate feed due to an unexpected error!\nException: {}", ex.getMessage());

            slingResponse.sendError(404);
        }
    }

    /**
     * Determine if the current request aligns with the current feed.
     *
     * @param feedType The identifier of the selected feed
     * @return {@code true} when all conditions match
     */
    protected boolean feedMatchesRequest(String feedType) {
        return Arrays.asList(request.getRequestPathInfo().getSelectors()).contains(feedType);
    }

    /**
     * Attempt to retrieve the given {@code property} from the policy (if found).
     *
     * @param property Property to lookup
     * @param fallback A default value if no policy/property is found
     * @return {@code T} found value or {@code null}
     */
    protected <T> T getStyle(String property, T fallback) {
        return policy.get(property, fallback);
    }

    /**
     * Default handler when the feed hasn't been implemented correctly.
     *
     * @param slingResponse {@code SlingHttpServletResponse} instance
     * @throws IOException
     */
    protected void handleResponse(final SlingHttpServletResponse slingResponse) throws IOException {
        handleMethodNotImplemented(request, slingResponse);
    }
}
