package scoreFourGame;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Player
 * The Player class represent a player (human player or computer player) that plays the game.
 * It capture player number, steps moved, player type and the opponent player. Each time a game starts,
 * Two brand new player objects will be created to pass to the BoardPanel to play the game.
 * The player object will be referenced by a cell if the player successfully places a bead in the cell.
 * Cloned copies of player objects will be created to pass to methods to check for the next best move.
 * @author Alyssa Gao, Tara Rafi
 * @version 1.0
 * @since March 6, 2018
 */
public class Player {
  
  /**
   * Player Type enum.
   */
  public enum PlayerType {
    /**
     * Indicate the player is a human player.
     */
    HumanPlayer,
      
      /**
       * Indicate the player is a computer player.
       */
      ComputerPlayer;
  }
  
  // ====== VARIABLES ======
  /**
   * The type of the player (i.e., human or computer player). Once it is set, it cannot be changed.
   */
  private final PlayerType playerType;
  
  /**
   * The no of the player. The valid number can only be 1 or 2, which is used to determine the play sequence.
   * Once it is set, it cannot be changed.
   */
  private final int playerNo;
  
  /**
   * Captures the number steps that the player moved (i.e., placed a bead). It is increased each time a player gets his/her turn to place a bead.
   */
  private int stepsMoved = 0;
  
  /**
   * Captures the opponent player/
   */
  private Player theOtherPlayer = null;
  
  /**
   * The constructor
   * @param playerNo the no of the player. It should be either 1 or 2.
   * @param playType the type of the player, either PlayerType.HumanPlayer or PlayerType.ComputerPlayer.
   */
  public Player ( int playerNo, PlayerType playType ) {
    this.playerNo = playerNo;
    this.playerType = playType;
  }
  
  // =============== METHODS ===============
  
  /**
   * getStepsMoved
   * The getter method of the stepsMoved.
   * @return the stepsMoved value.
   */
  public int getStepsMoved() {
    return stepsMoved;
  }
  
  /**
   * setStepsMoved
   * Set the stepsMoved
   * @param stepsMoved the new stepsMoved value
   */
  public void setStepsMoved(int stepsMoved) {
    this.stepsMoved = stepsMoved;
  }
  
  /**
   * getPlayerNo
   * Returns the player number
   * @return the player number.
   */
  public int getPlayerNo() {
    return playerNo;
  }
  
  /**
   * getPlayerType
   * Returns the player type.
   * @return the player type.
   */
  public PlayerType getPlayerType() {
    return playerType;
  }
  
  /**
   * getTheOtherPlayer
   * Returns the opponent player.
   * @return the opponent player.
   */
  public Player getTheOtherPlayer() {
    return theOtherPlayer;
  }
  
  /**
   * setTheOtherPlayer
   * Set the opponent player.
   * @param theOtherPlayer the opponent player.
   */
  public void setTheOtherPlayer(Player theOtherPlayer) {
    this.theOtherPlayer = theOtherPlayer;
  }
  
  /**
   * setOpponent
   * The convenient method of setting the opponent player for each other.
   * @param theOtherPlayer the opponent player. 
   */
  public void setOpponent ( Player theOtherPlayer ) {
    setTheOtherPlayer(theOtherPlayer);
    theOtherPlayer.setTheOtherPlayer( this );
  }
  
  /**
   * incrementStepsMoved
   * Increase the stepsMoved
   */
  public void incrementStepsMoved ( ) {
    this.stepsMoved ++;
  }
  
  /**
   * toString
   * Returns the string representation of the Player, based on the playerNo and playerType fields. It is used to be
   * displayed in the player selection drop-down list box.
   * @returns the string representation of the Player, based on the playerNo and playerType fields.
   */
   @Override
    public String toString() {
    return ( this.playerType == PlayerType.HumanPlayer ? "Human Player #" : "Computer Player #" ) + Integer.toString( this.playerNo );
  }
  
  /**
   * clone
   * Creates and returns a copy of the Player object with the same playerNo and playerType value.
   * @return a copy of the Player object with the same playerNo and playerType value.
   */
   @Override
    protected Player clone() {
    return new Player ( this.playerNo, this.playerType );
  }
  
