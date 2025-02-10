package org.egov.asset.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.egov.tracer.config.TracerConfiguration;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.TimeZone;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
@Import({TracerConfiguration.class})
public class AssetConfiguration {

    @Value("${app.timezone}")
    private String timeZone;

    // MDMS Related Configurations
    @Value("${egov.mdms.host}")
    private String mdmsHost;

    @Value("${egov.mdms.search.endpoint}")
    private String mdmsEndPoint;

    // Idgen Config
    @Value("${egov.idgen.host}")
    private String idGenHost;

    @Value("${egov.idgen.path}")
    private String idGenPath;

    @Value("${egov.idgen.asset.applicationNum.name}")
    private String applicationNoIdgenName;

    @Value("${egov.idgen.asset.applicationNum.format}")
    private String applicationNoIdgenFormat;


    // Persister Config
    @Value("${persister.save.assetdetails.topic}")
    private String saveTopic;

    @Value("${persister.update.assetdetails.topic}")
    private String updateTopic;

    @Value("${persister.update.assetstatus.insystem.topics}")
    private String updateAssetStatusInSystem;


    @Value("${persister.save.assetassignment.topic}")
    private String saveAssignmentTopic;

    @Value("${persister.update.assetassignment.topic}")
    private String updateAssignmentTopic;

    @Value("${employee.allowed.search.params}")
    private String allowedEmployeeSearchParameters;

    @Value("${egov.asset.default.limit}")
    private Integer defaultLimit;

    @Value("${egov.asset.default.offset}")
    private Integer defaultOffset;

    @Value("${egov.asset.max.limit}")
    private Integer maxSearchLimit;

    @Value("${workflow.context.path}")
    private String wfHost;

    @Value("${workflow.transition.path}")
    private String wfTransitionPath;

    @Value("${workflow.businessservice.search.path}")
    private String wfBusinessServiceSearchPath;

    @Value("${workflow.process.path}")
    private String wfProcessPath;

    @Value("${asset.calculator.service.host}")
    private String assetCalculatorServiceHost;

    @Value("${asset.calculator.depreciation.calculate.api}")
    private String assetCalculatorDepreciationApi;

    @Value("${asset.calculator.depreciation.list.api}")
    private String assetCalculatorDepreciationListApi;

    @Value("${persister.save.asset.disposal.topic}")
    private String saveAssetDisposal;

    @Value("${persister.update.asset.disposal.topic}")
    private String updateAssetDisposal;

    @Value("${persister.save.asset.maintenance.topic}")
    private String saveAssetMaintenance;

    @Value("${persister.update.asset.maintenance.topic}")
    private String updateAssetMaintenance;


    @PostConstruct
    public void initialize() {
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    }

//    @Bean
//    public ObjectMapper objectMapper() {
//        return new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).setTimeZone(TimeZone.getTimeZone(timeZone));
//    }



    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setTimeZone(TimeZone.getTimeZone(timeZone));
        return objectMapper;
    }

    @Bean
    public MappingJackson2HttpMessageConverter jacksonConverter(ObjectMapper objectMapper) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        return converter;
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}