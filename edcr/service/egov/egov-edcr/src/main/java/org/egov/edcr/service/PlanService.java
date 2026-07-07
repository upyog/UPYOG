package org.egov.edcr.service;

import static org.egov.infra.utils.PdfUtils.appendFiles;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Building;
import org.egov.common.entity.edcr.EdcrPdfDetail;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.PlanFeature;
import org.egov.common.entity.edcr.PlanInformation;
import org.egov.commons.edcr.mdms.filter.MdmsFilter;
import org.egov.commons.mdms.BpaMdmsUtil;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.contract.ComparisonRequest;
//import org.egov.edcr.contract.EdcrRequest;
import org.egov.common.edcr.model.EdcrRequest;
import org.egov.edcr.entity.Amendment;
import org.egov.edcr.entity.AmendmentDetails;
import org.egov.edcr.entity.ApplicationType;
import org.egov.edcr.entity.EdcrApplication;
import org.egov.edcr.entity.EdcrApplicationDetail;
import org.egov.edcr.entity.OcComparisonDetail;
import org.egov.edcr.entity.blackbox.PlanDetail;
import org.egov.edcr.feature.Coverage;
import org.egov.edcr.feature.FeatureProcess;
import org.egov.edcr.feature.FrontYardService;
import org.egov.edcr.feature.Parking;
import org.egov.edcr.feature.PlanInfoFeature;
import org.egov.edcr.feature.PlotArea;
import org.egov.edcr.feature.RoadWidth;
import org.egov.edcr.utility.DcrConstants;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.custom.CustomImplProvider;
import org.egov.infra.filestore.entity.FileStoreMapper;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.microservice.models.RequestInfo;
import org.egov.infra.microservice.models.Role;
import org.python.antlr.PythonParser.print_stmt_return;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import static org.egov.edcr.constants.DxfFileConstants.*;

@Service
public class PlanService {
    private static final Logger LOG = LogManager.getLogger(PlanService.class);
    @Autowired
    private PlanFeatureService featureService;
    @Autowired
    private FileStoreService fileStoreService;
    @Autowired
    private CustomImplProvider specificRuleService;
    @Autowired
    private EdcrApplicationDetailService edcrApplicationDetailService;
    @Autowired
    private EdcrPdfDetailService edcrPdfDetailService;
    @Autowired
    private ExtractService extractService;
    @Autowired
    private EdcrApplicationService edcrApplicationService;
    @Autowired
    private OcComparisonService ocComparisonService;
    @Autowired
    private OcComparisonDetailService ocComparisonDetailService;
    
    @Autowired
    private BpaMdmsUtil bpaMdmsUtil;
    
    @Autowired
    private PlanReportServiceV2 planReportServiceV2;
    
	private static final List<String> FAR_PRIORITY = Collections.unmodifiableList(
			Arrays.asList(S_ECFG, A_FH, S_SAS, D_B, D_C, D_A, H_PP, E_NS, M_DFPAB, E_PS, E_SFMC, E_SFDAP, E_EARC, S_MCH,
					S_BH, S_CRC, S_CA, S_SC, S_ICC, A2, E_CLG, M_OHF, M_VH, M_NAPI, A_SA, M_HOTHC, E_SACA,

					C_MA, C_MIP, C_MOP,

					F, A, C,

					J_FS, J_FCSS, J_CNG, J,

					A_AF, A_AIF,

					G_G, G_F, G_S, G_HI, G_WT, G_RSI, G_GIP, G_GIF, G_ITP, G_ITF, G_TI, G_KI, G_SI,

					L_GP, L_GO, L_NS, L_PS, L_CO, L_ERC, L_MP, L_NH, L_C,

					R_R,

					F_RB, F_HM, F_SCC, F_PO, F_B, F_LB, F_D, F_CA, F_VGP, F_BU, F_PFSF, F_PFST, F_PFSS, F_PS, F_CNGS));
	
	private static final String SOURCE_INVESTPUNJAB = "INVESTPUNJAB";
	private static final String SOURCE_OBPAS = "OBPAS";
	private static final String INVALID_SOURCE = "INVALID SOURCE";
	
	private static final String ERROR_MSG = 
	        "Dear Investors,\n"
	                + "Please be informed that with effect from 15.05.2025, all building plan applications under the following categories will no longer be accepted on the eNaksha Portal: https://enaksha.lgpunjab.gov.in and https://mseva.lgpunjab.gov.in/\n"
	                + "• Industry • Hotel • Nursing Home / Hospital • Institutions\n"
	                + "Instead, investors are requested to submit such applications exclusively through the Punjab Bureau of Investment Promotion (PBIP) Portal: https://fasttrack.punjab.gov.in/webportal/login We request all stakeholders to kindly take note of this change and plan submissions accordingly.\n"
	                + "Local Government Department, Government of Punjab.”";

