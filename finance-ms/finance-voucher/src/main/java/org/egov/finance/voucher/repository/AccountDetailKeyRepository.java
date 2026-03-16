package org.egov.finance.voucher.repository;

import java.util.List;
import java.util.Optional;

import org.egov.finance.voucher.entity.AccountDetailKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountDetailKeyRepository extends JpaRepository<AccountDetailKey, Long>, JpaSpecificationExecutor<AccountDetailKey> {

    @Query("from AccountDetailKey where accountDetailType.id = :detailtypeid and upper(detailname) like upper(:detailname)")
    List<AccountDetailKey> findByDetailTypeIdAndDetailNameLikeIgnoreCase(@Param("detailtypeid") Integer typeid,
                                                                          @Param("detailname") String detailname);

    @Query("from AccountDetailKey where accountDetailType.id = :detailtypeid")
    List<AccountDetailKey> findByDetailType(@Param("detailtypeid") Integer typeid);

    boolean existsByDetailkey(Integer detailkey);  // match exactly 'detailkey'

    boolean existsByAccountDetailType_IdAndDetailkey(Integer detailtypeid, Integer detailkey); // nested property query
    
//    Optional<AccountDetailKey> findByAccountdetailtypeIdAndDetailkey(Integer detailtypeId, Integer detailkey);
    
    Optional<AccountDetailKey> findByAccountDetailType_IdAndDetailkey(Integer accountDetailTypeId, Integer detailkey);
}