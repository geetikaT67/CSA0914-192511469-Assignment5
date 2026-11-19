# Task 06 – B1: File Store and Alternative Design Report

## Student Details
- **Student Name:** Geetika T
- **Register Number:** 192511469
- **Subject:** Programming in Java (CSA09)
- **Assignment:** Assignment 5 – CO5: Data Persistence Engineering

---

## 1. Implementation Overview
The `BusPassFileStore` class implements the complete persistence store for the university transport office using a pipe-delimited text file format.
The three mandatory operations are:
1. `add(BusPass pass)`: Appends formatted records to the file.
2. `find(String passId)`: Sequentially scans the file and returns the matching `BusPass` or `null`.
3. `listByRoute(String routeNo)`: Filters and returns all valid passes on a specified route.

---

## 2. Test Cases and Execution Evidence

| Test ID | Test Scenario | Input Data | Expected Behavior | Actual Observed Result | Status |
|:---:|:---|:---|:---|:---|:---:|
| **TC-01** | Add new valid passes | `P1001`, `P1002`, `P1003` | Records appended with newline | Appended 3 records to file | **PASS** |
| **TC-02** | Find existing pass | `passId = "P1001"` | Returns full BusPass object | `BusPass[ID=P1001, Name=Geetika T, ...]` | **PASS** |
| **TC-03** | Find non-existent pass | `passId = "P9999"` | Returns `null` | Returned `null` cleanly | **PASS** |
| **TC-04** | Find across damaged line | `P1004` (Amount = `INVALID_AMOUNT`) | Skips/returns `null` | Returned `null` without throwing unhandled exception | **PASS** |
| **TC-05** | Find valid pass located after damaged records | `passId = "P1005"` | Finds pass despite earlier damaged lines | Found `BusPass[ID=P1005, Name=Karthik K, ...]` | **PASS** |
| **TC-06** | Filter passes by route | `routeNo = "Route-104"` | Returns only Route-104 passes | Returned 3 matching valid passes | **PASS** |

---

## 3. Comparison: Java Implementation vs. Part A Pseudocode

| Algorithmic Construct | Part A Pseudocode (`Task01_Pseudocode.md`) | Part B Java Implementation (`BusPassFileStore.java`) | Fidelity & Conformity |
|:---|:---|:---|:---|
| **File Open Mode** | `OPEN_FILE(filePath, APPEND_MODE = TRUE)` | `new FileWriter(dataFile, true)` | Identical append-only behavior. |
| **Tokenization** | `SPLIT(line, "\|")` | `line.split("\\|", -1)` | Strict pipe splitting without dropping empty tokens. |
| **Validation Filter** | `IF LENGTH(tokens) ≠ 7 THEN CONTINUE` | `if (tokens.length != 7) continue;` | Identical token count verification. |
| **Case Sensitivity** | `EQUALS_IGNORE_CASE(tokens[0], targetId)` | `tokens[0].trim().equalsIgnoreCase(passId)` | Robust case-insensitive identifier matching. |
| **Resource Cleanup** | `bufferedReader.CLOSE()` | Java 7+ `try-with-resources` | Deterministic automatic stream closure. |

---

## 4. Alternative Design: Linear File Scanning vs. In-Memory HashMap Cache

### 4.1 Theoretical Complexity Analysis:
- Let $n$ be the total number of records in the file store.
- Let $q$ be the number of search queries executed during system runtime.

#### Approach A: File Scanning (On-Demand Sequential I/O)
- Every single search opens the file and scans on average $n/2$ records.
- Time Complexity for $q$ searches:
  $$T_{\text{scan}}(n, q) = O(q \times n)$$
- Auxiliary Space Complexity:
  $$S_{\text{scan}} = O(1)\text{ (Only one line buffer in memory)}$$

#### Approach B: In-Memory HashMap Cache (Loaded at Startup)
- Startup phase reads the entire file once and inserts $n$ records into a `HashMap`: $O(n)$.
- Each subsequent search performs an $O(1)$ expected hash lookup.
- Time Complexity for $q$ searches:
  $$T_{\text{hash}}(n, q) = O(n + q)$$
- Auxiliary Space Complexity:
  $$S_{\text{hash}} = O(n)\text{ (Heap memory holds all domain objects)}$$

### 4.2 Crossover Point and Workload Suitability:
- When $q$ is very small (e.g., $q = 1$ during a one-off CLI query), scanning is faster because it avoids loading all $n$ records.
- When $q \gg 1$ (e.g., counter issuing tickets with hundreds of daily lookups), the HashMap provides **orders-of-magnitude speedup**:
  $$O(n + q) \ll O(q \times n)$$

### 4.3 Why Damaged Lines Must Not Stop the Complete Search
In real-world university persistence systems, power cuts, bad disk sectors, or manual text-editor modifications can corrupt a single line (e.g., leaving 3 tokens instead of 7).
- If the parsing loop threw an unhandled exception or aborted:
  $$\text{All } (n - k) \text{ valid records appearing after the damaged line would become unreachable!}$$
- By skipping damaged records with a logged warning, the search engine maintains high availability and fault isolation.
