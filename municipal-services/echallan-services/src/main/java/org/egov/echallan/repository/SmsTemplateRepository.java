package org.egov.echallan.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.egov.echallan.model.SMSTemplate;
import org.egov.echallan.repository.builder.SmsTemplateQueryBuilder;
import org.egov.echallan.repository.rowmapper.SMSTemplateRowMapper;
import org.egov.tracer.model.ServiceCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class SmsTemplateRepository {

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private SMSTemplateRowMapper rowMapper;
	
	@Autowired
    private JdbcTemplate jdbcTemplate;

    private SmsTemplateQueryBuilder queryBuilder;
	

	public  List<SMSTemplate> GetTemplateData(String templateName) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getSmsTemplate(templateName,preparedStmtList);
       return  jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);

	}
}
