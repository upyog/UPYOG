package org.egov.edcr.service;

import static org.egov.edcr.utility.DcrConstants.FILESTORE_MODULECODE;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.Collections;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.multipdf.LayerUtility;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.util.Matrix;
import org.egov.common.entity.edcr.Plan;
import org.egov.commons.mdms.BpaMdmsUtil;
import org.egov.commons.service.RestCallService;
//import org.egov.edcr.contract.EdcrRequest;
import org.egov.common.edcr.model.EdcrRequest;
import org.egov.edcr.contract.EdcrDetail;
import org.egov.edcr.entity.ApplicationType;
import org.egov.edcr.entity.EdcrApplication;
import org.egov.edcr.entity.EdcrApplicationDetail;
import org.egov.edcr.entity.SearchBuildingPlanScrutinyForm;
import org.egov.edcr.repository.EdcrApplicationDetailRepository;
import org.egov.edcr.repository.EdcrApplicationRepository;
import org.egov.edcr.service.es.EdcrIndexService;
import org.egov.edcr.utility.DcrConstants;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.config.persistence.datasource.routing.annotation.ReadOnly;
import org.egov.infra.filestore.entity.FileStoreMapper;
import org.egov.infra.filestore.repository.FileStoreMapperRepository;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.microservice.contract.RequestInfoWrapper;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.utils.ApplicationNumberGenerator;
import org.hibernate.Session;
import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.aspose.cad.Color;
import com.aspose.cad.Image;
import com.aspose.cad.fileformats.cad.CadDrawTypeMode;
import com.aspose.cad.imageoptions.CadRasterizationOptions;
import com.aspose.cad.imageoptions.PdfDocumentOptions;
import com.aspose.cad.imageoptions.PdfOptions;
import com.aspose.cad.imageoptions.UnitType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;

import java.io.File;
import java.io.IOException;
//PDFBox - Document & Page
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

//PDFBox - Content Stream
import org.apache.pdfbox.pdmodel.PDPageContentStream;

//PDFBox - Font (PDFBox 2.0.x)
import org.apache.pdfbox.pdmodel.font.PDType1Font;

//PDFBox - Text Extraction (core pdfbox, no pdfbox-tools needed)
import org.apache.pdfbox.text.PDFTextStripper;

//Java IO
import java.io.File;
import java.io.IOException;

//Java AWT - Region for PDFTextStripperByRegion
import java.awt.geom.Rectangle2D;

//Java IO
import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.JsonPath;

//import com.aspose.cad.Color;
//import com.aspose.cad.Image;
//import com.aspose.cad.fileformats.cad.CadDrawTypeMode;
//import com.aspose.cad.imageoptions.CadRasterizationOptions;
//import com.aspose.cad.imageoptions.PdfOptions;

import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
public class EdcrApplicationService {
    private static final String RESUBMIT_SCRTNY = "Resubmit Plan Scrutiny";
    private static final String NEW_SCRTNY = "New Plan Scrutiny";
    public static final String ULB_NAME = "ulbName";
    public static final String ABORTED = "Aborted";
    private static Logger LOG = LogManager.getLogger(EdcrApplicationService.class);
        
    private static final float EXPAND_RIGHT  = 80f;
    private static final float EXPAND_BOTTOM = 80f;
    private static final String BLANK_TEXT = "-";
    
    // GAP CONTROLS  ← spacing between elements
    private static final float GAP_DRAWING_TO_TABLES = 20f;
 
    /*Gap from the top edge of the (expanded) page to the first table row. */
    private static final float GAP_TOP = 15f;
    
    //private static final PDFont TIMESTAMP_FONT = PDType1Font.HELVETICA_BOLD;
    private static final DateTimeFormatter TS_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Autowired
    protected SecurityUtils securityUtils;

    @Autowired
    private EdcrApplicationRepository edcrApplicationRepository;

    @Autowired
    private EdcrApplicationDetailRepository edcrApplicationDetailRepository;

    @Autowired
    private PlanService planService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private FileStoreService fileStoreService;

    @Autowired
    private ApplicationNumberGenerator applicationNumberGenerator;

    @Autowired
    private EdcrIndexService edcrIndexService;
    
    private RestCallService serviceRequestRepository;
    
    @Autowired
    private EdcrApplicationDetailService edcrApplicationDetailService;

    @Autowired
    private PdfOverlayTemplateService pdfOverlayTemplateService;
    
    @Autowired
    private BpaMdmsUtil bpaMdmsUtil;
    
    @Autowired
    private EdcrRestService edcrRestService;
    
    @Autowired
    private FileStoreMapperRepository fileStoreMapperRepository;
    
    @Autowired
    private PlanReportServiceV2 planReportServiceV2;
    

    public Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }
    
    public EdcrApplicationService(RestCallService serviceRequestRepository) {
		this.serviceRequestRepository = serviceRequestRepository;		
	}
    

    @Transactional
    public EdcrApplication create(final EdcrApplication edcrApplication) {

        // edcrApplication.setApplicationDate(new Date("01/01/2020"));
        edcrApplication.setApplicationDate(new Date());
        edcrApplication.setApplicationNumber(applicationNumberGenerator.generate());
        edcrApplication.setSavedDxfFile(saveDXF(edcrApplication));
        edcrApplication.setStatus(ABORTED);

        edcrApplicationRepository.save(edcrApplication);
        
        edcrIndexService.updateIndexes(edcrApplication, NEW_SCRTNY);

        callDcrProcess(edcrApplication, NEW_SCRTNY);
        edcrIndexService.updateIndexes(edcrApplication, NEW_SCRTNY);

        return edcrApplication;
    }

    @Transactional
    public EdcrApplication update(final EdcrApplication edcrApplication) {
        edcrApplication.setSavedDxfFile(saveDXF(edcrApplication));
        edcrApplication.setStatus(ABORTED);
        Plan unsavedPlanDetail = edcrApplication.getEdcrApplicationDetails().get(0).getPlan();
        EdcrApplication applicationRes = edcrApplicationRepository.save(edcrApplication);
        edcrApplication.getEdcrApplicationDetails().get(0).setPlan(unsavedPlanDetail);

        edcrIndexService.updateIndexes(edcrApplication, RESUBMIT_SCRTNY);

        callDcrProcess(edcrApplication, RESUBMIT_SCRTNY);

        return applicationRes;
    }

    private Plan callDcrProcess(EdcrApplication edcrApplication, String applicationType, EdcrRequest edcrRequest){
        Plan planDetail = new Plan();
        planDetail = planService.process(edcrApplication, applicationType, edcrRequest);         
        updateFilev2(planDetail, edcrApplication);
        edcrApplicationDetailService.saveAll(edcrApplication.getEdcrApplicationDetails());
        return planDetail;
    }
    
    private Plan callDcrProcess(EdcrApplication edcrApplication, String applicationType){
        Plan planDetail = new Plan();
        planDetail = planService.process(edcrApplication, applicationType);
        updateFile(planDetail, edcrApplication);
        edcrApplicationDetailService.saveAll(edcrApplication.getEdcrApplicationDetails());
        return planDetail;
    }

    private File saveDXF(EdcrApplication edcrApplication) {
        FileStoreMapper fileStoreMapper = addToFileStore(edcrApplication.getDxfFile());
        LOG.info("Returned FileStoreMapper : {}", fileStoreMapper);
        if (fileStoreMapper == null) {
            LOG.error("FileStoreMapper is null");
            return null;
        }

        LOG.info("FileStoreId : {}", fileStoreMapper.getFileStoreId());
        LOG.info("TenantId    : {}", fileStoreMapper.getTenantId());
        
        File dxfFile = fileStoreService.fetch(fileStoreMapper.getFileStoreId(), FILESTORE_MODULECODE);
        planService.buildDocuments(edcrApplication, fileStoreMapper, null, null);
        List<EdcrApplicationDetail> edcrApplicationDetails = edcrApplication.getEdcrApplicationDetails();
        edcrApplicationDetails.get(0).setStatus(ABORTED);
        edcrApplication.setEdcrApplicationDetails(edcrApplicationDetails);
        return dxfFile;

    }

    public File savePlanDXF(final MultipartFile file) {
        FileStoreMapper fileStoreMapper = addToFileStore(file);
        return fileStoreService.fetch(fileStoreMapper.getFileStoreId(), FILESTORE_MODULECODE);
    }

    private FileStoreMapper addToFileStore(final MultipartFile file) {
        FileStoreMapper fileStoreMapper = null;
        try {
            fileStoreMapper = fileStoreService.store(file.getInputStream(), file.getOriginalFilename(),
                    file.getContentType(), FILESTORE_MODULECODE);
        } catch (final IOException e) {
            LOG.error("Error occurred, while getting input stream!!!!!", e);
        }
        return fileStoreMapper;
    }

    public List<EdcrApplication> findAll() {
        return edcrApplicationRepository.findAll(new Sort(Sort.Direction.ASC, "name"));
    }

    public EdcrApplication findOne(Long id) {
        return edcrApplicationRepository.findOne(id);
    }

    public EdcrApplication findByApplicationNo(String appNo) {
        return edcrApplicationRepository.findByApplicationNumber(appNo);
    }

    public EdcrApplication findByApplicationNoAndType(String applnNo, ApplicationType type) {
        return edcrApplicationRepository.findByApplicationNumberAndApplicationType(applnNo, type);
    }

    public EdcrApplication findByPlanPermitNumber(String permitNo) {
        return edcrApplicationRepository.findByPlanPermitNumber(permitNo);
    }

    public EdcrApplication findByTransactionNumber(String transactionNo) {
        return edcrApplicationRepository.findByTransactionNumber(transactionNo);
    }

    public EdcrApplication findByTransactionNumberAndTPUserCode(String transactionNo, String userCode) {
        return edcrApplicationRepository.findByTransactionNumberAndThirdPartyUserCode(transactionNo, userCode);
    }

    public List<EdcrApplication> search(EdcrApplication edcrApplication) {
        return edcrApplicationRepository.findAll();
    }

    public List<EdcrApplication> findByThirdPartyUserCode(String userCode) {
        return edcrApplicationRepository.findByThirdPartyUserCode(userCode);
    }

    public List<EdcrApplication> getEdcrApplications() {
        Pageable pageable = new PageRequest(0, 25, Sort.Direction.DESC, "id");
        Page<EdcrApplication> edcrApplications = edcrApplicationRepository.findAll(pageable);
        return edcrApplications.getContent();
    }

    @ReadOnly
    public Page<SearchBuildingPlanScrutinyForm> planScrutinyPagedSearch(SearchBuildingPlanScrutinyForm searchRequest) {
        final Pageable pageable = new PageRequest(searchRequest.pageNumber(), searchRequest.pageSize(),
                searchRequest.orderDir(), searchRequest.orderBy());
        List<SearchBuildingPlanScrutinyForm> searchResults = new ArrayList<>();
        Page<EdcrApplicationDetail> dcrApplications = edcrApplicationDetailRepository
                .findAll(DcrReportSearchSpec.searchReportsSpecification(searchRequest), pageable);
        for (EdcrApplicationDetail applicationDetail : dcrApplications)
            searchResults.add(buildResponseAsPerForm(applicationDetail));
        return new PageImpl<>(searchResults, pageable, dcrApplications.getTotalElements());
    }

    private SearchBuildingPlanScrutinyForm buildResponseAsPerForm(EdcrApplicationDetail applicationDetail) {
        SearchBuildingPlanScrutinyForm planScrtnyFrm = new SearchBuildingPlanScrutinyForm();
        EdcrApplication application = applicationDetail.getApplication();
        planScrtnyFrm.setApplicationNumber(application.getApplicationNumber());
        planScrtnyFrm.setApplicationDate(application.getApplicationDate());
        planScrtnyFrm.setApplicantName(application.getApplicantName());
        planScrtnyFrm.setBuildingPlanScrutinyNumber(applicationDetail.getDcrNumber());
        planScrtnyFrm.setUploadedDateAndTime(applicationDetail.getCreatedDate());
        if (applicationDetail.getDxfFileId() != null)
            planScrtnyFrm.setDxfFileStoreId(applicationDetail.getDxfFileId().getFileStoreId());
        if (applicationDetail.getDxfFileId() != null)
            planScrtnyFrm.setDxfFileName(applicationDetail.getDxfFileId().getFileName());
        if (applicationDetail.getReportOutputId() != null)
            planScrtnyFrm.setReportOutputFileStoreId(applicationDetail.getReportOutputId().getFileStoreId());
        if (applicationDetail.getReportOutputId() != null)
            planScrtnyFrm.setReportOutputFileName(applicationDetail.getReportOutputId().getFileName());
        planScrtnyFrm.setStakeHolderId(application.getCreatedBy().getId());
        planScrtnyFrm.setStatus(applicationDetail.getStatus());
        planScrtnyFrm.setBuildingLicenceeName(application.getCreatedBy().getName());
        return planScrtnyFrm;
    }

    private static String readFile(File srcFile) {
        String fileAsString = null;
        try {
            String canonicalPath = srcFile.getCanonicalPath();
            if (!canonicalPath.equals(srcFile.getPath()))
                throw new FileNotFoundException("Invalid file path, please try again.");
        } catch (IOException e) {
            LOG.error("Invalid file path, please try again.", e);
        }
        try (InputStream is = new FileInputStream(srcFile);
                BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line = br.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line).append("\n");
                line = br.readLine();
            }
            fileAsString = sb.toString();
        } catch (IOException e) {
            LOG.error("Error occurred when reading file!!!!!", e);
        }
        return fileAsString;
    }


