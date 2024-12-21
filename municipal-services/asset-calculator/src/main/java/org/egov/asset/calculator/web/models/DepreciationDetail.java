package org.egov.asset.calculator.web.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "eg_asset_depreciation_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepreciationDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long assetId; // Foreign key to asset service
    private LocalDate fromDate;
    private LocalDate toDate;
    private Double depreciationValue;
    private Double bookValue;
    private LocalDate updatedAt;
    @Column(name = "rate")
    private double rate;
    private Double oldBookValue;
}