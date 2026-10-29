package org.upyog.chb.constants;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class WorkflowStatusTest {

    @Test
    void testToString() {
        assertEquals("CREATE", WorkflowStatus.CREATE.toString());
        assertEquals("UPDATE", WorkflowStatus.UPDATE.toString());
        assertEquals("STATUS", WorkflowStatus.STATUS.toString());
    }

    @Test
    void testFromValue() {
        assertEquals(WorkflowStatus.CREATE, WorkflowStatus.fromValue("CREATE"));
        assertEquals(WorkflowStatus.UPDATE, WorkflowStatus.fromValue("UPDATE"));
        assertEquals(WorkflowStatus.STATUS, WorkflowStatus.fromValue("STATUS"));
    }

    @Test
    void testFromValueIgnoreCase() {
        assertEquals(WorkflowStatus.CREATE, WorkflowStatus.fromValue("create"));
        assertEquals(WorkflowStatus.UPDATE, WorkflowStatus.fromValue("update"));
        assertEquals(WorkflowStatus.STATUS, WorkflowStatus.fromValue("status"));
    }

    @Test
    void testFromValueInvalid() {
        assertNull(WorkflowStatus.fromValue("INVALID"));
        assertNull(WorkflowStatus.fromValue(null));
    }
}