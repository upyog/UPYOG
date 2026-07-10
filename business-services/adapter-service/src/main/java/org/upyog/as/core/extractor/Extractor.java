package org.upyog.as.core.extractor;

import java.time.LocalDate;

import org.springframework.jdbc.core.JdbcTemplate;
import org.upyog.as.extractor.record.PTRawData;

public interface Extractor {

	PTRawData extract(JdbcTemplate jdbc, String tenantId, LocalDate date);

}
