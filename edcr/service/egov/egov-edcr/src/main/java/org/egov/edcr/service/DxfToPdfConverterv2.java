package org.egov.edcr.service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * Full-featured DXF Parser and SVG/PDF Converter.
 *
 * Handles:
 *  - LINE, CIRCLE, ARC, POLYLINE, LWPOLYLINE, SPLINE, ELLIPSE, HATCH
 *  - TEXT, MTEXT (with full RTF-style formatting codes)
 *  - INSERT (block references) -> SVG <use>
 *  - DIMENSION entities
 *  - Proper coordinate flip (DXF Y-up -> SVG Y-down)
 *  - Layer colors (ACI color table)
 *  - Multiple fonts via font-family mapping
 *  - Viewport/extents calculation for proper fit-to-page
 */

@Service
public class DxfToPdfConverterv2 {
	
	private static Logger LOG = LogManager.getLogger(DxfToPdfConverterv2.class);
	
    private static final ThreadLocal<Boolean> RENDERING_BLOCK_DEFINITION =
            ThreadLocal.withInitial(() -> false);

    // ── ACI (AutoCAD Color Index) table ──────────────────────────────────────
    private static final String[] ACI_COLORS = new String[256];
    static {
        // Populate standard ACI colors
        ACI_COLORS[0]  = "#000000"; // ByBlock
        ACI_COLORS[1]  = "#FF0000"; // Red
        ACI_COLORS[2]  = "#FFFF00"; // Yellow
        ACI_COLORS[3]  = "#00FF00"; // Green
        ACI_COLORS[4]  = "#00FFFF"; // Cyan
        ACI_COLORS[5]  = "#0000FF"; // Blue
        ACI_COLORS[6]  = "#FF00FF"; // Magenta
        ACI_COLORS[7]  = "#000000"; // White/Black (on white bg -> black)
        ACI_COLORS[8]  = "#808080";
        ACI_COLORS[9]  = "#C0C0C0";
        for (int i = 10; i < 250; i++) {
            ACI_COLORS[i] = "#000000";
        }

        int[][] hueBases = {
                {10,255,0,0},     {20,255,63,0},    {30,255,127,0},   {40,255,191,0},
                {50,255,255,0},   {60,191,255,0},   {70,127,255,0},   {80,63,255,0},
                {90,0,255,0},     {100,0,255,63},   {110,0,255,127},  {120,0,255,191},
                {130,0,255,255},  {140,0,191,255},  {150,0,127,255},  {160,0,63,255},
                {170,0,0,255},    {180,63,0,255},   {190,127,0,255},  {200,191,0,255},
                {210,255,0,255},  {220,255,0,191},  {230,255,0,127},  {240,255,0,63}
        };
        for (int[] base : hueBases) {
            for (int offset = 0; offset < 10; offset++) {
                int index = base[0] + offset;
                if (index < 250) {
                    ACI_COLORS[index] = aciShade(base[1], base[2], base[3], offset);
                }
            }
        }

        ACI_COLORS[250] = "#333333";
        ACI_COLORS[251] = "#5B5B5B";
        ACI_COLORS[252] = "#848484";
        ACI_COLORS[253] = "#ADADAD";
        ACI_COLORS[254] = "#D6D6D6";
        ACI_COLORS[255] = "#FFFFFF";
    }

    static String aciShade(int r, int g, int b, int offset) {
        double darken = 1.0 - (offset / 2) * 0.20;
        int rr = clampColor((int) Math.round(r * darken));
        int gg = clampColor((int) Math.round(g * darken));
        int bb = clampColor((int) Math.round(b * darken));
        if ((offset & 1) == 1) {
            rr = clampColor((rr + 255) / 2);
            gg = clampColor((gg + 255) / 2);
            bb = clampColor((bb + 255) / 2);
        }
        return String.format("#%02X%02X%02X", rr, gg, bb);
    }

    static int clampColor(int value) {
        return Math.max(0, Math.min(255, value));
    }

    // ── DXF Data Model ────────────────────────────────────────────────────────

    static class Layer {
        String name = "0";
        int colorIndex = 7;
        int trueColor = -1;
        String lineType = "CONTINUOUS";
        double lineWeight = -1;
        boolean visible = true;
    }

    static class TextStyle {
        String name = "Standard";
        String fontFile = "";
        double height = 0;
        double widthFactor = 1.0;
        double obliqueAngle = 0;
    }

    static class Block {
        String name = "";
        double baseX = 0, baseY = 0, baseZ = 0;
        List<Entity> entities = new ArrayList<>();
    }

    // Base entity
    static abstract class Entity {
        String layer = "0";
        int colorIndex = -1; // -1 = bylayer, 0 = byblock
        int trueColor = -1;
        String lineType = "";
        double lineWeight = -1;
        boolean visible = true;

        abstract String toSvg(DxfDocument doc, double[] transform);
    }

    static class LineEntity extends Entity {
        double x1, y1, z1, x2, y2, z2;

        @Override
        String toSvg(DxfDocument doc, double[] t) {
            String semanticColor = resolveApprovalPlanLineColor(doc, this, lineCenterX(), lineCenterY(), t);
            String color = semanticColor != null ? semanticColor : resolveColor(doc, this);
            double lw = resolveLineWeight(doc, layer, lineWeight);
            if (semanticColor != null) {
                lw = Math.max(lw, 2.0);
            }
            return String.format(Locale.US,
                "<line x1=\"%.4f\" y1=\"%.4f\" x2=\"%.4f\" y2=\"%.4f\" " +
                "stroke=\"%s\" stroke-width=\"%.4f\" fill=\"none\"/>",
                tx(x1, t), ty(y1, t), tx(x2, t), ty(y2, t), color, lw);
        }

        double lineCenterX() {
            return (x1 + x2) / 2.0;
        }

        double lineCenterY() {
            return (y1 + y2) / 2.0;
        }
    }

    static class CircleEntity extends Entity {
        double cx, cy, cz, radius;

        @Override
        String toSvg(DxfDocument doc, double[] t) {
            String color = resolveColor(doc, this);
            double lw = resolveLineWeight(doc, layer, lineWeight);
            return String.format(Locale.US,
                "<circle cx=\"%.4f\" cy=\"%.4f\" r=\"%.4f\" " +
                "stroke=\"%s\" stroke-width=\"%.4f\" fill=\"none\"/>",
                tx(cx, t), ty(cy, t), Math.abs(radius * t[0]), color, lw);
        }
    }

    static class ArcEntity extends Entity {
        double cx, cy, cz, radius, startAngle, endAngle;

        @Override
        String toSvg(DxfDocument doc, double[] t) {
            String color = resolveColor(doc, this);
            double lw = resolveLineWeight(doc, layer, lineWeight);
            double scale = t[0];
            // DXF angles are CCW from +X; SVG Y is flipped -> negate angles
            double sa = -Math.toRadians(startAngle);
            double ea = -Math.toRadians(endAngle);
            // ensure arc direction after flip
            double r = Math.abs(radius * scale);
            double cx2 = tx(cx, t);
            double cy2 = ty(cy, t);
            double sx = cx2 + r * Math.cos(sa);
            double sy = cy2 + r * Math.sin(sa);
            double ex = cx2 + r * Math.cos(ea);
            double ey = cy2 + r * Math.sin(ea);
            // compute swept angle in original coords (CCW in DXF)
            double sweep = endAngle - startAngle;
            if (sweep < 0) sweep += 360;
            // After Y-flip, CCW becomes CW (sweep-flag=0)
            int largeArc = (sweep > 180) ? 1 : 0;
            int sweepFlag = 0; // Y flipped -> CW

            String d = String.format(Locale.US,
                    "M %.4f %.4f A %.4f %.4f 0 %d %d %.4f %.4f",
                    sx, sy, r, r, largeArc, sweepFlag, ex, ey);

                String safeD = validatePathData(d);
                if (safeD == null) {
                    LOG.debug("Skipping invalid arc path: {}", d);
                    return "";
                }
            
            return String.format(Locale.US,
                "<path d=\"M %.4f %.4f A %.4f %.4f 0 %d %d %.4f %.4f\" " +
                "stroke=\"%s\" stroke-width=\"%.4f\" fill=\"none\"/>",
                sx, sy, r, r, largeArc, sweepFlag, ex, ey, color, lw);
        }
    }

    static class EllipseEntity extends Entity {
        double cx, cy, cz;
        double majorX, majorY; // major axis endpoint relative to center
        double ratio;           // minor/major ratio
        double startParam, endParam; // 0..2PI

        @Override
        String toSvg(DxfDocument doc, double[] t) {
            String color = resolveColor(doc, this);
            double lw = resolveLineWeight(doc, layer, lineWeight);
            double scale = t[0];
            double rx = Math.sqrt(majorX * majorX + majorY * majorY) * scale;
            double ry = rx * ratio;
            double rotation = -Math.toDegrees(Math.atan2(majorY, majorX));
            double cx2 = tx(cx, t);
            double cy2 = ty(cy, t);
            if (Math.abs(startParam) < 1e-6 && Math.abs(endParam - 2 * Math.PI) < 1e-4) {
                return String.format(Locale.US,
                    "<ellipse cx=\"%.4f\" cy=\"%.4f\" rx=\"%.4f\" ry=\"%.4f\" " +
                    "transform=\"rotate(%.4f %.4f %.4f)\" " +
                    "stroke=\"%s\" stroke-width=\"%.4f\" fill=\"none\"/>",
                    cx2, cy2, rx, ry, rotation, cx2, cy2, color, lw);
            }
            // Partial ellipse as path
            double cosR = Math.cos(Math.toRadians(rotation));
            double sinR = Math.sin(Math.toRadians(rotation));
            double[] s = ellipsePoint(rx, ry, cosR, sinR, cx2, cy2, startParam);
            double[] e = ellipsePoint(rx, ry, cosR, sinR, cx2, cy2, endParam);
            double sweep = endParam - startParam;
            if (sweep < 0) sweep += 2 * Math.PI;
            int largeArc = sweep > Math.PI ? 1 : 0;
            
            String d = String.format(Locale.US,
                    "M %.4f %.4f A %.4f %.4f %.4f %d 0 %.4f %.4f",
                    s[0], s[1], rx, ry, rotation, largeArc, e[0], e[1]);

                String safeD = validatePathData(d);
                if (safeD == null) {
                    LOG.debug("Skipping invalid ellipse path: {}", d);
                    return "";
                }
                
            return String.format(Locale.US,
                "<path d=\"M %.4f %.4f A %.4f %.4f %.4f %d 0 %.4f %.4f\" " +
                "stroke=\"%s\" stroke-width=\"%.4f\" fill=\"none\"/>",
                s[0], s[1], rx, ry, rotation, largeArc, e[0], e[1], color, lw);
        }

        private double[] ellipsePoint(double rx, double ry, double cosR, double sinR,
                                      double cx, double cy, double param) {
            double lx = rx * Math.cos(param);
            double ly = -ry * Math.sin(param);
            return new double[]{cx + lx * cosR - ly * sinR, cy + lx * sinR + ly * cosR};
        }
    }

    static class PolylineEntity extends Entity {
        List<double[]> vertices = new ArrayList<>(); // [x, y, bulge]
        boolean closed = false;
        boolean isMesh = false;

        @Override
        String toSvg(DxfDocument doc, double[] t) {
            if (vertices.isEmpty()) return "";
            String semanticColor = closed ? resolveApprovalPlanColor(doc, this, polylineCenterX()) : null;
            String color = semanticColor != null ? semanticColor : resolveColor(doc, this);
            double lw = resolveLineWeight(doc, layer, lineWeight);
            if (semanticColor != null) {
                lw = Math.max(lw, 2.0);
            }
            StringBuilder d = new StringBuilder();
            for (int i = 0; i < vertices.size(); i++) {
                double[] v = vertices.get(i);
                double vx = tx(v[0], t);
                double vy = ty(v[1], t);
                double bulge = v.length > 2 ? v[2] : 0;
                if (i == 0) {
                    d.append(String.format(Locale.US, "M %.4f %.4f", vx, vy));
                } else {
                    double[] prev = vertices.get(i - 1);
                    if (Math.abs(bulge) > 1e-10) {
                        // Arc segment from bulge
                        appendBulgeArc(d, prev[0], prev[1], v[0], v[1], prev[2], t);
                    } else {
                        d.append(String.format(Locale.US, " L %.4f %.4f", vx, vy));
                    }
                }
            }
            // Handle last bulge if any when going to first vertex
            if (closed && !vertices.isEmpty()) {
                double[] last = vertices.get(vertices.size() - 1);
                double[] first = vertices.get(0);
                if (Math.abs(last[2]) > 1e-10) {
                    appendBulgeArc(d, last[0], last[1], first[0], first[1], last[2], t);
                }
                d.append(" Z");
            }
            
            String safeD = validatePathData(d.toString());
            if (safeD == null) {
                LOG.debug("Skipping invalid polyline path on layer {}: {}", layer, d);
                return "";
            }
            return String.format(Locale.US,
                "<path d=\"%s\" stroke=\"%s\" stroke-width=\"%.4f\" fill=\"none\"/>",
                d, color, lw);
        }

