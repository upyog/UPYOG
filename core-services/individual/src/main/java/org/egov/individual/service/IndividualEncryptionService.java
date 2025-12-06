package org.egov.individual.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.models.Error;
import org.egov.common.models.ErrorDetails;
import org.egov.common.models.individual.Identifier;
import org.egov.common.models.individual.Individual;
import org.egov.common.models.individual.IndividualBulkRequest;
import org.egov.individual.repository.IndividualRepository;
import org.egov.individual.util.EncryptionDecryptionUtil;
import org.egov.individual.web.models.IndividualSearch;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.egov.common.utils.CommonUtils.getTenantId;
import static org.egov.common.utils.CommonUtils.handleErrors;
import static org.egov.common.utils.CommonUtils.populateErrorDetails;
import static org.egov.individual.Constants.SET_INDIVIDUALS;
import static org.egov.individual.Constants.VALIDATION_ERROR;

@Service
@Slf4j
public class IndividualEncryptionService {
    private final EncryptionDecryptionUtil encryptionDecryptionUtil;

    private final IndividualRepository individualRepository;

    public IndividualEncryptionService(EncryptionDecryptionUtil encryptionDecryptionUtil,
                                       IndividualRepository individualRepository) {
        this.encryptionDecryptionUtil = encryptionDecryptionUtil;
        this.individualRepository = individualRepository;
    }


    public List<Individual> encrypt(IndividualBulkRequest request, List<Individual> individuals, String key, boolean isBulk) {
        List<Individual> encryptedIndividuals = (List<Individual>) encryptionDecryptionUtil
                .encryptObject(individuals, key, Individual.class);
        validateAadhaarUniqueness(encryptedIndividuals, request, isBulk);
        return encryptedIndividuals;
    }

    public IndividualSearch encrypt(IndividualSearch individualSearch, String key) {
        IndividualSearch encryptedIndividualSearch = (IndividualSearch) encryptionDecryptionUtil
                .encryptObject(individualSearch, key, IndividualSearch.class);
        return encryptedIndividualSearch;
    }

    public List<Individual> decrypt(List<Individual> individuals, String key, RequestInfo requestInfo) {
        List<Individual> encryptedIndividuals = filterEncryptedIndividuals(individuals);
        List<Individual> decryptedIndividuals = (List<Individual>) encryptionDecryptionUtil
                .decryptObject(encryptedIndividuals, key, Individual.class, requestInfo);
        if (individuals.size() > decryptedIndividuals.size()) {
            // add the already decrypted objects to the list
            List<String> ids = decryptedIndividuals.stream()
                    .map(Individual::getId)
                    .collect(Collectors.toList());
            for (Individual individual : individuals) {
                if (!ids.contains(individual.getId())) {
                    decryptedIndividuals.add(individual);
                }
            }
        }
        return decryptedIndividuals;
    }

    private List<Individual> filterEncryptedIndividuals(List<Individual> individuals) {
        return individuals.stream()
                .filter(individual -> isCipherText(individual.getMobileNumber())
                        || isCipherText(!CollectionUtils.isEmpty(individual.getIdentifiers())
                        ? individual.getIdentifiers().stream().findAny().get().getIdentifierId()
                        : null))
                .collect(Collectors.toList());
    }

    private boolean isCipherText(String text) {
        //sample encrypted data - 640326|7hsFfY6olwUbet1HdcLxbStR1BSkOye8N3M=
        //Encrypted data will have a prefix followed by '|' and the base64 encoded data
        if ((StringUtils.isNotBlank(text) && text.contains("|"))) {
            String base64Data = text.split("\\|")[1];
            return StringUtils.isNotBlank(base64Data) && (base64Data.length() % 4 == 0 || base64Data.endsWith("="));
        }
        return false;
    }

    private void validateAadhaarUniqueness (List<Individual> individuals, IndividualBulkRequest request, boolean isBulk) {

        Map<Individual, List<Error>> errorDetailsMap = new HashMap<>();
        String tenantId = getTenantId(individuals);

        if (!individuals.isEmpty()) {
            for (Individual individual : individuals) {
                if (!CollectionUtils.isEmpty(individual.getIdentifiers())) {
                    Identifier identifier = individual.getIdentifiers().stream()
                            .filter(id -> id.getIdentifierType().contains("AADHAAR"))
                            .findFirst().orElse(null);
                    if (identifier != null && StringUtils.isNotBlank(identifier.getIdentifierId())) {
                        Identifier identifierSearch = Identifier.builder().identifierType(identifier
                                .getIdentifierType()).identifierId(identifier.getIdentifierId()).build();
                        IndividualSearch individualSearch = IndividualSearch.builder().identifier(identifierSearch).build();
                        List<Individual> individualsList = null;
                        try {
                            individualsList = individualRepository.find(individualSearch,null,
                                    null,tenantId,null,false).getResponse();
                        } catch (Exception exception) {
                            log.error("database error occurred", exception);
                            throw new CustomException("DATABASE_ERROR", exception.getMessage());
                        }
                        if (!CollectionUtils.isEmpty(individualsList)) {
                            boolean isSelfIdentifier = individualsList.stream()
                                    .anyMatch(ind -> ind.getId().equalsIgnoreCase(individual.getId()));
                            if (!isSelfIdentifier) {
                                Error error = Error.builder().errorMessage("Aadhaar already exists for Individual - "
                                        +individualsList.get(0).getIndividualId()).errorCode("DUPLICATE_AADHAAR")
                                        .type(Error.ErrorType.NON_RECOVERABLE)
                                        .exception(new CustomException("DUPLICATE_AADHAAR", "Aadhaar already exists for Individual - "
                                                +individualsList.get(0).getIndividualId())).build();
                                populateErrorDetails(individual, error, errorDetailsMap);
                            }
                        }
                    }

                }
            }
        }

        if (!errorDetailsMap.isEmpty()) {
            Map<Individual, ErrorDetails> errorDetailsMapForTracer = new HashMap<>();
            Stream.of(errorDetailsMap).forEach(e -> populateErrorDetails(request, errorDetailsMapForTracer, e,
                    SET_INDIVIDUALS));
            handleErrors(errorDetailsMapForTracer, isBulk, VALIDATION_ERROR);
        }
    }
}
