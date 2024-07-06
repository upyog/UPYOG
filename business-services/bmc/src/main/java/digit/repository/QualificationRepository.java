package digit.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import digit.web.models.Qualification;

@Repository
public class QualificationRepository {
    @Autowired
	private JdbcTemplate jdbcTemplate;
	public int save(Qualification qualificationDetails) {
        String sql = "INSERT INTO public.eg_bmc_qualification_details (" +
                     "qualification, passingyear, percentage, bord, createdon, modifiedon, createdby, modifiedby" +
                     ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        return jdbcTemplate.update(sql, 
            qualificationDetails.getQualification(),
            qualificationDetails.getPassingYear(),
            qualificationDetails.getPercentage(),
            qualificationDetails.getBord(),
            qualificationDetails.getCreatedOn(),
            qualificationDetails.getModifiedOn(),
            qualificationDetails.getCreatedBy(),
            qualificationDetails.getModifiedBy()
        );
    }
	
	public Long getMaxId() {
        String sql = "SELECT MAX(id) FROM public.eg_bmc_qualification_details";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }


}
