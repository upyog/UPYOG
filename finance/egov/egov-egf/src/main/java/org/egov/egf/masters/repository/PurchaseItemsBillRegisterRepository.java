package org.egov.egf.masters.repository;

import java.util.List;



import org.egov.model.bills.EgBillPurchaseItemsDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseItemsBillRegisterRepository extends JpaRepository<EgBillPurchaseItemsDetails, Long>{
	
	void save(List<EgBillPurchaseItemsDetails> egBillPurchaseItemsDetailsList);
	
	@Query(value="select * from eg_billregisterwithpurchaseitems where billregister_id=:billregister_id",nativeQuery = true)
	List<EgBillPurchaseItemsDetails> findAllByBillRegister(@Param("billregister_id") String billregister_id);

	

}
