import dataModel.Ingredient;
import dataModel.IngredientsManager;
import dataModel.Menu;
import dataModel.Recipe;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** Controller of the view for the recipeIngreModifier. */
public class RecipeIngreModifierController {

  @FXML private TableView<Recipe> recipeTV;
  @FXML private TableColumn<Recipe, String> recipeNameTC, recipeRefIDTC;
  @FXML private TableView<Ingredient> ingreTV;
  @FXML private TableColumn<Ingredient, String> ingreRefIDTC, ingreNameTC;
  @FXML private Label ingreErrorInfo;
  @FXML private Label ingreNameL, ingrePriceL, ingreRefIDL, addL, shortL, emailL;
  @FXML private Label recipeErrorInfo;
  @FXML private Label recipeNameL, recipeRefIDL, recipePriceL;
  @FXML private Text recipeText;
  @FXML private TextField recipeNameTF, recipePriceTF, recipeRefIDTF;
  @FXML private TextArea ingredientTA;
  @FXML private TextField ingreNameTF, ingrePriceTF, ingreRefIDTF, addTF, shortTF, emailTF;

  private IngredientsManager ingredientsManager = Main.restaurant.getIngredientManager();
  private Menu menu = Main.restaurant.getMenu();

  @FXML
  private void initialize() {
    ingreTV.setItems(ingredientsManager.getIngredients());
    ingreRefIDTC.setCellValueFactory(new PropertyValueFactory<>("refID"));
    ingreNameTC.setCellValueFactory(new PropertyValueFactory<>("name"));
    ingreTV
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (obs, oldSelection, newSelection) -> {
              ingreErrorInfo.setText("");
              recipeErrorInfo.setText("");
              if (newSelection != null) {
                ingreRefIDL.setText(newSelection.getRefID());
                ingreNameL.setText(newSelection.getName());
                ingrePriceL.setText(String.valueOf(newSelection.getPrice()));
                addL.setText(String.valueOf(newSelection.getAddNumber()));
                shortL.setText(String.valueOf(newSelection.getMinNumber()));
                emailL.setText(newSelection.getProviderEmail());
              } else {
                ingreErrorInfo.setText("");
                ingreRefIDL.setText("");
                ingreNameL.setText("");
                ingrePriceL.setText("");
                addL.setText("");
                shortL.setText("");
                emailL.setText("");
              }
            });
    recipeTV.setItems(menu.getRecipes());
    recipeNameTC.setCellValueFactory(new PropertyValueFactory<>("name"));
    recipeRefIDTC.setCellValueFactory(new PropertyValueFactory<>("refID"));
    recipeTV
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (obs, oldSelection, newSelection) -> {
              recipeErrorInfo.setText("");
              ingreErrorInfo.setText("");
              if (newSelection != null) {
                recipeRefIDL.setText(String.valueOf(newSelection.getRefID()));
                recipeNameL.setText(newSelection.getName());
                recipePriceL.setText(String.valueOf(newSelection.getPrice()));
                recipeText.setText(newSelection.getIngredientsString());
              } else {
                recipeRefIDL.setText("");
                recipeNameL.setText("");
                recipePriceL.setText("");
                recipeText.setText("");
              }
            });
  }

  @FXML
  private void toAddNew(ActionEvent event) throws IOException {
    Parent root = FXMLLoader.load(getClass().getResource("newRecipeIngre.fxml"));
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
  private void setRecipe() {
    recipeErrorInfo.setText("");
    Recipe selected = recipeTV.getSelectionModel().getSelectedItem();
    if (selected == null) {
      recipeErrorInfo.setText("No Recipe Selected");
      return;
    }

    String name = recipeNameTF.getText();
    if (!name.equals("")) {
      selected.setName(name);
      recipeNameTF.setText("");
      recipeNameL.setText(selected.getName());
      recipeNameTC.setVisible(false);
      recipeNameTC.setVisible(true);
    }

    if (!recipeRefIDTF.getText().equals("")) {
      int refID = 0;
      try {
        refID = Integer.parseInt(recipeRefIDTF.getText());
      } catch (Exception e) {
        recipeErrorInfo.setText("refID Invalid");
      }
      if (refID != 0 && menu.isValidRefID(refID)) {
        selected.setRefID(refID);
        recipeRefIDTF.setText("");
        recipeRefIDL.setText(String.valueOf(selected.getRefID()));
        recipeRefIDTC.setVisible(false);
        recipeRefIDTC.setVisible(true);
      } else {
        recipeErrorInfo.setText("RefID Exists");
      }
    }
    if (!recipePriceTF.getText().equals("")) {
      double price = 0;
      try {
        price = Double.parseDouble(recipePriceTF.getText());
      } catch (Exception e) {
        recipeErrorInfo.setText("Price Invalid");
      }
      if (price != 0) {
        selected.setPrice(price);
        recipePriceTF.setText("");
        recipePriceL.setText(String.valueOf(selected.getPrice()));
      }
    }

    String ingreText = ingredientTA.getText();
    if (!ingreText.equals("")) {
      Map<Ingredient, Double> map = convertIngredientsText(ingredientTA.getText());
      if (map != null) {
        selected.setIngredients(map);
        ingredientTA.setText("");
        recipeText.setText(selected.getIngredientsString());
      }
    }
  }

  @FXML
  private void setIngredient() {
    ingreErrorInfo.setText("");
    Ingredient selected = ingreTV.getSelectionModel().getSelectedItem();
    if (selected == null) {
      ingreErrorInfo.setText("Nothing Selected");
      return;
    }

    String name = ingreNameTF.getText();
    if (!name.equals("")) {
      selected.setName(name);
      ingreNameTF.setText("");
      ingreNameL.setText(selected.getName());
      ingreNameTC.setVisible(false);
      ingreNameTC.setVisible(true);
    }
    String refID = ingreRefIDTF.getText();
    if (!refID.equals("")) {
      if (ingredientsManager.isValidRefID(refID)) {
        selected.setRefID(refID);
        ingreRefIDTF.setText("");
        ingreRefIDL.setText(selected.getRefID());
        ingreRefIDTC.setVisible(false);
        ingreRefIDTC.setVisible(true);
      } else {
        ingreErrorInfo.setText("RefID Invalid");
      }
    }
    String email = emailTF.getText();
    if (!email.equals("")) {
      selected.setProviderEmail(email);
      emailTF.setText("");
      emailL.setText(selected.getProviderEmail());
    }

    String add = addTF.getText();
    if (!add.equals("")) {
      try {
        selected.setAddNumber(Double.parseDouble(add));
        addTF.setText("");
        addL.setText(String.valueOf(selected.getAddNumber()));
      } catch (Exception e) {
        ingreErrorInfo.setText("Add Each Time Invalid");
      }
    }

    String shortNum = shortTF.getText();
    if (!shortNum.equals("")) {
      try {
        selected.setAddNumber(Double.parseDouble(shortNum));
        shortTF.setText("");
        shortL.setText(String.valueOf(selected.getAddNumber()));
      } catch (Exception e) {
        ingreErrorInfo.setText("Shortage Threshold Invalid");
      }
    }

    String price = ingrePriceTF.getText();
    if (!price.equals("")) {
      try {
        selected.setAddNumber(Double.parseDouble(price));
        ingrePriceTF.setText("");
        ingrePriceL.setText(String.valueOf(selected.getAddNumber()));
      } catch (Exception e) {
        ingreErrorInfo.setText("Price Invalid");
      }
    }
  }

  @FXML
  private void deleteIngredient() {
    recipeErrorInfo.setText("");
    ingreErrorInfo.setText("");
    Ingredient selected = ingreTV.getSelectionModel().getSelectedItem();
    if (selected != null) {
      ingredientsManager.removeIngredient(selected);
    } else {
      ingreErrorInfo.setText("Nothing Selected");
    }
    ingreTV.setVisible(false);
    ingreTV.setVisible(true);
  }

  @FXML
  private void deleteRecipe() {
    recipeErrorInfo.setText("");
    ingreErrorInfo.setText("");
    Recipe selected = recipeTV.getSelectionModel().getSelectedItem();
    if (selected != null) {
      menu.removeRecipe(selected);
    } else {
      recipeErrorInfo.setText("Nothing selected");
    }
    recipeTV.setVisible(false);
    recipeTV.setVisible(true);
  }

  /**
   * Helper function for reading ingredient text
   *
   * @param ingreText the ingredient text
   * @return the Map of ingredient and their number of usage
   */
  private Map<Ingredient, Double> convertIngredientsText(String ingreText) {
    String[] ingreTuples = ingreText.split("; ");
    Map<Ingredient, Double> map = new HashMap<>();
    for (String s : ingreTuples) {
      String[] tuple = s.split(",");
      Ingredient i = IngredientsManager.getIngredient(tuple[0]);
      double num;
      if (i == null) {
        recipeErrorInfo.setText(tuple[0] + " doesn't exist");
        return null;
      }
      try {
        num = Double.parseDouble(tuple[1]);
      } catch (Exception e) {
        recipeErrorInfo.setText(tuple[0] + " 's number invalid");
        return null;
      }
      map.put(i, num);
    }
    return map;
  }
}
