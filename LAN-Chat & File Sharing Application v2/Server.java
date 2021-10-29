import java.util.Scanner;
import java.io.*;
import java.net.*;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;

public class Server {
    static ServerSocket ss;
    static DataInputStream dis;
    static DataOutputStream dos;
    static Scanner in = new Scanner(System.in);
    public static int limit = 10;
    public static user users[] = new user[limit];
    public static int totalClientsOnline=0;
    public String FILE_TO_RECEIVE;
    static int port;

    public static void main(String args[]) throws Exception {
        try {
            System.out.print("Private chat? [y/n]: ");
            char wc = in.next().charAt(0);
            if(wc == 'n' || wc == 'N') port = 7777;
            else { 
                System.out.print("Port Number: ");
                port = in.nextInt(); 
            }
            ss = new ServerSocket(port);
            System.out.println("Server Started...");
            for(int i=0;i<limit;i++) {
                users[i] = new user(i+1,ss.accept());
            }
        } catch(Exception e) { System.out.println("Exception caught in main due to user connection loss..."); }
    }

    public void MessageHistory(String chat) {
        try {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            FileWriter fw = new FileWriter("ChatHistory//"+df.format(now)+" Chat History.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(dtf.format(now)+" > "+chat+"\n");
            bw.close();
        }catch(Exception e){System.out.println(e);}
    }

    public void sendMessageToAll(String msg) {
        for(int c=0;c<totalClientsOnline;c++) {
            try {
                users[c].sendMessage(msg);
            } catch(Exception e){}
        }
    }

    public void receiveAndSendFileToAll(Socket uSocket, int userID) {
        String workingDir = System.getProperty("user.dir");
        String FILE_TO_RECEIVED =workingDir +"\\Files\\"+FILE_TO_RECEIVE;
        int FILE_SIZE = 131000;
        int bytesRead;
        int current = 0;

        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        byte [] mybytearray  = new byte [FILE_SIZE];
        
        try {
            InputStream is = uSocket.getInputStream();
            fos = new FileOutputStream(FILE_TO_RECEIVED);
            bos = new BufferedOutputStream(fos);
            bytesRead = is.read(mybytearray,0,mybytearray.length);
            current = bytesRead;
            bos.write(mybytearray, 0 , current);
            bos.flush();
            bos.close();
            System.out.println("File " + FILE_TO_RECEIVED+ " downloaded (" + current + " bytes read)");
            for(int c=0;c<totalClientsOnline;c++) {
                if(c != userID-1) users[c].sendFile(FILE_TO_RECEIVE);
            }
        } catch(Exception e) { System.out.println("Exception caught in receiveFile()."); }
    }
}

class user extends Thread {
    Server tirth = new Server();
    int userID;
    public Socket userSocket;
    public DataInputStream userDIS;
    public DataOutputStream userDOS;
    public Thread t;
    OutputStream os;
    public String secretCode = "46511231dsfdsfsd#@$#$#@^$%#@*$#^";

    public user(int id,Socket a) {
        try {
            userID = id;
            userSocket = a;
            userDIS = new DataInputStream(userSocket.getInputStream());
            userDOS = new DataOutputStream(userSocket.getOutputStream());
            tirth.totalClientsOnline++;
            System.out.println("Client connected with id"+userID);
            userDOS.writeUTF(secretCode+"User"+userID);
            t = new Thread(this);
            t.start();
        } catch(Exception e) { System.out.println("Exception caught in constructor."); }
    }

    public void run() {
        String message;
        while(true) {
            try {
                message = userDIS.readUTF();
                if(message.equals(secretCode+"Logout")){
                    System.out.println("id"+userID+" Client disconnected.");
                }
                else if(message.length()>30 && message.substring(0,36).equals(secretCode+"File")) {
                    tirth.FILE_TO_RECEIVE = message.substring(36);
                    tirth.receiveAndSendFileToAll(userSocket, userID);
                }
                else{
                    tirth.sendMessageToAll(message);
                    tirth.MessageHistory(message);
                }
            } catch(Exception e){}
        }
    }

    public void sendMessage(String s) {
        try {
            userDOS.writeUTF(s);
        } catch(Exception e){}
    }

    public void sendFile(String filename) {
        try {
            userDOS.writeUTF(secretCode+"File"+filename);
        } catch(Exception e) { System.out.println("Exception in sendFile() method."); }
    }
}