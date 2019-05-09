package dataModel;

import java.util.ArrayList;
import java.util.Map;

/**
 * Recipe record all the name, ingredient, price, and reference of a dish or a topping. A dish's
 * recipe also has a list of toppings.
 *
 * <p>name the name of this dish type the type of this dish. refID the id of this dish ingredients
 * the required ingredient list when making this dish toppings a map of toppings which can be added
 * to this dish price the price of this dish
 */
public class Recipe {

  private String name;
  private String type;
  private int refID;
  private Map<Ingredient, Double> ingredients;
  private double price;
  private ArrayList<Recipe> toppings;

  /**
   * creates a recipe for a dish(Dish or Topping) with given name, ingredients and price.
   *
   * @param name string name of this dish
   * @param ingredients the ingredient list with quantity
   * @param price double price of this dish
   * @param type string showing the type of this dish
   */
  Recipe(
      String name,
      int refID,
      Map<Ingredient, Double> ingredients,
      double price,
      String type,
      ArrayList<Recipe> toppings) {
    this.name = name;
    this.refID = refID;
    this.ingredients = ingredients;
    this.price = price;
    this.type = type;
    this.toppings = toppings;
  }

  public void setToppings(ArrayList<Recipe> toppings) {
    this.toppings = toppings;
  }

  public Map<Ingredient, Double> getIngredients() {
    return ingredients;
  }

  public int getRefID() {
    return refID;
  }

  public String getName() {
    return name;
  }

  public double getPrice() {
    return price;
  }

  public String getType() {
    return type;
  }

  public void setIngredients(Map<Ingredient, Double> ingredients) {
    this.ingredients = ingredients;
  }

  public void setRefID(int refID) {
    this.refID = refID;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ArrayList<Recipe> getToppings() {
    return toppings;
  }

  public String getIngredientsString() {
    StringBuilder ret = new StringBuilder();
    for (Ingredient i : ingredients.keySet()) {
      ret.append(i.getName())
          .append(", ")
          .append(ingredients.get(i))
          .append(System.lineSeparator());
    }
    return ret.toString();
  }

  @Override
  public String toString() {
    //        StringBuilder ret = new StringBuilder();
    //        for (Recipe r: toppings){
    //            ret.append(r.getName()).append(" ");
    //        }
    return refID + ": " + name + System.lineSeparator() + getIngredientsString();
  }
}
