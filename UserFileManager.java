import com.sun.prism.shader.Solid_RadialGradient_REFLECT_AlphaTest_Loader;

import javax.jws.soap.SOAPBinding;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class UserFileManager {


    RandomAccessFile usersRandomAccessFile;

    public static final int ID_LENGTH = 4;
    public static final int PASSWORD_LENGTH = 10 *2;
    public static final int USERNAME_LENGTH = 10 *2;
    public static final int PHONENUMBER_LENGTH = 10*2;
    public static final int FRIEND_LENGTH = 10*2;
    public static final int NUM_OF_FRIENDS_LENGTH = 4;

    public static final int ID_POS = 0;
    public static final int PHONENUMBER_POS = 4;
    public static final int USERNAME_POS = 24;
    public static final int PASSWORD_POS = 44;
    public static final int NUM_OF_FRIEND_POS = 64;

    public static final int HALF_LENGTH = ID_LENGTH + PASSWORD_LENGTH + USERNAME_LENGTH + PHONENUMBER_LENGTH;
    public static final int RECORD_LENGTH = ID_LENGTH + PASSWORD_LENGTH + USERNAME_LENGTH + PHONENUMBER_LENGTH +
            NUM_OF_FRIENDS_LENGTH + (5*FRIEND_LENGTH); // = 168


    public UserFileManager()  throws FileNotFoundException {
        usersRandomAccessFile = new RandomAccessFile(new File("C:\\Users\\ASUS\\Desktop\\users.dat"), "rw");
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

    ///////////////////////////////////////////////////////FINDING USER/////////////////////////////////////////////////

    public void initialize() throws IOException {

        usersRandomAccessFile.seek(0);
        for (int i = 0; i <10 ; i++) {
            usersRandomAccessFile.writeInt(i);             //id
            usersRandomAccessFile.writeChars(setStringLength("0"+i, 10));  //phone number
            usersRandomAccessFile.writeChars(setStringLength("user"+i, 10));//user name
            usersRandomAccessFile.writeChars(setStringLength("0"+i, 10));  //pass
            usersRandomAccessFile.writeInt(0);

            for (int j = 0; j <5 ; j++) {
                usersRandomAccessFile.writeChars(setStringLength("!",10));
            }

        }

        //add 10 user with random access file
    }

    public User findUserById(int userId) throws IOException {

        int sit = 0;
        usersRandomAccessFile.seek(0);
        while (usersRandomAccessFile.getFilePointer() < usersRandomAccessFile.length()){
            usersRandomAccessFile.seek((RECORD_LENGTH*sit)+ID_POS);
            if (usersRandomAccessFile.readInt() == userId){
                //name is found
                return makeUser(sit*RECORD_LENGTH);
                //usersRandomAccessFile.getFilePointer() - (ID_LENGTH + PHONENUMBER_LENGTH + USERNAME_LENGTH)
            }
            sit++;
        }
        //if name was not found
        return  null;
    }

    public User findUserByPhoneNumber(String phoneNumber) throws IOException {

        String tempPnumber = null;
        int sit = 0;
        usersRandomAccessFile.seek(0);
        while (usersRandomAccessFile.getFilePointer() < usersRandomAccessFile.length()){
            usersRandomAccessFile.seek((RECORD_LENGTH*sit)+PHONENUMBER_POS);
            tempPnumber = readString(usersRandomAccessFile, 10);
            if (phoneNumber.equals(tempPnumber)){
                //phone number is found
                return makeUser(usersRandomAccessFile.getFilePointer() - (ID_LENGTH+PHONENUMBER_LENGTH));
            }
            sit++;
        }
        //if p number was not found
        return null;
    } /**completed-tested*/

    public User findUserByName(String name) throws IOException {

        String tempName = null;
        int sit = 0;
        usersRandomAccessFile.seek(0);
        while (usersRandomAccessFile.getFilePointer() < usersRandomAccessFile.length()){
            usersRandomAccessFile.seek((RECORD_LENGTH*sit)+USERNAME_POS);
            tempName = readString(usersRandomAccessFile, 10);
            if (name.equals(tempName)){
                //name is found
                return makeUser(usersRandomAccessFile.getFilePointer() - (ID_LENGTH + PHONENUMBER_LENGTH + USERNAME_LENGTH));
            }
            sit++;
        }
        //if name was not found
        return null;
    } /**completed-tested*/

    private User makeUser(long pos) throws IOException {

        User user = null;

        usersRandomAccessFile.seek(pos);
        while (usersRandomAccessFile.getFilePointer() < pos + HALF_LENGTH){
             user = new User(usersRandomAccessFile.readInt(), readString(usersRandomAccessFile, 10),
                     readString(usersRandomAccessFile, 10), readString(usersRandomAccessFile, 10));
        }

        return user;
    } /**completed-tested*/

    //////////////////////////////////////////////////FRIENDS///////////////////////////////////////////////////////////

    public void addNewUserFriend(int id, String name) throws IOException {

        usersRandomAccessFile.seek(0);
        int sit = 0;
        while (usersRandomAccessFile.getFilePointer() < usersRandomAccessFile.length()){
            usersRandomAccessFile.seek(sit * RECORD_LENGTH);
            if (id == usersRandomAccessFile.readInt()){
                usersRandomAccessFile.seek((sit*RECORD_LENGTH) + NUM_OF_FRIEND_POS);
                int numOfFriend = usersRandomAccessFile.readInt();
                usersRandomAccessFile.seek((sit*RECORD_LENGTH) + (numOfFriend*FRIEND_LENGTH) + ID_LENGTH +
                        PHONENUMBER_LENGTH + USERNAME_LENGTH + PASSWORD_LENGTH + NUM_OF_FRIENDS_LENGTH); //pointer+nomOfFri*20
                usersRandomAccessFile.writeChars(setStringLength(name, 10));
                usersRandomAccessFile.seek((sit*RECORD_LENGTH) + NUM_OF_FRIEND_POS);
                usersRandomAccessFile.writeInt(numOfFriend+1);
                System.out.println("friend added successfully");
                return;
            }
            sit++;
        }

        usersRandomAccessFile.close();
        //exafe kardan tedad friend ha
        //exafe kardan friend ba name
    } /**completed-tested*/

    public ArrayList<String> findUserFriends(int userId) throws IOException {

        ArrayList<String> frindArr = new ArrayList<>();

        usersRandomAccessFile.seek(0);
        int sit = 0;
        while (usersRandomAccessFile.getFilePointer() < usersRandomAccessFile.length()){
            usersRandomAccessFile.seek(sit * RECORD_LENGTH);
            if (userId == usersRandomAccessFile.readInt()){
                usersRandomAccessFile.seek((sit*RECORD_LENGTH) + NUM_OF_FRIEND_POS);
                int len = usersRandomAccessFile.readInt();
                for (int i = 0; i <len ; i++) {
                    String st = readString(usersRandomAccessFile, 10);
                    if (!st.equals("!")){
                        frindArr.add(st);
                    }
                }
                return frindArr;
            }
            sit++;
        }

        usersRandomAccessFile.close();
        //go to the user with id = id
        //seek to friends
        //read and return an array of friend name
        return null;
    } /**completed-not tested*/





}
