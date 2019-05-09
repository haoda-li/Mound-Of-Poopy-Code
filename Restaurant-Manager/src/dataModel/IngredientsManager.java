package dataModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Observable;
import java.util.Observer;

/**
 * An IngredientsManager object manages all the ingredients' consumption and restoration
 * Ingredients. All the ingredient in a Array list.
 *
 * <p>shortMessage ObservableList of strings as short message. logManager the LogManager given for
 * logging
 */
public class IngredientsManager implements Observer {

  private static ObservableList<Ingredient> ingredients;

  public ObservableList<String> shortMessage;
  private LogManager logManager;

  /** Creates an IngredientsManager object. */
  public IngredientsManager() {
    ingredients = FXCollections.observableArrayList();
    shortMessage = FXCollections.observableArrayList();
  }

  public void setLogManager(LogManager logManager) {
    this.logManager = logManager;
  }

  public ObservableList<Ingredient> getIngredients() {
    return ingredients;
  }

  /**
   * Using keyword to search for ingredients.
   *
   * @param search the string to be searched
   * @return all the ingredients contain the keyword searched
   */
  public ObservableList<Ingredient> getIngredients(String search) {
    ObservableList<Ingredient> ret = FXCollections.observableArrayList();
    for (Ingredient i : ingredients) {
      if (i.getName().contains(search)) {
        ret.add(i);
      }
    }
    return ret;
  }

  /**
   * Gets an ingredient by name or the refID.
   *
   * @param name the name of the refID
   * @return the ingredient
   */
  public static Ingredient getIngredient(String name) {
    for (Ingredient i : ingredients) {
      if (i.getName().equalsIgnoreCase(name) || i.getRefID().equalsIgnoreCase(name)) {
        return i;
      }
    }
    return null;
  }

  /**
   * Adds a number of ingredient.
   *
   * @param i the ingredient
   * @param number the number
   */
  public void addStock(Ingredient i, double number) {
    i.changeNumber(number);
    logManager.addIngredientLog(i, number);
  }

  /** Adds a new ingredient into the ingredientManager. */
  private void addIngredient(Ingredient i) {
    ingredients.add(i);
  }

  /**
   * Adds a new ingredient into the ingredientManager.
   *
   * @param name the name
   * @param refID the refID
   * @param minNumber the shortage threshold
   * @param addNumber add each time
   * @param providerEmail the provider's email
   * @param price the price
   */
  public void addIngredient(
      String name,
      String refID,
      double minNumber,
      double addNumber,
      String providerEmail,
      double price) {
    Ingredient i = new Ingredient(refID, name, providerEmail, price);
    if (minNumber != 0.0) {
      i.setMinNumber(minNumber);
    }
    if (addNumber != 0.0) {
      i.setAddNumber(addNumber);
    }
    addIngredient(i);
  }

  /**
   * Checks if the ID matches the correct pattern and is not used by other ingredients.
   *
   * @param refID the refID
   * @return true IFF it is not used and is valid
   */
  public boolean isValidRefID(String refID) {
    if (!refID.matches("[A-Z][0-9]+")) {
      return false;
    }
    for (Ingredient ingredient : ingredients) {
      if (ingredient.getRefID().equals(refID)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Remove this ingredient from ingredientManager.
   *
   * @param i the ingredient
   */
  public void removeIngredient(Ingredient i) {
    ingredients.remove(i);
  }

  /**
   * Initialize this ingredientManager by a given file of configures.
   *
   * @param fileName the file
   */
  public void initIM(String fileName) {
    try (BufferedReader fileReader = new BufferedReader(new FileReader(fileName))) {
      String line = fileReader.readLine();
      while (line != null) {
        String[] info = line.split("; ");
        Ingredient toAdd = new Ingredient(info[0], info[1], info[3], Double.parseDouble(info[2]));
        toAdd.setNumber(10);
        ingredients.add(toAdd);
        toAdd.addObserver(this);
        line = fileReader.readLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void update(Observable o, Object arg) {
    String input = (String) arg;
    if (input.contains(" is short ")) {
      shortMessage.add(
          LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " " + input);
    } else {
      for (String s : shortMessage) {
        if (s.contains(input)) {
          shortMessage.remove(s);
          return;
        }
      }
    }
  }
}
