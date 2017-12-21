package LogInPage;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class AuthenticateController {


    public TextField userName;
    private static String user;
    public PasswordField pass;
    public Label authLabel;
    public Button logInButton;
    public Hyperlink newUserLink;
    public Stage newStage;


    public void logIn(ActionEvent actionEvent) throws IOException {
        String user = userName.getText(), passWord = pass.getText();
        setUser(user);

        try {
            Main.cm.sendStringData("AUTH");
            Main.cm.flushOut();
            String auth = Main.cm.receiveStringData();
            System.out.println(auth);
            if(user.isEmpty() && passWord.isEmpty()){
                authLabel.setTextFill(Color.color(1,0,0));
                authLabel.setText("Please enter a username/password");
                Main.cm.connectionReset();
            }else{
                Main.cm.sendStringData(user);
                Main.cm.sendStringData(passWord);
                Main.cm.flushOut();
                auth = Main.cm.receiveStringData();
                System.out.println(auth);
                if(auth.contains("535") || auth.contains("335")){
                    userName.clear();
                    pass.clear();
                    authLabel.setTextFill(Color.color(1,0,0));
                    authLabel.setText("Username/Password is incorrect. Please try again.");
                }else{
                    setNewStage(new Stage());
                    Main.resetScene("/HomePage/HomePage.fxml", "Home");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void closeApp(ActionEvent actionEvent) {
        try {
            Main.cm.sendStringData("QUIT");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setNewStage(Stage stage){
        this.newStage = stage;
    }

    public Stage getNewStage(){
        return newStage;
    }

    public void newAccountWindow(ActionEvent actionEvent) {
        try {
            Main.resetScene("/NewAccountPage/NewAccount.fxml", "New Account");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}




