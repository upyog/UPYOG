package org.egov.edcr.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Building;
import org.egov.common.entity.edcr.DcrReportBlockDetail;
import org.egov.common.entity.edcr.DcrReportFloorDetail;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.autonumber.DcrApplicationNumberGenerator;
import org.egov.edcr.autonumber.OCPlanScrutinyNumberGenerator;
import org.egov.edcr.entity.ApplicationType;
import org.egov.edcr.entity.EdcrApplication;
import org.egov.edcr.entity.EdcrApplicationDetail;
import org.egov.edcr.feature.RoadWidth;
import org.egov.edcr.utility.DcrConstants;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.security.utils.SecureCodeUtils;
import org.egov.infra.utils.DateUtils;
import org.joda.time.LocalDate;

import static org.egov.infra.security.utils.SecureCodeUtils.generatePDF417Code;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;


@Service
public class PlanReportServiceV2 {

    private final RoadWidth roadWidth;

    private static final Logger LOG = LogManager.getLogger(PlanReportServiceV2.class);

    @Value("${edcr.service.url:}")
    private String edcr_internal_service_url;

    @Value("${edcr.report.mseva.logo.url:}")
    private String edcr_mseva_logo_url;

    @Value("${edcr.report.logodep.url:}")
    private String edcr_logodep_url;

    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private DcrApplicationNumberGenerator dcrApplicationNumberGenerator;
    @Autowired
    private OCPlanScrutinyNumberGenerator ocPlanScrutinyNumberGenerator;

    private static final BigDecimal Fire_Tender_Movement = BigDecimal.valueOf(21);

    // -----------------------------------------------------------------------
    // Setback feature key constants — mirrors PlanReportService
    // -----------------------------------------------------------------------
    public static final String FRONT_YARD_DESC   = "Front Setback";
    public static final String REAR_YARD_DESC    = "Rear Setback";
    public static final String SIDE_YARD_DESC    = "Side Setback";

    PlanReportServiceV2(RoadWidth roadWidth) {
        this.roadWidth = roadWidth;
    }

