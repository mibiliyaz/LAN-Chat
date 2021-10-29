import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.Scanner;
import java.io.*;
import java.net.*;

public class Client {
    static Scanner in = new Scanner(System.in);
    static DataOutputStream dos;
    static DataInputStream dis;
    static Socket s;
    static boss server;
    public static String username;
    public static int port = 7777;
    public static String secretCode = "46511231dsfdsfsd#@$#$#@^$%#@*$#^";

    //-------For File sharing------------
    static FileInputStream fis = null;
    static BufferedInputStream bis = null;
    static OutputStream os = null;
    static InputStream is = null;
    static FileOutputStream fos = null;
    static BufferedOutputStream bos = null;
    static String FILE_TO_RECEIVE;
    static int bytesRead;
    static int current = 0;
    //-------------------------------

    static JTextPane chatMessages = new JTextPane();
    static JScrollPane JPchatMessages = new JScrollPane(chatMessages);

    static String msgHistory = new String("");

    static JLabel helloUser;
    public static void main(final String args[]) throws IOException {
    	System.out.println("Client Started...\nLogin to connect");
    	
        JFrame LoginScreen = new JFrame("Login to LAN Chat & File Sharing");
        JFrame ChatScreen = new JFrame("LAN Chat & File Sharing");
        JFrame FileSharingScreen = new JFrame("Choose a file to send");

//---------------------- CHAT SCREEN ----------------------------------
        ChatScreen.setSize(400,650);
        ChatScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ChatScreen.setLayout(new GridBagLayout());

        helloUser = new JLabel("Hello and Welcome !");
        ChatScreen.add(helloUser, new GridBagConstraints(0,0,1,1,3,1,GridBagConstraints.CENTER, GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));

        JButton logOutButton = new JButton("Logout");
        ChatScreen.add(logOutButton, new GridBagConstraints(2,0,1,1,.25,1,GridBagConstraints.CENTER, GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
        logOutButton.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent logOutButtonClick) {
                                        try {
                                            ChatScreen.setVisible(false);
                                            System.exit(0);
                                        } catch(Exception e){ System.out.println("logout failed. Press Ctrl+C to exit.."); }
                                    }
                                });

        chatMessages.setEditable(false);
        ChatScreen.add(JPchatMessages, new GridBagConstraints(0,1,3,1,1.0,100.0,GridBagConstraints.CENTER, GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));

        JTextField message = new JTextField(20);
        ChatScreen.add(message, new GridBagConstraints(0,2,1,1,.5,1,GridBagConstraints.CENTER, GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));

        JButton send = new JButton("Send");
        ChatScreen.add(send, new GridBagConstraints(1,2,1,1,.25,.25,GridBagConstraints.CENTER, GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
        send.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent sendButtonClick) {
                                        String msg = message.getText();
                                        message.setText(null);
                                        try {
                                            if(!msg.equals("")) dos.writeUTF(username + " : " + msg);
                                        } catch(IOException e){}
                                    }
                                });

        JButton sendFile = new JButton("Upload File");
        ChatScreen.add(sendFile, new GridBagConstraints(2,2,1,1,.25,.25,GridBagConstraints.CENTER, GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
        sendFile.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent sendFileButtonClick) {
                                        FileSharingScreen.setVisible(true);
                                    }
                                });
//----------------------------------------------------------------

//---------------------- LOGIN SCREEN ----------------------------
        LoginScreen.setLayout(new GridBagLayout());

        LoginScreen.setSize(400,650);
        LoginScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //----For bg image-----------
        JLabel background=new JLabel(new ImageIcon("logo.png"),JLabel.CENTER);
        LoginScreen.add(background, new GridBagConstraints(0,0,1,1,2.0,1.0,GridBagConstraints.CENTER, GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
        //---------------------------

        //------Label-----------------
        JLabel enter = new JLabel("Enter your name");
        LoginScreen.add(enter, new GridBagConstraints(0,2,1,1,1.0,1.0,GridBagConstraints.CENTER, GridBagConstraints.NONE,new Insets(0,0,0,0),0,0));
        enter.setFont(new Font(enter.getFont().getName(), Font.BOLD, 16));
        //----------------------------

        //------Username Textfield-------
        JTextField usernameTextArea = new JTextField(12);
        LoginScreen.add(usernameTextArea, new GridBagConstraints(0,3,1,1,1.0,1.0,GridBagConstraints.CENTER, GridBagConstraints.NONE,new Insets(0,0,0,0),0,0));
        usernameTextArea.setFont(new Font(usernameTextArea.getFont().getName(), Font.BOLD, 16));
        //----------------------------

        //------Label-----------------
        JLabel enterPort = new JLabel("Enter Port Number");
        LoginScreen.add(enterPort, new GridBagConstraints(0,7,1,1,1.0,1.0,GridBagConstraints.CENTER, GridBagConstraints.NONE,new Insets(0,0,0,0),0,0));
        enterPort.setFont(new Font(enterPort.getFont().getName(), Font.BOLD, 12));
        enterPort.setVisible(false);
        //----------------------------            

        //------PortNumber Textfield-------
        JTextField portTextArea = new JTextField(4);
        LoginScreen.add(portTextArea, new GridBagConstraints(0,8,1,1,1.0,1.0,GridBagConstraints.CENTER, GridBagConstraints.NONE,new Insets(0,0,0,0),0,0));
        portTextArea.setVisible(false);
        //----------------------------

        //------PrivateChat Option----
        JRadioButton privateChat = new JRadioButton("Private Chat");
        LoginScreen.add(privateChat, new GridBagConstraints(0,5,1,1,1.0,1.0,GridBagConstraints.CENTER, GridBagConstraints.NONE,new Insets(0,0,0,0),0,0));
        privateChat.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent ChangeEvent) {
                                        try {
                                            if(privateChat.isSelected()){
                                                enterPort.setVisible(true);
                                                portTextArea.setVisible(true);
                                            }
                                            else{
                                                enterPort.setVisible(false);
                                                portTextArea.setVisible(false);
                                            }
                                        }catch(Exception e) { System.out.println("Server unavailable to connect. Press Ctrl+C to exit.."); }
                                    }
                                });

        //-------Login Button-------------
        JButton login = new JButton("Join chat");
        LoginScreen.add(login, new GridBagConstraints(0,10,1,1,1.0,1.0,GridBagConstraints.CENTER, GridBagConstraints.NONE,new Insets(0,0,0,0),0,0));
        login.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent buttonClick) {
                                        try {
                                            username = usernameTextArea.getText();
                                            if(!portTextArea.getText().equals("")) port = Integer.parseInt(portTextArea.getText());
                                            LoginScreen.setVisible(false);

                                            s = new Socket("localhost", port);
                                            dos = new DataOutputStream(s.getOutputStream());
                                            dis = new DataInputStream(s.getInputStream());

                                            server = new boss(dis);
                                            Thread t = new Thread(server);
                                            t.start();
                                            System.out.println("Connected to server...");
                                            
                                            ChatScreen.setVisible(true);
                                        } catch(Exception e) { System.out.println("Server unavailable to connect."); }
                                    }
                                });
        //-------------------------------
        LoginScreen.setVisible(true);
