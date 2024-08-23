package org.egov.advertisementcanopy.repository.builder;

import org.springframework.stereotype.Component;

@Component
public class SiteApplicationQueryBuilder {

	public static final String CREATE_QUERY = "INSERT INTO eg_site_application "
			+ "(id, uuid,site_id,site_name,site_description,gps_location,site_address,site_cost,site_photograph,structure,size_length,size_width,led_selection,security_amount,powered,others,district_name,ulb_name,ulb_type,ward_number,pincode,additional_detail,created_by,created_date,last_modified_by,last_modified_date,site_type,account_id) "
			+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	public static final String UPDATE_QUERY = "UPDATE eg_site_application "
			+ "SET application_no = ?, status = ?, garbage_id = ? " + "WHERE uuid = ?";

}
