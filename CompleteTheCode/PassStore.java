package CompleteTheCode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Complete the Code: PassStore
 * Student Name: Geetika T
 * Register Number: 192511469
 * Course: Programming in Java (CSA09) - Assignment 5
 *
 * Expected Output:
 * - P1003 record should be returned.
 * - P1002 should return null when its record is damaged.
 */
public class PassStore {

    public static class BusPass {
        private String passId;
        private String regNo;
        private String name;
        private String routeNo;
        private String boardingStop;
        private String validityDate;
        private double amountPaid;

        public BusPass(String passId, String regNo, String name, String routeNo,
                       String boardingStop, String validityDate, double amountPaid) {
            this.passId = passId;
            this.regNo = regNo;
            this.name = name;
            this.routeNo = routeNo;
            this.boardingStop = boardingStop;
            this.validityDate = validityDate;
            this.amountPaid = amountPaid;
        }

        public String getPassId() { return passId; }
        public String getRegNo() { return regNo; }
        public String getName() { return name; }
        public String getRouteNo() { return routeNo; }
        public String getBoardingStop() { return boardingStop; }
        public String getValidityDate() { return validityDate; }
        public double getAmountPaid() { return amountPaid; }

        @Override
        public String toString() {
            return String.format("BusPass[ID=%s, RegNo=%s, Name=%s, Route=%s, Stop=%s, Validity=%s, Amount=%.2f]",
                    passId, regNo, name, routeNo, boardingStop, validityDate, amountPaid);
        }
    }

    private final String filePath;

    public PassStore(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Find a bus pass record by passId.
     * Skips damaged lines gracefully without stopping the search.
     *
     * @param passId The target pass identifier (e.g. "P1003")
     * @return Matching BusPass object if found and valid; null otherwise.
     */
    public BusPass find(String passId) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("Warning: Pass store file does not exist at " + filePath);
            return null;
        }

        // =========================================================================
        // TODO 1: Complete the find() method
        // 1. Read the file line by line.
        // 2. Parse each record.
        // 3. Skip damaged lines.
        // 4. Find the matching pass ID.
        // 5. Return the matching record.
        // 6. Return null when the record is not found.
        // =========================================================================
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                // Skip blank or empty lines
                if (line.isEmpty()) {
                    continue;
                }

                // Split on pipe delimiter
                String[] tokens = line.split("\\|", -1);

                // Skip damaged line if token count is incorrect (must be exactly 7)
                if (tokens.length != 7) {
                    // System.out.println("Line " + lineNumber + " skipped: invalid field count (" + tokens.length + ")");
                    continue;
                }

                String currentId = tokens[0].trim();

                // Check for target pass ID
                if (currentId.equalsIgnoreCase(passId)) {
                    try {
                        String regNo = tokens[1].trim();
                        String name = tokens[2].trim();
                        String routeNo = tokens[3].trim();
                        String boardingStop = tokens[4].trim();
                        String validityDate = tokens[5].trim();
                        double amountPaid = Double.parseDouble(tokens[6].trim());

                        // Record successfully matched and parsed
                        return new BusPass(currentId, regNo, name, routeNo, boardingStop, validityDate, amountPaid);
                    } catch (NumberFormatException nfe) {
                        // Damaged numeric amount on matching record
                        // System.out.println("Line " + lineNumber + " skipped: invalid numeric amount");
                        return null;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("I/O Exception while reading pass store: " + e.getMessage());
        }

        // Return null when record is not found or was damaged
        return null;
    }

    public static void main(String[] args) {
        System.out.println("=== Complete the Code: PassStore.find() Execution ===");
        System.out.println("Student: Geetika T | Reg No: 192511469\n");

        String dataPath = "TestData/bus_passes_sample.txt";
        PassStore store = new PassStore(dataPath);

        // Test 1: Search for valid record P1003 (Expected: Returns BusPass object)
        System.out.println("Searching for pass 'P1003'...");
        BusPass passP1003 = store.find("P1003");
        System.out.println("Result for P1003: " + passP1003);

        // Test 2: Search for damaged record P1002 (Expected: Returns null)
        System.out.println("\nSearching for pass 'P1002' (Damaged Line)...");
        BusPass passP1002 = store.find("P1002");
        System.out.println("Result for P1002: " + passP1002);

        // Test 3: Search for non-existent record P9999 (Expected: Returns null)
        System.out.println("\nSearching for pass 'P9999' (Non-existent)...");
        BusPass passP9999 = store.find("P9999");
        System.out.println("Result for P9999: " + passP9999);

        System.out.println("\n--- Execution Summary ---");
        if (passP1003 != null && passP1002 == null && passP9999 == null) {
            System.out.println("SUCCESS: Output matches the PDF specification exactly!");
            System.out.println(" - P1003 was found and returned correctly.");
            System.out.println(" - P1002 returned null because its record was damaged.");
            System.out.println(" - P9999 returned null because it does not exist.");
        } else {
            System.out.println("FAILURE: Output differs from expected specification.");
        }
    }
}
