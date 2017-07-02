package dribbble.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String id;
    private String name;

    @JsonProperty("shots_url")
    private String shotsUrl;

    @JsonProperty("followers_url")
    private String followersUrl;

    public User() {
    }

    public User(String id, String name, String shotsUrl, String followersUrl) {
        this.id = id;
        this.name = name;
        this.shotsUrl = shotsUrl;
        this.followersUrl = followersUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getShotsUrl() {
        return shotsUrl;
    }

    public String getFollowersUrl() {
        return followersUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id != null ? id.equals(user.id) : user.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", shotsUrl='" + shotsUrl + '\'' +
                ", followersUrl='" + followersUrl + '\'' +
                '}';
    }
}
