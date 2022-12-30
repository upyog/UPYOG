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
@XStreamAlias("Person")
public class Person {
	
	@XmlAttribute
    @XStreamAlias("uid")
    private String uid;
    
    @XmlAttribute
    @XStreamAlias("title")
    private String title;
    
    @XmlAttribute
    @XStreamAlias("name")
    private String name;
    
    @XmlAttribute
    @XStreamAlias("dob")
    private String dob;
    
    @XmlAttribute
    @XStreamAlias("age")
    private String age;
	
    @XmlAttribute
    @XStreamAlias("swd")
    private String swd;
    
    @XmlAttribute
    @XStreamAlias("swdIndicator")
    private String swdIndicator;
    
    @XmlAttribute
    @XStreamAlias("motherName")
    private String motherName;
    
    @XmlAttribute
    @XStreamAlias("gender")
    private String gender;
    
    @XmlAttribute
    @XStreamAlias("maritalStatus")
    private String maritalStatus;
    
    @XmlAttribute
    @XStreamAlias("relationWithHof")
    private String relationWithHof;
    
    @XmlAttribute
    @XStreamAlias("disabilityStatus")
    private String disabilityStatus;
    
    @XmlAttribute
    @XStreamAlias("category")
    private String category;
    
    @XmlAttribute
    @XStreamAlias("religion")
    private String religion;

    @XmlAttribute
    @XStreamAlias("phone")
    private String phone;
    
    @XmlAttribute
    @XStreamAlias("email")
    private String email;
    
    @XStreamAlias("Address")
    private Address address;
    
	@XStreamAlias("Photo")
    private String photo;
	
	     
}
