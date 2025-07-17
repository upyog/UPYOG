

package org.egov.finance.inbox.util;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import static org.egov.finance.inbox.util.DateUtils.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.sound.midi.Instrument;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.finance.inbox.entity.Department;
import org.egov.finance.inbox.exception.ApplicationRuntimeException;
import org.egov.finance.inbox.model.EmployeeInfo;
import org.egov.finance.inbox.model.EmployeeInfoResponse;
import org.egov.finance.inbox.model.EmployeeSearchCriteria;
import org.egov.finance.inbox.model.MasterDetail;
import org.egov.finance.inbox.model.MdmsCriteria;
import org.egov.finance.inbox.model.MdmsCriteriaReq;
import org.egov.finance.inbox.model.ModuleDetail;
import org.egov.finance.inbox.model.RequestInfo;
import org.egov.finance.inbox.model.request.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@SuppressWarnings("deprecation")
@Service
public class MicroserviceUtils {

 
    private static final String CLIENT_ID = "client.id";
    private static final int DEFAULT_PAGE_SIZE = 100;

   
    @Autowired
    private RestTemplate restTemplate;

    
    @Autowired
    private Environment environment;

   // @Autowired
   // public RedisTemplate<Object, Object> redisTemplate;

   
  

    @Value("${egov.hrms.service.endpoint}")
    private String hmrsurl;

    @Value("${egov.services.user.approvers.url}")
    private String approverSrvcUrl;

   
    private ObjectMapper mapper;
    SimpleDateFormat ddMMMyyyyFormat = new SimpleDateFormat("dd-MMM-yyyy");

 
    public MicroserviceUtils() {
        mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public RestTemplate createRestTemplate() {

        return restTemplate;
    }

  

    public List<EmployeeInfo> getApprovers(String departmentId, String designationId) {
        return this.getEmployeeBySearchCriteria(
                new EmployeeSearchCriteria().builder().departments(Collections.singletonList(departmentId))
                        .designations(Collections.singletonList(designationId)).build());
    }

    public List<EmployeeInfo> getEmployeeBySearchCriteria(EmployeeSearchCriteria criteria) {
        final RestTemplate restTemplate = createRestTemplate();
        StringBuilder url = new StringBuilder(hmrsurl).append(approverSrvcUrl)
                .append("?tenantId=").append(ApplicationThreadLocals.getTenantID());
        this.prepareEmplyeeSearchQueryString(criteria, url);
        RequestInfo requestInfo = new RequestInfo();
        RequestInfoWrapper reqWrapper = new RequestInfoWrapper();
        requestInfo.setAuthToken(ApplicationThreadLocals.getUserToken());
        requestInfo.setTs(new Date().toInstant().toEpochMilli());
        reqWrapper.setRequestInfo(requestInfo);
        EmployeeInfoResponse empResponse = restTemplate.postForObject(url.toString(), reqWrapper,
                EmployeeInfoResponse.class);
        return empResponse.getEmployees();
    }



    public List<EmployeeInfo> getEmployee(Long empId, Date toDay, String departmentId, String designationId) {
       EmployeeSearchCriteria creiteria = this.prepareEmployeeSearchQueryBuilder(empId, toDay, departmentId,
               designationId);
        return this.getEmployeeBySearchCriteria(creiteria);
    }

    private EmployeeSearchCriteria prepareEmployeeSearchQueryBuilder(Long empId, Date toDay, String departmentId,
            String designationId) {
        EmployeeSearchCriteria criteria = new EmployeeSearchCriteria().builder().build();
        if (empId != null && empId != 0) {
            criteria.setIds(Collections.singletonList(empId));
        }
        if (toDay != null) {
            criteria.setAsOnDate(new Date().toInstant().toEpochMilli());
        }
        if (departmentId != null && !departmentId.isEmpty()) {
            criteria.setDepartments(Collections.singletonList(departmentId));
        }
        if (designationId != null && !designationId.isEmpty()) {
            criteria.setDesignations(Collections.singletonList(designationId));
        }
        return criteria;
    }
        
        private void prepareEmplyeeSearchQueryString(EmployeeSearchCriteria criteria, StringBuilder url) {
            if (criteria.getAsOnDate() != null && criteria.getAsOnDate() != 0) {
                url.append("&asOnDate=").append(criteria.getAsOnDate());
            }
            if (CollectionUtils.isNotEmpty(criteria.getCodes())) {
                url.append("&codes=").append(StringUtils.join(criteria.getCodes(), ","));
            }
            if (CollectionUtils.isNotEmpty(criteria.getNames())) {
                url.append("&names=").append(StringUtils.join(criteria.getNames(), ","));
            }
            if (CollectionUtils.isNotEmpty(criteria.getDepartments())) {
                url.append("&departments=").append(StringUtils.join(criteria.getDepartments(), ","));
            }
            if (CollectionUtils.isNotEmpty(criteria.getDesignations())) {
                url.append("&designations=").append(StringUtils.join(criteria.getDesignations(), ","));
            }
            if (CollectionUtils.isNotEmpty(criteria.getRoles())) {
                url.append("&roles=").append(StringUtils.join(criteria.getRoles(), ","));
            }
            if (CollectionUtils.isNotEmpty(criteria.getIds())) {
                url.append("&ids=").append(StringUtils.join(criteria.getIds(), ","));
            }
            if (CollectionUtils.isNotEmpty(criteria.getEmployeestatuses())) {
                url.append("&employeestatuses=").append(StringUtils.join(criteria.getEmployeestatuses(), ","));
            }
            if (CollectionUtils.isNotEmpty(criteria.getEmployeetypes())) {
                url.append("&employeetypes=").append(StringUtils.join(criteria.getEmployeetypes(), ","));
            }
            if (CollectionUtils.isNotEmpty(criteria.getPositions())) {
                url.append("&positions=").append(StringUtils.join(criteria.getPositions(), ","));
            }
            if (StringUtils.isNotBlank(criteria.getPhone())) {
                url.append("&phone=").append(criteria.getPhone());
            }
            if (criteria.getLimit() != null && criteria.getLimit() != 0) {
                url.append("&limit=").append(criteria.getLimit());
            }
        
    }

   

}
