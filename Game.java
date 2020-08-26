package scoreFourGame;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Game
 * The main program of the Score Four game.
 * @author Alyssa Gao, Tara Rafi
 * @version 1.0
 * @since March 6, 2019
 */
public class Game extends JFrame {
  
  // =============== VARIABLES ===============
  /**
   * The panel that holds the game board
   */
  private BoardPanel gameMainPanel;
  
  /**
   * The main panel that holds all player interactive UI controls.
   */
  private JPanel commandPanel;
  
  /**
   * A JLabel that is used for displaying instructions and the game states. 
   */
  private JLabel infoLabel;
  
  /**
   * The label of the drop-down list for choosing the Player 1 
   */
  private JLabel player1ChoiceLabel;
  
  /**
   * The drop-down list for choosing the Player 1 
   */
  private JComboBox<Player> player1ChoiceComboBox;
  
  /**
   * The label of the drop-down list for choosing the Player 2 
   */
  private JLabel player2ChoiceLabel;
  
  /**
   * The drop-down list for choosing the Player 2 
   */
  private JComboBox<Player> player2ChoiceComboBox;
  
  /**
   * The button to start a new game. 
   */
  private JButton newGameButton;
  
  /**
   * The button to exit the game. 
   */
  private JButton exitButton;
  
  /**
   * The Player 1 object. Each time when a new game is started, a cloned copy of the Player object 
   * will be obtained from the drop-down list (JComboBox) for choosing the Player 1 and eventually 
   * is passed to the gameMainPanel as the currentPlayer.
   */
  private Player player1 = null;
  
  /**
   * The Player 2 object. Each time when a new game is started, a cloned copy of the Player object 
   * will be obtained from the drop-down list (JComboBox) for choosing the Player 2 and eventually 
   * is passed to the gameMainPanel as the currentPlayer.
   */
  private Player player2 = null;
  
  /**
   * A static instance of the current game. It is for other class method to invoke Game's instance methods.
   */
  public static Game currentGame; 
  
  /**
   * The constructor of the Game class. It initializes the Game UI and set the currentGame to this
   */
  public Game() {
    initUI();
    currentGame = this;
  }
  
  // =============== METHODS ===============
  
