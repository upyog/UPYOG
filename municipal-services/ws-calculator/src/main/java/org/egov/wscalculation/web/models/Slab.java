package org.egov.wscalculation.web.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Slab {
	private double from;
	private double to;
	private double charge;
	private double meterCharge;
	private long effectiveFrom;
	
	private long effectiveTo;
	
	public int getFrom() {
		return from;
	}
	public void setFrom(int from) {
		this.from = from;
	}
	public int getTo() {
		return to;
	}
	public void setTo(int to) {
		this.to = to;
	}
	public double getCharge() {
		return charge;
	}
	public void setCharge(double charge) {
		this.charge = charge;
	}
	public double getMeterCharge() {
		return meterCharge;
	}
	public void setMeterCharge(double meterCharge) {
		this.meterCharge = meterCharge;
	}

	public long getEffectiveFrom() {
		return effectiveFrom;
	}
	public void setEffectiveFrom(long  effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}
	
	public long getEffectiveTo() {
		return effectiveTo;
	}
	public void setEffectiveTo(long  effectiveTo) {
		this.effectiveTo = effectiveTo;
	}
	@Override
	public String toString() {
		return "Slab [from=" + from + ", to=" + to + ", charge=" + charge + ", meterCharge=" + meterCharge
				+ ", effectiveFrom=" + effectiveFrom + ", effectiveTo=" + effectiveTo + "]";
	}
	
	
	
}
