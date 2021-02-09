package design.aem.reports;

import com.adobe.acs.commons.reports.api.ReportCellCSVExporter;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.components.ComponentManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static design.aem.reports.PageComponentsReportCellValue.getResourceChildrenComponents;
import static design.aem.utils.components.CommonUtil.RESOURCE_TYPE;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Model for rendering component paths in a page when exporting content to csv
 */
@Model(adaptables = Resource.class)
public class PageComponentsReportCellCSVExporter implements ReportCellCSVExporter {

    private static final Logger log = LoggerFactory.getLogger(PageComponentsReportCellCSVExporter.class);

    @Override
    public String getValue(Object result) {

        Resource resource = (Resource)result;

        log.debug("Finding components in {}", resource.getPath());
        HashMap components = new HashMap();

        if (resource != null && !ResourceUtil.isNonExistingResource(resource)) {

            Page page = resource.adaptTo(Page.class);
            Resource pageContent = page.getContentResource();

            components.putAll(getResourceChildrenComponents(pageContent, RESOURCE_TYPE));
        }

        log.debug("Found components: {}", components);

        return StringUtils.join(components.keySet(), "\r");

    }


}