//  private void updateFile(Plan pl, EdcrApplication edcrApplication) {
//  String readFile = readFile(edcrApplication.getSavedDxfFile());
//  String replace = readFile.replace("ENTITIES", "ENTITIES\n0\n" + pl.getAdditionsToDxf());
//  String newFile = edcrApplication.getDxfFile().getOriginalFilename().replace(".dxf", "_system_scrutinized.dxf");
//  File f = new File(newFile);
//  try (FileOutputStream fos = new FileOutputStream(f)) {
//      if (!f.exists())
//          f.createNewFile();
//      fos.write(replace.getBytes());
//      fos.flush();
//      FileStoreMapper fileStoreMapper = fileStoreService.store(f, f.getName(),
//              edcrApplication.getDxfFile().getContentType(), FILESTORE_MODULECODE);
//      edcrApplication.getEdcrApplicationDetails().get(0).setScrutinizedDxfFileId(fileStoreMapper);
//  } catch (IOException e) {
//      LOG.error("Error occurred when reading file!!!!!", e);
//  }
//}
        
    private File mergePdfFiles(File pdf1, File pdf2, String outputFileName) throws IOException {

        LOG.info("🔗 Merging PDFs: {} + {}", pdf1.getName(), pdf2.getName());

        // Ensure .pdf extension
        if (!outputFileName.toLowerCase().endsWith(".pdf")) {
            outputFileName = outputFileName + ".pdf";
        }

        //Create file safely in temp directory
        String tempDir = System.getProperty("java.io.tmpdir");
        String uniqueFileName = outputFileName.replace(".pdf", "") 
                + "_" + System.currentTimeMillis() + ".pdf";

        File mergedFile = new File(tempDir, uniqueFileName);

        //Initialize merger
        PDFMergerUtility merger = new PDFMergerUtility();

        merger.addSource(pdf1);
        merger.addSource(pdf2);

        merger.setDestinationFileName(mergedFile.getAbsolutePath());

        //Use temp file strategy (best for large PDFs)
        merger.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());

        LOG.info("✅ PDF merge completed: {}", mergedFile.getAbsolutePath());

        //Final safety check
        if (!mergedFile.exists() || mergedFile.length() == 0) {
            throw new IOException("Merged PDF file is empty or not created properly");
        }

        return mergedFile;
    }
 

    private JsonNode buildJsonNode(Plan pl, File signatureImageFile, EdcrApplication dcrApplication) {
        return buildJsonNode(pl, signatureImageFile, null, dcrApplication);
    }

    private JsonNode buildJsonNode(Plan pl, File signatureImageFile, JsonNode patchFields, EdcrApplication dcrApplication) {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode root = mapper.createObjectNode();
        final ObjectNode details = mapper.createObjectNode();
        root.set("details", details);

        try {
            Map<String, Object> finalReportData = extractFinalReportData(pl, dcrApplication);
            if(!CollectionUtils.isEmpty(pl.getBlocks())) {
            	finalReportData.put("buildingHeight", pl.getBlocks().get(0).getBuilding().getBuildingHeightExcludingMP().setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS));
            	finalReportData.put("totalBuildingHeight", pl.getBlocks().get(0).getBuilding().getBuildingHeight().setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS));
            }
            if (finalReportData == null || finalReportData.isEmpty()) {
                LOG.warn("finalReportData is empty. Building minimal overlay JSON.");
                return root;
            }

            String zone = BLANK_TEXT;
            if (patchFields != null && !patchFields.isNull() && !patchFields.isMissingNode()) {
            	JsonNode patchRoot = patchFields.path("details").isMissingNode() ? patchFields : patchFields.path("details");
            	zone = meaningfulText(patchRoot, "zone");
            }
            
            JsonNode frd = mapper.valueToTree(finalReportData);

            details.set("applicationDetails", buildApplicationDetails(mapper, frd, zone));
            details.set("plotAreaDetails", buildPlotAreaDetails(mapper, frd));
            details.set("builtUpArea", buildBuiltUpArea(mapper, frd));
            details.set("farDetails", buildFarDetails(mapper, frd));
            details.set("ecsDetails", buildEcsDetails(mapper, frd));
            details.set("buildingHeight", buildBuildingHeight(mapper, frd));
            details.set("roadDescription", buildRoadDescription(mapper, frd));
            details.set("officeUse", buildOfficeUse(mapper, frd));
            details.set("professionalSignature", buildProfessionalSignature(mapper, frd, signatureImageFile));
            details.set("eSign", buildESign(mapper, frd));
            details.set("blockWiseSummary", buildBlockWiseSummary(mapper, frd));
            details.set("blocks", buildBlocks(mapper, frd));
            details.set("setbacks", buildSetbacks(mapper, frd));
            applyLateFieldPatch(details, patchFields, mapper);
            return root;
        } catch (Exception ex) {
            LOG.error("Failed to build overlay JSON from finalReportData", ex);
            throw new RuntimeException("Unable to build overlay JSON", ex);
        }
    }

    private void applyLateFieldPatch(ObjectNode details, JsonNode patchFields, ObjectMapper mapper) {
        if (patchFields == null || patchFields.isNull() || patchFields.isMissingNode()) {
            return;
        }

        JsonNode patchRoot = patchFields.path("details").isMissingNode() ? patchFields : patchFields.path("details");

        // File Number patch
        String fileNumber = meaningfulText(patchRoot, "fileNumber");
        if (fileNumber == null) {
            fileNumber = meaningfulText(patchRoot.path("applicationDetails"), "fileNumber");
        }
        if (fileNumber != null && details.path("applicationDetails").isObject()) {
            ((ObjectNode) details.get("applicationDetails")).put("fileNumber", fileNumber);
        }

        // Office Use patch
        JsonNode officeUsePatch = patchRoot.path("officeUse");
        if (officeUsePatch.isObject() && details.path("officeUse").isObject()) {
            mergePatchObject((ObjectNode) details.get("officeUse"), officeUsePatch);
        }

        // E-sign patch
        JsonNode eSignPatch = patchRoot.path("eSign");
        if (eSignPatch.isArray() && eSignPatch.size() > 0) {
            ArrayNode cleaned = mapper.createArrayNode();
            for (JsonNode row : eSignPatch) {
                ObjectNode rowCopy = mapper.createObjectNode();
                String signatoryName = meaningfulText(row, "signatoryName");
                String designation = meaningfulText(row, "designation");

                if (signatoryName != null) {
                    rowCopy.put("signatoryName", signatoryName);
                }
                if (designation != null) {
                    rowCopy.put("designation", designation);
                }

                if (rowCopy.size() > 0) {
                    cleaned.add(rowCopy);
                }
            }
            if (cleaned.size() > 0) {
                details.set("eSign", cleaned);
            }
        }
    }

    private JsonNode buildLateFieldPatch(String fileNumber,
                                         String examinedBy,
                                         String approvedSanctionedBy,
                                         String approvalSanctionDate,
                                         String validTill,
                                         String signatoryName,
                                         String designation,
                                         String zone) {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode root = mapper.createObjectNode();
        final ObjectNode details = mapper.createObjectNode();
        root.set("details", details);

        if (StringUtils.isNotBlank(fileNumber)) {
            String trimmed = fileNumber.trim();
            if (isMeaningfulPatchText(trimmed)) {
                details.put("fileNumber", trimmed);
            }
        }

        ObjectNode officeUse = mapper.createObjectNode();
        //putIfNotBlank(officeUse, "examinedBy", examinedBy);
        putIfNotBlank(officeUse, "approvedSanctionedBy", approvedSanctionedBy);
        putIfNotBlank(officeUse, "approvalSanctionDate", approvalSanctionDate);
        putIfNotBlank(officeUse, "validTill", validTill);
        if (officeUse.size() > 0) {
            details.set("officeUse", officeUse);
        }

        if (StringUtils.isNotBlank(signatoryName) || StringUtils.isNotBlank(designation)) {
            ArrayNode eSign = mapper.createArrayNode();
            ObjectNode row = mapper.createObjectNode();
            putIfNotBlank(row, "signatoryName", signatoryName);
            putIfNotBlank(row, "designation", designation);
            if (row.size() > 0) {
                eSign.add(row);
                details.set("eSign", eSign);
            }
        }
        
        details.put("zone", zone);

        return root;
    }

    private void putIfNotBlank(ObjectNode node, String key, String value) {
        if (node != null && StringUtils.isNotBlank(value) && isMeaningfulPatchText(value.trim())) {
            node.put(key, value.trim());
        }
    }

    private String meaningfulText(JsonNode node, String field) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }

        JsonNode value = node.path(field);
        if (value == null || value.isMissingNode() || value.isNull()) {
            return null;
        }

        String text = value.asText("").trim();
        return isMeaningfulPatchText(text) ? text : null;
    }

    private boolean isMeaningfulPatchText(String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }

        String normalized = value.trim();
        return !("N/A".equalsIgnoreCase(normalized)
                || "NA".equalsIgnoreCase(normalized)
                || "NULL".equalsIgnoreCase(normalized)
                || "0".equals(normalized)
                || "----".equals(normalized));
    }

    private void mergePatchObject(ObjectNode target, JsonNode patch) {
        if (target == null || patch == null || !patch.isObject()) {
            return;
        }
        patch.fields().forEachRemaining(entry -> {
            JsonNode value = entry.getValue();
            if (value == null || value.isNull() || value.isMissingNode()) {
                return;
            }

            if (value.isTextual() && !isMeaningfulPatchText(value.asText(""))) {
                return;
            }

            target.set(entry.getKey(), value.deepCopy());
        });
    }

    private ObjectNode buildApplicationDetails(ObjectMapper mapper, JsonNode frd, String zone) {
        ObjectNode n = mapper.createObjectNode();
        JsonNode pi = frd.path("planInformation");
        n.put("nameOfApplicant", txt(frd, "applicantName"));
        n.put("fileNumber", txt(frd, "fileNumber"));
        n.put("edcrNumber", txt(frd, "dcrNo"));
        n.put("ulbName", txt(frd, "district"));
        n.put("ulbType", txt(frd, "ulbType"));
        n.put("buildingCategory", txt(pi, "occupancy"));
        n.put("proposedSiteAddress", txt(pi, "city"));
        n.put("khasraNo", txt(pi, "khasraNo"));
        n.put("zone", zone);
        return n;
    }

    private ObjectNode buildPlotAreaDetails(ObjectMapper mapper, JsonNode frd) {
        ObjectNode n = mapper.createObjectNode();

        double area = num(frd.path("planInformation"), "plotArea");
        n.put("plotAreaAsPerDeclaration", area);

        area = num(frd.path("plot"), "plotBndryArea");
        n.put("plotAreaAsPerDrawing", area);

        return n;
    }

    private ObjectNode buildBuiltUpArea(ObjectMapper mapper, JsonNode frd) {
        ObjectNode n = mapper.createObjectNode();
        n.put("existingBuiltUpArea", 0.0d);
        n.put("proposedBuiltUpArea", num(frd, "totalBuiltUpArea"));
        n.put("totalBuiltUpArea", num(frd, "totalBuiltUpArea"));
        return n;
    }

    private ObjectNode buildFarDetails(ObjectMapper mapper, JsonNode frd) {
        ObjectNode n = mapper.createObjectNode();

        JsonNode farDetails = findCommonFarDetail(frd);
        JsonNode bws = frd.path("sections")
                         .path("Block Wise Summary")
                         .path("Block No 1 - Proposed Details");

        n.put("totalPermissibleFAR", txt(farDetails, "Permissible"));
        n.put("totalPermissibleFARArea", getTotalFloorArea(bws));
        n.put("totalProposedFAR", txt(farDetails, "Provided"));
        n.put("totalProposedFARArea", getTotalFloorArea(bws));

        return n;
    }
    
    private double getTotalFloorArea(JsonNode bws) {
        for (JsonNode row : bws.path("detail")) {
            if ("Total-colspan-2".equals(
                    row.path("Occupancy/Sub Occupancy").asText())) {
                return row.path("Floor Area in m²").asDouble();
            }
        }
        return 0.0;
    }
    
    private String extractTotalHeight(String remarks) {
        if (remarks == null) {
            return BLANK_TEXT;
        }

        Pattern pattern = Pattern.compile("Total Height of building is\\s*([\\d.]+)");
        Matcher matcher = pattern.matcher(remarks);

        return matcher.find() ? matcher.group(1) : BLANK_TEXT;
    }

    private ObjectNode buildEcsDetails(ObjectMapper mapper, JsonNode frd) {
        ObjectNode n = mapper.createObjectNode();
        JsonNode parkingDetails = new ObjectMapper().createObjectNode();
        JsonNode parkingDetailsArr = findCommonParkingDetail(frd);
        String oprnParking = "0.0";
        String stiltParking = "0.0";
        String coveredParking = "0.0";
        String basementParking = "0.0";
        for (JsonNode row : parkingDetailsArr) {
        	String description = row.path("Description").asText();
        	switch (description) {
				case "Parking":
					parkingDetails = row;
					break;
				case "Open Parking Area":
					oprnParking = txt(row, "Provided");
					break;
				case "Stilt Parking Area":
					stiltParking = txt(row, "Provided");
					break;
				case "Cover Parking Area":
					coveredParking = txt(row, "Provided");
					break;
				case "Basement Parking Area":
					basementParking = txt(row, "Provided");
					break;
				default:
					break;
			}
			
		}
        
        n.put("parking", txt(parkingDetails, "Provided"));
        n.put("required", txt(parkingDetails, "Required"));
        n.put("twoWheelerParking", BLANK_TEXT);
        n.put("openParkingArea", oprnParking);
        n.put("stiltParkingArea", stiltParking);
        n.put("coveredParkingArea", coveredParking);
        n.put("basementParkingArea", basementParking);
        return n;
    }

    private ObjectNode buildBuildingHeight(ObjectMapper mapper, JsonNode frd) {
        ObjectNode n = mapper.createObjectNode();
       
        n.put("permissibleBuildingHeight", "0.0");
        n.put("proposedBuildingHeight", txt(frd, "buildingHeight") + " m");
        n.put("permissibleTotalHeight", "----");
        n.put("proposedTotalHeight", txt(frd, "totalBuildingHeight") + " m");
        return n;
    }

    private ObjectNode buildRoadDescription(ObjectMapper mapper, JsonNode frd) {
        ObjectNode n = mapper.createObjectNode();
        JsonNode rd = findCommonRoadWidthDetail(frd);
        n.put("approachRoadWidth", extractNumber(txt(rd, "Provided")));
        n.put("rearSideRoadWidth", 0.0d);
        n.put("side1RoadWidth", 0.0d);
        n.put("side2RoadWidth", 0.0d);
        return n;
    }

    private ObjectNode buildOfficeUse(ObjectMapper mapper, JsonNode frd) {
        ObjectNode n = mapper.createObjectNode();
        JsonNode officeUse = frd.path("officeUse");

        //n.put("examinedBy", txt(officeUse, "examinedBy"));
        n.put("approvedSanctionedBy", txt(officeUse, "approvedSanctionedBy"));
        n.put("approvalSanctionDate", txt(officeUse, "approvalSanctionDate"));
        n.put("validTill", txt(officeUse, "validTill"));
        return n;
    }

    private ObjectNode buildProfessionalSignature(ObjectMapper mapper, JsonNode frd, File signatureImageFile) {
        ObjectNode n = mapper.createObjectNode();
        String signatureDataUri = toDataUri(signatureImageFile);
        n.put("uploadedSignature", StringUtils.isNotBlank(signatureDataUri) ? signatureDataUri : txt(frd, BLANK_TEXT));
        n.put("hasSignatureImage", StringUtils.isNotBlank(signatureDataUri));
        return n;
    }

    private String toDataUri(File imageFile) {
        if (imageFile == null || !imageFile.exists() || !imageFile.isFile()) {
            return "";
        }

        try {
            byte[] bytes = Files.readAllBytes(imageFile.toPath());
            if (bytes.length == 0) {
                return "";
            }

            String mimeType = Files.probeContentType(imageFile.toPath());
            if (StringUtils.isBlank(mimeType)) {
                mimeType = "image/png";
            }

            return "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (IOException ex) {
            LOG.warn("Unable to convert signature image to base64: {}", imageFile.getAbsolutePath(), ex);
            return "";
        }
    }

    private ArrayNode buildESign(ObjectMapper mapper, JsonNode frd) {
        ArrayNode arr = mapper.createArrayNode();
        JsonNode eSignNode = frd.path("eSign");

        if (eSignNode.isArray() && eSignNode.size() > 0) {
            for (JsonNode sign : eSignNode) {
                ObjectNode row = mapper.createObjectNode();
                row.put("signatoryName", txt(sign, "signatoryName"));
                row.put("designation", txt(sign, "designation"));
                arr.add(row);
            }
            return arr;
        }

        ObjectNode p1 = mapper.createObjectNode();
        p1.put("signatoryName", BLANK_TEXT);
        p1.put("designation", BLANK_TEXT);
        arr.add(p1);

//        ObjectNode p2 = mapper.createObjectNode();
//        p2.put("signatoryName", txt(frd, "N/A"));
//        p2.put("designation", "Applicant");
//        arr.add(p2);
        return arr;
    }

    private ObjectNode buildBlockWiseSummary(ObjectMapper mapper, JsonNode frd) {
        ObjectNode n = mapper.createObjectNode();
        n.put("totalPlotArea", num(frd.path("plot"), "area"));
        n.put("groundCoverage", txt(frd, "coverage") + "%");
        n.put("totalBuiltUpArea", num(frd, "totalBuiltUpArea"));
        
        JsonNode bws = frd.path("sections")
                .path("Block Wise Summary")
                .path("Block No 1 - Proposed Details");
        
        n.put("totalFARArea", getTotalFloorArea(bws));
        return n;
    }

    private ArrayNode buildBlocks(ObjectMapper mapper, JsonNode frd) {
        ArrayNode blocks = mapper.createArrayNode();
        JsonNode bws = frd.path("sections").path("Block Wise Summary");
        if (!bws.isObject()) return blocks;

        bws.fieldNames().forEachRemaining(blockKey -> {
            JsonNode blockSection = bws.path(blockKey);
            ArrayNode detail = (ArrayNode) blockSection.path("detail");
            if (!detail.isArray()) return;

            ObjectNode block = mapper.createObjectNode();
            block.put("blockName", blockKey.replace(" - Proposed Details", ""));
            block.put("proposedTitle", "1. " + blockKey);
            block.set("proposedDetails", buildProposedDetails(mapper, detail));
            block.put("remarks", txt(blockSection, "remarks"));
            block.set("floorWiseBuiltUpFAR", buildFloorWiseBuiltUpFAR(mapper, detail));
//            block.set("deductionDetails", buildDeductionDetails(mapper, detail));
//            block.set("builtUpDeductions", buildBuiltUpDeductions(mapper, detail));
//            block.set("farDeductionDetails", buildFarDeductionDetails(mapper, detail));
            blocks.add(block);
        });
        return blocks;
    }

    private ArrayNode buildProposedDetails(ObjectMapper mapper, ArrayNode rows) {
        ArrayNode out = mapper.createArrayNode();
        for (JsonNode r : rows) {
            ObjectNode n = mapper.createObjectNode();
            String occupancy = txt(r, "Occupancy/Sub Occupancy");

            if ("Total-colspan-2".equalsIgnoreCase(occupancy)) {
                n.put("floor", "Total");
                n.put("occupancySubOccupancy", BLANK_TEXT);
            } else {
                n.put("floor", txt(r, "Floor"));
                n.put("occupancySubOccupancy", occupancy);
            }

            n.put("builtUpArea", txt(r, "Built Up Area in m²"));
            n.put("deductionArea", txt(r, "Deduction Area in m²"));
            n.put("floorArea", txt(r, "Floor Area in m²"));
            out.add(n);
        }
        return out;
    }

    private ArrayNode buildFloorWiseBuiltUpFAR(ObjectMapper mapper, ArrayNode rows) {
        ArrayNode out = mapper.createArrayNode();
        for (JsonNode r : rows) {
            if ("Total-colspan-2".equalsIgnoreCase(txt(r, "Occupancy/Sub Occupancy"))) continue;
            ObjectNode n = mapper.createObjectNode();
            n.put("floor", floorLabel(txt(r, "Floor")));
            n.put("Occupancy/Sub Occupancy", floorLabel(txt(r, "Occupancy/Sub Occupancy")));
            n.put("Built Up Area in m²", floorLabel(txt(r, "Built Up Area in m²")));
            n.put("Deduction Area in m²", floorLabel(txt(r, "Deduction Area in m²")));
            n.put("Floor Area in m²", floorLabel(txt(r, "Floor Area in m²")));
            //n.put("effectiveBuiltUpArea", num(r, "Built Up Area in m²"));
            //n.put("existingBuiltUpArea", 0.0d);
            //n.put("proposedFAR", num(r, "Floor Area in m²"));
            //n.put("existingFAR", 0.0d);
            out.add(n);
        }
        out.add(totalRow(mapper, out, "effectiveBuiltUpArea", "proposedFAR"));
        return out;
    }

    private ArrayNode buildDeductionDetails(ObjectMapper mapper, ArrayNode rows) {
        ArrayNode out = mapper.createArrayNode();
        for (JsonNode r : rows) {
            if ("Total-colspan-2".equalsIgnoreCase(txt(r, "Occupancy/Sub Occupancy"))) continue;
            double bua = num(r, "Built Up Area in m²");
            double farArea = num(r, "Floor Area in m²");
            double ded = 0.0d;
            ObjectNode n = mapper.createObjectNode();
            n.put("floor", floorLabel(txt(r, "Floor")));
            n.put("builtUpAreaIncludingDeduction", bua);
            n.put("builtUpDeductionArea", ded);
            n.put("effectiveBuiltUpArea", bua - ded);
            n.put("nonFARArea", Math.max(0.0d, (bua - ded) - farArea));
            n.put("farArea", farArea);
            out.add(n);
        }
        out.add(totalDeductionRow(mapper, out));
        return out;
    }

    private ArrayNode buildBuiltUpDeductions(ObjectMapper mapper, ArrayNode rows) {
        ArrayNode out = mapper.createArrayNode();
        for (JsonNode r : rows) {
            if ("Total-colspan-2".equalsIgnoreCase(txt(r, "Occupancy/Sub Occupancy"))) continue;
            ObjectNode n = mapper.createObjectNode();
            n.put("floor", floorLabel(txt(r, "Floor")));
            n.put("voids", 0.0d);
            n.put("ramp", 0.0d);
            n.put("totalDeduction", 0.0d);
            out.add(n);
        }
        out.add(totalSimpleDeductionRow(mapper, out));
        return out;
    }

    private ArrayNode buildFarDeductionDetails(ObjectMapper mapper, ArrayNode rows) {
        ArrayNode out = mapper.createArrayNode();
        for (JsonNode r : rows) {
            if ("Total-colspan-2".equalsIgnoreCase(txt(r, "Occupancy/Sub Occupancy"))) continue;
            ObjectNode n = mapper.createObjectNode();
            n.put("floor", floorLabel(txt(r, "Floor")));
            n.put("mumty", 0.0d);
            n.put("totalDeduction", 0.0d);
            out.add(n);
        }
        out.add(totalMumtyRow(mapper, out));
        return out;
    }

    private ObjectNode buildSetbacks(ObjectMapper mapper, JsonNode frd) {
        ObjectNode n = mapper.createObjectNode();
        n.put("frontPermissible", BLANK_TEXT);
        n.put("frontProvided", BLANK_TEXT);
        n.put("rearPermissible", BLANK_TEXT);
        n.put("rearProvided", BLANK_TEXT);
        n.put("side1Permissible", BLANK_TEXT);
        n.put("side1Provided", BLANK_TEXT);
        n.put("side2Permissible", BLANK_TEXT);
        n.put("side2Provided", BLANK_TEXT);

        JsonNode setbackRows = frd.path("sections").path("Block 1 - Scrutiny Details").path("Setback").path("detail");
        if (setbackRows.isArray()) {
            for (JsonNode row : setbackRows) {
                String type = txt(row, "Setback").toLowerCase();
                if (type.contains("front")) {
                    n.put("frontPermissible", txt(row, "Permissible"));
                    n.put("frontProvided", txt(row, "Provided"));
                } else if (type.contains("rear")) {
                    n.put("rearPermissible", txt(row, "Permissible"));
                    n.put("rearProvided", txt(row, "Provided"));
                } else if (type.contains("1")) {
                    n.put("side1Permissible", txt(row, "Permissible"));
                    n.put("side1Provided", txt(row, "Provided"));
                } else if (type.contains("2")) {
                    n.put("side2Permissible", txt(row, "Permissible"));
                    n.put("side2Provided", txt(row, "Provided"));
                }
            }
        }
        return n;
    }

    private JsonNode findCommonParkingDetail(JsonNode frd) {
        JsonNode arr = frd.path("sections").path("Common - Scrutiny Details").path("Parking").path("detail");
        return arr.isArray() && arr.size() > 0 ? arr : new ObjectMapper().createObjectNode();
    }
    
    private JsonNode findCommonFarDetail(JsonNode frd) {
        JsonNode arr = frd.path("sections").path("Common - Scrutiny Details").path("FAR").path("detail");
        return arr.isArray() && arr.size() > 0 ? arr.get(0) : new ObjectMapper().createObjectNode();
    }

    private JsonNode findCommonRoadWidthDetail(JsonNode frd) {
        JsonNode arr = frd.path("sections").path("Common - Scrutiny Details").path("Road Width ").path("detail");
        return arr.isArray() && arr.size() > 0 ? arr.get(0) : new ObjectMapper().createObjectNode();
    }

    private JsonNode findBlockScrutinyFirstDetailByKey(JsonNode frd, String key) {
        JsonNode arr = frd.path("sections").path("Block 1 - Scrutiny Details").path(key).path("detail");
        return arr.isArray() && arr.size() > 0 ? arr.get(0) : new ObjectMapper().createObjectNode();
    }

    private ObjectNode totalRow(ObjectMapper mapper, ArrayNode rows, String builtField, String farField) {
        double totalBuilt = 0.0d;
        double totalFar = 0.0d;
        for (JsonNode r : rows) {
            totalBuilt += num(r, builtField);
            totalFar += num(r, farField);
        }
        ObjectNode t = mapper.createObjectNode();
        t.put("floor", "Total");
        t.put("effectiveBuiltUpArea", round2(totalBuilt));
        t.put("existingBuiltUpArea", 0.0d);
        t.put("proposedFAR", round2(totalFar));
        t.put("existingFAR", 0.0d);
        return t;
    }

    private ObjectNode totalDeductionRow(ObjectMapper mapper, ArrayNode rows) {
        double incl = 0.0d, ded = 0.0d, eff = 0.0d, nonFar = 0.0d, far = 0.0d;
        for (JsonNode r : rows) {
            incl += num(r, "builtUpAreaIncludingDeduction");
            ded += num(r, "builtUpDeductionArea");
            eff += num(r, "effectiveBuiltUpArea");
            nonFar += num(r, "nonFARArea");
            far += num(r, "farArea");
        }
        ObjectNode t = mapper.createObjectNode();
        t.put("floor", "Total");
        t.put("builtUpAreaIncludingDeduction", round2(incl));
        t.put("builtUpDeductionArea", round2(ded));
        t.put("effectiveBuiltUpArea", round2(eff));
        t.put("nonFARArea", round2(nonFar));
        t.put("farArea", round2(far));
        return t;
    }

    private ObjectNode totalSimpleDeductionRow(ObjectMapper mapper, ArrayNode rows) {
        double voids = 0.0d, ramp = 0.0d, total = 0.0d;
        for (JsonNode r : rows) {
            voids += num(r, "voids");
            ramp += num(r, "ramp");
            total += num(r, "totalDeduction");
        }
        ObjectNode t = mapper.createObjectNode();
        t.put("floor", "Total");
        t.put("voids", round2(voids));
        t.put("ramp", round2(ramp));
        t.put("totalDeduction", round2(total));
        return t;
    }

    private ObjectNode totalMumtyRow(ObjectMapper mapper, ArrayNode rows) {
        double mumty = 0.0d, total = 0.0d;
        for (JsonNode r : rows) {
            mumty += num(r, "mumty");
            total += num(r, "totalDeduction");
        }
        ObjectNode t = mapper.createObjectNode();
        t.put("floor", "Total");
        t.put("mumty", round2(mumty));
        t.put("totalDeduction", round2(total));
        return t;
    }

    private String floorLabel(String floorNo) {
//        if ("0".equals(floorNo)) return "Ground Floor";
//        if ("1".equals(floorNo)) return "1st Floor";
//        if ("2".equals(floorNo)) return "2nd Floor";
        return floorNo == null || floorNo.trim().isEmpty() ? BLANK_TEXT : floorNo;
    }

    private String txt(JsonNode node, String field) {
        JsonNode v = node == null ? null : node.path(field);
        if (v == null || v.isMissingNode() || v.isNull()) return BLANK_TEXT;
        String out = v.asText("").trim();
        return out.isEmpty() ? BLANK_TEXT : out;
    }

    private double num(JsonNode node, String field) {
        JsonNode v = node == null ? null : node.path(field);
        if (v == null || v.isMissingNode() || v.isNull()) return 0.0d;
        if (v.isNumber()) return round2(v.asDouble());
        try {
            return round2(Double.parseDouble(v.asText("0").replaceAll("[^0-9.-]", "")));
        } catch (Exception ex) {
            return 0.0d;
        }
    }

    private double extractNumber(String text) {
        if (text == null) return 0.0d;
        try {
            String value = text.replaceAll("[^0-9.-]", "");
            return value.isEmpty() ? 0.0d : round2(Double.parseDouble(value));
        } catch (Exception ex) {
            return 0.0d;
        }
    }

    private double round2(double val) {
        return Math.round(val * 100.0d) / 100.0d;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractFinalReportData(Plan pl, EdcrApplication dcrApplication) {
        if (pl == null) return new java.util.HashMap<String, Object>();
        try {
            java.lang.reflect.Method getter = pl.getClass().getMethod("getFinalReportData");
            Object value = getter.invoke(pl);
            if (value instanceof Map) return (Map<String, Object>) value;
        } catch (NoSuchMethodException ignored) {
            LOG.debug("Plan does not expose getFinalReportData(). Building fallback JSON from Plan fields.");
        } catch (Exception ex) {
            LOG.warn("Unable to read finalReportData from Plan. Using fallback fields.", ex);
        }
        
        Map<String, Object> model = planReportServiceV2.buildReportModelV2(pl, dcrApplication);
        if(!CollectionUtils.isEmpty(model))
        	return model;
        
        Map<String, Object> fallback = new java.util.HashMap<String, Object>();
        Map<String, Object> planInfo = new java.util.HashMap<String, Object>();
        if (pl.getPlanInformation() != null) {
            planInfo.put("plotArea", pl.getPlanInformation().getPlotArea());
            planInfo.put("occupancy", pl.getPlanInformation().getOccupancy());
            planInfo.put("khasraNo", pl.getPlanInformation().getKhasraNo());
            planInfo.put("landUseZone", pl.getPlanInformation().getLandUseZone());
            planInfo.put("city", pl.getPlanInformation().getCity());
            planInfo.put("ulbType", pl.getPlanInformation().getUlbType());
        }
        fallback.put("planInformation", planInfo);
        if (pl.getFarDetails() != null) fallback.put("far", pl.getFarDetails().getProvidedFar());
        if (pl.getVirtualBuilding() != null) {
            fallback.put("totalBuiltUpArea", pl.getVirtualBuilding().getTotalBuitUpArea());
            fallback.put("totalFloorArea", pl.getVirtualBuilding().getTotalFloorArea());
            fallback.put("coverage", pl.getCoverage());
        }
        return fallback;
    }
    
    private void updateFilev2(Plan pl, EdcrApplication edcrApplication) {
        updateFilev2(pl, edcrApplication, null);
    }

	private void updateFileNew(Plan pl, EdcrApplication edcrApplication, String fileNumber, String examinedBy,
			String approvedSanctionedBy, String approvalSanctionDate, String validTill, String signatoryName,
			String designation, String tenantId, String zone) {
		JsonNode patchFields = buildLateFieldPatch(fileNumber, examinedBy, approvedSanctionedBy, approvalSanctionDate,
				validTill, signatoryName, designation, zone);
		updateFileV4(pl, edcrApplication, patchFields, tenantId);
	}
    
//    private void updateFileV4(Plan pl, EdcrApplication edcrApplication, JsonNode patchFields, String tenantId) {
//        long start = System.currentTimeMillis();
//        
//        String newFileName = edcrApplication.getEdcrApplicationDetails().get(0).getDxfFileId().getFileName()
//                .replace(".dxf", "_system_scrutinized.pdf");
//        LOG.info("Starting scrutinized PDF generation for: {}", newFileName);
//        
//        File tempPdf = null;
//        JsonNode additionalDetails = null;
//        try {
//            tempPdf = File.createTempFile("scrutinized_", ".pdf");
//            try (
//            		FileInputStream dxfInput =
//                    new FileInputStream(edcrApplication.getSavedDxfFile())
//                         ) {
//            	DxfToPdfConverterv2.convertDxfToPdf(dxfInput, tempPdf, false); // false = don't save SVG
//                LOG.info("DXF to PDF conversion completed: {} bytes", tempPdf.length());
//            }
//
//            String uuid = pl.getEdcrRequest().getRequestInfo().getUserInfo().getUuid();
//            LOG.info("UUID no : "  + uuid);
//            Object data = getUserData(uuid);
//            List<String> signatures = JsonPath.read(data, "$.user[*].signature");
//            String signatureFileStoreId = null;
//            if (!CollectionUtils.isEmpty(signatures)) {
//            	signatureFileStoreId = signatures.get(0);
//            }
//            File uploadedDiagramFile = null;
//            if (StringUtils.isNotBlank(signatureFileStoreId)) {
//                uploadedDiagramFile = fileStoreService.fetch(signatureFileStoreId, "", "pb");
//            }
//
//            additionalDetails = buildJsonNode(pl, uploadedDiagramFile, patchFields);
//
//            // Overlay the information onto the DXF-converted PDF using template renderer
//            tempPdf = pdfOverlayTemplateService.impose(
//                    tempPdf,
//                    tempPdf.getAbsolutePath(),
//                    additionalDetails,
//                    EXPAND_RIGHT,
//                    EXPAND_BOTTOM,
//                    GAP_DRAWING_TO_TABLES,
//                    GAP_TOP);
//            
//            FileStoreMapper fileStoreMapper = fileStoreService.store(
//                    tempPdf,
//                    newFileName,
//                    "application/pdf",
//                    FILESTORE_MODULECODE
//            );
//            
//            edcrApplication.getEdcrApplicationDetails()
//                    .get(0)
//                    .setScrutinizedDxfFileId(fileStoreMapper);
//            
//            LOG.info("PDF stored in filestore: {}",
//                    fileStoreMapper != null ? fileStoreMapper.getFileStoreId() : "null");
//
//        } catch (AbstractMethodError ame) {
//            LOG.error("AbstractMethodError in updateFile() — Batik/FOP JAR conflict: {}", ame.getMessage(), ame);
//            throw new RuntimeException("SVG→PDF conversion failed due to Batik classpath conflict.", ame);
//
//        } catch (Exception e) {
//            LOG.error("Error in updateFile() for : {}",e.getMessage(), e);
//            throw new RuntimeException(e);
//
//        } finally {
//            if (tempPdf != null && tempPdf.exists() && !tempPdf.delete()) {
//                LOG.warn("Temporary PDF not deleted: {}", tempPdf.getAbsolutePath());
//            }
//            long elapsed = System.currentTimeMillis() - start;
//            LOG.info("updateFile() completed in ms → {}", elapsed);
//        }
//    }
	
	private void updateFileV4(Plan pl, EdcrApplication edcrApplication, JsonNode patchFields, String tenantId) {

		long start = System.currentTimeMillis();

		File tempPdf = null;

		try {

			validateInputs(pl, edcrApplication);

			String newFileName = edcrApplication.getEdcrApplicationDetails().get(0).getDxfFileId().getFileName()
					.replace(".dxf", "_system_scrutinized.pdf");

			LOG.info("Starting scrutinized PDF generation for {}", newFileName);

			tempPdf = File.createTempFile("scrutinized_", ".pdf");

			try (FileInputStream dxfInput = new FileInputStream(edcrApplication.getSavedDxfFile())) {

				DxfToPdfConverterv2.convertDxfToPdf(dxfInput, tempPdf, false);

				LOG.info("DXF converted successfully.");
			}

			File signatureFile = fetchSignatureFile(pl);

			JsonNode additionalDetails = buildJsonNode(pl, signatureFile, patchFields, edcrApplication);

			tempPdf = pdfOverlayTemplateService.impose(tempPdf, tempPdf.getAbsolutePath(), additionalDetails,
					EXPAND_RIGHT, EXPAND_BOTTOM, GAP_DRAWING_TO_TABLES, GAP_TOP);

			FileStoreMapper fileStoreMapper = fileStoreService.store(tempPdf, newFileName, "application/pdf",
					FILESTORE_MODULECODE);

			if (fileStoreMapper == null) {
				throw new IllegalStateException("Unable to store scrutinized PDF in FileStore.");
			}

			edcrApplication.getEdcrApplicationDetails().get(0).setScrutinizedDxfFileId(fileStoreMapper);

			LOG.info("Scrutinized PDF stored successfully. FileStoreId={}", fileStoreMapper.getFileStoreId());

		} catch (AbstractMethodError ex) {

			LOG.error("Batik/FOP dependency conflict detected.", ex);

			throw new IllegalStateException("PDF generation failed due to server dependency conflict.", ex);

		} catch (IOException ex) {

			LOG.error("File processing error.", ex);

			throw new IllegalStateException("Unable to generate scrutinized PDF.", ex);

		} catch (Exception ex) {

			LOG.error("Unexpected error while generating scrutinized PDF.", ex);

			throw new IllegalStateException("Failed to generate scrutinized PDF.", ex);

		} finally {

			if (tempPdf != null && tempPdf.exists() && !tempPdf.delete()) {

				LOG.warn("Unable to delete temporary file {}", tempPdf.getAbsolutePath());
			}

			LOG.info("updateFileV4 completed in {} ms", System.currentTimeMillis() - start);
		}
	}
    
    private void validateInputs(Plan pl, EdcrApplication application) {

        if (pl == null) {
            throw new IllegalArgumentException("Plan cannot be null.");
        }

        if (application == null) {
            throw new IllegalArgumentException("EDCR application not found.");
        }

        if (CollectionUtils.isEmpty(application.getEdcrApplicationDetails())) {
            throw new IllegalArgumentException(
                    "EDCR application details not found.");
        }

        if (application.getSavedDxfFile() == null
                || !application.getSavedDxfFile().exists()) {

            throw new IllegalArgumentException(
                    "Saved DXF file not found.");
        }
    }
    
    private File fetchSignatureFile(Plan pl) {

        try {

            String uuid = pl.getEdcrRequest()
                    .getRequestInfo()
                    .getUserInfo()
                    .getUuid();

            Object data = getUserData(uuid);

            List<String> signatures =
                    JsonPath.read(data, "$.user[*].signature");

            if (CollectionUtils.isEmpty(signatures)) {
                return null;
            }

            return fileStoreService.fetch(
                    signatures.get(0),
                    "",
                    "pb");

        } catch (Exception ex) {

            LOG.warn("Unable to fetch signature. PDF will continue without signature.", ex);

            return null;
        }
    }
    
    private void updateFilev2(Plan pl, EdcrApplication edcrApplication, JsonNode patchFields) {
        long start = System.currentTimeMillis();
        String newFileName = edcrApplication.getDxfFile()
                .getOriginalFilename()
                .replace(".dxf", "_system_scrutinized.pdf");
        LOG.info("Starting scrutinized PDF generation for: {}", newFileName);

        File tempPdf = null;
        JsonNode additionalDetails = null;
        try {
            tempPdf = File.createTempFile("scrutinized_", ".pdf");

            try (FileInputStream dxfInput =
                         new FileInputStream(edcrApplication.getSavedDxfFile())) {
                DxfToPdfConverterv2.convertDxfToPdf(dxfInput, tempPdf, false); // false = don't save SVG
                LOG.info("DXF to PDF conversion completed: {} bytes", tempPdf.length());
            }

            String uuid = pl.getEdcrRequest().getRequestInfo().getUserInfo().getUuid();
            LOG.info("UUID no : "  + uuid);
            Object data = getUserData(uuid);
            List<String> signatures = JsonPath.read(data, "$.user[*].signature");
            String signatureFileStoreId = null;
            if (!CollectionUtils.isEmpty(signatures)) {
            	signatureFileStoreId = signatures.get(0);
            }
            File uploadedDiagramFile = null;
            if (StringUtils.isNotBlank(signatureFileStoreId)) {
                uploadedDiagramFile = fileStoreService.fetch(signatureFileStoreId, "", "pb");
            }

            additionalDetails = buildJsonNode(pl, uploadedDiagramFile, patchFields,edcrApplication);

            // Overlay the information onto the DXF-converted PDF using template renderer
            tempPdf = pdfOverlayTemplateService.impose(
                    tempPdf,
                    tempPdf.getAbsolutePath(),
                    additionalDetails,
                    EXPAND_RIGHT,
                    EXPAND_BOTTOM,
                    GAP_DRAWING_TO_TABLES,
                    GAP_TOP);

            
            FileStoreMapper fileStoreMapper = fileStoreService.store(
                    tempPdf,
                    newFileName,
                    "application/pdf",
                    FILESTORE_MODULECODE
            );

            edcrApplication.getEdcrApplicationDetails()
                    .get(0)
                    .setScrutinizedDxfFileId(fileStoreMapper);

            LOG.info("PDF stored in filestore: {}",
                    fileStoreMapper != null ? fileStoreMapper.getFileStoreId() : "null");

        } catch (AbstractMethodError ame) {
            LOG.error("AbstractMethodError in updateFile() — Batik/FOP JAR conflict: {}", ame.getMessage(), ame);
            throw new RuntimeException("SVG→PDF conversion failed due to Batik classpath conflict.", ame);

        } catch (Exception e) {
            LOG.error("Error in updateFile() for '{}': {}", newFileName, e.getMessage(), e);
            throw new RuntimeException(e);

        } finally {
            if (tempPdf != null && tempPdf.exists() && !tempPdf.delete()) {
                LOG.warn("Temporary PDF not deleted: {}", tempPdf.getAbsolutePath());
            }
            long elapsed = System.currentTimeMillis() - start;
            LOG.info("updateFile() completed in {} ms → {}", elapsed, newFileName);
        }
    }

    private void updateFile(Plan pl, EdcrApplication edcrApplication) {
        long start = System.currentTimeMillis();
        String filePath = edcrApplication.getSavedDxfFile().getAbsolutePath();
        String newFileName = edcrApplication.getDxfFile().getOriginalFilename()
                .replace(".dxf", "_system_scrutinized.pdf");
        File finalOutputFile = new File(newFileName);

        LOG.info("Starting scrutinized PDF generation for: {}", newFileName);

        File tempPdf = null;
        File watermarkRemovedFile = null;
        try {
            tempPdf = File.createTempFile("scrutinized_", ".pdf");
            LOG.debug("Temporary PDF path: {}", tempPdf.getAbsolutePath());

            try (Image cadImage = Image.load(filePath);
                 FileOutputStream tempOut = new FileOutputStream(tempPdf)) {

                PdfOptions pdfOptions = new PdfOptions();
                CadRasterizationOptions rasterOpts = new CadRasterizationOptions();

                rasterOpts.setBackgroundColor(Color.getWhite());
                rasterOpts.setDrawType(CadDrawTypeMode.UseObjectColor);

                rasterOpts.setPageWidth(4494);   
                rasterOpts.setPageHeight(3178);

                rasterOpts.setAutomaticLayoutsScaling(true);
                
                rasterOpts.setNoScaling(false);

                rasterOpts.setLayouts(new String[]{"Model"});

                rasterOpts.setUnitType(UnitType.Millimeter);

                pdfOptions.setVectorRasterizationOptions(rasterOpts);

                pdfOptions.setCorePdfOptions(new PdfDocumentOptions());

                cadImage.save(tempOut, pdfOptions);
                LOG.debug("CAD to PDF conversion complete.");

            } catch (OutOfMemoryError oom) {
                LOG.error("OutOfMemoryError while converting DXF → PDF: {}", filePath, oom);
                throw oom;
            } catch (Exception ex) {
                LOG.error("Error converting DXF → PDF: {}", filePath, ex);
                throw ex;
            }

            // --- Step 2: Post-process PDF (timestamp, incremental save) ---
            try (RandomAccessBufferedFileInputStream rar = new RandomAccessBufferedFileInputStream(tempPdf);
                 PDDocument document = PDDocument.load(rar);
                 BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(finalOutputFile))) {

                PDPage page = document.getPage(0);
                float pageWidth  = page.getMediaBox().getWidth();
                float pageHeight = page.getMediaBox().getHeight();

                // Set initial view to center
                PDPageXYZDestination dest = new PDPageXYZDestination();
                dest.setPage(page);
                dest.setLeft((int) (pageWidth / 2f));
                dest.setTop((int) (pageHeight / 2f));
                dest.setZoom(1.0f);
                document.getDocumentCatalog().setOpenAction(dest);

                try (PDPageContentStream contentStream = new PDPageContentStream(
                        document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {

                    PDExtendedGraphicsState gs = new PDExtendedGraphicsState();
                    gs.setNonStrokingAlphaConstant(0.7f);
                    contentStream.setGraphicsStateParameters(gs);

                    String timestamp = LocalDateTime.now().format(TS_FORMAT);

                    float fontSize = Math.max(12f, pageWidth * 0.024f);
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize);

                    float textWidth = (PDType1Font.HELVETICA_BOLD.getStringWidth(timestamp) / 1000f) * fontSize;

                    float margin = pageWidth * 0.005f;
                    float xPos = Math.max(margin, pageWidth - textWidth - margin);
                    float yPos = margin;

                    contentStream.beginText();
                    contentStream.newLineAtOffset(xPos, yPos);
                    contentStream.showText(timestamp);
                    contentStream.endText();
                }

                document.saveIncremental(out);
                LOG.info("PDF timestamp appended incrementally.");

            } catch (Exception pdfEx) {
                LOG.error("Error during PDF post-processing for '{}': {}", newFileName, pdfEx.getMessage(), pdfEx);
                throw pdfEx;
            }
            
         // --- Step 3: Replace Aspose Watermark ---
            watermarkRemovedFile = File.createTempFile("watermark_removed_", ".pdf");
            try {
                watermarkRemovedFile = replaceAsposeWatermark(
                        finalOutputFile,
                        watermarkRemovedFile.getAbsolutePath(),
                        ""
                );
                LOG.info("Aspose watermark replaced successfully.");
            } catch (Exception wmEx) {
                LOG.error("Failed to replace Aspose watermark: {}", wmEx.getMessage(), wmEx);
                throw wmEx;
            }

            // --- Step 4: Store to Filestore ---
            try {
                FileStoreMapper fileStoreMapper = fileStoreService.store(
                        watermarkRemovedFile,              // ← use watermark-removed file
                        finalOutputFile.getName(),
                        edcrApplication.getDxfFile().getContentType(), FILESTORE_MODULECODE);

                edcrApplication.getEdcrApplicationDetails()
                        .get(0).setScrutinizedDxfFileId(fileStoreMapper);

                LOG.info("File stored in filestore: {}",
                        fileStoreMapper != null ? fileStoreMapper.getFileStoreId() : "null");
            } catch (Exception storeEx) {
                LOG.error("Failed to store generated PDF: {}", storeEx.getMessage(), storeEx);
                throw storeEx;
            }

        } catch (Exception e) {
            LOG.error("Error in updateFile() for '{}': {}", newFileName, e.getMessage(), e);
        } finally {
        	if (tempPdf != null && tempPdf.exists() && !tempPdf.delete()) {
                LOG.warn("Temporary PDF not deleted: {}", tempPdf.getAbsolutePath());
            }
            if (watermarkRemovedFile != null && watermarkRemovedFile.exists() && !watermarkRemovedFile.delete()) {
                LOG.warn("Watermark-removed temp PDF not deleted: {}", watermarkRemovedFile.getAbsolutePath());
            }
            long elapsed = System.currentTimeMillis() - start;
            LOG.info("updateFile() completed in {} ms → {}", elapsed, newFileName);
        }
    }
   
 //Safe delete with retry (shared by both methods)
 private boolean safeDeleteWithRetry(Path path, int maxRetries, long sleepMillis) {
     for (int attempt = 1; attempt <= maxRetries; attempt++) {
         try {
             if (Files.deleteIfExists(path)) {
                 LOG.debug("✅ Successfully deleted file on attempt {}: {}", attempt, path);
                 return true;
             }
         } catch (FileSystemException fse) {
             LOG.debug("Attempt {} to delete '{}' failed (file locked): {}", attempt, path, fse.getMessage());
         } catch (Exception e) {
             LOG.debug("Attempt {} to delete '{}' failed: {}", attempt, path, e.getMessage());
         }

         // wait and retry
         try {
             Thread.sleep(sleepMillis);
         } catch (InterruptedException ignored) {
             Thread.currentThread().interrupt();
             break;
         }
     }
     LOG.warn("⚠️ Failed to delete file after {} retries: {}", maxRetries, path);
     return false;
 }

    @Transactional
    public EdcrApplication createRestEdcr(final EdcrApplication edcrApplication, EdcrRequest edcrRequest){
        String comparisonDcrNo = edcrApplication.getEdcrApplicationDetails().get(0).getComparisonDcrNumber();
        if (edcrApplication.getApplicationDate() == null)
            edcrApplication.setApplicationDate(new Date());
        edcrApplication.setApplicationNumber(applicationNumberGenerator.generate());
        edcrApplication.setSavedDxfFile(saveDXF(edcrApplication));
        edcrApplication.setStatus(ABORTED);
        edcrApplicationRepository.save(edcrApplication);
        edcrApplication.getEdcrApplicationDetails().get(0).setComparisonDcrNumber(comparisonDcrNo);
//        callDcrProcess(edcrApplication, NEW_SCRTNY);
        callDcrProcess(edcrApplication, NEW_SCRTNY,edcrRequest);
        edcrIndexService.updateEdcrRestIndexes(edcrApplication, NEW_SCRTNY);
        return edcrApplication;
    }
    
    public FileStoreMapper mergeSanctionLetter(JsonNode additionalDetails) throws IOException {
        String sanctionLetterId = additionalDetails
                .path("sanctionLetter")
                .path("filestoreId")
                .asText();
        
        String sanctionLetterTenant = additionalDetails
                .path("sanctionLetter")
                .path("tenantId")
                .asText();

        String uploadedDiagramId = additionalDetails
                .path("uploadedDiagram")
                .path("filestoreId")
                .asText();
        
        String dxfToPdfTenant = additionalDetails
                .path("uploadedDiagram")
                .path("tenantId")
                .asText();
        
        if(ApplicationThreadLocals.getFilestoreTenantID()==null) {
        	ApplicationThreadLocals.setFilestoreTenantID(sanctionLetterTenant);
        }
        
        dxfToPdfTenant = dxfToPdfTenant.substring(dxfToPdfTenant.indexOf('.') + 1);

        if (sanctionLetterId.isEmpty() || uploadedDiagramId.isEmpty()) {
            throw new IllegalArgumentException("Missing filestoreId in request");
        }        
        
        // Fetch files from filestore
        File sanctionLetterFile = fileStoreService.fetch(sanctionLetterId, FILESTORE_MODULECODE,sanctionLetterTenant);
        File uploadedDiagramFile = fileStoreService.fetch(uploadedDiagramId, "",dxfToPdfTenant);

        if (sanctionLetterFile == null || uploadedDiagramFile == null) {
            throw new FileNotFoundException("File not found in filestore");
        }

//        File updatedDiagramFile =
//                File.createTempFile("updated_diagram_", ".pdf");
//
//        updatedDiagramFile = replaceAsposeWatermark(
//                uploadedDiagramFile,
//                updatedDiagramFile.getAbsolutePath(),
//                ""
//        );
        
        // Step 1: Overlay on uploadedDiagram
        File overlayedDiagram = File.createTempFile("overlayed_", ".pdf");
        overlayedDiagram = overlayPlotDetails( uploadedDiagramFile, overlayedDiagram.getAbsolutePath(), additionalDetails );

        
        PDDocument dxfDoc = PDDocument.load(overlayedDiagram);
        PDRectangle dxfSize = dxfDoc.getPage(0).getMediaBox();
        dxfDoc.close();

        PDDocument d1 = PDDocument.load(sanctionLetterFile);
        System.out.println("Scaled sanction pages: " + d1.getNumberOfPages());
        d1.close();

        PDDocument d2 = PDDocument.load(overlayedDiagram);
        System.out.println("DXF pages: " + d2.getNumberOfPages());
        d2.close();

        
        File scaledSanctionLetter = scalePdfToMatch(sanctionLetterFile, dxfSize);

        //Step 2: Merge PDFs
        File mergedOutput = mergePdfFiles(
                scaledSanctionLetter,   //
                overlayedDiagram,
                "merged_sanction_letter"
        );

        //Store merged file
        FileStoreMapper fileStoreMapper = fileStoreService.store(
                mergedOutput,
                mergedOutput.getName(),
                "application/pdf",
                FILESTORE_MODULECODE,
                sanctionLetterTenant
        );

        return fileStoreMapper;
    }

    public File replaceAsposeWatermark(
            File inputPdf,
            String outputPath,
            String newText) throws IOException {

        LOG.info("========== REPLACING ASPOSE WATERMARK ==========");

        PDDocument document = PDDocument.load(inputPdf);
        int totalPages = document.getNumberOfPages();
        LOG.info("Total pages in PDF: {}", totalPages);

        int    targetPageIndex = -1;
        float  wmX = 0, wmY = 0, wmWidth = 0, wmHeight = 0;

        PDFTextStripper stripper = new PDFTextStripper();

        for (int i = 0; i < totalPages; i++) {
            stripper.setStartPage(i + 1);
            stripper.setEndPage(i + 1);
            String pageText = stripper.getText(document);

            if (pageText.contains("Aspose") || pageText.contains("Evaluation only")) {
                targetPageIndex = i;
                LOG.info("Aspose watermark detected on page index: {}", i);

                PDRectangle mediaBox = document.getPage(i).getMediaBox();
                float pageWidth  = mediaBox.getWidth();
                float pageHeight = mediaBox.getHeight();
                LOG.info("Page dimensions → width={}, height={}", pageWidth, pageHeight);

                float padding = pageHeight * 0.005f;

                // Watermark is always top-left; cover from top edge downward
                wmHeight = (pageHeight * 0.040f) + (padding * 2);
                wmWidth  = (pageWidth  * 0.35f)  + (padding * 2);
                wmX      = -padding;
                wmY      = pageHeight - wmHeight - padding;  // near top in PDFBox coords

                LOG.info("Cover rect → x={}, y={}, w={}, h={}", wmX, wmY, wmWidth, wmHeight);
                break;
            }
        }

        if (targetPageIndex == -1) {
            LOG.warn("No Aspose watermark found in PDF — saving unchanged.");
            File outputFile = new File(outputPath);
            document.save(outputFile);
            document.close();
            return outputFile;
        }

        PDPage targetPage = document.getPage(targetPageIndex);

        PDPageContentStream contentStream = new PDPageContentStream(
                document,
                targetPage,
                PDPageContentStream.AppendMode.APPEND,
                true,
                true
        );

        // WHITE RECTANGLE — covers the watermark
        contentStream.setNonStrokingColor(255, 255, 255);
        contentStream.addRect(wmX, wmY, wmWidth, wmHeight);
        contentStream.fill();
        LOG.info("Watermark area covered successfully");

        // Optional replacement text (pass "" to just erase)
        if (newText != null && !newText.isEmpty()) {
            float fontSize = Math.max(10f, targetPage.getMediaBox().getWidth() * 0.008f);
            contentStream.beginText();
            contentStream.setNonStrokingColor(0, 0, 0);
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize);
            contentStream.newLineAtOffset(wmX + 10, wmY + (wmHeight / 2f) - (fontSize / 2f));
            contentStream.showText(newText);
            contentStream.endText();
        }

        contentStream.close();

        File outputFile = new File(outputPath);
        document.save(outputFile);
        document.close();

        LOG.info("Updated PDF saved at: {}", outputFile.getAbsolutePath());
        LOG.info("========== WATERMARK REPLACED ==========");

        return outputFile;
    }
         
    public File scalePdfToMatch(File inputPdf, PDRectangle targetSize) throws IOException {

        File outputFile = File.createTempFile("scaled_", ".pdf");

        try (PDDocument inputDoc  = PDDocument.load(inputPdf);
             PDDocument outputDoc = new PDDocument()) {

            org.apache.pdfbox.multipdf.LayerUtility layerUtility =
                    new org.apache.pdfbox.multipdf.LayerUtility(outputDoc);

            for (int i = 0; i < inputDoc.getNumberOfPages(); i++) {

                PDPage srcPage = inputDoc.getPage(i);
                PDPage newPage = new PDPage(targetSize);
                outputDoc.addPage(newPage);

                PDFormXObject pageForm = layerUtility.importPageAsForm(inputDoc, i);

                float srcW = srcPage.getMediaBox().getWidth();
                float srcH = srcPage.getMediaBox().getHeight();

                float scaleX = targetSize.getWidth()  / srcW;
                float scaleY = targetSize.getHeight() / srcH;
                float scale  = Math.min(scaleX, scaleY) * 0.8f;

                float offsetX = (targetSize.getWidth()  - srcW * scale) / 2f;
                float offsetY = (targetSize.getHeight() - srcH * scale) / 2f;

                try (PDPageContentStream cs = new PDPageContentStream(outputDoc, newPage)) {
                    cs.transform(Matrix.getTranslateInstance(offsetX, offsetY));
                    cs.transform(Matrix.getScaleInstance(scale, scale));
                    cs.drawForm(pageForm);
                }
            }

            outputDoc.save(outputFile);
        }

        return outputFile;
    }

    
    public File overlayPlotDetails(
            File inputPdf,
            String outputFileName,
            JsonNode additionalDetails) throws IOException {

        LOG.info("Overlaying plot details using template renderer: {}", inputPdf.getName());

        return pdfOverlayTemplateService.impose(
                inputPdf,
                outputFileName,
                additionalDetails,
                EXPAND_RIGHT,
                EXPAND_BOTTOM,
                GAP_DRAWING_TO_TABLES,
                GAP_TOP);
    }
    
    public Object getUserData(String uuid) {

        Map<String, Object> request = new HashMap<>();
        request.put("tenantId", "pb");
        request.put("uuid", Collections.singletonList(uuid));
        request.put("pageSize", "100");

        return serviceRequestRepository.fetchResult(
                bpaMdmsUtil.getUserSearchUrl(),
                request
        );
    }
    
	public FileStoreMapper updateDXFOutput(String fileNo, String examinedBy, String approvedBy, String approvedDate,
			String validDate, String edcrNo, Boolean isSelfCertification, String eSign, String eSignName,
			String tenantId, String zone) throws IOException {

		if (StringUtils.isBlank(edcrNo)) {
			throw new IllegalArgumentException("EDCR Number is mandatory.");
		}

		EdcrRequest edcrRequest = new EdcrRequest();
		edcrRequest.setEdcrNumber(edcrNo);
		edcrRequest.setTenantId(tenantId);

		RequestInfoWrapper reqInfoWrapper = new RequestInfoWrapper();

		List<EdcrDetail> edcrDetail = edcrRestService.fetchEdcr(edcrRequest, reqInfoWrapper);

		if (CollectionUtils.isEmpty(edcrDetail)) {
			throw new IllegalArgumentException("No EDCR details found for EDCR Number : " + edcrNo);
		}

		EdcrDetail detail = edcrDetail.get(0);

		String applicationNumber = detail.getApplicationNumber();
		Plan pl = detail.getPlanDetail();

		Map<String, String> params = getfFileStoreId(detail.getDxfFile());

		String dxfFileStoreId = params.get("fileStoreId");
		String dxfFileTenantId = params.get("tenantId");

		if (StringUtils.isBlank(dxfFileStoreId)) {
			throw new IllegalStateException("DXF File Store Id not found for EDCR Number : " + edcrNo);
		}

		File dxfFile = fileStoreService.fetch(dxfFileStoreId, FILESTORE_MODULECODE, dxfFileTenantId);

		if (dxfFile == null || !dxfFile.exists()) {
			throw new FileNotFoundException("DXF file not found in filestore : " + dxfFileStoreId);
		}

		EdcrApplication edcrApplication = edcrApplicationRepository.findByApplicationNumber(applicationNumber);

		if (edcrApplication == null) {
			throw new IllegalArgumentException(
					"EDCR Application not found for application number : " + applicationNumber);
		}

		if (CollectionUtils.isEmpty(edcrApplication.getEdcrApplicationDetails())) {
			throw new IllegalStateException(
					"EDCR Application Details not found for application number : " + applicationNumber);
		}

		EdcrApplicationDetail appDetail = edcrApplication.getEdcrApplicationDetails().get(0);

		if (appDetail.getScrutinizedDxfFileId() == null) {
			throw new IllegalStateException("Scrutinized DXF File mapping not found.");
		}

		Long oldScrutinizedFileStoreId = appDetail.getScrutinizedDxfFileId().getId();

		edcrApplication.setSavedDxfFile(dxfFile);

		updateFileNew(pl, edcrApplication, fileNo, examinedBy, approvedBy, approvedDate, validDate, eSignName, eSign,
				tenantId, zone);

		FileStoreMapper mapper = appDetail.getScrutinizedDxfFileId();

		if (mapper == null) {
			throw new IllegalStateException("New scrutinized DXF file mapping was not generated.");
		}

		fileStoreMapperRepository.updateFileStoreId(oldScrutinizedFileStoreId, mapper.getFileStoreId());

		//appDetail.setScrutinizedDxfFileId(mapper);

		//edcrApplicationRepository.save(edcrApplication);
		//edcrApplicationDetailService.saveAll(edcrApplication.getEdcrApplicationDetails());

		return mapper;
	}
 
