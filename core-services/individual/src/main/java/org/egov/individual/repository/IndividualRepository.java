package org.egov.individual.repository;

import static org.egov.common.utils.CommonUtils.constructTotalCountCTEAndReturnResult;
import static org.egov.common.utils.CommonUtils.getIdMethod;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.egov.common.data.query.builder.GenericQueryBuilder;
import org.egov.common.data.query.builder.QueryFieldChecker;
import org.egov.common.data.query.builder.SelectQueryBuilder;
import org.egov.common.data.repository.GenericRepository;
import org.egov.common.models.individual.Address;
import org.egov.common.models.individual.Identifier;
import org.egov.common.models.individual.Individual;
import org.egov.common.models.individual.Skill;
import org.egov.common.producer.Producer;
import org.egov.individual.repository.rowmapper.AddressRowMapper;
import org.egov.individual.repository.rowmapper.IdentifierRowMapper;
import org.egov.individual.repository.rowmapper.IndividualRowMapper;
import org.egov.individual.repository.rowmapper.SkillRowMapper;
import org.egov.individual.web.models.IndividualSearch;
import org.egov.individual.web.models.SearchResponse;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class IndividualRepository extends GenericRepository<Individual> {

    private final String cteQuery = "WITH cte_search_criteria_waypoint(s_latitude, s_longitude) AS (VALUES(:s_latitude, :s_longitude))";
    private final String calculateDistanceFromTwoWaypointsFormulaQuery = "( 6371.4 * acos ( LEAST ( GREATEST (cos ( radians(cte_scw.s_latitude) ) * cos( radians(a.latitude) ) * cos( radians(a.longitude) - radians(cte_scw.s_longitude) )+ sin ( radians(cte_scw.s_latitude) ) * sin( radians(a.latitude) ), -1), 1) ) ) AS distance ";

    protected IndividualRepository(@Qualifier("individualProducer")  Producer producer,
                                   NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                   RedisTemplate<String, Object> redisTemplate,
                                   SelectQueryBuilder selectQueryBuilder,
                                   IndividualRowMapper individualRowMapper) {
        super(producer, namedParameterJdbcTemplate, redisTemplate,
                selectQueryBuilder, individualRowMapper, Optional.of("individual"));
    }

    public SearchResponse<Individual> findById(List<String> ids, String idColumn, Boolean includeDeleted) {
        List<Individual> objFound;
        objFound = findInCache(ids).stream()
                .filter(individual -> individual.getIsDeleted().equals(includeDeleted))
                .collect(Collectors.toList());
        if (!objFound.isEmpty()) {
            Method idMethod = getIdMethod(objFound, idColumn);
            ids.removeAll(objFound.stream()
                    .map(obj -> (String) ReflectionUtils.invokeMethod(idMethod, obj))
                    .collect(Collectors.toList()));
            if (ids.isEmpty()) {
                return SearchResponse.<Individual>builder().totalCount(Long.valueOf(objFound.size())).response(objFound).build();
            }
        }

        String individualQuery = String.format(getQuery("SELECT * FROM individual WHERE %s IN (:ids)",
                includeDeleted), idColumn);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ids", ids);
        Long totalCount = constructTotalCountCTEAndReturnResult(individualQuery, paramMap, this.namedParameterJdbcTemplate);
        List<Individual> individuals = this.namedParameterJdbcTemplate
                .query(individualQuery, paramMap, this.rowMapper);
        enrichIndividuals(individuals, includeDeleted);
        objFound.addAll(individuals);
        putInCache(objFound);
        return SearchResponse.<Individual>builder().totalCount(totalCount).response(objFound).build();
    }

    public SearchResponse<Individual> find(IndividualSearch searchObject, Integer limit, Integer offset,
                                           String tenantId, Long lastChangedSince, Boolean includeDeleted) {
        Map<String, Object> paramsMap = new HashMap<>();
        String query = getQueryForIndividual(searchObject, limit, offset, tenantId, lastChangedSince,
                includeDeleted, paramsMap);
        if (isProximityBasedSearch(searchObject)) {
            return findByRadius(query, searchObject, includeDeleted, paramsMap);
        }
        if (searchObject.getIdentifier() == null) {
            String queryWithoutLimit = query.replace("ORDER BY createdTime DESC LIMIT :limit OFFSET :offset", "");
            Long totalCount = constructTotalCountCTEAndReturnResult(queryWithoutLimit, paramsMap, this.namedParameterJdbcTemplate);
            List<Individual> individuals = this.namedParameterJdbcTemplate.query(query, paramsMap, this.rowMapper);
            if (!individuals.isEmpty()) {
                enrichIndividuals(individuals, includeDeleted);
            }
            return SearchResponse.<Individual>builder().totalCount(totalCount).response(individuals).build();
        } else {
            Map<String, Object> identifierParamMap = new HashMap<>();
            String identifierQuery = getIdentifierQuery(searchObject.getIdentifier(), identifierParamMap);
            identifierParamMap.put("isDeleted", includeDeleted);
            List<Identifier> identifiers = this.namedParameterJdbcTemplate
                    .query(identifierQuery, identifierParamMap, new IdentifierRowMapper());
            if (!identifiers.isEmpty()) {
                query = query.replace(" tenantId=:tenantId ", " tenantId=:tenantId AND id=:individualId ");
                paramsMap.put("individualId", identifiers.stream().findAny().get().getIndividualId());
                List<Individual> individuals = this.namedParameterJdbcTemplate.query(query,
                        paramsMap, this.rowMapper);
                if (!individuals.isEmpty()) {
                    individuals.forEach(individual -> {
                        individual.setIdentifiers(identifiers);
                        List<Address> addresses = getAddressForIndividual(individual.getId(), includeDeleted);
                        individual.setAddress(addresses);
                        Map<String, Object> indServerGenIdParamMap = new HashMap<>();
                        indServerGenIdParamMap.put("individualId", individual.getId());
                        indServerGenIdParamMap.put("isDeleted", includeDeleted);
                        enrichSkills(includeDeleted, individual, indServerGenIdParamMap);
                    });
                }
                return SearchResponse.<Individual>builder().response(individuals).build();
            }
            return SearchResponse.<Individual>builder().build();
        }
    }

    /**
     * @param query
     * @param searchObject
     * @param includeDeleted
     * @param paramsMap
     * @return Fetch all the household which falls under the radius provided using longitude and latitude provided.
     */
    public SearchResponse<Individual> findByRadius(String query, IndividualSearch searchObject, Boolean includeDeleted, Map<String, Object> paramsMap) {
        query = query.replace("LIMIT :limit OFFSET :offset", "");
        paramsMap.put("s_latitude", searchObject.getLatitude());
        paramsMap.put("s_longitude", searchObject.getLongitude());
        if (searchObject.getIdentifier() != null) {
            Map<String, Object> identifierParamMap = new HashMap<>();
            String identifierQuery = getIdentifierQuery(searchObject.getIdentifier(), identifierParamMap);
            identifierParamMap.put("isDeleted", includeDeleted);
            List<Identifier> identifiers = this.namedParameterJdbcTemplate
                    .query(identifierQuery, identifierParamMap, new IdentifierRowMapper());
            if (CollectionUtils.isEmpty(identifiers)) {
                query = query.replace(" tenantId=:tenantId ", " tenantId=:tenantId AND id=:individualId ");
                paramsMap.put("individualId", identifiers.stream().findAny().get().getIndividualId());
                query = cteQuery + ", cte_individual AS (" + query + ")";
                query = query + "SELECT * FROM (SELECT cte_i.*, " + calculateDistanceFromTwoWaypointsFormulaQuery
                        +" FROM cte_individual cte_i LEFT JOIN public.individual_address ia ON ia.individualid = cte_i.id LEFT JOIN public.address a ON ia.addressid = a.id , cte_search_criteria_waypoint cte_scw) rt ";
                if(searchObject.getSearchRadius() != null) {
                    query = query + " WHERE rt.distance < :distance ";
                }
                query = query + " ORDER BY distance ASC ";
                paramsMap.put("distance", searchObject.getSearchRadius());
                Long totalCount = constructTotalCountCTEAndReturnResult(query, paramsMap, this.namedParameterJdbcTemplate);
                query = query + "LIMIT :limit OFFSET :offset";
                List<Individual> individuals = this.namedParameterJdbcTemplate.query(query,
                        paramsMap, this.rowMapper);
                if (!individuals.isEmpty()) {
                    individuals.forEach(individual -> {
                        individual.setIdentifiers(identifiers);
                        List<Address> addresses = getAddressForIndividual(individual.getId(), includeDeleted);
                        individual.setAddress(addresses);
                        Map<String, Object> indServerGenIdParamMap = new HashMap<>();
                        indServerGenIdParamMap.put("individualId", individual.getId());
                        indServerGenIdParamMap.put("isDeleted", includeDeleted);
                        enrichSkills(includeDeleted, individual, indServerGenIdParamMap);
                    });
                }
                return SearchResponse.<Individual>builder().totalCount(totalCount).response(individuals).build();
            }
        } else {
            query = cteQuery + ", cte_individual AS (" + query + ")";
            query = query + "SELECT * FROM (SELECT cte_i.*, "+ calculateDistanceFromTwoWaypointsFormulaQuery
                    +" FROM cte_individual cte_i LEFT JOIN public.individual_address ia ON ia.individualid = cte_i.id LEFT JOIN public.address a ON ia.addressid = a.id , cte_search_criteria_waypoint cte_scw) rt ";
            if(searchObject.getSearchRadius() != null) {
                query = query + " WHERE rt.distance < :distance ";
            }
            query = query + " ORDER BY distance ASC ";
            paramsMap.put("distance", searchObject.getSearchRadius());

            Long totalCount = constructTotalCountCTEAndReturnResult(query, paramsMap, this.namedParameterJdbcTemplate);

            query = query + "LIMIT :limit OFFSET :offset";
            List<Individual> individuals = this.namedParameterJdbcTemplate.query(query,
                    paramsMap, this.rowMapper);
            if (!individuals.isEmpty()) {
                enrichIndividuals(individuals, includeDeleted);
            }
            return SearchResponse.<Individual>builder().totalCount(totalCount).response(individuals).build();
        }
        return SearchResponse.<Individual>builder().build();
    }


    private Boolean isProximityBasedSearch(IndividualSearch searchObject) {
        return searchObject.getLatitude() != null && searchObject.getLongitude() != null && searchObject.getSearchRadius() != null;
    }

    private void enrichSkills(Boolean includeDeleted, Individual individual, Map<String, Object> indServerGenIdParamMap) {
        String individualSkillQuery = getQuery("SELECT * FROM individual_skill WHERE individualId =:individualId",
                includeDeleted);
        List<Skill> skills = this.namedParameterJdbcTemplate.query(individualSkillQuery, indServerGenIdParamMap,
                new SkillRowMapper());
        individual.setSkills(skills);
    }

    private String getQueryForIndividual(IndividualSearch searchObject, Integer limit, Integer offset,
                                         String tenantId, Long lastChangedSince,
                                         Boolean includeDeleted, Map<String, Object> paramsMap) {
        String query = "SELECT * FROM individual";
        List<String> whereFields = GenericQueryBuilder.getFieldsWithCondition(searchObject, QueryFieldChecker.isNotNull, paramsMap);
        query = GenericQueryBuilder.generateQuery(query, whereFields).toString();

        query += " AND tenantId=:tenantId ";
        if (query.contains(tableName + " AND")) {
            query = query.replace(tableName + " AND", tableName + " WHERE ");
        }
        if (searchObject.getIndividualName() != null) {
            query = query + "AND givenname ILIKE :individualName ";
            paramsMap.put("individualName", "%"+searchObject.getIndividualName()+"%");
        }
        if (searchObject.getGender() != null) {
            query = query + "AND gender =:gender ";
            paramsMap.put("gender", searchObject.getGender().name());
        }
        if (searchObject.getDateOfBirth() != null) {
            query = query + "AND dateOfBirth =:dateOfBirth ";
            paramsMap.put("dateOfBirth", searchObject.getDateOfBirth());
        }
        if (searchObject.getSocialCategory() != null) {
            query = query + "AND additionaldetails->'fields' @> '[{\"key\": \"SOCIAL_CATEGORY\", \"value\":" + "\"" + searchObject.getSocialCategory() + "\"}]' ";
        }
        if (searchObject.getCreatedFrom() != null) {

            //If user does not specify toDate, take today's date as toDate by default.
            if (searchObject.getCreatedTo() == null) {
                searchObject.setCreatedTo(new BigDecimal(Instant.now().toEpochMilli()));
            }
            query = query + "AND createdTime BETWEEN :createdFrom AND :createdTo ";
            paramsMap.put("createdFrom", searchObject.getCreatedFrom());
            paramsMap.put("createdTo", searchObject.getCreatedTo());

        } else {
            //if only toDate is provided as parameter without fromDate parameter, throw an exception.
            if (searchObject.getCreatedTo() != null) {
                throw new CustomException("INVALID_SEARCH_PARAM", "Cannot specify createdToDate without a createdFromDate");
            }
        }
        if (Boolean.FALSE.equals(includeDeleted)) {
            query = query + "AND isDeleted=:isDeleted ";
        }

        if (lastChangedSince != null) {
            query = query + "AND lastModifiedTime>=:lastModifiedTime ";
        }
        if (searchObject.getRoleCodes() != null && !searchObject.getRoleCodes().isEmpty()) {
            query = query + "AND roles @> '[";
            for (int i = 0; i < searchObject.getRoleCodes().size(); i++) {
                query = query + "{\"code\": \"" + searchObject.getRoleCodes().get(i) + "\"}";
                if (i != searchObject.getRoleCodes().size() - 1) {
                    query = query + ",";
                }
            }
            query = query + "]' ";
        }

        if (searchObject.getUsername() != null) {
            query = query + "AND username=:username ";
            paramsMap.put("username", searchObject.getUsername());
        }

        if (searchObject.getUserId() != null) {
            query = query + "AND userId=:userId ";
            paramsMap.put("userId", String.valueOf(searchObject.getUserId()));
        }
        query = query + "ORDER BY createdTime DESC LIMIT :limit OFFSET :offset";
        paramsMap.put("tenantId", tenantId);
        paramsMap.put("isDeleted", includeDeleted);
        paramsMap.put("lastModifiedTime", lastChangedSince);
        paramsMap.put("limit", limit);
        paramsMap.put("offset", offset);
        return query;
    }

    private String getIdentifierQuery(Identifier identifier, Map<String, Object> paramMap) {
        String identifierQuery = "SELECT * FROM individual_identifier";
        List<String> identifierWhereFields = GenericQueryBuilder.getFieldsWithCondition(identifier,
                QueryFieldChecker.isNotNull, paramMap);
        return GenericQueryBuilder.generateQuery(identifierQuery, identifierWhereFields).toString();
    }

    private List<Address> getAddressForIndividual(String individualId, Boolean includeDeleted) {
        String addressQuery = getQuery("SELECT a.*, ia.individualId, ia.addressId, ia.createdBy, ia.lastModifiedBy, ia.createdTime, ia.lastModifiedTime, ia.isDeleted" +
                " FROM (" +
                "    SELECT individualId, addressId, type, createdBy, lastModifiedBy, createdTime, lastModifiedTime, isDeleted, " +
                "           ROW_NUMBER() OVER (PARTITION BY individualId, type ORDER BY lastModifiedTime DESC) AS rn" +
                "    FROM individual_address" +
                "    WHERE individualId = :individualId" +
                " ) AS ia" +
                " JOIN address AS a ON ia.addressId = a.id" +
                " WHERE ia.rn = 1 ", includeDeleted, "ia");
        Map<String, Object> indServerGenIdParamMap = new HashMap<>();
        indServerGenIdParamMap.put("individualId", individualId);
        indServerGenIdParamMap.put("isDeleted", includeDeleted);
        return this.namedParameterJdbcTemplate
                .query(addressQuery, indServerGenIdParamMap, new AddressRowMapper());
    }

    private void enrichIndividuals(List<Individual> individuals, Boolean includeDeleted) {
        if (!individuals.isEmpty()) {
            individuals.forEach(individual -> {
                Map<String, Object> indServerGenIdParamMap = new HashMap<>();
                indServerGenIdParamMap.put("individualId", individual.getId());
                indServerGenIdParamMap.put("isDeleted", includeDeleted);
                List<Address> addresses = getAddressForIndividual(individual.getId(), includeDeleted);
                String individualIdentifierQuery = getQuery("SELECT * FROM individual_identifier ii WHERE ii.individualId =:individualId",
                        includeDeleted);
                List<Identifier> identifiers = this.namedParameterJdbcTemplate
                        .query(individualIdentifierQuery, indServerGenIdParamMap,
                                new IdentifierRowMapper());
                enrichSkills(includeDeleted, individual, indServerGenIdParamMap);
                individual.setAddress(addresses);
                individual.setIdentifiers(identifiers);
            });
        }
    }

    private String getQuery(String baseQuery, Boolean includeDeleted) {
        return getQuery(baseQuery, includeDeleted, null);
    }

    private String getQuery(String baseQuery, Boolean includeDeleted, String alias) {
        String isDeletedClause = " AND %sisDeleted = false";
        if (alias != null) {
            isDeletedClause = String.format(isDeletedClause, alias + ".");
        } else {
            isDeletedClause = String.format(isDeletedClause, "");
        }
        StringBuilder baseQueryBuilder = new StringBuilder(baseQuery);
        if (null != includeDeleted && includeDeleted) {
            return baseQuery;
        } else {
            baseQueryBuilder.append(isDeletedClause);
        }
        return baseQueryBuilder.toString();
    }
}
