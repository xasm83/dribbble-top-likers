package dribbble.api;

public interface PagedResponseProvider<T> {
    PagedResponse<T> get(String url);
}
