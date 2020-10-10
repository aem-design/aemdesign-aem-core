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
package design.aem.impl.models;

import design.aem.models.Component;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.jetbrains.annotations.NotNull;

import static design.aem.utils.components.ComponentsUtil.FIELD_VARIANT;

@Model(adaptables = SlingHttpServletRequest.class, adapters = Component.class)
public class ComponentImpl extends AbstractComponentImpl {
    @Override
    public String getVariant() {
        return properties.get(FIELD_VARIANT, String.class);
    }

    @NotNull
    @Override
    public String getExportedType() {
        return resource.getResourceType();
    }
}
