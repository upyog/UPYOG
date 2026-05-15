package org.egov.edcr.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.egov.common.entity.bpa.SubOccupancy;
import org.egov.common.entity.bpa.Usage;
import org.egov.common.entity.dcr.helper.OccupancyHelperDetail;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.EdcrPdfDetail;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyType;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plot;
import org.egov.common.entity.edcr.TypicalFloor;
import org.egov.commons.mdms.LayerErrorType;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.entity.blackbox.PlanDetail;
import org.egov.edcr.service.LayerNames;
import org.egov.edcr.utility.math.Polygon;
import org.kabeja.dxf.DXFBlock;
import org.kabeja.dxf.DXFCircle;
import org.kabeja.dxf.DXFConstants;
import org.kabeja.dxf.DXFDimension;
import org.kabeja.dxf.DXFDimensionStyle;
import org.kabeja.dxf.DXFDocument;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.DXFLWPolyline;
import org.kabeja.dxf.DXFLayer;
import org.kabeja.dxf.DXFLine;
import org.kabeja.dxf.DXFMText;
import org.kabeja.dxf.DXFPolyline;
import org.kabeja.dxf.DXFText;
import org.kabeja.dxf.DXFVertex;
import org.kabeja.dxf.helpers.Point;
import org.kabeja.dxf.helpers.StyledTextParagraph;
import org.kabeja.math.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Util {
    public static final int COMPARE_WITH_2_PERCENT_ERROR_DIGITS = 2;
    private static final int DECIMALDIGITS = 10;
    private static String FLOOR_NAME_PREFIX = "FLOOR_";
    static final Logger LOG = LogManager.getLogger(Util.class);
    private static final BigDecimal ONEHUNDREDFIFTY = BigDecimal.valueOf(150);
    private static final BigDecimal FIFTY = BigDecimal.valueOf(50);
    private static final BigDecimal THREEHUNDRED = BigDecimal.valueOf(300);

    @Autowired
    public LayerNames layerNames;

    
 // 1. Helper class for Layer Rules
    private static class LayerRule {
        Pattern pattern;
        int colorCode;

        LayerRule(String regex, int colorCode) {
            String finalizedRegex = regex
                .replace("_n_", "_\\d+_")
                .replace("_l_", "_\\d+_")
                .replace("_i_", "_\\d+_")
                .replace("_k", "_\\d+")
                .replace("_m", "_\\d+");
            this.pattern = Pattern.compile(finalizedRegex, Pattern.CASE_INSENSITIVE);
            this.colorCode = colorCode;
        }
    }

    private static final List<LayerRule> LAYER_RULES = new ArrayList<>();

    static {
        // --- SITE & BUILDING BASICS ---
        LAYER_RULES.add(new LayerRule(".*BLDG_FOOT_PRINT$", 61));
        LAYER_RULES.add(new LayerRule(".*BLDG_FOOT_PRINT_BASEMENT$", 62));
        LAYER_RULES.add(new LayerRule(".*COVERED_AREA$", 63));
        LAYER_RULES.add(new LayerRule(".*COVERED_AREA_DEDUCT$", 64));
        LAYER_RULES.add(new LayerRule(".*FLOOR_HEIGHT$", 66));
        LAYER_RULES.add(new LayerRule(".*HT_OF_BLDG$", 67));
        LAYER_RULES.add(new LayerRule(".*HT_OF_BLDG_EXCLUDING_MP$", 68));
        LAYER_RULES.add(new LayerRule(".*PLINTH_HEIGHT$", 69));
        LAYER_RULES.add(new LayerRule(".*FRONT_SETBACK$", 70));
        LAYER_RULES.add(new LayerRule(".*REAR_SETBACK$", 71));
        LAYER_RULES.add(new LayerRule(".*SIDE_SETBACK1$", 72));
        LAYER_RULES.add(new LayerRule(".*SIDE_SETBACK2$", 73));
        
     // FIRE STAIR (handle FIRESTAIR and FIRE_STAIR both)
        LAYER_RULES.add(new LayerRule(".*FIRE[_]?STAIR(_\\d+)?$", 97));
        LAYER_RULES.add(new LayerRule(".*FIRE[_]?STAIR(_\\d+)?_FLIGHT(_\\d+)?$", 98));
        LAYER_RULES.add(new LayerRule(".*FIRE[_]?STAIR(_\\d+)?_LANDING(_\\d+)?$", 99));
        LAYER_RULES.add(new LayerRule(".*SPIRA[_]?FIRE[_]?STAIR(_\\d+)?$", 102));


        // --- STAIRS & CIRCULATION ---
        LAYER_RULES.add(new LayerRule(".*STAIR_k$", 74));
        LAYER_RULES.add(new LayerRule(".*STAIR_k_FLIGHT_k$", 75));
        LAYER_RULES.add(new LayerRule(".*STAIR_k_FLIGHT_m_LENGTH$", 1));      // Sub-code 01 (Length)
        LAYER_RULES.add(new LayerRule(".*STAIR_k_FLIGHT_m_WIDTH$", 2));       // Sub-code 02 (Width)
        LAYER_RULES.add(new LayerRule(".*STAIR_k_FLIGHT_m_TREADS$", 3));	// Sub-code 03 (Treads)
        
        LAYER_RULES.add(new LayerRule(".*STAIR_k_LANDING_m$", 76));
        LAYER_RULES.add(new LayerRule(".*STAIR_k_LANDING_m_LENGTH$", 1));     // Sub-code 01
        LAYER_RULES.add(new LayerRule(".*STAIR_k_LANDING_m_WIDTH$", 2));      // Sub-code 02
 
        // --- ROOMS & SANITATION ---
        //LAYER_RULES.add(new LayerRule(".*REGULAR_ROOM_k$", 77)); 
        LAYER_RULES.add(new LayerRule(".*REGULAR_ROOM_k$", 78)); 
        //LAYER_RULES.add(new LayerRule(".*REGULAR_ROOM_k$", 79)); 
        LAYER_RULES.add(new LayerRule(".*STORE_ROOM_k$", 80));
        LAYER_RULES.add(new LayerRule(".*KITCHEN(_\\d+)?$", 81));
        LAYER_RULES.add(new LayerRule(".*KITCHEN(_\\d+)?$", 82));
        LAYER_RULES.add(new LayerRule(".*KITCHEN(_\\d+)?$", 83));
        LAYER_RULES.add(new LayerRule(".*REGULAR_ROOM_k$", 84)); 
        LAYER_RULES.add(new LayerRule(".*BATH_k$", 85));
        LAYER_RULES.add(new LayerRule(".*WATER_CLOSET_k$", 86));
        LAYER_RULES.add(new LayerRule(".*URINAL$", 87));
        LAYER_RULES.add(new LayerRule(".*WASH$", 88));
        LAYER_RULES.add(new LayerRule(".*WATER_CLOSET$", 89));
        LAYER_RULES.add(new LayerRule(".*DRINKING_WATER$", 90));
        LAYER_RULES.add(new LayerRule(".*TOILET_k$", 91));
        LAYER_RULES.add(new LayerRule(".*TOILET_VENTILATION$", 92));

        // --- BASEMENT & PARKING ---
        LAYER_RULES.add(new LayerRule(".*BASEMENT_FOOT_PRINT$", 110));
        LAYER_RULES.add(new LayerRule(".*BSMNT_FRONT_SETBACK$", 103));
        LAYER_RULES.add(new LayerRule(".*BSMNT_REAR_SETBACK$", 104));
        LAYER_RULES.add(new LayerRule(".*BSMNT_SIDE_SETBACK1$", 7));
        LAYER_RULES.add(new LayerRule(".*BSMNT_SIDE_SETBACK2$", 8));
        LAYER_RULES.add(new LayerRule(".*OPEN_PARKING$", 116));
        LAYER_RULES.add(new LayerRule(".*STILT(_PARKING)?(_\\d+)?$", 117));
        LAYER_RULES.add(new LayerRule(".*MECH_PARKING$", 118));
        LAYER_RULES.add(new LayerRule(".*VISITOR_PARKING$", 119));
        LAYER_RULES.add(new LayerRule(".*SPECIAL_PARKING$", 120));
        LAYER_RULES.add(new LayerRule("^TWO_WHEELER_PARKING$", 132));
        LAYER_RULES.add(new LayerRule("^DA_PARKING$", 134));
        LAYER_RULES.add(new LayerRule("^LOADING_UNLOADING$", 133));

        // --- UNIT AREA & LIFTS (Added from previous tables) ---
        LAYER_RULES.add(new LayerRule(".*UNITFA$", 126));
        LAYER_RULES.add(new LayerRule(".*UNITFA_DEDUCT$", 127));
        LAYER_RULES.add(new LayerRule(".*LIFT_k$", 128));
        LAYER_RULES.add(new LayerRule(".*DA_LIFT$", 138));

        // --- DOORS, WINDOWS & SERVICES ---
        LAYER_RULES.add(new LayerRule(".*REGULAR_ROOM_\\d+_DOOR_\\d+$", 139));
        LAYER_RULES.add(new LayerRule(".*WINDOW_\\d+$", 140));
        LAYER_RULES.add(new LayerRule(".*NON_HABITATIONAL_DOOR_\\d+$", 146));
        LAYER_RULES.add(new LayerRule("^SOLAR_PANEL$", 143));
        LAYER_RULES.add(new LayerRule("^SOLAR_WATER_HEATER$", 144));
        LAYER_RULES.add(new LayerRule("^WASTE_MANAGEMENT$", 14));
        LAYER_RULES.add(new LayerRule(".*SHADE_OVERHANG$", 3));
        LAYER_RULES.add(new LayerRule(".*FIRE_TENDER_MOVEMENT$", 148));
        LAYER_RULES.add(new LayerRule("^MAIN_GATE$", 1));
        LAYER_RULES.add(new LayerRule("^DIST_EXIT$", 149));
        LAYER_RULES.add(new LayerRule(".*BLT_UP_AREA_DEDUCT_.*$", 147));
        
        // --- GREEN ---
        LAYER_RULES.add(new LayerRule("^PLANTATION_GREENSTRIP$", 125));

        // --- ROOF WATER TANK HEIGHT ---
        LAYER_RULES.add(new LayerRule("^BLK_\\d+_ROOF_WATER_TANK_HT$", 131));

        // --- PARAPET HEIGHT ---
        LAYER_RULES.add(new LayerRule("^BLK_\\d+_PARAPET_HT$", 110));

        // --- EXIT WIDTH DOOR ---
        LAYER_RULES.add(new LayerRule("^BLK_\\d+_FLR_-?\\d+_EXIT_WIDTH_DOOR$", 100));

        // --- RAIN WATER HARVESTING ---
        LAYER_RULES.add(new LayerRule("^RWH$", 112));

        // --- NORTH DIRECTION ---
        LAYER_RULES.add(new LayerRule("^NORTH_DIRECTION$", 108));

        // --- LOCATION PLAN ---
        LAYER_RULES.add(new LayerRule("^LOCATION_PLAN$", 109));

        // --- STAIR HEADROOM ---
        LAYER_RULES.add(new LayerRule("^BLK_\\d+_STAIR_HEADROOM$", 107));

        // --- LIGHT & VENTILATION ---
        LAYER_RULES.add(new LayerRule("^BLK_\\d+_FLR_-?\\d+_LIGHT_VENTILATION$", 121));

        // --- TOILET VENTILATION ---
        LAYER_RULES.add(new LayerRule("^BLK_\\d+_FLR_-?\\d+_TOILET_\\d+_VENTILATION$", 92));

        
    }

    public static int resolveLayerColor(String layerName) {
        try {
            if (StringUtils.isBlank(layerName)) {
                throw new IllegalArgumentException("Layer name cannot be null or empty.");
            }
            final String normalized = layerName.trim().toUpperCase();
            return LAYER_RULES.stream()
                    .filter(rule -> rule.pattern.matcher(normalized).matches())
                    .map(rule -> rule.colorCode)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Layer '" + layerName + "' is not defined in building plan standards."));

        } catch (IllegalArgumentException | NoSuchElementException e) {
            LOG.error("Color Resolution Failed: " + e.getMessage());
            return -1;
        } catch (Exception e) {
            LOG.error("Unexpected error resolving layer: " + layerName, e);
            return -1;
        }
    }
    
    /**
     * Resolves color codes for specific features (Length, Width, Treads) within a layer.
     * This prevents impact on the standard resolveLayerColor method.
     */
    public static int resolveLayerFeatureColor(String layerName, String feature) {
        try {
            if (StringUtils.isBlank(layerName) || StringUtils.isBlank(feature)) {
                throw new IllegalArgumentException("Layer name and Feature cannot be null.");
            }

            // Combine layer name and feature to match the sub-rules (e.g., "STAIR_1_FLIGHT_1_LENGTH")
            final String lookupName = layerName.trim().toUpperCase() + "_" + feature.trim().toUpperCase();

            return LAYER_RULES.stream()
                    .filter(rule -> rule.pattern.matcher(lookupName).matches())
                    .map(rule -> rule.colorCode)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Feature '" + feature + "' not found for layer: " + layerName));

        } catch (Exception e) {
            LOG.error("Feature Resolution Failed: " + e.getMessage());
            return -1;
        }
    }
    
    public static void validateLayerColor(String layerName, int actualColorCode, PlanDetail pl) {
        int expectedColorCode = resolveLayerColor(layerName);
        if (expectedColorCode == -1) {
            Map<String, String> errors = new HashMap<>();
            errors.put(LayerErrorType.INVALID_LAYER.getKey(layerName),LayerErrorType.INVALID_LAYER.getMessage(layerName));
            pl.addErrors(errors);
            return;
        }

        if (actualColorCode != expectedColorCode) {
            Map<String, String> errors = new HashMap<>();
            errors.put(
                    LayerErrorType.INVALID_COLOR.getKey(layerName),
                    LayerErrorType.INVALID_COLOR.getMessage(layerName,expectedColorCode,actualColorCode));
            pl.addErrors(errors);
        }
    }

    public static void validateSubLayerColor(String layerName, String feature, int actualColorCode, PlanDetail pl) {
        // 1. Resolve the expected sub-color (01, 02, 03, etc.)
        int expectedColorCode = resolveLayerFeatureColor(layerName, feature);

        // 2. Handle cases where the layer/feature combo isn't in our standards
        if (expectedColorCode == -1) {
            Map<String, String> errors = new HashMap<>();
            // Using a unique key for the sub-layer error
            String errorKey = "INVALID_SUB_LAYER_" + layerName + "_" + feature;
            String message = String.format("Sub-feature '%s' is not defined for layer '%s'.", feature, layerName);
            errors.put(errorKey, message);
            pl.addErrors(errors);
            return;
        }

        // 3. Compare the actual DXF entity color against the expected sub-color
        if (actualColorCode != expectedColorCode) {
            Map<String, String> errors = new HashMap<>();
            String errorKey = "INVALID_SUB_COLOR_" + layerName + "_" + feature;
            String message = String.format("Invalid color for %s on layer %s. Expected: %d, Found: %d", 
                                            feature, layerName, expectedColorCode, actualColorCode);
            errors.put(errorKey, message);
            pl.addErrors(errors);
        }
    }
    
    public static Map<String, String> getColorByDimensionByLayer(PlanDetail planDetail,String name) {
        Map<String, String> result = new HashMap<>();
        if (planDetail == null || planDetail.getDoc() == null || name == null) {
            return result;
        }
        DXFDocument dxfDocument = planDetail.getDoc();
        String layerName = name.trim().toUpperCase();
        DXFLayer dxfLayer = dxfDocument.getDXFLayer(layerName);
        if (dxfLayer != null && dxfLayer.getName().equalsIgnoreCase(layerName)) {
            List<?> dxfEntities = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_DIMENSION);
            if (dxfEntities != null && !dxfEntities.isEmpty()) {
                Object entity = dxfEntities.get(0);
                if (entity instanceof DXFDimension) {
                    int colorCode = ((DXFDimension) entity).getColor();
                    result.put("layerName", layerName);
                    result.put("colorCode", String.valueOf(colorCode));
                    return result;
                }
            }
        }

        // Optional fallback
        result.put("layerName", layerName);
        result.put("colorCode", "0");

        return result;
    }
    
    public static Map<String, String> getColorByDimensionByLayerByColorCode(PlanDetail planDetail,String name, 
    		int expectedColorCode) {
    	Map<String, String> result = new HashMap<>();

        if (planDetail == null || planDetail.getDoc() == null || name == null) {
            return result;
        }

        DXFDocument dxfDocument = planDetail.getDoc();
        String layerName = name.trim().toUpperCase();
        DXFLayer dxfLayer = dxfDocument.getDXFLayer(layerName);
        if (dxfLayer != null && dxfLayer.getName().equalsIgnoreCase(layerName)) {
            List<?> dxfEntities = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_DIMENSION);
            if (dxfEntities != null && !dxfEntities.isEmpty()) {
                for (Object entity : dxfEntities) {
                    if (entity instanceof DXFDimension) {
                        DXFDimension dimension = (DXFDimension) entity;
                        int actualColorCode = dimension.getColor();
                        if (actualColorCode == expectedColorCode) {
                            result.put("layerName", layerName);
                            result.put("colorCode", String.valueOf(actualColorCode));
                            return result;
                        }
                    }
                }
            }
        }
        result.put("layerName", layerName);
        result.put("colorCode", "0");
        return result;
    }
    
    public static int getColorByPolyLine(List<DXFLWPolyline> polyLinesByLayer) {
    	if (polyLinesByLayer !=null && !polyLinesByLayer.isEmpty()) {
    		return polyLinesByLayer.get(0).getColor();
        }
        return 0;
    }
    
    public static int getColorCodeByMTextANDLayerName(DXFDocument doc, String layerName) {
          DXFLayer planInfoLayer = doc.getDXFLayer(layerName);
            if (planInfoLayer != null) {
                List texts = planInfoLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_MTEXT);
                DXFText text = null;
                if (texts != null) {
                    Iterator iterator = texts.iterator();
                    while (iterator.hasNext()) {
                        text = (DXFText) iterator.next();
                        if (text != null && text.getText() != null) {                           
                        	return text.getColor();
                        }
                    }
                }
            }
            return 0;
    }
    
    public static List<Point> findPointsOnPolylines(List<Point> yardInSidePoints) {
        Point old = null;
        Point point1 = new Point();
        List<Point> myPoints = new ArrayList<>();

        for (Point in : yardInSidePoints) {

            if (old == null) {
                old = in;
                continue;
            }

            double distance = MathUtils.distance(old, in);

            // if(LOG.isDebugEnabled()) LOG.debug("Distance"+distance);

            for (double j = .01; j < distance; j = j + .01) {
                point1 = new Point();
                double t = j / distance;
                point1.setX((1 - t) * old.getX() + t * in.getX());
                point1.setY((1 - t) * old.getY() + t * in.getY());
                myPoints.add(point1);

            }

            old = in;
        }
        return myPoints;
    }

    public static List<Point> findPointsOnPolylines(List<Point> yardInSidePoints, List<DXFLine> lines, PlanDetail pl,
            String layerName) {
        Point point1 = new Point();
        List<DXFLine> pointsOnLineList = new ArrayList<>();
        List<Point> myPoints = new ArrayList<>();
        LOG.debug("finding line for the List points ..... ");
        for (Point old : yardInSidePoints) {
            PrintUtil.print(old, " ++++ from yardInSidePoints +++ ");
            for (Point in : yardInSidePoints) {
                PrintUtil.print(in, "\t\t to yardInSidePoints");
                if (old != in) {
                    DXFLine pointOnLine = isALine(old, in, lines);
                    if (pointOnLine != null && !pointsOnLineList.contains(pointOnLine)) {
                        LOG.debug("\t\tThis line is not added yet ");
                        pointsOnLineList.add(pointOnLine);
                        double distance = MathUtils.distance(old, in);
                        for (double j = .01; j < distance; j = j + .01) {
                            point1 = new Point();
                            double t = j / distance;
                            point1.setX((1 - t) * old.getX() + t * in.getX());
                            point1.setY((1 - t) * old.getY() + t * in.getY());
                            myPoints.add(point1);
                            LOG.debug("\t\tadded" + point1.getX() + "---" + point1.getY());
                        }
                    } else
                        LOG.debug("  This line is already added  ");
                }
                LOG.debug("pointsOnLineList ->>>>>>>>>>size " + pointsOnLineList.size());
            }
        }
        PrintUtil.printForDXfPoint(myPoints, layerName + "_CALCULATION", pl);

        return myPoints;
    }

    public static List<DXFDimension> getDimensionsByLayer(DXFDocument dxfDocument, String name) {
        if (dxfDocument == null)
            return Collections.emptyList();
        if (name == null)
            return Collections.emptyList();
        name = name.toUpperCase();

        if (dxfDocument.containsDXFLayer(name)) {
            DXFLayer dxfLayer = dxfDocument.getDXFLayer(name);
            List<DXFDimension> dimensions = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_DIMENSION);
            if (dimensions != null)
                return dimensions;
        }
        return Collections.emptyList();
    }

    protected static int getFloorCountExcludingCeller(DXFDocument dxfDocument, Integer colorCode) {
        int i = 0;
        Iterator dxfLayerIterator = dxfDocument.getDXFLayerIterator();
        while (dxfLayerIterator.hasNext()) {

            DXFLayer dxfLayer = (DXFLayer) dxfLayerIterator.next();

            if (colorCode != null && dxfLayer.getColor() == colorCode
                    || dxfLayer.getName().startsWith(FLOOR_NAME_PREFIX))
                try {

                    if (colorCode != null && dxfLayer.getColor() == colorCode)
                        i++;
                    else {
                        String[] floorName = dxfLayer.getName().split(FLOOR_NAME_PREFIX);
                        if (floorName.length > 0 && floorName[1] != null && Integer.parseInt(floorName[1]) >= 0)
                            i++;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    // throw new RuntimeException("Floor number not in format");
                    // //TODO: HANDLE THIS LATER
                }

        }

        return i;
    }

    public static List<String> getLayerNamesLike(DXFDocument doc, String regExp) {
        Set<String> layerNames = new TreeSet<>();
        List<String> disNames = new ArrayList();
        Iterator dxfLayerIterator = doc.getDXFLayerIterator();
        while (dxfLayerIterator.hasNext()) {
            DXFLayer name = (DXFLayer) dxfLayerIterator.next();
            Pattern pat = Pattern.compile(regExp);
            LOG.trace(pat);
            Matcher m = pat.matcher(name.getName());
            while (m.find()) {
                String group = m.group();
                LOG.trace("Found:" + group);
                layerNames.add(group);
            }

        }
        disNames.addAll(layerNames);
        return disNames;
    }
    
    public static List<String> getAllLayersNameLike(DXFDocument doc, String regExp) {
    	List<String> layerNames = new ArrayList<>();
        if (doc == null || regExp == null) {
            return layerNames;
        }
        Pattern pattern = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);
        Iterator<?> iterator = doc.getDXFLayerIterator();
        while (iterator.hasNext()) {
            DXFLayer layer = (DXFLayer) iterator.next();
            String name = layer.getName();
            if (name == null) {
                continue;
            }
            Matcher matcher = pattern.matcher(name);
            if (matcher.matches()) {
                layerNames.add(name);
            }
        }
        return layerNames;
    }


    public static List<DXFLine> getLinesByLayer(DXFDocument dxfDocument, String name) {
        List<DXFLine> lines = new ArrayList<>();
        if (name == null)
            return lines;
        name = name.toUpperCase();

        DXFLayer dxfLayer = dxfDocument.getDXFLayer(name);
        // if layer with name not found kabeja will return default layer or
        // create new layer and gives
        if (dxfLayer.getName().equalsIgnoreCase(name)) {
            List dxfPolyLineEntities = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_LINE);

            if (null != dxfPolyLineEntities)
                for (Object dxfEntity : dxfPolyLineEntities) {

                    DXFLine line = (DXFLine) dxfEntity;

                    if (name.contains(line.getLayerName().toUpperCase()))
                        lines.add(line);

                }

        }
        return lines;
    }

    /**
     * Get List of dimension values by passing color code
     *
     * @param dxfDocument
     * @param name
     * @param colourCode
     * @return
     */
    public static List<BigDecimal> getListOfDimensionByColourCode(PlanDetail planDetail, String name,
            int colourCode) {
        DXFDocument dxfDocument = planDetail.getDoc();
        if (dxfDocument == null)
            return Collections.emptyList();
        if (name == null)
            return Collections.emptyList();
        name = name.toUpperCase();
        List<BigDecimal> values = new ArrayList<>();

        DXFLayer dxfLayer = dxfDocument.getDXFLayer(name);
        if (dxfLayer != null && dxfLayer.getName().equalsIgnoreCase(name)) {
            List dxfLineEntities = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_DIMENSION);
            if (null != dxfLineEntities)
                for (Object dxfEntity : dxfLineEntities) {
                    DXFDimension line = (DXFDimension) dxfEntity;
                    if (line.getColor() == colourCode)
                        extractDimensionValue(planDetail, values, line, dxfLayer.getName());
                }
        }
        return values;
    }

    /**
     * Get List of dimension values which are other than colour code passed as parameter.
     *
     * @param dxfDocument
     * @param name
     * @param colourCode
     * @return
     */
    public static List<BigDecimal> getListOfDimensionOtherThanSpecifiedColourCode(DXFDocument dxfDocument, String name,
            int colourCode, PlanDetail planDetail) {

        if (dxfDocument == null)
            return null;
        if (name == null)
            return null;
        name = name.toUpperCase();
        List<BigDecimal> values = new ArrayList<>();

        DXFLayer dxfLayer = dxfDocument.getDXFLayer(name);
        if (dxfLayer != null && dxfLayer.getName().equalsIgnoreCase(name)) {
            List dxfLineEntities = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_DIMENSION);
            if (null != dxfLineEntities)
                for (Object dxfEntity : dxfLineEntities) {
                    DXFDimension line = (DXFDimension) dxfEntity;
                    if (line.getColor() != colourCode)
                        extractDimensionValue(planDetail, values, line, dxfLayer.getName());
                }

        }
        return values;

    }

    public static void extractDimensionValue(PlanDetail planDetail, List<BigDecimal> dimensionValues, DXFDimension line,
            String layerName) {
        DXFDocument dxfDocument = planDetail.getDoc();
        String dimensionBlock = line.getDimensionBlock();
        if (line.getDXFDimensionStyle() != null)
            LOG.info("DIM Style Name=" + line.getDXFDimensionStyle().getName());
        DXFBlock dxfBlock = dxfDocument.getDXFBlock(dimensionBlock);
        if (!planDetail.getStrictlyValidateDimension()) {
            Iterator dxfEntitiesIterator = dxfBlock.getDXFEntitiesIterator();
            while (dxfEntitiesIterator.hasNext()) {
                DXFEntity e = (DXFEntity) dxfEntitiesIterator.next();
                if (e.getType().equals(DXFConstants.ENTITY_TYPE_MTEXT)) {
                    DXFMText text = (DXFMText) e;
                    String text2 = text.getText();

                    Iterator styledParagraphIterator = text.getTextDocument().getStyledParagraphIterator();
                    while (styledParagraphIterator.hasNext()) {
                        StyledTextParagraph next = (StyledTextParagraph) styledParagraphIterator.next();
                        text2 = next.getText();
                    }

                    if (planDetail.getDrawingPreference() != null &&
                            org.egov.infra.utils.StringUtils.isNotBlank(planDetail.getDrawingPreference().getUom())
                            && (DxfFileConstants.INCH_UOM.equalsIgnoreCase(planDetail.getDrawingPreference().getUom())
                                    || DxfFileConstants.FEET_UOM.equalsIgnoreCase(planDetail.getDrawingPreference().getUom()))
                            && StringUtils.isNotBlank(text2)) {
                        BigDecimal convertedValue = convertToInch(text2);
                        dimensionValues.add(convertedValue);
                    } else {
                        if (text2.contains(";")) {
                            String[] textSplit = text2.split(";");
                            int length = textSplit.length;

                            if (length >= 1) {
                                int index = length - 1;
                                text2 = textSplit[index];
                                text2 = text2.replaceAll("[^\\d.]", "");
                            } else
                                text2 = text2.replaceAll("[^\\d.]", "");
                        } else
                            text2 = text2.replaceAll("[^\\d.]", "");

                        if (!text2.isEmpty())
                            dimensionValues.add(BigDecimal.valueOf(Double.parseDouble(text2)));
                    }

                }
            }
        } else {
            List<DXFLine> lines = new ArrayList<>();
            String text2 = null;
            Iterator dxfEntitiesIterator = dxfBlock.getDXFEntitiesIterator();
            List<BigDecimal> values = new ArrayList<>();
            List<BigDecimal> specialValues = new ArrayList<>();
            List<BigDecimal> byWeight = new ArrayList<>();
            while (dxfEntitiesIterator.hasNext()) {
                DXFEntity e = (DXFEntity) dxfEntitiesIterator.next();
                if (e.getType().equals(DXFConstants.ENTITY_TYPE_LINE)) {
                    DXFLine dxfLine = (DXFLine) e;
                    lines.add(dxfLine);
                    BigDecimal dub1 = new BigDecimal(dxfLine.getLength());
                    dub1 = dub1.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS);
                    values.add(dub1);
                    if (dxfLine.getLineType().equalsIgnoreCase("Continuous")) {
                        specialValues.add(dub1);
                    }

                    if (dxfLine.getLineWeight() == 20) {
                        byWeight.add(dub1);
                    }
                    LOG.error("line length=" + dxfLine.getLength() + " Layer Name : " + line.getLayerName() + "Style"
                            + line.getDimensionStyleID() + " type:" + dxfLine.getType() + " Line Type "
                            + dxfLine.getLineType() + " " + dxfLine.getLineWeight());

                }

                if (e.getType().equals(DXFConstants.ENTITY_TYPE_MTEXT)) {
                    DXFMText text = (DXFMText) e;
                    text2 = text.getText();

                    Iterator styledParagraphIterator = text.getTextDocument().getStyledParagraphIterator();
                    while (styledParagraphIterator.hasNext()) {
                        StyledTextParagraph next = (StyledTextParagraph) styledParagraphIterator.next();
                        text2 = next.getText();
                    }

                    if (text2.contains(";")) {
                        String[] textSplit = text2.split(";");
                        int length = textSplit.length;

                        if (length >= 1) {
                            int index = length - 1;
                            text2 = textSplit[index];
                            text2 = text2.replaceAll("[^\\d.]", "");
                        } else
                            text2 = text2.replaceAll("[^\\d.]", "");
                    } else
                        text2 = text2.replaceAll("[^\\d.]", "");

                }

            }

            if (values.size() != 3) {
                planDetail.getErrors().put(layerName + "-" + DcrConstants.DIMENSION_LINES_STANDARD, "Dimension " + text2
                        + " marked in layer " + layerName + " is not as per DIGIT-DCR defined standard.");
            }

            if (values.size() > 2) {
                BigDecimal value1 = roundOffTwoDecimal(values.get(0));
                BigDecimal value2 = roundOffTwoDecimal(values.get(1));
                BigDecimal value3 = roundOffTwoDecimal(values.get(2));

                if (value1.compareTo(value2) == 0) {
                    values.remove(1);
                    values.remove(0);

                } else if (value2.compareTo(value3) == 0) {
                    values.remove(2);
                    values.remove(1);

                } else if (value1.compareTo(value3) == 0) {
                    values.remove(2);
                    values.remove(0);

                }
            }
            LOG.error("Before Delete ArrayList : " + values);
            Iterator itr = values.iterator();
            int count = 0;
            while (itr.hasNext()) {
                BigDecimal x = (BigDecimal) itr.next();
                if (count < 2 && roundOffTwoDecimal(x).compareTo(DcrConstants.DIMENSION_MARKING_LINE) == 0) {
                    count++;
                    itr.remove();
                }
            }

            LOG.error("Modified ArrayList : " + values);
            LOG.error("Dimension text : " + text2);
            BigDecimal textValue = BigDecimal.ZERO;
            if (StringUtils.isNotBlank(text2)) {
                textValue = BigDecimal.valueOf(Double.parseDouble(text2))
                        .setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS);

            }
            LOG.error("dimDecimal : " + textValue);
            // LOG.error("Dimension text : " + text2);
            if (values.size() == 1) {
                if (values.get(0).compareTo(textValue) == 0) {
                    LOG.debug("Proper Dimension found");
                    dimensionValues.add(values.get(0));
                } else if (values.get(0).compareTo(textValue.subtract(BigDecimal.valueOf(0.4d))) == 0) {
                    BigDecimal actual = values.get(0).add(BigDecimal.valueOf(0.2d));
                    values.remove(0);
                    dimensionValues.add(actual);
                    LOG.debug("Proper Dimension found");
                } else {
                    planDetail.getErrors().put(layerName + "-" + DcrConstants.DIMENSION_EDITED,
                            "Dimension " + text2 + " marked in layer " + layerName + " is edited.");
                }
            } else if (specialValues.size() == 1) {
                if (specialValues.get(0).compareTo(textValue) == 0) {
                    LOG.debug("Next proper Dimension found");
                    dimensionValues.add(specialValues.get(0));
                } else if (specialValues.get(0).compareTo(textValue.subtract(BigDecimal.valueOf(0.4d))) == 0) {
                    BigDecimal actual = specialValues.get(0).add(BigDecimal.valueOf(0.2d));
                    specialValues.remove(0);
                    dimensionValues.add(actual);
                    LOG.debug("Proper Dimension found");
                } else {
                    planDetail.getErrors().put(layerName + "-" + DcrConstants.DIMENSION_EDITED,
                            "Dimension " + text2 + " marked in layer " + layerName + " is edited.");
                }

            } else if (byWeight.size() == 1) {
                if (byWeight.get(0).compareTo(textValue) == 0) {
                    LOG.debug("Next proper Dimension found");
                    dimensionValues.add(byWeight.get(0));
                } else if (byWeight.get(0).compareTo(textValue.subtract(BigDecimal.valueOf(0.4d))) == 0) {
                    BigDecimal actual = byWeight.get(0).add(BigDecimal.valueOf(0.2d));
                    byWeight.remove(0);
                    dimensionValues.add(actual);
                    LOG.debug("Proper Dimension found");
                } else {
                    planDetail.getErrors().put(layerName + "-" + DcrConstants.DIMENSION_EDITED,
                            "Dimension " + text2 + " marked in layer " + layerName + " is edited.");
                }
            } else {
                if (!planDetail.getErrors().containsKey(layerName + "-" + DcrConstants.DIMENSION_LINES_STANDARD))
                    planDetail.getErrors().put(layerName + "-" + DcrConstants.DIMENSION_LINES_STANDARD, "Dimension "
                            + text2 + " marked in layer " + layerName + " is not as per DIGIT-DCR defined standard.");
            }
        }
    }

    /**
     * Extract the all dimension values and will map as key, value pairs Key: Color Code, Value: List of dimension values
     * @param dxfDocument
     * @param name
     * @return
     */
    public static Map<Integer, List<BigDecimal>> extractAndMapDimensionValuesByColorCode(PlanDetail planDetail, String name) {
        DXFDocument dxfDocument = planDetail.getDoc();
        Map<Integer, List<BigDecimal>> dimensionValues = new ConcurrentHashMap<>();
        if (dxfDocument == null)
            return Collections.emptyMap();
        if (name == null)
            return Collections.emptyMap();
        name = name.toUpperCase();

        DXFLayer dxfLayer = dxfDocument.getDXFLayer(name);
        if (dxfLayer != null && dxfLayer.getName().equalsIgnoreCase(name)) {
            List dxfLineEntities = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_DIMENSION);
            if (null != dxfLineEntities)
                for (Object dxfEntity : dxfLineEntities) {
                    DXFDimension line = (DXFDimension) dxfEntity;
                    List<BigDecimal> values = new ArrayList<>();
                    if (dimensionValues.containsKey(line.getColor())) {
                        extractDimensionValue(planDetail, values, line, dxfLayer.getName());
                        List<BigDecimal> existValues = dimensionValues.get(line.getColor());
                        existValues.addAll(values);
                        dimensionValues.put(line.getColor(), existValues);
                    } else {
                        extractDimensionValue(planDetail, values, line, dxfLayer.getName());
                        dimensionValues.put(line.getColor(), values);
                    }
                }
        }
        return dimensionValues;
    }

    private static BigDecimal convertToInch(String text2) {

        if ((text2.contains("'") || text2.contains("\""))) {
            String[] split = text2.split("'");
            BigDecimal inch = BigDecimal.ZERO;

            if (split.length > 1) {
                split[1] = split[1].trim();
                if (split[1].contains(" ") && split[1].contains("/")) {
                    String[] inchSplit = split[1].split(" ");
                    if (inchSplit.length > 1) {
                        String[] fractionSplit = inchSplit[1].split("/");
                        BigDecimal inchDecimalvalue = new BigDecimal(fractionSplit[0])
                                .divide(new BigDecimal(fractionSplit[1].replaceAll("[^\\d]", "")));
                        inch = new BigDecimal(inchSplit[0].replaceAll("[^\\d]", "")).add(inchDecimalvalue);
                    }
                } else {
                    if (split[1].contains("/")) {
                        String[] fractionSplit = split[1].split("/");
                        inch = new BigDecimal(fractionSplit[0])
                                .divide(new BigDecimal(fractionSplit[1].replaceAll("[^\\d]", "")));
                    } else
                        inch = new BigDecimal(split[1].replaceAll("[^\\d.]", ""));
                }
                BigDecimal feetToInch = new BigDecimal(split[0]).multiply(BigDecimal.valueOf(12));
                return feetToInch.add(inch);
            } else if (split[0].contains("\"")) {
                return new BigDecimal(split[0].replaceAll("[^\\d]", ""));
            }
        } else {
            if (text2.contains(" ") && text2.contains("/")) {
                String[] inchSplit = text2.split(" ");
                if (inchSplit.length > 1) {
                    String[] fractionSplit = inchSplit[1].split("/");
                    BigDecimal inchDecimalvalue = new BigDecimal(fractionSplit[0])
                            .divide(new BigDecimal(fractionSplit[1].replaceAll("[^\\d]", "")));
                    return new BigDecimal(inchSplit[0].replaceAll("[^\\d]", "")).add(inchDecimalvalue);
                }
            } else if (text2.contains("/")) {
                String[] fractionSplit = text2.split("/");
                return new BigDecimal(fractionSplit[0])
                        .divide(new BigDecimal(fractionSplit[1].replaceAll("[^\\d]", "")));
            } else
                return new BigDecimal(text2.replaceAll("[^\\d]", ""));
        }

        return new BigDecimal(text2.replaceAll("[^\\d]", "")).multiply(BigDecimal.valueOf(12));
    }

    public static List<BigDecimal> getListOfDimensionValueByLayer(PlanDetail planDetail, String name) {
        DXFDocument dxfDocument = planDetail.getDoc();
        if (dxfDocument == null)
            return null;
        if (name == null)
            return null;
        name = name.toUpperCase();
        List<BigDecimal> values = new ArrayList<>();

        DXFLayer dxfLayer = dxfDocument.getDXFLayer(name);
        if (dxfLayer != null && dxfLayer.getName().equalsIgnoreCase(name)) {
            List dxfLineEntities = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_DIMENSION);
            if (null != dxfLineEntities)
                for (Object dxfEntity : dxfLineEntities) {
                    DXFDimension line = (DXFDimension) dxfEntity;
                    extractDimensionValue(planDetail, values, line, dxfLayer.getName());
                }

        }
        /*
         * if (BigDecimal.ZERO.compareTo(value) == 0) pl.addError(name, "Dimension value is invalid for layer " + name);
         */
        return values;

    } 
    
    public static String getMtextByLayerName(DXFDocument doc, String name) {
        if (name == null)
            return null;
        String param = null;
        name = name.toUpperCase();
        String[] split = name.split(",");
        for (String layerName : split) {

            Boolean found = false;
            Iterator dxfLayerIterator = doc.getDXFLayerIterator();
            while (dxfLayerIterator.hasNext()) {
                DXFLayer next = (DXFLayer) dxfLayerIterator.next();
                DXFLayer planInfoLayer = doc.getDXFLayer(next.getName());
                // if(LOG.isDebugEnabled())
                // LOG.debug("----------"+planInfoLayer.getName()+"---------------------------------------------------");
                if (planInfoLayer != null) {
                    List texts = planInfoLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_MTEXT);
                    if (texts != null) {
                        Iterator iterator = texts.iterator();

                        while (iterator.hasNext()) {
                            DXFText text = (DXFText) iterator.next();
                            // if(LOG.isDebugEnabled()) LOG.debug("Mtext
                            // :"+text.getText());
                        }
                    }

                }
                if (layerName.equals(next.getName().toUpperCase())) {
                    found = true;
                    layerName = next.getName();
                }
            }
            if (!found) {
                LOG.error("No Layer Found with name" + layerName);
                return null;
            }

            DXFLayer planInfoLayer = doc.getDXFLayer(layerName);
            // if(LOG.isDebugEnabled()) LOG.debug(planInfoLayer.getName());
            if (planInfoLayer != null) {
                List texts = planInfoLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_MTEXT);

                // if(LOG.isDebugEnabled()) LOG.debug("Texts list is null ");
                DXFText text = null;
                if (texts != null) {
                    Iterator iterator = texts.iterator();

                    while (iterator.hasNext()) {
                        text = (DXFText) iterator.next();
                        // if(LOG.isDebugEnabled()) LOG.debug("Mtext
                        // :"+text.getText());
                        if (text != null && text.getText() != null) {
                            Iterator styledParagraphIterator = text.getTextDocument().getStyledParagraphIterator();
                            while (styledParagraphIterator.hasNext()) {
                                StyledTextParagraph styledTextParagraph = (StyledTextParagraph) styledParagraphIterator.next();
                                String formattedText = styledTextParagraph.getText();
                                if (StringUtils.isNotBlank(formattedText))
                                    param = formattedText.replace("VOLTS", "").trim();
                            }

                            /*
                             * if(new Float(param).isNaN()) { throw new RuntimeException("Texts in the layer" + layerName
                             * +"Does not follow standard "); }
                             */
                        }
                    }
                }
            }
        }
        return param;
    }

    public static String getMtextByLayerName(DXFDocument doc, String name, String textName) {
        if (name == null)
            return null;
        String param = null;
        name = name.toUpperCase();
        String[] split = name.split(",");
        for (String layerName : split) {

            Boolean found = false;
            Iterator dxfLayerIterator = doc.getDXFLayerIterator();
            while (dxfLayerIterator.hasNext()) {
                DXFLayer next = (DXFLayer) dxfLayerIterator.next();
                DXFLayer planInfoLayer = doc.getDXFLayer(next.getName());
                // if(LOG.isDebugEnabled())
                // LOG.debug("----------"+planInfoLayer.getName()+"---------------------------------------------------");
                if (planInfoLayer != null) {
                    List texts = planInfoLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_MTEXT);
                    if (texts != null) {
                        Iterator iterator = texts.iterator();

                        while (iterator.hasNext()) {
                            DXFText text = (DXFText) iterator.next();
                            // if(LOG.isDebugEnabled()) LOG.debug("Mtext
                            // :"+text.getText());
                        }
                    }

                }
                if (layerName.equals(next.getName().toUpperCase())) {
                    found = true;
                    layerName = next.getName();
                }
            }
            if (!found) {
                LOG.error("No Layer Found with name " + layerName);
                return null;
            }

            DXFLayer planInfoLayer = doc.getDXFLayer(layerName);
            // if(LOG.isDebugEnabled()) LOG.debug(planInfoLayer.getName());
            if (planInfoLayer != null) {
                List texts = planInfoLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_MTEXT);

                // if(LOG.isDebugEnabled()) LOG.debug("Texts list is null ");
                DXFText text = null;
                if (texts != null) {
                    Iterator iterator = texts.iterator();

                    while (iterator.hasNext()) {
                        text = (DXFText) iterator.next();
                        // if(LOG.isDebugEnabled()) LOG.debug("Mtext
                        // :"+text.getText());
                        if (text != null && text.getText() != null) {

                            if (textName != null && textName.equalsIgnoreCase(text.getText())) {
                                param = text.getText();
                                break;
                            } else
                                param = text.getText();

                            param = param.replace("VOLTS", "").trim();
                        }
                    }
                }
            }
        }
        return param;
    }

    public static OccupancyType getOccupancyAsPerFloorArea(OccupancyType occupancy, BigDecimal floorArea) {
        if (OccupancyType.OCCUPANCY_B1.equals(occupancy) || OccupancyType.OCCUPANCY_B2.equals(occupancy)
                || OccupancyType.OCCUPANCY_B3.equals(occupancy)) {
            if (floorArea != null && floorArea.compareTo(ONEHUNDREDFIFTY) <= 0)
                occupancy = OccupancyType.OCCUPANCY_A2;
            else
                occupancy = OccupancyType.OCCUPANCY_B1;
        } else if (OccupancyType.OCCUPANCY_C.equals(occupancy) || OccupancyType.OCCUPANCY_C1.equals(occupancy)
                || OccupancyType.OCCUPANCY_C2.equals(occupancy) || OccupancyType.OCCUPANCY_C3.equals(occupancy)) {
            if (floorArea != null && floorArea.compareTo(ONEHUNDREDFIFTY) <= 0)
                occupancy = OccupancyType.OCCUPANCY_F;
            else
                occupancy = OccupancyType.OCCUPANCY_C;
        } else if (floorArea != null && floorArea.compareTo(ONEHUNDREDFIFTY) <= 0
                && OccupancyType.OCCUPANCY_D.equals(occupancy))
            occupancy = OccupancyType.OCCUPANCY_F;
        else if (OccupancyType.OCCUPANCY_D1.equals(occupancy) || OccupancyType.OCCUPANCY_D2.equals(occupancy))
            occupancy = OccupancyType.OCCUPANCY_D;

        else if (OccupancyType.OCCUPANCY_E.equals(occupancy)) {
            if (floorArea != null && floorArea.compareTo(THREEHUNDRED) <= 0)
                occupancy = OccupancyType.OCCUPANCY_F;
            else
                occupancy = OccupancyType.OCCUPANCY_E;
        } else if (OccupancyType.OCCUPANCY_H.equals(occupancy)) {
            if (floorArea != null && floorArea.compareTo(THREEHUNDRED) <= 0)
                occupancy = OccupancyType.OCCUPANCY_F;
            else
                occupancy = OccupancyType.OCCUPANCY_H;
        } else if (OccupancyType.OCCUPANCY_A5.equals(occupancy))
            if (floorArea != null && floorArea.compareTo(FIFTY) <= 0)
                occupancy = OccupancyType.OCCUPANCY_A1;
            else
                occupancy = OccupancyType.OCCUPANCY_F;
        return occupancy;
    }

    public Map<String, String> getPlanInfoProperties(DXFDocument doc) {

        DXFLayer planInfoLayer = doc.getDXFLayer(layerNames.getLayerName("LAYER_NAME_PLAN_INFO"));
        List texts = planInfoLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_MTEXT);
        String param = "";
        DXFText text = null;
        Map<String, String> planInfoProperties = new HashMap<>();

        if (texts != null) {

            Iterator iterator = texts.iterator();
            String[] split;
            String s = "\\";
            while (iterator.hasNext()) {
                text = (DXFText) iterator.next();

                param = text.getText();
                param = param.replace(s, "#");
                if (param.contains("#P"))
                    split = param.split("#P");
                else {
                    split = new String[1];
                    split[0] = param;
                }

                for (String element : split) {

                    String[] data = element.split("=");
                    if (data.length == 2)

                        planInfoProperties.put(data[0], data[1]);
                    else {
                        // throw new RuntimeException("Plan info sheet data not
                        // following standard '=' for " +param);
                    }
                }
            }
        }
        return planInfoProperties;

    }

    public Map<String, String> getFormatedPlanInfoProperties(DXFDocument doc) {

        DXFLayer planInfoLayer = doc.getDXFLayer(layerNames.getLayerName("LAYER_NAME_PLAN_INFO"));
        List texts = planInfoLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_MTEXT);
        DXFText text = null;
        Map<String, String> planInfoProperties = new HashMap<>();

        if (texts != null && texts.size() > 0) {
            Iterator iterator = texts.iterator();
            while (iterator.hasNext()) {
                text = (DXFText) iterator.next();
                Iterator styledParagraphIterator = text.getTextDocument().getStyledParagraphIterator();
                while (styledParagraphIterator.hasNext()) {
                    StyledTextParagraph styledTextParagraph = (StyledTextParagraph) styledParagraphIterator.next();
                    String[] data = styledTextParagraph.getText().split("=");
                    LOG.info(styledTextParagraph.getText());
                    if (data.length == 2)
                        planInfoProperties.put(data[0].trim(), data[1].trim());
                }

            }
        }
        return planInfoProperties;
    }

    public static List<DXFCircle> getPolyCircleByLayer(DXFDocument dxfDocument, String name) {

        List<DXFCircle> dxfCircles = new ArrayList<>();
        if (name == null)
            return dxfCircles;
        if (dxfDocument.containsDXFLayer(name)) {
            DXFLayer dxfLayer = dxfDocument.getDXFLayer(name);

            if (dxfLayer.getName().equalsIgnoreCase(name) && dxfLayer.hasDXFEntities(DXFConstants.ENTITY_TYPE_CIRCLE)) {
                List dxfCircleEntities = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_CIRCLE);
                for (Object dxfEntity : dxfCircleEntities) {
                    DXFCircle dxflwPolyline = (DXFCircle) dxfEntity;
                    dxfCircles.add(dxflwPolyline);
                }

            }
        }
        return dxfCircles;

    }

    public static Polygon getPolygon(DXFLWPolyline plotBoundary) {
        List<Point> pointsOnPolygon = pointsOnPolygon(plotBoundary);
        return new Polygon(pointsOnPolygon);
    }

    public static BigDecimal getPolyLineArea(DXFPolyline dxfPolyline) {

        ArrayList x = new ArrayList();
        ArrayList y = new ArrayList();
        if (dxfPolyline == null)
            return BigDecimal.ZERO;
        Iterator vertexIterator = dxfPolyline.getVertexIterator();

        // Vertex and coordinates of Polyline
        while (vertexIterator.hasNext()) {

            DXFVertex dxfVertex = (DXFVertex) vertexIterator.next();
            Point point = dxfVertex.getPoint();

            // values needed to calculate area
            x.add(point.getX());
            y.add(point.getY());

        }

        return polygonArea(x, y, dxfPolyline.getVertexCount());
    }
    
    public static BigDecimal getPolyLineLength(DXFPolyline dxfPolyline) {
        if (dxfPolyline == null) {
            return BigDecimal.ZERO;
        }

        List<Double> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();

        Iterator<?> vertexIterator = dxfPolyline.getVertexIterator();
        while (vertexIterator.hasNext()) {
            DXFVertex dxfVertex = (DXFVertex) vertexIterator.next();
            Point point = dxfVertex.getPoint();
            x.add(point.getX());
            y.add(point.getY());
        }

        double totalLength = 0.0;
        int n = x.size();

        for (int i = 0; i < n - 1; i++) {
            double dx = x.get(i + 1) - x.get(i);
            double dy = y.get(i + 1) - y.get(i);
            totalLength += Math.sqrt(dx * dx + dy * dy);
        }

        // If polyline is closed, add distance between last and first point
        if (dxfPolyline.isClosed() && n > 1) {
            double dx = x.get(0) - x.get(n - 1);
            double dy = y.get(0) - y.get(n - 1);
            totalLength += Math.sqrt(dx * dx + dy * dy);
        }

        return BigDecimal.valueOf(totalLength).setScale(2, RoundingMode.HALF_UP);
    }


    public static List<DXFLWPolyline> getPolyLinesByLayer(DXFDocument dxfDocument, String name) {

        List<DXFLWPolyline> dxflwPolylines = new ArrayList<>();
        if (name == null)
            return dxflwPolylines;
        if (dxfDocument.containsDXFLayer(name)) {
            DXFLayer dxfLayer = dxfDocument.getDXFLayer(name);
            if (dxfLayer.getName().equalsIgnoreCase(name))
                if (dxfLayer.hasDXFEntities(DXFConstants.ENTITY_TYPE_LWPOLYLINE)) {
                    List dxfPolyLineEntities = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_LWPOLYLINE);
                    for (Object dxfEntity : dxfPolyLineEntities) {
                        DXFLWPolyline dxflwPolyline = (DXFLWPolyline) dxfEntity;
                        dxflwPolylines.add(dxflwPolyline);
                    }

                } else {
                    // TODO: add what if polylines not found

                }
        }
        return dxflwPolylines;

    }

    public static List<DXFLWPolyline> getPolyLinesByLayerPattern(DXFDocument doc, String regex) {

        List<DXFLWPolyline> result = new ArrayList<>();

        List<String> layers = getLayerNamesLike(doc, regex);

        for (String layer : layers) {

            List<DXFLWPolyline> polylines = getPolyLinesByLayer(doc, layer);

            if (polylines != null && !polylines.isEmpty()) {
                result.addAll(polylines);
            }
        }

        return result;
    }

    
    public static List<DXFLWPolyline> getPolyLinesByLayerAndColor(DXFDocument dxfDocument, String layerName,
            int colorCode, PlanDetail pl) {

        List<DXFLWPolyline> dxflwPolylines = new ArrayList<>();

        if (layerName == null)
            return dxflwPolylines;
        if (dxfDocument.containsDXFLayer(layerName)) {
            DXFLayer dxfLayer = dxfDocument.getDXFLayer(layerName);
            if (dxfLayer.getName().equalsIgnoreCase(layerName)) {

                if (dxfLayer.hasDXFEntities(DXFConstants.ENTITY_TYPE_LWPOLYLINE)) {
                    List dxfPolyLineEntities = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_LWPOLYLINE);
                    for (Object dxfEntity : dxfPolyLineEntities) {
                        DXFLWPolyline dxflwPolyline = (DXFLWPolyline) dxfEntity;
                        if (colorCode == dxflwPolyline.getColor())
                            dxflwPolylines.add(dxflwPolyline);
                    }
                }

            } else {
                // TODO: add what if polylines not found
            }
        }

        return dxflwPolylines;
    }

    public static DXFDimension getSingleDimensionByLayer(DXFDocument dxfDocument, String name) {

        if (dxfDocument == null)
            return null;
        if (name == null)
            return null;
        name = name.toUpperCase();

        List<DXFDimension> dimensions = new ArrayList<>();

        Iterator dxfLayerIterator = dxfDocument.getDXFLayerIterator();

        while (dxfLayerIterator.hasNext()) {

            DXFLayer dxfLayer = (DXFLayer) dxfLayerIterator.next();
            if (dxfLayer.getName().equalsIgnoreCase(name)) {

                List dxfLineEntities = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_DIMENSION);

                if (null != dxfLineEntities)
                    for (Object dxfEntity : dxfLineEntities) {

                        DXFDimension line = (DXFDimension) dxfEntity;
                        String dimensionBlock = line.getDimensionBlock();
                        DXFBlock dxfBlock = dxfDocument.getDXFBlock(dimensionBlock);
                        if (LOG.isDebugEnabled())
                            LOG.debug("BLOCK data" + dxfBlock.getDescription());
                        DXFDimensionStyle dxfDimensionStyle = dxfDocument
                                .getDXFDimensionStyle(line.getDimensionStyleID());
                        if (LOG.isDebugEnabled())
                            LOG.debug("---" + dxfDimensionStyle.getProperty(DXFDimensionStyle.PROPERTY_DIMEXO));
                        // if(LOG.isDebugEnabled())
                        // LOG.debug(line.getInclinationHelpLine()+"HELP
                        // LINE"+line.getDimensionText()
                        // +"--"+line.getLayerName()+"--"+line.getDimensionArea());

                        if (name.contains(line.getLayerName().toUpperCase()))
                            dimensions.add(line);

                    }
            }
        }
        if (dimensions.size() == 1)
            return dimensions.get(0);
        else
            return null;

    }

    public static BigDecimal getSingleDimensionValueByLayer(DXFDocument dxfDocument, String name, PlanDetail pl) {

        if (dxfDocument == null)
            return null;
        if (name == null)
            return null;
        name = name.toUpperCase();

        if (!pl.getStrictlyValidateDimension()) {
            BigDecimal value = BigDecimal.ZERO;

            DXFLayer dxfLayer = dxfDocument.getDXFLayer(name);
            if (dxfLayer.getName().equalsIgnoreCase(name)) {
                List dxfLineEntities = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_DIMENSION);

                if (null != dxfLineEntities)
                    for (Object dxfEntity : dxfLineEntities) {

                        DXFDimension line = (DXFDimension) dxfEntity;
                        String dimensionBlock = line.getDimensionBlock();
                        DXFBlock dxfBlock = dxfDocument.getDXFBlock(dimensionBlock);
                        Iterator dxfEntitiesIterator = dxfBlock.getDXFEntitiesIterator();
                        while (dxfEntitiesIterator.hasNext()) {
                            DXFEntity e = (DXFEntity) dxfEntitiesIterator.next();
                            if (e.getType().equals(DXFConstants.ENTITY_TYPE_MTEXT)) {
                                DXFMText text = (DXFMText) e;
                                String text2 = text.getText();

                                Iterator styledParagraphIterator = text.getTextDocument().getStyledParagraphIterator();

                                while (styledParagraphIterator.hasNext()) {
                                    StyledTextParagraph next = (StyledTextParagraph) styledParagraphIterator.next();
                                    text2 = next.getText();
                                }

                                if (text2.contains(";")) {
                                    String[] textSplit = text2.split(";");
                                    int length = textSplit.length;

                                    if (length >= 1) {
                                        int index = length - 1;
                                        text2 = textSplit[index];
                                        text2 = text2.replaceAll("[^\\d.]", "");
                                    } else
                                        text2 = text2.replaceAll("[^\\d.]", "");
                                } else
                                    text2 = text2.replaceAll("[^\\d.]", "");

                                if (!text2.isEmpty())
                                    value = BigDecimal.valueOf(Double.parseDouble(text2));

                            }
                        }
                    }

            }

            return value;
        } else {
            List<BigDecimal> values = new ArrayList<>();
            DXFLayer dxfLayer = dxfDocument.getDXFLayer(name);
            if (dxfLayer.getName().equalsIgnoreCase(name)) {
                List dxfLineEntities = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_DIMENSION);

                if (null != dxfLineEntities) {
                    for (Object dxfEntity : dxfLineEntities) {
                        DXFDimension line = (DXFDimension) dxfEntity;
                        extractDimensionValue(pl, values, line, dxfLayer.getName());
                    }
                    return values.isEmpty() ? BigDecimal.ZERO : values.get(0);
                }

            }

        }
        return BigDecimal.ZERO;

    }

    public static DXFLine getSingleLineByLayer(DXFDocument dxfDocument, String name) {

        if (name == null)
            return null;
        if (dxfDocument == null)
            return null;
        if (name == null)
            return null;

        name = name.toUpperCase();

        List<DXFLine> lines = new ArrayList<>();

        new ArrayList<>();

        DXFLayer dxfLayer = dxfDocument.getDXFLayer(name);
        // if layer with name not found kabeja will return default layer or
        // create new layer and gives
        if (dxfLayer.getName().equalsIgnoreCase(name)) {

            List dxfLineEntities = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_LINE);

            if (null != dxfLineEntities)
                for (Object dxfEntity : dxfLineEntities) {

                    DXFLine line = (DXFLine) dxfEntity;

                    if (name.contains(line.getLayerName().toUpperCase()))
                        lines.add(line);

                }

        }
        if (lines.size() == 1)
            return lines.get(0);
        else
            return null;

    }

    public static BigDecimal getSmallestSide(DXFLWPolyline polyLine) {
        List<Point> pointsOnPolygon = pointsOnPolygon(polyLine);
        Point oldPoint = null;
        double distance = 0d;
        double smallSide = 0d;
        for (Point p : pointsOnPolygon)
            if (oldPoint == null)
                oldPoint = p;
            else {
                distance = MathUtils.distance(oldPoint, p);
                oldPoint = p;
                if (distance < smallSide)
                    smallSide = distance;
            }
        return BigDecimal.valueOf(smallSide);
    }

    protected static int getTotalFloorCount(DXFDocument dxfDocument, Integer colorCode) {

        int i = 0;
        Iterator dxfLayerIterator = dxfDocument.getDXFLayerIterator();
        while (dxfLayerIterator.hasNext()) {

            DXFLayer dxfLayer = (DXFLayer) dxfLayerIterator.next();

            if (colorCode != null && dxfLayer.getColor() == colorCode
                    || dxfLayer.getName().startsWith(FLOOR_NAME_PREFIX))
                i++;

        }

        return i;
    }

    private static DXFLine isALine(Point old, Point in, List<DXFLine> lines) {
        LOG.debug("IS A Line api...............");
        LOG.debug("Points are" + old.getX() + " ," + old.getY() + " and " + in.getX() + " , " + in.getY());
        /*
         * if(old.getX()== -30.8147745851d && old.getY()==18662.1171192d && in.getX()==-27.0547745852d ) { LOG.info("Debug This");
         * }
         */
        for (DXFLine line : lines) {

            if (pointsEquals(line.getStartPoint(), line.getEndPoint()))
                continue;
            boolean start1 = pointsEquals(old, line.getStartPoint());
            boolean start2 = pointsEquals(in, line.getStartPoint());
            boolean end1 = pointsEquals(old, line.getEndPoint());
            boolean end2 = pointsEquals(in, line.getEndPoint());
            /*
             * LOG.debug("The Line is " + line.getStartPoint().getX() + " , " + line.getStartPoint().getY() + " and " +
             * line.getEndPoint().getX() + " , " + line.getEndPoint().getY());
             */

            if ((start1 || start2) && (end1 || end2)) {
                LOG.debug("is line ........................................... ");
                return line;
            }
        }
        // LOG.debug("for Point" + old.getX() + " ," + old.getY() + " and " +
        // in.getX() + " , " + in.getY());
        LOG.debug("is not on any  line ........................................... ");
        LOG.debug("IS A Line api........END.......");
        return null;
    }

    public static boolean pointsEquals(Point point1, Point point) {
        BigDecimal px = BigDecimal.valueOf(point.getX()).setScale(DECIMALDIGITS, BigDecimal.ROUND_DOWN);
        BigDecimal py = BigDecimal.valueOf(point.getY()).setScale(DECIMALDIGITS, BigDecimal.ROUND_DOWN);
        BigDecimal p1x = BigDecimal.valueOf(point1.getX()).setScale(DECIMALDIGITS, BigDecimal.ROUND_DOWN);
        BigDecimal p1y = BigDecimal.valueOf(point1.getY()).setScale(DECIMALDIGITS, BigDecimal.ROUND_DOWN);
        if (px.compareTo(p1x) == 0 && py.compareTo(p1y) == 0)
            return true;
        else
            return false;
    }

    public static boolean pointsEqualsWith2PercentError(Point point1, Point point) {
        BigDecimal px = BigDecimal.valueOf(point.getX()).setScale(COMPARE_WITH_2_PERCENT_ERROR_DIGITS,
                BigDecimal.ROUND_DOWN);
        BigDecimal py = BigDecimal.valueOf(point.getY()).setScale(COMPARE_WITH_2_PERCENT_ERROR_DIGITS,
                BigDecimal.ROUND_DOWN);
        BigDecimal p1x = BigDecimal.valueOf(point1.getX()).setScale(COMPARE_WITH_2_PERCENT_ERROR_DIGITS,
                BigDecimal.ROUND_DOWN);
        BigDecimal p1y = BigDecimal.valueOf(point1.getY()).setScale(COMPARE_WITH_2_PERCENT_ERROR_DIGITS,
                BigDecimal.ROUND_DOWN);
        double d = 0.01;

        if (px.compareTo(p1x) == 0 && py.compareTo(p1y) == 0) {
            LOG.debug(" Matched in pointsEqualsWith2PercentError for points using round down with exact match");
            PrintUtil.print(point1, "Point on Boundary Line ");
            PrintUtil.print(point, "Point to match ");
            return true;
        } else if (Math.abs(px.doubleValue() - p1x.doubleValue()) <= d
                && Math.abs(py.doubleValue() - p1y.doubleValue()) <= d) {
            LOG.debug(" Matched in pointsEqualsWith2PercentError for points using round down");
            PrintUtil.print(point1, "Point on Boundary Line ");
            PrintUtil.print(point, "Point to match ");

            return true;
        } else {
            px = BigDecimal.valueOf(point.getX()).setScale(COMPARE_WITH_2_PERCENT_ERROR_DIGITS,
                    BigDecimal.ROUND_HALF_UP);
            py = BigDecimal.valueOf(point.getY()).setScale(COMPARE_WITH_2_PERCENT_ERROR_DIGITS,
                    BigDecimal.ROUND_HALF_UP);
            p1x = BigDecimal.valueOf(point1.getX()).setScale(COMPARE_WITH_2_PERCENT_ERROR_DIGITS,
                    BigDecimal.ROUND_HALF_UP);
            p1y = BigDecimal.valueOf(point1.getY()).setScale(COMPARE_WITH_2_PERCENT_ERROR_DIGITS,
                    BigDecimal.ROUND_HALF_UP);
            d = 0.01;

            if (px.compareTo(p1x) == 0 && py.compareTo(p1y) == 0) {
                LOG.debug(" Matched in pointsEqualsWith2PercentError for points using round halfup with exact match");
                PrintUtil.print(point1, "Point on Boundary Line ");
                PrintUtil.print(point, "Point to match ");
                return true;
            } else if (Math.abs(px.doubleValue() - p1x.doubleValue()) <= d
                    && Math.abs(py.doubleValue() - p1y.doubleValue()) <= d) {
                LOG.debug(" Matched in pointsEqualsWith2PercentError for points using round halfup");
                PrintUtil.print(point1, "Point on Boundary Line ");
                PrintUtil.print(point, "Point to match ");
                return true;
            }

        }
        return false;
    }

    public static List<Point> pointsOnPolygon(DXFLWPolyline plotBoundary) {
        if (plotBoundary == null)
            return null;
        plotBoundary.getVertexCount();
        List<Point> points = new ArrayList<>();
        Iterator plotBIterator1 = plotBoundary.getVertexIterator();
        while (plotBIterator1.hasNext()) {

            DXFVertex dxfVertex = (DXFVertex) plotBIterator1.next();
            Point point1 = dxfVertex.getPoint();

            points.add(point1);

        }

        points.add(points.get(0));
        return points;
    }

    // Using ShoeLace Formula to calculate area of polygon
    private static BigDecimal polygonArea(ArrayList<Double> x, ArrayList<Double> y, int numPoints) {

        double area = 0; // Accumulates area in the loop
        int j = numPoints - 1; // The last vertex is the 'previous' one to the
        // first

        for (int i = 0; i < numPoints; i++) {
            area = area + (x.get(j) + x.get(i)) * (y.get(j) - y.get(i));
            j = i; // j is previous vertex to i
        }

        BigDecimal convertedArea = new BigDecimal(area / 2);

        return convertedArea.setScale(4, RoundingMode.HALF_UP).abs();

    }

    public static void setDimension(Measurement measurement, DXFLWPolyline polyLine) {
        Iterator vertexIterator2 = polyLine.getVertexIterator();
        if (LOG.isDebugEnabled())
            while (vertexIterator2.hasNext()) {
                DXFVertex dxfVertex = (DXFVertex) vertexIterator2.next();
                Point p = dxfVertex.getPoint();
                LOG.debug(p.getX() + " " + p.getY());
            }

        // if (polyLine.getVertexCount() == 4 || polyLine.getVertexCount() == 5) {
        if (polyLine.getVertexCount() > 1) {
            Iterator vertexIterator = polyLine.getVertexIterator();
            Point next = null, first = null;
            List<Double> distances = new ArrayList<>();
            while (vertexIterator.hasNext()) {
                DXFVertex dxfVertex = (DXFVertex) vertexIterator.next();
                Point p = dxfVertex.getPoint();
                if (next == null) {
                    next = p;
                    first = p;
                    continue;
                }
                distances.add(MathUtils.distance(next, p));
                next = p;
            }
            if (!pointsEquals(next, first))
                distances.add(MathUtils.distance(next, first));

            if (!distances.isEmpty()) {
                measurement.setWidth(BigDecimal.valueOf(Collections.min(distances)));
                measurement.setHeight(BigDecimal.valueOf(Collections.max(distances)));
                measurement.setMinimumSide(BigDecimal.valueOf(Collections.min(distances)));
            } else {
                measurement.setWidth(BigDecimal.ZERO);
                measurement.setHeight(BigDecimal.ZERO);
                measurement.setMinimumSide(BigDecimal.ZERO);
            }
        } else
            measurement.setInvalidReason("It is not rectangle, found " + polyLine.getVertexCount() + " points");
    }

    public static void setOccupancyType(DXFLWPolyline pline, Occupancy occupancy) {
        if (pline.getColor() == DxfFileConstants.OCCUPANCY_A1_COLOR_CODE)
            occupancy.setType(OccupancyType.OCCUPANCY_A1);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_A4_APARTMENT_COLOR_CODE)
            occupancy.setType(OccupancyType.OCCUPANCY_A4);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_A1_PROFESSIONALOFFICE_COLOR_CODE)
            occupancy.setType(OccupancyType.OCCUPANCY_A5);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_A2_COLOR_CODE)
            occupancy.setType(OccupancyType.OCCUPANCY_A2);
