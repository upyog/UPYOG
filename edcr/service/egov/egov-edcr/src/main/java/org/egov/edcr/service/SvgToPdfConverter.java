package org.egov.edcr.service;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.fop.svg.PDFTranscoder;
import org.springframework.stereotype.Service;

import java.io.*;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * Converts SVG files to PDF using Apache Batik/FOP.
 * Falls back to iText7 SVG conversion if Batik fails.
 */

@Service
public class SvgToPdfConverter {
	
	private static Logger LOG = LogManager.getLogger(SvgToPdfConverter.class);

	public static void convert(File svgFile, File pdfFile) throws Exception {
	    LOG.info("Converting SVG → PDF: {}", svgFile.getAbsolutePath());

	    if (!svgFile.exists() || !svgFile.canRead()) {
	        throw new Exception("SVG file not found or not readable: " + svgFile.getAbsolutePath());
	    }

	    String parser = XMLResourceDescriptor.getXMLParserClassName();
	    SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
	    String uri = svgFile.toURI().toString();

	    // --- Step 1: Parse SVG DOM ---
	    SVGDocument svgDoc;
	    try (InputStream is = new FileInputStream(svgFile)) {
	        svgDoc = factory.createSVGDocument(uri, is);
	    } catch (Exception e) {
	        LOG.error("Failed to parse SVG DOM from '{}': {}", svgFile.getAbsolutePath(), e.getMessage(), e);
	        throw new Exception("SVG DOM parsing failed: " + e.getMessage(), e);
	    }

	    // --- Step 2: Read SVG dimensions ---
	    SVGSVGElement root = svgDoc.getRootElement();
	    float svgWidth  = parseAttrPx(root.getAttribute("width"));
	    float svgHeight = parseAttrPx(root.getAttribute("height"));

	    // Fallback to viewBox
	    if (svgWidth <= 0 || svgHeight <= 0) {
	        String vb = root.getAttribute("viewBox");
	        if (vb != null && !vb.isEmpty()) {
	            String[] p = vb.trim().split("[\\s,]+");
	            if (p.length >= 4) {
	                try {
	                    svgWidth  = Float.parseFloat(p[2]);
	                    svgHeight = Float.parseFloat(p[3]);
	                } catch (NumberFormatException e) {
	                    LOG.error("Invalid viewBox values in SVG '{}': {}", svgFile.getAbsolutePath(), vb);
	                    throw new Exception("Invalid SVG viewBox: " + vb, e);
	                }
	            }
	        }
	    }

	    if (svgWidth <= 0 || svgHeight <= 0) {
	        throw new Exception("Cannot determine SVG dimensions from: " + svgFile.getAbsolutePath());
	    }

	    LOG.info("SVG size: {} x {} px", svgWidth, svgHeight);

	    // --- Step 3: Transcode SVG → PDF ---
	    PDFTranscoder transcoder = new PDFTranscoder();
	    transcoder.addTranscodingHint(PDFTranscoder.KEY_WIDTH,  svgWidth);
	    transcoder.addTranscodingHint(PDFTranscoder.KEY_HEIGHT, svgHeight);
	    transcoder.addTranscodingHint(PDFTranscoder.KEY_PIXEL_UNIT_TO_MILLIMETER, 25.4f / 96f);

	    try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
	        TranscoderInput input = new TranscoderInput(svgDoc);
	        input.setURI(uri);
	        TranscoderOutput output = new TranscoderOutput(fos);
	        transcoder.transcode(input, output);
	    } catch (TranscoderException e) {
	        LOG.error("Transcoder error converting '{}' → '{}': {}",
	                svgFile.getAbsolutePath(), pdfFile.getAbsolutePath(), e.getMessage(), e);
	        throw new Exception("PDF transcoding failed: " + e.getMessage(), e);
	    } catch (IOException e) {
	        LOG.error("IO error writing PDF '{}': {}", pdfFile.getAbsolutePath(), e.getMessage(), e);
	        throw new Exception("PDF file write failed: " + e.getMessage(), e);
	    }

	    LOG.info("PDF saved (vector): {}", pdfFile.getAbsolutePath());
	}
	
	public static void convert(String svgPath, String pdfPath) throws Exception {
	    convert(new File(svgPath), new File(pdfPath));
	}

    /**
     * Parse SVG length attribute to pixels.
     * Handles: "2480px", "2480", "297mm", "21cm", "8.27in"
     */
    static float parseAttrPx(String val) {
        if (val == null || val.isEmpty()) return 0;
        val = val.trim().toLowerCase();
        try {
            if (val.endsWith("px"))  return Float.parseFloat(val.replace("px", ""));
            if (val.endsWith("mm"))  return Float.parseFloat(val.replace("mm", "")) * 96f / 25.4f;
            if (val.endsWith("cm"))  return Float.parseFloat(val.replace("cm", "")) * 96f / 2.54f;
            if (val.endsWith("in"))  return Float.parseFloat(val.replace("in", "")) * 96f;
            if (val.endsWith("pt"))  return Float.parseFloat(val.replace("pt", "")) * 96f / 72f;
            if (val.endsWith("pc"))  return Float.parseFloat(val.replace("pc", "")) * 96f / 6f;
            return Float.parseFloat(val); // bare number = px
        } catch (NumberFormatException e) {
            return 0;
        }
    }


