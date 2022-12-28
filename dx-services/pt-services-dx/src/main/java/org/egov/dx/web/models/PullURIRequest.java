package org.egov.dx.web.models;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

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
@XmlRootElement
@XStreamAlias("PullURIRequest")
public class PullURIRequest {

    @XStreamAlias("DocDetails")
    private DocDetailsRequest docDetails;
    
    @XmlAttribute
    @XStreamAlias("ts")
    private String ts;
    
    @XmlAttribute
    @XStreamAlias("txn")
    private String txn;
}
