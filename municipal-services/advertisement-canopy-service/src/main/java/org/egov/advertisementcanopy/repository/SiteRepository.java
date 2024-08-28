package org.egov.advertisementcanopy.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.egov.advertisementcanopy.model.SiteCreationData;
import org.egov.advertisementcanopy.model.SiteUpdateRequest;
import org.egov.advertisementcanopy.model.SiteUpdationData;
import org.egov.advertisementcanopy.repository.builder.SiteApplicationQueryBuilder;
import org.egov.advertisementcanopy.repository.rowmapper.SiteApplicationRowMapper;
import org.egov.advertisementcanopy.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class SiteRepository {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	SiteService siteService;

	@Autowired
	SiteApplicationRowMapper siteApplicationRowMapper;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	private SiteApplicationQueryBuilder queryBuilder;
	public static final String SELECT_NEXT_SEQUENCE = "select nextval('seq_id_eg_site_application')";
	public static final String SELECT_NEXT_SITE_SEQUENCE = "select nextval('seq_id_eg_site_type')";

	private Long getNextSequence() {
		return jdbcTemplate.queryForObject(SELECT_NEXT_SEQUENCE, Long.class);
	}

	private Long getNextSiteSequence() {
		return jdbcTemplate.queryForObject(SELECT_NEXT_SITE_SEQUENCE, Long.class);
	}

	public void create(SiteCreationData siteCreation) {
		siteCreation.setId(getNextSequence());
		if (siteCreation.getSiteType().equals(siteService.ADVERTISEMENT_HOARDING)) {
			siteCreation.setSiteID("AHS" + "/" + siteCreation.getUlbName() + "/" + getNextSiteSequence());
		}
		if (siteCreation.getSiteType().equals(siteService.CANOPY)) {
			siteCreation.setSiteID("ACS" + "/" + siteCreation.getUlbName() + "/" + getNextSiteSequence());
		}
		jdbcTemplate.update(queryBuilder.CREATE_QUERY, siteCreation.getId(), siteCreation.getUuid(),
				siteCreation.getSiteID(), siteCreation.getSiteName(), siteCreation.getSiteDescription(),
				siteCreation.getGpsLocation(), siteCreation.getSiteAddress(), siteCreation.getSiteCost(),
				siteCreation.getSitePhotograph(), siteCreation.getStructure(), siteCreation.getSizeLength(),
				siteCreation.getSizeWidth(), siteCreation.getLedSelection(), siteCreation.getSecurityAmount(),
				siteCreation.getPowered(), siteCreation.getOthers(), siteCreation.getDistrictName(),
				siteCreation.getUlbName(), siteCreation.getUlbType(), siteCreation.getWardNumber(),
				siteCreation.getPinCode(), siteCreation.getAdditionalDetail(),
				siteCreation.getAuditDetails().getCreatedBy(), siteCreation.getAuditDetails().getCreatedDate(),
				siteCreation.getAuditDetails().getLastModifiedBy(),
				siteCreation.getAuditDetails().getLastModifiedDate(), siteCreation.getSiteType(),
				siteCreation.getAccountId(), siteCreation.getStatus(), siteCreation.isActive(),
				siteCreation.getTenantId());
	}

	public List<SiteCreationData> searchSiteIds(String siteName, String districtName, String ulbName,
			String wardNumber) {
		List<SiteCreationData> list = new ArrayList<>();
		try {
			Object object = jdbcTemplate.query(queryBuilder.SEARCH_EXISTING_SITE,
					Arrays.asList(siteName, districtName, ulbName, wardNumber).toArray(), siteApplicationRowMapper);

			list = objectMapper.convertValue(object, ArrayList.class);

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return list;

	}

	public List<SiteCreationData> searchSites(SiteCreationData siteCreationData) {
		List<SiteCreationData> list = new ArrayList<>();
		try {
			Object object = jdbcTemplate.query(queryBuilder.SEARCH_QUERY_FOR_SITE_UPDATE,
					Arrays.asList(siteCreationData.getId(), siteCreationData.getUuid(), siteCreationData.getSiteID())
							.toArray(),
					siteApplicationRowMapper);

			list = objectMapper.convertValue(object, ArrayList.class);

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		return list;
	}

	public void updateSiteData(SiteUpdateRequest updateSiteRequest) {
		jdbcTemplate.update(queryBuilder.UPDATE_QUERY, updateSiteRequest.getSiteUpdationData().getSiteName(),
				updateSiteRequest.getSiteUpdationData().getSiteDescription(),
				updateSiteRequest.getSiteUpdationData().getGpsLocation(),
				updateSiteRequest.getSiteUpdationData().getSiteAddress(),
				updateSiteRequest.getSiteUpdationData().getSiteCost(),
				updateSiteRequest.getSiteUpdationData().getSitePhotograph(),
				updateSiteRequest.getSiteUpdationData().getStructure(),
				updateSiteRequest.getSiteUpdationData().getSizeLength(),
				updateSiteRequest.getSiteUpdationData().getSizeWidth(),
				updateSiteRequest.getSiteUpdationData().getLedSelection(),
				updateSiteRequest.getSiteUpdationData().getSecurityAmount(),
				updateSiteRequest.getSiteUpdationData().getPowered(),
				updateSiteRequest.getSiteUpdationData().getOthers(),
				updateSiteRequest.getSiteUpdationData().getDistrictName(),
				updateSiteRequest.getSiteUpdationData().getUlbName(),
				updateSiteRequest.getSiteUpdationData().getUlbType(),
				updateSiteRequest.getSiteUpdationData().getWardNumber(),
				updateSiteRequest.getSiteUpdationData().getPinCode(),
				updateSiteRequest.getSiteUpdationData().getAdditionalDetail(),
				updateSiteRequest.getSiteUpdationData().getAuditDetails().getLastModifiedBy(),
				updateSiteRequest.getSiteUpdationData().getAuditDetails().getLastModifiedDate(),
				updateSiteRequest.getSiteUpdationData().getSiteType(),
				updateSiteRequest.getSiteUpdationData().getAccountId(),
				updateSiteRequest.getSiteUpdationData().getStatus(), updateSiteRequest.getSiteUpdationData().isActive(),
				updateSiteRequest.getSiteUpdationData().getUuid(), updateSiteRequest.getSiteUpdationData().getId());

	}

}
