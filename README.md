# Data Persistence Engineering – Files and Databases
### Programming in Java (CSA09) – Assignment 5 (CO5)

---

## Student Details
- **Student Name:** Geetika T
- **Register Number:** 192511469
- **Assignment:** Assignment 5 – CO5: Data Persistence Engineering (Files and Databases)
- **Subject:** Programming in Java (CSA09)
- **Programming Language:** Java
- **Java Version:** Java 21 LTS (`jdk-21.0.4`)
- **Exact GitHub Repository:** [CSA0914-192511469-Assignment5](https://github.com/geetikaT67/CSA0914-192511469-Assignment5)
- **Mode:** Individual Assignment | **Total Marks:** 50

---

## 1. Assignment Overview
This repository contains the complete engineering design, algorithmic pseudocode following the **SIMATS Engineering Standard**, Java implementations, SQL schema scripts, adversarial stress testing, JDBC performance benchmarks, and architectural evaluation for **Assignment 5 (Course Outcome CO5: Java I/O and JDBC)**.

The assignment follows the mandatory engineering workflow:
$$\text{UNDERSTAND} \longrightarrow \text{DESIGN} \longrightarrow \text{PROTECT} \longrightarrow \text{IMPLEMENT} \longrightarrow \text{MEASURE} \longrightarrow \text{CHALLENGE} \longrightarrow \text{INTERPRET}$$

Saving data is not enough: the solution models data stores, evaluates byte-level streaming efficiency, decouples persistence via the generic DAO pattern, secures transactions with ACID rollback boundaries, neutralizes adversarial SQL injection attacks, measures scale across datasets up to 100,000 records, and justifies enterprise architectural decisions for 12 university campuses.

---

## 2. Project Folder Structure

```text
CSA0914-192511469-Assignment5/
├── Task01/              # A1: Design a File Format (Delimited & Binary layouts, RAF comparison)
├── Task02/              # A2: Buffered I/O (Syscall prediction, 8KB buffer design, Files.readAllLines)
├── Task03/              # A3: Data-Access Design Pattern (Generic Dao<T,K>, Student entity mapping)
├── Task04/              # A4: Serialization Design (Object graph, transient modifiers, serialVersionUID)
├── Task05/              # A5: Correctness and Safety (Atomic wallet transfer, SQL injection defense)
├── Task06/              # B1: Bus-Pass File Store & Search Scalability (add, find, list, benchmarks)
├── Task07/              # B2: Adversarial Input (SQLi test harness & damaged data robustness testing)
├── Task08/              # B3: JDBC Performance Comparison (Statement vs PreparedStatement vs Batch)
├── Task09/              # B4: Final Engineering Decision (Migration engine, audit log, campus decision)
├── CompleteTheCode/     # Supplied programs: PassStore.find() & PassLoader.java (TODOs completed)
├── SQL/                 # DDL and seed scripts: create_bus_pass.sql
├── TestData/            # Generated datasets: 1k, 10k, 100k pass files, 1MB, 10MB, 50MB logs, adversarial files
├── ExperimentalData/    # Raw empirical benchmark measurements saved as CSV files
├── Screenshots/         # High-resolution console execution outputs and comparison charts
├── Report/              # Comprehensive 20-section academic report (ASSIGNMENT5_REPORT.md and figures)
├── README.md            # Master repository documentation
```

---

## 3. Required Software and Environment Setup

- **Java Development Kit (JDK):** Java 21 LTS or higher (`javac` and `java` in PATH).
- **Relational Database Engine:** MySQL Server 8.0 or higher.
- **JDBC Driver:** MySQL Connector/J (`mysql-connector-j-8.0.33.jar`).
- **Python (Optional for charts):** Python 3.10+ with `matplotlib`, `pandas`, and `Pillow`.

---

## 4. MySQL Database Setup Instructions

1. Open MySQL Workbench or MySQL Command-Line Client:
   ```powershell
   mysql -u root -p
   ```
2. Execute the setup script located in the `SQL/` directory:
   ```sql
   SOURCE SQL/create_bus_pass.sql;
   ```
3. The script initializes:
   - Database `transport_db`.
   - Table `bus_pass` with `pass_id` PRIMARY KEY, indices on `route_no` and `reg_no`, and check constraints.
   - Tables `users`, `hostel_wallet`, and `canteen_wallet` with test seeds.

---

## 5. Compilation and Execution Instructions

All commands are run from the project root directory.

### Quick Build All Tasks:
```powershell
javac Task01/*.java Task02/*.java Task03/*.java Task04/*.java Task05/*.java Task06/*.java Task07/*.java Task08/*.java Task09/*.java CompleteTheCode/*.java
```

---

### Task-by-Task Execution Commands:

#### Task 01: File Format Record Layout
```powershell
javac Task01/BusPassRecordLayout.java
java -cp . Task01.BusPassRecordLayout
```

#### Task 02: Buffered I/O Demonstration
```powershell
javac Task02/BufferedLineCounterDemo.java
java -cp . Task02.BufferedLineCounterDemo
```

#### Task 03: Generic DAO Pattern
```powershell
javac Task03/*.java
java -cp . Task03.StudentDaoDemo
```

#### Task 04: Object Graph Serialization & Transient Security
```powershell
javac Task04/*.java
java -cp . Task04.SerializationDemo
```

#### Task 05: Transaction Rollback & SQL Injection Defense
```powershell
javac Task05/*.java
java -cp . Task05.TransactionAndSecurityDemo
```

#### Complete the Code: PassStore
```powershell
javac CompleteTheCode/PassStore.java
java -cp . CompleteTheCode.PassStore
```

#### Complete the Code: PassLoader
```powershell
javac CompleteTheCode/PassLoader.java
java -cp . CompleteTheCode.PassLoader
```

#### Task 06: Bus-Pass File Store & Benchmarks
```powershell
javac Task06/*.java
java -cp . Task06.BusPassFileStore
java -cp . Task06.LineCountingBenchmark
java -cp . Task06.PassSearchBenchmark
```

#### Task 07: Adversarial Input Testing
```powershell
javac Task07/*.java
java -cp . Task07.SQLInjectionTester
java -cp . Task07.AdversarialDataTester
```

#### Task 08: JDBC Ingestion Performance & Rollback
```powershell
javac Task08/*.java
java -cp . Task08.JDBCPerformanceComparison
java -cp . Task08.MetaDataViewer
```

#### Task 09: Data Migration Engine & Architectural Audit
```powershell
javac Task09/*.java
java -cp . Task09.DataMigrationEngine
```

---

## 6. Summary of Experimental Results Across All Tasks

| Task | Primary Objective | Key Measured / Observed Result | Status |
|:---:|:---|:---|:---:|
| **Task 01** | Record Formats | Text: ~70 B/rec, Binary: 73 B/rec. Round-trip serialization verified. | **PASS** |
| **Task 02** | Buffered I/O | 8 KB buffer achieves **471x speedup** on 1 MB, reducing syscalls from 1M to 129. | **PASS** |
| **Task 03** | Generic DAO Pattern | Full CRUD (`insert`, `findById`, `findAll`, `update`, `delete`) validated. | **PASS** |
| **Task 04** | Transient Security | Password and DB Connection safely restored as `null`; no credential leakage. | **PASS** |
| **Task 05** | Transaction Safety | Single transaction rolled back simulated error; zero money lost or created. | **PASS** |
| **Complete 1** | `PassStore.find()` | `P1003` returned; damaged `P1002` returned `null` (matches PDF spec). | **PASS** |
| **Complete 2** | `PassLoader.java` | 1,000-row batching and rollback handlers fully verified. | **PASS** |
| **Task 06** | Search Scalability | Linear scan took 6.1s for 100k records; HashMap took 0.08ms (**71,610x speedup**). | **PASS** |
| **Task 07** | Adversarial Inputs | Dynamic SQL bypassed with `admin' --`; `PreparedStatement` rejected attack. Survived corrupted data. | **PASS** |
| **Task 08** | JDBC Ingestion | Batching 1,000 rows was **604x faster** than individual statements (22ms vs 13.4s). | **PASS** |
| **Task 09** | Campus Migration | 1,000 records migrated in 0.26ms; 5 damaged records quarantined to audit log. | **PASS** |

---

## 7. Final Engineering Decision

For the expansion to **12 campuses with 800,000 bus passes annually**, the engineering evaluation overwhelmingly recommends a **Unified Relational Database Cluster (MySQL 8.0 InnoDB)** rather than flat files or Java serialization:
- **Search Latency:** Sub-millisecond indexed lookup ($O(\log n)$) vs. 48+ seconds linear file scan.
- **Concurrency:** Multi-Version Concurrency Control (MVCC) and row-level locking allow hundreds of ticket counters to issue passes simultaneously without whole-file lock contention.
- **Financial Safety:** ACID transaction boundaries with automated rollback eliminate partial state corruption and lost fee payments.
- **Data Integrity:** Engine-level check constraints and primary keys enforce validation at the storage tier.

---

## 8. GitHub Repository
- **URL:** [https://github.com/geetikaT67/CSA0914-192511469-Assignment5](https://github.com/geetikaT67/CSA0914-192511469-Assignment5)
- **Student:** Geetika T (192511469)
- **Institution:** SIMATS School of Engineering
