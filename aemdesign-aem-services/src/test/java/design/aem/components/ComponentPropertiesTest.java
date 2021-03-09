package design.aem.components;


import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ComponentPropertiesTest {


    private ComponentProperties componentProperties;

    @SuppressWarnings({"squid:S2699"})
    @Test
    public void ComponentPropertiesTest() {

        Map<String, String> sampleContent = new HashMap<String, String>();

        sampleContent.put("atest", "123");
        sampleContent.get("atest").equals("123");

        sampleContent.put("btest", "456");
        sampleContent.get("btest").equals("456");

        Map<String, String> sampleContentUpdate = new HashMap<String, String>();

        sampleContentUpdate.put("atest", "234");
        sampleContent.get("atest").equals("234");

        sampleContentUpdate.put("btest", "567");
        sampleContent.get("btest").equals("567");


        componentProperties = mock(ComponentProperties.class);
        componentProperties.put("test", "test1");

        when(componentProperties.get("test")).thenReturn("test1");

        componentProperties.putAll(sampleContent);

        when(componentProperties.get("atest")).thenReturn("123");
        when(componentProperties.get("btest")).thenReturn("456");

        componentProperties.putAll(sampleContentUpdate, true);

        when(componentProperties.get("atest")).thenReturn("123");
        when(componentProperties.get("btest")).thenReturn("456");

        componentProperties.putAll(sampleContentUpdate, false);

        when(componentProperties.get("atest")).thenReturn("234");
        when(componentProperties.get("btest")).thenReturn("567");

        assert componentProperties.get("btest").equals("567");
    }

}
