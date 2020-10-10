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
import com.fasterxml.jackson.annotation.JsonIgnore;
import design.aem.components.AttrBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Component extends ComponentExporter {
    /**
     * Get a {@code boolean} whether the component has been configured.
     */
    @JsonIgnore
    default boolean isConfigured() {
        return true;
    }

    /**
     * Retrieve the unique HTML identifier of the component.
     */
    @Nullable
    default String getComponentId() {
        return null;
    }

    /**
     * Get the instance of {@link AttrBuilder} which contains HTML attributes.
     */
    default AttrBuilder getAttributes() {
        throw new UnsupportedOperationException();
    }

    /**
     * Determine if the request is a 'badge' request.
     */
    default boolean isBadgeRequest() {
        return false;
    }

    /**
     * Determine if the request is a Sling 'model' request.
     */
    default boolean isSlingModelRequest() {
        return false;
    }

    /**
     * Retrieve the HTL template for the current badge request.
     */
    @Nullable
    default String getBadgeTemplate() {
        return null;
    }

    /**
     * Retrieve the name of the component.
     */
    default String getComponentName() {
        throw new UnsupportedOperationException();
    }

    /**
     * Retrieve the variant name for the component.
     */
    default String getVariant() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see ComponentExporter#getExportedType()
     */
    @Override
    @NotNull
    default String getExportedType() {
        throw new UnsupportedOperationException();
    }
}
