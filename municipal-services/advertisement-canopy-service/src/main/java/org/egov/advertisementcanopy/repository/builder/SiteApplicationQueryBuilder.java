package org.egov.advertisementcanopy.repository.builder;

import org.springframework.stereotype.Component;

@Component
public class SiteApplicationQueryBuilder {

	public static final String CREATE_QUERY = "INSERT INTO eg_site_application "
			+ "(id, uuid,site_id,site_name,site_description,gps_location,site_address,site_cost,site_photograph,structure,size_length,size_width,led_selection,security_amount,powered,others,district_name,ulb_name,ulb_type,ward_number,pincode,additional_detail,created_by,created_date,last_modified_by,last_modified_date,site_type,account_id) "
			+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	public static final String UPDATE_QUERY = "UPDATE eg_site_application "
			+ "SET site_name = ?, site_description = ?, gps_location = ?,site_address = ?,site_cost = ?,site_photograph = ?,structure = ?,size_length = ?,size_width = ?,led_selection = ?,security_amount = ?,powered =?,others =?,district_name = ?,ulb_name = ?,ulb_type = ?,ward_number = ?,pincode = ?,additional_detail = ?,created_by =?,created_date =?,last_modified_by = ?,last_modified_date = ?,site_type = ?,account_id = ?"
			+ "WHERE uuid = ? and id=?";

	public static final String SEARCH_QUERY_FOR_SITE_UPDATE = "SELECT * FROM eg_site_application"
			+ "WHERE id=? AND uuid=? AND site_id=? ";
	

	public static final String SEARCH_EXISTING_SITE="SELECT * FROM eg_site_application" + " WHERE site_name = ? AND district_name =? AND ulb_name = ? AND ward_number = ?";
}
