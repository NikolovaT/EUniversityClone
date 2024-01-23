public class Teacher extends User{

    public Teacher(String userName, String password){
        super(userName, password);
    }

    @Override
    public UserType getUserType(){
        return UserType.TEACHER;
    }

    @Override
    public String toString(){
        return "Teacher{} " + super.toString();
    }
}
