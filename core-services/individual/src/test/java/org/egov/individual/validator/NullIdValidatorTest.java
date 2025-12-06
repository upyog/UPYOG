package org.egov.individual.validator;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.models.individual.IndividualBulkRequest;
import org.egov.individual.helper.IndividualBulkRequestTestBuilder;
import org.egov.individual.validators.NullIdValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class NullIdValidatorTest {

    @InjectMocks
    private NullIdValidator nullIdValidator;

    @Test
    void shouldNotThrowErrorIfIdIsNotNull() {
        IndividualBulkRequest individualBulkRequest = IndividualBulkRequestTestBuilder.builder().withIndividuals().build();
        assertDoesNotThrow(() -> nullIdValidator.validate(individualBulkRequest));
    }

}
