package org.egov.individual.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.models.individual.Address;
import org.egov.common.models.individual.AddressType;
import org.egov.common.models.individual.Individual;
import org.egov.common.models.user.RoleRequest;
import org.egov.common.models.user.UserRequest;
import org.egov.common.models.user.UserType;
import org.egov.individual.config.IndividualProperties;

import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class IndividualMapper {

    private static final Random RANDOM = new Random();

    private IndividualMapper() {}

    public static UserRequest toUserRequest(Individual individual, IndividualProperties properties) {
        Long id = individual.getUserId() != null ? Long.parseLong(individual.getUserId()) : null;
        String addressLine1 = individual.getAddress() != null && !individual.getAddress().isEmpty()
                ? individual.getAddress().stream().filter(address -> address.getType()
                        .equals(AddressType.CORRESPONDENCE)).findFirst()
                .orElse(Address.builder().build())
                .getAddressLine1() : null;
        return  UserRequest.builder()
                .id(id)
                .uuid(individual.getUserUuid())
                .tenantId(individual.getTenantId())
                .name(individual.getName().getFamilyName() != null ? String.join(" ", individual.getName().getGivenName(),
                        individual.getName().getFamilyName()) : individual.getName().getGivenName())
                .correspondenceAddress(addressLine1)
                .emailId(individual.getEmail())
                .mobileNumber(generateDummyMobileNumber(individual.getMobileNumber()))
                .type(UserType.valueOf(properties.getUserServiceUserType()))
                .accountLocked(properties.isUserServiceAccountLocked())
                .active(individual.getIsSystemUserActive())
                .userName(null != individual.getUserDetails().getUsername() ? individual.getUserDetails().getUsername() : UUID.randomUUID().toString())
                .password(null != individual.getUserDetails().getPassword() ? individual.getUserDetails().getPassword() : null)
                .roles(individual.getUserDetails().getRoles().stream().map(role -> RoleRequest.builder()
                                .code(role.getCode())
                                .name(role.getName())
                                .tenantId(individual.getTenantId())
                                .build())
                        .collect(Collectors.toSet()))
                .type(UserType.fromValue(individual.getUserDetails().getUserType() != null
                        ? individual.getUserDetails().getUserType().name() : null))
                .build();
    }

    /**
     * Generates a dummy 10 digit mobile number not starting with 0, if the input mobile number is null.
     * If the input mobile number is not null, it returns the same input number.
     *
     * @param mobileNumber the input mobile number to check
     * @return a dummy 10 digit mobile number if the input is null, or the input number if it's not null
     */
    private static String generateDummyMobileNumber(String mobileNumber) {
        if (mobileNumber == null) {
            int number = RANDOM.nextInt(900000000) + 100000000; // generate 9 digit number
            return "1" + number; // prepend 1 to avoid starting with 0
        } else {
            return mobileNumber;
        }
    }
}
