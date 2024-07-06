package digit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import digit.bmc.model.Divyang;
import digit.repository.DivyangRepository;
import digit.web.models.SchemeApplicationRequest;

@Service
public class DivyangService {
    @Autowired
    private DivyangRepository divyangRepository;

    public Divyang getBDivyangByApplication(SchemeApplicationRequest  request) {
        Divyang divyang = new  Divyang();

        divyang.setId(request.getId());
        divyang.setName(request.getName());
        divyang.setDescription(request.getDescription());
        divyang.setCreatedBy(request.getCreatedBy());
        divyang.setModifiedBy(request.getModifiedby());
        return  divyangRepository.save(divyang);
    }

}
