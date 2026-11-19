# Task 01 – A1: Design a File Format

## Student Details
- **Student Name:** Geetika T
- **Register Number:** 192511469
- **Subject:** Programming in Java (CSA09)
- **Assignment:** Assignment 5 – CO5: Data Persistence Engineering

---

## 1. Scenario Overview
The university transport office manages approximately 8,000 bus-pass records annually. Every pass contains the following attributes:
1. **Pass ID:** Unique identifier (e.g., `P1001`)
2. **Register Number:** Unique student registration ID (e.g., `192511469`)
3. **Student Name:** Full name of the student (e.g., `Geetika T`)
4. **Route Number:** Designated bus route (e.g., `Route-104`)
5. **Boarding Stop:** Origin boarding point (e.g., `Poonamallee`)
6. **Validity Date:** Expiry date in ISO-8601 format (`YYYY-MM-DD`, e.g., `2026-12-31`)
7. **Amount Paid:** Numerical transaction amount in INR (e.g., `2500.00`)

---

## 2. File Format Specifications

### 2.1 Delimited Text-File Record Layout
In the delimited text format, each record occupies one physical line terminated by a newline character (`\n` or `\r\n`). Fields are separated by the pipe character (`|`), chosen because student names or boarding locations frequently contain commas or hyphens, but never pipe symbols.

#### Field Specifications:
| Field No | Field Name | Data Type | Maximum Length | Sample Value | Description |
|:---:|:---|:---|:---:|:---|:---|
| 1 | `passId` | String | 10 chars | `P1003` | Unique Bus Pass Identifier |
| 2 | `regNo` | String | 15 chars | `192511469` | Student Register Number |
| 3 | `name` | String | 50 chars | `Geetika T` | Student Full Name |
| 4 | `routeNo` | String | 15 chars | `Route-104` | University Route Code |
| 5 | `boardingStop`| String | 40 chars | `Poonamallee` | Assigned Bus Stop |
| 6 | `validityDate`| String/Date | 10 chars | `2026-12-31` | Expiry Date (`YYYY-MM-DD`) |
| 7 | `amountPaid` | Double | 10 chars | `2500.00` | Fees Paid (in INR) |

**Sample Delimited Text Record:**
```text
P1003|192511469|Geetika T|Route-104|Poonamallee|2026-12-31|2500.00
```
- **Record Delimiter:** `\n` (ASCII 0x0A)
- **Field Delimiter:** `|` (ASCII 0x7C)
- **Average Record Size:** ~70 bytes
- **Estimated Annual Size for 8,000 Records:** $8,000 \times 70\text{ bytes} \approx 560\text{ KB}$

---

### 2.2 Binary Record Layout (using DataOutputStream)
In the binary format, fields are written sequentially using Java's `java.io.DataOutputStream`. Numerical primitives are stored in big-endian binary format, while strings are serialized using modified UTF-8 (`writeUTF()`), which prefixes each string with a 2-byte unsigned length indicator.

#### Binary Layout Breakdown:
| Field Name | Storage Method | Internal Type | Byte Size Breakdown | Total Field Bytes |
|:---|:---|:---|:---|:---:|
| `passId` | `writeUTF(String)` | Modified UTF-8 | 2 bytes length + string bytes | 2 + 5 = 7 bytes |
| `regNo` | `writeUTF(String)` | Modified UTF-8 | 2 bytes length + string bytes | 2 + 9 = 11 bytes |
| `name` | `writeUTF(String)` | Modified UTF-8 | 2 bytes length + string bytes | 2 + 9 = 11 bytes |
| `routeNo` | `writeUTF(String)` | Modified UTF-8 | 2 bytes length + string bytes | 2 + 9 = 11 bytes |
| `boardingStop`| `writeUTF(String)` | Modified UTF-8 | 2 bytes length + string bytes | 2 + 11 = 13 bytes |
| `validityDate`| `writeLong(long)` | Unix Epoch ms | 8 bytes (fixed) | 8 bytes |
| `amountPaid` | `writeDouble(double)`| IEEE 754 float | 8 bytes (fixed) | 8 bytes |

