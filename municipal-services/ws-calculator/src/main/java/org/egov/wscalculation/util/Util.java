package org.egov.wscalculation.util;

import java.util.Objects;

import org.egov.tracer.model.CustomException;
import org.egov.wscalculation.constants.WSCalculationConstant;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class Util {

	@Autowired
	private ObjectMapper mapper;

    public JsonNode getJsonValue(PGobject pGobject){
        try {
            if(Objects.isNull(pGobject) || Objects.isNull(pGobject.getValue()))
                return null;
            else
                return mapper.readTree( pGobject.getValue());
        } catch (Exception e) {
        	throw new CustomException(WSCalculationConstant.EG_WS_CAL_JSON_EXCEPTION_KEY, WSCalculationConstant.EG_WS_CAL_JSON_EXCEPTION_MSG);
        }
    }


	
}