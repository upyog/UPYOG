package org.egov.vendor.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ModuleNameEnum {


    FSM("FSM"),
    WATER_TANKER("WT");

    private final String moduleName;

    ModuleNameEnum(String moduleName) {
        this.moduleName = moduleName;
    }

    /*
    Ensures when Jackson converts the enum to JSON, it outputs FSM or WT instead of FSM("FSM") or WATER_TANKER("WT").
     */
    @JsonValue
    public String getModuleName() {
        return moduleName;
    }

    /*
    Allows Jackson to map JSON "WT" → ModuleNameEnum.WATER_TANKER by matching the moduleName field.
     */
    @JsonCreator
    public static ModuleNameEnum fromValue(String value) {
        for (ModuleNameEnum module : ModuleNameEnum.values()) {
            if (module.moduleName.equalsIgnoreCase(value)) {
                return module;
            }
        }
        throw new IllegalArgumentException("Invalid ModuleNameEnum: " + value);
    }


}
