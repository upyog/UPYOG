-- =============================================================================
-- Migration: V20260720161500__remove_user_id_ulb_name_from_ingestion_detail
-- Description: Removes redundant columns user_id and ulb_name from ingestion_detail table.
-- =============================================================================

ALTER TABLE ingestion_detail DROP COLUMN IF EXISTS ulb_name;
ALTER TABLE ingestion_detail DROP COLUMN IF EXISTS user_id;
