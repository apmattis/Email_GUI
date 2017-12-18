package HomePage;

import LogInPage.AuthenticateController;
import LogInPage.Main;
import javafx.beans.value.ChangeListener;
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
import java.util.Arrays;
import java.util.List;

public class HomeController {


    private String path = String.format("out/%s/inbox", AuthenticateController.getUser());
    private List<String> fList = new ArrayList<>();
    private File[] files = new File(path).listFiles();

    public Label messageStatus;
    public TextField toAddrField;
    public TextField subjectField;
    public TextArea messageField;
    public Button sendButton;
    public Tab inboxTab;
    public ListView<String> inboxList;
    private VBox fileSelection;
    public GridPane inboxPane;
    public TabPane tabPane;


    public void getMessages() {
        tabEventHandler();
    }

    public void getContacts(){

    }

    private void tabEventHandler(){
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if(inboxTab == newTab && fList.isEmpty()) {
                try {
                    Main.cm.sendStringData("GET");
                    Main.cm.receiveFile();




                    for(File file : files){
                        fList.add(file.getName());
                    }
                    ObservableList<String> fileNames = FXCollections.observableArrayList(fList);
                    inboxList = new ListView<>(fileNames);
                    inboxList.setOrientation(Orientation.VERTICAL);

                    inboxList.getSelectionModel().selectedItemProperty().addListener(this::messageChanged);

                    fileSelection = new VBox();
                    fileSelection.getChildren().addAll(inboxList);
                    inboxPane.add(fileSelection, 0, 0);
                    inboxPane.add(messageField, 1, 0);
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void messageChanged(ObservableValue<? extends String> observable, String oldValue, String newValue)

    {


        //String oldText = oldValue == null ? "null" : oldValue.toString();

        String newText = newValue == null ? "null" : newValue;
        messageField.clear();

        try(BufferedReader br = new BufferedReader(new FileReader(String.format("%s/%s", path, newText)))) {
            String line;
            while((line = br.readLine()) != null){
                messageField.appendText(line);
                messageField.appendText("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public void sendMessage(ActionEvent actionEvent) {
        try {
            Main.cm.sendStringData("DATA");
            String toAddr = toAddrField.getText(),
                    subject = subjectField.getText(),
                    message = messageField.getText(),
                    auth;
            Main.cm.sendStringData(toAddr);
            Main.cm.sendStringData(subject);
            Main.cm.sendStringData(message);
            Main.cm.flushOut();
            auth = Main.cm.receiveStringData();
            System.out.println(auth);
            if(auth.contains("275")){
                toAddrField.clear();
                subjectField.clear();
                messageField.clear();
                messageStatus.setTextFill(Color.color(0, 1, 0));
                messageStatus.setText("Message Sent!");
            }else if (auth.contains("550")){
                messageStatus.setTextFill(Color.color(1,0, 0));
                messageStatus.setText("Send Failed: Recipient does not exist.");
                auth = Main.cm.receiveStringData();
                System.out.println(auth);
            }else if(auth.contains("265")){
                messageStatus.setTextFill(Color.color(1,0, 0));
                messageStatus.setText("Send Failed: Something went wrong.");
                auth = Main.cm.receiveStringData();
                System.out.println(auth);
            }
            auth = Main.cm.receiveStringData();
            System.out.println(auth);

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


