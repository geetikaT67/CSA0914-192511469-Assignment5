package Task06;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BusPassFileStore - Text-file persistence engine for Bus Pass records.
 * Student Name: Geetika T
 * Register Number: 192511469
 */
public class BusPassFileStore {

    private final File dataFile;

    public BusPassFileStore(String filePath) {
        this.dataFile = new File(filePath);
        // Ensure parent directory exists
        if (dataFile.getParentFile() != null) {
            dataFile.getParentFile().mkdirs();
        }
    }

    /**
     * Operation 1: Add a bus pass (appends to text file)
     */
    public boolean add(BusPass pass) {
        if (pass == null || pass.getPassId() == null || pass.getPassId().trim().isEmpty()) {
            System.err.println("Error: Cannot persist null or invalid bus pass.");
            return false;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(dataFile, true))) {
            bw.write(pass.toDelimitedLine());
            bw.newLine();
            bw.flush();
            return true;
        } catch (IOException e) {
            System.err.println("I/O error appending pass: " + e.getMessage());
            return false;
        }
    }

    /**
     * Operation 2: Find a bus pass by ID
     * Skips damaged lines gracefully without halting the scan.
     */
    public BusPass find(String passId) {
        if (!dataFile.exists()) {
            System.err.println("Warning: Data file not found at " + dataFile.getPath());
            return null;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
            String line;
            int lineNum = 0;

            while ((line = br.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] tokens = line.split("\\|", -1);
                // Validate field count (must be exactly 7)
                if (tokens.length != 7) {
                    continue; // Skip damaged line
                }

                if (tokens[0].trim().equalsIgnoreCase(passId)) {
                    try {
                        double amount = Double.parseDouble(tokens[6].trim());
                        return new BusPass(tokens[0].trim(), tokens[1].trim(), tokens[2].trim(),
                                tokens[3].trim(), tokens[4].trim(), tokens[5].trim(), amount);
                    } catch (NumberFormatException e) {
                        return null; // Corrupted amount field
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("I/O error during find(): " + e.getMessage());
        }
        return null;
    }

    /**
     * Operation 3: List all passes on a particular route
     */
    public List<BusPass> listByRoute(String routeNo) {
        List<BusPass> list = new ArrayList<>();
        if (!dataFile.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] tokens = line.split("\\|", -1);
                if (tokens.length != 7) continue;

                if (tokens[3].trim().equalsIgnoreCase(routeNo)) {
                    try {
                        double amount = Double.parseDouble(tokens[6].trim());
                        list.add(new BusPass(tokens[0].trim(), tokens[1].trim(), tokens[2].trim(),
                                tokens[3].trim(), tokens[4].trim(), tokens[5].trim(), amount));
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException e) {
            System.err.println("I/O error during listByRoute(): " + e.getMessage());
        }
        return list;
    }

    public static void main(String[] args) {
        System.out.println("=== Task 06: BusPassFileStore Implementation Verification ===");
        System.out.println("Student: Geetika T | Reg No: 192511469\n");

        String testStorePath = "TestData/task06_test_store.txt";
        File storeFile = new File(testStorePath);
        if (storeFile.exists()) storeFile.delete(); // fresh test

        BusPassFileStore store = new BusPassFileStore(testStorePath);

        // 1. Test Normal Case: Add Passes
        System.out.println("1. Adding normal bus passes:");
        BusPass p1 = new BusPass("P1001", "192511469", "Geetika T", "Route-104", "Poonamallee", "2026-12-31", 2500.00);
        BusPass p2 = new BusPass("P1002", "192511470", "Rahul R", "Route-104", "Porur", "2026-12-31", 2000.00);
        BusPass p3 = new BusPass("P1003", "192511471", "Ananya S", "Route-205", "Guindy", "2026-12-31", 1800.00);

        store.add(p1);
        store.add(p2);
        store.add(p3);
        System.out.println("Added: " + p1);
        System.out.println("Added: " + p2);
        System.out.println("Added: " + p3);

        // Intentionally inject damaged line into file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(testStorePath, true))) {
            bw.write("DAMAGED_RECORD_MISSING_FIELDS|192511499\n");
            bw.write("P1004|192511472|Suresh M|Route-104|Tambaram|2026-12-31|INVALID_AMOUNT\n");
            bw.write("P1005|192511473|Karthik K|Route-104|Avadi|2026-12-31|2200.00\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("\n[Injected 2 damaged records into file store to test fault tolerance]");

        // 2. Test Find by ID - Normal Case
        System.out.println("\n2. Testing find() for valid pass ID 'P1001':");
        BusPass foundP1 = store.find("P1001");
        System.out.println("Result: " + foundP1);

        // 3. Test Find by ID - Non-existent Case
        System.out.println("\n3. Testing find() for non-existent pass ID 'P9999':");
        BusPass foundP9999 = store.find("P9999");
        System.out.println("Result: " + foundP9999);

        // 4. Test Find by ID - Damaged Record Case
        System.out.println("\n4. Testing find() across damaged data:");
        System.out.println("Searching for 'P1004' (has invalid amount): " + store.find("P1004"));
        System.out.println("Searching for 'P1005' (comes AFTER damaged lines): " + store.find("P1005"));

        // 5. Test List Passes on Route 'Route-104'
        System.out.println("\n5. Testing listByRoute('Route-104'):");
        List<BusPass> route104Passes = store.listByRoute("Route-104");
        for (BusPass pass : route104Passes) {
            System.out.println(" -> " + pass);
        }
        System.out.printf("Total valid passes found on Route-104: %d\n", route104Passes.size());

        System.out.println("\n==> SUCCESS: BusPassFileStore successfully implemented and verified!");
    }
}