//    private static void convertWithFopDom(String svgPath, String pdfPath) throws Exception {
//        // Parse SVG with Batik DOM to read exact width/height
//        String parser = XMLResourceDescriptor.getXMLParserClassName();
//        SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
//        String uri = new File(svgPath).toURI().toString();
//
//        SVGDocument svgDoc;
//        try (InputStream is = new FileInputStream(svgPath)) {
//            svgDoc = factory.createSVGDocument(uri, is);
//        }
//
//        SVGSVGElement root = svgDoc.getRootElement();
//
//        // Get width and height from SVG root element
//        float svgWidth  = 0;
//        float svgHeight = 0;
//
//        // Try width/height attributes first
//        String wAttr = root.getAttribute("width");
//        String hAttr = root.getAttribute("height");
//
//        if (wAttr != null && !wAttr.isEmpty()) {
//            svgWidth  = parseFloatPx(wAttr);
//        }
//        if (hAttr != null && !hAttr.isEmpty()) {
//            svgHeight = parseFloatPx(hAttr);
//        }
//
//        // Fallback to viewBox if no width/height
//        if (svgWidth <= 0 || svgHeight <= 0) {
//            String viewBox = root.getAttribute("viewBox");
//            if (viewBox != null && !viewBox.isEmpty()) {
//                String[] parts = viewBox.trim().split("[\\s,]+");
//                if (parts.length >= 4) {
//                    svgWidth  = Float.parseFloat(parts[2]);
//                    svgHeight = Float.parseFloat(parts[3]);
//                }
//            }
//        }
//
//        if (svgWidth <= 0 || svgHeight <= 0) {
//            throw new Exception("Could not determine SVG dimensions from DOM");
//        }
//
//        LOG.info("SVG dimensions (DOM): %.2f x %.2f px%n", svgWidth, svgHeight);
//
//        PDFTranscoder transcoder = new PDFTranscoder();
//        // KEY: set exact pixel dimensions so FOP doesn't guess
//        transcoder.addTranscodingHint(PDFTranscoder.KEY_WIDTH,  svgWidth);
//        transcoder.addTranscodingHint(PDFTranscoder.KEY_HEIGHT, svgHeight);
//        // px -> pt conversion: 1px = 0.75pt at 96dpi (SVG spec)
//        // FOP KEY_PIXEL_UNIT_TO_MILLIMETER: 1px = 25.4/96 mm
//        transcoder.addTranscodingHint(PDFTranscoder.KEY_PIXEL_UNIT_TO_MILLIMETER, 25.4f / 96f);
//
//        try (OutputStream os = new FileOutputStream(pdfPath)) {
//            TranscoderInput input = new TranscoderInput(svgDoc);
//            input.setURI(uri);
//            TranscoderOutput output = new TranscoderOutput(os);
//            transcoder.transcode(input, output);
//        }
//        LOG.info("✓ Converted with FOP (DOM) — vector PDF");
//    }

    static float parseFloatPx(String val) {
        if (val == null || val.isEmpty()) return 0;
        val = val.trim().toLowerCase();
        try {
            if (val.endsWith("px")) return Float.parseFloat(val.replace("px", "").trim());
            if (val.endsWith("mm")) return Float.parseFloat(val.replace("mm", "").trim()) * 96f / 25.4f;
            if (val.endsWith("cm")) return Float.parseFloat(val.replace("cm", "").trim()) * 96f / 2.54f;
            if (val.endsWith("in")) return Float.parseFloat(val.replace("in", "").trim()) * 96f;
            if (val.endsWith("pt")) return Float.parseFloat(val.replace("pt", "").trim()) * 96f / 72f;
            return Float.parseFloat(val); // bare number = px
        } catch (NumberFormatException e) {
            return 0;
        }
    }

