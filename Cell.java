package scoreFourGame;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Cell
 * The Cell class encapsulates and is responsible for a single cell of the game board.
 * @author Alyssa Gao, Tara Rafi
 * @version 1.0
 * @since March 6, 2019
 */
public class Cell {
  
  // =============== VARIABLES =============== 
  /**
   * The layer of the cell. Once it is set, it cannot be changed.
   */
  private final int layer;
  
  /**
   * The row of the cell. Once it is set, it cannot be changed.
   */
  private final int row;
  
  /**
   * The column of the cell. Once it is set, it cannot be changed.
   */
  private final int column;
  
  /**
   * The X coordinate of four corners of the cell. This is for drawing the cell and for calculating for a given mouse cursor whether it is inside the cell.
   */
  private int cornerX[];
  
  /**
   * The Y coordinate of four corners of the cell. This is for drawing the cell and for calculating for a given mouse cursor whether it is inside the cell.
   */
  private int cornerY[];
  
  /**
   * The player in the cell, default to null.
   */
  private Player beadInCell;
  
  /**
   * Indicate the state of the cell: Blank, Focused, Occupied and Won.
   */
  private State state = State.Blank;
  
  /**
   * The image of the placing bead for player 1.
   */
  private static BufferedImage placingPlayerOneImage = null;
  
  /**
   * The image of the placed bead for player 1.
   */
  private static BufferedImage placedPlayerOneImage = null;
  
  /**
   * The image of the bead that is in the winning line for player 1.
   */
  private static BufferedImage winningPlayerOneImage = null;
  
  /**
   * The image of the placing bead for player 2.
   */
  private static BufferedImage placingPlayerTwoImage = null;
  
  /**
   * The image of the placed bead for player 2.
   */
  private static BufferedImage placedPlayerTwoImage = null;
  
  /**
   * The image of the bead that is in the winning line for player 2.
   */
  private static BufferedImage winningPlayerTwoImage = null;
  
  /**
   * The image file name of the placing bead for player 1.
   */
  public static final String PlacingPlayerOneImageFilename = "pink_planet_0.png";
  
  /**
   * The image file name of the placed bead for player 1.
   */
  public static final String PlacedPlayerOneImageFilename = "pink_planet_1.png";
  
  /**
   * The image file name of the bead that is in the winning line for player 1.
   */
  public static final String WinningPlayerOneImageFilename = "pink_planet_w.png";
  
  /**
   * The image file name of the placing bead for player 2.
   */
  public static final String PlacingPlayerTwoImageFilename = "blue_planet_0.png";
  
  /**
   * The image file name of the placed bead for player 2.
   */
  public static final String PlacedPlayerTwoImageFilename = "blue_planet_1.png";
  
  /**
   * The image file name of the bead that is in the winning line for player 2.
   */
  public static final String WinningPlayerTwoImageFilename = "blue_planet_w.png";
  
  // Initialize all images.
  static {
    try {
      placingPlayerOneImage = ImageIO.read(new File(PlacingPlayerOneImageFilename));
      placedPlayerOneImage = ImageIO.read(new File(PlacedPlayerOneImageFilename));
      winningPlayerOneImage = ImageIO.read(new File(WinningPlayerOneImageFilename));
      placingPlayerTwoImage = ImageIO.read(new File(PlacingPlayerTwoImageFilename));
      placedPlayerTwoImage = ImageIO.read(new File(PlacedPlayerTwoImageFilename));
      winningPlayerTwoImage = ImageIO.read(new File(WinningPlayerTwoImageFilename));
    } catch (IOException e) {
      e.printStackTrace(System.err);
    }
  }
  
  /**
   * Enum to indicate a cell state.
   */
  public enum State {
    /**
     * Indicate the cell is blank (i.e., not occupied).
     */
    Blank,
      
      /**
       * Indicate the cell is occupied. The Cell.beadInCell field should be set too.
       */
      Occupied,
      
      /**
       * Indicate a player is trying to move the mouse in the cell and trying to place (not yet placed) a bead in the cell. 
       */
      Focused, 
      
      /**
       * Indicate the cell is occupied and is in the winning line. The cell is marked to this state through Game.markWinningCells() method.
       */
      Won;
  }
  
