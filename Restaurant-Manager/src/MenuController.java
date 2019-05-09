import dataModel.*;
import dataModel.Menu;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Optional;

/** MenuController works as the controller of the menu view. */
public class MenuController {

  private static int ID;

  /** size of the the list of recipes in the Menu. */
  private int size;

  /** total price for the order this time. */
  private double totalPrice;

  /** the list of recipes in this Menu. */
  private ObservableList<Recipe> recipes;

  /** the order created this time. */
  private Order newOrder;

  @FXML private Button confirmButton;
  @FXML private ScrollPane scrollPane;
  @FXML private FlowPane menuFlow;
  @FXML private ListView<Text> orderList;
  @FXML private Label priceLabel;
  private CookManager cookManager = Main.restaurant.getCookManager();
  private BillManager billManager = Main.restaurant.getBillManager();
  private Menu menu = Main.restaurant.getMenu();
  private TableManager tableManager = Main.restaurant.getTableManager();

  // initializes the menu window.
  @FXML
  private void initialize() {
    recipes = menu.getRecipes();
    size = recipes.size();
    totalPrice = 0.00;
    ID = 0;
    newOrder = new Order(tableManager.getSelectedTable());
    menuFlow.getChildren().clear();
    initFlowPane();
  }

  // initializes and finishes the FlowPane containing recipes.
  @FXML
  private void initFlowPane() {
    menuFlow.setPadding(new Insets(5, 5, 5, 5));
    menuFlow.setVgap(5);
    menuFlow.setHgap(8);
    menuFlow.setPrefWrapLength(265);
    menuFlow.setStyle("-fx-background-color: #8c2829;");

    GridPane dishes[] = new GridPane[size];
    for (int i = 0; i < size; i++) {
      if (!recipes.get(i).getType().equals("topping")) {
        dishes[i] = addGridPane(recipes.get(i), i);
        menuFlow.getChildren().add(dishes[i]);
      }
    }
  }

  // button works for the FlowPane to get to the top.
  @FXML
  private void topMenuButton() {
    scrollPane.setVvalue(0);
  }

  // button confirms the ordering and close the window.
  @FXML
  private void confirmButton() {
    if (newOrder.getDishes().isEmpty()) {
      return;
    }
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Bill Confirmation");
    alert.setHeaderText("");
    alert.setContentText("Confirm this order?");

    Optional<ButtonType> result = alert.showAndWait();
    if (result.get() == ButtonType.OK) {
      billManager.addOrder(cookManager, newOrder); // ... user chose OK
    }
    Stage stage = (Stage) confirmButton.getScene().getWindow();
    stage.close();
  }

  // button adds the dish.
  @FXML
  private void addDishButton(ActionEvent event) {
    Button tempB = (Button) event.getSource();
    String addId = tempB.getId().replaceAll("[a-zA-Z]", "");
    int tempId = Integer.parseInt(addId);

    String oneDishText = addDish(tempId);
    if (oneDishText.equals("")) {
      return;
    }
    Text recipeItem = new Text();
    recipeItem.setId("ordered" + addId);
    recipeItem.setText(oneDishText);
    orderList.getItems().add(recipeItem);
    priceLabel.setText("$ " + String.format("%.2f", totalPrice));
  }

  // button removes the dish.
  @FXML
  private void removeDishButton(ActionEvent event) {
    Button temp = (Button) event.getSource();
    String addId = temp.getId().replaceAll("[a-zA-Z]", "");
    int tempId = Integer.parseInt(addId);
    Text removed = (Text) orderList.lookup("#ordered" + tempId);

    removeDish(tempId);

    orderList.getItems().remove(removed);
    priceLabel.setText("$ " + totalPrice);
  }

