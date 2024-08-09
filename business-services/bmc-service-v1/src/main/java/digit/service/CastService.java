package digit.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import digit.bmc.model.Caste;
import digit.repository.CasteRepository;
import digit.web.models.SchemeApplicationRequest;

@Service
public class CastService {

  @Autowired
  private CasteRepository casteRepository;
  private static final Logger logger = LoggerFactory.getLogger(CastService.class);
  public Caste getCastByApplication(SchemeApplicationRequest schemeApplicationRequest) {
    Caste caste = schemeApplicationRequest.getSchemeApplications().get(0).getCaste();
    Caste castes = new Caste();
    castes.setId(caste.getId());
    castes.setName(caste.getName());
    castes.setDescription(caste.getDescription());
    castes.setCreatedOn(caste.getCreatedOn());
    castes.setCreatedBy(caste.getCreatedBy());
    logger.info("Saving UserSchemeApplication: {}", castes);
    return casteRepository.save(castes);  
  }

}
