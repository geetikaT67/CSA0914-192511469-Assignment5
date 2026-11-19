# Task 09 – B4: Final Engineering Decision and Architecture Evaluation

## Student Details
- **Student Name:** Geetika T
- **Register Number:** 192511469
- **Subject:** Programming in Java (CSA09)
- **Assignment:** Assignment 5 – CO5: Data Persistence Engineering

---

## 1. Multi-Campus Expansion Scenario
The university transport office is expanding from a single local office managing 8,000 passes to **12 campuses** handling **100 times the current volume** ($800,000\text{ bus-pass records annually}$).
Each campus operates multiple ticketing counters issuing passes simultaneously, performing real-time route searches, verifying fee payments, and updating records under peak morning rush hours.

---

## 2. Multi-Store Comparative Evaluation

| Evaluation Criteria | Delimited Text Files (`.txt` / `.csv`) | Java Native Serialization (`.ser`) | Relational Database Engine (`MySQL / InnoDB`) |
|:---|:---|:---|:---|
| **Search Performance** | **Extremely Slow:** Linear scan $O(n)$; 100 searches on 100k records takes **6,094 ms** (measured in Task 06). | **Unusable for Search:** Requires deserializing the entire gigabyte object graph into heap memory before any search can begin. | **Sub-Millisecond:** B+ Tree indexing $O(\log n)$; indexed lookup takes **$< 1\text{ ms}$** regardless of database scale. |
| **Concurrency & Multi-Counter Support** | **Severe Race Conditions:** File systems lock at the file level. Two counters appending simultaneously will overwrite each other's records or corrupt lines. | **Zero Concurrency:** Cannot support multiple processes writing to a single `.ser` stream concurrently. | **Enterprise Concurrency:** Multi-Version Concurrency Control (MVCC) and fine-grained row-level locking allow hundreds of concurrent counters. |
| **Transaction Integrity (ACID)** | **Non-existent:** No native commit, rollback, or write-ahead logging. Crashes leave half-written corrupt lines. | **Non-existent:** A crash mid-stream produces a truncated, unparseable binary file that destroys all previously serialized data. | **Guaranteed ACID:** WAL (Write-Ahead Logging), doublewrite buffering, atomic `commit()`, and automatic `rollback()` guarantee 100% data preservation. |
| **Data Safety & Constraints** | **Fragile:** Relies entirely on application parsing logic. No database-enforced types, check constraints, or foreign keys. | **Brittle:** Vulnerable to `serialVersionUID` mismatch, class loader corruption, and Java deserialization gadget attacks. | **Strict Engine Validation:** Primary keys, NOT NULL, CHECK constraints, and typed columns prevent corrupted data entry at engine level. |
| **Memory Footprint** | Low for streaming, but high if cached ($O(n)$ heap). | Catastrophic; deserializing 800,000 objects consumes $>1.5\text{ GB}$ of JVM heap space. | Minimized client heap; database server manages dedicated buffer pool (LRU cache) independently of application JVM. |
| **Auditability & Reporting** | Requires custom ad-hoc scripts; slow aggregation. | Impossible to query without custom Java reflection tools. | Native SQL syntax (`COUNT()`, `GROUP BY`, `SUM()`) enables instantaneous administrative audits and reports across campuses. |

---

## 3. Architecture Comparison: One Shared Database vs. Separate Serialized Files Per Campus

```text
===================================================================================
ARCHITECTURE OPTION A: Separate Serialized Files Per Campus (DEPRECATED)
===================================================================================
Campus 1 (Chennai)      Campus 2 (Vellore)      ...      Campus 12 (Bengaluru)
  [campus1.ser]           [campus2.ser]                    [campus12.ser]
       |                       |                                 |
       v                       v                                 v
 Isolated Silo           Isolated Silo                    Isolated Silo

Defects:
- Cross-campus pass transfers impossible without manual file merging.
- No global reporting or central financial audit.
- Incompatible with web portals, student mobile apps, or multi-terminal counters.

===================================================================================
ARCHITECTURE OPTION B: Unified Enterprise Database Architecture (RECOMMENDED)
===================================================================================
 12 Campus Counters / Web Portals / Mobile Apps (Concurrent JDBC Client Pool)
       |            |            |            |            |            |
       +------------+------------+------------+------------+------------+
                                 | (TLS 1.3 / JDBC Connection Pool - HikariCP)
                                 v
            +-------------------------------------------+
            |  High-Availability Relational Database   |
            |     MySQL 8.0 Enterprise Cluster (InnoDB)  |
            |  - Primary Key & Check Constraints        |
            |  - B+ Tree Indexing on Route & Student ID |
            |  - Multi-Campus Partitioning by campus_id |
            |  - Automated WAL Redo/Undo Replication    |
            +-------------------------------------------+
```

---

## 4. Final Engineering Decision and Justification

### The Verdict:
**Deploy a Unified Relational Database (MySQL InnoDB) with connection pooling and range partitioning.**

### Justification Based on Empirical Measurements and Engineering Principles:

1. **Empirical Performance Evidence:**
   - As measured in **Task 06**, linear file search degraded from $263\text{ ms}$ at 1,000 records to **$6,094\text{ ms}$ at 100,000 records**. Extrapolating to 800,000 passes, a simple pass search would take over **48 seconds per student at the counter!**
   - In contrast, relational B+ Tree lookups execute in **$< 1\text{ ms}$**, eliminating student wait times.
   - As measured in **Task 08**, batch processing ingested 100,000 passes in **$22.21\text{ ms}$** ($604\times$ faster than single statements), demonstrating that MySQL easily absorbs the multi-campus registration rush.

2. **Transaction Safety and Financial Integrity:**
   - The transaction failure experiment in **Task 08** proved that when an insertion fails halfway, auto-commit leaves **500 corrupted records** stranded in the database.
   - An ACID transaction with `rollback()` reverted all rows cleanly to **0 rows**. In a multi-campus deployment handling millions in student transport fees, non-transactional file stores pose unacceptable legal and financial liability.

3. **High-Concurrency Counter Operations:**
   - File stores cannot arbitrate concurrent writes from 12 campuses without whole-file locks, creating massive CPU bottlenecks and deadlock risks.
   - InnoDB's row-level locking ensures counter clerks on Route 101 never block clerks issuing passes on Route 205.

4. **Maintenance, Auditing, and Longevity:**
   - Relational schemas support non-destructive schema migrations (e.g. `ALTER TABLE`), automated point-in-time recovery (PITR) via binlogs, and encrypted transport.
   - File-based serialization is deprecated for scalable architectures due to schema fragility and security vulnerabilities.
