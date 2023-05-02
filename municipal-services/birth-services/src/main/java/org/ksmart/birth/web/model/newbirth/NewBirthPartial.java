package org.ksmart.birth.web.model.newbirth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.deser.Deserializers;
import lombok.*;
import org.springframework.core.serializer.DefaultSerializer;
import org.springframework.core.serializer.Deserializer;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;


import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@RedisHash("NewBirthPartial")
public class NewBirthPartial implements Serializable {
   @Id
   private String  applicationNumber;

    @Indexed
    private String userUUid;

    private String applicationStatus;

    @JsonProperty("ChildDetails")
    private NewBirthApplication ChildDetails;

    public NewBirthPartial addBirthPartialDetails(NewBirthApplication birthDetail) {
        if (birthDetail == null) {
            birthDetail = null;
        }
        return this;
    }
}

