package org.ksmart.birth.utils.enums;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CertificateStatus {
	    ACTIVE("ACTIVE"),
	    CANCELLED("CANCELLED"),
	    FREE_DOWNLOAD("FREE_DOWNLOAD"),
	    PAID_DOWNLOAD("PAID_DOWNLOAD"),
	    PAID_PDF_GENERATED("PAID_PDF_GENERATED"),
	    PAID("PAID");

	    private String value;

	    @Override
	    @JsonValue
	    public String toString() {
	        return String.valueOf(value);
	    }

	    @JsonCreator
	    public static CertificateStatus fromValue(final String value) {
	        return Arrays.stream(CertificateStatus.values())
	                     .filter(ty -> String.valueOf(ty)
	                                         .equalsIgnoreCase(value))
	                     .findFirst()
	                     .orElse(null);
	    }

}
