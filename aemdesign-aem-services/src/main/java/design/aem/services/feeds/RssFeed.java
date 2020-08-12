package design.aem.services.feed;

import org.apache.sling.api.SlingHttpServletResponse;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import java.io.IOException;

@Component(
    name = "aem.design.rss.feed",
    service = Servlet.class,
    property = {
        "service.description=RSS servlet that provides a feed for lists",
        "sling.servlet.methods=GET",
        "sling.servlet.resourceTypes=aemdesign/components/lists/list",
        "sling.servlet.extensions=xml",
        "sling.servlet.selectors=rss",
    })
public class RssFeed extends FeedService {
    @Override
    protected boolean feedMatchesRequest(String feedType) {
        return super.feedMatchesRequest(feedType) &&
            Boolean.FALSE.equals(getStyle("disableFeedTypeRSS", false));
    }

    @Override
    protected void handleResponse(SlingHttpServletResponse slingResponse) throws IOException {
        slingResponse.setContentType("application/xml");

        StringBuilder rss = new StringBuilder();

        String feedTitle = "RSS feed for list";
        String feedUrl = resource.getPath();

        rss.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        rss.append("<rss xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:sy=\"http://purl.org/rss/1.0/modules/syndication/\" xmlns:admin=\"http://webns.net/mvcb/\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:media=\"http://search.yahoo.com/mrss/\" xmlns:content=\"http://purl.org/rss/1.0/modules/content/\" version=\"2.0\">");
        rss.append("<channel>");
        rss.append(String.format("<title>%s</title>", feedTitle));
        rss.append(String.format("<link>%s</link>", feedUrl));
        rss.append("</channel>");
        rss.append("</rss>");

        slingResponse.getWriter().write(rss.toString());
    }
}
