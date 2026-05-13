package org.egov.edcr.service;

import static org.egov.edcr.utility.DcrConstants.FILESTORE_MODULECODE;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
//import org.egov.edcr.contract.EdcrRequest;
import org.egov.common.edcr.model.EdcrRequest;
import org.egov.edcr.entity.ApplicationType;
import org.egov.edcr.entity.EdcrApplication;
import org.egov.edcr.entity.EdcrApplicationDetail;
import org.egov.edcr.entity.SearchBuildingPlanScrutinyForm;
import org.egov.edcr.repository.EdcrApplicationDetailRepository;
import org.egov.edcr.repository.EdcrApplicationRepository;
import org.egov.edcr.service.es.EdcrIndexService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.config.persistence.datasource.routing.annotation.ReadOnly;
import org.egov.infra.filestore.entity.FileStoreMapper;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.utils.ApplicationNumberGenerator;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

//import com.aspose.cad.Color;
//import com.aspose.cad.Image;
//import com.aspose.cad.fileformats.cad.CadDrawTypeMode;
//import com.aspose.cad.imageoptions.CadRasterizationOptions;
//import com.aspose.cad.imageoptions.PdfOptions;

@Service
@Transactional(readOnly = true)
public class EdcrApplicationService {
    private static final String RESUBMIT_SCRTNY = "Resubmit Plan Scrutiny";
    private static final String NEW_SCRTNY = "New Plan Scrutiny";
    public static final String ULB_NAME = "ulbName";
    public static final String ABORTED = "Aborted";
    private static Logger LOG = LogManager.getLogger(EdcrApplicationService.class);
    
    
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

    @Autowired
    private EdcrApplicationDetailService edcrApplicationDetailService;

    public Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
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
        updateFile(planDetail, edcrApplication);
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
    
  

    
//    private void updateFile(Plan pl, EdcrApplication edcrApplication) {
//        String filePath = edcrApplication.getSavedDxfFile().getAbsolutePath();
//        String newFile = edcrApplication.getDxfFile().getOriginalFilename().replace(".dxf", "_system_scrutinized.pdf");
//
//        // Load the source CAD file
//        Image objImage = Image.load(filePath);
//
//        // Create an instance of PdfOptions
//        PdfOptions pdfOptions = new PdfOptions();
//
//        // Create rasterization options and configure scaling
//        CadRasterizationOptions rasterizationOptions = new CadRasterizationOptions();
//        rasterizationOptions.setBackgroundColor(Color.getWhite()); // Set background color if needed
//        rasterizationOptions.setDrawType(CadDrawTypeMode.UseObjectColor); // Ensure object colors are used
//
//        // Set the page size (A0 size in points)
//        rasterizationOptions.setPageWidth(3370); // A0 width in points
//        rasterizationOptions.setPageHeight(2384); // A0 height in points
//
//        // Ensure content fits within the page size
//        rasterizationOptions.setAutomaticLayoutsScaling(true);
//        rasterizationOptions.setNoScaling(false);
//
//        pdfOptions.setVectorRasterizationOptions(rasterizationOptions);
//
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//
//        // Export CAD to PDF
//        objImage.save(outputStream, pdfOptions);
//
//        byte[] pdfBytes = outputStream.toByteArray();
//
//        try (PDDocument document = PDDocument.load(pdfBytes)) {
//            // Get the first page to set the view
//            PDPageTree pages = document.getPages();
//            PDPage page = pages.get(0);
//
//            // Set the destination to center of the page
//            PDPageXYZDestination dest = new PDPageXYZDestination();
//            dest.setPage(page);
//
//            // Calculate the center coordinates
//            float pageWidth = page.getMediaBox().getWidth();
//            float pageHeight = page.getMediaBox().getHeight();
//            int centerX = (int) (pageWidth / 2.0f);
//            int centerY = (int) (pageHeight / 2.0f);
//
//            dest.setLeft(centerX);
//            dest.setTop(centerY);
//            dest.setZoom(1.0f); // Adjust the zoom level if necessary
//
//            // Set the open action
//            PDDocumentCatalog catalog = document.getDocumentCatalog();
//            catalog.setOpenAction(dest);
//
//            byte[] modifiedPdfBytes;
//
//            // Create a new content stream to add the watermark
//            PDPageContentStream contentStream = new PDPageContentStream(document, page,
//                    PDPageContentStream.AppendMode.APPEND, true, true);
//
//            PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
//            graphicsState.setNonStrokingAlphaConstant(0.2f); // Set lower opacity
//            graphicsState.setAlphaSourceFlag(true);
//            contentStream.setGraphicsStateParameters(graphicsState);
//
////            InputStream imageStream = EdcrApplication.class.getResourceAsStream("/tcpicon.jpg");
////            java.awt.image.BufferedImage image1 = ImageIO.read(imageStream);
////            PDImageXObject image = LosslessFactory.createFromImage(document, image1);
////    
////            // Calculate the position to center the watermark
////            float scale = 10f; // Smaller scale for the watermark
////            float watermarkWidth = image.getWidth() * scale;
////            float watermarkHeight = image.getHeight() * scale;
////            float watermarkXPos = (pageWidth - watermarkWidth) / 2; // Center horizontally
////            float watermarkYPos = (pageHeight - watermarkHeight) / 2; // Center vertically
////
////            // Draw the watermark image on the page
////            contentStream.drawImage(image, watermarkXPos, watermarkYPos, watermarkWidth, watermarkHeight);
//
//            // Add timestamp
//            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//            contentStream.beginText();
//            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 200);
//
//            // Estimate the width of the timestamp text
//            float textWidth = (PDType1Font.HELVETICA_BOLD.getStringWidth(timestamp) / 1000) * 200;
//
//            // Set the position to bottom right corner
//            float xPos = pageWidth - textWidth - 700; // 700 units margin from the right edge
//            float yPos = 10; // 10 units margin from the bottom edge
//
//            contentStream.newLineAtOffset(xPos, yPos); // Position the timestamp at the bottom right corner
//
//            PDExtendedGraphicsState graphicsState1 = new PDExtendedGraphicsState();
//            graphicsState1.setNonStrokingAlphaConstant(0.7f); // Set text opacity
//            contentStream.setGraphicsStateParameters(graphicsState1);
//
//            contentStream.showText(timestamp);
//            contentStream.endText();
//
//            // Close the content stream
//            contentStream.close();
//
//            // Save the modified PDF
//            ByteArrayOutputStream modifiedPdfStream = new ByteArrayOutputStream();
//            document.save(modifiedPdfStream);
//
//            // Convert the modified PDF to a byte array
//            modifiedPdfBytes = modifiedPdfStream.toByteArray();
//
//            File f = new File(newFile);
//            try (FileOutputStream fos = new FileOutputStream(f)) {
//                if (!f.exists())
//                    f.createNewFile();
//                fos.write(modifiedPdfBytes);
//                fos.flush();
//                FileStoreMapper fileStoreMapper = fileStoreService.store(f, f.getName(),
//                        edcrApplication.getDxfFile().getContentType(), FILESTORE_MODULECODE);
//                edcrApplication.getEdcrApplicationDetails().get(0).setScrutinizedDxfFileId(fileStoreMapper);
//            } catch (IOException e) {
//                LOG.error("Error occurred when reading file!!!!!", e);
//            }
//        } catch (IOException e) {
//            LOG.error("Error occurred when processing PDF!!!!!", e);
//        }
//    }
    
//    private File mergePdfFiles(File pdf1, File pdf2, String outputFileName) throws IOException {
//
//        LOG.info("🔗 Merging PDFs: {} + {}", pdf1.getName(), pdf2.getName());
//
//        File mergedFile = new File(outputFileName);
//
//        PDFMergerUtility merger = new PDFMergerUtility();
//        merger.setDestinationFileName(mergedFile.getAbsolutePath());
//
//        merger.addSource(pdf1);
//        merger.addSource(pdf2);
//
//        // Use temp file strategy to avoid memory issues
//        merger.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());
//
//        LOG.info("✅ PDF merge completed: {}", mergedFile.getAbsolutePath());
//
//        return mergedFile;
//    }
    
