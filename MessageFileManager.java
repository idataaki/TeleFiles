import com.sun.xml.internal.bind.v2.TODO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class MessageFileManager {

    RandomAccessFile messageRandomAccessFile;

    public static final int ID_LENGTH = 4;
    public static final int SENDER_ID_LENGTH = 4;
    public static final int RECEIVER_ID_LENGTH = 4;
    public static final int TEXT_LENGTH = 50*2;
    public static final int VIEW_LENGTH = 4;
    public static final int IS_SENT_LENGTH = 1;
    public static final int IS_RECEIVED_LENGTH = 1;
    public static final int IS_FORWARDED_LENGTH = 1;
    public static final int RECORD_LENGTH = ID_LENGTH+SENDER_ID_LENGTH+RECEIVER_ID_LENGTH+TEXT_LENGTH+VIEW_LENGTH+
            IS_SENT_LENGTH+IS_RECEIVED_LENGTH+IS_FORWARDED_LENGTH; //=119

    public static final int SENDER_ID_POS = 4;
    public static final int RECEIVED_ID_POS = 8;
    public static final int TEXT_POS = 12;

    public MessageFileManager() throws FileNotFoundException {
        messageRandomAccessFile = new RandomAccessFile(new File("C:\\Users\\ASUS\\Desktop\\message.dat"), "rw");
    }

    ///////////////////////////////////////////////////////BASICS///////////////////////////////////////////////////////

    private String setStringLength(String str,int len) {
        String temp = str;
        if (str.length() > len) {
            return str.substring(0, len);
        } else {
            int spaceCount = len - str.length();
            for (int i = 0; i < spaceCount; i++) {
                temp += " ";
            }
            return temp;
        }
    }

    public String readString(RandomAccessFile randomAccessFile, int len) throws IOException {
        char array[]=new char[len];
        for (int i = 0; i < len; i++) {
            array[i]=randomAccessFile.readChar();

        }
        return (new String(array)).trim();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void addNewMessage(Message message) throws IOException {
        messageRandomAccessFile.seek(messageRandomAccessFile.length()); //seek to the end of the file
        messageRandomAccessFile.writeInt(message.getId());
        messageRandomAccessFile.writeInt(message.getSender().getId());
        messageRandomAccessFile.writeInt(message.getReceiver().getId());
        messageRandomAccessFile.writeChars(setStringLength(message.getMessage(), 50));
        messageRandomAccessFile.writeInt(message.getView());
        messageRandomAccessFile.writeBoolean(message.getIsSent());
        messageRandomAccessFile.writeBoolean(message.getIsReceived());
        messageRandomAccessFile.writeBoolean(message.getIsForwarded());
    }

    public ArrayList<Message> findUserMessage(int userId) throws IOException {
        ArrayList<Message> result = new ArrayList<>();

        int sit = 0;
        messageRandomAccessFile.seek(0);
        while (messageRandomAccessFile.getFilePointer() < messageRandomAccessFile.length()){
            messageRandomAccessFile.seek((sit*RECORD_LENGTH) + SENDER_ID_POS);
            if (messageRandomAccessFile.readInt() == userId){
                result.add(makeMessage(sit*RECORD_LENGTH));
            }else {
                messageRandomAccessFile.seek(((sit+1)*RECORD_LENGTH) + RECEIVED_ID_POS);
                if (messageRandomAccessFile.readInt() == userId){
                    result.add(makeMessage((sit+1)*RECORD_LENGTH));
                }
            }

            sit+=2;
            if (sit == messageRandomAccessFile.length()/RECORD_LENGTH){
                break;
            }
        }

        return result;
    }

    private Message makeMessage(int pos) throws IOException {

        messageRandomAccessFile.seek(pos);
        int id = messageRandomAccessFile.readInt();
        UserFileManager userFileManager = new UserFileManager();

        messageRandomAccessFile.seek(pos + SENDER_ID_POS);
        User sender = userFileManager.findUserById(messageRandomAccessFile.readInt());

        messageRandomAccessFile.seek(pos + RECEIVED_ID_POS);
        User receiver = userFileManager.findUserById(messageRandomAccessFile.readInt());

        messageRandomAccessFile.seek(pos + TEXT_POS);
        Message message = new Message(sender, receiver, readString(messageRandomAccessFile, 50), messageRandomAccessFile.readInt());

        message.setId(id);
        messageRandomAccessFile.seek(pos + TEXT_POS + TEXT_LENGTH + VIEW_LENGTH);
        message.setSent(messageRandomAccessFile.readBoolean());
        message.setReceived(messageRandomAccessFile.readBoolean());
        message.setForwarded(messageRandomAccessFile.readBoolean());
        //new a message
        //write a method to get a id and return a user
        //add attribute of message
        return message;

    }

}
