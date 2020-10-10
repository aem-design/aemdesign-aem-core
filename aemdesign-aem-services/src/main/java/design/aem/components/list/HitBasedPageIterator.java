/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 AEM.Design
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package design.aem.components.list;

import com.day.cq.search.result.Hit;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;

@SuppressWarnings("unchecked")
public class HitBasedPageIterator extends AbstractPageIterator {
    private final Iterator<Hit> hits;

    private static final Logger LOG = LoggerFactory.getLogger(HitBasedPageIterator.class);

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
