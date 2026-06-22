package org.egov.edcr.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.SetBack;
import org.egov.common.entity.edcr.Yard;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.entity.blackbox.MeasurementDetail;
import org.egov.edcr.entity.blackbox.PlanDetail;
import org.egov.edcr.entity.blackbox.PlotDetail;
import org.egov.edcr.entity.blackbox.YardDetail;
import org.egov.edcr.service.LayerNames;
import org.egov.edcr.utility.Util;
import org.kabeja.dxf.DXFDocument;
import org.kabeja.dxf.DXFLWPolyline;
import org.kabeja.dxf.DXFLine;
import org.kabeja.dxf.DXFVertex;
import org.kabeja.dxf.helpers.Point;
import org.kabeja.math.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MinDistance {

    @Autowired
    private LayerNames layerNames;
    private static final Logger LOG = LogManager.getLogger(MinDistance.class);

    public BigDecimal getYardMinDistance(PlanDetail pl, String name, String level, DXFDocument doc) {
        DXFLWPolyline plotBoundary = ((PlotDetail) pl.getPlot()).getPolyLine();

        DXFLWPolyline yardPolyline = null;
        String[] split = name.split("_");
        Block blockByName = pl.getBlockByName(split[1]);
        SetBack setBackByLevel = blockByName.getSetBackByLevel(level);
        // PrintUtil.print(buildFoorPrint,"buildFoorPrint");

        DXFLWPolyline buildFoorPrint = ((MeasurementDetail) setBackByLevel.getBuildingFootPrint()).getPolyLine();
        Yard yard = null;
        if (name.contains(layerNames.getLayerName("LAYER_NAME_FRONT_YARD"))
                || name.contains(layerNames.getLayerName("LAYER_NAME_BSMNT_FRONT_YARD"))) {
            yard = setBackByLevel.getFrontYard();
            yardPolyline = ((YardDetail) setBackByLevel.getFrontYard()).getPolyLine();
        } else if (name.contains(layerNames.getLayerName("LAYER_NAME_REAR_YARD"))
                || name.contains(layerNames.getLayerName("LAYER_NAME_BSMNT_REAR_YARD"))) {
            yard = setBackByLevel.getRearYard();
            yardPolyline = ((YardDetail) setBackByLevel.getRearYard()).getPolyLine();
        } else if (name.contains(layerNames.getLayerName("LAYER_NAME_SIDE_YARD_1"))
                || name.contains(layerNames.getLayerName("LAYER_NAME_BSMNT_SIDE_YARD_1"))) {
            yard = setBackByLevel.getSideYard1();
            yardPolyline = ((YardDetail) setBackByLevel.getSideYard1()).getPolyLine();
        } else if (name.contains(layerNames.getLayerName("LAYER_NAME_SIDE_YARD_2"))
                || name.contains(layerNames.getLayerName("LAYER_NAME_BSMNT_SIDE_YARD_2"))) {
            yard = setBackByLevel.getSideYard2();
            yardPolyline = ((YardDetail) setBackByLevel.getSideYard2()).getPolyLine();
        }
        LOG.info("yard Area  " + yard.getArea());

        if (level.equals(-1) && (plotBoundary == null || buildFoorPrint == null || yard == null)) {
            pl.getErrors().put("Set back calculation Error",
                    "Either" + layerNames.getLayerName("LAYER_NAME_BSMNT_FOOT_PRINT") + ","
                            + layerNames.getLayerName("LAYER_NAME_PLOT_BOUNDARY") + " or " + name + " is not found");
            return BigDecimal.ZERO.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS);
        } else if (plotBoundary == null || buildFoorPrint == null || yard == null) {
            pl.getErrors().put("Set back calculation Error",
                    "Either " + layerNames.getLayerName("LAYER_NAME_BUILDING_FOOT_PRINT") + ","
                            + layerNames.getLayerName("LAYER_NAME_PLOT_BOUNDARY") + " or " + name
                            + " is not found at level " + split[3]);
            return BigDecimal.ZERO.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS);
        }

        if (level.equals(-1) && (plotBoundary == null || buildFoorPrint == null || yardPolyline == null)) {
            pl.getErrors().put("Set back calculation Error",
                    "Either " + layerNames.getLayerName("LAYER_NAME_BSMNT_FOOT_PRINT") + ","
                            + layerNames.getLayerName("LAYER_NAME_PLOT_BOUNDARY") + " or " + name + " is not found ");
            return BigDecimal.ZERO.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS);
        } else if (plotBoundary == null || buildFoorPrint == null || yardPolyline == null) {
            pl.getErrors().put("Set back calculation Error",
                    "Either " + layerNames.getLayerName("LAYER_NAME_BUILDING_FOOT_PRINT") + ","
                            + layerNames.getLayerName("LAYER_NAME_PLOT_BOUNDARY") + " or " + name + " is not found ");
            return BigDecimal.ZERO.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS);
        }

        LOG.info(" ===============================");
        LOG.info(" YARD TYPE  " + name);
        LOG.info("Bounds Width " + yardPolyline.getBounds().getWidth());
        LOG.info("Bounds Height " + yardPolyline.getBounds().getHeight());
        LOG.info("Bounds Max x " + yardPolyline.getBounds().getMaximumX());
        LOG.info("Bounds Min x " + yardPolyline.getBounds().getMinimumX());

        LOG.info("Bounds Max y " + yardPolyline.getBounds().getMaximumY());
        LOG.info("Bounds Min y " + yardPolyline.getBounds().getMinimumY());

        if (!plotBoundary.isClosed())
            pl.getErrors().put("Plot boundary not closed",
                    layerNames.getLayerName("LAYER_NAME_PLOT_BOUNDARY") + " is not closed ");

        if (level.equals(-1) && !buildFoorPrint.isClosed())
            pl.getErrors().put("Building basement foot print not closed",
                    layerNames.getLayerName("LAYER_NAME_BSMNT_FOOT_PRINT") + " is not closed ");
        else if (!buildFoorPrint.isClosed())
            pl.getErrors().put("Building foot print not closed",
                    layerNames.getLayerName("LAYER_NAME_BUILDING_FOOT_PRINT") + " is not closed ");

        if (!yardPolyline.isClosed())
            pl.getErrors().put(name + " not closed", name + " is not closed ");

        if (!plotBoundary.isClosed() || !buildFoorPrint.isClosed() || !yardPolyline.isClosed())
            return BigDecimal.ZERO.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS);
        
