package dataModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * The manager of table, take charge of the current selection of Table, plus the add/remove of
 * tables selectedTable: the table which is currently selected. Every action will be performed on it
 * tables: an observable list of tables, which contains all the tables exist
 */
public class TableManager {
  private Table selectedTable;
  private ObservableList<Table> tables;

  TableManager() {
    tables = FXCollections.observableArrayList();
    selectedTable = null;
  }

  public ObservableList<Table> getTables() {
    return tables;
  }

  public Table getSelectedTable() {
    return selectedTable;
  }

  public void setSelectedTable(Table selectedTable) {
    this.selectedTable = selectedTable;
  }

  public void setSelectedTableStatus(int status) {
    selectedTable.setStatus(status);
  }
}
