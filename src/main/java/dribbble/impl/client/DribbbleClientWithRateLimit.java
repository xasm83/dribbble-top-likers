package dribbble.impl.client;

import com.google.common.util.concurrent.RateLimiter;
import dribbble.api.DribbbleClient;
import dribbble.api.PagedResponse;
import dribbble.api.entities.Follower;
import dribbble.api.entities.Like;
import dribbble.api.entities.Shot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Iterator;

/**
 * Synchronous client which provides paged operations. Inspired by existing  AWS, Facebook etc clients for collections retrieval.
 * Uses Guava's Rate limiter to handle Dribbble rate limitations.
 */
@Component
public class DribbbleClientWithRateLimit implements DribbbleClient {
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final Logger logger = LoggerFactory.getLogger(DribbbleClient.class);

    //    0.9 requests per second as per dribbble request rate limitation
    private final RateLimiter rateLimiter = RateLimiter.create(0.95);
    private RestTemplate restTemplate;
    private String apiAuthHeader;
    private String authHeaderName;

    @Autowired
    public DribbbleClientWithRateLimit(RestTemplate restTemplate,
                                       @Value("${dribble.api.auth.header}") String apiAuthHeader,
                                       @Value("${dribble.api.auth.header.name}") String authHeaderName) {
        this.restTemplate = restTemplate;
        this.apiAuthHeader = apiAuthHeader;
        this.authHeaderName = authHeaderName;
    }

    @Retryable(maxAttempts = MAX_RETRY_ATTEMPTS)
    public Iterator<Follower> getFollowers(String pageUrl) {
        ParameterizedTypeReference<Collection<Follower>> typeToken =
                new ParameterizedTypeReference<Collection<Follower>>() {
                };

        return new PagedIterator<>(pageUrl,
                new PagedResponseProvider<>(typeToken));
    }

    @Retryable(maxAttempts = MAX_RETRY_ATTEMPTS)
    public Iterator<Shot> getShots(String pageUrl) {
        ParameterizedTypeReference<Collection<Shot>> typeToken =
                new ParameterizedTypeReference<Collection<Shot>>() {
                };

        return new PagedIterator<>(pageUrl,
                new PagedResponseProvider<>(typeToken));
    }

    @Retryable(maxAttempts = MAX_RETRY_ATTEMPTS)
    public Iterator<Like> getLikes(String pageUrl) {
        ParameterizedTypeReference<Collection<Like>> typeToken =
                new ParameterizedTypeReference<Collection<Like>>() {
                };

        return new PagedIterator<>(pageUrl,
                new PagedResponseProvider<>(typeToken));
    }

    private HttpEntity<String> getRequestEntityWithAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(authHeaderName, apiAuthHeader);
        return new HttpEntity<>(headers);
    }

    private class PagedResponseProvider<T> implements dribbble.api.PagedResponseProvider<T> {
        private ParameterizedTypeReference<Collection<T>> typeToken;

        public PagedResponseProvider(ParameterizedTypeReference<Collection<T>> typeToken) {
            this.typeToken = typeToken;
        }

        public PagedResponse<T> get(String url) {
            rateLimiter.acquire();

            //getForEntity does not work with generic collections as an entity type
            ResponseEntity<Collection<T>> response = restTemplate.exchange(url,
                    HttpMethod.GET,
                    getRequestEntityWithAuthHeaders(),
                    typeToken
            );
            return new PagedResponseImpl<>(response);
        }

    }
}
