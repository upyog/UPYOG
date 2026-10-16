package org.upyog.dx.upyogdata.pfms.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PFMSData {

    private String id;
    private String state;
    private String ward;
    private String module;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;

    private String ulb;
    private String targetDestination;

    @NotEmpty(message = "Transactions cannot be empty")
    @Valid
    private List<PFMSTransaction> transactions;
}