  /**
   * Constructor of the Cell class. It also calculate the cornerX[] and cornerY[] arrays, as below:<p>
   * <tt>
   *   &nbsp;&nbsp;A/O-----------------B<br/>
   *   &nbsp;&nbsp;/&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;/<br/>
   *   &nbsp;/&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;/<br/>
   *   D------------------C<br/>
   * 
   * <p>
   * X<sub>O</sub> = BoardPanel.ORIGIN_X + BoardPanel.OFFSETX_3D * (BoardPanel.TOTAL_ROWS - row) + column * BoardPanel.CELL_WIDTH<br/>
   * Y<sub>O</sub> = BoardPanel.ORIGIN_Y + BoardPanel.CELL_HEIGHT * row + (BoardPanel.LAYER_GAP + BoardPanel.TOTAL_ROWS * BoardPanel.CELL_HEIGHT) * layer<p>
   *  
   * corner[] = {<br/> 
   * &nbsp;&nbsp;(X<sub>O</sub>, Y<sub>O</sub>),<br/>
   * &nbsp;&nbsp;&nbsp;&nbsp;(X<sub>O</sub> + BoardPanel.CELL_WIDTH, Y<sub>O</sub>),<br/>
   * &nbsp;&nbsp;&nbsp;&nbsp;(X<sub>O</sub> + BoardPanel.CELL_WIDTH - (int)BoardPanel.OFFSETX_3D, Y<sub>O</sub> + BoardPanel.CELL_HEIGHT),<br/>
   * &nbsp;&nbsp;&nbsp;&nbsp;(X<sub>O</sub> - (int)BoardPanel.OFFSETX_3D, Y<sub>O</sub> + BoardPanel.CELL_HEIGHT)<br/>
   * }
   *</tt><p>
   * 
   * @param layer the layer of the cell.
   * @param row the row of the cell.
   * @param column the column of the cell.
   */
  public Cell( int layer, int row, int column ) {
    
    this.layer = layer;
    this.row = row;
    this.column = column;
    
    int cellOriginX = BoardPanel.ORIGIN_X + (int)BoardPanel.OFFSETX_3D * (BoardPanel.TOTAL_ROWS - row) + column * BoardPanel.CELL_WIDTH;
    int cellOriginY = BoardPanel.ORIGIN_Y + BoardPanel.CELL_HEIGHT * row + (BoardPanel.LAYER_GAP + BoardPanel.TOTAL_ROWS * BoardPanel.CELL_HEIGHT) * layer;
    cornerX = new int[] { cellOriginX, cellOriginX + BoardPanel.CELL_WIDTH, cellOriginX + BoardPanel.CELL_WIDTH - (int)BoardPanel.OFFSETX_3D, cellOriginX - (int)BoardPanel.OFFSETX_3D };
    cornerY = new int[] { cellOriginY, cellOriginY, cellOriginY + BoardPanel.CELL_HEIGHT, cellOriginY + BoardPanel.CELL_HEIGHT };
  }
  
  /**
   * Constructor of the Cell class. It also calculate the cornerX[] and cornerY[] arrays, as below:<p>
   * <tt>
   *   &nbsp;&nbsp;A/O-----------------B<br/>
   *   &nbsp;&nbsp;/&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;/<br/>
   *   &nbsp;/&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;/<br/>
   *   D------------------C<br/>
   * 
   * <p>
   * X<sub>O</sub> = BoardPanel.ORIGIN_X + BoardPanel.OFFSETX_3D * (BoardPanel.TOTAL_ROWS - row) + column * BoardPanel.CELL_WIDTH<br/>
   * Y<sub>O</sub> = BoardPanel.ORIGIN_Y + BoardPanel.CELL_HEIGHT * row + (BoardPanel.LAYER_GAP + BoardPanel.TOTAL_ROWS * BoardPanel.CELL_HEIGHT) * layer<p>
   *  
   * corner[] = {<br/> 
   * &nbsp;&nbsp;(X<sub>O</sub>, Y<sub>O</sub>),<br/>
   * &nbsp;&nbsp;&nbsp;&nbsp;(X<sub>O</sub> + BoardPanel.CELL_WIDTH, Y<sub>O</sub>),<br/>
   * &nbsp;&nbsp;&nbsp;&nbsp;(X<sub>O</sub> + BoardPanel.CELL_WIDTH - (int)BoardPanel.OFFSETX_3D, Y<sub>O</sub> + BoardPanel.CELL_HEIGHT),<br/>
   * &nbsp;&nbsp;&nbsp;&nbsp;(X<sub>O</sub> - (int)BoardPanel.OFFSETX_3D, Y<sub>O</sub> + BoardPanel.CELL_HEIGHT)<br/>
   * }
   *</tt><p>
   * 
   * @param layer the layer of the cell.
   * @param row the row of the cell.
   * @param column the column of the cell.
   * @param beadInCell the Player object that set to this cell.
   * @param state the state set to this cell.
   */
  public Cell( int layer, int row, int column, Player beadInCell, State state ) {
    this ( layer, row, column );
    this.beadInCell = beadInCell;
    this.state = state;  
  }
  
