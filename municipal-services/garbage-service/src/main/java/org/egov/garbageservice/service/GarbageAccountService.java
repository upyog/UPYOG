package org.egov.garbageservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.garbageservice.config.GarbageServiceConfig;
import org.egov.garbageservice.contract.bill.*;
import org.egov.garbageservice.contract.bill.Bill.StatusEnum;
import org.egov.garbageservice.contract.workflow.*;
import org.egov.garbageservice.model.*;
import org.egov.garbageservice.model.contract.OwnerInfo;
import org.egov.garbageservice.producer.GarbageProducer;
import org.egov.garbageservice.repository.*;
import org.egov.garbageservice.repository.DemandRepository;
import org.egov.garbageservice.util.GrbgConstants;
import org.egov.garbageservice.util.GrbgUtils;
import org.egov.garbageservice.util.RequestInfoWrapper;
import org.egov.garbageservice.util.ResponseInfoFactory;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Central business service for garbage account lifecycle, billing integration, and citizen flows.
 * Handles create/update/search, workflow transitions, fee calculation, pay-now, PDF receipts,
 * bill tracker persistence, penalties, arrear generation, and Kafka publish after account changes.
 */
@Service
@Slf4j
public class GarbageAccountService {

    @Autowired
    private GarbageAccountRepository garbageAccountRepository;

    @Autowired
    private GrbgApplicationRepository grbgApplicationRepository;

    @Autowired
    private GrbgDocumentRepository grbgDocumentRepository;

    @Autowired
    private GrbgAddressRepository grbgAddressRepository;

    @Autowired
    private GrbgOldDetailsRepository grbgOldDetailsRepository;

    @Autowired
    private GrbgCollectionUnitRepository grbgCollectionUnitRepository;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private GrbgConstants applicationPropertiesAndConstant;
    
    @Autowired
    private GarbageServiceConfig config;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @Autowired
    private DemandService demandService;

    @Autowired
    private BillService billService;

    @Autowired
    private UserService userService;

    @Autowired
    private GarbageProducer producer;

    @Autowired
    private IdGenRepository idGenRepository;

    // idgen format name and pattern — configured in application.properties, registered in egov-idgen MDMS
    @Value("${egov.idgen.garbageservice.applicationNum.name}")
    private String idgenName;

    @Value("${egov.idgen.garbageservice.applicationNum.format}")
    private String idgenFormat;


    /**
     * Orchestrates the creation of one or more garbage accounts and handles citizen user mapping.
     *
     * <p>This primary entry point performs the following sequential workflow:
     * <ol>
     *   <li>Validates and enriches the incoming account payload (including sub-accounts).</li>
     *   <li>Creates or links eGov user identities for the citizens via {@link UserService}.</li>
     *   <li>Initiates a state transition in the workflow engine via {@link #callWfUpdate}.</li>
     *   <li>Persists the validated, enriched entities (address, documents, etc.) to the repository.</li>
     * </ol>
     *
     * @param createGarbageRequest the request payload containing the garbage accounts to create
     * @return a {@link GarbageAccountResponse} containing the created accounts and response info
     */

    public GarbageAccountResponse create(GarbageAccountRequest createGarbageRequest) {

        RequestInfo info = createGarbageRequest.getRequestInfo();
        List<GarbageAccount> garbageAccounts = new ArrayList<>();

        // payload may omit these flags; @Builder.Default leaves them null on JSON deserialization,
        // so default them here to avoid NPE when unboxed (e.g. !getCreateChildAccountOnly()).
        if (createGarbageRequest.getCreateChildAccountOnly() == null)
            createGarbageRequest.setCreateChildAccountOnly(false);
        if (createGarbageRequest.getFromMigration() == null)
            createGarbageRequest.setFromMigration(false);

        List<GarbageAccount> existingAccounts = new ArrayList<>();

        if (!CollectionUtils.isEmpty(createGarbageRequest.getGarbageAccounts())) {
            for (GarbageAccount garbageAccount : createGarbageRequest.getGarbageAccounts()) {
                // validate and enrich
                validateAndEnrichCreateGarbageAccount(createGarbageRequest, garbageAccount, existingAccounts);
            }

            if (createGarbageRequest != null && !CollectionUtils.isEmpty(createGarbageRequest.getGarbageAccounts())
                    && createGarbageRequest.getGarbageAccounts().stream()
                    .noneMatch(account -> StringUtils.isEmpty(account.getMobileNumber()))) {
                // create user if not exists
                createGarbageRequest = userService.createUser(createGarbageRequest);
            }

            // call workflow
            ProcessInstanceResponse processInstanceResponse = callWfUpdate(createGarbageRequest);

            createGarbageRequest.getGarbageAccounts().forEach(garbageAccount -> {
                if (!CollectionUtils.isEmpty(garbageAccount.getChildGarbageAccounts())) {
                    garbageAccount.getChildGarbageAccounts().stream().forEach(subAccount -> {
                        subAccount.setBusinessService(garbageAccount.getBusinessService());
                        org.egov.garbageservice.model.contract.Role role = org.egov.garbageservice.model.contract.Role.builder()
                                .code("CITIZEN").name("Citizen").build();
                        // map user uuid
                        userService.processGarbageAccount(info, role, subAccount);
                    });
                }
            });

            final GarbageAccountRequest request = createGarbageRequest;
            if (!createGarbageRequest.getCreateChildAccountOnly()) {
                createGarbageRequest.getGarbageAccounts().forEach(garbageAccount -> {
                    // create garbage account
                    garbageAccounts.add(garbageAccountRepository.create(garbageAccount));
                    createGarbageAccountObjects(garbageAccount);
                });
            }
        }

        GarbageAccountResponse garbageAccountResponse = GarbageAccountResponse.builder()
                .responseInfo(responseInfoFactory
                        .createResponseInfoFromRequestInfo(createGarbageRequest.getRequestInfo(), false))
                .garbageAccounts(garbageAccounts).build();
        if (!CollectionUtils.isEmpty(garbageAccounts)) {
            garbageAccountResponse.setResponseInfo(
                    responseInfoFactory.createResponseInfoFromRequestInfo(createGarbageRequest.getRequestInfo(), true));
        }

        return garbageAccountResponse;
    }

    /**
     * Persists all child and relation objects associated with a core garbage account.
     *
     * <p>This method delegates to individual repositories to save the application details,
     * address, old details mapping, collection units, and associated documents.
     *
     * @param garbageAccount the fully enriched garbage account being persisted
     */

    private void createGarbageAccountObjects(GarbageAccount garbageAccount) {
        // create garbage application
        grbgApplicationRepository.create(garbageAccount.getGrbgApplication());

        // create garbage address
        createGarbageAddress(garbageAccount);

        // create old garbage details
        createGarbageOldDetails(garbageAccount);

        // create garbage unit
        createGarbageUnit(garbageAccount);

        // enrich garbage document
        enrichCreateGarbageDocuments(garbageAccount);

        // create garbage documents
        createGarbageDocuments(garbageAccount);
    }

    /**
     * Flattens nested payload structures into the root entity fields expected by the service layer.
     *
     * <p>The incoming API payload structures data hierarchically (e.g., {@code garbageSpecification}
     * and {@code propertyLocation}), but the service and repository layers expect flat fields on the
     * {@link GarbageAccount} or its direct dependents like {@link GrbgAddress} and {@link GrbgCollectionUnit}.
     *
     * @param garbageAccount the garbage account payload to be flattened
     */

    private void mapNewPayloadToFlatFields(GarbageAccount garbageAccount) {
        if (garbageAccount.getGarbageSpecification() != null) {
            GarbageSpecification spec = garbageAccount.getGarbageSpecification();
            garbageAccount.setName(spec.getName());
            garbageAccount.setMobileNumber(spec.getPhoneNumber());
            garbageAccount.setEmailId(spec.getEmail());
            garbageAccount.setGender(spec.getGender());
            if (garbageAccount.getGrbgOldDetails() == null)
                garbageAccount.setGrbgOldDetails(new GrbgOldDetails());
            garbageAccount.getGrbgOldDetails().setOldGarbageId(spec.getOldGarbageId());

            // payload no longer sends grbgCollectionUnits; build one from garbageSpecification.
            // builder() is used so @Builder.Default flags (isbplunit, ismonthlybilling, ...) are applied.
            if (CollectionUtils.isEmpty(garbageAccount.getGrbgCollectionUnits())) {
                garbageAccount.setGrbgCollectionUnits(
                        new ArrayList<>(Collections.singletonList(GrbgCollectionUnit.builder().build())));
            }
            GrbgCollectionUnit unit = garbageAccount.getGrbgCollectionUnits().get(0);
            unit.setCategory(spec.getCategory());
            unit.setSubCategory(spec.getSubCategory());
            unit.setSubCategoryType(spec.getSubCategoryType());
            unit.setIsvariablecalculation(spec.getIsvariablecalculation());
            unit.setIsbulkgeneration(spec.getIsbulkgeneration());
            unit.setNo_of_units(spec.getNo_of_units());
            unit.setUnitType(spec.getTypeOfCollection());
            unit.setOwnerType(spec.getPropertyOwnerType());
            unit.setIsInheritance(spec.getIsInheritance());
            unit.setSpecialCategory(spec.getSpecialCategory());
        }
        if (garbageAccount.getPropertyLocation() != null) {
            PropertyLocation loc = garbageAccount.getPropertyLocation();
            garbageAccount.setPropertyId(loc.getPropertyId());

            // payload no longer sends the addresses block; build a GrbgAddress from
            // propertyLocation. Property-specific fields that have no column on GrbgAddress
            // (houseNo, houseName, streetName, landmark, locality) are kept in additionalDetail.
            if (CollectionUtils.isEmpty(garbageAccount.getAddresses())) {
                ObjectNode addressAdditionalDetail = objectMapper.createObjectNode();
                addressAdditionalDetail.put("houseNo", loc.getHouseNo());
                addressAdditionalDetail.put("houseName", loc.getHouseName());
                addressAdditionalDetail.put("streetName", loc.getStreetName());
                addressAdditionalDetail.put("landmark", loc.getLandmark());
                addressAdditionalDetail.put("locality", loc.getLocality());

                GrbgAddress address = GrbgAddress.builder()
                        .address1(loc.getAddressline1())
                        .address2(loc.getAddressline2())
                        .city(loc.getCity())
                        .pincode(loc.getPincode())
                        .isActive(true)
                        .additionalDetail(addressAdditionalDetail)
                        .build();
                garbageAccount.setAddresses(new ArrayList<>(Collections.singletonList(address)));
            }
        }
        if (garbageAccount.getWorkflow() != null) {
            garbageAccount.setWorkflowAction(garbageAccount.getWorkflow().getAction());
        }
        if (garbageAccount.getApplicationStatus() != null) {
            garbageAccount.setStatus(garbageAccount.getApplicationStatus());
            if (garbageAccount.getGrbgApplication() != null) {
                garbageAccount.getGrbgApplication().setStatus(garbageAccount.getApplicationStatus());
            }
        }

        // persist applicantDetails into additionalDetail (JSONB) so the new payload's
        // applicant block is saved with the account (no dedicated table for it).
        if (!CollectionUtils.isEmpty(garbageAccount.getApplicantDetails())) {
            ObjectNode additionalDetail = (garbageAccount.getAdditionalDetail() != null
                    && garbageAccount.getAdditionalDetail().isObject())
                    ? (ObjectNode) garbageAccount.getAdditionalDetail()
                    : objectMapper.createObjectNode();
            additionalDetail.set("applicantDetails", objectMapper.valueToTree(garbageAccount.getApplicantDetails()));
            garbageAccount.setAdditionalDetail(additionalDetail);
        }
    }