  // add Dish method to get the information of the added dish and add it to the order.
  @FXML
  private String addDish(int addId) {
    Text recipeName = (Text) menuFlow.lookup("#recipe" + addId);
    StringBuilder oneRecipeText = new StringBuilder(recipeName.getText() + "\n");
    Recipe tempRecipe = menu.getRecipe(recipeName.getText().split(": ")[1]);

    VBox toppings = (VBox) menuFlow.lookup("#toppingList" + addId);
    ObservableList<Node> nodes = toppings.getChildren();
    ArrayList<Recipe> tempToppings = new ArrayList<>();
    for (Node c : nodes) {
      if (c instanceof CheckBox) {
        CheckBox temp = (CheckBox) c;
        if (temp.isSelected()) {
          String topName = temp.getText();
          tempToppings.add(menu.getRecipe(topName));
          oneRecipeText.append("+").append(topName).append("\n");
        }
      }
    }
    String sizeChoice = "";
    if (!nodes.isEmpty()) {
      if (nodes.get(nodes.size() - 1) instanceof ChoiceBox) {
        sizeChoice = (String) ((ChoiceBox) nodes.get(nodes.size() - 1)).getValue();
      }
    }
    oneRecipeText.append(sizeChoice).append("\n");
    newOrder.addDish(tempRecipe, tempToppings, sizeChoice);

    if (!checkDish(newOrder.getDishes().get(ID))) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Dish sold out");
      alert.setHeaderText(null);
      alert.setContentText(recipeName.getText() + " is sold out");
      alert.showAndWait();
      newOrder.removeDish(newOrder.getDishes().get(ID));
      return "";
    }

