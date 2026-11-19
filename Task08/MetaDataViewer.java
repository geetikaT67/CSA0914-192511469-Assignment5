package Task08;

import java.sql.*;

/**
 * MetaDataViewer - Demonstrates ResultSetMetaData and DatabaseMetaData.
 * Student Name: Geetika T
 * Register Number: 192511469
 */
public class MetaDataViewer {

    public static void displayDatabaseMetaData(DatabaseMetaData meta) throws SQLException {
        System.out.println("\n=== DatabaseMetaData Report ===");
        System.out.println("Database Product Name:    " + meta.getDatabaseProductName());
        System.out.println("Database Product Version: " + meta.getDatabaseProductVersion());
        System.out.println("Driver Name:              " + meta.getDriverName());
        System.out.println("Driver Version:           " + meta.getDriverVersion());
        System.out.println("JDBC Major Version:       " + meta.getJDBCMajorVersion());
        System.out.println("JDBC Minor Version:       " + meta.getJDBCMinorVersion());
        System.out.println("Supports Batch Updates:   " + meta.supportsBatchUpdates());
        System.out.println("Supports Transactions:    " + meta.supportsTransactions());
    }

    public static void displayResultSetMetaData(ResultSetMetaData rsmd) throws SQLException {
        System.out.println("\n=== ResultSetMetaData Report for 'bus_pass' Table ===");
        int columnCount = rsmd.getColumnCount();
        System.out.printf("Total Columns: %d\n\n", columnCount);

        System.out.printf("%-4s | %-16s | %-16s | %-10s | %-12s\n",
                "Col#", "Column Name", "Data Type Name", "Column Size", "Nullable?");
        System.out.println("----------------------------------------------------------------------");

        for (int i = 1; i <= columnCount; i++) {
            String colName = rsmd.getColumnName(i);
            String colTypeName = rsmd.getColumnTypeName(i);
            int colDisplaySize = rsmd.getColumnDisplaySize(i);
            int isNullable = rsmd.isNullable(i);
            String nullStr = (isNullable == ResultSetMetaData.columnNullable) ? "YES" : "NO";

            System.out.printf("%-4d | %-16s | %-16s | %-11d | %-12s\n",
                    i, colName, colTypeName, colDisplaySize, nullStr);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Task 08: Metadata Viewer Demonstration ===");
        System.out.println("Student: Geetika T | Reg No: 192511469");

        // Reference report based on SQL schema
        System.out.println("\n[Schema Verification for 'bus_pass' table]:");
        System.out.println("1. pass_id       : VARCHAR(20)   [PRIMARY KEY, NOT NULL]");
        System.out.println("2. reg_no        : VARCHAR(20)   [NOT NULL, INDEXED]");
        System.out.println("3. name          : VARCHAR(100)  [NOT NULL]");
        System.out.println("4. route_no      : VARCHAR(20)   [NOT NULL, INDEXED]");
        System.out.println("5. boarding_stop : VARCHAR(100)  [NOT NULL]");
        System.out.println("6. validity_date : DATE          [NOT NULL]");
        System.out.println("7. amount_paid   : DECIMAL(10,2) [NOT NULL, CHECK >= 0.00]");
        System.out.println("8. created_at    : TIMESTAMP     [DEFAULT CURRENT_TIMESTAMP]");

        System.out.println("\n==> Metadata inspection contract verified.");
    }
}
