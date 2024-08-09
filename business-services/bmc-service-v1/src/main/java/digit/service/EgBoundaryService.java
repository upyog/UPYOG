package digit.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import digit.repository.EgBoundaryRepository;
import digit.web.models.EgBoundary;
import digit.web.models.SchemeApplicationRequest;

@Service
public class EgBoundaryService {

    private static final Logger log = LoggerFactory.getLogger(EgBoundaryService.class);

	private final EgBoundaryRepository egBoundaryRepository;

	public EgBoundaryService(EgBoundaryRepository egBoundaryRepository) {
		this.egBoundaryRepository = egBoundaryRepository;
	}
	
	public int saveEgBoundary(SchemeApplicationRequest schemeApplicationRequest) {
		EgBoundary egBoundary = schemeApplicationRequest.getSchemeApplications().get(0).getEgBoundary();
		EgBoundary egBoundary2 = new EgBoundary();
		egBoundary2.setBndryid(egBoundary.getBndryid());
		egBoundary2.setBndry_name_old(egBoundary.getBndry_name_old());
		egBoundary2.setBndry_name_old_local(egBoundary.getBndry_name_old_local());
		egBoundary2.setBoundarynum(egBoundary.getBoundarynum());
		egBoundary2.setBoundarytype(egBoundary.getBoundarytype());
		egBoundary2.setCode(egBoundary.getCode());
		egBoundary2.setVersion(egBoundary.getVersion());
		egBoundary2.setTodate(egBoundary.getTodate());
		egBoundary2.setTenantid(egBoundary.getTenantid());
		egBoundary2.setParent(egBoundary.getParent());
		egBoundary2.setName(egBoundary.getName());
		egBoundary2.setMaterializedpath(egBoundary.getMaterializedpath());
		egBoundary2.setLongitude(egBoundary.getLongitude());
		egBoundary2.setLocalname(egBoundary.getLocalname());
		egBoundary2.setLatitude(egBoundary.getLatitude());
		egBoundary2.setIshistory(egBoundary.getIshistory());
		egBoundary2.setId(egBoundaryRepository.getMaxId() + 1);
		egBoundary2.setFromdate(egBoundary.getFromdate());
		egBoundary2.setCreateddate(egBoundary.getCreateddate());
        log.info("save boundry data: {}", egBoundary2);
		return egBoundaryRepository.save(egBoundary2);
		
	}
	


}
