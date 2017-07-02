package dribbble.impl.client;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class PagedResponseTest {
    private static final String URL = "https://api.dribbble.com/v1/user/123/followers?page=3&per_page=100";

    @Test
    public void shouldReturnPageUrlWhenLinkHeader() {
        Optional<String> urlOptional = new PagedResponseImpl<Collection<String>>(null).
                getNextPageUrl(Arrays.asList("<https://api.dribbble.com/v1/user/followers?page=1&per_page=100>; rel=\"prev\"",
                        "<" + URL + ">; rel=\"next\""));
        assertEquals(urlOptional.get(), URL);
    }
}
