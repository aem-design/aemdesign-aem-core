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
package design.aem.utils.support;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.jcr.Session;
import java.util.HashMap;
import java.util.Map;

import static design.aem.utils.components.ComponentsUtil.DEFAULT_TENANT;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class ComponentPathInvocationHandler extends InvocationProxyHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentPathInvocationHandler.class);

    @Inject
    private QueryBuilder queryBuilder;

    @Inject
    private Session session;

    @Override
    protected Object handle(Object[] args) {
        String componentName = (String) args[0];

        Map<String, String> predicates = new HashMap<>();

        // TODO: Implement OSGI service to allow this to be configurable
        String tenantPath = DEFAULT_TENANT;

        predicates.put("orderby", "nodename");
        predicates.put("path", "/apps/" + tenantPath + "/components");
        predicates.put("type", "q:Component");
        predicates.put("1_property", "sling:resourceSuperType");
        predicates.put("1_property.operation", "like");
        predicates.put("1_property.value", "%" + componentName);
        predicates.put("p.limit", "1");

        Query query = queryBuilder.createQuery(PredicateGroup.create(predicates), session);

        if (query != null) {
            try {
                String componentPath = query.getResult().getHits().get(0).getPath();

                return componentPath.replace("/apps/", StringUtils.EMPTY);
            } catch (Exception ex) {
                LOGGER.error("Unexpected error occurred while trying to retrieve the component path: {}", ex.getLocalizedMessage());
            }
        }

        return null;
    }
}