//        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_A2_BOARDING_COLOR_CODE)
//            occupancy.setType(OccupancyType.OCCUPANCY_A3);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_B1_COLOR_CODE)
            occupancy.setType(OccupancyType.OCCUPANCY_B1);
//        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_B2_COLOR_CODE)
//            occupancy.setType(OccupancyType.OCCUPANCY_B2);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_B3_COLOR_CODE)
            occupancy.setType(OccupancyType.OCCUPANCY_B3);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_C1_COLOR_CODE)
            occupancy.setType(OccupancyType.OCCUPANCY_C1);
//        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_C2_COLOR_CODE)
//            occupancy.setType(OccupancyType.OCCUPANCY_C2);
//        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_C3_COLOR_CODE)
//            occupancy.setType(OccupancyType.OCCUPANCY_C3);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_D_COLOR_CODE)
            occupancy.setType(OccupancyType.OCCUPANCY_D);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_D1_COLOR_CODE)
            occupancy.setType(OccupancyType.OCCUPANCY_D1);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_D2_COLOR_CODE)
            occupancy.setType(OccupancyType.OCCUPANCY_D2);
//        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_E_COLOR_CODE)
//            occupancy.setType(OccupancyType.OCCUPANCY_E);
//        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F_COLOR_CODE)
//            occupancy.setType(OccupancyType.OCCUPANCY_F);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F1_COLOR_CODE)
        	occupancy.setType(OccupancyType.OCCUPANCY_F1);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F2_COLOR_CODE)
        	occupancy.setType(OccupancyType.OCCUPANCY_F2);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F3_COLOR_CODE)
        	occupancy.setType(OccupancyType.OCCUPANCY_F3);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F4_COLOR_CODE)
        	occupancy.setType(OccupancyType.OCCUPANCY_F4);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F5_COLOR_CODE)
        	occupancy.setType(OccupancyType.OCCUPANCY_F5);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F6_COLOR_CODE)
        	occupancy.setType(OccupancyType.OCCUPANCY_F6);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F7_COLOR_CODE)
        	occupancy.setType(OccupancyType.OCCUPANCY_F7);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F8_COLOR_CODE)
        	occupancy.setType(OccupancyType.OCCUPANCY_F8);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F9_COLOR_CODE)
        	occupancy.setType(OccupancyType.OCCUPANCY_F9);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F10_COLOR_CODE)
        	occupancy.setType(OccupancyType.OCCUPANCY_F10);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F11_COLOR_CODE)
        	occupancy.setType(OccupancyType.OCCUPANCY_F11);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F12_COLOR_CODE)
        	occupancy.setType(OccupancyType.OCCUPANCY_F12);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F13_COLOR_CODE)
        	occupancy.setType(OccupancyType.OCCUPANCY_F13);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F14_COLOR_CODE)
        	occupancy.setType(OccupancyType.OCCUPANCY_F14);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F15_COLOR_CODE)
        	occupancy.setType(OccupancyType.OCCUPANCY_F15);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F16_COLOR_CODE)
        	occupancy.setType(OccupancyType.OCCUPANCY_F16);
