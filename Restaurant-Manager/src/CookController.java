import dataModel.CookManager;
import dataModel.Dish;
import dataModel.Order;
import dataModel.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

/** Controller of the view for the cook. */
public class CookController {

  @FXML private TableView<Order> cookQueueTableView;
  @FXML private TableView<Order> deliverQueueTableView;
  @FXML private TableColumn<Order, String> cTime, cStatus;
  @FXML private TableColumn<Order, String> dTime, dTable;
  @FXML private Text errorInfo;
  @FXML private Text cookOrderDetail, deliverOrderDetail;
  private CookManager cookManager = Main.restaurant.getCookManager();

  // method to the Main window.
  @FXML
  private void toMain(ActionEvent event) throws IOException {
    Parent root = FXMLLoader.load(getClass().getResource("welcome.fxml"));
    Scene menuView = new Scene(root);
    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
    window.setScene(menuView);
    window.show();
  }

  // initializes this window.
  @FXML
  private void initialize() {
    cookQueueTableView.setItems(cookManager.getCookQueue());
    deliverQueueTableView.setItems(cookManager.getDeliveryQueue());
    cTime.setCellValueFactory(new PropertyValueFactory<>("dateTimeString"));
    cStatus.setCellValueFactory(new PropertyValueFactory<>("statusString"));
    dTime.setCellValueFactory(new PropertyValueFactory<>("dateTimeString"));
    dTable.setCellValueFactory(new PropertyValueFactory<>("table"));
    cookQueueTableView
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (obs, oldSelection, newSelection) -> {
              if (newSelection != null) {
                errorInfo.setText("");
                cookOrderDetail.setText(newSelection.printDishes());
              } else {
                cookOrderDetail.setText("");
              }
            });
    deliverQueueTableView
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (obs, oldSelection, newSelection) -> {
              if (newSelection != null) {
                errorInfo.setText("");
                deliverOrderDetail.setText(newSelection.printDishes());
              } else {
                deliverOrderDetail.setText("");
              }
            });
  }

  // sets the status text of the selected order for cooking.
  @FXML
  private void setAcknowledge() {
    errorInfo.setText("");
    Order selected = cookQueueTableView.getSelectionModel().getSelectedItem();
    if (selected == null) {
      errorInfo.setText("Nothing Selected");
      return;
    }
    if (!cookManager.confirmCookTask(selected)) {
      errorInfo.setText("Acknowledge the earlier Orders first");
    }
    cookManager.confirmCookTask(selected);
    cookOrderDetail.setText(selected.printDishes());
    cStatus.setVisible(false);
    cStatus.setVisible(true);
  }

  // sets the status text of the selected order for completing.
  @FXML
  private void setComplete() {
    errorInfo.setText("");
    Order selected = cookQueueTableView.getSelectionModel().getSelectedItem();
    if (selected == null) {
      errorInfo.setText("Nothing Selected");
      return;
    } else if (selected.getStatus() != State.COOKING) {
      errorInfo.setText("Acknowledge this order first.");
      return;
    }
    cookManager.finishCookTask(selected);
  }

  // sets the status text of the selected order for delivering.
  @FXML
  private void setDeliver() {
    errorInfo.setText("");
    Order selected = deliverQueueTableView.getSelectionModel().getSelectedItem();
    if (selected == null) {
      errorInfo.setText("Nothing Selected");
      return;
    }
    cookManager.deliverOrder(selected);
  }

  // sets the status text of the selected order for canceling.
  @FXML
  private void setCancel() {
    errorInfo.setText("");
    Order selected = deliverQueueTableView.getSelectionModel().getSelectedItem();
    if (selected == null) {
      errorInfo.setText("Nothing Selected");
      return;
    }
    cookManager.getDeliveryQueue().remove(selected);
    for (Dish d : selected.getDishes()) {
      selected.getTable().getBill().removeDish(d);
    }
  }
}
