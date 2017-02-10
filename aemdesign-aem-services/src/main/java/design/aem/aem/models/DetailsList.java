package design.aem.aem.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yawly on 17/05/2016.
 */
public class DetailsList {

    private final String property;

    private final String detailsPath;

    private final Resource resource;

    public DetailsList(Resource resource, String property, String detailsPath) {
        this.property = property;
        this.detailsPath = detailsPath;
        this.resource = resource;
    }

    public <T> List<T> toList(Class<T> cls) {

        String[] detailsProperty = resource.getValueMap().get(this.property, String[].class);

        List<T> details = new ArrayList<T>(detailsProperty.length);

        if(detailsProperty != null) {
            PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);

            for (String path : detailsProperty) {
                Page personPage = pageManager.getPage(path);

                if (personPage != null) {
                    Resource detailsResource = personPage.getContentResource(detailsPath);

                    if (detailsResource != null) {
                        T model = detailsResource.adaptTo(cls);

                        if (model != null) {
                            details.add(model);
                        }
                    }
                }
            }
        }

        return details;
    }


}
