package design.aem.models.v3.widgets;

import design.aem.components.AttrBuilder;
import design.aem.models.v3.Component;
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
    default String getComponentName() {
        throw new UnsupportedOperationException();
    }

    /**
     * Retrieve the Vue component HTML structure.
     */
    default String getComponentHTML() {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the instance of {@link Map} for
     */
    default Map<String, String> getConfigOutput() {
        throw new UnsupportedOperationException();
    }
}
