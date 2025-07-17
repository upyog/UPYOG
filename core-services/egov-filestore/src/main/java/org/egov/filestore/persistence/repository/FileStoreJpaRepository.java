package org.egov.filestore.persistence.repository;

import org.egov.filestore.persistence.entity.Artifact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
public interface FileStoreJpaRepository extends JpaRepository<Artifact, Long> {
	Artifact findByFileStoreIdAndTenantId(String fileStoreId, String tenantId);

	List<Artifact> findByTagAndTenantId(String tag, String tenantId);
	
	@Query(value = "SELECT * FROM eg_filestoremap T WHERE T.tenantId = (?1) AND T.fileStoreId IN (?2)",nativeQuery = true)
	List<Artifact> findByTenantIdAndFileStoreIdList(String tenantId, List<String> fileStoreIds);
	
	
	//value = "SELECT * FROM table WHERE property=(?1)", nativeQuery = true
	
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE  eg_filestoremap  SET alfresco_id = :alfresco_id WHERE tenantId = :tenantId AND fileStoreId = :fileStoreIds",nativeQuery = true)
	void updateFileStoreWithAlfrescoIds(@Param("alfresco_id") String alfrescoId,@Param("tenantId") String tenantId ,@Param("fileStoreIds")String fileStoreIds);
	
	
	@Transactional
	@Modifying
	@Query(value = "DELETE from  eg_filestoremap   WHERE tenantId = :tenantId AND fileStoreId in :fileStoreIds",nativeQuery = true)
	void deleteFileStoreMap(@Param("tenantId") String tenantId ,@Param("fileStoreIds" ) Set<String> fileStoreIds);
}
