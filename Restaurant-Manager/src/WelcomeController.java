import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomeController {

  @FXML
  private void toTable(ActionEvent event) throws IOException {
    Parent root = FXMLLoader.load(getClass().getResource("table.fxml"));
    Scene menuView = new Scene(root);
    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
    window.setScene(menuView);
    window.show();
  }

  @FXML
  private void toInventory(ActionEvent event) throws IOException {
    Parent root = FXMLLoader.load(getClass().getResource("inventory.fxml"));
    Scene menuView = new Scene(root);
    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
    window.setScene(menuView);
    window.show();
  }

  @FXML
  private void toManaging(ActionEvent event) throws IOException {
    Parent root = FXMLLoader.load(getClass().getResource("managing.fxml"));
    Scene menuView = new Scene(root);
    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
    window.setScene(menuView);
    window.show();
  }

  @FXML
  private void toCook(ActionEvent event) throws IOException {
    Parent root = FXMLLoader.load(getClass().getResource("cook.fxml"));
    Scene menuView = new Scene(root);
    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
    window.setScene(menuView);
    window.show();
  }
}