    /**
     * Validates and comprehensively enriches an incoming garbage account prior to creation.
     *
     * <p>The process handles both parent and child (sub) accounts:
     * <ol>
     *   <li>Flattens nested JSON payloads.</li>
     *   <li>If this is a parent account, applies base validation and assigns UUIDs/IDs to all sub-components.</li>
     *   <li>For sub-accounts, iterates and cascades parent details (property ID, tenant) downward.</li>
     * </ol>
     *
     * @param createGarbageRequest the root request object containing contextual info
     * @param garbageAccount       the specific account being enriched
     * @param existingAccounts     a list of currently existing accounts for duplicate checking
     */

    private void validateAndEnrichCreateGarbageAccount(GarbageAccountRequest createGarbageRequest,
                                                       GarbageAccount garbageAccount, List<GarbageAccount> existingAccounts) {
        List<GarbageAccount> parentAccount = new ArrayList<>();

        // map nested payload fields to flat fields before validation and enrichment
        mapNewPayloadToFlatFields(garbageAccount);

        if (!createGarbageRequest.getCreateChildAccountOnly()) {
            // validate create garbage account
            validateGarbageAccount(garbageAccount, existingAccounts);

            // enrich create garbage account
            enrichCreateGarbageAccount(garbageAccount, createGarbageRequest.getRequestInfo());

            // enrich garbage address
            validateAndsEnrichCreateGarbageAddress(garbageAccount);

            // enrich create garbage application
            enrichCreateGarbageApplication(garbageAccount, createGarbageRequest.getRequestInfo());

            // enrich old garbage details
            enrichCreateGarbageOldDetails(garbageAccount);

            // enrich garbage unit
            enrichCreateGarbageUnit(garbageAccount);

            // enrich garbage document
            enrichCreateGarbageDocuments(garbageAccount);
        } else {
            parentAccount = garbageAccountRepository.searchGarbageAccount(SearchCriteriaGarbageAccount.builder()
                    .garbageId(Collections.singletonList(garbageAccount.getGarbageId()))
                    .parentAccount(garbageAccount.getParentAccount()).build(), null);
        }

        // enrich garbage sub accounts
        enrichCreateGarbageSubAccounts(garbageAccount, parentAccount);

        // enrich garbage sub account unit
        enrichCreateSubGarbageAccountUnits(garbageAccount);

        // enrich garbage sub account application
        enrichCreateSubGarbageAccountAddress(garbageAccount);

        // enrich garbage sub account address
        enrichCreateSubGarbageAccountApplication(garbageAccount, parentAccount);
    }

    /**
     * Enriches child accounts with application tracking numbers.
     *
     * <p>Generates sequential application numbers for sub-accounts by appending a counter
     * (e.g., "/1", "/2") to the parent account's core application number.
     *
     * @param garbageAccount the parent garbage account
     * @param parentAccount  the list of parent accounts retrieved from the DB (if adding child to existing parent)
     */

    private void enrichCreateSubGarbageAccountApplication(GarbageAccount garbageAccount,
                                                          List<GarbageAccount> parentAccount) {

        if (!CollectionUtils.isEmpty(garbageAccount.getChildGarbageAccounts())) {
            AtomicLong childCount = new AtomicLong(1L);

            if (!CollectionUtils.isEmpty(parentAccount)
                    && !CollectionUtils.isEmpty(parentAccount.get(0).getChildGarbageAccounts())) {
                parentAccount.get(0).getChildGarbageAccounts().stream().map(ca -> {
                    // Split the application number and get the part after the concatenation
                    String[] parts = ca.getGrbgApplication().getApplicationNo()
                            .split(garbageAccount.getGrbgApplication().getApplicationNo().concat("/"));
                    return parts.length > 1 ? parts[1] : ""; // Return the second part if available, otherwise empty
                }).mapToLong(str -> {
                    try {
                        return Long.parseLong(str); // Try parsing the extracted part as a Long
                    } catch (NumberFormatException e) {
                        return Long.MIN_VALUE; // If parsing fails, return the smallest possible value
                    }
                }).forEach(value -> childCount.updateAndGet(v -> Math.max(v, value))); // Update the AtomicLong with the
                // maximum value

                childCount.getAndIncrement();
            }

            garbageAccount.getChildGarbageAccounts().forEach(subAccount -> {
                GrbgApplication grbgApplication = GrbgApplication.builder().uuid(UUID.randomUUID().toString())
                        .applicationNo(garbageAccount.getGrbgApplication().getApplicationNo().concat("/")
                                .concat(Long.toString(childCount.getAndIncrement())))
                        .status(GrbgConstants.STATUS_INITIATED).garbageId(subAccount.getGarbageId()).build();

                subAccount.setGrbgApplication(grbgApplication);
            });
        }

    }

    /**
     * Copies and enriches address details from the parent account to all its child accounts.
     *
     * @param garbageAccount the parent garbage account containing the child accounts
     */

    private void enrichCreateSubGarbageAccountAddress(GarbageAccount garbageAccount) {

        if (!CollectionUtils.isEmpty(garbageAccount.getChildGarbageAccounts())
                && !CollectionUtils.isEmpty(garbageAccount.getAddresses())) {
            garbageAccount.getChildGarbageAccounts().forEach(subAccount -> {

                List<GrbgAddress> grbgAddresses = new ArrayList<>();
                for (GrbgAddress tempG : garbageAccount.getAddresses()) {
                    grbgAddresses.add(objectMapper.convertValue(tempG, GrbgAddress.class));
                }
                subAccount.setAddresses(grbgAddresses);
                subAccount.getAddresses().stream().forEach(address -> {
                    address.setUuid(UUID.randomUUID().toString());
                    address.setGarbageId(subAccount.getGarbageId());
                });
            });
        }
    }

    /**
     * Enriches collection unit details for all child accounts by assigning UUIDs and active flags.
     *
     * @param garbageAccount the parent garbage account containing the child accounts
     */

    private void enrichCreateSubGarbageAccountUnits(GarbageAccount garbageAccount) {

        if (!CollectionUtils.isEmpty(garbageAccount.getChildGarbageAccounts())) {
            garbageAccount.getChildGarbageAccounts().forEach(subAccount -> {
                subAccount.getGrbgCollectionUnits().stream().forEach(unit -> {
                    unit.setUuid(UUID.randomUUID().toString());
                    unit.setIsActive(true);
                    unit.setGarbageId(subAccount.getGarbageId());
                });
            });
        }
    }

    /**
     * Enriches child garbage accounts by cascading core identifiers from the parent.
     *
     * <p>This assigns UUIDs, garbage IDs, property IDs, tenant contexts, and workflow initialization
     * statuses to all child accounts belonging to the parent.
     *
     * @param garbageAccount the parent garbage account
     * @param parentAccount  the list of parent accounts from the DB
     */

    private void enrichCreateGarbageSubAccounts(GarbageAccount garbageAccount, List<GarbageAccount> parentAccount) {
        if (!CollectionUtils.isEmpty(garbageAccount.getChildGarbageAccounts())) {

            AtomicInteger counter = new AtomicInteger(1);
            for (GarbageAccount subAccount : garbageAccount.getChildGarbageAccounts()) {
                subAccount.setId(garbageAccountRepository.getNextSequence());
                subAccount.setUuid(UUID.randomUUID().toString());
                subAccount.setPropertyId(garbageAccount.getPropertyId());
                subAccount.setTenantId(garbageAccount.getTenantId());
                subAccount.setAdditionalDetail(garbageAccount.getAdditionalDetail());
                subAccount.setGarbageId(garbageAccountRepository.getNextGarbageId());
                subAccount.setStatus(GrbgConstants.STATUS_INITIATED);
                subAccount.setAuditDetails(garbageAccount.getAuditDetails());
                subAccount.setParentAccount(garbageAccount.getUuid());
                subAccount.setIsActive(true);
            }
            garbageAccount.setSubAccountCount((long) counter.get());
        }
    }