    public Plan process(EdcrApplication dcrApplication, String applicationType) {
        Map<String, String> cityDetails = specificRuleService.getCityDetails();
      
        Date asOnDate = null;
        if (dcrApplication.getPermitApplicationDate() != null) {
            asOnDate = dcrApplication.getPermitApplicationDate();
        } else if (dcrApplication.getApplicationDate() != null) {
            asOnDate = dcrApplication.getApplicationDate();
        } else {
            asOnDate = new Date();
        }

        AmendmentService repo = (AmendmentService) specificRuleService.find("amendmentService");
        Amendment amd = repo.getAmendments();

        Plan plan = extractService.extract(dcrApplication.getSavedDxfFile(), amd, asOnDate,
                featureService.getFeatures(),ApplicationThreadLocals.getTenantID());
        plan.setCoreArea(dcrApplication.getCoreArea());
        LOG.info("coreArea : " + plan.getCoreArea());

       
        plan.setMdmsMasterData(dcrApplication.getMdmsMasterData());
        
        plan = applyRules(plan, amd, cityDetails);
      
        String comparisonDcrNumber = dcrApplication.getEdcrApplicationDetails().get(0).getComparisonDcrNumber();
        if (ApplicationType.PERMIT.getApplicationTypeVal()
                .equalsIgnoreCase(dcrApplication.getApplicationType().getApplicationType())
                || (ApplicationType.OCCUPANCY_CERTIFICATE.getApplicationTypeVal()
                        .equalsIgnoreCase(dcrApplication.getApplicationType().getApplicationType())
                        && StringUtils.isBlank(comparisonDcrNumber))) {
            InputStream reportStream = generateReport(plan, amd, dcrApplication);
            saveOutputReport(dcrApplication, reportStream, plan);
        } else if (ApplicationType.OCCUPANCY_CERTIFICATE.getApplicationTypeVal()
                .equalsIgnoreCase(dcrApplication.getApplicationType().getApplicationType())
                && StringUtils.isNotBlank(comparisonDcrNumber)) {
            ComparisonRequest comparisonRequest = new ComparisonRequest();
            EdcrApplicationDetail edcrApplicationDetail = dcrApplication.getEdcrApplicationDetails().get(0);
            comparisonRequest.setEdcrNumber(edcrApplicationDetail.getComparisonDcrNumber());
            comparisonRequest.setTenantId(edcrApplicationDetail.getApplication().getThirdPartyUserTenant());
            edcrApplicationDetail.setPlan(plan);
         
            OcComparisonDetail processCombinedStatus = ocComparisonService.processCombinedStatus(comparisonRequest,
                    edcrApplicationDetail);

            dcrApplication.setDeviationStatus(processCombinedStatus.getStatus());

            InputStream reportStream = generateReport(plan, amd, dcrApplication);
            saveOutputReport(dcrApplication, reportStream, plan);
            final List<InputStream> pdfs = new ArrayList<>();
            Path path = fileStoreService.fetchAsPath(
                    dcrApplication.getEdcrApplicationDetails().get(0).getReportOutputId().getFileStoreId(),
                    "Digit DCR");
            byte[] convertedDigitDcr = null;
            try {
                convertedDigitDcr = Files.readAllBytes(path);
            } catch (IOException e) {
                LOG.error("Error occurred while reading file!!!", e);
            }
            ByteArrayInputStream dcrReport = new ByteArrayInputStream(convertedDigitDcr);
            pdfs.add(dcrReport);

            if (Boolean.TRUE.equals(plan.getMainDcrPassed())) {
                OcComparisonDetail ocComparisonE = ocComparisonService.processCombined(processCombinedStatus,
                        edcrApplicationDetail);

                String fileName;
                if(StringUtils.isBlank(ocComparisonE.getOcdcrNumber()))
                    fileName = ocComparisonE.getDcrNumber() + "-comparison" + ".pdf";
                else
                    fileName = ocComparisonE.getOcdcrNumber() + "-" + ocComparisonE.getDcrNumber() +
                        "-comparison" + ".pdf";
                final FileStoreMapper fileStoreMapper = fileStoreService.store(ocComparisonE.getOutput(), fileName,
                        "application/pdf",
                        DcrConstants.FILESTORE_MODULECODE);
                ocComparisonE.setOcComparisonReport(fileStoreMapper);
                if (StringUtils.isNotBlank(dcrApplication.getEdcrApplicationDetails().get(0).getDcrNumber())) {
                    ocComparisonE.setOcdcrNumber(dcrApplication.getEdcrApplicationDetails().get(0).getDcrNumber());
                }
                ocComparisonDetailService.saveAndFlush(ocComparisonE);

                Path ocPath = fileStoreService.fetchAsPath(ocComparisonE.getOcComparisonReport().getFileStoreId(),
                        "Digit DCR");
                byte[] convertedComparison = null;
                try {
                    convertedComparison = Files.readAllBytes(ocPath);
                } catch (IOException e) {
                    LOG.error("Error occurred while reading file!!!", e);
                }
                ByteArrayInputStream comparisonReport = new ByteArrayInputStream(convertedComparison);
                pdfs.add(comparisonReport);
            }

            final byte[] data = appendFiles(pdfs);
            InputStream targetStream = new ByteArrayInputStream(data);
            saveOutputReport(dcrApplication, targetStream, plan);
            updateFinalReport(dcrApplication.getEdcrApplicationDetails().get(0).getReportOutputId());
        }
        return plan;
    }
    
