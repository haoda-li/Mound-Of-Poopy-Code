package dataModel;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Observable;

/**
 * An Ingredient object records the name and amount of ingredient, can check whether the ingredient
 * is short.
 *
 * <p>name The name of the ingredient number The number of ingredient left minNumber The minimum
 * number of ingredient left before become short addNumber The default number of ingredient to be
 * stocked provider The email of the company providing this ingredient price The price of the
 * ingredient reportShort Report whether this ingredient is short
 */
public class Ingredient extends Observable {

  private String name;
  private String refID;
  private double number = 0;
  private double minNumber = 10;
  private double addNumber = 20;
  private String providerEmail;
  private double price;
  private boolean reportShort = false;

  /**
   * Constructs a new ingredient.
   *
   * @param refID refID
   * @param name the name
   * @param providerEmail the email of the provider
   * @param price the price
   */
  Ingredient(String refID, String name, String providerEmail, double price) {
    this.refID = refID;
    this.name = name;
    this.providerEmail = providerEmail;
    this.price = price;
  }

  // getters for all the properties of this ingredient.
  public String getRefID() {
    return refID;
  }

  public String getName() {
    return name;
  }

  public double getNumber() {
    return number;
  }

  public double getMinNumber() {
    return minNumber;
  }

  public double getAddNumber() {
    return addNumber;
  }

  public double getPrice() {
    return price;
  }

  public String getProviderEmail() {
    return providerEmail;
  }

  private boolean isReportShort() {
    return reportShort;
  }

  // setters for all the properties of this ingredient.
  public void setNumber(double number) {
    this.number = number;
  }

  public void setAddNumber(double addNumber) {
    this.addNumber = addNumber;
  }

  public void setMinNumber(double minNumber) {
    this.minNumber = minNumber;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setProviderEmail(String providerEmail) {
    this.providerEmail = providerEmail;
  }

  public void setRefID(String refID) {
    this.refID = refID;
  }

  /**
   * adds or subtracts number of ingredient.
   *
   * @param number the number, positive if adding, negative if subtracting
   */
  public void changeNumber(double number) {
    this.number += number;
    need();
  }

  /** If some ingredient is short, request it */
  private void need() {
    if (!isReportShort() && this.number <= this.minNumber) {
      setChanged();
      notifyObservers(this.getName() + " is short ");
      reportShort = true;
      try (BufferedWriter writer = new BufferedWriter(new FileWriter("request.txt", true))) {
        writer.write("Send to " + providerEmail + ", need " + name + " *" + addNumber);
        writer.newLine();
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else if (this.number > this.minNumber) {
      reportShort = false;
      setChanged();
      notifyObservers(this.getName());
    }
  }

  @Override
  public String toString() {
    return refID + " " + name + ": " + number;
  }
}
