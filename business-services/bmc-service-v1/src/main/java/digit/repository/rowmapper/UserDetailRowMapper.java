package digit.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.egov.common.contract.models.Address;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import digit.bmc.model.AadharUser;
import digit.bmc.model.Caste;
import digit.bmc.model.Divyang;
import digit.bmc.model.UserOtherDetails;
import digit.web.models.BankDetails;
import digit.web.models.Religion;
import digit.web.models.user.DivyangDetails;
import digit.web.models.user.DocumentDetails;
import digit.web.models.user.QualificationDetails;
import digit.web.models.user.UserAddressDetails;
import digit.web.models.user.UserDetails;

@Component
public class UserDetailRowMapper implements ResultSetExtractor<List<UserDetails>> {

    @Override
    public List<UserDetails> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, UserDetails> userDetailsMap = new LinkedHashMap<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        Set<String> columns = new HashSet<>();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            columns.add(rsmd.getColumnName(i).toLowerCase());
        }

        while (rs.next()) {
            String userId = rs.getString("userid");
            UserDetails userDetails = userDetailsMap.get(userId);
            if (userDetails == null) {
                Caste caste = null;
                if (columns.contains("casteid") && columns.contains("caste") && rs.getObject("casteid") != null
                        && rs.getString("caste") != null) {
                    caste = Caste.builder()
                            .id(rs.getLong("casteid"))
                            .name(rs.getString("caste"))
                            .build();
                }

                Religion religion = null;
                if (columns.contains("religionid") && columns.contains("religion") && rs.getObject("religionid") != null
                        && rs.getString("religion") != null) {
                    religion = Religion.builder()
                            .id(rs.getLong("religionid"))
                            .name(rs.getString("religion"))
                            .build();
                }


                UserAddressDetails address = null;
                if (columns.contains("citytownvillage") && columns.contains("pincode")) {
                    address = UserAddressDetails.builder()
                            .id(rs.getLong("id"))
                            .areaLocalitySector(rs.getString("arealocalitysector"))
                            .cityTownVillage(rs.getString("citytownvillage"))
                            .district(rs.getString("district"))
                            .landmark(rs.getString("landmark"))
                            .houseNoBldgApt(rs.getString("housenobldgapt"))
                            .subDistrict(rs.getString("subdistrict"))
                            .postOffice(rs.getString("postoffice"))
                            .country(rs.getString("country"))
                            .streetRoadLine(rs.getString("streetroadline"))
                            .pincode(rs.getString("pincode"))
                            .state(rs.getString("state"))
                            .build();
                }

                DivyangDetails divyangDetails = null;
                if (columns.contains("divyangpercent") && columns.contains("divyangtype")
                        && columns.contains("divyangcardid")) {
                    Divyang divyang = Divyang.builder()
                            .id(rs.getLong("divyangid"))
                            .name(rs.getString("divyangtype"))
                            .build();
                    divyangDetails = DivyangDetails.builder()
                            .divyangpercent(rs.getDouble("divyangpercent"))
                            .divyangcardid(rs.getString("divyangcardid"))
                            .divyangtype(divyang).build();

                }
                String[] result = splitSalutation(rs.getString("aadharname"));
                AadharUser aadharUser = null;
                if (columns.contains("aadhardob") && columns.contains("aadharfathername")
                        && columns.contains("aadharmobile") && columns.contains("aadharname")
                        && columns.contains("gender")) {
                    aadharUser = AadharUser.builder()
                            .aadharRef(rs.getString("aadharref"))
                            .aadharName(result[1])
                            .aadharDob(rs.getDate("aadhardob"))
                            .aadharFatherName(rs.getString("aadharfathername"))
                            .aadharMobile(rs.getString("aadharmobile"))
                            .gender(rs.getString("gender"))
                            .build();
                }

                UserOtherDetails uod = UserOtherDetails.builder()
                        .caste(caste)
                        .religion(religion)
                        .transgenderId(rs.getString("transgenderid"))
                        .block(rs.getString("block"))
                        .zone(rs.getString("zone"))
                        .ward(rs.getString("ward"))
                        .build();
                userDetails = UserDetails.builder()
                        .title(result[0])
                        .aadharUser(aadharUser)
                        .userOtherDetails(uod)
                        .address(address)
                        .userID(columns.contains("userid") ? rs.getLong("userid") : null)
                        .bankDetail(new ArrayList<>())
                        .documentDetails(new ArrayList<>())
                        .divyang(divyangDetails)
                        .qualificationDetails(new ArrayList<>())
                        .build();

                userDetailsMap.put(userId, userDetails);
            }

            // Add criteria details to the scheme
            if (columns.contains("documentid")) {
                Long documentID = rs.getLong("documentid");
                if (documentID != null) {
                    DocumentDetails documentDetails = userDetails.getDocumentDetails().stream()
                            .filter(c -> c.getDocumentId().equals(documentID))
                            .findFirst()
                            .orElse(null);

                    if (documentDetails == null) {
                        documentDetails = DocumentDetails.builder()
                                .documentId(documentID)
                                .documentName(rs.getString("documentName"))
                                .available(rs.getBoolean("available"))
                                .build();

                        userDetails.getDocumentDetails().add(documentDetails);
                    }
                }
            }

            if (columns.contains("qualificationid")) {
                Long qualificationid = rs.getLong("qualificationid");
                if (qualificationid != null) {
                    QualificationDetails qualificationDetails = userDetails.getQualificationDetails().stream()
                            .filter(c -> c.getQualificationId().equals(qualificationid))
                            .findFirst()
                            .orElse(null);

                    if (qualificationDetails == null) {
                        qualificationDetails = QualificationDetails.builder()
                                .qualificationId(qualificationid)
                                .qualification(rs.getString("qualification"))
                                .board(rs.getString("board"))
                                .percentage(rs.getLong("percentage"))
                                .yearOfPassing(rs.getLong("year_of_passing"))
                                .build();
                        userDetails.getQualificationDetails().add(qualificationDetails);
                    }
                }
            }

            if (columns.contains("ifsc") && columns.contains("accountnumber")) {
                String bank = Objects.requireNonNullElse(rs.getString("ifsc"), "")
                        .concat(Objects.requireNonNullElse(rs.getString("accountnumber"), ""));
                if (bank != null) {
                    BankDetails bankDetails = userDetails.getBankDetail().stream()
                            .filter(c -> Objects.requireNonNullElse(c.getIfsc(), "")
                                    .concat(Objects.requireNonNullElse(c.getAccountNumber(), "")).equals(bank))
                            .findFirst()
                            .orElse(null);

                    if (bankDetails == null) {
                        bankDetails = BankDetails.builder()
                                .branchId(rs.getLong("bankbranchid"))
                                .accountNumber(rs.getString("accountnumber"))
                                .branchName(rs.getString("branchname"))
                                .name(rs.getString("bankname"))
                                .ifsc(rs.getString("ifsc"))
                                .micr(rs.getString("micr"))
                                .build();
                        userDetails.getBankDetail().add(bankDetails);
                    }
                }
            }
        }
        return new ArrayList<>(userDetailsMap.values());
    }

    public static String[] splitSalutation(String fullName) {
        if (fullName == null || !fullName.contains(".")) {
            return new String[]{"", fullName != null ? fullName.trim() : ""};
        }
        String[] parts = fullName.split("\\.", 2); 
        String salutation = parts[0].trim(); 
        String name = parts.length > 1 ? parts[1].trim() : ""; 
        return new String[]{salutation, name};
    }
}