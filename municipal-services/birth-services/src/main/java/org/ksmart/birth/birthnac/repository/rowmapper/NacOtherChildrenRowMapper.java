package org.ksmart.birth.birthnac.repository.rowmapper;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.ksmart.birth.web.model.birthnac.NacOtherChildren;

public interface NacOtherChildrenRowMapper {
	default NacOtherChildren getOtherChildren (ResultSet rs) throws SQLException {
		return NacOtherChildren.builder()
				.childNameEn(rs.getString("ebcb_child_name_en"))  
				.childNameMl(rs.getString("ebcb_child_name_ml")) 
				.sex(rs.getString("ebcb_sex")) 
				.orderOfBirth(rs.getInt("ebcb_order_of_birth")) 
				.dob(rs.getLong("ebcb_dob")) 
				.isAlive(rs.getBoolean("ebcb_is_alive")) 
				.build();
	}

}
