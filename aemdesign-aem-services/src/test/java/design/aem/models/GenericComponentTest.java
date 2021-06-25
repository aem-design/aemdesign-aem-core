package design.aem.models;

import ch.qos.logback.classic.Logger;
import design.aem.components.ComponentProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.mockito.Mockito.*;

public class GenericComponentTest {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(GenericComponentTest.class);


    @Test
    public void getInterfaceUsingModel() {
        // Run the test
        GenericModel genericModel = mock(GenericModel.class, withSettings().extraInterfaces(GenericComponent.class));

        ComponentProperties componentProperties = new ComponentProperties();

        when(genericModel.getComponentProperties()).thenReturn(componentProperties);

        ComponentProperties output = ((GenericComponent) genericModel).getComponentProperties();

        // Verify the results
        Assertions.assertEquals(componentProperties, output);
    }


}
