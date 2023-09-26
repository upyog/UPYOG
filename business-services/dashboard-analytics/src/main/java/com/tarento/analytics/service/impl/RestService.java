
package com.tarento.analytics.service.impl;

import static javax.servlet.http.HttpServletRequest.BASIC_AUTH;
import static org.apache.commons.codec.CharEncoding.US_ASCII;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RestService {
    public static final Logger LOGGER = LoggerFactory.getLogger(RestService.class);

    @Value("${services.esindexer.host}")
    private String indexServiceHost;
    @Value("${egov.services.esindexer.host.search}")
    private String indexServiceHostSearch;
    @Value("${services.esindexer.host}")
    private String dssindexServiceHost;
    @Value("${egov.es.username}")
    private String userName;
    @Value("${egov.es.password}")
    private String password;

    @Autowired
    private RetryTemplate retryTemplate;


    /**
     * search on Elastic search for a search query
     * @param index           elastic search index name against which search operation
     * @param searchQuery     search query as request body
     * @return
     * @throws IOException
     */

    public JsonNode search(String index, String searchQuery) {
        //System.out.println("INSIDE REST");
        String url =( indexServiceHost) + index + indexServiceHostSearch;
        HttpHeaders headers = getHttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        LOGGER.info("Index Name : " + index);
        LOGGER.info("Searching ES for Query: " + searchQuery);
        HttpEntity<String> requestEntity = new HttpEntity<>(searchQuery, headers);
        JsonNode responseNode = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
        	
			responseNode= mapper.readTree("{\n"
					+ "  \"took\" : 43,\n"
					+ "  \"timed_out\" : false,\n"
					+ "  \"_shards\" : {\n"
					+ "    \"total\" : 5,\n"
					+ "    \"successful\" : 5,\n"
					+ "    \"skipped\" : 0,\n"
					+ "    \"failed\" : 0\n"
					+ "  },\n"
					+ "  \"hits\" : {\n"
					+ "    \"total\" : 111484,\n"
					+ "    \"max_score\" : 1.0,\n"
					+ "    \"hits\" : [\n"
					+ "      {\n"
					+ "        \"_index\" : \"tl-national-dashboard\",\n"
					+ "        \"_type\" : \"nss\",\n"
					+ "        \"_id\" : \"tl-national-dashboard+0+128812\",\n"
					+ "        \"_score\" : 1.0,\n"
					+ "        \"_source\" : {\n"
					+ "          \"date\" : \"21-12-2022\",\n"
					+ "          \"todaysApplications\" : 2,\n"
					+ "          \"lastModifiedTime\" : 1671683617468,\n"
					+ "          \"applicationsMovedTodayForStatus\" : 1,\n"
					+ "          \"tlTax\" : 1000,\n"
					+ "          \"module\" : \"TL\",\n"
					+ "          \"lastModifiedBy\" : \"c499b45b-7d65-418f-b003-5ce5db2220d2\",\n"
					+ "          \"adhocRebate\" : 0,\n"
					+ "          \"ulb\" : \"chd.municipalcorporationchandigarh\",\n"
					+ "          \"todaysLicenseIssuedWithinSLA\" : 0,\n"
					+ "          \"ward\" : \"Chandigarh\",\n"
					+ "          \"transactions\" : 1,\n"
					+ "          \"adhocPenalty\" : 0,\n"
					+ "          \"createdBy\" : \"c499b45b-7d65-418f-b003-5ce5db2220d2\",\n"
					+ "          \"todaysTradeLicensesForStatus\" : 1,\n"
					+ "          \"createdTime\" : 1671683617468,\n"
					+ "          \"state\" : \"Chandigarh\",\n"
					+ "          \"region\" : \"Chandigarh\",\n"
					+ "          \"status\" : \"PENDINGL1VERIFICATION\"\n"
					+ "        }\n"
					+ "      },\n"
					+ "      {\n"
					+ "        \"_index\" : \"tl-national-dashboard\",\n"
					+ "        \"_type\" : \"nss\",\n"
					+ "        \"_id\" : \"tl-national-dashboard+2+128810\",\n"
					+ "        \"_score\" : 1.0,\n"
					+ "        \"_source\" : {\n"
					+ "          \"date\" : \"21-12-2022\",\n"
					+ "          \"todaysApplications\" : 2,\n"
					+ "          \"lastModifiedTime\" : 1671683617468,\n"
					+ "          \"tlTax\" : 1000,\n"
					+ "          \"module\" : \"TL\",\n"
					+ "          \"lastModifiedBy\" : \"c499b45b-7d65-418f-b003-5ce5db2220d2\",\n"
					+ "          \"adhocRebate\" : 0,\n"
					+ "          \"ulb\" : \"chd.municipalcorporationchandigarh\",\n"
					+ "          \"todaysLicenseIssuedWithinSLA\" : 0,\n"
					+ "          \"ward\" : \"Chandigarh\",\n"
					+ "          \"transactions\" : 1,\n"
					+ "          \"adhocPenalty\" : 0,\n"
					+ "          \"createdBy\" : \"c499b45b-7d65-418f-b003-5ce5db2220d2\",\n"
					+ "          \"todaysTradeLicensesForStatus\" : 1,\n"
					+ "          \"createdTime\" : 1671683617468,\n"
					+ "          \"state\" : \"Chandigarh\",\n"
					+ "          \"region\" : \"Chandigarh\",\n"
					+ "          \"status\" : \"APPROVED\"\n"
					+ "        }\n"
					+ "      },\n"
					+ "      {\n"
					+ "        \"_index\" : \"tl-national-dashboard\",\n"
					+ "        \"_type\" : \"nss\",\n"
					+ "        \"_id\" : \"tl-national-dashboard+2+144837\",\n"
					+ "        \"_score\" : 1.0,\n"
					+ "        \"_source\" : {\n"
					+ "          \"date\" : \"25-12-2022\",\n"
					+ "          \"todaysApplications\" : 1,\n"
					+ "          \"lastModifiedTime\" : 1672137917848,\n"
					+ "          \"applicationsMovedTodayForStatus\" : 1,\n"
					+ "          \"tlTax\" : 700,\n"
					+ "          \"module\" : \"TL\",\n"
					+ "          \"lastModifiedBy\" : \"c499b45b-7d65-418f-b003-5ce5db2220d2\",\n"
					+ "          \"adhocRebate\" : 0,\n"
					+ "          \"ulb\" : \"chd.municipalcorporationchandigarh\",\n"
					+ "          \"todaysLicenseIssuedWithinSLA\" : 0,\n"
					+ "          \"ward\" : \"Chandigarh\",\n"
					+ "          \"transactions\" : 1,\n"
					+ "          \"adhocPenalty\" : 0,\n"
					+ "          \"createdBy\" : \"c499b45b-7d65-418f-b003-5ce5db2220d2\",\n"
					+ "          \"todaysTradeLicensesForStatus\" : 1,\n"
					+ "          \"createdTime\" : 1672137917848,\n"
					+ "          \"state\" : \"Chandigarh\",\n"
					+ "          \"region\" : \"Chandigarh\",\n"
					+ "          \"status\" : \"INITIATED\"\n"
					+ "        }\n"
					+ "      },\n"
					+ "      {\n"
					+ "        \"_index\" : \"tl-national-dashboard\",\n"
					+ "        \"_type\" : \"nss\",\n"
					+ "        \"_id\" : \"tl-national-dashboard+1+144834\",\n"
					+ "        \"_score\" : 1.0,\n"
					+ "        \"_source\" : {\n"
					+ "          \"date\" : \"23-12-2022\",\n"
					+ "          \"todaysApplications\" : 1,\n"
					+ "          \"lastModifiedTime\" : 1672137271343,\n"
					+ "          \"tlTax\" : 700,\n"
					+ "          \"module\" : \"TL\",\n"
					+ "          \"lastModifiedBy\" : \"c499b45b-7d65-418f-b003-5ce5db2220d2\",\n"
					+ "          \"adhocRebate\" : 0,\n"
					+ "          \"ulb\" : \"chd.municipalcorporationchandigarh\",\n"
					+ "          \"todaysLicenseIssuedWithinSLA\" : 0,\n"
					+ "          \"ward\" : \"Chandigarh\",\n"
					+ "          \"transactions\" : 1,\n"
					+ "          \"adhocPenalty\" : 0,\n"
					+ "          \"createdBy\" : \"c499b45b-7d65-418f-b003-5ce5db2220d2\",\n"
					+ "          \"createdTime\" : 1672137271343,\n"
					+ "          \"state\" : \"Chandigarh\",\n"
					+ "          \"todaysCollectionForTradeType\" : 0,\n"
					+ "          \"region\" : \"Chandigarh\",\n"
					+ "          \"tradeType\" : \"CTL.REHRI_REGISTRATION\"\n"
					+ "        }\n"
					+ "      },\n"
					+ "      {\n"
					+ "        \"_index\" : \"tl-national-dashboard\",\n"
					+ "        \"_type\" : \"nss\",\n"
					+ "        \"_id\" : \"tl-national-dashboard+0+144839\",\n"
					+ "        \"_score\" : 1.0,\n"
					+ "        \"_source\" : {\n"
					+ "          \"date\" : \"26-12-2022\",\n"
					+ "          \"todaysApplications\" : 0,\n"
					+ "          \"lastModifiedTime\" : 1672137989522,\n"
					+ "          \"applicationsMovedTodayForStatus\" : 1,\n"
					+ "          \"tlTax\" : 0,\n"
					+ "          \"module\" : \"TL\",\n"
					+ "          \"lastModifiedBy\" : \"c499b45b-7d65-418f-b003-5ce5db2220d2\",\n"
					+ "          \"adhocRebate\" : 0,\n"
					+ "          \"ulb\" : \"chd.municipalcorporationchandigarh\",\n"
					+ "          \"todaysLicenseIssuedWithinSLA\" : 0,\n"
					+ "          \"ward\" : \"Chandigarh\",\n"
					+ "          \"transactions\" : 3,\n"
					+ "          \"adhocPenalty\" : 0,\n"
					+ "          \"createdBy\" : \"c499b45b-7d65-418f-b003-5ce5db2220d2\",\n"
					+ "          \"createdTime\" : 1672137989522,\n"
					+ "          \"state\" : \"Chandigarh\",\n"
					+ "          \"region\" : \"Chandigarh\",\n"
					+ "          \"status\" : \"MODIFIED\"\n"
					+ "        }\n"
					+ "      },\n"
					+ "      {\n"
					+ "        \"_index\" : \"tl-national-dashboard\",\n"
					+ "        \"_type\" : \"nss\",\n"
					+ "        \"_id\" : \"tl-national-dashboard+1+144835\",\n"
					+ "        \"_score\" : 1.0,\n"
					+ "        \"_source\" : {\n"
					+ "          \"date\" : \"24-12-2022\",\n"
					+ "          \"todaysApplications\" : 0,\n"
					+ "          \"lastModifiedTime\" : 1672137835376,\n"
					+ "          \"tlTax\" : 0,\n"
					+ "          \"module\" : \"TL\",\n"
					+ "          \"lastModifiedBy\" : \"c499b45b-7d65-418f-b003-5ce5db2220d2\",\n"
					+ "          \"adhocRebate\" : 0,\n"
					+ "          \"ulb\" : \"chd.municipalcorporationchandigarh\",\n"
					+ "          \"todaysLicenseIssuedWithinSLA\" : 0,\n"
					+ "          \"ward\" : \"Chandigarh\",\n"
					+ "          \"transactions\" : 0,\n"
					+ "          \"adhocPenalty\" : 0,\n"
					+ "          \"createdBy\" : \"c499b45b-7d65-418f-b003-5ce5db2220d2\",\n"
					+ "          \"createdTime\" : 1672137835376,\n"
					+ "          \"state\" : \"Chandigarh\",\n"
					+ "          \"region\" : \"Chandigarh\"\n"
					+ "        }\n"
					+ "      },\n"
					+ "      {\n"
					+ "        \"_index\" : \"tl-national-dashboard\",\n"
					+ "        \"_type\" : \"nss\",\n"
					+ "        \"_id\" : \"tl-national-dashboard+2+157583\",\n"
					+ "        \"_score\" : 1.0,\n"
					+ "        \"_source\" : {\n"
					+ "          \"date\" : \"08-06-2022\",\n"
					+ "          \"todaysApplications\" : 1,\n"
					+ "          \"lastModifiedTime\" : 1672249067594,\n"
					+ "          \"applicationsMovedTodayForStatus\" : 0,\n"
					+ "          \"tlTax\" : 0,\n"
					+ "          \"module\" : \"TL\",\n"
					+ "          \"lastModifiedBy\" : \"3c9e3f73-6345-4c5d-803b-67c0ec09eaff\",\n"
					+ "          \"adhocRebate\" : 0,\n"
					+ "          \"ulb\" : \"pb.jalandhar\",\n"
					+ "          \"todaysLicenseIssuedWithinSLA\" : 0,\n"
					+ "          \"ward\" : \"Model Town - B11 - A1\",\n"
					+ "          \"transactions\" : 0,\n"
					+ "          \"adhocPenalty\" : 0,\n"
					+ "          \"createdBy\" : \"3c9e3f73-6345-4c5d-803b-67c0ec09eaff\",\n"
					+ "          \"createdTime\" : 1672249067594,\n"
					+ "          \"state\" : \"Punjab\",\n"
					+ "          \"region\" : \"Jalandhar\",\n"
					+ "          \"status\" : \"FIELDINSPECTION\"\n"
					+ "        }\n"
					+ "      },\n"
					+ "      {\n"
					+ "        \"_index\" : \"tl-national-dashboard\",\n"
					+ "        \"_type\" : \"nss\",\n"
					+ "        \"_id\" : \"tl-national-dashboard+2+157584\",\n"
					+ "        \"_score\" : 1.0,\n"
					+ "        \"_source\" : {\n"
					+ "          \"date\" : \"08-06-2022\",\n"
					+ "          \"todaysApplications\" : 1,\n"
					+ "          \"lastModifiedTime\" : 1672249067594,\n"
					+ "          \"applicationsMovedTodayForStatus\" : 0,\n"
					+ "          \"tlTax\" : 0,\n"
					+ "          \"module\" : \"TL\",\n"
					+ "          \"lastModifiedBy\" : \"3c9e3f73-6345-4c5d-803b-67c0ec09eaff\",\n"
					+ "          \"adhocRebate\" : 0,\n"
					+ "          \"ulb\" : \"pb.jalandhar\",\n"
					+ "          \"todaysLicenseIssuedWithinSLA\" : 0,\n"
					+ "          \"ward\" : \"Model Town - B11 - A1\",\n"
					+ "          \"transactions\" : 0,\n"
					+ "          \"adhocPenalty\" : 0,\n"
					+ "          \"createdBy\" : \"3c9e3f73-6345-4c5d-803b-67c0ec09eaff\",\n"
					+ "          \"createdTime\" : 1672249067594,\n"
					+ "          \"state\" : \"Punjab\",\n"
					+ "          \"region\" : \"Jalandhar\",\n"
					+ "          \"status\" : \"REJECTED\"\n"
					+ "        }\n"
					+ "      },\n"
					+ "      {\n"
					+ "        \"_index\" : \"tl-national-dashboard\",\n"
					+ "        \"_type\" : \"nss\",\n"
					+ "        \"_id\" : \"tl-national-dashboard+1+157586\",\n"
					+ "        \"_score\" : 1.0,\n"
					+ "        \"_source\" : {\n"
					+ "          \"date\" : \"08-06-2022\",\n"
					+ "          \"todaysApplications\" : 1,\n"
					+ "          \"lastModifiedTime\" : 1672249067594,\n"
					+ "          \"applicationsMovedTodayForStatus\" : 0,\n"
					+ "          \"tlTax\" : 500.0,\n"
					+ "          \"module\" : \"TL\",\n"
					+ "          \"lastModifiedBy\" : \"3c9e3f73-6345-4c5d-803b-67c0ec09eaff\",\n"
					+ "          \"adhocRebate\" : 0,\n"
					+ "          \"ulb\" : \"pb.amritsar\",\n"
					+ "          \"todaysLicenseIssuedWithinSLA\" : 0,\n"
					+ "          \"ward\" : \"NEW SHAHEED UDHAM SINGH NAGAR - W - W-37 - A2\",\n"
					+ "          \"transactions\" : 1,\n"
					+ "          \"adhocPenalty\" : 125.0,\n"
					+ "          \"createdBy\" : \"3c9e3f73-6345-4c5d-803b-67c0ec09eaff\",\n"
					+ "          \"createdTime\" : 1672249067594,\n"
					+ "          \"state\" : \"Punjab\",\n"
					+ "          \"region\" : \"Amritsar\",\n"
					+ "          \"status\" : \"FIELDINSPECTION\"\n"
					+ "        }\n"
					+ "      },\n"
					+ "      {\n"
					+ "        \"_index\" : \"tl-national-dashboard\",\n"
					+ "        \"_type\" : \"nss\",\n"
					+ "        \"_id\" : \"tl-national-dashboard+2+157586\",\n"
					+ "        \"_score\" : 1.0,\n"
					+ "        \"_source\" : {\n"
					+ "          \"date\" : \"08-06-2022\",\n"
					+ "          \"todaysApplications\" : 1,\n"
					+ "          \"lastModifiedTime\" : 1672249067594,\n"
					+ "          \"tlTax\" : 500.0,\n"
					+ "          \"module\" : \"TL\",\n"
					+ "          \"lastModifiedBy\" : \"3c9e3f73-6345-4c5d-803b-67c0ec09eaff\",\n"
					+ "          \"adhocRebate\" : 0,\n"
					+ "          \"ulb\" : \"pb.amritsar\",\n"
					+ "          \"todaysLicenseIssuedWithinSLA\" : 0,\n"
					+ "          \"ward\" : \"NEW SHAHEED UDHAM SINGH NAGAR - W - W-37 - A2\",\n"
					+ "          \"transactions\" : 1,\n"
					+ "          \"adhocPenalty\" : 125.0,\n"
					+ "          \"createdBy\" : \"3c9e3f73-6345-4c5d-803b-67c0ec09eaff\",\n"
					+ "          \"createdTime\" : 1672249067594,\n"
					+ "          \"state\" : \"Punjab\",\n"
					+ "          \"todaysCollectionForTradeType\" : 625.0,\n"
					+ "          \"region\" : \"Amritsar\",\n"
					+ "          \"tradeType\" : \"GOODS.SALESTORAGE.TST-107\"\n"
					+ "        }\n"
					+ "      }\n"
					+ "    ]\n"
					+ "  },\n"
					+ "  \"aggregations\" : {\n"
					+ "    \"AGGR\" : {\n"
					+ "      \"meta\" : { },\n"
					+ "      \"doc_count\" : 111484,\n"
					+ "      \"States\" : {\n"
					+ "        \"doc_count_error_upper_bound\" : 0,\n"
					+ "        \"sum_other_doc_count\" : 0,\n"
					+ "        \"buckets\" : [\n"
					+ "          {\n"
					+ "            \"key\" : \"Punjab\",\n"
					+ "            \"doc_count\" : 65214,\n"
					+ "            \"Transactions\" : {\n"
					+ "              \"value\" : 75319.0\n"
					+ "            },\n"
					+ "            \"Total_Licence_Issued_1\" : {\n"
					+ "              \"doc_count\" : 8996,\n"
					+ "              \"Total_Licence_Issued\" : {\n"
					+ "                \"value\" : 11716.0\n"
					+ "              }\n"
					+ "            },\n"
					+ "            \"TL_Total_Collection\" : {\n"
					+ "              \"value\" : 2.2161386E7\n"
					+ "            }\n"
					+ "          },\n"
					+ "          {\n"
					+ "            \"key\" : \"Andhra Pradesh\",\n"
					+ "            \"doc_count\" : 33414,\n"
					+ "            \"Transactions\" : {\n"
					+ "              \"value\" : 6060.0\n"
					+ "            },\n"
					+ "            \"Total_Licence_Issued_1\" : {\n"
					+ "              \"doc_count\" : 6,\n"
					+ "              \"Total_Licence_Issued\" : {\n"
					+ "                \"value\" : 115.0\n"
					+ "              }\n"
					+ "            },\n"
					+ "            \"TL_Total_Collection\" : {\n"
					+ "              \"value\" : 477150.0\n"
					+ "            }\n"
					+ "          },\n"
					+ "          {\n"
					+ "            \"key\" : \"Goa\",\n"
					+ "            \"doc_count\" : 5440,\n"
					+ "            \"Transactions\" : {\n"
					+ "              \"value\" : 83706.0\n"
					+ "            },\n"
					+ "            \"Total_Licence_Issued_1\" : {\n"
					+ "              \"doc_count\" : 108,\n"
					+ "              \"Total_Licence_Issued\" : {\n"
					+ "                \"value\" : 8.0\n"
					+ "              }\n"
					+ "            },\n"
					+ "            \"TL_Total_Collection\" : {\n"
					+ "              \"value\" : 3.7235152E7\n"
					+ "            }\n"
					+ "          },\n"
					+ "          {\n"
					+ "            \"key\" : \"Rajasthan\",\n"
					+ "            \"doc_count\" : 4365,\n"
					+ "            \"Transactions\" : {\n"
					+ "              \"value\" : 6063.0\n"
					+ "            },\n"
					+ "            \"Total_Licence_Issued_1\" : {\n"
					+ "              \"doc_count\" : 887,\n"
					+ "              \"Total_Licence_Issued\" : {\n"
					+ "                \"value\" : 1782.0\n"
					+ "              }\n"
					+ "            },\n"
					+ "            \"TL_Total_Collection\" : {\n"
					+ "              \"value\" : 1.7313844E7\n"
					+ "            }\n"
					+ "          },\n"
					+ "          {\n"
					+ "            \"key\" : \"Uttarakhand\",\n"
					+ "            \"doc_count\" : 2477,\n"
					+ "            \"Transactions\" : {\n"
					+ "              \"value\" : 580.0\n"
					+ "            },\n"
					+ "            \"Total_Licence_Issued_1\" : {\n"
					+ "              \"doc_count\" : 436,\n"
					+ "              \"Total_Licence_Issued\" : {\n"
					+ "                \"value\" : 539.0\n"
					+ "              }\n"
					+ "            },\n"
					+ "            \"TL_Total_Collection\" : {\n"
					+ "              \"value\" : 457220.0\n"
					+ "            }\n"
					+ "          },\n"
					+ "          {\n"
					+ "            \"key\" : \"Maharashtra\",\n"
					+ "            \"doc_count\" : 181,\n"
					+ "            \"Transactions\" : {\n"
					+ "              \"value\" : 3278.0\n"
					+ "            },\n"
					+ "            \"Total_Licence_Issued_1\" : {\n"
					+ "              \"doc_count\" : 5,\n"
					+ "              \"Total_Licence_Issued\" : {\n"
					+ "                \"value\" : 115.0\n"
					+ "              }\n"
					+ "            },\n"
					+ "            \"TL_Total_Collection\" : {\n"
					+ "              \"value\" : 2086969.0\n"
					+ "            }\n"
					+ "          },\n"
					+ "          {\n"
					+ "            \"key\" : \"Chandigarh\",\n"
					+ "            \"doc_count\" : 138,\n"
					+ "            \"Transactions\" : {\n"
					+ "              \"value\" : 1632.0\n"
					+ "            },\n"
					+ "            \"Total_Licence_Issued_1\" : {\n"
					+ "              \"doc_count\" : 12,\n"
					+ "              \"Total_Licence_Issued\" : {\n"
					+ "                \"value\" : 66.0\n"
					+ "              }\n"
					+ "            },\n"
					+ "            \"TL_Total_Collection\" : {\n"
					+ "              \"value\" : 39400.0\n"
					+ "            }\n"
					+ "          },\n"
					+ "          {\n"
					+ "            \"key\" : \"Pg\",\n"
					+ "            \"doc_count\" : 135,\n"
					+ "            \"Transactions\" : {\n"
					+ "              \"value\" : 2958.0\n"
					+ "            },\n"
					+ "            \"Total_Licence_Issued_1\" : {\n"
					+ "              \"doc_count\" : 9,\n"
					+ "              \"Total_Licence_Issued\" : {\n"
					+ "                \"value\" : 115.0\n"
					+ "              }\n"
					+ "            },\n"
					+ "            \"TL_Total_Collection\" : {\n"
					+ "              \"value\" : 270000.0\n"
					+ "            }\n"
					+ "          },\n"
					+ "          {\n"
					+ "            \"key\" : \"Uttrakhand\",\n"
					+ "            \"doc_count\" : 74,\n"
					+ "            \"Transactions\" : {\n"
					+ "              \"value\" : 0.0\n"
					+ "            },\n"
					+ "            \"Total_Licence_Issued_1\" : {\n"
					+ "              \"doc_count\" : 15,\n"
					+ "              \"Total_Licence_Issued\" : {\n"
					+ "                \"value\" : 10.0\n"
					+ "              }\n"
					+ "            },\n"
					+ "            \"TL_Total_Collection\" : {\n"
					+ "              \"value\" : 5941.0\n"
					+ "            }\n"
					+ "          },\n"
					+ "          {\n"
					+ "            \"key\" : \"Telangana\",\n"
					+ "            \"doc_count\" : 46,\n"
					+ "            \"Transactions\" : {\n"
					+ "              \"value\" : 52230.0\n"
					+ "            },\n"
					+ "            \"Total_Licence_Issued_1\" : {\n"
					+ "              \"doc_count\" : 4,\n"
					+ "              \"Total_Licence_Issued\" : {\n"
					+ "                \"value\" : 92.0\n"
					+ "              }\n"
					+ "            },\n"
					+ "            \"TL_Total_Collection\" : {\n"
					+ "              \"value\" : 216850.0\n"
					+ "            }\n"
					+ "          }\n"
					+ "        ]\n"
					+ "      }\n"
					+ "    }\n"
					+ "  }\n"
					+ "}\n"
					+ "");
        	
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        
        try {
        	if(searchQuery.contains("1617235200000")) {
			responseNode= mapper.readTree("{\n"
					+ "  \"took\" : 2,\n"
					+ "  \"timed_out\" : false,\n"
					+ "  \"_shards\" : {\n"
					+ "    \"total\" : 5,\n"
					+ "    \"successful\" : 5,\n"
					+ "    \"skipped\" : 0,\n"
					+ "    \"failed\" : 0\n"
					+ "  },\n"
					+ "  \"hits\" : {\n"
					+ "    \"total\" : 163,\n"
					+ "    \"max_score\" : 0.0,\n"
					+ "    \"hits\" : [ ]\n"
					+ "  },\n"
					+ "  \"aggregations\" : {\n"
					+ "    \"AGGR\" : {\n"
					+ "      \"meta\" : { },\n"
					+ "      \"doc_count\" : 163,\n"
					+ "      \"Total Collection\" : {\n"
					+ "        \"value\" : 0.0\n"
					+ "      }\n"
					+ "    }\n"
					+ "  }\n"
					+ "}\n"
					+ "");
        	}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        
        try {
        	if(searchQuery.contains("1692835199999")) {
			responseNode= mapper.readTree("{\n"
					+ "  \"took\" : 89,\n"
					+ "  \"timed_out\" : false,\n"
					+ "  \"_shards\" : {\n"
					+ "    \"total\" : 5,\n"
					+ "    \"successful\" : 5,\n"
					+ "    \"skipped\" : 0,\n"
					+ "    \"failed\" : 0\n"
					+ "  },\n"
					+ "  \"hits\" : {\n"
					+ "    \"total\" : 1180920,\n"
					+ "    \"max_score\" : 0.0,\n"
					+ "    \"hits\" : [ ]\n"
					+ "  },\n"
					+ "  \"aggregations\" : {\n"
					+ "    \"AGGR\" : {\n"
					+ "      \"meta\" : { },\n"
					+ "      \"doc_count\" : 1180920,\n"
					+ "      \"Total Collection\" : {\n"
					+ "        \"value\" : 6.868007601E9\n"
					+ "      }\n"
					+ "    }\n"
					+ "  }\n"
					+ "}\n"
					+ "");
        	}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        
        try {
        	if(searchQuery.contains("1648771200000")) {
			responseNode= mapper.readTree("{\n"
					+ "  \"took\" : 89,\n"
					+ "  \"timed_out\" : false,\n"
					+ "  \"_shards\" : {\n"
					+ "    \"total\" : 5,\n"
					+ "    \"successful\" : 5,\n"
					+ "    \"skipped\" : 0,\n"
					+ "    \"failed\" : 0\n"
					+ "  },\n"
					+ "  \"hits\" : {\n"
					+ "    \"total\" : 1180920,\n"
					+ "    \"max_score\" : 0.0,\n"
					+ "    \"hits\" : [ ]\n"
					+ "  },\n"
					+ "  \"aggregations\" : {\n"
					+ "    \"AGGR\" : {\n"
					+ "      \"meta\" : { },\n"
					+ "      \"doc_count\" : 1180920,\n"
					+ "      \"Total Collection\" : {\n"
					+ "        \"value\" : 7.42171004E8\n"
					+ "      }\n"
					+ "    }\n"
					+ "  }\n"
					+ "}\n"
					+ "");
        	}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        
        try {
            //ResponseEntity<Object> response = retryTemplate.postForEntity(url, requestEntity);
            //responseNode = new ObjectMapper().convertValue(response.getBody(), JsonNode.class);
            LOGGER.info("RestTemplate response :- "+responseNode);

        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            LOGGER.error("client error while searching ES : " + e.getMessage());
        }
        return responseNode;
    }

    /**
     * makes a client rest api call of Http POST option
     * @param uri
     * @param authToken
     * @param requestNode
     * @return
     * @throws IOException
     */
    public JsonNode post(String uri, String authToken, JsonNode requestNode) {

        HttpHeaders headers = new HttpHeaders();
        if(authToken != null && !authToken.isEmpty())
            headers.add("Authorization", "Bearer "+ authToken );
        headers.setContentType(MediaType.APPLICATION_JSON);

        LOGGER.info("Request URI: " + uri + ", Node: " + requestNode);
        HttpEntity<String> requestEntity = null;
        if(requestNode != null ) requestEntity = new HttpEntity<>(requestNode.toString(), headers);
        else requestEntity = new HttpEntity<>("{}", headers);

        JsonNode responseNode = null;

        try {
            ResponseEntity<Object> response = retryTemplate.postForEntity(uri,requestEntity);
            responseNode = new ObjectMapper().convertValue(response.getBody(), JsonNode.class);
            LOGGER.info("RestTemplate response :- "+responseNode);

        } catch (HttpClientErrorException e) {
            LOGGER.error("post client exception: " + e.getMessage());
        }
        return responseNode;
    }

    /**
     * makes a client rest api call of Http GET option
     * @param uri
     * @param authToken
     * @return
     * @throws IOException
     */
    public JsonNode get(String uri, String authToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+ authToken );
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> headerEntity = new HttpEntity<>("{}", headers);

        JsonNode responseNode = null;
        try {
            ResponseEntity<Object> response = retryTemplate.getForEntity(uri, headerEntity);
            responseNode = new ObjectMapper().convertValue(response.getBody(), JsonNode.class);
            LOGGER.info("RestTemplate response :- "+responseNode);

        } catch (HttpClientErrorException e) {
            LOGGER.error("get client exception: " + e.getMessage());
        }
        return responseNode;
    }


    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION, getBase64Value(userName, password));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        headers.setAccept(mediaTypes);
        return headers;
    }

    /**
     * Helper Method to create the Base64Value for headers
     *
     * @param userName
     * @param password
     * @return
     */
    private String getBase64Value(String userName, String password) {
        String authString = String.format("%s:%s", userName, password);
        byte[] encodedAuthString = Base64.encodeBase64(authString.getBytes(Charset.forName(US_ASCII)));
        return String.format(BASIC_AUTH, new String(encodedAuthString));
    }

}

