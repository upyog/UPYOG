package com.mseva.gisintegration.validator;

import com.mseva.gisintegration.model.Property;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class PropertyValidator {

    public boolean isValid(Property property) {
        if (property == null) {
            return false;
        }
        if (!StringUtils.hasText(property.getPropertyid())) {
            return false;
        }
        if (!StringUtils.hasText(property.getLocalitycode())) {
            return false;
        }
        return true;
    }
}
