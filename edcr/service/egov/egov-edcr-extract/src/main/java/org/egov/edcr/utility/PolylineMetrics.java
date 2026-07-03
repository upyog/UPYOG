package org.egov.edcr.utility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.edcr.Plot;
import org.jfree.util.Log;
import org.kabeja.dxf.DXFLWPolyline;
import org.kabeja.dxf.DXFLine;
import org.kabeja.dxf.DXFVertex;
import org.kabeja.dxf.helpers.Point;
import org.kabeja.math.MathUtils;
import org.kabeja.math.ParametricPlane;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PolylineMetrics {

    public static class PolylineMeasurement {
        public double height;
        public double width;
        public double length;
        public double minDistance;
        public double meanDistance;
        public double area; // New field
    }
    
    private static final Logger LOG = LogManager.getLogger(PolylineMetrics.class);

    public static PolylineMeasurement calculateMetrics(DXFLWPolyline polyline) {
        PolylineMeasurement metrics = new PolylineMeasurement();
        
        // 1. Existing Bounds and Length logic
        metrics.height = polyline.getBounds().getHeight();
        metrics.width = polyline.getBounds().getWidth();

        List<Point> points = new ArrayList<>();
        Iterator it = polyline.getVertexIterator();
        while (it.hasNext()) {
            points.add(((DXFVertex) it.next()).getPoint());
        }

        double totalLength = 0;
        List<Double> segmentDistances = new ArrayList<>();

        for (int i = 0; i < points.size(); i++) {
            Point p1 = points.get(i);
            Point p2 = (i == points.size() - 1) ? points.get(0) : points.get(i + 1);
            
            double dist = MathUtils.distance(p1, p2);
            totalLength += dist;
            segmentDistances.add(dist);
        }

        metrics.length = totalLength;
        metrics.minDistance = segmentDistances.stream().mapToDouble(d -> d).min().orElse(0.0);
        metrics.meanDistance = segmentDistances.stream().mapToDouble(d -> d).average().orElse(0.0);

        // 2. Area Calculation using Kabeja's MathUtils
        // MathUtils.getPolygonArea uses the Shoelace formula
        metrics.area = calculatePolylineArea(polyline).doubleValue();

        return metrics;
    }
    
    public static BigDecimal calculatePolylineArea(DXFLWPolyline polyline) {
        if (polyline == null) {
            return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        }

        List<Point> points = new ArrayList<>();
        Iterator it = polyline.getVertexIterator();
        while (it.hasNext()) {
            points.add(((DXFVertex) it.next()).getPoint());
        }

        int numPoints = points.size();
        if (numPoints < 3) {
            return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP); // Not a valid closed polygon
        }

        double area = 0.0;
        int j = numPoints - 1; // Start with the last vertex to close the loop

        for (int i = 0; i < numPoints; i++) {
            Point pPrev = points.get(j);
            Point pCurr = points.get(i);

            // Trapezoid variation of the Shoelace formula (matches your original math)
            area += (pPrev.getX() + pCurr.getX()) * (pPrev.getY() - pCurr.getY());
            
            j = i; // j becomes the previous vertex for the next iteration
        }

        // Divide by 2 and get absolute value (handles clockwise vs counter-clockwise CAD drawings)
        BigDecimal convertedArea = BigDecimal.valueOf(Math.abs(area / 2.0));

        return convertedArea.setScale(4, RoundingMode.HALF_UP);
    }
    
    public static BigDecimal calculateShortestDistance(DXFLWPolyline poly1, DXFLWPolyline poly2) {
        if (poly1 == null || poly2 == null) {
            return BigDecimal.ZERO;
        }

        List<Point> points1 = extractPoints(poly1);
        List<Point> points2 = extractPoints(poly2);

        if (points1.isEmpty() || points2.isEmpty()) {
            return BigDecimal.ZERO;
        }

        double minDistance = Double.MAX_VALUE;

        // 1. Check distance from every vertex of Poly1 to every edge of Poly2
        for (Point p : points1) {
            for (int i = 0; i < points2.size(); i++) {
                Point edgeStart = points2.get(i);
                // Wrap around to the first point to close the polygon loop
                Point edgeEnd = (i == points2.size() - 1) ? points2.get(0) : points2.get(i + 1);
                
                double dist = pointToSegmentDistance(p, edgeStart, edgeEnd);
                if (dist < minDistance) {
                    minDistance = dist;
                }
            }
        }

        // 2. Check distance from every vertex of Poly2 to every edge of Poly1
        for (Point p : points2) {
            for (int i = 0; i < points1.size(); i++) {
                Point edgeStart = points1.get(i);
                Point edgeEnd = (i == points1.size() - 1) ? points1.get(0) : points1.get(i + 1);
                
                double dist = pointToSegmentDistance(p, edgeStart, edgeEnd);
                if (dist < minDistance) {
                    minDistance = dist;
                }
            }
        }

        // Return with 4 decimal precision, adjust this to match your DcrConstants
        return BigDecimal.valueOf(minDistance).setScale(4, RoundingMode.HALF_UP);
    }

    public static BigDecimal getSandwichSetbackDistance(DXFLWPolyline plotBoundary, DXFLWPolyline footprint, DXFLWPolyline yard) {
        if (plotBoundary == null || footprint == null || yard == null) {
            return BigDecimal.ZERO;
        }

        // 1. Isolate the lines that belong to this specific yard
        List<DXFLine> footprintLinesInYard = getLinesIntersectingYard(footprint, yard);
        List<DXFLine> boundaryLinesInYard = getLinesIntersectingYard(plotBoundary, yard);

        if (footprintLinesInYard.isEmpty() || boundaryLinesInYard.isEmpty()) {
            // Failsafe: The yard isn't properly touching either the building or the boundary
            return BigDecimal.ZERO; 
        }

        double minDistance = Double.MAX_VALUE;
        boolean distanceFound = false;

        // 2. Cross-compare the isolated lines to find the shortest segment-to-segment distance
        for (DXFLine fpLine : footprintLinesInYard) {
            for (DXFLine bLine : boundaryLinesInYard) {
                
                double dist = getShortestDistanceBetweenSegments(fpLine, bLine);
                
                // Ignore 0.0 distances (which implies intersections or identical overlapping lines)
                if (dist > 0.001 && dist < minDistance) {
                    minDistance = dist;
                    distanceFound = true;
                }
            }
        }

        if (distanceFound) {
            return BigDecimal.valueOf(minDistance).setScale(4, RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO;
    }

    /**
     * Calculates the absolute shortest distance between two finite line segments.
     * This handles angled lines and stepped geometries perfectly.
     */
    private static double getShortestDistanceBetweenSegments(DXFLine l1, DXFLine l2) {
        Point p1 = l1.getStartPoint();
        Point p2 = l1.getEndPoint();
        Point p3 = l2.getStartPoint();
        Point p4 = l2.getEndPoint();

        double d1 = pointToSegmentDistance(p1, p3, p4);
        double d2 = pointToSegmentDistance(p2, p3, p4);
        double d3 = pointToSegmentDistance(p3, p1, p2);
        double d4 = pointToSegmentDistance(p4, p1, p2);

        return Math.min(Math.min(d1, d2), Math.min(d3, d4));
    }
    
    public static BigDecimal getMaxSegmentLength(DXFLWPolyline polyline) {
        List<Point> points = extractPoints(polyline);
        double maxDist = 0.0;

        for (int i = 0; i < points.size(); i++) {
            Point p1 = points.get(i);
            Point p2 = (i == points.size() - 1) ? points.get(0) : points.get(i + 1);
            
            double dist = MathUtils.distance(p1, p2);
            if (dist > maxDist) {
                maxDist = dist;
            }
        }
        return BigDecimal.valueOf(maxDist).setScale(4, RoundingMode.HALF_UP);
    }

    /**
     * Finds the length of the longest strictly VERTICAL (or near-vertical) segment.
     * This targets the 17.89 edge on the right of your image.
     */
    public static BigDecimal getMaxVerticalEdgeLength(DXFLWPolyline polyline) {
        List<Point> points = extractPoints(polyline);
        double maxVerticalLength = 0.0;

        for (int i = 0; i < points.size(); i++) {
            Point p1 = points.get(i);
            Point p2 = (i == points.size() - 1) ? points.get(0) : points.get(i + 1);
            
            // Check if the segment is mostly vertical (X coordinates are very close)
            // Using a small tolerance (e.g., 0.1) to account for slight CAD inaccuracies
            if (Math.abs(p1.getX() - p2.getX()) < 0.1) {
                double dist = MathUtils.distance(p1, p2);
                if (dist > maxVerticalLength) {
                    maxVerticalLength = dist;
                }
            }
        }
        return BigDecimal.valueOf(maxVerticalLength).setScale(4, RoundingMode.HALF_UP);
    }

    private static List<Point> extractPoints(DXFLWPolyline polyline) {
        List<Point> points = new ArrayList<>();
        Iterator it = polyline.getVertexIterator();
        while (it.hasNext()) {
            points.add(((DXFVertex) it.next()).getPoint());
        }
        return points;
    }
 
    public static BigDecimal getMinParallelDistance(DXFLWPolyline polyline) {
        List<DXFLine> allLines = getLinesOfPolyline(polyline);
        List<DXFLine> topEdges = new ArrayList<>();
        List<DXFLine> bottomEdges = new ArrayList<>();

        // 1. Find the middle Y-coordinate to separate Top from Bottom
        double maxY = polyline.getBounds().getMaximumY();
        double minY = polyline.getBounds().getMinimumY();
        double midY = (maxY + minY) / 2.0;

        // 2. Classify edges into Top and Bottom (and ignore vertical sides)
        for (DXFLine line : allLines) {
            double startX = line.getStartPoint().getX();
            double endX = line.getEndPoint().getX();
            
            // If the line is mostly horizontal (X changes significantly)
            if (Math.abs(startX - endX) > 1.0) {
                double avgY = (line.getStartPoint().getY() + line.getEndPoint().getY()) / 2.0;
                
                if (avgY > midY) {
                    topEdges.add(line);
                } else {
                    bottomEdges.add(line);
                }
            }
        }

        // 3. Find the shortest distance between any Top edge and any Bottom edge
        double minOppositeDistance = Double.MAX_VALUE;
        boolean found = false;

        for (DXFLine topEdge : topEdges) {
            for (DXFLine bottomEdge : bottomEdges) {
                double dist = getShortestDistanceBetweenSegments(topEdge, bottomEdge);
                if (dist > 0 && dist < minOppositeDistance) {
                    minOppositeDistance = dist;
                    found = true;
                }
            }
        }

        if (found) {
            return BigDecimal.valueOf(minOppositeDistance).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }
    
    public static BigDecimal getMinDistance(
            DXFLWPolyline plotBoundary,
            DXFLWPolyline footprint,
            DXFLWPolyline setbackPolyline) {

        LOG.info("getMinParallelSetback started");

        if (plotBoundary == null || footprint == null || setbackPolyline == null) {
            LOG.warn("getMinParallelSetback: null input");
            return BigDecimal.ZERO;
        }

        List<DXFLine> boundaryLines = getLinesOfPolyline(plotBoundary);
        List<DXFLine> footprintLines = getLinesOfPolyline(footprint);
        List<DXFLine> setbackLines = getLinesOfPolyline(setbackPolyline);

        LOG.info("boundaryLines={}, footprintLines={}, setbackLines={}",
                boundaryLines.size(), footprintLines.size(), setbackLines.size());

        if (boundaryLines.isEmpty() || footprintLines.isEmpty() || setbackLines.isEmpty()) {
            LOG.warn("getMinParallelSetback: empty line set");
            return BigDecimal.ZERO;
        }

        DXFLine referenceLine = getLongestLine(setbackLines);
        if (referenceLine == null) {
            LOG.warn("getMinParallelSetback: no reference line found in setback polyline");
            return BigDecimal.ZERO;
        }

        LOG.info("Reference setback line: ({}, {}) -> ({}, {})",
                referenceLine.getStartPoint().getX(), referenceLine.getStartPoint().getY(),
                referenceLine.getEndPoint().getX(), referenceLine.getEndPoint().getY());

        double angleTolerance = Math.toRadians(10.0);

        DXFLine bestBoundaryLine = findBestParallelLine(
                boundaryLines, setbackLines, referenceLine, angleTolerance, "boundary");

        DXFLine bestFootprintLine = findBestParallelLine(
                footprintLines, setbackLines, referenceLine, angleTolerance, "footprint");

        if (bestBoundaryLine == null || bestFootprintLine == null) {
            LOG.warn("getMinParallelSetback: could not find matching parallel lines. boundary={}, footprint={}",
                    bestBoundaryLine != null, bestFootprintLine != null);
            return BigDecimal.ZERO;
        }

        if (!isParallel(bestBoundaryLine, bestFootprintLine, angleTolerance)) {
            LOG.warn("getMinParallelSetback: selected lines are not parallel to each other");
            LOG.info("Selected boundary line: ({}, {}) -> ({}, {})",
                    bestBoundaryLine.getStartPoint().getX(), bestBoundaryLine.getStartPoint().getY(),
                    bestBoundaryLine.getEndPoint().getX(), bestBoundaryLine.getEndPoint().getY());
            LOG.info("Selected footprint line: ({}, {}) -> ({}, {})",
                    bestFootprintLine.getStartPoint().getX(), bestFootprintLine.getStartPoint().getY(),
                    bestFootprintLine.getEndPoint().getX(), bestFootprintLine.getEndPoint().getY());
            return BigDecimal.ZERO;
        }

        double distance = getPerpendicularDistanceBetweenParallelSegments(bestFootprintLine, bestBoundaryLine);

        LOG.info("Selected boundary line: ({}, {}) -> ({}, {})",
                bestBoundaryLine.getStartPoint().getX(), bestBoundaryLine.getStartPoint().getY(),
                bestBoundaryLine.getEndPoint().getX(), bestBoundaryLine.getEndPoint().getY());

        LOG.info("Selected footprint line: ({}, {}) -> ({}, {})",
                bestFootprintLine.getStartPoint().getX(), bestFootprintLine.getStartPoint().getY(),
                bestFootprintLine.getEndPoint().getX(), bestFootprintLine.getEndPoint().getY());

        LOG.info("Computed setback distance={}", distance);

        if (distance <= 0.1) {
            LOG.warn("getMinParallelSetback: invalid distance");
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(distance).setScale(2, RoundingMode.HALF_UP);
    }
   
   
   private static BigDecimal getFallbackShortestDistance(List<DXFLine> fpLines, List<DXFLine> bndLines) {
       double minDistance = Double.MAX_VALUE;
       boolean found = false;

       for (DXFLine fpLine : fpLines) {
           for (DXFLine bndLine : bndLines) {
               double dist = getShortestDistanceBetweenSegments(fpLine, bndLine);
               // Ignore 0.0 (intersections) and find the shortest physical gap
               if (dist > 0.1 && dist < minDistance) {
                   minDistance = dist;
                   found = true;
               }
           }
       }
       
       if (found) {
           return BigDecimal.valueOf(minDistance).setScale(2, RoundingMode.HALF_UP);
       }
       return BigDecimal.ZERO;
   }
   
   
    /**
     * Checks if two lines are mathematically parallel, within a defined angular tolerance.
     */
   private static boolean isParallel(DXFLine l1, DXFLine l2, double toleranceRadians) {
	    double dx1 = l1.getEndPoint().getX() - l1.getStartPoint().getX();
	    double dy1 = l1.getEndPoint().getY() - l1.getStartPoint().getY();

	    double dx2 = l2.getEndPoint().getX() - l2.getStartPoint().getX();
	    double dy2 = l2.getEndPoint().getY() - l2.getStartPoint().getY();

	    double len1 = Math.sqrt(dx1 * dx1 + dy1 * dy1);
	    double len2 = Math.sqrt(dx2 * dx2 + dy2 * dy2);

	    if (len1 == 0 || len2 == 0) {
	        LOG.debug("isParallel: zero-length segment");
	        return false;
	    }

	    double nx1 = dx1 / len1;
	    double ny1 = dy1 / len1;
	    double nx2 = dx2 / len2;
	    double ny2 = dy2 / len2;

	    double dotProduct = Math.abs(nx1 * nx2 + ny1 * ny2);
	    return dotProduct >= Math.cos(toleranceRadians);
	}
   
   
   private static Point getPointAlongLine(Point start, Point end, double fraction) {
       Point p = new Point();
       p.setX(start.getX() + (end.getX() - start.getX()) * fraction);
       p.setY(start.getY() + (end.getY() - start.getY()) * fraction);
       p.setZ(0.0);
       return p;
   }
   
   
    // --- Masking and Filtering Helpers ---

   private static List<DXFLine> getLinesIntersectingYard(DXFLWPolyline sourcePolyline, DXFLWPolyline yardMask) {
       List<DXFLine> allLines = getLinesOfPolyline(sourcePolyline);
       List<DXFLine> validLines = new ArrayList<>();

       for (DXFLine line : allLines) {
           Point p0 = line.getStartPoint();
           Point p100 = line.getEndPoint();
           
           // Calculate checkpoints along the line segment
           Point p25 = getPointAlongLine(p0, p100, 0.25);
           Point p50 = getPointAlongLine(p0, p100, 0.50);
           Point p75 = getPointAlongLine(p0, p100, 0.75);

           int pointsInYard = 0;
           if (isPointInOrOnYard(yardMask, p0)) pointsInYard++;
           if (isPointInOrOnYard(yardMask, p25)) pointsInYard++;
           if (isPointInOrOnYard(yardMask, p50)) pointsInYard++;
           if (isPointInOrOnYard(yardMask, p75)) pointsInYard++;
           if (isPointInOrOnYard(yardMask, p100)) pointsInYard++;

           // If 3 out of 5 points touch the yard, it belongs to this yard zone.
           if (pointsInYard >= 3) {
               validLines.add(line);
           }
       }
       return validLines;
   }
   
   private static boolean isPointInOrOnYard(DXFLWPolyline yardMask, Point p) {
       return org.egov.edcr.utility.Util.isPointStrictlyInsidePolygon(yardMask, p) || 
              org.egov.edcr.utility.Util.isPointOnPolygonBoundary(yardMask, p);
   }

   private static List<DXFLine> getLinesOfPolyline(DXFLWPolyline polyline) {
	    List<DXFLine> lines = new ArrayList<>();
	    Iterator vertexIterator = polyline.getVertexIterator();
	    DXFVertex first = null;
	    DXFVertex previous = null;

	    while (vertexIterator.hasNext()) {
	        DXFVertex current = (DXFVertex) vertexIterator.next();
	        if (previous != null) {
	            DXFLine line = new DXFLine();
	            line.setStartPoint(previous.getPoint());
	            line.setEndPoint(current.getPoint());
	            lines.add(line);
	        } else {
	            first = current;
	        }
	        previous = current;
	    }

	    if (previous != null && first != null && !org.egov.edcr.utility.Util.pointsEquals(first.getPoint(), previous.getPoint())) {
	        DXFLine line = new DXFLine();
	        line.setStartPoint(previous.getPoint());
	        line.setEndPoint(first.getPoint());
	        lines.add(line);
	    }

	    return lines;
	}

   private static double pointToSegmentDistance(Point p, Point v, Point w) {
       double l2 = Math.pow(v.getX() - w.getX(), 2) + Math.pow(v.getY() - w.getY(), 2);
       if (l2 == 0) return Math.sqrt(Math.pow(p.getX() - v.getX(), 2) + Math.pow(p.getY() - v.getY(), 2));
       
       double t = Math.max(0, Math.min(1, ((p.getX() - v.getX()) * (w.getX() - v.getX()) + (p.getY() - v.getY()) * (w.getY() - v.getY())) / l2));
       
       double projX = v.getX() + t * (w.getX() - v.getX());
       double projY = v.getY() + t * (w.getY() - v.getY());
       
       return Math.sqrt(Math.pow(p.getX() - projX, 2) + Math.pow(p.getY() - projY, 2));
   }
   
   public static BigDecimal getSpecificSetbackDistance(
	        DXFLWPolyline plotBoundary,
	        DXFLWPolyline footprint,
	        DXFLWPolyline setbackPolyline) {

	    LOG.info("getSpecificSetbackDistance -> delegating to getMinParallelSetback");
	    return getMinDistance(plotBoundary, footprint, setbackPolyline);
	}
   
 private static double getPerpendicularDistanceBetweenParallelSegments(DXFLine l1, DXFLine l2) {
	    Point a1 = l1.getStartPoint();
	    Point a2 = l1.getEndPoint();
	    Point b1 = l2.getStartPoint();
	    Point b2 = l2.getEndPoint();

	    double d1 = pointToLineDistance(a1, b1, b2);
	    double d2 = pointToLineDistance(a2, b1, b2);
	    double d3 = pointToLineDistance(b1, a1, a2);
	    double d4 = pointToLineDistance(b2, a1, a2);

	    return Math.min(Math.min(d1, d2), Math.min(d3, d4));
	}
   
 private static double pointToLineDistance(Point p, Point v, Point w) {
	    double dx = w.getX() - v.getX();
	    double dy = w.getY() - v.getY();
	    double len = Math.sqrt(dx * dx + dy * dy);

	    if (len == 0) {
	        return Math.sqrt(
	                Math.pow(p.getX() - v.getX(), 2) +
	                Math.pow(p.getY() - v.getY(), 2)
	        );
	    }

	    return Math.abs(dy * p.getX() - dx * p.getY() + w.getX() * v.getY() - w.getY() * v.getX()) / len;
	}

   
   
   
   private static List<DXFLine> getLinesInsideSetbackZone(DXFLWPolyline sourcePolyline, DXFLWPolyline setbackPolyline) {
    List<DXFLine> allLines = getLinesOfPolyline(sourcePolyline);
    List<DXFLine> validLines = new ArrayList<>();

    for (DXFLine line : allLines) {
        Point p0 = line.getStartPoint();
        Point p100 = line.getEndPoint();
        Point p25 = getPointAlongLine(p0, p100, 0.25);
        Point p50 = getPointAlongLine(p0, p100, 0.50);
        Point p75 = getPointAlongLine(p0, p100, 0.75);

        int pointsInZone = 0;
        if (isPointInOrOnPolygon(setbackPolyline, p0)) pointsInZone++;
        if (isPointInOrOnPolygon(setbackPolyline, p25)) pointsInZone++;
        if (isPointInOrOnPolygon(setbackPolyline, p50)) pointsInZone++;
        if (isPointInOrOnPolygon(setbackPolyline, p75)) pointsInZone++;
        if (isPointInOrOnPolygon(setbackPolyline, p100)) pointsInZone++;

        LOG.debug("Line ({}, {}) -> ({}, {}) pointsInZone={}",
                p0.getX(), p0.getY(), p100.getX(), p100.getY(), pointsInZone);

        if (pointsInZone >= 3) {
            validLines.add(line);
        }
    }

    return validLines;
}

   private static boolean isPointInOrOnPolygon(DXFLWPolyline polygon, Point p) {
	    boolean inside = org.egov.edcr.utility.Util.isPointStrictlyInsidePolygon(polygon, p);
	    boolean onBoundary = org.egov.edcr.utility.Util.isPointOnPolygonBoundary(polygon, p);
	    return inside || onBoundary;
	}
   
   
   
   private static DXFLine findBestParallelLine(
	        List<DXFLine> sourceLines,
	        List<DXFLine> setbackLines,
	        DXFLine referenceLine,
	        double angleTolerance,
	        String label) {

	    DXFLine bestLine = null;
	    double bestScore = Double.MAX_VALUE;

	    for (DXFLine line : sourceLines) {
	        if (!isParallel(line, referenceLine, angleTolerance)) {
	            continue;
	        }

	        double distanceToSetback = getMinDistanceToPolyline(line, setbackLines);
	        double length = getLineLength(line);

	        LOG.info("{} candidate: ({}, {}) -> ({}, {}), distanceToSetback={}, length={}",
	                label,
	                line.getStartPoint().getX(), line.getStartPoint().getY(),
	                line.getEndPoint().getX(), line.getEndPoint().getY(),
	                distanceToSetback,
	                length);

	        if (distanceToSetback < bestScore) {
	            bestScore = distanceToSetback;
	            bestLine = line;
	        }
	    }

	    return bestLine;
	}
   
   private static double getMinDistanceToPolyline(DXFLine line, List<DXFLine> polylineLines) {
	    double minDistance = Double.MAX_VALUE;

	    for (DXFLine polyLine : polylineLines) {
	        double dist = getShortestDistanceBetweenSegments(line, polyLine);
	        if (dist < minDistance) {
	            minDistance = dist;
	        }
	    }

	    return minDistance;
	}
   
   private static DXFLine getLongestLine(List<DXFLine> lines) {
	    DXFLine best = null;
	    double maxLength = -1.0;

	    for (DXFLine line : lines) {
	        double length = getLineLength(line);
	        if (length > maxLength) {
	            maxLength = length;
	            best = line;
	        }
	    }

	    return best;
	}
   private static double getLineLength(DXFLine line) {
	    return MathUtils.distance(line.getStartPoint(), line.getEndPoint());
	}
    
}
