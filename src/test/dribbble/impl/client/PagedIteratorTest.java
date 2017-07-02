package dribbble.impl.client;

import dribbble.api.PagedResponse;
import dribbble.api.PagedResponseProvider;
import dribbble.api.entities.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class PagedIteratorTest {
    private static String URL = "url";
    private static String URL_NEXT = "urlNext";

    //annotation is needed for generics
    @Mock
    private PagedResponseProvider<User> providerMock;

    @Mock
    private PagedResponse<User> responseMock;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldGetNextPageWhenMultiplePages() {

        when(providerMock.get(URL)).thenReturn(responseMock);
        when(providerMock.get(URL_NEXT)).thenReturn(responseMock);

        when(responseMock.getEntries()).thenReturn(Collections.singleton(new User()));
        when(responseMock.getNextPageUrl()).thenReturn(Optional.of(URL_NEXT));

        PagedIterator<?> iterator = new PagedIterator<>(URL, providerMock);
        iterator.hasNext();
        iterator.next();
        verify(providerMock, times(1)).get(URL);
        iterator.next();
        verify(responseMock, times(2)).getNextPageUrl();
        verify(providerMock, times(1)).get(URL_NEXT);
        verify(responseMock, times(2)).getEntries();
    }
}
