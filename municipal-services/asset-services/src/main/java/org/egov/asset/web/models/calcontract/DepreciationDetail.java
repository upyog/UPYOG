package org.egov.asset.web.models.calcontract;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepreciationDetail {

    @Column(name = "id")
    private Long id;

    @Column(name = "asset_id", nullable = false)
    private String assetId; // Foreign key to asset service

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "to_date", nullable = false)
    private LocalDate toDate;

    @Column(name = "depreciation_value", nullable = false)
    private Double depreciationValue;

    @Column(name = "book_value", nullable = false)
    private Double bookValue;

    @Column(name = "rate", nullable = false)
    private double rate;

    @Column(name = "old_book_value", nullable = false, columnDefinition = "double precision default 0.0")
    private Double oldBookValue;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at", updatable = false, nullable = false)
    private Timestamp createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "updated_by", nullable = false)
    private String updatedBy;

    @Column(name = "depreciationMethod")
    private String depreciationMethod;
}