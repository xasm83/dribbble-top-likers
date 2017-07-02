package dribbble.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import dribbble.api.DribbbleClient;
import dribbble.api.DribbbleTopLikersService;
import dribbble.api.entities.Follower;
import dribbble.api.entities.Like;
import dribbble.api.entities.Shot;
import dribbble.api.entities.User;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class DribbbleTopLikersServiceImpl implements DribbbleTopLikersService {
    private static final Logger logger = LoggerFactory.getLogger(DribbbleTopLikersServiceImpl.class);
    private static final int AMOUNT_OF_THREADS = 20;
    private static final int FLAT_MAP_PARALLELISM_LEVEL = 10;
    private final ExecutorService executor = Executors.newFixedThreadPool(AMOUNT_OF_THREADS);
    private DribbbleClient dribbbleClient;
    private String userEndpoint;

    @Autowired
    public DribbbleTopLikersServiceImpl(DribbbleClient dribbbleClient,
                                        @Value("${dribble.api.followers.endpoint}") String userEndpoint) {
        this.dribbbleClient = dribbbleClient;
        this.userEndpoint = userEndpoint;
    }

    public Collection<User> getTopLikers(String userId, int likersAmount) {
        Map<User, AtomicInteger> likersMap = new ConcurrentHashMap<>();

        Observable.<User>create(emitter -> {
            Iterator<Follower> followerIterator = dribbbleClient.getFollowers(userEndpoint.replaceFirst("#USER_ID#", userId));
            while (followerIterator.hasNext()) {
                User user = followerIterator.next().getUser();
                logger.debug("Got a follower to process: {}", user);
                String shotsUrl = user.getShotsUrl();
                if (!Strings.isNullOrEmpty(shotsUrl)) {
                    emitter.onNext(user);
                }
            }
            emitter.onComplete();
        }).observeOn(Schedulers.from(executor)).subscribeOn(Schedulers.from(executor)).
                flatMap(user -> Observable.<Shot>create(emitter -> {
                            Iterator<Shot> shotIterator = dribbbleClient.getShots(user.getShotsUrl());
                            while (shotIterator.hasNext()) {
                                Shot shot = shotIterator.next();
                                logger.debug("Got a shot to process: {}", shot);
                                String likesUrl = shot.getLikesUrl();
                                if (!Strings.isNullOrEmpty(likesUrl)) {
                                    emitter.onNext(shot);
                                }
                            }
                            emitter.onComplete();
                        }).observeOn(Schedulers.from(executor)).subscribeOn(Schedulers.from(executor)),
                        FLAT_MAP_PARALLELISM_LEVEL
                ).
                flatMap(shot -> Observable.<Like>create(emitter -> {
                            Iterator<Like> likersIterator = dribbbleClient.getLikes(shot.getLikesUrl());
                            while (likersIterator.hasNext()) {
                                Like like = likersIterator.next();
                                logger.debug("Got a like to process: {}", like);
                                emitter.onNext(like);
                            }
                            emitter.onComplete();
                        }).observeOn(Schedulers.from(executor)).subscribeOn(Schedulers.from(executor)),
                        FLAT_MAP_PARALLELISM_LEVEL
                ).
                blockingForEach(like -> {
                    logger.debug("Got a like to process: {}", like);
                    AtomicInteger count = likersMap.putIfAbsent(like.getUser(), new AtomicInteger(1));
                    if (count != null) {
                        count.incrementAndGet();
                    }
                });
        executor.shutdown();
        return getTopLikers(likersMap, likersAmount);
    }

    private Collection<User> getTopLikers(Map<User, AtomicInteger> likersMap, int likersAmount) {
        Multimap<Integer, User> myMultimap = TreeMultimap.create(Ordering.natural().reverse(),
                Comparator.comparing(User::getName));

        //could have been a parallel stream but did not want to clutter the code with Concurrent* classes
        // and custom impl of multimap
        likersMap.entrySet().forEach(entry -> myMultimap.put(entry.getValue().get(), entry.getKey()));
        return myMultimap.entries().stream().
                limit(likersAmount).
                map(Map.Entry::getValue).
                collect(Collectors.toList());
    }
}
