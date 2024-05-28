package com.example.hpgarbageservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hpgarbageservice.model.GarbageBill;
import com.example.hpgarbageservice.repository.GarbageBillRepository;

@Service
public class GarbageBillService {

    @Autowired
    private GarbageBillRepository repository;

    public GarbageBill create(GarbageBill bill) {
        return repository.create(bill);
    }

    public GarbageBill update(GarbageBill bill) {
        repository.update(bill);
        return bill;
    }

    public GarbageBill findById(Long id) {
        return repository.findById(id);
    }

}
