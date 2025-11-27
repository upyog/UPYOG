package org.egov.ptr.models;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class BreedType {

	String name;
	String code;
	String active;
	String petType;
	BigDecimal newapplication;
	BigDecimal renewapplication;
	String feeType;
}
