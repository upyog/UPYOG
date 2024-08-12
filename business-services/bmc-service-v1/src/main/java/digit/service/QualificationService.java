package digit.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import digit.repository.BmcUserRepository;
import digit.repository.QualificationRepository;
import digit.repository.UserQualificationRepository;
import digit.web.models.EgBmcUserQualification;
import digit.web.models.Qualification;
import digit.web.models.SchemeApplicationRequest;

@Service
public class QualificationService {
    
	private static final Logger logger = LoggerFactory.getLogger(QualificationService.class);
    private final  QualificationRepository qualificationRepository;
    private final BmcUserRepository bmcUserRepository;
    private final UserQualificationRepository userqualificationRepositpry;
    
    @Autowired
    public QualificationService(QualificationRepository qualificationRepository, BmcUserRepository bmcUserRepository,UserQualificationRepository userqualificationRepositpry) {
		this.qualificationRepository = qualificationRepository;
		this.bmcUserRepository = bmcUserRepository;
		this.userqualificationRepositpry = userqualificationRepositpry;
	}

    public Qualification saveQualification(SchemeApplicationRequest schemeApplicationRequest) {
        Qualification qualifications = schemeApplicationRequest.getSchemeApplications().get(0).getQualification();
        Qualification qualification = new Qualification();

        qualification.setId(qualifications.getId());
        qualification.setQualification(qualifications.getQualification());
        qualification.setBoard(qualifications.getBoard());
        qualification.setPassingYear(qualifications.getPassingYear());
        qualification.setPercentage(qualifications.getPercentage());
        logger.info("Saving UserSchemeApplication: {}", qualification);

        int result = qualificationRepository.save(qualification);
        if (result > 0) {
            logger.info("Qualification saved successfully");
            Long maxId = qualificationRepository.getMaxId();

            int userQualification = saveUserWithQualifications(schemeApplicationRequest, maxId);
//            if (userQualification != null) {
//                return qualification;
//            } else {
//                logger.error("Error saving UserQualification association");
//                return null;
//            }
        } else {
            logger.error("Error saving Qualification");
            return null;
        }
		return qualification;
    }

    public int saveUserWithQualifications(SchemeApplicationRequest schemeApplicationRequest, Long qualificationId) {
        EgBmcUserQualification userQualification = new EgBmcUserQualification();
        userQualification.setId(schemeApplicationRequest.getId());
        userQualification.setUserID(schemeApplicationRequest.getRequestInfo().getUserInfo().getId());
        userQualification.setQualificatioID(qualificationId);
        
        return userqualificationRepositpry.save(userQualification);
    }


}
