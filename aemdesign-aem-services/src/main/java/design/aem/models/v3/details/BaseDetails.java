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
package design.aem.models.v3.details;

import design.aem.models.Component;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ConsumerType;

@ConsumerType
public interface BaseDetails extends Component {
    /**
     * Get the title of the page using the details component first, then Page Properties.
     */
    @Nullable
    default String getTitle() {
        return null;
    }

    /**
     * Get the description of the page using the details component first, then Page Properties.
     */
    @Nullable
    default String getDescription() {
        return null;
    }
}