//    private static void convertWithFopRegex(String svgPath, String pdfPath) throws Exception {
//        float[] dims = readSvgDimensions(svgPath);
//        float svgWidth  = dims[0];
//        float svgHeight = dims[1];
//
//        LOG.info("SVG dimensions (regex): %.2f x %.2f px%n", svgWidth, svgHeight);
//
//        PDFTranscoder transcoder = new PDFTranscoder();
//        transcoder.addTranscodingHint(PDFTranscoder.KEY_WIDTH,  svgWidth);
//        transcoder.addTranscodingHint(PDFTranscoder.KEY_HEIGHT, svgHeight);
//        transcoder.addTranscodingHint(PDFTranscoder.KEY_PIXEL_UNIT_TO_MILLIMETER, 25.4f / 96f);
//
//        try (InputStream is = new FileInputStream(svgPath);
//             OutputStream os = new FileOutputStream(pdfPath)) {
//            TranscoderInput input = new TranscoderInput(is);
//            input.setURI(new File(svgPath).toURI().toString());
//            transcoder.transcode(input, new TranscoderOutput(os));
//        }
//        LOG.info("✓ Converted with FOP (regex dims) — vector PDF");
//    }


    static float[] readSvgDimensions(String svgPath) throws IOException {
        float w = 2480, h = 3508; // A4 portrait fallback
        StringBuilder header = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(svgPath), "UTF-8"))) {
            String line;
            int count = 0;
            while ((line = br.readLine()) != null && count < 15) {
                header.append(line).append(" ");
                count++;
                if (line.contains(">") && count > 1) break;
            }
        }
        String h2 = header.toString();
        java.util.regex.Matcher mw = java.util.regex.Pattern
                .compile("width=\"([0-9.]+(?:px|mm|cm|in|pt)?)\"").matcher(h2);
        java.util.regex.Matcher mh = java.util.regex.Pattern
                .compile("height=\"([0-9.]+(?:px|mm|cm|in|pt)?)\"").matcher(h2);
        if (mw.find()) w = parseFloatPx(mw.group(1));
        if (mh.find()) h = parseFloatPx(mh.group(1));
        return new float[]{w, h};
    }


//    private static void convertWithIText(String svgPath, String pdfPath) throws Exception {
//        float[] dims = readSvgDimensions(svgPath);
//        float w = dims[0];
//        float h = dims[1];
//
//        com.itextpdf.kernel.geom.PageSize pageSize =
//                new com.itextpdf.kernel.geom.PageSize(w, h);
//
//        com.itextpdf.kernel.pdf.PdfWriter writer =
//                new com.itextpdf.kernel.pdf.PdfWriter(pdfPath);
//        com.itextpdf.kernel.pdf.PdfDocument pdfDoc =
//                new com.itextpdf.kernel.pdf.PdfDocument(writer);
//
//        com.itextpdf.layout.Document document =
//                new com.itextpdf.layout.Document(pdfDoc, pageSize);
//        document.setMargins(0, 0, 0, 0);
//
//        try (InputStream svgStream = new FileInputStream(svgPath)) {
//            // Convert SVG to a PDF XObject (vector, not raster)
//            com.itextpdf.kernel.pdf.xobject.PdfFormXObject xObject =
//                    com.itextpdf.svg.converter.SvgConverter.convertToXObject(
//                            svgStream, pdfDoc
//                    );
//
//            // Add it as an image filling the whole page
//            com.itextpdf.layout.element.Image img =
//                    new com.itextpdf.layout.element.Image(xObject);
//            img.setFixedPosition(0, 0);
//            img.setWidth(w);
//            img.setHeight(h);
//            document.add(img);
//        }
//
//        document.close();
//        LOG.info("✓ Converted with iText7 (XObject) — vector PDF");
//    }


//    private static void convertViaPng(String svgPath, String pdfPath) throws Exception {
//        String pngPath = pdfPath.replaceAll("\\.pdf$", "_temp.png");
//
//        // Read dimensions
//        float[] dims = readSvgDimensions(svgPath);
//        float w = dims[0];
//        float h = dims[1];
//
//        PNGTranscoder pngTranscoder = new PNGTranscoder();
//        pngTranscoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH,  w);
//        pngTranscoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, h);
//        pngTranscoder.addTranscodingHint(PNGTranscoder.KEY_PIXEL_UNIT_TO_MILLIMETER, 25.4f / 96f);
//
//        try (InputStream is = new FileInputStream(svgPath);
//             OutputStream os = new FileOutputStream(pngPath)) {
//            TranscoderInput input = new TranscoderInput(is);
//            input.setURI(new File(svgPath).toURI().toString());
//            pngTranscoder.transcode(input, new TranscoderOutput(os));
//        }
//
//        // PNG -> PDF with correct page size
//        com.itextpdf.kernel.geom.PageSize pageSize = new com.itextpdf.kernel.geom.PageSize(w, h);
//        com.itextpdf.kernel.pdf.PdfDocument pdfDoc =
//                new com.itextpdf.kernel.pdf.PdfDocument(
//                        new com.itextpdf.kernel.pdf.PdfWriter(pdfPath));
//        pdfDoc.setDefaultPageSize(pageSize);
//
//        com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDoc, pageSize);
//        document.setMargins(0, 0, 0, 0); // No margins - fill page
//
//        com.itextpdf.io.image.ImageData imgData =
//                com.itextpdf.io.image.ImageDataFactory.create(pngPath);
//        com.itextpdf.layout.element.Image img =
//                new com.itextpdf.layout.element.Image(imgData);
//        img.setFixedPosition(0, 0);
//        img.setWidth(w);
//        img.setHeight(h);
//        document.add(img);
//        document.close();
//
//        new File(pngPath).delete();
//        LOG.info("Converted via PNG->PDF raster fallback (" + w + "x" + h + ")");
//    }
}
