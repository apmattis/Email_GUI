package LogInPage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

public class Main extends Application {

    public static ConnectionManager cm = new ConnectionManager();
    private Stage stage;
    private static Stage primaryStage;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        Main.primaryStage = primaryStage;
    }


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Authenticate.fxml"));
        setStage(primaryStage);
        getStage().setTitle("Email");
        getStage().setScene(new Scene(root, 500, 350));
        getStage().show();
        setPrimaryStage(primaryStage);
        cm.run();
    }

    public static void resetScene(String fxml, String title){
        try{
            FXMLLoader loader = new FXMLLoader();
            InputStream in = Main.class.getResourceAsStream(fxml);
            loader.setBuilderFactory(new JavaFXBuilderFactory());
            loader.setLocation(Main.class.getResource(fxml));
            Pane page;
            page = loader.load(in);
            getPrimaryStage().setTitle(title);
            getPrimaryStage().setScene(new Scene(page, 850, 450));
            getPrimaryStage().show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) { launch(args);  }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
