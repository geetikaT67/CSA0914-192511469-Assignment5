# Task 05 – A5: Correctness and Safety (Transactions & SQL Injection)

## Student Details
- **Student Name:** Geetika T
- **Register Number:** 192511469
- **Subject:** Programming in Java (CSA09)
- **Assignment:** Assignment 5 – CO5: Data Persistence Engineering

---

# PART A: JDBC Transactions & Wallet Fund Transfer

## 1. Scenario Overview
A financial transaction transfers ₹500 from a student's `hostel_wallet` to their `canteen_wallet`.
- **Step 1:** Debit ₹500 from `hostel_wallet`.
- **Step 2:** Credit ₹500 to `canteen_wallet`.

---

## 2. The Purpose of Disabling Auto-Commit
By default in JDBC, `Connection.getAutoCommit()` is `true`. Every individual SQL statement executed is automatically committed to persistent storage as an independent, irreversible transaction.

### Why Auto-Commit MUST Be Disabled:
```java
connection.setAutoCommit(false);
```
1. **Enforcing ACID Atomicity:** Atomic execution demands that a business operation consisting of multiple SQL statements succeeds completely or fails completely ("all-or-nothing").
2. **Preventing Intermediate State Exposure:** With auto-commit disabled, modifications reside in temporary transaction undo/redo logs in the database engine, invisible to concurrent transactions (depending on isolation level), until explicit commit.
3. **Enabling Manual Rollback:** If an exception (e.g. network partition, power failure, JVM crash, constraint violation, or insufficient balance) occurs between Step 1 and Step 2, `connection.rollback()` reverts Step 1 entirely, preserving financial equilibrium.

---

## 3. Single Transaction vs. Two Independent Auto-Commit Updates

| Failure Scenario | Two Independent Auto-Commit Updates | Single Atomic Transaction (`setAutoCommit(false)`) |
|:---|:---|:---|
| **Step 1 succeeds (Hostel debited ₹500), then power cut / server crash occurs before Step 2** | **Disaster (Data Inconsistency):** Step 1 was committed permanently. Step 2 never ran. ₹500 has vanished into thin air from the student's balance without reaching the canteen! | **Consistent (Guaranteed Atomicity):** Because `commit()` was never reached, database crash-recovery / rollback automatically aborts Step 1 upon reboot. Balance is preserved. |
| **Step 2 fails due to SQL constraint violation (e.g. canteen account locked or invalid ID)** | Step 1 remains committed; student loses ₹500 with zero recourse. | Rollback invoked in catch block; hostel account is restored to original balance. |
| **Concurrent reading by auditing process** | Auditor can observe inconsistent intermediate state (money missing in transit). | Transaction isolation prevents other sessions from viewing uncommitted debits. |

---

# PART B: SQL Injection Vulnerability and Defense

## 1. Vulnerability Analysis of Unsafe String Concatenation

### The Vulnerable Query:
```java
String sql = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";
Statement stmt = connection.createStatement();
ResultSet rs = stmt.executeQuery(sql);
```

### 1.1 The Attack Mechanism:
SQL injection occurs when untrusted user input is concatenated directly into an SQL string without structural separation between code and data. The database SQL compiler cannot distinguish between the developer's intended query syntax and the attacker's malicious SQL control characters (such as single quotes `'`, hyphens `--`, or boolean operators `OR`).

### 1.2 Crafted Adversarial Attack Vectors:
1. **Authentication Bypass Payload:**
   - **Malicious Username:** `admin' -- `
   - **Password:** `anything`
   - **Resulting Query Sent to Database:**
     ```sql
     SELECT * FROM users WHERE username = 'admin' -- ' AND password = 'anything'
     ```
   - **How it Works:** The single quote `'` closes the username literal early. The `--` characters represent an SQL comment delimiter, causing the database parser to completely ignore the remainder of the query (the password check!). The attacker logs in as `admin` without knowing the password.

2. **Universal Tautology Payload:**
   - **Malicious Username:** `' OR '1'='1`
   - **Malicious Password:** `' OR '1'='1`
   - **Resulting Query Sent to Database:**
     ```sql
     SELECT * FROM users WHERE username = '' OR '1'='1' AND password = '' OR '1'='1'
     ```
   - **How it Works:** The condition `'1'='1'` evaluates to `TRUE` for every row in the `users` table. The query returns the first user in the database (typically the administrator), completely bypassing authentication.

---

## 2. Redesign Using PreparedStatement and Parameterization

### Secure Implementation:
```java
String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
    pstmt.setString(1, username);
    pstmt.setString(2, password);
    try (ResultSet rs = pstmt.executeQuery()) {
        return rs.next(); // True only if genuine match exists
    }
}
```

### 2.1 Why Parameterized Queries Protect Against SQL Injection:
1. **Pre-Compilation of AST (Abstract Syntax Tree):**
   When `connection.prepareStatement(sql)` is called, the database parses and compiles the query skeleton *before* any user input is provided. The grammar, clauses, and logic are permanently fixed into execution bytecode.
2. **Type-Safe Data Binding:**
   When `pstmt.setString(1, input)` is executed, the driver sends the input values as raw literal parameters through a dedicated binary protocol (or escapes them strictly).
3. **Immunity to Syntax Modification:**
   If an attacker submits `' OR '1'='1`, the database treats the entire sequence literally as a string of 10 characters matching the column value. It looks for a user whose literal username in the table is exactly `' OR '1'='1`. Because no such user exists, the query safely returns 0 rows and rejects the attack.

---

## 3. Security Comparison Matrix

| Evaluation Dimension | Dynamic SQL Concatenation (`Statement`) | Parameterized SQL (`PreparedStatement`) |
|:---|:---|:---|
| **SQL Injection Vulnerability** | **Extremely Vulnerable:** User input alters query structure. | **100% Immune:** User input is strictly treated as literal data. |
| **Parsing Performance** | Hard parsing required on every query; high database CPU load. | Pre-compiled once; execution plan cached in database memory ($O(1)$ reuse). |
| **Handling Special Characters** | Fails or crashes on names with apostrophes (e.g., `O'Connor`). | Handles apostrophes, emojis, binary data, and quotes transparently. |
| **Code Readability & Cleanliness** | Messy quote and plus-sign concatenation; error-prone. | Clean, structured SQL templates with explicit typed setters. |
| **Compliance & Industry Standards** | Prohibited by OWASP Top 10 and PCI-DSS compliance standards. | Mandatory industry best practice for all persistent database operations. |
