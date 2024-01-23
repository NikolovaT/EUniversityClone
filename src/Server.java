import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static final String FILE_NAME = "users.bin";

    ServerSocket server = null;
    public Server(){
        initDataBase();
    }

    public void start(){
        try{
            server = new ServerSocket(8080);
            System.out.println("Server is up.");

            while (true){
                Socket client = server.accept();

                ClientHandler clientSock = new ClientHandler(client);
                new Thread(clientSock).start();
            }
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
        finally {
            if(server != null){
                try{
                    server.close();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void initDataBase(){
        if(new File(FILE_NAME).exists()){
            return;
        }
        List<User> users = new ArrayList<>();
        users.add(new Admin("admin", "admin"));
        saveUsers(users);
    }

    public static void saveUsers(List<User> users){
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))){
            out.writeObject(users);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
