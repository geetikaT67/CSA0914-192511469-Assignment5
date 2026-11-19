# Task 02 – SIMATS Engineering Standard Algorithmic Pseudocode

## Student Details
- **Student Name:** Geetika T
- **Register Number:** 192511469
- **Subject:** Programming in Java (CSA09)
- **Standard:** SIMATS Engineering – Standard for Algorithmic Pseudocode

---

## Algorithm 1: Unbuffered Single-Byte Line Counter (Baseline)

```text
ALGORITHM CountLinesUnbuffered(filePath)
  INPUT: 
    filePath : STRING (Path to the target log file)
  OUTPUT: 
    INTEGER (Total number of newline characters counted)

BEGIN
  IF NOT FILE_EXISTS(filePath) THEN
    PRINT "Error: File not found at " + filePath
    RETURN -1
  END IF

  lineCount ← 0
  totalBytesRead ← 0

  TRY
    fileInputStream ← OPEN_FILE_INPUT_STREAM(filePath)
    byteRead ← 0

    // Read single byte repeatedly until End-Of-File (-1)
    WHILE (byteRead ← fileInputStream.READ_BYTE()) ≠ -1 DO
      totalBytesRead ← totalBytesRead + 1

      // ASCII 10 corresponds to newline '\n'
      IF byteRead = 10 THEN
        lineCount ← lineCount + 1
      END IF
    END WHILE

    fileInputStream.CLOSE()
    RETURN lineCount

  CATCH IOException AS ex
    PRINT "I/O error during unbuffered scan: " + ex.MESSAGE
    RETURN -1
  END TRY
END
```

---

## Algorithm 2: Buffered Block-Array Line Counter (Optimized)

```text
ALGORITHM CountLinesBuffered(filePath, bufferSize)
  INPUT: 
    filePath   : STRING (Path to the target log file)
    bufferSize : INTEGER (Buffer capacity in bytes, e.g. 8192 or 65536)
  OUTPUT: 
    INTEGER (Total number of newline characters counted)

BEGIN
  IF NOT FILE_EXISTS(filePath) THEN
    PRINT "Error: File not found at " + filePath
    RETURN -1
  END IF

  IF bufferSize ≤ 0 THEN
    bufferSize ← 8192  // Default to standard 8 KB page buffer
  END IF

  lineCount ← 0
  buffer ← ALLOCATE_BYTE_ARRAY(bufferSize)

  TRY
    fileInputStream ← OPEN_FILE_INPUT_STREAM(filePath)
    bytesRead ← 0

    // Read full block into memory buffer in a single operating system call
    WHILE (bytesRead ← fileInputStream.READ_BLOCK(buffer, 0, bufferSize)) ≠ -1 DO
      // Iterate over RAM buffer without crossing kernel boundary
      FOR index ← 0 TO (bytesRead - 1) DO
        IF buffer[index] = 10 THEN
          lineCount ← lineCount + 1
        END IF
      END FOR
    END WHILE

    fileInputStream.CLOSE()
    RETURN lineCount

  CATCH IOException AS ex
    PRINT "I/O error during buffered scan: " + ex.MESSAGE
    RETURN -1
  END TRY
END
```
