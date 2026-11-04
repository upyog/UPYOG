package org.upyog.adv.web.models.events;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Setter
@ToString
@Builder
public class EventDetails {

	private String id;

	private String eventId;

	private Long fromDate;

	private Long toDate;

	private BigDecimal latitude;

	private BigDecimal longitude;

	private String address;

	public boolean isEmpty(EventDetails details) {
		return null == details.getFromDate() || null == details.getToDate() || null == details.getLatitude()
				|| null == details.getLongitude();
	}

}
