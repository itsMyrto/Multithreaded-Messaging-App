/**
 * This Class represents the message that a user can send to another user
 */
public class Message {
    private boolean isRead;
    private String sender;
    private String receiver;
    private String body;

    /**
     * @param isRead This variable shows if the receiver has read the message or not
     * @param sender This variable stores the username of the sender
     * @param receiver This variable stores the username of the receiver
     * @param body This is the body of the message
     */
    public Message(boolean isRead, String sender, String receiver, String body){
        this.isRead = false;
        this.sender = sender;
        this.receiver = receiver;
        this.body = body;
    }

    public boolean getIsRead(){
        return this.isRead;
    }

    public String getSender(){
        return this.sender;
    }

    public String getReceiver(){
        return this.receiver;
    }

    public String getBody(){
        return this.body;
    }

    public void setIsRead(boolean isRead){
        this.isRead = isRead;
    }
}
