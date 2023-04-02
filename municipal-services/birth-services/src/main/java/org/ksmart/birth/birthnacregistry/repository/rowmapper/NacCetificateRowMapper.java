package org.ksmart.birth.birthnacregistry.repository.rowmapper;

import org.ksmart.birth.birthnacregistry.model.RegisterCertificateData;
import org.ksmart.birth.birthregistry.repository.RegisterBirthRepository;
import org.ksmart.birth.birthregistry.service.EnrichmentService;
import org.ksmart.birth.birthregistry.service.MdmsDataService;
import org.ksmart.birth.utils.MdmsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.ksmart.birth.utils.BirthConstants.*;
@Component
public class NacCetificateRowMapper  implements ResultSetExtractor<List<RegisterCertificateData>> {
	
	 private final MdmsUtil mdmsUtil;
	    private final MdmsDataService mdmsDataService;

	    @Autowired
	    NacCetificateRowMapper( MdmsUtil mdmsUtil, MdmsDataService mdmsDataService) {
	        this.mdmsUtil = mdmsUtil;
	        this.mdmsDataService = mdmsDataService;
	    }

	    @Override
	    public List<RegisterCertificateData> extractData(ResultSet rs) throws SQLException, DataAccessException {
	        List<RegisterCertificateData> result = new ArrayList<>();
	        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
	        while (rs.next()) {
	            Date regDate = new Date(rs.getLong("registration_date"));
	            Date dobDate = new Date(rs.getLong("dateofbirth"));
	            result.add(RegisterCertificateData.builder()
	                    .id(rs.getString("id"))
	                    .dobStr(formatter.format(dobDate))
	                    .registrationDateStr(formatter.format(regDate))

	                    .fullName(getFullNameEn(rs))
	                    .fullNameMl(getFullNameMl(rs))
	                    .ackNo(rs.getString("ack_no"))
	                    .tenantId(rs.getString("tenantid"))	                     
	                    
	                     
	                    .fatherDetails(rs.getString("father_name"))
	                    .motherDetails(rs.getString("mother_name"))
	                    .fatherDetailsMl(rs.getString("father_name_ml"))
	                    .motherDetailsMl(rs.getString("mother_name_ml"))
	                    .registrationNo(rs.getString("registrationno"))
	                    .registrationDate(rs.getLong("registration_date"))
	                    .currentDate(formatter.format(new Date()).toString())
	                    .registarDetails(getRegistar(rs))
	                    .placeDetails(getPlaceDetailsEn(rs))
	                    .placeDetailsMl(getPlaceDetailsMl(rs))
	                   


	                    .build());
	        }
	        return result;
	    }
	    
	    private String getFullNameEn(ResultSet rs) throws SQLException {
	        String fullName = (rs.getString("firstname_en") == null ? "" : rs.getString("firstname_en") + ' ') +
	                (rs.getString("middlename_en") == null ? "" : rs.getString("middlename_en") + ' ') +
	                (rs.getString("lastname_en") == null ? "" : rs.getString("lastname_en"));
	        return fullName;
	    }

	    private String getFullNameMl(ResultSet rs) throws SQLException {
	        String fullName = (rs.getString("firstname_ml") == null ? "" : rs.getString("firstname_ml") + ' ') +
	                (rs.getString("middlename_ml") == null ? "" : rs.getString("middlename_ml") + ' ') +
	                (rs.getString("lastname_ml") == null ? "" : rs.getString("lastname_ml"));
	        return fullName;
	    }

	    
	    private String getRegistar(ResultSet rs) throws SQLException {
	        String reg = new StringBuilder()
	                .append(rs.getString("esign_user_desig_code") == null ? "" : rs.getString("firstname_ml")+" ")
	                .append(rs.getString("tenantid")).toString();
	        return reg;
	    }
	    
