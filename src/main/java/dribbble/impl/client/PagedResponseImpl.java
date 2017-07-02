package dribbble.impl.client;

import dribbble.api.PagedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PagedResponseImpl<T> implements PagedResponse<T> {
    private static final Logger logger = LoggerFactory.getLogger(PagedResponseImpl.class);
    private static final String LINK_HEADER = "Link";
    private ResponseEntity<Collection<T>> response;

    public PagedResponseImpl(ResponseEntity<Collection<T>> response) {
        this.response = response;
    }

    @Override
    public Collection<T> getEntries() {
        Collection<T> body = response.getBody();
        return body != null ? body : Collections.emptyList();
    }

    @Override
    public Optional<String> getNextPageUrl() {
        return getNextPageUrl(response.getHeaders().get(LINK_HEADER));
    }

    /**
     * header looks like this
     * <p>
     * Link: <https://api.dribbble.com/v1/user/followers?page=1&per_page=100>; rel="prev",
     * <https://api.dribbble.com/v1/user/followers?page=3&per_page=100>; rel="next"
     * <p>
     * could  be empty or without rel="next"
     **/
    protected Optional<String> getNextPageUrl(List<String> links) {
        if (links != null && !links.isEmpty()) {
            return links.stream().
                    filter(line -> line.matches(".*rel=\"next\"$")).
                    map(line -> {
                        Matcher matcher = Pattern.compile(".*<(.*)>").matcher(line);
                        if (matcher.find()) {
                            return matcher.group(1);
                        } else {
                            throw new IllegalStateException("Unable to parse Link header. Header value is:" + line);
                        }
                    }).
                    findFirst();
        }
        return Optional.empty();
    }

}
