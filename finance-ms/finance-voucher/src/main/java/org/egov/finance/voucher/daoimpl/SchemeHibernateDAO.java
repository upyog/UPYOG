package org.egov.finance.voucher.daoimpl;

import org.egov.finance.voucher.dao.SchemeDAO;
import org.egov.finance.voucher.model.SchemeModel;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

//@Repository
public class SchemeHibernateDAO implements SchemeDAO {
	
//	 @Transactional
//	    public Scheme update(final SchemeModel entity) {
//	        getCurrentSession().update(entity);
//	        return entity;
//	    }
//
//	    @Transactional
//	    public Scheme create(final Scheme entity) {
//	        getCurrentSession().persist(entity);
//	        return entity;
//	    }
//
//	    @Transactional
//	    public void delete(Scheme entity) {
//	        getCurrentSession().delete(entity);
//	    }
//
//	    public Scheme findById(Number id, boolean lock) {
//	        return (Scheme) getCurrentSession().load(Scheme.class, id);
//	    }
//
//	    public List<Scheme> findAll() {
//	        return (List<Scheme>) getCurrentSession().createCriteria(Scheme.class).list();
//	    }
//
//	    @PersistenceContext
//	    private EntityManager entityManager;
//
//	    public Session getCurrentSession() {
//	        return entityManager.unwrap(Session.class);
//	    }
//
//	   
//
//	    @Override
//	    public Scheme getSchemeById(final Integer id) {
//	        final Query query = getCurrentSession().createQuery("from Scheme s where s.id=:schemeid");
//	        query.setInteger("schemeid", id);
//	        return (Scheme) query.uniqueResult();
//	    }
//
//	    @Override
//	    public Scheme getSchemeByCode(final String code) {
//	        final Query query = getCurrentSession().createQuery("from Scheme s where s.code=:code");
//	        query.setString("code", code);
//	        return (Scheme) query.uniqueResult();
//	    }
	
	

}
