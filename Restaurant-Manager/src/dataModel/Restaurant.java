package dataModel;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The most top manager who takes charge of "billManager", "cookManager" and "ingredientManager".
 *
 * <p>cookManager the cook of this restaurant ingredientManager the manager who is in charge of
 * ingredient factory of this restaurant logManager the manager who deals with the log of this
 * restaurant billManager the manager who deals with customer's orders menu a menu which has all the
 * food that the restaurant can offer
 */
public class Restaurant {

  private BillManager billManager;
  private CookManager cookManager;
  private IngredientsManager ingredientManager;
  private LogManager logManager;
  private Menu menu;
  private TableManager tableManager;

  public Restaurant(String menuText, String ingredientsText) {
    billManager = new BillManager();
    cookManager = new CookManager();
    logManager = new LogManager();
    tableManager = new TableManager();

    ingredientManager = new IngredientsManager();
    ingredientManager.initIM(ingredientsText);

    menu = new Menu();
    menu.initMenu(menuText);
    for (int i = 1; i < 9; i++) {
      tableManager.getTables().add(new Table(i));
    }
    tableManager.setSelectedTable(tableManager.getTables().get(0));

    ingredientManager.setLogManager(logManager);
    billManager.setLogManager(logManager);
    cookManager.setLogManager(logManager);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter("request.txt"))) {
      writer.write("");
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public IngredientsManager getIngredientManager() {
    return ingredientManager;
  }

  public CookManager getCookManager() {
    return cookManager;
  }

  public LogManager getLogManager() {
    return logManager;
  }

  public Menu getMenu() {
    return menu;
  }

  public BillManager getBillManager() {
    return billManager;
  }

  public TableManager getTableManager() {
    return tableManager;
  }
}