    private File mergePdfFiles(File pdf1, File pdf2, String outputFileName) throws IOException {

        LOG.info("🔗 Merging PDFs: {} + {}", pdf1.getName(), pdf2.getName());

        // ✅ Ensure .pdf extension
        if (!outputFileName.toLowerCase().endsWith(".pdf")) {
            outputFileName = outputFileName + ".pdf";
        }

        // ✅ Create file safely in temp directory
        String tempDir = System.getProperty("java.io.tmpdir");
        String uniqueFileName = outputFileName.replace(".pdf", "") 
                + "_" + System.currentTimeMillis() + ".pdf";

        File mergedFile = new File(tempDir, uniqueFileName);

        // ✅ Initialize merger
        PDFMergerUtility merger = new PDFMergerUtility();

        merger.addSource(pdf1);
        merger.addSource(pdf2);

        merger.setDestinationFileName(mergedFile.getAbsolutePath());

        // ✅ Use temp file strategy (best for large PDFs)
        merger.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());

        LOG.info("✅ PDF merge completed: {}", mergedFile.getAbsolutePath());

        // ✅ Final safety check
        if (!mergedFile.exists() || mergedFile.length() == 0) {
            throw new IOException("Merged PDF file is empty or not created properly");
        }

        return mergedFile;
    }

