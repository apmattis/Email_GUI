package LogInPage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.InputStream;

public class Main extends Application {

    public static ConnectionManager cm = new ConnectionManager();
    private Stage stage;
    private static Stage newStage;

    private void setNewStage(Stage st){
        newStage = st;
    }



    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Authenticate.fxml"));
        stage = primaryStage;
        stage.setTitle("Email");
        stage.setScene(new Scene(root, 850, 450));
        stage.show();
        setNewStage(stage);
        cm.run();
    }

    public static void resetScene(String fxml, String title) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        InputStream in = Main.class.getResourceAsStream(fxml);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(Main.class.getResource(fxml));
        Pane page;
        page = loader.load(in);
        newStage.setTitle(title);
        newStage.setScene(new Scene(page, 850, 450));
        newStage.show();
    }





    public static void main(String[] args) { launch(args);  }
}
