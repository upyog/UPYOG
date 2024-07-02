package org.egov.wf.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.TimeZone;


@Import({TracerConfiguration.class})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
public class SmsConfig {



    @Value("${app.timezone}")
    private String timeZone;

    @PostConstruct
    public void initialize() {
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    }

//    @Bean
//    @Autowired
//    public MappingJackson2HttpMessageConverter jacksonConverter(ObjectMapper objectMapper) {
//        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//        converter.setObjectMapper(objectMapper);
//        return converter;
//    }

    //HRMS
    @Value("${egov.hrms.host}")
    private String hrmsHost;

    @Value("${egov.hrms.search.endpoint}")
    private String hrmsEndPoint;

    //Notification

    @Value("${notification.sms.enabled}")
    private Boolean isSMSEnabled;

    @Value("${egov.localization.statelevel}")
    private Boolean isLocalizationStateLevel;

    @Value("${egov.localization.host}")
    private String localizationHost;

    @Value("${egov.localization.context.path}")
    private String localizationContextPath;

    @Value("${egov.localization.search.endpoint}")
    private String localizationSearchEndpoint;

	// SMS
	@Value("${kafka.topics.notification.sms}")
	private String smsNotifTopic;


}
