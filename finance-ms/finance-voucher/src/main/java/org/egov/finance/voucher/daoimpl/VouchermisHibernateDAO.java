package org.egov.finance.voucher.daoimpl;

import java.util.List;


import org.egov.finance.voucher.entity.CVoucherHeader;
import org.egov.finance.voucher.repository.VoucherRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class VouchermisHibernateDAO {
	
	@Autowired
	private VoucherRepository voucherRepository;

	@PersistenceContext
	private EntityManager entityManager;

	public Session getCurrentSession() {
		return entityManager.unwrap(Session.class);
	}
	
	
//	 public List<CVoucherHeader> getRecentVoucherByServiceNameAndReferenceDoc(String serviceName,String referenceDocument){
//	        StringBuilder builderQuery = new StringBuilder("select vh from Vouchermis vmis,CVoucherHeader vh where vmis.voucherheaderid=vh.id");
//	        if(!StringUtils.isBlank(serviceName)){
//	            builderQuery.append(" and vmis.serviceName=:serviceName ");
//	        }
//	        builderQuery.append(" and vmis.referenceDocument=:referenceDocument ");
//	        builderQuery.append(" order by vh.createdDate desc ");
//	        final Query qry = getCurrentSession().createQuery(builderQuery.toString());
//	        if(!StringUtils.isBlank(serviceName)){
//	            qry.setString("serviceName", serviceName);
//	        }
//	        qry.setString("referenceDocument", referenceDocument);
//	        List<CVoucherHeader> list = qry.list();
//	        return list.isEmpty() ? null : list ;
//	    }
	 
	 
	public List<CVoucherHeader> getRecentVoucherByServiceNameAndReferenceDoc1(String serviceName, String referenceDocument) {
	    List<CVoucherHeader> vouchers = voucherRepository.findRecentVouchers(
	        StringUtils.hasText(serviceName) ? serviceName : null, 
	        referenceDocument
	    );
	    return vouchers == null || vouchers.isEmpty() ? null : vouchers;
	}

}
