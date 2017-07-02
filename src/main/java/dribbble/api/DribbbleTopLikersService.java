package dribbble.api;

import dribbble.api.entities.User;

import java.util.Collection;

public interface DribbbleTopLikersService {
    Collection<User> getTopLikers(String userId, int likersAmount);
}
