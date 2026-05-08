package org.egov.edcr.feature;

import static org.egov.edcr.constants.DxfFileConstants.OCCUPANCY_A2_PARKING_WITHATTACHBATH_COLOR_CODE;
import static org.egov.edcr.constants.DxfFileConstants.OCCUPANCY_A2_PARKING_WITHDINE_COLOR_CODE;
import static org.egov.edcr.constants.DxfFileConstants.OCCUPANCY_A2_PARKING_WOATTACHBATH_COLOR_CODE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.FloorUnit;
import org.egov.common.entity.edcr.Hall;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.OccupancyType;
import org.egov.common.entity.edcr.TypicalFloor;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.entity.blackbox.MeasurementDetail;
import org.egov.edcr.entity.blackbox.PlanDetail;
import org.egov.edcr.entity.blackbox.PlotDetail;
import org.egov.edcr.service.LayerNames;
import org.egov.edcr.utility.DcrConstants;
import org.egov.edcr.utility.PrintUtil;
import org.egov.edcr.utility.Util;
import org.egov.edcr.utility.math.Polygon;
import org.egov.edcr.utility.math.Ray;
import org.kabeja.dxf.DXFDocument;
import org.kabeja.dxf.DXFLWPolyline;
import org.kabeja.dxf.DXFLine;
import org.kabeja.dxf.DXFVertex;
import org.kabeja.dxf.helpers.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParkingExtract extends FeatureExtract {
    private static final Logger LOGGER = LogManager.getLogger(ParkingExtract.class);
    private static final String DA_PARKING = "DA parking";
    final Ray rayCasting = new Ray(new Point(-1.123456789, -1.987654321, 0d));

    @Autowired
    private LayerNames layerNames;

    @Override
    public PlanDetail extract(PlanDetail pl) {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting of Parking Extract......");
        for (Block block : pl.getBlocks()) {
            for (Floor floor : block.getBuilding().getFloors()) {
                String layerRegEx = layerNames.getLayerName("LAYER_NAME_BLOCK_NAME_PREFIX") + block.getNumber() + "_"
                        + layerNames.getLayerName("LAYER_NAME_FLOOR_NAME_PREFIX") + floor.getNumber() + "_"
                        + layerNames.getLayerName("LAYER_NAME_UNITFA");
                List<DXFLWPolyline> occupancyUnits = Util.getPolyLinesByLayer(pl.getDoc(), layerRegEx);
                
                if(!occupancyUnits.isEmpty()) {
                	Util.validateLayerColor(layerRegEx, 
                			Util.getColorByPolyLine(occupancyUnits), pl);
                }
                
                extractByLayer(pl, pl.getDoc(), block, floor, occupancyUnits);
                
                String coveredParkLayer = layerNames.getLayerName("LAYER_NAME_BLOCK_NAME_PREFIX") + block.getNumber()
                        + "_" + layerNames.getLayerName("LAYER_NAME_FLOOR_NAME_PREFIX") + floor.getNumber() + "_"
                        + layerNames.getLayerName("LAYER_NAME_COVERED_PARKING");
                List<String> covereredParkLayerNames = Util.getLayerNamesLike(pl.getDoc(), coveredParkLayer);
                for (String s : covereredParkLayerNames)
                    Util.getPolyLinesByLayer(pl.getDoc(), s).forEach(coveredPark -> {
                        if (floor.getNumber() < 0)
                            floor.getParking().getBasementCars().add(new MeasurementDetail(coveredPark, true));
                        else
                            floor.getParking().getCoverCars().add(new MeasurementDetail(coveredPark, true));
                    });
                String stiltParkLayer = layerNames.getLayerName("LAYER_NAME_BLOCK_NAME_PREFIX") + block.getNumber()
                        + "_" + layerNames.getLayerName("LAYER_NAME_FLOOR_NAME_PREFIX") + floor.getNumber() + "_"
                        + layerNames.getLayerName("LAYER_NAME_STILT");
                List<BigDecimal> heightFromFloorToBottomOfBeam = Util.getListOfDimensionValueByLayer(pl, stiltParkLayer);
                floor.setHeightFromFloorToBottomOfBeam(heightFromFloorToBottomOfBeam);
                List<String> stiltParkLayerNames = Util.getLayerNamesLike(pl.getDoc(), stiltParkLayer);
//                for (String s : stiltParkLayerNames)
//                    Util.getPolyLinesByLayer(pl.getDoc(), s).forEach(
//                            stiltPark -> floor.getParking().getStilts().add(new MeasurementDetail(stiltPark, true)));
                
                if(!stiltParkLayerNames.isEmpty()) {
                	List<DXFLWPolyline> stiltParkLyr = Util.getPolyLinesByLayer(pl.getDoc(), 
                			stiltParkLayerNames.get(0));        
                    if(!stiltParkLyr.isEmpty()) {
                    	Util.validateLayerColor(stiltParkLayerNames.get(0), 
                    			Util.getColorByPolyLine(stiltParkLyr), pl);
                    }
                }
                
                for (String s : stiltParkLayerNames) {
                    Util.getPolyLinesByLayer(pl.getDoc(), s).forEach(stiltPark -> {
                        MeasurementDetail detail = new MeasurementDetail(stiltPark, true);

                        // Log key information
                        LOGGER.info("Adding Stilt Measurement: Layer = {}, Area = {}",s, detail.getArea());

                        floor.getParking().getStilts().add(detail);
                        pl.getParkingDetails().getStilts().add(detail);
                    });
                }

            }

            String hallLayer = layerNames.getLayerName("LAYER_NAME_BLOCK_NAME_PREFIX") + block.getNumber() + "_"
                    + layerNames.getLayerName("LAYER_NAME_UNITFA_HALL") + "_" + "\\d";
            List<String> layerNames1 = Util.getLayerNamesLike(pl.getDoc(), hallLayer);
            for (String s : layerNames1) {

                List<DXFLWPolyline> hallPolylines = Util.getPolyLinesByLayer(pl.getDoc(), s);
                for (DXFLWPolyline pline : hallPolylines) {
                    MeasurementDetail m = new MeasurementDetail(pline, true);
                    Hall hall = new Hall();
                    hall.setNumber(pline.getLayerName().substring(pline.getLayerName().length() - 1));
                    hall.setArea(m.getArea());
                    hall.setLength(m.getLength());
                    hall.setWidth(m.getWidth());
                    hall.setHeight(m.getHeight());
                    hall.setMinimumSide(m.getMinimumSide());
                    block.getHallAreas().add(hall);
                }
            }

            String dinningLayer = layerNames.getLayerName("LAYER_NAME_BLOCK_NAME_PREFIX") + block.getNumber() + "_"
                    + layerNames.getLayerName("LAYER_NAME_UNITFA_DINING") + "_" + "\\d";
            List<String> layerNames2 = Util.getLayerNamesLike(pl.getDoc(), dinningLayer);
            for (String s : layerNames2)
                Util.getPolyLinesByLayer(pl.getDoc(), s).forEach(
                        dinningPolyline -> block.getDiningSpaces().add(new MeasurementDetail(dinningPolyline, true)));
        }

        Util.getPolyLinesByLayer(pl.getDoc(), layerNames.getLayerName("LAYER_NAME_LOADING_UNLOADING"))
                .forEach(loadUnloadPolyline -> pl.getParkingDetails().getLoadUnload()
                        .add(new MeasurementDetail(loadUnloadPolyline, true)));
        
        List<DXFLWPolyline> loadUnload = Util.getPolyLinesByLayer(pl.getDoc(), 
        		layerNames.getLayerName("LAYER_NAME_LOADING_UNLOADING"));        
        if(!loadUnload.isEmpty()) {
        	Util.validateLayerColor(layerNames.getLayerName("LAYER_NAME_LOADING_UNLOADING"), 
        			Util.getColorByPolyLine(loadUnload), pl);
        }

        Util.getPolyLinesByLayer(pl.getDoc(), layerNames.getLayerName("LAYER_NAME_MECH_PARKING"))
                .forEach(mechParkPolyline -> pl.getParkingDetails().getMechParking()
                        .add(new MeasurementDetail(mechParkPolyline, true)));

        List<DXFLWPolyline> mechPark = Util.getPolyLinesByLayer(pl.getDoc(), 
        		layerNames.getLayerName("LAYER_NAME_MECH_PARKING"));        
        if(!mechPark.isEmpty()) {
        	Util.validateLayerColor(layerNames.getLayerName("LAYER_NAME_MECH_PARKING"), 
        			Util.getColorByPolyLine(mechPark), pl);
        }
        
        
        Util.getPolyLinesByLayer(pl.getDoc(), layerNames.getLayerName("LAYER_NAME_TWO_WHEELER_PARKING"))
                .forEach(twoWheelerPolyline -> pl.getParkingDetails().getTwoWheelers()
                        .add(new MeasurementDetail(twoWheelerPolyline, true)));
        
        List<DXFLWPolyline> twoWheeler = Util.getPolyLinesByLayer(pl.getDoc(), 
        		layerNames.getLayerName("LAYER_NAME_TWO_WHEELER_PARKING"));        
        if(!twoWheeler.isEmpty()) {
        	Util.validateLayerColor(layerNames.getLayerName("LAYER_NAME_TWO_WHEELER_PARKING"), 
        			Util.getColorByPolyLine(twoWheeler), pl);
        }

        Util.getPolyLinesByLayer(pl.getDoc(), DA_PARKING).forEach(disablePersonParkPolyline -> pl.getParkingDetails()
                .getDisabledPersons().add(new MeasurementDetail(disablePersonParkPolyline, true)));
        
        List<DXFLWPolyline> daParking = Util.getPolyLinesByLayer(pl.getDoc(), 
        		DA_PARKING);        
        if(!daParking.isEmpty()) {
        	Util.validateLayerColor(DA_PARKING,Util.getColorByPolyLine(daParking), pl);
        }

        BigDecimal dimension = Util.getSingleDimensionValueByLayer(pl.getDoc(), DA_PARKING, pl);
        pl.getParkingDetails().setDistFromDAToMainEntrance(dimension);
        List<DXFLWPolyline> bldParking = Util.getPolyLinesByLayer(pl.getDoc(),
                layerNames.getLayerName("LAYER_NAME_PARKING_SLOT"));
        for (DXFLWPolyline pline : bldParking)
            pl.getParkingDetails().getCars().add(new MeasurementDetail(pline, true));

        for (Block b : pl.getBlocks()) {
            b.getBuilding().sortFloorByName();
            if (!b.getTypicalFloor().isEmpty())
                for (TypicalFloor typical : b.getTypicalFloor()) {
                    Floor modelFloor = b.getBuilding().getFloorNumber(typical.getModelFloorNo());
                    for (Integer no : typical.getRepetitiveFloorNos()) {
                        Floor typicalFloor = b.getBuilding().getFloorNumber(no);
                        typicalFloor.setUnits(modelFloor.getUnits());
                    }
                }
        }

        //
//        Util.getPolyLinesByLayer(pl.getDoc(), layerNames.getLayerName("LAYER_NAME_OPEN_PARKING")).forEach(
//                openParking -> pl.getParkingDetails().getOpenCars().add(new MeasurementDetail(openParking, true)));
        List<DXFLWPolyline> openParkingPloyLine = Util.getPolyLinesByLayer(pl.getDoc(), 
        		layerNames.getLayerName("LAYER_NAME_OPEN_PARKING"));
        
        if(!openParkingPloyLine.isEmpty()) {
        	Util.validateLayerColor(layerNames.getLayerName("LAYER_NAME_OPEN_PARKING"), 
        			Util.getColorByPolyLine(openParkingPloyLine), pl);
        }
        
        if(openParkingPloyLine!=null && !openParkingPloyLine.isEmpty()) {        	
        	pl.getParkingDetails().getOpenCars().add(new MeasurementDetail(openParkingPloyLine.get(0),true));
        	List<DXFLWPolyline> buildingFootPrintPolyLinesByLayer;
            String buildingFootPrint = layerNames.getLayerName("LAYER_NAME_BLOCK_NAME_PREFIX") + "\\d+_"
    				+ layerNames.getLayerName("LAYER_NAME_LEVEL_NAME_PREFIX") + "\\d+_"
    				+ layerNames.getLayerName("LAYER_NAME_BUILDING_FOOT_PRINT");
    		List<String> layerNames1 = Util.getLayerNamesLike(pl.getDoc(), buildingFootPrint);
    		for (String s : layerNames1) {
    			buildingFootPrintPolyLinesByLayer = Util.getPolyLinesByLayer(pl.getDoc(), s);
    			DXFLWPolyline plotBoundaryPolyLine = ((PlotDetail) pl.getPlot()).getPolyLine();
    			// Checking for the overlapping Building foot print
//    			isPolyLineOutsideOrTouchingBuildingOnly(openParkingPloyLine.get(0), buildingFootPrintPolyLinesByLayer.get(0), 
//    	        		"open Parking", pl, layerNames);
    			isYardOutsideOrTouchingBuildingOnly(
    			        openParkingPloyLine.get(0),
    			        buildingFootPrintPolyLinesByLayer.get(0),
    			        plotBoundaryPolyLine,   // 👈 ADD THIS
    			        "open Parking",
    			        pl,
    			        layerNames
    			);

    		}
        }
        
        Util.getPolyLinesByLayer(pl.getDoc(), layerNames.getLayerName("LAYER_NAME_MECHANICAL_LIFT")).forEach(
                mechLift -> pl.getParkingDetails().getMechParking().add(new MeasurementDetail(mechLift, true)));
        
        List<DXFLWPolyline> mechParking = Util.getPolyLinesByLayer(pl.getDoc(), 
        		layerNames.getLayerName("LAYER_NAME_MECHANICAL_LIFT"));        
        if(!mechParking.isEmpty()) {
        	Util.validateLayerColor(layerNames.getLayerName("LAYER_NAME_MECHANICAL_LIFT"), 
        			Util.getColorByPolyLine(mechParking), pl);
        }
        
        Util.getPolyLinesByLayer(pl.getDoc(), layerNames.getLayerName("LAYER_NAME_VISITOR_PARKING")).forEach(
                visitorPark -> pl.getParkingDetails().getVisitors().add(new MeasurementDetail(visitorPark, true)));        
        
        	List<DXFLWPolyline> visitorParkLyr = Util.getPolyLinesByLayer(pl.getDoc(), layerNames.getLayerName("LAYER_NAME_VISITOR_PARKING"));        
            if(!visitorParkLyr.isEmpty()) {
            	Util.validateLayerColor(layerNames.getLayerName("LAYER_NAME_VISITOR_PARKING"), 
            			Util.getColorByPolyLine(visitorParkLyr), pl);
            }
       
        
        Util.getPolyLinesByLayer(pl.getDoc(), layerNames.getLayerName("LAYER_NAME_SPECIAL_PARKING")).forEach(
                specialPark -> pl.getParkingDetails().getSpecial().add(new MeasurementDetail(specialPark, true)));
        
        List<DXFLWPolyline> spclParkLyr = Util.getPolyLinesByLayer(pl.getDoc(), 
        		layerNames.getLayerName("LAYER_NAME_SPECIAL_PARKING"));        
        if(!spclParkLyr.isEmpty()) {
        	Util.validateLayerColor(layerNames.getLayerName("LAYER_NAME_SPECIAL_PARKING"), 
        			Util.getColorByPolyLine(spclParkLyr), pl);
        }

        validate(pl);
        
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("End of Parking Extract......");
        return pl;
    }

    @Override
    public PlanDetail validate(PlanDetail pl) {
        if (pl.getStrictlyValidateDimension()) {
            validateDuplicate(pl);
        }
        return pl;
    }

    private void extractByLayer(PlanDetail pl, DXFDocument doc, Block block, Floor floor,
            List<DXFLWPolyline> dxflwPolylines) {
        int i = 0;
        if (!dxflwPolylines.isEmpty()) {
            List<FloorUnit> floorUnits = new ArrayList<>();
            for (DXFLWPolyline flrUnitPLine : dxflwPolylines) {
                FloorUnit floorUnit = new FloorUnit();
                floorUnit.setColorCode(flrUnitPLine.getColor());
                Occupancy occupancy = new Occupancy();
                // this should not be called
                Util.setOccupancyType(flrUnitPLine, occupancy);
                occupancy.setTypeHelper(Util.findOccupancyType(flrUnitPLine, pl));
                specialCaseCheckForOccupancyType(flrUnitPLine, occupancy);
                floorUnit.setOccupancy(occupancy);
                floorUnit.setArea(Util.getPolyLineArea(flrUnitPLine));
                i++;
                Polygon polygon = Util.getPolygon(flrUnitPLine);
                BigDecimal deduction = BigDecimal.ZERO;
                String deductLayerName = layerNames.getLayerName("LAYER_NAME_BLOCK_NAME_PREFIX") + block.getNumber()
                        + "_" + layerNames.getLayerName("LAYER_NAME_FLOOR_NAME_PREFIX") + floor.getNumber() + "_"
                        + layerNames.getLayerName("LAYER_NAME_UNITFA_DEDUCT");
                
                List<DXFLWPolyline> deductPolylines = Util.getPolyLinesByLayer(doc, deductLayerName);
                if(!deductPolylines.isEmpty()) {
                	Util.validateLayerColor(deductLayerName, 
                			Util.getColorByPolyLine(deductPolylines), pl);
                }
                
                for (DXFLWPolyline occupancyDeduct : Util.getPolyLinesByLayer(doc, deductLayerName)) {
                	boolean contains = false;
                    Iterator buildingIterator = occupancyDeduct.getVertexIterator();
                    while (buildingIterator.hasNext()) {
                        DXFVertex dxfVertex = (DXFVertex) buildingIterator.next();
                        Point point = dxfVertex.getPoint();
                        if (rayCasting.contains(point, polygon)) {
                            contains = true;
                            MeasurementDetail measurement = new MeasurementDetail();
                            measurement.setPolyLine(occupancyDeduct);
                            measurement.setArea(Util.getPolyLineArea(occupancyDeduct));
                            floorUnit.getArea().subtract(Util.getPolyLineArea(occupancyDeduct));
                            floorUnit.getDeductions().add(measurement);
                        }
                    }
                    if (contains) {
                        LOGGER.info("current deduct " + deduction + "  :add deduct for rest unit " + i + " area added "
                                + Util.getPolyLineArea(occupancyDeduct));
                        deduction = deduction.add(Util.getPolyLineArea(occupancyDeduct));
                    }
                }

                floorUnit.setTotalUnitDeduction(deduction);
                floorUnits.add(floorUnit);
            }
            floor.setUnits(floorUnits);
        }
    }

    private void specialCaseCheckForOccupancyType(DXFLWPolyline pLine, Occupancy occupancy) {
        if (pLine.getColor() == OCCUPANCY_A2_PARKING_WITHATTACHBATH_COLOR_CODE) {
            occupancy.setWithAttachedBath(true);
            occupancy.setType(OccupancyType.OCCUPANCY_A2);
        } else if (pLine.getColor() == OCCUPANCY_A2_PARKING_WOATTACHBATH_COLOR_CODE) {
            occupancy.setWithOutAttachedBath(true);
            occupancy.setType(OccupancyType.OCCUPANCY_A2);
        } else if (pLine.getColor() == OCCUPANCY_A2_PARKING_WITHDINE_COLOR_CODE) {
            occupancy.setWithDinningSpace(true);
            occupancy.setType(OccupancyType.OCCUPANCY_A2);
        }
    }

    private void validateDuplicate(PlanDetail pl) {
        List<Measurement> all = new ArrayList<>();
        all.addAll(pl.getParkingDetails().getCars());
        all.addAll(pl.getParkingDetails().getDisabledPersons());
        all.addAll(pl.getParkingDetails().getLoadUnload());
        all.addAll(pl.getParkingDetails().getMechParking());
        all.addAll(pl.getParkingDetails().getTwoWheelers());
        all.addAll(pl.getParkingDetails().getCars());
        
        all.addAll(pl.getParkingDetails().getOpenCars());
        all.addAll(pl.getParkingDetails().getCoverCars());
        all.addAll(pl.getParkingDetails().getBasementCars());
        all.addAll(pl.getParkingDetails().getVisitors());
        all.addAll(pl.getParkingDetails().getStilts());
        all.addAll(pl.getParkingDetails().getMechanicalLifts());

        /*
         * for (Block block : pl.getBlocks()) { for (Floor floor : block.getBuilding().getFloors()) {
         * all.addAll(floor.getParking().getOpenCars()); all.addAll(floor.getParking().getCoverCars());
         * all.addAll(floor.getParking().getBasementCars()); all.addAll(floor.getParking().getVisitors());
         * all.addAll(floor.getParking().getStilts()); all.addAll(floor.getParking().getMechanicalLifts()); } }
         */
        Set<MeasurementDetail> duplicates = new HashSet<>();

        for (Measurement m : all) {
            MeasurementDetail md = (MeasurementDetail) m;
            Iterator vertexIterator = md.getPolyLine().getVertexIterator();
            LOGGER.debug("Points on the " + " outside");
            // LOG.debug("Max x: " + m.getPolyLine().getBounds().getMaximumX() + " Min x:" +
            // m.getPolyLine().getBounds().getMinimumX());
            // LOG.debug("Max y: " + m.getPolyLine().getBounds().getMaximumY() + " Min x:" +
            // m.getPolyLine().getBounds().getMinimumY());
            while (vertexIterator.hasNext()) {
                DXFVertex next = (DXFVertex) vertexIterator.next();
                LOGGER.error(next.getPoint().getX() + "," + next.getPoint().getY());
            }

            for (Measurement m1 : all) {
                MeasurementDetail md1 = (MeasurementDetail) m1;
                Iterator mVertexIterator = md1.getPolyLine().getVertexIterator();
                vertexIterator = md1.getPolyLine().getVertexIterator();
                LOGGER.error("Points on the " + " Inside");
                // LOG.debug("Max x: " + m1.getPolyLine().getBounds().getMaximumX() + " Min x:" +
                // m1.getPolyLine().getBounds().getMinimumX());
                // LOG.debug("Max y: " + m1.getPolyLine().getBounds().getMaximumY() + " Min x:" +
                // m1.getPolyLine().getBounds().getMinimumY());
                while (vertexIterator.hasNext()) {
                    DXFVertex next = (DXFVertex) vertexIterator.next();
                    LOGGER.debug("           " + next.getPoint().getX() + "," + next.getPoint().getY());
                }
                int duplicatePoint = 0;
                if (m == m1)
                    continue;

                while (mVertexIterator.hasNext()) {

                    Iterator m1VertexIterator = md1.getPolyLine().getVertexIterator();

                    DXFVertex mNext = (DXFVertex) mVertexIterator.next();
                    Point mPoint = mNext.getPoint();
                    while (m1VertexIterator.hasNext()) {
                        DXFVertex m1Next = (DXFVertex) m1VertexIterator.next();
                        Point m1Point = m1Next.getPoint();

                        if (Util.pointsEquals(mPoint, m1Point)) {
                        	LOGGER.info("duplicate points = " + mPoint + ", "+ m1Point);
                            duplicatePoint++;
                        }

                    }

                }
                if (duplicatePoint > 2) {
                    duplicates.add(md);
                    LOGGER.error(" found duplicate for outside point ");
                }

            }
        }
        if (!duplicates.isEmpty()) {
            PrintUtil.markCircle(duplicates, pl, "DUPLICATE_PARKING", DxfFileConstants.ERROR_MARK_CIRCLE_COLOR);
            LOGGER.error(" found duplicates for outside point ");
            pl.addError("Duplicate", "Duplicate/Overlaying of items found in  Parking");
        }
    }
    
//    public boolean isPolyLineOutsideOrTouchingBuildingOnly(
//            DXFLWPolyline openParkingPloyLine,
//            DXFLWPolyline buildingFootprint,
//            String openParking,
//            PlanDetail pl,
//            LayerNames layerNames) {
//    	Boolean finalStatus = true;
//
//        if (openParkingPloyLine == null || buildingFootprint == null) 
//        	finalStatus = true;
//
//        List<DXFLine> yardLines = getLinesOfPolyline(openParkingPloyLine);
//
//        for (DXFLine yLine : yardLines) {
//            Point y1 = yLine.getStartPoint();
//            Point y2 = yLine.getEndPoint();
//
//            // ---- 1. Check Start Vertex ----
//            if (Util.isPointStrictlyInsidePolygon(buildingFootprint, y1)) {
//                pl.getErrors().put(
//                    "OPEN_PARKING_POINTS_NOT_ON_PLOT_BOUNDARY - " + openParking,
//                    "Points of " + openParking + " not properly on " + layerNames.getLayerName("LAYER_NAME_BUILDING_FOOT_PRINT"));
//                finalStatus = false;
//            }
//
//            // ---- 2. Check End Vertex ----
//            if (Util.isPointStrictlyInsidePolygon(buildingFootprint, y2)) {
//                try {
//                	pl.getErrors().put("Open Parking calculation error for boundary" + openParking,
//                            "Points of " + openParking + " not properly on " 
//                	+ layerNames.getLayerName("LAYER_NAME_BUILDING_FOOT_PRINT"));
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//                finalStatus = false;
//            }
//
//            // ---- 3. Check Midpoint ----
//            Point mid = new Point();
//            mid.setX((y1.getX() + y2.getX()) / 2.0);
//            mid.setY((y1.getY() + y2.getY()) / 2.0);
//
//            if (Util.isPointStrictlyInsidePolygon(buildingFootprint, mid)) {
//                pl.getErrors().put(
//                    "OPEN_PARKING_POINTS_NOT_ON_PLOT_BOUNDARY - " + openParking,
//                    "Points of " + openParking + " not properly on PLOT_BOUNDARY"
//                );
//                // Keep your DXF debugging line
//                PrintUtil.printForDXf(y1, y2, openParking + "_EDGE_INSIDE", pl);
//                finalStatus = false;
//            }
//        }
//
//        return finalStatus;
//    }

    public boolean isYardOutsideOrTouchingBuildingOnly(
            DXFLWPolyline openParkingPloyLine,
            DXFLWPolyline buildingFootprint,
            DXFLWPolyline plotBoundary,
            String openParking,
            PlanDetail pl,
            LayerNames layerNames) {

        boolean finalStatus = true;

        if (openParkingPloyLine == null || buildingFootprint == null || plotBoundary == null)
            return true;

        List<DXFLine> yardLines = getLinesOfPolyline(openParkingPloyLine);

        for (DXFLine yLine : yardLines) {

            Point y1 = yLine.getStartPoint();
            Point y2 = yLine.getEndPoint();

            // ---------- 1. OPEN PARKING MUST NOT BE INSIDE BUILDING ----------
            if (Util.isPointStrictlyInsidePolygon(buildingFootprint, y1)
                    || Util.isPointStrictlyInsidePolygon(buildingFootprint, y2)) {

                pl.getErrors().put(
                        "OPEN_PARKING_INSIDE_BUILDING - " + openParking,
                        "Open parking lies inside building footprint: "
                                + layerNames.getLayerName("LAYER_NAME_BUILDING_FOOT_PRINT"));
                finalStatus = false;
            }

            // ---------- 2. CHECK MIDPOINT AGAINST BUILDING ----------
            Point mid = new Point();
            mid.setX((y1.getX() + y2.getX()) / 2.0);
            mid.setY((y1.getY() + y2.getY()) / 2.0);

            if (Util.isPointStrictlyInsidePolygon(buildingFootprint, mid)) {
                pl.getErrors().put(
                        "OPEN_PARKING_INSIDE_BUILDING - " + openParking,
                        "Open parking overlaps building footprint");
                PrintUtil.printForDXf(y1, y2, openParking + "_INSIDE_BUILDING", pl);
                finalStatus = false;
            }

            // ---------- 3. NEW CONDITION: MUST BE INSIDE / TOUCH PLOT ----------
            if (isPointOutsidePolygon(plotBoundary, y1)
                    || isPointOutsidePolygon(plotBoundary, y2)
                    || isPointOutsidePolygon(plotBoundary, mid)) {

                pl.getErrors().put(
                        "OPEN_PARKING_OUTSIDE_PLOT - " + openParking,
                        "Open parking lies outside plot boundary: "
                                + layerNames.getLayerName("LAYER_NAME_PLOT_BOUNDARY"));

                PrintUtil.printForDXf(y1, y2, openParking + "_OUTSIDE_PLOT", pl);
                finalStatus = false;
            }
        }

        return finalStatus;
    }

    public static boolean isPointOutsidePolygon(DXFLWPolyline poly, Point p) {
        // outside = NOT inside AND NOT on boundary
        return !Util.isPointStrictlyInsidePolygon(poly, p)
                && !Util.isPointOnPolygonBoundary(poly, p);
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
    
}