  /**
   * play
   * The player moves one step. If it is the computer player, it invokes calculateTheNextBestMove() method to obtain the calculated
   * next best move's coordinate. If it is a human player, it just simply returns a null value, which is not used at all. The actual
   * logic that handles human player's interaction is in the BoardPanel class' mouse click event handler.
   * @param cells the game board cells
   * @return the next best move's coordinate for a ComputerPlayer (or null to indicate cannot move, i.e., tie game) or null for a human player.  
   */
  public Coordinate play ( Cell[][][] cells ) {
    this.incrementStepsMoved();
    if ( this.playerType == PlayerType.ComputerPlayer ) {
      // Algorithm for determining the optimum move
      return calculateTheNextBestMove ( cells );
    } else {
      // do nothing else, the logic is in the BoardPanel's mouse listeners
      return null;
    }
  }
  
  /**
   * calculateTheNextBestMove
   * 1) If I have three beads (not necessarily consecutive) in a row and the 4th is BLANK, returns the coordinate of the 4th position.
   * 2) if the other player has three beads in a row and the 4th is BLANK, returns the coordinate of the 4th position.
   * 3) If I have two beads in a row and the rest positions in the same row are BLANK, returns either one of the BLANK position.
   * 4) If the other player has two beads in a row and the rest positions in the same row are BLANK, returns either one of the BLANK position.
   * Not-to-use -> 5) If the other player has two beads in a row and only the next position to one end is BLANK (the next position to the other end is OCCUPIED or is border), returns the coordinate of the position.
   * Not-to-use -> 6) If I have two beads in a row and only the next position to one end is BLANK (the next position to the other end is OCCUPIED or is border), returns the coordinate of the position.
   * 7) If I have one bead and the rest of the positions are BLANK, return one of these BLANK positions. (Ignore the other player has the same situation)
   * 8) otherwise, choose the first or randomly selection one available position.
   * All positions returned from any of the above rules must be one of the availableMoves. 
   * @param cells the game board cells
   * @return the next best move's coordinate for a ComputerPlayer, or null to indicate cannot move, i.e., tie game.
   */
  public Coordinate calculateTheNextBestMove ( Cell[][][] cells ) {
    
    Player player1 = new Player ( this.playerNo, Player.PlayerType.ComputerPlayer ); // setting as a computer player or human player doesn't affect the evaluation 
    Player player2 = new Player ( this.theOtherPlayer.playerNo, Player.PlayerType.ComputerPlayer );
    player1.setOpponent(player2);// this step is not necessary.
    
    Cell[][][] cellsCopyForEvaluation = makeACopy ( cells, player1, player2 );
    
    Cell[] availableMoves = calculateAvailableMoves(cells);
    
    if ( availableMoves.length == 0 ) {
      return null;
    }
    
    // Rule #1
    Coordinate theNextBestMove = checkTheNextBestMove(cellsCopyForEvaluation, player1, 3, availableMoves);
    if ( theNextBestMove != null ){
      return theNextBestMove;
    }
    
    // Rule #2
    theNextBestMove = checkTheNextBestMove(cellsCopyForEvaluation, player2, 3, availableMoves);
    if ( theNextBestMove != null ){
      return theNextBestMove;
    }
    
    // Rule #3
    theNextBestMove = checkTheNextBestMove(cellsCopyForEvaluation, player1, 2, availableMoves);
    if ( theNextBestMove != null ){
      return theNextBestMove;
    }
    
    // Rule #4
    theNextBestMove = checkTheNextBestMove(cellsCopyForEvaluation, player2, 2, availableMoves);
    if ( theNextBestMove != null ){
      return theNextBestMove;
    }
    
    // Rule #7
    theNextBestMove = checkTheNextBestMove(cellsCopyForEvaluation, player1, 1, availableMoves);
    if ( theNextBestMove != null ){
      return theNextBestMove;
    }
    
    // Rule #8
    Cell cell = availableMoves [ (int) ( Math.random() * availableMoves.length ) ];
    return new Coordinate ( cell.getLayer(), cell.getRow(), cell.getColumn() );
    
  }
  
  /**
   * checkTheNextBestMove
   * Calculate the next best move. The logic structure is similar to Game.checkWinner() method, i.e.,
   * it iterates each cell and then invoke checkTheNextBestMoveInAllDirections() method to check the next best move
   * in all directions (26 directions).
   * @param cells the game board cells
   * @param player the player against whom it will check 
   * @param numOfBeadsInARow the number of beads in a line that it will check
   * @param validMoves all possible moves (cells) that are valid at the current steps by the player (up to 16 moves/cells)  
   * @return the next best move's coordinate for a ComputerPlayer, or null to indicate cannot move, i.e., tie game.
   */
  public Coordinate checkTheNextBestMove(Cell[][][] cells, Player player, int numOfBeadsInARow, Cell[] validMoves ) {
    Set<Cell> recommendedMoves = new HashSet<>();
    for ( int layer = 0; layer < BoardPanel.TOTAL_LAYERS; layer ++ ) {
      for ( int row = 0; row < BoardPanel.TOTAL_ROWS; row ++ ) {
        for ( int column = 0; column < BoardPanel.TOTAL_COLUMNS; column ++ ) {
          Set<Cell> result = checkTheNextBestMoveInAllDirections ( cells, layer, row, column, player, numOfBeadsInARow );
          if ( result.size() > 0 ) {
            recommendedMoves.addAll( result );
          }
        }
      }
    }
    
    return findAValidMoveRandomly ( recommendedMoves, validMoves );
  }
  
