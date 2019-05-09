package dataModel;

import java.util.Map;

/**
 * A Dish object represents a dish in the Menu of this restaurant. This is an abstract class that
 * will be inherited by several kind of dish class based on the menu. For this restaurant, there are
 * ColdDrink, HotDrink and Sides.
 *
 * <p>ID reference ID. name name of the dish. price double price of the dish. ingredients a map of
 * Ingredients with details and quantity. status an integer showing whether this dish is ordered or
 * not or cancelled. recipeID the ID of the related recipe. comments comments for customers to make
 * for this dish. toWhichBill Bill this dish located on. toWhichOrder Order this dish located on.
 */
public abstract class Dish {

  private static int currID = 1; // static integer represents the current ID.

  private int ID;
  private String name;
  private double price;
  private Map<Ingredient, Double> ingredients;
  private int status;
  private Bill toWhichBill;
  private Order toWhichOrder;

  /**
   * Creates a Dish object with given recipe.
   *
   * @param r Recipe for creating this dish.
   */
  public Dish(Recipe r) {
    this.name = r.getName();
    this.ingredients = r.getIngredients();
    this.price = r.getPrice();
    this.ID = currID;
    currID++;
    status = State.ORDERED;
  }

  // getters of the properties of this Dish.
  public int getID() {
    return ID;
  }

  public String getName() {
    return name;
  }

  public double getPrice() {
    return price;
  }

  public Map<Ingredient, Double> getIngredients() {
    return ingredients;
  }

  public int getStatus() {
    return status;
  }

  public Bill getToWhichBill() {
    return toWhichBill;
  }

  public Order getToWhichOrder() {
    return toWhichOrder;
  }

  public Table getTable() {
    return toWhichBill.getTable();
  }

  /**
   * Default get size, which will be overwritten by
   *
   * @return the string of size
   */
  public abstract String getSize();

  // setters of the properties of this Dish.
  public void setName(String name) {
    this.name = name;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public void setStatus(int s) {
    status = s;
  }

  public void setToWhichBill(Bill bill) {
    this.toWhichBill = bill;
  }

  public void setToWhichOrder(Order o) {
    this.toWhichOrder = o;
  }

  /**
   * Adds ingredients to the dish.
   *
   * @param ingredientMap Map of the ingredients.
   */
  void addIngredients(Map<Ingredient, Double> ingredientMap) {
    for (Ingredient i : ingredientMap.keySet()) {
      if (ingredients.containsKey(i)) {
        ingredients.replace(i, ingredientMap.get(i));
      } else {
        ingredients.put(i, ingredientMap.get(i));
      }
    }
  }

  /**
   * Checks the availability of the ingredients for this dish.
   *
   * @return true if there are enough ingredients, false otherwise.
   */
  public boolean checkAvailable() {
    for (Ingredient i : ingredients.keySet()) {
      if (i.getNumber() < ingredients.get(i)) {
        return false;
      }
    }
    return true;
  }

  /** Uses the ingredients for this dish. */
  public void useIngredient() {
    for (Ingredient i : ingredients.keySet()) {
      i.changeNumber(-1 * ingredients.get(i));
    }
  }

  /** Cancels the ingredients for this dish. */
  public void cancelIngredient() {
    for (Ingredient i : ingredients.keySet()) {
      i.changeNumber(1 * ingredients.get(i));
    }
  }

  @Override
  public String toString() {
    return name + "  |  Status: " + State.stateMap.get(status);
  }
}
