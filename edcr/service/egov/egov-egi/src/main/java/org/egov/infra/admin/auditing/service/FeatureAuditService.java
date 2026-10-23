
//TODO: This class is currently not in use. It is intended to be used for fetching the audit details of feature role change.
// The implementation will be done in future when the requirement arises.

//package org.egov.infra.admin.auditing.service;
//
//import org.egov.infra.admin.auditing.contract.FeatureRoleChangeAuditReportRequest;
//import org.egov.infra.admin.master.entity.Feature;
//import org.egov.infra.admin.master.repository.FeatureRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.history.Revision;
//import org.springframework.stereotype.Service;
//
//@Service
//public class FeatureAuditService {
//
//    @Autowired
//    private FeatureRepository featureRepository;
//
//    public Page<Revision<Integer, Feature>> getFeatureRoleChangeAudit(FeatureRoleChangeAuditReportRequest featureRoleChangeAuditReportRequest) {
//        final Pageable pageable =  PageRequest.of(featureRoleChangeAuditReportRequest.pageNumber(),
//                featureRoleChangeAuditReportRequest.pageSize(),
//                featureRoleChangeAuditReportRequest.orderDir(), featureRoleChangeAuditReportRequest.orderBy());
//        //return featureRepository.findRevisions(featureRoleChangeAuditReportRequest.getFeatureId(), pageable);
//        return new org.springframework.data.domain.PageImpl<>(java.util.Collections.emptyList());
//    }
//}
