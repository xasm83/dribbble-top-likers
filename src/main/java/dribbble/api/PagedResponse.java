package dribbble.api;

import java.util.Collection;
import java.util.Optional;

public interface PagedResponse<T> {
    Collection<T> getEntries();

    Optional<String>  getNextPageUrl();
}