//private void updateFile(Plan pl, EdcrApplication edcrApplication) {
//    long start = System.currentTimeMillis();
//    String filePath = edcrApplication.getSavedDxfFile().getAbsolutePath();
//    String newFileName = edcrApplication.getDxfFile().getOriginalFilename()
//            .replace(".dxf", "_system_scrutinized.pdf");
//    File finalOutputFile = new File(newFileName);
//
//    LOG.info("🔄 Starting scrutinized PDF generation for: {}", newFileName);
//
//    File tempPdf = null;
//    try {
//        // --- Step 1: Convert DXF → PDF using Aspose CAD ---
//        tempPdf = File.createTempFile("scrutinized_", ".pdf");
//        LOG.debug("Temporary PDF path: {}", tempPdf.getAbsolutePath());
//
//        try (Image cadImage = Image.load(filePath);
//             FileOutputStream tempOut = new FileOutputStream(tempPdf)) {
//
//            PdfOptions pdfOptions = new PdfOptions();
//            CadRasterizationOptions rasterOpts = new CadRasterizationOptions();
//            rasterOpts.setBackgroundColor(Color.getWhite());
//            rasterOpts.setDrawType(CadDrawTypeMode.UseObjectColor);
//            rasterOpts.setPageWidth(2480); // ~A4 horizontal, smaller to reduce memory
//            rasterOpts.setPageHeight(3508); // ~A4 vertical
//            rasterOpts.setAutomaticLayoutsScaling(true);
//            rasterOpts.setNoScaling(false);
//            pdfOptions.setVectorRasterizationOptions(rasterOpts);
//
//            cadImage.save(tempOut, pdfOptions);
//            LOG.debug("✅ CAD to PDF conversion complete.");
//        } catch (OutOfMemoryError oom) {
//            LOG.error("❌ OutOfMemoryError while converting DXF → PDF: {}", filePath, oom);
//            throw oom;
//        } catch (Exception ex) {
//            LOG.error("❌ Error converting DXF → PDF: {}", filePath, ex);
//            throw ex;
//        }
//
//        // --- Step 2: Post-process PDF (timestamp, incremental save) ---
//        try (RandomAccessBufferedFileInputStream rar = new RandomAccessBufferedFileInputStream(tempPdf);
//             PDDocument document = PDDocument.load(rar);
//             BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(finalOutputFile))) {
//
//            PDPage page = document.getPage(0);
//            float pageWidth = page.getMediaBox().getWidth();
//            float pageHeight = page.getMediaBox().getHeight();
//
//            // Set initial view to center
//            PDPageXYZDestination dest = new PDPageXYZDestination();
//            dest.setPage(page);
//            dest.setLeft((int) (pageWidth / 2f));
//            dest.setTop((int) (pageHeight / 2f));
//            dest.setZoom(1.0f);
//            document.getDocumentCatalog().setOpenAction(dest);
//
//            try (PDPageContentStream contentStream = new PDPageContentStream(
//                    document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
//
//                PDExtendedGraphicsState gs = new PDExtendedGraphicsState();
//                gs.setNonStrokingAlphaConstant(0.7f);
//                contentStream.setGraphicsStateParameters(gs);
//
//                // --- (COMMENTED WATERMARK IMAGE CODE - preserved) ---
////                InputStream imageStream = EdcrApplication.class.getResourceAsStream("/tcpicon.jpg");
////                java.awt.image.BufferedImage image1 = ImageIO.read(imageStream);
////                PDImageXObject image = LosslessFactory.createFromImage(document, image1);
////                float scale = 10f;
////                float watermarkWidth = image.getWidth() * scale;
////                float watermarkHeight = image.getHeight() * scale;
////                float watermarkXPos = (pageWidth - watermarkWidth) / 2;
////                float watermarkYPos = (pageHeight - watermarkHeight) / 2;
////                contentStream.drawImage(image, watermarkXPos, watermarkYPos, watermarkWidth, watermarkHeight);
//
//                // --- Add timestamp ---
//                String timestamp = LocalDateTime.now().format(TS_FORMAT);
//                float fontSize = 24f;
//                contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize);
//                float textWidth = (PDType1Font.HELVETICA_BOLD.getStringWidth(timestamp) / 1000f) * fontSize;
//
//                float xPos = Math.max(20, pageWidth - textWidth - 20);
//                float yPos = 20f;
//                contentStream.beginText();
//                contentStream.newLineAtOffset(xPos, yPos);
//                contentStream.showText(timestamp);
//                contentStream.endText();
//            }
//
//            // Incremental save to reduce memory usage
//            document.saveIncremental(out);
//            LOG.info("✅ PDF timestamp appended incrementally.");
//
//        } catch (Exception pdfEx) {
//            LOG.error("❌ Error during PDF post-processing for '{}': {}", newFileName, pdfEx.getMessage(), pdfEx);
//            throw pdfEx;
//        }
//
//        // --- Step 3: Store to Filestore ---
//        try {
//            FileStoreMapper fileStoreMapper = fileStoreService.store(
//                    finalOutputFile, finalOutputFile.getName(),
//                    edcrApplication.getDxfFile().getContentType(), FILESTORE_MODULECODE);
//
//            edcrApplication.getEdcrApplicationDetails()
//                    .get(0).setScrutinizedDxfFileId(fileStoreMapper);
//
//            LOG.info("📁 File stored successfully in filestore: {}", 
//                    fileStoreMapper != null ? fileStoreMapper.getFileStoreId() : "null");
//        } catch (Exception storeEx) {
//            LOG.error("❌ Failed to store generated PDF in filestore: {}", storeEx.getMessage(), storeEx);
//            throw storeEx;
//        }
//
//    } catch (Exception e) {
//        LOG.error("🚨 Error in updateFile() for '{}': {}", newFileName, e.getMessage(), e);
//    } finally {
//        if (tempPdf != null && tempPdf.exists() && !tempPdf.delete()) {
//            LOG.warn("⚠️ Temporary PDF not deleted: {}", tempPdf.getAbsolutePath());
//        }
//        long elapsed = System.currentTimeMillis() - start;
//        LOG.info("⚡ updateFile() completed in {} ms → {}", elapsed, newFileName);
//    }
//}
    
