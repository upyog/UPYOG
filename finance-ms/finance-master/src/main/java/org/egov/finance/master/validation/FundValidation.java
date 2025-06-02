/**
 * Created on Jun 2, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.master.validation;

import org.egov.finance.master.entity.Fund;
import org.egov.finance.master.model.FundModel;
import org.springframework.stereotype.Component;

@Component
public class FundValidation {
	
	public Fund  modelToEntity(FundModel model) {
		
		Fund f = new Fund();
		f.setId(model.getId()!=null ?model.getId():null);
		f.setIdentifier(null!=model.getIdentifier()?model.getIdentifier():null);
		f.setIsactive(null!=model.getIsactive() && model.getIsactive() ?true:false);
		f.setName(null!=model.getName()?model.getName():null);
		f.setIsnotleaf(model.getIsnotleaf()?true:false);
		f.setCode(null!=model.getCode()?model.getCode():null);
		return f;
	}
	
public 	FundModel  entityTOModel(Fund entity) {
		FundModel f = new FundModel();
		f.setId(entity.getId()!=null ?entity.getId():null);
		f.setIdentifier(null!=entity.getIdentifier()?entity.getIdentifier():null);
		f.setIsactive(entity.getIsactive()?true:false);
		f.setName(null!=entity.getName()?entity.getName():null);
		f.setIsnotleaf(entity.getIsnotleaf()?true:false);
		f.setCode(null!=entity.getCode()?entity.getCode():null);
		f.setCreatedby(null!=entity.getCreatedby()?entity.getCreatedby():null);
		f.setCreatedDate(null!=entity.getCreatedDate()?entity.getCreatedDate():null);
		f.setLastModifiedBy(null!=entity.getLastModifiedBy()?entity.getLastModifiedBy():null);
		f.setLastModifiedDate(null!=entity.getLastModifiedDate()?entity.getLastModifiedDate():null);
		f.setParentId(null!=entity.getParentId()?entity.getParentId().getId():null);
		return f;
	}

}

