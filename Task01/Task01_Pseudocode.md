# Task 01 – SIMATS Engineering Standard Algorithmic Pseudocode

## Student Details
- **Student Name:** Geetika T
- **Register Number:** 192511469
- **Subject:** Programming in Java (CSA09)
- **Standard:** SIMATS Engineering – Standard for Algorithmic Pseudocode

---

## Algorithm 1: Add a Bus Pass to Delimited File

```text
ALGORITHM AddBusPass(filePath, pass)
  INPUT: 
    filePath : STRING (Path to the bus pass data file)
    pass     : BusPass OBJECT (passId, regNo, name, routeNo, boardingStop, validityDate, amountPaid)
  OUTPUT: 
    BOOLEAN (TRUE if appended successfully, FALSE otherwise)

BEGIN
  TRY
    // Validate pass object integrity
    IF pass IS NULL OR pass.passId IS EMPTY THEN
      RAISE ERROR "Invalid bus pass record provided"
    END IF

    // Construct delimited text record string
    recordLine ← pass.passId + "|" + 
                 pass.regNo + "|" + 
                 pass.name + "|" + 
                 pass.routeNo + "|" + 
                 pass.boardingStop + "|" + 
                 pass.validityDate + "|" + 
                 FORMAT(pass.amountPaid, "0.00")

    // Open file in append mode (true flag prevents overwriting)
    fileWriter ← OPEN_FILE(filePath, APPEND_MODE = TRUE)
    bufferedWriter ← CREATE_BUFFERED_WRITER(fileWriter)

    // Write record followed by explicit newline
    bufferedWriter.WRITE_LINE(recordLine)
    bufferedWriter.FLUSH()
    bufferedWriter.CLOSE()

    RETURN TRUE

  CATCH IOException AS ex
    PRINT "Persistence error while appending pass: " + ex.MESSAGE
    RETURN FALSE
  END TRY
END
```

---

## Algorithm 2: Find a Bus Pass by ID

```text
ALGORITHM FindBusPassById(filePath, targetPassId)
  INPUT: 
    filePath     : STRING (Path to the bus pass data file)
    targetPassId : STRING (The bus pass identifier to locate, e.g. "P1003")
  OUTPUT: 
    BusPass OBJECT if found, NULL otherwise

BEGIN
  // Check if file exists prior to reading
  IF NOT FILE_EXISTS(filePath) THEN
    PRINT "Warning: Persistence file does not exist at " + filePath
    RETURN NULL
  END IF

  TRY
    fileReader ← OPEN_FILE(filePath, READ_MODE)
    bufferedReader ← CREATE_BUFFERED_READER(fileReader)

    line ← NULL
    lineNumber ← 0

    // Read file sequentially line by line
    WHILE (line ← bufferedReader.READ_LINE()) IS NOT NULL DO
      lineNumber ← lineNumber + 1
      trimmedLine ← TRIM(line)

      // Skip blank or empty lines
      IF trimmedLine IS EMPTY THEN
        CONTINUE
      END IF

      // Tokenize line using pipe delimiter without dropping trailing blanks
      tokens ← SPLIT(trimmedLine, "|")

      // Validate token count (must contain exactly 7 fields)
      IF LENGTH(tokens) ≠ 7 THEN
        PRINT "Skipping corrupted record at line " + lineNumber + ": Invalid field count (" + LENGTH(tokens) + ")"
        CONTINUE
      END IF

      currentPassId ← TRIM(tokens[0])

      // Match target identifier (case-insensitive)
      IF EQUALS_IGNORE_CASE(currentPassId, targetPassId) THEN
        // Parse attributes safely
        regNo ← TRIM(tokens[1])
        name ← TRIM(tokens[2])
        routeNo ← TRIM(tokens[3])
        boardingStop ← TRIM(tokens[4])
        validityDate ← TRIM(tokens[5])
        amountPaid ← PARSE_DOUBLE(TRIM(tokens[6]))

        // Reconstruct BusPass object
        foundPass ← NEW BusPass(currentPassId, regNo, name, routeNo, boardingStop, validityDate, amountPaid)
        bufferedReader.CLOSE()
        RETURN foundPass
      END IF
    END WHILE

    bufferedReader.CLOSE()
    // Pass ID was not found after full sequential scan
    RETURN NULL

  CATCH IOException AS ex
    PRINT "I/O error during bus pass search: " + ex.MESSAGE
    RETURN NULL
  CATCH NumberFormatException AS numEx
    PRINT "Data corruption error in numeric amount: " + numEx.MESSAGE
    RETURN NULL
  END TRY
END
```

---

## Algorithm 3: List Passes on a Particular Route

```text
ALGORITHM ListPassesByRoute(filePath, targetRoute)
  INPUT: 
    filePath    : STRING (Path to the bus pass data file)
    targetRoute : STRING (The designated route code to filter, e.g. "Route-104")
  OUTPUT: 
    LIST of BusPass OBJECTS matching the route criteria

BEGIN
  matchingPasses ← INITIALIZE_EMPTY_LIST()

  IF NOT FILE_EXISTS(filePath) THEN
    PRINT "Warning: File not found at " + filePath
    RETURN matchingPasses
  END IF

  TRY
    fileReader ← OPEN_FILE(filePath, READ_MODE)
    bufferedReader ← CREATE_BUFFERED_READER(fileReader)

    line ← NULL
    lineNumber ← 0

    WHILE (line ← bufferedReader.READ_LINE()) IS NOT NULL DO
      lineNumber ← lineNumber + 1
      trimmedLine ← TRIM(line)

      IF trimmedLine IS EMPTY THEN
        CONTINUE
      END IF

      tokens ← SPLIT(trimmedLine, "|")

      // Check field count integrity
      IF LENGTH(tokens) ≠ 7 THEN
        PRINT "Skipping damaged record at line " + lineNumber
        CONTINUE
      END IF

      currentRoute ← TRIM(tokens[3])

      // Filter by route number
      IF EQUALS_IGNORE_CASE(currentRoute, targetRoute) THEN
        TRY
          passId ← TRIM(tokens[0])
          regNo ← TRIM(tokens[1])
          name ← TRIM(tokens[2])
          boardingStop ← TRIM(tokens[4])
          validityDate ← TRIM(tokens[5])
          amountPaid ← PARSE_DOUBLE(TRIM(tokens[6]))

          passObj ← NEW BusPass(passId, regNo, name, currentRoute, boardingStop, validityDate, amountPaid)
          matchingPasses.ADD(passObj)
        CATCH NumberFormatException AS ex
          PRINT "Malformed amount field at line " + lineNumber + ", skipping."
        END TRY
      END IF
    END WHILE

    bufferedReader.CLOSE()
    RETURN matchingPasses

  CATCH IOException AS ex
    PRINT "Error reading route data: " + ex.MESSAGE
    RETURN matchingPasses
  END TRY
END
```