    /**
     * Persists collection unit records for the specified garbage account.
     *
     * @param garbageAccount the account whose units should be persisted
     */

    private void createGarbageUnit(GarbageAccount garbageAccount) {
        if (!CollectionUtils.isEmpty(garbageAccount.getGrbgCollectionUnits())) {
            garbageAccount.getGrbgCollectionUnits().stream().forEach(unit -> {
                grbgCollectionUnitRepository.create(unit);
            });
        }
    }

    /**
     * Assigns UUIDs and active flags to collection units prior to persistence.
     *
     * @param garbageAccount the account whose units should be enriched
     */

    private void enrichCreateGarbageUnit(GarbageAccount garbageAccount) {

        if (!CollectionUtils.isEmpty(garbageAccount.getGrbgCollectionUnits())) {
            garbageAccount.getGrbgCollectionUnits().stream().forEach(unit -> {
                unit.setUuid(UUID.randomUUID().toString());
                unit.setIsActive(true);
                unit.setGarbageId(garbageAccount.getGarbageId());
            });
        }
    }

    /**
     * Assigns UUIDs and links the garbage ID for legacy/old detail records.
     *
     * @param garbageAccount the account whose old details are being enriched
     */

    private void enrichCreateGarbageOldDetails(GarbageAccount garbageAccount) {
        if (null != garbageAccount.getGrbgOldDetails()) {
            garbageAccount.getGrbgOldDetails().setUuid(UUID.randomUUID().toString());
            garbageAccount.getGrbgOldDetails().setGarbageId(garbageAccount.getGarbageId());
        }
    }

    /**
     * Persists legacy/old mapping details for the garbage account.
     *
     * @param garbageAccount the account whose old details should be persisted
     */

    private void createGarbageOldDetails(GarbageAccount garbageAccount) {

        if (null != garbageAccount.getGrbgOldDetails()) {
            grbgOldDetailsRepository.create(garbageAccount.getGrbgOldDetails());
        }

    }

    /**
     * Persists address records for the garbage account.
     *
     * @param garbageAccount the account whose addresses should be persisted
     */

    private void createGarbageAddress(GarbageAccount garbageAccount) {

        if (!CollectionUtils.isEmpty(garbageAccount.getAddresses())) {
            garbageAccount.getAddresses().stream().forEach(address -> {
                grbgAddressRepository.create(address);
            });
        }
    }

    /**
     * Validates the presence of mandatory address fields and enriches the records with UUIDs.
     *
     * @param garbageAccount the account whose addresses are being processed
     * @throws CustomException if mandatory address fields or the address block itself is missing
     */

    private void validateAndsEnrichCreateGarbageAddress(GarbageAccount garbageAccount) {
        if (!CollectionUtils.isEmpty(garbageAccount.getAddresses())) {
            garbageAccount.getAddresses().stream().forEach(address -> {

                // validate address — district/ulb/ward dropped from new payload, only address1 required
                if (StringUtils.isEmpty(address.getAddress1())) {
                    throw new CustomException("MISSING_ADDRESS_DETAILS", "Provide mendatory details of address.");
                }

                // enrich address
                address.setUuid(UUID.randomUUID().toString());
                address.setIsActive(true);
                address.setGarbageId(garbageAccount.getGarbageId());
            });
        } else {
            throw new CustomException("MISSING_ADDRESS", "Provide address.");
        }
    }

    /**
     * Persists document references for the garbage account.
     *
     * @param garbageAccount the account whose documents should be persisted
     */

    private void createGarbageDocuments(GarbageAccount garbageAccount) {
        if (!CollectionUtils.isEmpty(garbageAccount.getDocuments())) {
            garbageAccount.getDocuments().stream().forEach(doc -> {
                grbgDocumentRepository.create(doc);
            });
        }
    }

    /**
     * Assigns UUIDs and links table references for documents uploaded with the account.
     *
     * @param garbageAccount the account whose documents are being enriched
     */

    private void enrichCreateGarbageDocuments(GarbageAccount garbageAccount) {
        if (!CollectionUtils.isEmpty(garbageAccount.getDocuments())) {
            garbageAccount.getDocuments().stream().forEach(doc -> {
                doc.setUuid(UUID.randomUUID().toString());
                doc.setTblRefUuid(garbageAccount.getUuid());
                doc.setGarbageId(garbageAccount.getGarbageId());
            });
        }
    }

    /**
     * Generates a unique, platform-standard application number via the IDGen service.
     *
     * @param garbageAccount the account requiring an application number
     * @param requestInfo    the contextual information for the API request
     */

    private void enrichCreateGarbageApplication(GarbageAccount garbageAccount, RequestInfo requestInfo) {

        // call egov-idgen to generate a platform-standard unique application number
        List<String> applicationNumbers = idGenRepository.getIdList(
                requestInfo, garbageAccount.getTenantId(), idgenName, idgenFormat, 1);
        String applicationNumber = applicationNumbers.get(0);

        GrbgApplication grbgApplication = GrbgApplication.builder().uuid(UUID.randomUUID().toString())
                .applicationNo(applicationNumber).status(GrbgConstants.STATUS_INITIATED)
                .garbageId(garbageAccount.getGarbageId()).build();

        garbageAccount.setGrbgApplication(grbgApplication);
    }

    /**
     * Validates the core identity and deduplication rules for a garbage account.
     *
     * @param garbageAccount   the account being validated
     * @param existingAccounts a list of existing accounts to check against for duplicates
     * @throws CustomException if mandatory details are missing or if duplicates are detected
     */

