package org.egov.garbageservice.repository.builder;

import org.springframework.stereotype.Component;

@Component
public class GrbgCollectionUnitQueryBuilder {

    public static final String CREATE_QUERY = "INSERT INTO eg_grbg_collection_unit " +
                                              "(uuid, unit_name, unit_ward, ulb_name, type_of_ulb, is_active " 
                                              + ", garbage_id, unit_type, category, sub_category, sub_category_type"
                                              + ", isbplunit,isvariablecalculation,isbulkgeneration,no_of_units, ismonthlybilling )"
                                              + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String UPDATE_QUERY = "UPDATE eg_grbg_collection_unit " +
                                              "SET unit_name = ?, unit_ward = ?, ulb_name = ?, type_of_ulb = ? " 
                                              + ", garbage_id = ?, unit_type = ?, category = ?, sub_category = ?, sub_category_type = ?, is_active = ? "
                                              + ",isbplunit = ? , isbulkgeneration = ? , isvariablecalculation = ? , no_of_units = ?,ismonthlybilling = ?"
                                              + " WHERE uuid = ?";
    
    public static final String DELETE_QUERY = "DELETE FROM eg_grbg_collection_unit WHERE garbage_id = ?";
}
