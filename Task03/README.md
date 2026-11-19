# Task 03: Data-Access Design Pattern

## Student Details
- **Student Name:** Geetika T
- **Register Number:** 192511469
- **Subject:** Programming in Java (CSA09)
- **Folder:** Task03

---

## Directory Contents
1. `Task03_Design.md` - Analysis of common vs differing JDBC operations, generic `Dao<T, K>` specification, Student object-to-table mapping, primary key identification, UML class diagrams, and architectural comparison against direct SQL in GUI/Service.
2. `Task03_Pseudocode.md` - SIMATS Engineering standard algorithmic pseudocode for `insert`, `findById`, and `findAll`.
3. `Dao.java` - Generic DAO interface definition.
4. `Student.java` - Domain model entity.
5. `StudentDao.java` - Concrete JDBC implementation using `PreparedStatement`.
6. `StudentDaoDemo.java` - Java verification test executing CRUD operations through the generic DAO interface.

## How to Compile and Run:
```powershell
javac Task03/*.java
java -cp . Task03.StudentDaoDemo
```
