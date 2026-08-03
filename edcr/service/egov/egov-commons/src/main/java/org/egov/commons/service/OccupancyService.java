package org.egov.commons.service;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.entity.bpa.Occupancy;
import org.egov.common.entity.bpa.SubOccupancy;
import org.egov.common.entity.bpa.Usage;
import org.egov.commons.repository.bpa.OccupancyRepository;
import org.egov.commons.repository.bpa.SubOccupancyRepository;
import org.egov.commons.repository.bpa.UsagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OccupancyService {

    @Autowired
    private OccupancyRepository occupancyRepository;
    @Autowired
    private SubOccupancyRepository subOccupancyRepository;

    @Autowired
    private UsagesRepository usagesRepository;

    public Occupancy findById(final Long id) {
        return occupancyRepository.findById(id).orElse(null);
    }

    public List<Occupancy> findAll() {
        return occupancyRepository.findAll();
    }

    public List<Occupancy> findAllByActive() {
        return occupancyRepository.findByIsactiveTrueOrderByOrderNumberAsc();
    }

    /*
    public List<Occupancy> findAllOrderByOrderNumber() {
        return occupancyRepository.findAll(Sort.by(Sort.Direction.ASC, "orderNumber"));
    }
    */

    public List<Usage> findSubUsagesByOccupancy(final String occupancyName) {
        Occupancy occupancy = occupancyRepository.findByName(occupancyName);
        List<SubOccupancy> list = null;
        List<Usage> usagesList = new ArrayList<>();
        if (occupancy != null) {
            list = subOccupancyRepository.findByOccupancyAndIsActiveTrueOrderByOrderNumberAsc(occupancy);
            for (SubOccupancy subOccupancy : list) {
                List<Usage> usages = usagesRepository
                        .findBySubOccupancyAndIsActiveTrueOrderByOrderNumberAsc(subOccupancy);
                usagesList.addAll(usages);
            }
        } else {
            SubOccupancy subOccupancy;
            if (list == null) {
                subOccupancy = subOccupancyRepository.findByName(occupancyName);
                List<Usage> usages = usagesRepository
                        .findBySubOccupancyAndIsActiveTrueOrderByOrderNumberAsc(subOccupancy);
                usagesList.addAll(usages);
            }
        }

        return usagesList;
    }
}
