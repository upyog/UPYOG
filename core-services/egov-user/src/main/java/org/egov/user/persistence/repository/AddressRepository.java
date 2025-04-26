package org.egov.user.persistence.repository;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.egov.user.domain.model.Address;
import org.egov.user.domain.model.enums.AddressType;
import org.egov.user.repository.rowmapper.AddressRowMapper;
import org.egov.user.repository.rowmapper.AddressRowMapperV2;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AddressRepository {

    public static final String GET_ADDRESS_BY_USERID = "select * from eg_user_address where userid=:userId and tenantid =:tenantId";
    public static final String INSERT_ADDRESS_BYUSERID = "insert into eg_user_address (id,type,address,city,pincode,userid,tenantid,createddate,lastmodifieddate,createdby,lastmodifiedby) "
            + "values(:id,:type,:address,:city,:pincode,:userid,:tenantid,:createddate,:lastmodifieddate,:createdby,:lastmodifiedby)";
    public static final String SELECT_NEXT_SEQUENCE = "select nextval('seq_eg_user_address')";
    public static final String DELETE_ADDRESSES = "delete from eg_user_address where id IN (:id)";
    public static final String DELETE_ADDRESS = "delete from eg_user_address where id=:id";
    public static final String UPDATE_ADDRESS_BYIDAND_TENANTID = "update eg_user_address set address=:address,city=:city,pincode=:pincode,lastmodifiedby=:lastmodifiedby,lastmodifieddate=:lastmodifieddate where userid=:userid and tenantid=:tenantid and type=:type";
    private static final String GET_ADDRESS_BY_USER_UUID = "select uadd.id as id, uadd.type as type, uadd.address as address, uadd.city as city, uadd.pincode as pincode, uadd.userid as userid,\n" +
            "uadd.tenantid as tenantid, uadd.createddate as createddate, uadd.lastmodifieddate as lastmodifieddate, uadd.createdby as createdby,\n" +
            // Adding new fields in Address select query: address2, houseNumber, houseName, streetName, landmark, locality
            "uadd.address2, uadd.houseNumber, uadd.houseName, uadd.streetName, uadd.landmark, uadd.locality, " +
            "uadd.lastmodifiedby as lastmodifiedby from eg_user_address uadd left join eg_user usr on uadd.userid = usr.id\n" +
            "where usr.uuid=:uuid and uadd.tenantid=:tenantId";
    // Adding new fields in Address insert query: address2, houseNumber, houseName, streetName, landmark, locality
    public static final String INSERT_ADDRESS_BYUSERID_V2 = "insert into eg_user_address (id,type,address,city,pincode,userid,tenantid,createddate,lastmodifieddate,createdby,lastmodifiedby, address2, houseNumber, houseName, streetName, landmark, locality) "
            + "values(:id,:type,:address,:city,:pincode,:userid,:tenantid,:createddate,:lastmodifieddate,:createdby,:lastmodifiedby, :address2, :houseNumber, :houseName, :streetName, :landmark, :locality)";
    public static final String UPDATE_ADDRESS_BYIDAND_TENANTID_V2 = "UPDATE eg_user_address SET address = :address, city = :city, pincode = :pincode, lastmodifiedby = :lastmodifiedby, " +
            "lastmodifieddate = :lastmodifieddate, address2 = :address2, houseNumber = :houseNumber, houseName = :houseName, streetName = :streetName, landmark = :landmark," +
            " locality = :locality " +
            "WHERE userid = :userid AND tenantid = :tenantid AND type = :type";
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private JdbcTemplate jdbcTemplate;

    public AddressRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate, JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }
    public String getInsertUserAddressQuery() {
        return "INSERT INTO eg_user_address (id, version, createddate, lastmodifieddate, createdby, lastmodifiedby, " +
                "type, address, city, pincode, userid, tenantid, housenumber, housename, streetname, address2, " +
                "landmark, locality) VALUES (:id, :version, :createddate, :lastmodifieddate, :createdby, :lastmodifiedby, " +
                ":type, :address, :city, :pincode, " +
                "(SELECT id FROM eg_user WHERE uuid=:userUuid AND tenantid=:tenantid), :tenantid, " +
                ":housenumber, :housename, :streetname, :address2, :landmark, :locality)";
    }

    public String getUpdateUserAddressQuery() {
        return "UPDATE eg_user_address SET version=:version, lastmodifieddate=:lastmodifieddate, lastmodifiedby=:lastmodifiedby, " +
                "type=:type, address=:address, city=:city, pincode=:pincode, housenumber=:housenumber, housename=:housename, " +
                "streetname=:streetname, address2=:address2, landmark=:landmark, locality=:locality " +
                "WHERE id=:id";
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
     * Fetches the user's addresses (both permanent and correspondence) by the user's UUID and tenantId.
     *
     * @param uuid     the unique identifier for the user
     * @param tenantId the tenant identifier
     * @return a List of Address objects associated with the given user
     */
    public List<Address> getAddressByUserUuid(String uuid, String tenantId) {
        final Map<String, Object> params = new HashMap<>();
        params.put("uuid", uuid);
        params.put("tenantId", tenantId);

        List<Address> addressList = namedParameterJdbcTemplate.query(GET_ADDRESS_BY_USER_UUID, params,
                new AddressRowMapperV2());
        return addressList;
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

        try {
            namedParameterJdbcTemplate.update(INSERT_ADDRESS_BYUSERID_V2, addressInputs);
        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException("Address already exists for user with type: " + address.getType());
        }
        address.setUserId(userId);
        address.setId(addressId);
        return address;
    }

    /**
     * Updates the user address records (v2) for a given user.
     * It first retrieves the existing addresses for the user and then:
     *   - Deletes addresses that are no longer present in the update request,
     *   - Creates new addresses for any newly added address details,
     *   - Updates existing addresses with the new information.
     *
     * @param domainAddresses the new set of Address objects to update
     * @param userId          the user identifier for which addresses are updated
     * @param tenantId        the tenant identifier
     */
    public void updateV2(List<Address> domainAddresses, Long userId, String tenantId) {
        final Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("tenantId", tenantId);

        List<Address> entityAddresses = namedParameterJdbcTemplate.query(GET_ADDRESS_BY_USERID, params,
                new AddressRowMapperV2());

        if (isEmpty(domainAddresses) && isEmpty(entityAddresses)) {
            return;
        }

        // Delete all addresses if needed, then remove addresses that are no longer present,
        // create any new addresses, and finally update the existing addresses.
        conditionallyDeleteAllAddresses(domainAddresses, entityAddresses);
        deleteRemovedAddresses(domainAddresses, entityAddresses);
        createNewAddresses(domainAddresses, entityAddresses, userId, tenantId);
        updateAddressesV2(domainAddresses, entityAddresses, userId);
    }

    /**
     * Iterates over the provided list of new addresses (domainAddresses) and updates each address
     * by matching it with the corresponding address (based on type) in the existing entityAddresses.
     *
     * @param domainAddresses the new set of Address objects provided for update
     * @param entityAddresses the current set of Address objects from the database
     * @param userId          the user identifier
     */
    private void updateAddressesV2(List<Address> domainAddresses, List<Address> entityAddresses, Long userId) {
        // Create a map from address type to entity address for fast lookup
        Map<String, Address> typeToEntityAddressMap = toMap(entityAddresses);
        domainAddresses.forEach(address -> updateAddressV2(typeToEntityAddressMap, address, userId));
    }

    /**
     * Updates a specific address for a user based on the address type.
     * It retrieves the existing address using the address type; if a matching address exists,
     * it updates that address with the new values provided.
     *
     * @param typeToEntityAddressMap a map of existing addresses keyed by address type name
     * @param address                the new Address object containing updated details
     * @param userId                 the user identifier
     */
    private void updateAddressV2(Map<String, Address> typeToEntityAddressMap, Address address, Long userId) {
        // Retrieve the existing address for the given address type (using the enum name as key)
        final Address matchingEntityAddress = typeToEntityAddressMap.getOrDefault(address.getType().name(), null);
        if (matchingEntityAddress == null) {
            return;
        }
        Map<String, Object> addressInputs = new HashMap<>();
        addressInputs.put("address", address.getAddress());
        addressInputs.put("type", address.getType().toString());
        addressInputs.put("city", address.getCity());
        addressInputs.put("pincode", address.getPinCode());
        addressInputs.put("userid", userId);
        addressInputs.put("tenantid", matchingEntityAddress.getTenantId());
        addressInputs.put("lastmodifieddate", new Date());
        addressInputs.put("lastmodifiedby", userId);
        // New fields for v2 update: additional address details
        addressInputs.put("address2", address.getAddress2());
        addressInputs.put("houseNumber", address.getHouseNumber());
        addressInputs.put("houseName", address.getHouseName());
        addressInputs.put("streetName", address.getStreetName());
        addressInputs.put("landmark", address.getLandmark());
        addressInputs.put("locality", address.getLocality());

        namedParameterJdbcTemplate.update(UPDATE_ADDRESS_BYIDAND_TENANTID_V2, addressInputs);
    }

    /**
     * Updates the address record for a specific addressId (update address endpoint).
     * Only the address record corresponding to the given primary key (id) is updated.
     *
     * @param addressId the unique identifier of the address record to update
     * @param address   the new Address details to update the record with
     * @return the updated Address object
     */
    public Address updateAddressV2(String addressId, Address address) {
        Map<String, Object> addressInputs = new HashMap<>();
        addressInputs.put("id", addressId);
        addressInputs.put("address", address.getAddress());
        addressInputs.put("type", address.getType().toString());
        addressInputs.put("city", address.getCity());
        addressInputs.put("pincode", address.getPinCode());
        addressInputs.put("userid", address.getUserId());
        addressInputs.put("tenantid", address.getTenantId());
        addressInputs.put("lastmodifieddate", new Date());
        addressInputs.put("lastmodifiedby", address.getUserId());
        addressInputs.put("address2", address.getAddress2());
        addressInputs.put("houseNumber", address.getHouseNumber());
        addressInputs.put("houseName", address.getHouseName());
        addressInputs.put("streetName", address.getStreetName());
        addressInputs.put("landmark", address.getLandmark());
        addressInputs.put("locality", address.getLocality());

        namedParameterJdbcTemplate.update(UPDATE_ADDRESS_BYIDAND_TENANTID, addressInputs);
        return address;
    }

    /**
     * Checks if the address exists in the database by querying the `eg_user_address` table.
     *
     * @param addressId The unique identifier of the address.
     * @return true if the address exists, false otherwise.
     */
    public Boolean isAddressPresent(String addressId) {
        final Map<String, Object> Map = new HashMap<String, Object>();
        Map.put("id", Long.parseLong(addressId));
        Integer count = namedParameterJdbcTemplate.queryForObject("select count(*) from eg_user_address where id=:id", Map, Integer.class);
        return count != null && count > 0;
    }
}