        double polylineCenterX() {
            if (vertices.isEmpty()) return 0;
            double sum = 0;
            for (double[] v : vertices) sum += v[0];
            return sum / vertices.size();
        }

        void appendBulgeArc(StringBuilder d, double x1, double y1, double x2, double y2,
                             double bulge, double[] t) {
            // Convert bulge to SVG arc params
            double scale = t[0];
            double len = Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
            double r = len * (bulge * bulge + 1) / (4 * Math.abs(bulge)) * scale;
            int largeArc = Math.abs(bulge) > 1 ? 1 : 0;
            // In SVG (Y-flipped), positive bulge (CCW) -> sweepFlag=0
            int sweepFlag = bulge > 0 ? 0 : 1;
            d.append(String.format(Locale.US, " A %.4f %.4f 0 %d %d %.4f %.4f",
                r, r, largeArc, sweepFlag, tx(x2, t), ty(y2, t)));
        }
    }

    static class SplineEntity extends Entity {
        List<double[]> controlPoints = new ArrayList<>();
        List<double[]> fitPoints = new ArrayList<>();
        int degree = 3;
        boolean closed = false;

        @Override
        String toSvg(DxfDocument doc, double[] t) {
            String color = resolveColor(doc, this);
            double lw = resolveLineWeight(doc, layer, lineWeight);
            List<double[]> pts = controlPoints.isEmpty() ? fitPoints : controlPoints;
            if (pts.size() < 2) return "";
            StringBuilder d = new StringBuilder();
            d.append(String.format(Locale.US, "M %.4f %.4f", tx(pts.get(0)[0], t), ty(pts.get(0)[1], t)));
            if (degree == 3 && pts.size() >= 4) {
                // Cubic bezier segments
                for (int i = 1; i + 2 < pts.size(); i += 3) {
                    d.append(String.format(Locale.US, " C %.4f %.4f %.4f %.4f %.4f %.4f",
                        tx(pts.get(i)[0], t), ty(pts.get(i)[1], t),
                        tx(pts.get(i+1)[0], t), ty(pts.get(i+1)[1], t),
                        tx(pts.get(i+2)[0], t), ty(pts.get(i+2)[1], t)));
                }
            } else {
                // Polyline fallback for control points
                for (int i = 1; i < pts.size(); i++) {
                    d.append(String.format(Locale.US, " L %.4f %.4f",
                        tx(pts.get(i)[0], t), ty(pts.get(i)[1], t)));
                }
            }
            if (closed) d.append(" Z");
            
            String safeD = validatePathData(d.toString());
            if (safeD == null) {
                LOG.debug("Skipping invalid spline path on layer {}: {}", layer, d);
                return "";
            }
            
            return String.format(Locale.US,
                "<path d=\"%s\" stroke=\"%s\" stroke-width=\"%.4f\" fill=\"none\"/>",
                d, color, lw);
        }
    }

    static class HatchEntity extends Entity {
        String patternName = "SOLID";
        boolean solid = false;
        List<List<double[]>> boundaryLoops = new ArrayList<>();
        double angle = 0;
        double scale = 1.0;

        @Override
        String toSvg(DxfDocument doc, double[] t) {
            // HATCH boundary paths are construction geometry defining fill areas.
            // Rendering them as visible lines creates unwanted overlap with the
            // actual LINE/POLYLINE entities that already draw the same boundaries.
            // Skip entirely — no visible output.
            return "";
        }
    }

    // ── TEXT ENTITY ───────────────────────────────────────────────────────────
    static class TextEntity extends Entity {
        double x, y, z;
        double secondX = Double.NaN, secondY = Double.NaN;
        double height = 2.5;
        String text = "";
        double rotation = 0;
        double widthFactor = 1.0;
        double obliqueAngle = 0;
        String styleName = "Standard";
        int hJustify = 0; // 0=left, 1=center, 2=right, 3=aligned, 4=middle, 5=fit
        int vJustify = 0; // 0=baseline, 1=bottom, 2=middle, 3=top
        int textFlags = 0; // Group code 71: bit 2=RTL, bit 3=vertical text

        @Override
        String toSvg(DxfDocument doc, double[] t) {
            if (text.isEmpty()) return "";
        String color = resolveColor(doc, this);
        double scale = t[0];
        double svgHeight = height * scale;
        // Enforce minimum font size to prevent text from becoming too small
        if (svgHeight < 1.0) {
            svgHeight = 1.0;
        }
        double px = tx(x, t);
        double py = ty(y, t);

            // Determine text anchor based on horizontal justification
            String anchor = "start";
            if (hJustify == 1 || hJustify == 4) anchor = "middle";
            else if (hJustify == 2) anchor = "end";
            
            // For vertical text baseline
            String dominantBaseline = "alphabetic";
            if (vJustify == 3) dominantBaseline = "hanging";      // top
            else if (vJustify == 2) dominantBaseline = "middle";  // middle
            else if (vJustify == 1) dominantBaseline = "text-bottom"; // bottom
            else dominantBaseline = "alphabetic"; // baseline (DXF default)

            String fontFamily = resolveFontFamily(doc, styleName);
            String cleanText = processTextCodes(text);
            
            // Build SVG text element - NO transforms, simple and clean
            StringBuilder svg = new StringBuilder();
            svg.append(String.format(
                    "<text x=\"%.6f\" y=\"%.6f\" font-size=\"%.6f\" fill=\"%s\" " +
                    "font-family=\"%s\" text-anchor=\"%s\" dominant-baseline=\"%s\">",
                    px, py, svgHeight, color, fontFamily, anchor, dominantBaseline));
            
            // Apply rotation if needed (use transform attribute)
            if (Math.abs(rotation) > 0.01) {
                svg.insert(svg.length() - 1, String.format(Locale.US, 
                    " transform=\"rotate(%.6f %.6f %.6f)\"", -rotation, px, py));
            }
            
            svg.append(escapeXml(cleanText));
            svg.append("</text>");
            
            return svg.toString();
        }
    }

    // ── MTEXT ENTITY (full RTF-style DXF codes) ───────────────────────────────
    static class MTextEntity extends Entity {
        double x, y, z;
        double height = 2.5;
        double width = 0;    // 0 = no wrapping
        String text = "";    // Raw MTEXT content (with { } codes)
        double rotation = 0; // In degrees (group 50) or radians from X-direction vector
        String xDir = "";    // Group 11,21,31 X-direction vector
        double xDirX = 1, xDirY = 0;
        int attachment = 1;  // 1=TL,2=TC,3=TR,4=ML,5=MC,6=MR,7=BL,8=BC,9=BR
        String styleName = "Standard";
        double lineSpacing = 1.0;
        int textDirection = 0; // Group code 74: 0=LTR, 1=RTL, 3=vertical

        @Override
        String toSvg(DxfDocument doc, double[] t) {
            if (text.isEmpty()) return "";
            double scale = t[0];
            double px = tx(x, t);
            double py = ty(y, t);
            double fontSize = height * scale;
            // Enforce minimum font size to prevent text from becoming too small
            if (fontSize < 1.0) {
                fontSize = 1.0;
            }

            String fontFamily = resolveFontFamily(doc, styleName);
            List<MTextSegment> segments = parseMText(text, doc, styleName, fontSize);

            // Map attachment point to SVG text-anchor.
            // Attachment grid: 1=TL  2=TC  3=TR
            //                 4=ML  5=MC  6=MR
            //                 7=BL  8=BC  9=BR
            String anchor = "start";      // 1,4,7 = left
            if (attachment == 2 || attachment == 5 || attachment == 8) {
                anchor = "middle";  // center
            } else if (attachment == 3 || attachment == 6 || attachment == 9) {
                anchor = "end";     // right
            }

            StringBuilder sb = new StringBuilder();

            // Preserve MTEXT inline color/font runs while splitting into visual lines.
            List<List<MTextSegment>> lines = new ArrayList<>();
            List<MTextSegment> currentLine = new ArrayList<>();
            
            for (MTextSegment seg : segments) {
                if (seg.isNewline) {
                    lines.add(currentLine);
                    currentLine = new ArrayList<>();
                } else if (!seg.text.isEmpty()) {
                    currentLine.add(seg);
                }
            }
            if (!currentLine.isEmpty()) lines.add(currentLine);
            if (lines.isEmpty()) lines.add(new ArrayList<>());

            double effectiveLineSpacing = Math.max(1.0, lineSpacing > 0 ? lineSpacing : 1.0);
            List<Double> lineFontSizes = new ArrayList<>();
            for (List<MTextSegment> line : lines) {
                lineFontSizes.add(maxMTextLineFontSize(line, fontSize));
            }
            double totalHeight = 0.0;
            for (double lineFontSize : lineFontSizes) {
                totalHeight += lineFontSize * effectiveLineSpacing;
            }

            // Keep the DXF insertion point as the exact local origin. Attachment
            // offsets are applied inside this local group, so rotation cannot move
            // MTEXT away from the declared 10/20 point.
            double firstBaselineOffset = lineFontSizes.get(0) * 0.8;
            double localBaselineY = firstBaselineOffset;
            if (attachment == 4 || attachment == 5 || attachment == 6) {
                localBaselineY = -(totalHeight / 2.0) + firstBaselineOffset;
            } else if (attachment == 7 || attachment == 8 || attachment == 9) {
                localBaselineY = -totalHeight + firstBaselineOffset;
            }

            String transform;
            if (Math.abs(rotation) > 0.01) {
                transform = String.format(Locale.US,
                        "translate(%.6f %.6f) rotate(%.6f)", px, py, -rotation);
            } else {
                transform = String.format(Locale.US, "translate(%.6f %.6f)", px, py);
            }

            sb.append(String.format(Locale.US, "<g transform=\"%s\">", transform));

            // Single line - simple output
            if (lines.size() == 1) {
                sb.append(String.format(
                    "<text x=\"%.6f\" y=\"%.6f\" font-size=\"%.6f\" fill=\"%s\" " +
                    "font-family=\"%s\" text-anchor=\"%s\" dominant-baseline=\"alphabetic\" xml:space=\"preserve\">%s</text>",
                    0.0, localBaselineY, fontSize,
                    resolveColor(doc, this),
                    fontFamily, anchor, renderMTextRuns(lines.get(0), resolveColor(doc, this), fontSize)));
            } else {
                // Multiple lines with tspan
                sb.append(String.format(
                    "<text x=\"%.6f\" y=\"%.6f\" font-size=\"%.6f\" fill=\"%s\" " +
                    "font-family=\"%s\" text-anchor=\"%s\" dominant-baseline=\"alphabetic\" xml:space=\"preserve\">",
                    0.0, localBaselineY, fontSize,
                    resolveColor(doc, this),
                    fontFamily, anchor));
                    
                for (int i = 0; i < lines.size(); i++) {
                    if (i == 0) {
                        sb.append(String.format("<tspan x=\"%.6f\" dy=\"0\">%s</tspan>",
                                0.0, renderMTextRuns(lines.get(i), resolveColor(doc, this), fontSize)));
                    } else {
                        sb.append(String.format("<tspan x=\"%.6f\" dy=\"%.6f\">%s</tspan>",
                                0.0, lineFontSizes.get(i - 1) * effectiveLineSpacing,
                                renderMTextRuns(lines.get(i), resolveColor(doc, this), fontSize)));
                    }
                }
                sb.append("</text>");
            }
            sb.append("</g>");
            
            return sb.toString();
        }


    }

    static String renderMTextRuns(List<MTextSegment> runs, String baseColor, double baseFontSize) {
        StringBuilder out = new StringBuilder();
        for (MTextSegment run : runs) {
            String text = cleanMTextVisibleText(run.text);
            if (text.isEmpty()) continue;
            StringBuilder attrs = new StringBuilder();
            if (run.color != null && !run.color.equalsIgnoreCase(baseColor)) {
                attrs.append(String.format(" fill=\"%s\"", run.color));
            }
            if (run.fontSize > 0 && Math.abs(run.fontSize - baseFontSize) > 0.01) {
                attrs.append(String.format(Locale.US, " font-size=\"%.6f\"", run.fontSize));
            }
            if (run.bold) attrs.append(" font-weight=\"bold\"");
            if (run.italic) attrs.append(" font-style=\"italic\"");
            if (run.underline) attrs.append(" text-decoration=\"underline\"");

            if (attrs.length() > 0) {
                out.append("<tspan").append(attrs).append(">")
                        .append(escapeXml(text)).append("</tspan>");
            } else {
                out.append(escapeXml(text));
            }
        }
        return out.toString();
    }

