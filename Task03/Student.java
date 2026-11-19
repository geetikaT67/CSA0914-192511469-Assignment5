package Task03;

import java.io.Serializable;
import java.util.Objects;

/**
 * Student Domain Entity
 * Student: Geetika T | Reg No: 192511469
 */
public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private String regNo;       // Primary Key (e.g. "192511469")
    private String name;        // e.g. "Geetika T"
    private String email;       // e.g. "192511469@saveetha.com"
    private String department;  // e.g. "Computer Science & Engineering"
    private double cgpa;        // e.g. 9.15

    public Student() {}

    public Student(String regNo, String name, String email, String department, double cgpa) {
        this.regNo = regNo;
        this.name = name;
        this.email = email;
        this.department = department;
        this.cgpa = cgpa;
    }

    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public double getCgpa() { return cgpa; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }

    @Override
    public String toString() {
        return String.format("Student[regNo='%s', name='%s', email='%s', dept='%s', cgpa=%.2f]",
                regNo, name, email, department, cgpa);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student student = (Student) o;
        return Objects.equals(regNo, student.regNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regNo);
    }
}
