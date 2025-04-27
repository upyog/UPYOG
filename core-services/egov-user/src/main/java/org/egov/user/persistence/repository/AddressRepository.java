package org.egov.user.persistence.repository;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.*;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.egov.user.domain.model.Address;
import org.egov.user.domain.model.AddressSearchCriteria;
import org.egov.user.domain.model.enums.AddressType;
import org.egov.user.domain.service.utils.UserConstants;
import org.egov.user.repository.builder.AddressQueryBuilder;
import org.egov.user.repository.rowmapper.AddressRowMapper;
import org.egov.user.repository.rowmapper.AddressRowMapperV2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class AddressRepository {

    public static final String GET_ADDRESS_BY_USERID = "select * from eg_user_address where userid=:userId and tenantid =:tenantId";
    public static final String INSERT_ADDRESS_BYUSERID = "insert into eg_user_address (id,type,address,city,pincode,userid,tenantid,createddate,lastmodifieddate,createdby,lastmodifiedby) "
            + "values(:id,:type,:address,:city,:pincode,:userid,:tenantid,:createddate,:lastmodifieddate,:createdby,:lastmodifiedby)";
    public static final String SELECT_NEXT_SEQUENCE = "select nextval('seq_eg_user_address')";
    public static final String DELETE_ADDRESSES = "delete from eg_user_address where id IN (:id)";
    public static final String DELETE_ADDRESS = "delete from eg_user_address where id=:id";
    public static final String UPDATE_ADDRESS_BYIDAND_TENANTID = "update eg_user_address set address=:address,city=:city,pincode=:pincode,lastmodifiedby=:lastmodifiedby,lastmodifieddate=:lastmodifieddate, status=:status where userid=:userid and tenantid=:tenantid and type=:type";
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private JdbcTemplate jdbcTemplate;
    private AddressQueryBuilder addressQueryBuilder;

    public AddressRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate, JdbcTemplate jdbcTemplate,
                             AddressQueryBuilder addressQueryBuilder) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.addressQueryBuilder =  addressQueryBuilder;
    }

    /**
     * api will create the address for particular userId and tenantId.
     *
     * @param address
     * @param userId
     * @param tenantId
     * @return
     */
    public Address create(Address address, Long userId, String tenantId) {
        Map<String, Object> addressInputs = new HashMap<String, Object>();

        addressInputs.put("id", getNextSequence());
        addressInputs.put("type", address.getType().toString());
        addressInputs.put("address", address.getAddress());
        addressInputs.put("city", address.getCity());
        addressInputs.put("pincode", address.getPinCode());
        addressInputs.put("userid", userId);
        addressInputs.put("tenantid", tenantId);
        addressInputs.put("createddate", new Date());
        addressInputs.put("lastmodifieddate", new Date());
        addressInputs.put("createdby", userId);
        addressInputs.put("lastmodifiedby", address.getUserId());
        addressInputs.put("status", UserConstants.ADDRESS_ACTIVE_STATUS); // Set status to active

        namedParameterJdbcTemplate.update(INSERT_ADDRESS_BYUSERID, addressInputs);
        return address;
    }

    /**
     * api will give the next sequence generator for user-address.
     *
     * @return
     */
    private Long getNextSequence() {
        return jdbcTemplate.queryForObject(SELECT_NEXT_SEQUENCE, Long.class);
    }

    /**
     * api will update the address based on userId and tenantId
     *
     * @param domainAddresses
     * @param userId
     * @param tenantId
     */
    public void update(List<Address> domainAddresses, Long userId, String tenantId) {

        final Map<String, Object> Map = new HashMap<String, Object>();
        Map.put("userId", userId);
        Map.put("tenantId", tenantId);

        List<Address> entityAddresses = namedParameterJdbcTemplate.query(GET_ADDRESS_BY_USERID, Map,
                new AddressRowMapper());

        if (isEmpty(domainAddresses) && isEmpty(entityAddresses)) {
            return;
        }

        conditionallyDeleteAllAddresses(domainAddresses, entityAddresses);
        deleteRemovedAddresses(domainAddresses, entityAddresses);
        createNewAddresses(domainAddresses, entityAddresses, userId, tenantId);
        updateAddresses(domainAddresses, entityAddresses, userId);
    }

    /**
     * api will fetch the user address by userId And tenantId
     *
     * @param userId
     * @param tenantId
     * @return
     */
    public List<Address> find(Long userId, String tenantId) {

        final Map<String, Object> Map = new HashMap<String, Object>();
        Map.put("userId", userId);
        Map.put("tenantId", tenantId);

        List<Address> addressList = namedParameterJdbcTemplate.query(GET_ADDRESS_BY_USERID, Map,
                new AddressRowMapper());
        return addressList;
    }

    /**
     * api will update the user addresses.
     *
     * @param domainAddresses
     * @param entityAddresses
     * @param userId
     */
    private void updateAddresses(List<Address> domainAddresses, List<Address> entityAddresses, Long userId) {
        Map<String, Address> typeToEntityAddressMap = toMap(entityAddresses);
        domainAddresses.forEach(address -> updateAddress(typeToEntityAddressMap, address, userId));
    }


    /**
     * update the address for particular userId
     *
     * @param typeToEntityAddressMap
     * @param address
     * @param userId
     */
    private void updateAddress(Map<String, Address> typeToEntityAddressMap, Address address, Long userId) {
        final Address matchingEntityAddress = typeToEntityAddressMap.getOrDefault(address.getType().name(), null);

        if (matchingEntityAddress == null) {
            return;
        }
        Map<String, Object> addressInputs = new HashMap<String, Object>();

        addressInputs.put("address", address.getAddress());
        addressInputs.put("type", address.getType().toString());
        addressInputs.put("city", address.getCity());
        addressInputs.put("pincode", address.getPinCode());
        addressInputs.put("userid", userId);
        addressInputs.put("tenantid", matchingEntityAddress.getTenantId());
        addressInputs.put("lastmodifieddate", new Date());
        addressInputs.put("lastmodifiedby", userId);
        addressInputs.put("status", UserConstants.ADDRESS_ACTIVE_STATUS);

        namedParameterJdbcTemplate.update(UPDATE_ADDRESS_BYIDAND_TENANTID, addressInputs);
    }

    private Map<String, Address> toMap(List<Address> entityAddresses) {
        return entityAddresses.stream().collect(Collectors.toMap(Address::getAddressType, address -> address));
    }

    private void conditionallyDeleteAllAddresses(List<Address> domainAddresses, List<Address> entityAddresses) {
        if (domainAddresses == null) {
            deleteEntityAddresses(entityAddresses);
        }
    }

    private void deleteEntityAddresses(List<Address> entityAddresses) {

        List<Long> ids = entityAddresses.parallelStream().map(Address::getId).collect(Collectors.toList());
        final Map<String, Object> adressInputs = new HashMap<String, Object>();
        adressInputs.put("id", ids);
        if (ids != null && !ids.isEmpty()) {
            namedParameterJdbcTemplate.update(DELETE_ADDRESSES, adressInputs);
        }
    }

    private void deleteEntityAddress(Address adress) {

        final Map<String, Object> adressInputs = new HashMap<String, Object>();
        adressInputs.put("id", adress.getId());
        namedParameterJdbcTemplate.update(DELETE_ADDRESS, adressInputs);
    }

    private void deleteRemovedAddresses(List<Address> domainAddresses,
                                        List<Address> entityAddresses) {
        final List<String> addressTypesToRetain = getAddressTypesToRetain(domainAddresses);
        entityAddresses.stream().filter(address -> !addressTypesToRetain.contains(address.getType().name()))
                .forEach(address -> deleteEntityAddress(address));
    }

    private void createNewAddresses(List<Address> domainAddresses, List<Address> entityAddresses, Long userId,
                                    String tenantId) {
        final List<AddressType> addressTypesToCreate = getAddressTypesToCreate(domainAddresses, entityAddresses);
        domainAddresses.stream().filter(address -> addressTypesToCreate.contains(address.getType()))
                .forEach(address -> create(address, userId, tenantId));
    }

    private List<String> getAddressTypesToRetain(List<Address> domainAddresses) {
        return domainAddresses.stream().map(address -> address.getType().name()).collect(Collectors.toList());
    }

    private List<AddressType> getAddressTypesToCreate(List<Address> domainAddresses, List<Address> entityAddresses) {
        final List<String> entityAddressTypes = entityAddresses.stream().map(Address::getAddressType)
                .collect(Collectors.toList());

        return domainAddresses.stream().filter(address -> !entityAddressTypes.contains(address.getType().name()))
                .map(Address::getType).collect(Collectors.toList());
    }

    /**
     * Creates a new address record (v2) for the specified user. The userId is determined by the provided UUID.
     *
     * @param address  the Address object containing the address details to be saved
     * @param userId   the numeric user ID corresponding to the provided UUID
     * @param tenantId the tenant identifier
     * @return the created Address object as returned by the repository after insertion
     * @throws DuplicateKeyException if an address with the same unique constraints already exists
     */
    public Address createAddressV2(Address address, Long userId, String tenantId) {
        Map<String, Object> addressInputs = new HashMap<>();
        Long addressId = getNextSequence();  // Generate the next unique address ID from the sequence
        addressInputs.put("id", addressId);
        addressInputs.put("type", address.getType().toString());
        addressInputs.put("address", address.getAddress());
        addressInputs.put("city", address.getCity());
        addressInputs.put("pincode", address.getPinCode());
        addressInputs.put("userid", userId);
        addressInputs.put("tenantid", tenantId);
        addressInputs.put("createddate", new Date());
        addressInputs.put("lastmodifieddate", new Date());
        addressInputs.put("createdby", userId);
        addressInputs.put("lastmodifiedby", address.getUserId());
        // New fields for v2 insert: additional address details
        addressInputs.put("address2", address.getAddress2());
        addressInputs.put("houseNumber", address.getHouseNumber());
        addressInputs.put("houseName", address.getHouseName());
        addressInputs.put("streetName", address.getStreetName());
        addressInputs.put("landmark", address.getLandmark());
        addressInputs.put("locality", address.getLocality());
        addressInputs.put("status", UserConstants.ADDRESS_ACTIVE_STATUS); // Set status to active

        try {
            String query = AddressQueryBuilder.INSERT_ADDRESS_BYUSERID_V2;
            namedParameterJdbcTemplate.update(query, addressInputs);
        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException("Address already exists for user with type: " + address.getType());
        } catch (DataIntegrityViolationException e) {
            // Handle foreign key constraint violation specifically
            if (e.getMessage() != null && e.getMessage().contains("eg_user_address_user_fkey")) {
                throw new IllegalArgumentException("User not found: Cannot add address for non-existent user with ID " + userId + " and tenantId '" + tenantId + "'");
            }
            throw new RuntimeException("Data integrity violation while creating address: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while creating address: " + e.getMessage(), e);
        }
        address.setUserId(userId);
        address.setId(addressId);
        return address;
    }

    /**
     * Updates the status of an address to inactive based on the provided addressId.
     *
     * @param addressId the unique identifier of the address to be updated
     */
    public void updateAddressV2(Long addressId) {
        final Map<String, Object> params = new HashMap<>();
        params.put("addressId", addressId);
        params.put("status", UserConstants.ADDRESS_INACTIVE_STATUS);
        String query = AddressQueryBuilder.UPDATE_ADDRESS_STATUS_INACTIVE;
        namedParameterJdbcTemplate.update(query, params);
    }


    public List<Address> getAddressV2(AddressSearchCriteria addressSearchCriteria) {

        final List<Object> preparedStatementValues = new ArrayList<>();
        String queryStr = addressQueryBuilder.getAddressQuery(addressSearchCriteria, preparedStatementValues);
        // Add debug logging for the query AND prepared statement values
        log.debug("Query: {}", queryStr);
        log.debug("Prepared Statement Values: {}", preparedStatementValues);

        List<Address> addresses = jdbcTemplate.query(queryStr, preparedStatementValues.toArray(), new AddressRowMapperV2());

        return addresses;
    }
}
