# Task 04: Serialization Design

## Student Details
- **Student Name:** Geetika T
- **Register Number:** 192511469
- **Subject:** Programming in Java (CSA09)
- **Folder:** Task04

---

## Directory Contents
1. `Task04_Design.md` - Object relationship diagram, transient-field decision matrix (password, database connection), depth-first graph traversal mechanics, `serialVersionUID` schema evolution, and alternative format comparison (Java vs CSV vs JSON).
2. `Task04_Pseudocode.md` - Algorithmic pseudocode for graph serialization and versioned deserialization following SIMATS Engineering standards.
3. `Hostel.java` - Root serializable domain entity.
4. `Room.java` - Contained serializable entity.
5. `Resident.java` - Leaf serializable entity with `transient` security modifiers.
6. `SerializationDemo.java` - Java verification test executing end-to-end graph serialization and verifying transient field exclusion.

## How to Compile and Run:
```powershell
javac Task04/*.java
java -cp . Task04.SerializationDemo
```
