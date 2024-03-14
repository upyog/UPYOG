package org.egov.commons.service;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.egov.commons.repository.CVoucherHeaderRepository;
import org.egov.commons.utils.EntityType;
import org.egov.infra.validation.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.egov.infstr.services.PersistenceService;



@Service
@Transactional
public class CVouvherHeaderServiece implements EntityTypeService{
	
	@Autowired
    @Qualifier("persistenceService")
    private PersistenceService persistenceService;
    
    @PersistenceContext
    private EntityManager entityManager; 
    
    @Autowired
    private CVoucherHeaderRepository cVoucherHeaderRepository;
    
    List<BigInteger> getMissingData(){
    List<BigInteger> list= cVoucherHeaderRepository.findMissingVoucherHeaders();
    return list;
    }

	@Override
	public List<? extends EntityType> getAllActiveEntities(Integer accountDetailTypeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends EntityType> filterActiveEntities(String filterKey, int maxRecords,
			Integer accountDetailTypeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List getAssetCodesForProjectCode(Integer accountdetailkey) throws ValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends EntityType> validateEntityForRTGS(List<Long> idsList) throws ValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends EntityType> getEntitiesById(List<Long> idsList) throws ValidationException {
		// TODO Auto-generated method stub
		return null;
	}

}