//        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F2_COLOR_CODE)
//            occupancy.setType(OccupancyType.OCCUPANCY_F2);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F3_HOTEL_COLOR_CODE)
            occupancy.setType(OccupancyType.OCCUPANCY_F3);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_G1_COLOR_CODE)
            occupancy.setType(OccupancyType.OCCUPANCY_G1);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_G2_COLOR_CODE)
            occupancy.setType(OccupancyType.OCCUPANCY_G2);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_H_COLOR_CODE)
            occupancy.setType(OccupancyType.OCCUPANCY_H);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_I1_COLOR_CODE)
            occupancy.setType(OccupancyType.OCCUPANCY_I1);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_I2_COLOR_CODE)
            occupancy.setType(OccupancyType.OCCUPANCY_I2);
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_I2_KIOSK_COLOR_CODE)
            occupancy.setType(OccupancyType.OCCUPANCY_F4);
    }

    public static OccupancyType findOccupancyType(DXFLWPolyline pline) {
        if (pline.getColor() == DxfFileConstants.OCCUPANCY_A1_COLOR_CODE)
            return OccupancyType.OCCUPANCY_A1;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_A4_APARTMENT_COLOR_CODE)
            return OccupancyType.OCCUPANCY_A4;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_A1_PROFESSIONALOFFICE_COLOR_CODE)
            return OccupancyType.OCCUPANCY_A5;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_A6_INDEPENDENT_FLOOR_COLOR_CODE)
            return OccupancyType.OCCUPANCY_A6;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_A2_COLOR_CODE)
            return OccupancyType.OCCUPANCY_A2;
