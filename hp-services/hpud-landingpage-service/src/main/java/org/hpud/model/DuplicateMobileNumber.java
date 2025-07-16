package org.hpud.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DuplicateMobileNumber {
    private Long count;
    private String mobileNumber;

}