    static double maxMTextLineFontSize(List<MTextSegment> runs, double baseFontSize) {
        double max = baseFontSize;
        for (MTextSegment run : runs) {
            if (run != null && !run.isNewline && run.fontSize > 0) {
                max = Math.max(max, run.fontSize);
            }
        }
        return max;
    }

    static String cleanMTextVisibleText(String text) {
        if (text == null || text.isEmpty()) return "";
        return text
                .replace("^I", "    ")
                .replace("%%U", "").replace("%%u", "")
                .replaceAll("(?<![\\w])([a-zA-Z]{1,3}[+-]?[0-9]*\\.?[0-9]+(?:,[a-zA-Z]{1,3}[+-]?[0-9]*\\.?[0-9]+)*);", "")
                .replaceAll("(?<![\\w=])[a-zA-Z]{1,2}[0-9]?(?:,[a-zA-Z]{1,2}[0-9]?)+;", "");
    }

    // MText segment (result of parsing)
    static class MTextSegment {
        String text = "";
        String color = null;
        double fontSize = 2.5;
        boolean bold = false;
        boolean italic = false;
        boolean underline = false;
        boolean isNewline = false;
        String fontName = "";
    }

    // ── INSERT ENTITY ─────────────────────────────────────────────────────────
    static class InsertEntity extends Entity {
        String blockName = "";
        double x, y, z;
        double scaleX = 1, scaleY = 1, scaleZ = 1;
        double rotation = 0;
        int cols = 1, rows = 1;
        double colSpacing = 0, rowSpacing = 0;

        @Override
        String toSvg(DxfDocument doc, double[] t) {
            if (blockName.startsWith("*")) return "";
            if (!doc.blocks.containsKey(blockName.toUpperCase())) return "";

            // Insertion point in SVG coords
            double px = tx(x, t);
            double py = ty(y, t);

            StringBuilder sb = new StringBuilder();
            for (int r = 0; r < Math.max(1, rows); r++) {
                for (int c = 0; c < Math.max(1, cols); c++) {
                    double ox = px + c * colSpacing * t[0];
                    double oy = py - r * rowSpacing * t[0];

                    // Build transform: translate to insertion point, then scale and rotate
                    // Scale and rotation are applied around the symbol's own origin (base point)
                    String transform;
                    if (Math.abs(rotation) > 0.001 ||
                            Math.abs(scaleX - 1.0) > 0.001 ||
                            Math.abs(scaleY - 1.0) > 0.001) {
                        transform = String.format(Locale.US,
                                "translate(%.4f,%.4f) rotate(%.4f) scale(%.4f,%.4f)",
                                ox, oy, -rotation, scaleX, scaleY);
                    } else {
                        transform = String.format(Locale.US,
                                "translate(%.4f,%.4f)", ox, oy);
                    }

                    sb.append(String.format(
                            "<use xlink:href=\"#block_%s\" transform=\"%s\" color=\"%s\"/>\n",
                            escapeXml(sanitizeId(blockName)), transform, resolveColor(doc, this)));
                }
            }
            return sb.toString();
        }
    }

    // ── DIMENSION ENTITY ──────────────────────────────────────────────────────
    static class DimensionEntity extends Entity {
        double defX, defY;
        double midX, midY;
        double leaderX, leaderY;
        String dimensionBlockName = "";
        String text = "";
        int type = 0;

        @Override
        String toSvg(DxfDocument doc, double[] t) {
            StringBuilder sb = new StringBuilder();

            if (!dimensionBlockName.isEmpty()) {
                String key = dimensionBlockName.toUpperCase();
                Block block = doc.blocks.get(key);
                if (block != null) {
                    String dimColor = resolveColor(doc, this);
                    sb.append(String.format("<g fill=\"%s\" stroke=\"%s\" color=\"%s\">", dimColor, dimColor, dimColor));
                    for (Entity e : block.entities) {
                        if (!e.visible) continue;
                        String savedLayer = e.layer;
                        if (e.layer.equals("0") && !layer.isEmpty()) e.layer = layer;
                        try {
                            String svg = e.toSvg(doc, t);
                            svg = svg.replaceAll(" stroke=\"#[0-9A-Fa-f]{6}\"", "");
                            svg = svg.replaceAll(" fill=\"#[0-9A-Fa-f]{6}\"", "");
                            svg = svg.replaceAll(" color=\"#[0-9A-Fa-f]{6}\"", "");
                            svg = normalizeDimensionTextStyle(svg);
                            if (!svg.isEmpty()) sb.append(svg).append("\n");
                        } catch (Exception ex) { /* skip */ }
                        e.layer = savedLayer;
                    }
                    sb.append("</g>");
                }
            }

            if (text != null && !text.isEmpty() && !text.equals("<>") && !text.equals("{}")) {
                String cleanText = processTextCodes(stripNonStandardCodes(text));
                if (!cleanText.isEmpty()) {
                    String color = resolveColor(doc, this);
                    double fontSize = 2.5 * t[0];
                    double px = tx(midX, t);
                    double py = ty(midY, t);
                    sb.append(String.format(Locale.US,
                            "<text x=\"%.4f\" y=\"%.4f\" font-size=\"%.4f\" fill=\"%s\" " +
                                    "font-family=\"Arial, sans-serif\" text-anchor=\"middle\" stroke=\"none\" " +
                                    "stroke-width=\"0\" fill-opacity=\"0.85\" font-weight=\"400\" " +
                                    "dominant-baseline=\"central\" overflow=\"visible\">%s</text>\n",
                            px, py, fontSize, color, escapeXml(cleanText)));
                }
            }
            return sb.toString();
        }
    }

    // ── LEADER ENTITY ─────────────────────────────────────────────────────────
    static String normalizeDimensionTextStyle(String svg) {
        if (svg == null || svg.isEmpty() || !svg.contains("<text")) return svg;
        return svg.replace("<text ", "<text stroke=\"none\" stroke-width=\"0\" fill-opacity=\"0.85\" font-weight=\"400\" ");
    }

    static class LeaderEntity extends Entity {
        List<double[]> vertices = new ArrayList<>();

        @Override
        String toSvg(DxfDocument doc, double[] t) {
            if (vertices.size() < 2) return "";
            String color = resolveColor(doc, this);
            double lw = resolveLineWeight(doc, layer, lineWeight);
            StringBuilder d = new StringBuilder();
            d.append(String.format(Locale.US, "M %.4f %.4f", tx(vertices.get(0)[0], t), ty(vertices.get(0)[1], t)));
            for (int i = 1; i < vertices.size(); i++)
                d.append(String.format(Locale.US, " L %.4f %.4f", tx(vertices.get(i)[0], t), ty(vertices.get(i)[1], t)));
            
            String safeD = validatePathData(d.toString());
            if (safeD == null) {
                LOG.debug("Skipping invalid leader path on layer {}: {}", layer, d);
                return "";
            }
            
            return String.format(Locale.US,
                "<path d=\"%s\" stroke=\"%s\" stroke-width=\"%.4f\" fill=\"none\"/>",
                d, color, lw);
        }
    }

    static class SolidEntity extends Entity {
        double[] corners = new double[8]; // x1,y1,x2,y2,x3,y3,x4,y4

        @Override
        String toSvg(DxfDocument doc, double[] t) {
            String color = resolveColor(doc, this);
            return String.format(Locale.US,
                "<polygon points=\"%.4f,%.4f %.4f,%.4f %.4f,%.4f %.4f,%.4f\" " +
                "fill=\"%s\" stroke=\"%s\" stroke-width=\"0.5\"/>",
                tx(corners[0], t), ty(corners[1], t),
                tx(corners[2], t), ty(corners[3], t),
                tx(corners[6], t), ty(corners[7], t), // DXF SOLID: 3rd/4th swap
                tx(corners[4], t), ty(corners[5], t),
                color, color);
        }
    }

    // ── DXF Document ──────────────────────────────────────────────────────────
    static class DxfDocument {
        Map<String, Layer> layers = new LinkedHashMap<>();
        Map<String, TextStyle> textStyles = new LinkedHashMap<>();
        Map<String, Block> blocks = new LinkedHashMap<>();
        List<Entity> entities = new ArrayList<>();
        List<Double> proposedPlanTitleXs = new ArrayList<>();
        List<Double> existingPlanTitleXs = new ArrayList<>();
        List<double[]> proposedPlanTitles = new ArrayList<>();
        List<double[]> existingPlanTitles = new ArrayList<>();
        // Drawing extents (from EXTMIN/EXTMAX or computed)
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;
        boolean extentsSet = false;
        // Header vars
        Map<String, double[]> headerVars = new HashMap<>();
        double insUnits = 0; // 0=unitless,1=in,2=ft,4=mm,5=cm,6=m
    }

    // ── Transform helpers ─────────────────────────────────────────────────────
    // t = [scale, translateX, translateY, viewHeight]
    static double tx(double x, double[] t) { return x * t[0] + t[1]; }
    static double ty(double y, double[] t) { return t[3] - (y * t[0] + t[2]); } // Y-flip

    // ── Color / LineWeight resolution ─────────────────────────────────────────
    static String resolveColor(DxfDocument doc, Entity e) {
        return resolveColor(doc, e.layer, e.colorIndex, e.trueColor);
    }

    static String resolveApprovalPlanColor(DxfDocument doc, Entity e, double centerX) {
        if (!isApprovalPlanGeometryLayer(e.layer)) return null;

        return resolveApprovalPlanColor(doc, centerX);
    }

    static String resolveApprovalPlanColor(DxfDocument doc, double centerX) {
        if (!doc.existingPlanTitleXs.isEmpty()) {
            double existingDistance = nearestDistance(centerX, doc.existingPlanTitleXs);
            double proposedDistance = doc.proposedPlanTitleXs.isEmpty()
                    ? Double.MAX_VALUE
                    : nearestDistance(centerX, doc.proposedPlanTitleXs);
            if (existingDistance < proposedDistance) {
                return "#FFFF00";
            }
        }

        if (!doc.proposedPlanTitleXs.isEmpty()) {
            return "#FF0000";
        }
        return null;
    }

    static String resolveApprovalPlanLineColor(DxfDocument doc, Entity e, double centerX, double centerY, double[] t) {
        if (RENDERING_BLOCK_DEFINITION.get()) return null;
        if (isApprovalPlanGeometryLayer(e.layer)) {
            return resolveApprovalPlanColor(doc, centerX);
        }
        if (!"0".equalsIgnoreCase(e.layer)) return null;
        if (doc.proposedPlanTitles.isEmpty() && doc.existingPlanTitles.isEmpty()) return null;

        double svgY = ty(centerY, t);
        if (svgY < 0 || svgY > t[3] * 0.58) return null;

        double[] nearestTitle = nearestApprovalPlanTitle(doc, centerX);
        if (nearestTitle == null) return null;
        double titleSvgY = ty(nearestTitle[1], t);
        if (svgY < titleSvgY + 8.0) return null;

        return resolveApprovalPlanColor(doc, centerX);
    }

    static boolean isApprovalPlanGeometryLayer(String layerName) {
        if (layerName == null) return false;
        String layer = layerName.toUpperCase(Locale.ROOT);
        if (!layer.startsWith("BLK_")) return false;
        if (layer.contains("DOOR") || layer.contains("WINDOW") || layer.contains("VENTILATION")) return false;
        if (layer.contains("FLOOR_HEIGHT") || layer.contains("HT_OF") || layer.contains("PLINTH")) return false;
        if (layer.contains("SETBACK") || layer.contains("LIGHT_VENTILATION")) return false;

        return layer.contains("BLT_UP_AREA")
                || layer.contains("REGULAR_ROOM")
                || layer.contains("KITCHEN")
                || layer.contains("TOILET")
                || layer.contains("STAIR");
    }

    static double[] nearestApprovalPlanTitle(DxfDocument doc, double x) {
        double[] nearest = null;
        double min = Double.MAX_VALUE;
        for (double[] title : doc.proposedPlanTitles) {
            double distance = Math.abs(x - title[0]);
            if (distance < min) {
                min = distance;
                nearest = title;
            }
        }
        for (double[] title : doc.existingPlanTitles) {
            double distance = Math.abs(x - title[0]);
            if (distance < min) {
                min = distance;
                nearest = title;
            }
        }
        return nearest;
    }

    static double nearestDistance(double x, List<Double> anchors) {
        double min = Double.MAX_VALUE;
        for (Double anchor : anchors) {
            if (anchor != null) min = Math.min(min, Math.abs(x - anchor));
        }
        return min;
    }

