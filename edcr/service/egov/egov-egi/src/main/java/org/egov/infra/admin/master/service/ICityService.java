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
 * Introduced during the Java 17 / Spring 5.3 upgrade so that servlet filters
 * and other components can inject the city service via its interface type.
 * This avoids Spring proxy type-mismatch errors when {@code CityService} is
 * wrapped by a JDK dynamic proxy or CGLIB subclass.
 * </p>
 *
 * @see org.egov.infra.admin.master.service.CityService
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