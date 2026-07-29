package org.egov.loadgenerator.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.Executor;

/**
 * Spring configuration for Load Generator infrastructure beans.
 *
 * <p>Configures the asynchronous task executor and the WebClient
 * used for executing concurrent API requests.</p>
 */
@Configuration
public class MainConfiguration {

    @Autowired
    private LoadGeneratorConfig config;

    /**
 * Creates the thread pool executor used for asynchronous
 * load generation tasks.
 *
 * @return configured task executor
 */
    @Bean(name = "loadGeneratorExecutor")
    public Executor loadGeneratorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(config.getThreadPoolSize());
        executor.setMaxPoolSize(config.getThreadPoolSize() * 2);
        executor.setQueueCapacity(10000);
        executor.setThreadNamePrefix("load-gen-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }
/**
 * Creates a WebClient with the configured response timeout
 * for communicating with eGov services.
 *
 * @return configured WebClient instance
 */
    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(config.getWebClientTimeoutSeconds()));
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
