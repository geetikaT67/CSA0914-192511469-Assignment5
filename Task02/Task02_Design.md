# Task 02 – A2: Buffered I/O Analysis and Design

## Student Details
- **Student Name:** Geetika T
- **Register Number:** 192511469
- **Subject:** Programming in Java (CSA09)
- **Assignment:** Assignment 5 – CO5: Data Persistence Engineering

---

## 1. Scenario and Mathematical Prediction
An attendance log file of size **50 MB** ($50 \times 1024 \times 1024 = 52,428,800\text{ bytes}$) is scanned sequentially byte-by-byte to count line breaks (`\n`, ASCII 10).

### 1.1 Predicted Read Calls
- When using an unbuffered `FileInputStream.read()`:
  $$\text{Total Read Calls} = 52,428,800\text{ calls}$$
  Plus $1$ final EOF read call returning `-1`.
  $$\text{Total Invocations} = 52,428,801\text{ method calls}$$

### 1.2 Comparison with Buffered Reading (8 KB buffer)
- Default buffer size in `BufferedInputStream` is $8,192\text{ bytes}$ ($8\text{ KB}$):
  $$\text{Operating System I/O Calls} = \left\lceil \frac{52,428,800}{8,192} \right\rceil = 6,400\text{ OS system calls}$$
- **Reduction in OS calls:**
  $$\text{Call Reduction Factor} = \frac{52,428,800}{6,400} = 8,192\times \text{ fewer system interactions!}$$

---

## 2. Why Reading One Byte at a Time is Inefficient

1. **Context Switching Overhead:**
   Every unbuffered `read()` invokes a JNI (Java Native Interface) call crossing the boundary between user space and kernel space. A kernel context switch requires:
   - Saving CPU registers and program counter.
   - Switching CPU privilege levels (Ring 3 to Ring 0).
   - Executing kernel interrupt/syscall dispatchers.
   - Restoring registers upon return.
   Repeating this $52.4\text{ million}$ times incurs tens of seconds of pure CPU trap overhead.

2. **Hardware Disk Sector Inefficiency:**
   Storage drives (HDDs, NVMe SSDs) operate on blocks/pages (typically 4 KB or 8 KB). Requesting a single byte causes the OS file subsystem and controller to fetch an entire 4 KB block, discard 4,095 bytes, and repeat for the next byte if uncached.

3. **CPU Pipeline Inefficiency:**
   Tight unbuffered loops prevent modern CPU out-of-order execution and branch prediction from operating optimally due to constant JNI trap interruptions.

---

## 3. How Buffering Reduces Operating System Calls
A `BufferedInputStream` acts as an in-memory intermediate reservoir:
1. When the first `read()` occurs, the wrapper requests a block of $8,192\text{ bytes}$ from the OS in a single syscall (`read(byte[] b, 0, 8192)`).
2. The operating system kernel services this request with one device block read and transfers 8 KB into the internal `byte[] buf` array in JVM heap memory.
3. The subsequent $8,191$ invocations of `read()` are satisfied entirely in RAM by indexing the internal array (`buf[pos++]`), taking fractions of a nanosecond with zero kernel traps.
4. Only when `pos == count` does the buffer trigger the next kernel syscall.

```
+-----------------------------------------------------------------------------------+
|  JVM User Space Memory                                                            |
|  +------------------------+        +------------------------------------------+   |
|  | Application Byte Read  | <====> | BufferedInputStream (Internal 8KB buf[]) |   |
|  +------------------------+        +------------------------------------------+   |
+--------------------------------------------------|--------------------------------+
                                                   | 1 OS System Call per 8192 bytes
+--------------------------------------------------v--------------------------------+
|  Operating System Kernel Space                                                    |
|  [ File System Cache / Page Cache ] <== Block I/O ==> [ NVMe / SSD Storage Block ] |
+-----------------------------------------------------------------------------------+
```

---

## 4. Limitations of Buffering: When Buffering Gives Little or No Benefit

**Specific Scenario: Pure In-Memory Byte Streams (`ByteArrayInputStream`) or Memory-Mapped Files (`MappedByteBuffer`)**
1. **In-Memory Streams:** Wrapping a `ByteArrayInputStream` inside a `BufferedInputStream` provides zero performance gain and actually degrades throughput by adding an unnecessary layer of method dispatch and secondary memory copying. Since the underlying source is already an array in RAM, no OS context switch or disk I/O exists to be amortized.
2. **Single Small File Reads:** If a file is smaller than the buffer size (e.g., a 200-byte config file read once), buffering adds allocation overhead without amortizing multiple system calls.
3. **Random-Access Seeks:** In workloads where an application jumps randomly across gigabyte files reading 4 bytes at non-contiguous offsets, prefetching 8 KB sequential buffers wastes I/O bandwidth fetching data that will never be consumed.

---

## 5. Comparative Analysis: Buffered Stream vs. Files.readAllLines()

| Metric / Dimension | `BufferedInputStream` / Custom Byte Buffer | `Files.readAllLines(Path)` |
|:---|:---|:---|
| **Memory Allocation ($O$)** | $O(1)$ constant auxiliary memory ($8\text{ KB}$ fixed buffer) | $O(N)$ linear memory proportional to file size |
| **Garbage Collector Impact** | Negligible; single byte buffer reused continuously | Severe; instantiates millions of `String` objects, char arrays, and `ArrayList` references |
| **Risk of OutOfMemoryError** | Zero risk on arbitrary multi-gigabyte files | Extreme risk; a 500 MB log can consume $>2\text{ GB}$ heap, triggering JVM crashes |
| **Execution Latency** | High streaming throughput; pipeline starts immediately | High latency; blocks until the entire file is fully parsed into memory |
| **Use Case Appropriateness** | Industrial scale, large log processing, memory-constrained services | Quick scripts for small configuration files (< 2 MB) |

---

## 6. Time and Space Complexity Summary

| Implementation Method | Time Complexity | Auxiliary Space Complexity | Operating System Traps |
|:---|:---:|:---:|:---:|
| **Unbuffered `FileInputStream.read()`** | $\Theta(N)$ | $O(1)$ | $N$ syscalls ($52,428,800$ for 50 MB) |
| **`BufferedInputStream` (8 KB)** | $\Theta(N)$ | $O(1)$ (fixed 8 KB) | $\lceil N / 8192 \rceil$ syscalls ($6,400$ for 50 MB) |
| **Block-Buffer `read(byte[])`** | $\Theta(N)$ | $O(1)$ (custom buffer) | $\lceil N / \text{bufferSize} \rceil$ syscalls |
| **`Files.readAllLines()`** | $\Theta(N)$ | $\Theta(N)$ heap space | Low syscalls, but catastrophic heap footprint |
