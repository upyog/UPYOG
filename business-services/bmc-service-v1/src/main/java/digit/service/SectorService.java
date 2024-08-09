package digit.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import digit.bmc.model.Sector;
import digit.repository.SectorRepository;
import digit.web.models.SchemeApplicationRequest;

@Service
public class SectorService {
    @Autowired
	private SectorRepository sectorRepository;
	private static final Logger logger = LoggerFactory.getLogger(SectorService.class);
	public Sector saveSector(SchemeApplicationRequest schemeApplicationRequest) {
		
		Sector sector = schemeApplicationRequest.getSchemeApplications().get(0).getSector();
		Sector sectors = new Sector();
		sectors.setId(sector.getId());
		sectors.setSector(sector.getSector());
		sectors.setRemark(sector.getRemark());
		sectors.setCreatedOn(sector.getCreatedOn());
		sectors.setCreatedBy(sector.getCreatedBy());
		logger.info("Saving UserSchemeApplication: {}", sector);
		return sectorRepository.save(sector);
		
	}

}
