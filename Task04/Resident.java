package Task04;

import java.io.Serializable;
import java.sql.Connection;

/**
 * Resident Entity
 * Demonstrates transient fields (password, live DB Connection).
 * Student: Geetika T | Reg No: 192511469
 */
public class Resident implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String registerNumber;

    // Transient: Sensitive security credential - must NEVER be serialized
    private transient String password;

    // Transient: Live OS/Network resource - non-serializable handle
    private transient Connection dbConnection;

    // Added field to demonstrate schema evolution compatibility
    private String phoneNumber;

    public Resident(String name, String registerNumber, String password, Connection dbConnection) {
        this.name = name;
        this.registerNumber = registerNumber;
        this.password = password;
        this.dbConnection = dbConnection;
    }

    public Resident(String name, String registerNumber, String password, Connection dbConnection, String phoneNumber) {
        this(name, registerNumber, password, dbConnection);
        this.phoneNumber = phoneNumber;
    }

    public String getName() { return name; }
    public String getRegisterNumber() { return registerNumber; }
    public String getPassword() { return password; }
    public Connection getDbConnection() { return dbConnection; }
    public String getPhoneNumber() { return phoneNumber; }

    public void setPassword(String password) { this.password = password; }
    public void setDbConnection(Connection dbConnection) { this.dbConnection = dbConnection; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    @Override
    public String toString() {
        return "Resident{" +
                "name='" + name + '\'' +
                ", regNo='" + registerNumber + '\'' +
                ", password=" + (password != null ? "'" + password + "'" : "null (transient/omitted)") +
                ", dbConnection=" + (dbConnection != null ? "ACTIVE_HANDLE" : "null (transient/omitted)") +
                ", phoneNumber=" + (phoneNumber != null ? "'" + phoneNumber + "'" : "null") +
                '}';
    }
}
