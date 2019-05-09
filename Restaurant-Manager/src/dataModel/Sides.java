package dataModel;

/** A side object. A side has no size but has a value indicates any possible special. */
public class Sides extends Dish {

  /**
   * Creates a Sides object with given Recipe.
   *
   * @param r Recipe for this Sides.
   */
  public Sides(Recipe r) {
    super(r);
  }

  @Override
  public String getSize() {
    return "";
  }
}
