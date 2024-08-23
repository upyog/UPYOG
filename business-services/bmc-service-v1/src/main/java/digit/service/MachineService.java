package digit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import digit.bmc.model.Machines;
import digit.repository.MachinesRepository;
import digit.web.models.SchemeApplicationRequest;
@Service
public class MachineService {
    
    private final  MachinesRepository machinesRepository;
    @Autowired
    public MachineService(MachinesRepository machinesRepository) {
        this.machinesRepository = machinesRepository;
    }

}
