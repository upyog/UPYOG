package org.egov.edcr.service;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.kabeja.dxf.Bounds;
import org.kabeja.dxf.DXFBlock;
import org.kabeja.dxf.DXFColor;
import org.kabeja.dxf.DXFConstants;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.DXFLayer;
import org.kabeja.dxf.DXFLineType;
import org.kabeja.dxf.DXFMText;
import org.kabeja.dxf.DXFStyle;
import org.kabeja.dxf.DXFText;
import org.kabeja.dxf.DXFVariable;
import org.kabeja.dxf.objects.DXFDictionary;
import org.kabeja.dxf.objects.DXFLayout;
import org.kabeja.math.TransformContext;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGContext;
import org.kabeja.svg.SVGGenerationException;
import org.kabeja.svg.SVGSAXGenerator;
import org.kabeja.svg.SVGSAXGeneratorManager;
import org.kabeja.svg.SVGUtils;
import org.kabeja.svg.generators.SVGStyleGenerator;
import org.kabeja.xml.AbstractSAXGenerator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Extends Kabeja's AbstractSAXGenerator and streams a DXF document out as SVG SAX events.
 * EDCR adds an optional mode for one full-drawing PDF: when the generator map contains PROPERTY_SINGLE_PDF (set from
 * DxfToPdfUnifiedConverter), extra tuning runs. Legacy multi-sheet runs never pass that key.
 */
public class DcrSvgGenerator extends AbstractSAXGenerator {
   public final static String PROPERTY_MARGIN = "margin";
   public final static String PROPERTY_STROKE_WIDTH = "stroke-width";
   public final static String PROPERTY_DOCUMENT_BOUNDS = "useBounds";

   /**
    * This property defines the way of calculation/setup the bounds of the
    * Document. Possible values are:
    * <ul>
    * <li>kabeja: Bounds calculate on the geometries. (default)</li>
    * <li>paperspace: extracts the values of the limits from paperspace</li>
    * <li>modelspace: extracts the values of the limits from modelspace</li>
    * </ul>
    */
   public final static String PROPERTY_DOCUMENT_BOUNDS_RULE = "bounds-rule";
   public final static int PROPERTY_DOCUMENT_BOUNDS_RULE_KABEJA = 1;
   public final static int PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE = 2;
   public final static int PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE = 3;
   public final static String PROPERTY_DOCUMENT_BOUNDS_RULE_KABEJA_VALUE = "kabeja";
   public final static String PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE_VALUE = "Paperspace";
   public final static String PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE_LIMITS_VALUE =
       "Paperspace-Limits";
   public final static String PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE_VALUE = "Modelspace";
   public final static String PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE_LIMITS_VALUE =
       "Modelspace-Limits";
   public final static String PROPERTY_DOCUMENT_OUTPUT_STYLE = "output-style";
   public final static int PROPERTY_DOCUMENT_OUTPUT_STYLE_NOLAYOUT = 0;
   public final static int PROPERTY_DOCUMENT_OUTPUT_STYLE_LAYOUT = 1;
   public final static int PROPERTY_DOCUMENT_OUTPUT_STYLE_PLOTSETTING = 2;
   public final static String PROPERTY_DOCUMENT_OUTPUT_STYLE_LAYOUT_VALUE = "layout";
   public final static String PROPERTY_DOCUMENT_OUTPUT_STYLE_PLOTSETTING_VALUE = "plotsetting";
   public final static String PROPERTY_DOCUMENT_OUTPUT_STYLE_NAME = "output-style-name";
   public final static String PROPERTY_WIDTH = "width";
   public final static String PROPERTY_HEIGHT = "height";
   public final static String PROPERTY_OVERFLOW = "svg-overflow";
   /**
    * Map key DxfToPdfUnifiedConverter sets only when EdcrPdfDetail.getKabejaSinglePageDXFToPdf() is true.
    * Pass the string "true" as the value. setupProperties copies intent into the shared context as the Boolean
    * egov.singlePdfUsingKabeja so DcrSvgStyleGenerator can pick the direct Single PDF font path.
    */
   public static final String PROPERTY_SINGLE_PDF = "egov.singlePdfUsingKabeja";
   public static final double DEFAULT_MARGIN_PERCENT = 0.0;
   public final static String SUPPORTED_SVG_VERSION = "1.1"; // we say we produce version 1.1 to fool svgo
   private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(DcrSvgGenerator.class);
   private boolean overflow = true;
   private boolean useLimits = false;
   private int boundsRule = PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE;
   private int outputStyle = PROPERTY_DOCUMENT_OUTPUT_STYLE_NOLAYOUT;
   private String marginSettings;
   private String outputStyleName = DXFConstants.LAYOUT_DEFAULT_NAME;
   protected SVGSAXGeneratorManager manager;
   /**
    * When true: extra SVG/CSS and per-entity handling for one full-drawing PDF (setupProperties, getBounds, layerToSAX,
    * singlePdfConfigurations). When false: same behaviour as the original Kabeja-only path for legacy multi-sheet PDFs.
    */
   private boolean singlePdfMode;

