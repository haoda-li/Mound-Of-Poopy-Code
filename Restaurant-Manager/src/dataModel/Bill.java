package dataModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * A Bill object recording dishes, time and table.
 *
 * <p>dishes ObservableList of All the dishes in this order. table Every order belongs to a Table.
 * datetime Local time of the terminal. ID The ID of this order.
 */
public class Bill {
  private static int num = 1; // static field for setting the ID

  private ObservableList<Dish> dishes;
  private Table table;
  private LocalDateTime datetime;
  private int ID;

  /**
   * Creates a Bill recording dishes and a specific table.
   *
   * @param dishes the dishes that the customer order.
   * @param table the table to which this order belongs.
   */
  public Bill(ObservableList<Dish> dishes, Table table) {
    this.dishes = FXCollections.observableArrayList();
    addDishes(dishes);
    this.table = table;
    table.setBill(this);
    this.datetime = LocalDateTime.now();
    ID = num;
    num++;
    for (Dish d : dishes) {
      d.setToWhichBill(this);
    }
  }

  /**
   * Creates a Bill recording a specific table.
   *
   * @param table the table to which this order belongs.
   */
  public Bill(Table table) {
    this.dishes = FXCollections.observableArrayList();
    this.table = table;
    this.datetime = LocalDateTime.now();
    this.ID = num;
    num++;
  }

  // getters of the properties of this Bill
  public ObservableList<Dish> getDishes() {
    return dishes;
  }

  public Table getTable() {
    return table;
  }

  public int getID() {
    return ID;
  }

  public LocalDateTime getDatetime() {
    return datetime;
  }

  /**
   * Sets the Table for this bill.
   *
   * @param table a Table object representing the table.
   */
  public void setTable(Table table) {
    this.table = table;
  }

  /**
   * Gets the id of the table this bill belongs to.
   *
   * @return integer id of the Table.
   */
  public int getTableID() {
    return table.getID();
  }

  /**
   * Gets the total price of this bill.
   *
   * @return double price of the dishes in this bill.
   */
  public double getPrice() {
    double ret = 0;
    for (Dish d : getDishes()) {
      ret += d.getPrice();
    }
    return ret * 1.13;
  }

  /**
   * Adds new dishes to dishes.
   *
   * @param newDishes dishes to be added.
   */
  public void addDishes(ObservableList<Dish> newDishes) {
    dishes.addAll(newDishes);
  }

  /**
   * Removes a dish from the dish queue.
   *
   * @param d the specific dish which is gonna be removed.
   */
  public void removeDish(Dish d) {
    dishes.remove(d);
  }

  /**
   * Calculates the price of the whole order, which is the sum of all the finished dishes. The price
   * is to 0.1 and need to add 13% tax and 12% tip.
   *
   * @param payOrNot boolean represents pay or not.
   * @param tipOrNot boolean represents tip or not.
   * @return a string representing the bill.
   */
  public String getBill(boolean payOrNot, boolean tipOrNot) {
    ArrayList<Dish> tempDishes = new ArrayList<>(dishes);
    return helpGetBill(tempDishes, 1, payOrNot, tipOrNot);
  }

  /**
   * Calculates the price of the separate bill with given number of people, then divides evenly.
   *
   * @param numPeople number of people of the table.
   * @param payOrNot boolean represents pay or not.
   * @return the price.
   */
  public String getBill(int numPeople, boolean payOrNot) {
    ArrayList<Dish> tempDishes = new ArrayList<>(dishes);
    if (numPeople >= 8) {
      return helpGetBill(tempDishes, numPeople, payOrNot, true);
    } else {
      return helpGetBill(tempDishes, numPeople, payOrNot, false);
    }
  }

  /**
   * Calculates the price of given dishes.
   *
   * @param dishes the dishes.
   * @param payOrNot boolean represents pay or not.
   * @param tipOrNot boolean represents tip or not.
   * @return the total price of this bill.
   */
  public String getBill(ArrayList<Dish> dishes, boolean payOrNot, boolean tipOrNot) {
    return helpGetBill(dishes, 1, payOrNot, tipOrNot);
  }

  /**
   * Helper function for get bill.
   *
   * @param dishes the dishes.
   * @param numOfPeople the number of people.
   * @param payOrNot whether pay.
   * @param tipOrNot whether add tip.
   * @return the bill as a string.
   */
  private String helpGetBill(
      ArrayList<Dish> dishes, int numOfPeople, boolean payOrNot, boolean tipOrNot) {
    double price = 0;
    StringBuilder bill = new StringBuilder();
    for (Dish dish : dishes) {
      if (dish.getStatus() == State.DELIVERED) {
        double subPrice = dish.getPrice() / numOfPeople;
        subPrice = Math.round(subPrice * 10.0) / 10.0;
        bill.append(dish.getName()).append(" ").append(subPrice).append(System.lineSeparator());
        price += dish.getPrice() / numOfPeople;
        if (payOrNot) {
          dish.setStatus(State.PAID);
        }
      }
    }
    bill.append("subtotal = ")
        .append(Math.round(price * 10.0) / 10.0)
        .append(System.lineSeparator());
    double tax = price * 0.13;
    tax = Math.round(tax * 10.0) / 10.0;
    bill.append("13% tax = ").append(tax).append(System.lineSeparator());
    if (tipOrNot) {
      double tip = price * 1.13 * 0.15;
      tip = Math.round(tip * 10.0) / 10.0;
      bill.append("15% tip = ").append(tip).append(System.lineSeparator());
      bill.append("total = ")
          .append(Math.round((price + tax + tip) * 10.0) / 10.0)
          .append(System.lineSeparator());
      return bill.toString();
    }
    bill.append("total = ").append(Math.round((price + tax) * 10.0) / 10.0);
    return bill.toString();
  }

  /**
   * Returns true IFF all the dishes in the order is "delivered".
   *
   * @return true of false.
   */
  public boolean checkOrder() {
    boolean temp = true;
    for (Dish dish : dishes) {
      if (dish.getStatus() < State.DELIVERED) {
        temp = false;
        break;
      }
    }
    return temp;
  }

  @Override
  public String toString() {
    StringBuilder temp =
        new StringBuilder(
            "Bill"
                + ID
                + System.lineSeparator()
                + "Time: "
                + datetime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"))
                + System.lineSeparator());
    temp.append(System.lineSeparator());
    for (Dish dish : dishes) {
      temp.append(dish.toString()).append(System.lineSeparator());
    }
    return temp.toString();
  }
}
