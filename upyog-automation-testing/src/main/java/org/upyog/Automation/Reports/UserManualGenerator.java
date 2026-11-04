package org.upyog.Automation.Reports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.upyog.Automation.Utils.AutomationConstants;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Generator for interactive, printable, and downloadable step-by-step User Manuals
 * constructed from captured automation screenshots.
 */
public class UserManualGenerator {

    private static final Logger logger = LoggerFactory.getLogger(UserManualGenerator.class);

    /**
     * Data structure representing a single step in the User Manual.
     */
    public static class ManualStep {
        private final int stepNumber;
        private final String moduleName;
        private final String testCase;
        private final String screenName;
        private final String formattedScreenName;
        private final String timestamp;
        private final boolean isFailure;
        private final File imageFile;
        private final String base64Data;

        public ManualStep(int stepNumber, String moduleName, String testCase, String screenName,
                          String formattedScreenName, String timestamp, boolean isFailure,
                          File imageFile, String base64Data) {
            this.stepNumber = stepNumber;
            this.moduleName = moduleName;
            this.testCase = testCase;
            this.screenName = screenName;
            this.formattedScreenName = formattedScreenName;
            this.timestamp = timestamp;
            this.isFailure = isFailure;
            this.imageFile = imageFile;
            this.base64Data = base64Data;
        }

        public int getStepNumber() { return stepNumber; }
        public String getModuleName() { return moduleName; }
        public String getTestCase() { return testCase; }
        public String getScreenName() { return screenName; }
        public String getFormattedScreenName() { return formattedScreenName; }
        public String getTimestamp() { return timestamp; }
        public boolean isFailure() { return isFailure; }
        public File getImageFile() { return imageFile; }
        public String getBase64Data() { return base64Data; }
    }

    /**
     * Retrieves all screenshot files matching a given module name and optional test case / workflow.
     *
     * @param moduleName the target module name or "ALL"
     * @param testCaseFilter optional test case or workflow filter (null/blank for all)
     * @return sorted list of screenshot files
     */
    public static List<File> getScreenshotsForModule(String moduleName, String testCaseFilter) {
        File dir = new File(AutomationConstants.SCREENSHOTS_DIR);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
            return Collections.emptyList();
        }

        File[] allFiles = dir.listFiles((d, name) -> {
            String lower = name.toLowerCase();
            return lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg");
        });

        if (allFiles == null || allFiles.length == 0) {
            return Collections.emptyList();
        }

        List<File> matched = new ArrayList<>();
        String modKey = (moduleName != null && !moduleName.trim().isEmpty() && !"ALL".equalsIgnoreCase(moduleName))
                ? moduleName.trim().toUpperCase() : null;
        String tcKey = (testCaseFilter != null && !testCaseFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(testCaseFilter))
                ? testCaseFilter.trim().toUpperCase() : null;

        for (File f : allFiles) {
            String uname = f.getName().toUpperCase();
            boolean modMatches = true;
            if (modKey != null) {
                String shortPrefix = modKey.contains("_") ? modKey.substring(0, modKey.indexOf('_')) : modKey;
                modMatches = uname.startsWith(modKey + "__") || uname.startsWith(modKey + "_")
                        || uname.contains(modKey) || uname.startsWith(shortPrefix + "_") || uname.startsWith(shortPrefix + "__");
            }

            boolean tcMatches = true;
            if (tcKey != null) {
                tcMatches = uname.contains("__" + tcKey + "__") || uname.contains("_" + tcKey + "_") || uname.contains(tcKey);
            }

            if (modMatches && tcMatches) {
                matched.add(f);
            }
        }

        if (matched.isEmpty() && modKey == null && tcKey == null) {
            matched.addAll(Arrays.asList(allFiles));
        }

