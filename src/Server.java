import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * This Class represents the Server. The Server is active all the time. It gets all the Client's requests and responds to each one of them
 * Since it's a multithreaded app, the Server uses a different thread to handle each client.
 */
public class Server {

    public static void main(String[] args) {
        ServerSocket server = null;

        ArrayList<Account> accounts_connected_to_server = new ArrayList<>();

        try {
            server = new ServerSocket(Integer.parseInt(args[0]));

            while(true){
                Socket client = server.accept();
                //System.out.println("New Client is trying to connect");
                ClientHandler clientSocket = new ClientHandler(client,accounts_connected_to_server);
                new Thread(clientSocket).start();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * This class is used to handle each Client as a different thread
     */
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private ArrayList<Account> accounts_connected_to_server;

        public ClientHandler(Socket socket, ArrayList<Account> accounts_connected_to_server) {
            this.clientSocket = socket;
            this.accounts_connected_to_server = accounts_connected_to_server;
        }

        @Override
        public void run() {
            PrintWriter out = null;
            BufferedReader in = null;
            String[] client_call = null;
            Account account = null;
            int authToken;
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String line = in.readLine();
                client_call = line.split(" ");
                if(!client_call[2].equals("1")){
                    authToken = Integer.parseInt(client_call[3]);
                    account = findUserBasedOnToken(authToken);
                }

                if(account != null || client_call[2].equals("1")){
                    switch (Objects.requireNonNull(client_call)[2]) {
                        case "1" :
                            out.println(createAccount(client_call[3]));
                            break;
                        case "2":
                            out.println(showAccounts());
                            break;
                        case "3":
                            String message_to_send = "";
                            for(int i=5;i<client_call.length;i++){
                                message_to_send = message_to_send.concat(client_call[i] + " ");
                            }
                            out.println(sendMessage(account, client_call[4], message_to_send));
                            break;
                        case "4":
                            out.println(showInbox(account));
                            break;
                        case "5":
                            out.println(readMessage(account, Integer.parseInt(client_call[4])));
                            break;
                        case "6":
                            out.println(deleteMessage(account, Integer.parseInt(client_call[4])));
                            break;
                    }
                }
                else {
                    out.println("Invalid Auth Token");
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * This function finds an account based on the username (since all the usernames are unique) and returns it
         * @param username The username that it searches for
         * @return The account that has the specified username
         */
        public Account findUserBasedOnUsername(String username){
            for(Account account : this.accounts_connected_to_server){
                if(account.getUsername().equals(username)){
                    return account;
                }
            }
            return null;
        }

        /**
         * This function finds an account based on a token (tokens are unique) and returns it
         * @param authToken The token that it searches for
         * @return The account that has the specific token
         */
        public Account findUserBasedOnToken(int authToken){
            for(Account account : this.accounts_connected_to_server){
                if(account.getAuthToken() == authToken){
                    return account;
                }
            }
            return null;
        }

        /**
         * This function finds if a username is invalid or not. Usernames can only contain letters,digits and the underscore ([a-z][A-Z][0-9]_)
         * @param username The username to check if it's valid or not
         * @return True if it's valid or false if it's invalid
         */
        public boolean isInvalid(String username){
            boolean invalid_username = false;
            for(int i=0;i<username.length();i++){
                if(!((username.charAt(i) >= 'A' && username.charAt(i) <= 'Z') || (username.charAt(i) >= 'a' && username.charAt(i) <= 'z') || (username.charAt(i) >= '0' && username.charAt(i) <= '9') || (username.charAt(i) == '_'))){
                    invalid_username = true;
                    break;
                }
            }
            return invalid_username;
        }

        /**
         * This function creates a new account
         * @param username The username of the new account
         * @return The token that it is given to account
         */
        public String createAccount(String username){
            int authToken = 0;
            if(findUserBasedOnUsername(username) != null){
                return "Sorry, the user already exists";
            }
            else if(isInvalid(username)){
                return "Invalid Username";
            }
            else{
                Random rand = new Random();
                int upperbound = 9999;
                int lowerbound = 1000;
                boolean authIsTaken = false;
                while(!authIsTaken) {
                    authToken = rand.nextInt(upperbound - lowerbound) + lowerbound;
                    for (Account account : this.accounts_connected_to_server) {
                        if (account.getAuthToken() == authToken) {
                            authIsTaken = true;
                            break;
                        }
                    }
                    if (!authIsTaken){
                        Account new_account = new Account(username,authToken);
                        this.accounts_connected_to_server.add(new_account);
                        break;
                    }
                }
            }
            return "Your token: " + authToken;
        }

        /**
         * This function creates a list of all the accounts of the messaging app
         * @return The list of the accounts
         */
        public StringBuilder showAccounts(){
            int counter = 1;
            StringBuilder all_accounts = new StringBuilder();
            for (Account account : this.accounts_connected_to_server) {
                all_accounts.append(counter).append(". ").append(account.getUsername()).append("-");
                counter++;
            }
            return all_accounts;
        }

        /**
         * This function finds the inbox of a specific user.
         * @param account The account whose inbox is going to be found
         * @return The inbox
         */
        public StringBuilder showInbox(Account account){
            int message_id = 1;
            List<Message> messageBox = account.getMessageBox();
            StringBuilder account_inbox = new StringBuilder();
            for(Message message:messageBox){
                account_inbox.append(message_id).append(". from: ").append(message.getSender());
                if(!message.getIsRead()){
                    account_inbox.append("*");
                }
                account_inbox.append("-");
                message_id ++;
            }
            return account_inbox;
        }

        /**
         * This function s used to send a message from one user to another
         * @param sender The account of the sender
         * @param recipient The account of the receiver
         * @param messageBody The body of the message
         * @return "OK" if the message was sent successfully
         */
        public String sendMessage(Account sender, String recipient,String messageBody){
            for(Account account : this.accounts_connected_to_server){
                if(account.getUsername().equals(recipient)){
                    Message new_message = new Message(false,sender.getUsername(),recipient,messageBody);
                    account.addMessage(new_message);
                    return "OK";
                }
            }
            return "User does not exist";
        }

        /**
         * This function is used for reading a specific message
         * @param account The account of the user who wants to read a message
         * @param message_id The id of the message
         * @return The message (if it exists)
         */
        public String readMessage(Account account,int message_id){
            List<Message> messageBox = account.getMessageBox();
            int counter = 1;
            for(Message message:messageBox){
                if(counter == message_id){
                    message.setIsRead(true);
                    return "(" + message.getSender() + ")" + message.getBody();
                }
                counter++;
            }
            return "Message ID does not exist";
        }

        /**
         * This function is used to delete a message
         * @param account The account who wants to delete a message
         * @param message_id The id of the message that is going to be deleted
         * @return "OK" if the message was successfully deleted
         */
        public String deleteMessage(Account account,int message_id){
            List<Message> messageBox = account.getMessageBox();
            int counter = 1;
            for(Message message:messageBox){
                if(counter == message_id){
                    messageBox.remove(message);
                    return "OK";
                }
            }
            return "Message does not exist";
        }
    }
}
