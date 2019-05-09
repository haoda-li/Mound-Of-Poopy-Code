package dataModel;

/**
 * Table class, which contains all the information of a table in the restaurant id: the id of table
 * bill: the bill of people in this table status: 0 if this table is occupied, 1 if this table is
 * unoccupied, 2 if this table is broken numPeople: the number of people sitting on this table
 */
public class Table {
  private int id;
  private int status;
  private Bill bill;
  private int numPeople;
  public static int OCCUPIED = 0;
  public static int UNOCCUPIED = 1;
  public static int UNAVAILABLE = 2;

  public Table(int id) {
    this.id = id;
    this.status = UNOCCUPIED;
    this.numPeople = 0;
  }

  /**
   * get the status of this table
   *
   * @return 0 represents occupied, 1 represents unoccupied, 2 represents unavailable
   */
  public int getStatus() {
    return status;
  }

  /**
   * Set status of table. If set to unoccupied, we delete the bill of this table
   *
   * @param status 0 represents occupied, 1 represents unoccupied, 2 represents unavailable
   */
  public void setStatus(int status) {
    this.status = status;
    if (status == UNOCCUPIED) {
      bill = null;
    }
  }

  /**
   * get the number of people sitting in this table
   *
   * @return number of people sitting in this table
   */
  public int getNumPeople() {
    return numPeople;
  }

  /** @return the id of this table */
  public int getID() {
    return id;
  }

  /**
   * set the number of people will sit in this table.
   *
   * @param num number of people
   */
  public void setNumPeople(int num) {
    numPeople = num;
  }

  /**
   * set the bill of this table, succeed iff this table is occupied
   *
   * @param o the bill which is gonna be set
   */
  public void setBill(Bill o) {
    if (isOccupied()) {
      bill = o;
    }
  }

  /** @return if this table is occupied */
  public boolean isOccupied() {
    return status == OCCUPIED;
  }

  /** @return if this table is available */
  public boolean isAvailable() {
    return status == UNOCCUPIED;
  }

  /** @return the bill of this table */
  public Bill getBill() {
    return bill;
  }

  @Override
  public String toString() {
    return String.valueOf(id);
  }
}
