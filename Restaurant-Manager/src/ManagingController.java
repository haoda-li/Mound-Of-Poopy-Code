import dataModel.BillLog;
import dataModel.IngredientLog;
import dataModel.LogManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

/** Controller of the view for the managing. */
public class ManagingController {

  @FXML private TableView<IngredientLog> ingredientLogTV;
  @FXML private TableView<BillLog> orderLogTV;
  @FXML private TableColumn<IngredientLog, String> ingreTimeTC, ingreNameTC, ingreNumberTC;
  @FXML private TableColumn<BillLog, String> orderTimeTC, orderIDTC, tableIDTC;
  @FXML private Text orderDetail;
  @FXML private DatePicker timePicker1, timePicker2;
  private LogManager logManager = Main.restaurant.getLogManager();

  // initializes the window.
  @FXML
  private void initialize() {
    ingredientLogTV.setItems(logManager.getIngredientLogs());
    ingreTimeTC.setCellValueFactory(new PropertyValueFactory<>("time"));
    ingreNameTC.setCellValueFactory(new PropertyValueFactory<>("ingredient"));
    ingreNumberTC.setCellValueFactory(new PropertyValueFactory<>("number"));
    orderLogTV.setItems(logManager.getBillLogs());
    orderTimeTC.setCellValueFactory(new PropertyValueFactory<>("time"));
    orderIDTC.setCellValueFactory(new PropertyValueFactory<>("billID"));
    tableIDTC.setCellValueFactory(new PropertyValueFactory<>("tableID"));
    orderLogTV
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (obs, oldSelection, newSelection) -> {
              if (newSelection != null) {
                orderDetail.setText(newSelection.printActions());
              }
            });
  }

  // button to the stats window.
  @FXML
  private void toStats(ActionEvent event) throws IOException {
    Parent root = FXMLLoader.load(getClass().getResource("stats.fxml"));
    Scene menuView = new Scene(root);
    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
    window.setScene(menuView);
    window.show();
  }

  // button to the recipeingreModifier window.
  @FXML
  private void toModify(ActionEvent event) throws IOException {
    Parent root = FXMLLoader.load(getClass().getResource("recipeIngreModifier.fxml"));
    Scene menuView = new Scene(root);
    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
    window.setScene(menuView);
    window.show();
  }

  // button to the main window.
  @FXML
  private void toMain(ActionEvent event) throws IOException {
    Parent root = FXMLLoader.load(getClass().getResource("welcome.fxml"));
    Scene menuView = new Scene(root);
    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
    window.setScene(menuView);
    window.show();
  }

  // sets the items of the TableView based on the search result.
  @FXML
  private void search() {
    LocalDate date1 = timePicker1.getValue();
    LocalDate date2 = timePicker2.getValue();
    if (date1 == null && date2 == null) {
      orderLogTV.setItems(logManager.getBillLogs());
      ingredientLogTV.setItems(logManager.getIngredientLogs());
    } else if (date1 != null && date2 == null) {
      orderLogTV.setItems(logManager.getBillLogsAfter(date1));
      ingredientLogTV.setItems(logManager.getIngredientLogsAfter(date1));
    } else if (date1 == null) {
      orderLogTV.setItems(logManager.getBillLogsBefore(date2));
      ingredientLogTV.setItems(logManager.getIngredientLogsBefore(date2));
    } else {
      orderLogTV.setItems(logManager.getBillLogs(date1, date2));
      ingredientLogTV.setItems(logManager.getIngredientLogs(date1, date2));
    }
    timePicker1.setValue(null);
    timePicker2.setValue(null);
  }
}