    static String resolveColor(DxfDocument doc, String layerName, int colorIndex, int trueColor) {
        if (trueColor >= 0) return trueColorToHex(trueColor);
        if (colorIndex == -1 || colorIndex == 256) { // BYLAYER
            // Only use currentColor inside a block symbol definition (for ByBlock inheritance)
            if (RENDERING_BLOCK_DEFINITION.get() && "0".equalsIgnoreCase(layerName)) {
                return "currentColor";
            }
            Layer layer = doc.layers.get(layerName.toUpperCase());
            if (layer != null) {
                if (layer.trueColor >= 0) return trueColorToHex(layer.trueColor);
                colorIndex = layer.colorIndex;
            } else {
                colorIndex = 7; // default black
            }
        } else if (colorIndex == 0) {
            // ByBlock: inside symbol → currentColor, outside → black
            if (RENDERING_BLOCK_DEFINITION.get()) {
                return "currentColor";
            }
            return "#000000"; // outside block = black, not invisible currentColor
        }
        colorIndex = Math.abs(colorIndex);
        if (colorIndex <= 0 || colorIndex >= ACI_COLORS.length) return "#000000";
        String c = ACI_COLORS[colorIndex];
        return c == null ? "#000000" : c;
    }

    static String trueColorToHex(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return String.format("#%02X%02X%02X", r, g, b);
    }

    static double resolveLineWeight(DxfDocument doc, String layerName, double lw) {
        if (lw < 0) {
            Layer layer = doc.layers.get(layerName.toUpperCase());
            if (layer != null && layer.lineWeight >= 0) lw = layer.lineWeight;
        }
        if (lw < 0) return 0.35;
        double resolved = lw * 0.01; // lineweight in hundredths of mm
        // Enforce a minimum visible stroke width — very thin lines disappear at scale
        // DXF lineweight 0 = "default" which should render visibly
        return Math.max(0.35, resolved);
    }

    static String resolveFontFamily(DxfDocument doc, String styleName) {
        TextStyle style = doc.textStyles.get(styleName.toUpperCase());
        if (style != null && !style.fontFile.isEmpty()) {
            return mapDxfFontToWeb(style.fontFile);
        }
        return "Arial, sans-serif";
    }

    static String mapDxfFontToWeb(String dxfFont) {
        String f = dxfFont.toLowerCase().replaceAll("\\.[a-z]+$", "");
        switch (f) {
            case "txt": case "simplex": return "Arial, sans-serif";
            case "romans": case "roman": return "Times New Roman, serif";
            case "romand": return "Times New Roman, serif";
            case "romanc": return "Times New Roman, serif";
            case "italicc": case "italict": return "Arial, sans-serif";
            case "gothice": case "gothicg": case "gothici": return "Georgia, serif";
            case "arial": return "Arial, sans-serif";
            case "times": return "Times New Roman, serif";
            case "courier": return "Courier New, monospace";
            case "verdana": return "Verdana, sans-serif";
            case "calibri": return "Calibri, Arial, sans-serif";
            default: return "Arial, sans-serif";
        }
    }

    static String sanitizeId(String s) {
        return s.replaceAll("[^a-zA-Z0-9_-]", "_");
    }

    static String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    // ── MTEXT Parser ──────────────────────────────────────────────────────────
    /**
     * Parses DXF MTEXT content string which uses {\\f...;} {\\C...;} etc.
     * Codes: \\P=paragraph break, \\n=newline, \\~=non-breaking space,
     *        \\Ffont; \\ffont|b0|i0|c0|p0; = font
     *        \\Hheight; or \\Hheight; = text height
     *        \\Ccolor; = ACI color
     *        \\c color = 24-bit color
     *        \\L \\l = underline on/off
     *        \\O \\o = overline on/off
     *        \\B \\b = bold on/off (non-standard but common)
     *        \\T = tracking
     *        \\Q = oblique angle
     *        \\W = width factor
     *        \\A0=bottom \\A1=center \\A2=top = alignment
     *        { } = group (save/restore)
     */
    static List<MTextSegment> parseMText(String raw, DxfDocument doc, String baseStyle, double baseHeight) {
        List<MTextSegment> result = new ArrayList<>();
        if (raw == null || raw.isEmpty()) return result;

        // Normalize line continuation
        raw = raw.replace("\r\n", "\n").replace("\r", "\n");

        // State stack
        Deque<MTextState> stack = new ArrayDeque<>();
        MTextState state = new MTextState();
        state.color = null;
        state.fontSize = baseHeight;
        state.fontFamily = resolveFontFamily(doc, baseStyle);
        stack.push(state.copy());

        int i = 0;
        StringBuilder buf = new StringBuilder();

        while (i < raw.length()) {
            char c = raw.charAt(i);

            if (c == '{') {
                // Push state
                if (buf.length() > 0) {
                    result.add(stateSegment(buf.toString(), state));
                    buf = new StringBuilder();
                }
                stack.push(state.copy());
                i++;
            } else if (c == '}') {
                if (buf.length() > 0) {
                    result.add(stateSegment(buf.toString(), state));
                    buf = new StringBuilder();
                }
                if (stack.size() > 1) state = stack.pop();
                i++;
            } else if (c == '\\' && i + 1 < raw.length()) {
                char next = raw.charAt(i + 1);
                if (next == '\\') {
                    buf.append('\\'); i += 2;
                } else if (next == '{') {
                    buf.append('{'); i += 2;
                } else if (next == '}') {
                    buf.append('}'); i += 2;
                } else if (next == 'P') {
                    // Paragraph break
                    if (buf.length() > 0) { result.add(stateSegment(buf.toString(), state)); buf = new StringBuilder(); }
                    MTextSegment nl = new MTextSegment(); nl.isNewline = true; result.add(nl);
                    i += 2;
                } else if (next == 'p') {
                    // Paragraph formatting properties, e.g. \pi-0.8,l1.1,t1.1;
                    i += 2;
                    while (i < raw.length() && raw.charAt(i) != ';') i++;
                    if (i < raw.length()) i++;
                } else if (next == 'n') {
                    if (buf.length() > 0) { result.add(stateSegment(buf.toString(), state)); buf = new StringBuilder(); }
                    MTextSegment nl = new MTextSegment(); nl.isNewline = true; result.add(nl);
                    i += 2;
                } else if (next == '~') {
                    buf.append('\u00A0'); i += 2;
                } else if (next == 'L') {
                    state.underline = true; i += 2;
                } else if (next == 'l') {
                    state.underline = false; i += 2;
                } else if (next == 'O') {
                    state.overline = true; i += 2;
                } else if (next == 'o') {
                    state.overline = false; i += 2;
                } else if (next == 'K') {
                    state.strikethrough = true; i += 2;
                } else if (next == 'k') {
                    state.strikethrough = false; i += 2;
                } else if (next == 'B') {
                    state.bold = true; i += 2;
                } else if (next == 'b') {
                    state.bold = false; i += 2;
                } else if (next == 'I') {
                    state.italic = true; i += 2;
                } else if (next == 'i') {
                    state.italic = false; i += 2;
                } else if (next == 'H' || next == 'h') {
                    // \Hvalue; or \Hvaluex;  (x = relative)
                    i += 2;
                    StringBuilder val = new StringBuilder();
                    boolean relative = false;
                    while (i < raw.length() && raw.charAt(i) != ';' && raw.charAt(i) != ' ') {
                        if (raw.charAt(i) == 'x' || raw.charAt(i) == 'X') relative = true;
                        else val.append(raw.charAt(i));
                        i++;
                    }
                    if (i < raw.length() && raw.charAt(i) == ';') i++;
                    try {
                        double h = Double.parseDouble(val.toString());
                        state.fontSize = relative ? state.fontSize * h : h;
                    } catch (NumberFormatException ignored) {}
                } else if (next == 'C' || next == 'c') {
                    if (next == 'C') {
                        // ACI color
                        i += 2;
                        StringBuilder val = new StringBuilder();
                        while (i < raw.length() && raw.charAt(i) != ';') { val.append(raw.charAt(i)); i++; }
                        if (i < raw.length()) i++; // skip ;
                        try {
                            int aci = Integer.parseInt(val.toString().trim());
                            state.color = aci < ACI_COLORS.length && ACI_COLORS[aci] != null
                                ? ACI_COLORS[aci] : "#000000";
                        } catch (NumberFormatException ignored) {}
                    } else {
                        // 24-bit color \cRRGGBB; (BGR in DXF)
                        i += 2;
                        StringBuilder val = new StringBuilder();
                        while (i < raw.length() && raw.charAt(i) != ';') { val.append(raw.charAt(i)); i++; }
                        if (i < raw.length()) i++;
                        try {
                            long bgr = Long.parseLong(val.toString().trim());
                            int r2 = (int)(bgr & 0xFF);
                            int g2 = (int)((bgr >> 8) & 0xFF);
                            int b2 = (int)((bgr >> 16) & 0xFF);
                            state.color = String.format("#%02X%02X%02X", r2, g2, b2);
                        } catch (NumberFormatException ignored) {}
                    }
                } else if (next == 'F' || next == 'f') {
                    // Font: \Ffontname; or \ffontname|b0|i0|c0|p0;
                    i += 2;
                    StringBuilder val = new StringBuilder();
                    while (i < raw.length() && raw.charAt(i) != ';') { val.append(raw.charAt(i)); i++; }
                    if (i < raw.length()) i++;
                    String[] parts2 = val.toString().split("\\|");
                    if (parts2.length > 0) {
                        state.fontFamily = mapDxfFontToWeb(parts2[0].trim());
                        if (parts2.length > 1) state.bold = parts2[1].equalsIgnoreCase("b1");
                        if (parts2.length > 2) state.italic = parts2[2].equalsIgnoreCase("i1");
                    }
                } else if (next == 'T' || next == 'Q' || next == 'W' || next == 'A') {
                    // Tracking, oblique, width, alignment - skip value
                    i += 2;
                    while (i < raw.length() && raw.charAt(i) != ';') i++;
                    if (i < raw.length()) i++;
                } else if (next == 'S') {
                    // Stacking: \Snum/denom; or \Snum#denom; or \Snum^denom;
                    i += 2;
                    StringBuilder val = new StringBuilder();
                    while (i < raw.length() && raw.charAt(i) != ';') { val.append(raw.charAt(i)); i++; }
                    if (i < raw.length()) i++;
                    String sv = val.toString();
                    // Just output numerator/denominator separated by /
                    String[] parts3 = sv.split("[/#^]");
                    if (parts3.length >= 2) buf.append(parts3[0]).append("/").append(parts3[1]);
                    else buf.append(sv);
                } else if (next == 'U' || next == 'u') {
                    // Unicode character \Unnnn;
                    i += 2;
                    StringBuilder val = new StringBuilder();
                    while (i < raw.length() && raw.charAt(i) != ';') { val.append(raw.charAt(i)); i++; }
                    if (i < raw.length()) i++;
                    try {
                        int codePoint = Integer.parseInt(val.toString().trim(), 16);
                        buf.append((char) codePoint);
                    } catch (NumberFormatException ignored) {}
                } else {
                    // Unknown escape - skip
                    i += 2;
                }
            } else if (c == '%' && i + 2 < raw.length() && raw.charAt(i+1) == '%') {
                // AutoCAD special: %%d=°  %%p=±  %%c=⌀  %%o=overline  %%u=underline
                char special = raw.charAt(i + 2);
                switch (special) {
                    case 'd': case 'D': buf.append('°'); break;
                    case 'p': case 'P': buf.append('±'); break;
                    case 'c': case 'C': buf.append('\u2300'); break; // diameter
                    case 'o': case 'O': state.overline = !state.overline; break;
                    case 'u': case 'U': state.underline = !state.underline; break;
                    default: buf.append('%').append('%').append(special);
                }
                i += 3;
            } else if (c == '\n') {
                if (buf.length() > 0) { result.add(stateSegment(buf.toString(), state)); buf = new StringBuilder(); }
                MTextSegment nl = new MTextSegment(); nl.isNewline = true; result.add(nl);
                i++;
            } else {
                buf.append(c);
                i++;
            }
        }
        if (buf.length() > 0) result.add(stateSegment(buf.toString(), state));
        return result;
    }

    static MTextSegment stateSegment(String text, MTextState state) {
        MTextSegment seg = new MTextSegment();
        seg.text = text;
        seg.color = state.color;
        seg.fontSize = state.fontSize;
        seg.bold = state.bold;
        seg.italic = state.italic;
        seg.underline = state.underline;
        seg.fontName = state.fontFamily;
        return seg;
    }

    static class MTextState {
        String color = null;
        double fontSize = 2.5;
        boolean bold = false;
        boolean italic = false;
        boolean underline = false;
        boolean overline = false;
        boolean strikethrough = false;
        String fontFamily = "Arial, sans-serif";

