package org.egov.ndc.repository.builder;

import org.egov.ndc.config.NDCConfiguration;
import org.egov.ndc.web.model.ndc.NdcApplicationSearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class NdcQueryBuilderTest {

    private NdcQueryBuilder queryBuilder;
    private NDCConfiguration config;

    @BeforeEach
    void setUp() throws Exception {
        config = new NDCConfiguration();
        config.setDefaultLimit(10);
        config.setDefaultOffset(0);
        config.setMaxSearchLimit(100);

        queryBuilder = new NdcQueryBuilder();

        // Inject config via reflection
        java.lang.reflect.Field field = NdcQueryBuilder.class.getDeclaredField("ndcConfig");
        field.setAccessible(true);
        field.set(queryBuilder, config);
    }

    @Test
    void testGetPaginatedApplicationUuidsWithAllFilters() {
        NdcApplicationSearchCriteria criteria = NdcApplicationSearchCriteria.builder()
                .tenantId("tenant-1")
                .uuid(Arrays.asList("uuid-1", "uuid-2"))
                .applicationNo(Arrays.asList("app-1", "app-2"))
                .status("ACTIVE")
                .active("true")
                .ownerIds(new HashSet<>(Arrays.asList("owner-1", "owner-2")))
                .limit(5)
                .offset(2)
                .build();

        List<Object> stmtList = new ArrayList<>();
        String query = queryBuilder.getPaginatedApplicationUuids(criteria, stmtList);

        assertTrue(query.contains("a.tenantid = ?"));
        assertTrue(query.contains("a.uuid in (?,?)"));
        assertTrue(query.contains("a.applicationno in (?,?)"));
        assertTrue(query.contains("a.applicationstatus = ?"));
        assertTrue(query.contains("a.active = ?"));
        assertTrue(query.contains("ndcapplicationuuid FROM eg_ndc_owner WHERE uuid IN (?,?)"));
        assertTrue(query.contains("LIMIT ? OFFSET ?"));

        assertEquals(11, stmtList.size());
        assertEquals("tenant-1", stmtList.get(0));
        assertEquals("uuid-1", stmtList.get(1));
        assertEquals("uuid-2", stmtList.get(2));
        assertEquals("app-1", stmtList.get(3));
        assertEquals("app-2", stmtList.get(4));
        assertEquals("ACTIVE", stmtList.get(5));
        assertEquals("true", stmtList.get(6));
        assertEquals("owner-1", stmtList.get(7));
        assertEquals("owner-2", stmtList.get(8));
        assertEquals(5, stmtList.get(9));
        assertEquals(2, stmtList.get(10));
    }

    @Test
    void testGetNdcApplicationDetailsQuery() {
        List<String> uuids = Arrays.asList("uuid-1", "uuid-2");
        List<Object> stmtList = new ArrayList<>();

        String query = queryBuilder.getNdcApplicationDetailsQuery(uuids, stmtList);

        assertNotNull(query);
        assertTrue(query.contains("WHERE a.uuid IN (?,?)"));
        assertEquals(2, stmtList.size());
        assertEquals("uuid-1", stmtList.get(0));
        assertEquals("uuid-2", stmtList.get(1));
    }

    @Test
    void testGetExistingUuids() {
        List<String> uuids = Arrays.asList("uuid-1", "uuid-2");
        String query = queryBuilder.getExistingUuids("eg_ndc_application", uuids);

        assertEquals("SELECT uuid FROM eg_ndc_application WHERE uuid IN ('uuid-1','uuid-2')", query);
    }

    @Test
    void testCheckApplicationExists() {
        String query = queryBuilder.checkApplicationExists("uuid-123");
        assertEquals("SELECT uuid FROM eg_ndc_application WHERE uuid =?", query);
    }

    @Test
    void testCheckUniqueUserAndApplicationUUid() {
        List<String> uuids = Arrays.asList("owner-1", "owner-2");
        String query = queryBuilder.checkUniqueUserAndApplicationUUid(uuids, "app-123");

        assertEquals("SELECT 1 FROM eg_ndc_owner WHERE uuid in (?) and ndcapplicationuuid = ? ", query);
    }

    @Test
    void testGetNdcApplicationDetailsQueryWithEmptyListReturnsNull() {
        List<Object> stmtList = new ArrayList<>();
        assertNull(queryBuilder.getNdcApplicationDetailsQuery(Collections.emptyList(), stmtList));
    }
}