  /**
   * main
   * The entry point (main method) of the Game.
   * @param args command-line argument. It is not used in the game.
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        Game game = new Game();
        game.setVisible(true);
      }
    });
  }
  
  /**
   * initUI
   * Initialize the Game UI. It consists two major panels: a gameMainPanel (BoardPanel) and a commandPanel (JPanel).
   */
  private void initUI() {
    
    setLayout(new BorderLayout(10, 10));
    
    gameMainPanel = new BoardPanel(this);
    commandPanel = new JPanel();
    commandPanel.setPreferredSize(new Dimension(600, 150));
    
    add ( new JScrollPane ( gameMainPanel ) );
    add(commandPanel, BorderLayout.SOUTH);
    
    infoLabel = new JLabel("Click \"New Game\" button to start a game . . .");
    infoLabel.setPreferredSize(new Dimension(600, 25));
    player1ChoiceLabel = new JLabel("Player 1:");
    player1ChoiceComboBox = new JComboBox<>(
                                            new Player[] { new Player(1, Player.PlayerType.HumanPlayer), new Player(1, Player.PlayerType.ComputerPlayer) });
    player2ChoiceLabel = new JLabel("Player 2:");
    player2ChoiceComboBox = new JComboBox<>(
                                            new Player[] { new Player(2, Player.PlayerType.HumanPlayer), new Player(2, Player.PlayerType.ComputerPlayer) });
    newGameButton = new JButton("New Game");
    newGameButton.addActionListener(new ActionListener() {
      
      /**
       * actionPerformed
       * Overrided method to start new game
       * @param e the action ocurred
       */
      @Override
      public void actionPerformed(ActionEvent e) {
        startNewGame ( );
      }
      
    });
    // Exit button and handler
    exitButton = new JButton("Exit");
    exitButton.addActionListener(new ActionListener() {
      
      /**
       * actionPerformed
       * Overrided method to exit game
       * @param e the action ocurred
       */
      @Override
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
      
    });
    
    // Build command panels: infoPanel + controlPanel (controlPanel1 + controlPanel2)
    JPanel infoPanel = new JPanel ( new FlowLayout( FlowLayout.LEFT ) );
    infoPanel.add(infoLabel);
    JPanel controlPanel = new JPanel ( new BorderLayout() );
    JPanel controlPanel1 = new JPanel ( new FlowLayout( FlowLayout.LEFT ) );
    controlPanel1.add(player1ChoiceLabel);
    controlPanel1.add(player1ChoiceComboBox);
    controlPanel1.add(player2ChoiceLabel);
    controlPanel1.add(player2ChoiceComboBox);
    controlPanel1.add(newGameButton);
    controlPanel1.add(exitButton);
    
    JPanel controlPanel2 = new JPanel ( new FlowLayout( FlowLayout.LEFT ) );
    JLabel gameBoardSizeLabel = new JLabel ( "Board size:" );
    SpinnerModel boardSizeModel = new SpinnerNumberModel ( 4, 4, 10, 1 );
    JSpinner boardSizeSpinner = new JSpinner(boardSizeModel);
    boardSizeSpinner.setEditor(new JSpinner.NumberEditor(boardSizeSpinner, "#"));
    boardSizeSpinner.setPreferredSize( new Dimension ( 100, 25 ) );
    boardSizeSpinner.addChangeListener( new ChangeListener ( ) {
      
      /**
       * stateChanged
       * Overrided method that checks if the size of the board is changed as well as if a game is currently ocurring
       * @param e the change ocurred
       */
      @Override
      public void stateChanged(ChangeEvent e) {
        
        JSpinner spinner = (JSpinner) e.getSource();
        Integer size = (Integer) spinner.getValue();
        int originalSize = BoardPanel.TOTAL_LAYERS;
        if ( originalSize != size ) {
          // A new size value is input.
          if ( gameMainPanel.isGameStarted() ) {
            // Display warning message if the game has already started.
            int ret = JOptionPane.showConfirmDialog ( currentGame, 
                                                     "The game is in progress. If you proceed the change to the game board size, the game will be restarted. The current game will be discarded. Do you want to proceed?", "Change Game Board Size",
                                                     JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );
            if ( ret == JOptionPane.YES_OPTION ) {
              BoardPanel.setBoardSize ( size );
              startNewGame ( );
            } else {
              spinner.setValue( originalSize );
              return;
            }
          } else {
            BoardPanel.setBoardSize ( size );
          }
          gameMainPanel.initCells();
          setSize ( getCalculatedSize() );
          repaint();
        }
        
      } 
      
    });
    
    controlPanel2.add ( gameBoardSizeLabel, BorderLayout.WEST );
    controlPanel2.add ( boardSizeSpinner );
    
    controlPanel.add( controlPanel1, BorderLayout.NORTH );
    controlPanel.add( controlPanel2, BorderLayout.SOUTH );
    
    commandPanel.add( infoPanel );
    commandPanel.add( controlPanel );
    
    setTitle("3D Connect Four Game");
    // The main JFrame size is calculated based on the sizes of gameMainPanel and commandPanel.
    setSize( getCalculatedSize() );
    
    setLocationRelativeTo(null);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    
  }
  
  /**
   * getCalculatedSize
   * Gets the calculated size for panels
   * @return dimension for calculated size
   */
  public Dimension getCalculatedSize() {
    return new Dimension ( Math.max(gameMainPanel.getPreferredSize().width, commandPanel.getPreferredSize().width) + 50,
                          gameMainPanel.getPreferredSize().height + commandPanel.getPreferredSize().height + 50 );
  }
  