        MTextState copy() {
            MTextState s = new MTextState();
            s.color = color; s.fontSize = fontSize; s.bold = bold;
            s.italic = italic; s.underline = underline; s.overline = overline;
            s.strikethrough = strikethrough; s.fontFamily = fontFamily;
            return s;
        }
    }

    // Process simple TEXT codes (%%d %%p %%c)
    static String processTextCodes(String text) {
        if (text == null) return "";
        return stripNonStandardCodes(
                text
                        .replace("%%d", "°").replace("%%D", "°")
                        .replace("%%p", "±").replace("%%P", "±")
                        .replace("%%c", "\u2300").replace("%%C", "\u2300")
                        .replace("%%U", "").replace("%%u", "") // underline toggle - strip
                        .replace("\\P", "\n").replace("\\p", "\n")
        );
    }
    // ── DXF PARSER ────────────────────────────────────────────────────────────

    public static DxfDocument parseDxf(String filePath) throws IOException {
        return parseDxf(new File(filePath));
    }

    public static DxfDocument parseDxf(File file) throws IOException {
        // Detect encoding (try UTF-8, fallback to ISO-8859-1)
        byte[] bytes = readFileBytes(file);
        String content = tryDecodeUtf8(bytes);

        DxfDocument doc = new DxfDocument();
        // Default layer
        Layer defaultLayer = new Layer();
        defaultLayer.name = "0";
        defaultLayer.colorIndex = 7;
        doc.layers.put("0", defaultLayer);

        // Default text style
        TextStyle defaultStyle = new TextStyle();
        defaultStyle.name = "Standard";
        doc.textStyles.put("STANDARD", defaultStyle);

        List<String[]> tokens = tokenize(content);
        int idx = 0;
        while (idx < tokens.size()) {
            String[] tok = tokens.get(idx);
            if (tok[0].equals("0") && tok[1].equalsIgnoreCase("SECTION")) {
                idx++;
                if (idx >= tokens.size()) break;
                String[] secNameTok = tokens.get(idx);
                String secName = secNameTok[1].toUpperCase();
                idx++;
                if (secName.equals("HEADER")) {
                    idx = parseHeader(tokens, idx, doc);
                } else if (secName.equals("TABLES")) {
                    idx = parseTables(tokens, idx, doc);
                } else if (secName.equals("BLOCKS")) {
                    idx = parseBlocks(tokens, idx, doc);
                } else if (secName.equals("ENTITIES")) {
                    idx = parseEntities(tokens, idx, doc, doc.entities, null);
                } else {
                    // Skip other sections (CLASSES, OBJECTS, etc.)
                    idx = skipSection(tokens, idx);
                }
            } else {
                idx++;
            }
        }

        // Compute extents if not set from header
        if (!doc.extentsSet) {
            computeExtents(doc);
        }
        detectApprovalPlanTitles(doc);

        return doc;
    }

    static void detectApprovalPlanTitles(DxfDocument doc) {
        doc.proposedPlanTitleXs.clear();
        doc.existingPlanTitleXs.clear();
        doc.proposedPlanTitles.clear();
        doc.existingPlanTitles.clear();
        for (Entity entity : doc.entities) {
            if (entity instanceof TextEntity) {
                TextEntity text = (TextEntity) entity;
                collectApprovalPlanTitle(doc, text.text, estimatedTextCenterX(text), text.y);
            } else if (entity instanceof MTextEntity) {
                MTextEntity text = (MTextEntity) entity;
                collectApprovalPlanTitle(doc, text.text, estimatedMTextCenterX(text), text.y);
            }
        }
    }

    static double estimatedTextCenterX(TextEntity text) {
        String value = processTextCodes(text.text);
        return text.x + value.length() * text.height * text.widthFactor * 0.45;
    }

    static double estimatedMTextCenterX(MTextEntity text) {
        String value = processTextCodes(text.text);
        return text.x + value.length() * text.height * 0.45;
    }

    static void collectApprovalPlanTitle(DxfDocument doc, String rawText, double x, double y) {
        if (rawText == null || rawText.isEmpty()) return;
        String text = processTextCodes(rawText).toUpperCase(Locale.ROOT);
        if (!text.contains("FLOOR PLAN")) return;
        if (text.contains("EXISTING")) {
            doc.existingPlanTitleXs.add(x);
            doc.existingPlanTitles.add(new double[]{x, y});
        } else if (text.contains("PROP") || text.contains("PROPOSED")) {
            doc.proposedPlanTitleXs.add(x);
            doc.proposedPlanTitles.add(new double[]{x, y});
        }
    }