//----------------------------------------------------------------

//---------------------- FILE SELECTOR ---------------------------------
        FileSharingScreen.setSize(500,700);
        FileSharingScreen.setLayout(new GridBagLayout());

        JFileChooser fileSelector = new JFileChooser();
        FileSharingScreen.add(fileSelector, new GridBagConstraints(1,0,1,1,.25,.25,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
        fileSelector.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent fileChooserEvent) {
                                        String command = fileChooserEvent.getActionCommand();
                                        if(command.equals(JFileChooser.APPROVE_SELECTION)) {
                                            File fileToBeSent = fileSelector.getSelectedFile();
                                            FileSharingScreen.setVisible(false);

                                            FILE_TO_RECEIVE = fileToBeSent.getName();
                                            try {
                                                byte [] fileByteArray  = new byte [(int)fileToBeSent.length()];
                                                dos.writeUTF(username+" : Uploaded \""+FILE_TO_RECEIVE+"\"");
                                                fis = new FileInputStream(fileToBeSent);
                                                bis = new BufferedInputStream(fis);
                                                bis.read(fileByteArray,0,fileByteArray.length);
                                                os = s.getOutputStream();
                                                dos.flush();
                                                os.flush();
                                                dos.flush();
                                                dos.writeUTF(secretCode+"File"+FILE_TO_RECEIVE);
                                                os.write(fileByteArray,0,fileByteArray.length);
                                                os.flush();
                                                System.out.println("File Successfully Sent.");
                                            } catch(Exception e){}
                                        }
                                    }
                                });
//----------------------------------------------------------------
        Runtime.getRuntime().addShutdownHook(new Thread() {
                                                    public void run() {
                                                        try {
                                                            dos.writeUTF("> "+username+" logged out...");
                                                            dos.writeUTF(secretCode+"Logout");
                                                            System.out.println("Logged out....");
                                                        } catch(Exception e) {}
                                                    }
                                                });
    }

    public static void setUsername(String str) {
        if(username.equals("")) username = str;
        helloUser.setText("Hello "+username+". Welcome !");
        try {
            dos.writeUTF("> "+username+" logged in...");
        } catch(IOException e){}
    }

    public static void updateMessageArea(String msg) {
        msgHistory = msgHistory + msg + "\n";
        chatMessages.setText(msgHistory);
    }

    public static void reconnect() {
        try {
            s.close();
            s = new Socket("localhost", port);
            dos = new DataOutputStream(s.getOutputStream());
            dis = new DataInputStream(s.getInputStream());
            server = new boss(dis);
            Thread newConnection = new Thread(server);
            newConnection.start();
        } catch(Exception e) { System.out.println("Exception caught in reconnect()."); }
    }

    public static void receiveFile() {
        String workingDir = System.getProperty("user.dir");
        String FILE_TO_RECEIVED =workingDir +"\\Files\\"+FILE_TO_RECEIVE;
        File myFile = new File(FILE_TO_RECEIVED);
        try {
            current = (int)myFile.length();
            System.out.println("File " + FILE_TO_RECEIVED+ " downloaded (" + current + " bytes read)");
        } catch(Exception e) { System.out.println("Exception caught in receiveFile()."); }
    }
}

class boss extends Thread {
    DataInputStream disServer;
    public boss(DataInputStream z) {
        disServer = z;
    }

    public void run() {
        while(true) {
            try {
                String str = disServer.readUTF();
                if(str.length()>30 && str.substring(0,36).equals(Client.secretCode+"File")) {
                    Client.FILE_TO_RECEIVE = str.substring(36);
                    Client.receiveFile();
                }
                else if (str.length()>30 && str.substring(0,36).equals(Client.secretCode+"User"))
                    Client.setUsername(str.substring(32));
                else 
                    Client.updateMessageArea(str);
            } catch(Exception e) {
                System.out.println("Exception in run method. Reconnecting....");
                Client.reconnect();
                break;
            }
        }
    }
}