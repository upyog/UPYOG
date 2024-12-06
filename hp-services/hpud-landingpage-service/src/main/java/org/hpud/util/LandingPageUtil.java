package org.hpud.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hpud.model.AbstractAuditable;

public class LandingPageUtil {
	public void applyAuditing(AbstractAuditable auditable, Long createdBy) {
		Date currentDate = new Date();
		if (auditable.isNew()) {
			auditable.setCreatedBy(createdBy);
			auditable.setCreatedDate(currentDate);
		}
		auditable.setLastModifiedBy(createdBy);
		auditable.setLastModifiedDate(currentDate);
	}

	public Long getUserId() {
		return 1L;
	}

	public String getDateinFormat(Date date, String format) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);

	}

}
