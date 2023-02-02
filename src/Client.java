import java.io.*;
import java.net.*;

/**
 * This Class represents the Client.
 * When a user wants to do something in the messaging app (for example send a message) they have to make a request to the server
 * and wait until the server responds with an answer. Then, the connection is terminated.
 */
public class Client {

    public static void main(String[] args){
        try (Socket socket = new Socket(args[0],Integer.parseInt(args[1]))){
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String to_server = "";
            for(String arg : args){
                to_server = to_server.concat(arg + " ");
            }
            to_server = to_server.concat("\n");
            out.print(to_server);
            out.flush();
            if(args[2].equals("2") || args[2].equals("4")){
                String[] server_answer = in.readLine().split("-");
                for(String server_line : server_answer){
                    System.out.println(server_line);
                }
            }
            else{
                System.out.println(in.readLine());
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
