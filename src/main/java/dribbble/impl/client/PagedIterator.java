package dribbble.impl.client;

import dribbble.api.PagedResponse;
import dribbble.api.PagedResponseProvider;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class PagedIterator<T> implements Iterator<T> {
    private String initialUrl;
    private PagedResponseProvider<T> pagesProvider;
    private PagedResponse<T> currentPagedResponse;
    private Iterator<T> iterator;
    private boolean initialized;

    public PagedIterator(String initialUrl, PagedResponseProvider<T> pagesProvider) {
        this.initialUrl = initialUrl;
        this.pagesProvider = pagesProvider;
    }

    @Override
    public boolean hasNext() {
        if (!initialized) {
            currentPagedResponse = pagesProvider.get(initialUrl);
            iterator = currentPagedResponse.getEntries().iterator();
            initialized = true;
        }
        return iterator.hasNext() || currentPagedResponse.getNextPageUrl().isPresent();
    }

    @Override
    public T next() {
        if (!iterator.hasNext() && currentPagedResponse.getNextPageUrl().isPresent()) {
            currentPagedResponse = pagesProvider.get(currentPagedResponse.getNextPageUrl().get());
            iterator = currentPagedResponse.getEntries().iterator();
        } else if (!hasNext()) {
            throw new NoSuchElementException();

        }
        return iterator.next();
    }
}
