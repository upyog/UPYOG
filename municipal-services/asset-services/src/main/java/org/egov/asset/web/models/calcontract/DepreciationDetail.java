package org.egov.asset.web.models.calcontract;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @Column(name = "rate", nullable = false)
    private double rate;

    @Column(name = "old_book_value", nullable = false, columnDefinition = "double precision default 0.0")
    private Double oldBookValue;
}