package org.upyog.wms.repository.rowmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import org.egov.tracer.model.CustomException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.upyog.wms.web.models.ActionPlan;
import org.upyog.wms.web.models.AuditDetails;
import org.upyog.wms.web.models.Beneficiary;
import org.upyog.wms.web.models.Document;
import org.upyog.wms.web.models.FundDetail;
import org.upyog.wms.web.models.Location;
import org.upyog.wms.web.models.ProjectDetail;
import org.upyog.wms.web.models.Recommendation;

/** Row mappers for the header and every child table (one place, one style). */
@Component
@RequiredArgsConstructor
public class ActionPlanRowMapper {

    private final ObjectMapper mapper;

    public RowMapper<ActionPlan> header() {
        return (rs, i) -> ActionPlan.builder()
                .id(rs.getString("id"))
                .tenantId(rs.getString("tenantid"))
                .applicationNumber(rs.getString("applicationnumber"))
                .actionPlanNumber(rs.getString("actionplannumber"))
                .version(rs.getObject("version", Integer.class))
                .isLatestVersion(rs.getBoolean("islatestversion"))
                .financialYear(rs.getString("financialyear"))
                .projectId(rs.getString("projectid"))
                .projectName(rs.getString("projectname"))
                .schemeCode(rs.getString("schemecode"))
                .subSchemeCode(rs.getString("subschemecode"))
                .offlineApprovalDate(rs.getObject("offlineapprovaldate", Long.class))
                .approvingAuthority(rs.getString("approvingauthority"))
                .adminSanction(rs.getBoolean("adminsanction"))
                .technicalSanction(rs.getObject("technicalsanction", Boolean.class))
                .approvedAmount(rs.getBigDecimal("approvedamount"))
                .fundSource(rs.getString("fundsource"))
                .totalComponents(rs.getObject("totalcomponents", Integer.class))
                .declarationAccepted(rs.getBoolean("declarationaccepted"))
                .status(rs.getString("status"))
                .submittedDate(rs.getObject("submitteddate", Long.class))
                .approvedDate(rs.getObject("approveddate", Long.class))
                .approvedBy(rs.getString("approvedby"))
                .isActive(rs.getBoolean("isactive"))
                .additionalDetails(json(rs, "additionaldetails"))
                .auditDetails(audit(rs))
                .build();
    }

    public RowMapper<ProjectDetail> projectDetail() {
        return (rs, i) -> ProjectDetail.builder()
                .id(rs.getString("id"))
                .tenantId(rs.getString("tenantid"))
                .purposeCode(rs.getString("purposecode"))
                .purposeOther(rs.getString("purposeother"))
                .startDate(rs.getObject("startdate", Long.class))
                .endDate(rs.getObject("enddate", Long.class))
                .typeOfWork(rs.getString("typeofwork"))
                .workCategory(rs.getString("workcategory"))
                .isActive(rs.getBoolean("isactive"))
                .additionalDetails(json(rs, "additionaldetails"))
                .auditDetails(audit(rs))
                .build();
    }

    public RowMapper<Recommendation> recommendation() {
        return (rs, i) -> Recommendation.builder()
                .id(rs.getString("id"))
                .tenantId(rs.getString("tenantid"))
                .recommendedBy(rs.getString("recommendedby"))
                .recommendedByName(rs.getString("recommendedbyname"))
                .isActive(rs.getBoolean("isactive"))
                .additionalDetails(json(rs, "additionaldetails"))
                .auditDetails(audit(rs))
                .build();
    }

    public RowMapper<Location> location() {
        return (rs, i) -> Location.builder()
                .id(rs.getString("id"))
                .tenantId(rs.getString("tenantid"))
                .stateCode(rs.getString("statecode"))
                .districtCode(rs.getString("districtcode"))
                .mlaConstituency(rs.getString("mlaconstituency"))
                .mpConstituency(rs.getString("mpconstituency"))
                .isActive(rs.getBoolean("isactive"))
                .additionalDetails(json(rs, "additionaldetails"))
                .auditDetails(audit(rs))
                .build();
    }

    /** Component row; the parent id is read separately by the repository for grouping. */
    public RowMapper<org.upyog.wms.web.models.Component> component() {
        return (rs, i) -> org.upyog.wms.web.models.Component.builder()
                .id(rs.getString("id"))
                .tenantId(rs.getString("tenantid"))
                .componentNumber(rs.getString("componentnumber"))
                .componentName(rs.getString("componentname"))
                .category(rs.getString("category"))
                .subCategory(rs.getString("subcategory"))
                .fundSource(rs.getString("fundsource"))
                .approvedAmount(rs.getBigDecimal("approvedamount"))
                .status(rs.getString("status"))
                .isActive(rs.getBoolean("isactive"))
                .additionalDetails(json(rs, "additionaldetails"))
                .auditDetails(audit(rs))
                .build();
    }

    public RowMapper<FundDetail> fundDetail() {
        return (rs, i) -> FundDetail.builder()
                .id(rs.getString("id"))
                .tenantId(rs.getString("tenantid"))
                .actionPlanId(rs.getString("actionplanid"))
                .componentId(rs.getString("componentid"))
                .fundType(rs.getString("fundtype"))
                .sourceCode(rs.getString("sourcecode"))
                .fundName(rs.getString("fundname"))
                .amount(rs.getBigDecimal("amount"))
                .isActive(rs.getBoolean("isactive"))
                .additionalDetails(json(rs, "additionaldetails"))
                .auditDetails(audit(rs))
                .build();
    }

    public RowMapper<Beneficiary> beneficiary() {
        return (rs, i) -> Beneficiary.builder()
                .id(rs.getString("id"))
                .tenantId(rs.getString("tenantid"))
                .actionPlanId(rs.getString("actionplanid"))
                .componentId(rs.getString("componentid"))
                .name(rs.getString("name"))
                .remark(rs.getString("remark"))
                .isActive(rs.getBoolean("isactive"))
                .additionalDetails(json(rs, "additionaldetails"))
                .auditDetails(audit(rs))
                .build();
    }

    public RowMapper<Document> document() {
        return (rs, i) -> Document.builder()
                .id(rs.getString("id"))
                .tenantId(rs.getString("tenantid"))
                .documentType(rs.getString("documenttype"))
                .fileStoreId(rs.getString("filestoreid"))
                .documentUid(rs.getString("documentuid"))
                .isActive(rs.getBoolean("isactive"))
                .auditDetails(audit(rs))
                .build();
    }

    private static AuditDetails audit(ResultSet rs) throws SQLException {
        return AuditDetails.builder()
                .createdBy(rs.getString("createdby"))
                .createdTime(rs.getObject("createdtime", Long.class))
                .lastModifiedBy(rs.getString("lastmodifiedby"))
                .lastModifiedTime(rs.getObject("lastmodifiedtime", Long.class))
                .build();
    }

    /** JSONB is read as text (no PGobject dependency) and parsed into a tree. */
    private Object json(ResultSet rs, String column) throws SQLException {
        String value = rs.getString(column);
        if (value == null) {
            return null;
        }
        try {
            return mapper.readTree(value);
        } catch (JsonProcessingException e) {
            throw new CustomException("WMS_PARSING_ERROR", "Failed to parse " + column + ": " + e.getMessage());
        }
    }
}
