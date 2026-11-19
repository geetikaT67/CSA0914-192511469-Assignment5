package Task03;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * StudentDao - Concrete implementation of generic Dao<Student, String>
 * Handles all JDBC operations for the 'student' table using PreparedStatement.
 * Student: Geetika T | Reg No: 192511469
 */
public class StudentDao implements Dao<Student, String> {

    private final Connection connection;

    public StudentDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(Student student) throws SQLException {
        String sql = "INSERT INTO student (reg_no, name, email, department, cgpa) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, student.getRegNo());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getDepartment());
            pstmt.setDouble(5, student.getCgpa());
            pstmt.executeUpdate();
        }
    }

    @Override
    public Student findById(String regNo) throws SQLException {
        String sql = "SELECT reg_no, name, email, department, cgpa FROM student WHERE reg_no = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, regNo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToStudent(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Student> findAll() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT reg_no, name, email, department, cgpa FROM student ORDER BY reg_no ASC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToStudent(rs));
            }
        }
        return list;
    }

    @Override
    public void update(Student student) throws SQLException {
        String sql = "UPDATE student SET name = ?, email = ?, department = ?, cgpa = ? WHERE reg_no = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getEmail());
            pstmt.setString(3, student.getDepartment());
            pstmt.setDouble(4, student.getCgpa());
            pstmt.setString(5, student.getRegNo());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(String regNo) throws SQLException {
        String sql = "DELETE FROM student WHERE reg_no = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, regNo);
            pstmt.executeUpdate();
        }
    }

    private Student mapRowToStudent(ResultSet rs) throws SQLException {
        return new Student(
                rs.getString("reg_no"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("department"),
                rs.getDouble("cgpa")
        );
    }
}
