package org.egov.nationaldashboardingest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.TimeZone;

/**
 * Entry point bootstrap class for the National Dashboard Ingest microservice.
 * <p>
 * Configures the Spring Boot application container, enabling:
 * <ul>
 *   <li>Kafka message consumption and production ({@link EnableKafka}) for event-driven bulk ingestion.</li>
 *   <li>Task scheduling ({@link EnableScheduling}) for background jobs and audit reconciliation.</li>
 *   <li>Tracer integration ({@link TracerConfiguration}) for distributed request tracing and logging.</li>
 *   <li>Customized {@link ObjectMapper} bean for Jackson JSON serialization with case-insensitivity.</li>
 *   <li>Permissive SSL trust context for internal cluster service communications.</li>
 * </ul>
 * </p>
 */
@SpringBootApplication
@EnableKafka
@EnableScheduling
@Import({ TracerConfiguration.class })
public class NationalDashboardIngestApplication {

	@Value("${app.timezone}")
	private String timeZone;

	/**
	 * Configures and registers a customized {@link ObjectMapper} bean.
	 * <p>
	 * Configured to:
	 * <ul>
	 *   <li>Accept case-insensitive properties (via {@link MapperFeature#ACCEPT_CASE_INSENSITIVE_PROPERTIES}).</li>
	 *   <li>Ignore unknown JSON properties without failing (via {@link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES}).</li>
	 *   <li>Align timestamps with the configured application timezone.</li>
	 * </ul>
	 * </p>
	 *
	 * @return pre-configured {@link ObjectMapper} instance
	 */
	@Bean
	public ObjectMapper objectMapper(){
		return new ObjectMapper()
				.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				.setTimeZone(TimeZone.getTimeZone(timeZone));
	}

	/**
	 * Configures the JVM default SSL context to trust self-signed SSL certificates
	 * and disables hostname verification for internal HTTPS communications.
	 * <p>
	 * Useful in non-production, staging, or internal Kubernetes mesh environments where
	 * internal services communicate over self-signed SSL/TLS endpoints.
	 * </p>
	 */
	public static void trustSelfSignedSSL() {
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			ctx.init(null, new TrustManager[]{tm}, null);
			SSLContext.setDefault(ctx);

			// Disable hostname verification
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
					return true;
				}
			});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Main application bootstrap method.
	 * <p>
	 * Initializes self-signed SSL trust settings and starts the Spring Application context.
	 * </p>
	 *
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		trustSelfSignedSSL();
		SpringApplication.run(NationalDashboardIngestApplication.class, args);
	}

}
