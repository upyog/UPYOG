package digit.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import digit.web.models.scheme.CourseDetails;
import digit.web.models.scheme.CriteriaDetails;
import digit.web.models.scheme.EventDetails;
import digit.web.models.scheme.MachineDetails;
import digit.web.models.scheme.SchemeDetails;
import digit.web.models.scheme.SchemeHeadDetails;

@Component
public class SchemeRowMapper implements ResultSetExtractor<List<EventDetails>> {
    @Override
    public List<EventDetails> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, EventDetails> eventDetailsMap = new LinkedHashMap<>();
        while (rs.next()) {
            String eventName = rs.getString("eventname");
            EventDetails eventDetails = eventDetailsMap.get(eventName);
            if (eventDetails == null) {
                eventDetails = EventDetails.builder()
                        .eventName(rs.getString("eventName"))
                        .startDate(rs.getDate("startDate"))
                        .endDate(rs.getDate("enddate"))
                        .schemeshead(new ArrayList<>())
                        .build();
                eventDetailsMap.put(eventName, eventDetails);
            }
            String schemeHeadName = rs.getString("schemeHead");
            SchemeHeadDetails schemeHeadDetails = eventDetails.getSchemeshead().stream()
                    .filter(sh -> sh.getSchemeHead().equals(schemeHeadName))
                    .findFirst()
                    .orElse(null);

            if (schemeHeadDetails == null) {
                schemeHeadDetails = SchemeHeadDetails.builder()
                        .schemeHead(schemeHeadName)
                        .schemeheadDesc(rs.getString("schemeHeadDesc"))
                        .schemeDetails(new ArrayList<>()) // Initialize schemes list
                        .build();
                eventDetails.getSchemeshead().add(schemeHeadDetails);
            }

            Long schemeID = rs.getLong("SchemeID");
            SchemeDetails schemeDetails = schemeHeadDetails.getSchemeDetails().stream()
                    .filter(s -> s.getSchemeID().equals(schemeID))
                    .findFirst()
                    .orElse(null);

            if (schemeDetails == null) {
                schemeDetails = SchemeDetails.builder()
                        .schemeID(schemeID)
                        .schemeDesc(rs.getString("SchemeDescription"))
                        .schemeName(rs.getString("SchemeName"))
                        .criteria(new ArrayList<>()) // Initialize criteria list
                        .courses(new ArrayList<>())
                        .machines(new ArrayList<>())
                        .build();
                schemeHeadDetails.getSchemeDetails().add(schemeDetails);
            }

            // Add criteria details to the scheme
            Long criteriaID = rs.getLong("criteriaID");
            if (criteriaID != 0) { // Check if course details are present
                CriteriaDetails criteriaDetails = schemeDetails.getCriteria().stream()
                        .filter(c -> c.getCriteriaID().equals(criteriaID))
                        .findFirst()
                        .orElse(null);

                if (criteriaDetails == null) {
                    criteriaDetails = CriteriaDetails.builder()
                            .criteriaID(criteriaID)
                            .criteriaCondition(rs.getString("criteriacondition"))
                            .criteriaType(rs.getString("criteriatype"))
                            .criteriaValue(rs.getString("criteriavalue"))
                            .build();

                    schemeDetails.getCriteria().add(criteriaDetails);
                }
            }
            Long courseID = rs.getLong("courseid");
            if (courseID != 0) { // Check if course details are present
                CourseDetails courseDetails = schemeDetails.getCourses().stream()
                        .filter(c -> c.getCourseID().equals(courseID))
                        .findFirst()
                        .orElse(null);

                if (courseDetails == null) {
                    courseDetails = CourseDetails.builder()
                            .courseID(courseID)
                            .courseName(rs.getString("coursename"))
                            .courseDesc(rs.getString("coursedesc"))
                            .courseDuration(rs.getString("courseDuration"))
                            .coursestartdate(rs.getDate("coursestartdate"))
                            .courseenddate(rs.getDate("courseenddate"))
                            .courseUrl(rs.getString("courseurl"))
                            .courseInstitute(rs.getString("courseInstitute"))
                            .courseImageUrl(rs.getString("courseimageurl"))
                            .instituteAddress(rs.getString("instututeAddress"))
                            .courseAmount(rs.getDouble("courseAmount"))
                            .build();
                    schemeDetails.getCourses().add(courseDetails);

                }
            }

            Long machID = rs.getLong("machID");
            if (machID != 0) { // Check if course details are present
                MachineDetails machDetails = schemeDetails.getMachines().stream()
                        .filter(c -> c.getMachID().equals(machID))
                        .findFirst()
                        .orElse(null);

                if (machDetails == null) {
                    machDetails = MachineDetails.builder()
                            .machID(machID)
                            .machName(rs.getString("machname"))
                            .machDesc(rs.getString("machdesc"))
                            .machAmount(rs.getDouble("machAmount"))
                            .build();
                    schemeDetails.getMachines().add(machDetails);

                }
            }
        }
        return new ArrayList<>(eventDetailsMap.values());
    }
}