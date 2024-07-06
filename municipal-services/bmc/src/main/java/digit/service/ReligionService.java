package digit.service;

import org.springframework.stereotype.Service;

import digit.repository.ReligionRepositoty;
import digit.web.models.Religion;
import digit.web.models.SchemeApplicationRequest;

@Service
public class ReligionService {

    private ReligionRepositoty religionRepositoty;

    public Religion saveReligion (SchemeApplicationRequest schemeApplicationRequest){

        Religion religion = new Religion();
        religion.setId(schemeApplicationRequest.getId());
        religion.setName(schemeApplicationRequest.getName());
        religion.setDescription(schemeApplicationRequest.getDescription());
        return  religionRepositoty.save(religion);
    }

}