    static byte[] readFileBytes(File file) throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] chunk = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(chunk)) != -1) {
                buffer.write(chunk, 0, bytesRead);
            }
            return buffer.toByteArray();
        }
    }

    static String tryDecodeUtf8(byte[] bytes) {
        // Try UTF-8 first
        try {
            String s = new String(bytes, StandardCharsets.UTF_8);
            if (!s.contains("\uFFFD")) return s;
        } catch (Exception ignored) {}
        // Fallback
        return new String(bytes, StandardCharsets.ISO_8859_1);
    }

    static List<String[]> tokenize(String content) {
        List<String[]> tokens = new ArrayList<>();
        String[] lines = content.split("\r?\n");
        for (int i = 0; i + 1 < lines.length; i += 2) {
            String code = lines[i].trim();
            String value = lines[i + 1]; // preserve spaces in value
            // Trim trailing whitespace but not leading (for text)
            value = value.replaceAll("\\s+$", "");
            tokens.add(new String[]{code, value});
        }
        return tokens;
    }

    static int parseHeader(List<String[]> tokens, int idx, DxfDocument doc) {
        String currentVar = "";
        while (idx < tokens.size()) {
            String[] tok = tokens.get(idx);
            if (tok[0].equals("0") && tok[1].equalsIgnoreCase("ENDSEC")) {
                return idx + 1;
            }
            if (tok[0].equals("9")) {
                currentVar = tok[1].toUpperCase();
                doc.headerVars.put(currentVar, new double[]{0, 0, 0});
            } else {
                double[] arr = doc.headerVars.getOrDefault(currentVar, new double[]{0,0,0});
                try {
                    double v = Double.parseDouble(tok[1].trim());
                    int c = Integer.parseInt(tok[0].trim());
                    if (c == 10) { arr[0] = v; }
                    else if (c == 20) { arr[1] = v; }
                    else if (c == 30) { arr[2] = v; }
                    doc.headerVars.put(currentVar, arr);
                } catch (NumberFormatException ignored) {}
            }
            idx++;
        }
        return idx;
    }

    static int parseTables(List<String[]> tokens, int idx, DxfDocument doc) {
        while (idx < tokens.size()) {
            String[] tok = tokens.get(idx);
            if (tok[0].equals("0") && tok[1].equalsIgnoreCase("ENDSEC")) return idx + 1;
            if (tok[0].equals("0") && tok[1].equalsIgnoreCase("TABLE")) {
                idx++;
                if (idx >= tokens.size()) break;
                String tableName = tokens.get(idx)[1].toUpperCase();
                idx++;
                if (tableName.equals("LAYER")) idx = parseLayerTable(tokens, idx, doc);
                else if (tableName.equals("STYLE")) idx = parseStyleTable(tokens, idx, doc);
                else idx = skipTable(tokens, idx);
            } else {
                idx++;
            }
        }
        return idx;
    }

    static int parseLayerTable(List<String[]> tokens, int idx, DxfDocument doc) {
        while (idx < tokens.size()) {
            String[] tok = tokens.get(idx);
            if (tok[0].equals("0") && tok[1].equalsIgnoreCase("ENDTAB")) return idx + 1;
            if (tok[0].equals("0") && tok[1].equalsIgnoreCase("LAYER")) {
                Layer layer = new Layer();
                idx++;
                while (idx < tokens.size()) {
                    tok = tokens.get(idx);
                    if (tok[0].equals("0")) break;
                    int c = Integer.parseInt(tok[0].trim());
                    if (c == 2) layer.name = tok[1].trim();
                    else if (c == 62) {
                        int ci = Integer.parseInt(tok[1].trim());
                        layer.visible = ci >= 0;
                        layer.colorIndex = Math.abs(ci);
                    } else if (c == 6) layer.lineType = tok[1].trim();
                    else if (c == 420) layer.trueColor = Integer.parseInt(tok[1].trim());
                    else if (c == 370) layer.lineWeight = Double.parseDouble(tok[1].trim());
                    idx++;
                }
                doc.layers.put(layer.name.toUpperCase(), layer);
            } else {
                idx++;
            }
        }
        return idx;
    }

    static int parseStyleTable(List<String[]> tokens, int idx, DxfDocument doc) {
        while (idx < tokens.size()) {
            String[] tok = tokens.get(idx);
            if (tok[0].equals("0") && tok[1].equalsIgnoreCase("ENDTAB")) return idx + 1;
            if (tok[0].equals("0") && tok[1].equalsIgnoreCase("STYLE")) {
                TextStyle style = new TextStyle();
                idx++;
                while (idx < tokens.size()) {
                    tok = tokens.get(idx);
                    if (tok[0].equals("0")) break;
                    int c = Integer.parseInt(tok[0].trim());
                    if (c == 2) style.name = tok[1].trim();
                    else if (c == 3) style.fontFile = tok[1].trim();
                    else if (c == 4) style.fontFile = style.fontFile.isEmpty() ? tok[1].trim() : style.fontFile;
                    else if (c == 40) style.height = Double.parseDouble(tok[1].trim());
                    else if (c == 41) style.widthFactor = Double.parseDouble(tok[1].trim());
                    else if (c == 50) style.obliqueAngle = Double.parseDouble(tok[1].trim());
                    idx++;
                }
                doc.textStyles.put(style.name.toUpperCase(), style);
            } else {
                idx++;
            }
        }
        return idx;
    }

    static int skipTable(List<String[]> tokens, int idx) {
        while (idx < tokens.size()) {
            String[] tok = tokens.get(idx);
            idx++;
            if (tok[0].equals("0") && tok[1].equalsIgnoreCase("ENDTAB")) return idx;
        }
        return idx;
    }

    static int parseBlocks(List<String[]> tokens, int idx, DxfDocument doc) {
        while (idx < tokens.size()) {
            String[] tok = tokens.get(idx);
            if (tok[0].equals("0") && tok[1].equalsIgnoreCase("ENDSEC")) return idx + 1;
            if (tok[0].equals("0") && tok[1].equalsIgnoreCase("BLOCK")) {
                Block block = new Block();
                idx++;
                while (idx < tokens.size()) {
                    tok = tokens.get(idx);
                    if (tok[0].equals("0")) break;
                    int c = Integer.parseInt(tok[0].trim());
                    if (c == 2) block.name = tok[1].trim();
                    else if (c == 10) block.baseX = Double.parseDouble(tok[1].trim());
                    else if (c == 20) block.baseY = Double.parseDouble(tok[1].trim());
                    else if (c == 30) block.baseZ = Double.parseDouble(tok[1].trim());
                    idx++;
                }
                idx = parseEntities(tokens, idx, doc, block.entities, block);
                doc.blocks.put(block.name.toUpperCase(), block);
            } else {
                idx++;
            }
        }
        return idx;
    }

    static int parseEntities(List<String[]> tokens, int idx, DxfDocument doc,
                              List<Entity> entityList, Block currentBlock) {
        while (idx < tokens.size()) {
            String[] tok = tokens.get(idx);
            if (tok[0].equals("0")) {
                String type = tok[1].toUpperCase();
                if (type.equals("ENDSEC") || type.equals("ENDBLK")) {
                    return idx + 1;
                }
                // Parse entity
                idx++;
                switch (type) {
                    case "LINE":       { LineEntity e = new LineEntity();       idx = parseLineEntity(tokens, idx, e);       entityList.add(e); break; }
                    case "CIRCLE":     { CircleEntity e = new CircleEntity();   idx = parseCircleEntity(tokens, idx, e);     entityList.add(e); break; }
                    case "ARC":        { ArcEntity e = new ArcEntity();         idx = parseArcEntity(tokens, idx, e);        entityList.add(e); break; }
                    case "ELLIPSE":    { EllipseEntity e = new EllipseEntity(); idx = parseEllipseEntity(tokens, idx, e);   entityList.add(e); break; }
                    case "POLYLINE":   { idx = parsePolyline(tokens, idx, doc, entityList); break; }
                    case "LWPOLYLINE": { PolylineEntity e = new PolylineEntity(); idx = parseLwPolyline(tokens, idx, e);   entityList.add(e); break; }
                    case "SPLINE":     { SplineEntity e = new SplineEntity();   idx = parseSplineEntity(tokens, idx, e);    entityList.add(e); break; }
                    case "HATCH":      { HatchEntity e = new HatchEntity();     idx = parseHatchEntity(tokens, idx, e);     entityList.add(e); break; }
                    case "TEXT":       { TextEntity e = new TextEntity();       idx = parseTextEntity(tokens, idx, e);      entityList.add(e); break; }
                    case "MTEXT":      { MTextEntity e = new MTextEntity();     idx = parseMTextEntity(tokens, idx, e);     entityList.add(e); break; }
                    case "INSERT":     { InsertEntity e = new InsertEntity();   idx = parseInsertEntity(tokens, idx, e);    entityList.add(e); break; }
                    case "DIMENSION":  { DimensionEntity e = new DimensionEntity(); idx = parseDimensionEntity(tokens, idx, e); entityList.add(e); break; }
                    case "LEADER":     { LeaderEntity e = new LeaderEntity();   idx = parseLeaderEntity(tokens, idx, e);    entityList.add(e); break; }
                    case "SOLID":      { SolidEntity e = new SolidEntity();     idx = parseSolidEntity(tokens, idx, e);     entityList.add(e); break; }
                    default:           { idx = skipEntity(tokens, idx); break; }
                }
            } else {
                idx++;
            }
        }
        return idx;
    }

    // Generic common entity fields reader
    static int readCommonFields(List<String[]> tokens, int idx, Entity e) {
        // Already at first field after entity type
        return idx;
    }

    static void applyCommonCode(String[] tok, Entity e) {
        try {
            int c = Integer.parseInt(tok[0].trim());
            if (c == 8) e.layer = tok[1].trim();
            else if (c == 62) e.colorIndex = Integer.parseInt(tok[1].trim());
            else if (c == 420) e.trueColor = Integer.parseInt(tok[1].trim());
            else if (c == 6) e.lineType = tok[1].trim();
            else if (c == 370) e.lineWeight = Double.parseDouble(tok[1].trim());
            else if (c == 60) e.visible = Integer.parseInt(tok[1].trim()) == 0;
        } catch (NumberFormatException ignored) {}
    }

    static int parseLineEntity(List<String[]> tokens, int idx, LineEntity e) {
        while (idx < tokens.size() && !tokens.get(idx)[0].equals("0")) {
            String[] tok = tokens.get(idx);
            applyCommonCode(tok, e);
            try {
                int c = Integer.parseInt(tok[0].trim());
                if (c == 10) e.x1 = Double.parseDouble(tok[1].trim());
                else if (c == 20) e.y1 = Double.parseDouble(tok[1].trim());
                else if (c == 30) e.z1 = Double.parseDouble(tok[1].trim());
                else if (c == 11) e.x2 = Double.parseDouble(tok[1].trim());
                else if (c == 21) e.y2 = Double.parseDouble(tok[1].trim());
                else if (c == 31) e.z2 = Double.parseDouble(tok[1].trim());
            } catch (NumberFormatException ignored) {}
            idx++;
        }
        return idx;
    }

    static int parseCircleEntity(List<String[]> tokens, int idx, CircleEntity e) {
        while (idx < tokens.size() && !tokens.get(idx)[0].equals("0")) {
            String[] tok = tokens.get(idx);
            applyCommonCode(tok, e);
            try {
                int c = Integer.parseInt(tok[0].trim());
                if (c == 10) e.cx = Double.parseDouble(tok[1].trim());
                else if (c == 20) e.cy = Double.parseDouble(tok[1].trim());
                else if (c == 30) e.cz = Double.parseDouble(tok[1].trim());
                else if (c == 40) e.radius = Double.parseDouble(tok[1].trim());
            } catch (NumberFormatException ignored) {}
            idx++;
        }
        return idx;
    }

    static int parseArcEntity(List<String[]> tokens, int idx, ArcEntity e) {
        while (idx < tokens.size() && !tokens.get(idx)[0].equals("0")) {
            String[] tok = tokens.get(idx);
            applyCommonCode(tok, e);
            try {
                int c = Integer.parseInt(tok[0].trim());
                if (c == 10) e.cx = Double.parseDouble(tok[1].trim());
                else if (c == 20) e.cy = Double.parseDouble(tok[1].trim());
                else if (c == 30) e.cz = Double.parseDouble(tok[1].trim());
                else if (c == 40) e.radius = Double.parseDouble(tok[1].trim());
                else if (c == 50) e.startAngle = Double.parseDouble(tok[1].trim());
                else if (c == 51) e.endAngle = Double.parseDouble(tok[1].trim());
            } catch (NumberFormatException ignored) {}
            idx++;
        }
        return idx;
    }

    static int parseEllipseEntity(List<String[]> tokens, int idx, EllipseEntity e) {
        while (idx < tokens.size() && !tokens.get(idx)[0].equals("0")) {
            String[] tok = tokens.get(idx);
            applyCommonCode(tok, e);
            try {
                int c = Integer.parseInt(tok[0].trim());
                if (c == 10) e.cx = Double.parseDouble(tok[1].trim());
                else if (c == 20) e.cy = Double.parseDouble(tok[1].trim());
                else if (c == 30) e.cz = Double.parseDouble(tok[1].trim());
                else if (c == 11) e.majorX = Double.parseDouble(tok[1].trim());
                else if (c == 21) e.majorY = Double.parseDouble(tok[1].trim());
                else if (c == 40) e.ratio = Double.parseDouble(tok[1].trim());
                else if (c == 41) e.startParam = Double.parseDouble(tok[1].trim());
                else if (c == 42) e.endParam = Double.parseDouble(tok[1].trim());
            } catch (NumberFormatException ignored) {}
            idx++;
        }
        if (e.endParam == 0) e.endParam = 2 * Math.PI;
        return idx;
    }

    static int parsePolyline(List<String[]> tokens, int idx, DxfDocument doc, List<Entity> entityList) {
        PolylineEntity poly = new PolylineEntity();
        while (idx < tokens.size() && !tokens.get(idx)[0].equals("0")) {
            String[] tok = tokens.get(idx);
            applyCommonCode(tok, poly);
            try {
                int c = Integer.parseInt(tok[0].trim());
                if (c == 70) {
                    int flags = Integer.parseInt(tok[1].trim());
                    poly.closed = (flags & 1) != 0;
                }
            } catch (NumberFormatException ignored) {}
            idx++;
        }
        // Read VERTEX entities
        while (idx < tokens.size()) {
            String[] tok = tokens.get(idx);
            if (tok[0].equals("0")) {
                if (tok[1].equalsIgnoreCase("VERTEX")) {
                    idx++;
                    double[] v = new double[]{0, 0, 0};
                    while (idx < tokens.size() && !tokens.get(idx)[0].equals("0")) {
                        tok = tokens.get(idx);
                        try {
                            int c = Integer.parseInt(tok[0].trim());
                            if (c == 10) v[0] = Double.parseDouble(tok[1].trim());
                            else if (c == 20) v[1] = Double.parseDouble(tok[1].trim());
                            else if (c == 42) v[2] = Double.parseDouble(tok[1].trim());
                        } catch (NumberFormatException ignored) {}
                        idx++;
                    }
                    poly.vertices.add(v);
                } else if (tok[1].equalsIgnoreCase("SEQEND")) {
                    idx++;
                    break;
                } else {
                    break;
                }
            } else {
                idx++;
            }
        }
        entityList.add(poly);
        return idx;
    }

    static int parseLwPolyline(List<String[]> tokens, int idx, PolylineEntity e) {
        double curX = 0, curY = 0, curBulge = 0;
        boolean firstVertex = true;
        while (idx < tokens.size() && !tokens.get(idx)[0].equals("0")) {
            String[] tok = tokens.get(idx);
            applyCommonCode(tok, e);
            try {
                int c = Integer.parseInt(tok[0].trim());
                if (c == 70) { int flags = Integer.parseInt(tok[1].trim()); e.closed = (flags & 1) != 0; }
                else if (c == 10) {
                    if (!firstVertex) e.vertices.add(new double[]{curX, curY, curBulge});
                    curX = Double.parseDouble(tok[1].trim()); curBulge = 0; firstVertex = false;
                }
                else if (c == 20) curY = Double.parseDouble(tok[1].trim());
                else if (c == 42) curBulge = Double.parseDouble(tok[1].trim());
            } catch (NumberFormatException ignored) {}
            idx++;
        }
        e.vertices.add(new double[]{curX, curY, curBulge});
        return idx;
    }

    static int parseSplineEntity(List<String[]> tokens, int idx, SplineEntity e) {
        while (idx < tokens.size() && !tokens.get(idx)[0].equals("0")) {
            String[] tok = tokens.get(idx);
            applyCommonCode(tok, e);
            try {
                int c = Integer.parseInt(tok[0].trim());
                if (c == 71) e.degree = Integer.parseInt(tok[1].trim());
                else if (c == 70) { int flags = Integer.parseInt(tok[1].trim()); e.closed = (flags & 1) != 0; }
                else if (c == 10) {
                    double x = Double.parseDouble(tok[1].trim());
                    // peek next tokens for y,z
                    double y = 0, z = 0;
                    if (idx+1 < tokens.size() && tokens.get(idx+1)[0].trim().equals("20")) {
                        idx++; y = Double.parseDouble(tokens.get(idx)[1].trim());
                    }
                    if (idx+1 < tokens.size() && tokens.get(idx+1)[0].trim().equals("30")) {
                        idx++; z = Double.parseDouble(tokens.get(idx)[1].trim());
                    }
                    e.controlPoints.add(new double[]{x, y, z});
                } else if (c == 11) {
                    double x = Double.parseDouble(tok[1].trim());
                    double y = 0;
                    if (idx+1 < tokens.size() && tokens.get(idx+1)[0].trim().equals("21")) {
                        idx++; y = Double.parseDouble(tokens.get(idx)[1].trim());
                    }
                    e.fitPoints.add(new double[]{x, y});
                }
            } catch (NumberFormatException ignored) {}
            idx++;
        }
        return idx;
    }

    static int parseHatchEntity(List<String[]> tokens, int idx, HatchEntity e) {
        double pendingX = 0;
        boolean waitingForY = false;
        boolean inBoundaryData = false;
        List<double[]> currentLoop = null;
        while (idx < tokens.size() && !tokens.get(idx)[0].equals("0")) {
            String[] tok = tokens.get(idx);
            applyCommonCode(tok, e);
            try {
                int c = Integer.parseInt(tok[0].trim());
                if (c == 2) e.patternName = tok[1].trim();
                else if (c == 70) e.solid = Integer.parseInt(tok[1].trim()) == 1;
                else if (c == 52) e.angle = Double.parseDouble(tok[1].trim());
                else if (c == 41) e.scale = Double.parseDouble(tok[1].trim());
                else if (c == 91) inBoundaryData = Integer.parseInt(tok[1].trim()) > 0;
                else if (c == 92 && inBoundaryData) {
                    if (currentLoop != null && !currentLoop.isEmpty()) {
                        e.boundaryLoops.add(currentLoop);
                    }
                    currentLoop = new ArrayList<>();
                } else if (c == 10 && inBoundaryData) {
                    pendingX = Double.parseDouble(tok[1].trim());
                    waitingForY = true;
                } else if (c == 20 && waitingForY && currentLoop != null) {
                    currentLoop.add(new double[]{pendingX, Double.parseDouble(tok[1].trim())});
                    waitingForY = false;
                }
            } catch (NumberFormatException ignored) {}
            idx++;
        }
        if (currentLoop != null && !currentLoop.isEmpty()) {
            e.boundaryLoops.add(currentLoop);
        }
        return idx;
    }

    static int parseTextEntity(List<String[]> tokens, int idx, TextEntity e) {
        while (idx < tokens.size() && !tokens.get(idx)[0].equals("0")) {
            String[] tok = tokens.get(idx);
            applyCommonCode(tok, e);
            try {
                int c = Integer.parseInt(tok[0].trim());
                if (c == 10) e.x = Double.parseDouble(tok[1].trim());
                else if (c == 20) e.y = Double.parseDouble(tok[1].trim());
                else if (c == 30) e.z = Double.parseDouble(tok[1].trim());
                else if (c == 11) e.secondX = Double.parseDouble(tok[1].trim());
                else if (c == 21) e.secondY = Double.parseDouble(tok[1].trim());
                else if (c == 40) e.height = Double.parseDouble(tok[1].trim());
                else if (c == 1) e.text = stripNonStandardCodes(tok[1]);
                else if (c == 50) e.rotation = Double.parseDouble(tok[1].trim());
                else if (c == 41) e.widthFactor = Double.parseDouble(tok[1].trim());
                else if (c == 51) e.obliqueAngle = Double.parseDouble(tok[1].trim());
                else if (c == 7) e.styleName = tok[1].trim();
                else if (c == 71) e.textFlags = Integer.parseInt(tok[1].trim()); // Text flags: bit 2=RTL, bit 3=vertical
                else if (c == 72) e.hJustify = Integer.parseInt(tok[1].trim());
                else if (c == 73) e.vJustify = Integer.parseInt(tok[1].trim());
            } catch (NumberFormatException ignored) {}
            idx++;
        }
        // Use alignment point if specified
        if (!Double.isNaN(e.secondX) && !Double.isNaN(e.secondY)
                && (e.hJustify > 0 || e.vJustify > 0)
                && (Math.abs(e.secondX - e.x) > 1e-6 || Math.abs(e.secondY - e.y) > 1e-6)) {
            e.x = e.secondX;
            e.y = e.secondY;
        }
        return idx;
    }

    static int parseMTextEntity(List<String[]> tokens, int idx, MTextEntity e) {
        StringBuilder textBuf = new StringBuilder();
        boolean sawMTextSubclass = false;
        boolean inMTextSubclass = false;
        boolean ignoreNestedData = false;
        while (idx < tokens.size() && !tokens.get(idx)[0].equals("0")) {
            String[] tok = tokens.get(idx);
            try {
                int c = Integer.parseInt(tok[0].trim());
                String value = tok[1].trim();

                if (c == 100) {
                    if ("AcDbMText".equalsIgnoreCase(value)) {
                        sawMTextSubclass = true;
                        inMTextSubclass = true;
                        ignoreNestedData = false;
                    } else if (sawMTextSubclass) {
                        inMTextSubclass = false;
                        ignoreNestedData = true;
                    } else {
                        applyCommonCode(tok, e);
                    }
                    idx++;
                    continue;
                }

                // Some DXF writers append an embedded object payload after the
                // real AcDbMText data. Its group codes can repeat 10/20/40/41
                // and 11/21 values, but those are not the text anchor, height,
                // width, or direction vector. Stop consuming MTEXT geometry
                // once that nested payload begins.
                if (c == 101 && sawMTextSubclass) {
                    inMTextSubclass = false;
                    ignoreNestedData = true;
                    idx++;
                    continue;
                }

                if (!sawMTextSubclass) {
                    applyCommonCode(tok, e);
                }

                boolean readMTextCode = !ignoreNestedData && (!sawMTextSubclass || inMTextSubclass);
                if (!readMTextCode) {
                    idx++;
                    continue;
                }

                if (c == 10) e.x = Double.parseDouble(tok[1].trim());
                else if (c == 20) e.y = Double.parseDouble(tok[1].trim());
                else if (c == 30) e.z = Double.parseDouble(tok[1].trim());
                else if (c == 11) { e.xDirX = Double.parseDouble(tok[1].trim()); e.xDir = "set"; }
                else if (c == 21) { e.xDirY = Double.parseDouble(tok[1].trim()); }
                else if (c == 40) e.height = Double.parseDouble(tok[1].trim());
                else if (c == 41) e.width = Double.parseDouble(tok[1].trim());
                else if (c == 71) e.attachment = Integer.parseInt(tok[1].trim());
                else if (c == 50) e.rotation = Double.parseDouble(tok[1].trim());
                else if (c == 7)  e.styleName = tok[1].trim();
                else if (c == 44) e.lineSpacing = Double.parseDouble(tok[1].trim());
                else if (c == 74) e.textDirection = Integer.parseInt(tok[1].trim()); // Text direction: 0=LTR, 1=RTL, 3=vertical
                else if (c == 1 || c == 3) {
                    // Group 3 is continuation of text (when > 250 chars)
                    textBuf.append(tok[1].replaceAll("^[a-zA-Z][a-zA-Z0-9,-]*;", "")); // strip leading codes
                }
            } catch (NumberFormatException ignored) {}
            idx++;
        }
        e.text = textBuf.toString();
        if ("set".equals(e.xDir) && (Math.abs(e.xDirX) > 1e-9 || Math.abs(e.xDirY) > 1e-9)) {
            e.rotation = Math.toDegrees(Math.atan2(e.xDirY, e.xDirX));
        }
        return idx;
    }

    static int parseInsertEntity(List<String[]> tokens, int idx, InsertEntity e) {
        while (idx < tokens.size() && !tokens.get(idx)[0].equals("0")) {
            String[] tok = tokens.get(idx);
            applyCommonCode(tok, e);
            try {
                int c = Integer.parseInt(tok[0].trim());
                if (c == 2)  e.blockName = tok[1].trim();
                else if (c == 10) e.x = Double.parseDouble(tok[1].trim());
                else if (c == 20) e.y = Double.parseDouble(tok[1].trim());
                else if (c == 30) e.z = Double.parseDouble(tok[1].trim());
                else if (c == 41) e.scaleX = Double.parseDouble(tok[1].trim());
                else if (c == 42) e.scaleY = Double.parseDouble(tok[1].trim());
                else if (c == 43) e.scaleZ = Double.parseDouble(tok[1].trim());
                else if (c == 50) e.rotation = Double.parseDouble(tok[1].trim());
                else if (c == 70) e.cols = Integer.parseInt(tok[1].trim());
                else if (c == 71) e.rows = Integer.parseInt(tok[1].trim());
                else if (c == 44) e.colSpacing = Double.parseDouble(tok[1].trim());
                else if (c == 45) e.rowSpacing = Double.parseDouble(tok[1].trim());
            } catch (NumberFormatException ignored) {}
            idx++;
        }
        return idx;
    }

    static int parseDimensionEntity(List<String[]> tokens, int idx, DimensionEntity e) {
        while (idx < tokens.size() && !tokens.get(idx)[0].equals("0")) {
            String[] tok = tokens.get(idx);
            applyCommonCode(tok, e);
            try {
                int c = Integer.parseInt(tok[0].trim());
                if (c == 2)  e.dimensionBlockName = tok[1].trim();
                else if (c == 1) e.text = tok[1].trim();
                else if (c == 10) e.defX = Double.parseDouble(tok[1].trim());
                else if (c == 20) e.defY = Double.parseDouble(tok[1].trim());
                else if (c == 11) e.midX = Double.parseDouble(tok[1].trim());
                else if (c == 21) e.midY = Double.parseDouble(tok[1].trim());
                else if (c == 13) e.leaderX = Double.parseDouble(tok[1].trim());
                else if (c == 23) e.leaderY = Double.parseDouble(tok[1].trim());
                else if (c == 70) e.type = Integer.parseInt(tok[1].trim());
            } catch (NumberFormatException ignored) {}
            idx++;
        }
        return idx;
    }

    static int parseLeaderEntity(List<String[]> tokens, int idx, LeaderEntity e) {
        while (idx < tokens.size() && !tokens.get(idx)[0].equals("0")) {
            String[] tok = tokens.get(idx);
            applyCommonCode(tok, e);
            try {
                int c = Integer.parseInt(tok[0].trim());
                if (c == 10) {
                    double x = Double.parseDouble(tok[1].trim());
                    double y = 0;
                    if (idx+1 < tokens.size() && tokens.get(idx+1)[0].trim().equals("20")) {
                        idx++; y = Double.parseDouble(tokens.get(idx)[1].trim());
                    }
                    e.vertices.add(new double[]{x, y});
                }
            } catch (NumberFormatException ignored) {}
            idx++;
        }
        return idx;
    }

    static int parseSolidEntity(List<String[]> tokens, int idx, SolidEntity e) {
        while (idx < tokens.size() && !tokens.get(idx)[0].equals("0")) {
            String[] tok = tokens.get(idx);
            applyCommonCode(tok, e);
            try {
                int c = Integer.parseInt(tok[0].trim());
                if (c == 10) e.corners[0] = Double.parseDouble(tok[1].trim());
                else if (c == 20) e.corners[1] = Double.parseDouble(tok[1].trim());
                else if (c == 11) e.corners[2] = Double.parseDouble(tok[1].trim());
                else if (c == 21) e.corners[3] = Double.parseDouble(tok[1].trim());
                else if (c == 12) e.corners[4] = Double.parseDouble(tok[1].trim());
                else if (c == 22) e.corners[5] = Double.parseDouble(tok[1].trim());
                else if (c == 13) e.corners[6] = Double.parseDouble(tok[1].trim());
                else if (c == 23) e.corners[7] = Double.parseDouble(tok[1].trim());
            } catch (NumberFormatException ignored) {}
            idx++;
        }
        return idx;
    }

    static int skipEntity(List<String[]> tokens, int idx) {
        while (idx < tokens.size() && !tokens.get(idx)[0].equals("0")) idx++;
        return idx;
    }

    static int skipSection(List<String[]> tokens, int idx) {
        while (idx < tokens.size()) {
            String[] tok = tokens.get(idx);
            idx++;
            if (tok[0].equals("0") && tok[1].equalsIgnoreCase("ENDSEC")) return idx;
        }
        return idx;
    }

    // ── Extents computation ───────────────────────────────────────────────────
    static void computeExtents(DxfDocument doc) {
        // Try from HEADER vars first
        double[] extMin = doc.headerVars.get("$EXTMIN");
        double[] extMax = doc.headerVars.get("$EXTMAX");
        if (extMin != null && extMax != null
            && extMax[0] > extMin[0] && extMax[1] > extMin[1]
            && Math.abs(extMin[0]) < 1e15 && Math.abs(extMax[0]) < 1e15) {
            doc.minX = extMin[0]; doc.minY = extMin[1];
            doc.maxX = extMax[0]; doc.maxY = extMax[1];
            doc.extentsSet = true;
            return;
        }
        // Compute from entities
        EntityExtentVisitor visitor = new EntityExtentVisitor();
        for (Entity e : doc.entities) visitor.visit(e);
        for (Block b : doc.blocks.values())
            for (Entity e : b.entities) visitor.visit(e);
        if (visitor.valid) {
            doc.minX = visitor.minX; doc.minY = visitor.minY;
            doc.maxX = visitor.maxX; doc.maxY = visitor.maxY;
            doc.extentsSet = true;
        } else {
            // Fallback
            doc.minX = 0; doc.minY = 0; doc.maxX = 100; doc.maxY = 100;
        }
    }

    static class EntityExtentVisitor {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        boolean valid = false;

        void add(double x, double y) {
            if (Double.isInfinite(x) || Double.isInfinite(y) || Double.isNaN(x) || Double.isNaN(y)) return;
            if (Math.abs(x) > 1e12 || Math.abs(y) > 1e12) return;
            minX = Math.min(minX, x); minY = Math.min(minY, y);
            maxX = Math.max(maxX, x); maxY = Math.max(maxY, y);
            valid = true;
        }

        void visit(Entity e) {
            if (e instanceof LineEntity) {
                LineEntity l = (LineEntity) e; add(l.x1, l.y1); add(l.x2, l.y2);
            } else if (e instanceof CircleEntity) {
                CircleEntity c = (CircleEntity) e;
                add(c.cx - c.radius, c.cy - c.radius); add(c.cx + c.radius, c.cy + c.radius);
            } else if (e instanceof ArcEntity) {
                ArcEntity a = (ArcEntity) e;
                add(a.cx - a.radius, a.cy - a.radius); add(a.cx + a.radius, a.cy + a.radius);
            } else if (e instanceof PolylineEntity) {
                for (double[] v : ((PolylineEntity) e).vertices) add(v[0], v[1]);
            } else if (e instanceof TextEntity) {
                TextEntity t = (TextEntity) e; add(t.x, t.y);
            } else if (e instanceof MTextEntity) {
                MTextEntity m = (MTextEntity) e; add(m.x, m.y);
            } else if (e instanceof InsertEntity) {
                InsertEntity ins = (InsertEntity) e; add(ins.x, ins.y);
            }
        }
    }

    // ── SVG GENERATOR ─────────────────────────────────────────────────────────

    public static String generateSvg(DxfDocument doc, int targetWidthPx, int targetHeightPx) {
        // Compute transform
        double dxfW = doc.maxX - doc.minX;
        double dxfH = doc.maxY - doc.minY;
        if (dxfW <= 0) dxfW = 1;
        if (dxfH <= 0) dxfH = 1;

        // Add 2% margin
        double margin = 0.02;
        double mw = dxfW * margin;
        double mh = dxfH * margin;
        double adjMinX = doc.minX - mw, adjMinY = doc.minY - mh;
        double adjMaxX = doc.maxX + mw, adjMaxY = doc.maxY + mh;
        dxfW = adjMaxX - adjMinX;
        dxfH = adjMaxY - adjMinY;

        // Fit to target keeping aspect ratio
        double scale = Math.min(targetWidthPx / dxfW, targetHeightPx / dxfH);
        double svgW = dxfW * scale;
        double svgH = dxfH * scale;

        // transform: [scale, translateX, translateY, viewHeight]
        // tx(x) = (x - adjMinX) * scale
        // ty(y) = svgH - (y - adjMinY) * scale
        double[] t = new double[]{scale, -adjMinX * scale, -adjMinY * scale, svgH};

        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.US,
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<svg xmlns=\"http://www.w3.org/2000/svg\" " +
            "xmlns:xlink=\"http://www.w3.org/1999/xlink\" " +
            "width=\"%.2fpx\" height=\"%.2fpx\" " +
            "viewBox=\"0 0 %.4f %.4f\">\n",
            svgW, svgH, svgW, svgH));

        // Defs: block symbols + arrowhead marker
        sb.append("<defs>\n");
        sb.append("<marker id=\"arrow\" markerWidth=\"10\" markerHeight=\"7\" " +
                  "refX=\"10\" refY=\"3.5\" orient=\"auto\">" +
                  "<polygon points=\"0 0, 10 3.5, 0 7\" fill=\"#000000\"/></marker>\n");

            // Render block definitions
    // FIND this entire defs block loop and REPLACE:
    for (Map.Entry<String, Block> entry : doc.blocks.entrySet()) {
        Block block = entry.getValue();
        // Skip only true model/paper space blocks
        if (block.name.equalsIgnoreCase("*Model_Space") ||
                block.name.equalsIgnoreCase("*Paper_Space") ||
                block.name.toLowerCase().startsWith("*paper_space")) continue;

        String safeId = sanitizeId(block.name);
        sb.append(String.format("<symbol id=\"block_%s\" overflow=\"visible\">\n", safeId));

        // Block symbols must be local to their own base point. Do not include
        // the drawing/page translation here, because INSERT applies that when
        // the symbol is reused at the actual DXF insertion point.
        double[] blockT = new double[]{
                t[0],
                -block.baseX * t[0],
                -block.baseY * t[0],
                0
        };

        // Set flag to indicate we're rendering inside a block definition
        RENDERING_BLOCK_DEFINITION.set(true);
        try {
            for (Entity e : block.entities) {
                if (!e.visible) continue;
                try {
                    String svg = e.toSvg(doc, blockT);
                    if (!svg.isEmpty()) sb.append("  ").append(svg).append("\n");
                } catch (Exception ex) {
                    System.err.println("Block entity error in " + block.name + ": " + ex.getMessage());
                }
            }
        } finally {
            // Always reset the flag after rendering block entities
            RENDERING_BLOCK_DEFINITION.remove();
        }
        sb.append("</symbol>\n");
    }
    sb.append("</defs>\n");

        // White background
        sb.append(String.format(Locale.US,
            "<rect width=\"%.4f\" height=\"%.4f\" fill=\"white\"/>\n", svgW, svgH));

        // Render entities layer by layer (visible layers first, then by draw order)
        // Collect layer names in order
        Set<String> visibleLayers = new LinkedHashSet<>();
        for (Entity e : doc.entities) {
            Layer lay = doc.layers.get(e.layer.toUpperCase());
            if (lay == null || lay.visible) visibleLayers.add(e.layer);
        }

        // Group by layer
        Map<String, List<Entity>> byLayer = new LinkedHashMap<>();
        for (Entity e : doc.entities) {
            Layer lay = doc.layers.get(e.layer.toUpperCase());
            if (lay != null && !lay.visible) continue;
            byLayer.computeIfAbsent(e.layer, k -> new ArrayList<>()).add(e);
        }

        for (Map.Entry<String, List<Entity>> entry : byLayer.entrySet()) {
            String layerName = entry.getKey();
            sb.append(String.format("<g id=\"layer_%s\">\n", sanitizeId(layerName)));
            for (Entity e : entry.getValue()) {
                if (!e.visible) continue;
                try {
                    String svg = e.toSvg(doc, t);
                    if (!svg.isEmpty()) sb.append("  ").append(svg).append("\n");
                } catch (Exception ex) {
                    System.err.println("Warning: entity render error on layer " + layerName + ": " + ex.getMessage());
                }
            }
            sb.append("</g>\n");
        }

        sb.append("</svg>\n");
        return sb.toString();
    }

    static String stripNonStandardCodes(String text) {
        if (text == null) return "";
        return text
                // ^I is a tab in DXF MTEXT — replace with space
                .replace("^I", " ")
                // %%U/%%u = underline toggle — strip
                .replace("%%U", "").replace("%%u", "")
                // Strip CAD-specific italic/indent codes: i-12.64; i0.6218; i-3.462; etc.
                // Must NOT strip things like "1IN12" — only codes ending in ;
                .replaceAll("(?<![\\w])([a-zA-Z]{1,3}[+-]?[0-9]*\\.?[0-9]+(?:,[a-zA-Z]{1,3}[+-]?[0-9]*\\.?[0-9]+)*);", "")
                // Strip reset codes like i0,l0,tz;
                .replaceAll("(?<![\\w=])[a-zA-Z]{1,2}[0-9]?(?:,[a-zA-Z]{1,2}[0-9]?)+;", "")
                // Collapse multiple spaces
                .replaceAll("  +", " ")
                .trim();
    }

    // ── MAIN ENTRY POINT ──────────────────────────────────────────────────────

