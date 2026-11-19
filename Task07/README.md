# Task 07: Adversarial Input Testing

## Student Details
- **Student Name:** Geetika T
- **Register Number:** 192511469
- **Subject:** Programming in Java (CSA09)
- **Folder:** Task07

---

## Directory Contents
1. `SQLInjectionTester.java` - Executes adversarial SQL injection payloads against dynamic string concatenation vs parameterized `PreparedStatement`.
2. `AdversarialDataTester.java` - Stress tests file parsing against corrupted datasets: wrong field count, non-numeric amounts, and empty lines.
3. `Task07_Report.md` - Complete experimental report documenting attack vectors, security observations, and robustness audit summaries.

## How to Compile and Run:

### Compile All Classes:
```powershell
javac Task07/*.java
```

### Run SQL Injection Tests:
```powershell
java -cp . Task07.SQLInjectionTester
```

### Run Damaged Data Adversarial Tests:
```powershell
java -cp . Task07.AdversarialDataTester
```
