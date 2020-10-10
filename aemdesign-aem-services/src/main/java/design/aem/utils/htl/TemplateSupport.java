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
package design.aem.utils.htl;

import design.aem.utils.support.ComponentPathInvocationHandler;
import design.aem.utils.support.InvocationProxyFactory;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.osgi.annotation.versioning.ConsumerType;

import javax.annotation.PostConstruct;
import java.util.Map;

@ConsumerType
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class TemplateSupport {
    private Map<String, String> componentPathProxy;

    @ScriptVariable
    private Resource resource;

    @PostConstruct
    protected void init() {
        componentPathProxy = InvocationProxyFactory.create(resource.adaptTo(ComponentPathInvocationHandler.class));
    }

    public Map<String, String> getComponentPath() {
        return componentPathProxy;
    }
}
