package digit.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import digit.web.models.EgBoundary;

@Repository
public class EgBoundaryRepository {

    @Autowired
	private JdbcTemplate jdbcTemplate;
	
	public int save(EgBoundary egBoundary) {
        String sql = "INSERT INTO public.eg_boundary (id, boundarynum, parent, name, boundarytype, localname, " +
                "bndry_name_old, bndry_name_old_local, fromdate, todate, bndryid, longitude, latitude, materializedpath, " +
                "ishistory, createddate, lastmodifieddate, createdby, lastmodifiedby, version, tenantid, code) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        return jdbcTemplate.update(sql, egBoundary.getId(), egBoundary.getBoundarynum(), egBoundary.getParent(), 
                egBoundary.getName(), egBoundary.getBoundarytype(), egBoundary.getLocalname(), 
                egBoundary.getBndry_name_old(), egBoundary.getBndry_name_old_local(), egBoundary.getFromdate(), 
                egBoundary.getTodate(), egBoundary.getBndryid(), egBoundary.getLongitude(), egBoundary.getLatitude(), 
                egBoundary.getMaterializedpath(), egBoundary.getIshistory(), egBoundary.getCreateddate(), 
                egBoundary.getLastmodifieddate(), egBoundary.getCreatedby(), egBoundary.getLastmodifiedby(), 
                egBoundary.getVersion(), egBoundary.getTenantid(), egBoundary.getCode());
    }
	
	
	public Long getMaxId() {
        String sql = "SELECT COALESCE(MAX(id), 0) FROM public.eg_boundary";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

}