    public Plan process(EdcrApplication dcrApplication, String applicationType,EdcrRequest edcrRequest) {
        Map<String, String> cityDetails = specificRuleService.getCityDetails();
      
        Date asOnDate = null;
        if (dcrApplication.getPermitApplicationDate() != null) {
            asOnDate = dcrApplication.getPermitApplicationDate();
        } else if (dcrApplication.getApplicationDate() != null) {
            asOnDate = dcrApplication.getApplicationDate();
        } else {
            asOnDate = new Date();
        }

        AmendmentService repo = (AmendmentService) specificRuleService.find("amendmentService");
        Amendment amd = repo.getAmendments();

		List<PlanFeature> features = featureService.getFeatures();		

//		if (edcrRequest.getAreaType().equalsIgnoreCase("SCHEME_AREA")) {
//			// Get scheme name
//			if (edcrRequest.getSchName() != null && !edcrRequest.getSchName().isEmpty()) {
//				// Upload or Select layout + control sheet
//				// Master is created for scheme-wise layout and control sheets for all ULBs
//				if (edcrRequest.getSiteReserved()) {
//					if (edcrRequest.getApprovedCS()) {
//						// Exempt scrutiny
//						// uploadPDF();
//						// proceedToFormFill();
//						// as of now not implemented the code for control sheet
//					} else {
//						// No approved control sheet
//						// min plot area and road width not required
//						// uploadDXF();
//						Set<Class<?>> classesToRemove = new HashSet<>(Arrays.asList(PlotArea.class, RoadWidth.class));
//
//						features.removeIf(feature -> feature.getRuleClass() != null
//								&& classesToRemove.contains(feature.getRuleClass()));
//					}
//				} else {
//					// Not reserved
//					// Mark as "Scrutiny as per PMBL"
//					// uploadDXF();
//					// process file normally
//				}
//			} else {
//				LOG.info("Error: Scheme Name is required");
//			}
//
//		} else if (edcrRequest.getAreaType().equalsIgnoreCase("NON_SCHEME_AREA")) {
//			Set<Class<?>> classesToRemove = new HashSet<>();
//			if (edcrRequest.getCluApprove()) {
//				// Check for min plot area and road width
//				// If both present, no scrutiny needed
//				LOG.info("Min plot area and road width met. No scrutiny required.");
//				classesToRemove.addAll(Arrays.asList(PlotArea.class, RoadWidth.class));
//			}
//
//			if ("yes".equalsIgnoreCase(edcrRequest.getCoreArea())) {
//				// Exempt plot coverage, front setback and ECS
//				LOG.info("Core area: coverage, setback and ECS exempted.");
//				classesToRemove.addAll(Arrays.asList(Coverage.class, Parking.class, FrontYardService.class));
//			} else {
//				// Not a core area
//				// Scrutiny will be done as per PMBL
//				LOG.info("Scrutiny as per PMBL.");
//				// process normally
//			}
//
//			features.removeIf(
//					feature -> feature.getRuleClass() != null && classesToRemove.contains(feature.getRuleClass()));
//
//		} else {
//			// Not CLU approved
//			// Scrutiny will be done as per PMBL
//			LOG.info("Scrutiny as per PMBL.");
//			// process normally
//		}
//		
//		LOG.info("*** Features for Processing Plan file start *** ");
//		
//		features.forEach(feature -> {
//		    if (feature.getRuleClass() != null) {
//		    	LOG.info("Feature name : " + feature.getRuleClass().getSimpleName());
//		    } else {
//		    	LOG.info("Feature name : " + feature.getName());
//		    }
//		});
		
		LOG.info("*** Features for Processing Plan file end *** ");
        Plan plan = extractService.extract(dcrApplication.getSavedDxfFile(), amd, asOnDate,
                features, edcrRequest.getTenantId());
        plan.setCoreArea(dcrApplication.getCoreArea());
        LOG.info("coreArea : -> " + plan.getCoreArea());
        plan.setEdcrRequest(edcrRequest);
        LOG.info("Competency Check Role Wise");
        BigDecimal plotArea = (plan.getPlot() != null) ? plan.getPlot().getArea() : null;

        if (plotArea != null && plotArea.compareTo(BigDecimal.ZERO) > 0) {
            // Valid case → pass actual plot area
            extractService.validateRolesWisePlotArea(
                    dcrApplication.getSavedDxfFile(),
                    asOnDate,
                    plan.getEdcrRequest().getRequestInfo().getUserInfo().getRoles(),
                    plotArea,
                    plan
            );
        } else {
            // Invalid case → pass ZERO explicitly
            extractService.validateRolesWisePlotArea(
                    dcrApplication.getSavedDxfFile(),
                    asOnDate,
                    plan.getEdcrRequest().getRequestInfo().getUserInfo().getRoles(),
                    BigDecimal.ZERO,
                    plan
            );
        }
        
        // validate Source of the request
        validateSourcePortal(plan);
           
        //return (Plan) planDetail;
        // remove requestInfo before plan processing
        //edcrRequest.setRequestInfo(null);
        //Setting edcr Data to Plan        
        //plan.setEdcrRequest(edcrRequest);
        // validate planInfo city and tenantId city
        
        String cityName = getCityFromTenant(plan.getEdcrRequest().getTenantId());
        
        if (plan.getPlanInformation().getCity() == null 
                || !plan.getPlanInformation().getCity().equalsIgnoreCase(cityName)) {

            plan.getErrors().put("Invalid ULB", "Plan ULB and login ULB must be the same.");
        }        
       
        // Check Measured and Declared plot area match
        BigDecimal declaredPlotArea = plan.getPlanInformation().getPlotArea().setScale(0, RoundingMode.DOWN);
        BigDecimal measuredPlotArea = plan.getPlot().getArea().setScale(0, RoundingMode.DOWN);
        
        if(!declaredPlotArea.equals(measuredPlotArea))
        	plan.getErrors().put("Invalid Plot Area", "Declared plot area and Measured plot area must be the same.");
        
        String ulbType = "";
        String districtName = "";
        try {
            Object mdmsData = bpaMdmsUtil.getUlbTypeFromMdms(
                    new RequestInfo(), plan.getEdcrRequest()
            );

            @SuppressWarnings("unchecked")
            Map<String, Object> cityInfo =
                    (Map<String, Object>) BpaMdmsUtil
                            .extractMdmsValue(mdmsData,
                                    MdmsFilter.ULB_TYPE_AND_DISTRICT_FILTER,
                                    Map.class)
                            .orElse(Collections.emptyMap());

            ulbType = String.valueOf(cityInfo.getOrDefault("ulbType", ""));
            districtName = String.valueOf(cityInfo.getOrDefault("districtName", ""));

            if (ulbType.isEmpty()) {
                LOG.warn("ULB Type not found in MDMS response for tenant : {}",
                        plan.getEdcrRequest().getTenantId());
            }

        } catch (Exception e) {
            LOG.error("Error while fetching ULB Type and District from MDMS", e);
        }

        plan.getPlanInformation().setUlbType(ulbType);
        plan.getPlanInformation().setDistrict(districtName);

        LOG.info("ULB Type value from MDMS : {}", ulbType);
        LOG.info("District value from MDMS : {}", districtName);
        
        LOG.info("Setting mdms master data");
        plan.setMdmsMasterData(dcrApplication.getMdmsMasterData());
        LOG.info("mdms master data set successfully");
        
        if (plan.getDrawingPreference() != null &&
                org.egov.infra.utils.StringUtils.isNotBlank(plan.getDrawingPreference().getUom())
                && (DxfFileConstants.INCH_UOM.equalsIgnoreCase(plan.getDrawingPreference().getUom())
                        || DxfFileConstants.FEET_UOM.equalsIgnoreCase(plan.getDrawingPreference().getUom()))) {
        	plan.getErrors().put("units not in meters", "The Drawing should be in meters.");
		}
        
//        plan = applyRules(plan, amd, cityDetails);
        if(plan.getErrors().containsKey("Not authorized to scrutinize") || plan.getErrors().containsKey("Invalid ULB")
        		|| plan.getErrors().containsKey("units not in meters") 
        		|| plan.getErrors().containsKey("INVALID SOURCE")) {
        	
        }else {
        	plan = applyRules(plan, amd, cityDetails,features);
        }
        
        LOG.info("Competency Role Checked successfully ");
        
      
        String comparisonDcrNumber = dcrApplication.getEdcrApplicationDetails().get(0).getComparisonDcrNumber();
        if (ApplicationType.PERMIT.getApplicationTypeVal()
                .equalsIgnoreCase(dcrApplication.getApplicationType().getApplicationType())
                || (ApplicationType.OCCUPANCY_CERTIFICATE.getApplicationTypeVal()
                        .equalsIgnoreCase(dcrApplication.getApplicationType().getApplicationType())
                        && StringUtils.isBlank(comparisonDcrNumber))
               || (ApplicationType.BUILDING_PLAN_SCRUTINY.getApplicationTypeVal()
                        .equalsIgnoreCase(dcrApplication.getApplicationType().getApplicationType())
                        && StringUtils.isBlank(comparisonDcrNumber))
                ) {
            InputStream reportStream = generateReport(plan, amd, dcrApplication);
            saveOutputReport(dcrApplication, reportStream, plan);
        } else if (ApplicationType.OCCUPANCY_CERTIFICATE.getApplicationTypeVal()
                .equalsIgnoreCase(dcrApplication.getApplicationType().getApplicationType())
                && StringUtils.isNotBlank(comparisonDcrNumber)) {
            ComparisonRequest comparisonRequest = new ComparisonRequest();
            EdcrApplicationDetail edcrApplicationDetail = dcrApplication.getEdcrApplicationDetails().get(0);
            comparisonRequest.setEdcrNumber(edcrApplicationDetail.getComparisonDcrNumber());
            comparisonRequest.setTenantId(edcrApplicationDetail.getApplication().getThirdPartyUserTenant());
            edcrApplicationDetail.setPlan(plan);
         
            OcComparisonDetail processCombinedStatus = ocComparisonService.processCombinedStatus(comparisonRequest,
                    edcrApplicationDetail);

            dcrApplication.setDeviationStatus(processCombinedStatus.getStatus());

            InputStream reportStream = generateReport(plan, amd, dcrApplication);
            saveOutputReport(dcrApplication, reportStream, plan);
            final List<InputStream> pdfs = new ArrayList<>();
            Path path = fileStoreService.fetchAsPath(
                    dcrApplication.getEdcrApplicationDetails().get(0).getReportOutputId().getFileStoreId(),
                    "Digit DCR");
            byte[] convertedDigitDcr = null;
            try {
                convertedDigitDcr = Files.readAllBytes(path);
            } catch (IOException e) {
                LOG.error("Error occurred while reading file!!!", e);
            }
            ByteArrayInputStream dcrReport = new ByteArrayInputStream(convertedDigitDcr);
            pdfs.add(dcrReport);

            if (Boolean.TRUE.equals(plan.getMainDcrPassed())) {
                OcComparisonDetail ocComparisonE = ocComparisonService.processCombined(processCombinedStatus,
                        edcrApplicationDetail);

                String fileName;
                if(StringUtils.isBlank(ocComparisonE.getOcdcrNumber()))
                    fileName = ocComparisonE.getDcrNumber() + "-comparison" + ".pdf";
                else
                    fileName = ocComparisonE.getOcdcrNumber() + "-" + ocComparisonE.getDcrNumber() +
                        "-comparison" + ".pdf";
                final FileStoreMapper fileStoreMapper = fileStoreService.store(ocComparisonE.getOutput(), fileName,
                        "application/pdf",
                        DcrConstants.FILESTORE_MODULECODE);
                ocComparisonE.setOcComparisonReport(fileStoreMapper);
                if (StringUtils.isNotBlank(dcrApplication.getEdcrApplicationDetails().get(0).getDcrNumber())) {
                    ocComparisonE.setOcdcrNumber(dcrApplication.getEdcrApplicationDetails().get(0).getDcrNumber());
                }
                ocComparisonDetailService.saveAndFlush(ocComparisonE);

                Path ocPath = fileStoreService.fetchAsPath(ocComparisonE.getOcComparisonReport().getFileStoreId(),
                        "Digit DCR");
                byte[] convertedComparison = null;
                try {
                    convertedComparison = Files.readAllBytes(ocPath);
                } catch (IOException e) {
                    LOG.error("Error occurred while reading file!!!", e);
                }
                ByteArrayInputStream comparisonReport = new ByteArrayInputStream(convertedComparison);
                pdfs.add(comparisonReport);
            }

            final byte[] data = appendFiles(pdfs);
            InputStream targetStream = new ByteArrayInputStream(data);
            saveOutputReport(dcrApplication, targetStream, plan);
            updateFinalReport(dcrApplication.getEdcrApplicationDetails().get(0).getReportOutputId());
        }
        return plan;
    }

