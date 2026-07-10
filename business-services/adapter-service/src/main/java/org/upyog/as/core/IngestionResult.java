package org.upyog.as.core;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IngestionResult {
	private String ingestionStatus;
	private String responseData;
	private String failureReason;
	private long ingestedAt;
}