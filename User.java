import java.util.ArrayList;

public class User {

    /**attribute*/
    private String phoneNumber;
    private String userName;
    private String password;
    private ArrayList<User> friend;
    private ArrayList<Message> message;
    private int id;
    private int NUMBER_OF_FRIENDS = 0;

    /**constructor*/
    public User(int id, String phoneNumber, String userName, String password){

        this.phoneNumber = phoneNumber;
        this.password= password;
        this.userName = userName;
        this.id = id;
        friend = new ArrayList<>();
        message = new ArrayList<>();

    }

    /*
        public void setReceived(Message rMess){
            rMess.isReceived();
            message.add(rMess);
        }

        public void setSent(Message sMess){
            sMess.isSent();
            message.add(sMess);
        }
    */
    /**setter*/

    public void setMessage(Message mess) {
        //mess.setId(message.size()+1);
        message.add(mess);
    }

    public void setFriend(User fri){
        friend.add(fri);
        NUMBER_OF_FRIENDS = friend.size();
    }

    public void setMessageArray(ArrayList<Message> message){
        this.message = message;
    }

    /**getter*/
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public ArrayList<User> getFriend() {
        return friend;
    }

    public ArrayList<Message> getMessage() {
        return message;
    }

    public int getId(){
        return id;
    }
}
