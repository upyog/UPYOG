/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */
package org.egov.collection.web.actions.receipts;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.codehaus.jackson.map.ObjectMapper;
import org.egov.collection.constants.CollectionConstants;
import org.egov.collection.entity.ReceiptHeader;
import org.egov.collection.utils.CollectionsUtil;
import org.egov.eis.service.AssignmentService;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.microservice.models.BillDetail;
import org.egov.infra.microservice.models.BillDetailAdditional;
import org.egov.infra.microservice.models.BusinessService;
import org.egov.infra.microservice.models.Receipt;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.persistence.utils.Page;
import org.egov.infra.utils.DateUtils;
import org.egov.infra.web.struts.actions.SearchFormAction;
import org.egov.infra.web.utils.EgovPaginatedList;
import org.egov.infstr.search.SearchQuery;
import org.egov.infstr.search.SearchQueryHQL;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ParentPackage("egov")
@Results({
        @Result(name = SearchReceiptAction.SUCCESS, location = "searchReceipt.jsp")
})
public class SearchReceiptAction extends SearchFormAction {

    private static final Logger LOGGER = Logger.getLogger(SearchReceiptAction.class);
    private static final long serialVersionUID = 1L;
    private String serviceTypeId = null;
    private Long userId = (long) -1;
    private String instrumentType;
    private String receiptNumber;
    private Date fromDate;
    private Date toDate;
    private Integer searchStatus = -1;
    private String target = "new";
    private String manualReceiptNumber;
    private List resultList = new ArrayList();
    private String serviceClass = "-1";
    private TreeMap<String, String> serviceClassMap = new TreeMap<String, String>();
    private CollectionsUtil collectionsUtil;
    private Integer branchId;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    protected EgovMasterDataCaching masterDataCache;

    @Autowired
    private MicroserviceUtils microserviceUtils;
    
    private String collectionVersion;
    
    private String serviceCategory;
    private transient Map<String, String> serviceCategoryNames = new HashMap<>();
    private transient Map<String, Map<String, String>> serviceTypeMap = new HashMap<>();

    @Autowired
    private AppConfigValueService appConfigValuesService; 

    @Override
    public Object getModel() {
        return null;
    }

    public String getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(final String serviceType) {
        serviceTypeId = serviceType;
    }

    public String getInstrumentType() {
        return instrumentType;
    }

