package org.egov.ndc.repository;

import org.egov.ndc.config.NDCConfiguration;
import org.egov.ndc.producer.Producer;
import org.egov.ndc.repository.NDCRepository;
import org.egov.ndc.repository.builder.NdcQueryBuilder;
import org.egov.ndc.repository.rowmapper.NdcRowMapper;
import org.egov.ndc.web.model.ndc.Application;
import org.egov.ndc.web.model.ndc.NdcApplicationSearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class NDCRepositoryTest {

    @Mock
    private Producer producer;

    @Mock
    private NDCConfiguration config;

    @Mock
    private NdcQueryBuilder queryBuilder;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private NdcRowMapper rowMapper;

    @InjectMocks
    private NDCRepository ndcRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this); // works with Mockito 2.x/early 3.x
    }

    @Test
    void testGetExistingUuids_ReturnsSet() {
        String tableName = "eg_ndc_application";
        List<String> inputUuids = Arrays.asList("u1", "u2");
        String sql = "SELECT uuid FROM eg_ndc_application WHERE ...";
        when(queryBuilder.getExistingUuids(tableName, inputUuids)).thenReturn(sql);

        // simulate two rows with uuid column
        when(jdbcTemplate.query(eq(sql), any(RowMapper.class)))
                .thenReturn(Arrays.asList("u1", "u2"));

        Set<String> result = ndcRepository.getExistingUuids(tableName, inputUuids);

        assertEquals(2, result.size());
        assertTrue(result.contains("u1"));
        assertTrue(result.contains("u2"));
        verify(queryBuilder).getExistingUuids(tableName, inputUuids);
        verify(jdbcTemplate).query(eq(sql), any(RowMapper.class));
    }

    @Test
    void testCheckApplicationExists_WhenExists_ReturnsTrue() {
        String uuid = "some-uuid";
        String sql = "SELECT uuid FROM table WHERE uuid=?";
        when(queryBuilder.checkApplicationExists(uuid)).thenReturn(sql);
        when(jdbcTemplate.queryForObject(eq(sql), any(Object[].class), eq(String.class)))
                .thenReturn(uuid);

        boolean exists = ndcRepository.checkApplicationExists(uuid);

        assertTrue(exists);
        verify(queryBuilder).checkApplicationExists(uuid);
        verify(jdbcTemplate).queryForObject(eq(sql), any(Object[].class), eq(String.class));
    }

    @Test
    void testCheckApplicationExists_WhenNotExists_ReturnsFalse() {
        String uuid = "missing-uuid";
        String sql = "SELECT uuid FROM table WHERE uuid=?";
        when(queryBuilder.checkApplicationExists(uuid)).thenReturn(sql);
        when(jdbcTemplate.queryForObject(eq(sql), any(Object[].class), eq(String.class)))
                .thenReturn(null);

        boolean exists = ndcRepository.checkApplicationExists(uuid);

        assertFalse(exists);
    }

    @Test
    void testFetchNdcApplications_WhenNoUuids_ReturnsEmptyList() {
        NdcApplicationSearchCriteria criteria = new NdcApplicationSearchCriteria();
        List<Object> uuidStmtList = new ArrayList<>();
        String uuidQuery = "SELECT uuid FROM eg_ndc_application LIMIT ...";

        when(queryBuilder.getPaginatedApplicationUuids(eq(criteria), anyList()))
                .thenAnswer(invocation -> {
                    // fill the uuidStmtList if needed
                    List<Object> params = invocation.getArgument(1);
                    params.add("param1");
                    return uuidQuery;
                });

        when(jdbcTemplate.query(eq(uuidQuery), any(Object[].class), any(RowMapper.class)))
                .thenReturn(Collections.emptyList());

        List<Application> result = ndcRepository.fetchNdcApplications(criteria);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFetchNdcApplications_WithUuids_ReturnsApplications() {
        NdcApplicationSearchCriteria criteria = new NdcApplicationSearchCriteria();
        List<Object> uuidStmtList = new ArrayList<>();
        String uuidQuery = "SELECT uuid FROM eg_ndc_application LIMIT ...";

        // First query to fetch paginated UUIDs
        when(queryBuilder.getPaginatedApplicationUuids(eq(criteria), anyList()))
                .thenAnswer(invocation -> {
                    List<Object> params = invocation.getArgument(1);
                    params.add("param1");
                    return uuidQuery;
                });

        List<String> dbUuids = Arrays.asList("u1", "u2");
        when(jdbcTemplate.query(eq(uuidQuery), any(Object[].class), any(RowMapper.class)))
                .thenReturn(dbUuids);

        // Second query to fetch details
        String detailQuery = "SELECT * FROM eg_ndc_application WHERE uuid IN (...)";
        when(queryBuilder.getNdcApplicationDetailsQuery(eq(dbUuids), anyList()))
                .thenAnswer(invocation -> {
                    List<Object> params = invocation.getArgument(1);
                    params.addAll(dbUuids);
                    return detailQuery;
                });

        Application app1 = new Application();
        Application app2 = new Application();
        List<Application> apps = Arrays.asList(app1, app2);

        when(jdbcTemplate.query(eq(detailQuery), any(Object[].class), eq(rowMapper)))
                .thenReturn(apps);

        List<Application> result = ndcRepository.fetchNdcApplications(criteria);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(queryBuilder).getPaginatedApplicationUuids(eq(criteria), anyList());
        verify(queryBuilder).getNdcApplicationDetailsQuery(eq(dbUuids), anyList());
        verify(jdbcTemplate).query(eq(detailQuery), any(Object[].class), eq(rowMapper));
    }
}
