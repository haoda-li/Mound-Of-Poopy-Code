package dataModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/** An Order object records dishes, time and table ID with an id ID in a specific status. */
public class Order {
  private ObservableList<Dish> dishes;
  private Table table;
  private LocalDateTime datetime;
  private int status;
  private int ID;
  private static int num = 1;

  /**
   * creates an order recording dishes, time and table with an id ID in a specific status.
   *
   * @param dishes the dishes that the customer order.
   * @param table the table to which this order belongs.
   */
  public Order(ObservableList<Dish> dishes, Table table) {
    this.dishes = FXCollections.observableArrayList();
    addDishes(dishes);
    this.table = table;
    this.datetime = LocalDateTime.now();
    ID = num;
    num++;
    this.status = State.ORDERED;
    for (Dish d : dishes) {
      d.setToWhichOrder(this);
    }
  }

  /**
   * creates an order recording dishes, time and table ID with an id ID in a specific status.
   *
   * @param table the table to which this order belongs.
   */
  public Order(Table table) {
    this.dishes = FXCollections.observableArrayList();
    this.table = table;
    this.datetime = LocalDateTime.now();
    this.ID = num;
    num++;
    this.status = State.ORDERED;
  }

  // getters of the properties of this Order.
  public String getDateTimeString() {
    return getDatetime().format(DateTimeFormatter.ofPattern("HH:mm"));
  }

  public ObservableList<Dish> getDishes() {
    return dishes;
  }

  public int getTableID() {
    return table.getID();
  }

  public LocalDateTime getDatetime() {
    return datetime;
  }

  public Table getTable() {
    return table;
  }

  public int getStatus() {
    return status;
  }

  public int getID() {
    return ID;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getStatusString() {
    return State.stateMap.get(status);
  }

  /**
   * Adds new dishes to dishes.
   *
   * @param newDishes dishes to be added
   */
  private void addDishes(ObservableList<Dish> newDishes) {
    dishes.addAll(newDishes);
  }

  /**
   * Changes status to "canceled", "ordered", "cooked", "cooking" or "delivered"
   *
   * @param newStatus an int which is -1 or 0 or 1.
   */
  public void changeStatus(int newStatus) {
    if (newStatus == State.COOKING
        || newStatus == State.COOKED
        || newStatus == State.DELIVERED
        || newStatus == State.CANCELLED
        || newStatus == State.ORDERFINISHED) {
      status = newStatus;
      for (Dish d : dishes) {
        if (!(d.getStatus() == State.CANCELLED)) {
          d.setStatus(newStatus);
        }
      }
    } else {
      System.out.println("This is not a valid status.");
    }
  }

  /**
   * Creates and adds one dish based on given Recipe, toppings added and its size.
   *
   * @param r Recipe object specified dish.
   * @param toppings ArrayList of topping recipes.
   * @param size string represents size of this dish.
   */
  public void addDish(Recipe r, ArrayList<Recipe> toppings, String size) {
    String type = r.getType();
    switch (type) {
      case "cold":
        ColdDrink cd = new ColdDrink(r, toppings, size);
        dishes.add(cd);
        cd.setToWhichOrder(this);
        break;
      case "hot":
        HotDrink hd = new HotDrink(r, toppings, size);
        dishes.add(hd);
        hd.setToWhichOrder(this);
        break;
      case "sides":
        Sides s = new Sides(r);
        dishes.add(s);
        s.setToWhichOrder(this);
        break;
    }
  }

  /**
   * Remove a dish from the dish queue.
   *
   * @param d the specific dish which is gonna be removed
   */
  public void removeDish(Dish d) {
    dishes.remove(d);
  }

  public String printDishes() {
    StringBuilder ret = new StringBuilder();
    for (Dish d : getDishes()) {
      ret.append("ID: ")
          .append(d.getID())
          .append(", ")
          .append(d.toString())
          .append(System.lineSeparator());
    }
    return ret.toString();
  }
}
