package org.upyog.as.core.validator;

import org.upyog.as.model.payload.ModuleData;

public interface Validator {

	public default Boolean validatePropertyTaxRequest(ModuleData data) {
		return true;
	}

	public default Boolean validateTradeLicenseRequest(ModuleData data) {
		return true;
	}

	public default Boolean validateMiscCollectionRequest(ModuleData data) {
		return true;
	}

	public default Boolean validateRequest(ModuleData data, String type) {
		switch (type) {
		case "PT":
			return validatePropertyTaxRequest(data);
		case "TL":
			return validateTradeLicenseRequest(data);
		case "MSC":
			return validateMiscCollectionRequest(data);
		default:
			return false;
		}
	}
}
