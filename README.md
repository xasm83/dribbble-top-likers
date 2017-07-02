Dribbble top likers scrapper, finds top likers among user's followers

Simple implementation of Dribbble API Java client with pagination, rate limits and retries support.
Multithreading is done using RxJava 2.0. Could be a good starting point to implement your own API SDK.

Runner finds and prints top X likers.

to build:
add your API key to application.properties: dribble.api.auth.header=Bearer PASTE_TOKEN_HERE 
mvn clean && install
cd target
java -jar xxx.jar 12345
where 12345 - dribbble user id

Multithreading example:

19:08:06.726 [pool-1-thread-1] DEBUG dribbble.impl.DribbbleTopLikersServiceImpl - Got a follower to process: User{id='3', name='name3', shotsUrl='shots3', followersUrl=''}
19:08:06.727 [pool-1-thread-3] DEBUG dribbble.impl.DribbbleTopLikersServiceImpl - Got a shot to process: Shot{id='11', likesUrl='likes1'}
19:08:06.727 [pool-1-thread-5] DEBUG dribbble.impl.DribbbleTopLikersServiceImpl - Got a shot to process: Shot{id='13', likesUrl='likes3'}
19:08:06.728 [pool-1-thread-4] DEBUG dribbble.impl.DribbbleTopLikersServiceImpl - Got a shot to process: Shot{id='12', likesUrl='likes2'}
19:08:06.728 [pool-1-thread-4] DEBUG dribbble.impl.DribbbleTopLikersServiceImpl - Got a shot to process: Shot{id='12', likesUrl='likes2'}
19:08:06.730 [pool-1-thread-10] DEBUG dribbble.impl.DribbbleTopLikersServiceImpl - Got a like to process: Like{id='21', user=User{id='21', name='liker21', shotsUrl='', followersUrl=''}}
19:08:06.730 [pool-1-thread-12] DEBUG dribbble.impl.DribbbleTopLikersServiceImpl - Got a like to process: Like{id='31', user=User{id='31', name='liker31', shotsUrl='', followersUrl=''}}
19:08:06.730 [pool-1-thread-10] DEBUG dribbble.impl.DribbbleTopLikersServiceImpl - Got a like ...
