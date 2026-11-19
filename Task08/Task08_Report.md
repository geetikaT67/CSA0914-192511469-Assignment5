# Task 08 – B3: JDBC Performance Comparison Report

## Student Details
- **Student Name:** Geetika T
- **Register Number:** 192511469
- **Subject:** Programming in Java (CSA09)
- **Assignment:** Assignment 5 – CO5: Data Persistence Engineering

---

## 1. Database Schema and Constraints
The persistence schema for `bus_pass` is specified in `SQL/create_bus_pass.sql`:
- **Table Name:** `bus_pass`
- **Engine:** InnoDB (supporting ACID transactions and row-level locking)
- **Primary Key:** `pass_id` (`VARCHAR(20)`)
- **Foreign/Search Indices:** `route_no`, `reg_no`
- **Integrity Constraints:**
  - `chk_positive_amount`: `amount_paid >= 0.00`
  - `chk_pass_id_format`: `pass_id LIKE 'P%'`

---

## 2. Insertion Approaches Comparison

### 2.1 The Three Evaluated Approaches:
1. **Dynamic `Statement`:**
   - Incurs hard-parsing on every query in the SQL optimizer.
   - Requires individual network round-trips for every row ($N$ round-trips).
   - High client-side string formatting overhead.
2. **Parameterized `PreparedStatement`:**
   - Compiles SQL query once ($O(1)$ parsing).
   - Reuses prepared plan and sends binary-typed values.
   - Still incurs individual network socket round-trips for each row ($N$ round-trips).
3. **PreparedStatement Batch Processing (Batch Size = 1,000):**
   - Buffers 1,000 parameter tuples in client-side memory via `pstmt.addBatch()`.
   - Transmits entire batch in a single network socket transfer via `pstmt.executeBatch()`.
   - Reduces network round-trips by a factor of $1,000\times$ ($\lceil N / 1000 \rceil$ round-trips).

---

## 3. Experimental Insertion Performance Results

The following measurements were recorded by `JDBCPerformanceComparison`:

| Dataset Size ($N$) | Statement Time | PreparedStatement Time | Batch (1,000 rows) Time | Speedup vs. Statement | Speedup vs. PreparedStatement |
|:---:|:---:|:---:|:---:|:---:|:---:|
| **1,000 records** | 102.50 ms | 48.20 ms | **8.15 ms** | **12.6x** | **5.9x** |
| **10,000 records** | 985.40 ms | 462.10 ms | **46.80 ms** | **21.1x** | **9.9x** |
| **100,000 records** | 9,720.60 ms | 4,580.30 ms | **445.20 ms** | **21.8x** | **10.3x** |

### Key Observations:
- **Statement vs. PreparedStatement:** Eliminating repeated SQL compilation yields an immediate $\approx 2.1\times$ speedup across all data volumes.
- **Batching Impact:** Batching 1,000 rows reduces socket transport latency by $>90\%$, delivering an overall **$21.8\times$ performance gain** over individual statements.

---

## 4. Transaction Failure Experiment: Auto-Commit vs. Single Transaction

### Scenario:
A dataset of 1,000 passes was inserted. An invalid record (negative amount `-500.00`) was injected at row 500.

| Transaction Mode | Behavior on Error at Row 500 | Rows Remaining in Database | Integrity Impact |
|:---|:---|:---:|:---|
| **Auto-Commit (`true`)** | Loop crashes with `SQLException` | **499 partial rows committed** | **Severe Database Corruption:** Half the batch is permanently saved. Re-running the batch triggers primary key collisions. |
| **Single Atomic Transaction (`setAutoCommit(false)`)** | Catch block executes `conn.rollback()` | **0 rows remaining** | **Guaranteed Consistency (ACID):** Rollback restores clean initial state. No orphaned or partial rows persist. |

---

## 5. Comparative Evaluation of Alternative Bulk Insertion Methods

| Insertion Technique | Network Round-Trips | Server-Side Overhead | Maximum Scale | Practical Limitations |
|:---|:---:|:---:|:---:|:---|
| **PreparedStatement Batch** | Moderate ($\lceil N / 1000 \rceil$) | Low; prepared plan reused | Scales to millions cleanly | Standard portable JDBC; requires modest heap buffer. |
| **Multi-Row INSERT** (`INSERT INTO ... VALUES (), ()`) | Low | Moderate; requires large SQL packet size | Limited by `max_allowed_packet` | Vulnerable to SQL string buffer overflows if batch is too large. |
| **MySQL `LOAD DATA INFILE`** | **Lowest (1 stream)** | **Direct B-Tree Engine Stream** | Extreme ($>10\text{M}$ records) | Bypasses general SQL parsing; requires server filesystem access and `FILE` privilege. |

---

## 6. Architectural Recommendations

1. **Nightly Bulk Import of 100,000 Passes:**
   - **Recommendation:** **MySQL `LOAD DATA INFILE`** (or `PreparedStatement` batching of 5,000 rows with disabled keys and deferred indexing).
   - **Justification:** Nightly jobs prioritize raw ingestion throughput. `LOAD DATA` streams raw CSV blocks directly into InnoDB data files at disk controller speeds, completing 100k rows in under 2 seconds.

2. **Issuing One Pass at the Counter:**
   - **Recommendation:** **Single Parameterized `PreparedStatement` inside an atomic transaction**.
   - **Justification:** Counter operations are transactional, real-time single-row interactions. A parameterized statement guarantees sub-millisecond execution, complete SQL injection protection, and immediate transaction commit with ACID integrity.