  /**
   * startNewGame
   * Start a new game. This method is invoked when the player clicks "New Game" button, or is triggered when the 
   * game board size is changed.
   */
  protected void startNewGame() {
    gameMainPanel.initCells();
    // obtains cloned copies from the player choice drop-down list. This avoids different games to share
    // the same player instance, i.e., isolates player objects used in different games.
    player1 = ((Player) player1ChoiceComboBox.getSelectedItem()).clone();
    player2 = ((Player) player2ChoiceComboBox.getSelectedItem()).clone();
    player1.setOpponent ( player2 );
    
    // start the first step by invoking the Player.play() depending if the player is a computer player or not
    if ( (player1.getPlayerType() == Player.PlayerType.ComputerPlayer) && (player2.getPlayerType() == Player.PlayerType.ComputerPlayer) ) {
      // for the case where the both players are computer players
      Player currentPlayer = player1;
      while ( true ) {
        setInfo ( "Player " + currentPlayer.getPlayerNo() + " (Computer player) is thinking . . ." );
        Coordinate coordinate = currentPlayer.play( gameMainPanel.getCells() );
        if ( coordinate == null ) {
          // Tie situation.
          setInfo( "Draw game." );
          gameMainPanel.setGameStarted( false );
          return;
        } else {
          gameMainPanel.setCell ( coordinate, currentPlayer, Cell.State.Occupied );
          if ( gameMainPanel.checkWinner() ) {
            // The current computer player has won.
            return;
          }
        }
        // Turn to the the other player 
        currentPlayer = currentPlayer.getTheOtherPlayer();
        try {
          Thread.sleep( 500 );
          gameMainPanel.paintImmediately(0, 0, gameMainPanel.getWidth(), gameMainPanel.getHeight() );
        } catch (InterruptedException e) { }
      }
    } else if (player1.getPlayerType() == Player.PlayerType.ComputerPlayer) {
      // for the case where the first player is the computer player while the second player is human
      setInfo ( "Player 1 (Computer player) is thinking . . ." );
      Coordinate coordinate = player1.play( gameMainPanel.getCells() );
      if ( coordinate == null ) {
        // Tie situation. This should not happen when the game just starts
        setInfo( "Draw game." );
        gameMainPanel.setGameStarted( false );
        return;
      } else {
        gameMainPanel.setCell ( coordinate, player1, Cell.State.Occupied );
        if ( gameMainPanel.checkWinner() ) {
          // The computer player has won. This should not occur as the game has just started.
          return;
        }
      }
      // Turn to the player2, 
      setInfo("Player 2 to place a bead . . .");
      gameMainPanel.setCurrentPlayer(player2);
      player2.play(gameMainPanel.getCells());
    } else {
      // for the case where the first player is human and the second player could be either human or computer.
      // The human player logic that handles the human interactivity is in the BoardPanel's mouse listeners.
      setInfo("Player 1 to place a bead . . .");
      gameMainPanel.setCurrentPlayer(player1);
    }
    gameMainPanel.setGameStarted(true);
  }
  
  /**
   * setInfo
   * A utility method to set the information (e.g., instructions, game status) to the infoLabel (JLabel)
   * @param info the information to write to the infoLabel
   */
  public void setInfo(String info) {
    infoLabel.setText(info);
    infoLabel.paintImmediately(0, 0, getWidth(), getHeight() );
  }
  
  /**
   * checkWinner
   * Check if there is a winning player. It does so by transforming a 3D cells to multiple 2D cells and passing to
   * checkWinnerFor2DSlice method to check for winning cells.
   *    do the 1), 2) and 3) to see if there is a winning line, if yes, mark the cells in the line as "Won" state.
   * <ol>
   * <li>Transform 3D cells to 2D by layers, and then invoke the checkWinnerFor2DSlice method.</li>
   * <li>Transform 3D cells to 2D by rows, and then invoke the checkWinnerFor2DSlice method.</li>
   * <li>Transform 3D cells to 2D by columns, and then invoke the checkWinnerFor2DSlice method.</li>
   * <li>Check 3D diagonally in 3D. This step doesn't invoke the checkWinnerFor2DSlice method.</li>
   * </ol>
   * If a winning cell line is found, it marks the cells in the line as Cell.State.Won state.
   * @param cells the game board cells
   * @return an array of cells that consists of the winning cells, or null if no winning player is found.
   */
  public Cell[] checkWinner(Cell[][][] cells) {
    for ( int layer = 0; layer < BoardPanel.TOTAL_LAYERS; layer ++ ) {
      for ( int row = 0; row < BoardPanel.TOTAL_ROWS; row ++ ) {
        for ( int column = 0; column < BoardPanel.TOTAL_COLUMNS; column ++ ) {
          Cell[] winningCells = checkWinningLinesInAllDirections ( cells, layer, row, column );
          if ( winningCells != null ) {
            markWinningCells ( winningCells );
            return winningCells;
          }
        }
      }
    }
    return null;
  }
  
