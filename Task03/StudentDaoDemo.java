package Task03;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * StudentDaoDemo - Verifies Generic DAO contract, entity mapping, and CRUD operations.
 * Student: Geetika T | Reg No: 192511469
 */
public class StudentDaoDemo {

    /**
     * In-Memory Reference DAO implementing the exact same Dao<Student, String> interface
     * to demonstrate the pattern's decoupling and testability when external DB is decoupled.
     */
    public static class InMemoryStudentDao implements Dao<Student, String> {
        private final Map<String, Student> store = new HashMap<>();

        @Override
        public void insert(Student student) throws SQLException {
            if (store.containsKey(student.getRegNo())) {
                throw new SQLException("Primary Key Violation: Duplicate reg_no " + student.getRegNo());
            }
            store.put(student.getRegNo(), student);
        }

        @Override
        public Student findById(String regNo) throws SQLException {
            return store.get(regNo);
        }

        @Override
        public List<Student> findAll() throws SQLException {
            return new java.util.ArrayList<>(store.values());
        }

        @Override
        public void update(Student student) throws SQLException {
            if (!store.containsKey(student.getRegNo())) {
                throw new SQLException("Record not found for update: " + student.getRegNo());
            }
            store.put(student.getRegNo(), student);
        }

        @Override
        public void delete(String regNo) throws SQLException {
            store.remove(regNo);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Task 03: Generic DAO Pattern Verification ===");
        System.out.println("Student: Geetika T | Reg No: 192511469");

        // Instantiate DAO via generic interface
        Dao<Student, String> studentDao = new InMemoryStudentDao();

        try {
            // 1. Insert Operation
            System.out.println("\n1. Testing insert():");
            Student s1 = new Student("192511469", "Geetika T", "192511469@saveetha.com", "CSE", 9.15);
            Student s2 = new Student("192511470", "Rahul R", "192511470@saveetha.com", "IT", 8.85);
            studentDao.insert(s1);
            studentDao.insert(s2);
            System.out.println("Inserted: " + s1);
            System.out.println("Inserted: " + s2);

            // 2. Find By ID Operation
            System.out.println("\n2. Testing findById('192511469'):");
            Student found = studentDao.findById("192511469");
            System.out.println("Found: " + found);

            // 3. Update Operation
            System.out.println("\n3. Testing update() - Updating CGPA to 9.40:");
            found.setCgpa(9.40);
            studentDao.update(found);
            System.out.println("Updated: " + studentDao.findById("192511469"));

            // 4. Find All Operation
            System.out.println("\n4. Testing findAll():");
            List<Student> allStudents = studentDao.findAll();
            for (Student s : allStudents) {
                System.out.println(" -> " + s);
            }

            // 5. Delete Operation
            System.out.println("\n5. Testing delete('192511470'):");
            studentDao.delete("192511470");
            System.out.println("After delete, total records: " + studentDao.findAll().size());
            System.out.println("Lookup 192511470 returns: " + studentDao.findById("192511470"));

            System.out.println("\n==> Generic DAO contract successfully validated!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
