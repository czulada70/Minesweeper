//Aaron Czulada
//This is the minesweeper program with GUI

import java.util.*;
import java.util.Timer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MinesweeperGUI extends TimerTask implements ActionListener{
	static JButton[][] panels = new JButton[10][10];
	static Block[][] board = new Block[10][10];
	static int gamePlayed = 0, t, totalTime, gamesPlayed, avgTime;
	static JLabel label = new JLabel();
	static JLabel avgTimeLabel = new JLabel();
	static Timer time = new Timer();
	static boolean gameLost = false, alreadyLost = false, alreadyWon = false;
	
	public static void main(String[] args) {
			
			createBoard();
			gamePlayed = 1;
			time.schedule(new MinesweeperGUI(), 0, 1000);
						
	}
	
	/*
	 * This function creates the entire board. It creates the GUI and the parallel array for the game
	 * and if the game has already been played once, it clears both 2-D arrays and remakes the game. 
	 */
	
	public static void createBoard() {
		if(gamePlayed != 0) {
			for(int i = 0; i < panels.length; i++) {
				for(int j = 0; j < panels[i].length; j++) {
					panels[i][j] = null;
					board[i][j] = null;
				}
			}
		}
		
		Random random = new Random();
		
		/*
		 * Intializing all the block objects to null
		 */
		
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board[i].length; j++) {
				board[i][j] = new Block(false);
			}
		}
		
		/*
		 * This randomly places 13 bombs around the board
		 */
		
		int bombCount = 0;
		while(bombCount <= 12) {
			int random1 = random.nextInt(10);
			int random2 = random.nextInt(10);
			if(board[random1][random2].checkIsBomb() == false) {
				board[random1][random2].setBomb(true);
				board[random1][random2].setMarked(true);
				bombCount++;
			}	
		}
		
		/*
		 * This sets the value of each non-bomb block based on how many bombs surround that block. 
		 */
		
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board[i].length; j++) {
				int blockValue = getBombs(i,j,1);
				board[i][j].setBlockValue(blockValue);
			}
		}
		
		
		/*
		 * This double for loop creates all of the JButtons that are shown in the game. This adds an 
		 * action listener to each JButton, one for left click and one for right click, that corresponds 
		 * to the parallel array "board". It sees what the block is (bomb or not) and sets the text or icon
		 * of the button if the user clicks on it. 
		 */
		

		for (int i = 0; i < panels.length; i++) {
			for(int q = 0; q < panels[i].length; q++) {
				JButton myButton = new JButton();
				Block myBlock = board[i][q];
				int x = i; int y = q;
				myButton.addMouseListener(new MouseListener() {
					
					@Override
					public void mouseClicked(MouseEvent e) 
					{
						
						if(SwingUtilities.isLeftMouseButton(e))
						{
							if(!myBlock.checkFlag())
								if(myBlock.checkIsBomb()) {
									myButton.setIcon(new ImageIcon("bomb.png"));
									endGame();
									gameLost = true;}
								else if(myBlock.checkBlockValue() != 0) {
									myButton.setText("" + myBlock.checkBlockValue() + "");
									myBlock.setMarked(true);
									if(checkDub() && !alreadyWon) {
										alreadyWon = true;
										dub();
									}
								}
								else {
									myButton.setText("-");
									zeroBomb(x,y);
									myBlock.setMarked(true);
									if(checkDub() && !alreadyWon) {
										alreadyWon = true;
										dub();
									}
							}
						}
						else		//This is if the user right clicks to make a flag
							if(SwingUtilities.isRightMouseButton(e))
							{
								if(!myBlock.checkFlag() && (!myBlock.checkMark() || myBlock.checkIsBomb())) {
									myBlock.setFlag(true);
									myBlock.setMarked(true);
								    myButton.setIcon(new ImageIcon("flag.png"));
								}
								else if(myBlock.checkFlag()){
									myBlock.setFlag(false);
									myBlock.setMarked(false);
									myButton.setIcon(null);
								}
							}
					}
					
					@Override
					public void mousePressed(MouseEvent e) {	
					}
					@Override
					public void mouseReleased(MouseEvent e) {						
					}
					@Override
					public void mouseEntered(MouseEvent e) {	
					}
					@Override
					public void mouseExited(MouseEvent e) {
					}
				});
				panels[i][q] = myButton;
				
				//This code changes the colors of the numbers based on what number it has
				
				if(board[i][q].checkBlockValue() == 1)
					myButton.setForeground(Color.blue);
				else if(board[i][q].checkBlockValue() == 2)
					myButton.setForeground(Color.green);
				else if(board[i][q].checkBlockValue() == 3)
					myButton.setForeground(Color.red);
				else if(board[i][q].checkBlockValue() == 4)
					myButton.setForeground(Color.black);
				else if(board[i][q].checkBlockValue() == 5)
					myButton.setForeground(Color.cyan);

			}
		}
		

		

		//Beginning GUI
		JFrame theGUI = new JFrame();
		theGUI.setTitle("Minesweeper");
		theGUI.setSize(400, 500);
		theGUI.setResizable(false);
		theGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		

		//Creating the container and grid layout
		Container pane = theGUI.getContentPane();
		pane.setLayout(new GridBagLayout());
		GridBagConstraints gridBag = new GridBagConstraints();

		
		JPanel bombs = new JPanel();
		avgTimeLabel.setText("Average Time: " + avgTime);
		bombs.add(avgTimeLabel);
		gridBag.fill = GridBagConstraints.HORIZONTAL;
		gridBag.weightx = 0.5;
		gridBag.gridwidth = 3;
		gridBag.gridx = 0;
		gridBag.gridy = 0;
		pane.add(bombs, gridBag);
		
		JButton restart = new JButton("Restart");
		restart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { 
				createBoard();
				totalTime += t;
				gamesPlayed++;
				t = 0;
				avgTime = totalTime / gamesPlayed;
				avgTimeLabel.setText("Average Time: " + avgTime);
				time.cancel();
				time = new Timer();
				time.schedule(new MinesweeperGUI(), 0, 1000);
				alreadyLost = false;
				gameLost = false;
				alreadyWon = false;
			}
		});
		
		gridBag.fill = GridBagConstraints.HORIZONTAL;
		gridBag.weightx = 0.5;
		gridBag.gridwidth = 2;
		gridBag.gridx = 4;
		gridBag.gridy = 0;
		pane.add(restart, gridBag);
		
		JPanel time = new JPanel();
		label.setText("Time: " + t);
		time.add(label);
		gridBag.fill = GridBagConstraints.HORIZONTAL;
		gridBag.weightx = 0.5;
		gridBag.gridwidth = 2;
		gridBag.gridx = 8;
		gridBag.gridy = 0;
		pane.add(time, gridBag);
		
		
		
		
		int row = 1;
		int col = 0;
		//adds each panel to the container
		for (int i = 0; i < panels.length; i++) {
			for (int j = 0; j < panels[i].length; j++) {
				gridBag.weightx = 0.5;
				gridBag.gridwidth = 1;
				gridBag.ipady = 10;  
				gridBag.gridx = col;
				gridBag.gridy = row;
				pane.add(panels[i][j], gridBag);
				col++;
			}
			row++;
			col = 0;
		}
	
		theGUI.setVisible(true);
		
	}

	
	/*
	 * Get bombs checks around a block object to see how many bombs are around that object. It recursively
	 * checks each block around and adds it to a bomb counter, then returns the amount of bombs around it. 
	 */
    public static int getBombs(int row, int col, int round) {
    
    		int bombSum = 0;
    	
    		if (row > 9 || row < 0 || col > 9 || col < 0) {
    			return 0;
    		}
    		if (round == 1) {
    			if(board[row][col].checkIsBomb() == false) {
        			bombSum += getBombs(row, col + 1, 2);
        			bombSum += getBombs(row, col - 1, 2);
        			bombSum += getBombs(row + 1, col, 2);
        			bombSum += getBombs(row - 1, col, 2);
        			bombSum += getBombs(row + 1, col + 1, 2);
        			bombSum += getBombs(row + 1, col - 1, 2);
        			bombSum += getBombs(row - 1, col + 1, 2);
        			bombSum += getBombs(row - 1, col - 1, 2);
        			return bombSum;
        		}
    		}
    		if(board[row][col].checkIsBomb())
    			return 1;
    		else return 0;
    }

    /*
     * This method is called when a user clicks on a bomb, it reveals all of the bombs on the board, cancels 
     * the timer and shows a message saying the game is over
     */
    
    public static void endGame() {
    		for(int i = 0; i < panels.length; i++) {
    			for(int q = 0; q < panels[i].length; q++) {
    				
    				if(board[i][q].checkIsBomb())
					panels[i][q].setIcon(new ImageIcon("bomb.png"));	
			}
    		}	
    		time.cancel();
    			if(!alreadyLost) {
    				JOptionPane.showMessageDialog(null, "Game over");
    				alreadyLost = true;
    			}
    }
    
    /*
     * This method checks to see if the user has selected all non-bomb blocks and has won the game
     */
    
    public static boolean checkDub() {
    		for(int i = 0; i < panels.length; i++) {
			for(int q = 0; q < panels[i].length; q++) {
				if(!board[i][q].checkMark())
					return false;
			}	
    		}
    		return true;
    }
    
    /*
     * This method is called when all the spaces without bombs are clicked and it shows a message 
     * that the user won and cancels the timer
     */
    
    public static void dub() {
    		time.cancel();
    		if(!gameLost)
    			JOptionPane.showMessageDialog(null, "You won!");
    }
    
    
    /*
     * It forced me to make this method and gave me an error when I did not have it. It does nothing.
     */
	@Override
	public void actionPerformed(ActionEvent arg0) {}

	/*
	 * This method takes in the row and column of the button that the user chose and checks to see if that
	 * block value is a zero. Then it recursively checks around it for other zeroes and reveals them to the user
	 */
	
	public static void zeroBomb(int row, int col) 
	{
		
		if(row < 10 && col < 10 && row > -1 && col > -1){
			{
				if (board[row][col].checkBlockValue() == 0 && !board[row][col].checkFlag())
					panels[row][col].setText("-");
				else {
					if(!board[row][col].checkFlag()) {
						panels[row][col].setText(board[row][col].toString());
						board[row][col].setMarked(true);
					}
				}
				if(board[row][col].checkBlockValue() == 0 && !board[row][col].checkMark())
				{		
					
					board[row][col].setMarked(true);
					zeroBomb(row, col + 1);
					zeroBomb(row, col - 1);
					zeroBomb(row + 1, col);
					zeroBomb(row - 1, col);
					zeroBomb(row + 1, col + 1);
					zeroBomb(row + 1, col - 1);
					zeroBomb(row - 1, col + 1);
					zeroBomb(row - 1, col - 1);

				}
			}
		}	
	}

	/*
	 * This is the run method for the timer, is called every 1000 milliseconds.
	 */

	@Override
	public void run() {
		t++;	
		label.setText("Time: "+t);
		
	}

	
}