  /**
   * checkWinningLinesInAllDirections
   * Check winning line in all 3D directions (26 directions).
   * @param cells the game board cells
   * @param layer the layer coordinate of the starting point to check for all directions.
   * @param row the row coordinate of the starting point to check for all directions.
   * @param column the column coordinate of the starting point to check for all directions.
   * @return an array of cells that consists of the winning cells, or null if no winning player is found.
   */
  private Cell[] checkWinningLinesInAllDirections(Cell[][][] cells, int layer, int row, int column) {
    Player firstPlayerPlacedBeadInTheLine = cells[layer][row][column].getBeadInCell();
    // iterate all directions: layer+/-1, row+/-1, column+/-1
    for ( int layerDirectionFactor = -1; layerDirectionFactor <= 1; layerDirectionFactor ++ ) {
      for ( int rowDirectionFactor = -1; rowDirectionFactor <= 1; rowDirectionFactor ++ ) {
        for ( int columnDirectionFactor = -1; columnDirectionFactor <= 1; columnDirectionFactor ++ ) {
          if ( ( layerDirectionFactor != 0) || (rowDirectionFactor != 0) || (columnDirectionFactor != 0) ) {
            Cell[] winningCells = new Cell[BoardPanel.TOTAL_BEADS_TO_WIN];
            int totalSameBeadsInTheLine = 1;
            winningCells[0] = cells[layer][row][column];
            if (firstPlayerPlacedBeadInTheLine != null) {
              boolean keepLooking = true;
              for ( int k = 1; k < BoardPanel.TOTAL_BEADS_TO_WIN && keepLooking; k ++ ) {
                int newLayer = layer+layerDirectionFactor*k;
                int newRow = row+rowDirectionFactor*k;
                int newColumn = column+columnDirectionFactor*k;
                if ( (newLayer < 0) || (newLayer >= BoardPanel.TOTAL_LAYERS) ||
                    (newRow < 0) || (newRow >= BoardPanel.TOTAL_ROWS) ||
                    (newColumn < 0) || (newColumn >= BoardPanel.TOTAL_COLUMNS) ) {
                  // exceeds the boundary
                  keepLooking = false;
                } else {
                  Player nextPlayerPlacedBeadInTheLine = cells[newLayer][newRow][newColumn].getBeadInCell();
                  if ( (nextPlayerPlacedBeadInTheLine == null) || (nextPlayerPlacedBeadInTheLine != firstPlayerPlacedBeadInTheLine) ) {
                    keepLooking = false;
                  } else {
                    winningCells[totalSameBeadsInTheLine] = cells[layer+layerDirectionFactor*k][row+rowDirectionFactor*k][column+columnDirectionFactor*k];
                    totalSameBeadsInTheLine++;
                  }
                }
              }
            }
            if (totalSameBeadsInTheLine == BoardPanel.TOTAL_BEADS_TO_WIN) {
              // found winning line
              return winningCells;
            }
          }
        }
      }
    }
    return null;
  }
  
  /**
   * markWinningCells
   * Mark each winningCells' state to Cell.State.Won
   * @param winningCells the cells that to be set to Cell.State.Won state.
   */
  private void markWinningCells(Cell[] winningCells) {
    for ( Cell cell : winningCells ) {
      cell.setState ( Cell.State.Won );
    }
  }
}