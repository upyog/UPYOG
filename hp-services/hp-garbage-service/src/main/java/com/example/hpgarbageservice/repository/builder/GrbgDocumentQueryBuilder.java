package com.example.hpgarbageservice.repository.builder;

import org.springframework.stereotype.Component;

@Component
public class GrbgDocumentQueryBuilder {

    public static final String CREATE_QUERY = "INSERT INTO eg_grbg_document " +
                                              "(uuid, doc_ref_id, doc_name, doc_type, doc_category, tbl_ref_uuid) " +
                                              "VALUES (?, ?, ?, ?, ?, ?)";

    public static final String UPDATE_QUERY = "UPDATE eg_grbg_document " +
                                              "SET doc_ref_id = ?, doc_name = ?, doc_type = ?, doc_category = ?, tbl_ref_uuid = ? " +
                                              "WHERE uuid = ?";
}