//    public static void main(String[] args) throws Exception {
//        if (args.length < 2) {
//            LOG.info("Usage: com.dxf.reader.service.DxfToPdfConverter <input.dxf> <output.pdf> [widthPx] [heightPx] [saveSvg]");
//            LOG.info("Example: com.dxf.reader.service.DxfToPdfConverter drawing.dxf output.pdf 2480 3508 false");
//            System.exit(1);
//        }
//
//        String inputDxf = args[0];
//        String outputPdf = args[1];
//        int width  = args.length > 2 ? Integer.parseInt(args[2]) : 3508; // A3 at 300dpi
//        int height = args.length > 3 ? Integer.parseInt(args[3]) : 2480;
//        boolean saveSvg = args.length > 4 && Boolean.parseBoolean(args[4]);
//
//        LOG.info("Parsing DXF: " + inputDxf);
//        DxfDocument doc = parseDxf(inputDxf);
//        LOG.info("Extents: (%.2f, %.2f) to (%.2f, %.2f)%n",
//            doc.minX, doc.minY, doc.maxX, doc.maxY);
//        LOG.info("Entities: %d, Blocks: %d, Layers: %d%n",
//            doc.entities.size(), doc.blocks.size(), doc.layers.size());
//
//        LOG.info("Generating SVG...");
//        String svg = generateSvg(doc, width, height);
//
//        // Convert SVG -> PDF using Apache Batik + FOP
//        LOG.info("Converting SVG to PDF...");
//        convertSvgToPdf(svg, new File(outputPdf), saveSvg);
//        LOG.info("PDF saved: " + outputPdf);
//    }

    public static void convertDxfToPdf(InputStream dxfInputStream, File outputPdfFile) throws Exception {
        convertDxfToPdf(dxfInputStream, outputPdfFile, false);
    }

    public static void convertDxfToPdf(InputStream dxfInputStream, File outputPdfFile, boolean saveSvg)
            throws Exception {

        int width  = 3508; // A3 at 300dpi
        int height = 2480;

        File tempDxf = null;
        try {
            // --- Step 1: Write InputStream → temp DXF file ---
            tempDxf = File.createTempFile("upload_", ".dxf");
            try (FileOutputStream fos = new FileOutputStream(tempDxf)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = dxfInputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
            LOG.info("DXF written to temp file: {}", tempDxf.getAbsolutePath());

            // --- Step 2: Parse DXF ---
            LOG.info("Parsing DXF: {}", tempDxf.getAbsolutePath());
            DxfDocument doc;
            try {
                doc = parseDxf(tempDxf.getAbsolutePath());
            } catch (Exception e) {
                LOG.error("Failed to parse DXF file '{}': {}", tempDxf.getAbsolutePath(), e.getMessage(), e);
                throw new Exception("DXF parsing failed: " + e.getMessage(), e);
            }

            LOG.info("Extents: ({}, {}) to ({}, {})",
                    doc.minX, doc.minY, doc.maxX, doc.maxY);
            LOG.info("Entities: {}, Blocks: {}, Layers: {}",
                    doc.entities.size(), doc.blocks.size(), doc.layers.size());

            // --- Step 3: Generate SVG in memory ---
            LOG.info("Generating SVG...");
            String svg;
            try {
                svg = generateSvg(doc, width, height);
            } catch (Exception e) {
                LOG.error("Failed to generate SVG from DXF: {}", e.getMessage(), e);
                throw new Exception("SVG generation failed: " + e.getMessage(), e);
            }
            
            // sanitize before handing to Batik
            LOG.info("Sanitizing SVG paths...");
            svg = sanitizeSvgPaths(svg);

            // --- Step 4: Convert SVG → PDF ---
            LOG.info("Converting SVG to PDF...");
            try {
                convertSvgToPdf(svg, outputPdfFile, saveSvg);
            } catch (Exception e) {
                LOG.error("Failed to convert SVG to PDF '{}': {}", outputPdfFile.getAbsolutePath(), e.getMessage(), e);
                throw new Exception("SVG→PDF conversion failed: " + e.getMessage(), e);
            }

            LOG.info("PDF saved: {}", outputPdfFile.getAbsolutePath());

        } finally {
            // Always clean up temp DXF
            if (tempDxf != null && tempDxf.exists() && !tempDxf.delete()) {
                LOG.warn("Temp DXF file not deleted: {}", tempDxf.getAbsolutePath());
                tempDxf.deleteOnExit();
            }
        }
    }

    private static void convertSvgToPdf(String svg, File outputPdfFile, boolean saveSvg) throws Exception {

        // If saveSvg=true  → save alongside the PDF with .svg extension (kept after method returns)
        // If saveSvg=false → write to a temp file, deleted in finally block
        File svgFile = saveSvg
                ? new File(outputPdfFile.getAbsolutePath().replaceAll("\\.pdf$", ".svg"))
                : File.createTempFile("dxf-reader-", ".svg");

        try {
            // Write SVG string to file (temp or named)
            try (OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(svgFile), StandardCharsets.UTF_8)) {
                writer.write(svg);
            }

            if (saveSvg) {
                LOG.info("SVG saved (saveSvg=true): {}", svgFile.getAbsolutePath());
            } else {
                LOG.debug("SVG written to temp file: {}", svgFile.getAbsolutePath());
            }

            // Convert SVG file → PDF
            SvgToPdfConverter.convert(svgFile.getAbsolutePath(), outputPdfFile.getAbsolutePath());

        } catch (IOException e) {
            LOG.error("IO error during SVG write or PDF conversion for '{}': {}",
                    outputPdfFile.getAbsolutePath(), e.getMessage(), e);
            throw new Exception("SVG file write/convert IO error: " + e.getMessage(), e);

        } catch (Exception e) {
            LOG.error("Error converting SVG → PDF for '{}': {}",
                    outputPdfFile.getAbsolutePath(), e.getMessage(), e);
            throw e;

        } finally {
            // Only delete if saveSvg=false (temp file)
            if (!saveSvg && svgFile.exists()) {
                if (!svgFile.delete()) {
                    LOG.warn("Temp SVG file not deleted: {}", svgFile.getAbsolutePath());
                    svgFile.deleteOnExit();
                } else {
                    LOG.debug("Temp SVG file deleted: {}", svgFile.getAbsolutePath());
                }
            }
        }
    }
    
    
    private static String sanitizeSvgPaths(String svg) {
        if (svg == null || svg.isEmpty()) return svg;

        // Remove <path> elements with empty d=""
        svg = svg.replaceAll("<path([^>]*?)\\sd=\"\\s*\"([^>]*?)/>", "<!-- removed empty path -->");
        svg = svg.replaceAll("<path([^>]*?)\\sd=\"\\s*\"([^>]*?)>\\s*</path>", "<!-- removed empty path -->");

        // Remove <path> elements where d contains NaN
        svg = svg.replaceAll("<path[^>]*?d=\"[^\"]*NaN[^\"]*\"[^>]*/?>", "<!-- removed NaN path -->");

        // Remove <path> elements where d is just "M x y" with nothing after (no drawing commands)
        svg = svg.replaceAll(
            "<path([^>]*?)\\sd=\"\\s*M\\s+[\\d.eE+\\-]+[,\\s]+[\\d.eE+\\-]+\\s*\"([^>]*?)/>",
            "<!-- removed moveto-only path -->"
        );

        return svg;
    }
    
    private static String validatePathData(String d) {
        if (d == null || d.trim().isEmpty()) {
            return null;
        }
        // Reject NaN or Infinity values
        if (d.contains("NaN") || d.contains("Infinity") || d.contains("infinity")) {
            return null;
        }
        // Reject moveto-only paths — they render nothing and Batik may reject them
        String trimmed = d.trim();
        if (trimmed.matches("(?i)M\\s+[\\d.eE+\\-]+[,\\s]+[\\d.eE+\\-]+\\s*")) {
            return null;
        }
        return d;
    }

}