//        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_A2_BOARDING_COLOR_CODE)
//            return OccupancyType.OCCUPANCY_A3;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_B1_COLOR_CODE)
            return OccupancyType.OCCUPANCY_B1;
//        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_B2_COLOR_CODE)
//            return OccupancyType.OCCUPANCY_B2;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_B3_COLOR_CODE)
            return OccupancyType.OCCUPANCY_B3;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_C1_COLOR_CODE)
            return OccupancyType.OCCUPANCY_C1;
//        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_C2_COLOR_CODE)
//            return OccupancyType.OCCUPANCY_C2;
//        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_C3_COLOR_CODE)
//            return OccupancyType.OCCUPANCY_C3;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_D_COLOR_CODE)
            return OccupancyType.OCCUPANCY_D;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_D1_COLOR_CODE)
            return OccupancyType.OCCUPANCY_D1;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_D2_COLOR_CODE)
            return OccupancyType.OCCUPANCY_D2;
//        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_E_COLOR_CODE)
//            return OccupancyType.OCCUPANCY_E;
//        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F_COLOR_CODE)
//            return OccupancyType.OCCUPANCY_F;
//        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F1_COLOR_CODE)
//            return OccupancyType.OCCUPANCY_F1;
//        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F2_COLOR_CODE)
//            return OccupancyType.OCCUPANCY_F2;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F_COLOR_CODE)
            return OccupancyType.OCCUPANCY_F;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F1_COLOR_CODE)
            return OccupancyType.OCCUPANCY_F1;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F2_COLOR_CODE)
            return OccupancyType.OCCUPANCY_F2;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F3_COLOR_CODE)
            return OccupancyType.OCCUPANCY_F3;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F4_COLOR_CODE)
            return OccupancyType.OCCUPANCY_F4;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F5_COLOR_CODE)
            return OccupancyType.OCCUPANCY_F5;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F6_COLOR_CODE)
            return OccupancyType.OCCUPANCY_F6;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F7_COLOR_CODE)
            return OccupancyType.OCCUPANCY_F7;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F8_COLOR_CODE)
            return OccupancyType.OCCUPANCY_F8;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F9_COLOR_CODE)
            return OccupancyType.OCCUPANCY_F9;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F10_COLOR_CODE)
            return OccupancyType.OCCUPANCY_F10;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F11_COLOR_CODE)
            return OccupancyType.OCCUPANCY_F11;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F12_COLOR_CODE)
            return OccupancyType.OCCUPANCY_F12;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F13_COLOR_CODE)
            return OccupancyType.OCCUPANCY_F13;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F14_COLOR_CODE)
            return OccupancyType.OCCUPANCY_F14;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F15_COLOR_CODE)
            return OccupancyType.OCCUPANCY_F15;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F16_COLOR_CODE)
            return OccupancyType.OCCUPANCY_F16;
        