        matched.sort(Comparator.comparingLong(File::lastModified));
        return matched;
    }

    public static List<File> getScreenshotsForModule(String moduleName) {
        return getScreenshotsForModule(moduleName, null);
    }

    /**
     * Parses screenshot files into structured manual steps with intelligent module/workflow naming.
     *
     * @param files list of screenshot files
     * @param fallbackModule default module name to use if not resolvable from file name
     * @return list of parsed {@link ManualStep} objects
     */
    public static List<ManualStep> parseManualSteps(List<File> files, String fallbackModule) {
        List<ManualStep> steps = new ArrayList<>();
        int count = 1;

        for (File file : files) {
            String name = file.getName();
            int dotIdx = name.lastIndexOf('.');
            String base = (dotIdx > 0) ? name.substring(0, dotIdx) : name;
            boolean isFailure = base.endsWith("_FAIL") || base.contains("FAIL") || base.contains("__FAIL");

            String module = "";
            String testCase = "";
            String screenName = "";

            if (base.contains("__")) {
                // Structured format: [MODULE]__[TESTCASE]__[SCREEN]__[TIMESTAMP].png
                String[] parts = base.split("__");
                module = (parts.length > 0 && !parts[0].isBlank()) ? parts[0] : "";
                testCase = (parts.length > 1 && !parts[1].isBlank()) ? parts[1] : "";
                screenName = (parts.length > 2 && !parts[2].isBlank()) ? parts[2] : "Screen Step";
            } else {
                // Legacy format: [MODULE]_[TESTCASE]_[SCREEN]_[TIMESTAMP].png
                String[] parts = base.split("_");
                List<String> screenTokens = new ArrayList<>();

                for (int i = 0; i < parts.length; i++) {
                    String part = parts[i];
                    if (part.matches("\\d{8}") || part.matches("\\d{6}") || part.matches("\\d{3}") || part.equalsIgnoreCase("FAIL")) {
                        continue;
                    }
                    if (part.equalsIgnoreCase("TC") && i + 1 < parts.length && parts[i + 1].matches("\\d+")) {
                        testCase = "TC_" + parts[i + 1];
                        i++;
                        continue;
                    }
                    if (part.toUpperCase().startsWith("TC") && part.matches("(?i)TC_?\\d+")) {
                        testCase = part.toUpperCase();
                        continue;
                    }
                    if (module.isEmpty()) {
                        if (part.equalsIgnoreCase("citizen") || part.equalsIgnoreCase("employee") || part.equalsIgnoreCase("vendor")) {
                            testCase = humanize(part) + " Flow";
                        } else {
                            module = part;
                        }
                    } else if (testCase.isEmpty() && (part.equalsIgnoreCase("citizen") || part.equalsIgnoreCase("employee") || part.equalsIgnoreCase("vendor") || part.equalsIgnoreCase("flow"))) {
                        testCase = humanize(part) + " Flow";
                    } else if (!part.equalsIgnoreCase("module")) {
                        screenTokens.add(part);
                    }
                }
                screenName = screenTokens.isEmpty() ? base : String.join(" ", screenTokens);
            }

            // Clean module name: ensure "TC", "TEST", "MODULE" are NEVER used as module names
            if (module.isEmpty() || module.equalsIgnoreCase("TC") || module.equalsIgnoreCase("MODULE")
                    || module.equalsIgnoreCase("TEST") || module.equalsIgnoreCase("UNKNOWN") || module.matches("(?i)TC_?\\d+")) {
                if (testCase.isEmpty() && module.matches("(?i)TC_?\\d+")) {
                    testCase = module.toUpperCase();
                }
                module = (fallbackModule != null && !fallbackModule.isBlank() && !"ALL".equalsIgnoreCase(fallbackModule))
                        ? fallbackModule : "UPYOG Application";
            }

            if (testCase.isEmpty() || testCase.equalsIgnoreCase("TEST")) {
                testCase = "Standard Workflow";
            }

            String formattedModule = humanize(module.replace("_", " "));
            String formattedTestCase = testCase.replace("_", " ");
            String formattedTitle = humanize(screenName.replace("_", " "));
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(file.lastModified()));

            String base64 = "";
            try {
                byte[] bytes = Files.readAllBytes(file.toPath());
                base64 = Base64.getEncoder().encodeToString(bytes);
            } catch (Exception e) {
                logger.warn("Unable to encode image {}: {}", file.getName(), e.getMessage());
            }

            steps.add(new ManualStep(count++, formattedModule, formattedTestCase, screenName, formattedTitle, timestamp, isFailure, file, base64));
        }

        return steps;
    }

    public static List<ManualStep> parseManualSteps(List<File> files) {
        return parseManualSteps(files, null);
    }

    /**
     * Generates a standalone, beautiful HTML User Manual with embedded Base64 images and workflow filtering.
     *
     * @param moduleName target module name (e.g. "PET", "PROPERTY_TAX", or "ALL")
     * @param testCaseFilter optional test case or workflow filter
     * @return the generated HTML File
     * @throws IOException if generation fails
     */
    public static File generateHtmlManual(String moduleName, String testCaseFilter) throws IOException {
        List<File> screenshotFiles = getScreenshotsForModule(moduleName, testCaseFilter);
        List<ManualStep> steps = parseManualSteps(screenshotFiles, moduleName);

        String displayModule = (moduleName == null || moduleName.trim().isEmpty() || "ALL".equalsIgnoreCase(moduleName))
                ? "UPYOG Application"
                : humanize(moduleName.replace("_", " "));

        Path manualsDir = Paths.get(AutomationConstants.MANUALS_DIR);
        Files.createDirectories(manualsDir);

        String safeName = (moduleName == null || moduleName.trim().isEmpty()) ? "ALL" : moduleName.replaceAll("[^a-zA-Z0-9_-]", "_");
        if (testCaseFilter != null && !testCaseFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(testCaseFilter)) {
            safeName += "_" + testCaseFilter.replaceAll("[^a-zA-Z0-9_-]", "_");
        }
        File outputFile = manualsDir.resolve("User_Manual_" + safeName + ".html").toFile();

        // Extract unique workflows for interactive filter tabs
        Set<String> uniqueWorkflows = new LinkedHashSet<>();
        for (ManualStep s : steps) {
            if (s.getTestCase() != null && !s.getTestCase().isBlank()) {
                uniqueWorkflows.add(s.getTestCase());
            }
        }

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n")
                .append("<html lang=\"en\">\n")
                .append("<head>\n")
                .append("  <meta charset=\"UTF-8\">\n")
                .append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n")
                .append("  <title>User Manual - ").append(escapeHtml(displayModule)).append("</title>\n")
                .append("  <link rel=\"preconnect\" href=\"https://fonts.googleapis.com\">\n")
                .append("  <link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin>\n")
                .append("  <link href=\"https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap\" rel=\"stylesheet\">\n")
                .append("  <style>\n")
                .append(getManualCss())
                .append("  </style>\n")
                .append("</head>\n")
                .append("<body>\n")
                .append("  <div class=\"top-bar no-print\">\n")
                .append("    <div class=\"brand-title\"><span>UPYOG</span> Automation User Manual</div>\n")
                .append("    <div class=\"actions-group\">\n")
                .append("      <button class=\"btn btn-primary\" onclick=\"window.print()\"><svg width=\"14\" height=\"14\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\" style=\"vertical-align:middle;margin-right:4px;\"><polyline points=\"6 9 6 2 18 2 18 9\"></polyline><path d=\"M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2\"></path><rect x=\"6\" y=\"14\" width=\"12\" height=\"8\"></rect></svg>Print / Save as PDF</button>\n")
                .append("      <a href=\"/api/report/manual/download/").append(escapeHtml(safeName)).append("\" class=\"btn btn-secondary\"><svg width=\"14\" height=\"14\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\" style=\"vertical-align:middle;margin-right:4px;\"><line x1=\"16.5\" y1=\"9.4\" x2=\"7.5\" y2=\"4.21\"></line><path d=\"M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z\"></path><polyline points=\"3.27 6.96 12 12.01 20.73 6.96\"></polyline><line x1=\"12\" y1=\"22.08\" x2=\"12\" y2=\"12\"></line></svg>Download Manual Package (ZIP)</a>\n")
                .append("      <a href=\"/api/report/screenshots/download/").append(escapeHtml(safeName)).append("\" class=\"btn btn-secondary\"><svg width=\"14\" height=\"14\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\" style=\"vertical-align:middle;margin-right:4px;\"><rect x=\"3\" y=\"3\" width=\"18\" height=\"18\" rx=\"2\" ry=\"2\"></rect><circle cx=\"8.5\" cy=\"8.5\" r=\"1.5\"></circle><polyline points=\"21 15 16 10 5 21\"></polyline></svg>Download Images (ZIP)</a>\n")
                .append("    </div>\n")
                .append("  </div>\n\n")
                .append("  <div class=\"container\">\n")
                .append("    <header class=\"manual-header\">\n")
                .append("      <div class=\"header-badge\">Step-by-Step Workflow Guide</div>\n")
                .append("      <h1 class=\"module-title\">").append(escapeHtml(displayModule)).append("</h1>\n")
                .append("      <p class=\"header-subtitle\">Visual execution manual generated from automated test walkthroughs.</p>\n")
                .append("      <div class=\"meta-grid\">\n")
                .append("        <div class=\"meta-card\"><span class=\"meta-label\">Total Steps</span><span class=\"meta-val\" id=\"total-steps-val\">").append(steps.size()).append("</span></div>\n")
                .append("        <div class=\"meta-card\"><span class=\"meta-label\">Workflows</span><span class=\"meta-val\">").append(uniqueWorkflows.size()).append("</span></div>\n")
                .append("        <div class=\"meta-card\"><span class=\"meta-label\">Generated On</span><span class=\"meta-val\">").append(new SimpleDateFormat("dd MMM yyyy, hh:mm a").format(new Date())).append("</span></div>\n")
                .append("        <div class=\"meta-card\"><span class=\"meta-label\">Status</span><span class=\"meta-val text-success\">Ready</span></div>\n")
                .append("      </div>\n");

        if (uniqueWorkflows.size() > 1) {
            html.append("      <div class=\"workflow-filter-bar no-print\">\n")
                    .append("        <span class=\"wf-filter-title\">Filter by Workflow:</span>\n")
                    .append("        <div class=\"wf-chips-wrapper\">\n")
                    .append("          <button type=\"button\" class=\"wf-chip active\" onclick=\"filterWorkflow('ALL', this)\">All Workflows (").append(steps.size()).append(")</button>\n");
            for (String wf : uniqueWorkflows) {
                long wfCount = steps.stream().filter(s -> wf.equalsIgnoreCase(s.getTestCase())).count();
                html.append("          <button type=\"button\" class=\"wf-chip\" onclick=\"filterWorkflow('").append(escapeHtml(wf)).append("', this)\">").append(escapeHtml(wf)).append(" (").append(wfCount).append(")</button>\n");
            }
            html.append("        </div>\n")
                    .append("      </div>\n");
        }

        html.append("    </header>\n\n");

        if (steps.isEmpty()) {
            html.append("    <div class=\"empty-state\">\n")
                    .append("      <div class=\"empty-icon\"><svg width=\"48\" height=\"48\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"#94a3b8\" stroke-width=\"1.5\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><rect x=\"3\" y=\"3\" width=\"18\" height=\"18\" rx=\"2\" ry=\"2\"></rect><circle cx=\"8.5\" cy=\"8.5\" r=\"1.5\"></circle><polyline points=\"21 15 16 10 5 21\"></polyline></svg></div>\n")
                    .append("      <h3>No Screenshots Found</h3>\n")
                    .append("      <p>No captured step screenshots were found for module: <strong>").append(escapeHtml(displayModule)).append("</strong>.<br>Run an automated test to capture step-by-step screen images.</p>\n")
                    .append("    </div>\n");
        } else {
            html.append("    <div class=\"steps-timeline\" id=\"steps-container\">\n");
            for (ManualStep step : steps) {
                String imgFileName = (step.getImageFile() != null) ? step.getImageFile().getName() : "";
                html.append("      <div class=\"step-card\" id=\"step-").append(step.getStepNumber()).append("\" data-workflow=\"").append(escapeHtml(step.getTestCase())).append("\">\n")
                        .append("        <div class=\"step-card-header\">\n")
                        .append("          <div class=\"step-badge-wrap\">\n")
                        .append("            <span class=\"step-number\">Step ").append(step.getStepNumber()).append("</span>\n")
                        .append("            <h2 class=\"step-title\">").append(escapeHtml(step.getFormattedScreenName())).append("</h2>\n")
                        .append("          </div>\n")
                        .append("          <div class=\"step-tags\">\n")
                        .append("            <span class=\"tag tag-module\">").append(escapeHtml(step.getModuleName())).append("</span>\n")
                        .append("            <span class=\"tag tag-workflow\">").append(escapeHtml(step.getTestCase())).append("</span>\n");
                if (step.isFailure()) {
                    html.append("            <span class=\"tag tag-fail\"><svg width=\"12\" height=\"12\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\" style=\"vertical-align:middle;margin-right:3px;\"><path d=\"M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z\"></path><line x1=\"12\" y1=\"9\" x2=\"12\" y2=\"13\"></line><line x1=\"12\" y1=\"17\" x2=\"12.01\" y2=\"17\"></line></svg>Failed Step</span>\n");
                } else {
                    html.append("            <span class=\"tag tag-pass\"><svg width=\"12\" height=\"12\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\" style=\"vertical-align:middle;margin-right:3px;\"><polyline points=\"20 6 9 17 4 12\"></polyline></svg>Completed</span>\n");
                }
                html.append("            <span class=\"tag tag-time\"><svg width=\"12\" height=\"12\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\" style=\"vertical-align:middle;margin-right:3px;\"><circle cx=\"12\" cy=\"12\" r=\"10\"></circle><polyline points=\"12 6 12 12 16 14\"></polyline></svg>").append(step.getTimestamp()).append("</span>\n")
                        .append("            <button type=\"button\" class=\"btn-remove-step no-print\" title=\"Remove repeated or unnecessary image\" onclick=\"deleteStepCard('step-").append(step.getStepNumber()).append("', '").append(escapeHtml(imgFileName)).append("')\"><svg width=\"13\" height=\"13\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\" style=\"vertical-align:middle;margin-right:3px;\"><polyline points=\"3 6 5 6 21 6\"></polyline><path d=\"M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2\"></path><line x1=\"10\" y1=\"11\" x2=\"10\" y2=\"17\"></line><line x1=\"14\" y1=\"11\" x2=\"14\" y2=\"17\"></line></svg>Remove Image</button>\n")
                        .append("          </div>\n")
                        .append("        </div>\n")
                        .append("        <div class=\"step-card-body\">\n")
                        .append("          <p class=\"step-instruction\">Screen capture after completing inputs for <strong>").append(escapeHtml(step.getFormattedScreenName())).append("</strong> in workflow <code>").append(escapeHtml(step.getTestCase())).append("</code>.</p>\n")
                        .append("          <div class=\"image-container\" onclick=\"openLightbox('img-").append(step.getStepNumber()).append("')\">\n");

                if (!step.getBase64Data().isEmpty()) {
                    html.append("            <img id=\"img-").append(step.getStepNumber()).append("\" src=\"data:image/png;base64,").append(step.getBase64Data()).append("\" alt=\"").append(escapeHtml(step.getFormattedScreenName())).append("\" class=\"manual-img\" loading=\"lazy\"/>\n");
                } else {
                    html.append("            <div class=\"image-missing\">Image unavailable</div>\n");
                }

                html.append("            <div class=\"image-overlay\"><span class=\"zoom-btn\"><svg width=\"13\" height=\"13\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\" style=\"vertical-align:middle;margin-right:3px;\"><circle cx=\"11\" cy=\"11\" r=\"8\"></circle><line x1=\"21\" y1=\"21\" x2=\"16.65\" y2=\"16.65\"></line><line x1=\"11\" y1=\"8\" x2=\"11\" y2=\"14\"></line><line x1=\"8\" y1=\"11\" x2=\"14\" y2=\"11\"></line></svg>Click to Enlarge</span></div>\n")
                        .append("          </div>\n")
                        .append("        </div>\n")
                        .append("      </div>\n\n");
            }
            html.append("    </div>\n");
        }

        html.append("    <footer class=\"manual-footer no-print\">\n")
                .append("      <p>© ").append(Calendar.getInstance().get(Calendar.YEAR)).append(" UPYOG NIUA Automation Suite • Generated Automatically</p>\n")
                .append("    </footer>\n")
                .append("  </div>\n\n")
                .append("  <!-- Lightbox Modal -->\n")
                .append("  <div id=\"lightboxModal\" class=\"modal\" onclick=\"closeLightbox()\">\n")
                .append("    <span class=\"modal-close\">&times;</span>\n")
                .append("    <img class=\"modal-content\" id=\"lightboxImage\">\n")
                .append("    <div id=\"caption\" class=\"modal-caption\"></div>\n")
                .append("  </div>\n\n")
                .append("  <script>\n")
                .append("    function openLightbox(imgId) {\n")
                .append("      var img = document.getElementById(imgId);\n")
                .append("      var modal = document.getElementById('lightboxModal');\n")
                .append("      var modalImg = document.getElementById('lightboxImage');\n")
                .append("      var captionText = document.getElementById('caption');\n")
                .append("      if(img && modal) {\n")
                .append("        modal.style.display = 'flex';\n")
                .append("        modalImg.src = img.src;\n")
                .append("        captionText.innerHTML = img.alt;\n")
                .append("      }\n")
                .append("    }\n")
                .append("    function closeLightbox() {\n")
                .append("      document.getElementById('lightboxModal').style.display = 'none';\n")
                .append("    }\n")
                .append("    function filterWorkflow(wfName, btn) {\n")
                .append("      var chips = document.querySelectorAll('.wf-chip');\n")
                .append("      chips.forEach(function(c) { c.classList.remove('active'); });\n")
                .append("      if (btn) btn.classList.add('active');\n")
                .append("      var cards = document.querySelectorAll('.step-card');\n")
                .append("      var visibleIdx = 1;\n")
                .append("      cards.forEach(function(card) {\n")
                .append("        var cardWf = card.getAttribute('data-workflow') || '';\n")
                .append("        if (wfName === 'ALL' || cardWf.toLowerCase() === wfName.toLowerCase()) {\n")
                .append("          card.style.display = '';\n")
                .append("          var badge = card.querySelector('.step-number');\n")
                .append("          if (badge) badge.innerText = 'Step ' + (visibleIdx++);\n")
                .append("        } else {\n")
                .append("          card.style.display = 'none';\n")
                .append("        }\n")
                .append("      });\n")
                .append("      var totalEl = document.getElementById('total-steps-val');\n")
                .append("      if (totalEl) totalEl.innerText = (visibleIdx - 1);\n")
                .append("    }\n")
                .append("    function deleteStepCard(cardId, fileName) {\n")
                .append("      if (!confirm('Are you sure you want to remove this image/step from the user manual?')) return;\n")
                .append("      var card = document.getElementById(cardId);\n")
                .append("      if (card) {\n")
                .append("        card.style.transition = 'all 0.3s ease';\n")
                .append("        card.style.opacity = '0';\n")
                .append("        card.style.transform = 'scale(0.96)';\n")
                .append("        setTimeout(function() {\n")
                .append("          card.remove();\n")
                .append("          renumberSteps();\n")
                .append("        }, 300);\n")
                .append("      }\n")
                .append("      if (fileName && fileName.trim().length > 0) {\n")
                .append("        fetch('/api/report/manual/delete-image?fileName=' + encodeURIComponent(fileName), {\n")
                .append("          method: 'DELETE'\n")
                .append("        }).then(function(res) {\n")
                .append("          console.log('Image deleted from disk:', fileName, res.status);\n")
                .append("        }).catch(function(err) {\n")
                .append("          console.warn('Delete request failed:', err);\n")
                .append("        });\n")
                .append("      }\n")
                .append("    }\n")
                .append("    function renumberSteps() {\n")
                .append("      var cards = document.querySelectorAll('.step-card:not([style*=\"display: none\"])');\n")
                .append("      cards.forEach(function(c, idx) {\n")
                .append("        var badge = c.querySelector('.step-number');\n")
                .append("        if (badge) badge.innerText = 'Step ' + (idx + 1);\n")
                .append("      });\n")
                .append("      var totalEl = document.getElementById('total-steps-val');\n")
                .append("      if (totalEl) totalEl.innerText = cards.length;\n")
                .append("    }\n")
                .append("    document.addEventListener('keydown', function(e) {\n")
                .append("      if (e.key === 'Escape') closeLightbox();\n")
                .append("    });\n")
                .append("  </script>\n")
                .append("</body>\n")
                .append("</html>\n");

        Files.writeString(outputFile.toPath(), html.toString());
        logger.info("HTML User Manual generated successfully: {}", outputFile.getAbsolutePath());
        return outputFile;
    }

    public static File generateHtmlManual(String moduleName) throws IOException {
        return generateHtmlManual(moduleName, null);
    }

    /**
     * Bundles the generated HTML User Manual and all raw PNG screenshots into a ZIP archive.
     *
     * @param moduleName target module name
     * @param testCaseFilter optional test case or workflow filter
     * @param out OutputStream to write ZIP bytes to
     * @throws IOException if packaging fails
     */
    public static void createManualZipPackage(String moduleName, String testCaseFilter, OutputStream out) throws IOException {
        File htmlManual = generateHtmlManual(moduleName, testCaseFilter);
        List<File> screenshotFiles = getScreenshotsForModule(moduleName, testCaseFilter);

        try (ZipOutputStream zos = new ZipOutputStream(out)) {
            // Add HTML Manual
            if (htmlManual.exists()) {
                ZipEntry manualEntry = new ZipEntry(htmlManual.getName());
                zos.putNextEntry(manualEntry);
                Files.copy(htmlManual.toPath(), zos);
                zos.closeEntry();
            }

            // Add Screenshots folder
            for (File img : screenshotFiles) {
                ZipEntry imgEntry = new ZipEntry("screenshots/" + img.getName());
                zos.putNextEntry(imgEntry);
                Files.copy(img.toPath(), zos);
                zos.closeEntry();
            }

            // Add README
            String readme = "UPYOG AUTOMATION USER MANUAL\n"
                    + "============================\n\n"
                    + "Module: " + (moduleName != null ? moduleName : "ALL") + "\n"
                    + "Workflow: " + (testCaseFilter != null ? testCaseFilter : "ALL") + "\n"
                    + "Generated: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n"
                    + "Total Screenshots: " + screenshotFiles.size() + "\n\n"
                    + "Contents:\n"
                    + "1. " + htmlManual.getName() + " - Complete interactive visual manual (open in any web browser)\n"
                    + "2. screenshots/ - Directory containing all raw captured screen images (.png)\n";

            ZipEntry readmeEntry = new ZipEntry("README.txt");
            zos.putNextEntry(readmeEntry);
            zos.write(readme.getBytes());
            zos.closeEntry();
        }
    }

    public static void createManualZipPackage(String moduleName, OutputStream out) throws IOException {
        createManualZipPackage(moduleName, null, out);
    }

    /**
     * Creates a ZIP archive containing only the raw screenshot image files.
     *
     * @param moduleName target module name or "ALL"
     * @param testCaseFilter optional test case or workflow filter
     * @param out OutputStream to write ZIP bytes to
     * @throws IOException if packaging fails
     */
    public static void createScreenshotsZip(String moduleName, String testCaseFilter, OutputStream out) throws IOException {
        List<File> screenshotFiles = getScreenshotsForModule(moduleName, testCaseFilter);

        try (ZipOutputStream zos = new ZipOutputStream(out)) {
            for (File img : screenshotFiles) {
                ZipEntry imgEntry = new ZipEntry(img.getName());
                zos.putNextEntry(imgEntry);
                Files.copy(img.toPath(), zos);
                zos.closeEntry();
            }
        }
    }

    public static void createScreenshotsZip(String moduleName, OutputStream out) throws IOException {
        createScreenshotsZip(moduleName, null, out);
    }

    /**
     * Converts identifier words into title-cased, readable descriptions.
     */
    private static String humanize(String text) {
        if (text == null || text.trim().isEmpty()) return "Screen Step";
        String[] words = text.replaceAll("[_-]", " ").trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (w.isEmpty()) continue;
            if (sb.length() > 0) sb.append(" ");
            sb.append(Character.toUpperCase(w.charAt(0)));
            if (w.length() > 1) {
                sb.append(w.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private static String getManualCss() {
        return ":root {\n"
                + "  --primary: #4f46e5;\n"
                + "  --primary-hover: #4338ca;\n"
                + "  --primary-light: #eef2ff;\n"
                + "  --success: #10b981;\n"
                + "  --danger: #ef4444;\n"
                + "  --bg: #f8fafc;\n"
                + "  --card-bg: #ffffff;\n"
                + "  --text-main: #0f172a;\n"
                + "  --text-muted: #64748b;\n"
                + "  --border: #e2e8f0;\n"
                + "  --radius: 12px;\n"
                + "  --shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.05), 0 8px 10px -6px rgba(0, 0, 0, 0.01);\n"
                + "}\n"
                + "* { box-sizing: border-box; margin: 0; padding: 0; font-family: 'Inter', system-ui, -apple-system, sans-serif; }\n"
                + "body { background-color: var(--bg); color: var(--text-main); line-height: 1.6; }\n"
                + ".top-bar { background: #1e1b4b; color: white; padding: 14px 32px; display: flex; justify-content: space-between; align-items: center; position: sticky; top: 0; z-index: 100; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }\n"
                + ".brand-title { font-weight: 700; font-size: 1.15rem; letter-spacing: -0.02em; }\n"
                + ".brand-title span { color: #818cf8; }\n"
                + ".actions-group { display: flex; gap: 10px; }\n"
                + ".btn { display: inline-flex; align-items: center; padding: 8px 16px; border-radius: 8px; font-size: 0.875rem; font-weight: 600; text-decoration: none; cursor: pointer; border: none; transition: all 0.2s ease; }\n"
                + ".btn-primary { background: #6366f1; color: white; }\n"
                + ".btn-primary:hover { background: #4f46e5; transform: translateY(-1px); }\n"
                + ".btn-secondary { background: rgba(255,255,255,0.12); color: white; border: 1px solid rgba(255,255,255,0.2); }\n"
                + ".btn-secondary:hover { background: rgba(255,255,255,0.2); }\n"
                + ".container { max-width: 1100px; margin: 0 auto; padding: 36px 20px; }\n"
                + ".manual-header { background: linear-gradient(135deg, #312e81 0%, #4338ca 50%, #4f46e5 100%); color: white; padding: 40px; border-radius: 16px; margin-bottom: 36px; box-shadow: 0 20px 25px -5px rgba(79, 70, 229, 0.2); }\n"
                + ".header-badge { display: inline-block; background: rgba(255,255,255,0.18); backdrop-filter: blur(8px); padding: 4px 12px; border-radius: 20px; font-size: 0.8rem; font-weight: 600; text-transform: uppercase; letter-spacing: 0.05em; margin-bottom: 12px; }\n"
                + ".module-title { font-size: 2.4rem; font-weight: 800; letter-spacing: -0.03em; line-height: 1.2; margin-bottom: 8px; }\n"
                + ".header-subtitle { font-size: 1.05rem; opacity: 0.9; max-width: 700px; margin-bottom: 24px; }\n"
                + ".meta-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 16px; }\n"
                + ".meta-card { background: rgba(255,255,255,0.1); backdrop-filter: blur(10px); padding: 14px 18px; border-radius: 10px; border: 1px solid rgba(255,255,255,0.15); }\n"
                + ".meta-label { display: block; font-size: 0.75rem; text-transform: uppercase; letter-spacing: 0.05em; opacity: 0.8; margin-bottom: 4px; }\n"
                + ".meta-val { font-size: 1.15rem; font-weight: 700; }\n"
                + ".text-success { color: #a7f3d0; }\n"
                + ".workflow-filter-bar { margin-top: 24px; padding-top: 20px; border-top: 1px solid rgba(255,255,255,0.2); display: flex; align-items: center; gap: 12px; flex-wrap: wrap; }\n"
                + ".wf-filter-title { font-size: 0.82rem; font-weight: 600; text-transform: uppercase; letter-spacing: 0.05em; opacity: 0.9; }\n"
                + ".wf-chips-wrapper { display: flex; gap: 8px; flex-wrap: wrap; }\n"
                + ".wf-chip { background: rgba(255,255,255,0.14); color: white; border: 1px solid rgba(255,255,255,0.25); padding: 6px 14px; border-radius: 20px; font-size: 0.82rem; font-weight: 600; cursor: pointer; transition: all 0.2s ease; }\n"
                + ".wf-chip:hover { background: rgba(255,255,255,0.25); transform: translateY(-1px); }\n"
                + ".wf-chip.active { background: white; color: #312e81; border-color: white; box-shadow: 0 4px 12px rgba(0,0,0,0.15); }\n"
                + ".steps-timeline { display: flex; flex-direction: column; gap: 32px; }\n"
                + ".step-card { background: var(--card-bg); border-radius: var(--radius); border: 1px solid var(--border); box-shadow: var(--shadow); overflow: hidden; transition: transform 0.2s ease, box-shadow 0.2s ease; }\n"
                + ".step-card:hover { transform: translateY(-2px); box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.08); }\n"
                + ".step-card-header { padding: 20px 24px; border-bottom: 1px solid var(--border); display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 12px; background: #ffffff; }\n"
                .replace("\\", "\\\\")
                + ".step-badge-wrap { display: flex; align-items: center; gap: 14px; }\n"
                + ".step-number { background: var(--primary-light); color: var(--primary); font-weight: 800; font-size: 0.875rem; padding: 6px 14px; border-radius: 20px; }\n"
                + ".step-title { font-size: 1.25rem; font-weight: 700; color: var(--text-main); }\n"
                + ".step-tags { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; }\n"
                + ".tag { font-size: 0.75rem; font-weight: 600; padding: 4px 10px; border-radius: 6px; }\n"
                + ".tag-module { background: #e0e7ff; color: #3730a3; font-weight: 700; }\n"
                + ".tag-workflow { background: #fef3c7; color: #92400e; font-weight: 700; }\n"
                + ".tag-pass { background: #ecfdf5; color: #059669; }\n"
                + ".tag-fail { background: #fef2f2; color: #dc2626; }\n"
                + ".tag-time { background: #f8fafc; color: #64748b; border: 1px solid #e2e8f0; }\n"
                + ".btn-remove-step { display: inline-flex; align-items: center; gap: 4px; background: #fef2f2; color: #dc2626; border: 1px solid #fecaca; padding: 4px 9px; border-radius: 6px; font-size: 0.75rem; font-weight: 600; cursor: pointer; transition: all 0.2s ease; }\n"
                + ".btn-remove-step:hover { background: #fee2e2; color: #b91c1c; border-color: #fca5a5; transform: translateY(-1px); }\n"
                + ".step-card-body { padding: 24px; }\n"
                + ".step-instruction { font-size: 0.95rem; color: var(--text-muted); margin-bottom: 18px; }\n"
                + ".step-instruction strong { color: var(--text-main); }\n"
                + ".step-instruction code { background: #f1f5f9; color: #0f172a; padding: 2px 6px; border-radius: 4px; font-size: 0.85rem; }\n"
                + ".image-container { position: relative; border-radius: 10px; overflow: hidden; border: 1px solid var(--border); background: #0f172a; cursor: pointer; }\n"
                + ".manual-img { width: 100%; height: auto; display: block; transition: transform 0.3s ease; }\n"
                + ".image-container:hover .manual-img { transform: scale(1.01); }\n"
                + ".image-overlay { position: absolute; bottom: 0; left: 0; right: 0; padding: 12px; background: linear-gradient(to top, rgba(0,0,0,0.6), transparent); display: flex; justify-content: flex-end; opacity: 0; transition: opacity 0.2s ease; }\n"
                + ".image-container:hover .image-overlay { opacity: 1; }\n"
                + ".zoom-btn { background: rgba(255,255,255,0.9); color: #0f172a; font-size: 0.8rem; font-weight: 600; padding: 6px 12px; border-radius: 6px; }\n"
                + ".empty-state { text-align: center; padding: 60px 20px; background: white; border-radius: 16px; border: 2px dashed var(--border); }\n"
                + ".empty-icon { font-size: 3rem; margin-bottom: 16px; }\n"
                + ".empty-state h3 { font-size: 1.3rem; margin-bottom: 8px; }\n"
                + ".empty-state p { color: var(--text-muted); }\n"
                + ".manual-footer { text-align: center; margin-top: 48px; color: var(--text-muted); font-size: 0.85rem; }\n"
                + ".modal { display: none; position: fixed; z-index: 1000; left: 0; top: 0; width: 100%; height: 100%; background: rgba(15, 23, 42, 0.9); backdrop-filter: blur(4px); align-items: center; justify-content: center; flex-direction: column; }\n"
                + ".modal-content { max-width: 92%; max-height: 85%; border-radius: 8px; box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5); animation: zoomIn 0.2s ease; }\n"
                + ".modal-close { position: absolute; top: 20px; right: 30px; color: #f1f5f9; font-size: 40px; font-weight: bold; cursor: pointer; }\n"
                + ".modal-caption { color: #e2e8f0; font-size: 1rem; margin-top: 16px; font-weight: 500; }\n"
                + "@keyframes zoomIn { from { transform: scale(0.9); opacity: 0; } to { transform: scale(1); opacity: 1; } }\n"
                + "@media print {\n"
                + "  .no-print { display: none !important; }\n"
                + "  body { background: white !important; color: black !important; }\n"
                + "  .container { max-width: 100% !important; padding: 0 !important; }\n"
                + "  .manual-header { background: #312e81 !important; -webkit-print-color-adjust: exact; print-color-adjust: exact; margin-bottom: 20px; padding: 24px; }\n"
                + "  .step-card { page-break-inside: avoid; border: 1px solid #ccc !important; box-shadow: none !important; margin-bottom: 24px; }\n"
                + "  .manual-img { max-height: 500px; object-fit: contain; }\n"
                + "}\n";
    }
}
