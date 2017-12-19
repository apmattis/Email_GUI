package HomePage;

import LogInPage.AuthenticateController;
import LogInPage.ConnectionManager;
import LogInPage.Main;
import javafx.beans.value.ChangeListener;
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

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
    private List<String> fList = new ArrayList<>();
    private StringBuilder message = new StringBuilder();
    private File[] files = new File(path).listFiles();


    public void getMessages() {
        tabEventHandler();
    }

    public void getContacts(){

    }

    private void tabEventHandler(){
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if(inboxTab == newTab && fList.isEmpty()) {
                try {
                    Main.cm.sendStringData("FETCH");
                    fList = Main.cm.listMail();
                    ObservableList<String> fileNames = FXCollections.observableArrayList(fList);
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
        try {
            Main.cm.sendStringData("READ");
            message = Main.cm.readMail(String.format("%s%s", path, newText));
            messageContents.appendText(message.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
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