    public void savePlanDetail(Plan plan, EdcrApplicationDetail detail) {

        if (LOG.isInfoEnabled())
            LOG.info("*************Before serialization******************");
        File f = new File("plandetail.txt");
        try (FileOutputStream fos = new FileOutputStream(f); ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            mapper.writeValue(f, plan);
            detail.setPlanDetailFileStore(
                    fileStoreService.store(f, f.getName(), "text/plain", DcrConstants.APPLICATION_MODULE_TYPE));
            oos.flush();
        } catch (IOException e) {
            LOG.error("Unable to serialize!!!!!!", e);
        }
        if (LOG.isInfoEnabled())
            LOG.info("*************Completed serialization******************");

    }

    private Plan applyRules(Plan plan, Amendment amd, Map<String, String> cityDetails) {
    	LOG.info("Inside apply Rules");

        // check whether valid amendments are present
        int index = -1;
        AmendmentDetails[] a = null;
        int length = amd.getDetails().size();
        if (!amd.getDetails().isEmpty()) {
            index = amd.getIndex(plan.getApplicationDate());
            a = new AmendmentDetails[amd.getDetails().size()];
            amd.getDetails().toArray(a);
        }

        for (PlanFeature ruleClass : featureService.getFeatures()) {

            FeatureProcess rule = null;
            String str = ruleClass.getRuleClass().getSimpleName();
            str = str.substring(0, 1).toLowerCase() + str.substring(1);
            LOG.info("Looking for bean " + str);
            // when amendments are not present
            if (amd.getDetails().isEmpty() || index == -1)
                rule = (FeatureProcess) specificRuleService.find(ruleClass.getRuleClass().getSimpleName());
            // when amendments are present
            else {
                if (index >= 0) {
                    // find amendment specific beans
                    for (int i = index; i < length; i++) {
                        if (a[i].getChanges().keySet().contains(ruleClass.getRuleClass().getSimpleName())) {
                            String strNew = str + "_" + a[i].getDateOfBylawString();
                            rule = (FeatureProcess) specificRuleService.find(strNew);
                            if (rule != null)
                                break;
                        }
                    }
                    // when amendment specific beans not found
                    if (rule == null) {
                        rule = (FeatureProcess) specificRuleService.find(ruleClass.getRuleClass().getSimpleName());
                    }

                }

            }

            if (rule != null) {
                LOG.info("Looking for bean resulted in " + rule.getClass().getSimpleName());
                rule.process(plan);
                LOG.info("Completed Process " + rule.getClass().getSimpleName() + "  " + new Date());
            }

            if (plan.getErrors().containsKey(DxfFileConstants.OCCUPANCY_ALLOWED_KEY)
                    || plan.getErrors().containsKey("units not in meters")
                    || plan.getErrors().containsKey(DxfFileConstants.OCCUPANCY_PO_NOT_ALLOWED_KEY))
                return plan;
        }
        LOG.info("Exit from apply Rules");
        return plan;
    }
    
//    private Plan applyRules(Plan plan, Amendment amd, Map<String, String> cityDetails, List<PlanFeature> feature) {
//
//        // check whether valid amendments are present
//       int index = -1;
//        AmendmentDetails[] a = null;
//        int length = amd.getDetails().size();
//        if (!amd.getDetails().isEmpty()) {
//            index = amd.getIndex(plan.getApplicationDate());
//            a = new AmendmentDetails[amd.getDetails().size()];
//            amd.getDetails().toArray(a);
//        }
//
//        for (PlanFeature ruleClass : feature) {
//
//            FeatureProcess rule = null;
//            String str = ruleClass.getRuleClass().getSimpleName();
//            str = str.substring(0, 1).toLowerCase() + str.substring(1);
//            LOG.info("Looking for bean " + str);
//            // when amendments are not present
//            if (amd.getDetails().isEmpty() || index == -1)
//                rule = (FeatureProcess) specificRuleService.find(ruleClass.getRuleClass().getSimpleName());
//            // when amendments are present
//            else {
//                if (index >= 0) {
//                    // find amendment specific beans
//                    for (int i = index; i < length; i++) {
//                        if (a[i].getChanges().keySet().contains(ruleClass.getRuleClass().getSimpleName())) {
//                            String strNew = str + "_" + a[i].getDateOfBylawString();
//                            rule = (FeatureProcess) specificRuleService.find(strNew);
//                            if (rule != null)
//                                break;
//                        }
//                    }
//                    // when amendment specific beans not found
//                    if (rule == null) {
//                        rule = (FeatureProcess) specificRuleService.find(ruleClass.getRuleClass().getSimpleName());
//                    }
//
//                }
//
//            }
//
//            if (rule != null) {
//                LOG.info("Looking for bean resulted in " + rule.getClass().getSimpleName());
//                rule.process(plan);
//                LOG.info("Completed Process " + rule.getClass().getSimpleName() + "  " + new Date());
//            }
//
//            if (plan.getErrors().containsKey(DxfFileConstants.OCCUPANCY_ALLOWED_KEY)
//                    || plan.getErrors().containsKey("units not in meters")
//                    || plan.getErrors().containsKey(DxfFileConstants.OCCUPANCY_PO_NOT_ALLOWED_KEY))
//                return plan;
//        }
//        return plan;
//    }
    