   protected void generate() throws SAXException {
       this.setupProperties();
       this.generateSAX();
       this.context = null;
   }

   protected void setupProperties() {
       if (this.context == null) {
           this.context = new HashMap();
       } else {
           //copy setup from context to 
           //properties
           Iterator i = this.context.keySet().iterator();

           while (i.hasNext()) {
               String key = (String) i.next();
               this.properties.put(key, this.context.get(key));
           }
       }

       // setup the properties

       // the margin
       if (this.properties.containsKey(PROPERTY_MARGIN)) {
           this.marginSettings = (String) this.properties.get(PROPERTY_MARGIN);
       }

       if (this.properties.containsKey(PROPERTY_OVERFLOW)) {
           this.overflow = Boolean.valueOf((String) this.properties.get(
                       PROPERTY_OVERFLOW)).booleanValue();
       }

       if (this.properties.containsKey(PROPERTY_STROKE_WIDTH)) {
           this.context.put(SVGContext.STROKE_WIDTH,
               new Double(this.properties.get(PROPERTY_STROKE_WIDTH).toString()));
           // set to ignore the draft stroke width
           this.context.put(SVGContext.STROKE_WIDTH_IGNORE, StringUtils.EMPTY);
       }

       if (this.properties.containsKey(PROPERTY_DOCUMENT_BOUNDS_RULE)) {
           String value = ((String) this.properties.get(PROPERTY_DOCUMENT_BOUNDS_RULE)).trim();

           if (value.equals(PROPERTY_DOCUMENT_BOUNDS_RULE_KABEJA_VALUE)) {
               //the new default is modelspace now
               this.boundsRule = PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE;
               this.useLimits = false;
           } else if (value.equals(
                       PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE_VALUE)) {
               this.boundsRule = PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE;
               this.useLimits = false;
           } else if (value.equals(
                       PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE_VALUE)) {
               this.boundsRule = PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE;
               this.useLimits = false;
           } else if (value.equals(
                       PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE_LIMITS_VALUE)) {
               this.boundsRule = PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE;
               this.useLimits = true;
           } else if (value.equals(
                       PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE_LIMITS_VALUE)) {
               this.boundsRule = PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE;
               this.useLimits = true;
           }
       }

       if (this.properties.containsKey(PROPERTY_DOCUMENT_OUTPUT_STYLE)) {
           String value = ((String) this.properties
                           .get(PROPERTY_DOCUMENT_OUTPUT_STYLE)).trim()
                           .toLowerCase();

           if (value.equals(PROPERTY_DOCUMENT_OUTPUT_STYLE_LAYOUT_VALUE)) {
               this.outputStyle = PROPERTY_DOCUMENT_OUTPUT_STYLE_LAYOUT;
           } else if (value.equals(
                       PROPERTY_DOCUMENT_OUTPUT_STYLE_PLOTSETTING_VALUE)) {
               this.outputStyle = PROPERTY_DOCUMENT_OUTPUT_STYLE_PLOTSETTING;
           }

           if (this.properties.containsKey(PROPERTY_DOCUMENT_OUTPUT_STYLE_NAME)) {
               this.outputStyleName = ((String) this.properties.get(PROPERTY_DOCUMENT_OUTPUT_STYLE_NAME)).trim();
           }
       }

       if (this.manager == null) {
           this.manager = new SVGSAXGeneratorManager();
       }

       /**
        * Copy single-PDF intent into Kabeja's shared context map so DcrSvgStyleGenerator.toSAX can branch without
        * changing Kabeja's public APIs.
        */
       String singlePdf = this.properties.containsKey(PROPERTY_SINGLE_PDF)
               ? String.valueOf(this.properties.get(PROPERTY_SINGLE_PDF)).trim()
               : "";
       this.singlePdfMode = "true".equalsIgnoreCase(singlePdf);
       if (this.singlePdfMode) {
           this.context.put("egov.singlePdfUsingKabeja", Boolean.TRUE);
       }

       this.context.put(SVGContext.SVGSAXGENERATOR_MANAGER, manager);
   }

