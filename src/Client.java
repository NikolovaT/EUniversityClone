import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Socket server = null;
        Scanner console = null;
        Scanner in = null;

        try {
            server = new Socket("localhost", 8080);

            console = new Scanner(System.in);

            in = new Scanner(server.getInputStream());
            PrintStream out = new PrintStream(server.getOutputStream());

            userMenu(console, in, out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (in != null)
                in.close();
        }
    }

    private static void userMenu(Scanner console, Scanner in, PrintStream out) {
        while (true) {

            // Login message
            System.out.println(in.nextLine());

            // Enter Y/N for login
            out.println(console.nextLine());

            String next = in.nextLine();
            System.out.println(next);
            if (next.equals("Goodbye."))
                return;

            // Enter username
            out.println(console.nextLine());

            // Enter password
            System.out.println(in.nextLine());
            out.println(console.nextLine());

            // Login type
            next = in.nextLine();
            System.out.println(next);
            if (next.startsWith("Error"))
                continue;
            if (next.equals("Logged in as admin.")) {
                adminMenu(console, in, out);
            }
            if (next.equals("Logged in as student.")) {
                //studentMenu(console, in, out);
            }
            if (next.equals("Logged in as teacher.")) {
                teacherMenu(console, in, out);
            }
        }
    }

    public static void adminMenu(Scanner console, Scanner in, PrintStream out) {
        // Enter user type
        System.out.println(in.nextLine());
        out.println(console.nextLine());

        // Enter username
        String next = in.nextLine();
        System.out.println(next);
        if (next.startsWith("Error"))
            return;
        out.println(console.nextLine());

        // Enter password
        next = in.nextLine();
        System.out.println(next);
        if (next.startsWith("Error"))
            return;
        out.println(console.nextLine());

        // Result
        System.out.println(in.nextLine());
    }

    private static void teacherMenu(Scanner console, Scanner sc, PrintStream out) {
        // Enter faculty number
        System.out.println(sc.nextLine());
        out.println(console.nextLine());

        // Enter subject
        System.out.println(sc.nextLine());
        out.println(console.nextLine());

        // Enter semester
        System.out.println(sc.nextLine());
        out.println(console.nextLine());

        // Enter grade
        System.out.println(sc.nextLine());
        out.println(console.nextLine());

        // Result
        System.out.println(sc.nextLine());
    }

    private static void studentMenu(Scanner console, Scanner sc, PrintStream out) {
        // Print grades
        System.out.println(sc.nextLine());
    }
}
