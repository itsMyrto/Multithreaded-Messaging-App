import java.util.ArrayList;
import java.util.List;

/**
 * This Class represents the account of user in the messaging app
 */
public class Account {
    private String username;
    private int authToken;
    List<Message> messageBox;

    /**
     * @param username This variable stores the username of the user
     * @param authToken This is the token of the user. It's a unique number.
     */
    public Account(String username, int authToken){
        this.username = username;
        this.authToken = authToken;
        messageBox = new ArrayList<>();
    }

    public String getUsername(){
        return this.username;
    }

    public int getAuthToken(){
        return this.authToken;
    }

    public List<Message> getMessageBox(){
        return this.messageBox;
    }

    public void addMessage(Message message){
        messageBox.add(message);
    }
}
