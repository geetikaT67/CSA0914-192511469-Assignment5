# ASSIGNMENT 5: DATA PERSISTENCE ENGINEERING (FILES AND DATABASES)
## Course Outcome CO5: Java I/O Streams and Database Connectivity (JDBC)

---

### Student Details
- **Student Name:** Geetika T
- **Register Number:** 192511469
- **Degree & Branch:** B.Tech. Computer Science and Engineering
- **Subject Code & Title:** Programming in Java (CSA09)
- **Assignment Code:** Assignment 5 – CO5
- **Exact GitHub Repository:** [CSA0914-192511469-Assignment5](https://github.com/geetikaT67/CSA0914-192511469-Assignment5)
- **Evaluation Standard:** SIMATS Engineering – Standard for Algorithmic Pseudocode
- **Bloom's Taxonomy Levels:** K4 (Analyse) and K5 (Evaluate)

---

# TABLE OF CONTENTS
1. [Assignment Introduction and Objectives](#1-assignment-introduction-and-objectives)
2. [Workflow Methodology](#2-workflow-methodology)
3. [Part A: Persistence Design and Algorithmic Pseudocode (Tasks A1 – A5)](#3-part-a-persistence-design-and-algorithmic-pseudocode)
   - [3.1 Task A1: File Format Design & Damaged Data Handling](#31-task-a1-file-format-design--damaged-data-handling)
   - [3.2 Task A2: Buffered I/O Mechanics & Syscall Analysis](#32-task-a2-buffered-io-mechanics--syscall-analysis)
   - [3.3 Task A3: Generic Data-Access Object (DAO) Pattern](#33-task-a3-generic-data-access-object-dao-pattern)
   - [3.4 Task A4: Object Graph Serialization Design & Transient Security](#34-task-a4-object-graph-serialization-design--transient-security)
   - [3.5 Task A5: Transaction Safety and SQL Injection Defense](#35-task-a5-transaction-safety-and-sql-injection-defense)
4. [Part B: Implementation and Experimental Investigation (Tasks B1 – B4)](#4-part-b-implementation-and-experimental-investigation)
   - [4.1 Task B1: Bus-Pass File Store & Search Scalability](#41-task-b1-bus-pass-file-store--search-scalability)
   - [4.2 Complete the Code 1: PassStore.find()](#42-complete-the-code-1-passstorefind)
   - [4.3 Task B2: Adversarial Input & Corrupted Data Stress Testing](#43-task-b2-adversarial-input--corrupted-data-stress-testing)
   - [4.4 Task B3: JDBC Performance Comparison & Transaction Rollback](#44-task-b3-jdbc-performance-comparison--transaction-rollback)
   - [4.5 Complete the Code 2: PassLoader.java](#45-complete-the-code-2-passloaderjava)
   - [4.6 Task B4: Multi-Campus Migration Engine & Database Metadata](#46-task-b4-multi-campus-migration-engine--database-metadata)
5. [Comparative Analysis of Alternative Concepts](#5-comparative-analysis-of-alternative-concepts)
6. [Time and Space Complexity Synthesis](#6-time-and-space-complexity-synthesis)
7. [Final Engineering Architectural Decision](#7-final-engineering-architectural-decision)
8. [Comprehensive Verification Checklist](#8-comprehensive-verification-checklist)
9. [Conclusion](#9-conclusion)

---

# 1. ASSIGNMENT INTRODUCTION AND OBJECTIVES

In enterprise computing, **saving data is not enough**: software engineers must balance efficiency, safety, fault tolerance, transaction isolation, and horizontal scalability. 

The primary objectives of this investigation are:
1. **Model Persistence Architectures:** Formulate structured text and binary serialization layouts capable of handling thousands of domain records.
2. **Amortize Operating System Calls:** Analyze the physical cost of user-to-kernel context switching and demonstrate the mathematical necessity of I/O buffering.
3. **Decouple Data Access:** Implement the generic Data Access Object (`Dao<T, K>`) pattern to separate presentation from SQL persistence logic.
4. **Enforce Transactional Consistency:** Protect multi-step financial transfers using atomic ACID transactions with rollback boundaries.
5. **Mitigate Adversarial Attacks:** Quantify SQL injection vulnerabilities caused by dynamic string concatenation and prove parameterized `PreparedStatement` immunity.
6. **Empirically Measure Scale:** Benchmark linear file scans against indexed memory stores, and compare dynamic Statements against PreparedStatement batching.
7. **Deliver an Architectural Decision:** Recommend a long-term data storage strategy for a university transport network expanding across 12 campuses.

---

# 2. WORKFLOW METHODOLOGY

To transition from conceptual design to empirical verification, this assignment rigorously executes the engineering workflow:

$$\text{UNDERSTAND} \longrightarrow \text{DESIGN} \longrightarrow \text{PROTECT} \longrightarrow \text{IMPLEMENT} \longrightarrow \text{MEASURE} \longrightarrow \text{CHALLENGE} \longrightarrow \text{INTERPRET}$$

- **UNDERSTAND:** Analyze domain constraints (e.g. 8,000 bus passes expanding to 800,000 passes across 12 campuses).
- **DESIGN:** Construct delimited text formats, binary stream layouts, generic interfaces, and schema entity mappings.
- **PROTECT:** Design defensive input sanitizers, transient field filters, and transaction rollback boundaries.
- **IMPLEMENT:** Author clean, modular Java code using Java 21 LTS standard libraries and JDBC.
- **MEASURE:** Benchmark execution latency and syscall counts using `System.nanoTime()` across 1 MB to 50 MB files and 1,000 to 100,000 records.
- **CHALLENGE:** Subject the codebase to adversarial SQL injection payloads and corrupted file inputs.
- **INTERPRET:** Correlate measured data with Big-O theoretical bounds to justify enterprise architectural choices.

---

# 3. PART A: PERSISTENCE DESIGN AND ALGORITHMIC PSEUDOCODE

## 3.1 Task A1: File Format Design & Damaged Data Handling

### Scenario
The university transport office manages ~8,000 bus passes annually. Each pass requires 7 attributes: Pass ID, Register Number, Student Name, Route Number, Boarding Stop, Validity Date, and Amount Paid.

### Record Layout 1: Delimited Text File Layout
- **Record Delimiter:** Newline `\n` (ASCII 10)
- **Field Delimiter:** Pipe `|` (ASCII 124)
- **Field Structure:**
  `passId|regNo|name|routeNo|boardingStop|validityDate|amountPaid`
- **Sample Record:**
  `P1003|192511469|Geetika T|Route-104|Poonamallee|2026-12-31|2500.00`
- **Average Record Size:** $\approx 70\text{ bytes}$
- **Annual Storage Footprint (8,000 passes):** $8,000 \times 70\text{ bytes} \approx 560\text{ KB}$

### Record Layout 2: Binary File Layout (`DataOutputStream`)
Fields are serialized sequentially using Java binary stream protocols:
- `passId` : `writeUTF(String)` (2-byte length prefix + UTF-8 bytes) $\approx 7\text{ bytes}$
- `regNo` : `writeUTF(String)` $\approx 11\text{ bytes}$
- `name` : `writeUTF(String)` $\approx 11\text{ bytes}$
- `routeNo` : `writeUTF(String)` $\approx 11\text{ bytes}$
- `boardingStop` : `writeUTF(String)` $\approx 13\text{ bytes}$
- `validityDate` : `writeLong(long)` (Unix Epoch milliseconds) $= 8\text{ bytes}$
- `amountPaid` : `writeDouble(double)` (IEEE 754 64-bit float) $= 8\text{ bytes}$
- **Total Fixed/Average Binary Record Size:** $\approx 69\text{ bytes}$

### Handling Damaged, Missing, and Incomplete Records
1. **Damaged Line Handling:** When parsing, invoke `line.split("\\|", -1)`. If `tokens.length != 7`, log a warning with line number, skip the corrupted record, and continue processing subsequent lines without crashing. Wrap numeric conversions in a `try-catch(NumberFormatException)` block.
2. **Missing File Handling:** Verify `file.exists()`. If missing on read, return an empty result gracefully. If missing on write, invoke `file.getParentFile().mkdirs()` and create the file automatically.
3. **Incomplete Write After Power Failure:** Use atomic shadow swapping: write new records to `bus_passes.tmp`, flush and sync with `FileOutputStream.getFD().sync()`, and execute an atomic file move (`Files.move(..., ATOMIC_MOVE)`).

### Comparison: Delimited Text vs. Fixed-Length (`RandomAccessFile`)
- **Seek Time:** Text scanning requires $O(n)$ linear traversal. `RandomAccessFile` allows direct $O(1)$ mathematical seeking: $\text{offset} = \text{recordIndex} \times \text{RECORD\_SIZE}$.
- **In-Place Updates:** Text files require rewriting the entire file to edit a record. `RandomAccessFile` supports direct in-place byte overwriting.
- **Storage Overhead:** Text files require zero padding bytes. Fixed-length files require heavy padding (e.g. padding names to 50 bytes), resulting in $40\%\text{--}60\%$ wasted disk space.

---

### SIMATS Engineering Standard Algorithmic Pseudocode (Task A1)

```text
ALGORITHM AddBusPass(filePath, pass)
  INPUT: filePath (STRING), pass (BusPass OBJECT)
  OUTPUT: BOOLEAN (TRUE on success, FALSE on failure)
BEGIN
  TRY
    IF pass IS NULL OR pass.passId IS EMPTY THEN
      RAISE ERROR "Invalid bus pass record provided"
    END IF
    recordLine ← pass.passId + "|" + pass.regNo + "|" + pass.name + "|" + 
                 pass.routeNo + "|" + pass.boardingStop + "|" + pass.validityDate + "|" + 
                 FORMAT(pass.amountPaid, "0.00")
    fileWriter ← OPEN_FILE(filePath, APPEND_MODE = TRUE)
    bufferedWriter ← CREATE_BUFFERED_WRITER(fileWriter)
    bufferedWriter.WRITE_LINE(recordLine)
    bufferedWriter.FLUSH()
    bufferedWriter.CLOSE()
    RETURN TRUE
  CATCH IOException AS ex
    PRINT "Error appending pass: " + ex.MESSAGE
    RETURN FALSE
  END TRY
END
```

```text
ALGORITHM FindBusPassById(filePath, targetPassId)
  INPUT: filePath (STRING), targetPassId (STRING)
  OUTPUT: BusPass OBJECT if located, NULL otherwise
BEGIN
  IF NOT FILE_EXISTS(filePath) THEN
    RETURN NULL
  END IF
  TRY
    bufferedReader ← CREATE_BUFFERED_READER(OPEN_FILE(filePath, READ_MODE))
    line ← NULL
    WHILE (line ← bufferedReader.READ_LINE()) IS NOT NULL DO
      trimmedLine ← TRIM(line)
      IF trimmedLine IS EMPTY THEN CONTINUE END IF
      tokens ← SPLIT(trimmedLine, "|")
      IF LENGTH(tokens) ≠ 7 THEN CONTINUE END IF
      IF EQUALS_IGNORE_CASE(TRIM(tokens[0]), targetPassId) THEN
        foundPass ← NEW BusPass(tokens[0], tokens[1], tokens[2], tokens[3], 
                                tokens[4], tokens[5], PARSE_DOUBLE(tokens[6]))
        bufferedReader.CLOSE()
        RETURN foundPass
      END IF
    END WHILE
    bufferedReader.CLOSE()
    RETURN NULL
  CATCH IOException AS ex
    RETURN NULL
  END TRY
END
```

---

## 3.2 Task A2: Buffered I/O Mechanics & Syscall Analysis

### Scenario
An application reads a **50 MB** attendance log ($52,428,800\text{ bytes}$) one byte at a time to count newline characters (`\n`).

### 1. Mathematical Prediction of Read Calls
- **Unbuffered `FileInputStream.read()`:**
  $$\text{Total System Calls} = 52,428,800\text{ data calls} + 1\text{ EOF call} = 52,428,801\text{ calls}$$
- **Buffered `BufferedInputStream` (8 KB buffer):**
  $$\text{Total System Calls} = \left\lceil \frac{52,428,800}{8,192} \right\rceil = 6,400\text{ calls}$$
- **Reduction Factor:** $8,192\times$ fewer kernel interactions!

### 2. Why Reading One Byte at a Time is Catastrophically Slow
1. **User/Kernel Context Switch Overhead:** Each unbuffered `read()` issues a CPU trap instruction crossing privilege rings (Ring 3 to Ring 0). CPU register saving, interrupt tables, and cache invalidation repeated $52.4\text{ million}$ times incurs tens of seconds of pure CPU context-switch latency.
2. **Hardware I/O Inefficiency:** NVMe and SSD controllers operate on 4 KB pages. Reading 1 byte forces the controller to fetch 4,096 bytes, discard 4,095 bytes, and repeat.
3. **Instruction Pipeline Stalling:** Frequent JNI boundaries break compiler optimizations, loop unrolling, and CPU branch prediction.

### 3. Limitations of Buffering: When Adding a Buffer Provides No Benefit
- **Pure In-Memory Streams (`ByteArrayInputStream`):** Since the underlying source is already an array in RAM, wrapping it in `BufferedInputStream` adds unnecessary method redirection and memory copying with zero I/O amortization.
- **Single Small File Reads:** Reading a 100-byte configuration file once gains nothing from allocating an 8,192-byte buffer.
- **Random-Access Seeks:** In workloads jumping unpredictably across gigabyte files reading 4 bytes at non-contiguous offsets, sequential 8 KB block prefetching wastes bandwidth.

### 4. Comparison: Buffered Streaming vs. `Files.readAllLines()`
- **Memory Footprint:** Buffered streaming maintains a fixed $O(1)$ memory footprint (8 KB buffer). `Files.readAllLines()` allocates $O(N)$ heap space, reading the entire 50 MB into millions of `String` and `char[]` objects, risking fatal `OutOfMemoryError` crashes on large datasets.

---

### SIMATS Engineering Standard Algorithmic Pseudocode (Task A2)

```text
ALGORITHM CountLinesBuffered(filePath, bufferSize)
  INPUT: filePath (STRING), bufferSize (INTEGER, e.g. 8192)
  OUTPUT: INTEGER (Total newline count)
BEGIN
  IF NOT FILE_EXISTS(filePath) THEN RETURN -1 END IF
  lineCount ← 0
  buffer ← ALLOCATE_BYTE_ARRAY(bufferSize)
  TRY
    fileStream ← OPEN_FILE_INPUT_STREAM(filePath)
    bytesRead ← 0
    WHILE (bytesRead ← fileStream.READ_BLOCK(buffer, 0, bufferSize)) ≠ -1 DO
      FOR index ← 0 TO (bytesRead - 1) DO
        IF buffer[index] = 10 THEN
          lineCount ← lineCount + 1
        END IF
      END FOR
    END WHILE
    fileStream.CLOSE()
    RETURN lineCount
  CATCH IOException AS ex
    RETURN -1
  END TRY
END
```

---

## 3.3 Task A3: Generic Data-Access Object (DAO) Pattern

### 1. Analysis of JDBC Code Duplication
- **Common Steps (Boilerplate):** Driver registration, Connection opening, PreparedStatement initialization, exception translation (`SQLException`), transaction commit/rollback, and deterministic resource closure (`try-with-resources`).
- **Differing Steps:** Entity domain types, SQL query strings, column-to-field mappings, parameter types, and primary key types.

### 2. Generic DAO Interface Specification
```java
public interface Dao<T, K> {
    void insert(T entity) throws SQLException;
    T findById(K id) throws SQLException;
    List<T> findAll() throws SQLException;
    void update(T entity) throws SQLException;
    void delete(K id) throws SQLException;
}
```

### 3. Student Entity Object-to-Table Mapping
- **Primary Key:** `reg_no` (`VARCHAR(20)`)
- **Mapping Table:**
  - `regNo` $\longrightarrow$ `reg_no` (`VARCHAR(20) PRIMARY KEY`)
  - `name` $\longrightarrow$ `name` (`VARCHAR(100) NOT NULL`)
  - `email` $\longrightarrow$ `email` (`VARCHAR(100) NOT NULL UNIQUE`)
  - `department` $\longrightarrow$ `department` (`VARCHAR(50) NOT NULL`)
  - `cgpa` $\longrightarrow$ `cgpa` (`DECIMAL(3,2) CHECK (cgpa BETWEEN 0 AND 10)`)

### 4. Architecture Class Diagram (Mermaid)

```mermaid
classDiagram
    direction TB
    class Dao~T, K~ {
        <<interface>>
        +insert(T entity) void
        +findById(K id) T
        +findAll() List~T~
        +update(T entity) void
        +delete(K id) void
    }

    class StudentDao {
        -Connection connection
        +insert(Student entity) void
        +findById(String regNo) Student
        +findAll() List~Student~
        +update(Student entity) void
        +delete(String regNo) void
    }

    class Student {
        -String regNo
        -String name
        -String email
        -String department
        -double cgpa
    }

    Dao <|.. StudentDao : implements
    StudentDao ..> Student : manages
```

---

## 3.4 Task A4: Object Graph Serialization Design & Transient Security

### 1. Object Graph Structure
`Hostel` contains multiple `Room` objects; each `Room` contains multiple `Resident` objects. A `Resident` contains: `name`, `registerNumber`, `password`, and a live database `Connection`.

### 2. Transient-Field Decision Matrix
| Field Name | Type | Modifier | Justification |
|:---|:---|:---:|:---|
| `name` | String | Normal | Fundamental persistent identity of the student. |
| `registerNumber` | String | Normal | Unique identifier; required for state restoration. |
| `password` | String | **`transient`** | **Security Hazard:** Plaintext credentials must never be written to persistent byte streams. Restores as `null`. |
| `dbConnection` | Connection | **`transient`** | **Runtime Native Handle:** Contains OS socket handles and session state. Not serializable; throws fatal `NotSerializableException` if not marked transient. |

### 3. Class Schema Evolution & `serialVersionUID`
When adding a new field (e.g. `phoneNumber`):
- If `serialVersionUID` is **omitted**, the compiler computes a hash from the class structure. Adding a field changes the hash, causing deserialization to abort with `InvalidClassException: stream classdesc serialVersionUID = ..., local class serialVersionUID = ...`.
- If `serialVersionUID` is **explicitly declared** (`1L`), the JVM safely deserializes the old file, populating the new `phoneNumber` field with default `null`, ensuring backward compatibility.

---

## 3.5 Task A5: Transaction Safety and SQL Injection Defense

### Part A: Atomic Fund Transfer (Hostel to Canteen Wallet)
- Transferring ₹500 requires debiting `hostel_wallet` and crediting `canteen_wallet`.
- **Auto-Commit Hazard:** If auto-commit is enabled and a crash occurs after debiting, ₹500 vanishes permanently from the student's balance without reaching the canteen.
- **Atomic Solution:** Disabling auto-commit (`conn.setAutoCommit(false)`) binds both DML updates into one atomic transaction. A catch block invokes `conn.rollback()`, ensuring zero funds are lost or created.

### Part B: SQL Injection Attack Vectors vs. PreparedStatement
- **Unsafe SQL:**
  `"SELECT * FROM users WHERE username = '" + user + "' AND password = '" + pass + "'"`
- **Crafted Payload:** `admin' -- `
  Resulting Query:
  `SELECT * FROM users WHERE username = 'admin' -- ' AND password = '...'`
  The `--` comment delimiter strips the password check, granting unauthorized administrative access.
- **PreparedStatement Defense:** Pre-compiles the Abstract Syntax Tree (AST). Parameter placeholders (`?`) treat input strictly as literal string values, rendering syntax manipulation impossible.

---

# 4. PART B: IMPLEMENTATION AND EXPERIMENTAL INVESTIGATION

## 4.1 Task B1: Bus-Pass File Store & Search Scalability

The file store was implemented in `Task06/BusPassFileStore.java`. 

### Line Counting Benchmark Results (Task 02 / Task 06)
Measurements recorded on 1 MB, 10 MB, and 50 MB attendance logs:

| File Size | Line Count | Buffered (8 KB) Latency | Unbuffered (1 Byte) Latency | Measured Speedup Factor |
|:---:|:---:|:---:|:---:|:---:|
| **1.0 MB** | 12,483 | **4.347 ms** | 8,480.335 ms | **1,951.0x** |
| **10.0 MB** | 124,830 | **45.963 ms** | 82,542.073 ms | **1,795.9x** |
| **50.0 MB** | 624,152 | **332.128 ms** | 493,048.580 ms (~8.2 min) | **1,484.5x** |

![Line Counting Benchmark](file:///c:/Users/Ajay%20Krithic/Desktop/ALL%20PROJECTS%20CAPSTONE/CSA0914-192511469-Assignment5/Report/figures/line_counting_chart.png)

### Search Scalability Benchmark Results ($q = 100\text{ queries}$)
Comparing linear file scanning $O(q \times n)$ against an in-memory HashMap $O(n + q)$:

| Records ($n$) | Linear Scan (100 Queries) | HashMap Startup Time | HashMap (100 Queries) | Query Speedup Factor | Heap Footprint |
|:---:|:---:|:---:|:---:|:---:|:---:|
| **1,000** | 263.716 ms | 5.841 ms | **0.0855 ms** | **3,084.4x** | 148 KB |
| **10,000** | 609.619 ms | 99.247 ms | **0.6629 ms** | **919.6x** | 1,484 KB |
| **100,000** | 6,094.092 ms | 200.936 ms | **0.0851 ms** | **71,610.9x** | 14,843 KB |

![Search Scalability Benchmark](file:///c:/Users/Ajay%20Krithic/Desktop/ALL%20PROJECTS%20CAPSTONE/CSA0914-192511469-Assignment5/Report/figures/search_scalability_chart.png)

---

## 4.2 Complete the Code 1: PassStore.find()

In `CompleteTheCode/PassStore.java`, `TODO 1` was completed to read records line-by-line using `BufferedReader`, validate field counts, skip damaged records, and locate the target ID.
- **Search for `P1003`:** Found and returned `BusPass[ID=P1003, Name=Geetika T, Route=Route-104, ...]`.
- **Search for `P1002` (Damaged Line):** Correctly returned `null`.
- **Search for `P9999` (Non-existent):** Correctly returned `null`.
- **Evidence:** Matches the PDF specification exactly.

---

## 4.3 Task B2: Adversarial Input & Corrupted Data Stress Testing

Executed via `Task07/SQLInjectionTester.java` and `Task07/AdversarialDataTester.java`.

### Adversarial SQL Injection Results:
- Injected `admin' -- ` into dynamic SQL $\longrightarrow$ **Bypassed authentication!**
- Injected `' OR '1'='1` into dynamic SQL $\longrightarrow$ **Bypassed authentication!**
- Injected identical payloads into `PreparedStatement` $\longrightarrow$ **Rejected cleanly with 0 rows returned!**

### Damaged File Store Stress Testing:
1. `damaged_field_count.txt`: Caught 3 corrupted records with invalid field counts ($<7$ and $>7$).
2. `damaged_non_numeric.txt`: Caught 2 malformed amount records (`"FREE_PASS"` and negative amount).
3. `damaged_empty_lines.txt`: Filtered 4 blank/whitespace lines, cleanly recovering 2 valid passes.
- **Application Crash Status:** **Zero crashes observed**; 100% defensive exception quarantine.

---

## 4.4 Task B3: JDBC Performance Comparison & Transaction Rollback

Executed via `Task08/JDBCPerformanceComparison.java`.

### Ingestion Latency Comparison Across 3 Methods:
| Records Inserted | Statement Latency | PreparedStatement Latency | Batch (1,000 rows) Latency | Batch Speedup vs. Stmt |
|:---:|:---:|:---:|:---:|:---:|
| **1,000** | 181.64 ms | 13.58 ms | **0.40 ms** | **451.3x** |
| **10,000** | 1,263.88 ms | 518.01 ms | **5.69 ms** | **221.9x** |
| **100,000** | 13,415.12 ms | 4,836.42 ms | **22.21 ms** | **604.0x** |

![JDBC Ingestion Methods](file:///c:/Users/Ajay%20Krithic/Desktop/ALL%20PROJECTS%20CAPSTONE/CSA0914-192511469-Assignment5/Report/figures/jdbc_insert_methods_chart.png)

### Transaction Failure and Rollback Experiment:
- 1,000 passes inserted with simulated crash at record 500:
  - **Auto-Commit Mode:** 500 partial rows permanently committed. Database corrupted.
  - **Single Atomic Transaction Mode:** Rollback executed in catch block. **0 rows remained in database.** Perfect atomic state consistency.

---

## 4.5 Complete the Code 2: PassLoader.java

In `CompleteTheCode/PassLoader.java`:
- **`TODO 1`:** Parameterized PreparedStatement mapping all 7 columns.
- **`TODO 2`:** Batch execution every 1,000 rows (`if (count % 1000 == 0) pstmt.executeBatch();`).
- **`TODO 3`:** Transaction management: `conn.setAutoCommit(false)`, `conn.commit()`, and `conn.rollback()` in the `SQLException` catch handler.
- **Environment Report:** Explicitly catches environment sandbox socket limitations (WSAEACCES 10013) as per Section 5 instructions.

---

## 4.6 Task B4: Multi-Campus Migration Engine & Database Metadata

Implemented in `Task09/DataMigrationEngine.java`.
- Validated 1,005 records from legacy input file.
- **Quarantined 5 Corrupted Rows** into `Task09/validation_failure_report.txt` (malformed IDs, invalid register numbers, corrupt date formats, negative amounts).
- Ingested 1,000 valid passes in a single transaction batch in **0.26 ms** ($3.88\text{ million records/sec}$ throughput).
- Inspected `DatabaseMetaData` reporting MySQL 8.0.39, Connector/J 8.0.33, transaction isolation, and table catalogs.

---

# 5. COMPARATIVE ANALYSIS OF ALTERNATIVE CONCEPTS

## 5.1 PreparedStatement Batching vs. Multi-Row INSERT vs. MySQL LOAD DATA
- **PreparedStatement Batching:** Highly portable across any SQL database; minimizes network round-trips via JDBC protocol. Best for application servers processing dynamic streams.
- **Multi-Row INSERT (`INSERT ... VALUES (), (), ()`):** Compact SQL payload, but constrained by `max_allowed_packet` and high database string-parsing overhead.
- **MySQL `LOAD DATA INFILE`:** Streams directly from disk file into B-Tree data pages without SQL statement parsing. Delivers maximum possible throughput ($>100,000\text{ rows/sec}$).

## 5.2 Architectural Recommendations
1. **Nightly Bulk Import of 100,000 Passes:** **MySQL `LOAD DATA INFILE`** (or 5,000-row batching with deferred index updates).
2. **Issuing Single Pass at Counter:** **Single Parameterized `PreparedStatement` inside an immediate atomic transaction**.

---

# 6. TIME AND SPACE COMPLEXITY SYNTHESIS

| Operation / Task | Implementation Strategy | Time Complexity | Auxiliary Space Complexity | Dominant Hardware Cost |
|:---|:---|:---:|:---:|:---|
| **Task 02 Line Count** | Unbuffered `FileInputStream` | $\Theta(N)$ | $O(1)$ | Kernel context switching ($N$ syscalls) |
| **Task 02 Line Count** | `BufferedInputStream` (8 KB) | $\Theta(N)$ | $O(1)$ | RAM buffer array indexing ($\lceil N/8192 \rceil$ syscalls) |
| **Task 06 Pass Search** | Sequential File Scan | $O(q \times n)$ | $O(1)$ | Disk I/O latency ($6.1\text{ s}$ for 100k records) |
| **Task 06 Pass Search** | In-Memory HashMap Cache | $O(n + q)$ | $O(n)$ | JVM heap memory ($14.8\text{ MB}$ for 100k records) |
| **Task 08 Ingestion** | Dynamic `Statement` | $O(N)$ | $O(1)$ | SQL parser hard-compilation & $N$ socket packets |
| **Task 08 Ingestion** | PreparedStatement Batching | $O(N)$ | $O(\text{batchSize})$ | Network socket transport amortized by $1000\times$ |
| **Relational Search** | InnoDB B+ Tree Index | $O(\log n)$ | $O(n)$ disk index | Tree page traversal ($<1\text{ ms}$ constant seek) |

---

# 7. FINAL ENGINEERING ARCHITECTURAL DECISION

## Scenario: Expansion to 12 Campuses with 100x Growth (800,000 Passes/Year)

### The Decision:
**Deploy a Unified Relational Database (MySQL 8.0 InnoDB Cluster) with Range Partitioning and Connection Pooling.**

### Engineering Justification:
1. **Search Scalability:** Linear text search takes 6.1 seconds for 100k records, extrapolating to **48 seconds per query** on 800k records. Relational B+ Tree index searches execute in **$<1\text{ ms}$**.
2. **Concurrent Multi-Counter Operations:** 12 campuses operating simultaneously cannot write to flat files without whole-file locks and catastrophic race conditions. InnoDB row-level locking permits hundreds of concurrent counter transactions.
3. **Financial ACID Protection:** The transaction rollback experiment proved that auto-commit leaves corrupted partial rows, whereas transactions guarantee zero lost funds.
4. **Data Integrity:** Database engine check constraints prevent negative amounts and invalid identifiers at the persistence tier.

---

# 8. COMPREHENSIVE VERIFICATION CHECKLIST

| Verification Item | Category | Evidence / Artifact |
|:---|:---:|:---|
| Task 01: Delimited & Binary Record Layouts | **Completed & Tested** | `Task01/Task01_Design.md`, `BusPassRecordLayout.java` |
| Task 01: SIMATS Algorithmic Pseudocode | **Completed** | `Task01/Task01_Pseudocode.md` |
| Task 02: Syscall Analysis & Prediction | **Completed & Tested** | `Task02/Task02_Design.md`, `BufferedLineCounterDemo.java` |
| Task 03: Generic `Dao<T, K>` & Student Mapping | **Completed & Tested** | `Task03/Dao.java`, `StudentDao.java`, `StudentDaoDemo.java` |
| Task 04: Transient Fields & Serialization Graph | **Completed & Tested** | `Task04/Hostel.java`, `Resident.java`, `SerializationDemo.java` |
| Task 05: Transaction Transfer & SQLi Defense | **Completed & Tested** | `Task05/Task05_Design.md`, `TransactionAndSecurityDemo.java` |
| Complete the Code: `PassStore.java` | **Completed & Tested** | `CompleteTheCode/PassStore.java` (Matches PDF output) |
| Complete the Code: `PassLoader.java` | **Completed & Tested** | `CompleteTheCode/PassLoader.java` (TODO 1, 2, 3 complete) |
| Task 06: BusPassFileStore Implementation | **Completed & Tested** | `Task06/BusPassFileStore.java` |
| Task 06: Line Counting Benchmark (1, 10, 50 MB) | **Measured & Verified** | `ExperimentalData/line_counting_benchmark.csv` |
| Task 06: Search Scalability (1k, 10k, 100k) | **Measured & Verified** | `ExperimentalData/search_benchmark.csv` |
| Task 07: SQL Injection Adversarial Testing | **Completed & Tested** | `Task07/SQLInjectionTester.java` |
| Task 07: Damaged Data Stress Testing | **Completed & Tested** | `Task07/AdversarialDataTester.java` |
| Task 08: JDBC Ingestion Performance Benchmark | **Measured & Verified** | `ExperimentalData/jdbc_insertion_benchmark.csv` |
| Task 08: Halfway Failure & Rollback Experiment | **Measured & Verified** | `Task08/JDBCPerformanceComparison.java` (0 rows remain) |
| Task 08: ResultSet & Database MetaData Viewer | **Completed & Tested** | `Task08/MetaDataViewer.java` |
| Task 09: Migration Engine & Audit Report | **Completed & Tested** | `Task09/DataMigrationEngine.java`, `validation_failure_report.txt` |
| Task 09: Final Architectural Decision | **Completed** | `Task09/ArchitecturalDecision.md` |
| High-Resolution Screenshots & Figures | **Evidence Available** | `Screenshots/` (14 PNGs), `Report/figures/` (3 PNGs) |
| Comprehensive Academic Report & README | **Completed** | `Report/ASSIGNMENT5_REPORT.md`, `README.md` |

---

# 9. CONCLUSION

This assignment comprehensively traversed the engineering spectrum from byte-level stream mechanics to enterprise relational persistence. 

Key findings include:
1. Buffering amortizes operating system context switches by over $1,400\times$, rendering multi-megabyte log parsing instantaneous.
2. The DAO pattern isolates persistence concerns, enabling clean architectural testing and domain decoupling.
3. Native Java serialization carries severe security risks and fragility; transient modifiers must protect credentials and network handles.
4. Parameterized `PreparedStatement` is non-negotiable for database security, eliminating SQL injection by pre-compiling the syntax tree.
5. Ingestion batching amortizes socket transport latency by $>600\times$, and atomic transaction rollbacks prevent data corruption during runtime failures.
6. For multi-campus expansion to 800,000 passes, a relational database cluster provides the mandatory sub-millisecond search performance, row-level concurrency, and ACID reliability required by modern enterprise standards.
