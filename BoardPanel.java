package scoreFourGame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * BoardPanel
 * A JPanel that maintain the 3D Connect Four game board. 
 * @author Alyssa Gao, Tara Rafi
 * @version 1.0
 * @since March 6, 2019
 */
public class BoardPanel extends JPanel {
  
  // =============== VARIABLES ===============
  /**
   * The start X coordinate of the top layer (i.e., the top-left corner of the cell(0,0,0) or first row, first column at layer 4
   */
  public static final int ORIGIN_X = 50;
  
  /**
   * The start Y coordinate of the top layer (i.e., the top-left corner of the cell(0,0,0) or first row, first column at layer 4
   */
  public static final int ORIGIN_Y = 50;
  
  /**
   * Total numbers of Layer, default to 4 layers
   */
  public static int TOTAL_LAYERS = 4;
  
  /**
   * Total numbers of Rows, default to 4 rows.
   */
  public static int TOTAL_ROWS = 4;
  
  /**
   * Total numbers of Columns, default to 4 columns.
   */
  public static int TOTAL_COLUMNS = 4;
  
  /**
   * Total numbers of cells in a line to win, default to 4 cells.
   */
  public static final int TOTAL_BEADS_TO_WIN = 4;
  
  /**
   * The gap between layers, default to 25 pixels.
   */
  public static final int LAYER_GAP = 25;
  
  /**
   * The width of each cell, default to 100 pixels.
   */
  public static final int CELL_WIDTH = 100;
  
  /**
   * The height of each cell, default to 25 pixels.
   */
  public static final int CELL_HEIGHT = 25;
  
  /**
   * A calculated distance that the bottom line shifts from the top line of a cell, assuming the 
   * angle between the left edge and the bottom edge is 60 degrees. 
   */
  public static final double OFFSETX_3D = Math.tan( Math.toRadians( 60 ) ) * CELL_HEIGHT;
  
  /**
   * The currentPlayer who is in turn the game, default to null.
   */
  private Player currentPlayer = null;
  
  /**
   * The main board cells.
   */
  private Cell cells[][][] = null;
  
  /**
   * A boolean attribute to indicate whether the game has started. By default it is set to false.
   * It is set to true when a game starts, and is set to false when a game is over (one player wins or a tie) 
   */
  private boolean gameStarted = false;
  
  /**
   * Used to remember the cell where a player was trying to place a bead in. It is captured during
   * the mouse move event, and is cleared when a mouse is moved outside a cell, or a mouse click event is captured.
   */
  private Cell lastFocusedCell = null;
  
  /**
   * A reference to the current game.
   */
  private Game game;
   
  /**
   * The background image file name.
   */
  public static final String BackgroundImageFilename = "galaxy.jpg";

  /**
   * The background image.
   */
  private static BufferedImage backgroundImage = null;

  // Initialize all images.
  static {
    try {
     backgroundImage = ImageIO.read(new File(BackgroundImageFilename));
    } catch (IOException e) {
      e.printStackTrace(System.err);
    }
  }
  
