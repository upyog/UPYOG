package org.egov.dx.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import lombok.extern.slf4j.Slf4j;
import org.egov.dx.web.models.PullDocRequest;
import org.egov.dx.web.models.PullURIRequest;
import org.egov.dx.web.models.SearchCriteria;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class XMLRequestParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public SearchCriteria parsePullURIRequest(String xmlBody, String origin) {
        XStream xstream = configureXStream();
        xstream.processAnnotations(PullURIRequest.class);
        Object obj = xstream.fromXML(xmlBody);
        PullURIRequest request = objectMapper.convertValue(obj, PullURIRequest.class);
        String txn = extractTxn(xmlBody, request.getTxn()); 
        
        // FIX: Check for both propertyId and consumerCode
        String idToSearch = request.getDocDetails().getPropertyId();
        if (idToSearch == null || idToSearch.isEmpty()) {
            idToSearch = request.getDocDetails().getConsumerCode();
        }

        SearchCriteria criteria = SearchCriteria.builder()
            .propertyId(idToSearch) // Pass the resolved ID here
            .city(request.getDocDetails().getCity())
            .connType(request.getDocDetails().getConnType())
            .origin(origin)
            .txn(txn)
            .docType(request.getDocDetails().getDocType())
            .payerName(request.getDocDetails().getFullName())
            .mobile(request.getDocDetails().getMobile())
            .build();
            
        return criteria;
    }

    public SearchCriteria parsePullDocRequest(String xmlBody, String origin) {
        XStream xstream = configureXStream();
        xstream.processAnnotations(PullDocRequest.class);
        PullDocRequest request = (PullDocRequest) xstream.fromXML(xmlBody);
        String txn = extractTxn(xmlBody, request.getTxn());
        SearchCriteria criteria = new SearchCriteria();
        criteria.setURI(request.getDocDetails().getURI());
        criteria.setOrigin(origin);
        criteria.setTxn(txn);
        return criteria;
    }

    private String extractTxn(String xmlBody, String txnAttr) {
        if (txnAttr != null) return txnAttr;
        // Fallback regex split from original
        return xmlBody.split("txn=\"")[1].split("\"")[0];
    }

    private XStream configureXStream() {
        XStream xstream = new XStream();
        xstream.addPermission(NoTypePermission.NONE);
        xstream.addPermission(NullPermission.NULL);
        xstream.addPermission(PrimitiveTypePermission.PRIMITIVES);
        xstream.addPermission(AnyTypePermission.ANY);
        return xstream;
    }
}
