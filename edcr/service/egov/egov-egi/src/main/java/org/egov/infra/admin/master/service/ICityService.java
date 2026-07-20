package org.egov.infra.admin.master.service;

import java.util.List;
import java.util.Map;

import org.egov.infra.admin.master.entity.City;

/**
 * Service interface for managing City master data.
 *
 * <p>
 * Defines operations for retrieving, updating, and managing city-related
 * information used across the application. This includes fetching city
 * details by code, name, or URL, retrieving state-city mappings, obtaining
 * tenant-specific city information, and accessing city logo resources.
 * </p>
 *
 * <p>
 * Implementations of this interface provide the business logic for city
 * administration and tenant-specific city configuration management.
 * </p>
 */
public interface ICityService {

    City updateCity(City city);
    City getCityByURL(String url);
    City getCityByName(String cityName);
    City getCityByCode(String code);
    City fetchStateCityDetails();
	Map<String, Object> cityDataAsMap();
	List<City> findAll();
	Object getCityLogoURLByCurrentTenant();
	byte[] getCityLogoAsBytes();
	
}