package org.egov.edcr.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.multipdf.LayerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.util.Matrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.fasterxml.jackson.databind.JsonNode;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@Service
public class PdfOverlayTemplateService {

    @Autowired
    private TemplateEngine templateEngine;

    public File impose(File inputPdf,
                       String outputFileName,
                       JsonNode additionalDetails,
                       float expandRight,
                       float expandBottom,
                       float gapDrawingToTables,
                       float gapTop) throws IOException {

        File outputFile = new File(outputFileName);
        File panelPdf = File.createTempFile("impose_panel_", ".pdf");

        try {
            byte[] panelBytes = renderPanelPdf(additionalDetails, Math.max(280f, expandRight - gapDrawingToTables - 16f));
            try (FileOutputStream fos = new FileOutputStream(panelPdf)) {
                fos.write(panelBytes);
            }

            try (PDDocument baseDoc = PDDocument.load(inputPdf);
                 PDDocument panelDoc = PDDocument.load(panelPdf);
                 PDDocument outputDoc = new PDDocument()) {

                LayerUtility lu = new LayerUtility(outputDoc);
                PDDocument flattenedPanelDoc = flattenPanelPages(panelDoc);
                PDFormXObject panelForm = lu.importPageAsForm(flattenedPanelDoc, 0);
                float panelW = flattenedPanelDoc.getPage(0).getMediaBox().getWidth();
                float panelH = flattenedPanelDoc.getPage(0).getMediaBox().getHeight();

                for (int i = 0; i < baseDoc.getNumberOfPages(); i++) {
                    PDPage srcPage = baseDoc.getPage(i);
                    float origW = srcPage.getMediaBox().getWidth();
                    float origH = srcPage.getMediaBox().getHeight();

                    float targetX = origW + gapDrawingToTables;
                    float minWToFit = targetX + panelW + 12f;
                    float minHToFit = gapTop + panelH + 12f;

                    float newW = Math.max(origW + expandRight, minWToFit);
                    float newH = Math.max(origH + expandBottom, minHToFit);

                    PDPage outPage = new PDPage(new PDRectangle(newW, newH));
                    outputDoc.addPage(outPage);

                    PDFormXObject drawingForm = lu.importPageAsForm(baseDoc, i);
                    float drawingY = newH - origH;
                    float panelY = newH - gapTop - panelH;

                    try (PDPageContentStream cs = new PDPageContentStream(outputDoc, outPage,
                            PDPageContentStream.AppendMode.APPEND, true, true)) {
                        cs.setNonStrokingColor(1f, 1f, 1f);
                        cs.addRect(0, 0, newW, newH);
                        cs.fill();

                        // Keep drawing at top-left; grow canvas to right/bottom only.
                        cs.transform(Matrix.getTranslateInstance(0, drawingY));
                        cs.drawForm(drawingForm);

                        cs.transform(Matrix.getTranslateInstance(targetX, panelY - drawingY));
                        cs.drawForm(panelForm);
                    }
                }

                if (outputDoc.getNumberOfPages() > 0) {
                    org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination dest =
                            new org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination();
                    dest.setPage(outputDoc.getPage(0));
                    dest.setLeft(0);
                    dest.setTop((int) outputDoc.getPage(0).getMediaBox().getHeight());
                    dest.setZoom(1.25f);
                    outputDoc.getDocumentCatalog().setOpenAction(dest);
                }
                outputDoc.save(outputFile);
            }
        } finally {
            if (panelPdf.exists()) {
                panelPdf.delete();
            }
        }

        return outputFile;
    }

    private PDDocument flattenPanelPages(PDDocument panelDoc) throws IOException {
        if (panelDoc.getNumberOfPages() <= 1) {
            PDDocument single = new PDDocument();
            single.importPage(panelDoc.getPage(0));
            return single;
        }

        float maxW = 0f;
        float totalH = 0f;
        for (int i = 0; i < panelDoc.getNumberOfPages(); i++) {
            PDRectangle mb = panelDoc.getPage(i).getMediaBox();
            maxW = Math.max(maxW, mb.getWidth());
            totalH += mb.getHeight();
        }

        PDDocument flattened = new PDDocument();
        PDPage onePage = new PDPage(new PDRectangle(maxW, totalH));
        flattened.addPage(onePage);

        LayerUtility lu = new LayerUtility(flattened);
        float yTop = totalH;
        try (PDPageContentStream cs = new PDPageContentStream(flattened, onePage,
                PDPageContentStream.AppendMode.APPEND, true, true)) {
            for (int i = 0; i < panelDoc.getNumberOfPages(); i++) {
                PDPage src = panelDoc.getPage(i);
                PDRectangle mb = src.getMediaBox();
                PDFormXObject form = lu.importPageAsForm(panelDoc, i);
                yTop -= mb.getHeight();
                cs.saveGraphicsState();
                cs.transform(Matrix.getTranslateInstance(0, yTop));
                cs.drawForm(form);
                cs.restoreGraphicsState();
            }
        }
        return flattened;
    }

