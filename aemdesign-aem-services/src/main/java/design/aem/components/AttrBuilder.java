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
package design.aem.components;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.xss.XSSAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class AttrBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttrBuilder.class);
    private static final String ATTR_CLASS = "class";

    private final XSSAPI xss;

    private final Map<String, String> attributes = new LinkedHashMap<>();
    private final Map<String, EncodingType> attributeEncoding = new LinkedHashMap<>();
    private final Set<String> classes = new HashSet<>();

    public AttrBuilder(@Nonnull XSSAPI xss) {
        this.xss = xss;
    }

    /**
     * Add the provided values to the {@code class} attribute.
     *
     * @param values the class value(s) to add
     */
    public void addClass(@CheckForNull String... values) {
        Arrays.stream(values).filter(StringUtils::isNotBlank).forEach(value -> {
            if (this.classes.add(value)) {
                add(ATTR_CLASS, value);
            }
        });
    }

    /**
     * Adds the attribute without a value when {@code true}.
     *
     * @param name the name of the field
     * @param value {@code boolean} value
     */
    public void addBoolean(@CheckForNull String name, boolean value) {
        if (!value) {
            return;
        }

        add(name, StringUtils.EMPTY);
    }

    /**
     * Adds the given name as {@code data-*} attribute.
     *
     * @param name  the name of the {@code data-*} attribute to add
     * @param value the value of the attribute
     */
    public void addOther(@CheckForNull String name, @CheckForNull String value) {
        if (StringUtils.isBlank(name)) {
            return;
        }

        add("data-" + xss.encodeForHTML(name), value);
    }

    /**
     * Adds attribute with the given name. The value will be added to existing
     * attribute using space-delimited convention. e.g. class="class1 class2"
     *
     * @param name  the name of the attribute to add
     * @param value the boolean value of the attribute
     */
    public void add(@CheckForNull String name, @CheckForNull Boolean value) {
        if (isFieldInvalid(name, value)) {
            return;
        }

        addNoCheck(name, value.toString());
    }

    /**
     * Adds attribute with the given name. The value will be added to existing
     * attribute using space-delimited convention. e.g. class="class1 class2"
     *
     * @param name  the name of the attribute to add
     * @param value the string value of the attribute
     */
    public void add(@CheckForNull String name, @CheckForNull String value) {
        if (isFieldInvalid(name, value)) {
            return;
        }

        addNoCheck(name, value);
        attributeEncoding.put(name, EncodingType.HTML_ATTR);
    }

    /**
     * Adds attribute with the given name using the supplied {@link EncodingType}.
     *
     * @param name  the name of the attribute to add
     * @param value the string value of the attribute
     * @param encoding {@link EncodingType}
     */
    public void add(@CheckForNull String name, @CheckForNull String value, EncodingType encoding) {
        if (isFieldInvalid(name, value)) {
            return;
        }

        addNoCheck(name, value);

        if (encoding != null) {
            attributeEncoding.put(name, encoding);
        }
    }

    /**
     * Sets attribute with the given name. Existing value previously set will be replaced
     * by the given value.
     *
     * @param name  the name of the attribute to set or replace (if exists)
     * @param value the string value of the attribute
     */
    public void set(@CheckForNull String name, @CheckForNull String value) {
        if (value == null || name == null || StringUtils.isBlank(name)) {
            return;
        }

        attributes.put(name, value);
        attributeEncoding.put(name, EncodingType.HTML_ATTR);
    }

    private void addNoCheck(@Nonnull String name, @Nonnull String value) {
        if (attributes.containsKey(name)) {
            // When the attribute has already been added, append the new value onto the end of it
            attributes.put(name, attributes.get(name) + " " + value);
        } else {
            attributes.put(name, value);
        }
    }

    /**
     * Check if the field is invalid.
     *
     * @param name Name of the field
     * @param value Value of the field
     * @return {@code true} when the field is invalid
     */
    private boolean isFieldInvalid(String name, Object value) {
        return value == null || name == null || StringUtils.isBlank(name);
    }

    /**
     * Retrieves a {@code boolean} as to whether the attributes map is empty.
     *
     * @return {@code true} when no attributes exist or {@code false}
     */
    public boolean isEmpty() {
        return attributes.isEmpty();
    }

    /**
     * Retrieve the raw attributes {@link Map} without any encoding applied.
     *
     * @return {@link Map} of raw attributes
     */
    @Nonnull
    public Map<String, String> getAttributes() {
        return attributes;
    }

    /**
     * Builds the attributes in the form of {@code <attr-name>='<attr-value>'*}.
     *
     * @return the string containing the built attributes
     */
    public String build() {
        StringWriter out = new StringWriter();

        try {
            build(out);
        } catch (IOException ex) {
            LOGGER.error("Build error while constructing attributes. Error: {}", ex.getMessage());
        }

        return out.toString();
    }

    /**
     * Builds the attributes in the form of {@code <attr-name>='<attr-value>'*}*.
     *
     * @param out the writer
     * @throws IOException in case there's an error when appending to the writer
     */
    public void build(@Nonnull Writer out) throws IOException {
        List<String> attrs = new LinkedList<>();

        attributes.forEach((attribute, value) -> {
            EncodingType encoding = attributeEncoding.get(attribute);

            if (encoding != null && value.length() > 0) {
                value = encoding == EncodingType.HREF ? xss.getValidHref(value) : xss.encodeForHTMLAttr(value);
            }

            if (isNotEmpty(value)) {
                attrs.add(String.format("%s=\"%s\"", attribute, value));
            } else {
                attrs.add(attribute);
            }
        });

        out.write(String.join(" ", attrs));
    }

    public String toString() {
        return build();
    }

    public enum EncodingType {
        HREF,
        HTML_ATTR,
    }
}