    private Plan applyRules(Plan plan, Amendment amd, Map<String, String> cityDetails, List<PlanFeature> feature) {
    try {
        int index = -1;
        AmendmentDetails[] amendmentArray = null;
        int length = 0;

        if (amd != null && amd.getDetails() != null && !amd.getDetails().isEmpty()) {
            length = amd.getDetails().size();
            index = amd.getIndex(plan.getApplicationDate());
            amendmentArray = amd.getDetails().toArray(new AmendmentDetails[0]);
        }

        if (feature != null) {
            for (PlanFeature ruleClass : feature) {
                String featureName = "UnknownFeature";

                try {
                    if (ruleClass == null || ruleClass.getRuleClass() == null) {
                        continue;
                    }

                    featureName = ruleClass.getRuleClass().getSimpleName();
                    FeatureProcess rule = null;

                    String beanName =
                            featureName.substring(0, 1).toLowerCase() + featureName.substring(1);

                    LOG.info("Looking for bean {}", beanName);

                    // When amendments are NOT present
                    if (amd == null || amd.getDetails() == null || amd.getDetails().isEmpty() || index == -1) {
                        rule = (FeatureProcess) specificRuleService.find(featureName);
                    }
                    // When amendments ARE present
                    else {
                        for (int i = index; i < length; i++) {
                            if (amendmentArray[i].getChanges().containsKey(featureName)) {
                                String amendedBean = beanName + "_" + amendmentArray[i].getDateOfBylawString();
                                rule = (FeatureProcess) specificRuleService.find(amendedBean);

                                if (rule != null) {
                                    break;
                                }
                            }
                        }

                        // Fallback to default rule
                        if (rule == null) {
                            rule = (FeatureProcess) specificRuleService.find(featureName);
                        }
                    }

                    if (rule != null) {
                        LOG.info("Processing rule {}", rule.getClass().getSimpleName());
                        rule.process(plan);
                        LOG.info("Completed rule {} at {}", rule.getClass().getSimpleName(), new Date());
                    }

                    if (plan.getErrors().containsKey(DxfFileConstants.OCCUPANCY_ALLOWED_KEY)
                            || plan.getErrors().containsKey("units not in meters")
                            || plan.getErrors().containsKey(DxfFileConstants.OCCUPANCY_PO_NOT_ALLOWED_KEY)) {
                        return plan;
                    }

                } catch (Exception e) {
                    // FULL STACKTRACE logged
                    LOG.error("Exception while processing feature: {}", featureName, e);

                    plan.getErrors().put(
                            "Errors in \"" + featureName + "\"",
                            "Errors in " + featureName + ". Please correct the file and try again."
                    );
                }
            }
        }
    } catch (Exception e) {
        // FULL STACKTRACE logged
        LOG.error("Critical error in applyRules()", e);

        plan.getErrors().put(
                "System Error",
                "Please correct the file and try again."
        );
    }

    return plan;
}


