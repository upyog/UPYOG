package org.egov.edcr.feature;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.HeadRoom;
import org.egov.edcr.entity.blackbox.PlanDetail;
import org.egov.edcr.service.LayerNames;
import org.egov.edcr.utility.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HeadRoomExtract extends FeatureExtract {

    @Autowired
    private LayerNames layerNames;

    @Override
    public PlanDetail extract(PlanDetail planDetail) {

        List<Block> blocks = planDetail.getBlocks();

        for (Block block : blocks)
            if (block.getBuilding() != null) {
                String layerName = String.format(layerNames.getLayerName("LAYER_NAME_STAIR_HEAD_ROOM"), block.getNumber());
                List<BigDecimal> headRoomDimensions = Util.getListOfDimensionValueByLayer(planDetail, layerName);

                Map<String, String> data = Util.getColorByDimensionByLayer(planDetail, layerName);
		        String layer = data.get("layerName");
		        String color = data.get("colorCode");
		        
		      //Code added for the layername with colorCode match
				Util.validateLayerColor(layer, Integer.parseInt(color), planDetail);
                
                if (headRoomDimensions != null && headRoomDimensions.size() > 0) {
                    HeadRoom headRoom = new HeadRoom();
                    headRoom.setHeadRoomDimensions(headRoomDimensions);
                    block.getBuilding().setHeadRoom(headRoom);
                }
            }

        return planDetail;
    }

    @Override
    public PlanDetail validate(PlanDetail planDetail) {
        return planDetail;
    }

}
