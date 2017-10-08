package design.aem.components;

import com.adobe.granite.ui.components.AttrBuilder;
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
        if (map != null) {
            if (update == null || !update) {
                //on null or not update do putAll
                super.putAll(map);
                return;
            }
            for (Entry<? extends String, ?> entry : map.entrySet()) {
                if (update) {
                    if (super.containsKey(entry.getKey())) {
                        //System.out.println("update: " + entry.getKey() + ", [" + entry.getValue().toString() + "],[" + super.get(entry.getKey(), "") + "]");
                        //skip if non blank value exist
                        if (StringUtils.isNotBlank(super.get(entry.getKey(), ""))) {
                            continue;
                        }
                    }
                }
                super.put(entry.getKey(), entry.getValue());
            }
        }
    }
}
