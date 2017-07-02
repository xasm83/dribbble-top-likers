package dribbble.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Shot {
    private String id;

    @JsonProperty("likes_url")
    private String likesUrl;

    public Shot() {
    }

    public Shot(String id, String likesUrl) {
        this.id = id;
        this.likesUrl = likesUrl;
    }

    public String getId() {
        return id;
    }

    public String getLikesUrl() {
        return likesUrl;
    }

    @Override
    public String toString() {
        return "Shot{" +
                "id='" + id + '\'' +
                ", likesUrl='" + likesUrl + '\'' +
                '}';
    }
}
