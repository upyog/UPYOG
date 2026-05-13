package org.egov.edcr.feature;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Flight;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.GeneralStair;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.StairLanding;
import org.egov.common.entity.edcr.TypicalFloor;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.entity.blackbox.MeasurementDetail;
import org.egov.edcr.entity.blackbox.PlanDetail;
import org.egov.edcr.service.LayerNames;
import org.egov.edcr.utility.DcrConstants;
import org.egov.edcr.utility.Util;
import org.kabeja.dxf.DXFConstants;
import org.kabeja.dxf.DXFDocument;
import org.kabeja.dxf.DXFLWPolyline;
import org.kabeja.dxf.DXFLayer;
import org.kabeja.dxf.DXFLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
public class GeneralStairExtract extends FeatureExtract {
	@Autowired
	private LayerNames layerNames;

	@Override
	public PlanDetail extract(PlanDetail pl) {
		for (Block block : pl.getBlocks())
			if (block.getBuilding() != null && !block.getBuilding().getFloors().isEmpty()) {
				boolean highRise = block.getBuilding().getBuildingHeight()
						.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS)
						.compareTo(BigDecimal.valueOf(16).setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS,
								DcrConstants.ROUNDMODE_MEASUREMENTS)) > 0;

				for (Floor floor : block.getBuilding().getFloors())
					addGeneralStairs(pl, block, floor, highRise);
			}
		return pl;
	}

	@Override
	public PlanDetail validate(PlanDetail pl) {
		return pl;
	}

	private void addGeneralStairs(PlanDetail pl, Block block, Floor floor, boolean highRise) {
		if (!block.getTypicalFloor().isEmpty())
			for (TypicalFloor tp : block.getTypicalFloor())
				if (tp.getRepetitiveFloorNos().contains(floor.getNumber()))
					for (Floor allFloors : block.getBuilding().getFloors())
						if (allFloors.getNumber().equals(tp.getModelFloorNo())
								&& !allFloors.getGeneralStairs().isEmpty()) {
							floor.setGeneralStairs(allFloors.getGeneralStairs());
							return;
						}

		// Layer name convention BLK_n_FLR_i_STAIR_k
		String generalStairNamePattern = "BLK_" + block.getNumber() + "_FLR_" + floor.getNumber() + "_STAIR" + "_+\\d";

		DXFDocument doc = pl.getDoc();

		List<String> generalStairNames = Util.getLayerNamesLike(doc, generalStairNamePattern);

        if (!generalStairNames.isEmpty())
            for (String generalStairName : generalStairNames) {
                DXFLayer dxfLayer = doc.getDXFLayer(generalStairName);
                List polyLines = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_LWPOLYLINE);
                List mTexts = dxfLayer.getDXFEntities(DXFConstants.ENTITY_TYPE_MTEXT);
                if ((polyLines != null && !polyLines.isEmpty()) || (mTexts != null
                        && !mTexts.isEmpty())) {
                	
                //Code added for the layername with colorCode match
        		Util.validateLayerColor(generalStairName, Util.getColorByPolyLine(polyLines), pl);
        		
				String[] stairName = generalStairName.split("_");
				String stairNo = stairName[5];
				if (stairName.length == 6 && stairNo != null && !stairNo.isEmpty()) {
					GeneralStair generalStair = new GeneralStair();
					generalStair.setNumber(stairNo);

					// set polylines in BLK_n_FLR_i_STAIR_k
					String stairLayerName = String.format(layerNames.getLayerName("LAYER_NAME_STAIR_FLOOR"),
							block.getNumber(), floor.getNumber(), stairNo);

					String floorHeight = Util.getMtextByLayerName(doc, stairLayerName, "FLR_HT_M");

					if (!isBlank(floorHeight)) {
						if (floorHeight.contains("=")) {
							floorHeight = floorHeight.split("=")[1] != null
									? floorHeight.split("=")[1].replaceAll("[^\\d.]", "")
									: "";
						} else
							floorHeight = floorHeight.replaceAll("[^\\d.]", "");

						if (!isBlank(floorHeight)) {
							BigDecimal height = BigDecimal.valueOf(Double.parseDouble(floorHeight));
							generalStair.setFloorHeight(height);
						} else {
							pl.addError(stairLayerName + "_FLR_HT_M",
									"Floor height is not defined in layer " + stairLayerName);
						}
					} else {
						pl.addError(stairLayerName + "_FLR_HT_M",
								"Floor height is not defined in layer " + stairLayerName);
					}

					List<DXFLWPolyline> generalStairPolyLines = Util.getPolyLinesByLayer(doc, stairLayerName);

					if (!generalStairPolyLines.isEmpty()) {
						List<Measurement> stairMeasurements = generalStairPolyLines.stream()
								.map(flightPolyLine -> new MeasurementDetail(flightPolyLine, true))
								.collect(Collectors.toList());
						generalStair.setStairMeasurements(stairMeasurements);
					}

					String flightLayerNamePattern = String.format(layerNames.getLayerName("LAYER_NAME_STAIR_FLIGHT"),
							block.getNumber(), floor.getNumber(), stairNo, "+\\d");

					addFlight(pl, flightLayerNamePattern, generalStair);

					String landingNamePattern = String.format(layerNames.getLayerName("LAYER_NAME_STAIR_LANDING"),
							block.getNumber(), floor.getNumber(), stairNo, "+\\d");

					addStairLanding(pl, landingNamePattern, generalStair);

					floor.addGeneralStair(generalStair);
				}
			}
			}
	}

	private void addFlight(PlanDetail pl, String flightLayerNamePattern, GeneralStair generalStair) {
	    DXFDocument doc = pl.getDoc();
		List<String> flightLayerNames = Util.getLayerNamesLike(doc, flightLayerNamePattern);
		
		List<DXFLWPolyline> stairFlightPolyLines1 = Util.getPolyLinesByLayer(doc, flightLayerNames.get(0));
		
//		//Code added for the layername with colorCode match
//		Util.validateLayerColor(flightLayerNames.get(0), Util.getColorByPolyLine(stairFlightPolyLines1), pl);
		
		
		Map<String, String> data = Util.getColorByDimensionByLayerByColorCode(pl, flightLayerNames.get(0),DxfFileConstants.STAIR_FLIGHT_LENGTH_COLOR);
		Util.validateSubLayerColor(data.get("layerName"), "LENGTH", Integer.parseInt(data.get("colorCode")), pl);
		
		
		data = Util.getColorByDimensionByLayerByColorCode(pl, flightLayerNames.get(0),DxfFileConstants.STAIR_FLIGHT_WIDTH_COLOR);
		Util.validateSubLayerColor(data.get("layerName"), "WIDTH", Integer.parseInt(data.get("colorCode")), pl);
		
		if (!flightLayerNames.isEmpty()) {
			List<Flight> flights = new ArrayList<>();
			for (String flightLayer : flightLayerNames) {

				Flight flight = new Flight();

				String[] flightNo = flightLayer.split("_");

				flight.setNumber(flightNo[7]);

				List<DXFLWPolyline> stairFlightPolyLines = Util.getPolyLinesByLayer(doc, flightLayer);
    			
				boolean isClosed = stairFlightPolyLines.stream().allMatch(dxflwPolyline -> dxflwPolyline.isClosed());

				flight.setFlightClosed(isClosed);

				List<Measurement> flightPolyLines = stairFlightPolyLines.stream()
						.map(flightPolyLine -> new MeasurementDetail(flightPolyLine, true))
						.collect(Collectors.toList());

				flight.setFlights(flightPolyLines);

				// set length of flight
				List<BigDecimal> stairFlightLengths = Util.getListOfDimensionByColourCode(pl, flightLayer,
						DxfFileConstants.STAIR_FLIGHT_LENGTH_COLOR);
				
//				Map<String, String> data = Util.getColorByDimensionByLayerByColorCode(pl, flightLayer,DxfFileConstants.STAIR_FLIGHT_LENGTH_COLOR);
//				Util.validateSubLayerColor(data.get("layerName"), "LENGTH", Integer.parseInt(data.get("colorCode")), pl);
				
				flight.setLengthOfFlights(stairFlightLengths);

				// set width of flight
				List<BigDecimal> stairFlightWidths = Util.getListOfDimensionByColourCode(pl, flightLayer,
						DxfFileConstants.STAIR_FLIGHT_WIDTH_COLOR);
				
//				data = Util.getColorByDimensionByLayerByColorCode(pl, flightLayer,DxfFileConstants.STAIR_FLIGHT_WIDTH_COLOR);
//				Util.validateLayerColor(data.get("layerName"), Integer.parseInt(data.get("colorCode")), pl);
				
				flight.setWidthOfFlights(stairFlightWidths);

				// set number of rises
				List<DXFLine> fireStairLines = Util.getLinesByLayer(doc, flightLayer);

				flight.setNoOfRises(BigDecimal.valueOf(fireStairLines.size()));

				flights.add(flight);

			}
			generalStair.setFlights(flights);
		}
	}

	private void addStairLanding(PlanDetail pl, String landingNamePattern, GeneralStair generalStair) {
		DXFDocument doc = pl.getDoc();
	    List<String> landingLayerNames = Util.getLayerNamesLike(doc, landingNamePattern);
		List<StairLanding> landings = new ArrayList<>();

		Map<String, String> data = Util.getColorByDimensionByLayerByColorCode(pl, landingLayerNames.get(0),DxfFileConstants.STAIR_FLIGHT_LENGTH_COLOR);
		Util.validateSubLayerColor(data.get("layerName"), "LENGTH", Integer.parseInt(data.get("colorCode")), pl);
		
		
		data = Util.getColorByDimensionByLayerByColorCode(pl, landingLayerNames.get(0),DxfFileConstants.STAIR_FLIGHT_WIDTH_COLOR);
		Util.validateSubLayerColor(data.get("layerName"), "WIDTH", Integer.parseInt(data.get("colorCode")), pl);
		
		for (String landingLayer : landingLayerNames) {

			StairLanding stairLanding = new StairLanding();

			String[] landingNo = landingLayer.split("_");

			stairLanding.setNumber(landingNo[7]);

			List<DXFLWPolyline> landingPolyLines = Util.getPolyLinesByLayer(doc, landingLayer);
//			//Code added for the layername with colorCode match
//			Util.validateLayerColor(landingLayer, Util.getColorByPolyLine(landingPolyLines), pl);
			
			boolean isClosed = landingPolyLines.stream().allMatch(dxflwPolyline -> dxflwPolyline.isClosed());

			stairLanding.setLandingClosed(isClosed);

			List<Measurement> landingPolyLinesMeasurement = landingPolyLines.stream()
					.map(flightPolyLine -> new MeasurementDetail(flightPolyLine, true)).collect(Collectors.toList());

			stairLanding.setLandings(landingPolyLinesMeasurement);

			// set length of flight
			List<BigDecimal> landingLengths = Util.getListOfDimensionByColourCode(pl, landingLayer,
					DxfFileConstants.STAIR_FLIGHT_LENGTH_COLOR);
			
//			Map<String, String> data = Util.getColorByDimensionByLayerByColorCode(pl, landingLayer,DxfFileConstants.STAIR_FLIGHT_LENGTH_COLOR);
//			Util.validateLayerColor(data.get("layerName"), Integer.parseInt(data.get("colorCode")), pl);
			
			stairLanding.setLengths(landingLengths);

			// set width of flight
			List<BigDecimal> landingWidths = Util.getListOfDimensionByColourCode(pl, landingLayer,
					DxfFileConstants.STAIR_FLIGHT_WIDTH_COLOR);
			
//			data = Util.getColorByDimensionByLayerByColorCode(pl, landingLayer,DxfFileConstants.STAIR_FLIGHT_WIDTH_COLOR);
//			Util.validateLayerColor(data.get("layerName"), Integer.parseInt(data.get("colorCode")), pl);

			stairLanding.setWidths(landingWidths);

			landings.add(stairLanding);
		}

		generalStair.setLandings(landings);
	}

}
