package org.egov.individual.repository;

import org.egov.common.data.query.builder.SelectQueryBuilder;
import org.egov.common.data.query.exception.QueryBuilderException;
import org.egov.common.models.individual.Identifier;
import org.egov.common.models.individual.Individual;
import org.egov.individual.helper.IndividualSearchTestBuilder;
import org.egov.individual.helper.IndividualTestBuilder;
import org.egov.individual.repository.rowmapper.AddressRowMapper;
import org.egov.individual.repository.rowmapper.IdentifierRowMapper;
import org.egov.individual.repository.rowmapper.IndividualRowMapper;
import org.egov.individual.web.models.IndividualSearch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndividualRepositoryTest {

    @InjectMocks
    private IndividualRepository individualRepository;

    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Mock
    private SelectQueryBuilder selectQueryBuilder;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private IndividualRowMapper individualRowMapper;

    @Mock
    private HashOperations hashOperations;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        ReflectionTestUtils.setField(individualRepository, "timeToLive", "60");
    }

    @Test
    @DisplayName("should find by id from db and return all the dependent entities as well if present")
    void shouldFindByIdFromDbAndReturnAllTheDependentEntitiesAsWellIfPresent() throws QueryBuilderException {
        IndividualSearch individualSearch = IndividualSearchTestBuilder.builder()
                .byId()
                .build();
        Individual individual = IndividualTestBuilder.builder()
                .withId()
                .build();

        when(namedParameterJdbcTemplate.query(anyString(), anyMap(), any(IndividualRowMapper.class)))
                .thenReturn(Collections.singletonList(individual));

        when(namedParameterJdbcTemplate.query(anyString(), anyMap(), any(ResultSetExtractor.class))).thenReturn(0L);

        individualRepository.findById(Arrays.asList("some-id"), "id", false);

        verify(namedParameterJdbcTemplate, times(1))
                .query(anyString(), anyMap(), any(IndividualRowMapper.class));

        verify(namedParameterJdbcTemplate, times(1))
                .query(anyString(), anyMap(), any(AddressRowMapper.class));

        verify(namedParameterJdbcTemplate, times(1))
                .query(anyString(), anyMap(), any(IdentifierRowMapper.class));

        verify(namedParameterJdbcTemplate, times(1))
                .query(anyString(), anyMap(), any(ResultSetExtractor.class));
    }

    @Test
    @DisplayName("should find by other params from db and return all the dependent entities as well if present")
    void shouldFindOtherParamsFromDbAndReturnAllTheDependentEntitiesAsWellIfPresent() throws QueryBuilderException {
        IndividualSearch individualSearch = IndividualSearchTestBuilder.builder()
                .byId()
                .byClientReferenceId()
                .byGender()
                .byName()
                .byDateOfBirth()
                .byBoundaryCode()
                .build();
        Individual individual = IndividualTestBuilder.builder()
                .withId()
                .build();

        when(namedParameterJdbcTemplate.query(anyString(), anyMap(), any(IndividualRowMapper.class)))
                .thenReturn(Collections.singletonList(individual));

        when(namedParameterJdbcTemplate.query(anyString(), anyMap(), any(ResultSetExtractor.class))).thenReturn(0L);

        individualRepository.find(individualSearch,
                2, 0, "default", null, true);

        verify(namedParameterJdbcTemplate, times(1))
                .query(anyString(), anyMap(), any(IndividualRowMapper.class));
        verify(namedParameterJdbcTemplate, times(1))
                .query(anyString(), anyMap(), any(AddressRowMapper.class));
        verify(namedParameterJdbcTemplate, times(1))
                .query(anyString(), anyMap(), any(IdentifierRowMapper.class));
        verify(namedParameterJdbcTemplate, times(1))
                .query(anyString(), anyMap(), any(ResultSetExtractor.class));

    }

    @Test
    @DisplayName("should find only by identifier")
    void shouldFindOnlyByIdentifier() throws QueryBuilderException {
        IndividualSearch individualSearch = IndividualSearchTestBuilder.builder()
                .byIdentifier()
                .build();
        Individual individual = IndividualTestBuilder.builder()
                .withId()
                .build();
        when(namedParameterJdbcTemplate.query(anyString(), anyMap(), any(IdentifierRowMapper.class)))
                .thenReturn(Collections.singletonList(Identifier.builder()
                                .individualId("some-id")
                                .identifierId("some-identifier-id")
                                .identifierType("SYSTEM_GENERATED")
                        .build()));
        when(namedParameterJdbcTemplate.query(anyString(), anyMap(), any(IndividualRowMapper.class)))
                .thenReturn(Collections.singletonList(individual));

        individualRepository.find(individualSearch,
                2, 0, "default", null, true);

        verify(namedParameterJdbcTemplate, times(1))
                .query(anyString(), anyMap(), any(IndividualRowMapper.class));
        verify(namedParameterJdbcTemplate, times(1))
                .query(anyString(), anyMap(), any(AddressRowMapper.class));
        verify(namedParameterJdbcTemplate, times(1))
                .query(anyString(), anyMap(), any(IdentifierRowMapper.class));
    }

    @Test
    @DisplayName("should find by other params and identifier from db and return all the dependent entities as well if present")
    void shouldFindOtherParamsAndIdentifierFromDbAndReturnAllTheDependentEntitiesAsWellIfPresent() throws QueryBuilderException {
        IndividualSearch individualSearch = IndividualSearchTestBuilder.builder()
                .byId()
                .byClientReferenceId()
                .byGender()
                .byName()
                .byDateOfBirth()
                .byBoundaryCode()
                .byIdentifier()
                .build();
        Individual individual = IndividualTestBuilder.builder()
                .withId()
                .build();
        when(namedParameterJdbcTemplate.query(anyString(),
                anyMap(), any(IdentifierRowMapper.class)))
                .thenReturn(Collections.singletonList(Identifier.builder()
                        .identifierId("some-identifier-id")
                        .identifierType("SYSTEM_GENERATED")
                                .individualId("some-id")
                        .build()));
        when(namedParameterJdbcTemplate.query(anyString(),
                anyMap(), any(IndividualRowMapper.class)))
                .thenReturn(Collections.singletonList(individual));

        individualRepository.find(individualSearch,
                2, 0, "default", null, true);

        verify(namedParameterJdbcTemplate, times(1)).query(anyString(),
                anyMap(), any(IndividualRowMapper.class));
        verify(namedParameterJdbcTemplate, times(1)).query(anyString(),
                anyMap(), any(AddressRowMapper.class));
    }
}