  /**
   * findAValidMoveRandomly
   * Randomly returns a coordinate from the recommendedMoves[] that is also in the validMoves[].
   * @param recommendedMoves the recommended moves that calculated by checkTheNextBestMove() method and checkTheNextBestMoveInAllDirections() method.
   * @param validMoves all possible moves (cells) that are valid at the current steps by the player (up to 16 moves/cells)  
   * @return returns a randomly selected coordinate from the recommendedMoves[] that is also in the validMoves[].
   */
  private Coordinate findAValidMoveRandomly(Set<Cell> recommendedMoves, Cell[] validMoves) {
    List<Cell> recommendedAndValidMoves = new ArrayList<>();
    for ( Cell recommendedMove : recommendedMoves ) {
      for ( Cell validMove: validMoves ) {
        if ( (recommendedMove.getLayer() == validMove.getLayer()) && (recommendedMove.getRow() == validMove.getRow()) && (recommendedMove.getColumn() == validMove.getColumn()) ) {
          recommendedAndValidMoves.add ( recommendedMove );
        }
      }
    }
    if ( recommendedAndValidMoves.size() > 0 ) {
      Cell randomlySelectedMove = recommendedAndValidMoves.get ( (int)( Math.random() * recommendedAndValidMoves.size() ) );
      return new Coordinate ( randomlySelectedMove.getLayer(), randomlySelectedMove.getRow(), randomlySelectedMove.getColumn() );
    } else {
      return null;
    }
  }
  
  /**
   * checkTheNextBestMoveInAllDirections
   * Calculate the next best move. The logic structure is similar to Game.checkWinner() method, i.e.,
   * it iterates each cell and then invoke checkTheNextBestMoveInAllDirections() method to check the next best move
   * in all directions (26 directions).
   * @param cells the game board cells
   * @param layer the layer coordinate of the starting point to check for all directions.
   * @param row the row coordinate of the starting point to check for all directions.
   * @param column the column coordinate of the starting point to check for all directions.
   * @param playerToCheck the player against whom it will check 
   * @param numOfBeadsInARow the number of beads in a line that it will check
   * @return the next best move's coordinate for a ComputerPlayer, or null to indicate cannot move, i.e., tie game.
   */
  private Set<Cell> checkTheNextBestMoveInAllDirections(Cell[][][] cells, int layer, int row, int column, Player playerToCheck,
                                                        int numOfBeadsInARow) {
    
    Set<Cell> recommendedMoveToCells = new HashSet<>();
    // iterate all directions: layer+/-1, row+/-1, column+/-1
    for ( int layerDirectionFactor = -1; layerDirectionFactor <= 1; layerDirectionFactor ++ ) {
      for ( int rowDirectionFactor = -1; rowDirectionFactor <= 1; rowDirectionFactor ++ ) {
        for ( int columnDirectionFactor = -1; columnDirectionFactor <= 1; columnDirectionFactor ++ ) {
          // do not execute the checking logic if layerDirectionFactor == rowDirectionFactor == columnDirectionFactor == 0
          if ( (layerDirectionFactor != 0) || (rowDirectionFactor != 0) || (columnDirectionFactor != 0 ) ) {
            Set<Cell> recommendedMoveToCellsForCurrentDirection = new HashSet<>();;
            int totalSameBeadsInTheLine = 0;
            int totalBlankBeadsInTheLine = 0;
            boolean keepLooking = true;
            for ( int k = 0; k < BoardPanel.TOTAL_BEADS_TO_WIN && keepLooking; k ++ ) {
              int newLayer = layer+layerDirectionFactor*k;
              int newRow = row+rowDirectionFactor*k;
              int newColumn = column+columnDirectionFactor*k;
              if ( (newLayer < 0) || (newLayer >= BoardPanel.TOTAL_LAYERS) ||
                  (newRow < 0) || (newRow >= BoardPanel.TOTAL_ROWS) ||
                  (newColumn < 0) || (newColumn >= BoardPanel.TOTAL_COLUMNS) ) {
                // exceeds the boundary
                keepLooking = false;
              } else {
                Player playerPlacedBeadInTheLine = cells[newLayer][newRow][newColumn].getBeadInCell();
                if ( playerPlacedBeadInTheLine == null ) {
                  // Blank cell
                  recommendedMoveToCellsForCurrentDirection.add ( cells[newLayer][newRow][newColumn] );
                  totalBlankBeadsInTheLine ++;
                } else if ( playerPlacedBeadInTheLine == playerToCheck ) {
                  totalSameBeadsInTheLine ++;
                }
              }
            }
            if ( (totalSameBeadsInTheLine == numOfBeadsInARow) && (totalBlankBeadsInTheLine + totalSameBeadsInTheLine == BoardPanel.TOTAL_BEADS_TO_WIN) ) {
              // found pattern
              recommendedMoveToCells.addAll ( recommendedMoveToCellsForCurrentDirection );
            }
          }
        }
      }
    }
    
    return recommendedMoveToCells;
  }
  
