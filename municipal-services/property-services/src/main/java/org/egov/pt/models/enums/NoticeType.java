package org.egov.pt.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum NoticeType {
	

	
	REC_MIS_DEF_RET("RECTIFICATION OF MISTAKES IN A DEFECTIVE RETURN");
  


  private String value;

  NoticeType(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

	@JsonCreator
	public static NoticeType fromValue(String text) {
		for (NoticeType b : NoticeType.values()) {
			if (String.valueOf(b.value).equalsIgnoreCase(text)) {
				return b;
			}
		}
		return null;
	}


}
