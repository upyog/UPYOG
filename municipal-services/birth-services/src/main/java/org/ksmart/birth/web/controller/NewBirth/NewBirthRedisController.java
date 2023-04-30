package org.ksmart.birth.web.controller.NewBirth;

import org.ksmart.birth.newbirth.repository.NewBirthRedisRepository;
import org.ksmart.birth.web.model.newbirth.NewBirthPartialRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/cr/partial")
public class NewBirthRedisController {
    @Autowired
    private NewBirthRedisRepository repo;
    @PostMapping
    public NewBirthPartialRequest save(@RequestBody NewBirthPartialRequest product) {
        return repo.save(product);
    }

    @GetMapping
    public List<NewBirthPartialRequest> getAllProducts() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public NewBirthPartialRequest findProduct(@PathVariable int id) {
        return repo.findProductById(id);
    }
    @DeleteMapping("/{id}")
    public String remove(@PathVariable int id)   {
        return repo.deleteProduct(id);
    }

}
