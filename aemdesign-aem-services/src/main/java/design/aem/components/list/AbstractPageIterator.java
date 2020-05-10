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
