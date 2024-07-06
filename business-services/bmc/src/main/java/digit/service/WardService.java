package digit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import digit.repository.WardRepository;
import digit.web.models.SchemeApplicationRequest;
import digit.web.models.Ward;

@Service
public class WardService {
    @Autowired
    private WardRepository repository;

    public Ward saveWard (SchemeApplicationRequest request){

        Ward ward = new Ward();
         ward.setId(request.getId());
         ward.setCityName(request.getCityName());
         ward.setWardName(request.getWardName());
         ward.setRemark(request.getRemark());
        return  repository.save(ward);
    }
}