    private byte[] renderPanelPdf(JsonNode additionalDetails, float panelWidthPt) throws IOException {
        Context context = new Context();
        context.setVariable("sections", buildSections(additionalDetails));

        String html = templateEngine.process("imposePdfInto", context);
        String widthCss = "<style>@page { size: " + ptToMm(panelWidthPt) + "mm auto; margin: 8pt; }</style>";
        String htmlWithSize = html.replace("</head>", widthCss + "</head>");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(htmlWithSize, null);
        builder.toStream(os);
        builder.run();
        return os.toByteArray();
    }

    private List<Map<String, Object>> buildSections(JsonNode root) {
        JsonNode d = root.path("details");
        List<Map<String, Object>> sections = new ArrayList<Map<String, Object>>();

        addLabeledKeyValueSection(sections, "left", "Application Details", d.path("applicationDetails"), new String[][] {
                {"Name of Applicant", "nameOfApplicant"},
                {"File Number", "fileNumber"},
                {"eDCR Number", "edcrNumber"},
                {"ULB Name", "ulbName"},
                {"ULB Type", "ulbType"},
                {"Building Category", "buildingCategory"},
                {"Proposed Site Address", "proposedSiteAddress"},
                {"Khasra No.", "khasraNo"},
                {"Zone", "zone"}
        });

        addLabeledKeyValueSection(sections, "left", "Plot Area Details", d.path("plotAreaDetails"), new String[][] {
                {"Plot Area as per Drawing (m²)", "plotAreaAsPerDrawing"},
                {"Plot Area as per Declaration (m²)", "plotAreaAsPerDeclaration"}
        });

        addLabeledKeyValueSection(sections, "left", "Built Up Area", d.path("builtUpArea"), new String[][] {
                {"Existing Built-Up Area (m²)", "existingBuiltUpArea"},
                {"Proposed Built-Up Area (m²)", "proposedBuiltUpArea"},
                {"Total Built-Up Area (m²)", "totalBuiltUpArea"}
        });

        addTripleSection(sections, "left", "FAR Details", d.path("farDetails"),
                Arrays.asList("Description", "FAR", "FAR Area"),
                new String[][] {
                        {"Total Permissible FAR", safeText(d.path("farDetails"), "totalPermissibleFAR"), safeText(d.path("farDetails"), "totalPermissibleFARArea")},
                        {"Total Proposed FAR", safeText(d.path("farDetails"), "totalProposedFAR"), safeText(d.path("farDetails"), "totalProposedFARArea")}
                });

        addTripleSection(sections, "left", "ECS Details", d.path("ecsDetails"),
                Arrays.asList("Description", "Required", "Provided"),
                new String[][] {
                        {"Parking", safeText(d.path("ecsDetails"), "required"), safeText(d.path("ecsDetails"), "parking")},
                        {"Two-Wheeler Parking", "", safeText(d.path("ecsDetails"), "twoWheelerParking")},
                        {"Open Parking Area", "", safeText(d.path("ecsDetails"), "openParkingArea")},
                        {"Covered/Stilt Parking", "", safeText(d.path("ecsDetails"), "coveredStiltParkingArea")},
                        {"Basement Parking Area", "", safeText(d.path("ecsDetails"), "basementParkingArea")}
                });

        addTripleSection(sections, "left", "Building Height", d.path("buildingHeight"),
                Arrays.asList("Description", "Permissible", "Proposed"),
                new String[][] {
                        {"Building Height (m)", safeText(d.path("buildingHeight"), "permissibleBuildingHeight"), safeText(d.path("buildingHeight"), "proposedBuildingHeight")},
                        {"Total Building Height (m)", safeText(d.path("buildingHeight"), "permissibleTotalHeight"), safeText(d.path("buildingHeight"), "proposedTotalHeight")}
                });

        addLabeledKeyValueSection(sections, "left", "Road Description", d.path("roadDescription"), new String[][] {
                {"Approach Road Width (m)", "approachRoadWidth"},
                {"Rear Side Road Width (m)", "rearSideRoadWidth"},
                {"Side 1 Road Width (m)", "side1RoadWidth"},
                {"Side 2 Road Width (m)", "side2RoadWidth"}
        });

        addLabeledKeyValueSection(sections, "left", "Office Use", d.path("officeUse"), new String[][] {
                {"Examined By", "examinedBy"},
                {"Approved/Sanctioned By", "approvedSanctionedBy"},
                {"Approval/Sanction Date", "approvalSanctionDate"},
                {"Valid Till", "validTill"}
        });

        addLabeledKeyValueSection(sections, "left", "Professional's Signature", d.path("professionalSignature"), new String[][] {
                {"Uploaded Signature", "uploadedSignature"}
        });
        addESignSection(sections, "left", d.path("eSign"));

        addBlockWiseSummary(sections, "right", d.path("blockWiseSummary"));
        addDynamicBlocks(sections, "right", d.path("blocks"));
        addSetbacks(sections, "right", d.path("setbacks"));

        return sections;
    }

