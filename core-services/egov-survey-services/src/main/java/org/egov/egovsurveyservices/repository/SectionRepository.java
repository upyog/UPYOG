package org.egov.egovsurveyservices.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.egovsurveyservices.repository.querybuilder.SectionQueryBuilder;
import org.egov.egovsurveyservices.web.models.Section;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class SectionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SectionQueryBuilder sectionQueryBuilder;

    public List<Section> getSectionsBySurveyId(String surveyId) {
        String sql = sectionQueryBuilder.getSectionBySurveyUuid();
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Section section = new Section();
            section.setUuid(rs.getString("uuid"));
            section.setWeightage(rs.getBigDecimal("weightage"));
            return section;
        }, surveyId);
    }

    public Section getSectionById(String sectionId) {
        String sql = sectionQueryBuilder.getSectionBySectionUuid();
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Section section = new Section();
            section.setUuid(rs.getString("uuid"));
            section.setWeightage(rs.getBigDecimal("weightage"));
            return section;
        }, sectionId);
    }
}