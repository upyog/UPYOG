package org.upyog.reconciliation.model;

import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ug_recon_extraction_details")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class ReconExtractionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "config_id", nullable = false)
    private ReconConfiguration configuration;

    @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "extraction_date", nullable = false)
    private LocalDate extractionDate;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "acknowledged")
    private Boolean acknowledged = false;

    @Type(type = "jsonb")
    @Column(name = "data_payload", columnDefinition = "jsonb")
    private String dataPayload;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
