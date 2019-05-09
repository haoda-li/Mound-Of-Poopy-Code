package dataModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A Menu object is a special list containing all the dishes and their toppings For reference when
 * creating new dish in ordering
 *
 * <p>recipes A container which have menu entries in it. possibleTopping Map of topping Recipes with
 * the string name and the ArrayList of the topping
 */
public class Menu {
  private ObservableList<Recipe> recipes;
  private Map<String, ArrayList<Recipe>> possibleTopping = new HashMap<>();

  /** initializes an empty Menu with no recipe. */
  public Menu() {
    recipes = FXCollections.observableArrayList();
  }

  /**
   * constructs this Menu from a given file with special format. Each line of the file presents a
   * Recipe with all the details.
   *
   * @param filename string name of the file
   */
  public void initMenu(String filename) {
    try (BufferedReader fileReader = new BufferedReader(new FileReader(filename))) {
      String line = fileReader.readLine();
      if (line.equals("Recipes")) line = fileReader.readLine();
      while (!line.equals("Topping Configure")) {
        String[] info = line.split("; ");
        String type = info[0];
        if (!possibleTopping.keySet().contains(type)) {
          possibleTopping.put(type, new ArrayList<>());
        }
        int ID = Integer.parseInt(info[1]);
        String name = info[2];
        double price = Double.parseDouble(info[3]);
        HashMap<Ingredient, Double> ingredients = new HashMap<>();
        for (int i = 4; i < info.length; i++) {
          String[] tupleText = info[i].split(",");
          ingredients.put(
              IngredientsManager.getIngredient(tupleText[0]), Double.parseDouble(tupleText[1]));
        }
        addRecipe(name, ID, ingredients, price, type, new ArrayList<>());
        line = fileReader.readLine();
      }
      while (line != null) {
        String[] info = line.split("; ");
        ArrayList<Recipe> toppings = new ArrayList<>();
        for (int i = 1; i < info.length; i++) {
          toppings.add(getRecipe(Integer.parseInt(info[i])));
        }
        possibleTopping.replace(info[0], toppings);
        line = fileReader.readLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    for (Recipe r : recipes) {
      r.setToppings(possibleTopping.get(r.getType()));
    }
  }

  /**
   * adds a recipe
   *
   * @param name string of dish name.
   * @param refID integer ID for recipe reference.
   * @param ingredients a list of IngredientTuple.
   * @param price double number of price.
   * @param type the type of this recipe.
   * @param toppings the list of the toppings.
   */
  public void addRecipe(
      String name,
      int refID,
      Map<Ingredient, Double> ingredients,
      double price,
      String type,
      ArrayList<Recipe> toppings) {
    recipes.add(new Recipe(name, refID, ingredients, price, type, toppings));
  }

  /**
   * removes the Recipe given.
   *
   * @param r Recipe for removing.
   */
  public void removeRecipe(Recipe r) {
    recipes.remove(r);
  }

  /**
   * gets the specified Recipe with given reference number.
   *
   * @param ref reference integer of a recipe.
   * @return a Recipe object.
   */
  private Recipe getRecipe(int ref) {
    for (Recipe r : recipes) {
      if (r.getRefID() == ref) {
        return r;
      }
    }
    return null; // implement and throws an exception here
  }

  /**
   * gets the specified Recipe with given name.
   *
   * @param name string name of a recipe.
   * @return a Recipe object.
   */
  public Recipe getRecipe(String name) {
    for (Recipe r : recipes) {
      if (r.getName().equals(name)) {
        return r;
      }
    }
    return null;
  }

  /**
   * gets the possible toppings.
   *
   * @return Map of the toppings.
   */
  public Map<String, ArrayList<Recipe>> getPossibleTopping() {
    return possibleTopping;
  }

  /**
   * gets the list of Recipes.
   *
   * @return ObservableList of recipes.
   */
  public ObservableList<Recipe> getRecipes() {
    return recipes;
  }

  /**
   * checks whether the given refID is valid in this Menu.
   *
   * @param refID integer ID of the recipe.
   * @return true if there is a recipe with given refID, false otherwise.
   */
  public boolean isValidRefID(int refID) {
    for (Recipe r : recipes) {
      if (refID == r.getRefID()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String toString() {
    StringBuilder ret = new StringBuilder();
    for (Recipe r : recipes) {
      ret.append(r.getName()).append(", ").append(r.getName()).append(System.lineSeparator());
    }
    return ret.toString();
  }
}
