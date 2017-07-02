package dribbble;

import com.google.common.base.Strings;
import dribbble.api.DribbbleTopLikersService;
import dribbble.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;

@EnableRetry
@SpringBootApplication()
public class DribbbleTopLikersCommandLineRunner implements CommandLineRunner {
    private static Logger logger = LoggerFactory.getLogger(DribbbleTopLikersCommandLineRunner.class);

    private static int ERROR_EXIT_CODE = -1;
    private static int LIKERS_AMOUNT = 10;

    @Autowired
    private DribbbleTopLikersService topLikersService;

    @Autowired
    private ApplicationContext appContext;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }


    public static void main(String[] args) throws Exception {
        SpringApplication.run(DribbbleTopLikersCommandLineRunner.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length != 1) {
            exitWithError();
        }

        String userId = args[0];
        if (Strings.isNullOrEmpty(userId.trim())) {
            exitWithError();
        }
        Collection<User> likerNames = topLikersService.getTopLikers(userId, LIKERS_AMOUNT);
        logger.info("Top likers of {} user", userId);
        likerNames.forEach(user -> logger.info(user.getName()));
    }

    private void exitWithError() {
        logger.error("Invalid argument number. Usage java -jar xxx.jar username");
        SpringApplication.exit(appContext, () -> ERROR_EXIT_CODE);
        System.exit(ERROR_EXIT_CODE);
    }
}