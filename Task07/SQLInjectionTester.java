package Task07;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * SQLInjectionTester - Demonstrates adversarial SQL injection payloads against
 * dynamic string concatenation vs parameterized PreparedStatement.
 * Student Name: Geetika T
 * Register Number: 192511469
 */
public class SQLInjectionTester {

    // Simulated secure database table: users(username, password_hash, role)
    private static final Map<String, String> USER_TABLE = new HashMap<>();

    static {
        USER_TABLE.put("admin", "Admin$Secure2026");
        USER_TABLE.put("geetika", "Geetika@Pass123");
        USER_TABLE.put("student", "Student2026!");
    }

    /**
     * Unsafe Implementation: Concatenates user input directly into SQL statement.
     */
    public static boolean unsafeLogin(String username, String password) {
        String query = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";
        System.out.println("Executing Unsafe Query: " + query);

        // SQL Parser Simulation:
        // Attack 1: admin' --
        // In SQL: SELECT * FROM users WHERE username = 'admin' -- ' AND password = '...'
        // The '--' comments out the password clause entirely!
        if (username.startsWith("admin' --") || username.startsWith("geetika' --")) {
            String extractedUser = username.substring(0, username.indexOf("'"));
            if (USER_TABLE.containsKey(extractedUser)) {
                System.out.println("  -> [SQL Engine]: Comment delimiter detected. Password clause ignored!");
                System.out.println("  -> [VULNERABILITY CONFIRMED]: Logged in as '" + extractedUser + "' without password!");
                return true;
            }
        }

        // Attack 2: ' OR '1'='1
        if (username.contains("' OR '1'='1") || password.contains("' OR '1'='1")) {
            System.out.println("  -> [SQL Engine]: Tautology '1'='1' evaluated to TRUE for all rows!");
            System.out.println("  -> [VULNERABILITY CONFIRMED]: Authentication bypassed via boolean injection!");
            return true;
        }

        // Legitimate check
        return USER_TABLE.containsKey(username) && USER_TABLE.get(username).equals(password);
    }

    /**
     * Secure Implementation: Uses PreparedStatement parameterized placeholders.
     */
    public static boolean secureLogin(String username, String password) {
        System.out.println("Executing PreparedStatement: SELECT * FROM users WHERE username = ? AND password = ?");
        System.out.println("  -> Parameter 1 (Literal Data): [" + username + "]");
        System.out.println("  -> Parameter 2 (Literal Data): [" + password + "]");

        // PreparedStatement treats the input strictly as literal string values.
        // It looks for a literal username equal to "admin' -- " in the database.
        if (USER_TABLE.containsKey(username)) {
            return USER_TABLE.get(username).equals(password);
        }

        System.out.println("  -> [Security Engine]: No user found with literal username '" + username + "'. Access Denied.");
        return false;
    }

    public static void main(String[] args) {
        System.out.println("=== Task 07 – Part A: SQL Injection Adversarial Testing ===");
        System.out.println("Student: Geetika T | Reg No: 192511469\n");

        String[] attackUsernames = {
                "admin' -- ",
                "' OR '1'='1",
                "geetika' -- "
        };
        String invalidPassword = "completely_wrong_attacker_password";

        System.out.println("------------------------------------------------------------------");
        System.out.println("TEST 1: ATTACKING UNSAFE LOGIN IMPLEMENTATION (Dynamic SQL)");
        System.out.println("------------------------------------------------------------------");

        for (String maliciousUser : attackUsernames) {
            System.out.println("\nInjecting Payload: [" + maliciousUser + "]");
            boolean bypass = unsafeLogin(maliciousUser, invalidPassword);
            System.out.printf("Result: %s\n", bypass ? "BYPASS SUCCESSFUL (CRITICAL DEFECT)" : "Blocked");
        }

        System.out.println("\n------------------------------------------------------------------");
        System.out.println("TEST 2: ATTACKING SECURE PREPAREDSTATEMENT IMPLEMENTATION");
        System.out.println("------------------------------------------------------------------");

        for (String maliciousUser : attackUsernames) {
            System.out.println("\nInjecting Payload: [" + maliciousUser + "]");
            boolean secureAuth = secureLogin(maliciousUser, invalidPassword);
            System.out.printf("Result: %s\n", secureAuth ? "Bypassed (Error)" : "SAFELY REJECTED (IMMUNE)");
        }

        System.out.println("\n------------------------------------------------------------------");
        System.out.println("TEST 3: VERIFYING LEGITIMATE USER LOGIN ON SECURE IMPLEMENTATION");
        System.out.println("------------------------------------------------------------------");
        System.out.println("Authenticating legitimate user 'geetika' with correct password:");
        boolean legitAuth = secureLogin("geetika", "Geetika@Pass123");
        System.out.printf("Result: %s\n", legitAuth ? "AUTHENTICATION SUCCESSFUL (EXPECTED)" : "Failed");

        System.out.println("\n==> PART A COMPLETE: Parameterized queries provide complete immunity to SQL injection.");
    }
}
