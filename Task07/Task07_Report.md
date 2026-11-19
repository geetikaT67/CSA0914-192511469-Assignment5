# Task 07 – B2: Adversarial Input Analysis and Report

## Student Details
- **Student Name:** Geetika T
- **Register Number:** 192511469
- **Subject:** Programming in Java (CSA09)
- **Assignment:** Assignment 5 – CO5: Data Persistence Engineering

---

# PART A: SQL Injection Adversarial Investigation

## 1. Experimental Setup and Attack Vectors
A local security test was executed comparing dynamic SQL string concatenation against parameterized `PreparedStatement` queries.

### Tested Adversarial Payloads:
1. `admin' -- ` : Comment-out injection targeting administrator account bypass.
2. `' OR '1'='1` : Boolean tautology injection designed to extract all rows.
3. `geetika' -- ` : Privilege escalation attack targeting user account.

## 2. Experimental Observations

| Payload Injected | Password Provided | Unsafe Dynamic SQL Result | Secure PreparedStatement Result | Security Analysis |
|:---|:---|:---:|:---:|:---|
| `admin' -- ` | `wrong_attacker_pwd` | **BYPASSED (Logged in as admin)** | **REJECTED (Access Denied)** | Unsafe parser treats `--` as comment, skipping password check. PreparedStatement treats string literally. |
| `' OR '1'='1` | `wrong_attacker_pwd` | **BYPASSED (Tautology True)** | **REJECTED (Access Denied)** | Boolean condition evaluates to true across entire table in unsafe SQL. |
| `geetika' -- ` | `wrong_attacker_pwd` | **BYPASSED (Logged in as geetika)** | **REJECTED (Access Denied)** | Targeted account hijacked without credentials. |
| `geetika` (Legit) | `Geetika@Pass123` | **Allowed** | **Allowed** | Legitimate authentic credentials correctly accepted. |

## 3. What the Tests Reveal About the Original Design
1. **Dynamic String Concatenation Violates Code-Data Separation:**
   The fundamental design flaw is that user data is merged into the grammar stream of the SQL command, allowing user input to change the parser's Abstract Syntax Tree (AST).
2. **PreparedStatement Enforces Strong Typing and Binary Parameterization:**
   By pre-compiling the query structure before parameters are bound, the database engine treats all input strictly as literal literals, ensuring 100% immunity against injection attacks.

---

# PART B: Damaged Bus-Pass Data Investigation

## 1. Adversarial Datasets Evaluated

### 1.1 Dataset 1: Wrong Field Count (`damaged_field_count.txt`)
- **Row 1:** 4 fields instead of 7 (`P9001|192511469|Geetika T|Route-101`)
- **Row 2:** 2 fields instead of 7 (`P9002|192511470`)
- **Row 3:** 9 fields with extraneous trailing tokens
- **Robustness Test:** Verifies that splitting the line does not throw `ArrayIndexOutOfBoundsException` and skips lines where `tokens.length != 7`.

### 1.2 Dataset 2: Non-Numeric Amounts (`damaged_non_numeric.txt`)
- **Row 1:** Text literal `"FREE_PASS"` in amount position.
- **Row 2:** Value `"NaN"` (Not-a-Number).
- **Row 3:** Negative financial value (`-1500.00`).
- **Robustness Test:** Verifies that `Double.parseDouble()` is guarded by `try-catch(NumberFormatException)` and business constraints reject negative amounts without crashing the reader.

### 1.3 Dataset 3: Empty Lines (`damaged_empty_lines.txt`)
- **Rows:** Interspersed blank lines, spaces (`"   "`), and trailing empty newlines.
- **Robustness Test:** Verifies that `line.trim().isEmpty()` skips empty rows gracefully instead of parsing them as null tokens.

## 2. Experimental Execution Audit

```text
--- Robustness Audit Summary: damaged_field_count.txt ---
  Total Lines Scanned:      3
  Empty Lines Filtered:     0
  Invalid Field Counts:     3 (All caught and logged)
  Malformed Amounts Caught: 0
  Valid Records Preserved:  0
  Crash Occurred:           NO (100% Robust Exception Handling)

--- Robustness Audit Summary: damaged_non_numeric.txt ---
  Total Lines Scanned:      3
  Empty Lines Filtered:     0
  Invalid Field Counts:     0
  Malformed Amounts Caught: 3 (Caught via NumberFormatException and negative check)
  Valid Records Preserved:  0
  Crash Occurred:           NO (100% Robust Exception Handling)

--- Robustness Audit Summary: damaged_empty_lines.txt ---
  Total Lines Scanned:      6
  Empty Lines Filtered:     4
  Invalid Field Counts:     0
  Malformed Amounts Caught: 0
  Valid Records Preserved:  2 (P9007 and P9008 extracted successfully!)
  Crash Occurred:           NO (100% Robust Exception Handling)
```

## 3. Robustness Conclusion
The defensive programming mechanisms:
1. `split("\\|", -1)` with length verification.
2. `line.trim().isEmpty()` filtering.
3. Dedicated `try-catch(NumberFormatException)` wrapping.
guarantee that corrupted lines are logged and quarantined while the application continues processing remaining valid records without interruption.