    private InputStream generateReport(Plan plan, Amendment amd, EdcrApplication dcrApplication) {

        String beanName = "PlanReportServiceV2";
        PlanReportServiceV2 service = null;
        int index = -1;
        AmendmentDetails[] amdArray = null;
        InputStream reportStream = null;
        int length = amd.getDetails().size();
        if (!amd.getDetails().isEmpty()) {
            index = amd.getIndex(plan.getApplicationDate());
            amdArray = new AmendmentDetails[amd.getDetails().size()];
            amd.getDetails().toArray(amdArray);
        }

        try {
            beanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1);

            if (amd.getDetails().isEmpty() || index == -1)
                service = (PlanReportServiceV2) specificRuleService.find(beanName);
            else if (index >= 0) {
                for (int i = index; i < length; i++) {

                    service = (PlanReportServiceV2) specificRuleService
                            .find(beanName + "_" + amdArray[i].getDateOfBylawString());
                    if (service != null)
                        break;
                }
            }
            if (service == null) {
                service = (PlanReportServiceV2) specificRuleService.find(beanName);
            }

            reportStream = service.generateReport(plan, dcrApplication);

        } catch (BeansException e) {
            LOG.error("No Bean Defined for the Rule " + beanName);
        }

        return reportStream;
    }

    @Transactional
    public void saveOutputReport(EdcrApplication edcrApplication, InputStream reportOutputStream, Plan plan) {

        List<EdcrApplicationDetail> edcrApplicationDetails = edcrApplicationDetailService
                .fingByDcrApplicationId(edcrApplication.getId());
        final String fileName = edcrApplication.getApplicationNumber() + "-v" + edcrApplicationDetails.size() + ".pdf";

        final FileStoreMapper fileStoreMapper = fileStoreService.store(reportOutputStream, fileName, "application/pdf",
                DcrConstants.FILESTORE_MODULECODE);

        buildDocuments(edcrApplication, null, fileStoreMapper, plan);

        PlanInformation planInformation = plan.getPlanInformation();
        edcrApplication.getEdcrApplicationDetails().get(0).setPlanInformation(planInformation);
        edcrApplicationDetailService.saveAll(edcrApplication.getEdcrApplicationDetails());
    }

    public void buildDocuments(EdcrApplication edcrApplication, FileStoreMapper dxfFile, FileStoreMapper reportOutput,
            Plan plan) {

        if (dxfFile != null) {
            EdcrApplicationDetail edcrApplicationDetail = new EdcrApplicationDetail();

            edcrApplicationDetail.setDxfFileId(dxfFile);
            edcrApplicationDetail.setApplication(edcrApplication);
            for (EdcrApplicationDetail edcrApplicationDetail1 : edcrApplication.getEdcrApplicationDetails()) {
                edcrApplicationDetail.setPlan(edcrApplicationDetail1.getPlan());
            }
            List<EdcrApplicationDetail> edcrApplicationDetails = new ArrayList<>();
            edcrApplicationDetails.add(edcrApplicationDetail);
            edcrApplication.setSavedEdcrApplicationDetail(edcrApplicationDetail);
            edcrApplication.setEdcrApplicationDetails(edcrApplicationDetails);
        }

        if (reportOutput != null) {
            EdcrApplicationDetail edcrApplicationDetail = edcrApplication.getEdcrApplicationDetails().get(0);

            if (plan.getEdcrPassed()) {
                edcrApplicationDetail.setStatus("Accepted");
                edcrApplication.setStatus("Accepted");
            } else {
                edcrApplicationDetail.setStatus("Not Accepted");
                edcrApplication.setStatus("Not Accepted");
            }
            edcrApplicationDetail.setCreatedDate(new Date());
            edcrApplicationDetail.setReportOutputId(reportOutput);
            List<EdcrApplicationDetail> edcrApplicationDetails = new ArrayList<>();
            edcrApplicationDetails.add(edcrApplicationDetail);
            savePlanDetail(plan, edcrApplicationDetail);

            ArrayList<org.egov.edcr.entity.EdcrPdfDetail> edcrPdfDetails = new ArrayList<>();

            if (plan.getEdcrPdfDetails() != null && !plan.getEdcrPdfDetails().isEmpty()) {
                for (EdcrPdfDetail edcrPdfDetail : plan.getEdcrPdfDetails()) {
                    org.egov.edcr.entity.EdcrPdfDetail pdfDetail = new org.egov.edcr.entity.EdcrPdfDetail();
                    pdfDetail.setLayer(edcrPdfDetail.getLayer());
                    pdfDetail.setFailureReasons(edcrPdfDetail.getFailureReasons());
                    pdfDetail.setStandardViolations(edcrPdfDetail.getStandardViolations());

                    File convertedPdf = edcrPdfDetail.getConvertedPdf();
                    if (convertedPdf != null && convertedPdf.length() > 0) {
                        FileStoreMapper fileStoreMapper = fileStoreService.store(convertedPdf, convertedPdf.getName(),
                                DcrConstants.PDF_EXT, DcrConstants.FILESTORE_MODULECODE);
                        pdfDetail.setConvertedPdf(fileStoreMapper);
                        pdfDetail.setEdcrApplicationDetail(edcrApplicationDetail);
                        edcrPdfDetails.add(pdfDetail);
                    }
                }
            }

            if (!edcrPdfDetails.isEmpty()) {
                edcrApplicationDetail.getEdcrPdfDetails().addAll(edcrPdfDetails);
                edcrPdfDetailService.saveAll(edcrPdfDetails);
            }

            edcrApplication.setEdcrApplicationDetails(edcrApplicationDetails);
        }
    }

    public Plan extractPlan(EdcrRequest edcrRequest, MultipartFile dxfFile) {
        File planFile = edcrApplicationService.savePlanDXF(dxfFile);

        Date asOnDate = new Date();

        AmendmentService repo = (AmendmentService) specificRuleService.find(AmendmentService.class.getSimpleName());
        Amendment amd = repo.getAmendments();

        Plan plan = extractService.extract(planFile, amd, asOnDate, featureService.getFeatures(), edcrRequest.getTenantId());
        if (StringUtils.isNotBlank(edcrRequest.getApplicantName()))
            plan.getPlanInformation().setApplicantName(edcrRequest.getApplicantName());
        else
            plan.getPlanInformation().setApplicantName(DxfFileConstants.ANONYMOUS_APPLICANT);

        return plan;
    }

    private void updateFinalReport(FileStoreMapper fileStoreMapper) {
        try {
            Path path = fileStoreService.fetchAsPath(fileStoreMapper.getFileStoreId(),
                    "Digit DCR");

            PDDocument doc = PDDocument.load(new File(path.toString()));
            for (int i = 0; i < doc.getNumberOfPages(); i++) {
                PDPage page = doc.getPage(i);
                PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND,
                        true);
                /*
                 * if (i == 0) { contentStream.setNonStrokingColor(Color.white); contentStream.addRect(275, 720, 60, 20);
                 * contentStream.fill(); contentStream.setNonStrokingColor(Color.black); contentStream.beginText();
                 * contentStream.newLineAtOffset(275, 720); contentStream.setFont(PDType1Font.TIMES_BOLD, 12); if
                 * ("Not Accepted".equalsIgnoreCase(status)) { contentStream.setNonStrokingColor(Color.RED); } else {
                 * contentStream.setNonStrokingColor(0,127,0); } contentStream.showText(status); contentStream.endText(); }
                 */
                // page coordinate
                contentStream.setNonStrokingColor(Color.white);
                contentStream.addRect(230, 20, 80, 40);
                contentStream.fill();

                contentStream.setNonStrokingColor(Color.black);
                contentStream.beginText();

                contentStream.newLineAtOffset(248, 23);

                contentStream.setFont(PDType1Font.TIMES_ROMAN, 10);
                String text = (i + 1) + " of " + doc.getNumberOfPages();
                contentStream.showText(text);
                contentStream.endText();
                contentStream.close();
            }
            doc.save(new File(path.toString()));
            doc.close();
        } catch (IOException e) {
            LOG.error("error", e);
        }
    }
    
    public static String getCityFromTenant(String tenantId) {
        if (tenantId != null && tenantId.contains(".")) {
            String[] parts = tenantId.split("\\.");
            return parts.length > 1 ? parts[1] : tenantId;
        }
        return tenantId;
    }
    
    
    public OccupancyTypeHelper getOccTypeSubType(Plan plan) {

    if (plan == null || plan.getBlocks() == null || plan.getBlocks().isEmpty()) {
        return null;
    }

    Set<OccupancyTypeHelper> occupancyTypes = new HashSet<>();

    for (Block block : plan.getBlocks()) {

        Building building = block.getBuilding();
        if (building == null || building.getFloors() == null) {
            continue;
        }

        for (Floor floor : building.getFloors()) {
            if (floor.getOccupancies() == null) {
                continue;
            }
            for (Occupancy occupancy : floor.getOccupancies()) {
                if (occupancy != null && occupancy.getTypeHelper() != null) {
                    occupancyTypes.add(occupancy.getTypeHelper());
                }
            }
        }
    }

    return getMostRestrictiveFar(occupancyTypes);
}
    
	protected OccupancyTypeHelper getMostRestrictiveFar(Set<OccupancyTypeHelper> distinctOccupancyTypes) {

	    if (distinctOccupancyTypes == null || distinctOccupancyTypes.isEmpty()) {
	        return null;
	    }

	    Map<String, OccupancyTypeHelper> codeMap = new HashMap<>();

	    for (OccupancyTypeHelper helper : distinctOccupancyTypes) {

	        if (helper.getType() != null) {
	            codeMap.put(helper.getType().getCode(), helper);
	        }

	        if (helper.getSubtype() != null) {
	            codeMap.put(helper.getSubtype().getCode(), helper);
	        }
	    }

	    for (String priorityCode : FAR_PRIORITY) {
	        OccupancyTypeHelper helper = codeMap.get(priorityCode);
	        if (helper != null) {
	            return helper;
	        }
	    }

	    return null;
	}
	
	private void validateSourcePortal(Plan pl) {

		try {
			OccupancyTypeHelper mostRestrictiveFar = getOccTypeSubType(pl);

			if (mostRestrictiveFar == null || mostRestrictiveFar.getType() == null
					|| mostRestrictiveFar.getSubtype() == null || mostRestrictiveFar.getSubtype().getCode() == null) {
				LOG.info("validateSourcePortal: mostRestrictiveFar/type/subtype is null, skipping validation.");
				return;
			}

			String occTypeCode = mostRestrictiveFar.getType().getCode();
			String occSubTypeCode = mostRestrictiveFar.getSubtype().getCode();

			String sourceType = getSourceViaReflection(
					pl.getEdcrRequest() != null ? pl.getEdcrRequest().getAdditionalDetails() : null);

			LOG.info("validateSourcePortal: sourceType=" + sourceType + ", occTypeCode=" + occTypeCode
					+ ", occSubTypeCode=" + occSubTypeCode);

			if (sourceType == null) {
				LOG.info("validateSourcePortal: sourceType is null, skipping validation.");
				return;
			}

			boolean isGorLOrCommHousing = G.equals(occTypeCode) || L.equals(occTypeCode)
					|| (F.equals(occTypeCode) && F_HM.equals(occSubTypeCode));

			if (SOURCE_INVESTPUNJAB.equalsIgnoreCase(sourceType)) {
				// INVESTPUNJAB: G / L / F-HM -> OK, continue. Anything else -> error.
				if (!isGorLOrCommHousing) {
					String errorMsg = "Please process this file through the OBPAS portal.";
					LOG.info("validateSourcePortal: VALIDATION FAILED -> sourceType=" + sourceType + ", occTypeCode="
							+ occTypeCode + ", occSubTypeCode=" + occSubTypeCode + ", error=" + errorMsg);
					pl.addError(INVALID_SOURCE, errorMsg);
				}

			} else if (SOURCE_OBPAS.equalsIgnoreCase(sourceType)) {
				// OBPAS: G / L / F-HM -> error (must use Invest Punjab). Anything else -> OK.
				if (isGorLOrCommHousing) {
					String errorMsg = "Please process this file through the Invest Punjab portal.";
					LOG.info("validateSourcePortal: VALIDATION FAILED -> sourceType=" + sourceType + ", occTypeCode="
							+ occTypeCode + ", occSubTypeCode=" + occSubTypeCode + ", error=" + errorMsg);
					pl.addError(INVALID_SOURCE + "1", ERROR_MSG);
				}
			}

		} catch (Exception e) {
			LOG.error("validateSourcePortal: Error while validating source portal for occupancy type", e);
		}
	}

	private String getSourceViaReflection(Object additionalDetails) {
		if (additionalDetails == null) {
			return null;
		}
		Map<String, Object> details = (Map<String, Object>) additionalDetails;
		Object source = details.get("source");
		if (source == null || StringUtils.isBlank(String.valueOf(source))) {
			return null;
		}

		return (String) source;
	}
	
}