   private void generateSAX() throws SAXException {
       try {
           this.handler.startDocument();

           AttributesImpl attr = new AttributesImpl();

           String viewport = StringUtils.EMPTY;
           Bounds bounds = this.getBounds();

           // set the height and width from properties or layout settings
           if (this.outputStyle == PROPERTY_DOCUMENT_OUTPUT_STYLE_NOLAYOUT) {
               if (this.properties.containsKey(PROPERTY_WIDTH)) {
                   SVGUtils.addAttribute(attr,
                       SVGConstants.SVG_ATTRIBUTE_WIDTH,
                       (String) this.properties.get(PROPERTY_WIDTH));
               }

               if (this.properties.containsKey(PROPERTY_HEIGHT)) {
                   SVGUtils.addAttribute(attr,
                       SVGConstants.SVG_ATTRIBUTE_HEIGHT,
                       (String) this.properties.get(PROPERTY_HEIGHT));
               }
           } else if (this.outputStyle == PROPERTY_DOCUMENT_OUTPUT_STYLE_LAYOUT) {
               // check for a layout and get the papersize
               DXFDictionary dict = (DXFDictionary) this.doc.getRootDXFDictionary()
                                                            .getDXFObjectByName(DXFConstants.DICTIONARY_KEY_LAYOUT);

               if (dict != null) {
                   DXFLayout layout = (DXFLayout) dict.getDXFObjectByName(this.outputStyleName);

                   if (layout != null) {
                       Bounds paper = layout.getLimits();

                       // get the units of the paper
                       String units = StringUtils.EMPTY;

                       switch (layout.getPaperUnit()) {
                       case DXFConstants.PAPER_UNIT_INCH:
                           units = "in";

                           break;

                       case DXFConstants.PAPER_UNIT_MILLIMETER:
                           units = "mm";

                           break;

                       case DXFConstants.PAPER_UNIT_PIXEL:
                           units = "px";

                           break;
                       }

                       if (paper.isValid() && (paper.getWidth() > 0) &&
                               (paper.getHeight() > 0)) {
                           SVGUtils.addAttribute(attr,
                               SVGConstants.SVG_ATTRIBUTE_HEIGHT,
                               paper.getHeight() + units);
                           SVGUtils.addAttribute(attr,
                               SVGConstants.SVG_ATTRIBUTE_WIDTH,
                               paper.getWidth() + units);
                       }

                       // check for the bounds
                       // Note this value could be false
                       Bounds b = layout.getExtent();

                       if (b.isValid() && (b.getWidth() > 0) &&
                               (b.getHeight() > 0)) {
                           bounds = b;
                       }
                   }
               }
           }

           // add the viewport
           // with margin
           // this is important otherwise in most cases
           // the SVG-Viewer will not show the content
           viewport = SVGUtils.formatNumberAttribute(bounds.getMinimumX()) +
               " " +
               SVGUtils.formatNumberAttribute((-1 * bounds.getMaximumY())) +
               "  " + SVGUtils.formatNumberAttribute(bounds.getWidth()) + " " +
               SVGUtils.formatNumberAttribute(bounds.getHeight());

           SVGUtils.addAttribute(attr, "viewBox", viewport);

           // set the default namespace
           SVGUtils.addAttribute(attr, "xmlns", SVGConstants.SVG_NAMESPACE);

           // the version of SVG we generate now
           SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_VERSION,
               SUPPORTED_SVG_VERSION);

           // the overflow
           if (this.overflow) {
               SVGUtils.addAttribute(attr,
                   SVGConstants.SVG_ATTRIBUTE_OVERFLOW,
                   SVGConstants.SVG_ATTRIBUTEVALUE_VISIBLE);
           }

           SVGUtils.startElement(this.handler, SVGConstants.SVG_ROOT, attr);

           /*
            * Inject global CSS for text nodes so dimension labels inherit fill/stroke suitable for legible glyphs
            * (layer groups often set stroke-only drawing style which makes plain text unreadable).
            */
           if (this.singlePdfMode) {
                singlePdfConfigurations();
           }

           // the blocks as symbol in the defs-section of SVG
           attr = new AttributesImpl();
           SVGUtils.startElement(this.handler, SVGConstants.SVG_DEFS, attr);

           // set the context
           context.put(SVGContext.DRAFT_BOUNDS, bounds);

           double dotLength = 0.0;

           if (bounds.getWidth() > bounds.getHeight()) {
               dotLength = bounds.getHeight() * SVGConstants.DEFAULT_STROKE_WIDTH_PERCENT;
           } else {
               dotLength = bounds.getWidth() * SVGConstants.DEFAULT_STROKE_WIDTH_PERCENT;
           }

           context.put(SVGContext.DOT_LENGTH, new Double(dotLength));

           Iterator i = this.doc.getDXFBlockIterator();

           while (i.hasNext()) {
               DXFBlock block = (DXFBlock) i.next();
               this.blockToSAX(block, null);
           }

           // maybe there is a fontdescription available from DXFStyle
           i = this.doc.getDXFStyleIterator();

           while (i.hasNext()) {
               DXFStyle style = (DXFStyle) i.next();
               DcrSvgStyleGenerator.toSAX(handler, context, style);
           }

           SVGUtils.endElement(handler, SVGConstants.SVG_DEFS);

           // the draft
           attr = new AttributesImpl();
           SVGUtils.addAttribute(attr, SVGConstants.XML_ID, "draft");

           // the globale coordinate system transformation
           // note: DXF has the y-axis positiv from bottom to top
           // SVG has the y-axis positiv from top to bottom
           SVGUtils.addAttribute(attr, "transform", "matrix(1 0 0 -1 0 0)");

           // the stroke-width
           if (this.context.containsKey(SVGContext.STROKE_WIDTH)) {
               // the user has setup a stroke-width
               SVGUtils.addAttribute(attr,
                   SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH,
                   this.context.get(SVGContext.STROKE_WIDTH).toString());
           } else {
               double sw = (bounds.getWidth() + bounds.getHeight()) / 2 * SVGConstants.DEFAULT_STROKE_WIDTH_PERCENT;
               double defaultSW = ((double) DXFConstants.ENVIRONMENT_VARIABLE_LWDEFAULT) / 100.0;

               if (sw > defaultSW) {
                   sw = defaultSW;
               }

               SVGUtils.addAttribute(attr,
                   SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH,
                   SVGUtils.formatNumberAttribute(sw));
               this.context.put(SVGContext.STROKE_WIDTH, new Double(sw));
           }

           SVGUtils.startElement(handler, SVGConstants.SVG_GROUP, attr);

           // the layers as container g-elements
           i = this.doc.getDXFLayerIterator();

           while (i.hasNext()) {
               DXFLayer layer = (DXFLayer) i.next();

               if (this.boundsRule == PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE) {
                   //out put only the paper space maybe with views to 
                   //model space
                   this.layerToSAX(layer, false);
               } else {
                   //output only the model space -> the default
                   this.layerToSAX(layer, true);
               }
           }

           SVGUtils.endElement(handler, SVGConstants.SVG_GROUP);
           SVGUtils.endElement(handler, SVGConstants.SVG_ROOT);
           handler.endDocument();
       } catch (SAXException e) {
           e.printStackTrace();
       }
   }

   protected void blockToSAX(DXFBlock block, TransformContext transformContext)
       throws SAXException {
       AttributesImpl attr = new AttributesImpl();
       SVGUtils.addAttribute(attr, SVGConstants.XML_ID,
           SVGUtils.validateID(block.getName()));

       SVGUtils.startElement(handler, SVGConstants.SVG_GROUP, attr);

       Iterator i = block.getDXFEntitiesIterator();

        while (i.hasNext()) {
            DXFEntity entity = (DXFEntity) i.next();

            if (this.singlePdfMode) {
                String type = entity.getType();
                /*
                  FIX: Strip styling values from text objects to prevent Kabeja rendering anomalies.
                  1. Reset lineweight to -1 to prevent thick, unreadable texts.
                  2. Set thickness to 0 to prevent 3D extrusion rendering errors in 2D PDF output.
                  3. Reset Aligned (3) and Fit (5) text alignments to standard Left (0) alignment if they
                     have malformed/zeroed alignment points, avoiding vertical/horizontal text stretching.
                 */
                if (org.kabeja.dxf.DXFConstants.ENTITY_TYPE_TEXT.equals(type) || org.kabeja.dxf.DXFConstants.ENTITY_TYPE_MTEXT.equals(type)) {
                    entity.setLineWeight(-1);
                    if (entity instanceof org.kabeja.dxf.DXFText) {
                        org.kabeja.dxf.DXFText text = (org.kabeja.dxf.DXFText) entity;
                        text.setThickness(0d);
                        if (text.getAlign() == 3 || text.getAlign() == 5) {
                            text.setAlign(0);
                        }
                    } else if (entity instanceof org.kabeja.dxf.DXFMText) {
                        ((org.kabeja.dxf.DXFMText) entity).setThickness(0d);
                    }
                }
            }

            try {
                SVGSAXGenerator gen = manager.getSVGGenerator(entity.getType());
                gen.toSAX(handler, this.context, entity, transformContext);
            } catch (SVGGenerationException e) {
                e.printStackTrace();
            }
        }

       SVGUtils.endElement(handler, SVGConstants.SVG_GROUP);
   }

    /*
     * Adds SVG style configuration for single PDF generation.
     *
     * This method injects a CSS <style> block into the generated SVG output
     * before it is converted into PDF.
     *
     * Applied CSS rules:
     * - Removes stroke/border rendering from text elements.
     * - Forces text fill color to use the current drawing color.
     * - Normalizes font weight to avoid bold rendering issues.
     *
     * Affects:
     * - <text> elements
     * - <tspan> elements
     *
     * This configuration helps:
     * - improve text readability in generated PDFs
     * - avoid incorrect text outlines/strokes
     * - maintain consistent font rendering across PDF viewers
     *
     * The generated CSS:
     * text,tspan{
     *     stroke:none !important;
     *     fill:currentColor !important;
     *     font-weight:normal;
     * }
     *
     * Throws:
     * SAXException - if an error occurs while writing SVG SAX events.
     */
   public void singlePdfConfigurations() throws SAXException {
       AttributesImpl styleAttr = new AttributesImpl();
       SVGUtils.addAttribute(styleAttr, "type", "text/css");
       SVGUtils.startElement(handler, "style", styleAttr);
       String css = "text,tspan{stroke:none !important;fill:currentColor !important;font-weight:normal;}";
       char[] cssChars = css.toCharArray();
       handler.characters(cssChars, 0, cssChars.length);
       SVGUtils.endElement(handler, "style");
   }

   /**
    * Returns the margin-array where:
    * <ul>
    * <li>0 ->top margin</li>
    * <li>1 ->right margin</li>
    * <li>2 ->bottom margin</li>
    * <li>3 ->left margin</li>
    * </ul>
    *
    * @param bounds
    * @return
    */
   protected double[] getMargin(Bounds bounds) {
       double[] margin = new double[4];

       if (this.marginSettings != null) {
           StringTokenizer st = new StringTokenizer(this.marginSettings);
           int count = st.countTokens();

           switch (count) {
           case 4:

               for (int i = 0; i < count; i++) {
                   String m = st.nextToken().trim();

                   if (m.endsWith("%")) {
                       m = m.substring(0, m.length() - 1);

                       if ((i == 0) && (i == 2)) {
                           margin[i] = (Double.parseDouble(m) / 100) * bounds.getHeight();
                       } else {
                           margin[i] = (Double.parseDouble(m) / 100) * bounds.getWidth();
                       }
                   } else {
                       margin[i] = Double.parseDouble(m);
                   }
               }

               return margin;

           case 1:

               String m = st.nextToken().trim();

               if (m.endsWith("%")) {
                   m = m.substring(0, m.length() - 1);
               }

               margin[0] = Double.parseDouble(m);
               margin[1] = margin[2] = margin[3] = margin[0];

               return margin;
           }
       }

       margin[0] = bounds.getHeight() * (DEFAULT_MARGIN_PERCENT / 100);
       margin[2] = margin[0];
       margin[1] = bounds.getWidth() * (DEFAULT_MARGIN_PERCENT / 100);
       margin[3] = margin[1];

       return margin;
   }

   protected Bounds getBounds() {
       Bounds bounds = null;

       if (this.boundsRule == PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE) {
           // first the user based limits of the paperspace
           bounds = new Bounds();

           if (this.doc.getDXFHeader()
                           .hasVariable(DXFConstants.HEADER_VARIABLE_PEXTMAX) &&
                   this.doc.getDXFHeader()
                               .hasVariable(DXFConstants.HEADER_VARIABLE_PEXTMIN) &&
                   useLimits) {
               DXFVariable min = this.doc.getDXFHeader()
                                         .getVariable(DXFConstants.HEADER_VARIABLE_PEXTMIN);
               DXFVariable max = this.doc.getDXFHeader()
                                         .getVariable(DXFConstants.HEADER_VARIABLE_PEXTMAX);

               bounds.setMinimumX(min.getDoubleValue("10"));
               bounds.setMinimumY(min.getDoubleValue("20"));
               bounds.setMaximumX(max.getDoubleValue("10"));
               bounds.setMaximumY(max.getDoubleValue("20"));
           }

           if ((!bounds.isValid() || (bounds.getWidth() == 0.0) ||
                   (bounds.getHeight() == 0.0)) &&
                   this.doc.getDXFHeader()
                               .hasVariable(DXFConstants.HEADER_VARIABLE_PLIMMIN) &&
                   this.doc.getDXFHeader()
                               .hasVariable(DXFConstants.HEADER_VARIABLE_PLIMMAX) &&
                   useLimits) {
               DXFVariable min = this.doc.getDXFHeader()
                                         .getVariable(DXFConstants.HEADER_VARIABLE_PLIMMIN);
               DXFVariable max = this.doc.getDXFHeader()
                                         .getVariable(DXFConstants.HEADER_VARIABLE_PLIMMAX);

               bounds.setMinimumX(min.getDoubleValue("10"));
               bounds.setMinimumY(min.getDoubleValue("20"));
               bounds.setMaximumX(max.getDoubleValue("10"));
               bounds.setMaximumY(max.getDoubleValue("20"));
           }

           if (!bounds.isValid() || (bounds.getWidth() == 0.0) ||
                   (bounds.getHeight() == 0.0)) {
               //get bounds only from paper space entities 
               bounds = this.doc.getBounds(false);
           }
       } else if (this.boundsRule == PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE) {
           // first the user based limits of the modelspace
           bounds = new Bounds();

           if (this.doc.getDXFHeader()
                           .hasVariable(DXFConstants.HEADER_VARIABLE_EXTMIN) &&
                   this.doc.getDXFHeader()
                               .hasVariable(DXFConstants.HEADER_VARIABLE_EXTMAX) &&
                   useLimits) {
               DXFVariable min = this.doc.getDXFHeader()
                                         .getVariable(DXFConstants.HEADER_VARIABLE_EXTMIN);
               DXFVariable max = this.doc.getDXFHeader()
                                         .getVariable(DXFConstants.HEADER_VARIABLE_EXTMAX);

               bounds.setMinimumX(min.getDoubleValue("10"));
               bounds.setMinimumY(min.getDoubleValue("20"));
               bounds.setMaximumX(max.getDoubleValue("10"));
               bounds.setMaximumY(max.getDoubleValue("20"));
           }

           if ((!bounds.isValid() || (bounds.getWidth() == 0.0) ||
                   (bounds.getHeight() == 0.0)) &&
                   this.doc.getDXFHeader()
                               .hasVariable(DXFConstants.HEADER_VARIABLE_LIMMIN) &&
                   this.doc.getDXFHeader()
                               .hasVariable(DXFConstants.HEADER_VARIABLE_LIMMAX) &&
                   useLimits) {
               DXFVariable min = this.doc.getDXFHeader()
                                         .getVariable(DXFConstants.HEADER_VARIABLE_LIMMIN);
               DXFVariable max = this.doc.getDXFHeader()
                                         .getVariable(DXFConstants.HEADER_VARIABLE_LIMMAX);

               bounds.setMinimumX(min.getDoubleValue("10"));
               bounds.setMinimumY(min.getDoubleValue("20"));
               bounds.setMaximumX(max.getDoubleValue("10"));
               bounds.setMaximumY(max.getDoubleValue("20"));
           }

           if (!bounds.isValid() || (bounds.getWidth() == 0.0) ||
                   (bounds.getHeight() == 0.0)) {
               //get bounds only from model space entities
               bounds = this.doc.getBounds(true);
           }
       }

       if ((bounds == null) || !bounds.isValid() ||
               (bounds.getWidth() == 0.0) || (bounds.getHeight() == 0.0)) {
           if (this.boundsRule == PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE) {
               bounds = this.doc.getBounds(true);
               this.boundsRule = PROPERTY_DOCUMENT_BOUNDS_RULE_MODELSPACE;
           } else {
               bounds = this.doc.getBounds(false);
               this.boundsRule = PROPERTY_DOCUMENT_BOUNDS_RULE_PAPERSPACE;
           }
       }

       // Single DXF to PDF mode only
       if (this.singlePdfMode) {
           Bounds headerBounds = getHeaderModelSpaceBounds();
           if (isSaneBounds(headerBounds) && isLikelyOutlierBounds(bounds, headerBounds)) {
               bounds = headerBounds;
           }
       }

       // set a margin
       double[] margin = this.getMargin(bounds);
       bounds.setMinimumX(bounds.getMinimumX() - margin[3]);
       bounds.setMaximumX(bounds.getMaximumX() + margin[1]);
       bounds.setMinimumY(bounds.getMinimumY() - margin[2]);
       bounds.setMaximumY(bounds.getMaximumY() + margin[0]);

       return bounds;
   }

    /**
     * Retrieves the model space bounds from the DXF header information.
     *
     * <p>The method first checks for the presence of EXTMIN and EXTMAX
     * header variables, which represent the actual extents of the drawing
     * in model space.</p>
     *
     * <p>If EXTMIN and EXTMAX are not available, the method falls back to
     * LIMMIN and LIMMAX, which represent the configured drawing limits.</p>
     *
     * <p>The extracted bounds are used during DXF processing for:</p>
     * <ul>
     *     <li>Determining drawing dimensions</li>
     *     <li>Scaling and fitting content into PDF pages</li>
     *     <li>Viewport calculations</li>
     *     <li>Rendering and layout validation</li>
     * </ul>
     *
     * <p>DXF coordinate group codes used:</p>
     * <ul>
     *     <li>"10" - X coordinate</li>
     *     <li>"20" - Y coordinate</li>
     * </ul>
     *
     * @return a {@link Bounds} object containing the minimum and maximum
     *         X/Y coordinates extracted from the DXF header
     */
    private Bounds getHeaderModelSpaceBounds() {
       Bounds headerBounds = new Bounds();
       if (this.doc.getDXFHeader().hasVariable(DXFConstants.HEADER_VARIABLE_EXTMIN)
               && this.doc.getDXFHeader().hasVariable(DXFConstants.HEADER_VARIABLE_EXTMAX)) {
           DXFVariable min = this.doc.getDXFHeader().getVariable(DXFConstants.HEADER_VARIABLE_EXTMIN);
           DXFVariable max = this.doc.getDXFHeader().getVariable(DXFConstants.HEADER_VARIABLE_EXTMAX);
           headerBounds.setMinimumX(min.getDoubleValue("10"));
           headerBounds.setMinimumY(min.getDoubleValue("20"));
           headerBounds.setMaximumX(max.getDoubleValue("10"));
           headerBounds.setMaximumY(max.getDoubleValue("20"));
       } else if (this.doc.getDXFHeader().hasVariable(DXFConstants.HEADER_VARIABLE_LIMMIN)
               && this.doc.getDXFHeader().hasVariable(DXFConstants.HEADER_VARIABLE_LIMMAX)) {
           DXFVariable min = this.doc.getDXFHeader().getVariable(DXFConstants.HEADER_VARIABLE_LIMMIN);
           DXFVariable max = this.doc.getDXFHeader().getVariable(DXFConstants.HEADER_VARIABLE_LIMMAX);
           headerBounds.setMinimumX(min.getDoubleValue("10"));
           headerBounds.setMinimumY(min.getDoubleValue("20"));
           headerBounds.setMaximumX(max.getDoubleValue("10"));
           headerBounds.setMaximumY(max.getDoubleValue("20"));
       }
       return headerBounds;
   }

   /**
    * True when bounds exist, are valid, and have positive width and height.
    */
   private static boolean isSaneBounds(Bounds b) {
       return b != null && b.isValid() && b.getWidth() > 0 && b.getHeight() > 0;
   }

    /**
     * Determines whether the computed drawing bounds are likely abnormal
     * when compared against the bounds defined in the DXF header.
     *
     * <p>The method validates both computed and header bounds before
     * calculating their respective areas.</p>
     *
     * <p>If the computed bounds area is significantly larger than the
     * header bounds area (greater than 1000 times), the bounds are
     * treated as suspicious or outliers.</p>
     *
     * <p>This validation helps detect malformed or corrupted DXF entities
     * that may produce extremely large coordinates and cause issues during
     * DXF to PDF rendering, such as:</p>
     * <ul>
     *     <li>Incorrect scaling</li>
     *     <li>Oversized page generation</li>
     *     <li>Rendering failures</li>
     *     <li>Memory/performance problems</li>
     * </ul>
     *
     * @param computedBounds the bounds calculated from DXF entities
     * @param headerBounds the bounds extracted from the DXF header
     * @return {@code true} if the computed bounds appear to be abnormal
     *         outliers compared to the header bounds; otherwise {@code false}
     */
   private static boolean isLikelyOutlierBounds(Bounds computedBounds, Bounds headerBounds) {
       if (!isSaneBounds(computedBounds) || !isSaneBounds(headerBounds)) {
           return false;
       }
       double computedArea = computedBounds.getWidth() * computedBounds.getHeight();
       double headerArea = headerBounds.getWidth() * headerBounds.getHeight();
       if (headerArea <= 0) {
           return false;
       }
       return computedArea / headerArea > 1000d;
   }

   public void setSVGSAXGeneratorManager(SVGSAXGeneratorManager manager) {
       this.manager = manager;
   }

   protected void layerToSAX(DXFLayer layer, boolean onModelspace)
       throws SAXException {
       AttributesImpl attr = new AttributesImpl();

       SVGUtils.addAttribute(attr, SVGConstants.XML_ID,
           SVGUtils.validateID(layer.getName()));

       SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_COLOR,
           "rgb(" + DXFColor.getRGBString(Math.abs(layer.getColor())) + ")");
       SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_STROKE,
           SVGConstants.SVG_ATTRIBUTE_STROKE_VALUE_CURRENTCOLOR);

       SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_FILL,
           SVGConstants.SVG_ATTRIBUTE_FILL_VALUE_NONE);

       if (!layer.isVisible() && onModelspace) {
           SVGUtils.addAttribute(attr, SVGConstants.SVG_ATTRIBUTE_VISIBILITY,
               SVGConstants.SVG_ATTRIBUTE_VISIBILITY_VALUE_HIDDEN);
       }

       String lt = layer.getLineType();

       if (lt.length() > 0) {
           DXFLineType ltype = doc.getDXFLineType(lt);
           SVGUtils.addStrokeDashArrayAttribute(attr, ltype);
       }

       // the stroke-width
       int lineWeight = layer.getLineWeight();

       // the stroke-width
       Double lw = null;

       if ((lineWeight > 0) &&
               !context.containsKey(SVGContext.STROKE_WIDTH_IGNORE)) {
           lw = new Double(lineWeight);
           SVGUtils.addAttribute(attr,
               SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH,
               SVGUtils.lineWeightToStrokeWidth(lineWeight));
       } else {
           lw = (Double) context.get(SVGContext.STROKE_WIDTH);
           SVGUtils.addAttribute(attr,
               SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH,
               SVGUtils.formatNumberAttribute(lw.doubleValue()));
       }

       context.put(SVGContext.LAYER_STROKE_WIDTH, lw);

       SVGUtils.startElement(handler, SVGConstants.SVG_GROUP, attr);

       Iterator types = layer.getDXFEntityTypeIterator();

       while (types.hasNext()) {
           String type = (String) types.next();
           ArrayList list = (ArrayList) layer.getDXFEntities(type);

           /*
            * In single PDF conversion mode, explicitly wrap TEXT and MTEXT entities
            * inside a dedicated SVG group with controlled styling.

            * Why this is required:
            * During full DXF-to-single-PDF rendering, text entities may inherit unwanted
            * stroke/fill styles from parent SVG groups or previously rendered entities.
            * This can cause:
            *   - bold or distorted text rendering
            *   - outlined characters
            *   - invisible text
            *   - inconsistent font appearance in generated PDFs
            *
            * To avoid this, a separate SVG <g> element is created with:
            *   - fill        = currentColor  -> render text using active drawing color
            *   - stroke      = none          -> disable text outlines
            *   - stroke-width= 0             -> prevent accidental stroke rendering
            *   - font-weight = normal        -> normalize font weight rendering
            *
            * This styling isolation ensures consistent and readable text rendering
            * when converting the complete DXF drawing into a single PDF.
            */
           boolean isTextType = this.singlePdfMode
                   && (DXFConstants.ENTITY_TYPE_TEXT.equals(type) || DXFConstants.ENTITY_TYPE_MTEXT.equals(type));
           if (isTextType) {
               AttributesImpl textAttr = new AttributesImpl();
               SVGUtils.addAttribute(textAttr, SVGConstants.SVG_ATTRIBUTE_FILL, "currentColor");
               SVGUtils.addAttribute(textAttr, SVGConstants.SVG_ATTRIBUTE_STROKE, "none");
               SVGUtils.addAttribute(textAttr, SVGConstants.SVG_ATTRIBUTE_STROKE_WITDH, "0");
               SVGUtils.addAttribute(textAttr, "font-weight", "normal");
               SVGUtils.startElement(handler, SVGConstants.SVG_GROUP, textAttr);
           }

           try {
               try {
                   SVGSAXGenerator gen = this.manager.getSVGGenerator(type);

                   Iterator i = list.iterator();

                   while (i.hasNext()) {
                       DXFEntity entity = (DXFEntity) i.next();
                       try {
                           if (isTextType) {
                               // Drop aggressive text lineweight/thickness before Kabeja serializes to SVG paths.
                               entity.setLineWeight(-1);
                                if (entity instanceof DXFText) {
                                    DXFText text = (DXFText) entity;
                                    text.setThickness(0d);
                                    /*
                                      Kabeja dynamically scales text bounds for Aligned (3) and Fit (5) alignments.
                                      If the DXF alignment point is malformed (e.g. 0,0), it stretches the text massively across the page.
                                     */
                                    if (text.getAlign() == 3 || text.getAlign() == 5) {
                                        text.setAlign(0);
                                    }
                                } else if (entity instanceof org.kabeja.dxf.DXFMText) {
                                   ((org.kabeja.dxf.DXFMText) entity).setThickness(0d);
                               }
                           }
                           boolean v = entity.isVisibile();
                           entity.setVisibile(!layer.isFrozen());

                           if (!onModelspace) {
                               entity.setVisibile(layer.isVisible());
                           }

                           if ((onModelspace && entity.isModelSpace()) ||
                                   (!onModelspace && !entity.isModelSpace())) {
                               gen.toSAX(handler, context, entity, null);
                           }

                           //restore back the flag
                           entity.setVisibile(v);
                        } catch (Throwable t) {
                           /*
                               FIX: Gracefully handle parsing failures for individual entities (e.g., malformed MLINE elements)
                               to prevent the entire PDF generation from crashing.
                            */
                             if (t instanceof ArrayIndexOutOfBoundsException && "MLINE".equals(type)) {
                                 LOG.warn("Skipping malformed MLINE entity {} due to Kabeja parsing error: {}", entity.getID(), t.getMessage());
                             } else {
                                 LOG.warn("Failed to generate SVG for entity {} of type {}: {}", entity.getID(), type, t.getMessage());
                             }
                        }
                   }
               } catch (SVGGenerationException e) {
                   e.printStackTrace();
               }
           } catch (Exception e) {
               e.printStackTrace();
           }
           /*
              Close the SVG group wrapper created for text-based entities
              after completing text element rendering.
            */
           if (isTextType) {
               SVGUtils.endElement(handler, SVGConstants.SVG_GROUP);
           }
       }

       SVGUtils.endElement(handler, SVGConstants.SVG_GROUP);
   }
}