//    private File overlayPlotDetails(File inputPdf, String outputFileName) throws IOException {
//
//        LOG.info("🖊️ Overlaying plot details on PDF: {}", inputPdf.getName());
//
//        File outputFile = new File(outputFileName);
//
//        try (PDDocument document = PDDocument.load(inputPdf)) {
//
//            for (PDPage page : document.getPages()) {
//
//                float pageWidth = page.getMediaBox().getWidth();
//                float pageHeight = page.getMediaBox().getHeight();
//
//                // ✅ TOP-LEFT position (just below Aspose watermark)
//                float startX = 500f;
//
//                // 🔥 IMPORTANT: Push BELOW watermark safely
//                float startY = pageHeight - 920f;  
//                // adjust 120 → 150 if still overlapping
//
//                float lineHeight = 18f;
//
//                try (PDPageContentStream contentStream = new PDPageContentStream(
//                        document,
//                        page,
//                        PDPageContentStream.AppendMode.APPEND,
//                        true,
//                        true)) {
//
//                    // ✅ Strong visible text
//                    contentStream.setNonStrokingColor(0, 0, 255); // BLUE
//                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
//
//                    PDExtendedGraphicsState gs = new PDExtendedGraphicsState();
//                    gs.setNonStrokingAlphaConstant(1f);
//                    contentStream.setGraphicsStateParameters(gs);
//
//                    // --- Dummy Data ---
//                    String fileNo = "FILE-12345";
//                    String professionalName = "John Doe";
//
//                    String totalPlotArea = "188.28";
//                    String groundCoverage = "122.09";
//                    String builtUpArea = "325.45";
//
//                    contentStream.beginText();
//                    contentStream.newLineAtOffset(startX, startY);
//
//                    contentStream.showText("File No: " + fileNo);
//                    contentStream.newLineAtOffset(0, -lineHeight);
//
//                    contentStream.showText("Professional Name: " + professionalName);
//                    contentStream.newLineAtOffset(0, -lineHeight * 2);
//
//                    contentStream.showText("Total Plot Area: " + totalPlotArea);
//                    contentStream.newLineAtOffset(0, -lineHeight);
//
//                    contentStream.showText("Ground Coverage: " + groundCoverage);
//                    contentStream.newLineAtOffset(0, -lineHeight);
//
//                    contentStream.showText("Built Up Area: " + builtUpArea);
//
//                    contentStream.endText();
//                }
//            }
//
//            document.save(outputFile);
//        }
//
//        LOG.info("✅ Plot details placed below Aspose watermark");
//        return outputFile;
//    }
    
