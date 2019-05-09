package dataModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * An IngredientLog object records all the in stock ingredients.
 *
 * <p>time Local time. ingredient the name of this ingredient. number the number of each ingredient
 * got added. price the price of this ingredient.
 */
public class IngredientLog {

  private LocalDateTime time;
  private String ingredient;
  private double number;
  private double price;

  /**
   * Creates the IngredientLog based on given name and number.
   *
   * @param ingredient string of the name of the ingredient.
   * @param number double number.
   */
  IngredientLog(Ingredient ingredient, double number) {
    this.time = LocalDateTime.now();
    this.ingredient = ingredient.getName();
    this.number = number;
    this.price = ingredient.getPrice() * number * (-1);
  }

  // getters of the properties of this IngredientLog.
  public String getTime() {
    return time.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));
  }

  public String getIngredient() {
    return ingredient;
  }

  public double getNumber() {
    return number;
  }

  public double getPrice() {
    return price;
  }

  public LocalDate getLocalDate() {
    return time.toLocalDate();
  }
}