//        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F2_COLOR_CODE)
//            return OccupancyType.OCCUPANCY_F2;
//        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_F2_COLOR_CODE)
//            return OccupancyType.OCCUPANCY_F2;
//        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_G1_COLOR_CODE)
//            return OccupancyType.OCCUPANCY_G1;
//        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_G2_COLOR_CODE)
//            return OccupancyType.OCCUPANCY_G2;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_G_COLOR_CODE)
            return OccupancyType.OCCUPANCY_G;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_G1_COLOR_CODE)
            return OccupancyType.OCCUPANCY_G1;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_G2_COLOR_CODE)
            return OccupancyType.OCCUPANCY_G2;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_G3_COLOR_CODE)
            return OccupancyType.OCCUPANCY_G3;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_G4_COLOR_CODE)
            return OccupancyType.OCCUPANCY_G4;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_G5_COLOR_CODE)
            return OccupancyType.OCCUPANCY_G5;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_G6_COLOR_CODE)
            return OccupancyType.OCCUPANCY_G6;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_G7_COLOR_CODE)
            return OccupancyType.OCCUPANCY_G7;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_G8_COLOR_CODE)
            return OccupancyType.OCCUPANCY_G8;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_G9_COLOR_CODE)
            return OccupancyType.OCCUPANCY_G9;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_G10_COLOR_CODE)
            return OccupancyType.OCCUPANCY_G10;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_G11_COLOR_CODE)
            return OccupancyType.OCCUPANCY_G11;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_G12_COLOR_CODE)
            return OccupancyType.OCCUPANCY_G12;
        
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_H_COLOR_CODE)
            return OccupancyType.OCCUPANCY_H;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_I1_COLOR_CODE)
            return OccupancyType.OCCUPANCY_I1;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_I2_COLOR_CODE)
            return OccupancyType.OCCUPANCY_I2;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_I2_KIOSK_COLOR_CODE)
            return OccupancyType.OCCUPANCY_F4;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_J_COLOR_CODE)
            return OccupancyType.OCCUPANCY_J;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_J1_COLOR_CODE)
            return OccupancyType.OCCUPANCY_J1;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_J2_COLOR_CODE)
            return OccupancyType.OCCUPANCY_J2;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_J3_COLOR_CODE)
            return OccupancyType.OCCUPANCY_J3;
        
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_L1_COLOR_CODE)
            return OccupancyType.OCCUPANCY_L1;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_L2_COLOR_CODE)
            return OccupancyType.OCCUPANCY_L2;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_L3_COLOR_CODE)
            return OccupancyType.OCCUPANCY_L3;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_L4_COLOR_CODE)
            return OccupancyType.OCCUPANCY_L4;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_L5_COLOR_CODE)
            return OccupancyType.OCCUPANCY_L5;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_L6_COLOR_CODE)
            return OccupancyType.OCCUPANCY_L6;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_L7_COLOR_CODE)
            return OccupancyType.OCCUPANCY_L7;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_L8_COLOR_CODE)
            return OccupancyType.OCCUPANCY_L8;
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_L9_COLOR_CODE)
            return OccupancyType.OCCUPANCY_L9;
        
        else if (pline.getColor() == DxfFileConstants.OCCUPANCY_R_COLOR_CODE)
            return OccupancyType.OCCUPANCY_R;
        
        
        else
            return null;
    }

    public static OccupancyTypeHelper findOccupancyType(DXFLWPolyline pline, PlanDetail pl) {
        OccupancyTypeHelper oth = new OccupancyTypeHelper();
        if (!pl.getUsagesMaster().isEmpty() && pl.getUsagesMaster().containsKey(pline.getColor())) {
            Usage usage = pl.getUsagesMaster().get(pline.getColor());
            OccupancyHelperDetail usageTypeDtl = new OccupancyHelperDetail();
            usageTypeDtl.setColor(pline.getColor());
            usageTypeDtl.setCode(usage.getCode());
            usageTypeDtl.setName(usage.getName());
            oth.setUsage(usageTypeDtl);
            SubOccupancy subOcc = usage.getSubOccupancy();
            OccupancyHelperDetail occSubTypeDtl = new OccupancyHelperDetail();
            occSubTypeDtl.setCode(subOcc.getCode());
            occSubTypeDtl.setName(subOcc.getName());
            oth.setSubtype(occSubTypeDtl);
            OccupancyHelperDetail occTypeDtl = new OccupancyHelperDetail();
            org.egov.common.entity.bpa.Occupancy occ = subOcc.getOccupancy();
            occTypeDtl.setCode(occ.getCode());
            occTypeDtl.setName(occ.getName());
            oth.setType(occTypeDtl);
        }
        if (!pl.getSubOccupanciesMaster().isEmpty() && pl.getSubOccupanciesMaster().containsKey(pline.getColor())) {
            SubOccupancy subOcc = pl.getSubOccupanciesMaster().get(pline.getColor());
            OccupancyHelperDetail occSubTypeDtl = new OccupancyHelperDetail();
            occSubTypeDtl.setColor(pline.getColor());
            occSubTypeDtl.setCode(subOcc.getCode());
            occSubTypeDtl.setName(subOcc.getName());
            oth.setSubtype(occSubTypeDtl);
            OccupancyHelperDetail occTypeDtl = new OccupancyHelperDetail();
            org.egov.common.entity.bpa.Occupancy occ = subOcc.getOccupancy();
            occTypeDtl.setCode(occ.getCode());
            occTypeDtl.setName(occ.getName());
            oth.setType(occTypeDtl);
        }
        if (!pl.getOccupanciesMaster().isEmpty() && pl.getOccupanciesMaster().containsKey(pline.getColor())) {
            org.egov.common.entity.bpa.Occupancy occ = pl.getOccupanciesMaster().get(pline.getColor());
            OccupancyHelperDetail occTypeDtl = new OccupancyHelperDetail();
            occTypeDtl.setColor(pline.getColor());
            occTypeDtl.setCode(occ.getCode());
            occTypeDtl.setName(occ.getName());
            oth.setType(occTypeDtl);
        }

        return oth;
    }

    public List<DXFLine> getLinesByColor(DXFDocument dxfDocument, Integer color) {

        List<DXFLine> lines = new ArrayList<>();

        Iterator dxfLayerIterator = dxfDocument.getDXFLayerIterator();

        while (dxfLayerIterator.hasNext()) {

            DXFLayer dxfLayer = (DXFLayer) dxfLayerIterator.next();

            List dxfPolyLineEntities = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_LINE);

            if (null != dxfPolyLineEntities)
                for (Object dxfEntity : dxfPolyLineEntities) {

                    DXFLine line = (DXFLine) dxfEntity;

                    if (color == line.getColor())
                        lines.add(line);

                }
        }

        return lines;
    }

    public List<DXFLWPolyline> getPolyLinesByColor(DXFDocument dxfDocument, Integer colorCode) {

        List<DXFLWPolyline> dxflwPolylines = new ArrayList<>();

        Iterator dxfLayerIterator = dxfDocument.getDXFLayerIterator();

        while (dxfLayerIterator.hasNext()) {

            DXFLayer dxfLayer = (DXFLayer) dxfLayerIterator.next();

            List dxfPolyLineEntities = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_LWPOLYLINE);

            if (null != dxfPolyLineEntities)
                for (Object dxfEntity : dxfPolyLineEntities) {

                    DXFLWPolyline dxflwPolyline = (DXFLWPolyline) dxfEntity;

                    if (colorCode == dxflwPolyline.getColor())
                        dxflwPolylines.add(dxflwPolyline);
                }
        }

        return dxflwPolylines;
    }

    public List<DXFLWPolyline> getPolyLinesByColors(DXFDocument dxfDocument, List<Integer> colorCodes) {

        List<DXFLWPolyline> dxflwPolylines = new ArrayList<>();

        Iterator dxfLayerIterator = dxfDocument.getDXFLayerIterator();

        while (dxfLayerIterator.hasNext()) {

            DXFLayer dxfLayer = (DXFLayer) dxfLayerIterator.next();

            List dxfPolyLineEntities = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_LWPOLYLINE);

            if (null != dxfPolyLineEntities)
                for (Object dxfEntity : dxfPolyLineEntities) {

                    DXFLWPolyline dxflwPolyline = (DXFLWPolyline) dxfEntity;

                    for (int colorCode : colorCodes)
                        if (colorCode == dxflwPolyline.getColor())
                            dxflwPolylines.add(dxflwPolyline);
                }
        }

        return dxflwPolylines;
    }