//        if (!checkYardNotOverlappingBuilding(yardPolyline, buildFoorPrint, name, pl, layerNames)) {
//            //return BigDecimal.ZERO.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS);
//        }

     // NEW: STRONG VALIDATION - Yard must NOT enter building footprint
     // FINAL SAFETY: Yard must NOT enter building — only touch or superimpose
        // FINAL BULLETPROOF CHECK
        if (!isYardOutsideOrTouchingBuildingOnly(yardPolyline, buildFoorPrint, name, pl, layerNames)) {
            return BigDecimal.ZERO.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS);
        }
        
        Iterator yardVertexIterator = yardPolyline.getVertexIterator();
        PrintUtil.print(yardPolyline, name);
        List<Point> yardOutSidePoints = new ArrayList<>();
        List<Point> yardInSidePoints = new ArrayList<>();
        Set<Double> distanceList = new TreeSet<>();
        // Find the edges of PlotBoundary
        List<Point> plotBoundaryEdges = Util.pointsOnPolygon(plotBoundary);
        // Find the points connecting the edges of PlotBoundary
        List<Point> pointsOnPlot = Util.findPointsOnPolylines(plotBoundaryEdges);

        List<Point> footPrintEdges = Util.pointsOnPolygon(buildFoorPrint);
        // Find the points connecting the edges of PlotBoundary
        List<Point> footPrintPoints = Util.findPointsOnPolylines(footPrintEdges);
        List<DXFLine> yardLines = getLinesOfPolyline(yardPolyline);

        while (yardVertexIterator.hasNext()) {
            DXFVertex next = (DXFVertex) yardVertexIterator.next();
            Point yardEdge = next.getPoint();
            PrintUtil.print(yardEdge, "yardEdge");

            Iterator plotBIterator = plotBoundary.getVertexIterator();

            // Vertex and coordinates of Polyline
            boolean pointAdded = false;
            outside: while (plotBIterator.hasNext()) {

                DXFVertex dxfVertex = (DXFVertex) plotBIterator.next();
                Point plotBoundaryEdge = dxfVertex.getPoint();
                // Util.print(plotBoundaryEdge,"plotBoundaryEdge");

                if (Util.pointsEquals(plotBoundaryEdge, yardEdge)) {
                    pointAdded = true;
                    yardOutSidePoints.add(yardEdge);
                    LOG.debug("Adding yardEdge to outside points in direct compare");
                    break outside;
                }

                if (Util.pointsEqualsWith2PercentError(plotBoundaryEdge, yardEdge)) {
                    pointAdded = true;
                    yardOutSidePoints.add(yardEdge);
                    LOG.debug("Adding yardEdge to outside points in  pointsEqualsWith2PercentError compare");
                    break outside;
                }
            }

            if (!pointAdded && pointsOnPlot.contains(yardEdge)) {
                yardOutSidePoints.add(yardEdge);
                LOG.debug("Adding yardEdge to outside points in  Contains compare");
                pointAdded = true;
            }

            if (!pointAdded)
                for (Point p : pointsOnPlot)
                    if (Util.pointsEquals(p, yardEdge)) {
                        yardOutSidePoints.add(yardEdge);
                        LOG.debug("Adding yardEdge to outside points in  pointsOnPlot pointsEquals");
                        pointAdded = true;
                        break;
                    }

            if (!pointAdded)
                for (Point p : pointsOnPlot)
                    if (Util.pointsEqualsWith2PercentError(p, yardEdge)) {

                        yardOutSidePoints.add(yardEdge);
                        LOG.debug("Adding yardEdge to outside points in  pointsOnPlot pointsEqualsWith2PercentError");
                        pointAdded = true;
                        break;
                    }

            Boolean insidePointAdded = false;
            Iterator footPrintIterator = buildFoorPrint.getVertexIterator();

            // Vertex and coordinates of Polyline
            inside: while (footPrintIterator.hasNext()) {

                DXFVertex dxfVertex = (DXFVertex) footPrintIterator.next();
                Point footPrintEdge = dxfVertex.getPoint();
                // Util.print(footPrintEdge,"footPrintEdge");
                if (Util.pointsEquals(footPrintEdge, yardEdge)) {
                    insidePointAdded = true;
                    yardInSidePoints.add(yardEdge);
                    LOG.debug("Adding yardEdge to inside points in  footPrintEdge pointsEquals");
                    break inside;
                }
                // if(LOG.isDebugEnabled()) LOG.debug("Foot Print
                // :"+point1.getX()+","+point1.getY());
                if (Util.pointsEqualsWith2PercentError(footPrintEdge, yardEdge)) {
                    yardInSidePoints.add(yardEdge);
                    insidePointAdded = true;
                    LOG.debug("Adding yardEdge to inside points in  footPrintEdge pointsEquals");
                    break inside;
                }

            }
            // Now check yard edge on the plot points

            if (!insidePointAdded && footPrintPoints.contains(yardEdge)) {
                yardInSidePoints.add(yardEdge);
                insidePointAdded = true;
                LOG.debug("Adding yardEdge to inside points in  footPrint contains");
            }
            if (!insidePointAdded)
                for (Point p : footPrintPoints)
                    if (Util.pointsEquals(p, yardEdge)) {
                        yardInSidePoints.add(yardEdge);
                        insidePointAdded = true;
                        LOG.debug("Adding yardEdge to inside points in  footPrint pointsEquals");
                        break;
                    }

            if (!insidePointAdded)
                for (Point p : footPrintPoints)
                    if (Util.pointsEqualsWith2PercentError(p, yardEdge)) {

                        yardInSidePoints.add(yardEdge);
                        insidePointAdded = true;
                        LOG.debug("Adding yardEdge to inside points in  footPrint pointsEqualsWith2PercentError");
                        break;
                    }

        }
        PrintUtil.print(yardOutSidePoints, "yardOutSidePoints");
        PrintUtil.print(yardInSidePoints, "yardInSidePoints");
        removeDuplicates(yardOutSidePoints, yardInSidePoints);
        PrintUtil.print(yardOutSidePoints, "yardOutSidePoints");
        PrintUtil.print(yardInSidePoints, "yardInSidePoints");

        List<BigDecimal> yardWidthDistance = Util.getListOfDimensionByColourCode(pl, name,
                DxfFileConstants.YARD_DIMENSION_COLOR);
        // if yard dimension defined in plan, then use the same other wise calculate
        // manually.
        if (!yardWidthDistance.isEmpty()) {
            Collections.min(yardWidthDistance);

            yard.setMinimumDistance(Collections.min(yardWidthDistance));
            /*
             * if (minDistance.compareTo(BigDecimal.ZERO) > 0 && yard.getArea() != null) //
             * yard1.setMean(yard1.getArea().divide(maxDistance, 2, RoundingMode.HALF_UP));
             * yard.setMean(yard.getArea().divide(minDistance, 2, RoundingMode.HALF_UP)); else yard.setMean(BigDecimal.ZERO);
             */
        }

        // If minimum yard distance defined, then use the same. Here we are overwrite
        // the value which is calculated in
        // MinDistance.getYardMinDistance method.
        // List<BigDecimal> dimensions = Util.getListOfDimensionValueByLayer(doc, name);
        List<BigDecimal> distanceForMean = Util.getListOfDimensionOtherThanSpecifiedColourCode(doc, name,
                DxfFileConstants.YARD_DIMENSION_COLOR, pl);

        /*
         * if (!dimensions.isEmpty()) { yard.setDimensions(dimensions); yard.setMinimumDistance(
         * Collections.min(dimensions).setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, RoundingMode.HALF_UP)); //
         * Collections.min(dimensions).setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, RoundingMode.HALF_UP); }
         */
        if (!distanceForMean.isEmpty()) {
            BigDecimal min = Collections.min(distanceForMean).setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS,
                    RoundingMode.HALF_UP);
            yard.setMean(yard.getArea().divide(min, DcrConstants.DECIMALDIGITS_MEASUREMENTS, RoundingMode.HALF_UP));
        }

        List<Point> outsidePoints = Util.findPointsOnPolylines(yardOutSidePoints, yardLines, pl, name);

        List<Point> insidePoints = Util.findPointsOnPolylines(yardInSidePoints, yardLines, pl, name);

        if (yardInSidePoints.isEmpty() || yardInSidePoints.size() == 1)
            if (level.equals(-1))
                pl.getErrors().put("Set back calculation error for basementfootprint " + name, "Points of " + name
                        + " not properly on " + layerNames.getLayerName("LAYER_NAME_BSMNT_FOOT_PRINT"));
            else
                pl.getErrors().put("Set back calculation error for footprint" + name, "Points of " + name
                        + " not properly on " + layerNames.getLayerName("LAYER_NAME_BUILDING_FOOT_PRINT"));
        if (outsidePoints.isEmpty() || outsidePoints.size() == 1)
            pl.getErrors().put("Set back calculation error for boundary" + name,
                    "Points of " + name + " not properly on " + layerNames.getLayerName("LAYER_NAME_PLOT_BOUNDARY"));

        double distance = 0;
        Map<Double, DXFLine> map = new HashMap<>();
        Set<Double> singleDistanceList = new TreeSet<>();
        List<Double> avgList = new ArrayList<>();
        for (Point in : insidePoints) {
            if (!singleDistanceList.isEmpty()) {
                Iterator<Double> iterator = singleDistanceList.iterator();
                if (iterator.hasNext()) {
                    avgList.add(iterator.next());
                    singleDistanceList = new TreeSet<>();
                }
            }
            int incr = 0;
            for (Point out : outsidePoints)
                if (insidePoints.size() < 100) {
                    distance = MathUtils.distance(in, out);
                    distanceList.add(distance);
                    singleDistanceList.add(distance);
                } else {
                    incr++;

                    if (incr % 100 == 1) {

                        distance = MathUtils.distance(in, out);
                        singleDistanceList.add(distance);
                        distanceList.add(distance);
                        // if this is lowest print else dont
                        Iterator<Double> iterator = distanceList.iterator();
                        if (iterator.hasNext() && distance == iterator.next()) {
                            LOG.debug("Distance******* : " + distance);
                            DXFLine line = new DXFLine();
                            line.setStartPoint(out);
                            line.setEndPoint(in);
                            map.put(distance, line);
                            LOG.debug("Outside : " + out.getX() + "," + out.getY() + " inside" + in.getX() + ","
                                    + in.getY());
                        }

                    }
                }

        }

        if (yard.getMean().doubleValue() == 0.0d) {
            Double avg = 0.0d;
            if (!avgList.isEmpty()) {
                for (Double d : avgList)
                    avg = avg + d;

                avg = avg / avgList.size();
                LOG.info("Average from min distance is................. " + avg);

                yard.setMean(BigDecimal.valueOf(avg).setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS,
                        RoundingMode.HALF_UP));

            } else
                yard.setMean(BigDecimal.ZERO);
        }

        if (yard.getMinimumDistance().doubleValue() > 0.0d)
            return yard.getMinimumDistance();

        if (!distanceList.isEmpty()) {
            Double dist = distanceList.iterator().next();
            DXFLine line = map.get(dist);
            LOG.debug("the shortest Distance is " + dist);
            PrintUtil.printForDXf(line.getStartPoint(), line.getEndPoint(), name + "_MIN_DISTANCE", pl);
            return BigDecimal.valueOf(dist).setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, RoundingMode.HALF_UP);
        } else
            return BigDecimal.ZERO.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS);

    }
    
    public BigDecimal getYardMinDistanceV2(PlanDetail pl, String name, String level, DXFDocument doc) {
        DXFLWPolyline plotBoundary = ((PlotDetail) pl.getPlot()).getPolyLine();
        DXFLWPolyline yardPolyline = null;
        String[] split = name.split("_");
        Block blockByName = pl.getBlockByName(split[1]);
        SetBack setBackByLevel = blockByName.getSetBackByLevel(level);

        DXFLWPolyline buildFoorPrint = ((MeasurementDetail) setBackByLevel.getBuildingFootPrint()).getPolyLine();
        Yard yard = null;
        
        if (name.contains(layerNames.getLayerName("LAYER_NAME_FRONT_YARD")) || name.contains(layerNames.getLayerName("LAYER_NAME_BSMNT_FRONT_YARD"))) {
            yard = setBackByLevel.getFrontYard();
            yardPolyline = ((YardDetail) setBackByLevel.getFrontYard()).getPolyLine();
        } else if (name.contains(layerNames.getLayerName("LAYER_NAME_REAR_YARD")) || name.contains(layerNames.getLayerName("LAYER_NAME_BSMNT_REAR_YARD"))) {
            yard = setBackByLevel.getRearYard();
            yardPolyline = ((YardDetail) setBackByLevel.getRearYard()).getPolyLine();
        } else if (name.contains(layerNames.getLayerName("LAYER_NAME_SIDE_YARD_1")) || name.contains(layerNames.getLayerName("LAYER_NAME_BSMNT_SIDE_YARD_1"))) {
            yard = setBackByLevel.getSideYard1();
            yardPolyline = ((YardDetail) setBackByLevel.getSideYard1()).getPolyLine();
        } else if (name.contains(layerNames.getLayerName("LAYER_NAME_SIDE_YARD_2")) || name.contains(layerNames.getLayerName("LAYER_NAME_BSMNT_SIDE_YARD_2"))) {
            yard = setBackByLevel.getSideYard2();
            yardPolyline = ((YardDetail) setBackByLevel.getSideYard2()).getPolyLine();
        }
        
        LOG.info("yard Area  " + yard.getArea());

        // Basic Null Checks
        if (level.equals("-1") && (plotBoundary == null || buildFoorPrint == null || yardPolyline == null)) {
            pl.getErrors().put("Set back calculation Error", "Either " + layerNames.getLayerName("LAYER_NAME_BSMNT_FOOT_PRINT") + ", " + layerNames.getLayerName("LAYER_NAME_PLOT_BOUNDARY") + " or " + name + " is not found");
            return BigDecimal.ZERO.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS);
        } else if (plotBoundary == null || buildFoorPrint == null || yardPolyline == null) {
            pl.getErrors().put("Set back calculation Error", "Either " + layerNames.getLayerName("LAYER_NAME_BUILDING_FOOT_PRINT") + ", " + layerNames.getLayerName("LAYER_NAME_PLOT_BOUNDARY") + " or " + name + " is not found");
            return BigDecimal.ZERO.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS);
        }

        // Closure Checks
        if (!plotBoundary.isClosed()) pl.getErrors().put("Plot boundary not closed", layerNames.getLayerName("LAYER_NAME_PLOT_BOUNDARY") + " is not closed ");
        if (level.equals("-1") && !buildFoorPrint.isClosed()) pl.getErrors().put("Building basement foot print not closed", layerNames.getLayerName("LAYER_NAME_BSMNT_FOOT_PRINT") + " is not closed ");
        else if (!buildFoorPrint.isClosed()) pl.getErrors().put("Building foot print not closed", layerNames.getLayerName("LAYER_NAME_BUILDING_FOOT_PRINT") + " is not closed ");
        if (!yardPolyline.isClosed()) pl.getErrors().put(name + " not closed", name + " is not closed ");

        if (!plotBoundary.isClosed() || !buildFoorPrint.isClosed() || !yardPolyline.isClosed()) {
            return BigDecimal.ZERO.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS);
        }

        if (!isYardOutsideOrTouchingBuildingOnly(yardPolyline, buildFoorPrint, name, pl, layerNames)) {
            return BigDecimal.ZERO.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS);
        }

        List<Point> yardPts = Util.pointsOnPolygon(yardPolyline);
        List<Point> fpPts = Util.pointsOnPolygon(buildFoorPrint);
        List<Point> pbPts = Util.pointsOnPolygon(plotBoundary);
        
        // =========================================================================================
        // STRICT INTERIOR CHECK: SETBACK CANNOT BE INSIDE THE BUILDING
        // =========================================================================================
        boolean isYardInsideFootprint = false;
        for (Point yp : yardPts) {
            // If any yard point is strictly inside the footprint, the drawing is invalid.
            if (Util.isPointStrictlyInsidePolygon(buildFoorPrint, yp)) {
                isYardInsideFootprint = true;
                break;
            }
        }

        // Also check the reverse: Footprint cannot be completely swallowed by a single setback
        if (!isYardInsideFootprint) {
            for (Point fp : fpPts) {
                if (Util.isPointStrictlyInsidePolygon(yardPolyline, fp)) {
                    isYardInsideFootprint = true;
                    break;
                }
            }
        }

        if (isYardInsideFootprint) {
            pl.getErrors().put(name + "_INSIDE_FOOTPRINT", name + " is drawn overlapping or inside the Building Footprint. Setbacks must be placed strictly outside the building area.");
            LOG.error("{} is drawn inside the building footprint.", name);
            return BigDecimal.ZERO.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS);
        }
        // =========================================================================================
        
        List<Point> insidePoints = new ArrayList<>();
        List<Point> outsidePoints = new ArrayList<>();

        // =========================================================================================
        // STRICT ENFORCEMENT: REJECT "OLD WAY" (Corner-to-Corner Touching)
        // =========================================================================================
        boolean isOldWay = false;
        
        // ONLY check Front and Rear yards. Side yards naturally touch footprint corners in the Sandwich style.
        if (name.contains("FRONT") || name.contains("REAR")) {
            for (Point yp : yardPts) {
                for (Point fp : fpPts) {
                    if (Util.pointsEquals(yp, fp)) {
                        isOldWay = true;
                        LOG.info("Detected OLD WAY marking for " + name + ". Rejecting to trigger native errors.");
                        break;
                    }
                }
                if (isOldWay) break;
            }
        }

        // =========================================================================================
        // POINT MAPPING (Only runs if marked the New Way)
        // =========================================================================================
        if (!isOldWay) {
            // 1. Check if Yard corners touch the Footprint or Plot Boundary
            for (Point yp : yardPts) {
                if (Util.isPointStrictlyInsidePolygon(buildFoorPrint, yp) || Util.isPointOnPolygonBoundary(buildFoorPrint, yp)) {
                    if(!insidePoints.contains(yp)) insidePoints.add(yp);
                }
                if (Util.isPointStrictlyInsidePolygon(plotBoundary, yp) || Util.isPointOnPolygonBoundary(plotBoundary, yp)) {
                    if(!outsidePoints.contains(yp)) outsidePoints.add(yp);
                }
            }

            // 2. Check if Footprint or Plot Boundary corners touch the Yard
            for (Point fp : fpPts) {
                if (Util.isPointStrictlyInsidePolygon(yardPolyline, fp) || Util.isPointOnPolygonBoundary(yardPolyline, fp)) {
                    if(!insidePoints.contains(fp)) insidePoints.add(fp);
                }
            }
            for (Point pb : pbPts) {
                if (Util.isPointStrictlyInsidePolygon(yardPolyline, pb) || Util.isPointOnPolygonBoundary(yardPolyline, pb)) {
                    if(!outsidePoints.contains(pb)) outsidePoints.add(pb);
                }
            }

            // Failsafe mappings
            if (insidePoints.isEmpty() || outsidePoints.isEmpty()) {
                List<DXFLine> yardLines = getLinesOfPolyline(yardPolyline);
                if (insidePoints.isEmpty()) insidePoints = Util.findPointsOnPolylines(fpPts, yardLines, pl, name);
                if (outsidePoints.isEmpty()) outsidePoints = Util.findPointsOnPolylines(pbPts, yardLines, pl, name);
            }
        }

        // =========================================================================================
        // NATIVE ERROR TRIGGERS
        // =========================================================================================
        if (insidePoints == null || insidePoints.isEmpty() || insidePoints.size() == 1) {
            String layer = level.equals("-1") ? "LAYER_NAME_BSMNT_FOOT_PRINT" : "LAYER_NAME_BUILDING_FOOT_PRINT";
            pl.getErrors().put("Set back calculation error for footprint " + name, "Points of " + name + " not properly on " + layerNames.getLayerName(layer));
        }
                        
        if (outsidePoints == null || outsidePoints.isEmpty() || outsidePoints.size() == 1) {
            pl.getErrors().put("Set back calculation error for boundary " + name, "Points of " + name + " not properly on " + layerNames.getLayerName("LAYER_NAME_PLOT_BOUNDARY"));
        }

        // =========================================================================================
        // FAST DISTANCE CALCULATION
        // =========================================================================================
        BigDecimal finalMinDistance = BigDecimal.ZERO;
        
        List<BigDecimal> yardWidthDistance = Util.getListOfDimensionByColourCode(pl, name, DxfFileConstants.YARD_DIMENSION_COLOR);
        if (!yardWidthDistance.isEmpty()) {
            yard.setMinimumDistance(Collections.min(yardWidthDistance));
        }

        if (yard.getMinimumDistance() != null && yard.getMinimumDistance().doubleValue() > 0.0d) {
            finalMinDistance = yard.getMinimumDistance();
        } else if (!isOldWay && insidePoints != null && outsidePoints != null && !insidePoints.isEmpty() && !outsidePoints.isEmpty()) {
            // Ultra-fast math-based segment distance
            double calcDist = calculateFastYardDistance(buildFoorPrint, plotBoundary, yardPolyline);
            if (calcDist > 0) {
                finalMinDistance = BigDecimal.valueOf(calcDist).setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, RoundingMode.HALF_UP);
            }
        }

        List<BigDecimal> distanceForMean = Util.getListOfDimensionOtherThanSpecifiedColourCode(doc, name, DxfFileConstants.YARD_DIMENSION_COLOR, pl);
        if (!distanceForMean.isEmpty()) {
            BigDecimal min = Collections.min(distanceForMean).setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, RoundingMode.HALF_UP);
            yard.setMean(yard.getArea().divide(min, DcrConstants.DECIMALDIGITS_MEASUREMENTS, RoundingMode.HALF_UP));
        } else if (yard.getMean() == null || yard.getMean().doubleValue() == 0.0d) {
            yard.setMean(finalMinDistance);
        }

        return finalMinDistance;
    }
    
    /**
     * Highly optimized mathematical distance calculator.
     * Only checks Footprint segments and Boundary segments that are actively inside/touching the Yard.
     */
    /**
     * Highly optimized mathematical distance calculator.
     * Checks Footprint segments against both Plot Boundary and Road Widening segments that are actively inside/touching the Yard.
     */
    /**
     * Highly optimized mathematical distance calculator.
     */
    private double calculateFastYardDistance(DXFLWPolyline footprint, DXFLWPolyline plotBoundary, DXFLWPolyline yard) {
        if (footprint == null || plotBoundary == null || yard == null) return 0.0;

        List<DXFLine> fpLines = getLinesOfPolyline(footprint);
        List<DXFLine> boundLines = getLinesOfPolyline(plotBoundary);
        
        double minDistance = Double.MAX_VALUE;
        boolean foundValidDistance = false;

        for (DXFLine fpLine : fpLines) {
            // Only process footprint lines that actually touch this specific yard
            Point midFp = new Point((fpLine.getStartPoint().getX() + fpLine.getEndPoint().getX()) / 2, (fpLine.getStartPoint().getY() + fpLine.getEndPoint().getY()) / 2, 0.0);
            if (!Util.isPointStrictlyInsidePolygon(yard, midFp) && !Util.isPointOnPolygonBoundary(yard, midFp)) continue;

            for (DXFLine bLine : boundLines) {
                // Only process boundary lines that actually touch this specific yard
                Point midB = new Point((bLine.getStartPoint().getX() + bLine.getEndPoint().getX()) / 2, (bLine.getStartPoint().getY() + bLine.getEndPoint().getY()) / 2, 0.0);
                if (!Util.isPointStrictlyInsidePolygon(yard, midB) && !Util.isPointOnPolygonBoundary(yard, midB)) continue;

                double dist = fastSegmentDistance(fpLine, bLine);
                if (dist < minDistance && dist > 0.0) { // Ignore 0 distance (intersections)
                    minDistance = dist;
                    foundValidDistance = true;
                }
            }
        }
        
        return foundValidDistance ? minDistance : 0.0;
    }

    private double fastSegmentDistance(DXFLine l1, DXFLine l2) {
        Point p1 = l1.getStartPoint(), p2 = l1.getEndPoint();
        Point p3 = l2.getStartPoint(), p4 = l2.getEndPoint();
        
        double d1 = ptSegDist(p1.getX(), p1.getY(), p3.getX(), p3.getY(), p4.getX(), p4.getY());
        double d2 = ptSegDist(p2.getX(), p2.getY(), p3.getX(), p3.getY(), p4.getX(), p4.getY());
        double d3 = ptSegDist(p3.getX(), p3.getY(), p1.getX(), p1.getY(), p2.getX(), p2.getY());
        double d4 = ptSegDist(p4.getX(), p4.getY(), p1.getX(), p1.getY(), p2.getX(), p2.getY());
        
        return Math.min(Math.min(d1, d2), Math.min(d3, d4));
    }

    private double ptSegDist(double px, double py, double sx1, double sy1, double sx2, double sy2) {
        double l2 = Math.pow(sx1 - sx2, 2) + Math.pow(sy1 - sy2, 2);
        if (l2 == 0) return Math.sqrt(Math.pow(px - sx1, 2) + Math.pow(py - sy1, 2));
        double t = Math.max(0, Math.min(1, ((px - sx1) * (sx2 - sx1) + (py - sy1) * (sy2 - sy1)) / l2));
        return Math.sqrt(Math.pow(px - (sx1 + t * (sx2 - sx1)), 2) + Math.pow(py - (sy1 + t * (sy2 - sy1)), 2));
    }
