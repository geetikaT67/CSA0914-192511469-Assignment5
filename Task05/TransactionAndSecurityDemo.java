package Task05;

import java.util.HashMap;
import java.util.Map;

/**
 * TransactionAndSecurityDemo - Demonstrates transaction rollback and SQL injection defense.
 * Student Name: Geetika T
 * Register Number: 192511469
 */
public class TransactionAndSecurityDemo {

    // === PART A: Transaction Demonstration (Hostel to Canteen Wallet) ===
    static class WalletDatabase {
        Map<String, Double> hostelWallets = new HashMap<>();
        Map<String, Double> canteenWallets = new HashMap<>();

        public WalletDatabase() {
            // Seed initial state
            hostelWallets.put("192511469", 1200.00);
            canteenWallets.put("192511469", 150.00);
        }

        public void printBalances(String tag) {
            System.out.printf("[%s] Hostel Wallet: ₹%.2f | Canteen Wallet: ₹%.2f | Total In System: ₹%.2f\n",
                    tag, hostelWallets.get("192511469"), canteenWallets.get("192511469"),
                    hostelWallets.get("192511469") + canteenWallets.get("192511469"));
        }

        // Demonstration of Auto-Commit Failure (Money vanishes)
        public void transferWithAutoCommitFailure(String studentId, double amount) {
            System.out.println("\n--- Scenario 1: Independent Auto-Commit with Simulated Power Failure ---");
            printBalances("Initial State");
            // Step 1: Debit hostel
            hostelWallets.put(studentId, hostelWallets.get(studentId) - amount);
            System.out.println("Step 1 Committed: Hostel debited ₹" + amount);
            printBalances("After Step 1 (Auto-Committed)");

            // Sudden crash occurs before Step 2 can execute
            System.out.println("CRASH! Network link dropped / server power cut before Step 2 executes!");
            System.out.println("Result: ₹" + amount + " has vanished from the student's balance permanently!");
            printBalances("Corrupted Inconsistent State");
        }

        // Demonstration of Atomic Transaction with Rollback (Money preserved)
        public void transferWithAtomicTransaction(String studentId, double amount, boolean simulateFailure) {
            System.out.println("\n--- Scenario 2: Single Atomic Transaction (commit / rollback) ---");
            // Snapshot initial state for rollback
            Map<String, Double> hostelBackup = new HashMap<>(hostelWallets);
            Map<String, Double> canteenBackup = new HashMap<>(canteenWallets);
            printBalances("Transaction Started");

            try {
                // Step 1: Debit
                if (hostelWallets.get(studentId) < amount) {
                    throw new IllegalStateException("Insufficient funds!");
                }
                hostelWallets.put(studentId, hostelWallets.get(studentId) - amount);
                System.out.println("Step 1 Executed (Uncommitted): Hostel debited ₹" + amount);

                if (simulateFailure) {
                    throw new RuntimeException("Simulated Database Error / Connection Timeout during credit step!");
                }

                // Step 2: Credit
                canteenWallets.put(studentId, canteenWallets.get(studentId) + amount);
                System.out.println("Step 2 Executed (Uncommitted): Canteen credited ₹" + amount);

                // Commit
                System.out.println("Commit Succeeded: Transaction committed permanently.");
                printBalances("Committed State");

            } catch (Exception e) {
                System.out.println("FAILURE DETECTED: " + e.getMessage());
                System.out.println("Invoking rollback()...");
                // Rollback restores initial snapshot
                hostelWallets = hostelBackup;
                canteenWallets = canteenBackup;
                System.out.println("Rollback complete. Zero money lost or created!");
                printBalances("Rolled-Back State");
            }
        }
    }

    // === PART B: SQL Injection Demonstration ===
    static class SecurityDatabase {
        private final Map<String, String> userStore = new HashMap<>();

        public SecurityDatabase() {
            // Seed genuine user
            userStore.put("geetika_admin", "Secr3tP@ssw0rd");
            userStore.put("student_user", "JavaPass2026");
        }

        // 1. Unsafe Concatenation Simulator
        public boolean unsafeLogin(String username, String password) {
            String simulatedSql = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";
            System.out.println("Generated SQL Query: " + simulatedSql);

            // Simulating SQL parser evaluation
            // An SQL injection payload containing ' OR '1'='1 or '-- causes tautology
            if (username.contains("' OR '1'='1") || password.contains("' OR '1'='1") || username.contains("' --")) {
                System.out.println("[UNSAFE EVALUATION]: SQL condition evaluated to TRUE due to syntax manipulation!");
                return true; // Bypass successful!
            }

            return userStore.containsKey(username) && userStore.get(username).equals(password);
        }

        // 2. Secure Parameterized Simulator (PreparedStatement)
        public boolean secureLogin(String username, String password) {
            System.out.println("Secure PreparedStatement Query: SELECT * FROM users WHERE username = ? AND password = ?");
            System.out.println("Bound Parameter 1 (literal string): [" + username + "]");
            System.out.println("Bound Parameter 2 (literal string): [" + password + "]");

            // The input is treated strictly as literal data matching the exact table column
            if (userStore.containsKey(username)) {
                return userStore.get(username).equals(password);
            }
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Task 05: Correctness and Safety Demonstration ===");
        System.out.println("Student: Geetika T | Reg No: 192511469\n");

        // --- PART A ---
        WalletDatabase db = new WalletDatabase();
        // Demonstrate failure in auto-commit
        db.transferWithAutoCommitFailure("192511469", 500.00);

        // Reset database
        db = new WalletDatabase();
        // Demonstrate transaction rollback on failure
        db.transferWithAtomicTransaction("192511469", 500.00, true);

        // Demonstrate transaction commit on success
        db.transferWithAtomicTransaction("192511469", 500.00, false);

        // --- PART B ---
        System.out.println("\n=======================================================");
        System.out.println("=== PART B: SQL Injection Attack vs Defense ===");
        System.out.println("=======================================================");

        SecurityDatabase secDb = new SecurityDatabase();

        String attackUsername = "admin' -- ";
        String attackPassword = "random_incorrect_password";

        System.out.println("\n[Testing Unsafe Login with Malicious Payload]:");
        boolean unsafeResult = secDb.unsafeLogin(attackUsername, attackPassword);
        System.out.println("Unsafe Login Outcome: " + (unsafeResult ? "AUTHENTICATION BYPASSED! (VULNERABLE)" : "Rejected"));

        System.out.println("\n[Testing Secure PreparedStatement Login with Identical Payload]:");
        boolean secureResult = secDb.secureLogin(attackUsername, attackPassword);
        System.out.println("Secure Login Outcome: " + (secureResult ? "Allowed" : "REJECTED SAFELY! (SECURE)"));
    }
}
