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
 * Spring configuration class responsible for creating and configuring
 * infrastructure beans required by the Load Generator application.
 *
 * <p>This configuration centralizes the creation of reusable application
 * components that are shared across the load generation framework. It
 * provisions the asynchronous task executor responsible for executing
 * concurrent load generation jobs and the {@link WebClient} used for
 * communicating with eGov services.
 *
 * <h3>Configured Beans</h3>
 * <ul>
 *   <li>{@code loadGeneratorExecutor} – Thread pool used for executing
 *       asynchronous load generation tasks.</li>
 *   <li>{@link WebClient} – Reactive HTTP client configured with the
 *       application's response timeout.</li>
 * </ul>
 *
 * <h3>Configuration Source</h3>
 * <p>All runtime configuration values such as thread pool size and HTTP
 * timeout are obtained from {@link LoadGeneratorConfig}, allowing the
 * application behavior to be modified through external configuration
 * without requiring code changes.
 *
 * <h3>Thread Safety</h3>
 * <p>The beans created by this configuration are managed as singleton
 * Spring beans and are designed to be safely shared across multiple
 * concurrent requests.
 *
 * @see LoadGeneratorConfig
 * @see ThreadPoolTaskExecutor
 * @see WebClient
 */
@Configuration
public class MainConfiguration {

    @Autowired
    private LoadGeneratorConfig config;

    /**
     * Creates the thread pool executor used by the Load Generator for
     * executing asynchronous tasks.
     *
     * <p>The executor is configured using values provided by
     * {@link LoadGeneratorConfig}. It supports concurrent execution of
     * request generation tasks, graceful application shutdown, and a
     * bounded task queue to efficiently process large batches of requests.
     *
     * <p>The maximum pool size is configured as twice the configured core
     * pool size to allow the executor to temporarily scale under higher
     * workloads.
     *
     * @return the configured {@link Executor} used for asynchronous
     *         load generation
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
     * Creates the application's shared {@link WebClient} instance.
     *
     * <p>The client is configured with a response timeout obtained from
     * {@link LoadGeneratorConfig}. This client is reused across the
     * application to perform non-blocking HTTP requests to various eGov
     * module APIs while ensuring requests do not wait indefinitely for
     * responses.
     *
     * @return the configured {@link WebClient} instance
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
