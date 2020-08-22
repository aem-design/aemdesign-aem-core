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
package design.aem.models.v3.widgets;

import design.aem.components.AttrBuilder;
import design.aem.models.v3.Component;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ConsumerType;

import java.util.Map;

@ConsumerType
public interface Vue extends Component {
    /**
     * Get the instance of {@link AttrBuilder} which contains HTML attributes.
     */
    default AttrBuilder getVueAttributes() {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the current Vue component name.
     */
    @Nullable
    default String getComponentName() {
        return null;
    }

    /**
     * Retrieve the Vue component HTML structure.
     */
    @Nullable
    default String getComponentHTML() {
        return null;
    }

    /**
     * Get the instance of {@link Map} for
     */
    default Map<String, String> getConfigOutput() {
        throw new UnsupportedOperationException();
    }
}
