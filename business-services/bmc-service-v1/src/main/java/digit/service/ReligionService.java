package digit.service;

import org.springframework.stereotype.Service;

import digit.repository.ReligionRepositoty;

@Service
public class ReligionService {

    private ReligionRepositoty religionRepositoty;

   /*  public Religion saveReligion (SchemeApplicationRequest schemeApplicationRequest){

        Religion religion = new Religion();
        religion.setId(schemeApplicationRequest.getId());
        religion.setName(schemeApplicationRequest.getName());
        religion.setDescription(schemeApplicationRequest.getDescription());
        return  religionRepositoty.save(religion);
    }  */

}
