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

    /**
     * Creates a new wrapper around a given map.
     *
     * @param base wrapped object
     */
    public ComponentProperties(Map<String, Object> base) {
        super(base);
    }

    public Object get(String name) {
        return super.get(name);
    }

    /**
     * Created empty map
     */
    @SuppressWarnings("unchecked")
    public ComponentProperties() {
        super(new HashMap());
    }

    public AttrBuilder attr;

    @Override
    public void putAll(Map<? extends String, ?> map) {
        if (map != null) {
            super.putAll(map);
        }
    }
    /***
     * put map into existing map
     * @param map new entries
     * @param update update entries, skip if non blank value exist
     */
    public void putAll(Map<? extends String, ?> map, Boolean update) {
//        LOG.error("ComponentProperties.putAll");
        if (map != null) {
            if (update == null || !update) {
                //on null or not update do putAll
                super.putAll(map);
                return;
            }
            for (Entry<? extends String, ?> entry : map.entrySet()) {
                if (update) {
                    if (super.containsKey(entry.getKey())) {
//                        LOG.error("check update: " + entry.getKey() + ", n='" + entry.getValue().toString() + "', c='" + super.get(entry.getKey(), "") + "'");
                        //skip if non blank value exist
                        Object currentValue = super.get(entry.getKey());
                        Object newValue = entry.getValue();
                        if (currentValue != null && newValue != null) {
                            if (newValue.getClass().isArray()) {
                                // if newValue is empty don't do anything
                                if (ArrayUtils.getLength(newValue) == 0) {
//                                    LOG.error("skip update 1.1: " + entry.getKey() + " new value is not empty array");
                                    continue;
                                } else {
                                    //test if currentValue is not empty
                                    if (currentValue.getClass().isArray()) {
                                        //if its an empty array
                                        if (ArrayUtils.getLength(currentValue) != 0) {
                                            LOG.error("skip update 1.2: " + entry.getKey() + " current value it not empty array");
                                            continue;
                                        }
                                    }
//                                    else {
//                                        //itf its not empty value
//                                        if (StringUtils.isNotEmpty(currentValue.toString())) {
//                                            LOG.error("skip update 1.3: current value it not empty");
//                                            continue;
//                                        }
//                                    }
                                }
                            } else {
                                // if newValue is empty don't do anything
                                if (StringUtils.isEmpty(newValue.toString())) {
//                                    LOG.error("skip update: 2.1:" + entry.getKey() + " new value is empty");
                                    continue;
                                }
//                                else {
//                                    // if currentValue is not empty don't do anything
//                                    if (StringUtils.isNotEmpty(currentValue.toString())) {
//                                        LOG.error("skip update 2.2: current value it not empty and new value is empty");
//                                        continue;
//                                    }
//                                }
                            }
                        }
                    }
                }
//                LOG.error("update: " + entry.getKey() + "=" + entry.getValue() );

                super.put(entry.getKey(), entry.getValue());
            }
        }
    }
}