//    private File overlayPlotDetails(File inputPdf, String outputFileName) throws IOException {
//
//        LOG.info("🖊️ Overlaying plot details on PDF: {}", inputPdf.getName());
//
//        File outputFile = new File(outputFileName);
//
//        try (PDDocument document = PDDocument.load(inputPdf)) {
//
//            for (PDPage page : document.getPages()) {
//
//                float pageWidth = page.getMediaBox().getWidth();
//                float pageHeight = page.getMediaBox().getHeight();
//
//                // ✅ TOP-LEFT position (just below Aspose watermark)
//                float startX = 500f;
//
//                // 🔥 IMPORTANT: Push BELOW watermark safely
//                float startY = pageHeight - 920f;  
//                // adjust 120 → 150 if still overlapping
//
//                //float lineHeight = 18f;
//                
//                float rowHeight = 25f;
//                float col1Width = 200f;
//                float col2Width = 150f;
//
//                try (PDPageContentStream contentStream = new PDPageContentStream(
//                        document,
//                        page,
//                        PDPageContentStream.AppendMode.APPEND,
//                        true,
//                        true)) {
//
//                    // 🔵 Border color
//                    contentStream.setStrokingColor(0, 0, 0); // black borders
//
//                    // ✅ Strong visible text
//                    //contentStream.setNonStrokingColor(0, 0, 255); // BLUE
//                 // 🔵 Text color
//                    contentStream.setNonStrokingColor(0, 0, 255); // blue text
//                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
//
//                    // --- Dummy Data ---
//                    String[][] tableData = {
//                            {"File No", "FILE-12345"},
//                            {"Professional Name", "John Doe"},
//                            {"Total Plot Area", "188.28"},
//                            {"Ground Coverage", "122.09"},
//                            {"Built Up Area", "325.45"}
//                    };
//
//                    int rows = tableData.length;
//                    float tableWidth = col1Width + col2Width;
//
//                    // =========================
//                    // ✅ DRAW TABLE GRID
//                    // =========================
//
//                    float yPosition = startY;
//
//                    for (int i = 0; i <= rows; i++) {
//                        contentStream.moveTo(startX, yPosition - i * rowHeight);
//                        contentStream.lineTo(startX + tableWidth, yPosition - i * rowHeight);
//                    }
//
//                    // Vertical lines
//                    contentStream.moveTo(startX, yPosition);
//                    contentStream.lineTo(startX, yPosition - rows * rowHeight);
//
//                    contentStream.moveTo(startX + col1Width, yPosition);
//                    contentStream.lineTo(startX + col1Width, yPosition - rows * rowHeight);
//
//                    contentStream.moveTo(startX + tableWidth, yPosition);
//                    contentStream.lineTo(startX + tableWidth, yPosition - rows * rowHeight);
//
//                    contentStream.stroke();
//
//                    // =========================
//                    // ✅ ADD TEXT INTO TABLE
//                    // =========================
//
//                    float textXOffset = 5f;
//                    float textYOffset = 17f;
//
//                    for (int i = 0; i < rows; i++) {
//
//                        float textY = yPosition - (i * rowHeight) - textYOffset;
//
//                        // Column 1
//                        contentStream.beginText();
//                        contentStream.newLineAtOffset(startX + textXOffset, textY);
//                        contentStream.showText(tableData[i][0]);
//                        contentStream.endText();
//
//                        // Column 2
//                        contentStream.beginText();
//                        contentStream.newLineAtOffset(startX + col1Width + textXOffset, textY);
//                        contentStream.showText(tableData[i][1]);
//                        contentStream.endText();
//                    }
//                }
//            }
//
//            document.save(outputFile);
//        }
//
//        LOG.info("✅ Table overlay added successfully");
//        return outputFile;
//    }
    
    private File overlayPlotDetails(
            File inputPdf,
            String outputFileName,
            JsonNode additionalDetails) throws IOException {

        LOG.info("Overlaying plot details on PDF: {}", inputPdf.getName());

        File outputFile = new File(outputFileName);

        try (PDDocument document = PDDocument.load(inputPdf)) {

            JsonNode details = additionalDetails.path("details");

            List<String[]> tableDataList = new ArrayList<>();

            addRow(tableDataList, "ULB Name", details.path("ulbName").asText());
            addRow(tableDataList, "Approval Date", details.path("dateOfApproval").asText());
            addRow(tableDataList, "File Number", details.path("fileNumber").asText());
            addRow(tableDataList, "Building Category", details.path("buildingCategory").asText());
            addRow(tableDataList, "Professional Name", details.path("professionalName").asText());
            addRow(tableDataList, "Plot Area", details.path("plotArea").asText());
            addRow(tableDataList, "Built Up Area", details.path("builtUpArea").asText());
            addRow(tableDataList, "Auto Approved", details.path("isAutoApproved").asText());

            String[][] tableData = tableDataList.toArray(new String[0][]);

            for (PDPage page : document.getPages()) {

                float pageWidth = page.getMediaBox().getWidth();
                float pageHeight = page.getMediaBox().getHeight();

                float startX = pageWidth - 650f;
                float startY = pageHeight - 1100f;
                
                // Bigger Table & Font
                float rowHeight = 40f;
                float col1Width = 260f;
                float col2Width = 320f;

                float tableWidth = col1Width + col2Width;

                int rows = tableData.length;

                try (PDPageContentStream contentStream = new PDPageContentStream(
                        document, page, PDPageContentStream.AppendMode.APPEND, true,true)) {

                    // Border Styling
                    contentStream.setLineWidth(2f);
                    contentStream.setStrokingColor(0, 0, 0);
                    // Text Styling
                    contentStream.setNonStrokingColor(255, 0, 0);

                    // Bigger Font
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);

                    float yPosition = startY;
            
                    // Draw Horizontal Lines
                    for (int i = 0; i <= rows; i++) {

                        float y = yPosition - (i * rowHeight);

                        contentStream.moveTo(startX, y);
                        contentStream.lineTo(startX + tableWidth, y);
                    }
                    
                    //Draw Vertical Lines
                    contentStream.moveTo(startX, yPosition);
                    contentStream.lineTo(startX, yPosition - rows * rowHeight);

                    contentStream.moveTo(startX + col1Width, yPosition);
                    contentStream.lineTo(startX + col1Width, yPosition - rows * rowHeight);

                    contentStream.moveTo(startX + tableWidth, yPosition);
                    contentStream.lineTo(startX + tableWidth, yPosition - rows * rowHeight);

                    contentStream.stroke();

                    // Add Text
                    float textXOffset = 10f;
                    float textYOffset = 26f;

                    for (int i = 0; i < rows; i++) {

                        float textY = yPosition - (i * rowHeight) - textYOffset;
                        // Column 1
                        contentStream.beginText();

                        contentStream.newLineAtOffset(startX + textXOffset,textY);

                        contentStream.showText(
                                tableData[i][0] != null? tableData[i][0]: "");

                        contentStream.endText();

                        // Column 2
                        contentStream.beginText();

                        contentStream.newLineAtOffset(startX + col1Width + textXOffset,textY);

                        contentStream.showText(tableData[i][1] != null? tableData[i][1]: "");
                        contentStream.endText();
                    }
                }
            }

            document.save(outputFile);
        }

        LOG.info("Dynamic table overlay added successfully");

        return outputFile;
    }

    private void addRow(List<String[]> tableDataList, String key, String value) {

        if (!StringUtils.isEmpty(value)
                && !"null".equalsIgnoreCase(value)) {

            tableDataList.add(new String[]{key, value});
        }
    }


    
