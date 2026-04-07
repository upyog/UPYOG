package org.egov.dx.web.models;

import javax.xml.bind.annotation.XmlAttribute;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@XStreamAlias("Address")

public class Address {
	 @XStreamAsAttribute
	    private String type;
	    @XStreamAsAttribute
	    private String line1;
	    @XStreamAsAttribute
	    private String line2;
	    @XStreamAsAttribute
	    private String house;
	    @XStreamAsAttribute
	    private String landmark;
	    @XStreamAsAttribute
	    private String locality;
	    @XStreamAsAttribute
	    private String vtc;
	  
	 @XStreamAsAttribute
	    private String district;
	    @XStreamAsAttribute
	    private String pin;
	    @XStreamAsAttribute
	    private String state;
	    @XStreamAsAttribute
	    private String country;

	    // Constructors, getters, and setters
	    // ...

	    // Getter and setter methods for attributes
	    public String getDistrict() {
	        return district;
	    }

	    public void setDistrict(String district) {
	        this.district = district;
	    }

	    public String getPin() {
	        return pin;
	    }

	    public void setPin(String pin) {
	        this.pin = pin;
	    }

	    public String getState() {
	        return state;
	    }

	    public void setState(String state) {
	        this.state = state;
	    }

	    public String getCountry() {
	        return country;
	    }

	    public void setCountry(String country) {
	        this.country = country;
	    }
	}
