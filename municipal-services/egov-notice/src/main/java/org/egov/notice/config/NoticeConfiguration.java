package org.egov.notice.config;

import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
public class NoticeConfiguration {

	@Value("${app.timezone}")
	private String timeZone;

	@PostConstruct
	public void initialize() {
		TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
	}

	@Value("${egov.idgen.host}")
	private String idGenHost;

	@Value("${egov.idgen.path}")
	private String idGenPath;

	@Value("${egov.idgen.ack.name}")
	private String ackIdGenName;

	@Value("${egov.idgen.ack.format}")
	private String ackIdGenFormat;
	
	@Value("${egov.idgen.notice.name}")
	private String noticeidname;
	
	@Value("${egov.idgen.notice.format}")
	private String noticeformat;
	
	@Value("${persister.save.notice.topic}")
	private String savenoticetopic;
	
	@Value("${nt.search.pagination.default.limit}")
	private long defaultLimit;
	
	@Value("${nt.search.pagination.default.offset}")
    private Long defaultOffset;
    
    @Value("${nt.search.pagination.max.search.limit}")
    private Long maxSearchLimit;
	
}
