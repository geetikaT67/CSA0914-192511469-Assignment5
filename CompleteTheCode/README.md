# Complete the Code Programs

## Student Details
- **Student Name:** Geetika T
- **Register Number:** 192511469
- **Subject:** Programming in Java (CSA09)
- **Folder:** CompleteTheCode

---

## 1. Overview
This folder contains the two completed programs supplied in the course specification:
1. `PassStore.java` - File store lookup with damaged-line tolerance (`find()`).
2. `PassLoader.java` - JDBC batch loading with transaction rollback boundaries.

---

## 2. Program 1: `PassStore.java`

### Completed TODOs:
- **`TODO 1` in `find(String passId)`:**
  1. Opens the bus pass file using `BufferedReader`.
  2. Iterates line by line.
  3. Skips empty lines and damaged records (records where token count $\neq 7$ or numeric amount fails parsing).
  4. Matches the `passId` case-insensitively.
  5. Instantiates and returns the matching `BusPass` object.
  6. Returns `null` if the record does not exist or was damaged.

### Execution Verification:
- Search for `P1003`: Successfully returns the `P1003` record.
- Search for `P1002`: Correctly returns `null` because line 2 is damaged (missing fields).
- Search for `P9999`: Correctly returns `null` (not found).
- **Result:** Matches the PDF expected output specification exactly.

---

## 3. Program 2: `PassLoader.java`

### Completed TODOs:
- **`TODO 1`:** Prepared SQL statement with parameterized placeholders (`?`) for columns `(pass_id, reg_no, name, route_no, boarding_stop, validity_date, amount_paid)`.
- **`TODO 2`:** Batch processing logic. Calls `pstmt.addBatch()`, tracks row count, and executes batches of exactly 1,000 rows (`if (count % 1000 == 0) pstmt.executeBatch();`), plus executing any final remaining partial batch.
- **`TODO 3`:** Transaction boundary management:
  - `conn.setAutoCommit(false)` before executing batches.
  - `conn.commit()` strictly after all batches complete successfully.
  - `conn.rollback()` in the `SQLException` catch block to revert all inserted rows on failure, ensuring no partial rows remain in the table.

---

## 4. How to Compile and Run:

### Compile Both Programs:
```powershell
javac CompleteTheCode/*.java
```

### Run PassStore:
```powershell
java -cp . CompleteTheCode.PassStore
```

### Run PassLoader:
```powershell
java -cp . CompleteTheCode.PassLoader
```
