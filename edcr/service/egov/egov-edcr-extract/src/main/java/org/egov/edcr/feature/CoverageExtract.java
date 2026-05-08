package org.egov.edcr.feature;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Measurement;
import org.egov.edcr.entity.blackbox.MeasurementDetail;
import org.egov.edcr.entity.blackbox.PlanDetail;
import org.egov.edcr.service.LayerNames;
import org.egov.edcr.utility.Util;
import org.kabeja.dxf.DXFLWPolyline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoverageExtract extends FeatureExtract {
    private static final Logger LOG = LogManager.getLogger(CoverageExtract.class);

    @Autowired
    private LayerNames layerNames;

    @Override
    public PlanDetail validate(PlanDetail planDetail) {
        for (Block block : planDetail.getBlocks())
            if (block.getCoverage().isEmpty())
                planDetail.addError("coverageArea" + block.getNumber(),
                        "Coverage Area for block " + block.getNumber() + " not Provided");
        return planDetail;
    }

    @Override
    public PlanDetail extract(PlanDetail pl) {
        if (LOG.isDebugEnabled())
            LOG.debug("Starting Coverage Extract......");
        for (Block block : pl.getBlocks()) {
            List<DXFLWPolyline> polylinesCoverage = Util.getPolyLinesByLayer(pl.getDoc(),
                    String.format(layerNames.getLayerName("LAYER_NAME_COVERAGE"), block.getNumber()));
            //Code added for the layername with colorCode match
            if (polylinesCoverage != null && !polylinesCoverage.isEmpty()) {
                Util.validateLayerColor(polylinesCoverage.get(0).getLayerName(),Util.getColorByPolyLine(polylinesCoverage),pl);
            }
            List<DXFLWPolyline> polylinesCoverageDeduct = Util.getPolyLinesByLayer(pl.getDoc(),
                    String.format(layerNames.getLayerName("LAYER_NAME_COVERAGE_DEDUCT"), block.getNumber()));
            
            if (polylinesCoverageDeduct != null && !polylinesCoverageDeduct.isEmpty()) {
                Util.validateLayerColor(polylinesCoverageDeduct.get(0).getLayerName(),Util.getColorByPolyLine(polylinesCoverageDeduct),pl);
            }
            for (DXFLWPolyline polyline : polylinesCoverage) {
                Measurement measurement = new MeasurementDetail(polyline, false);
                block.getCoverage().add(measurement);
            }
            for (DXFLWPolyline polyline : polylinesCoverageDeduct) {
                Measurement measurement = new MeasurementDetail(polyline, false);
                block.getCoverageDeductions().add(measurement);
            }
        }
        if (LOG.isDebugEnabled())
            LOG.debug("Starting Coverage Extract......");
        return pl;
    }

}
