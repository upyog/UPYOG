package org.upyog.Automation.Controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.upyog.Automation.Utils.AutomationConstants;
import org.upyog.Automation.model.ReportDto;

import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.upyog.Automation.Reports.UserManualGenerator;
import org.upyog.Automation.Utils.ScreenRecorder;

import org.upyog.Automation.Utils.ExcelDataReader;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.util.Arrays;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    private static final Logger logger =
            LoggerFactory.getLogger(ReportController.class);

    private File getLatestReport() {

        File reportDir =
                new File(AutomationConstants.REPORTS_DIR);

        File[] files =
                reportDir.listFiles(
                        (dir, name) ->
                                name.endsWith(".html")
                );

        if (files == null || files.length == 0) {
            return null;
        }

        Arrays.sort(
                files,
                Comparator.comparingLong(File::lastModified)
                        .reversed()
        );

        return files[0];
    }

    @GetMapping("/view")
    public ResponseEntity<Resource> viewReport() {

        File latestReport = getLatestReport();

        if (latestReport == null) {
            return ResponseEntity.notFound().build();
        }

        Resource resource =
                new FileSystemResource(latestReport);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadReport() {

        File latestReport = getLatestReport();

        if (latestReport == null) {
            return ResponseEntity.notFound().build();
        }

        Resource resource =
                new FileSystemResource(latestReport);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename="
                                + latestReport.getName()
                )
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
    @GetMapping("/list")
    public ResponseEntity<String[]> getReports() {

        File reportDir =
                new File(AutomationConstants.REPORTS_DIR);

        File[] files =
                reportDir.listFiles(
                        (dir, name) ->
                                name.endsWith(".html")
                );

        if (files == null) {
            return ResponseEntity.ok(new String[0]);
        }
        logger.info("Total reports found : {}", files.length);
        Arrays.sort(
                files,
                Comparator.comparingLong(File::lastModified)
                        .reversed()
        );


        String[] reportNames =
                Arrays.stream(files)
                        .map(File::getName)
                        .toArray(String[]::new);

        return ResponseEntity.ok(reportNames);
    }

    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewReport(
            @PathVariable String fileName
    ) {

        try {

            if (fileName.contains("..")
                    || fileName.contains("/")
                    || fileName.contains("\\")) {

                return ResponseEntity
                        .badRequest()
                        .build();
            }

            File reportDir =
                    new File(AutomationConstants.REPORTS_DIR);

            File report =
                    new File(reportDir, fileName);

            String basePath =
                    reportDir.getCanonicalPath();

            String targetPath =
                    report.getCanonicalPath();

            if (!targetPath.startsWith(basePath)) {

                return ResponseEntity
                        .badRequest()
                        .build();
            }

            if (!report.exists()) {

                return ResponseEntity
                        .notFound()
                        .build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(new FileSystemResource(report));

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .build();
        }
    }

    @GetMapping("/module/{moduleName}")
    public List<ReportDto> getReportsByModule(@PathVariable String moduleName) {

        File reportDir = new File(AutomationConstants.REPORTS_DIR);
        logger.info("GET REPORTS API CALLED FOR MODULE : {}", moduleName);

        if (!reportDir.exists()) {
            return new ArrayList<>();
        }

        File[] files = reportDir.listFiles((dir, name) ->
                name.startsWith(moduleName.toUpperCase() + "_")
                        && name.endsWith(".html"));

        if (files == null) {
            return new ArrayList<>();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");

        return Arrays.stream(files)
                .sorted(Comparator.comparingLong(File::lastModified).reversed())
                .limit(5)
                .map(file -> {

                    Date modified = new Date(file.lastModified());

                    ReportDto dto = new ReportDto();

                    logger.info("Processing report : {}", file.getName());

                    try {

                        String html = Files.readString(file.toPath());

                        logger.info("HTML LENGTH = {}", html.length());
                        logger.info("HAS fail-bg = {}", html.contains("fail-bg"));
                        logger.info("HAS Fail = {}", html.contains("Fail"));
                        logger.info("HAS FAIL = {}", html.contains("FAIL"));
                        logger.info("FILE = {}", file.getAbsolutePath());

                        if (html.contains("fail-bg")) {
                            dto.setStatus(AutomationConstants.STATUS_FAIL);
                        } else {
                            dto.setStatus(AutomationConstants.STATUS_PASS);
                        }

                        logger.info("FINAL STATUS = {}", dto.getStatus());

                    } catch (IOException e) {
                        dto.setStatus("UNKNOWN");
                    }

                    logger.info("Final status : {}", dto.getStatus());

                    dto.setFileName(file.getName());
                    dto.setDate(dateFormat.format(modified));
                    dto.setTime(timeFormat.format(modified));

                    return dto;

                })
                .collect(Collectors.toList());
    }

    // =========================================================================
    // User Manual & Screenshot Download Endpoints
    // =========================================================================

    @GetMapping("/manual/{moduleName}")
    public ResponseEntity<Resource> viewUserManual(@PathVariable String moduleName,
                                                    @RequestParam(required = false) String testCase) {
        try {
            File manualFile = org.upyog.Automation.Reports.UserManualGenerator.generateHtmlManual(moduleName, testCase);
            if (!manualFile.exists()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(new FileSystemResource(manualFile));
        } catch (Exception e) {
            logger.error("Error generating user manual for module {} (testCase: {}): {}", moduleName, testCase, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/manual/download/{moduleName}")
    public ResponseEntity<byte[]> downloadUserManualPackage(@PathVariable String moduleName,
                                                            @RequestParam(required = false) String testCase) {
        try {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            org.upyog.Automation.Reports.UserManualGenerator.createManualZipPackage(moduleName, testCase, baos);
            byte[] zipBytes = baos.toByteArray();

            String suffix = (testCase != null && !testCase.isBlank()) ? "_" + testCase.toUpperCase() : "";
            String filename = moduleName.toUpperCase() + suffix + "_User_Manual.zip";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(zipBytes);
        } catch (Exception e) {
            logger.error("Error creating user manual package for module {}: {}", moduleName, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/manual/html/download/{moduleName}")
    public ResponseEntity<Resource> downloadUserManualHtml(@PathVariable String moduleName,
                                                            @RequestParam(required = false) String testCase) {
        try {
            File manualFile = org.upyog.Automation.Reports.UserManualGenerator.generateHtmlManual(moduleName, testCase);
            if (!manualFile.exists()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + manualFile.getName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new FileSystemResource(manualFile));
        } catch (Exception e) {
            logger.error("Error downloading user manual HTML for module {}: {}", moduleName, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/screenshots/download/{moduleName}")
    public ResponseEntity<byte[]> downloadModuleScreenshots(@PathVariable String moduleName,
                                                             @RequestParam(required = false) String testCase) {
        try {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            org.upyog.Automation.Reports.UserManualGenerator.createScreenshotsZip(moduleName, testCase, baos);
            byte[] zipBytes = baos.toByteArray();

            String suffix = (testCase != null && !testCase.isBlank()) ? "_" + testCase.toUpperCase() : "";
            String filename = moduleName.toUpperCase() + suffix + "_Screenshots.zip";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(zipBytes);
        } catch (Exception e) {
            logger.error("Error creating screenshots zip for module {}: {}", moduleName, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/screenshots/download")
    public ResponseEntity<byte[]> downloadAllScreenshots() {
        try {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            org.upyog.Automation.Reports.UserManualGenerator.createScreenshotsZip("ALL", baos);
            byte[] zipBytes = baos.toByteArray();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"All_Screenshots.zip\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(zipBytes);
        } catch (Exception e) {
            logger.error("Error creating all screenshots zip: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/manual/list")
    public ResponseEntity<List<java.util.Map<String, Object>>> listAvailableManuals() {
        try {
            File dir = new File(AutomationConstants.SCREENSHOTS_DIR);
            if (!dir.exists() || !dir.isDirectory()) {
                return ResponseEntity.ok(java.util.Collections.emptyList());
            }

            File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".png"));
            if (files == null || files.length == 0) {
                return ResponseEntity.ok(java.util.Collections.emptyList());
            }

            java.util.Map<String, List<File>> grouped = new java.util.HashMap<>();
            for (File file : files) {
                String name = file.getName();
                String module = name.contains("_") ? name.substring(0, name.indexOf('_')) : "GENERAL";
                grouped.computeIfAbsent(module, k -> new java.util.ArrayList<>()).add(file);
            }

            List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (java.util.Map.Entry<String, List<File>> entry : grouped.entrySet()) {
                java.util.Map<String, Object> map = new java.util.HashMap<>();
                map.put("module", entry.getKey());
                map.put("totalScreenshots", entry.getValue().size());
                long latest = entry.getValue().stream().mapToLong(File::lastModified).max().orElse(0L);
                map.put("lastUpdated", latest > 0 ? sdf.format(new Date(latest)) : "N/A");
                map.put("viewUrl", "/api/report/manual/" + entry.getKey());
                map.put("downloadPackageUrl", "/api/report/manual/download/" + entry.getKey());
                map.put("downloadImagesUrl", "/api/report/screenshots/download/" + entry.getKey());
                result.add(map);
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error listing manuals: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // =========================================================================
    // Screen Recording (Video) Endpoints
    // =========================================================================

    private ResponseEntity<ResourceRegion> streamVideoRegion(File file, HttpHeaders headers) {
        if (file == null || !file.exists()) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new FileSystemResource(file);
        long contentLength = file.length();
        List<HttpRange> ranges = headers != null ? headers.getRange() : java.util.Collections.emptyList();

        if (ranges.isEmpty()) {
            ResourceRegion region = new ResourceRegion(resource, 0, contentLength);
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.parseMediaType("video/mp4"))
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                    .body(region);
        } else {
            HttpRange range = ranges.get(0);
            long start = range.getRangeStart(contentLength);
            long end = range.getRangeEnd(contentLength);
            long rangeLength = Math.min(1024 * 1024 * 2L, end - start + 1);
            ResourceRegion region = new ResourceRegion(resource, start, rangeLength);
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .contentType(MediaType.parseMediaType("video/mp4"))
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                    .body(region);
        }
    }

    @GetMapping(value = {"/video/view/{fileName:.+}", "/video/stream/{fileName:.+}"})
    public ResponseEntity<ResourceRegion> streamRecordingFile(
            @PathVariable String fileName,
            @RequestHeader(required = false) HttpHeaders headers) {
        File file = ScreenRecorder.getRecordingFile(fileName);
        return streamVideoRegion(file, headers);
    }

    @GetMapping(value = {"/video/download/{fileName:.+}"})
    public ResponseEntity<Resource> downloadRecordingFile(@PathVariable String fileName) {
        File file = ScreenRecorder.getRecordingFile(fileName);
        if (file == null || !file.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(new FileSystemResource(file));
    }

    @GetMapping("/video/latest")
    public ResponseEntity<ResourceRegion> streamLatestRecording(@RequestHeader(required = false) HttpHeaders headers) {
        File file = ScreenRecorder.getLatestRecording("ALL");
        return streamVideoRegion(file, headers);
    }

    @GetMapping("/video/download/latest")
    public ResponseEntity<Resource> downloadLatestRecording() {
        File file = ScreenRecorder.getLatestRecording("ALL");
        if (file == null || !file.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(new FileSystemResource(file));
    }

    @GetMapping(value = {"/video/module/{moduleName:.+}"})
    public ResponseEntity<ResourceRegion> streamModuleRecording(
            @PathVariable String moduleName,
            @RequestHeader(required = false) HttpHeaders headers) {
        File file = ScreenRecorder.getLatestRecording(moduleName);
        return streamVideoRegion(file, headers);
    }

    @GetMapping(value = {"/video/download/module/{moduleName:.+}"})
    public ResponseEntity<Resource> downloadModuleRecording(@PathVariable String moduleName) {
        File file = ScreenRecorder.getLatestRecording(moduleName);
        if (file == null || !file.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(new FileSystemResource(file));
    }

    @GetMapping("/video/download-all")
    public ResponseEntity<byte[]> downloadAllRecordingsZip(@RequestParam(required = false) String module) {
        try {
            String targetModule = (module != null && !module.isBlank() && !"ALL".equalsIgnoreCase(module)) ? module : "ALL";
            List<File> files = ScreenRecorder.getRecordingsForModule(targetModule);

            if (files.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            ScreenRecorder.bundleRecordingsZip(files, baos);
            byte[] zipBytes = baos.toByteArray();

            String zipName = "Recordings_" + targetModule.replace(" ", "_") + ".zip";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(zipBytes);
        } catch (Exception e) {
            logger.error("Error creating recordings zip bundle: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/video/list")
    public ResponseEntity<List<java.util.Map<String, Object>>> listRecordings(@RequestParam(required = false) String module) {
        String targetModule = (module != null && !module.isBlank()) ? module : "ALL";
        return ResponseEntity.ok(ScreenRecorder.listRecordings(targetModule));
    }

    @RequestMapping(value = "/manual/delete-image", method = {RequestMethod.DELETE, RequestMethod.POST})
    public ResponseEntity<java.util.Map<String, Object>> deleteManualImage(
            @RequestParam String fileName,
            @RequestParam(required = false) String module) {

        java.util.Map<String, Object> resp = new java.util.HashMap<>();
        if (fileName == null || fileName.isBlank() || fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            resp.put("success", false);
            resp.put("message", "Invalid file name");
            return ResponseEntity.badRequest().body(resp);
        }

        File dir = new File(AutomationConstants.SCREENSHOTS_DIR);
        File targetFile = new File(dir, fileName);
        boolean deleted = false;
        if (targetFile.exists() && targetFile.isFile()) {
            deleted = targetFile.delete();
        }

        // Also check if screenshot was inside a subfolder
        if (!deleted && dir.exists()) {
            File[] subs = dir.listFiles(File::isDirectory);
            if (subs != null) {
                for (File sub : subs) {
                    File f = new File(sub, fileName);
                    if (f.exists() && f.isFile()) {
                        deleted = f.delete();
                        break;
                    }
                }
            }
        }

        // Re-generate the User Manual HTML so deleted image is permanently excluded
        if (deleted && module != null && !module.isBlank()) {
            try {
                UserManualGenerator.generateHtmlManual(module);
            } catch (Exception e) {
                logger.warn("Could not regenerate manual after image delete: {}", e.getMessage());
            }
        }

        resp.put("success", deleted);
        resp.put("message", deleted ? "Screenshot deleted successfully" : "Screenshot not found or could not be removed");
        return ResponseEntity.ok(resp);
    }

    /**
     * Cleans up all session artifacts (recordings, screenshots, reports, manuals, uploaded excel).
     * Called on page refresh, page reload, or when explicitly resetting.
     */
    @RequestMapping(value = "/cleanup-all", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Map<String, Object>> cleanupAllSessionFiles() {
        Map<String, Object> resp = new HashMap<>();
        try {
            logger.info("Cleaning up all session test artifacts...");
            cleanupDirectory(new File(AutomationConstants.RECORDINGS_DIR));
            cleanupDirectory(new File(AutomationConstants.SCREENSHOTS_DIR));
            cleanupDirectory(new File(AutomationConstants.REPORTS_DIR));
            cleanupDirectory(new File(AutomationConstants.MANUALS_DIR));
            ExcelDataReader.clearUploadedExcelFile();

            resp.put("success", true);
            resp.put("message", "All temporary test data, screenshots, and screen recordings deleted successfully.");
            logger.info("Session artifacts cleaned up successfully.");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            logger.error("Failed to clean up session test artifacts: {}", e.getMessage(), e);
            resp.put("success", false);
            resp.put("message", "Cleanup failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(resp);
        }
    }

    private static void cleanupDirectory(File dir) {
        if (dir == null || !dir.exists()) return;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    cleanupDirectory(file);
                }
                try {
                    file.delete();
                } catch (Exception ignored) {}
            }
        }
    }
}