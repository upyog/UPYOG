package org.egov.vehicle.util;

import lombok.Getter;

@Getter
public enum ModuleNameEnum {
    FSM("FSM"),
    WATER_TANKER("WT");

    private final String moduleName;

    ModuleNameEnum(String moduleName) {
        this.moduleName = moduleName;
    }
}
