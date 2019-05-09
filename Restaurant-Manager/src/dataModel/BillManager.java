package dataModel;

import java.util.ArrayList;

/**
 * A BillManager object manages all the bills of a restaurant, e.g., put customers' requirement into
 * the order list
 *
 * <p>bills bills only contain Orders that is "ordered". Any Bill with status "canceled" and
 * "finished" should be in log. logManager the LogManager given for logging
 */
public class BillManager {
  // static string of information of Failed.
  private static String FAILED = "Can't finish this order due to undelivered dish.";

  private ArrayList<Bill> bills;
  private LogManager logManager;

  /** Creates a BillManager in a restaurant. */
  public BillManager() {
    bills = new ArrayList<>();
  }

  /**
   * Sets the LogManager for this BillManager.
   *
   * @param logManager a LogManager given.
   */
  public void setLogManager(LogManager logManager) {
    this.logManager = logManager;
  }

  /**
   * Gets failed information.
   *
   * @return the string specified for FAILED.
   */
  public String getFAILED() {
    return FAILED;
  }

  /**
   * Adds new order into this bill and set CookManager.
   *
   * @param cookManager CookManager for the order to be manages.
   * @param order Order object represents the order to be added.
   */
  public void addOrder(CookManager cookManager, Order order) {
    cookManager.addCookQueue(order);
    for (Bill b : bills) {
      if (b.getTableID() == order.getTableID()) {
        b.addDishes(order.getDishes());
        for (Dish d : order.getDishes()) {
          d.setToWhichOrder(order);
          d.setToWhichBill(b);
          logManager.addAction(b.getID(), "add " + d.getName() + " | " + d.getSize());
        }
        return;
      }
    }
    Bill newBill = new Bill(order.getDishes(), order.getTable());
    bills.add(newBill);
    order.getTable().setBill(newBill);
    logManager.addBillLog(newBill.getID(), newBill.getTableID());
    for (Dish d : order.getDishes()) {
      logManager.addAction(newBill.getID(), "add " + d.getName() + " | " + d.getSize());
    }
  }

  /**
   * Checks the bill of the given table and gives specific info.
   *
   * @param table the Table to be checked.
   * @return A string represents the bill or an info indicating no bill.
   */
  public String checkBill(Table table) {
    Bill bill = table.getBill();
    if (bill != null) {
      if (table.getNumPeople() >= 8) {
        return bill.getBill(false, true);
      } else {
        return bill.getBill(false, false);
      }
    }
    return "Currently no bill";
  }

  /**
   * Pays the bill, finishes the order and gives info.
   *
   * @param table the Table to be paid.
   * @return A string represents the bill paid or an info.
   */
  public String pay(Table table) {
    Bill bill = table.getBill();
    if (bill != null) {
      if (bill.checkOrder()) {
        finishOrder(bill);
        if (table.getNumPeople() >= 8) {
          return bill.getBill(false, true);
        } else {
          return bill.getBill(false, false);
        }
      } else {
        return FAILED;
      }
    } else {
      return "Currently no bill.";
    }
  }

  /**
   * Pays the bill, finishes the order and gives info.
   *
   * @param table the Table to be paid.
   * @param numOfPeople the number of people for this Table.
   * @param payOrNot boolean value showing paid or not
   * @return A string represents the bill or an info.
   */
  public String pay(Table table, int numOfPeople, boolean payOrNot) {
    Bill bill = table.getBill();
    if (bill != null) {
      if (!payOrNot) {
        return bill.getBill(numOfPeople, false);

      } else {
        if (bill.checkOrder()) {
          finishOrder(bill);
          return bill.getBill(numOfPeople, true);
        } else {
          return FAILED;
        }
      }
    } else {
      return "Currently no bill.";
    }
  }

  /**
   * Pays the bill, finishes the order and gives info.
   *
   * @param table the Table to be paid.
   * @param dishes the ArrayList of dishes for this Table.
   * @param payOrNot boolean value showing paid or not
   * @return A string represents the bill or an info.
   */
  public String pay(Table table, ArrayList<Dish> dishes, boolean payOrNot) {
    Bill bill = table.getBill();
    if (bill != null) {
      if (!payOrNot) {
        if (table.getNumPeople() >= 8) {
          return bill.getBill(dishes, false, true);
        } else {
          return bill.getBill(dishes, false, false);
        }
      } else {
        if (table.getBill().checkOrder()) {
          String billStr;
          if (table.getNumPeople() >= 8) {
            billStr = bill.getBill(dishes, true, true);
          } else {
            billStr = bill.getBill(dishes, true, false);
          }

          boolean checkFinish = true;
          for (Dish d : bill.getDishes()) {
            if (d.getStatus() < State.PAID) {
              checkFinish = false;
            }
          }
          if (checkFinish) {
            finishOrder(bill);
          }

          return billStr;
        } else {
          return FAILED;
        }
      }
    } else {
      return "Current no bill.";
    }
  }

  /**
   * After the customers done dining, check anything related, check any dish not made, calculate the
   * price, optionally may need to split the bill
   *
   * @param b the order
   */
  private void finishOrder(Bill b) {
    if (b.checkOrder()) {
      bills.remove(b);
      b.getTable().setStatus(1);
      logManager.finishBillLog(b.getID(), b.getPrice());
    }
  }
}
