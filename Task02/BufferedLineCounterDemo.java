package Task02;

import java.io.*;

/**
 * BufferedLineCounterDemo - Verifies single-byte vs buffered reading concepts.
 * Student Name: Geetika T
 * Register Number: 192511469
 */
public class BufferedLineCounterDemo {

    public static long countLinesUnbuffered(File file) throws IOException {
        long lines = 0;
        try (FileInputStream fis = new FileInputStream(file)) {
            int b;
            while ((b = fis.read()) != -1) {
                if (b == '\n') {
                    lines++;
                }
            }
        }
        return lines;
    }

    public static long countLinesBuffered(File file, int bufferSize) throws IOException {
        long lines = 0;
        byte[] buffer = new byte[bufferSize];
        try (FileInputStream fis = new FileInputStream(file)) {
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                for (int i = 0; i < bytesRead; i++) {
                    if (buffer[i] == '\n') {
                        lines++;
                    }
                }
            }
        }
        return lines;
    }

    public static void main(String[] args) {
        System.out.println("=== Task 02: Buffered vs Unbuffered Line Counting Demonstration ===");
        System.out.println("Student: Geetika T | Reg No: 192511469");

        File testFile = new File("TestData/attendance_1mb.log");
        if (!testFile.exists()) {
            System.out.println("Test file not found at " + testFile.getAbsolutePath());
            return;
        }

        System.out.printf("Test File: %s (Size: %,d bytes)\n\n", testFile.getName(), testFile.length());

        try {
            // 1. Buffered Measurement (8 KB buffer)
            long t0 = System.nanoTime();
            long linesBuf = countLinesBuffered(testFile, 8192);
            long t1 = System.nanoTime();
            double msBuf = (t1 - t0) / 1_000_000.0;
            System.out.printf("[Buffered 8KB]:   Lines = %,d | Time = %.3f ms | Syscalls ~ %,d\n",
                    linesBuf, msBuf, (testFile.length() / 8192) + 1);

            // 2. Unbuffered Measurement (Single Byte read())
            long t2 = System.nanoTime();
            long linesUnbuf = countLinesUnbuffered(testFile);
            long t3 = System.nanoTime();
            double msUnbuf = (t3 - t2) / 1_000_000.0;
            System.out.printf("[Unbuffered 1B]:  Lines = %,d | Time = %.3f ms | Syscalls = %,d\n",
                    linesUnbuf, msUnbuf, testFile.length() + 1);

            double speedup = msUnbuf / msBuf;
            System.out.printf("\n==> Speedup Factor: %.2fx faster with 8KB buffering!\n", speedup);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
