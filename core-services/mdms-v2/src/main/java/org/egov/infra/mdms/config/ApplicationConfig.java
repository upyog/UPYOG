package org.egov.infra.mdms.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.egov.common.utils.MultiStateInstanceUtil;
import org.springframework.context.annotation.Import;

@Configuration
@ToString
@Setter
@Getter
@Import({MultiStateInstanceUtil.class})
public class ApplicationConfig {

    @Value("${egov.mdms.schema.definition.save.topic}")
    private String saveSchemaDefinitionTopicName;

    @Value("${egov.mdms.data.save.topic}")
    private String saveMdmsDataTopicName;

    @Value("${egov.mdms.data.update.topic}")
    private String updateMdmsDataTopicName;

    @Value("${mdms.default.offset}")
    private Integer defaultOffset;

    @Value("${mdms.default.limit}")
    private Integer defaultLimit;

    @Value("${egov.mdms.data.delete.topic}")
    private String deleteMdmsDataTopicName;

    @Value("${egov.mdms.schema.delete.topic}")
    private String deleteSchemaTopicName;

    /**
 * Kafka topic name used for creating theme configuration.
 *
 * This topic is consumed by Persister to store
 * theme configuration in live table.
 */
    @Value("${egov.mdms.theme.config.save.topic}")
    private String saveThemeConfigTopicName;


/**
 * Kafka topic name used for creating theme configuration staging request.
 */
    @Value("${egov.mdms.theme.config.staging.update.topic}")
    private String updateThemeConfigStagingTopicName;
    @Value("${egov.mdms.theme.config.status.update.topic}")
    private String updateThemeConfigStatusTopicName;


    /**
     * Kafka topic name used for creating theme configuration staging request.
     */
    @Value("${egov.mdms.theme.config.staging.save.topic}")
    private String saveThemeConfigStagingTopicName;

    @Value("${workflow.host}")
    private String workflowHost;

    @Value("${theme.config.workflow.business.service}")
    private String themeConfigBusinessService;

    

}
