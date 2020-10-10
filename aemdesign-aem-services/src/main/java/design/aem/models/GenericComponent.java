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
package design.aem.models;

import com.adobe.cq.export.json.ComponentExporter;
import design.aem.components.ComponentProperties;
import org.osgi.annotation.versioning.ConsumerType;

import javax.annotation.Nonnull;

/**
 * Defines a standard component that return a map of all its config.
 */
@ConsumerType
public interface GenericComponent extends ComponentExporter {

    /**
     * Retrieves the text value to be displayed.
     *
     * @return the text value to be displayed, or {@code null} if no value can be returned
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    default ComponentProperties getComponentProperties() {
        throw new UnsupportedOperationException();
    }

    /**
     * return component type.
     *
     * @see ComponentExporter#getExportedType()
     */
    @Nonnull
    @Override
    default String getExportedType() {
        throw new UnsupportedOperationException();
    }

}
