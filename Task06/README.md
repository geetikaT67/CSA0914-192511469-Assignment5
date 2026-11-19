# Task 06: File Store and Alternative Design

## Student Details
- **Student Name:** Geetika T
- **Register Number:** 192511469
- **Subject:** Programming in Java (CSA09)
- **Folder:** Task06

---

## Directory Contents
1. `BusPass.java` - Domain model representing university bus pass entities.
2. `BusPassFileStore.java` - Text-file persistence engine implementing `add()`, `find()`, and `listByRoute()`.
3. `LineCountingBenchmark.java` - Benchmark measuring buffered (8 KB) vs unbuffered line counting on 1 MB, 10 MB, and 50 MB files.
4. `PassSearchBenchmark.java` - Benchmark measuring search time across 1k, 10k, and 100k records, comparing linear scanning $O(q \times n)$ with in-memory HashMap caching $O(n + q)$.
5. `Task06_Report.md` - Complete evaluation report covering test cases, pseudocode conformity, Big-O complexity, and memory trade-offs.

## How to Compile and Run:

### Compile All Classes:
```powershell
javac Task06/*.java
```

### Run BusPassFileStore:
```powershell
java -cp . Task06.BusPassFileStore
```

### Run Line Counting Benchmark:
```powershell
java -cp . Task06.LineCountingBenchmark
```

### Run Pass Search Benchmark:
```powershell
java -cp . Task06.PassSearchBenchmark
```