//    private double fastSegmentDistance(DXFLine l1, DXFLine l2) {
//        Point p1 = l1.getStartPoint(), p2 = l1.getEndPoint();
//        Point p3 = l2.getStartPoint(), p4 = l2.getEndPoint();
//        
//        double d1 = ptSegDist(p1.getX(), p1.getY(), p3.getX(), p3.getY(), p4.getX(), p4.getY());
//        double d2 = ptSegDist(p2.getX(), p2.getY(), p3.getX(), p3.getY(), p4.getX(), p4.getY());
//        double d3 = ptSegDist(p3.getX(), p3.getY(), p1.getX(), p1.getY(), p2.getX(), p2.getY());
//        double d4 = ptSegDist(p4.getX(), p4.getY(), p1.getX(), p1.getY(), p2.getX(), p2.getY());
//        
//        return Math.min(Math.min(d1, d2), Math.min(d3, d4));
//    }

//    private double ptSegDist(double px, double py, double sx1, double sy1, double sx2, double sy2) {
//        double l2 = Math.pow(sx1 - sx2, 2) + Math.pow(sy1 - sy2, 2);
//        if (l2 == 0) return Math.sqrt(Math.pow(px - sx1, 2) + Math.pow(py - sy1, 2));
//        double t = Math.max(0, Math.min(1, ((px - sx1) * (sx2 - sx1) + (py - sy1) * (sy2 - sy1)) / l2));
//        return Math.sqrt(Math.pow(px - (sx1 + t * (sx2 - sx1)), 2) + Math.pow(py - (sy1 + t * (sy2 - sy1)), 2));
//    }
    
    private boolean isPointNearSegment(Point p, Point v, Point w, double tolerance) {
        if (p == null || v == null || w == null) return false;
        
        double lengthSquared = Math.pow(v.getX() - w.getX(), 2) + Math.pow(v.getY() - w.getY(), 2);
        if (lengthSquared == 0) {
            return Math.sqrt(Math.pow(p.getX() - v.getX(), 2) + Math.pow(p.getY() - v.getY(), 2)) <= tolerance;
        }
        
        double t = Math.max(0, Math.min(1, ((p.getX() - v.getX()) * (w.getX() - v.getX()) + (p.getY() - v.getY()) * (w.getY() - v.getY())) / lengthSquared));
        
        double projX = v.getX() + t * (w.getX() - v.getX());
        double projY = v.getY() + t * (w.getY() - v.getY());
        
        double distance = Math.sqrt(Math.pow(p.getX() - projX, 2) + Math.pow(p.getY() - projY, 2));
        
        return distance <= tolerance;
    }
    
    private double calculateShortestDistanceBetweenPolylines(DXFLWPolyline footprint, DXFLWPolyline boundary, DXFLWPolyline yard) {
        if (footprint == null || boundary == null || yard == null) return 0.0;

        List<DXFLine> fpLines = getLinesOfPolyline(footprint);
        List<DXFLine> boundLines = getLinesOfPolyline(boundary);
        
        double minDistance = Double.MAX_VALUE;
        boolean foundValidDistance = false;

        // Bounding box optimization: only check lines that are roughly near the yard
        double yMinX = yard.getBounds().getMinimumX();
        double yMaxX = yard.getBounds().getMaximumX();
        double yMinY = yard.getBounds().getMinimumY();
        double yMaxY = yard.getBounds().getMaximumY();
        // Add a small buffer to the bounding box to account for floating point errors
        double buffer = 1.0; 

        for (DXFLine fpLine : fpLines) {
            // Only consider footprint lines that are in/touching the yard
            if (!isLineNearBounds(fpLine, yMinX - buffer, yMaxX + buffer, yMinY - buffer, yMaxY + buffer)) {
                continue;
            }

            for (DXFLine bLine : boundLines) {
                // Only consider boundary lines that are in/touching the yard
                if (!isLineNearBounds(bLine, yMinX - buffer, yMaxX + buffer, yMinY - buffer, yMaxY + buffer)) {
                    continue;
                }

                // Calculate the shortest distance between these two finite line segments
                double dist = getShortestDistanceBetweenSegments(fpLine, bLine);
                
                if (dist < minDistance) {
                    minDistance = dist;
                    foundValidDistance = true;
                }
            }
        }

        return foundValidDistance ? minDistance : 0.0;
    }

    /**
     * Helper to check if a line segment overlaps with a bounding box.
     */
    private boolean isLineNearBounds(DXFLine line, double minX, double maxX, double minY, double maxY) {
        double lxMin = Math.min(line.getStartPoint().getX(), line.getEndPoint().getX());
        double lxMax = Math.max(line.getStartPoint().getX(), line.getEndPoint().getX());
        double lyMin = Math.min(line.getStartPoint().getY(), line.getEndPoint().getY());
        double lyMax = Math.max(line.getStartPoint().getY(), line.getEndPoint().getY());

        return !(lxMax < minX || lxMin > maxX || lyMax < minY || lyMin > maxY);
    }

    /**
     * Calculates the shortest distance between two finite line segments in 2D space.
     */
    private double getShortestDistanceBetweenSegments(DXFLine l1, DXFLine l2) {
        Point p1 = l1.getStartPoint();
        Point p2 = l1.getEndPoint();
        Point p3 = l2.getStartPoint();
        Point p4 = l2.getEndPoint();

        double x1 = p1.getX(), y1 = p1.getY();
        double x2 = p2.getX(), y2 = p2.getY();
        double x3 = p3.getX(), y3 = p3.getY();
        double x4 = p4.getX(), y4 = p4.getY();

        // Distance between endpoints
        double d1 = pointToSegmentDistance(x1, y1, x3, y3, x4, y4);
        double d2 = pointToSegmentDistance(x2, y2, x3, y3, x4, y4);
        double d3 = pointToSegmentDistance(x3, y3, x1, y1, x2, y2);
        double d4 = pointToSegmentDistance(x4, y4, x1, y1, x2, y2);

        return Math.min(Math.min(d1, d2), Math.min(d3, d4));
    }

    /**
     * Calculates the shortest distance from a point (px, py) to a finite line segment (sx1, sy1) to (sx2, sy2).
     */
    private double pointToSegmentDistance(double px, double py, double sx1, double sy1, double sx2, double sy2) {
        double l2 = Math.pow(sx1 - sx2, 2) + Math.pow(sy1 - sy2, 2);
        if (l2 == 0) return Math.sqrt(Math.pow(px - sx1, 2) + Math.pow(py - sy1, 2));

        double t = Math.max(0, Math.min(1, ((px - sx1) * (sx2 - sx1) + (py - sy1) * (sy2 - sy1)) / l2));
        
        double projX = sx1 + t * (sx2 - sx1);
        double projY = sy1 + t * (sy2 - sy1);
        
        return Math.sqrt(Math.pow(px - projX, 2) + Math.pow(py - projY, 2));
    }

    public static Double getSideForMean(List<Point> yardInSidePoints, List<Point> yardOutSidePoints, Yard yard1) {
        DXFLWPolyline yard = ((YardDetail) yard1).getPolyLine();
        Double distance = 0d;
        Point firstPoint = null;
        Point lastPoint = null;
        BigDecimal maxDistance = BigDecimal.ZERO;
        BigDecimal maxDistance2 = BigDecimal.ZERO;
        Point firstPoint2 = null;
        Point lastPoint2 = null;
        for (Point out : yardInSidePoints) {

            LOG.debug("Out =" + out.getX() + "  " + out.getY());
            for (Point in : yardInSidePoints) {

                if (out == in) {
                    PrintUtil.print(in, "First Point");
                    continue;
                }
                distance = MathUtils.distance(out, in);

                if (maxDistance.compareTo(BigDecimal.valueOf(0)) == 0
                        || maxDistance.compareTo(BigDecimal.valueOf(distance)) < 0) {
                    maxDistance = BigDecimal.valueOf(distance);
                    firstPoint = out;
                    lastPoint = in;
                }
            }
        }

        for (Point out : yardOutSidePoints) {

            LOG.debug("Out =" + out.getX() + "  " + out.getY());
            for (Point in : yardOutSidePoints) {

                if (out == in) {
                    PrintUtil.print(in, "Second first Point");
                    continue;
                }
                distance = MathUtils.distance(out, in);

                if (maxDistance2.compareTo(BigDecimal.valueOf(0)) == 0
                        || maxDistance2.compareTo(BigDecimal.valueOf(distance)) < 0) {
                    maxDistance2 = BigDecimal.valueOf(distance);
                    firstPoint2 = out;
                    lastPoint2 = in;
                }
            }
        }
        if (firstPoint != null) {
            LOG.debug(" firstPoint x   " + firstPoint.getX() + "  " + firstPoint.getY() + "  lastpoint  "
                    + lastPoint.getX() + "  " + lastPoint.getY());

            Double absXValue = Math.abs(firstPoint.getX() - lastPoint.getX());
            Double absYValue = Math.abs(firstPoint.getY() - lastPoint.getY());
            LOG.debug(" maxDistance = " + maxDistance);
            LOG.debug("");
            LOG.debug(" Max x - Min x = " + absXValue);
            LOG.debug(" Max y - Min y = " + absYValue);
            LOG.debug("");

            Double absXValue2 = 0d;
            Double absYValue2 = 0d;
            if (firstPoint2 != null) {
                absXValue2 = Math.abs(firstPoint2.getX() - lastPoint2.getX());
                absYValue2 = Math.abs(firstPoint2.getY() - lastPoint2.getY());
                LOG.debug(" maxDistance2 = " + maxDistance2);
                LOG.debug("");
                LOG.debug(" Max x2 - Min x2 = " + absXValue2);
                LOG.debug(" Max y2 - Min y2 = " + absYValue2);
                LOG.debug("");

            }

            Double width1 = Math.abs(yard.getBounds().getWidth() - absXValue);
            Double height1 = Math.abs(yard.getBounds().getHeight() - absXValue);

            Double width2 = Math.abs(yard.getBounds().getWidth() - absYValue);
            Double height2 = Math.abs(yard.getBounds().getHeight() - absYValue);
            LOG.debug("");
            LOG.debug("Width1 = " + width1);
            LOG.debug("Width2 = " + width2);
            LOG.debug("Height1 = " + height1);
            LOG.debug("Height2 = " + height2);
            LOG.debug("");
            Double sideDistance = 0d;
            double minWidth = Math.min(width1, width2);
            double minHeight = Math.min(height1, height2);

            if (minWidth < minHeight) {
                // sideDistance=(absXValue + absXValue2)/2;
                sideDistance = absXValue > absXValue2 ? absXValue : absXValue2;

                // sideDistance = yard.getBounds().getWidth();
                LOG.debug("Distance for Mean Calculation is Width = " + sideDistance);
            } else {
                // sideDistance=(absYValue + absYValue2)/2;
                sideDistance = absYValue > absYValue2 ? absYValue : absYValue2;

                // sideDistance = yard.getBounds().getHeight();
                LOG.debug("Distance for Mean Calculation is Height = " + sideDistance);
            }
            /*
             * if(maxDistance.doubleValue() < maxDistance2.doubleValue()) maxDistance=maxDistance2;
             */

            LOG.debug(" Area = " + yard1.getArea());
            if (sideDistance > 0d && yard1.getArea() != null)
                // yard1.setMean(yard1.getArea().divide(maxDistance, 2, RoundingMode.HALF_UP));
                yard1.setMean(yard1.getArea().divide(BigDecimal.valueOf(sideDistance), 2, RoundingMode.HALF_UP));
            else
                yard1.setMean(BigDecimal.ZERO);
            LOG.debug("Mean   = " + yard1.getMean());
        }
        return null;
    }

    private static void removeDuplicates(List<Point> fromList, List<Point> containingList) {
        List<Point> toRemove = new ArrayList<>();
        for (Point p : fromList)
            for (Point p1 : containingList)
                if (Util.pointsEquals(p1, p)) {
                    PrintUtil.print(p, "Marked for Removal from outside");
                    toRemove.add(p);
                }

        for (Point p : toRemove)
            fromList.remove(p);
    }

    private static List<DXFLine> getLinesOfPolyline(DXFLWPolyline yard) {
        List<DXFLine> lines = new ArrayList<>();
        Iterator vertexIterator = yard.getVertexIterator();
        DXFVertex next = null;
        DXFVertex first = null;

        while (vertexIterator.hasNext()) {
            DXFVertex point1 = (DXFVertex) vertexIterator.next();
            if (next != null) {
                DXFLine line = new DXFLine();
                line.setStartPoint(next.getPoint());
                line.setEndPoint(point1.getPoint());
                lines.add(line);
            } else
                first = point1;
            next = point1;

        }
        if (next != null && first != null && !Util.pointsEquals(first.getPoint(), next.getPoint())) {
            // if (next!=null && first!=null) {
            DXFLine line = new DXFLine();
            line.setStartPoint(next.getPoint());
            line.setEndPoint(first.getPoint());
            lines.add(line);
        }
        PrintUtil.printLine(lines, yard.getLayerName());

        return lines;
    }

    private boolean checkYardNotOverlappingBuilding(
            DXFLWPolyline yardPolyline,
            DXFLWPolyline buildingFootprint,
            String yardName,
            PlanDetail pl,
            LayerNames layerNames) {

        // 1) Check if any Yard vertex lies strictly inside footprint
        Iterator yardItr = yardPolyline.getVertexIterator();
        while (yardItr.hasNext()) {
            DXFVertex v = (DXFVertex) yardItr.next();
            Point p = v.getPoint();

            if (Util.isPointStrictlyInsidePolygon(buildingFootprint, p)) {
                pl.getErrors().put("Set back calculation Error - " + yardName,
                        "Point (" + p.getX() + ", " + p.getY() + ") of " + yardName +
                                " lies INSIDE the Building Footprint (" +
                                layerNames.getLayerName("LAYER_NAME_BUILDING_FOOT_PRINT") + ")");
                return false;
            }
        }

        // 2) Check segment intersections (yard edges must NOT cut footprint edges)
        List<DXFLine> yardLines = getLinesOfPolyline(yardPolyline);
        List<DXFLine> footprintLines = getLinesOfPolyline(buildingFootprint);

        for (DXFLine yl : yardLines) {
            for (DXFLine fl : footprintLines) {

                try {
                    if (Util.doLineSegmentsIntersect(
                            yl.getStartPoint(), yl.getEndPoint(),
                            fl.getStartPoint(), fl.getEndPoint())) {

                        if (!Util.pointsEquals(yl.getStartPoint(), fl.getStartPoint()) &&
                            !Util.pointsEquals(yl.getStartPoint(), fl.getEndPoint()) &&
                            !Util.pointsEquals(yl.getEndPoint(), fl.getStartPoint()) &&
                            !Util.pointsEquals(yl.getEndPoint(), fl.getEndPoint())) {                     	
                            pl.getErrors().put("Set back calculation error for boundary" + yardName,
                                    "Points of " + yardName + " not properly on " + layerNames.getLayerName("LAYER_NAME_BUILDING_FOOT_PRINT"));
                            return false;
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        return true; // No errors found
    }
    
    
    public static boolean isYardOutsideOrTouchingBuildingOnly(
            DXFLWPolyline yardPolyline,
            DXFLWPolyline buildingFootprint,
            String yardName,
            PlanDetail pl,
            LayerNames layerNames) {

        if (yardPolyline == null || buildingFootprint == null) return true;

        List<DXFLine> yardLines = getLinesOfPolyline(yardPolyline);

        for (DXFLine yLine : yardLines) {
            Point y1 = yLine.getStartPoint();
            Point y2 = yLine.getEndPoint();

            // ---- 1. Check Start Vertex ----
            if (Util.isPointStrictlyInsidePolygon(buildingFootprint, y1)) {

                pl.getErrors().put(
                    "YARD_POINTS_NOT_ON_PLOT_BOUNDARY - " + yardName,
                    "Points of " + yardName + " not properly on " + layerNames.getLayerName("LAYER_NAME_BUILDING_FOOT_PRINT"));
               
                return false;
            }

            // ---- 2. Check End Vertex ----
            if (Util.isPointStrictlyInsidePolygon(buildingFootprint, y2)) {

                try {
                	pl.getErrors().put("Set back calculation error for boundary" + yardName,
                            "Points of " + yardName + " not properly on " + layerNames.getLayerName("LAYER_NAME_BUILDING_FOOT_PRINT"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

                return false;
            }

            // ---- 3. Check Midpoint ----
            Point mid = new Point();
            mid.setX((y1.getX() + y2.getX()) / 2.0);
            mid.setY((y1.getY() + y2.getY()) / 2.0);

            if (Util.isPointStrictlyInsidePolygon(buildingFootprint, mid)) {

                pl.getErrors().put(
                    "YARD_POINTS_NOT_ON_PLOT_BOUNDARY - " + yardName,
                    "Points of " + yardName + " not properly on PLOT_BOUNDARY"
                );

                // Keep your DXF debugging line
                PrintUtil.printForDXf(y1, y2, yardName + "_EDGE_INSIDE", pl);

                return false;
            }
        }

        return true;
    }


    public static Point getMidPoint(Point p1, Point p2, int scale) {

        BigDecimal x1 = BigDecimal.valueOf(p1.getX());
        BigDecimal y1 = BigDecimal.valueOf(p1.getY());
        BigDecimal x2 = BigDecimal.valueOf(p2.getX());
        BigDecimal y2 = BigDecimal.valueOf(p2.getY());

        BigDecimal two = BigDecimal.valueOf(2);

        BigDecimal midX = x1.add(x2).divide(two, scale, RoundingMode.HALF_UP);
        BigDecimal midY = y1.add(y2).divide(two, scale, RoundingMode.HALF_UP);

        Point mid = new Point();
        mid.setX(midX.doubleValue());
        mid.setY(midY.doubleValue());

        return mid;
    }

    
}