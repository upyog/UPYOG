package org.egov.edcr.service;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.printing.Orientation;
import org.egov.common.entity.edcr.EdcrPdfDetail;
import org.egov.edcr.entity.PdfPageSize;
import org.egov.edcr.utility.DcrConstants;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.kabeja.batik.tools.SAXPDFSerializer;
import org.kabeja.dxf.DXFDocument;
import org.kabeja.xml.SAXSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aspose.cad.Color;
import com.aspose.cad.Image;
import com.aspose.cad.fileformats.cad.CadDrawTypeMode;
import com.aspose.cad.imageoptions.CadRasterizationOptions;
import com.aspose.cad.imageoptions.PdfOptions;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;

/**
 * Single entry point for DXF to PDF: runs either the Kabeja pipeline or Aspose CAD, never both.
 * <p>
 * Configure via app config module {@link DcrConstants#APPLICATION_MODULE_TYPE}, key
 * {@link DcrConstants#DXF_TO_PDF_ENGINE}: {@code KABEJA} (default) or {@code ASPOSE}.
 * Aspose reads the original DXF file from disk ({@code dxfSourceFile}); in-memory layer tweaks
 * from the extract step apply only to the Kabeja path.
 */
@Service
public class DxfToPdfUnifiedConverter {
    private static final Logger LOG = LogManager.getLogger(DxfToPdfUnifiedConverter.class);

    public enum Engine {
        KABEJA,
        ASPOSE
    }

    @Autowired
    private AppConfigValueService appConfigValueService;

    /**
     * Converts a DXF to a PDF using the configured engine.
     * <p>
     * Call from DxfToPdfConverterExtract (or similar) after the extract has populated the
     * <code>edcrPdfDetail</code> argument (page size, output layer or sheet name, and optionally
     * <code>kabejaSinglePageDXFToPdf</code>).
     * <ul>
     *   <li><b>Aspose</b> &mdash; requires <code>dxfSourceFile</code> on disk; loads the full drawing and does not use
     *       in-memory DXF mutations.</li>
     *   <li><b>Kabeja</b> &mdash; uses the in-memory <code>dxfDocument</code>; when <code>kabejaSinglePageDXFToPdf</code>
     *       is true, <code>convertWithKabeja</code> adds extra entries to the Kabeja generator map.</li>
     *   <li><b>Fallback</b> &mdash; if Aspose is selected but the file is missing or conversion throws, Kabeja is used
     *       so a PDF is still produced when possible.</li>
     * </ul>
     *
     * @param dxfSourceFile DXF on disk for Aspose (may be null when only Kabeja is used)
     * @param dxfDocument   parsed DXF for Kabeja
     * @param fileName      logical drawing name for logging
     * @param layerName     stem for the output PDF file (for example basename without extension in direct mode)
     * @param edcrPdfDetail settings built by extract (page size, hatch removal, single-PDF flag)
     * @return generated PDF file, or <code>null</code> on failure
     */
    public File convert(File dxfSourceFile, DXFDocument dxfDocument, String fileName, String layerName,
                        EdcrPdfDetail edcrPdfDetail) {
        Engine engine = resolveEngine();
        if (engine == Engine.ASPOSE) {
            if (dxfSourceFile == null || !dxfSourceFile.isFile()) {
                LOG.warn("DXF_TO_PDF_ENGINE=ASPOSE but DXF source file is missing; using Kabeja.");
                return convertWithKabeja(dxfDocument, fileName, layerName, edcrPdfDetail);
            }
            try {
                return convertWithAspose(dxfSourceFile, layerName, edcrPdfDetail);
            } catch (Exception ex) {
                LOG.error("Aspose DXF→PDF failed for {} sheet {}; falling back to Kabeja.", fileName, layerName, ex);
                return convertWithKabeja(dxfDocument, fileName, layerName, edcrPdfDetail);
            }
        }
        return convertWithKabeja(dxfDocument, fileName, layerName, edcrPdfDetail);
    }

    /**
     * Reads DXF_TO_PDF_ENGINE from Digit DCR app config. Any value other than ASPOSE (case-insensitive) means Kabeja,
     * including when the setting is missing.
     */
    public boolean isAsposeEngine() {
        return resolveEngine() == Engine.ASPOSE;
    }

