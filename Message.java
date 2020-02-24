public class Message {

    /**attribute*/
    private User sender;
    private User receiver;
    private String message;
    private boolean isForwarded;
    private boolean isSent;
    private boolean isReceived;
    private int view ;
    private int id;

    /**constructor*/
    public Message(User sender, User receiver, String message, int view){
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.view = view;
        isForwarded = false;
        isSent = false;
        isReceived = false;
    }

    /**setter*/
    public void isSent(){
        isSent = true;
    }

    public void isNotSent(){ isSent = false; }

    public void isReceived(){
        isReceived = true;
    }

    public void isForwarded(){
        isForwarded = true;
    }

    public void addView(){
        view++;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setForwarded(boolean forwarded) {
        isForwarded = forwarded;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public void setReceived(boolean received) {
        isReceived = received;
    }

    /**getter*/
    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }

    public boolean getIsForwarded() {
        return isForwarded;
    }

    public boolean getIsReceived() {
        return isReceived;
    }

    public boolean getIsSent(){
        return isSent;
    }

    public int getView() {
        return view;
    }

    public int getId() {
        return id;
    }
}
