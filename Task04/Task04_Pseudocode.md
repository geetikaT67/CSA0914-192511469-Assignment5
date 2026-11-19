# Task 04 – SIMATS Engineering Standard Algorithmic Pseudocode

## Student Details
- **Student Name:** Geetika T
- **Register Number:** 192511469
- **Subject:** Programming in Java (CSA09)
- **Standard:** SIMATS Engineering – Standard for Algorithmic Pseudocode

---

## Algorithm 1: Serialize Object Graph (Hostel -> Room -> Resident)

```text
ALGORITHM SerializeHostelGraph(hostelObject, destinationFilePath)
  INPUT: 
    hostelObject        : Hostel OBJECT (root node containing Room and Resident graphs)
    destinationFilePath : STRING (File path for binary serialization output, e.g. "hostel.ser")
  OUTPUT: 
    BOOLEAN (TRUE if serialization succeeds, FALSE otherwise)

BEGIN
  IF hostelObject IS NULL THEN
    PRINT "Error: Cannot serialize null hostel object"
    RETURN FALSE
  END IF

  TRY
    // Create underlying file stream
    fileOutputStream ← OPEN_FILE_OUTPUT_STREAM(destinationFilePath)
    
    // Wrap with ObjectOutputStream for object graph serialization
    objectOutputStream ← CREATE_OBJECT_OUTPUT_STREAM(fileOutputStream)

    // Traverse reachability graph and write object metadata and state
    // Note: transient fields (password, dbConnection) are automatically skipped
    objectOutputStream.WRITE_OBJECT(hostelObject)

    // Flush remaining buffer and release resources
    objectOutputStream.FLUSH()
    objectOutputStream.CLOSE()
    fileOutputStream.CLOSE()

    PRINT "Object graph successfully serialized to " + destinationFilePath
    RETURN TRUE

  CATCH NotSerializableException AS nse
    PRINT "Fatal Serialization Error: Non-serializable class encountered: " + nse.MESSAGE
    RETURN FALSE
  CATCH IOException AS ioEx
    PRINT "I/O failure during serialization: " + ioEx.MESSAGE
    RETURN FALSE
  END TRY
END
```

---

## Algorithm 2: Deserialize Object Graph with Version Validation

```text
ALGORITHM DeserializeHostelGraph(sourceFilePath)
  INPUT: 
    sourceFilePath : STRING (Path to serialized binary file)
  OUTPUT: 
    Hostel OBJECT if successful, NULL otherwise

BEGIN
  IF NOT FILE_EXISTS(sourceFilePath) THEN
    PRINT "Error: Serialized file does not exist at " + sourceFilePath
    RETURN NULL
  END IF

  TRY
    fileInputStream ← OPEN_FILE_INPUT_STREAM(sourceFilePath)
    objectInputStream ← CREATE_OBJECT_INPUT_STREAM(fileInputStream)

    // Reconstruct object graph from binary stream
    deserializedObject ← objectInputStream.READ_OBJECT()
    objectInputStream.CLOSE()
    fileInputStream.CLOSE()

    // Cast to target root domain type
    restoredHostel ← CAST(deserializedObject TO Hostel)

    // Verify transient field safety:
    // Any transient field (e.g. resident password or dbConnection) will be NULL
    FOR EACH room IN restoredHostel.rooms DO
      FOR EACH resident IN room.residents DO
        ASSERT resident.password IS NULL
        ASSERT resident.dbConnection IS NULL
      END FOR
    END FOR

    RETURN restoredHostel

  CATCH InvalidClassException AS ice
    PRINT "Schema Evolution Mismatch: serialVersionUID mismatch detected: " + ice.MESSAGE
    RETURN NULL
  CATCH ClassNotFoundException AS cnfe
    PRINT "Class structure missing in JVM classpath: " + cnfe.MESSAGE
    RETURN NULL
  CATCH IOException AS ioEx
    PRINT "I/O error during deserialization: " + ioEx.MESSAGE
    RETURN NULL
  END TRY
END
```
