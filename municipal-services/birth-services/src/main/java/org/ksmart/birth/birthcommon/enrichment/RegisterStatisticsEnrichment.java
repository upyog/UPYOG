package org.ksmart.birth.birthcommon.enrichment;

import org.ksmart.birth.birthregistry.model.RegisterBirthStatiticalInformation;
import org.ksmart.birth.common.services.MdmsTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RegisterStatisticsEnrichment {
    private final MdmsTenantService mdmsTenantService;
    @Autowired
    RegisterStatisticsEnrichment(MdmsTenantService mdmsTenantService) {
        this.mdmsTenantService = mdmsTenantService;
    }
    public void setRegisterStatistics(RegisterBirthStatiticalInformation registerStatistics, Object mdmsData, String tenantId) {
        registerStatistics.setMotherResdnceTenentId(tenantId);
        registerStatistics.setMotherResdnceCountryId(mdmsTenantService.getTenantCountry(mdmsData, tenantId));
        registerStatistics.setMotherResdnceStateId(mdmsTenantService.getTenantState(mdmsData, tenantId));
        registerStatistics.setMotherResdnceLbType(mdmsTenantService.getTenantLbType(mdmsData, tenantId));
        registerStatistics.setMotherResdnceDistrictId(mdmsTenantService.getTenantLbType(mdmsData, tenantId));

    }

}