  /**
   * The constructor of the BoardPanel.
   * @param game the current game object.
   */
  public BoardPanel( Game game ) {
    
    this.game = game;
    
    // Register a mouse click event handler
    setBorder(BorderFactory.createLineBorder(Color.black));
    addMouseListener ( new MouseAdapter() {
      
      /**
       * mouseClicked
       * Overridden method in the situation that mouse is clicked
       * @param e mouse action
       */
      @Override
      public void mouseClicked(MouseEvent e) {
        if ( gameStarted ) {
          for ( int layer = 0; layer < TOTAL_LAYERS; layer ++ ) {
            for ( int row = 0; row < TOTAL_ROWS; row ++ ) {
              for ( int column = 0; column < TOTAL_COLUMNS; column ++ ) {
                if ( cells[layer][row][column].isCoordinateInsideCell(e.getX(), e.getY()) ) {
                  if ( isCellAvailable ( layer, row, column, true ) ) {
                    // When the mouse cursor coordinate is within a cell, and the current cell is a valid 
                    // move (i.e., the current cell is BLANK, and a lower layer (same row and column) is not BLANK,
                    // set the currentPlayer to the cell, and also set the state to "Occupied"
                    cells[layer][row][column].setState( Cell.State.Occupied );
                    cells[layer][row][column].setBeadInCell( currentPlayer );
                    currentPlayer.play(cells);
                    // Clear the lastFocusedCell if it is applicable.
                    if ( lastFocusedCell != null ) {
                      if ( lastFocusedCell.getState() == Cell.State.Focused ) {
                        lastFocusedCell.setState( Cell.State.Blank );
                      }
                      lastFocusedCell = null;
                    }
                    repaint();
                    
                    // check if there is a winner after each move.
                    if ( checkWinner() ) {
                      return;
                    }
                    
                    // Check if it is a tie.
                    if ( calculateNumOfBlankCells(cells) == 0 ) {
                      game.setInfo( "Draw game." );
                      gameStarted = false;
                      return;
                    }
                    
                    // Switch players
                    currentPlayer = currentPlayer.getTheOtherPlayer();
                    if ( currentPlayer.getPlayerType() == Player.PlayerType.ComputerPlayer ) {
                      // if the next player is a computer player
                      game.setInfo ( "Player " + currentPlayer.getPlayerNo() + " (Computer player) is thinking . . ." );
                      game.repaint();
                      Coordinate coordinate = currentPlayer.play(cells);
                      if ( coordinate == null ) {
                        // This is a tie
                        game.setInfo( "Draw game." );
                        gameStarted = false;
                        return;
                      } else {
                        // Not a tie (maybe win, so need to check ...
                        cells[coordinate.layer][coordinate.row][coordinate.column].setBeadInCell(currentPlayer);
                        cells[coordinate.layer][coordinate.row][coordinate.column].setState ( Cell.State.Occupied );
                        // check if there is a winner after each move.
                        if ( checkWinner() ) {
                          return;
                        }
                      }
                      // switch player again (no need to check if the other player is a computer player (i.e., both players are computer player, 
                      // as it has been handled at the beginning of the game (in the "New Game" button click event handler).
                      currentPlayer = currentPlayer.getTheOtherPlayer();
                    }
                    game.setInfo ( "Player " + currentPlayer.getPlayerNo() + " to place a bead . . ." );
                    return;
                  }
                }
              }
            }
          }
        }
      }
    });
    
    // Register a mouse move event handler
    addMouseMotionListener ( new MouseMotionAdapter ( ) {
      
       /**
       * mouseMoved
       * Overridden method in the situation that mouse moving/hovering over a cell
       * @param e mouse action
       */
      @Override
      public void mouseMoved(MouseEvent e) {
        if ( gameStarted ) {
          // Check if a cell can be selected by passing the mouse cursor's coordinate
          for ( int layer = 0; layer < TOTAL_LAYERS; layer ++ ) {
            for ( int row = 0; row < TOTAL_ROWS; row ++ ) {
              for ( int column = 0; column < TOTAL_COLUMNS; column ++ ) {
                if ( cells[layer][row][column].isCoordinateInsideCell(e.getX(), e.getY()) ) {
                  if ( isCellAvailable ( layer, row, column, false ) ) {
                    // When the mouse cursor coordinate is within a cell, and the current cell is a valid 
                    // move (i.e., the current cell is BLANK, and a lower layer (same row and column) is not BLANK,
                    // mark state to "Focused".
                    // No need to worry for frequently execute the logic for the same cell
                    // as the isCellAvailable() method returns true only if the current cell is BLANK.
                    cells[layer][row][column].setState( Cell.State.Focused );
                    // Clear the lastFocusedCell (set the rest to BLANK).
                    if ( lastFocusedCell != null ) {
                      lastFocusedCell.setState( Cell.State.Blank );
                    }
                    lastFocusedCell = cells[layer][row][column];
                    repaint();
                  } else if ( (lastFocusedCell != null) && (cells[layer][row][column] != lastFocusedCell) ){
                    // Reset the lastFocusedCell's state to BLANK, ONLY IF it is not the same cell where the mouse cursor is now
                    lastFocusedCell.setState( Cell.State.Blank );
                    lastFocusedCell = null;
                    repaint();
                  }
                  return;
                }
              }
            }
          }
          // Reset the lastFocusedCell's state to BLANK. This is a catch-up run if the mouse is not in any available cell.
          if ( lastFocusedCell != null ) {
            lastFocusedCell.setState( Cell.State.Blank );
            lastFocusedCell = null;
            repaint();
          }
        }
      }
    });
    initCells ( ); 
  }
  
  // =============== METHODS ===============
  
  /**
   * setBoardSize
   * Sets the number of layers, rows and columns for the overall board. 
   * @param size the number of layers, rows and columns
   */
  public static void setBoardSize ( int size ) {
    TOTAL_LAYERS = size;
    TOTAL_ROWS = size;
    TOTAL_COLUMNS = size;
  }
  
  /**
   * setCell
   * Set the specified player to the cell, and mark the cell's state as the specified state. 
   * @param layer the layer of the cell.
   * @param row the row of the cell.
   * @param column the column of the cell.
   * @param player the player to set to the cell.
   * @param state the state to set as the cells' state.
   */
  public void setCell ( int layer, int row, int column, Player player, Cell.State state ) {
    this.cells[layer][row][column].setBeadInCell ( player );
    cells[layer][row][column].setState ( state );
  }
  
  /**
   * setCell
   * Set the specified player to the cell, and mark the cell's state as the specified state.
   * @param coordinate the coordinate of the cell.
   * @param player the player to set to the cell.
   * @param state the state to set as the cells' state.
   */
  public void setCell ( Coordinate coordinate, Player player, Cell.State state ) {
    setCell ( coordinate.layer, coordinate.row, coordinate.column, player, state );
  }
  
