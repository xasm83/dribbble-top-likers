package dribbble.impl;

import dribbble.api.DribbbleClient;
import dribbble.api.entities.Follower;
import dribbble.api.entities.Like;
import dribbble.api.entities.Shot;
import dribbble.api.entities.User;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.*;


public class DribbleTopLikersServiceImplTest {
    private static final String USER_ID = "userId";
    private static final String REPLACED_ENDPOINT = "endpoint\\" + USER_ID;

    /*
         tests the whole RxJava flow, count of likers , verifies that all methods called
         each user's shot has the same likes list for testing simplicity
         liker11, liker21, liker22,  liker23 -  are top 4 likers
     */
    @Test
    public void shouldCalculateTopLikers() {
        Collection<Follower> followers = Arrays.asList(
                new Follower("1", new User("1", "name1", "shots1", "")),
                new Follower("2", new User("2", "name2", "shots2", "")),
                new Follower("3", new User("3", "name3", "shots3", ""))
        );

        Collection<Shot> shots1 = Arrays.asList(
                new Shot("11", "likes1"),
                new Shot("12", "likes1"),
                new Shot("13", "likes1"));

        Collection<Shot> shots2 = Arrays.asList(
                new Shot("12", "likes2"),
                new Shot("12", "likes2"));

        Collection<Shot> shots3 = Arrays.asList(new Shot("13", "likes3"));


        Collection<Like> likes1 = Arrays.asList(
                new Like("11", new User("11", "liker11", "", "")),
                new Like("12", new User("11", "liker11", "", "")),
                new Like("13", new User("11", "liker11", "", "")),
                new Like("14", new User("21", "liker21", "", "")));

        Collection<Like> likes2 = Arrays.asList(
                new Like("21", new User("21", "liker21", "", "")),
                new Like("22", new User("22", "liker22", "", "")),
                new Like("22", new User("22", "liker22", "", "")),
                new Like("23", new User("23", "liker23", "", "")),
                new Like("23", new User("23", "liker23", "", "")));


        Collection<Like> likes3 = Arrays.asList(
                new Like("31", new User("31", "liker31", "", "")));

        DribbbleClient client = mock(DribbbleClient.class);
        when(client.getFollowers(REPLACED_ENDPOINT)).thenReturn(followers.iterator());

        when(client.getShots("shots1")).thenReturn(shots1.iterator());
        when(client.getLikes("likes1")).thenReturn(likes1.iterator());

        when(client.getShots("shots2")).thenReturn(shots2.iterator());
        when(client.getLikes("likes2")).thenReturn(likes2.iterator());

        when(client.getShots("shots3")).thenReturn(shots3.iterator());
        when(client.getLikes("likes3")).thenReturn(likes3.iterator());

        DribbbleTopLikersServiceImpl service = new DribbbleTopLikersServiceImpl(client, "endpoint\\#USER_ID#");
        Collection<User> topLikers = service.getTopLikers(USER_ID, 4);

        assertArrayEquals(topLikers.stream().map(User::getName).toArray(),
                new String[]{"liker11", "liker21", "liker22", "liker23"});

        verify(client, times(1)).getFollowers(REPLACED_ENDPOINT);
        verify(client, times(3)).getShots(any());
        verify(client, times(6)).getLikes(any());
    }
}
