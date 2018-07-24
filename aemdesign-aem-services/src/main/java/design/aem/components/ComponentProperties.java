package design.aem.components;

import com.adobe.granite.ui.components.AttrBuilder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ComponentProperties extends ValueMapDecorator {

  private static final Logger LOG = LoggerFactory.getLogger(ComponentProperties.class);

  public AttrBuilder attr; //NOSONAR use simpler patter as a util

  /***
   * <p>Created empty map.</p>
   */
  @SuppressWarnings("unchecked")
  public ComponentProperties() {
    super(new HashMap());
  }

  /***
   * <p>Creates a new wrapper around a given map.</p>
   *
   * @param base wrapped object
   */
  public ComponentProperties(Map<String, Object> base) {
    super(base);
  }

  public Object get(String name) {
    return super.get(name);
  }

  @Override
  public void putAll(Map<? extends String, ?> map) {
    if (map != null) {
      super.putAll(map);
    }
  }

  /***
   * <p>put map into existing map.</p>
   * @param map new entries
   * @param update update entries, skip if non blank value exist
   */
  public void putAll(Map<? extends String, ?> map, Boolean update) { //NOSONAR some things need to
    // be complicated
    if (map != null) {
      if (update == null || !update) {
        //on null or not update do putAll
        super.putAll(map);
        return;
      }
      for (Entry<? extends String, ?> entry : map.entrySet()) {
        if (super.containsKey(entry.getKey())) {
          //skip if non blank value exist
          Object currentValue = super.get(entry.getKey());
          Object newValue = entry.getValue();
          if (currentValue != null && newValue != null) {
            if (newValue.getClass().isArray()) {
              // if newValue is empty don't do anything
              if (ArrayUtils.getLength(newValue) == 0) {
                continue;
              } else {
                //test if currentValue is not empty
                if (currentValue.getClass().isArray() && ArrayUtils.getLength(currentValue) != 0) {
                  //if its an empty array
                  LOG.warn("skip: " + entry.getKey() + " current value it not empty array");
                  continue;
                }
              }
            } else {
              // if newValue is empty don't do anything
              if (StringUtils.isEmpty(newValue.toString())) {
                continue;
              }
            }
          }
        }
        super.put(entry.getKey(), entry.getValue());
      }
    }
  }
}
