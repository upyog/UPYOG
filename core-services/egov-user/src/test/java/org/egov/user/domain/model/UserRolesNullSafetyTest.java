package org.egov.user.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;

/**
 * Test to verify that User.getRoles() never returns null in any scenario
 * This is critical for gateway RBAC authorization which requires non-null roles
 */
public class UserRolesNullSafetyTest {

    @Test
    public void test_getRoles_returns_empty_set_when_roles_is_null() {
        // Create a user using builder (roles might be null due to @Builder.Default issue)
        User user = User.builder()
                .name("Test User")
                .build();

        // Verify getRoles never returns null
        assertNotNull(user.getRoles(), "getRoles() should never return null");
        assertEquals(0, user.getRoles().size(), "getRoles() should return empty set when no roles are set");
    }

    @Test
    public void test_setRoles_with_null_initializes_empty_set() {
        User user = User.builder()
                .name("Test User")
                .build();

        // Try to set roles to null
        user.setRoles(null);

        // Verify it's not actually null
        assertNotNull(user.getRoles(), "setRoles(null) should initialize empty set, not null");
        assertEquals(0, user.getRoles().size(), "setRoles(null) should result in empty set");
    }

    @Test
    public void test_getRoles_after_nullifySensitiveFields() {
        Role role = Role.builder().code("CITIZEN").build();
        User user = User.builder()
                .name("Test User")
                .roles(new HashSet<>())
                .build();
        user.getRoles().add(role);

        // Call nullifySensitiveFields which should NOT nullify roles anymore
        user.nullifySensitiveFields();

        // Verify roles is still not null
        assertNotNull(user.getRoles(), "getRoles() should never return null even after nullifySensitiveFields()");
        assertEquals(1, user.getRoles().size(), "Roles should be preserved after nullifySensitiveFields()");
    }

    @Test
    public void test_addRolesItem_when_roles_is_null() {
        User user = User.builder()
                .name("Test User")
                .build();

        // Force roles to null by direct field access would require reflection
        // Instead, just test the addRolesItem method
        Role role = Role.builder().code("CITIZEN").build();
        user.addRolesItem(role);

        // Verify roles is not null
        assertNotNull(user.getRoles(), "getRoles() should never return null after addRolesItem()");
        assertEquals(1, user.getRoles().size(), "Should have 1 role after addRolesItem()");
        assertTrue(user.getRoles().contains(role), "Should contain the added role");
    }

    @Test
    public void test_toUser_ensures_roles_is_not_null() {
        User sourceUser = User.builder()
                .name("Test User")
                .tenantId("default")
                .active(true)
                .build();

        // Convert using toUser
        User convertedUser = new User().toUser(sourceUser);

        // Verify roles is not null
        assertNotNull(convertedUser.getRoles(), "toUser() should ensure roles is never null");
        assertEquals(0, convertedUser.getRoles().size(), "toUser() should initialize empty roles when source has no roles");
    }
}
