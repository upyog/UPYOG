package org.egov.dx.web.models;

import javax.xml.bind.annotation.XmlAttribute;

import com.thoughtworks.xstream.annotations.XStreamAlias;

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
	
	@XmlAttribute
    @XStreamAlias("type")
    private String type;
    
    @XmlAttribute
    @XStreamAlias("line1")
    private String line1;
    
    @XmlAttribute
    @XStreamAlias("line2")
    private String line2;
    
    @XmlAttribute
    @XStreamAlias("house")
    private String house;
    
    @XmlAttribute
    @XStreamAlias("landmark")
    private String landmark;
	
    @XmlAttribute
    @XStreamAlias("locality")
    private String locality;
    
    @XmlAttribute
    @XStreamAlias("vtc")
    private String vtc;
    
    @XmlAttribute
    @XStreamAlias("district")
    private String district;
    
    @XmlAttribute
    @XStreamAlias("pin")
    private String pin;
    
    @XmlAttribute
    @XStreamAlias("state")
    private String state;
    
    @XmlAttribute
    @XStreamAlias("country")
    private String country="IN";
	
}
