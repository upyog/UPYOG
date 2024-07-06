package digit.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import digit.web.models.EgBmcUserQualification;
@Repository
public class UserQualificationRepository {

    @Autowired
	private JdbcTemplate jdbcTemplate;
	
	public int save(EgBmcUserQualification userQualification) {
        String sql = "INSERT INTO public.eg_bmc_userqualification (id, \"userID\", \"qualificatioID\", createdon, modifiedon, createdby, modifiedby) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        return jdbcTemplate.update(sql,
                userQualification.getId(),
                userQualification.getUserID(),
                userQualification.getQualificatioID(),
                userQualification.getCreatedOn(),
                userQualification.getModifiedOn(),
                userQualification.getCreatedBy(),
                userQualification.getModifiedBy()
        );
    }
}
