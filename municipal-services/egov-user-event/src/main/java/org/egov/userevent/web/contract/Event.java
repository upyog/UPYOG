package org.egov.userevent.web.contract;

import java.util.Comparator;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.egov.userevent.model.AuditDetails;
import org.egov.userevent.model.RecepientEvent;
import org.egov.userevent.model.enums.Source;
import org.egov.userevent.model.enums.Status;
import org.egov.userevent.validation.SanitizeHtml;
import org.springframework.validation.annotation.Validated;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Validated
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Setter
@ToString
@Builder
public class Event {

	@NotNull
	@SanitizeHtml
	private String tenantId;

	@SanitizeHtml
	private String id;

	@SanitizeHtml
	private String referenceId;

	@NotNull
	@SanitizeHtml
	private String eventType;

	@SanitizeHtml
	private String eventCategory;

	@NotNull
	@SanitizeHtml
	@Size(max = 65)
	private String name;

	@NotNull
	@SanitizeHtml
	@Size(max = 500)
	private String description;

	private Status status;

	@NotNull
	private Source source;

	@SanitizeHtml
	private String postedBy;

	private Recepient recepient;

	private Action actions;

	private EventDetails eventDetails;

	private AuditDetails auditDetails;

	private List<RecepientEvent> recepientEventMap;

	private Boolean generateCounterEvent;
	
	private Boolean internallyUpdted;

	

	/**
	 * Comparator for fromDate based sorting of EVENTSONGROUND
	 * 
	 * @return
	 */
	public static Comparator<Event> getFromDateComparatorForEvents() {
		return new Comparator<Event>() {
			@Override
			public int compare(Event o1, Event o2) {
				return o1.getEventDetails().getFromDate().compareTo(o2.getEventDetails().getFromDate());
			}
		};
	}

	/**
	 * Comparator for createdDate based sorting of all the events.
	 * 
	 * @return
	 */
	public static Comparator<Event> getCreatedDateComparator() {
		return new Comparator<Event>() {
			@Override
			public int compare(Event o1, Event o2) {
				return o2.getAuditDetails().getCreatedTime().compareTo(o1.getAuditDetails().getCreatedTime());
			}
		};
	}

}