    private void addESignSection(List<Map<String, Object>> sections, String side, JsonNode eSignNode) {
        Map<String, Object> s = new HashMap<String, Object>();
        s.put("side", side);
        s.put("showHeader", false);
        s.put("title", "E-sign");
        s.put("columns", Arrays.asList("Field", "Value"));
        List<List<String>> rows = new ArrayList<List<String>>();
        if (eSignNode.isArray()) {
            for (JsonNode sign : eSignNode) {
                rows.add(Arrays.asList(
                        safeText(sign, "signatoryName"),
                        safeText(sign, "designation")));
            }
        } else if (!eSignNode.isMissingNode()) {
            rows.add(Arrays.asList(
                    safeText(eSignNode, "signatoryName"),
                    safeText(eSignNode, "designation")));
        }
        if (rows.isEmpty()) {
            rows.add(Arrays.asList("N/A", "N/A"));
        }
        s.put("rows", rows);
        sections.add(s);
    }

    private void addDynamicBlocks(List<Map<String, Object>> sections, String side, JsonNode blocks) {
        if (!blocks.isArray()) return;
        for (JsonNode b : blocks) {
            String blockName = normalizeBlockName(safeText(b, "blockName"));
            String sectionTitle = safeText(b, "proposedTitle");
            if ("N/A".equals(sectionTitle)) {
                sectionTitle = "1. " + blockName + " - Proposed Details";
            }

            if (b.path("proposedDetails").isArray()) {
                addArrayAsTable(sections, side, sectionTitle, b.path("proposedDetails"),
                        Arrays.asList("Floor", "Occupancy/Sub Occupancy", "Built Up Area in m\u00B2", "Deduction Area in m\u00B2", "Floor Area in m\u00B2"),
                        Arrays.asList("floor", "occupancySubOccupancy", "builtUpArea", "deductionArea", "floorArea"));
                addRemarksSection(sections, side, b.path("remarks"));
                continue;
            }

            addArrayAsTable(sections, side, blockName + " - Floor wise Built Up/FAR", b.path("floorWiseBuiltUpFAR"),
                    Arrays.asList("Floor", "Eff. Built-Up (m\u00B2)", "Exist. Built-Up (m\u00B2)", "Proposed FAR (m\u00B2)", "Existing FAR (m\u00B2)"),
                    Arrays.asList("floor", "effectiveBuiltUpArea", "existingBuiltUpArea", "proposedFAR", "existingFAR"));
//            addArrayAsTable(sections, side, blockName + " - Deduction Details", b.path("deductionDetails"),
//                    Arrays.asList("Floor", "BUA incl. Ded.", "BUA Deduction", "Eff. BUA", "Non-FAR", "FAR Area"),
//                    Arrays.asList("floor", "builtUpAreaIncludingDeduction", "builtUpDeductionArea", "effectiveBuiltUpArea", "nonFARArea", "farArea"));
//            addArrayAsTable(sections, side, blockName + " - Built-Up Deductions", b.path("builtUpDeductions"),
//                    Arrays.asList("Floor", "Voids (m²)", "Ramp (m²)", "Total Deduction (m²)"),
//                    Arrays.asList("floor", "voids", "ramp", "totalDeduction"));
//            addArrayAsTable(sections, side, blockName + " - FAR Deduction Details", b.path("farDeductionDetails"),
//                    Arrays.asList("Floor", "Mumty (m²)", "Total Deduction (m²)"),
//                    Arrays.asList("floor", "mumty", "totalDeduction"));
        }
    }

