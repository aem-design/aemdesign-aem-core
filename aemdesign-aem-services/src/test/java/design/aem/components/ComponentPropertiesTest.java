package design.aem.components;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ComponentPropertiesTest {


  private ComponentProperties componentProperties;

  @Test
  public void ComponentPropertiesTest() {

    Map<String, String> sampleContent = new HashMap<String, String>();

    sampleContent.put("atest","123");
    sampleContent.put("btest","456");

    Map<String, String> sampleContentUpdate = new HashMap<String, String>();

    sampleContentUpdate.put("atest","234");
    sampleContentUpdate.put("btest","567");

    componentProperties = mock(ComponentProperties.class);
    componentProperties.put("test","test1");

    when(componentProperties.get("test")).thenReturn("test1");

    componentProperties.putAll(sampleContent);

    when(componentProperties.get("atest")).thenReturn("123");
    when(componentProperties.get("btest")).thenReturn("456");

    componentProperties.putAll(sampleContentUpdate,true);

    when(componentProperties.get("atest")).thenReturn("123");
    when(componentProperties.get("btest")).thenReturn("456");

    componentProperties.putAll(sampleContentUpdate,false);

    when(componentProperties.get("atest")).thenReturn("234");
    when(componentProperties.get("btest")).thenReturn("567");
  }

}
