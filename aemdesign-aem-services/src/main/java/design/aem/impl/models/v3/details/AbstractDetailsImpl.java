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
package design.aem.impl.models.v3.details;

import design.aem.impl.models.ComponentImpl;
import design.aem.models.v3.details.BaseDetails;
import design.aem.utils.components.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static design.aem.utils.components.ComponentsUtil.*;

public abstract class AbstractDetailsImpl extends ComponentImpl implements BaseDetails {
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(booleanValues = false)
    protected boolean hideTitle;

    @Override
    protected Map<String, Function<Object, Object>> getPropertyDefaultsMap() {
        Map<String, Function<Object, Object>> propertyDefaults = super.getPropertyDefaultsMap();

        propertyDefaults.put(FIELD_TITLE, value ->
            StringUtils.defaultIfEmpty((String) value, CommonUtil.getPageTitle(currentPage, resource)));

        propertyDefaults.put(FIELD_TITLE_TAG_TYPE, value ->
            StringUtils.defaultIfEmpty((String) value, DEFAULT_TITLE_LEVEL));

        propertyDefaults.put(FIELD_DESCRIPTION, value ->
            StringUtils.defaultIfEmpty((String) value, CommonUtil.getPageDescription(currentPage, properties)));

        return propertyDefaults;
    }

    @Nullable
    @Override
    public String getTitle() {
        return compileComponentMessage(FIELD_TITLE_FORMAT, getTitleFormat(), properties, slingScriptHelper);
    }

    protected String getTitleFormat() {
        return (String) properties.getOrDefault(FIELD_TITLE_FORMAT, getFormatExpression(FIELD_TITLE));
    }

    @Override
    public String getTitleLevel() {
        return properties.get(FIELD_TITLE_TAG_TYPE, String.class);
    }

    @Override
    public boolean hasTitle() {
        return Boolean.FALSE.equals(hideTitle) && StringUtils.isNotEmpty(getTitle());
    }

    @Nullable
    @Override
    public String getDescription() {
        return properties.get(FIELD_DESCRIPTION, String.class);
    }
}
