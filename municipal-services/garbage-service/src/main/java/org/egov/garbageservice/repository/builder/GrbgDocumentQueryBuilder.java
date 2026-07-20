package org.egov.garbageservice.repository.builder;

import org.springframework.stereotype.Component;

/** Holds SQL query constants for inserting and updating garbage document records. */
@Component
public class GrbgDocumentQueryBuilder {

    public static final String CREATE_QUERY = "INSERT INTO eg_grbg_document " +
                                              "(uuid, document_uid, file_store_id, document_type, tbl_ref_uuid, garbage_id) " +
                                              "VALUES (?, ?, ?, ?, ?, ?)";

    public static final String UPDATE_QUERY = "UPDATE eg_grbg_document " +
                                              "SET document_uid = ?, file_store_id = ?, document_type = ?, tbl_ref_uuid = ?, garbage_id = ? " +
                                              "WHERE uuid = ?";
}