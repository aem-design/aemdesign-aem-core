package design.aem.reports;

import com.day.cq.wcm.api.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import static design.aem.reports.PageComponentsReportCellValue.getResourceChildrenComponents;
import static design.aem.utils.components.CommonUtil.RESOURCE_TYPE;

/**
 * Model for rendering component paths in a page when exporting content to csv
 */
@Model(adaptables = Resource.class)
public class PageComponentsReportCellCSVExporter  {

    private static final Logger log = LoggerFactory.getLogger(PageComponentsReportCellCSVExporter.class);

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
