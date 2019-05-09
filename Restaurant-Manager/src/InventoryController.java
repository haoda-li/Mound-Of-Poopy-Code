import dataModel.Ingredient;
import dataModel.IngredientsManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

/** Controller of the view for the inventory. */
public class InventoryController {

  @FXML private TableView<Ingredient> ingredientTableView;
  @FXML private TableColumn<Ingredient, String> refID, name, number, email;
  @FXML private TextField searchValue;
  @FXML private TextField numberAddValue;
  @FXML private Label refIDValue, numberValue, nameValue, errorInfo;
  @FXML private ListView<String> needListView;
  @FXML private TextArea stringValue;
  private IngredientsManager ingredientsManager = Main.restaurant.getIngredientManager();

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
    ingredientTableView.setItems(ingredientsManager.getIngredients());
    refID.setCellValueFactory(new PropertyValueFactory<>("refID"));
    name.setCellValueFactory(new PropertyValueFactory<>("name"));
    number.setCellValueFactory(new PropertyValueFactory<>("number"));
    email.setCellValueFactory(new PropertyValueFactory<>("providerEmail"));

    ingredientTableView
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (obs, oldSelection, newSelection) -> {
              if (newSelection != null) {
                errorInfo.setText("");
                refIDValue.setText(newSelection.getRefID());
                numberValue.setText(String.valueOf(newSelection.getNumber()));
                nameValue.setText(newSelection.getName());
              } else {
                refIDValue.setText("");
                numberValue.setText("");
                nameValue.setText("");
              }
            });

    needListView.setItems(ingredientsManager.shortMessage);
  }

  // sets the items of the TableView based on the search result.
  @FXML
  private void search() {
    errorInfo.setText("");
    ingredientTableView.setItems(ingredientsManager.getIngredients(searchValue.getText()));
    searchValue.setText("");
  }

  // adds the stock based on the selected text.
  @FXML
  private void addStock() {
    errorInfo.setText("");
    Ingredient selected = ingredientTableView.getSelectionModel().getSelectedItem();
    if (selected == null) {
      errorInfo.setText("Nothing Selected");
      numberAddValue.setText("");
      return;
    }
    Double number = 0.0;
    try {
      number = Double.parseDouble(numberAddValue.getText());
    } catch (Exception e) {
      errorInfo.setText("Number Invalid");
    }
    if (number != 0.0) {
      ingredientsManager.addStock(selected, number);
      numberValue.setText(String.valueOf(selected.getNumber()));
    }
    numberAddValue.setText("");
    ingredientTableView.getColumns().get(2).setVisible(false);
    ingredientTableView.getColumns().get(2).setVisible(true);
  }

  // adds the stock based on the string input.
  @FXML
  private void addStockByString() {
    errorInfo.setText("");
    String[] input = stringValue.getText().split("; ");
    for (String tuples : input) {
      String[] t = tuples.split(",");
      Ingredient i = IngredientsManager.getIngredient(t[0]);
      if (i == null) {
        errorInfo.setText(t[0] + " doesn't exist");
        return;
      }
      double num;
      try {
        num = Double.parseDouble(t[1]);
      } catch (Exception e) {
        errorInfo.setText(t[0] + "'s number invalid");
        return;
      }
      ingredientsManager.addStock(i, num);
    }
    stringValue.setText("");
    ingredientTableView.getColumns().get(2).setVisible(false);
    ingredientTableView.getColumns().get(2).setVisible(true);
  }
}
