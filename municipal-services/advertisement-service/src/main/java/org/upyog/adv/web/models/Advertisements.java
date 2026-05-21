package org.upyog.adv.web.models;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
    public class Advertisements {
        private Integer id;
        private String poleNo;
        private String name;
        private String adType;
        private Integer width;
        private Integer height;
        private String imageSrc;
        private String light;
        private BigDecimal amount;
        private Boolean available;
        private String locationCode;
        private String feeType;
        private boolean taxApplicable;
    }
