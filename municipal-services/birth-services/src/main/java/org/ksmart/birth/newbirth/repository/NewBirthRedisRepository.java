package org.ksmart.birth.newbirth.repository;

import org.ksmart.birth.web.model.newbirth.NewBirthPartialRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NewBirthRedisRepository {
    public static final String HASH_KEY = "CRBRNR";
    @Autowired
    private RedisTemplate template;

    public NewBirthPartialRequest save(NewBirthPartialRequest product){
        template.opsForHash().put(HASH_KEY,product.getId(),product);
        return product;
    }

    public List<NewBirthPartialRequest> findAll(){
        return template.opsForHash().values(HASH_KEY);
    }

    public NewBirthPartialRequest findProductById(int id){
        return (NewBirthPartialRequest) template.opsForHash().get(HASH_KEY,id);
    }


    public String deleteProduct(int id){
        template.opsForHash().delete(HASH_KEY,id);
        return "product removed !!";
    }
}
