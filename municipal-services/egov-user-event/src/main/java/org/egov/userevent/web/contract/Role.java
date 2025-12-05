package org.egov.userevent.web.contract;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"code", "tenantId"})
@ToString
@NoArgsConstructor
//This class is serialized to Redis
public class Role implements Serializable {
    private static final long serialVersionUID = 2090518436085399889L;
    private String name;
    private String code;
    private String tenantId;
}