  /**
   * findAValidMoveRandomly
   * Randomly returns a coordinate from the recommendedMoves[] that is also in the validMoves[].
   * @param recommendedMoves the recommended moves that calculated by checkTheNextBestMoveFor2DSlice() method or checkTheNextBestMoveFor2DSlice() method.
   * @param validMoves all possible moves (cells) that are valid at the current steps by the player (up to 16 moves/cells)  
   * @return returns a randomly selected coordinate from the recommendedMoves[] that is also in the validMoves[].
   */
  @Deprecated
  private Coordinate findAValidMoveRandomly(Coordinate[] recommendedMoves, Cell[] validMoves) {
    List<Coordinate> recommendedAndValidMoves = new ArrayList<>();
    for ( int k = 0; k < recommendedMoves.length; k ++ ) {
      if ( recommendedMoves[k] != null ) {
        for ( Cell validMove: validMoves ) {
          if ( (recommendedMoves[k].layer == validMove.getLayer()) && (recommendedMoves[k].row == validMove.getRow()) && (recommendedMoves[k].column == validMove.getColumn()) ) {
            recommendedAndValidMoves.add ( recommendedMoves[k] );
          }
        }
      }
    }
    return recommendedAndValidMoves.size() == 0 ? null : recommendedAndValidMoves.get ( (int)( Math.random() * recommendedAndValidMoves.size() ) );
  }
  
  /**
   * calculateAvailableMoves
   * Calculate all available moves based on the current cells.
   * @param cells the board cell of the game.
   * @return all available moves based on the current cells.
   */
  private Cell[] calculateAvailableMoves ( Cell[][][] cells ) {
    List<Cell> availableMoves = new ArrayList<>();
    for ( int row = 0; row < BoardPanel.TOTAL_ROWS ; row ++ ) {
      for ( int column = 0; column < BoardPanel.TOTAL_COLUMNS ; column ++ ) {
        boolean found = false;
        for ( int layer = BoardPanel.TOTAL_LAYERS-1; (layer >= 0) && !found ; layer -- ) {
          if ( cells[layer][row][column].getState() != Cell.State.Occupied ) {
            availableMoves.add( cells[layer][row][column] );
            found = true;
          }
        }
      }
    }
    return availableMoves.toArray(new Cell[0]);
  }
  
  /**
   * makeACopy
   * Creates and returns a copy (not a reference) of the sourceCells. Player objects in the sourceCells[] will be
   * replaced by the provided player1 and player2 objects.
   * @param sourceCells the source cells that are copied from.
   * @param player1 the player object to replace the original player object in the sourceCells.
   * @param player2 the second player object to replace the original player object in the sourceCells.
   * @return returns a copy (not a reference) of the sourceCells.
   */
  private Cell[][][] makeACopy ( Cell[][][] sourceCells, Player player1, Player player2 ) {
    Cell[][][] targetCells = new Cell[sourceCells.length][sourceCells[0].length][sourceCells[0][0].length ];
    for ( int i = 0; i < sourceCells.length; i ++ ) {
      for ( int j = 0; j < sourceCells[i].length; j ++ ) {
        for ( int k = 0; k < sourceCells.length; k ++ ) {
          targetCells[i][j][k] = sourceCells[i][j][k].clone ( );
          if ( targetCells[i][j][k].getBeadInCell() != null ) {
            if ( targetCells[i][j][k].getBeadInCell().getPlayerNo() == player1.getPlayerNo() ) {
              targetCells[i][j][k].setBeadInCell( player1 );
            } else {
              targetCells[i][j][k].setBeadInCell( player2 );
            }
          }
        }
      }
    }
    return targetCells;
  } 
}