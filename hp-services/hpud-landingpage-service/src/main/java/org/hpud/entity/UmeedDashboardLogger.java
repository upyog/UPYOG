package org.hpud.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "umeed_dashboard_log")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class UmeedDashboardLogger {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	
	@Column(name = "date", length = 20, nullable = false)
	private String date;


	@Column(name = "servicetype", length = 20, nullable = false)
	private String serviceType;

	 @Type(type = "jsonb")
	@Column(name = "requestpayload", columnDefinition = "jsonb")
	private JsonNode  requestPayload;
	 
	 @Type(type = "jsonb")
	 @Column(name = "responsepayload", columnDefinition = "jsonb")
	private JsonNode  responsePayload;

	@Column(name = "createdtime", nullable = false)
	private Long createdTime;

}
