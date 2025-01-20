package org.egov.asset.calculator.web.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Table(name = "eg_asset_depreciation_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DepreciationDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "depreciationMethod")
    private String depreciationMethod;

    @Column(name = "rate", nullable = false)
    private double rate;

    @Column(name = "old_book_value", nullable = false, columnDefinition = "double precision default 0.0")
    private Double oldBookValue;

    @Column(name = "created_at", updatable = false, nullable = false)
    private Long createdAt;

    @Column(name = "updated_at")
    private Long updatedAt;

    @Column(name = "created_by", updatable = false, nullable = false)
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

}