    private Engine resolveEngine() {
        List<AppConfigValues> vals = appConfigValueService.getConfigValuesByModuleAndKey(
                DcrConstants.APPLICATION_MODULE_TYPE, DcrConstants.DXF_TO_PDF_ENGINE);
        if (vals != null && !vals.isEmpty()) {
            String v = vals.get(0).getValue();
            if (v != null && "ASPOSE".equalsIgnoreCase(v.trim())) {
                return Engine.ASPOSE;
            }
        }
        return Engine.KABEJA;
    }

    /**
     * Runs Kabeja SVG-to-PDF with a property map whose shape depends on legacy vs single full-DXF mode.
     * <p>
     * <b>Legacy multi-sheet PDFs</b> (EdcrPdfDetail.getKabejaSinglePageDXFToPdf() not true):
     * <ul>
     *   <li>Margin <code>0.5</code> and optional hatch stripping only (historical behaviour).</li>
     * </ul>
     * <p>
     * <b>Direct single full-DXF PDF</b> (getKabejaSinglePageDXFToPdf() true):
     * <ul>
     *   <li><code>DcrSvgGenerator.PROPERTY_SINGLE_PDF</code> &mdash; enables DcrSvgGenerator and DcrSvgStyleGenerator
     *       tuning for text, bounds, and fonts without affecting legacy runs.</li>
     *   <li><code>bounds-rule</code> = <code>Modelspace-Limits</code> &mdash; viewport from DXF header so the plan is not a speck.</li>
     *   <li><code>margin</code> = <code>0</code> &mdash; avoids shrinking usable area in this mode.</li>
     *   <li><code>stroke-width</code> = <code>0.12</code> &mdash; thin strokes so dimensions do not render as thick bars.</li>
     * </ul>
     * <p>
     * The output file is <code>layerName + ".pdf"</code>; in direct mode <code>layerName</code> is typically the DXF basename.
     *
     * @param dxfDocument     in-memory DXF for Kabeja
     * @param fileName        used in log messages
     * @param layerName       PDF filename stem
     * @param edcrPdfDetail   page size, hatch flag, and kabejaSinglePageDXFToPdf gate
     * @return written PDF file if non-empty, otherwise <code>null</code>
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private File convertWithKabeja(DXFDocument dxfDocument, String fileName, String layerName,
                                   EdcrPdfDetail edcrPdfDetail) {
        LOG.info("Converting Dxf to Pdf with Kabeja...");
        File fileOut = new File(layerName + ".pdf");
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("---------converting {} - {} to pdf (Kabeja)----------", fileName, layerName);
            }
            try (FileOutputStream fout = new FileOutputStream(fileOut)) {
                DcrSvgGenerator generator = new DcrSvgGenerator();
                SAXSerializer out = new SAXPDFSerializer();
                out.setOutput(fout);
                HashMap map = new HashMap();
                Rectangle rectangle = PageSize.getRectangle(edcrPdfDetail.getPageSize().getSize());
                if (edcrPdfDetail.getPageSize().getOrientation().ordinal() == Orientation.PORTRAIT.ordinal()) {
                    map.put("width", String.valueOf(rectangle.getWidth() * edcrPdfDetail.getPageSize().getEnlarge()));
                    map.put("height", String.valueOf(rectangle.getHeight() * edcrPdfDetail.getPageSize().getEnlarge()));
                } else {
                    map.put("width", String.valueOf(rectangle.getHeight() * edcrPdfDetail.getPageSize().getEnlarge()));
                    map.put("height", String.valueOf(rectangle.getWidth() * edcrPdfDetail.getPageSize().getEnlarge()));
                }
                // Single-PDF: tighter viewBox + thinner strokes (legacy keeps original margin / behaviour).
                boolean directSinglePdf = Boolean.TRUE.equals(edcrPdfDetail.getKabejaSinglePageDXFToPdf());
                if (directSinglePdf) {
                    map.put(DcrSvgGenerator.PROPERTY_SINGLE_PDF, "true");
                    map.put("bounds-rule", "Modelspace-Limits");
                    map.put("margin", String.valueOf(0));
                    // Uniform thin linework (dimension ticks, etc.); avoids heavy DXF lineweights in PDF.
                    map.put("stroke-width", String.valueOf(0.12));
                    if (Boolean.TRUE.equals(edcrPdfDetail.getPageSize().getRemoveHatch())) {
                        map.put("stroke.width", Double.valueOf(0));
                    }
                } else {
                    // Legacy per-sheet path: unchanged from historical Kabeja integration.
                    map.put("margin", String.valueOf(0.5));
                    if (edcrPdfDetail.getPageSize().getRemoveHatch()) {
                        map.put("stroke.width", Double.valueOf(0));
                    }
                }
                generator.generate(dxfDocument, out, map);
                fout.flush();
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("---------conversion success {} - {} (Kabeja)----------", fileName, layerName);
            }
            return fileOut.length() > 0 ? fileOut : null;
        } catch (Exception ep) {
            LOG.error("Pdf convertion failed for {} - {} due to {}", fileName, layerName, ep.getMessage());
            ep.printStackTrace();
            edcrPdfDetail.setFailureReasons(ep.getMessage());
            if (fileOut.exists()) {
                fileOut.delete();
            }
        }
        return null;
    }

    /**
     * Converts a DXF drawing file into a PDF document using the Aspose imaging library.
     *
     * <p>
     * The conversion process rasterizes the CAD drawing and exports it as a PDF
     * using configurable page dimensions derived from the provided
     * {@link EdcrPdfDetail}. If no valid page configuration is available,
     * default dimensions are used.
     * </p>
     *
     * <p>
     * Page size handling:
     * </p>
     * <ul>
     *   <li>Reads page size, orientation, and enlargement factor from
     *       {@link PdfPageSize}.</li>
     *   <li>Supports portrait and landscape orientation.</li>
     *   <li>Falls back to default dimensions when page configuration is invalid
     *       or unavailable.</li>
     * </ul>
     *
     * <p>
     * Aspose CAD rasterization settings:
     * </p>
     * <ul>
     *   <li>White background color</li>
     *   <li>Uses original object colors from the DXF</li>
     *   <li>Automatic layout scaling enabled</li>
     *   <li>Scaling adjustments allowed</li>
     * </ul>
     *
     * <p>
     * The generated PDF file is created in the working directory with the
     * provided layer name as the file name.
     * </p>
     *
     * @param dxfSourceFile the source DXF file to be converted
     * @param layerName the logical layer or output file name prefix used for the generated PDF
     * @param edcrPdfDetail PDF configuration details containing page size, orientation,
     *         and enlargement settings
     * @return the generated PDF file if conversion succeeds and the file exists
     *         with non-zero size; otherwise {@code null}
     * @throws RuntimeException
     *         if unexpected errors occur during PDF generation
     */
    private File convertWithAspose(File dxfSourceFile, String layerName, EdcrPdfDetail edcrPdfDetail) {
        LOG.info("Converting Dxf to Pdf with Aspose...");

        File fileOut = new File(layerName + ".pdf");
        float pageWidth = 3370f;
        float pageHeight = 2384f;
        PdfPageSize ps = edcrPdfDetail.getPageSize();
        if (ps != null && ps.getSize() != null) {
            try {
                Rectangle rectangle = PageSize.getRectangle(ps.getSize());
                int enlarge = ps.getEnlarge() > 0 ? ps.getEnlarge() : 1;
                if (ps.getOrientation() != null && ps.getOrientation() == Orientation.PORTRAIT) {
                    pageWidth = rectangle.getWidth() * enlarge;
                    pageHeight = rectangle.getHeight() * enlarge;
                } else {
                    pageWidth = rectangle.getHeight() * enlarge;
                    pageHeight = rectangle.getWidth() * enlarge;
                }
            } catch (RuntimeException ex) {
                LOG.debug("Using default Aspose page size: {}", ex.getMessage());
            }
        }
        Image image = Image.load(dxfSourceFile.getAbsolutePath());
        try {
            PdfOptions pdfOptions = new PdfOptions();
            CadRasterizationOptions rasterizationOptions = new CadRasterizationOptions();
            rasterizationOptions.setBackgroundColor(Color.getWhite());
            rasterizationOptions.setDrawType(CadDrawTypeMode.UseObjectColor);
            rasterizationOptions.setPageWidth(pageWidth);
            rasterizationOptions.setPageHeight(pageHeight);
            rasterizationOptions.setAutomaticLayoutsScaling(true);
            rasterizationOptions.setNoScaling(false);
            pdfOptions.setVectorRasterizationOptions(rasterizationOptions);
            image.save(fileOut.getAbsolutePath(), pdfOptions);
        } finally {
            image.dispose();
        }
        return fileOut.exists() && fileOut.length() > 0 ? fileOut : null;
    }
}