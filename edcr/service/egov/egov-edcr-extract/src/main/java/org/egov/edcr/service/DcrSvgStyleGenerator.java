package org.egov.edcr.service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.kabeja.dxf.DXFStyle;
import org.kabeja.svg.SVGUtils;
import org.kabeja.tools.FontManager;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class DcrSvgStyleGenerator {

    /** First existing file in this list gets applied when Kabeja has no font mapping (direct single-PDF path only). */
    private static final String[] FONT_FALLBACK_PATHS = new String[] {
            "/usr/share/fonts/truetype/msttcorefonts/Times_New_Roman.ttf",
            "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
            "/usr/share/fonts/truetype/liberation/LiberationSans-Regular.ttf",
            "/usr/share/fonts/truetype/noto/NotoSans-Regular.ttf"
    };

    /** Default constructor (used as a Spring bean where wired). */
    public DcrSvgStyleGenerator() {
    }

    /**
     * Writes the given DXF style information as SAX events to the provided
     * {@link org.xml.sax.ContentHandler}.
     *
     * <p>
     * This method acts as a dispatcher between two SVG generation strategies:
     * </p>
     *
     * <ul>
     *   <li>
     *     <b>Single PDF mode</b> - Uses the optimized SAX generation flow
     *     intended for unified PDF generation when the context flag
     *     {@code egov.singlePdfUsingKabeja} is enabled.
     *   </li>
     *   <li>
     *     <b>Legacy mode</b> - Uses the older SAX conversion implementation
     *     for backward compatibility.
     *   </li>
     * </ul>
     *
     * <p>
     * The selection is controlled through the {@code svgContext} map:
     * </p>
     *
     * <pre>
     * egov.singlePdfUsingKabeja = true
     * </pre>
     *
     * <p>
     * If the flag is set to {@link Boolean#TRUE}, the method delegates to
     * {@code toSaxSinglePdf(...)}; otherwise it falls back to
     * {@code toSaxLegacy(...)}.
     * </p>
     *
     * @param handler the SAX content handler that receives generated XML/SVG events
     * @param svgContext context map containing SVG rendering configuration and feature flags
     * @param style the DXF style definition to be converted into SAX events
     * @throws SAXException if an error occurs while emitting SAX events
     */
    public static void toSAX(ContentHandler handler, Map svgContext, DXFStyle style) throws SAXException {
        if (Boolean.TRUE.equals(svgContext.get("egov.singlePdfUsingKabeja"))) {
            toSaxSinglePdf(handler, style);
        } else {
            toSaxLegacy(handler, style);
        }
    }

    /** Original behaviour — unchanged for legacy multi-sheet Kabeja PDFs. */
    private static void toSaxLegacy(ContentHandler handler, DXFStyle style) throws SAXException {
        FontManager manager = FontManager.getInstance();
        if (manager.hasFontDescription(style.getBigFontFile())) {
            generateSaxFontDescriptionLegacy(handler, style.getBigFontFile());
        } else if (manager.hasFontDescription(style.getFontFile())) {
            generateSaxFontDescriptionLegacy(handler, style.getFontFile());
        }
    }

    /**
     * Generates legacy SVG font-face definitions for SAX-based SVG/PDF rendering.
     *
     * <p>This method creates SVG {@code font-face} and {@code font-face-src}
     * elements and writes them through the provided SAX {@link ContentHandler}.</p>
     *
     * <p>The method normalizes the supplied font name by removing the
     * {@code .shx} extension (if present) and uses the resulting value
     * as the SVG font-family name.</p>
     *
     * <p>A font-face URI is then mapped to a fallback TrueType font
     * ({@code Times_New_Roman.ttf}) located in the system font directory.</p>
     *
     * <p>This is mainly used to improve compatibility for DXF SHX fonts
     * during SVG to PDF conversion and avoid unsupported font rendering issues.</p>
     *
     * <p>Generated SVG structure:</p>
     * <pre>
     * {@code
     * <font-face font-family="romans" font-face="Regular">
     *     <font-face-src>
     *         <font-face-uri xlink:href="file:///.../Times_New_Roman.ttf#romans"/>
     *     </font-face-src>
     * </font-face>
     * }
     * </pre>
     *
     * @param handler the SAX content handler used to write SVG elements
     * @param font the DXF font name or SHX font file name
     * @throws SAXException if an error occurs while generating SAX events
     */
    private static void generateSaxFontDescriptionLegacy(ContentHandler handler, String font) throws SAXException {
        String fontFamily = font.toLowerCase();
        if (fontFamily.endsWith(".shx")) {
            fontFamily = fontFamily.substring(0, fontFamily.indexOf(".shx"));
        }
        AttributesImpl attr = new AttributesImpl();

        SVGUtils.addAttribute(attr, "font-family", fontFamily);
        SVGUtils.addAttribute(attr, "font-face", "Regular");
        SVGUtils.startElement(handler, "font-face", attr);
        attr = new AttributesImpl();
        SVGUtils.startElement(handler, "font-face-src", attr);
        attr = new AttributesImpl();

        String url = "file:///usr/share/fonts/truetype/msttcorefonts/Times_New_Roman.ttf" + "#" + fontFamily;
        attr.addAttribute("", "", "xmlns:xlink", "CDATA", "http://www.w3.org/1999/xlink");
        attr.addAttribute("http://www.w3.org/1999/xlink", "href", "xlink:href", "CDATA", url);
        SVGUtils.emptyElement(handler, "font-face-uri", attr);
        SVGUtils.endElement(handler, "font-face-src");
        SVGUtils.endElement(handler, "font-face");
    }

    /**
     * Generates SAX-based font definitions for single PDF rendering using
     * the most suitable DXF style font available.
     *
     * <p>The method collects possible font aliases from the DXF style,
     * including:</p>
     * <ul>
     *     <li>Style name</li>
     *     <li>Primary font file</li>
     *     <li>Big font file</li>
     * </ul>
     *
     * <p>It then checks the {@link FontManager} for available font
     * descriptions in the following priority order:</p>
     * <ol>
     *     <li>Big font file</li>
     *     <li>Primary font file</li>
     *     <li>Style name</li>
     * </ol>
     *
     * <p>Once a matching font description is found, the method generates
     * the corresponding SVG/SAX font-face definition for PDF rendering.</p>
     *
     * <p>This logic helps improve font compatibility and fallback handling
     * during DXF to PDF conversion, especially for SHX and custom DXF fonts.</p>
     *
     * @param handler the SAX content handler used to write SVG font elements
     * @param style the DXF text style containing font configuration details
     * @throws SAXException if an error occurs while generating SAX events
     */
    private static void toSaxSinglePdf(ContentHandler handler, DXFStyle style) throws SAXException {
        FontManager manager = FontManager.getInstance();
        Set<String> aliases = new LinkedHashSet<>();
        addAlias(aliases, style.getName());
        addAlias(aliases, style.getFontFile());
        addAlias(aliases, style.getBigFontFile());

        if (manager.hasFontDescription(style.getBigFontFile())) {
            generateSaxFontDescriptionDirect(handler, style.getBigFontFile(), aliases);
        } else if (manager.hasFontDescription(style.getFontFile())) {
            generateSaxFontDescriptionDirect(handler, style.getFontFile(), aliases);
        } else {
            generateSaxFontDescriptionDirect(handler, style.getName(), aliases);
        }
    }

    /**
     * Generates SVG/SAX font-face definitions for direct single PDF rendering
     * using the resolved system font path and all available font aliases.
     *
     * <p>The method normalizes the provided font name and resolves the
     * corresponding physical font file path from the system.</p>
     *
     * <p>It then builds a collection of font-family aliases including:</p>
     * <ul>
     *     <li>The primary font family</li>
     *     <li>Lowercase variant of the primary family</li>
     *     <li>All supplied aliases</li>
     *     <li>Lowercase variants of aliases</li>
     * </ul>
     *
     * <p>For each resolved family name, the method generates SVG
     * {@code font-face} definitions containing:</p>
     * <ul>
     *     <li>font-family</li>
     *     <li>font-style</li>
     *     <li>font-weight</li>
     *     <li>font-face-src</li>
     *     <li>font-face-uri</li>
     * </ul>
     *
     * <p>The generated font-face URI points to the resolved font file
     * using a {@code file://} URL reference.</p>
     *
     * <p>This mechanism improves font fallback handling and ensures
     * consistent text rendering during DXF to PDF conversion,
     * especially for SHX/custom DXF fonts.</p>
     *
     * @param handler the SAX content handler used to write SVG font elements
     * @param font the primary DXF font name
     * @param aliases additional font aliases associated with the DXF style
     * @throws SAXException if an error occurs while generating SAX events
     */
    private static void generateSaxFontDescriptionDirect(ContentHandler handler, String font, Set<String> aliases)
            throws SAXException {
        String primaryFamily = normalizeFamily(font);
        String resolvedPath = resolveFontPath(primaryFamily);
        Set<String> families = new LinkedHashSet<>();
        families.add(primaryFamily);
        families.add(primaryFamily.toLowerCase());
        for (String alias : aliases) {
            String family = normalizeFamily(alias);
            if (!family.isEmpty()) {
                families.add(family);
                families.add(family.toLowerCase());
            }
        }

        for (String family : families) {
            AttributesImpl attr = new AttributesImpl();
            SVGUtils.addAttribute(attr, "font-family", family);
            SVGUtils.addAttribute(attr, "font-style", "normal");
            SVGUtils.addAttribute(attr, "font-weight", "normal");
            SVGUtils.startElement(handler, "font-face", attr);
            attr = new AttributesImpl();
            SVGUtils.startElement(handler, "font-face-src", attr);
            attr = new AttributesImpl();

            String url = "file://" + resolvedPath + "#" + family;
            attr.addAttribute("", "", "xmlns:xlink", "CDATA", "http://www.w3.org/1999/xlink");
            attr.addAttribute("http://www.w3.org/1999/xlink", "href", "xlink:href", "CDATA", url);
            SVGUtils.emptyElement(handler, "font-face-uri", attr);
            SVGUtils.endElement(handler, "font-face-src");
            SVGUtils.endElement(handler, "font-face");
        }
    }

    /**
     * Resolves the physical font file path for the given font family.
     *
     * <p>The method first attempts to retrieve the font description/path
     * from the Kabeja {@link FontManager} configuration.</p>
     *
     * <p>If no valid font mapping is found, it falls back to a predefined
     * list of system font paths and returns the first existing font file.</p>
     *
     * <p>If none of the fallback font paths exist, the method returns
     * the first configured fallback path as the default value.</p>
     *
     * <p>This logic is used during DXF to PDF conversion to ensure that
     * text rendering always has a usable font file, even when the original
     * DXF font is unsupported or unavailable.</p>
     *
     * @param fontFamily the DXF font family name to resolve
     * @return the resolved physical font file path
     */
    private static String resolveFontPath(String fontFamily) {
        String fromKabeja = FontManager.getInstance().getFontDescription(fontFamily);
        if (fromKabeja != null && !fromKabeja.trim().isEmpty()) {
            return fromKabeja;
        }
        for (String candidate : FONT_FALLBACK_PATHS) {
            if (Files.exists(Paths.get(candidate))) {
                return candidate;
            }
        }
        return FONT_FALLBACK_PATHS[0];
    }

    /**
     * Adds font alias values to the provided alias collection.
     *
     * <p>The method validates the supplied font reference and adds:</p>
     * <ul>
     *     <li>The original trimmed font reference</li>
     *     <li>The normalized font name with the {@code .shx} extension removed</li>
     * </ul>
     *
     * <p>This helps improve font matching and fallback resolution during
     * DXF to PDF conversion by supporting both raw SHX font names and
     * normalized font-family names.</p>
     *
     * <p>Example:</p>
     * <pre>
     * {@code
     * Input  : "romans.shx"
     * Aliases: ["romans.shx", "romans"]
     * }
     * </pre>
     *
     * @param aliases the collection used to store font aliases
     * @param fontRef the DXF font reference or SHX font file name
     */
    private static void addAlias(Set<String> aliases, String fontRef) {
        if (fontRef == null || fontRef.trim().isEmpty()) {
            return;
        }
        aliases.add(fontRef.trim());
        String stripped = stripShx(fontRef);
        if (!stripped.isEmpty()) {
            aliases.add(stripped);
        }
    }

    /**
     * Normalizes a DXF SHX font reference by extracting the font name
     * and removing the {@code .shx} extension if present.
     *
     * <p>The method performs the following processing:</p>
     * <ul>
     *     <li>Returns an empty string for null input</li>
     *     <li>Trims surrounding whitespace</li>
     *     <li>Removes directory path information if present</li>
     *     <li>Removes the {@code .shx} extension (case-insensitive)</li>
     * </ul>
     *
     * <p>Examples:</p>
     * <pre>
     * {@code
     * "romans.shx"                -> "romans"
     * "C:/fonts/romans.shx"       -> "romans"
     * "C:\\fonts\\romans.SHX"     -> "romans"
     * "arial.ttf"                 -> "arial.ttf"
     * }
     * </pre>
     *
     * <p>This normalization helps improve font matching and fallback
     * handling during DXF to PDF conversion.</p>
     *
     * @param fontRef the DXF font reference or file path
     * @return the normalized font name without path or SHX extension
     */
    private static String stripShx(String fontRef) {
        if (fontRef == null) {
            return "";
        }
        String value = fontRef.trim();
        int slash = Math.max(value.lastIndexOf('/'), value.lastIndexOf('\\'));
        if (slash >= 0 && slash < value.length() - 1) {
            value = value.substring(slash + 1);
        }
        String lower = value.toLowerCase();
        if (lower.endsWith(".shx")) {
            return value.substring(0, value.length() - 4);
        }
        return value;
    }

    /**
     * Normalizes a DXF font family name for SVG/PDF rendering.
     *
     * <p>The method removes any SHX extension and path information
     * from the supplied font reference using {@link #stripShx(String)}.</p>
     *
     * <p>If the normalized value is empty, the method falls back to
     * the default font family {@code "romans"}.</p>
     *
     * <p>This normalization ensures consistent font-family handling
     * and provides a safe fallback for unsupported or invalid DXF fonts
     * during DXF to PDF conversion.</p>
     *
     * @param font the DXF font reference or file name
     * @return the normalized font family name, or {@code "romans"}
     *         if the input resolves to an empty value
     */
    private static String normalizeFamily(String font) {
        String normalized = stripShx(font);
        return normalized.isEmpty() ? "romans" : normalized;
    }
}
