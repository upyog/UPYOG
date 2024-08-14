package org.egov.collection.web.contract;
public class PropertyDetail {
    private String oldConnectionNo;
    private String plotSize;
    private String usageCategory;
    private String propertyId;
    private String address;
    private String meterDetails;
    private String meterId;
    private String meterMake;
    private String averageMeterReading;
    private String initialMeterReading;

    // Getters and Setters
    public String getOldConnectionNo() { return oldConnectionNo; }
    public void setOldConnectionNo(String oldConnectionNo) { this.oldConnectionNo = oldConnectionNo; }

    public String getPlotSize() { return plotSize; }
    public void setPlotSize(String plotSize) { this.plotSize = plotSize; }

    public String getUsageCategory() { return usageCategory; }
    public void setUsageCategory(String usageCategory) { this.usageCategory = usageCategory; }

    public String getPropertyId() { return propertyId; }
    public void setPropertyId(String propertyId) { this.propertyId = propertyId; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getMeterDetails() { return meterDetails; }
    public void setMeterDetails(String meterDetails) { this.meterDetails = meterDetails; }

    public String getMeterId() { return meterId; }
    public void setMeterId(String meterId) { this.meterId = meterId; }

    public String getMeterMake() { return meterMake; }
    public void setMeterMake(String meterMake) { this.meterMake = meterMake; }

    public String getAverageMeterReading() { return averageMeterReading; }
    public void setAverageMeterReading(String averageMeterReading) { this.averageMeterReading = averageMeterReading; }

    public String getInitialMeterReading() { return initialMeterReading; }
    public void setInitialMeterReading(String initialMeterReading) { this.initialMeterReading = initialMeterReading; }
}
