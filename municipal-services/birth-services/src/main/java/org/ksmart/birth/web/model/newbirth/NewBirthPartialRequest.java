package org.ksmart.birth.web.model.newbirth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("CRBRNR")
public class NewBirthPartialRequest {
    @Id
    private int id;
    private String name;
    private int qty;
    private long price;
}

