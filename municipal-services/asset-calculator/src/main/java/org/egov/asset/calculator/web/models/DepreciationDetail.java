package org.egov.asset.calculator.web.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.javers.core.metamodel.annotation.Entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "asset_depreciation_details")
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
}