package design.aem.components.list;

import com.day.cq.search.result.Hit;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import design.aem.components.ComponentProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;

public class HitBasedPageIterator extends AbstractPageIterator {
  private Iterator<Hit> hits;

  private static final Logger LOG = LoggerFactory.getLogger(ComponentProperties.class);

  public HitBasedPageIterator(PageManager pm, Iterator<Hit> hits, boolean avoidDuplicates, PageFilter pageFilter) {
    super();
    this.pm = pm;
    this.hits = hits;
    if (avoidDuplicates) {
      this.seen = new HashSet();
    }

    this.pageFilter = pageFilter;
    this.nextPage = this.seek();
  }

  protected Page seek() {
    while (this.hits != null && this.hits.hasNext()) {
      try {
        Hit hit = this.hits.next();

        Page page = this.pm.getContainingPage(hit.getPath());
        if (page != null && (this.seen == null || this.seen.add(page.getPath())) && (this.pageFilter == null || this.pageFilter.includes(page))) {
          return page;
        }
      } catch (Exception var2) {
        LOG.error("Could not get page behind search result hit", var2);
      }
    }

    return null;
  }
}
