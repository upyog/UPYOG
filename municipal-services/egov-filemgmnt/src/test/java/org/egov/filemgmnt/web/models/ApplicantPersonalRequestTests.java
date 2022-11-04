package org.egov.filemgmnt.web.models;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.filemgmnt.TestConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

//@Disabled
@SpringBootTest
@Import(TestConfig.class)
@TestPropertySource(locations = { "classpath:test.properties" })
@SuppressWarnings({ "PMD.JUnitTestsShouldIncludeAssert" })
@Slf4j
class ApplicantPersonalRequestTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void requestJson() {
        ApplicantPersonalRequest request = ApplicantPersonalRequest.builder()
                                                                   .requestInfo(RequestInfo.builder()
                                                                                           .userInfo(User.builder()
                                                                                                         .uuid(UUID.randomUUID()
                                                                                                                   .toString())
                                                                                                         .build())
                                                                                           .build())
                                                                   .build();
        request.addApplicantPersonal(ApplicantPersonal.builder()
                                                      .id(UUID.randomUUID()
                                                              .toString())
                                                      .firstName("FirstName")
                                                      .auditDetails(new AuditDetails())
                                                      .build());
        try {
            log.info(" *** APPLICANT PERSONAL JSON \n {}",
                     objectMapper.writerWithDefaultPrettyPrinter()
                                 .writeValueAsString(request));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
    }

    void convertDateToLong() {

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