    private void validateGarbageAccount(GarbageAccount garbageAccount, List<GarbageAccount> existingAccounts) {

        // validate nullability
        if (null == garbageAccount || null == garbageAccount.getMobileNumber() || null == garbageAccount.getName()) {
            throw new CustomException("MISSING_GARBAGE_ACCOUNT_DETAILS", "Provide garbage account details.");
        }

        // validate duplicate owner with same properyId

//		}else
        if (StringUtils.isNotEmpty(garbageAccount.getUuid())) // update account condition
        {

            List<GarbageAccount> existingAccounts1 = existingAccounts.stream()
                    .filter(account -> StringUtils.equals(garbageAccount.getUuid(), account.getUuid()))
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(existingAccounts1)) {
                throw new CustomException("GARBAGE_ACCOUNT_NOT_FOUND", "Not able to find garbage account.");
            } else if (existingAccounts1.size() > 1) {
                throw new CustomException("DUPLICATE_GARBAGE_ACCOUNT_FOUND", "Duplicate Garbage account found.");
            }

            if (CollectionUtils.isEmpty(garbageAccount.getChildGarbageAccounts())) {
                // validate child garbage account
                garbageAccount.getChildGarbageAccounts().stream().forEach(childAcc -> {
                    Optional<GarbageAccount> matchingChildAccount = existingAccounts1.get(0).getChildGarbageAccounts()
                            .stream().filter(existingChildAcc -> StringUtils.equals(existingChildAcc.getUuid(),
                                    childAcc.getUuid()))
                            .findFirst();
                    if (!matchingChildAccount.isPresent()) {
                        throw new CustomException("CHILD_GARBAGE_ACCOUNT_NOT_FOUND",
                                "Provide correct uuid for child garbage account.");
                    }
                });
            }

        }

    }

    /**
     * Initializes core auditing and identifier fields for a newly created garbage account.
     *
     * @param garbageAccount the account to enrich
     * @param requestInfo    the contextual information containing user details for auditing
     */

    private void enrichCreateGarbageAccount(GarbageAccount garbageAccount, RequestInfo requestInfo) {

        AuditDetails auditDetails = null;
        if (null != requestInfo && null != requestInfo.getUserInfo()) {
            auditDetails = AuditDetails.builder().createdBy(requestInfo.getUserInfo().getUuid())
                    .createdDate(new Date().getTime()).lastModifiedBy(requestInfo.getUserInfo().getUuid())
                    .lastModifiedDate(new Date().getTime()).build();
            garbageAccount.setAuditDetails(auditDetails);
        }

        // generate garbage_id
        garbageAccount.setId(garbageAccountRepository.getNextSequence());
        garbageAccount.setUuid(UUID.randomUUID().toString());
        garbageAccount.setGarbageId(garbageAccountRepository.getNextGarbageId());
        garbageAccount.setStatus(GrbgConstants.STATUS_INITIATED);
        garbageAccount.setWorkflowAction(GrbgConstants.WORKFLOW_ACTION_APPLY);
        garbageAccount.setParentAccount(null);
        garbageAccount.setIsActive(true);
        garbageAccount.setSubAccountCount(Optional.ofNullable(garbageAccount.getChildGarbageAccounts()).map(List::size)
                .map(Integer::longValue).orElse(0L));

    }

    /**
     * Reconciles a new payload with an existing garbage account during an update operation,
     * preserving audit trails and identifiers.
     *
     * @param newGarbageAccount                the incoming updated account payload
     * @param existingGarbageAccount           the current state of the account retrieved from the DB
     * @param requestInfo                      the contextual information for the API request
     * @param applicationNumberToCurrentStatus a map tracking the workflow status of accounts
     */

    private void enrichUpdateGarbageAccount(GarbageAccount newGarbageAccount, GarbageAccount existingGarbageAccount,
                                            RequestInfo requestInfo, Map<String, String> applicationNumberToCurrentStatus) {

        AuditDetails auditDetails = AuditDetails.builder().build();
        if (null != requestInfo && null != requestInfo.getUserInfo()) {
            auditDetails.setLastModifiedBy(requestInfo.getUserInfo().getUuid());
            auditDetails.setLastModifiedDate(new Date().getTime());
        }
        if (null != existingGarbageAccount.getAuditDetails()) {
            auditDetails.setCreatedBy(existingGarbageAccount.getAuditDetails().getCreatedBy());
            auditDetails.setCreatedDate(existingGarbageAccount.getAuditDetails().getCreatedDate());
        }

        // enrich parent account
        newGarbageAccount.setAuditDetails(auditDetails);
        newGarbageAccount.setId(existingGarbageAccount.getId());
        newGarbageAccount.setGarbageId(existingGarbageAccount.getGarbageId());
        newGarbageAccount.setBusinessService(existingGarbageAccount.getBusinessService());
        newGarbageAccount.setChannel(existingGarbageAccount.getChannel());
        ;

        // enrich child accounts
        if (!CollectionUtils.isEmpty(newGarbageAccount.getChildGarbageAccounts())) {

            newGarbageAccount.getChildGarbageAccounts().stream().forEach(childAccount -> {

                // update case
                if (StringUtils.isNotEmpty(childAccount.getUuid())) {

                    Optional<GarbageAccount> matchingChildAccount = existingGarbageAccount.getChildGarbageAccounts()
                            .stream().filter(existingChildAcc -> StringUtils.equals(existingChildAcc.getUuid(),
                                    childAccount.getUuid()))
                            .findFirst();

                    childAccount.setAuditDetails(AuditDetails.builder()
                            .createdBy(matchingChildAccount.get().getAuditDetails().getCreatedBy())
                            .createdDate(matchingChildAccount.get().getAuditDetails().getCreatedDate())
                            .lastModifiedBy(auditDetails.getLastModifiedBy())
                            .lastModifiedDate(auditDetails.getLastModifiedDate()).build());
                    childAccount.setChannel(matchingChildAccount.get().getChannel());

                } else {
                    // create case
                    childAccount.setAuditDetails(AuditDetails.builder().createdBy(auditDetails.getCreatedBy())
                            .createdDate(new Date().getTime()).build());
                }

                childAccount.setBusinessService(newGarbageAccount.getBusinessService());
            });
        }
    }

    /**
     * Orchestrates the update process for a garbage account, managing child accounts, workflow, and persistence.
     *
     * <p>The update sequence executes the following steps:
     * <ol>
     *   <li>Removes child accounts that are no longer present in the request.</li>
     *   <li>Provisions new child accounts added in the request.</li>
     *   <li>Fetches the existing baseline account from the database.</li>
     *   <li>Transitions the workflow state and captures the new statuses.</li>
     *   <li>Validates and enriches the incoming payload against the existing baseline.</li>
     *   <li>Persists changes, produces Kafka events, and potentially triggers demand/bill generation.</li>
     * </ol>
     *
     * @param updateGarbageRequest the request payload containing the updated account state
     * @return a {@link GarbageAccountResponse} containing the updated accounts and response info
     */

    public GarbageAccountResponse update(GarbageAccountRequest updateGarbageRequest) {

        // remove child garbage account if not in request
        removeChildGarbageAccount(updateGarbageRequest);

        // create child garbage account if new in request
        createChildGarbageAccount(updateGarbageRequest);

        List<GarbageAccount> garbageAccounts = new ArrayList<>();

        // search existing garbage accounts
        Map<Long, GarbageAccount> existingGarbageIdAccountsMap;
        Map<String, GarbageAccount> existingGarbageApplicationAccountsMap;
        try {
            SearchCriteriaGarbageAccount searchCriteriaGarbageAccount = createSearchCriteriaByGarbageAccounts(
                    updateGarbageRequest.getGarbageAccounts(), true);
            existingGarbageIdAccountsMap = searchGarbageAccountMap(searchCriteriaGarbageAccount,
                    updateGarbageRequest.getRequestInfo());
            existingGarbageApplicationAccountsMap = existingGarbageIdAccountsMap.entrySet().stream().collect(
                    Collectors.toMap(a -> a.getValue().getGrbgApplication().getApplicationNo(), b -> b.getValue()));
        } catch (Exception e) {
            throw new CustomException("FAILED_SEARCH_GARBAGE_ACCOUNTS", "Search Garbage account details failed.");
        }

        // load garbage account from backend if workflow = true
        GarbageAccountRequest garbageAccountRequest = loadUpdateGarbageAccountRequestFromMap(updateGarbageRequest,
                existingGarbageApplicationAccountsMap);

        ProcessInstanceResponse processInstanceResponse = null;
        // call workflow
        if (updateGarbageRequest != null && !CollectionUtils.isEmpty(updateGarbageRequest.getGarbageAccounts())
                && updateGarbageRequest.getGarbageAccounts().stream().anyMatch(GarbageAccount::getIsOnlyWorkflowCall)) {
            processInstanceResponse = callWfUpdate(garbageAccountRequest);
        }
        Map<String, String> applicationNumberToCurrentStatus = new HashMap<>();
        if (null != processInstanceResponse) {
            applicationNumberToCurrentStatus = processInstanceResponse.getProcessInstances().stream().collect(Collectors
                    .toMap(ProcessInstance::getBusinessId, instance -> instance.getState().getApplicationStatus()));
        }

        // update garbage account
        if (!CollectionUtils.isEmpty(garbageAccountRequest.getGarbageAccounts())) {
            garbageAccountRequest.getGarbageAccounts().stream().forEach(newGarbageAccount -> {

                // validate garbage account request
                validateGarbageAccount(newGarbageAccount, existingGarbageIdAccountsMap.entrySet().stream()
                        .map(entry -> entry.getValue()).collect(Collectors.toList()));

            });

            for (GarbageAccount newGarbageAccount : garbageAccountRequest.getGarbageAccounts()) {

                // get existing garbage account from map
                GarbageAccount existingGarbageAccount = existingGarbageIdAccountsMap
                        .get(newGarbageAccount.getGarbageId());

                // enrich garbage account
                enrichUpdateGarbageAccount(newGarbageAccount, existingGarbageAccount,
                        updateGarbageRequest.getRequestInfo(), applicationNumberToCurrentStatus);

                // update other objects of garbage account
                updateAndEnrichGarbageAccountObjects(newGarbageAccount, existingGarbageAccount,
                        applicationNumberToCurrentStatus);

                updateGarbageRequest.setGarbageAccounts(
                        Collections.singletonList(newGarbageAccount));

                producer.push(
                        config.getUpdateGarbageAccountTopic(),
                        updateGarbageRequest
                );

                garbageAccounts.add(newGarbageAccount);
            }

        }

        if (!updateGarbageRequest.getFromMigration()) {
            // generate certificate and upload

            // TODO: Uncomment once pdf-service is running

            // generate demand and fetch bill
            generateDemandAndBill(garbageAccountRequest);
        }

        // RESPONSE builder
        GarbageAccountResponse garbageAccountResponse = GarbageAccountResponse.builder()
                .responseInfo(responseInfoFactory
                        .createResponseInfoFromRequestInfo(garbageAccountRequest.getRequestInfo(), false))
                .garbageAccounts(garbageAccounts).build();
        if (!CollectionUtils.isEmpty(garbageAccounts)) {
            garbageAccountResponse.setResponseInfo(responseInfoFactory
                    .createResponseInfoFromRequestInfo(garbageAccountRequest.getRequestInfo(), true));
        }

        return garbageAccountResponse;
    }

    /**
     * Provisions new child garbage accounts added during an update operation.
     *
     * <p>Extracts any child accounts lacking a UUID (indicating they are new) and routes them
     * through the standard creation workflow by recursively invoking {@link #create}.
     *
     * @param updateGarbageRequest the update payload containing potential new child accounts
     * @return the unmodified original request payload
     */

    private GarbageAccountRequest createChildGarbageAccount(GarbageAccountRequest updateGarbageRequest) {
        if (updateGarbageRequest != null && !CollectionUtils.isEmpty(updateGarbageRequest.getGarbageAccounts())
                && updateGarbageRequest.getGarbageAccounts().stream().anyMatch(GarbageAccount::getIsOnlyWorkflowCall)) {
            return null;
        }
        // Check if there are any child garbage accounts with an empty UUID
        boolean hasChildAccountsWithEmptyUuid = updateGarbageRequest.getGarbageAccounts().stream()
                .flatMap(grbgAccount -> grbgAccount.getChildGarbageAccounts().stream())
                .anyMatch(childGrbgAccount -> StringUtils.isEmpty(childGrbgAccount.getUuid()));

        if (hasChildAccountsWithEmptyUuid) {
            // Create a new GarbageAccountRequest, leaving the original
            // `updateGarbageRequest` unchanged
            List<GarbageAccount> updatedGarbageAccounts = updateGarbageRequest.getGarbageAccounts().stream()
                    .map(grbgAccount -> {
                        // Filter child accounts to only include those with an empty UUID
                        List<GarbageAccount> filteredChildGarbageAccounts = grbgAccount.getChildGarbageAccounts()
                                .stream().filter(childGrbgAccount -> StringUtils.isEmpty(childGrbgAccount.getUuid()))
                                .collect(Collectors.toList());

                        // Create a new GarbageAccount instance with the updated child garbage accounts
                        return grbgAccount.toBuilder().childGarbageAccounts(filteredChildGarbageAccounts).build();
                    }).collect(Collectors.toList());

            // Build the new request object with the modified child garbage accounts
            GarbageAccountRequest createChildGarbageAccountRequest = GarbageAccountRequest.builder()
                    .garbageAccounts(updatedGarbageAccounts).requestInfo(updateGarbageRequest.getRequestInfo())
                    .createChildAccountOnly(true).build();

            // Perform the create operation with the new request object
            create(createChildGarbageAccountRequest);
        }

        // Return the original updateGarbageRequest unchanged
        return updateGarbageRequest;
    }

    /**
     * Deletes child garbage accounts that have been removed from the parent account's payload during an update.
     *
     * <p>Compares the child UUIDs in the incoming request with those persisted in the database.
     * Any database child UUID not present in the request is considered removed and is subsequently deleted.
     *
     * @param updateGarbageRequest the update payload containing the current list of desired child accounts
     * @return a {@link GarbageAccountResponse} representing the baseline accounts before deletion
     */

    private GarbageAccountResponse removeChildGarbageAccount(GarbageAccountRequest updateGarbageRequest) {
        if (updateGarbageRequest != null && !CollectionUtils.isEmpty(updateGarbageRequest.getGarbageAccounts())
                && updateGarbageRequest.getGarbageAccounts().stream().anyMatch(GarbageAccount::getIsOnlyWorkflowCall)) {
            return null;
        }

        // Extract all child UUIDs from the updateGarbageRequest
        Set<String> requestChildUuids = updateGarbageRequest.getGarbageAccounts().stream()
                .flatMap(grbgAccount -> grbgAccount.getChildGarbageAccounts().stream()).map(GarbageAccount::getUuid)
                .filter(Objects::nonNull) // Filter out null UUIDs
                .collect(Collectors.toSet());

        // Build the search criteria request
        SearchCriteriaGarbageAccountRequest searchCriteria = SearchCriteriaGarbageAccountRequest.builder()
                .requestInfo(updateGarbageRequest.getRequestInfo()).searchCriteriaGarbageAccount(
                        createSearchCriteriaByGarbageAccounts(updateGarbageRequest.getGarbageAccounts(), true))
                .build();

        // Get the response from the database
        GarbageAccountResponse garbageAccountResponse = searchGarbageAccounts(searchCriteria, false);

        // Map child garbage account UUIDs from the database response
        Map<String, GarbageAccount> dbChildGarbageAccountsMap = garbageAccountResponse.getGarbageAccounts().stream()
                .flatMap(grbgAccount -> grbgAccount.getChildGarbageAccounts().stream())
                .collect(Collectors.toMap(GarbageAccount::getUuid, Function.identity()));

        // Create a set of UUIDs that need to be removed
        Set<String> uuidsToRemove = new HashSet<>(dbChildGarbageAccountsMap.keySet());
        uuidsToRemove.removeAll(requestChildUuids); // Remove those present in the request

        // Create the list of GarbageAccounts to remove based on the UUIDs
        List<GarbageAccount> removeChildGarbageAccounts = uuidsToRemove.stream().map(dbChildGarbageAccountsMap::get)
                .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(removeChildGarbageAccounts)) {
            delete(GarbageAccountRequest.builder().requestInfo(updateGarbageRequest.getRequestInfo())
                    .garbageAccounts(removeChildGarbageAccounts).build());
        }
        return garbageAccountResponse;
    }

    /**
     * Triggers financial workflows for accounts returning to the initiator for payment.
     *
     * <p>Generates a financial demand via the {@link DemandService} and immediately fetches
     * or generates the corresponding bill via the {@link BillService}.
     *
     * @param updateGarbageRequest the update request containing the accounts
     */

    private void generateDemandAndBill(GarbageAccountRequest updateGarbageRequest) {
        updateGarbageRequest.getGarbageAccounts().stream().forEach(account -> {

            if (StringUtils.equalsIgnoreCase(GrbgConstants.WORKFLOW_ACTION_APPROVE,
                    account.getWorkflowAction())) {

                demandService.generateDemand(updateGarbageRequest.getRequestInfo(), account,
                        java.time.LocalDate.now());

                // fetch/create bill
                GenerateBillCriteria billCriteria = GenerateBillCriteria.builder().tenantId(account.getTenantId())
                        .businessService(account.getBusinessService()).consumerCode(account.getGrbgApplicationNumber())
                        .build();
                BillResponse billResponse = billService.generateBill(updateGarbageRequest.getRequestInfo(),
                        billCriteria);

            }
        });
    }

    /**
     * Reconstructs an update request payload by merging incoming workflow actions with existing database state.
     *
     * <p>If the update is strictly a workflow state transition (e.g., Approve, Reject), this method
     * extracts the existing account from the database, applies the new workflow statuses to both
     * parent and child accounts, and returns a synthetic request ready for processing.
     *
     * @param updateGarbageRequest                  the raw incoming update request
     * @param existingGarbageApplicationAccountsMap a map of existing accounts keyed by application number
     * @return a reconstructed {@link GarbageAccountRequest} merging requested state with existing state
     */

    private GarbageAccountRequest loadUpdateGarbageAccountRequestFromMap(GarbageAccountRequest updateGarbageRequest,
                                                                         Map<String, GarbageAccount> existingGarbageApplicationAccountsMap) {

        GarbageAccountRequest garbageAccountRequestTemp = GarbageAccountRequest.builder()
                .requestInfo(updateGarbageRequest.getRequestInfo()).garbageAccounts(new ArrayList<>()).build();

        updateGarbageRequest.getGarbageAccounts().stream().forEach(account -> {

            if (!BooleanUtils.isTrue(account.getIsOnlyWorkflowCall())) {
                org.egov.garbageservice.model.contract.Role role = org.egov.garbageservice.model.contract.Role.builder()
                        .code("CITIZEN").name("Citizen").build();
                userService.processGarbageAccount(updateGarbageRequest.getRequestInfo(), role, account);
                if (null != account.getChildGarbageAccounts()) {
                    account.getChildGarbageAccounts().stream().forEach(childAccount -> {
                        userService.processGarbageAccount(updateGarbageRequest.getRequestInfo(), role, childAccount);
                    });
                }
            }
            if (BooleanUtils.isTrue(account.getIsOnlyWorkflowCall())) {

                Boolean tempBol = account.getIsOnlyWorkflowCall();
                String tempApplicationNo = null != account.getGrbgApplicationNumber()
                        ? account.getGrbgApplicationNumber()
                        : account.getGrbgApplication().getApplicationNo();
                String action = account.getWorkflowAction();
                String status = getStatusOrAction(action, true);
                String comment = account.getWorkflowComment();

                GarbageAccount accountTemp = objectMapper
                        .convertValue(
                                existingGarbageApplicationAccountsMap.get(
                                        null != account.getGrbgApplicationNumber() ? account.getGrbgApplicationNumber()
                                                : account.getGrbgApplication().getApplicationNo()),
                                GarbageAccount.class);
                if (null == accountTemp) {
                    throw new CustomException("FAILED_SEARCH_GARBAGE_ACCOUNTS",
                            "Garbage Account not found to run workflow.");
                }

                accountTemp.setIsOnlyWorkflowCall(tempBol);
                accountTemp.setGrbgApplicationNumber(tempApplicationNo);
                accountTemp.setWorkflowAction(action);
                accountTemp.setWorkflowComment(comment);
                accountTemp.setStatus(status);
                accountTemp.getGrbgApplication().setStatus(status);

                if (!CollectionUtils.isEmpty(accountTemp.getChildGarbageAccounts())) {
                    accountTemp.getChildGarbageAccounts().stream().forEach(child -> {
                        child.setWorkflowAction(action);
                        child.setStatus(status);
                        child.getGrbgApplication().setStatus(status);
                    });
                }

//				accountTemp.setChildGarbageAccounts(null);			// at a time only 1 app no provided for WF

                garbageAccountRequestTemp.getGarbageAccounts().add(accountTemp);
            } else if (StringUtils.equals(account.getWorkflowAction(), GrbgConstants.WORKFLOW_ACTION_APPLY)
                    || StringUtils.equals(account.getWorkflowAction(),
                    GrbgConstants.WORKFLOW_ACTION_EDIT)) {
                // this block will work only when update Account and action is INITIATE
                GarbageAccount accountTemp = objectMapper.convertValue(
                        existingGarbageApplicationAccountsMap.get(account.getGrbgApplication().getApplicationNo()),
                        GarbageAccount.class);
                if (null == accountTemp) {
                    throw new CustomException("FAILED_SEARCH_GARBAGE_ACCOUNTS", "Garbage Account not found to update.");
                }
                account.setGrbgApplication(accountTemp.getGrbgApplication());
                garbageAccountRequestTemp.getGarbageAccounts().add(account);
            } else {
                garbageAccountRequestTemp.getGarbageAccounts().add(account);
            }

        });

        return garbageAccountRequestTemp;
    }

    /**
     * Maps between workflow actions (e.g., "APPROVE") and application statuses (e.g., "APPROVED").
     *
     * @param action     the workflow action or status string to translate
     * @param fetchValue if true, retrieves the status for a given action; if false, retrieves the action for a given status
     * @return the translated string mapping
     */

    public String getStatusOrAction(String action, Boolean fetchValue) {

        Map<String, String> map = new HashMap<>();

        map.put(GrbgConstants.WORKFLOW_ACTION_APPLY, GrbgConstants.STATUS_PENDING_FOR_VERIFICATION);
        map.put(GrbgConstants.WORKFLOW_ACTION_VERIFY, GrbgConstants.STATUS_PENDING_FOR_APPROVAL);
        map.put(GrbgConstants.WORKFLOW_ACTION_APPROVE, GrbgConstants.STATUS_APPROVED);
        map.put(GrbgConstants.WORKFLOW_ACTION_REJECT, GrbgConstants.STATUS_REJECTED);
        map.put(GrbgConstants.WORKFLOW_ACTION_RAISE_QUERY_TO_CITIZEN, GrbgConstants.STATUS_EDIT_APPLICATION);
        map.put(GrbgConstants.WORKFLOW_ACTION_SEND_BACK_TO_VERIFIER, GrbgConstants.STATUS_PENDING_FOR_VERIFICATION);
        map.put(GrbgConstants.WORKFLOW_ACTION_EDIT, GrbgConstants.STATUS_PENDING_FOR_VERIFICATION);

        if (!fetchValue) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (entry.getValue().equals(action)) {
                    return entry.getKey();
                }
            }
        }
        return map.get(action);
    }

    /**
     * Triggers state transitions in the eGov workflow engine for the garbage accounts.
     *
     * <p>Iterates through parent and child accounts, constructing {@link ProcessInstance}
     * payloads based on their workflow actions, and sends them to the workflow service.
     *
     * @param updateGarbageRequest the request containing the accounts and their intended workflow actions
     * @return a {@link ProcessInstanceResponse} containing the updated workflow states
     */

    private ProcessInstanceResponse callWfUpdate(GarbageAccountRequest updateGarbageRequest) {

        ProcessInstanceResponse processInstanceResponse = null;

        if (!CollectionUtils.isEmpty(updateGarbageRequest.getGarbageAccounts())) {

            ProcessInstanceRequest processInstanceRequest = null;
            List<ProcessInstance> processInstances = new ArrayList<>();
            String businessService = null;

            Set<String> userRoles = updateGarbageRequest.getRequestInfo().getUserInfo().getRoles().stream()
                    .map(Role::getCode).collect(Collectors.toSet());

            for (GarbageAccount newGarbageAccount : updateGarbageRequest.getGarbageAccounts()) {

                if (!StringUtils.isEmpty(newGarbageAccount.getBusinessService())) {
                    businessService = newGarbageAccount.getBusinessService();
                } else {
                    if (userRoles.contains(GrbgConstants.USER_TYPE_CITIZEN)) {
                        businessService = GrbgConstants.BUSINESS_SERVICE_GB_CITIZEN;
                    } else {
                        businessService = GrbgConstants.BUSINESS_SERVICE_GB_EMPLOYEE;
                    }
                }

                newGarbageAccount.setBusinessService(businessService);

                if (!updateGarbageRequest.getCreateChildAccountOnly()) {
                    ProcessInstance parentProcessInstance = ProcessInstance.builder()
                            .tenantId(newGarbageAccount.getTenantId()).businessService(businessService)
                            .moduleName(GrbgConstants.WORKFLOW_MODULE_NAME)
                            .businessId(newGarbageAccount.getGrbgApplication().getApplicationNo())
                            .action(null != newGarbageAccount.getWorkflowAction()
                                    ? newGarbageAccount.getWorkflowAction()
                                    : getStatusOrAction(newGarbageAccount.getStatus(), false))
                            .comment(newGarbageAccount.getWorkflowComment()).build();

                    processInstances.add(parentProcessInstance);
                }
                if (!CollectionUtils.isEmpty(newGarbageAccount.getChildGarbageAccounts())) {
                    for (GarbageAccount subAccount : newGarbageAccount.getChildGarbageAccounts()) {
                        String action;
                        if (updateGarbageRequest.getCreateChildAccountOnly()) {
                            action = GrbgConstants.WORKFLOW_ACTION_APPLY;
                        } else {
                            action = null != newGarbageAccount.getWorkflowAction()
                                    ? newGarbageAccount.getWorkflowAction()
                                    : getStatusOrAction(newGarbageAccount.getStatus(), false);
                        }
                            ProcessInstance subProcessInstance = ProcessInstance.builder()
                                    .tenantId(subAccount.getTenantId()).businessService(businessService)
                                    .moduleName(GrbgConstants.WORKFLOW_MODULE_NAME)
                                    .businessId(subAccount.getGrbgApplication().getApplicationNo()).action(action)
                                    .comment(newGarbageAccount.getWorkflowComment()).build();

                            processInstances.add(subProcessInstance);
                        }
                    }

                if (!StringUtils.isEmpty(newGarbageAccount.getWorkflowAction()) && newGarbageAccount.getWorkflowAction()
                        .equalsIgnoreCase(GrbgConstants.WORKFLOW_ACTION_APPROVE)) {
                    newGarbageAccount.setApprovalDate(new Date().getTime());
                    if (!CollectionUtils.isEmpty(newGarbageAccount.getChildGarbageAccounts())) {
                        for (GarbageAccount subAccount : newGarbageAccount.getChildGarbageAccounts()) {
                            subAccount.setApprovalDate(new Date().getTime());
                        }
                    }
                }

            }

            processInstanceRequest = ProcessInstanceRequest.builder().requestInfo(updateGarbageRequest.getRequestInfo())
                    .processInstances(processInstances).build();

            // call workflow
            processInstanceResponse = workflowService.callWf(processInstanceRequest);

        }

        return processInstanceResponse;
    }

    /**
     * Selectively synchronizes and enriches nested entities (addresses, units, old details) during an account update.
     *
     * @param newGarbageAccount                the incoming updated account payload
     * @param existingGarbageAccount           the current baseline account from the database
     * @param applicationNumberToCurrentStatus map tracking updated workflow statuses
     */

    private void updateAndEnrichGarbageAccountObjects(GarbageAccount newGarbageAccount,
                                                      GarbageAccount existingGarbageAccount, Map<String, String> applicationNumberToCurrentStatus) {

        // 1. update application
        if (null != newGarbageAccount.getGrbgApplication()) {
            // enrich application
            if (null != existingGarbageAccount.getGrbgApplication()) {
                newGarbageAccount.getGrbgApplication().setUuid(existingGarbageAccount.getGrbgApplication().getUuid());
            }
            String currentStatus = applicationNumberToCurrentStatus.get(newGarbageAccount.getGrbgApplication().getApplicationNo());
            if (null != currentStatus) {
                newGarbageAccount.getGrbgApplication().setStatus(currentStatus);
            } else if (null != newGarbageAccount.getStatus()) {
                newGarbageAccount.getGrbgApplication().setStatus(newGarbageAccount.getStatus());
            }
        }

        // 2. update commercial details
//			// create commercial details
//				&& StringUtils.isNotEmpty(newGarbageAccount.getGrbgCommercialDetails().getUuid()) && !newGarbageAccount
//			// enrich
//			// update commercial details

        // 3. update grbgOldDetails
        if (null != newGarbageAccount.getGrbgOldDetails()
                && StringUtils.isEmpty(newGarbageAccount.getGrbgOldDetails().getUuid())) {
            // create grbgOldDetails
        } else if (null != newGarbageAccount.getGrbgOldDetails()
                && StringUtils.isNotEmpty(newGarbageAccount.getGrbgOldDetails().getUuid())
                && !newGarbageAccount.getGrbgOldDetails().equals(existingGarbageAccount.getGrbgOldDetails())) {
            // enrich
            if (null != existingGarbageAccount.getGrbgOldDetails()
                    && StringUtils.isNotEmpty(existingGarbageAccount.getGrbgOldDetails().getUuid())) {
                newGarbageAccount.getGrbgOldDetails().setUuid(existingGarbageAccount.getGrbgOldDetails().getUuid());
                newGarbageAccount.getGrbgOldDetails()
                        .setGarbageId(existingGarbageAccount.getGrbgOldDetails().getGarbageId());
                // update grbgOldDetails
            }
        }

        // 4. update grbgCollectionUnits
        updateGrbgCollectionUnits(newGarbageAccount, existingGarbageAccount);

        // 5. update grbgAddresses
        updateGrbgAddress(newGarbageAccount, existingGarbageAccount);

        // 6. update child garbage account
        updateChildGarbageAccounts(newGarbageAccount);

        // 2. update bills
//				bills loop > make list of deleting, updating and creating bills

    }

    /**
     * Cascades updates from a parent garbage account down to its child accounts.
     *
     * @param newGarbageAccount the parent account containing the updated children
     */

    private void updateChildGarbageAccounts(GarbageAccount newGarbageAccount) {
        if (!CollectionUtils.isEmpty(newGarbageAccount.getChildGarbageAccounts())) {
            newGarbageAccount.getChildGarbageAccounts().stream().forEach(child -> {

                if (child.getAdditionalDetail() == null) {
                    child.setAdditionalDetail(newGarbageAccount.getAdditionalDetail());
                }
                // update application

            });
        }
    }

    /**
     * Reconciles address updates by identifying and deactivating removed addresses.
     *
     * @param newGarbageAccount      the incoming updated account payload
     * @param existingGarbageAccount the current baseline account from the database
     */

    private void updateGrbgAddress(GarbageAccount newGarbageAccount, GarbageAccount existingGarbageAccount) {
        // Identify addresses to deactivate
        Map<String, GrbgAddress> grbgAddressesToDeactivate = existingGarbageAccount.getAddresses().stream()
                .filter(existingAddress -> newGarbageAccount.getAddresses().stream()
                        .noneMatch(newAddress -> StringUtils.equals(existingAddress.getUuid(), newAddress.getUuid())))
                .collect(Collectors.toMap(GrbgAddress::getUuid, existingAddress -> existingAddress));

        // Deactivate grbgAddressesToDeactivate
        grbgAddressesToDeactivate.values().forEach(grbgAddress -> {
            grbgAddress.setIsActive(false);
        });

        // Update new GrbgAddresses
    }

    /**
     * Reconciles collection unit updates by identifying and deactivating removed units.
     *
     * @param newGarbageAccount      the incoming updated account payload
     * @param existingGarbageAccount the current baseline account from the database
     */

    private void updateGrbgCollectionUnits(GarbageAccount newGarbageAccount, GarbageAccount existingGarbageAccount) {
        Map<String, GrbgCollectionUnit> grbgCollectionUnitsToDeactivate = existingGarbageAccount
                .getGrbgCollectionUnits().stream()
                .filter(existingUnit -> newGarbageAccount.getGrbgCollectionUnits().stream()
                        .noneMatch(newUnit -> StringUtils.equals(existingUnit.getUuid(), newUnit.getUuid())))
                .collect(Collectors.toMap(GrbgCollectionUnit::getUuid, existingUnit -> existingUnit));

        // deactivate grbgCollectionUnitsToDeactivate
        grbgCollectionUnitsToDeactivate.entrySet().stream().forEach(map -> {
            GrbgCollectionUnit grbgCollectionUnit = map.getValue();
            grbgCollectionUnit.setIsActive(false);
        });

        // update new GrbgCollectionUnits
    }

    /**
     * Retrieves a batch of garbage accounts and maps them by their internal Garbage ID.
     *
     * @param searchCriteriaGarbageAccount the criteria to search by (typically IDs or application numbers)
     * @param requestInfo                  the contextual information for the API request
     * @return a {@link Map} linking internal Garbage IDs to their respective {@link GarbageAccount} objects
     */

    private Map<Long, GarbageAccount> searchGarbageAccountMap(SearchCriteriaGarbageAccount searchCriteriaGarbageAccount,
                                                              RequestInfo requestInfo) {

        SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest = SearchCriteriaGarbageAccountRequest
                .builder().searchCriteriaGarbageAccount(searchCriteriaGarbageAccount).requestInfo(requestInfo).build();

        GarbageAccountResponse garbageAccountResponse = searchGarbageAccounts(searchCriteriaGarbageAccountRequest,
                false);

        Map<Long, GarbageAccount> existingGarbageAccountsMap = new HashMap<>();
        garbageAccountResponse.getGarbageAccounts().stream().forEach(account -> {
            existingGarbageAccountsMap.put(account.getGarbageId(), account);
        });

        return existingGarbageAccountsMap;
    }

    /**
     * Constructs a search criteria object by extracting identifiers from a list of garbage accounts.
     *
     * @param garbageAccounts     the list of accounts providing the Garbage IDs and Application Numbers
     * @param searchActiveAccount flag to enforce searching only for active accounts and sub-accounts
     * @return a populated {@link SearchCriteriaGarbageAccount}
     */

    private SearchCriteriaGarbageAccount createSearchCriteriaByGarbageAccounts(List<GarbageAccount> garbageAccounts, Boolean searchActiveAccount) {

        SearchCriteriaGarbageAccount searchCriteriaGarbageAccount = SearchCriteriaGarbageAccount.builder().build();
        if (searchActiveAccount) {
            searchCriteriaGarbageAccount.setIsActiveAccount(true);
            searchCriteriaGarbageAccount.setIsActiveSubAccount(true);
        }

        List<Long> garbageIds = new ArrayList<>();
        List<String> applicationNos = new ArrayList<>();

        garbageAccounts.stream().forEach(grbgAcc -> {
            if (null != grbgAcc.getGarbageId() && 0 <= grbgAcc.getGarbageId()) {
                garbageIds.add(grbgAcc.getGarbageId());
            }
            if (!StringUtils.isEmpty(grbgAcc.getGrbgApplicationNumber())) {
                applicationNos.add(grbgAcc.getGrbgApplicationNumber());
            }
        });

        if (!CollectionUtils.isEmpty(applicationNos)) {
            searchCriteriaGarbageAccount.setApplicationNumber(applicationNos);
        }
        if (!CollectionUtils.isEmpty(garbageIds)) {
            searchCriteriaGarbageAccount.setGarbageId(garbageIds);
        }

        return searchCriteriaGarbageAccount;
    }

    /**
     * Orchestrates the search for garbage accounts based on comprehensive criteria.
     *
     * <p>This method performs several steps:
     * <ol>
     *   <li>Validates and enriches the search criteria based on the user's role (e.g., citizen vs. employee).</li>
     *   <li>Constructs multi-faceted queries (e.g., filtering by 'created by' or specific workflow statuses for employees).</li>
     *   <li>Delegates the actual search to the repository layer (supporting both index and standard DB searches).</li>
     *   <li>Formats the returned entities into a standardized {@link GarbageAccountResponse}.</li>
     * </ol>
     *
     * @param searchCriteriaGarbageAccountRequest the request containing the search criteria
     * @param isIndex                             whether to route the search to an indexed datastore (if applicable)
     * @return a {@link GarbageAccountResponse} containing the matching accounts
     */

    public GarbageAccountResponse searchGarbageAccounts(
            SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest, Boolean isIndex) {

        // FIX: Ensure searchCriteriaGarbageAccount is not null to prevent NPE
        if (searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount() == null) {
            searchCriteriaGarbageAccountRequest.setSearchCriteriaGarbageAccount(new SearchCriteriaGarbageAccount());
        }
        // validate search criteria
        validateAndEnrichSearchGarbageAccount(searchCriteriaGarbageAccountRequest);


        List<GarbageAccount> grbgAccs = new ArrayList<>();
        Map<Integer, SearchCriteriaGarbageAccount> garbageCriteriaMap = new HashMap<>();
        Integer counter = 1;

//

        garbageCriteriaMap.put(counter++, searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount());

        if (isCriteriaEmpty(searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount())
                && null != searchCriteriaGarbageAccountRequest.getRequestInfo()
                && null != searchCriteriaGarbageAccountRequest.getRequestInfo().getUserInfo()
                && searchCriteriaGarbageAccountRequest.getRequestInfo().getUserInfo().getType()
                .equalsIgnoreCase(GrbgConstants.USER_TYPE_EMPLOYEE)) {
            SearchCriteriaGarbageAccount searchCriteriaGarbageAccountCreatedBy = searchCriteriaGarbageAccountRequest
                    .getSearchCriteriaGarbageAccount().copy();
            searchCriteriaGarbageAccountCreatedBy.setUserType(searchCriteriaGarbageAccountRequest.getRequestInfo().getUserInfo().getType());
            if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccountCreatedBy.getStatusList())) {
                searchCriteriaGarbageAccountCreatedBy.setStatusList(null);
            }
            garbageCriteriaMap.put(counter++, searchCriteriaGarbageAccountCreatedBy);
        }

        if (isCriteriaEmpty(searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount())
                && null != searchCriteriaGarbageAccountRequest.getRequestInfo()
                && null != searchCriteriaGarbageAccountRequest.getRequestInfo().getUserInfo()
                && searchCriteriaGarbageAccountRequest.getRequestInfo().getUserInfo().getType()
                .equalsIgnoreCase(GrbgConstants.USER_TYPE_EMPLOYEE)) {

            List<String> rolesWithinTenant = getRolesByTenantId(
                    searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getTenantId(),
                    searchCriteriaGarbageAccountRequest.getRequestInfo().getUserInfo().getRoles());

            for (String role : rolesWithinTenant) {
                if (role.equalsIgnoreCase(GrbgConstants.USER_ROLE_GB_VERIFIER)) {
                    SearchCriteriaGarbageAccount garbageCriteriaFromExcel = searchCriteriaGarbageAccountRequest
                            .getSearchCriteriaGarbageAccount().copy();
                    if (!CollectionUtils.isEmpty(garbageCriteriaFromExcel.getStatusList())) {
                        garbageCriteriaFromExcel.setStatusList(null);
                    }
                    garbageCriteriaFromExcel.setCreatedBy(Collections
                            .singletonList(searchCriteriaGarbageAccountRequest.getRequestInfo().getUserInfo().getUuid()));
                    garbageCriteriaFromExcel.setStatus(Collections.singletonList(GrbgConstants.STATUS_INITIATED));
                    garbageCriteriaFromExcel.setChannels(Collections.singletonList(GrbgConstants.CHANNEL_TYPE_MIGRATE));
                    garbageCriteriaFromExcel.setCreatedBy(Collections
                            .singletonList(searchCriteriaGarbageAccountRequest.getRequestInfo().getUserInfo().getUuid()));
                    garbageCriteriaFromExcel.setUserType(searchCriteriaGarbageAccountRequest.getRequestInfo().getUserInfo().getType());
                    garbageCriteriaMap.put(counter++, garbageCriteriaFromExcel);
                }
            }
        }

        // search garbage account
        if (isIndex)
            grbgAccs = garbageAccountRepository.searchGarbageAccountIndex(
                    searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount(), garbageCriteriaMap);
        else
            grbgAccs = garbageAccountRepository.searchGarbageAccount(
                    searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount(), garbageCriteriaMap);

        GarbageAccountResponse garbageAccountResponse = getSearchResponseFromAccounts(grbgAccs);

        if (CollectionUtils.isEmpty(garbageAccountResponse.getGarbageAccounts())) {
            garbageAccountResponse.setResponseInfo(responseInfoFactory
                    .createResponseInfoFromRequestInfo(searchCriteriaGarbageAccountRequest.getRequestInfo(), false));
        } else {
            garbageAccountResponse.setResponseInfo(responseInfoFactory
                    .createResponseInfoFromRequestInfo(searchCriteriaGarbageAccountRequest.getRequestInfo(), true));
        }

        return garbageAccountResponse;
    }


    /**
     * Wraps a list of fetched garbage accounts into a standardized response object.
     *
     * @param grbgAccs the list of accounts fetched from the datastore
     * @return a {@link GarbageAccountResponse} populated with the accounts and aggregate statistics
     */

    private GarbageAccountResponse getSearchResponseFromAccounts(List<GarbageAccount> grbgAccs) {

        GarbageAccountResponse garbageAccountResponse = GarbageAccountResponse.builder().garbageAccounts(grbgAccs)
                .build();

        processResponse(garbageAccountResponse);

        return garbageAccountResponse;
    }

    /**
     * Validates search criteria and enforces role-based access controls for data retrieval.
     *
     * <p>Ensures that citizens can only search their own records and assigns default
     * status filters for employees based on their specific departmental roles.
     *
     * @param searchCriteriaGarbageAccountRequest the search request payload to validate and enrich
     * @throws CustomException if mandatory search parameters are missing for an employee search
     */

    private void validateAndEnrichSearchGarbageAccount(
            SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest) {
        RequestInfo requestInfo = searchCriteriaGarbageAccountRequest.getRequestInfo();

        if (searchCriteriaGarbageAccountRequest.getIsSchedulerCall()) {
            if (null != searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount()) {
                searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount()
                        .setIsSchedulerCall(searchCriteriaGarbageAccountRequest.getIsSchedulerCall());
            } else {
                searchCriteriaGarbageAccountRequest.setSearchCriteriaGarbageAccount(SearchCriteriaGarbageAccount
                        .builder().isSchedulerCall(searchCriteriaGarbageAccountRequest.getIsSchedulerCall()).build());
            }
        }

        if (null != searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount()) {
            searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount()
                    .setIsUserUuidNull(searchCriteriaGarbageAccountRequest.getIsUserUuidNull());
        }

        if (null != searchCriteriaGarbageAccountRequest.getIsSchedulerCall()
                && !searchCriteriaGarbageAccountRequest.getIsSchedulerCall()) {
            if (null != searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount()) {
                if (CollectionUtils
                        .isEmpty(searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getId())
                        && CollectionUtils.isEmpty(
                        searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getGarbageId())
                        && CollectionUtils.isEmpty(
                        searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getPropertyId())
                        && CollectionUtils.isEmpty(
                        searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getType())
                        && CollectionUtils.isEmpty(
                        searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getName())
                        && CollectionUtils.isEmpty(
                        searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getMobileNumber())
                        && CollectionUtils.isEmpty(searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount()
                        .getApplicationNumber())
                        && null == searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getIsOwner()) {

                    if (null != requestInfo && null != requestInfo.getUserInfo() && StringUtils
                            .equalsIgnoreCase(requestInfo.getUserInfo().getType(), GrbgConstants.USER_TYPE_CITIZEN)) {
                        searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount()
                        ;
                    } else if (null != requestInfo && null != requestInfo.getUserInfo() && StringUtils
                            .equalsIgnoreCase(requestInfo.getUserInfo().getType(), GrbgConstants.USER_TYPE_EMPLOYEE)) {

                        List<String> listOfStatus = getAccountStatusListByRoles(
                                searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount().getTenantId(),
                                requestInfo.getUserInfo().getRoles());
                        if (!CollectionUtils.isEmpty(listOfStatus) && isCriteriaEmpty(
                                searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount())) {
                            searchCriteriaGarbageAccountRequest.getSearchCriteriaGarbageAccount()
                                    .setStatusList(listOfStatus);
                        }
                    } else {
                        throw new CustomException("MISSING_SEARCH_PARAMETER",
                                "Provide the parameters to search garbage accounts.");
                    }
                }
            } else if (null != requestInfo && null != requestInfo.getUserInfo() && StringUtils
                    .equalsIgnoreCase(requestInfo.getUserInfo().getType(), GrbgConstants.USER_TYPE_CITIZEN)) {
                searchCriteriaGarbageAccountRequest
                        .setSearchCriteriaGarbageAccount(SearchCriteriaGarbageAccount.builder()
                                .build());
            }
        }

    }

    /**
     * Evaluates whether a search criteria object is completely devoid of filtering parameters.
     *
     * @param criteria the search criteria to evaluate
     * @return {@code true} if all major filtering fields are empty or null; {@code false} otherwise
     */

    public Boolean isCriteriaEmpty(SearchCriteriaGarbageAccount criteria) {
        Boolean isCriteriaEmpty = CollectionUtils.isEmpty(criteria.getId())
                && CollectionUtils.isEmpty(criteria.getGarbageId()) && CollectionUtils.isEmpty(criteria.getPropertyId())
                && CollectionUtils.isEmpty(criteria.getUuid()) && CollectionUtils.isEmpty(criteria.getType())
                && CollectionUtils.isEmpty(criteria.getName()) && CollectionUtils.isEmpty(criteria.getMobileNumber())
                && CollectionUtils.isEmpty(criteria.getApplicationNumber())
                && CollectionUtils.isEmpty(criteria.getCreatedBy()) && CollectionUtils.isEmpty(criteria.getStatus());
        return isCriteriaEmpty;
    }

    /**
     * Determines which workflow statuses an employee is permitted to view based on their roles.
     *
     * @param tenantId the tenant ID to scope the roles
     * @param roles    the list of roles assigned to the user
     * @return a list of application statuses (e.g., PENDINGFORAPPROVAL) relevant to the user's roles
     */

    private List<String> getAccountStatusListByRoles(String tenantId, List<Role> roles) {

        List<String> rolesWithinTenant = getRolesByTenantId(tenantId, roles);
        Set<String> statusWithRoles = new HashSet();

        rolesWithinTenant.stream().forEach(role -> {

            if (StringUtils.equalsIgnoreCase(role, GrbgConstants.USER_ROLE_GB_VERIFIER)) {
                statusWithRoles.add(GrbgConstants.STATUS_PENDING_FOR_VERIFICATION);
                statusWithRoles.add(GrbgConstants.STATUS_EDIT_APPLICATION);
                statusWithRoles.add(GrbgConstants.STATUS_PENDING_FOR_APPROVAL);
                statusWithRoles.add(GrbgConstants.STATUS_APPROVED);
                statusWithRoles.add(GrbgConstants.STATUS_REJECTED);
            } else if (StringUtils.equalsIgnoreCase(role, GrbgConstants.USER_ROLE_GB_APPROVER)) {
                statusWithRoles.add(GrbgConstants.STATUS_PENDING_FOR_APPROVAL);
                statusWithRoles.add(GrbgConstants.STATUS_APPROVED);
                statusWithRoles.add(GrbgConstants.STATUS_EDIT_APPLICATION);
                statusWithRoles.add(GrbgConstants.STATUS_REJECTED);
            }

        });

        return new ArrayList<>(statusWithRoles);
    }

    /**
     * Filters a user's role list to extract only those applicable to a specific tenant.
     *
     * @param tenantId the target tenant ID
     * @param roles    the user's complete list of roles across all tenants
     * @return a list of role codes active in the specified tenant
     */

    private List<String> getRolesByTenantId(String tenantId, List<Role> roles) {

        List<String> roleCodes = roles.stream()
                .filter(role -> StringUtils.equalsIgnoreCase(role.getTenantId(), tenantId)).map(role -> role.getCode())
                .collect(Collectors.toList());
        return roleCodes;
    }

    /**
     * Calculates and populates aggregate dashboard statistics on a search response.
     *
     * <p>Counts accounts across various workflow states (e.g., Initiated, Pending Payment,
     * Approved, Rejected) to provide high-level metrics alongside the actual data.
     *
     * @param response the search response to mutate with calculated statistics
     */

    public void processResponse(GarbageAccountResponse response) {

        // categorize each accounts
        if (!CollectionUtils.isEmpty(response.getGarbageAccounts())) {
            response.setApplicationCount((int) response.getGarbageAccounts().stream().count());
            response.setApplicationInitiated((int) response
                    .getGarbageAccounts().stream().filter(account -> StringUtils
                            .equalsIgnoreCase(applicationPropertiesAndConstant.STATUS_INITIATED, account.getStatus()))
                    .count());
            response.setApplicationApplied((int) response.getGarbageAccounts().stream()
                    .filter(account -> StringUtils.equalsAnyIgnoreCase(account.getStatus(),
                            applicationPropertiesAndConstant.STATUS_PENDING_FOR_VERIFICATION,
                            applicationPropertiesAndConstant.STATUS_PENDING_FOR_APPROVAL,
                            applicationPropertiesAndConstant.STATUS_EDIT_APPLICATION))
                    .count());
            response.setApplicationPendingForPayment(0);
            response.setApplicationRejected((int) response
                    .getGarbageAccounts().stream().filter(account -> StringUtils
                            .equalsIgnoreCase(applicationPropertiesAndConstant.STATUS_REJECTED, account.getStatus()))
                    .count());
            response.setApplicationApproved((int) response
                    .getGarbageAccounts().stream().filter(account -> StringUtils
                            .equalsIgnoreCase(applicationPropertiesAndConstant.STATUS_APPROVED, account.getStatus()))
                    .count());
        }

    }

    /**
     * Soft-deletes or completely removes garbage accounts based on the provided request.
     *
     * @param deleteGarbageRequest the request containing the accounts to delete
     * @return currently returns null (placeholder for future response structure)
     */

    public GarbageAccountResponse delete(GarbageAccountRequest deleteGarbageRequest) {

        deleteGarbageRequest.getGarbageAccounts().stream().forEach(garbageAccount -> {
//			deleteGarbageAccountObjects(garbageAccount); // TODO
            garbageAccountRepository.delete(garbageAccount);
        });

        return null;
    }

}