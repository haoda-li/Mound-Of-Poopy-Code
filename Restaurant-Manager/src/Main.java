import dataModel.Restaurant;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Main method initializes and runs the Application.
public class Main extends Application {

  static Restaurant restaurant = new Restaurant("menu.txt", "ingredients.txt");

  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root = FXMLLoader.load(getClass().getResource("welcome.fxml"));
    primaryStage.setTitle("0428");
    primaryStage.setScene(new Scene(root));
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