//    private void updateFile(Plan pl, EdcrApplication edcrApplication) {
//        long start = System.currentTimeMillis();
//        String filePath = edcrApplication.getSavedDxfFile().getAbsolutePath();
//        String newFileName = edcrApplication.getDxfFile().getOriginalFilename()
//                .replace(".dxf", "_system_scrutinized.pdf");
//        File finalOutputFile = new File(newFileName);
//
//        LOG.info("🔄 Starting scrutinized PDF generation for: {}", newFileName);
//
//        File tempPdf = null;
//        try {
//            // --- Step 1: Convert DXF → PDF using Aspose CAD ---
//            tempPdf = File.createTempFile("scrutinized_", ".pdf");
//            LOG.debug("Temporary PDF path: {}", tempPdf.getAbsolutePath());
//
//            try (Image cadImage = Image.load(filePath);
//                 FileOutputStream tempOut = new FileOutputStream(tempPdf)) {
//
//                PdfOptions pdfOptions = new PdfOptions();
//                CadRasterizationOptions rasterOpts = new CadRasterizationOptions();
//                rasterOpts.setBackgroundColor(Color.getWhite());
//                rasterOpts.setDrawType(CadDrawTypeMode.UseObjectColor);
//                rasterOpts.setPageWidth(2480); // ~A4 horizontal, smaller to reduce memory
//                rasterOpts.setPageHeight(3508); // ~A4 vertical
//                rasterOpts.setAutomaticLayoutsScaling(true);
//                rasterOpts.setNoScaling(false);
//                pdfOptions.setVectorRasterizationOptions(rasterOpts);
//
//                cadImage.save(tempOut, pdfOptions);
//                LOG.debug("✅ CAD to PDF conversion complete.");
//            } catch (OutOfMemoryError oom) {
//                LOG.error("❌ OutOfMemoryError while converting DXF → PDF: {}", filePath, oom);
//                throw oom;
//            } catch (Exception ex) {
//                LOG.error("❌ Error converting DXF → PDF: {}", filePath, ex);
//                throw ex;
//            }
//
//            // --- Step 2: Post-process PDF (timestamp, incremental save) ---
//            try (RandomAccessBufferedFileInputStream rar = new RandomAccessBufferedFileInputStream(tempPdf);
//                 PDDocument document = PDDocument.load(rar);
//                 BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(finalOutputFile))) {
//
//                PDPage page = document.getPage(0);
//                float pageWidth = page.getMediaBox().getWidth();
//                float pageHeight = page.getMediaBox().getHeight();
//
//                // Set initial view to center
//                PDPageXYZDestination dest = new PDPageXYZDestination();
//                dest.setPage(page);
//                dest.setLeft((int) (pageWidth / 2f));
//                dest.setTop((int) (pageHeight / 2f));
//                dest.setZoom(1.0f);
//                document.getDocumentCatalog().setOpenAction(dest);
//
//                try (PDPageContentStream contentStream = new PDPageContentStream(
//                        document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
//
//                    PDExtendedGraphicsState gs = new PDExtendedGraphicsState();
//                    gs.setNonStrokingAlphaConstant(0.7f);
//                    contentStream.setGraphicsStateParameters(gs);
//
//                    // --- (COMMENTED WATERMARK IMAGE CODE - preserved) ---
////                    InputStream imageStream = EdcrApplication.class.getResourceAsStream("/tcpicon.jpg");
////                    java.awt.image.BufferedImage image1 = ImageIO.read(imageStream);
////                    PDImageXObject image = LosslessFactory.createFromImage(document, image1);
////                    float scale = 10f;
////                    float watermarkWidth = image.getWidth() * scale;
////                    float watermarkHeight = image.getHeight() * scale;
////                    float watermarkXPos = (pageWidth - watermarkWidth) / 2;
////                    float watermarkYPos = (pageHeight - watermarkHeight) / 2;
////                    contentStream.drawImage(image, watermarkXPos, watermarkYPos, watermarkWidth, watermarkHeight);
//
//                    // --- Add timestamp ---
//                    String timestamp = LocalDateTime.now().format(TS_FORMAT);
//                    float fontSize = 24f;
//                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize);
//                    float textWidth = (PDType1Font.HELVETICA_BOLD.getStringWidth(timestamp) / 1000f) * fontSize;
//
//                    float xPos = Math.max(20, pageWidth - textWidth - 20);
//                    float yPos = 20f;
//                    contentStream.beginText();
//                    contentStream.newLineAtOffset(xPos, yPos);
//                    contentStream.showText(timestamp);
//                    contentStream.endText();
//                }
//
//                // Incremental save to reduce memory usage
//                document.saveIncremental(out);
//                LOG.info("✅ PDF timestamp appended incrementally.");
//
//            } catch (Exception pdfEx) {
//                LOG.error("❌ Error during PDF post-processing for '{}': {}", newFileName, pdfEx.getMessage(), pdfEx);
//                throw pdfEx;
//            }
//
//            // --- Step 3: Store to Filestore ---
//            try {
//                FileStoreMapper fileStoreMapper = fileStoreService.store(
//                        finalOutputFile, finalOutputFile.getName(),
//                        edcrApplication.getDxfFile().getContentType(), FILESTORE_MODULECODE);
//
//                edcrApplication.getEdcrApplicationDetails()
//                        .get(0).setScrutinizedDxfFileId(fileStoreMapper);
//
//                LOG.info("📁 File stored successfully in filestore: {}", 
//                        fileStoreMapper != null ? fileStoreMapper.getFileStoreId() : "null");
//            } catch (Exception storeEx) {
//                LOG.error("❌ Failed to store generated PDF in filestore: {}", storeEx.getMessage(), storeEx);
//                throw storeEx;
//            }
//
//        } catch (Exception e) {
//            LOG.error("🚨 Error in updateFile() for '{}': {}", newFileName, e.getMessage(), e);
//        } finally {
//            if (tempPdf != null && tempPdf.exists() && !tempPdf.delete()) {
//                LOG.warn("⚠️ Temporary PDF not deleted: {}", tempPdf.getAbsolutePath());
//            }
//            long elapsed = System.currentTimeMillis() - start;
//            LOG.info("⚡ updateFile() completed in {} ms → {}", elapsed, newFileName);
//        }
//    }

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

    
//    private void updateFile(Plan pl, EdcrApplication edcrApplication) {
//        long start = System.currentTimeMillis();
//        String originalFileName = edcrApplication.getDxfFile().getOriginalFilename();
//        String newFileName = originalFileName.replace(".dxf", "_system_scrutinized.pdf");
//        File finalOutputFile = new File(newFileName);
//
//        LOG.info("🔄 Starting DXF → DWG → PDF process for: {}", newFileName);
//
//        File tempInputDir = new File(System.getProperty("java.io.tmpdir"), "teigha_input");
//        File tempOutputDir = new File(System.getProperty("java.io.tmpdir"), "teigha_output");
//        tempInputDir.mkdirs();
//        tempOutputDir.mkdirs();
//
//        try {
//            // --- Step 0: Copy DXF to temp folder with simple name ---
//            File originalDxfFile = edcrApplication.getSavedDxfFile();
//            File tempDxfFile = new File(tempInputDir, "temp.dxf");
//            Files.copy(originalDxfFile.toPath(), tempDxfFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//
//            // --- Step 1: DXF → DWG (TeighaFileConverter) ---
//            String converterPath = System.getProperty("os.name").toLowerCase().contains("win")
//                    ? "C:\\Program Files\\ODA\\ODAFileConverter 26.8.0\\ODAFileConverter.exe"
//                    : "/opt/ODA/TeighaFileConverter/TeighaFileConverter";
//
//            ProcessBuilder pb = new ProcessBuilder(
//                    converterPath,
//                    tempInputDir.getAbsolutePath(),
//                    tempOutputDir.getAbsolutePath(),
//                    "ACAD2018",
//                    "DWG",
//                    "0", // recurse = false
//                    "1"  // audit = true
//            );
//            pb.redirectErrorStream(true);
//            Process process = pb.start();
//
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    LOG.debug("[Teigha] {}", line);
//                }
//            }
//
//            int exitCode = process.waitFor();
//            if (exitCode != 0) {
//                throw new RuntimeException("TeighaFileConverter failed with exit code: " + exitCode);
//            }
//            LOG.info("✅ DXF → DWG conversion done.");
//
//            // --- Step 2: Locate generated DWG ---
//            File[] dwgFiles = tempOutputDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".dwg"));
//            if (dwgFiles == null || dwgFiles.length == 0) {
//                throw new RuntimeException("No DWG file generated by Teigha.");
//            }
//            File dwgFile = dwgFiles[0];
//
//            // --- Step 3: DWG → PDF (Aspose.CAD) ---
//            com.aspose.cad.Image dwgImage = com.aspose.cad.Image.load(dwgFile.getAbsolutePath());
//            com.aspose.cad.imageoptions.PdfOptions pdfOptions = new com.aspose.cad.imageoptions.PdfOptions();
//            dwgImage.save(finalOutputFile.getAbsolutePath(), pdfOptions);
//            LOG.info("✅ DWG → PDF conversion done: {}", finalOutputFile.getAbsolutePath());
//
//            // --- Step 4: Add timestamp (PDFBox) ---
//            try (PDDocument document = PDDocument.load(finalOutputFile);
//                 BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(finalOutputFile))) {
//
//                PDPage page = document.getPage(0);
//                float pageWidth = page.getMediaBox().getWidth();
//
//                try (PDPageContentStream contentStream = new PDPageContentStream(
//                        document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
//
//                    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
//                    contentStream.beginText();
//                    contentStream.newLineAtOffset(pageWidth - 200, 20);
//                    contentStream.showText(timestamp);
//                    contentStream.endText();
//                }
//
//                document.save(out);
//                LOG.info("✅ Timestamp added successfully.");
//            }
//
//            // --- Step 5: Store in filestore ---
//            FileStoreMapper fileStoreMapper = fileStoreService.store(
//                    finalOutputFile, finalOutputFile.getName(),
//                    "application/pdf", FILESTORE_MODULECODE);
//
//            edcrApplication.getEdcrApplicationDetails()
//                    .get(0).setScrutinizedDxfFileId(fileStoreMapper);
//
//            LOG.info("📁 PDF stored in filestore: {}",
//                    fileStoreMapper != null ? fileStoreMapper.getFileStoreId() : "null");
//
//        } catch (Exception e) {
//            LOG.error("🚨 Error in updateFile(): {}", e.getMessage(), e);
//        } finally {
//            // --- Cleanup temp folders ---
//            try {
//                Files.walk(tempInputDir.toPath())
//                        .sorted(Comparator.reverseOrder())
//                        .map(Path::toFile)
//                        .forEach(File::delete);
//                Files.walk(tempOutputDir.toPath())
//                        .sorted(Comparator.reverseOrder())
//                        .map(Path::toFile)
//                        .forEach(File::delete);
//            } catch (IOException ignored) {}
//
//            long elapsed = System.currentTimeMillis() - start;
//            LOG.info("⚡ updateFile() completed in {} ms → {}", elapsed, newFileName);
//        }
//    }








 // =======================================================
 // ✅ Utility: Safe delete with retry (shared by both methods)
 // =======================================================
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

 private File addSignatureImage(File inputPdf, String imagePath, String outputFileName) throws IOException {

	    LOG.info("✍️ Adding signature to PDF: {}", inputPdf.getName());

	    File outputFile = new File(outputFileName);

	    try (PDDocument document = PDDocument.load(inputPdf)) {

	        for (PDPage page : document.getPages()) {

	            float pageWidth = page.getMediaBox().getWidth();
	            float pageHeight = page.getMediaBox().getHeight();

	            // Load image
	            PDImageXObject image = PDImageXObject.createFromFile(imagePath, document);

	            // 🔥 Signature size (adjust if needed)
	            float imageWidth = 150f;
	            float imageHeight = 50f;

	            // 🔥 Bottom-right position
	            float x = pageWidth - imageWidth - 20; // right margin
	            float y = 20; // bottom margin

	            try (PDPageContentStream contentStream = new PDPageContentStream(
	                    document,
	                    page,
	                    PDPageContentStream.AppendMode.APPEND,
	                    true,
	                    true)) {

	                contentStream.drawImage(image, x, y, imageWidth, imageHeight);
	            }
	        }

	        document.save(outputFile);
	    }

	    LOG.info("✅ Signature added successfully: {}", outputFile.getAbsolutePath());
	    return outputFile;
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
    
//    public File replaceAsposeWatermark(File inputPdf, String outputPath, String newText) throws IOException {
//
//        LOG.info("========== REPLACING ASPOSE WATERMARK ==========");
//
//        PDDocument document = PDDocument.load(inputPdf);
//
//        /*
//         * PAGE 2
//         * index = 1
//         */
//        PDPage page = document.getPage(0);
//
//        PDRectangle mediaBox = page.getMediaBox();
//
//        float pageWidth = mediaBox.getWidth();
//        float pageHeight = mediaBox.getHeight();
//
//        LOG.info("PAGE WIDTH  : {}", pageWidth);
//        LOG.info("PAGE HEIGHT : {}", pageHeight);
//
//        /*
//         * WATERMARK POSITION (CAD coordinate space: 10173.09 x 14390.0)
//         *
//         * Aspose watermark is near the TOP of the page:
//         *   "Evaluation only. Created with Aspose.CAD. Copyright 2016-2025 Aspose Pty Ltd."
//         * Detected bbox: x0=299.94, y0=13648.71, x1=3287.66, y1=14230.49
//         * With 50-unit padding applied on all sides.
//         */
//        float x = 249.94f;
//
//        float y = 13598.71f;
//
//        float width = 3087.72f;
//
//        float height = 681.78f;
//
//        LOG.info("Covering watermark area...");
//        LOG.info("X      : {}", x);
//        LOG.info("Y      : {}", y);
//        LOG.info("WIDTH  : {}", width);
//        LOG.info("HEIGHT : {}", height);
//
//        PDPageContentStream contentStream =
//                new PDPageContentStream(document,page,PDPageContentStream.AppendMode.APPEND,true,true);
//
//        /*
//         * WHITE RECTANGLE — covers the Aspose watermark
//         */
//        contentStream.setNonStrokingColor(255, 255, 255);
//
//        contentStream.addRect(x,y,width,height);
//
//        contentStream.fill();
//
//        LOG.info("Watermark hidden successfully");
//
//        /*
//         * WRITE NEW TEXT at same position
//         */
//        contentStream.beginText();
//
//        contentStream.setNonStrokingColor(0, 0, 0);
//
//        contentStream.setFont(PDType1Font.HELVETICA_BOLD,80);
//
//        contentStream.newLineAtOffset(x + 20, y + height / 2 - 40);
//
//        contentStream.showText(newText);
//
//        contentStream.endText();
//
//        contentStream.close();
//
//        File outputFile = new File(outputPath);
//
//        document.save(outputFile);
//
//        document.close();
//
//        LOG.info("Updated PDF saved at : {}", outputFile.getAbsolutePath());
//
//        LOG.info("========== WATERMARK REPLACED ==========");
//
//        return outputFile;
//    }

    public File replaceAsposeWatermark(
            File inputPdf,
            String outputPath,
            String newText) throws IOException {

        LOG.info("========== REPLACING ASPOSE WATERMARK ==========");

        PDDocument document = PDDocument.load(inputPdf);
        int totalPages = document.getNumberOfPages();
        LOG.info("Total pages in PDF: {}", totalPages);

        // -------------------------------------------------------
        // DYNAMIC DETECTION using core PDFTextStripper only.
        // Scans every page for Aspose watermark text, then covers
        // it using proportional coordinates derived from page size.
        //
        // Observed across all PDF sizes generated by Aspose.CAD:
        //   - Always top-left corner
        //   - Height  ≈ 4.0% of page height
        //   - Y gap from top ≈ 1.1% of page height
        //   - X starts near 0 (left edge)
        //   - Width varies by DXF, so we use a safe 35% of page width
        // -------------------------------------------------------
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
    
    private File scalePdfToMatch(File inputPdf, PDRectangle targetSize) throws IOException {

        File outputFile = File.createTempFile("scaled_", ".pdf");

        try (PDDocument inputDoc = PDDocument.load(inputPdf);
             PDDocument outputDoc = new PDDocument()) {

            LayerUtility layerUtility = new LayerUtility(outputDoc);

            for (int i = 0; i < inputDoc.getNumberOfPages(); i++) {

                PDPage srcPage = inputDoc.getPage(i);

                //Create ONLY ONE new page per input page
                PDPage newPage = new PDPage(targetSize);
                outputDoc.addPage(newPage);

                //Import correct page by index (NOT indexOf)
                PDFormXObject pageForm = layerUtility.importPageAsForm(inputDoc, i);

                float srcW = srcPage.getMediaBox().getWidth();
                float srcH = srcPage.getMediaBox().getHeight();

                float scaleX = targetSize.getWidth() / srcW;
                float scaleY = targetSize.getHeight() / srcH;

                float scale = Math.min(scaleX, scaleY) * 0.8f; // 90%

                float offsetX = (targetSize.getWidth() - srcW * scale) / 2;
                float offsetY = (targetSize.getHeight() - srcH * scale) / 2;

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



    
}
