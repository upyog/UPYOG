package org.egov.individual.service;

import digit.models.coremodels.AuditDetails;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.models.individual.Address;
import org.egov.common.models.individual.Identifier;
import org.egov.common.models.individual.Individual;
import org.egov.common.models.individual.IndividualBulkRequest;
import org.egov.common.models.individual.Skill;
import org.egov.common.service.IdGenService;
import org.egov.individual.config.IndividualProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.egov.common.utils.CommonUtils.collectFromList;
import static org.egov.common.utils.CommonUtils.enrichForCreate;
import static org.egov.common.utils.CommonUtils.enrichForDelete;
import static org.egov.common.utils.CommonUtils.enrichForUpdate;
import static org.egov.common.utils.CommonUtils.getAuditDetailsForUpdate;
import static org.egov.common.utils.CommonUtils.getIdToObjMap;
import static org.egov.common.utils.CommonUtils.getMethod;
import static org.egov.common.utils.CommonUtils.getTenantId;
import static org.egov.common.utils.CommonUtils.uuidSupplier;
import static org.egov.individual.Constants.GET_ID;
import static org.egov.individual.Constants.SYSTEM_GENERATED;

@Service
@Slf4j
public class EnrichmentService {

    private final IdGenService idGenService;

    private final IndividualProperties properties;

    @Autowired
    public EnrichmentService(IdGenService idGenService,
                             IndividualProperties properties) {
        this.idGenService = idGenService;
        this.properties = properties;
    }

    public void create(List<Individual> validIndividuals, IndividualBulkRequest request) {
        log.info("starting the enrichment for create individuals");

        log.info("extracting tenantId");
        //fetch the root tenantId if it is in state.city format
        final String tenantId = getTenantId(validIndividuals).split("\\.")[0];
        log.info("generating id for individuals");
        List<String> indIdList = idGenService.getIdList(request.getRequestInfo(),
                tenantId, properties.getIndividualId(),
                null, validIndividuals.size());
        log.info("enriching individuals");

        //individual.id is uuid and individual.individualId is idgen generated formatted value
        List<String> idList = uuidSupplier().apply(validIndividuals.size());
        enrichForCreate(validIndividuals, idList, request.getRequestInfo());
        enrichIndividualIdOnCreate(validIndividuals,indIdList);

        enrichAddressesForCreate(request, validIndividuals);
        enrichIdentifiersForCreate(request, validIndividuals);
        enrichSkillsForCreate(request, validIndividuals);

        log.info("completed the enrichment for create individuals");
    }

    public void update(List<Individual> validIndividuals, IndividualBulkRequest request) throws Exception {
        log.info("starting the enrichment for update individuals");
        validIndividuals.forEach(
                individual -> {
                    enrichAddressForUpdate(request, individual);
                    enrichIdentifierForUpdate(request, individual);
                    enrichSkillForUpdate(request, individual);
                }
        );
        Map<String, Individual> iMap = getIdToObjMap(validIndividuals);
        log.info("enriching individuals for update");
        enrichForUpdate(iMap, request);
        log.info("completed the enrichment for update individuals");
    }

    public void delete(List<Individual> validIndividuals, IndividualBulkRequest request) throws Exception {
        log.info("starting the enrichment for delete individuals");
        enrichIndividualIdInAddress(validIndividuals);
        validIndividuals = validIndividuals.stream()
                .map(EnrichmentService::enrichIndividualIdInIdentifiers)
                .collect(Collectors.toList());
        validIndividuals.forEach(individual -> {
            RequestInfo requestInfo = request.getRequestInfo();
            if (individual.getIsDeleted()) {
                log.info("enriching individuals for delete");
                enrichForDelete(Collections.singletonList(individual), requestInfo, true);
                if (individual.getAddress() != null && !individual.getAddress().isEmpty()) {
                    log.info("enriching all addresses for delete");
                    enrichForDelete(individual.getAddress(), requestInfo, false);
                }
                if (individual.getIdentifiers() != null && !individual.getIdentifiers().isEmpty()) {
                    log.info("enriching all identifiers for delete");
                    enrichForDelete(individual.getIdentifiers(), requestInfo, false);
                }
                if (individual.getSkills() != null && !individual.getSkills().isEmpty()) {
                    log.info("enriching all skills for delete");
                    enrichForDelete(individual.getSkills(), requestInfo, false);
                }
            } else {
                Integer previousRowVersion = individual.getRowVersion();
                if (individual.getIdentifiers() != null) {
                    log.info("enriching identifiers for delete");
                    individual.getIdentifiers().stream().filter(Identifier::getIsDeleted)
                            .forEach(identifier -> {
                                AuditDetails existingAuditDetails = identifier.getAuditDetails();
                                AuditDetails auditDetails = getAuditDetailsForUpdate(existingAuditDetails,
                                        request.getRequestInfo().getUserInfo().getUuid());
                                identifier.setAuditDetails(auditDetails);
                                individual.setAuditDetails(auditDetails);
                                individual.setRowVersion(previousRowVersion + 1);
                            });
                }

                if (individual.getAddress() != null) {
                    log.info("enriching addresses for delete");
                    individual.getAddress().stream().filter(Address::getIsDeleted)
                            .forEach(address -> {
                                AuditDetails existingAuditDetails = address.getAuditDetails();
                                AuditDetails auditDetails = getAuditDetailsForUpdate(existingAuditDetails,
                                        request.getRequestInfo().getUserInfo().getUuid());
                                address.setAuditDetails(auditDetails);
                                individual.setAuditDetails(auditDetails);
                                individual.setRowVersion(previousRowVersion + 1);
                            });
                }

                if (individual.getSkills() != null) {
                    log.info("enriching skills for delete");
                    individual.getSkills().stream().filter(Skill::getIsDeleted)
                            .forEach(skill -> {
                                AuditDetails existingAuditDetails = skill.getAuditDetails();
                                AuditDetails auditDetails = getAuditDetailsForUpdate(existingAuditDetails,
                                        request.getRequestInfo().getUserInfo().getUuid());
                                skill.setAuditDetails(auditDetails);
                                individual.setAuditDetails(auditDetails);
                                individual.setRowVersion(previousRowVersion + 1);
                            });
                }
            }
        });

        log.info("completed the enrichment for delete individuals");
    }

