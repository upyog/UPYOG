package org.egov.dx.web.models;

import javax.xml.bind.annotation.XmlAttribute;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@XStreamAlias("Organization")

public class Organization {
	
	@XmlAttribute
    @XStreamAlias("name")
    private String name;
    
    @XmlAttribute
    @XStreamAlias("code")
    private String code="";
    
    @XmlAttribute
    @XStreamAlias("tin")
    private String tin="";
    
    @XmlAttribute
    @XStreamAlias("uid")
    private String uid;
    
    @XmlAttribute
    @XStreamAlias("type")
    private String type="SG";
	
    @XStreamAlias("Address")
    private Address address;
}