private Map<String, String> getfFileStoreId(String detail) {

    Map<String, String> params = new HashMap<>();

    if (StringUtils.isBlank(detail) || !detail.contains("?")) {
        return params;
    }

    String queryString = detail.split("\\?", 2)[1];

    String[] queryParams = queryString.split("&");

    for (String param : queryParams) {

        if (StringUtils.isBlank(param)) {
            continue;
        }

        String[] paramArr = param.split("=", 2);

        String key = paramArr[0];
        String value = paramArr.length > 1 ? paramArr[1] : "";

        params.put(key, value);
    }

    return params;
}

public class CustomMultipartFile implements MultipartFile {

    private final byte[] content;
    private final String fileName;

    public CustomMultipartFile(File file) throws IOException {
        this.fileName = file.getName();
        this.content = Files.readAllBytes(file.toPath());
    }

    @Override
    public String getName() {
        return fileName;
    }

    @Override
    public String getOriginalFilename() {
        return fileName;
    }

    @Override
    public String getContentType() {
        return "application/octet-stream";
    }

    @Override
    public boolean isEmpty() {
        return content.length == 0;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @Override
    public byte[] getBytes() {
        return content;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(content);
    }

    @Override
    public void transferTo(File dest) throws IOException {
        Files.copy(getInputStream(), dest.toPath());
    }
}

}







