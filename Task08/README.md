# Task 08: JDBC Performance Comparison

## Student Details
- **Student Name:** Geetika T
- **Register Number:** 192511469
- **Subject:** Programming in Java (CSA09)
- **Folder:** Task08

---

## Directory Contents
1. `JDBCPerformanceComparison.java` - Measures execution times for `Statement`, `PreparedStatement`, and `Batch` (1,000 batch size) across 1k, 10k, and 100k records, and runs the transaction failure/rollback experiment.
2. `MetaDataViewer.java` - Demonstrates metadata reporting using `ResultSetMetaData` and `DatabaseMetaData`.
3. `Task08_Report.md` - Complete experimental report with measurement tables, transaction rollback findings, and architectural recommendations for nightly imports vs counter operations.

## How to Compile and Run:

### Compile All Classes:
```powershell
javac Task08/*.java
```

### Run Performance Comparison:
```powershell
java -cp . Task08.JDBCPerformanceComparison
```

### Run Metadata Viewer:
```powershell
java -cp . Task08.MetaDataViewer
```
