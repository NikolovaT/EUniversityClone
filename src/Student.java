import java.util.ArrayList;
import java.util.List;

public class Student extends User {
    List<Grade> grades = null;

    public Student(String userName, String password) {
        super(userName, password);
        grades = new ArrayList<>();
    }

    @Override
    public UserType getUserType() {
        return UserType.STUDENT;
    }

    @Override
    public String toString() {
        return "Student{} " + super.toString();
    }

    public List<Grade> getGrades() {
        return grades;
    }
}
