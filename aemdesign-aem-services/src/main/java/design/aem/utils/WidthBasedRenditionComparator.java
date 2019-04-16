package design.aem.utils;

import com.adobe.granite.asset.api.Rendition;

import java.util.Comparator;

import static design.aem.utils.components.ImagesUtil.getWidth;

/***
 * allow asset rendition compare by width
 */
public class WidthBasedRenditionComparator implements Comparator<Rendition>  {
    public int compare(com.adobe.granite.asset.api.Rendition r1, com.adobe.granite.asset.api.Rendition r2)
    {
        int w1 = getWidth(r1);
        int w2 = getWidth(r2);
        if (w1 < w2) {
            return -1;
        }
        if (w1 == w2) {
            return 0;
        }
        return 1;
    }
}
