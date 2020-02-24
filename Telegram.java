import javax.jws.soap.SOAPBinding;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Telegram {

    /**attribute*/
    private User activeUser;
    private Scanner input;
    private UserFileManager userFileManager;
    private MessageFileManager messageFileManager;

    /**constructor*/
    public Telegram() throws FileNotFoundException {
        input = new Scanner(System.in);
        userFileManager = new UserFileManager();
        messageFileManager= new MessageFileManager();
    }

    private User signIn(String phoneNumber, String pass) throws IOException {
        User user = userFileManager.findUserByPhoneNumber(phoneNumber);
        if (user != null){
            if (user.getPassword().equals(pass)){
                return user;
            }else {
                System.out.println("ERROR! wrong password");
                return null;
            }
        }
        System.out.println("ERROR! wrong phone number");
        return null;
    } /**completed*/

    private void welcome() throws IOException {
        //userFileManager.initialize();
        activeUser = signIn(String.valueOf(printAndScan("getPhoneNumber")), String.valueOf(printAndScan("getPass")));

        if (activeUser != null){

            System.out.println("-------------------------------------------------------------------\nYou Are Signed In As [" +
                    activeUser.getUserName() + "]\n");
        }
    } /**completed*/

    public void mainMenu() throws IOException {
        int recur = 1;
        welcome();
        while (recur == 1) {

            activeUser.setMessageArray(messageFileManager.findUserMessage(activeUser.getId()));

            showUserInfo();

            System.out.println("\nMAIN MENU\n\t1.send message\n\t2.forward message\n\t3.add friend\n\t4.log out");
            String selection = input.next();
            switch (selection){
                case "1":
                    send((String.valueOf(printAndScan("send"))) , activeUser, (User) printAndScan("findFriend"));
                    break;
                case "2":
                    for (Message mes : findReceived(activeUser.getMessage())){
                            System.out.println(String.format("%d) %s", mes.getId(), mes.getMessage()));
                    }
                    System.out.println();
                    forward((Integer)printAndScan("textId") , activeUser, (User) printAndScan("findFriend"));
                    break;

                case "3":
                    User user = (User) printAndScan("addFriend");
                    if (user != null) {
                        addFriend(user);
                    }else {
                        System.out.println("ERROR! user not found");
                    }
                    break;
                case "4":
                    recur = 0;
                    break;
                default:
                    break;
            }

        }
    } /**completed*/

    private void send(String text, User sender, User receiver) throws IOException {
        //1
        Message message = new Message(sender,receiver,text,1);
        //2
        message.isSent();
        message.setId(sender.getMessage().size()+1);
        sender.setMessage(message);
        messageFileManager.addNewMessage(message);
        //3
        message.isNotSent();
        message.isReceived();
        message.setId(receiver.getMessage().size()+1);
        receiver.setMessage(message);
        messageFileManager.addNewMessage(message);

        /*
        1 make a message with text sender receiver view =1
        2 isSent true and add the massage to the massage list of sender
          use a method of UserFileManager to add the new information
        3 isSent false, isReceived true and add the massage to the massage list of receiver
          use a method of UserFileManager
        */
    } /**completed*/

    private void forward(int textId, User sender, User receiver) throws IOException {

        Message newMessage = null;
        //1 and 2 and 3
        for (Message message: findReceived(sender.getMessage())) {
            System.out.println(message.getId() + "in forward");
            if (message.getId() == textId){
                newMessage = new Message(sender, receiver, message.getMessage(), message.getView());
            }
        }

        if (newMessage == null){
            System.out.println("wrong message id");
            return;
        }

        //4
        newMessage.isSent();
        newMessage.setId(sender.getMessage().size()+1);
        newMessage.addView();
        sender.setMessage(newMessage);
        messageFileManager.addNewMessage(newMessage);

        //5
        newMessage.isNotSent();
        newMessage.isReceived();
        newMessage.isForwarded();
        newMessage.addView();
        newMessage.setId(receiver.getMessage().size()+1);
        receiver.setMessage(newMessage);
        messageFileManager.addNewMessage(newMessage);

        /*
         1 find the text with textId in received message of sender
         2 save it text
         3 make a new message with saved text, sender, receiver
         4 isSent true and add it to sender message
           UserFileManager.addNewMessage
         5 isSent False and isReceived and isForwarded true and add to receiver
           UserFileManager.addNewMessage

         */
    } /**completed*/

    private void addFriend(User friend) throws IOException {

        activeUser.setFriend(friend);
        userFileManager.addNewUserFriend(activeUser.getId(), friend.getUserName());

        /*
           User friend have been found before in method findUserByName in UserFileManager
           add the friend to the friend list of activeUser
           call addNewUserFriend of UserFileManager
         */
    }  /**completed*/

    private void showUserFriend() throws IOException {
        if (userFileManager.findUserFriends(activeUser.getId()) != null) {
            for (String friName : userFileManager.findUserFriends(activeUser.getId())) {
                System.out.println(friName);
            }
        }
    } /**completed*/

    private void showReceivedMessage() throws IOException {

        String pattern = "ID) (TEXT) from SENDER - VIEW views";

        for (Message message : findReceived(activeUser.getMessage())){
            if (message.getSender().getId() != activeUser.getId()) {
                pattern = pattern.replaceAll("ID", String.valueOf(message.getId()));
                pattern = pattern.replaceAll("TEXT", message.getMessage());
                pattern = pattern.replaceAll("SENDER", message.getSender().getUserName());
                pattern = pattern.replaceAll("VIEW", String.valueOf(message.getView()));
                if (message.getIsForwarded()) {
                    pattern = pattern + "(forwarded message)";
                }
                System.out.println(pattern);
                pattern = "ID) (TEXT) from SENDER - VIEW views";
            }
        }
    } /**completed*/

    private void showSentMessage() throws IOException {

        for (Message message : findSent(activeUser.getMessage())){
            if (message.getReceiver().getId() != activeUser.getId() ){
            System.out.println(String.format("%d) (%s) to %s - %d views", message.getId(), message.getMessage(),
                    message.getReceiver().getUserName(), message.getView()));
            }
        }
    } /**completed*/

    private void showUserInfo() throws IOException {
        System.out.println("\nFRIENDS:");
        showUserFriend();

        System.out.println("\nRECEIVED:");
        showReceivedMessage();

        System.out.println("\nSENT:");
        showSentMessage();

    } /**completed*/

    private Object printAndScan(String sit) throws IOException {

        switch (sit){
            case "send":
                System.out.println("enter your text");
                return input.next();
            case "textId":
                System.out.println("inter the text id");
                return input.nextInt();
            case "findFriend":
                System.out.println("enter friend name:");
                String friName = input.next();
                for (String name: userFileManager.findUserFriends(activeUser.getId())) {
                    if (name.equals(friName)){
                        User nUser = userFileManager.findUserByName(friName);
                        nUser.setMessageArray(messageFileManager.findUserMessage(nUser.getId()));
                        return nUser;
                       // return userFileManager.findUserByName(friName);
                    }
                }
                break;
            case "getPhoneNumber":
                System.out.println("phone number:");
                return input.next();
            case "getPass":
                System.out.println("password:");
                return input.next();
            case "addFriend":
                System.out.println("enter user name:");
                String name = input.next();
                return userFileManager.findUserByName(name);
            default:
                    break;
        }

        return null;
    }  /**completed*/

    public ArrayList<Message> findReceived(ArrayList<Message> messages){
        ArrayList<Message> received = new ArrayList<>();
        for (Message message : messages) {
            if (message.getIsReceived()){
                received.add(message);
            }
        }
        return received;
    } /**completed*/

    public ArrayList<Message> findSent(ArrayList<Message> messages){
        ArrayList<Message> sent = new ArrayList<>();
        for (Message message:messages) {
            if (message.getIsSent()){
                sent.add(message);
            }
        }
        return sent;
    } /**completed*/

}
