package dataModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A CookManager object is in charge of the information of kitchen, e.g., finish dish, add dish,
 * etc.
 *
 * <p>cookQueue the queue of dish which is waiting to be finished. deliveryQueue the queue of dish
 * in delivering. logManager the LogManager given for logging.
 */
public class CookManager {

  private ObservableList<Order> cookQueue;
  private ObservableList<Order> deliveryQueue;
  private LogManager logManager;

  /** creates a CookManager object. */
  public CookManager() {
    cookQueue = FXCollections.observableArrayList();
    deliveryQueue = FXCollections.observableArrayList();
  }

  // setter of the logManager of this CookManager.
  public void setLogManager(LogManager logManager) {
    this.logManager = logManager;
  }

  // getters of the properties of this CookManager.
  public ObservableList<Order> getCookQueue() {
    return cookQueue;
  }

  public ObservableList<Order> getDeliveryQueue() {
    return deliveryQueue;
  }

  /**
   * adds dishes to a cookQueue
   *
   * @param order the bill
   */
  public void addCookQueue(Order order) {
    cookQueue.add(order);
  }

  /**
   * cancels the cook task with given Dish and IngredientsManager.
   *
   * @param d Dish given for removing.
   * @param ingredientsManager IngredientsManager.
   */
  public void cancelCookTask(Dish d, IngredientsManager ingredientsManager) {
    if (d.getStatus() < State.COOKING) {
      for (Ingredient i : d.getIngredients().keySet()) {
        ingredientsManager.addStock(i, d.getIngredients().get(i));
      }
    }
    d.setStatus(State.CANCELLED);
    d.getToWhichOrder().removeDish(d);
    d.getToWhichBill().removeDish(d);
    if (d.getToWhichOrder().getDishes().isEmpty()) {
      if (cookQueue.contains(d.getToWhichOrder())) {
        cookQueue.remove(d.getToWhichOrder());
      }
      if (deliveryQueue.contains(d.getToWhichOrder())) {
        deliveryQueue.remove(d.getToWhichOrder());
      }
    }
  }

  /**
   * confirms the cook task with given order.
   *
   * @param order Order given for confirming.
   */
  public boolean confirmCookTask(Order order) {
    for (int i = 0; i < getCookQueue().indexOf(order); i++) {
      if (getCookQueue().get(i).getStatus() < State.COOKING) {
        return false;
      }
    }
    order.changeStatus(State.COOKING);
    return true;
  }

  /**
   * finishes the cook task based on the order given.
   *
   * @param order Order given for finishing.
   */
  public void finishCookTask(Order order) {
    if (order.getStatus() == State.COOKING) {
      order.changeStatus(State.COOKED);
      cookQueue.remove(order);
      deliveryQueue.add(order);
    }
  }

  /**
   * delivers the cook task based on the order given.
   *
   * @param o Order given for delivering.
   */
  public void deliverOrder(Order o) {
    if (o.getStatus() == State.COOKED) {
      deliveryQueue.remove(o);
      o.setStatus(State.DELIVERED);
      for (Dish d : o.getDishes()) {
        d.setStatus(State.DELIVERED);
      }
    }
  }

  /**
   * Handle the issues relating to any dish in this order, issues are remake, replace, reprocess
   *
   * @param cases the string of cases the customer's facing
   */
  public boolean handleCases(Dish d, String cases) {
    switch (cases) {
      case "cancel":
        if (d.getStatus() == State.ORDERED) d.cancelIngredient();
        d.setStatus(State.CANCELLED);
        return true;
      case "remake":
        if (d.checkAvailable()) {
          d.useIngredient();
          logManager.addAction(d.getToWhichBill().getID(), "remake " + d.getName());
          d.setStatus(State.ORDERED);
        } else {
          return false;
        }
        return true;
      case "reheat":
        d.setStatus(State.ORDERED);
        return true;
    }
    return false;
  }
}
