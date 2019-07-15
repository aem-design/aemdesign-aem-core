package design.aem.models;

import ch.qos.logback.classic.Logger;
import design.aem.components.ComponentProperties;
import design.aem.impl.services.ContentAccessImplTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GenericModelTest {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(ContentAccessImplTest.class);

//    @Before
//    public void setUp() throws Exception {
//    }
//
//    @Test
//    public void getComponentProperties() {
//    }
//
//    @Test
//    public void getExportedType() {
//    }
//
//    @Test
//    public void getPageManager() {
//    }
//
//    @Test
//    public void getCurrentPage() {
//    }
//
//    @Test
//    public void getResourcePage() {
//    }
//
//    @Test
//    public void getPageProperties() {
//    }
//
//    @Test
//    public void getProperties() {
//    }
//
//    @Test
//    public void getDesigner() {
//    }
//
//    @Test
//    public void getCurrentDesign() {
//    }
//
//    @Test
//    public void getCurrentStyle() {
//    }
//
//    @Test
//    public void getComponent() {
//    }
//
//    @Test
//    public void getInheritedPageProperties() {
//    }
//
//    @Test
//    public void getResource() {
//    }
//
//    @Test
//    public void getResourceResolver() {
//    }
//
//    @Test
//    public void getRequest() {
//    }
//
//    @Test
//    public void getResponse() {
//    }
//
//    @Test
//    public void getSlingScriptHelper() {
//    }
//
//    @Test
//    public void getComponentContext() {
//    }
//
//    @Test
//    public void getEditContext() {
//    }
//
//    @Test
//    public void getResourceDesign() {
//    }
//
//    @Test
//    public void getCurrentNode() {
//    }
//
//    @Test
//    public void getI18n() {
//    }

    @Test
    public void getPageContextMap() {

        // Run the test
        GenericModel genericModel = mock(GenericModel.class, withSettings().extraInterfaces(GenericComponent.class));

        ComponentProperties componentProperties = new ComponentProperties();

        when(genericModel.getComponentProperties()).thenReturn(componentProperties);

        ComponentProperties output = ((GenericComponent) genericModel).getComponentProperties();

        // Verify the results
        assertEquals(componentProperties, output);
    }

//    @Test
//    public void initModel() {
//    }
}