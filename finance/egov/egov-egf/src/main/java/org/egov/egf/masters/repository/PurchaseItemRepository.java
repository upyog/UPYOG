package org.egov.egf.masters.repository;

import java.util.List;

import org.egov.model.bills.EgBillPurchaseItemsDetails;
import org.egov.model.masters.PurchaseItems;
import org.egov.model.masters.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseItemRepository extends JpaRepository<PurchaseItems, Long>{
	
	List<PurchaseItems> findByPurchaseOrder(PurchaseOrder purchaseOrder);

	/*
	 * @Query(value = "SELECT * FROM citya.egf_purchaseorder pi " +
	 * "INNER JOIN citya.egf_purchaseitems pt ON pi.ordernumber = pt.ordernumber " +
	 * "WHERE pi.name = :name", nativeQuery = true) List<PurchaseItems>
	 * findByPurchaseOrder(@Param("name") String name);
	 */
	
//	@Query(value = "SELECT * FROM citya.egf_purchaseitems pi " +          
//            "WHERE pi.ordernumber = :purchaseOrderId", nativeQuery = true)
//	List<PurchaseItems> findbyPurchaseOrderId(@Param("purchaseOrderId") String purchaseOrderId);
	
	@Query("from PurchaseItems where orderNumber=:orderNumber")
    public List<PurchaseItems> findbyOrderNumber(@Param("orderNumber") String orderNumber);

	PurchaseItems findByItemCode(String itemCode);

	PurchaseItems findByQuantity(int quantity);



	



	
	//void saveAndFlush(List<PurchaseItems> purchaseItems);

	//void saveAndFlush(List<EgBillPurchaseItemsDetails> egBillPurchaseItemsDetails);
	

}
