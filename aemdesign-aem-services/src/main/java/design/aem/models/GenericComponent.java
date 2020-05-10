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
