package design.aem.models;

import ch.qos.logback.classic.Logger;
import com.adobe.cq.export.json.ComponentExporter;
import design.aem.components.ComponentProperties;
import design.aem.impl.services.ContentAccessImpl;
import design.aem.impl.services.ContentAccessImplTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GenericComponentTest {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(GenericComponentTest.class);


    @Test
    public void getInterfaceUsingModel() {
        // Run the test
        GenericModel genericModel = mock(GenericModel.class, withSettings().extraInterfaces(GenericComponent.class));

        ComponentProperties componentProperties = new ComponentProperties();

        when(genericModel.getComponentProperties()).thenReturn(componentProperties);

        GenericComponent genericComponent = (GenericComponent) genericModel;

        ComponentProperties output = genericComponent.getComponentProperties();

        // Verify the results
        assertEquals(componentProperties, output);
    }


}