	    private String getPlaceDetailsEn(ResultSet rs) throws SQLException {
	        String address = "";
	        if(rs.getString("placeofbirthid") != null) {
	            if (rs.getString("placeofbirthid").contains(BIRTH_PLACE_HOSPITAL)) {
	                address = new StringBuilder()
	                        .append(rs.getString("hospitalid") == null ? "" : rs.getString("hospitalid")).toString();
	            } else if (rs.getString("placeofbirthid").contains(BIRTH_PLACE_INSTITUTION)) {
	                address = new StringBuilder()
	                        .append(rs.getString("institution_id") == null ? "" : rs.getString("institution_id")).toString();
	            } else if (rs.getString("placeofbirthid").contains(BIRTH_PLACE_HOME)) {
	                address = new StringBuilder()
	                        .append(rs.getString("ho_housename_en") == null ? "" : rs.getString("ho_housename_en") + ',')
	                        .append(rs.getString("ho_locality_en") == null ? "" : rs.getString("ho_locality_en") + ',')
	                        .append(rs.getString("ho_street_name_en") == null ? "" : rs.getString("ho_street_name_en") + ',')
	                        .append(rs.getString("ho_poid") == null ? "" : rs.getString("ho_poid") + ',')
	                        .append(rs.getString("ho_pinno") == null ? "" : rs.getString("ho_pinno") + ',')
	                        .append(rs.getString("ho_districtid") == null ? "" : rs.getString("ho_districtid") + ',')
	                        .append(rs.getString("ho_stateid") == null ? "" : rs.getString("ho_stateid") + ',')
	                        .append(rs.getString("ho_countryid") == null ? "" : rs.getString("ho_countryid")).toString();
	            } else if (rs.getString("placeofbirthid").contains(BIRTH_PLACE_VEHICLE)) {

	            } else if (rs.getString("placeofbirthid").contains(BIRTH_PLACE_PUBLIC)) {

	            } else if (rs.getString("placeofbirthid").contains(BIRTH_PLACE_OTHERS_COUNTRY)) {

	            } else {
	                address = "";
	            }
	        }
	        return address;
	    }

	    private String getPlaceDetailsMl(ResultSet rs) throws SQLException {
	        String address = "";
	        if(rs.getString("placeofbirthid") != null) {
	            if (rs.getString("placeofbirthid").contains(BIRTH_PLACE_HOSPITAL)) {
	                address = new StringBuilder()
	                        .append(rs.getString("hospitalid") == null ? "" : rs.getString("hospitalid") + ',').toString();
	            } else if (rs.getString("placeofbirthid").contains(BIRTH_PLACE_INSTITUTION)) {

	            } else if (rs.getString("placeofbirthid").contains(BIRTH_PLACE_HOME)) {
	                address = new StringBuilder()
	                        .append(rs.getString("ho_housename_ml") == null ? "" : rs.getString("ho_housename_ml") + ',')
	                        .append(rs.getString("ho_locality_en") == null ? "" : rs.getString("ho_locality_en") + ',')
	                        .append(rs.getString("ho_street_name_en") == null ? "" : rs.getString("ho_street_name_en") + ',')
	                        .append(rs.getString("ho_poid") == null ? "" : rs.getString("ho_poid") + "_ML" + ',')
	                        .append(rs.getString("ho_pinno") == null ? "" : rs.getString("ho_pinno") + ',')
	                        .append(rs.getString("ho_districtid") == null ? "" : rs.getString("ho_districtid") + "_ML" + ',')
	                        .append(rs.getString("ho_stateid") == null ? "" : rs.getString("ho_stateid") + "_ML" + ',')
	                        .append(rs.getString("ho_countryid") == null ? "" : rs.getString("ho_countryid") + "_ML").toString();
	            } else if (rs.getString("placeofbirthid").contains(BIRTH_PLACE_VEHICLE)) {

	            } else if (rs.getString("placeofbirthid").contains(BIRTH_PLACE_PUBLIC)) {

	            } else if (rs.getString("placeofbirthid").contains(BIRTH_PLACE_OTHERS_COUNTRY)) {
	                //address = new StringBuilder()
	            } else {
	                address = "";
	            }
	        }
	        return address;

	    }
}
