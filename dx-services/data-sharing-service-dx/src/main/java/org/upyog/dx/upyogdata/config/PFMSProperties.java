package org.upyog.dx.upyogdata.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds {@code pfms.*} application properties for outbound PFMS HTTP and schedulers.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "pfms")
public class PFMSProperties {

    private final Auth auth = new Auth();
    private final Data data = new Data();
    private final Client client = new Client();
    private final Scheduler scheduler = new Scheduler();
    private final Retry retry = new Retry();

    @Getter
    @Setter
    public static class Auth {
        private String url;
        private String username;
        private String password;
    }

    @Getter
    @Setter
    public static class Data {
        private String url;
    }

    @Getter
    @Setter
    public static class Client {
        private String ip;
    }

    @Getter
    @Setter
    public static class Scheduler {
        private String cron;
        private int batchSize = 50;
    }

    @Getter
    @Setter
    public static class Retry {
        private final Scheduler scheduler = new Scheduler();
    }
}
