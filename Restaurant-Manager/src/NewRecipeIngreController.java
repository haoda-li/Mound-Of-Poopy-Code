import dataModel.Ingredient;
import dataModel.IngredientsManager;
import dataModel.Menu;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** Controller of the view for the newRecipeIngre. */
public class NewRecipeIngreController {
  @FXML private TextField recipeNameTF, recipeRefIDTF, recipePriceTF;
  @FXML private TextField ingreNameTF, ingreRefIDTF, shortTF, addTF, ingrePriceTF, emailTF;
  @FXML private TextArea recipeIngreTF;
  @FXML
  private Label ingreErrorInfo,
      ingreSuccessInfo,
      recipeErrorInfo,
      recipeSuccessfulInfo,
      recipeIngreErrorInfo;
  @FXML private RadioButton cold, hot, sides, topping;
  private ToggleGroup typeChoice = new ToggleGroup();
  private IngredientsManager ingredientsManager = Main.restaurant.getIngredientManager();
  private Menu menu = Main.restaurant.getMenu();

  // initializes the window.
  @FXML
  private void initialize() {
    cold.setToggleGroup(typeChoice);
    hot.setToggleGroup(typeChoice);
    sides.setToggleGroup(typeChoice);
    topping.setToggleGroup(typeChoice);
  }

  // button to the modifying window.
  @FXML
  private void toModify(ActionEvent event) throws IOException {
    Parent root = FXMLLoader.load(getClass().getResource("recipeIngreModifier.fxml"));
    Scene menuView = new Scene(root);
    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
    window.setScene(menuView);
    window.show();
  }

  // adds new Ingredient and text.
  @FXML
  private void addNewIngre() {
    ingreErrorInfo.setText("");
    recipeErrorInfo.setText("");
    String name = ingreNameTF.getText();
    if (name.equals("")) {
      ingreErrorInfo.setText("Name Empty");
      return;
    }
    String refID = ingreRefIDTF.getText();
    if (!ingredientsManager.isValidRefID(refID)) {
      ingreErrorInfo.setText("RefID Empty or Invalid");
      return;
    }
    String email = emailTF.getText();
    if (email.equals("")) {
      ingreErrorInfo.setText("Email Empty");
      return;
    }
    Double min = 0.0;
    Double add = 0.0;
    Double price;
    if (!addTF.getText().equals("")) {
      try {
        add = Double.parseDouble(addTF.getText());
      } catch (Exception e) {
        ingreErrorInfo.setText("Add Each Time Invalid");
        return;
      }
    }
    if (!shortTF.getText().equals("")) {
      try {
        min = Double.parseDouble(shortTF.getText());
      } catch (Exception e) {
        ingreErrorInfo.setText("Shortage Threshold Invalid");
        return;
      }
    }

    try {
      price = Double.parseDouble(ingrePriceTF.getText());
    } catch (Exception e) {
      ingreErrorInfo.setText("Price Invalid");
      return;
    }
    ingredientsManager.addIngredient(name, refID, min, add, email, price);
    ingrePriceTF.setText("");
    ingreNameTF.setText("");
    ingreRefIDTF.setText("");
    emailTF.setText("");
    shortTF.setText("");
    addTF.setText("");
    ingreSuccessInfo.setText("Successfully Added");
  }

  // adds new Recipe and text.
  @FXML
  private void addNewRecipe() {
    recipeErrorInfo.setText("");
    ingreErrorInfo.setText("");
    recipeSuccessfulInfo.setText("");
    recipeIngreErrorInfo.setText("");
    String name = recipeNameTF.getText();
    if (name.equals("")) {
      recipeErrorInfo.setText("Name Empty");
      return;
    }
    if (!recipeRefIDTF.getText().matches("[0-9]{3}")) {
      recipeErrorInfo.setText("RefID invalid");
      return;
    }
    int refID = Integer.parseInt(recipeRefIDTF.getText());
    if (!menu.isValidRefID(refID)) {
      recipeErrorInfo.setText("RefID Invalid or empty");
      return;
    }
    double price;
    try {
      price = Double.parseDouble(recipePriceTF.getText());
    } catch (Exception e) {
      recipeErrorInfo.setText("Price Invalid");
      return;
    }
    if (price == 0) {
      recipeErrorInfo.setText("Price Empty");
      return;
    }
    if (typeChoice.getSelectedToggle() == null) {
      recipeErrorInfo.setText("Type Empty");
      return;
    }
    String type = ((RadioButton) typeChoice.getSelectedToggle()).getId();
    String ingreText = recipeIngreTF.getText();
    String[] ingreTuples = ingreText.split("; ");
    Map<Ingredient, Double> map = new HashMap<>();
    for (String s : ingreTuples) {
      String[] tuple = s.split(",");
      Ingredient i = IngredientsManager.getIngredient(tuple[0]);
      if (i == null) {
        recipeIngreErrorInfo.setText(tuple[0] + " not found");
        return;
      }
      double number;
      try {
        number = Double.parseDouble(tuple[1]);
      } catch (Exception e) {
        recipeIngreErrorInfo.setText(tuple[0] + " 's number invalid");
        return;
      }
      map.put(i, number);
    }
    menu.addRecipe(name, refID, map, price, type, menu.getPossibleTopping().get(type));
    recipeSuccessfulInfo.setText("Successful");
  }
}