    private static void enrichAddressesForCreate(IndividualBulkRequest request, List<Individual> validIndividuals) {
        List<Address> addresses = collectFromList(validIndividuals,
                Individual::getAddress);
        if (!addresses.isEmpty()) {
            log.info("enriching addresses for create individuals");
            List<String> addressIdList = uuidSupplier().apply(addresses.size());
            enrichForCreate(addresses, addressIdList, request.getRequestInfo(), false);
            enrichIndividualIdInAddress(validIndividuals);
        }
    }

    /**
     * Enriches individualId - formatted idGen generated value on create
     */
    private static void enrichIndividualIdOnCreate(List<Individual> individuals, List<String> idGenList) {
        IntStream.range(0, individuals.size()).forEach((i) -> {
            individuals.get(i).setIndividualId(idGenList.get(i));
        });
    }

    private static void enrichSkillsForCreate(IndividualBulkRequest request, List<Individual> validIndividuals) {
        log.info("enriching skills for create individuals");
        List<Skill> skills = collectFromList(validIndividuals,
                Individual::getSkills);
        if (!skills.isEmpty()) {
            List<String> skillIds = uuidSupplier().apply(skills.size());
            enrichForCreate(skills, skillIds, request.getRequestInfo(), false);
            enrichIndividualIdInSkill(validIndividuals);
        }
    }

    private static void enrichIdentifiersForCreate(IndividualBulkRequest request, List<Individual> validIndividuals) {
        log.info("enriching identifiers for create individuals");
        request.setIndividuals(validIndividuals.stream()
                .map(EnrichmentService::enrichWithSystemGeneratedIdentifier)
                .map(EnrichmentService::enrichIndividualIdInIdentifiers)
                .collect(Collectors.toList()));
        List<Identifier> identifiers = collectFromList(validIndividuals,
                Individual::getIdentifiers);
        List<String> identifierIdList = uuidSupplier().apply(identifiers.size());
        enrichForCreate(identifiers, identifierIdList, request.getRequestInfo(), false);
    }

    private static Individual enrichIndividualIdInIdentifiers(Individual individual) {
        log.info("enriching individual id in identifiers");
        List<Identifier> identifiers = individual.getIdentifiers();
        if (identifiers != null) {
            identifiers.forEach(identifier -> identifier.setIndividualId(individual.getId()));
            individual.setIdentifiers(identifiers);
        }
        return individual;
    }

    private static void enrichIndividualIdInAddress(List<Individual> individuals) {
        log.info("enriching individual id in addresses");
        individuals.stream().filter(individual -> individual.getAddress() != null)
                .forEach(individual -> individual.getAddress()
                        .forEach(address -> address.setIndividualId(individual.getId())));
    }

    private static void enrichIndividualIdInSkill(List<Individual> individuals) {
        log.info("enriching individual id in skills");
        individuals.stream().filter(individual -> individual.getSkills() != null)
                .forEach(individual -> individual.getSkills()
                        .forEach(skill -> skill.setIndividualId(individual.getId())));
    }

    private static Individual enrichWithSystemGeneratedIdentifier(Individual individual) {
        if (individual.getIdentifiers() == null || individual.getIdentifiers().isEmpty()) {
            log.info("enriching individual with system generated identifier");
            List<Identifier> identifiers = new ArrayList<>();
            identifiers.add(Identifier.builder()
                    .identifierType(SYSTEM_GENERATED)
                    .identifierId(individual.getId())
                    .build());
            individual.setIdentifiers(identifiers);
        }
        return individual;
    }

