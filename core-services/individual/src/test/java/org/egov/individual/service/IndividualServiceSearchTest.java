package org.egov.individual.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.data.query.exception.QueryBuilderException;
import org.egov.common.helper.RequestInfoTestBuilder;
import org.egov.individual.web.models.SearchResponse;
import org.egov.common.models.individual.Individual;
import org.egov.common.service.IdGenService;
import org.egov.individual.helper.IndividualSearchTestBuilder;
import org.egov.individual.helper.IndividualTestBuilder;
import org.egov.individual.repository.IndividualRepository;
import org.egov.individual.web.models.IndividualSearch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndividualServiceSearchTest {

    @InjectMocks
    private IndividualService individualService;


    @Mock
    private IdGenService idGenService;

    @Mock
    private IndividualRepository individualRepository;

    @Mock
    private IndividualEncryptionService encryptionService;

    @Test
    @DisplayName("should search only by id if only id is present")
    void shouldSearchOnlyByIdIfOnlyIdIsPresent() throws QueryBuilderException {
        IndividualSearch individualSearch = IndividualSearchTestBuilder.builder()
                .byId()
                .build();
        RequestInfo requestInfo = RequestInfoTestBuilder.builder().withCompleteRequestInfo().build();

        when(individualRepository.findById(anyList(), anyString(), anyBoolean()))
                .thenReturn(SearchResponse.<Individual>builder().totalCount(1L).response(Collections.singletonList(IndividualTestBuilder.builder()
                        .withId("some-id")
                        .build())).build());

        individualService.search(individualSearch, 0, 10,
                "default", null, false,requestInfo);

        verify(individualRepository, times(1)).findById(anyList(),
                eq("id"), anyBoolean());
    }

    @Test
    @DisplayName("should not throw exception in case the array is null")
    void shouldNotThrowExceptionIfArrayIsNull() throws QueryBuilderException {
        IndividualSearch individualSearch = IndividualSearchTestBuilder.builder()
                .byNullId()
                .build();
        RequestInfo requestInfo = RequestInfoTestBuilder.builder().withCompleteRequestInfo().build();

        when(encryptionService.encrypt(any(IndividualSearch.class), any(String.class))).thenReturn(individualSearch);

        when(individualRepository.find(any(IndividualSearch.class), anyInt(), anyInt(), anyString(),
                any(), anyBoolean())).thenReturn(SearchResponse.<Individual>builder()
                .totalCount(1L)
                .response(Collections.singletonList(IndividualTestBuilder.builder()
                .withId("some-id")
                .build())).build());

        individualService.search(individualSearch, 0, 10,
                "default", null, false, requestInfo);

        verify(individualRepository, times(0)).findById(anyList(),
                eq("id"), anyBoolean());
    }

    @Test
    @DisplayName("should search only clientReferenceId if only clientReferenceId is present")
    void shouldSearchByOnlyClientReferenceIdIfOnlyClientReferenceIdIsPresent() throws QueryBuilderException {
        IndividualSearch individualSearch = IndividualSearchTestBuilder.builder()
                .byClientReferenceId()
                .build();
        RequestInfo requestInfo = RequestInfoTestBuilder.builder().withCompleteRequestInfo().build();

        when(individualRepository.findById(anyList(), anyString(), anyBoolean()))
                .thenReturn(SearchResponse.<Individual>builder().response(Collections.singletonList(IndividualTestBuilder.builder()
                        .withId("some-id")
                        .build())).build());

        individualService.search(individualSearch, 0, 10,
                "default", null, false,requestInfo);

        verify(individualRepository, times(1)).findById(anyList(),
                eq("clientReferenceId"), anyBoolean());
    }

    @Test
    @DisplayName("should not call findById if parameters other than id are present")
    void shouldNotCallFindByIdIfParametersOtherThanIdArePresent() throws QueryBuilderException {
        IndividualSearch individualSearch = IndividualSearchTestBuilder.builder()
                .byClientReferenceId()
                .byName()
                .build();

        RequestInfo requestInfo = RequestInfoTestBuilder.builder().withCompleteRequestInfo().build();

        when(individualRepository.find(any(IndividualSearch.class), anyInt(), anyInt(), anyString(), any(), anyBoolean()))
                .thenReturn(SearchResponse.<Individual>builder().build());

        when(encryptionService.encrypt(any(IndividualSearch.class), any(String.class))).thenReturn(individualSearch);

        individualService.search(individualSearch, 0, 10,
                "default", null, false,requestInfo);

        verify(individualRepository, times(0)).findById(anyList(),
                eq("clientReferenceId"), anyBoolean());
    }

    @Test
    @DisplayName("should call find if parameters other than id are present")
    void shouldCallFindIfParametersOtherThanIdArePresent() throws QueryBuilderException {
        IndividualSearch individualSearch = IndividualSearchTestBuilder.builder()
                .byClientReferenceId()
                .byGender()
                .build();
        RequestInfo requestInfo = RequestInfoTestBuilder.builder().withCompleteRequestInfo().build();
        when(encryptionService.encrypt(any(IndividualSearch.class), any(String.class))).thenReturn(individualSearch);

        when(individualRepository.find(any(IndividualSearch.class), anyInt(), anyInt(), anyString(),
                any(), anyBoolean())).thenReturn(SearchResponse.<Individual>builder().totalCount(1L).response(Collections.singletonList(IndividualTestBuilder.builder()
                .withId("some-id")
                .build())).build());

        individualService.search(individualSearch, 0, 10,
                "default", null, false,requestInfo);

        verify(individualRepository, times(1))
                .find(individualSearch, 0, 10, "default", null, false);
    }
}
