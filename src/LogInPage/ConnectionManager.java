package LogInPage;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ConnectionManager {

    private static final String serverName = "192.168.1.4";
    private static final int portNum = 9099;
    private Socket client;
    private static DataOutputStream out;
    private static DataInputStream in;
    private static String serverCode;


//    Thread thread = new Thread(new Runnable() {
//        @Override
//        public void run() {
//            try {
//                while(true){
//                    setServerCode(in.readUTF());
//                    System.out.println("Server code changed: " + serverCode);
//                    Thread.yield();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    });
//
//    public Thread thread2 = new Thread(new Runnable() {
//        @Override
//        public void run() {
//            receiveFile();
//            Thread.yield();
//        }
//    });

    public void run(){
        connect();
    }

    private void connect(){
        try{
            System.out.println("Trying to connect to " + serverName);
            client = new Socket(serverName, portNum);

            if (client.isConnected()) {
                System.out.println("Connection successful!");
            }

            OutputStream serverOut = client.getOutputStream();
            out = new DataOutputStream(serverOut);

            InputStream serverIn = client.getInputStream();
            in = new DataInputStream(serverIn);

            out.writeUTF("HELO");
            in.readUTF();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveFile() {
        try {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(client.getInputStream()));

            int numFiles = dis.readInt();
            Path path = Paths.get("out/"+ AuthenticateController.getUser() + "/inbox/");
            Files.createDirectories(path);
            String fName = null;
            int n;
            byte[] buf = new byte[8192];

            //outer loop, executes one for each file
            for (int i = 0; i < numFiles; i++) {
                fName = dis.readUTF();
                File file = new File(path +"/"+ fName);
                FileOutputStream fos = new FileOutputStream(file);
                long fSize = dis.readLong();
                System.out.println("Receiving file: " + fName + " of size: " + fSize);

                //read file
                while(fSize > 0 && (n = dis.read(buf, 0, (int)Math.min(buf.length, fSize))) != -1){
                    fos.write(buf,0,n);
                    fSize -= n;
                    fos.flush();
                }
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connectionReset(){
        try {
            client.close();
            connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendStringData(String str) throws IOException {
        out.writeUTF(str);
    }
    public void sendIntData(int num) throws IOException {
        out.writeInt(num);
    }
    public String receiveStringData() throws IOException {
        return in.readUTF();
    }
    public void flushOut() throws IOException {
        out.flush();
    }

    private void setServerCode(String code){
        serverCode = code;
    }

    public static String getServerCode(){
        return serverCode;
    }



}
