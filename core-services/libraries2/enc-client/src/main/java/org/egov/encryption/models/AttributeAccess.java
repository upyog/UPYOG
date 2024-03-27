package org.egov.encryption.models;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttributeAccess {

	private Attribute attribute = null;

	private AccessType accessType = null;
	private Visibility firstLevelVisibility = null;

	private Visibility secondLevelVisibility = null;

}
