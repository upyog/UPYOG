package digit.repository;

import digit.repository.querybuilder.ServiceDefinitionQueryBuilder;
import digit.repository.rowmapper.ServiceDefinitionRowMapper;
import digit.web.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class ServiceDefinitionRequestRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ServiceDefinitionRowMapper serviceDefinitionRowMapper;

    @Autowired
    private ServiceDefinitionQueryBuilder serviceDefinitionQueryBuilder;


    public List<ServiceDefinition> getServiceDefinitions(ServiceDefinitionSearchRequest serviceDefinitionSearchRequest) {
        ServiceDefinitionCriteria criteria = serviceDefinitionSearchRequest.getServiceDefinitionCriteria();

        List<Object> preparedStmtList = new ArrayList<>();

        if(CollectionUtils.isEmpty(criteria.getIds()) && ObjectUtils.isEmpty(criteria.getTenantId()) && CollectionUtils.isEmpty(criteria.getCode()))
            return new ArrayList<>();

        // Fetch ids based on criteria if ids are not present
        if(CollectionUtils.isEmpty(criteria.getIds())){
            // Fetch ids according to given criteria
            String idQuery = serviceDefinitionQueryBuilder.getServiceDefinitionsIdsQuery(serviceDefinitionSearchRequest, preparedStmtList);
            log.info("Service definition ids query: " + idQuery);
            log.info("Parameters: " + preparedStmtList.toString());
            List<String> serviceDefinitionIds = jdbcTemplate.query(idQuery, preparedStmtList.toArray(), new SingleColumnRowMapper<>(String.class));

            if(CollectionUtils.isEmpty(serviceDefinitionIds))
                return new ArrayList<>();

            // Set ids in criteria
            criteria.setIds(serviceDefinitionIds);
            preparedStmtList.clear();
        }



        System.out.println("inside get service definition");
        System.out.println(criteria);
        String query = serviceDefinitionQueryBuilder.getServiceDefinitionSearchQuery(criteria, preparedStmtList);
        log.info("query for search: " + query + " params: " + preparedStmtList);
        return jdbcTemplate.query(query, preparedStmtList.toArray(), serviceDefinitionRowMapper);

    }

    public Integer fetchTotalSurveyCount(ServiceDefinitionSearchRequest ServiceDefinitionSearchRequest) {
        List<Object> preparedStmtList = new ArrayList<>();
        ServiceDefinitionCriteria ServiceDefinitionCriteria = ServiceDefinitionSearchRequest.getServiceDefinitionCriteria();

        if(CollectionUtils.isEmpty(ServiceDefinitionCriteria.getIds()))
            return 0;

        // Omit pagination in case of count
        ServiceDefinitionCriteria.setIsCountCall(Boolean.TRUE);
        String query = serviceDefinitionQueryBuilder.getSurveyCountQuery(ServiceDefinitionSearchRequest, preparedStmtList);
        ServiceDefinitionCriteria.setIsCountCall(Boolean.FALSE);

        log.info("query for count search call: " + query + " params: " + preparedStmtList);

        return jdbcTemplate.queryForObject(query, preparedStmtList.toArray(), Integer.class);
    }

    public List<Service> getService(ServiceCriteria criteria) {
        return new ArrayList<>();
    }
}
