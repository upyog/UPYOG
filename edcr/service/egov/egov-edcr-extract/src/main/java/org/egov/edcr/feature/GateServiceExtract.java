package org.egov.edcr.feature;

import static org.egov.edcr.constants.DxfFileConstants.HEIGHT_OR_LENGTH_COLOR_CODE;
import static org.egov.edcr.constants.DxfFileConstants.WIDTH_COLOR_CODE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.egov.common.entity.edcr.Gate;
import org.egov.edcr.entity.blackbox.MeasurementDetail;
import org.egov.edcr.entity.blackbox.PlanDetail;
import org.egov.edcr.service.LayerNames;
import org.egov.edcr.utility.Util;
import org.kabeja.dxf.DXFDimension;
import org.kabeja.dxf.DXFLWPolyline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GateServiceExtract extends FeatureExtract {
    private static final Logger LOG = LogManager.getLogger(GateServiceExtract.class);
    private static final String GATE = "GATE";
    @Autowired
    private LayerNames layerNames;

    @Override
    public PlanDetail validate(PlanDetail planDetail) {
        return planDetail;
    }

    @Override
    public PlanDetail extract(PlanDetail planDetail) {
        String gateLayerName = String.format(layerNames.getLayerName("LAYER_NAME_GATE"));
        String mainGateLayerName = String.format(layerNames.getLayerName("LAYER_NAME_MAIN_GATE"));
        String wicketGateLayerName = String.format(layerNames.getLayerName("LAYER_NAME_WICKET_GATE"));

        Gate gate = new Gate();
        gate.setGates(new ArrayList<>());

        List<DXFLWPolyline> polyLinesByLayer = Util.getPolyLinesByLayer(planDetail.getDoc(), gateLayerName);

        List<DXFLWPolyline> mainGatePolyLinesByLayer = Util.getPolyLinesByLayer(planDetail.getDoc(), mainGateLayerName);
        
        if(!mainGatePolyLinesByLayer.isEmpty()) {
        	Util.validateLayerColor(mainGateLayerName, 
        			Util.getColorByPolyLine(mainGatePolyLinesByLayer), planDetail);
        }
        
        List<DXFLWPolyline> wicketGatePolyLinesByLayer = Util.getPolyLinesByLayer(planDetail.getDoc(), wicketGateLayerName);
        
        try {
        	if (mainGatePolyLinesByLayer == null || mainGatePolyLinesByLayer.isEmpty()) {

                LOG.error("Main gate polyline is not defined.");

				/*
				 * planDetail.addError( "GATE_POLYLINE_MISSING",
				 * "Main gate polyline is mandatory but not defined in the plan." );
				 */
                return planDetail;
            }

            if (mainGatePolyLinesByLayer != null && !mainGatePolyLinesByLayer.isEmpty()) {

                LOG.info("Main gate polylines found. Extracting width/height from polylines...");

                Map<String, Integer> subFeaturesColor =
                        planDetail.getSubFeatureColorCodesMaster() != null
                                ? planDetail.getSubFeatureColorCodesMaster().get(GATE)
                                : null;

                if (subFeaturesColor == null || subFeaturesColor.isEmpty()) {
                    LOG.warn("Sub-feature color codes not found for GATE. Skipping extraction...");
                    return planDetail;
                }

                Set<String> keySet = new HashSet<>(subFeaturesColor.keySet());

                // --- Extract gates from polyline colors ---
                for (DXFLWPolyline pline : mainGatePolyLinesByLayer) {
                    if (pline == null) {
                        LOG.warn("Null polyline encountered. Skipping...");
                        continue;
                    }

                    for (String s : keySet) {
                        Integer colorCode = subFeaturesColor.get(s);

                        if (colorCode != null && colorCode == pline.getColor()) {
                            MeasurementDetail measurement = new MeasurementDetail(pline, true);
                            measurement.setName(s);

                            if (gate.getGates() == null) gate.setGates(new ArrayList<>());
                            gate.getGates().add(measurement);

                            LOG.info("Gate measurement added for key: {}", s);
                        }
                    }
                }

                // --- Extract dimensions from layer ---
                List<DXFDimension> dims = Util.getDimensionsByLayer(planDetail.getDoc(), gateLayerName);
                List<BigDecimal> dimList = new ArrayList<>();

                if (dims != null) {
                    for (DXFDimension dim : dims) {
                        if (dim == null) {
                            LOG.warn("Null dimension found, skipping...");
                            continue;
                        }

                        if (subFeaturesColor.containsValue(dim.getColor())) {
                            Util.extractDimensionValue(planDetail, dimList, dim, gateLayerName);

                            if (!dimList.isEmpty()) {
                                if (gate.getHeights() == null) {
                                    gate.setHeights(new ArrayList<>(dimList));
                                } else {
                                    gate.getHeights().addAll(dimList);
                                }

                                LOG.info("Dimension value extracted for gate: {}", dimList);
                            }
                        }
                    }
                }

            } else {

                LOG.info("Main gate polyline NOT found. Extracting using dimension color codes...");

                // --- MAIN GATE WIDTH/HEIGHT via Color Code ---
                List<BigDecimal> mainGateWidths =
                        Util.getListOfDimensionByColourCode(planDetail, mainGateLayerName, WIDTH_COLOR_CODE);
                List<BigDecimal> mainGateHeights =
                        Util.getListOfDimensionByColourCode(planDetail, mainGateLayerName, HEIGHT_OR_LENGTH_COLOR_CODE);

                if ((mainGateWidths != null && !mainGateWidths.isEmpty()) ||
                    (mainGateHeights != null && !mainGateHeights.isEmpty())) {

                    BigDecimal mainGateWidth =
                            (mainGateWidths == null || mainGateWidths.isEmpty())
                                    ? BigDecimal.ZERO
                                    : Collections.min(mainGateWidths);

                    BigDecimal mainGateHeight =
                            (mainGateHeights == null || mainGateHeights.isEmpty())
                                    ? BigDecimal.ZERO
                                    : Collections.max(mainGateHeights);

                    MeasurementDetail mainGate = new MeasurementDetail();
                    mainGate.setWidth(mainGateWidth);
                    mainGate.setHeight(mainGateHeight);
                    mainGate.setLength(mainGateHeight);
                    mainGate.setName(mainGateLayerName);

                    BigDecimal area = BigDecimal.ZERO;
                    if (mainGatePolyLinesByLayer != null && !mainGatePolyLinesByLayer.isEmpty()) {
                        area = Util.getPolyLineArea(mainGatePolyLinesByLayer.get(0));
                    }
                    mainGate.setArea(area);

                    if (gate.getGates() == null) gate.setGates(new ArrayList<>());
                    gate.getGates().add(mainGate);

                    LOG.info("Main gate (dimension-based) extracted successfully: {} x {}", mainGateWidth, mainGateHeight);
                }

                // --- WICKET GATE WIDTH/HEIGHT via Color Code ---
                List<BigDecimal> wicketGateWidths =
                        Util.getListOfDimensionByColourCode(planDetail, wicketGateLayerName, WIDTH_COLOR_CODE);
                List<BigDecimal> wicketGateHeights =
                        Util.getListOfDimensionByColourCode(planDetail, wicketGateLayerName, HEIGHT_OR_LENGTH_COLOR_CODE);

                if ((wicketGateWidths != null && !wicketGateWidths.isEmpty()) ||
                    (wicketGateHeights != null && !wicketGateHeights.isEmpty())) {

                    BigDecimal wicketGateWidth =
                            (wicketGateWidths == null || wicketGateWidths.isEmpty())
                                    ? BigDecimal.ZERO
                                    : Collections.min(wicketGateWidths);

                    BigDecimal wicketGateHeight =
                            (wicketGateHeights == null || wicketGateHeights.isEmpty())
                                    ? BigDecimal.ZERO
                                    : Collections.max(wicketGateHeights);

                    MeasurementDetail wicketGate = new MeasurementDetail();
                    wicketGate.setWidth(wicketGateWidth);
                    wicketGate.setHeight(wicketGateHeight);
                    wicketGate.setLength(wicketGateHeight);
                    wicketGate.setName(wicketGateLayerName);

                    BigDecimal area = wicketGatePolyLinesByLayer == null ||
                                      wicketGatePolyLinesByLayer.isEmpty()
                            ? BigDecimal.ZERO
                            : Util.getPolyLineArea(wicketGatePolyLinesByLayer.get(0));

                    wicketGate.setArea(area);

                    if (gate.getGates() == null) gate.setGates(new ArrayList<>());
                    gate.getGates().add(wicketGate);

                    LOG.info("Wicket gate extracted successfully via dimensions.");
                }
            }

        } catch (Exception e) {
            LOG.error("Error occurred while extracting gate details: {}", e.getMessage(), e);

            planDetail.addError("GATE_EXTRACTION_ERROR",
                    "Error occurred while extracting main/wicket gate details. Please verify the drawing.");
        }

        
//        if (mainGatePolyLinesByLayer.isEmpty() && wicketGatePolyLinesByLayer.isEmpty()) {
//            Map<String, Integer> subFeaturesColor = planDetail.getSubFeatureColorCodesMaster().get(GATE);
//
//            if (subFeaturesColor == null || subFeaturesColor.isEmpty())
//                return planDetail;
//            // ArrayList<Integer> values = (ArrayList<Integer>) subFeaturesColor.values();
//            Set<String> keySet = new HashSet<>();
//            keySet.addAll(subFeaturesColor.keySet());
//            for (DXFLWPolyline pline : polyLinesByLayer) {
//
//                for (String s : keySet) {
//
//                    if (subFeaturesColor.get(s) == pline.getColor()) {
//                        MeasurementDetail measurement = new MeasurementDetail(pline, true);
//                        measurement.setName(s);
//                        gate.getGates().add(measurement);
//                    }
//                }
//
//            }
//
//            List<DXFDimension> dims = Util.getDimensionsByLayer(planDetail.getDoc(), gateLayerName);
//            List<BigDecimal> dimList = new ArrayList<>();
//            if (dims != null) {
//                for (DXFDimension dim : dims) {
//
//                    if (subFeaturesColor.containsValue(dim.getColor())) {
//                        Util.extractDimensionValue(planDetail, dimList, dim, gateLayerName);
//                        if (gate.getHeights() != null) {
//                            gate.setHeights(dimList);
//                        } else {
//                            gate.getHeights().addAll(dimList);
//                        }
//                    }
//
//                }
//            }
//        } else {
//            /*
//             * Extract the width and height of the main gate and wicket gate, when defined as a dimension using color code
//             */
//            List<BigDecimal> mainGateWidths = Util.getListOfDimensionByColourCode(planDetail, mainGateLayerName,
//                    WIDTH_COLOR_CODE);
//            List<BigDecimal> mainGateHeights = Util.getListOfDimensionByColourCode(planDetail, mainGateLayerName,
//                    HEIGHT_OR_LENGTH_COLOR_CODE);
//            if (!mainGateWidths.isEmpty() || !mainGateHeights.isEmpty()) {
//                BigDecimal mainGateWidth = mainGateWidths.isEmpty() ? BigDecimal.ZERO : Collections.min(mainGateWidths);
//                BigDecimal mainGateHeight = mainGateHeights.isEmpty() ? BigDecimal.ZERO : Collections.max(mainGateHeights);
//                MeasurementDetail mainGate = new MeasurementDetail();
//                mainGate.setWidth(mainGateWidth);
//                mainGate.setHeight(mainGateHeight);
//                mainGate.setLength(mainGateHeight);
//                mainGate.setName(mainGateLayerName);
//                mainGate.setArea(Util.getPolyLineArea(mainGatePolyLinesByLayer.get(0)));
//                gate.getGates().add(mainGate);
//            }
//
//            List<BigDecimal> wicketGateWidths = Util.getListOfDimensionByColourCode(planDetail, wicketGateLayerName,
//                    WIDTH_COLOR_CODE);
//            List<BigDecimal> wicketGateHeights = Util.getListOfDimensionByColourCode(planDetail, wicketGateLayerName,
//                    HEIGHT_OR_LENGTH_COLOR_CODE);
//            if (!wicketGateWidths.isEmpty() || !wicketGateHeights.isEmpty()) {
//                BigDecimal wicketGateWidth = mainGateWidths.isEmpty() ? BigDecimal.ZERO : Collections.min(wicketGateWidths);
//                BigDecimal wicketGateHeight = mainGateHeights.isEmpty() ? BigDecimal.ZERO : Collections.max(wicketGateHeights);
//                MeasurementDetail wicketGate = new MeasurementDetail();
//                wicketGate.setWidth(wicketGateWidth);
//                wicketGate.setHeight(wicketGateHeight);
//                wicketGate.setLength(wicketGateHeight);
//                wicketGate.setName(wicketGateLayerName);
//                wicketGate.setArea(wicketGatePolyLinesByLayer.isEmpty() ? BigDecimal.ZERO
//                        : Util.getPolyLineArea(wicketGatePolyLinesByLayer.get(0)));
//                gate.getGates().add(wicketGate);
//            }
//
//        }

        planDetail.setGate(gate);
        return planDetail;
    }

}
