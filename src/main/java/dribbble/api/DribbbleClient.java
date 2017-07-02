package dribbble.api;

import dribbble.api.entities.Follower;
import dribbble.api.entities.Like;
import dribbble.api.entities.Shot;

import java.util.Iterator;


public interface DribbbleClient {
    Iterator<Follower> getFollowers(String pageUrl);

    Iterator<Shot> getShots(String pageUrl);

    Iterator<Like> getLikes(String pageUrl);

}