//    public static Map<String, Object> getTypicalFloorValues(Block block, Floor floor,
//            Boolean isTypicalRepititiveFloor) {
//        Map<String, Object> mapOfTypicalFloorValues = new HashMap<>();
//        List<Integer> typicalFlrs = new ArrayList<>();
//        String typicalFloors = null;
//        Integer maxTypicalFloors;
//        Integer minTypicalFloors;
//        if (block.getTypicalFloor() != null)
//            for (TypicalFloor typicalFloor : block.getTypicalFloor()) {
//                if (typicalFloor.getRepetitiveFloorNos().contains(floor.getNumber()))
//                    isTypicalRepititiveFloor = true;
//                if (typicalFloor.getModelFloorNo() == floor.getNumber()) {
//                    typicalFlrs.add(floor.getNumber());
//                    typicalFlrs.addAll(typicalFloor.getRepetitiveFloorNos());
//                    if (!typicalFlrs.isEmpty()) {
//                        maxTypicalFloors = typicalFlrs.get(0);
//                        minTypicalFloors = typicalFlrs.get(0);
//                        for (Integer typical : typicalFlrs) {
//                            if (typical > maxTypicalFloors)
//                                maxTypicalFloors = typical;
//                            if (typical < minTypicalFloors)
//                                minTypicalFloors = typical;
//                        }
//                        typicalFloors = "Typical Floor " + minTypicalFloors + " to " + maxTypicalFloors;
//
//                    }
//                }
//            }
//        mapOfTypicalFloorValues.put("isTypicalRepititiveFloor", isTypicalRepititiveFloor);
//        mapOfTypicalFloorValues.put("typicalFloors", typicalFloors);
//        return mapOfTypicalFloorValues;
//    }
    
    public static Map<String, Object> getTypicalFloorValues(Block block, Floor floor,
            Boolean isTypicalRepititiveFloor) { // Reverting the signature change
        
    Map<String, Object> mapOfTypicalFloorValues = new HashMap<>();
    List<Integer> typicalFlrs = new ArrayList<>();
    String typicalFloors = null;
    Integer maxTypicalFloors = null;
    Integer minTypicalFloors = null;

    if (block.getTypicalFloor() != null) {
        for (TypicalFloor typicalFloor : block.getTypicalFloor()) {
            
            // Check if the current floor is a repetitive floor and update the input boolean
            if (typicalFloor.getRepetitiveFloorNos().contains(floor.getNumber())) {
                isTypicalRepititiveFloor = true;
            }

            // This is the core fix: Check if the floor is EITHER the model or a repetitive floor.
            if (typicalFloor.getModelFloorNo().equals(floor.getNumber())
                    || typicalFloor.getRepetitiveFloorNos().contains(floor.getNumber())) {
                
                // Add the model floor number and all repetitive floor numbers to a list.
                typicalFlrs.add(typicalFloor.getModelFloorNo());
                typicalFlrs.addAll(typicalFloor.getRepetitiveFloorNos());

                // Calculate the min and max floor numbers for the typical set.
                if (!typicalFlrs.isEmpty()) {
                    // Use streams for a cleaner way to find min/max
                    minTypicalFloors = typicalFlrs.stream().min(Integer::compareTo).orElse(null);
                    maxTypicalFloors = typicalFlrs.stream().max(Integer::compareTo).orElse(null);
                    
                    if (minTypicalFloors != null && maxTypicalFloors != null) {
                        typicalFloors = "Typical Floor " + minTypicalFloors + " to " + maxTypicalFloors;
                    }
                }
                
                // Break the loop once the typical floor set is found and processed
                break; 
            }
        }
    }
    
    // The map is populated with the potentially updated boolean and the calculated string
    mapOfTypicalFloorValues.put("isTypicalRepititiveFloor", isTypicalRepititiveFloor);
    mapOfTypicalFloorValues.put("typicalFloors", typicalFloors);
    return mapOfTypicalFloorValues;
}

    public static boolean checkExemptionConditionForBuildingParts(Block blk) {
        if (blk.getBuilding() != null && blk.getBuilding().getFloorsAboveGround() != null)
            if (blk.getResidentialBuilding() && blk.getBuilding().getFloorsAboveGround().intValue() <= 3)
                return true;
        return false;
    }

    public static boolean checkExemptionConditionForSmallPlotAtBlkLevel(Plot plot, Block blk) {
        if (plot != null && blk.getBuilding() != null && blk.getBuilding().getFloorsAboveGround() != null)
            if (blk.getResidentialOrCommercialBuilding() && plot.getSmallPlot()
                    && blk.getBuilding().getFloorsAboveGround().intValue() <= 3)
                return true;
        return false;
    }

    public static boolean isSmallPlot(PlanDetail pl) {
        if (pl != null && !pl.getBlocks().isEmpty() && pl.getPlot() != null && pl.getVirtualBuilding() != null)
            if (checkAnyBlockHasFloorsGreaterThanThree(pl.getBlocks()) == false
                    && pl.getVirtualBuilding().getResidentialOrCommercialBuilding().equals(Boolean.TRUE)
                    && pl.getPlot().getSmallPlot().equals(Boolean.TRUE))
                return true;
        return false;
    }

    public static boolean checkAnyBlockHasFloorsGreaterThanThree(List<Block> blockList) {
        boolean isBlockFloorsGreaterThanThree = false;
        if (!blockList.isEmpty())
            for (Block blk : blockList)
                if (blk.getBuilding() != null && blk.getBuilding().getFloorsAboveGround() != null
                        && blk.getBuilding().getFloorsAboveGround().compareTo(BigDecimal.valueOf(3)) > 0) {
                    isBlockFloorsGreaterThanThree = true;
                    break;
                }
        return isBlockFloorsGreaterThanThree;
    }

    public static BigDecimal roundOffTwoDecimal(BigDecimal number) {
        return number != null
                ? number.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS)
                : BigDecimal.ZERO;
    }

    public void setLayerNames(LayerNames layerNames) {
        this.layerNames = layerNames;
    }

    public static double getSlope(Point startPoint, Point endPoint) {
        return (endPoint.getY() - startPoint.getY()) / (endPoint.getX() - startPoint.getX());
    }

    public static Point getMidPoint(DXFVertex line1, DXFVertex line2) {

        return new Point((line1.getX() + line2.getX()) / 2, (line1.getY() + line2.getY()) / 2, 0d);
    }

    public static String getTexForDimension(String text) {
        String[] split = text.split(" X ");
        if (split.length > 4) {
            return text;
        } else if (split.length == 4) {
            Set<String> set = new HashSet();
            set.add(split[0]);
            set.add(split[1]);
            set.add(split[2]);
            set.add(split[3]);
            String[] a = new String[2];
            if (set.size() == 2) {
                set.toArray(a);
                return a[0] + " X " + a[1];
            } else {
                return text;
            }
        } else
            return text;

    }

    public static Point findCentroid(DXFEntity e) {

        DXFPolyline pline = (DXFPolyline) e;
        Iterator vertexIterator = pline.getVertexIterator();
        double x = 0, y = 0;
        double centroidX = 0, centroidY = 0;
        DXFVertex p;
        DXFVertex first = null, point1 = null;
        StringBuilder text = new StringBuilder(50);
        while (vertexIterator.hasNext()) {
            if (point1 == null) {
                point1 = (DXFVertex) vertexIterator.next();
                first = point1;
            }
            p = (DXFVertex) vertexIterator.next();

            x += p.getX();
            y += p.getY();

            text.append(p.getLength());
            if (vertexIterator.hasNext())
                text.append(" X ");
        }

        centroidX = x / pline.getVertexCount();
        centroidY = y / pline.getVertexCount();
        return new Point(centroidX, centroidY, 0);

    }

    public static String getDimensionText(DXFEntity e) {
        DXFPolyline pline = (DXFPolyline) e;
        Iterator vertexIterator = pline.getVertexIterator();
        DXFVertex p;
        StringBuilder text = new StringBuilder(50);
        while (vertexIterator.hasNext()) {
            p = (DXFVertex) vertexIterator.next();
            text.append(p.getLength());
            if (vertexIterator.hasNext())
                text.append(" X ");
        }

        return text.toString();
    }

    public static String getPolylinePrintableText(DXFPolyline pline, DXFLayer dxfLayer, EdcrPdfDetail detail, PlanDetail pl) {

        OccupancyTypeHelper occupancyType = null;
        String name = null;
        
        if (pline.getColor() != 0) {
            occupancyType = findOccupancyType((DXFLWPolyline) pline, pl);
        }
        if (occupancyType != null) {
            String occupancyName = "";
                if (occupancyType.getSubtype() != null)
                    occupancyName = occupancyType.getSubtype().getName();
                else {
                    if (occupancyType.getType() != null)
                        occupancyName = occupancyType.getType().getName();
                }
            LOG.info("returning Occupancy " + occupancyName);
            return occupancyName;

        } else {
            name = dxfLayer.getName();
            name = name.replace("BLK_", "");
            name = name.replace("FLR_", "");
            name.replace("NO_", "");
            name.replaceAll("[^\\d.]", "");
            LOG.info("returning layer name " + name);
            return name;
        }
    }
    
    public static boolean isPointStrictlyInsidePolygon(DXFLWPolyline poly, Point p) {

        List<Point> pts = new ArrayList<>();

        Iterator it = poly.getVertexIterator();
        while (it.hasNext()) {
            DXFVertex v = (DXFVertex) it.next();
            pts.add(v.getPoint());
        }

        int n = pts.size();
        boolean inside = false;

        for (int i = 0, j = n - 1; i < n; j = i++) {
            double xi = pts.get(i).getX();
            double yi = pts.get(i).getY();
            double xj = pts.get(j).getX();
            double yj = pts.get(j).getY();

            boolean intersect =
                    ((yi > p.getY()) != (yj > p.getY())) &&
                            (p.getX() < (xj - xi) * (p.getY() - yi) / (yj - yi) + xi);

            if (intersect)
                inside = !inside;
        }

        // strictly inside → not on boundary
        if (inside && !isPointOnPolygonBoundary(poly, p)) {
            return true;
        }

        return false;
    }
    
    public static boolean isPointOnPolygonBoundary(DXFLWPolyline poly, Point p) {

        Iterator it = poly.getVertexIterator();
        Point prev = null;

        if (it.hasNext()) {
            prev = ((DXFVertex) it.next()).getPoint();
        }

        while (it.hasNext()) {
            Point curr = ((DXFVertex) it.next()).getPoint();
            if (isPointOnLine(prev, curr, p)) return true;
            prev = curr;
        }

        // close last-to-first segment
        Point first = ((DXFVertex) poly.getVertex(0)).getPoint();
        if (isPointOnLine(prev, first, p)) return true;

        return false;
    }

    public static boolean isPointOnLine(Point a, Point b, Point p) {
        double cross = (p.getY() - a.getY()) * (b.getX() - a.getX())
                - (p.getX() - a.getX()) * (b.getY() - a.getY());

        if (Math.abs(cross) > 1e-6) return false;

        double dot = (p.getX() - a.getX()) * (b.getX() - a.getX()) +
                (p.getY() - a.getY()) * (b.getY() - a.getY());

        if (dot < 0) return false;

        double lenSq = (b.getX() - a.getX()) * (b.getX() - a.getX())
                + (b.getY() - a.getY()) * (b.getY() - a.getY());

        return dot <= lenSq;
    }

    public static boolean doLineSegmentsIntersect(Point p1, Point p2, Point q1, Point q2) {
        // Check orientations
        int o1 = orientation(p1, p2, q1);
        int o2 = orientation(p1, p2, q2);
        int o3 = orientation(q1, q2, p1);
        int o4 = orientation(q1, q2, p2);

        // General case
        if (o1 != o2 && o3 != o4)
            return true;

        // Special Cases
        if (o1 == 0 && onSegment(p1, q1, p2)) return true;
        if (o2 == 0 && onSegment(p1, q2, p2)) return true;
        if (o3 == 0 && onSegment(q1, p1, q2)) return true;
        if (o4 == 0 && onSegment(q1, p2, q2)) return true;

        return false;
    }

    private static int orientation(Point a, Point b, Point c) {
        double val = (b.getY() - a.getY()) * (c.getX() - b.getX()) -
                     (b.getX() - a.getX()) * (c.getY() - b.getY());

        if (Math.abs(val) < 1e-10) return 0;  // Collinear
        return (val > 0) ? 1 : 2;            // Clockwise or Counterclockwise
    }

    private static boolean onSegment(Point a, Point b, Point c) {
        return b.getX() <= Math.max(a.getY(), c.getX()) && b.getX() >= Math.min(a.getX(), c.getX()) &&
               b.getY() <= Math.max(a.getY(), c.getY()) && b.getY() >= Math.min(a.getY(), c.getY());
    }

    
}