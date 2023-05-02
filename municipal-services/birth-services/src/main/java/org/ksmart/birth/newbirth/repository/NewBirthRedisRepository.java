package org.ksmart.birth.newbirth.repository;

import org.apache.kafka.common.protocol.types.Field;
import org.ksmart.birth.web.model.newbirth.NewBirthPartial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface NewBirthRedisRepository extends CrudRepository<NewBirthPartial, String> {
//    @Override
//    Optional<NewBirthPartial> findById(String s);

    List<NewBirthPartial> findAllByUserUUidEquals(String uid);

    NewBirthPartial findNewBirthPartialByApplicationStatusEquals(String status);
    String delete(Optional<NewBirthPartial> newBirthPartial);

}

   // public static final String HASH_KEY = "CRBRNR";
//    @Autowired
//    private RedisTemplate template;
//    Student retrievedStudent =
//            studentRepository.findById("Eng2015001").get();
//    public NewBirthPartial save(NewBirthPartial request){
//        template.opsForHash().put(HASH_KEY,request.getApplicationNumber(),request);
//        return request;
//    }
//
//    public List<NewBirthPartial> findAll(){
//        return template.opsForHash().values(HASH_KEY);
//    }
//
//    public NewBirthPartial findProductById(String id){
//
//        return  (NewBirthPartial) template.opsForHash().get(HASH_KEY,id);
//    }
//
////    public NewBirthPartial findProductByUid(String userId){
////        return (NewBirthPartial) template.opsForHash().get(HASH_KEY,userId);
////    }
////
//
//    public String deleteProduct(String id){
//        template.opsForHash().delete(HASH_KEY,id);
//        return "product removed !!";
//    }
//}
