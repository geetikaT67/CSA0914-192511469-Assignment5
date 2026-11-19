# Task 05 – SIMATS Engineering Standard Algorithmic Pseudocode

## Student Details
- **Student Name:** Geetika T
- **Register Number:** 192511469
- **Subject:** Programming in Java (CSA09)
- **Standard:** SIMATS Engineering – Standard for Algorithmic Pseudocode

---

## Algorithm 1: Atomic Fund Transfer Transaction

```text
ALGORITHM TransferFunds(connection, studentId, transferAmount)
  INPUT: 
    connection     : JDBC Connection OBJECT
    studentId      : STRING (Student Register Number, e.g. "192511469")
    transferAmount : REAL (Amount to transfer, e.g. 500.00)
  OUTPUT: 
    BOOLEAN (TRUE if transfer committed atomically, FALSE if rolled back)

BEGIN
  IF transferAmount ≤ 0 THEN
    PRINT "Error: Transfer amount must be strictly positive"
    RETURN FALSE
  END IF

  previousAutoCommitState ← connection.GET_AUTO_COMMIT()

  TRY
    // Step 1: Disable auto-commit to begin single atomic transaction
    connection.SET_AUTO_COMMIT(FALSE)

    // Step 2: Check hostel wallet balance with row-level lock (FOR UPDATE)
    checkBalSql ← "SELECT balance FROM hostel_wallet WHERE reg_no = ? FOR UPDATE"
    checkStmt ← connection.PREPARE_STATEMENT(checkBalSql)
    checkStmt.SET_STRING(1, studentId)
    rsBal ← checkStmt.EXECUTE_QUERY()

    IF NOT rsBal.NEXT() THEN
      PRINT "Error: Hostel wallet not found for student " + studentId
      connection.ROLLBACK()
      RETURN FALSE
    END IF

    currentBalance ← rsBal.GET_DOUBLE("balance")
    rsBal.CLOSE()
    checkStmt.CLOSE()

    IF currentBalance < transferAmount THEN
      PRINT "Error: Insufficient funds in hostel wallet. Balance: " + currentBalance
      connection.ROLLBACK()
      RETURN FALSE
    END IF

    // Step 3: Debit funds from hostel wallet
    debitSql ← "UPDATE hostel_wallet SET balance = balance - ? WHERE reg_no = ?"
    debitStmt ← connection.PREPARE_STATEMENT(debitSql)
    debitStmt.SET_DOUBLE(1, transferAmount)
    debitStmt.SET_STRING(2, studentId)
    rowsDebited ← debitStmt.EXECUTE_UPDATE()
    debitStmt.CLOSE()

    IF rowsDebited ≠ 1 THEN
      PRINT "Error updating hostel wallet. Triggering rollback."
      connection.ROLLBACK()
      RETURN FALSE
    END IF

    // Step 4: Credit funds to canteen wallet
    creditSql ← "UPDATE canteen_wallet SET balance = balance + ? WHERE reg_no = ?"
    creditStmt ← connection.PREPARE_STATEMENT(creditSql)
    creditStmt.SET_DOUBLE(1, transferAmount)
    creditStmt.SET_STRING(2, studentId)
    rowsCredited ← creditStmt.EXECUTE_UPDATE()
    creditStmt.CLOSE()

    IF rowsCredited ≠ 1 THEN
      PRINT "Error updating canteen wallet. Triggering rollback."
      connection.ROLLBACK()
      RETURN FALSE
    END IF

    // Step 5: Both operations succeeded, commit atomic transaction
    connection.COMMIT()
    PRINT "Transaction SUCCESS: ₹" + transferAmount + " transferred atomically!"
    RETURN TRUE

  CATCH SQLException AS ex
    PRINT "Transaction FAILED due to SQL exception: " + ex.MESSAGE
    TRY
      // Revert any partial changes back to initial state
      connection.ROLLBACK()
      PRINT "Rollback executed successfully. No money lost or created."
    CATCH SQLException AS rbEx
      PRINT "Fatal Rollback Error: " + rbEx.MESSAGE
    END TRY
    RETURN FALSE

  FINALLY
    TRY
      // Restore original connection auto-commit state
      connection.SET_AUTO_COMMIT(previousAutoCommitState)
    CATCH SQLException AS finalEx
      PRINT "Error restoring auto-commit: " + finalEx.MESSAGE
    END TRY
  END TRY
END
```

---

## Algorithm 2: Secure Parameterized Authentication

```text
ALGORITHM AuthenticateUserSecure(connection, inputUsername, inputPassword)
  INPUT: 
    connection    : JDBC Connection OBJECT
    inputUsername : STRING (Provided username from client)
    inputPassword : STRING (Provided password from client)
  OUTPUT: 
    BOOLEAN (TRUE if credentials match authentic database record, FALSE otherwise)

BEGIN
  // Compile query skeleton with parameterized question marks
  secureSql ← "SELECT user_id, username FROM users WHERE username = ? AND password = ?"

  TRY
    pstmt ← connection.PREPARE_STATEMENT(secureSql)

    // Bind parameters as raw literals; SQL engine will NOT parse as syntax
    pstmt.SET_STRING(1, inputUsername)
    pstmt.SET_STRING(2, inputPassword)

    resultSet ← pstmt.EXECUTE_QUERY()

    // Determine if authentic match exists
    isAuthenticated ← resultSet.NEXT()

    resultSet.CLOSE()
    pstmt.CLOSE()

    RETURN isAuthenticated

  CATCH SQLException AS ex
    PRINT "Database error during authentication: " + ex.MESSAGE
    RETURN FALSE
  END TRY
END
```
