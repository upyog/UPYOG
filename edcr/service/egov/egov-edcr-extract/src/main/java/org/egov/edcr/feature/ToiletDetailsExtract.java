package org.egov.edcr.feature;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Room;
import org.egov.common.entity.edcr.RoomHeight;
import org.egov.common.entity.edcr.Toilet;
import org.egov.common.entity.edcr.Window;
import org.egov.edcr.entity.blackbox.MeasurementDetail;
import org.egov.edcr.entity.blackbox.PlanDetail;
import org.egov.edcr.service.LayerNames;
import org.egov.edcr.utility.Util;
import org.kabeja.dxf.DXFDimension;
import org.kabeja.dxf.DXFLWPolyline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ToiletDetailsExtract extends FeatureExtract {
    private static final Logger LOG = LogManager.getLogger(ToiletDetailsExtract.class);
    @Autowired
    private LayerNames layerNames;

    @Override
    public PlanDetail validate(PlanDetail planDetail) {
        return planDetail;
    }

    @Override
    public PlanDetail extract(PlanDetail planDetail) {
        for (Block block : planDetail.getBlocks()) {
            if (block.getBuilding() != null && block.getBuilding().getFloors() != null) {
                for (Floor f : block.getBuilding().getFloors()) {
                    List<Toilet> toilets = new ArrayList<>();
                    String layerName = String.format(layerNames.getLayerName("LAYER_NAME_BLK_FLR_TOILET"), block.getNumber(),
                            f.getNumber(), "+\\d");

                    List<String> names = Util.getLayerNamesLike(planDetail.getDoc(), layerName);

                    for (String toiletLayer : names) {
                        List<DXFLWPolyline> toiletMeasurements = Util.getPolyLinesByLayer(planDetail.getDoc(), toiletLayer);

                        if (!toiletMeasurements.isEmpty()) {
                            Toilet toiletObj = new Toilet();
                            List<Measurement> toiletMeasurementList = new ArrayList<>();
                            toiletMeasurements.forEach(toilet -> {
                                Measurement measurementToilet = new MeasurementDetail(toilet, true);
                                toiletMeasurementList.add(measurementToilet);
                            });

                            toiletObj.setToilets(toiletMeasurementList);
                            toilets.add(toiletObj);
                        }
                    }

                    String toiletVentilationLayer = String.format(layerNames.getLayerName("LAYER_NAME_BLK_FLR_TOILET_VENTILATION"), block.getNumber(),
                            f.getNumber(), "+\\d");
                    List<String> ventilationList = Util.getLayerNamesLike(planDetail.getDoc(), toiletVentilationLayer);

                    for (String ventilationHeightLayer : ventilationList) {
                        List<DXFLWPolyline> toiletVentilationMeasurements = Util.getPolyLinesByLayer(planDetail.getDoc(), ventilationHeightLayer);
                        String windowHeight = Util.getMtextByLayerName(planDetail.getDoc(), ventilationHeightLayer);

                        BigDecimal windowHeight1 = windowHeight != null
                                ? BigDecimal.valueOf(Double.parseDouble(windowHeight.replaceAll("WINDOW_HT_M=", "")))
                                : BigDecimal.ZERO;

                        for (Toilet toiletObj : toilets) {
                            toiletObj.setToiletVentilation(windowHeight1);
                        }
                    }
                    
                    f.setToilet(toilets);
                }
            }
        }

        return planDetail;
    }
}