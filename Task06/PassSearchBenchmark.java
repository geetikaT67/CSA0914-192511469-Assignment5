package Task06;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * PassSearchBenchmark - Measures search time as file grows: 1,000, 10,000, and 100,000 records.
 * Compares Linear File Scan O(q * n) vs In-Memory HashMap O(n + q).
 * Student Name: Geetika T
 * Register Number: 192511469
 */
public class PassSearchBenchmark {

    // Linear File Scan
    public static BusPass linearFind(File file, String targetPassId) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] tokens = line.split("\\|", -1);
                if (tokens.length != 7) continue;

                if (tokens[0].trim().equalsIgnoreCase(targetPassId)) {
                    double amount = Double.parseDouble(tokens[6].trim());
                    return new BusPass(tokens[0].trim(), tokens[1].trim(), tokens[2].trim(),
                            tokens[3].trim(), tokens[4].trim(), tokens[5].trim(), amount);
                }
            }
        }
        return null;
    }

    // In-Memory HashMap Loader
    public static Map<String, BusPass> loadHashMap(File file) throws IOException {
        Map<String, BusPass> map = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] tokens = line.split("\\|", -1);
                if (tokens.length != 7) continue;

                try {
                    double amount = Double.parseDouble(tokens[6].trim());
                    BusPass pass = new BusPass(tokens[0].trim(), tokens[1].trim(), tokens[2].trim(),
                            tokens[3].trim(), tokens[4].trim(), tokens[5].trim(), amount);
                    map.put(pass.getPassId(), pass);
                } catch (NumberFormatException ignored) {}
            }
        }
        return map;
    }

    public static void main(String[] args) {
        System.out.println("=== Task 06: Pass Search Scalability Benchmark ===");
        System.out.println("Student: Geetika T | Reg No: 192511469\n");

        int[] recordCounts = {1000, 10000, 100000};
        String[] filePaths = {
                "TestData/bus_passes_1k.txt",
                "TestData/bus_passes_10k.txt",
                "TestData/bus_passes_100k.txt"
        };

        int q = 100; // Number of search queries
        Random rand = new Random(42); // fixed seed for reproducible queries

        File csvOutput = new File("ExperimentalData/search_benchmark.csv");
        if (csvOutput.getParentFile() != null) csvOutput.getParentFile().mkdirs();

        try (java.io.PrintWriter csvWriter = new java.io.PrintWriter(new java.io.FileWriter(csvOutput))) {
            csvWriter.println("Dataset_Records,NumQueries_q,LinearScan_Total_ms,LinearScan_Avg_ms,HashMap_Startup_ms,HashMap_Search_ms,HashMap_Avg_ms,SpeedupFactor,Estimated_Heap_KB");

            System.out.printf("%-10s | %-6s | %-16s | %-16s | %-16s | %-10s\n",
                    "Records (n)", "Queries", "Linear Scan (ms)", "HashMap Startup", "HashMap Queries", "Speedup");
            System.out.println("-----------------------------------------------------------------------------------------");

            for (int i = 0; i < recordCounts.length; i++) {
                int n = recordCounts[i];
                File file = new File(filePaths[i]);
                if (!file.exists()) {
                    System.err.println("File not found: " + filePaths[i]);
                    continue;
                }

                // Generate q target IDs
                List<String> targetIds = new ArrayList<>();
                for (int k = 0; k < q; k++) {
                    int idNum = 1000 + rand.nextInt(n) + 1;
                    targetIds.add("P" + idNum);
                }

                // 1. Measure Linear Scan for q queries
                long t0 = System.nanoTime();
                for (String targetId : targetIds) {
                    linearFind(file, targetId);
                }
                long t1 = System.nanoTime();
                double linearTotalMs = (t1 - t0) / 1_000_000.0;
                double linearAvgMs = linearTotalMs / q;

                // 2. Measure HashMap Startup and Querying
                long t2 = System.nanoTime();
                Map<String, BusPass> cache = loadHashMap(file);
                long t3 = System.nanoTime();
                double mapStartupMs = (t3 - t2) / 1_000_000.0;

                long t4 = System.nanoTime();
                for (String targetId : targetIds) {
                    BusPass p = cache.get(targetId);
                }
                long t5 = System.nanoTime();
                double mapQueryMs = (t5 - t4) / 1_000_000.0;
                double mapAvgMs = mapQueryMs / q;

                double speedup = linearTotalMs / mapQueryMs;
                // Estimated memory: ~120 bytes per BusPass object + map node overhead ~ 32 bytes = ~152 bytes/record
                long estimatedHeapKB = (n * 152) / 1024;

                System.out.printf("%-10d | %-6d | %14.3f ms | %14.3f ms | %14.4f ms | %8.1fx\n",
                        n, q, linearTotalMs, mapStartupMs, mapQueryMs, speedup);

                csvWriter.printf("%d,%d,%.3f,%.4f,%.3f,%.4f,%.5f,%.2f,%d\n",
                        n, q, linearTotalMs, linearAvgMs, mapStartupMs, mapQueryMs, mapAvgMs, speedup, estimatedHeapKB);
            }

            System.out.println("-----------------------------------------------------------------------------------------");
            System.out.println("Search benchmark data saved to: " + csvOutput.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
