# Task 03 – SIMATS Engineering Standard Algorithmic Pseudocode

## Student Details
- **Student Name:** Geetika T
- **Register Number:** 192511469
- **Subject:** Programming in Java (CSA09)
- **Standard:** SIMATS Engineering – Standard for Algorithmic Pseudocode

---

## Algorithm 1: StudentDao.insert(student)

```text
ALGORITHM StudentDao_Insert(connection, student)
  INPUT: 
    connection : JDBC Connection OBJECT (active database connection)
    student    : Student OBJECT (regNo, name, email, department, cgpa)
  OUTPUT: 
    NONE (Throws SQLException on persistence failure)

BEGIN
  // Define parameterized SQL query preventing injection
  sql ← "INSERT INTO student (reg_no, name, email, department, cgpa) VALUES (?, ?, ?, ?, ?)"

  TRY
    // Create PreparedStatement
    preparedStatement ← connection.PREPARE_STATEMENT(sql)

    // Bind object fields to corresponding SQL parameters
    preparedStatement.SET_STRING(1, student.regNo)
    preparedStatement.SET_STRING(2, student.name)
    preparedStatement.SET_STRING(3, student.email)
    preparedStatement.SET_STRING(4, student.department)
    preparedStatement.SET_DOUBLE(5, student.cgpa)

    // Execute insertion
    rowsAffected ← preparedStatement.EXECUTE_UPDATE()

    IF rowsAffected = 0 THEN
      RAISE ERROR "Insertion failed, no rows affected."
    END IF

    preparedStatement.CLOSE()

  CATCH SQLException AS ex
    PRINT "Database persistence failure in StudentDao_Insert: " + ex.MESSAGE
    THROW ex
  END TRY
END
```

---

## Algorithm 2: StudentDao.findById(regNo)

```text
ALGORITHM StudentDao_FindById(connection, targetRegNo)
  INPUT: 
    connection  : JDBC Connection OBJECT
    targetRegNo : STRING (Student registration number to find)
  OUTPUT: 
    Student OBJECT if located, NULL otherwise

BEGIN
  sql ← "SELECT reg_no, name, email, department, cgpa FROM student WHERE reg_no = ?"
  locatedStudent ← NULL

  TRY
    preparedStatement ← connection.PREPARE_STATEMENT(sql)
    preparedStatement.SET_STRING(1, targetRegNo)

    // Execute query and retrieve ResultSet cursor
    resultSet ← preparedStatement.EXECUTE_QUERY()

    // Inspect if a row matches the unique primary key
    IF resultSet.NEXT() THEN
      regNo ← resultSet.GET_STRING("reg_no")
      name ← resultSet.GET_STRING("name")
      email ← resultSet.GET_STRING("email")
      department ← resultSet.GET_STRING("department")
      cgpa ← resultSet.GET_DOUBLE("cgpa")

      locatedStudent ← NEW Student(regNo, name, email, department, cgpa)
    END IF

    resultSet.CLOSE()
    preparedStatement.CLOSE()

    RETURN locatedStudent

  CATCH SQLException AS ex
    PRINT "Query error in StudentDao_FindById: " + ex.MESSAGE
    THROW ex
  END TRY
END
```

---

## Algorithm 3: StudentDao.findAll()

```text
ALGORITHM StudentDao_FindAll(connection)
  INPUT: 
    connection : JDBC Connection OBJECT
  OUTPUT: 
    LIST of Student OBJECTS

BEGIN
  studentList ← INITIALIZE_EMPTY_LIST()
  sql ← "SELECT reg_no, name, email, department, cgpa FROM student ORDER BY reg_no ASC"

  TRY
    preparedStatement ← connection.PREPARE_STATEMENT(sql)
    resultSet ← preparedStatement.EXECUTE_QUERY()

    // Iterate through all records in the cursor
    WHILE resultSet.NEXT() DO
      regNo ← resultSet.GET_STRING("reg_no")
      name ← resultSet.GET_STRING("name")
      email ← resultSet.GET_STRING("email")
      department ← resultSet.GET_STRING("department")
      cgpa ← resultSet.GET_DOUBLE("cgpa")

      studentObj ← NEW Student(regNo, name, email, department, cgpa)
      studentList.ADD(studentObj)
    END WHILE

    resultSet.CLOSE()
    preparedStatement.CLOSE()

    RETURN studentList

  CATCH SQLException AS ex
    PRINT "Error retrieving all students: " + ex.MESSAGE
    THROW ex
  END TRY
END
```
