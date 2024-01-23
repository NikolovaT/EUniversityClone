import java.io.*;
import java.net.Socket;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ClientHandler implements Runnable {
    Socket client = null;
    private final Object usersLock;

    public ClientHandler(Socket client) {
        this.client = client;
        usersLock = new Object();
    }

    @Override
    public void run() {
        System.out.println(loadUsers());
        System.out.println("New client connected.");
        Scanner in = null;
        PrintStream out = null;

        try {
            in = new Scanner(client.getInputStream());
            out = new PrintStream(client.getOutputStream());
            userMenu(in, out);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CredentialsException e) {
            out.println(e.getMessage());
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    public void userMenu(Scanner in, PrintStream out) throws CredentialsException {
        while (true) {
            out.println("Login? Y/N");
            String login = in.nextLine();

            if (!login.equalsIgnoreCase("Y")) {
                out.println("Goodbye.");
                return;
            }

            out.println("Enter username:");
            String userName = in.nextLine();

            out.println("Enter password:");
            String password = in.nextLine();

            User user = login(userName, password);

            if (user == null) {
                out.println("Invalid login.");
                continue;
            }

            switch (user.getUserType()) {
                case ADMIN: {
                    adminMenu(in, out, (Admin) user);
                    break;
                }
                case TEACHER: {
                    teacherMenu(in, out, (Teacher) user);
                    break;
                }
                case STUDENT: {
                    studentMenu(in, out, (Student) user);
                    break;
                }
            }

        }
    }

    public User login(String userName, String password) {
        synchronized (usersLock) {
            for (User user : loadUsers()) {
                if (Objects.equals(user.getUserName(), userName) && Objects.equals(user.getPassword(), password)) {
                    return user;
                }
            }
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<User> loadUsers() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(Server.FILE_NAME))) {
            return (List<User>) in.readObject();
        } catch (IOException e) {
            if (e instanceof InvalidClassException) {
                throw new RuntimeException("One or more of the User subclasses was changed." +
                        " Serializable versions are not supported." +
                        " Run initDataBase() again", e);
            }

            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public void adminMenu(Scanner in, PrintStream out, Admin admin) throws CredentialsException {
        out.println("Logged in as admin.");
        out.println("Enter user type to create: (ADMIN | STUDENT | TEACHER");
        try {
            UserType userType = UserType.valueOf(in.nextLine().toUpperCase());

            out.println("Enter username:");
            String userName = in.nextLine();

            out.println("Enter password:");
            String password = in.nextLine();

            registerUser(userName, password, userType);

            out.println("Success.");
        } catch (IllegalArgumentException e) {
            out.println("Error: Invalid user type.");
        }
    }

    public void registerUser(String userName, String password, UserType userType) throws CredentialsException {
        User user = UserFactory.createUser(userName, password, userType);

        synchronized (usersLock) {
            List<User> users = loadUsers();
            users.add(user);
            Server.saveUsers(users);
        }
    }


    private void teacherMenu(Scanner in, PrintStream out, Teacher user) {
        out.println("Logged in as teacher.");
        out.println("Enter enter faculty No of a student:");
        String userName = in.nextLine();

        out.println("Enter subject:");
        String subject = in.nextLine();

        out.println("Enter semester:");
        int semester = Integer.parseInt(in.nextLine());

        out.println("Enter grade:");
        int gradeValue = Integer.parseInt(in.nextLine());

        Grade grade = new Grade(subject, semester, gradeValue);

        synchronized (usersLock) {
            List<User> users = loadUsers();
            for (User target : users) {
                if (target.getUserName().equals(userName) && target instanceof Student) {
                    Student student = (Student) target;
                    student.getGrades().add(grade);
                    Server.saveUsers(users);

                    out.println("Success.");
                    return;
                }
            }
            out.println("No such student.");
        }
    }

    private void studentMenu(Scanner sc, PrintStream out, Student student) {
        out.println("Logged in as student.");
        List<Grade> sortedGrades = student.getGrades()
                .stream()
                .sorted(Comparator.comparingInt(Grade::getSemester).thenComparing(Grade::getSubject))
                .collect(Collectors.toList());
        out.println(sortedGrades);
    }
}
