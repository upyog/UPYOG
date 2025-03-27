
package org.egov.edcr.feature;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.egov.common.entity.edcr.Door;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.PlanInformation;
import org.egov.common.entity.edcr.Road;
import org.egov.edcr.entity.blackbox.MeasurementDetail;
import org.egov.edcr.entity.blackbox.PlanDetail;
import org.egov.edcr.service.LayerNames;
import org.egov.edcr.utility.Util;
import org.kabeja.dxf.DXFDimension;
import org.kabeja.dxf.DXFLWPolyline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoadReserveExtract extends FeatureExtract {
    private static final Logger LOG = LogManager.getLogger(RoadReserveExtract.class);
    @Autowired
    private LayerNames layerNames;
   
    @Override
    public PlanDetail validate(PlanDetail planDetail) {
        return planDetail;
    }

    @Override
    public PlanDetail extract(PlanDetail planDetail) {
        if (LOG.isDebugEnabled())
            LOG.debug("Starting of Road Reserve Extract......");
        String layerName = layerNames.getLayerName("LAYER_NAME_ROAD_RESERVE_FRONT");
        List<Road> roadReserves = new ArrayList<>();
        List<DXFDimension> roadReserveFront = Util.getDimensionsByLayer(planDetail.getDoc(), layerName);

        if (roadReserveFront != null && !roadReserveFront.isEmpty()) {
            for (Object dxfEntity : roadReserveFront) {
                DXFDimension dimension = (DXFDimension) dxfEntity;
                List<BigDecimal> values = new ArrayList<>();
                Util.extractDimensionValue(planDetail, values, dimension, layerName);

                if (!values.isEmpty()) {
                    for (BigDecimal width : values) {
                        Road road = new Road();  
                        road.setWidth(width);
                        road.setName(layerName);
                        roadReserves.add(road);  
                    }
                } else {
                    Road road = new Road();
                    road.setWidth(BigDecimal.ZERO);
                    roadReserves.add(road);  
                }
            }
            for (Road road : roadReserves) {
                if (layerName.equals(road.getName())) {
                    // Process front road reserve
                	planDetail.setRoadReserveFront(road.getWidth());          
                  }
                
               
                System.out.println("uu" +  planDetail.getRoadReserveFront());
            }
        }
        String layerNameRear = layerNames.getLayerName("LAYER_NAME_ROAD_RESERVE_REAR");
        List<DXFDimension> roadReserveRear = Util.getDimensionsByLayer(planDetail.getDoc(), layerNameRear);

        if (roadReserveRear != null && !roadReserveRear.isEmpty()) {
            for (Object dxfEntity : roadReserveRear) {
                DXFDimension dimension = (DXFDimension) dxfEntity;
                List<BigDecimal> values = new ArrayList<>();
                Util.extractDimensionValue(planDetail, values, dimension, layerNameRear);

                if (!values.isEmpty()) {
                    for (BigDecimal width : values) {
                        Road road = new Road();
                        road.setWidth(width);  // Set width
                        road.setName(layerNameRear);  // Set name based on layer
                        roadReserves.add(road);  // Add to the list
                    }
                } else {
                    Road road = new Road();
                    road.setWidth(BigDecimal.ZERO);  // Set default width
                    road.setName(layerNameRear);  // Set name based on layer
                    roadReserves.add(road);
                }
                
            }
            for (Road road : roadReserves) {
                if (layerNameRear.equals(road.getName())) {
                    // Process front road reserve
                	planDetail.setRoadReserveRear(road.getWidth());          
                  }
                
                
            }
            System.out.println("uu" +  planDetail.getRoadReserveRear());
        }

        // Process Side Road Reserves
//        String layerNameSide = layerNames.getLayerName("LAYER_NAME_ROAD_RESERVE_SIDE1");
//        List<DXFDimension> roadReserveSide = Util.getDimensionsByLayer(planDetail.getDoc(), layerNameSide);
//
//        if (roadReserveSide != null && !roadReserveSide.isEmpty()) {
//            for (Object dxfEntity : roadReserveSide) {
//                DXFDimension dimension = (DXFDimension) dxfEntity;
//                List<BigDecimal> values = new ArrayList<>();
//                Util.extractDimensionValue(planDetail, values, dimension, layerNameSide);
//
//                if (!values.isEmpty()) {
//                    for (BigDecimal width : values) {
//                        Road road = new Road();
//                        road.setWidth(width);  // Set width
//                        road.setName(layerNameSide);  // Set name based on layer
//                        roadReserves.add(road);  // Add to the list
//                    }
//                } else {
//                    Road road = new Road();
//                    road.setWidth(BigDecimal.ZERO);  // Set default width
//                    road.setName(layerNameSide);  // Set name based on layer
//                    roadReserves.add(road);
//                }
//            }
//            for (Road road : roadReserves) {
//                if (layerNameSide.equals(road.getName())) {
//                    // Process front road reserve
//                	plInfo.setRoadReserveSide(road.getWidth());          
//                  }
//                
//                
//            }
    //    }
        
        
			
		
//        List<DXFLWPolyline> roadReserveFront1 = Util.getPolyLinesByLayer(planDetail.getDoc(), layerName);
//        if (roadReserveFront != null && !roadReserveFront.isEmpty()) {
//            List<Road> roadReservFronts = new ArrayList<>();
//            for (DXFDimension polygon : roadReserveFront)
//                roadReservFronts.add(buildRoadReserveDetails(planDetail, layerName, polygon));
//            roadReserves.addAll(roadReservFronts);
//        }
//        String layerName1 = layerNames.getLayerName("LAYER_NAME_ROAD_RESERVE_REAR");
//        List<DXFDimension> roadReserveRear = Util.getDimensionsByLayer(planDetail.getDoc(), layerName1);
//        List<DXFLWPolyline> roadReserveRear1 = Util.getPolyLinesByLayer(planDetail.getDoc(),
//                layerName1);
//        if (roadReserveRear != null && !roadReserveRear.isEmpty()) {
//            List<Road> rearRoadReserves = new ArrayList<>();
//            for (DXFDimension polygon : roadReserveRear)
//                rearRoadReserves.add(buildRoadReserveDetails(planDetail, layerName1, polygon));
//            roadReserves.addAll(rearRoadReserves);
//        }
//        String layerName2 = layerNames.getLayerName("LAYER_NAME_ROAD_RESERVE_SIDE1");
//        
//        List<DXFDimension> roadReserveSide1 = Util.getDimensionsByLayer(planDetail.getDoc(), layerName2);
//        List<DXFLWPolyline> roadReserveSide = Util.getPolyLinesByLayer(planDetail.getDoc(),
//                layerName2);
//        if (roadReserveSide1 != null && !roadReserveSide1.isEmpty()) {
//            List<Road> sideRoadReserve = new ArrayList<>();
//            for (DXFDimension polygon : roadReserveSide1)
//                sideRoadReserve.add(buildRoadReserveDetails(planDetail, layerName2, polygon));
//            roadReserves.addAll(sideRoadReserve);
//        }
//        String layerName3 = layerNames.getLayerName("LAYER_NAME_ROAD_RESERVE_SIDE2");
//        List<DXFDimension> roadReserveSide2 = Util.getDimensionsByLayer(planDetail.getDoc(), layerName3);
//        List<DXFLWPolyline> roadReserveSide21 = Util.getPolyLinesByLayer(planDetail.getDoc(),
//                layerName3);
//        if (roadReserveSide2 != null && !roadReserveSide2.isEmpty()) {
//            List<Road> side2RoadReserves = new ArrayList<>();
//            for (DXFDimension polygon : roadReserveSide2)
//                side2RoadReserves.add(buildRoadReserveDetails(planDetail, layerName3, polygon));
//            roadReserves.addAll(side2RoadReserves);
//        }
        if (LOG.isDebugEnabled())
            LOG.debug("End of Road Reserve Extract......");
        planDetail.setRoadReserves(roadReserves);
        return planDetail;
    }

    private Road buildRoadReserveDetails(PlanDetail planDetail, String layerName, DXFDimension polygon) {
        Measurement measurement = new MeasurementDetail();
        Road road = new Road();
        road.setName(layerName);
        road.setArea(measurement.getArea());
        road.setColorCode(measurement.getColorCode());
        road.setHeight(measurement.getHeight());
        road.setWidth(measurement.getWidth());
        road.setLength(measurement.getLength());
        road.setMinimumSide(measurement.getMinimumSide());
        road.setInvalidReason(measurement.getInvalidReason());
        road.setPresentInDxf(true);
        road.setShortestDistanceToRoad(Util.getListOfDimensionValueByLayer(planDetail, layerName));
        return road;
    }

}
