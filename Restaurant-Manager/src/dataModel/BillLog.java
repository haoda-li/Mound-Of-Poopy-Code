package dataModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * A BillLog object take notes of bills that happen.
 *
 * <p>time: the happen time of specific bill. billID: the id of specific bill. tableID: the id of
 * table where the bill happens. actions: a sequence of action that this bill does. price: how much
 * this bill costs. dishes: the dishes that is in this bill.
 */
public class BillLog {
  private LocalDateTime time;
  private int billID;
  private int tableID;
  private ArrayList<String> actions;
  private double price;
  private ArrayList<String> dishes = new ArrayList<>();

  /**
   * Creates a BillLog to record the log of the bill with given billID and tableID.
   *
   * @param billID integer ID of the bill given.
   * @param tableID integer ID of the table given.
   */
  BillLog(int billID, int tableID) {
    time = LocalDateTime.now();
    this.billID = billID;
    this.tableID = tableID;
    actions = new ArrayList<>();
    this.price = 0;
  }

  /** @return the happen time of this bill */
  public String getTime() {
    return time.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));
  }

  /** @return the happening time of this bill */
  public LocalDate getLocalDate() {
    return time.toLocalDate();
  }

  // getters of the properties of this BillLog.
  public int getBillID() {
    return billID;
  }

  public int getTableID() {
    return tableID;
  }

  public ArrayList<String> getActions() {
    return actions;
  }

  public double getPrice() {
    return price;
  }

  public ArrayList<String> getDishes() {
    return dishes;
  }

  /**
   * change the cost of this bill
   *
   * @param price the new cost of this bill
   */
  public void setPrice(double price) {
    this.price = price;
  }

  /**
   * add an action done by bill
   *
   * @param action an action
   */
  public void addAction(String action) {
    actions.add(action);
  }

  /** @return a string of all actions done by this bill */
  public String printActions() {
    StringBuilder ret =
        new StringBuilder("BillID: " + getBillID() + " | TableID: " + getTableID())
            .append(System.lineSeparator());
    for (String s : getActions()) {
      ret.append(s).append(System.lineSeparator());
    }
    return ret.toString();
  }
}
