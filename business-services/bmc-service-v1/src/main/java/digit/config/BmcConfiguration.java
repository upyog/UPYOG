package digit.config;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;






@Configuration
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class BmcConfiguration {

    @Value("${app.timezone}")
    private String timeZone;

    @PostConstruct
    public void initialize() {
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    }

    @Bean
    @Autowired
    public MappingJackson2HttpMessageConverter jacksonConverter(ObjectMapper objectMapper) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        return converter;
    }

    // User Config
    @Value("${egov.user.host}")
    private String userHost;

    @Value("${egov.user.context.path}")
    private String userContextPath;

    @Value("${egov.user.create.path}")
    private String userCreateEndpoint;

    @Value("${egov.user.search.path}")
    private String userSearchEndpoint;

    @Value("${egov.user.update.path}")
    private String userUpdateEndpoint;

    // Idgen Config
    @Value("${egov.idgen.host}")
    private String idGenHost;

    @Value("${egov.idgen.path}")
    private String idGenPath;

    @Value("${egov.idgen.bmcid.format}")
    private String bmcIdGenFormat;

    @Value("${egov.idgen.bmcid.name}")
    private String bmcIdGenName;

    // Workflow Config
    @Value("${egov.workflow.host}")
    //@Value("http://sundeep.localhost.com")
	private String wfHost;

    @Value("${egov.workflow.transition.path}")
    //@Value("/egov-workflow-v2/egov-wf/process/_transition")
	private String wfTransitionPath;

    @Value("${egov.workflow.businessservice.search.path}")
    //@Value("/egov-workflow-v2/egov-wf/businessservice/_search")
	private String wfBusinessServiceSearchPath;

    @Value("${egov.workflow.processinstance.search.path}")
    //@Value("/egov-workflow-v2/egov-wf/process/_search")
	private String wfProcessInstanceSearchPath;

    @Value("${is.workflow.enabled}")
    private Boolean isWorkflowEnabled;

    // BMC Variables
    @Value("${bmc.kafka.create.topic}")
    private String createTopic;

    @Value("${bmc.kafka.update.topic}")
    private String updateTopic;

    @Value("${bmc.default.offset}")
    private Integer defaultOffset;

    @Value("${bmc.default.limit}")
    private Integer defaultLimit;

    @Value("${bmc.search.max.limit}")
    private Integer maxLimit;

    // MDMS
    @Value("${egov.mdms.host}")
    private String mdmsHost;

    @Value("${egov.mdms.search.endpoint}")
    private String mdmsEndPoint;

    // HRMS
    @Value("${egov.hrms.host}")
    private String hrmsHost;

    @Value("${egov.hrms.search.endpoint}")
    private String hrmsEndPoint;

    // URL Shortener
    @Value("${egov.url.shortner.host}")
    private String urlShortnerHost;

    @Value("${egov.url.shortner.endpoint}")
    private String urlShortnerEndpoint;

    // SMS Notification
    @Value("${egov.sms.notification.topic}")
    private String smsNotificationTopic;
}
