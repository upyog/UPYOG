package org.egov.asset.web.models;

public class LandAsset extends Asset{
	private LandInfo landInfo;

	public LandAsset(String type, LandInfo landInfo) {
        super(type, landInfo);
        this.landInfo = landInfo;
    }

    public LandInfo getLandInfo() {
    	this.landInfo = (LandInfo) super.getAdditionalDetails();
        return landInfo;
    }

}
