# Task 05: Correctness and Safety

## Student Details
- **Student Name:** Geetika T
- **Register Number:** 192511469
- **Subject:** Programming in Java (CSA09)
- **Folder:** Task05

---

## Directory Contents
1. `Task05_Design.md` - Complete transaction architecture (hostel-to-canteen fund transfer, auto-commit hazards, ACID atomicity) and SQL injection vulnerability breakdown (crafted payloads, PreparedStatement AST pre-compilation, security comparison matrix).
2. `Task05_Pseudocode.md` - SIMATS Engineering standard algorithmic pseudocode for atomic fund transfer (`TransferFunds`) and secure parameterized login (`AuthenticateUserSecure`).
3. `TransactionAndSecurityDemo.java` - Java verification program verifying transaction rollback behavior and demonstrating SQL injection defense.

## How to Compile and Run:
```powershell
javac Task05/*.java
java -cp . Task05.TransactionAndSecurityDemo
```
