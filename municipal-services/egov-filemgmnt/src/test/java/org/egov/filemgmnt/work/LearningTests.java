package org.egov.filemgmnt.work;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.egov.filemgmnt.TestConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import lombok.extern.slf4j.Slf4j;

@Disabled
@SpringBootTest
@Import(TestConfig.class)
@TestPropertySource(locations = { "classpath:test.properties" })
@SuppressWarnings({ "PMD.JUnitTestsShouldIncludeAssert" })
@Slf4j
class LearningTests {

    @Test
    void convertDateToLong() {
        LocalDateTime localDateTime = LocalDateTime.of(2022, 11, 13, 19, 10, 05);
        long localDateTimeMillis = localDateTime.atZone(ZoneId.systemDefault())
                                                .toInstant()
                                                .toEpochMilli();
        log.info("localDate={}, milliseconds={}", localDateTime, localDateTimeMillis);

//        java.util.Date date = new java.util.Date(localDateTimeMillis);
//        long dateMillis = date.getTime();
//        log.info("date={}, milliseconds={}", date, dateMillis);
    }

    @Test
    void dateToLongTest() {
        java.util.Date now = new java.util.Date();
        long millis = now.getTime();
        log.info("now={}, milliseconds={}", now, millis);

        java.util.Date date = new java.util.Date(millis);
        long dateMillis = date.getTime();
        log.info("date={}, milliseconds={}", date, dateMillis);

        LocalDateTime localDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
        long localDateMillis = localDate.atZone(ZoneId.systemDefault())
                                        .toInstant()
                                        .toEpochMilli();
        log.info("localDate={}, milliseconds={}", localDate, localDateMillis);
    }

    @Disabled
    @Test
    void testSms() {
        try {
            SSLContext ctx = SSLContext.getInstance("TLSv1.2");
            try (InputStream is = getClass().getClassLoader()
                                            .getResourceAsStream("smsgwsmsgovin-sep22.cer")) {

                CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                X509Certificate caCert = (X509Certificate) certFactory.generateCertificate(is);

                StringBuilder ccBuf = new StringBuilder();
                ccBuf.append("Type: ")
                     .append(caCert.getType());
                ccBuf.append("\nHashcode: ")
                     .append(caCert.hashCode());
                ccBuf.append("\nFormat: ")
                     .append(caCert.getPublicKey()
                                   .getFormat());
                ccBuf.append("\nAlgorithm: ")
                     .append(caCert.getPublicKey()
                                   .getAlgorithm());

                log.info("*** Client Certificate:: \n{}", ccBuf.toString());

                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null);
                trustStore.setCertificateEntry("caCert", caCert);

                TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustFactory.init(trustStore);

                TrustManager[] trustManagers = trustFactory.getTrustManagers();
                ctx.init(null, trustManagers, null);
            }

            StringBuilder query = new StringBuilder();
            query.append("username=ikmlsg.sms");
            query.append("&pin=GHt@#321ter");
            query.append("&message=testmsg");
            query.append("&mnumber=919446903827");
            query.append("&signature=IKMLSG");
            query.append("&dlt_entity_id=1701159193290176741");
            query.append("&dlt_template_id=1");

            HttpsURLConnection conn = (HttpsURLConnection) new URL(
                    "https://smsgw.sms.gov.in/failsafe/MLink?" + query.toString()).openConnection();
            conn.setSSLSocketFactory(ctx.getSocketFactory());
            conn.setRequestMethod("POST");
            conn.setDoInput(true);

            try (BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                log.info("*** SMS Status: {} {}", conn.getResponseCode(), conn.getResponseMessage());

                StringBuilder buf = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    buf.append(line);
                }
                log.info("*** HttpsURLConnection: \n{}", conn.toString());

                Certificate[] serverCerts = conn.getServerCertificates();
                for (Certificate cert : serverCerts) {
                    StringBuilder cBuf = new StringBuilder();
                    cBuf.append("Type: ")
                        .append(cert.getType());
                    cBuf.append("\nHashcode: ")
                        .append(cert.hashCode());
                    cBuf.append("\nFormat: ")
                        .append(cert.getPublicKey()
                                    .getFormat());
                    cBuf.append("\nAlgorithm: ")
                        .append(cert.getPublicKey()
                                    .getAlgorithm());

                    log.info("*** Server Certificates:: \n{}", cBuf.toString());
                }
            }

            conn.disconnect();
        } catch (Exception e) {
            log.error("HttpsURLConnection failed with error", e);
        }
    }
}