    private static void enrichAddressForUpdate(IndividualBulkRequest request, Individual individual) {
        if (individual.getAddress() == null) {
            return;
        }

        List<Address> addressesToCreate = individual.getAddress().stream()
                .filter(ad1 -> ad1.getId() == null)
                .collect(Collectors.toList());
        List<Address> addressesToUpdate = individual.getAddress().stream()
                .filter(ad1 -> ad1.getId() != null)
                .collect(Collectors.toList());
        if (!addressesToCreate.isEmpty()) {
            log.info("enriching addresses to create");
            List<String> addressIdList = uuidSupplier().apply(addressesToCreate.size());
            enrichForCreate(addressesToCreate, addressIdList, request.getRequestInfo(), false);
            addressesToCreate.forEach(address -> address.setIndividualId(individual.getId()));
        }

        if (!addressesToUpdate.isEmpty()) {
            log.info("enriching addresses to update");
            addressesToUpdate.forEach(address -> {
                address.setIndividualId(individual.getId());
                AuditDetails existingAuditDetails = address.getAuditDetails();
                AuditDetails auditDetails = getAuditDetailsForUpdate(existingAuditDetails,
                        request.getRequestInfo().getUserInfo().getUuid());
                address.setAuditDetails(auditDetails);
                if (address.getIsDeleted() == null) {
                    address.setIsDeleted(Boolean.FALSE);
                }
            });
        }
    }

    private static void enrichIdentifierForUpdate(IndividualBulkRequest request,
                                                  Individual individual) {
        if (individual.getIdentifiers() != null) {
            List<Identifier> identifiersToCreate = individual.getIdentifiers().stream().filter(havingNullId())
                    .collect(Collectors.toList());
            List<Identifier> identifiersToUpdate = individual.getIdentifiers().stream()
                    .filter(notHavingNullId())
                    .collect(Collectors.toList());

            if (!identifiersToCreate.isEmpty()) {
                log.info("enriching identifiers to create");
                List<String> identifierIdList = uuidSupplier().apply(identifiersToCreate.size());
                enrichForCreate(identifiersToCreate, identifierIdList, request.getRequestInfo(), false);
                identifiersToCreate.forEach(identifier -> identifier.setIndividualId(individual.getId()));
            }

            if (!identifiersToUpdate.isEmpty()) {
                log.info("enriching identifiers to update");
                identifiersToUpdate.forEach(identifier -> {
                    identifier.setIndividualId(individual.getId());
                    AuditDetails existingAuditDetails = identifier.getAuditDetails();
                    AuditDetails auditDetails = getAuditDetailsForUpdate(existingAuditDetails,
                            request.getRequestInfo().getUserInfo().getUuid());
                    identifier.setAuditDetails(auditDetails);
                    if (identifier.getIsDeleted() == null) {
                        identifier.setIsDeleted(Boolean.FALSE);
                    }
                });
            }
        }
    }

    private static void enrichSkillForUpdate(IndividualBulkRequest request,
                                                  Individual individual) {
        if (individual.getSkills() != null) {
            List<Skill> skillsToCreate = individual.getSkills().stream().filter(havingNullId())
                    .collect(Collectors.toList());
            if (!skillsToCreate.isEmpty()) {
                log.info("enriching skills to create");
                List<String> skillIdList = uuidSupplier().apply(skillsToCreate.size());
                enrichForCreate(skillsToCreate, skillIdList, request.getRequestInfo(), false);
                skillsToCreate.forEach(skill -> skill.setIndividualId(individual.getId()));
            }

            List<Skill> skillsToUpdate = individual.getSkills().stream()
                    .filter(notHavingNullId())
                    .collect(Collectors.toList());
            if (!skillsToUpdate.isEmpty()) {
                log.info("enriching skills to update");
                skillsToUpdate.forEach(skill -> {
                    skill.setIndividualId(individual.getId());
                    AuditDetails existingAuditDetails = skill.getAuditDetails();
                    AuditDetails auditDetails = getAuditDetailsForUpdate(existingAuditDetails,
                            request.getRequestInfo().getUserInfo().getUuid());
                    skill.setAuditDetails(auditDetails);
                    if (skill.getIsDeleted() == null) {
                        skill.setIsDeleted(Boolean.FALSE);
                    }
                });
            }
        }
    }


    private static <T> Predicate<T> havingNullId() {
        return obj -> ReflectionUtils.invokeMethod(getMethod(GET_ID, obj.getClass()), obj) == null;
    }

    private static <T> Predicate<T> notHavingNullId() {
        return (Predicate<T>) havingNullId().negate();
    }
}