    public void setInstrumentType(final String instrumentType) {
        this.instrumentType = instrumentType;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(final String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(final Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(final Date toDate) {
        this.toDate = toDate;
    }

    @Action(value = "/receipts/searchReceipt-reset")
    public String reset() {
        setPage(1);
        serviceTypeId = "null";
        userId = (long) -1;
        receiptNumber = "";
        fromDate = null;
        toDate = null;
        instrumentType = "";
        searchStatus = -1;
        manualReceiptNumber = "";
        serviceClass = "-1";
        branchId = -1;
        return SUCCESS;
    }

    @Override
    public void prepare() {
        super.prepare();
        // if(searchResult==null)
        // searchResult = new EgovPaginatedList();

        setupDropdownDataExcluding();
        // addDropdownData("instrumentTypeList",
        // getPersistenceService().findAllBy("from InstrumentType i where i.isActive = true order by type"));
        // addDropdownData("userList",
        // getPersistenceService().findAllByNamedQuery(CollectionConstants.QUERY_CREATEDBYUSERS_OF_RECEIPTS));

        // serviceClassMap.putAll(CollectionConstants.SERVICE_TYPE_CLASSIFICATION);
        // serviceClassMap.remove(CollectionConstants.SERVICE_TYPE_PAYMENT);
        // addDropdownData("serviceTypeList", Collections.EMPTY_LIST);
//        addDropdownData("businessCategorylist", microserviceUtils.getBusinessCategories());
        addDropdownData("serviceTypeList", microserviceUtils.getBusinessService("Finance"));
        
        getServiceCategoryList();
        
        // addDropdownData("bankBranchList", collectionsUtil.getBankCollectionBankBranchList());
    }

    private void getServiceCategoryList() {
        List<BusinessService> businessService = microserviceUtils.getBusinessService("Finance");
        for (BusinessService bs : businessService) {
            String[] splitServName = bs.getBusinessService().split(Pattern.quote("."));
            String[] splitSerCode  = bs.getCode().split(Pattern.quote("."));
            if (splitServName.length == 2 && splitSerCode.length == 2) {
                if (!serviceCategoryNames.containsKey(splitSerCode[0]))
                    serviceCategoryNames.put(splitSerCode[0], splitServName[0]);
                if (serviceTypeMap.containsKey(splitSerCode[0])) {
                    serviceTypeMap.get(splitSerCode[0]).put(splitSerCode[1], splitServName[1]);
                } else {
                    Map<String, String> map = new HashMap<>();
                    map.put(splitSerCode[1], splitServName[1]);
                    serviceTypeMap.put(splitSerCode[0], map);
                }
            } else {
                serviceCategoryNames.put(splitSerCode[0], splitServName[0]);
            }
        }
        serviceCategoryNames = serviceCategoryNames.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));

        Map<String, Map<String, String>> sorted = new LinkedHashMap<>();
        serviceTypeMap.forEach((k, v) -> sorted.put(k,
                v.entrySet().stream().sorted(Map.Entry.comparingByValue())
                 .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                         (e1, e2) -> e1, LinkedHashMap::new))));
        serviceTypeMap = sorted;
    }

	@Override
    @Action(value = "/receipts/searchReceipt")
    public String execute() {
        return SUCCESS;
    }

    public List getReceiptStatuses() {
        return persistenceService.findAllBy(
                "from EgwStatus s where moduletype=? and code != ? order by description",
                ReceiptHeader.class.getSimpleName(), CollectionConstants.RECEIPT_STATUS_CODE_PENDING);
    }

    @Override
    @Action(value = "/receipts/searchReceipt-search")
    public String search() {
    	validateSearchParams();
    	if (hasErrors())
    		return SUCCESS;
    		
        target = "searchresult";
        collectionVersion = ApplicationThreadLocals.getCollectionVersion();
        
        

        List<ReceiptHeader> receiptList = new ArrayList<>();
		/*
		 * List<Receipt> receipts = microserviceUtils.searchReciepts("MISCELLANEOUS",
		 * getFromDate(), getToDate(), getServiceTypeId(), (getReceiptNumber() != null
		 * && !getReceiptNumber().isEmpty() && !"".equalsIgnoreCase(getReceiptNumber()))
		 * ? getReceiptNumber() : null);
		 */
        
		/*
		 * String effectiveServiceId = (serviceTypeId != null &&
		 * !serviceTypeId.isEmpty() && !serviceTypeId.equals("-1")) ? serviceCategory +
		 * "." + serviceTypeId : serviceCategory;
		 */
        String effectiveServiceId=null;
        if (serviceCategory != null && !serviceCategory.isEmpty() && !serviceCategory.equals("-1")) {
        	effectiveServiceId= (serviceTypeId != null && !serviceTypeId.isEmpty() && !serviceTypeId.equals("-1"))
                    ? serviceCategory + "." + serviceTypeId
                    : serviceCategory;
        }

        List<Receipt> receipts = microserviceUtils.searchReciepts(
                "MISCELLANEOUS", getFromDate(), getToDate(),
                (effectiveServiceId !=null && !effectiveServiceId.isEmpty() && !effectiveServiceId.equals("-1") ?effectiveServiceId:null),
                (getReceiptNumber() != null && !getReceiptNumber().isEmpty())
                        ? getReceiptNumber() : null);
        

        for (Receipt receipt : receipts) {
        	
        	
            for (org.egov.infra.microservice.models.Bill bill : receipt.getBill()) {

                for (BillDetail billDetail : bill.getBillDetails()) {
                    ReceiptHeader receiptHeader = new ReceiptHeader();
                    JsonNode additionalDetails = receipt.getAdditionalDetails();
                	receiptHeader.setWardNo(additionalDetails.get("wardNo")!=null?additionalDetails.get("wardNo").asText():null);
                    receiptHeader.setFund(additionalDetails.get("fundName").asText());
                    receiptHeader.setPaymentId(receipt.getPaymentId());
                    receiptHeader.setReceiptnumber(billDetail.getReceiptNumber());
                    receiptHeader.setReceiptdate(new Date(billDetail.getReceiptDate()));
                    receiptHeader.setService(microserviceUtils.getBusinessServiceNameByCode(billDetail.getBusinessService()));
                    receiptHeader.setReferencenumber(billDetail.getBillNumber());
                    receiptHeader.setReferenceDesc(additionalDetails.get("narration")!=null?additionalDetails.get("narration").asText():null);
                    receiptHeader.setPaidBy(bill.getPaidBy());
                    receiptHeader.setTotalAmount(billDetail.getTotalAmount());
                    receiptHeader.setCurretnStatus(billDetail.getStatus());
                    receiptHeader.setCurrentreceipttype(billDetail.getReceiptType());
                    if (null != billDetail.getManualReceiptNumber()) {
                        receiptHeader.setManualreceiptnumber(billDetail.getManualReceiptNumber());
                        receiptHeader.setG8data(billDetail.getManualReceiptNumber());
                    }
                    if (billDetail.getManualReceiptDate() != null && billDetail.getManualReceiptDate() != 0) {
                        receiptHeader.setManualreceiptdate(new Date(billDetail.getManualReceiptDate()));
                        if (null != billDetail.getManualReceiptNumber()) {
                            receiptHeader.setG8data(billDetail.getManualReceiptNumber()+"/"+new Date(billDetail.getManualReceiptDate()).toString()); 
                        }
                        else
                            receiptHeader.setG8data(new Date(billDetail.getManualReceiptDate()).toString());
                    }
                    receiptHeader.setModOfPayment(receipt.getInstrument().getInstrumentType().getName());

                    JsonNode jsonNode = billDetail.getAdditionalDetails();
                    BillDetailAdditional additional = null;
                    try {
                        if (null != jsonNode)
                            additional = (BillDetailAdditional) new ObjectMapper().readValue(jsonNode.toString(),
                                    BillDetailAdditional.class);
                    } catch (IOException e) {
                        LOGGER.error("error occured reading value from object mapper" +e.getMessage());
                    }
                    if (null != additional) {
//                        if (null != additional.getBusinessReason()) {
//                            if (additional.getBusinessReason().contains("-")) {
//                                receiptHeader.setService(additional.getBusinessReason().split("-")[0]);
//                            } else {
//                                receiptHeader.setService(additional.getBusinessReason());
//                            }
//                        }

                        if (null != additional.getNarration())
                            receiptHeader.setReferenceDesc(additional.getNarration());
                        if (null != additional.getPayeeaddress())
                            receiptHeader.setPayeeAddress(additional.getPayeeaddress());
                    }

                    receiptList.add(receiptHeader);

                }
            }

        }

        if (searchResult == null) {
            Page page = new Page<ReceiptHeader>(1, receiptList.size(), receiptList);
            searchResult = new EgovPaginatedList(page, receiptList.size());
        } else {
            searchResult.getList().clear();
            searchResult.getList().addAll(receiptList);
        }

        resultList = searchResult.getList();
        return SUCCESS;
    }

	private void validateSearchParams() {
		if (StringUtils.isEmpty(serviceCategory) || serviceCategory.equals("-1"))
	        addActionError(getText("error.select.service.category"));
		if (fromDate != null && toDate != null && !fromDate.equals(toDate) && !fromDate.before(toDate))
			addActionError(getText("common.comparedate.errormessage"));
	}

	/**
     * @return the target
     */
    public String getTarget() {
        return target;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    @Override
    public SearchQuery prepareQuery(final String sortField, final String sortDir) {
        final ArrayList<Object> params = new ArrayList<Object>(0);
        final StringBuilder searchQueryString = new StringBuilder("select distinct receipt ");
        final StringBuilder countQueryString = new StringBuilder("select count(distinct receipt) ");
        final StringBuilder fromString = new StringBuilder(" from org.egov.collection.entity.ReceiptHeader receipt ");
        final String orderByString = " group by receipt.receiptdate,receipt.id  order by receipt.receiptdate desc";

        // Get only those receipts whose status is NOT PENDING
        final StringBuilder criteriaString = new StringBuilder(" where receipt.status.code != ? ");
        params.add(CollectionConstants.RECEIPT_STATUS_CODE_PENDING);

        if (StringUtils.isNotBlank(getInstrumentType())) {
            fromString.append(" inner join receipt.receiptInstrument as instruments ");
            criteriaString.append(" and instruments.instrumentType.type = ? ");
            params.add(getInstrumentType());
        }

        if (StringUtils.isNotBlank(getReceiptNumber())) {
            criteriaString.append(" and upper(receiptNumber) like ? ");
            params.add("%" + getReceiptNumber().toUpperCase() + "%");
        }
        if (StringUtils.isNotBlank(getManualReceiptNumber())) {
            criteriaString.append(" and upper(receipt.manualreceiptnumber) like ? ");
            params.add("%" + getManualReceiptNumber().toUpperCase() + "%");
        }
        if (getSearchStatus() != -1) {
            criteriaString.append(" and receipt.status.id = ? ");
            params.add(getSearchStatus());
        }
        if (getFromDate() != null) {
            criteriaString.append(" and receipt.receiptdate >= ? ");
            params.add(fromDate);
        }
        if (getToDate() != null) {
            criteriaString.append(" and receipt.receiptdate < ? ");
            params.add(DateUtils.add(toDate, Calendar.DATE, 1));
        }
        if (getServiceTypeId() != null) {
            criteriaString.append(" and receipt.service.id = ? ");
            params.add(Long.valueOf(getServiceTypeId()));
        }

        if (!getServiceClass().equals("-1")) {
            criteriaString.append(" and receipt.service.serviceType = ? ");
            params.add(getServiceClass());
        }

        if (getUserId() != -1) {
            criteriaString.append(" and receipt.createdBy.id = ? ");
            params.add(userId);
        }
        if (getBranchId() != -1) {
            criteriaString.append(" and receipt.receiptMisc.depositedBranch.id = ? ");
            params.add(getBranchId());
        }

        final String searchQuery = searchQueryString.append(fromString).append(criteriaString).append(orderByString).toString();
        final String countQuery = countQueryString.append(fromString).append(criteriaString).toString();

        return new SearchQueryHQL(searchQuery, countQuery, params);
    }

    public Integer getSearchStatus() {
        return searchStatus;
    }

    public void setSearchStatus(final Integer searchStatus) {
        this.searchStatus = searchStatus;
    }

    public SearchQuery prepareQuery() {

        return null;
    }

    public String getManualReceiptNumber() {
        return manualReceiptNumber;
    }

    public void setManualReceiptNumber(final String manualReceiptNumber) {
        this.manualReceiptNumber = manualReceiptNumber;
    }

    public List getResultList() {
        return resultList;
    }

    public void setResultList(List resultList) {
        this.resultList = resultList;
    }

    public String getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(String serviceClass) {
        this.serviceClass = serviceClass;
    }

    public TreeMap<String, String> getServiceClassMap() {
        return serviceClassMap;
    }

    public void setServiceClassMap(TreeMap<String, String> serviceClassMap) {
        this.serviceClassMap = serviceClassMap;
    }

    /**
     * @param collectionsUtil the collectionsUtil to set
     */
    public void setCollectionsUtil(final CollectionsUtil collectionsUtil) {
        this.collectionsUtil = collectionsUtil;
    }

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }
    
    public String getCollectionVersion() {
        return collectionVersion;
    }
    
    public void setCollectionVersion(String collectionVersion) {
        this.collectionVersion = collectionVersion;
    }

	public String getServiceCategory() {
		return serviceCategory;
	}

	public void setServiceCategory(String serviceCategory) {
		this.serviceCategory = serviceCategory;
	}

	public Map<String, String> getServiceCategoryNames() {
		return serviceCategoryNames;
	}

	public void setServiceCategoryNames(Map<String, String> m) {
		this.serviceCategoryNames = m;
	}

	public Map<String, Map<String, String>> getServiceTypeMap() {
		return serviceTypeMap;
	}

	public void setServiceTypeMap(Map<String, Map<String, String>> m) {
		this.serviceTypeMap = m;
	}
}
