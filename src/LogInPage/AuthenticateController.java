package LogInPage;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AuthenticateController {

    public Pane newAccountPane;
    public TextField userName;
    private static String user;
    public PasswordField pass;
    public Label authLabel;
    public TextField newUsername;
    public PasswordField newPass;
    public PasswordField confirmPass;
    public Label notificationLabel;
    public Button logInButton;
    private String auth = null;


    public void logIn(ActionEvent actionEvent) throws IOException {
        String user = userName.getText(), passWord = pass.getText();
        setUser(user);

        try {
            Main.cm.sendStringData("AUTH");
            Main.cm.flushOut();
            auth = Main.cm.receiveStringData();
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
                    authLabel.setTextFill(Color.color(0,1,0));
                    authLabel.setText("Welcome back " + user + "!");
                    Main.resetScene("/HomePage/HomePage.fxml", "Home");
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createAccount(ActionEvent actionEvent) throws IOException {
        Main.cm.sendStringData("CREATE");
        auth = Main.cm.receiveStringData();
        System.out.println(auth);
        String newUser = newUsername.getText(),
                newPassword = newPass.getText(),
                confirmNewPassword = confirmPass.getText();

        try {
            Main.cm.sendStringData(newUser);
            Main.cm.sendStringData(newPassword);
            Main.cm.sendStringData(confirmNewPassword);
            auth = Main.cm.receiveStringData();
            System.out.println(auth);
            if (auth.contains("337")) {
                notificationLabel.setTextFill(Color.color(0, 1, 0));
                notificationLabel.setText("New account created!");
                newUsername.clear();
                newPass.clear();
                confirmPass.clear();
            }else {
                notificationLabel.setTextFill(Color.color(1, 0, 0));
                notificationLabel.setText("PASSWORDS DO NOT MATCH");
                newPass.clear();
                confirmPass.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}




