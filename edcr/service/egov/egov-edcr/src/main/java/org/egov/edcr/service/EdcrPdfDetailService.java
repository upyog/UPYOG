package org.egov.edcr.service;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.egov.common.entity.edcr.EdcrPdfDetail;
import org.egov.edcr.repository.EdcrPdfDetailRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EdcrPdfDetailService {

    @Autowired
    private EdcrPdfDetailRepository edcrPdfDetailRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }

    public void save(org.egov.edcr.entity.EdcrPdfDetail edcrPdfDetail) {
        edcrPdfDetailRepository.save(edcrPdfDetail);
    }

    public void saveAll(List<org.egov.edcr.entity.EdcrPdfDetail> edcrPdfDetails) {
        edcrPdfDetailRepository.saveAll(edcrPdfDetails);
    }

    public List<org.egov.edcr.entity.EdcrPdfDetail> findByDcrApplicationId(Long applicationDetailId) {
        return edcrPdfDetailRepository.findByEdcrApplicationDetailId(applicationDetailId);
    }


}
