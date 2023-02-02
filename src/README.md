# Multithreaded Messaging App

This project has 4 public classes:

- Server

- Client

- Account

- Message

### Message
This class represents a message that can be sent from an account
to another account. <br>

Private fields: <br>
`isRead` - if the message is read or not <br>
`sender` - username of the sender's account <br>
`receiver` - username of the receiver's account <br>
`body` - the body of the message <br>
Public Methods: <br>
`getIsRead()` - getter for *isRead* <br>
`getSender()` - getter for *sender*<br>
`getReceiver()` - getter for *receiver* <br>
`getBody()` - getter for *body* <br>
`setIsRead(boolean isRead)` - setter for isRead <br>

Create a new object:
```sh
Message new_message = new Message(false,sender,recipient,messageBody);
```

### Account
This class represents the account of a user. <br>
Private Fields: <br>
`username` - account's username (unique name) <br>
`authToken` - account's token (unique number) <br>
`messageBox` - a list that stores all the messages that the account has received <br>
Public Methods: <br>
`getUsername()` - getter for *username* <br>
`getAuthToken()` - getter for *token* <br>
`getMessageBox()` - getter for the *MessageBox* <br>
`addMessage(Message message)` - adds a new message to the message box. <br>

Create a new object: <br>
```sh
Account new_account = new Account(username,authToken)
```


### Client
A client requests something from the server and waits for a response.
The connection terminates when the server replies. <br>
Client options:
- 1 - *Create account*
- 2 - *Show accounts*
- 3 - *Send Message*
- 4 - *Show Inbox*
- 5 - *Read A Message*
- 6 - *Delete A Message*

Run the Client:
```shell
java Client localhost <port> <option> <parameters>
```
Where *parameters* : <br>
`option==1` : `<username>` <br>
`option==2` : `<token>` <br>
`option==3` : `<token> <receiver_username> <message>` <br>
`option==4` : `<token>` <br>
`option==5` : `<token> <messageid>` <br>
`option==6` : `<token> <messageid>` <br>

It has only one method, the `main()` method, and it's a very simple one. First, it tries to connect to the server.
If the connection is successful, it creates a string with all the arguments
and passes it to the server using a `PrintWriter()` object.
To get the reply from the server it uses a `BufferedReader()` object.
The server always sends the response as a single string, so if
client's option is 2 or 4, it splits the string so that the output is in the correct form.

### Server
Run the Server:
```shell
java Server <port>
```
The server is always active. To handle each client separately, 
server creates different objects from the class
Client Handler, which is a private class inside the server class.
Each object runs in a different thread.
##### Client Handler
This class is used to serve a client. It implements the Runnable class.
To create a new object:
```sh
ClientHandler clientSocket = new ClientHandler(client,accounts_connected_to_server);
```
where the parameter client is a socket and accounts_connected_to_server is 
an arraylist that stores all the accounts. 

The fields of this class are:
- `private final Socket clientSocket` - the client
- `private ArrayList<Account> accounts_connected_to_server` - list with all the accounts of the app

The methods of this class are:
- `public boolean isInvalid(String username)` - This method is used to check if a username is valid or not.
- `public Account findUserBasedOnUsername(String username)` - This method is used to search an account based on a username
and returns the account or null if it does not exist.
- `public Account findUserBasedOnToken(int authToken)` - This method is used to search for an account based on a token and returns
the account or null if it does not exist.
- `public String createAccount(String username)` - This method is used to create a new account for the client. 
First, it checks if the username that the client wants to use is taken by someone else or is invalid. If not then it randomly produces
a unique token for the new user. Returns the token if the account is successfully created or a warning message if something went wrong.
- `public StringBuilder showAccounts()` - This method gets all the usernames of the app from the list where the accounts 
are stored and creates a string with all the usernames numbered and seperated with a dash "-". It 
returns something like this: *"1. username1-2. username2"*. In the client's main method the string is split into different lines,
so that it's printed in the correct way.
- `public StringBuilder showInbox(Account account)` - This method works the same way with the previous method, but it returns
a string with the inbox of the account.
- `public String sendMessage(Account sender, String recipient,String messageBody)` - This method is used to send a message from one
account to another. It just adds a new message to the receiver's message box. Returns "OK" if the message is sent.
- `public String readMessage(Account account,int message_id)` - This method is used to read a specific message based on the message id. Returns "OK" if the message is read
- `public String deleteMessage(Account account,int message_id)` - This method is used to delete a specific message based on the message id. Returns "OK" if the message is deleted.
- `run()` - This method reads the client's request using an object of the `BufferedReader()` class. Then, based on client's
option it calls the correct method. Of course if `option!=1` it checks if the given token is valid. When the reply is ready to sent, it uses a `PrintWriter()` object to send the reply to the client's `InputStream`


