package design.aem.utils.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.apache.commons.lang3.StringUtils.join;

public class ThemeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThemeUtil.class);

    /**
     * compile css classes.
     * @param classes
     * @return
     */
    public static String addClasses(String... classes) {
        //don't return blank space and remove double spaces
        String sreturn = join(classes, " ").trim().replace("  "," ");
        if ("".equals(sreturn)) {
            return "";
        }
        return sreturn;
    }
}