  /**
   * calculateNumOfBlankCells
   * Calculate number of cells that have BLANK state.
   * @param cells The 3D cells to check and calculate.
   * @return the number of cells that have BLANK state, or zero if no move available.
   */
  protected int calculateNumOfBlankCells(Cell[][][] cells) {
    int numOfBlankCells = 0;
    for ( int layer = 0; layer < TOTAL_LAYERS; layer ++ ) {
      for ( int row = 0; row < TOTAL_ROWS; row ++ ) {
        for ( int column = 0; column < TOTAL_COLUMNS; column ++ ) {
          if ( cells[layer][row][column].getState() == Cell.State.Blank ) {
            numOfBlankCells ++;
          }
        }
      }
    }
    return numOfBlankCells;
  }
  
  /**
   * isCellAvailable
   * Check if the cell is available for selection or placing a bead.
   * Considers if cell is blank or focused as well as there being a bead directly benath the cell if it is not the bottom layer
   * @param layer the layer of the cell.
   * @param row the row of the cell.
   * @param column the column of the cell.
   * @param isForPlacingBead true for placing a bead in the cell (via mouse-click event), false for checking for availability to focus (via mouse-move event).
   * @return true if the cell is available, false otherwise
   */
  protected boolean isCellAvailable(int layer, int row, int column, boolean isForPlacingBead ) {
    return ( (cells[layer][row][column].getState() == Cell.State.Blank)
              || ((isForPlacingBead) && (cells[layer][row][column].getState() == Cell.State.Focused)) ) 
      && ( (layer == TOTAL_LAYERS-1) || (cells[layer+1][row][column].getState() != Cell.State.Blank) );
  }
  
  /**
   * checkWinner
   * Check if a winning line exists. It invokes Game.checkWinner() method and also updates the Game.infoLabel. 
   * @return true if a winning line exists, false if there is not a winning line formed.
   */
  public boolean checkWinner ( ) {
    Cell[] winningCells = game.checkWinner ( cells );
    if ( winningCells != null ) {
      gameStarted = false;
      game.setInfo(winningCells[0].getBeadInCell().toString() + " has won in "
                     + winningCells[0].getBeadInCell().getStepsMoved() + " steps.");
      return true;
    } else {
      return false;
    }
  }
  
  /**
   * initCells
   * Initializes the 3D cells of the game board.
   */
  public void initCells() {
    this.cells = new Cell [TOTAL_LAYERS][TOTAL_ROWS][TOTAL_COLUMNS];
    for ( int layer = 0; layer < TOTAL_LAYERS; layer ++ ) {
      for ( int row = 0; row < TOTAL_ROWS; row ++ ) {
        for ( int column = 0; column < TOTAL_COLUMNS; column ++ ) {
          cells[layer][row][column] = new Cell ( layer, row, column );
        }
      }
    }
    repaint();
  }
  
  /**
   * getPreferredSize
   * Returns the preferred size of the JPanel.
   * @return the preferred size of the JPanel.
   */
  @Override
  public Dimension getPreferredSize() {
    return new Dimension (
                          ORIGIN_X + (int)OFFSETX_3D*TOTAL_COLUMNS + TOTAL_COLUMNS * CELL_WIDTH,
                          ORIGIN_Y + (LAYER_GAP + TOTAL_ROWS * CELL_HEIGHT) * TOTAL_LAYERS
                         );
  }
  
  /**
   * paintComponent
   * Clear the panel and paint all cells based on individual cells (via individal Cell.draw() method).
   * @param g the Graphics object for draw cells. 
   */
  protected void paintComponent(Graphics g) {
    super.paintComponents(g);
    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null );
    
    for ( int layer = 0; layer < TOTAL_LAYERS; layer ++ ) {
      for ( int row = 0; row < TOTAL_ROWS; row ++ ) {
        for ( int column = 0; column < TOTAL_COLUMNS; column ++ ) {
          cells[layer][row][column].drawCell(g, currentPlayer);
        }
      }
    }
  }
  
  /**
   * getCurrentPlayer
   * The getter method of the currentPlayer
   * @return the currentPlayer
   */
  public Player getCurrentPlayer() {
    return currentPlayer;
  }
  
  /**
   * setCurrentPlayer
   * Set the currentPlayer
   * @param currentPlayer the new currentPlayer to set.
   */
  public void setCurrentPlayer(Player currentPlayer) {
    this.currentPlayer = currentPlayer;
  }
  
  /**
   * isGameStarted
   * The getter method of the gameStarted
   * @return true if game has started.
   */
  public boolean isGameStarted() {
    return gameStarted;
  }
  
  /**
   * setGameStarted
   * Set the gameStarted's new value
   * @param gameStarted the new value to set to the gameStated instance variable.
   */
  public void setGameStarted(boolean gameStarted) {
    this.gameStarted = gameStarted;
  }
  
  /**
   * getCells
   * The getter method of the board cells array.
   * @return the board cells array.
   */
  public Cell[][][] getCells() {
    return cells;
  }
}