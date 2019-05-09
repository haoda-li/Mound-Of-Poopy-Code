package dataModel;

import java.util.HashMap;
import java.util.Map;

/** A set of values for describing the state of the Dish. */
public class State {
  public static int ORDERED = 0;
  public static int CANCELLED = -1;
  public static int COOKING = 1;
  public static int COOKED = 2;
  public static int DELIVERED = 3;
  public static int PAID = 4;
  public static int ORDERFINISHED = 5;

  public static Map<Integer, String> stateMap =
      new HashMap<Integer, String>() {
        {
          put(0, "Ordered");
          put(-1, "Cancelled");
          put(1, "Cooking");
          put(2, "Cooked");
          put(3, "Delivered");
          put(4, "Paid");
          put(5, "Finished");
        }
      };
}
