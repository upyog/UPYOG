package org.ksmart.birth.web.controller.NewBirth;

import org.ksmart.birth.newbirth.repository.NewBirthRedisRepository;
import org.ksmart.birth.web.model.newbirth.NewBirthPartial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cr/partial")
public class NewBirthRedisController {
    @Autowired
    private NewBirthRedisRepository repo;
    @PostMapping
    public NewBirthPartial save(@RequestBody NewBirthPartial request) {
        return repo.save(request);
    }

    @GetMapping
    public Iterable<NewBirthPartial> getAllProducts() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public NewBirthPartial findById(@PathVariable String id) {
        return repo.findById(id).get();
    }
    @GetMapping("/getbyuid/{uid}")
    public List<NewBirthPartial> findByUUid(@PathVariable String uid) {
        List<NewBirthPartial> newBirthPartial = new ArrayList<>();
        repo.findAllByUserUUidEquals(uid).forEach(newBirthPartial::add);
        System.out.println(newBirthPartial.size());
        return newBirthPartial;
    }
    @GetMapping("/getbystatus/{status}")
    public NewBirthPartial findByStatus(@PathVariable String status) {
        return repo.findNewBirthPartialByApplicationStatusEquals(status);
    }
    @DeleteMapping("/{id}")
    public void remove(@PathVariable String id)   {
        NewBirthPartial newBirthPartial = repo.findById(id).get();
        repo.delete(newBirthPartial);
    }

}
