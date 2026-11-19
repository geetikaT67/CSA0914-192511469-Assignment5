package Task07;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * AdversarialDataTester - Stress tests file-reading program against corrupted datasets:
 * 1. Wrong field count
 * 2. Non-numeric amount
 * 3. Empty lines
 * Ensures that damaged data does NOT crash the application and valid records remain accessible.
 *
 * Student Name: Geetika T
 * Register Number: 192511469
 */
public class AdversarialDataTester {

    public static class BusPassRecord {
        public String passId;
        public String regNo;
        public String name;
        public String routeNo;
        public String boardingStop;
        public String validityDate;
        public double amountPaid;

        public BusPassRecord(String passId, String regNo, String name, String routeNo,
                              String boardingStop, String validityDate, double amountPaid) {
            this.passId = passId;
            this.regNo = regNo;
            this.name = name;
            this.routeNo = routeNo;
            this.boardingStop = boardingStop;
            this.validityDate = validityDate;
            this.amountPaid = amountPaid;
        }

        @Override
        public String toString() {
            return String.format("BusPass[ID=%s, Name=%s, Route=%s, Amount=₹%.2f]", passId, name, routeNo, amountPaid);
        }
    }

    public static class RobustPassReader {
        private int totalLinesScanned = 0;
        private int emptyLinesSkipped = 0;
        private int invalidFieldCounts = 0;
        private int malformedAmounts = 0;
        private int validRecordsParsed = 0;

        public List<BusPassRecord> readStore(File file) {
            List<BusPassRecord> records = new ArrayList<>();
            if (!file.exists()) {
                System.err.println("File not found: " + file.getPath());
                return records;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                int lineNo = 0;

                while ((line = reader.readLine()) != null) {
                    lineNo++;
                    totalLinesScanned++;

                    // Check 1: Empty or whitespace lines
                    if (line.trim().isEmpty()) {
                        emptyLinesSkipped++;
                        System.out.printf("  [Line %2d] WARN: Empty/whitespace line skipped.\n", lineNo);
                        continue;
                    }

                    // Check 2: Delimited Token Count
                    String[] tokens = line.split("\\|", -1);
                    if (tokens.length != 7) {
                        invalidFieldCounts++;
                        System.out.printf("  [Line %2d] ERROR: Wrong field count (Found %d, Expected 7). Content: \"%s\"\n",
                                lineNo, tokens.length, line);
                        continue;
                    }

                    // Check 3: Numeric Amount Parsing
                    try {
                        String passId = tokens[0].trim();
                        String regNo = tokens[1].trim();
                        String name = tokens[2].trim();
                        String route = tokens[3].trim();
                        String stop = tokens[4].trim();
                        String validity = tokens[5].trim();
                        double amount = Double.parseDouble(tokens[6].trim());

                        if (amount < 0) {
                            malformedAmounts++;
                            System.out.printf("  [Line %2d] ERROR: Negative amount rejected (₹%.2f). Pass ID: %s\n",
                                    lineNo, amount, passId);
                            continue;
                        }

                        records.add(new BusPassRecord(passId, regNo, name, route, stop, validity, amount));
                        validRecordsParsed++;
                        System.out.printf("  [Line %2d] SUCCESS: Parsed valid record: %s\n", lineNo, passId);

                    } catch (NumberFormatException nfe) {
                        malformedAmounts++;
                        System.out.printf("  [Line %2d] ERROR: Non-numeric amount value \"%s\". Pass ID: %s\n",
                                lineNo, tokens[6].trim(), tokens[0].trim());
                    }
                }
            } catch (IOException e) {
                System.err.println("I/O failure: " + e.getMessage());
            }

            return records;
        }

        public void printSummary(String datasetName) {
            System.out.println("\n--- Robustness Audit Summary: " + datasetName + " ---");
            System.out.println("  Total Lines Scanned:      " + totalLinesScanned);
            System.out.println("  Empty Lines Filtered:     " + emptyLinesSkipped);
            System.out.println("  Invalid Field Counts:     " + invalidFieldCounts);
            System.out.println("  Malformed Amounts Caught: " + malformedAmounts);
            System.out.println("  Valid Records Preserved:  " + validRecordsParsed);
            System.out.println("  Crash Occurred:           NO (100% Robust Exception Handling)");
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Task 07 – Part B: Damaged Bus-Pass Data Adversarial Testing ===");
        System.out.println("Student: Geetika T | Reg No: 192511469\n");

        String[] testFiles = {
                "TestData/adversarial/damaged_field_count.txt",
                "TestData/adversarial/damaged_non_numeric.txt",
                "TestData/adversarial/damaged_empty_lines.txt"
        };

        for (String filePath : testFiles) {
            File f = new File(filePath);
            System.out.println("==================================================================");
            System.out.println("TESTING ADVERSARIAL INPUT FILE: " + f.getName());
            System.out.println("==================================================================");

            RobustPassReader reader = new RobustPassReader();
            List<BusPassRecord> validRecords = reader.readStore(f);
            reader.printSummary(f.getName());
            System.out.println("Valid Records Extracted: " + validRecords.size());
            System.out.println();
        }

        System.out.println("==> ALL ADVERSARIAL TESTS COMPLETED: Program survived all corruptions without crashing!");
    }
}