- **Total Binary Record Size:** Approximately $69\text{ bytes}$
- **Estimated Annual Size for 8,000 Records:** $8,000 \times 69\text{ bytes} \approx 552\text{ KB}$

---

## 3. Handling Corrupted, Missing, and Incomplete Data

### 3.1 Damaged Line Detection and Handling
- **Symptoms:** Missing delimiters (e.g. fewer than 7 fields), unexpected field count (> 7 fields), unparseable numeric values (`NumberFormatException` on amount), or malformed date strings.
- **Handling Strategy:**
  1. Split line using `line.split("\\|", -1)` to prevent dropping trailing empty tokens.
  2. Validate that `tokens.length == 7`. If not, log a `DataCorruptionWarning` with the file line number and raw text, skip the record, and proceed to the next line without aborting the program.
  3. Wrap field parsing (`Double.parseDouble`, date parsing) in a dedicated `try-catch` block. If parsing fails, quarantine the corrupted record into `corrupted_records.log` and continue scanning.

### 3.2 Missing File Handling
- **Symptoms:** Program attempts to open the store before any passes have been registered or after accidental deletion (`FileNotFoundException`).
- **Handling Strategy:**
  1. Inspect `file.exists()` before read initialization.
  2. If missing during a read/search operation, gracefully return an empty collection or `null` accompanied by an informational message rather than an unhandled stack trace.
  3. If missing during an `addPass` write operation, automatically invoke `file.getParentFile().mkdirs()` and `file.createNewFile()` to bootstrap the persistence store cleanly.

### 3.3 Incomplete Write After Power Failure
- **Symptoms:** System crash, sudden power loss, or OS buffer interruption leaves a half-written record at the end of the file or corrupted trailing bytes.
- **Handling Strategy:**
  1. **Write-Ahead / Atomic Swap (Shadow Paging):** Write updates to a temporary shadow file (`bus_passes.tmp`). Once flushed and synced to disk via `FileOutputStream.getFD().sync()`, perform an atomic filesystem rename (`Files.move(tmp, target, StandardCopyOption.ATOMIC_MOVE)`).
  2. **Append Verification:** When opening an existing file in append mode, scan the final line. If the final line does not terminate with a newline or fails field validation, truncate the file back to the last valid newline offset using `FileChannel.truncate()`.

---

## 4. Comparison: Delimited Text File vs. Fixed-Length Records (RandomAccessFile)

| Feature | Delimited Text File (`BufferedReader`) | Fixed-Length Binary (`RandomAccessFile`) |
|:---|:---|:---|
| **Record Size** | Variable (exact string length) | Fixed (e.g., strictly 128 bytes per record) |
| **Search Mechanism** | Linear sequential scan ($O(n)$) | Direct mathematical seek ($O(1)$) via `seek(i * RECORD_SIZE)` |
| **In-Place Updates** | Impossible without rewriting entire file | Directly possible via offset overwrite |
| **Storage Overhead** | Minimal (no padding bytes required) | High (strings must be padded with spaces/zeros to fixed size) |
| **Human Readability** | High (viewable and editable in any text editor) | Low (binary view requires hex editor or custom reader) |
| **Corruption Containment** | Single damaged line skips to next newline | Misaligned record corrupts all subsequent offset calculations |
| **Suitability** | Archival logs, batch processing, small catalogs | Real-time indexing, random access, frequent record modifications |

---

## 5. Architectural Trade-Off Analysis

1. **Delimited Text Format:**
   - *Pros:* Human-readable, simple debugging, universal cross-platform interoperability, trivial export to Excel/CSV.
   - *Cons:* Slower random lookup ($O(n)$), expensive full-file rewrites for updates or deletions, parsing overhead.
2. **Binary DataOutputStream Format:**
   - *Pros:* Compact representation, faster numeric serialization without string formatting, native Java type safety.
   - *Cons:* Non-human readable, vulnerable to byte misalignment, strictly bound to Java stream protocol.
3. **Fixed-Length RandomAccessFile Format:**
   - *Pros:* Constant time direct retrieval ($O(1)$) when record index is known, fast in-place record updates.
   - *Cons:* Wasted disk space due to maximum-length padding, rigid schema changes require full file migration.
