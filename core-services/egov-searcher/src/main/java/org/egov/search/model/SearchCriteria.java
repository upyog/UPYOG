/**
 * 
 */
package org.egov.search.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {

	private String module;
	private String plotMapId;
	private String town;
	private String searchName;

}
