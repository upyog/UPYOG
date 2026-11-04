package org.upyog.dx.upyogdata.pfms.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * Parent batch row for Kafka and {@code ug_pfms_data} persistence.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PfmsDataEntity {

    /** Correlation id; maps to {@code ug_pfms_data.id}. */
    private String id;
    private String state;
    private String ward;
    private String module;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;

    private String ulb;
    private String targetDestination;
    private List<PfmsTransactionEntity> transactions;
}
