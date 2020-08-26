package scoreFourGame;
/**
 * Coordinate
 * Contains layer, row and column to represent a 3D coordinate of a cell.
 * @author Alyssa Gao, Tara Rafi
 * @version 1.0
 * @since March 6, 2019
 */
public class Coordinate {
  
  // =============== VARIABLES ===============
  /**
   * The layer coordinate of a cell.
   */
  public int layer;
  
  /**
   * The row coordinate of a cell.
   */
  public int row;
  
  /**
   * The column coordinate of a cell.
   */
  public int column;
  
  /**
   * The default constructor.
   */
  public Coordinate() {
  }
  
  // =============== METHODS ===============
  /**
   * The constructor of the Coordinate
   * @param layer the layer coordinate of a cell.
   * @param row the row coordinate of a cell.
   * @param column the column coordinate of a cell.
   */
  public Coordinate(int layer, int row, int column) {
    this.layer = layer;
    this.row = row;
    this.column = column;
  } 
}