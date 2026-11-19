package Task04;

import java.io.*;

/**
 * SerializationDemo - Verifies Object Graph Serialization and Transient Security.
 * Student: Geetika T | Reg No: 192511469
 */
public class SerializationDemo {

    public static void main(String[] args) {
        System.out.println("=== Task 04: Serialization Design Verification ===");
        System.out.println("Student: Geetika T | Reg No: 192511469\n");

        File serFile = new File("TestData/hostel_test.ser");

        // 1. Build Object Graph
        Hostel hostel = new Hostel("Cauvery International Hostel");
        Room room304 = new Room(304, 2);

        // Resident with transient password and live connection simulation
        Resident r1 = new Resident("Geetika T", "192511469", "SuperSecretPwd@2026", null, "9876543210");
        Resident r2 = new Resident("Ananya S", "192511470", "MyHostelPass#99", null, "9123456780");

        room304.addResident(r1);
        room304.addResident(r2);
        hostel.addRoom(room304);

        System.out.println("[Original Object Graph Before Serialization]:");
        System.out.println(hostel);
        System.out.println(" -> Resident 1 Password in memory: " + r1.getPassword());
        System.out.println(" -> Resident 1 Phone Number: " + r1.getPhoneNumber());

        // 2. Serialize Object Graph to File
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(serFile))) {
            oos.writeObject(hostel);
            oos.flush();
            System.out.println("\n[Serialization Success]: Serialized entire object graph to " + serFile.getPath());
            System.out.println("File Size on Disk: " + serFile.length() + " bytes");
        } catch (IOException e) {
            System.err.println("Serialization failed: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // 3. Deserialize Object Graph from File
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(serFile))) {
            Hostel restoredHostel = (Hostel) ois.readObject();
            System.out.println("\n[Deserialization Success]: Restored object graph:");
            System.out.println(restoredHostel);

            Resident restoredR1 = restoredHostel.getRooms().get(0).getResidents().get(0);
            System.out.println("\n[Transient Field Verification]:");
            System.out.println(" -> Restored Name: " + restoredR1.getName() + " (PRESERVED)");
            System.out.println(" -> Restored Reg No: " + restoredR1.getRegisterNumber() + " (PRESERVED)");
            System.out.println(" -> Restored Password: " + restoredR1.getPassword() + " (PROTECTED / NULL)");
            System.out.println(" -> Restored DB Connection: " + restoredR1.getDbConnection() + " (PROTECTED / NULL)");
            System.out.println(" -> Restored Phone Number: " + restoredR1.getPhoneNumber() + " (PRESERVED)");

            if (restoredR1.getPassword() == null && restoredR1.getDbConnection() == null) {
                System.out.println("\n==> SUCCESS: All transient fields safely protected and excluded from persistence!");
            } else {
                System.out.println("\n==> FAILURE: Sensitive fields were leaked into serialized store!");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Deserialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
