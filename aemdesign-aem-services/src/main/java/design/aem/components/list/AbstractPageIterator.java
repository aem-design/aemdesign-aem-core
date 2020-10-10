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


import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public abstract class AbstractPageIterator implements Iterator<Page> {
    protected PageManager pm;
    protected Page nextPage;
    protected Set<String> seen;
    protected PageFilter pageFilter;

    public AbstractPageIterator() {
    }

    public boolean hasNext() {
        return this.nextPage != null;
    }

    public Page next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        } else {
            Page page = this.nextPage;
            this.nextPage = this.seek();
            return page;
        }
    }

    protected abstract Page seek();

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