    private void addArrayAsTable(List<Map<String, Object>> sections, String side, String title, JsonNode arr,
                                 List<String> columns, List<String> keys) {
        Map<String, Object> s = new HashMap<String, Object>();
        s.put("side", side);
        s.put("showHeader", true);
        s.put("title", title);
        s.put("columns", columns);
        List<List<String>> rows = new ArrayList<List<String>>();
        if (arr.isArray()) {
            for (JsonNode item : arr) {
                List<String> row = new ArrayList<String>();
                String floorValue = safeText(item, "floor");
                for (int i = 0; i < keys.size(); i++) {
                    String value = safeText(item, keys.get(i));
                    // For Total row, only numeric columns should fallback to 0.0.
                    // Keep Occupancy/Sub Occupancy column as N/A.
                    if (i > 1 && "Total".equalsIgnoreCase(floorValue) && "N/A".equals(value)) {
                        value = "0.0";
                    }
                    row.add(value);
                }
                rows.add(row);
            }
        }
        s.put("rows", rows);
        sections.add(s);
    }

    private String normalizeBlockName(String rawBlockName) {
        if (rawBlockName == null || rawBlockName.trim().isEmpty() || "N/A".equalsIgnoreCase(rawBlockName)) {
            return "Block No 1";
        }
        String normalized = rawBlockName.replace(" - Proposed Details", "").trim();
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)").matcher(normalized);
        if (m.find()) {
            return "Block No " + m.group(1);
        }
        return normalized.replace("Block", "Block No").replaceAll("\\s+", " ").trim();
    }

    private void addRemarksSection(List<Map<String, Object>> sections, String side, JsonNode remarksNode) {
        String remarks = remarksNode == null || remarksNode.isMissingNode() || remarksNode.isNull()
                ? "" : remarksNode.asText("");
        if (remarks.trim().isEmpty() || "N/A".equalsIgnoreCase(remarks.trim())) {
            return;
        }

        Map<String, Object> s = new HashMap<String, Object>();
        s.put("side", side);
        s.put("showHeader", false);
        s.put("title", "");
        s.put("hideTitle", true);
        s.put("columns", Arrays.asList("Remarks"));

        List<List<String>> rows = new ArrayList<List<String>>();
        String[] lines = remarks.split("\\r?\\n");
        for (String line : lines) {
            if (line != null && !line.trim().isEmpty()) {
                rows.add(Arrays.asList(line.trim()));
            }
        }
        if (!rows.isEmpty()) {
            s.put("rows", rows);
            sections.add(s);
        }
    }

    private void addSetbacks(List<Map<String, Object>> sections, String side, JsonNode setNode) {
        Map<String, Object> s = new HashMap<String, Object>();
        s.put("side", side);
        s.put("showHeader", true);
        s.put("title", "Setbacks");
        s.put("columns", Arrays.asList("Description", "Permissible", "Provided"));
        List<List<String>> rows = new ArrayList<List<String>>();
        rows.add(Arrays.asList("Front", safeText(setNode, "frontPermissible"), safeText(setNode, "frontProvided")));
        rows.add(Arrays.asList("Rear", safeText(setNode, "rearPermissible"), safeText(setNode, "rearProvided")));
        rows.add(Arrays.asList("Side 1", safeText(setNode, "side1Permissible"), safeText(setNode, "side1Provided")));
        rows.add(Arrays.asList("Side 2", safeText(setNode, "side2Permissible"), safeText(setNode, "side2Provided")));
        s.put("rows", rows);
        sections.add(s);
    }

    private void addBlockWiseSummary(List<Map<String, Object>> sections, String side, JsonNode bws) {
        Map<String, Object> s = new HashMap<String, Object>();
        s.put("side", side);
        s.put("showHeader", true);
        s.put("title", "Block Wise Summary");
        s.put("columns", Arrays.asList("Total Plot Area (m²)", "Ground Coverage (m²)", "Total Built-up Area (m²)", "Total FAR Area (m²)"));
        s.put("rows", Arrays.asList(Arrays.asList(
                safeText(bws, "totalPlotArea"),
                safeText(bws, "groundCoverage"),
                safeText(bws, "totalBuiltUpArea"),
                safeText(bws, "totalFARArea")
        )));
        sections.add(s);
    }

    private void addTripleSection(List<Map<String, Object>> sections, String side, String title,
                                  JsonNode node, List<String> columns, String[][] rowsData) {
        Map<String, Object> s = new HashMap<String, Object>();
        s.put("side", side);
        s.put("showHeader", true);
        s.put("title", title);
        s.put("columns", columns);
        List<List<String>> rows = new ArrayList<List<String>>();
        for (String[] r : rowsData) {
            rows.add(Arrays.asList(r));
        }
        s.put("rows", rows);
        sections.add(s);
    }

    private void addKeyValueSection(List<Map<String, Object>> sections, String title, JsonNode node, List<String> keys) {
        Map<String, Object> s = new HashMap<String, Object>();
        s.put("title", title);
        s.put("columns", Arrays.asList("Field", "Value"));

        List<List<String>> rows = new ArrayList<List<String>>();
        for (String key : keys) {
            rows.add(Arrays.asList(toLabel(key), safeText(node, key)));
        }
        s.put("rows", rows);
        sections.add(s);
    }

    private void addLabeledKeyValueSection(List<Map<String, Object>> sections, String title, JsonNode node, String[][] labelsAndKeys) {
        Map<String, Object> s = new HashMap<String, Object>();
        s.put("side", "left");
        s.put("showHeader", false);
        s.put("title", title);
        s.put("columns", Arrays.asList("Field", "Value"));
        List<List<String>> rows = new ArrayList<List<String>>();
        for (String[] pair : labelsAndKeys) {
            rows.add(Arrays.asList(pair[0], safeText(node, pair[1])));
        }
        s.put("rows", rows);
        sections.add(s);
    }

    private void addLabeledKeyValueSection(List<Map<String, Object>> sections, String side, String title, JsonNode node, String[][] labelsAndKeys) {
        Map<String, Object> s = new HashMap<String, Object>();
        s.put("side", side);
        s.put("showHeader", false);
        s.put("title", title);
        s.put("columns", Arrays.asList("Field", "Value"));
        List<List<String>> rows = new ArrayList<List<String>>();
        for (String[] pair : labelsAndKeys) {
            rows.add(Arrays.asList(pair[0], safeText(node, pair[1])));
        }
        s.put("rows", rows);
        sections.add(s);
    }

    private String safeText(JsonNode node, String field) {
        if (node == null || node.isMissingNode()) return "N/A";
        JsonNode child = node.path(field);
        if (child.isMissingNode() || child.isNull()) return "N/A";
        if (child.isBoolean()) return child.asBoolean() ? "Yes" : "No";
        String val = child.asText("").trim();
        return val.isEmpty() || "null".equalsIgnoreCase(val) ? "N/A" : val;
    }

    private String toLabel(String key) {
        if (key == null || key.isEmpty()) return "";
        StringBuilder out = new StringBuilder();
        out.append(Character.toUpperCase(key.charAt(0)));
        for (int i = 1; i < key.length(); i++) {
            char c = key.charAt(i);
            if (Character.isUpperCase(c)) {
                out.append(' ');
            }
            out.append(c);
        }
        return out.toString();
    }

    private File expandPageCanvas(File inputPdf, float expandRight, float expandBottom) throws IOException {
        File output = File.createTempFile("expanded_", ".pdf");

        try (PDDocument inputDoc = PDDocument.load(inputPdf);
             PDDocument outputDoc = new PDDocument()) {

            LayerUtility lu = new LayerUtility(outputDoc);

            for (int i = 0; i < inputDoc.getNumberOfPages(); i++) {
                PDPage srcPage = inputDoc.getPage(i);
                float origW = srcPage.getMediaBox().getWidth();
                float origH = srcPage.getMediaBox().getHeight();

                float newW = origW + expandRight;
                float newH = origH + expandBottom;

                PDPage newPage = new PDPage(new PDRectangle(newW, newH));
                outputDoc.addPage(newPage);

                try (PDPageContentStream cs = new PDPageContentStream(outputDoc, newPage)) {
                    cs.setNonStrokingColor(1f, 1f, 1f);
                    cs.addRect(0, 0, newW, newH);
                    cs.fill();
                }

                PDFormXObject form = lu.importPageAsForm(inputDoc, i);
                try (PDPageContentStream cs = new PDPageContentStream(
                        outputDoc, newPage, PDPageContentStream.AppendMode.APPEND, true, true)) {
                    cs.transform(Matrix.getTranslateInstance(0, expandBottom));
                    cs.drawForm(form);
                }
            }
            outputDoc.save(output);
        }
        return output;
    }

    private float ptToMm(float pt) {
        return (pt * 25.4f) / 72f;
    }
}



