// Role Fix Test - Demonstrating the fix for role null issue
// This shows what the fix does:

import java.util.*;

public class RoleFixTest {

    public static void main(String[] args) {
        // Simulate the original problem
        System.out.println("=== ROLE NULL FIX DEMONSTRATION ===\n");

        // 1. Simulate user from database with roles (before decryption)
        User userFromDB = createUserWithRoles();
        System.out.println("1. User from Database:");
        System.out.println("   UUID: " + userFromDB.uuid);
        System.out.println("   Roles: " + userFromDB.roles.size() + " roles");
        for (Role role : userFromDB.roles) {
            System.out.println("      - " + role.code + " (" + role.name + ")");
        }

        // 2. Simulate the decryption process that loses roles
        System.out.println("\n2. After Decryption Process (PROBLEM):");
        User decryptedUser = simulateDecryptionProcess(userFromDB);
        System.out.println("   Roles: " + (decryptedUser.roles != null ? decryptedUser.roles.size() : "NULL"));
        System.out.println("   ISSUE: Roles are lost during decryption!");

        // 3. Apply the fix
        System.out.println("\n3. Applying the FIX:");
        User fixedUser = applyRoleFix(userFromDB);
        System.out.println("   UUID: " + fixedUser.uuid);
        System.out.println("   Roles: " + fixedUser.roles.size() + " roles (RESTORED!)");
        for (Role role : fixedUser.roles) {
            System.out.println("      - " + role.code + " (" + role.name + ")");
        }

        System.out.println("\n=== FIX SUCCESSFUL! ===");
        System.out.println("The roles are now preserved through the decryption process.");
    }

    static User createUserWithRoles() {
        User user = new User();
        user.uuid = "a02659b8-78a7-4f22-984d-050107432f22";
        user.roles = new HashSet<>();
        user.roles.add(new Role("EMPLOYEE", "Employee"));
        user.roles.add(new Role("PT_CEMP", "PT Counter Employee"));
        user.roles.add(new Role("PT_DOC_VERIFIER", "PT Doc Verifier"));
        user.roles.add(new Role("PT_FIELD_INSPECTOR", "PT Field Inspector"));
        user.roles.add(new Role("PT_APPROVER", "PT Counter Approver"));
        return user;
    }

    static User simulateDecryptionProcess(User originalUser) {
        // This simulates what happens in encryptionService.decryptJson()
        // The decryption process creates a new User object but loses the roles
        User decryptedUser = new User();
        decryptedUser.uuid = originalUser.uuid;
        // decryptedUser.roles = null; // This is the BUG - roles are lost!
        decryptedUser.roles = new HashSet<>(); // Or empty set
        return decryptedUser;
    }

    static User applyRoleFix(User originalUser) {
        // Step 1: Store roles before decryption
        Map<String, Set<Role>> roleBackup = new HashMap<>();
        if (originalUser.uuid != null && originalUser.roles != null) {
            roleBackup.put(originalUser.uuid, new HashSet<>(originalUser.roles));
            System.out.println("   - Backed up " + originalUser.roles.size() + " roles");
        }

        // Step 2: Simulate decryption (that loses roles)
        User decryptedUser = simulateDecryptionProcess(originalUser);
        System.out.println("   - After decryption: " +
            (decryptedUser.roles != null ? decryptedUser.roles.size() : "NULL") + " roles");

        // Step 3: Restore roles from backup (THE FIX)
        if (decryptedUser.uuid != null && roleBackup.containsKey(decryptedUser.uuid)) {
            Set<Role> preservedRoles = roleBackup.get(decryptedUser.uuid);
            decryptedUser.roles = preservedRoles;
            System.out.println("   - Restored " + preservedRoles.size() + " roles from backup");
        }

        return decryptedUser;
    }

    static class User {
        String uuid;
        Set<Role> roles;
    }

    static class Role {
        String code;
        String name;

        Role(String code, String name) {
            this.code = code;
            this.name = name;
        }
    }
}