  // =============== METHODS ===============
  
  /**
   * clone
   * Creates and returns a copy of this Cell object. The new object will have the same layer, row, column and state.
   * The beadInCell is cloned using Player.clone() method.
   * @return a copy of this object.
   */
  @Override
  protected Cell clone() {
    return new Cell ( this.layer, this.row, this.column, this.beadInCell == null ? null : this.beadInCell.clone(), this.state );
  }
  
  /**
   * getBeadInCell
   * Getter method for the beadInCell instance variable.
   * @return the beadInCell of the cell.
   */
  public Player getBeadInCell() {
    return beadInCell;
  }
  
  /**
   * setBeadInCell
   * The setter method of the beadInCell instance variable.
   * @param beadInCell the player to set to this cell.
   */
  public void setBeadInCell(Player beadInCell) {
    this.beadInCell = beadInCell;
  }
  
  /**
   * getState
   * The getter method of the state instance variable.
   * @return the state of the cell.
   */
  public State getState() {
    return state;
  }
  
  /**
   * setState
   * The setter method of the state instance variable.
   * @param state the new state to set to this cell.
   */
  public void setState(State state) {
    this.state = state;
  }
  
  /**
   * getLayer
   * The getter method of the layer.
   * @return the layer of the cell.
   */
  public int getLayer() {
    return layer;
  }
  
  /**
   * getRow
   * The getter method of the row
   * @return the row of the cell
   */
  public int getRow() {
    return row;
  }
  
  /**
   * getColumn
   * The getter method of the column
   * @return the column of the cell
   */
  public int getColumn() {
    return column;
  }
  
  /**
   * drawCell
   * Draw the cell on the UI. It uses the player number & state information to determine how to draw the beads.
   * @param g the Graphics object that is used to draw on the UI.
   * @param player the player object using which to determine how to draw the bead on the UI.
   */
  public void drawCell ( Graphics g, Player player ) {
    if ( this.state != State.Blank ) {
      Image image = null;
      switch ( this.state ) {
        case Occupied:
          image = this.beadInCell.getPlayerNo() == 1 ? placedPlayerOneImage : placedPlayerTwoImage;
        break;
        case Won:
          image = this.beadInCell.getPlayerNo() == 1 ? winningPlayerOneImage : winningPlayerTwoImage;
        break;
        case Focused:
          if ( player != null ) {
          image = player.getPlayerNo() == 1 ? placingPlayerOneImage : placingPlayerTwoImage;
        }
          break;
      }
      g.setColor( Color.WHITE );
      g.drawPolygon(cornerX, cornerY, 4);
      g.drawImage(image, cornerX[0] + 5, cornerY[0] - 25, null );
    } else {
      g.setColor( Color.WHITE );
      g.drawPolygon(cornerX, cornerY, 4);
    }
  }
  
  /**
   * isCoordinateInsideCell
   * To calculate if the provided coordinate (x, y) is inside the cell.
   * @param x the x coordinate that is required to decided whether it is inside the cell
   * @param y the y coordinate that is required to decided whether it is inside the cell
   * @return true if the provided coordinate (x, y) is inside the cell, false otherwise.
   */
  public boolean isCoordinateInsideCell ( int x, int y ) {
    // determine slope and b in equation: y = slope * x + b of two side edges (line #1, line #2)
    /* Line1: y = slope*x + b1
     Line2: y = slope*x + b2
     
     To calculate slope
     y0 = slope*x0 + slope1 (1)
     y3 = slope*x3 + slope1 (2)
     (1)-(2): y0 – y3 = slope(x0-x3) => slope=(y0-y3)/(x0-x3)
     
     b1 = y0 – x0*slope
     b2 = y1 – x1*slope
     
     xValueLineOne = (y – b1) / slope
     xValueLineTwo = (y – b2) / slope
     */
    double slope = ( (double) this.cornerY[0] - this.cornerY[3] ) / ( (double) this.cornerX[0] - this.cornerX[3] ) ;
    double b1 = this.cornerY[0] - this.cornerX[0] * slope;
    double b2 = this.cornerY[1] - this.cornerX[1] * slope;
    double xValueLineOne = (y - b1) / slope;
    double xValueLineTwo = (y - b2) / slope;
    return y >= this.cornerY[0] && y <= this.cornerY[3] && x >= xValueLineOne && x <= xValueLineTwo;
  } 
}