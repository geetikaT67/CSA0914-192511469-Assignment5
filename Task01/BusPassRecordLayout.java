package Task01;

import java.io.*;

/**
 * BusPassRecordLayout - Demonstrates both delimited text and DataOutputStream binary layouts.
 * Student Name: Geetika T
 * Register Number: 192511469
 */
public class BusPassRecordLayout {

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

        // Delimited Text Representation
        public String toDelimitedText() {
            return String.format("%s|%s|%s|%s|%s|%s|%.2f",
                    passId, regNo, name, routeNo, boardingStop, validityDate, amountPaid);
        }

        public static BusPass fromDelimitedText(String line) throws IllegalArgumentException {
            if (line == null || line.trim().isEmpty()) {
                throw new IllegalArgumentException("Empty line encountered");
            }
            String[] tokens = line.split("\\|", -1);
            if (tokens.length != 7) {
                throw new IllegalArgumentException("Invalid token count: expected 7, found " + tokens.length);
            }
            double amount = Double.parseDouble(tokens[6].trim());
            return new BusPass(tokens[0].trim(), tokens[1].trim(), tokens[2].trim(),
                    tokens[3].trim(), tokens[4].trim(), tokens[5].trim(), amount);
        }

        // Binary Representation using DataOutputStream
        public void writeBinary(DataOutputStream dos) throws IOException {
            dos.writeUTF(passId);
            dos.writeUTF(regNo);
            dos.writeUTF(name);
            dos.writeUTF(routeNo);
            dos.writeUTF(boardingStop);
            dos.writeUTF(validityDate);
            dos.writeDouble(amountPaid);
        }

        public static BusPass readBinary(DataInputStream dis) throws IOException {
            String passId = dis.readUTF();
            String regNo = dis.readUTF();
            String name = dis.readUTF();
            String routeNo = dis.readUTF();
            String boardingStop = dis.readUTF();
            String validityDate = dis.readUTF();
            double amountPaid = dis.readDouble();
            return new BusPass(passId, regNo, name, routeNo, boardingStop, validityDate, amountPaid);
        }

        @Override
        public String toString() {
            return "BusPass{" +
                    "passId='" + passId + '\'' +
                    ", regNo='" + regNo + '\'' +
                    ", name='" + name + '\'' +
                    ", routeNo='" + routeNo + '\'' +
                    ", boardingStop='" + boardingStop + '\'' +
                    ", validityDate='" + validityDate + '\'' +
                    ", amountPaid=" + amountPaid +
                    '}';
        }

        public String getPassId() { return passId; }
        public String getRegNo() { return regNo; }
        public String getName() { return name; }
        public String getRouteNo() { return routeNo; }
        public String getBoardingStop() { return boardingStop; }
        public String getValidityDate() { return validityDate; }
        public double getAmountPaid() { return amountPaid; }
    }

    public static void main(String[] args) {
        System.out.println("=== Task 01: File Format Record Layout Verification ===");
        System.out.println("Student: Geetika T | Reg No: 192511469");

        BusPass pass = new BusPass("P1003", "192511469", "Geetika T", "Route-104", 
                                  "Poonamallee", "2026-12-31", 2500.00);

        // 1. Delimited Text Demonstration
        String delimited = pass.toDelimitedText();
        System.out.println("\n[Delimited Text Format]:\n" + delimited);
        BusPass restoredText = BusPass.fromDelimitedText(delimited);
        System.out.println("Restored from Delimited Text: " + restoredText);

        // 2. Binary Serialization Demonstration
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(baos)) {
            pass.writeBinary(dos);
            dos.flush();
            byte[] binaryBytes = baos.toByteArray();
            System.out.println("\n[Binary DataOutputStream Format]:");
            System.out.println("Total Serialized Bytes: " + binaryBytes.length + " bytes");

            try (ByteArrayInputStream bais = new ByteArrayInputStream(binaryBytes);
                 DataInputStream dis = new DataInputStream(bais)) {
                BusPass restoredBinary = BusPass.readBinary(dis);
                System.out.println("Restored from Binary Stream: " + restoredBinary);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
