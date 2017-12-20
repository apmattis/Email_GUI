package HomePage;

import LogInPage.AuthenticateController;
import LogInPage.Main;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HomeController {

    public GridPane contactsPane;
    public Label messageStatus;
    public TextField toAddrField;
    public TextField subjectField;
    public TextArea messageField;
    public Button sendButton;
    public Tab inboxTab;
    public Tab sentTab;
    public VBox fileSelection;
    public GridPane inboxPane;
    public TabPane tabPane;

    private ListView<String> inboxList;
    private TextArea messageContents;
    private String path = String.format("db/%s/inbox/", AuthenticateController.getUser());
    private List<String> messageList = new ArrayList<>();
    private StringBuilder message = new StringBuilder();
    private File[] files = new File(path).listFiles();
    private Messages inbox = new Messages();


    public void getMessages() {
        tabEventHandler();
    }

    public void getContacts(){

    }

    private void tabEventHandler(){
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if(inboxTab == newTab && messageList.isEmpty()) {
                try {
                    Main.cm.sendStringData("READ");
                    listMail();

                    messageList = inbox.getMessageNames();
                    ObservableList<String> fileNames = FXCollections.observableArrayList(messageList);
                    inboxList = new ListView<>(fileNames);
                    inboxList.setOrientation(Orientation.VERTICAL);

                    inboxList.getSelectionModel().selectedItemProperty().addListener(this::messageChanged);
                    messageContents = new TextArea();

                    fileSelection = new VBox();
                    fileSelection.getChildren().addAll(inboxList);
                    inboxPane.add(fileSelection, 0, 0);
                    inboxPane.add(messageContents, 1, 0);
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void messageChanged(ObservableValue<? extends String> observable, String oldValue, String newValue){

        String newText = newValue == null ? "null" : newValue;
        messageContents.clear();
        message = inbox.getMessageMap().get(newText);
        messageContents.appendText(message.toString());
    }

    public void listMail(){
        try{
            DataInputStream dis = new DataInputStream(new BufferedInputStream(Main.cm.getClient().getInputStream()));

            int numFiles = dis.readInt();
            for(int i = 0; i < numFiles; i++){
                String fName = dis.readUTF();
                if(!inbox.containsMessageName(fName)){
                    Message message = new Message(fName);
                    inbox.addMessage(message.getName());
                    String line;
                    int n;
                    byte[] buf = new byte[8192];

                    long fSize = dis.readLong();
                    //read file
                    while(fSize > 0 && (n = dis.read(buf, 0, (int)Math.min(buf.length, fSize))) != -1){
                        line = new String(buf);
                        message.setContents(new StringBuilder().append(line));
                        inbox.addMessageToMap(message.getName(), message.getContents());
                        fSize -= n;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public StringBuilder readMail(String path){

        try{
            Main.cm.sendStringData(path);
            String line;
            int n;
            byte[] buf = new byte[8192];

            DataInputStream dis = new DataInputStream(new BufferedInputStream(Main.cm.getClient().getInputStream()));
            long fSize = dis.readLong();
            //read file
            while(fSize > 0 && (n = dis.read(buf, 0, (int)Math.min(buf.length, fSize))) != -1){
                line = new String(buf);
                inbox.setMessageContents(message.append(line));
                inbox.addMessageToMap(inbox.getMessage(path), inbox.getMessageContents());
                fSize -= n;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return inbox.getMessageContents();

    }

    public void sendMessage(ActionEvent actionEvent) throws IOException {
        String toAddr = toAddrField.getText(),
                subject = subjectField.getText(),
                message = messageField.getText(),
                auth;
        try {
            Main.cm.sendStringData("DATA");
            Main.cm.flushOut();
            Main.cm.sendStringData(toAddr);
            Main.cm.sendStringData(subject);
            Main.cm.sendStringData(message);
            Main.cm.flushOut();
            auth = Main.cm.receiveStringData();
            System.out.println(auth);
            if(!(auth.isEmpty()) && auth.contains("275")){
                messageStatus.setTextFill(Color.color(0, 1, 0));
                messageStatus.setText("Message Sent!");
                toAddrField.clear();
                subjectField.clear();
                messageField.clear();
            }else if(auth.contains("266")){
                messageStatus.setTextFill(Color.color(1,0, 0));
                messageStatus.setText("Subject is empty.");
            }else if(auth.contains("267")){
                messageStatus.setTextFill(Color.color(1,0, 0));
                messageStatus.setText("Message is empty.");
            }else if (auth.contains("550")){
                messageStatus.setTextFill(Color.color(1,0, 0));
                messageStatus.setText("Send Failed: Recipient does not exist.");
            }else if(auth.contains("500") || !(auth.isEmpty())){
                messageStatus.setTextFill(Color.color(1,0, 0));
                messageStatus.setText("Send Failed: Something went wrong.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeApp(ActionEvent actionEvent) {
        try {
            Main.cm.sendStringData("QUIT");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