    double dishPrice = newOrder.getDishes().get(ID).getPrice();
    Text recipePrice = new Text(String.format("%.2f", dishPrice) + "");
    totalPrice += dishPrice;
    oneRecipeText.append("$ ").append(recipePrice.getText()).append("\n");
    ID++;
    return oneRecipeText.toString();
  }

  // helper method to check whether this dish is available.
  private boolean checkDish(Dish dish) {
    if (dish.checkAvailable()) {
      dish.useIngredient();
      return true;
    }
    return false;
  }

  // remove Dish method to get the information of the removed dish and remove it from the order.
  @FXML
  private void removeDish(int addId) {
    Text recipeName = (Text) menuFlow.lookup("#recipe" + addId);
    String recipe = recipeName.getText().split(": ")[1];
    ArrayList<Dish> temp = new ArrayList<>();
    for (Dish d : newOrder.getDishes()) {
      if (d.getName().equals(recipe)) {
        d.cancelIngredient();
        totalPrice -= d.getPrice();
        temp.add(d);
        break;
      }
    }
    temp.stream().findFirst().ifPresent(d -> newOrder.removeDish(d));
  }

  // adds the ToppingPane as a ScrollPane.
  @FXML
  private ScrollPane addToppingPane(Recipe recipe, int id) {
    ScrollPane scroll = new ScrollPane();
    VBox toppingList = new VBox();
    toppingList.setId("toppingList" + id);
    for (int i = 0; i < recipe.getToppings().size(); i++) {
      CheckBox top = new CheckBox();
      top.setPrefHeight(35);
      top.setText(recipe.getToppings().get(i).getName());
      toppingList.getChildren().add(top);
    }

    if (!recipe.getType().equals("sides")) {
      ChoiceBox<String> sizeChoice = addSizeBox(recipe);
      sizeChoice.getSelectionModel().select(0);
      sizeChoice.setId("Size" + id);
      toppingList.getChildren().add(sizeChoice);
    }
    scroll.setContent(toppingList);
    return scroll;
  }

  // adds the ChoiceBox of the sizes.
  @FXML
  private ChoiceBox<String> addSizeBox(Recipe recipe) {
    ObservableList<String> sizeList = FXCollections.observableArrayList("Size-M", "Size-L");
    if (recipe.getType().equals("hot")) {
      sizeList.add(0, "Size-S");
    }
    return new ChoiceBox<>(sizeList);
  }

  // adds the GridPane containing the recipe with buttons and toppingPane.
  @FXML
  private GridPane addGridPane(Recipe recipe, int id) {
    GridPane grid = new GridPane();
    setGridLayout(grid);
    grid.setId("grid" + id);

    // set recipeText node in column 1, row 1, spans columns 1,2 and rows 1.
    String head = recipe.toString().split(System.lineSeparator())[0];
    Text recipeText = new Text(head);
    recipeText.setId("recipe" + id);
    recipeText.setWrappingWidth(135);
    recipeText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
    GridPane.setHalignment(recipeText, HPos.CENTER);
    GridPane.setValignment(recipeText, VPos.CENTER);
    grid.add(recipeText, 0, 0, 2, 1);

    // set recipeDetail node in column 1, row 2, spans columns 1,2 and rows 2,3.
    Text recipeDetail = new Text(recipe.toString().replaceAll(head, ""));
    recipeDetail.setId("recipeDetail" + id);
    recipeDetail.setWrappingWidth(130);
    recipeDetail.setFont(Font.font("Arial", FontPosture.ITALIC, 12));
    GridPane.setHalignment(recipeDetail, HPos.CENTER);
    GridPane.setValignment(recipeDetail, VPos.CENTER);
    grid.add(recipeDetail, 0, 1, 2, 2);

    // set recipePrice node in column 1, row 4, spans columns 1,2 and rows 4.
    Text recipePrice = new Text();
    recipePrice.setId("recipePrice" + id);
    GridPane.setHalignment(recipePrice, HPos.CENTER);
    GridPane.setValignment(recipePrice, VPos.BASELINE);
    String priceText = "$ " + recipe.getPrice();
    if (recipe.getType().equals("cold")) {
      priceText += " / $ " + Math.round(recipe.getPrice() * 1.4 * 10.0) / 10.0;
    } else if (recipe.getType().equals("hot")) {
      priceText +=
          " / $ "
              + Math.round(recipe.getPrice() * 1.4 * 10.0) / 10.0
              + " / $"
              + Math.round(recipe.getPrice() * 1.8 * 10.0) / 10.0;
    }
    recipePrice.setText(priceText);
    recipePrice.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.ITALIC, 14));
    grid.add(recipePrice, 0, 3, 2, 1);

    // set toppingPane node in column 3, row 1, spans columns 3, rows 1,2,3,4.
    ScrollPane toppingPane = addToppingPane(recipe, id);
    toppingPane.setId("toppings" + id);
    grid.add(toppingPane, 2, 0, 1, 4);

    // set addDish button node in column 3, row 4.
    Button addDish = new Button("Add");
    addDish.setId("addButton" + id);
    addDish.setOnAction(this::addDishButton);
    addDish.setStyle(
        "-fx-background-color: linear-gradient(#f0ff35,#a9ff00),"
            + "radial-gradient(center 50%-40%, radius 200%,#b8ee36 45%,#80c800 50%);"
            + "-fx-background-radius: 10;"
            + "-fx-border-radius: 10;");
    addDish.setPrefWidth(grid.getColumnConstraints().get(2).getPrefWidth());
    GridPane.setHalignment(addDish, HPos.CENTER);
    grid.add(addDish, 2, 4);

    // set cancelDish button node in column 2, row 4
    Button cancelDish = new Button("Remove");
    cancelDish.setId("removeButton" + id);
    cancelDish.setOnAction(this::removeDishButton);
    cancelDish.setStyle(
        "-fx-background-color: linear-gradient(#ff7575,#d1381d);"
            + "-fx-background-radius: 10;"
            + "-fx-border-radius: 10;");
    cancelDish.setPrefWidth(grid.getColumnConstraints().get(0).getPrefWidth());
    GridPane.setHalignment(cancelDish, HPos.CENTER);
    //        new Image(LayoutSample.class.getResourceAsStream(“graphics/house.png”)));
    grid.add(cancelDish, 1, 4);
    return grid;
  }

  // helper method to set the GridPane layout.
  @FXML
  private void setGridLayout(GridPane grid) {
    grid.setPrefSize(250, 180);

    ColumnConstraints column1 = new ColumnConstraints(67);
    grid.getColumnConstraints().add(column1);
    grid.getColumnConstraints().add(column1);
    grid.getColumnConstraints().add(new ColumnConstraints(110));

    grid.getRowConstraints().add(new RowConstraints(50));
    grid.getRowConstraints().add(new RowConstraints(35));
    grid.getRowConstraints().add(new RowConstraints(35));
    grid.getRowConstraints().add(new RowConstraints(30));

    grid.setHgap(8);
    grid.setVgap(5);
    grid.setPadding(new Insets(5, 8, 5, 8));
    grid.setStyle("-fx-background-color: white;");
    //    grid.setGridLinesVisible(true);
  }
}
