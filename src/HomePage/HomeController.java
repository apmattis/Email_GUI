package HomePage;

import LogInPage.Main;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HomeController{
    public GridPane contactsPane;
    public Label messageStatus;
    public TextField recipientField;
    public TextField subjectField;
    public TextArea messageField;
    public Button sendButton;
    public Tab inboxTab;
    public Tab sentTab;
    public VBox inboxFileSelection;
    public VBox outboxFileSelection;
    public GridPane inboxPane;
    public TabPane tabPane;
    public GridPane outBoxPane;
    public MenuItem logout;
    public MenuItem deleteMessage;
    public Tab contactsTab;
    public Button addContactButton;
    public TextField newContactField;
    public Tab draftsTab;
    public GridPane draftsPane;
    public VBox draftsFileSelection;
    public TextField draftToAddressField;
    public TextField draftSubjectField;
    public AnchorPane draftsMessagePane;
    public TextArea draftMessageContents;

    private ListView<String> inboxList;
    private ListView<String> outboxList;
    private ListView<String> draftsList;
    private TextArea inboxMessageContents;
    private TextArea outboxMessageContents;
    private List<String> inboxMessageList = new ArrayList<>();
    private List<String> outboxMessageList = new ArrayList<>();
    private List<String> draftsMessageList = new ArrayList<>();
    private Messages inbox = new Messages();
    private Messages outbox = new Messages();
    private Messages drafts = new Messages();
    private int initInboxCount, initOutboxCount, newInboxCount = 0, newOutboxCount = 0;



    public void getMessages() {
        tabEventHandler();
    }

    @FXML
    private void tabEventHandler(){
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if(inboxTab == newTab && inboxMessageList.isEmpty()) {
                try {
                    Main.cm.sendStringData("INBX");
                    listInboxMail();

                    inboxMessageList = inbox.getMessageNames();
                    ObservableList<String> fileNames = FXCollections.observableArrayList(inboxMessageList);
                    inboxList = new ListView<>(fileNames);
                    inboxList.setOrientation(Orientation.VERTICAL);

                    inboxList.getSelectionModel().selectedItemProperty().addListener(this::inboxMessageChanged);
                    inboxMessageContents = new TextArea();

                    inboxFileSelection = new VBox();
                    inboxFileSelection.getChildren().addAll(inboxList);
                    inboxPane.add(inboxFileSelection, 0, 0);
                    inboxPane.add(inboxMessageContents, 1, 0);
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(sentTab == newTab && outboxMessageList.isEmpty()){
                try {
                    Main.cm.sendStringData("OTBX");
                    listOutboxMail();

                    outboxMessageList = outbox.getMessageNames();
                    ObservableList<String> fileNames = FXCollections.observableArrayList(outboxMessageList);
                    outboxList = new ListView<>(fileNames);
                    outboxList.setOrientation(Orientation.VERTICAL);

                    outboxList.getSelectionModel().selectedItemProperty().addListener(this::outboxMessageChanged);
                    outboxMessageContents = new TextArea();

                    outboxFileSelection = new VBox();
                    outboxFileSelection.getChildren().addAll(outboxList);
                    outBoxPane.add(outboxFileSelection, 0, 0);
                    outBoxPane.add(outboxMessageContents, 1, 0);
                }catch (IOException e) {
                    e.printStackTrace();
                }

            }else if(draftsTab == newTab && draftsMessageList.isEmpty()){
                try{
                    Main.cm.sendStringData("DRFT");
                    listDraftMail();

                    draftsMessageList = drafts.getMessageNames();
                    ObservableList<String> fileNames = FXCollections.observableArrayList(draftsMessageList);
                    draftsList = new ListView<>(fileNames);
                    draftsList.setOrientation(Orientation.VERTICAL);

                    draftsList.getSelectionModel().selectedItemProperty().addListener(this::draftsMessageChanged);
                    draftMessageContents = new TextArea();

                    draftsFileSelection = new VBox();
                    draftsFileSelection.getChildren().addAll(draftsList);
//                    draftsMessagePane.getChildren().addAll(draftToAddressField, draftSubjectField, draftMessageContents);
                    draftsPane.add(draftsFileSelection, 0, 0);
                    draftsPane.add(draftMessageContents, 1, 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void inboxMessageChanged(ObservableValue<? extends String> observable, String oldValue, String newValue){

        String newText = newValue == null ? "null" : newValue;
        inboxMessageContents.clear();
        StringBuilder message = inbox.getMessageMap().get(newText);
        inboxMessageContents.appendText(message.toString());
    }

    private void outboxMessageChanged(ObservableValue<? extends String> observable, String oldValue, String newValue){

        String newText = newValue == null ? "null" : newValue;
        outboxMessageContents.clear();
        StringBuilder message = outbox.getMessageMap().get(newText);
        outboxMessageContents.appendText(message.toString());
    }

    private void draftsMessageChanged(ObservableValue<? extends String> observable, String oldValue, String newValue){

        String newText = newValue == null ? "null" : newValue;
        draftMessageContents.clear();
        StringBuilder message = drafts.getMessageMap().get(newText);
        draftMessageContents.appendText(message.toString());
        
    }

    public void listInboxMail(){
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

    public void listDraftMail(){
        try{
            DataInputStream dis = new DataInputStream(new BufferedInputStream(Main.cm.getClient().getInputStream()));

            int numFiles = dis.readInt();
            for(int i = 0; i < numFiles; i++){
                String fName = dis.readUTF();
//                String toAddr = dis.readUTF();
//                String subject = dis.readUTF();
//                draftToAddressField.setText(toAddr);
//                draftSubjectField.setText(subject);
                if(!drafts.containsMessageName(fName)){
                    Message message = new Message(fName);
                    drafts.addMessage(message.getName());
                    String line;
                    int n;
                    byte[] buf = new byte[8192];

                    long fSize = dis.readLong();
                    //read file
                    while(fSize > 0 && (n = dis.read(buf, 0, (int)Math.min(buf.length, fSize))) != -1){
                        line = new String(buf);
                        message.setContents(new StringBuilder().append(line));
                        drafts.addMessageToMap(message.getName(), message.getContents());
                        fSize -= n;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listOutboxMail(){
        try{
            DataInputStream dis = new DataInputStream(new BufferedInputStream(Main.cm.getClient().getInputStream()));

            int numFiles = dis.readInt();
            for(int i = 0; i < numFiles; i++){
                String fName = dis.readUTF();
                if(!outbox.containsMessageName(fName)){
                    Message message = new Message(fName);
                    outbox.addMessage(message.getName());
                    String line;
                    int n;
                    byte[] buf = new byte[8192];

                    long fSize = dis.readLong();
                    //read file
                    while(fSize > 0 && (n = dis.read(buf, 0, (int)Math.min(buf.length, fSize))) != -1){
                        line = new String(buf);
                        message.setContents(new StringBuilder().append(line));
                        outbox.addMessageToMap(message.getName(), message.getContents());
                        fSize -= n;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(ActionEvent actionEvent) throws IOException {
        String recipient = recipientField.getText(),
                subject = subjectField.getText(),
                message = messageField.getText(),
                auth;
        try {
            Main.cm.sendStringData("DATA");
            Main.cm.flushOut();
            Main.cm.sendStringData(recipient);
            Main.cm.sendStringData(subject);
            Main.cm.sendStringData(message);
            Main.cm.flushOut();
            auth = Main.cm.receiveStringData();
            System.out.println(auth);
            if(!(auth.isEmpty()) && auth.contains("275")){
                messageStatus.setTextFill(Color.color(0, 1, 0));
                messageStatus.setText("Message Sent!");
                recipientField.clear();
                subjectField.clear();
                messageField.clear();
                newOutboxCount++;
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

    public void getContacts(){

    }

    public void logOut(ActionEvent actionEvent) {
        try {
            Main.cm.connectionReset();
            Main.resetScene("/LogInPage/Authenticate.fxml", "Log In");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteMessage(ActionEvent actionEvent) {
        
    }

    public void addContact(ActionEvent actionEvent) {
        ;
    }

    public void saveAsDraft(ActionEvent actionEvent) {
        try {
            String auth;
            Main.cm.sendStringData("SAVE");
            Main.cm.sendStringData(recipientField.getText());
            Main.cm.sendStringData(subjectField.getText());
            Main.cm.sendStringData(messageField.getText());
            auth = Main.cm.receiveStringData();
            if(auth.contains("280")){
                messageStatus.setTextFill(Color.color(0, 1, 0));
                messageStatus.setText("Message Saved as Draft.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}


