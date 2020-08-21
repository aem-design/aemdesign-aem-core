package design.aem.models.v3;

import com.adobe.cq.export.json.ComponentExporter;
import design.aem.components.AttrBuilder;

import javax.annotation.Nonnull;

public interface Component extends ComponentExporter {
    /**
     * Get the instance of {@link AttrBuilder} which contains HTML attributes.
     */
    default AttrBuilder getAttributes() {
        throw new UnsupportedOperationException();
    }

    /**
     * Determine if the request is a Sling 'model' request.
     */
    default boolean isSlingModelRequest() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see ComponentExporter#getExportedType()
     */
    @Nonnull
    @Override
    default String getExportedType() {
        throw new UnsupportedOperationException();
    }
}
