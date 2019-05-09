import dataModel.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

public class TableController {

  @FXML private ListView<Dish> orderedItems;
  @FXML private Label currentTableID, peopleNumberLabel;
  @FXML private Text currentBill, status;
  @FXML private Button brokenButton;
  @FXML private Text errorInfo;
  private TableManager tableManager = Main.restaurant.getTableManager();
  private BillManager billManager = Main.restaurant.getBillManager();
  private CookManager cookManager = Main.restaurant.getCookManager();
  private IngredientsManager ingredientsManager = Main.restaurant.getIngredientManager();

  @FXML
  private void initialize() {
    orderedItems.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
  }

  @FXML
  protected void handlePayByDish() {
    errorInfo.setText("");
    if (!tableManager.getSelectedTable().isOccupied()) {
      errorInfo.setText("This table is not occupied");
      return;
    }
    ObservableList<Integer> selectedIntegers =
        orderedItems.getSelectionModel().getSelectedIndices();
    ObservableList<Dish> dishes = tableManager.getSelectedTable().getBill().getDishes();
    ArrayList<Dish> dishList = new ArrayList<>();
    for (Integer i : selectedIntegers) {
      dishList.add(dishes.get(i));
    }

    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Warning");
    alert.setHeaderText("Pay by dishes");
    alert.setContentText(billManager.pay(tableManager.getSelectedTable(), dishList, false));
    Optional result = alert.showAndWait();
    if (result.get() == ButtonType.OK) {
      billManager.pay(tableManager.getSelectedTable(), dishList, true);
    } else {
      errorInfo.setText("Pay failed");
    }
    updateTableInfo(tableManager.getSelectedTable());
  }

  @FXML
  protected void handleDishCancelled() {
    errorInfo.setText("");
    if (!tableManager.getSelectedTable().isOccupied()) {
      errorInfo.setText("This table is not occupied");
      return;
    }
    ArrayList<Dish> selected = new ArrayList<>(orderedItems.getSelectionModel().getSelectedItems());
    for (Dish d : selected) {
      cookManager.cancelCookTask(d, ingredientsManager);
    }
    updateTableInfo(tableManager.getSelectedTable());
  }

  @FXML
  private void toMain(ActionEvent event) throws IOException {
    Parent root = FXMLLoader.load(getClass().getResource("welcome.fxml"));
    Scene menuView = new Scene(root);
    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
    window.setScene(menuView);
    window.show();
  }

  private Table getTable(String tableName) {
    errorInfo.setText("");
    int tableID = Integer.parseInt(tableName.substring(tableName.length() - 1));
    return tableManager.getTables().get(tableID - 1); // have to minus one for the sake of arraylist
  }

  @FXML
  protected void handleSeatedButton() {
    errorInfo.setText("");
    Table currentTable = tableManager.getSelectedTable();
    if (currentTable.isAvailable()) {
      final TextInputDialog textInputDialog = new TextInputDialog("1");
      textInputDialog.setTitle("Seated");
      textInputDialog.setHeaderText("How many people will this table have?");
      textInputDialog.setContentText("Please input a number");
      final Optional<String> opt = textInputDialog.showAndWait();
      String peopleNum;
      try {
        peopleNum = opt.get();
      } catch (final NoSuchElementException ex) {
        peopleNum = null;
      }

      if (peopleNum == null || !peopleNum.matches("[0-9]+")) {
        errorInfo.setText("Invalid number of people");
      } else {
        int num = Integer.parseInt(peopleNum);
        currentTable.setNumPeople(Integer.parseInt(peopleNum));
        setPeopleNumOfTable(num);
        currentTable.setStatus(Table.OCCUPIED);
        updateTableInfo(currentTable);
      }
    }
  }

  private void setPeopleNumOfTable(int peopleNum) {
    String originLabel = peopleNumberLabel.getText();
    String templateLabel = originLabel.substring(0, originLabel.lastIndexOf(" ") + 1);
    peopleNumberLabel.setText(templateLabel + peopleNum);
  }

  @FXML
  protected void handlePayTogether() {
    errorInfo.setText("");
    Table currentTable = tableManager.getSelectedTable();
    if (currentTable.isOccupied()) {
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Warning");
      alert.setHeaderText("Pay together");
      alert.setContentText("Are you sure this transaction is done?");

      Optional result = alert.showAndWait();
      if (result.get() == ButtonType.OK) {
        if (!billManager.pay(currentTable).equals(billManager.getFAILED())) {
          currentTable.setStatus(Table.UNOCCUPIED);
          updateTableInfo(currentTable);
        }
      } else {
        errorInfo.setText("Pay failed");
      }
    } else {
      errorInfo.setText("This table is not occupied");
    }
  }

  @FXML
  protected void handlePaySeparately() {
    errorInfo.setText("");
    Table currentTable = tableManager.getSelectedTable();
    if (currentTable.isOccupied()) {
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Warning");
      alert.setHeaderText("Pay Separately");
      alert.setContentText(billManager.pay(currentTable, currentTable.getNumPeople(), false));

      Optional result = alert.showAndWait();
      if (result.get() == ButtonType.OK) {
        if (!billManager
            .pay(currentTable, currentTable.getNumPeople(), true)
            .equals(billManager.getFAILED())) {
          currentTable.setStatus(Table.UNOCCUPIED);
          updateTableInfo(currentTable);
        }
      } else {
        errorInfo.setText("Pay failed");
      }

    } else {
      errorInfo.setText("This table is not occupied");
    }
  }

