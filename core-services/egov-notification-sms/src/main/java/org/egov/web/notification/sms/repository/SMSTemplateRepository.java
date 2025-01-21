package org.egov.web.notification.sms.repository;

import java.util.ArrayList;
import java.util.List;

import org.egov.web.notification.sms.models.SMSTemplate;
import org.egov.web.notification.sms.models.SMSTemplateSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class SMSTemplateRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private SMSTemplateRowMapper rowMapper;

	public List<SMSTemplate> fetchSmsTemplate(SMSTemplateSearchCriteria smsTemplateSearchCriteria) {
		List<Object> params = new ArrayList<>();
		String query = SMSTemplateQueryBuilder.getSmsTemplate(smsTemplateSearchCriteria, params);
		log.debug(query);
		return jdbcTemplate.query(query, params.toArray(), rowMapper);
	}

}
