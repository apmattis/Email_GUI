package NewAccountPage;

import LogInPage.Main;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class NewAccountController {
    public TextField newUsername;
    public PasswordField newPass;
    public PasswordField confirmPass;
    public Label notificationLabel;
    private Stage newPrimaryStage = new Stage();

    public void createAccount(ActionEvent actionEvent) throws IOException {
        Main.cm.sendStringData("CREA");
        String auth = Main.cm.receiveStringData();
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
                Main.resetScene("/LogInPage/Authenticate.fxml", "Email");
            }else if(auth.contains("555")){
                notificationLabel.setTextFill(Color.color(1, 0, 0));
                notificationLabel.setText("Password does not meet requirements.");
                newPass.clear();
                confirmPass.clear();
            }else if(auth.contains("560")){
                notificationLabel.setTextFill(Color.color(1, 0, 0));
                notificationLabel.setText("Username taken.");
                newUsername.clear();
                newPass.clear();
                confirmPass.clear();
            }else{
                notificationLabel.setTextFill(Color.color(1, 0, 0));
                notificationLabel.setText("Passwords do not match.");
                newPass.clear();
                confirmPass.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeApp(ActionEvent actionEvent) {
        try {
            Main.cm.sendStringData("QUIT");
            System.exit(0);
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    public Stage getNewPrimaryStage() {
        return newPrimaryStage;
    }

    public void setNewPrimaryStage(Stage newPrimaryStage) {
        this.newPrimaryStage = newPrimaryStage;
    }
}