    // -----------------------------------------------------------------------
    // buildReportModel (V1 — kept as-is, not changed)
    // -----------------------------------------------------------------------
    public Map<String, Object> buildReportModel(Plan plan, EdcrApplication dcrApplication) {

        Map<String, Object> model = new HashMap<>();

        String applicationNumber = StringUtils.isNotBlank(dcrApplication.getApplicationNumber())
                ? dcrApplication.getApplicationNumber() : "NA";
        String applicationDate = DateUtils.toDefaultDateFormat(dcrApplication.getApplicationDate());

        StringBuilder errors = new StringBuilder();
        boolean finalReportStatus = true;
        if (plan.getErrors() != null && plan.getErrors().size() > 0) {
            int i = 1;
            for (Map.Entry<String, String> entry : plan.getErrors().entrySet()) {
                errors.append(String.valueOf(i)).append(". ");
                errors.append(entry.getValue());
                errors.append("\n");
                i++;
                finalReportStatus = false;
            }
        }

        int count = 1;

        
        model.put("ulbName", ApplicationThreadLocals.getMunicipalityName());
        model.put("applicantName", dcrApplication.getApplicantName());
        model.put("licensee", dcrApplication.getArchitectInformation());
        model.put("applicationNumber", applicationNumber);
        model.put("applicationDate", applicationDate);
        model.put("errors", plan.getErrors());
        model.put("errorString", errors.toString());
        model.put("nocs", plan.getNoObjectionCertificates());
        model.put("reportGeneratedDate", DateUtils.toDefaultDateTimeFormat(new Date()));
        model.put("currentYear", new LocalDate().getYear());
        model.put("far", plan.getFarDetails() != null ? plan.getFarDetails().getProvidedFar() : "");
        model.put("coverage", plan.getCoverage());
        model.put("totalFloorArea",
                plan.getVirtualBuilding() != null ? plan.getVirtualBuilding().getTotalFloorArea() : BigDecimal.valueOf(0));
        model.put("totalBuiltUpArea",
                plan.getVirtualBuilding() != null ? plan.getVirtualBuilding().getTotalBuitUpArea() : BigDecimal.valueOf(0));
        model.put("blockCount",
                plan.getBlocks() != null && !plan.getBlocks().isEmpty() ? plan.getBlocks().size() : 0);
        model.put("surrenderRoadArea", plan.getTotalSurrenderRoadArea());
//        model.put("egovLogo", edcr_mseva_logo_url);
//        model.put("logo", edcr_logodep_url);
        
        ClassPathResource logoResource =
                new ClassPathResource("images/logo_dep.png");
        
        ClassPathResource footerLogoResource =
                new ClassPathResource("images/mseva.png");        

        try {
			model.put("logo", logoResource.getURL().toString());
			model.put("egovLogo", footerLogoResource.getURL().toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        model.put("logo", imageUrlToBase64(edcr_logodep_url));
        model.put("egovLogo", imageUrlToBase64(edcr_mseva_logo_url));
        
        model.put("cityLogo", edcr_logodep_url);
        model.put("numberOfFloors", plan.getPlanInformation().getNumberOfFloors());
        model.put("ulbType", plan.getPlanInformation().getUlbType());
        model.put("district", plan.getPlanInformation().getDistrict());
        model.put("roadType", plan.getPlanInformation().getRoadType());

        List<ScrutinyDetail> allDetails = plan.getReportOutput().getScrutinyDetails();

        List<Map<String, Object>> commonSections = new ArrayList<>();
        List<ScrutinyDetail> commonScrutinyDetails = allDetails.stream()
                .filter(sc -> !sc.getKey().toLowerCase().startsWith("block"))
                .collect(Collectors.toList());

        for (ScrutinyDetail sd : commonScrutinyDetails) {
            Map<String, Object> section = new HashMap<>();
            List<String> columns = new ArrayList<>();
            sd.getColumnHeading().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(e -> columns.add(e.getValue().name));
            section.put("columns", columns);
            section.put("rows", sd.getDetail());
            section.put("remarks", sd.getRemarks());
            String[] keyArr = sd.getKey().split("_");
            section.put("heading", count + "." + keyArr[keyArr.length - 1]);
            count++;
            commonSections.add(section);
        }
        model.put("commonSections", commonSections);

        Map<String, Map<String, List<ScrutinyDetail>>> grouped = new LinkedHashMap<>();
        for (ScrutinyDetail sd : allDetails) {
            if (!sd.getKey().startsWith("Block")) continue;
            String key = sd.getKey();
            String[] parts = key.split("_");
            String block = parts[1];
            String featureRaw = key.substring(key.indexOf(parts[2]));
            String feature = featureRaw.toLowerCase().contains("setback") ? "Setback" : featureRaw;
            grouped.computeIfAbsent(block, k -> new LinkedHashMap<>())
                    .computeIfAbsent(feature, k -> new ArrayList<>())
                    .add(sd);
        }

        List<Map<String, Object>> blocks = new ArrayList<>();
        for (String blockKey : grouped.keySet()) {
            Map<String, Object> blockMap = new HashMap<>();
            blockMap.put("name", "Block " + blockKey);
            List<Map<String, Object>> features = new ArrayList<>();
            for (String featureKey : grouped.get(blockKey).keySet()) {
                List<ScrutinyDetail> list = grouped.get(blockKey).get(featureKey);
                Map<String, Object> feature = new HashMap<>();
                feature.put("heading", featureKey);
                List<Map<String, Object>> rows = new ArrayList<>();
                List<String> columns = new ArrayList<>();
                if ("Setback".equals(featureKey)) {
                    columns.add("Section"); columns.add("Setback"); columns.add("Occupancy");
                    columns.add("Level"); columns.add("Required"); columns.add("Provided"); columns.add("Status");
                    for (ScrutinyDetail sd : list) {
                        for (Map<String, String> d : sd.getDetail()) {
                            Map<String, Object> row = new HashMap<>();
                            if (sd.getKey().toLowerCase().contains("front")) {
                                d.put("Setback", "Front");
                            } else if (sd.getKey().toLowerCase().contains("rear")) {
                                d.put("Setback", "Rear");
                            } else {
                                d.put("Setback", d.getOrDefault("Side Number", "Side"));
                            }
                            for (String col : columns) row.put(col, d.getOrDefault(col, ""));
                            rows.add(row);
                        }
                    }
                } else {
                    ScrutinyDetail first = list.get(0);
                    first.getColumnHeading().entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .forEach(e -> columns.add(e.getValue().name));
                    for (ScrutinyDetail sd : list) {
                        for (Map<String, String> d : sd.getDetail()) {
                            Map<String, Object> row = new HashMap<>();
                            for (String col : columns) row.put(col, d.getOrDefault(col, ""));
                            rows.add(row);
                        }
                    }
                }
                feature.put("columns", columns);
                feature.put("rows", rows);
                features.add(feature);
            }
            blockMap.put("features", features);
            blocks.add(blockMap);
        }
        model.put("blocks", blocks);
        return model;
    }

    // -----------------------------------------------------------------------
    // buildReportModelV2  — the main method used by generateReport()
    // -----------------------------------------------------------------------
    public Map<String, Object> buildReportModelV2(Plan plan, EdcrApplication dcrApplication) {

        Map<String, Object> model = new HashMap<>();

        String applicationNumber = StringUtils.isNotBlank(dcrApplication.getApplicationNumber())
                ? dcrApplication.getApplicationNumber() : "NA";
        String applicationDate = DateUtils.toDefaultDateFormat(dcrApplication.getApplicationDate());

        boolean finalReportStatus = true;
        if (plan.getErrors() != null && plan.getErrors().size() > 0)
            finalReportStatus = false;

        model.put("logo", edcr_logodep_url);
        model.put("ulbName", ApplicationThreadLocals.getMunicipalityName());

        // applicationType — safe extraction with fallback
        String applicationTypeVal = null;
        if (dcrApplication != null && dcrApplication.getApplicationType() != null) {
            applicationTypeVal = dcrApplication.getApplicationType().getApplicationTypeVal();
        }
        if (StringUtils.isBlank(applicationTypeVal)
                && dcrApplication != null
                && dcrApplication.getEdcrApplicationDetails() != null
                && !dcrApplication.getEdcrApplicationDetails().isEmpty()
                && dcrApplication.getEdcrApplicationDetails().get(0).getApplication() != null
                && dcrApplication.getEdcrApplicationDetails().get(0).getApplication().getApplicationType() != null) {
            applicationTypeVal = dcrApplication.getEdcrApplicationDetails().get(0)
                    .getApplication().getApplicationType().getApplicationTypeVal();
        }
        if (StringUtils.isBlank(applicationTypeVal)) applicationTypeVal = "NA";
        model.put("applicationType", applicationTypeVal);

        model.put("reportHeading", "AUTOMATED BUILDING PERMIT APPROVAL SYSTEM");
        model.put("reportType",    "PLAN DOCUMENT SCRUTINY REPORT");

        model.put("applicantName",      dcrApplication.getApplicantName());
        model.put("licensee",           dcrApplication.getArchitectInformation());
        model.put("applicationNumber",  applicationNumber);
        model.put("applicationDate",    applicationDate);
        model.put("errors",             plan.getErrors());
        model.put("nocs",               plan.getNoObjectionCertificates());
        model.put("reportGeneratedDate", DateUtils.toDefaultDateTimeFormat(new Date()));
        model.put("currentYear",        new LocalDate().getYear());
        model.put("far",                plan.getFarDetails() != null ? plan.getFarDetails().getProvidedFar() : "");
        model.put("coverage",           plan.getCoverage());
        model.put("totalFloorArea",
                plan.getVirtualBuilding() != null ? plan.getVirtualBuilding().getTotalFloorArea() : BigDecimal.valueOf(0));
        model.put("totalBuiltUpArea",
                plan.getVirtualBuilding() != null ? plan.getVirtualBuilding().getTotalBuitUpArea() : BigDecimal.valueOf(0));
        model.put("blockCount",
                plan.getBlocks() != null && !plan.getBlocks().isEmpty() ? plan.getBlocks().size() : 0);
        model.put("surrenderRoadArea",  plan.getTotalSurrenderRoadArea());
        model.put("egovLogo",           edcr_mseva_logo_url);
        model.put("cityLogo",           edcr_logodep_url);
        model.put("numberOfFloors",     plan.getPlanInformation().getNumberOfFloors());
        model.put("ulbType",            plan.getPlanInformation().getUlbType());
        model.put("district",           plan.getPlanInformation().getDistrict());
        model.put("roadType",           plan.getPlanInformation().getRoadType());
        model.put("planInformation",    plan.getPlanInformation());
        model.put("plot",               plan.getPlot());
        model.put("plotBndryArea",
                plan.getPlot() != null ? plan.getPlot().getPlotBndryArea() : BigDecimal.ZERO);

        // ---- serviceType (same logic as PlanReportService) ----
        Map<String, String> serviceTypeList = new ConcurrentHashMap<>();
        serviceTypeList.put("NEW_CONSTRUCTION", "New Construction");
        if (StringUtils.isNotBlank(dcrApplication.getServiceType())) {
            model.put("serviceType", serviceTypeList.getOrDefault(dcrApplication.getServiceType(),
                    dcrApplication.getServiceType()));
        } else {
            model.put("serviceType", "NA");
        }

        // ---- Occupancy / Sub-Occupancy ----
        if (plan.getVirtualBuilding() != null && !plan.getVirtualBuilding().getOccupancyTypes().isEmpty()) {
            List<String> occupancies = new ArrayList<>();
            plan.getVirtualBuilding().getOccupancyTypes().forEach(occ -> {
                String occType    = occ.getType()    != null ? occ.getType().getName()    : "";
                String subOccType = occ.getSubtype() != null ? occ.getSubtype().getName() : "";
                if (!occType.isEmpty() && !subOccType.isEmpty()) {
                    occupancies.add(occType + " (" + subOccType + ")");
                } else if (!occType.isEmpty()) {
                    occupancies.add(occType);
                }
            });
            Set<String> distinctOccupancies = new HashSet<>();
            if (!occupancies.isEmpty()) distinctOccupancies.add(occupancies.get(0));
            plan.getPlanInformation().setOccupancy(
                    distinctOccupancies.stream().map(String::new).collect(Collectors.joining(",")));
        }

        // ---- Scrutiny details ----
        List<ScrutinyDetail> allDetails = new ArrayList<>();
        if (plan.getReportOutput() != null && plan.getReportOutput().getScrutinyDetails() != null) {
            allDetails = plan.getReportOutput().getScrutinyDetails();
        }

        // sections is a LinkedHashMap so order is preserved exactly as the PDF
        Map<String, Map<String, ScrutinyDetail>> sections = new LinkedHashMap<>();

        // 1. Overall Summary
        if (plan.getBlocks() != null && plan.getBlocks().size() > 0) {
            sections.put("Overall Summary", getOverallSummaryBlock(plan));

            List<DcrReportBlockDetail> proposedBlockDetails = buildBlockWiseProposedInfo(plan);
            addBlockWiseSummary(proposedBlockDetails, sections);
        }

        // 2. Prefix → section name mapping (preserves PDF order)
        Map<String, String> prefixSummaryNameMap = new LinkedHashMap<>();
        prefixSummaryNameMap.put("Common_", "Common - Scrutiny Details");
        if (plan.getBlocks() != null && plan.getBlocks().size() > 0) {
            for (int i = 1; i <= plan.getBlocks().size(); i++) {
                prefixSummaryNameMap.put("Block_" + i, "Block " + i + " - Scrutiny Details");
            }
        }

        // Initialise empty section maps in order
        prefixSummaryNameMap.forEach((prefix, name) ->
                sections.put(name, new LinkedHashMap<>()));

        // 3. Fill sections — setback logic fixed here
        addSummarySections(allDetails, sections, prefixSummaryNameMap);

        // 4. Determine final status from "Not Fulfilled" count
        long notFulfilledCount = sections.entrySet().stream()
                .flatMap(entry -> entry.getValue().entrySet().stream())
                .map(e -> e.getValue().getDetail())
                .flatMap(List::stream)
                .filter(d -> d.containsKey("Status") && d.get("Status").equalsIgnoreCase("Not Accepted"))
                .count();

        if (notFulfilledCount > 0) finalReportStatus = false;

        // 5. DCR number
        if (finalReportStatus) {
            String dcrApplicationNumber = "";
            if (ApplicationType.OCCUPANCY_CERTIFICATE.equals(dcrApplication.getApplicationType()))
                dcrApplicationNumber = ocPlanScrutinyNumberGenerator.generateEdcrApplicationNumber();
            else
                dcrApplicationNumber = dcrApplicationNumberGenerator.generateEdcrApplicationNumber(dcrApplication);
            EdcrApplicationDetail edcrApplicationDetail = dcrApplication.getEdcrApplicationDetails().get(0);
            edcrApplicationDetail.setDcrNumber(dcrApplicationNumber);
            if (StringUtils.isEmpty(dcrApplicationNumber)) dcrApplicationNumber = "NA";
            model.put("dcrNo", dcrApplicationNumber);

            String qrContent = "DCR Number : " + dcrApplicationNumber + "\n"
                    + "Application Number : " + applicationNumber + "\n"
                    + "Application Date : " + applicationDate + "\n"
                    + "Report Status : Fulfilled\n";
            model.put("qrCode", SecureCodeUtils.generatePDF417CodeV2(qrContent));  // import from SecureCodeUtils
        } else {
            model.put("dcrNo", "NA");
            model.put("qrCode", null);
        }

        model.put("sections",      sections);
        model.put("reportStatus",  finalReportStatus ? "Fulfilled" : "Not Fulfilled");
        plan.setEdcrPassed(finalReportStatus);
        return model;
    }

    // -----------------------------------------------------------------------
    // getOverallSummaryBlock — unchanged from original
    // -----------------------------------------------------------------------
    private Map<String, ScrutinyDetail> getOverallSummaryBlock(Plan plan) {

        Map<String, ScrutinyDetail> overallSummaryDetails = new LinkedHashMap<>();

        // Ground Coverage row (Total Plot Area | Ground Coverage | Built Up Area)
        if (plan.getVirtualBuilding() != null) {
            ScrutinyDetail groundCoverageTable = new ScrutinyDetail();
            groundCoverageTable.setKey("Ground Coverage");
            groundCoverageTable.setHeading("Ground Coverage (in m²)");
            groundCoverageTable.addColumnHeading(1, "Total Plot Area");
            groundCoverageTable.addColumnHeading(2, "Ground Coverage");
            groundCoverageTable.addColumnHeading(3, "Built Up Area");

            Map<String, String> details = new HashMap<>();
            details.put("Total Plot Area",
                    plan.getVirtualBuilding().getTotalFloorArea() != null
                            ? plan.getVirtualBuilding().getTotalFloorArea().setScale(2, RoundingMode.HALF_UP).toString()
                            : "0");
            details.put("Ground Coverage",
                    plan.getVirtualBuilding().getTotalCoverageArea() != null
                            ? plan.getVirtualBuilding().getTotalCoverageArea().setScale(2, RoundingMode.HALF_UP).toString()
                            : "0");
            details.put("Built Up Area",
                    plan.getVirtualBuilding().getTotalBuitUpArea() != null
                            ? plan.getVirtualBuilding().getTotalBuitUpArea().setScale(2, RoundingMode.HALF_UP).toString()
                            : "0");

            groundCoverageTable.addDetail(details);
            overallSummaryDetails.put("Ground Coverage", groundCoverageTable);
        }

        return overallSummaryDetails;
    }

    // -----------------------------------------------------------------------
    // addSummarySections — KEY FIX: setback label now mirrors PlanReportService
    // -----------------------------------------------------------------------
    private void addSummarySections(List<ScrutinyDetail> scrutinyDetails,
                                    Map<String, Map<String, ScrutinyDetail>> sections,
                                    Map<String, String> prefixSummaryNameMap) {

        final String SETBACK_KEY = "Setback";

        // Column headings for the combined Setback table — matches PDF column order
        final String[] setbackColumns = {
                "Section", "Setback", "Occupancy", "Level", "Permissible", "Provided", "Status"
        };

        for (ScrutinyDetail scrutinyDetail : scrutinyDetails) {

            if (scrutinyDetail.getKey() == null) continue;

            String[] keyArr   = scrutinyDetail.getKey().split("_");
            String detailsHeading = keyArr[keyArr.length - 1];

            prefixSummaryNameMap.forEach((prefix, summaryName) -> {

                if (!scrutinyDetail.getKey().toLowerCase().startsWith(prefix.toLowerCase())) return;

                boolean isSetback = scrutinyDetail.getKey().toLowerCase().contains("setback")
                        && !CollectionUtils.isEmpty(scrutinyDetail.getDetail());

                if (isSetback) {
                    // ---- Combined Setback table ----
                    if (!sections.get(summaryName).containsKey(SETBACK_KEY)) {
                        ScrutinyDetail setbackSd = new ScrutinyDetail();
                        setbackSd.setKey(SETBACK_KEY);
                        for (int i = 0; i < setbackColumns.length; i++)
                            setbackSd.addColumnHeading(i + 1, setbackColumns[i]);
                        sections.get(summaryName).put(SETBACK_KEY, setbackSd);
                    }

                    // Each row in this setback detail becomes one row in the combined table
                    for (Map<String, String> srcRow : scrutinyDetail.getDetail()) {

                        Map<String, String> row = new HashMap<>();

                        // Determine the "Setback" label — mirrors PlanReportService logic
                        String sdKeyLower = scrutinyDetail.getKey().toLowerCase();
                        if (sdKeyLower.contains("front")) {
                            row.put("Setback", "Front");
                        } else if (sdKeyLower.contains("rear")) {
                            row.put("Setback", "Rear");
                        } else {
                            // Side setback — use Side Number value like PlanReportService
                            // e.g. "Side Setback 1", "Side Setback 2"
                            String sideNum = srcRow.getOrDefault("Side Number", "");
                            if (StringUtils.isNotBlank(sideNum)) {
                                // Grab last character of sideNum as the number (matches PlanReportService)
                                row.put("Setback", "Side Setback " + sideNum.trim().charAt(sideNum.trim().length() - 1));
                            } else {
                                // fallback: use heading e.g. "Side Setback 1"
                                row.put("Setback", scrutinyDetail.getHeading() != null
                                        ? scrutinyDetail.getHeading() : "Side");
                            }
                        }

                        // Copy all standard columns from source row
                        for (String col : setbackColumns) {
                            if (!"Setback".equals(col)) {
                                row.put(col, srcRow.getOrDefault(col, ""));
                            }
                        }

                        sections.get(summaryName).get(SETBACK_KEY).addDetail(row);
                    }

                } else {
                    // Normal (non-setback) feature — put as-is
                    sections.get(summaryName).put(detailsHeading, scrutinyDetail);
                }
            });
        }
    }

    // -----------------------------------------------------------------------
    // addBlockWiseSummary — unchanged from original
    // -----------------------------------------------------------------------
    private void addBlockWiseSummary(List<DcrReportBlockDetail> proposedBlockDetails,
                                     Map<String, Map<String, ScrutinyDetail>> sections) {

        sections.put("Block Wise Summary", new LinkedHashMap<>());

        proposedBlockDetails.forEach(proposedBlockDetail -> {

            BigDecimal totalBuiltUpArea  = BigDecimal.ZERO;
            BigDecimal totalDeductionArea = BigDecimal.ZERO;
            BigDecimal totalFloorArea    = BigDecimal.ZERO;

            String heading = "Block No " + proposedBlockDetail.getBlockNo() + " - Proposed Details";
            ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
            scrutinyDetail.setKey(heading);
            scrutinyDetail.addColumnHeading(1, "Floor");
            scrutinyDetail.addColumnHeading(2, "Occupancy/Sub Occupancy");
            scrutinyDetail.addColumnHeading(3, "Built Up Area in m²");
            scrutinyDetail.addColumnHeading(4, "Deduction Area in m²");
            scrutinyDetail.addColumnHeading(5, "Floor Area in m²");

            for (DcrReportFloorDetail floor : proposedBlockDetail.getDcrReportFloorDetails()) {
                totalBuiltUpArea   = totalBuiltUpArea.add(floor.getBuiltUpArea());
                totalDeductionArea = totalDeductionArea.add(floor.getBuiltUpDeductionArea());
                totalFloorArea     = totalFloorArea.add(floor.getFloorArea());

                Map<String, String> details = new HashMap<>();
                details.put("Floor",                    floor.getFloorNo());
                details.put("Occupancy/Sub Occupancy",  floor.getOccupancy());
                details.put("Built Up Area in m²",      floor.getBuiltUpArea().toString());
                details.put("Deduction Area in m²",     floor.getBuiltUpDeductionArea().toString());
                details.put("Floor Area in m²",         floor.getFloorArea().toString());
                scrutinyDetail.addDetail(details);
            }

            // Total row using colspan marker
            Map<String, String> totalRow = new HashMap<>();
            totalRow.put("Occupancy/Sub Occupancy", "Total-colspan-2");
            totalRow.put("Built Up Area in m²",     totalBuiltUpArea.toString());
            totalRow.put("Deduction Area in m²",    totalDeductionArea.toString());
            totalRow.put("Floor Area in m²",        totalFloorArea.toString());
            scrutinyDetail.addDetail(totalRow);

            // Remarks: building height / coverage notes
            StringBuilder text = new StringBuilder();
            text.append("1. Ground Coverage Area is ")
                    .append(proposedBlockDetail.getCoverageArea() != null
                            ? proposedBlockDetail.getCoverageArea()
                              .setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS)
                            : BigDecimal.ZERO)
                    .append(" m²");
            text.append("\n2. Height of building is ")
                    .append(proposedBlockDetail.getBuildingHeightExcludingMP() != null
                            ? proposedBlockDetail.getBuildingHeightExcludingMP()
                              .setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS)
                            : BigDecimal.ZERO)
                    .append(" m");
            text.append("\n3. Total Height of building is ")
                    .append(proposedBlockDetail.getBuildingHeight() != null
                            ? proposedBlockDetail.getBuildingHeight()
                              .setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS)
                            : BigDecimal.ZERO)
                    .append(" m");

            if (proposedBlockDetail.getConstructedArea().compareTo(BigDecimal.ZERO) > 0) {
                text.append("\n4. Already constructed area is ")
                        .append(proposedBlockDetail.getConstructedArea()
                                .setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS))
                        .append(" m²");
            }

            scrutinyDetail.setRemarks(text.toString());
            sections.get("Block Wise Summary").put(heading, scrutinyDetail);
        });
    }

    // -----------------------------------------------------------------------
    // buildBlockWiseProposedInfo — unchanged from original
    // -----------------------------------------------------------------------
    private List<DcrReportBlockDetail> buildBlockWiseProposedInfo(Plan plan) {

        List<DcrReportBlockDetail> dcrReportBlockDetails = new ArrayList<>();
        List<Block> blocks = plan.getBlocks();

        if (!blocks.isEmpty()) {
            for (Block block : blocks) {
                Building building = block.getBuilding();
                if (building != null) {
                    DcrReportBlockDetail dcrReportBlockDetail = new DcrReportBlockDetail();
                    dcrReportBlockDetail.setBlockNo(block.getNumber());
                    dcrReportBlockDetail.setCoverageArea(building.getCoverageArea());
                    dcrReportBlockDetail.setBuildingHeight(building.getBuildingHeight());
                    dcrReportBlockDetail.setBuildingHeightExcludingMPt(building.getBuildingHeightExcludingMP());
                    dcrReportBlockDetail.setConstructedArea(building.getTotalConstructedArea());

                    List<Floor> floors = building.getFloors();
                    BigDecimal buildingHeightExMumpty = building.getBuildingHeightExcludingMP()
                            .setScale(2, RoundingMode.HALF_UP);
                    if (buildingHeightExMumpty != null &&
                            buildingHeightExMumpty.compareTo(Fire_Tender_Movement) > 0) {
                        LOG.info("building height exclude mumpty : " + buildingHeightExMumpty);
                    }

                    if (!floors.isEmpty()) {
                        List<DcrReportFloorDetail> dcrReportFloorDetails = new ArrayList<>();
                        for (Floor floor : floors) {
                            List<Occupancy> occupancies = floor.getOccupancies();
                            if (!occupancies.isEmpty()) {
                                for (Occupancy occupancy : occupancies) {
                                    String occupancyName = "";
                                    if (occupancy.getTypeHelper() != null) {
                                        if (occupancy.getTypeHelper().getSubtype() != null) {
                                            occupancyName = occupancy.getTypeHelper().getSubtype().getName();
                                        } else if (occupancy.getTypeHelper().getType() != null) {
                                            occupancyName = occupancy.getTypeHelper().getType().getName();
                                        }
                                    }

                                    DcrReportFloorDetail dcrReportFloorDetail = new DcrReportFloorDetail();
                                    String floorNo;
                                    if (floor.getTerrace()) {
                                        floorNo = "Terrace";
                                    } else if (occupancy.getIsMezzanine()) {
                                        floorNo = floor.getNumber() + " (Mezzanine " + floor.getNumber() + ")";
                                    } else {
                                        floorNo = String.valueOf(floor.getNumber());
                                    }
                                    dcrReportFloorDetail.setFloorNo(floorNo);
                                    dcrReportFloorDetail.setOccupancy(occupancyName);

                                    BigDecimal builtUpArea = occupancy.getExistingBuiltUpArea()
                                            .compareTo(BigDecimal.ZERO) > 0
                                            ? occupancy.getBuiltUpArea().subtract(occupancy.getExistingBuiltUpArea())
                                            : occupancy.getBuiltUpArea();
                                    dcrReportFloorDetail.setBuiltUpArea(
                                            builtUpArea.setScale(2, BigDecimal.ROUND_HALF_UP));

                                    if (floor.getIsStiltFloor()) {
                                        dcrReportFloorDetail.setFloorArea(BigDecimal.valueOf(0.0));
                                        dcrReportFloorDetail.setOccupancy(occupancyName + "(Stilt)");
                                    } else {
                                        dcrReportFloorDetail.setOccupancy(occupancyName);
                                        BigDecimal floorArea = occupancy.getExistingFloorArea()
                                                .compareTo(BigDecimal.ZERO) > 0
                                                ? occupancy.getFloorArea().subtract(occupancy.getExistingFloorArea())
                                                : occupancy.getFloorArea();
                                        dcrReportFloorDetail.setFloorArea(
                                                floorArea.setScale(2, BigDecimal.ROUND_HALF_UP));
                                    }

                                    BigDecimal builtUpDeductionArea = occupancy.getDeduction() == null
                                            ? BigDecimal.ZERO : occupancy.getDeduction();
                                    dcrReportFloorDetail.setBuiltUpDeductionArea(
                                            builtUpDeductionArea.setScale(2, BigDecimal.ROUND_HALF_UP));

                                    if (dcrReportFloorDetail.getBuiltUpArea().compareTo(BigDecimal.ZERO) > 0) {
                                        dcrReportFloorDetails.add(dcrReportFloorDetail);
                                    }
                                }
                            }
                        }
                        dcrReportFloorDetails = dcrReportFloorDetails.stream()
                                .sorted(Comparator.comparing(DcrReportFloorDetail::getFloorNo))
                                .collect(Collectors.toList());
                        dcrReportBlockDetail.setDcrReportFloorDetails(dcrReportFloorDetails);
                    }
                    dcrReportBlockDetails.add(dcrReportBlockDetail);
                }
            }
        }
        return dcrReportBlockDetails;
    }

    // -----------------------------------------------------------------------
    // generatePdf — unchanged
    // -----------------------------------------------------------------------
    public byte[] generatePdf(Map<String, Object> model) throws Exception {
        Context context = new Context();
        context.setVariables(model);
        String html = templateEngine.process("report2", context);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(html, null);
        builder.toStream(os);
        builder.run();
        return os.toByteArray();
    }

    public static void replaceStatusWithFulfillTerms(Map<String, Object> model) {
        LOG.info("Replacing 'Accepted/Not Accepted' with 'Fulfilled/Not Fulfilled'...");

        // 1. Top-level reportStatus string
        if (model.containsKey("reportStatus")) {
            Object val = model.get("reportStatus");
            if (val instanceof String) {
                model.put("reportStatus", swapStatus((String) val));
            }
        }

        // 2. Traverse sections -> Map<sectionName, Map<featureName, ScrutinyDetail>>
        Object sectionsObj = model.get("sections");
        if (sectionsObj instanceof Map) {
            for (Object sectionVal : ((Map<?, ?>) sectionsObj).values()) {
                if (!(sectionVal instanceof Map)) continue;
                for (Object sdObj : ((Map<?, ?>) sectionVal).values()) {
                    if (!(sdObj instanceof ScrutinyDetail)) continue;
                    for (Map<String, String> row : ((ScrutinyDetail) sdObj).getDetail()) {
                        if (row.containsKey("Status")) {
                            row.put("Status", swapStatus(row.get("Status")));
                        }
                    }
                }
            }
        }

        LOG.info("Status terminology update completed.");
    }
    
    public static String imageUrlToBase64(String imageUrl) {

        try (InputStream inputStream = new URL(imageUrl).openStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            byte[] imageBytes = baos.toByteArray();

            String base64 = Base64.getEncoder().encodeToString(imageBytes);

            return "data:image/png;base64," + base64;

        } catch (Exception e) {
            throw new RuntimeException("Failed to convert image URL to Base64", e);
        }
    }

    private static String swapStatus(String status) {
        if (status == null) return null;
        // Order matters: replace the longer string first
        return status.replace("Not Accepted", "Not Fulfilled")
                .replace("Accepted",     "Fulfilled");
    }

    // -----------------------------------------------------------------------
    // generateReport — unchanged
    // -----------------------------------------------------------------------
    public InputStream generateReport(Plan plan, EdcrApplication dcrApplication) {
        try {
            LOG.info("Generating report for application: {}", dcrApplication.getApplicationNumber());
            Map<String, Object> model = buildReportModelV2(plan, dcrApplication);
            replaceStatusWithFulfillTerms(model);
            byte[] pdfBytes = generatePdf(model);

            InputStream reportStream = new ByteArrayInputStream(pdfBytes);
            LOG.info("Report generated successfully for application: {}", dcrApplication.getApplicationNumber());
            return reportStream;
        } catch (Exception e) {
            LOG.error("Error occurred while generating report for application: {} - Error: {}",
                    dcrApplication.getApplicationNumber(), e.getMessage(), e);
            throw new RuntimeException("Failed to generate report: " + e.getMessage(), e);
        }
    }
}