  @FXML
  protected void handleViewMenu() throws IOException {
    errorInfo.setText("");
    if (!tableManager.getSelectedTable().isOccupied()) {
      errorInfo.setText("This table is not occupied");
      return;
    }
    if (!cookManager.getDeliveryQueue().isEmpty()) {
      errorInfo.setText("Deliver dishes first");
      return;
    }
    Parent tableViewParent = FXMLLoader.load(getClass().getResource("menu.fxml"));
    Scene tableViewScene = new Scene(tableViewParent);
    Stage window = new Stage();
    window.setResizable(false);
    window.setScene(tableViewScene);
    window.show();
    window.setOnHiding(event1 -> updateTableInfo(tableManager.getSelectedTable()));
  }

  @FXML
  protected void handleReheat() {
    errorInfo.setText("");
    if (!tableManager.getSelectedTable().isOccupied()) {
      errorInfo.setText("This table is not occupied");
      return;
    }
    ObservableList<Dish> selectedIntegers = orderedItems.getSelectionModel().getSelectedItems();
    ObservableList<Dish> dishList = FXCollections.observableArrayList();
    StringBuilder dishError = new StringBuilder();
    for (Dish d : selectedIntegers) {
      if (d.getStatus() == State.DELIVERED) {
        cookManager.handleCases(d, "reheat");
        dishList.add(d);
      } else {
        dishError.append(d.getID()).append(" ").append(d.getName());
        dishError.append(" is not delivered yet; ").append(System.lineSeparator());
      }
    }
    errorInfo.setText(dishError.toString());
    if (!dishList.isEmpty()) {
      Order newOrder = new Order(dishList, tableManager.getSelectedTable());
      cookManager.addCookQueue(newOrder);
      updateTableInfo(tableManager.getSelectedTable());
    }
  }

  @FXML
  protected void handleRemake() {
    errorInfo.setText("");
    if (!tableManager.getSelectedTable().isOccupied()) {
      errorInfo.setText("This table is not occupied");
      return;
    }
    ObservableList<Dish> selectedIntegers = orderedItems.getSelectionModel().getSelectedItems();
    ObservableList<Dish> dishList = FXCollections.observableArrayList();
    StringBuilder dishError = new StringBuilder();
    for (Dish d : selectedIntegers) {
      if (d.getStatus() == State.DELIVERED) {
        if (cookManager.handleCases(d, "remake")) {
          dishList.add(d);
        } else {
          dishError.append(" Can't remake ").append(d.getID()).append(" ").append(d.getName());
          dishError.append(" because ingredients shortage").append(System.lineSeparator());
        }
      } else {
        dishError.append(d.getID()).append(" ").append(d.getName());
        dishError.append(" is not delivered yet").append(System.lineSeparator());
      }
    }
    if (selectedIntegers.size() != dishList.size()) {
      errorInfo.setText(dishError.toString());
    }
    if (!dishList.isEmpty()) {
      Order newOrder = new Order(dishList, tableManager.getSelectedTable());
      cookManager.addCookQueue(newOrder);
    }
  }

  @FXML
  protected void handleTableSelected(ActionEvent event) {
    String selectedTable = ((Button) event.getTarget()).getText();
    currentTableID.setText(selectedTable);
    Table currentTable = getTable(selectedTable);
    tableManager.setSelectedTable(currentTable);
    updateTableInfo(currentTable);
  }

  @FXML
  private void handleBroken() {
    if (tableManager.getSelectedTable().isOccupied()) {
      for (Table t : tableManager.getTables()) {
        if (t.isAvailable()) {
          t.setStatus(Table.OCCUPIED);
          Bill bill = tableManager.getSelectedTable().getBill();
          tableManager.getSelectedTable().setBill(null);
          bill.setTable(t);
          t.setBill(bill);
          t.setNumPeople(tableManager.getSelectedTable().getNumPeople());
          break;
        }
      }
    }
    if (tableManager.getSelectedTable().isAvailable()
        || tableManager.getSelectedTable().isOccupied()) {
      tableManager.setSelectedTableStatus(Table.UNAVAILABLE);
    } else if (tableManager.getSelectedTable().getStatus() == Table.UNAVAILABLE) {
      tableManager.setSelectedTableStatus(Table.UNOCCUPIED);
    }
    updateTableInfo(tableManager.getSelectedTable());
  }

  private void updateTableInfo(Table currentTable) {
    if (currentTable.isOccupied()) {
      if (currentTable.getBill() != null && !currentTable.getBill().getDishes().isEmpty()) {
        orderedItems.setItems(currentTable.getBill().getDishes());
      } else {
        orderedItems.setItems(FXCollections.emptyObservableList());
      }
      String bill = billManager.checkBill(currentTable);
      currentBill.setText(bill);
      setPeopleNumOfTable(currentTable.getNumPeople());
    } else {
      orderedItems.setItems(FXCollections.emptyObservableList());
      currentBill.setText("");
      setPeopleNumOfTable(0);
    }
    if (currentTable.getStatus() == 0) {
      status.setText("OCCUPIED");
      status.setFill(Color.RED);
    } else if (currentTable.getStatus() == 1) {
      status.setText("UNOCCUPIED");
      status.setFill(Color.GREEN);
    } else if (currentTable.getStatus() == 2) {
      status.setText("UNAVAILABLE");
      status.setFill(Color.GRAY);
    }

    if (currentTable.getStatus() == Table.UNAVAILABLE) {
      brokenButton.setText("Table Fixed");
    } else {
      brokenButton.setText("Table Broken");
    }
  }
}
