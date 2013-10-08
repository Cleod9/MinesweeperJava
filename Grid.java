//Greg McLeod
//3.19.2008
//MineSweeper (GUI and Engine)

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


@SuppressWarnings("serial")

class Grid extends JFrame implements MouseListener,ActionListener
{
	private int m_rows; //Number of rows
	private int m_cols; //Number of columns
	private int m_size; //Size of each square
	private JButton m_smiley; //Smiley button for resetting the game
	private JButton[][] m_grid; //Buttons which will be used as grid UI
	private int[][] m_data; //Numerical data representing grid locations [0 = neutral, -1 = flag, -2 = mine, otherwise it's a number tile]
	private int[][] m_data_original; //To preserve original data
	private boolean m_gameover; //Lose status
	private boolean m_win; //Win status
	
	//Create a new Grid given a window title, dimensions of grid, and size of grid cells
	public Grid(String title, int dim, int size)
	{
	    //Set up sizes
		m_gameover = false;
		m_win = false;
		m_rows = dim;
		m_cols = dim;
		m_size = size;
		m_grid = new JButton[m_rows][m_cols];
		m_data = new int[m_rows][m_cols];
		m_data_original = new int[m_rows][m_cols];
		generateBoard();
		
		//Adjust some window properties
		this.setTitle(title);
		this.setResizable(false);
		this.setSize(m_cols*m_size, m_rows*m_size + m_size * 2);
		this.setVisible(true);
		this.setLayout(null);
		
		//Prep smiley button
		m_smiley = new JButton();
		m_smiley.setSize(m_size,m_size);
   		this.add(m_smiley);
		m_smiley.setLocation((this.getWidth()/2 - m_smiley.getWidth()/2),0);
		m_smiley.setIcon(new ImageIcon("images/happy.png"));
		
		//Set up events
		this.addWindowListener(new wQuit());
		m_smiley.setActionCommand("smiley");
		m_smiley.addActionListener(this);
		m_smiley.addMouseListener(this);
		
		
		//Build grid
		for(int r=0;r<m_rows;r++)
		{
			for(int c=0;c<m_rows;c++)
			{
				//Create a new button and places it in the grid
				JButton tmpBtn = new JButton();
				tmpBtn.setSize(m_size,m_size);
				tmpBtn.setLocation(c*m_size,m_size + r*m_size);
   				m_grid[r][c] = tmpBtn;
		   		tmpBtn.addActionListener(this);
		   		tmpBtn.addMouseListener(this);
		   		tmpBtn.setActionCommand(r+","+c);
   				this.add(tmpBtn);
			}
		}
	}
	
	//Checks if the game has been won
	private void checkWin()
	{
		int totalmines = 0;
		for(int r=0;r<m_rows;r++)
		{
			for(int c=0;c<m_rows;c++)
			{
				
				if(m_data[r][c] == -2 && m_grid[r][c].isEnabled())
				{
					totalmines++;
				}
			}
		}
		//If no mines are found you win
		if(totalmines == 0)
		{
			m_win = true;
			m_smiley.setIcon(new ImageIcon("images/win.png"));
		}
		
	}
	private void generateBoard()
	{
        //Generate mines (If you want to make it easier, change this number)
        int mines = 10;
        
        int r = 0;
        int c = 0;
        
		m_data = new int[m_rows][m_cols];
		m_data_original = new int[m_rows][m_cols];
        
        while(mines > 0)
        {
        	//Dump a mine in a random location (Possible to have less than the amount specfied because of this FYI)
        	m_data[((int)(Math.random()*(m_rows-1)))][(int)(Math.random()*(m_cols-1))] = -2;
        	mines--;
        }
        
        //Generate Number spaces
        for(r = 0;r<m_data.length;r++)
        {
        	for(c = 0;c<m_data[r].length;c++)
       		{
       			if(m_data[r][c] == 0)
       			{
       				//For all empty spaces, we look in all 8 adjacent tile directions and increment the value if a mine exists
       				if(r-1 >= 0 && m_data[r-1][c] == -2)
       				{
        				m_data[r][c]++;
       				}
       				if(c-1 >= 0 && m_data[r][c-1] == -2)
       				{
        				m_data[r][c]++;
       				}
       				if(c+1 < m_data[r].length && m_data[r][c+1] == -2)
       				{
        				m_data[r][c]++;
       				}
       				if(r+1 < m_data.length && m_data[r+1][c] == -2)
       				{
        				m_data[r][c]++;
       				}
       				if(r+1 < m_data.length && c+1 < m_data[r].length && m_data[r+1][c+1] == -2)
       				{
        				m_data[r][c]++;
       				}
       				if(r+1 < m_data.length && c-1 >= 0 && m_data[r+1][c-1] == -2)
       				{
        				m_data[r][c]++;
       				}
       				if(r-1 >= 0 && c+1 < m_data[r].length && m_data[r-1][c+1] == -2)
       				{
        				m_data[r][c]++;
       				}
       				if(r-1 >= 0 && c-1 >= 0 && m_data[r-1][c-1] == -2)
       				{
        				m_data[r][c]++;
       				}
       			}
       		}
       		
       		//Make a quick copy of the original (since currently the way it's coded, flags will affect the original array)
	       m_data_original = m_data.clone();
        }
	}
	public void spread(int row, int col)
	{
		//Center is the current slot in the array being checked, not the center of the grid
		int CENTER = m_data[row][col];
		
		//Update the state of this particular cell
		updateButton(row,col);
		
		//If this is a blank cell, recurisively call this same function on the surrounding Top, Bottom, Left, and Right cells
		if(CENTER == 0)
		{
			if(row-1 >= 0 && m_data[row-1][col] >= 0 && m_grid[row-1][col].isEnabled())
			{
				spread(row-1,col);
			}
			if(col-1 >= 0 && m_data[row][col-1] >= 0 && m_grid[row][col-1].isEnabled())
			{
				spread(row,col-1);
			}
			if(col+1 < m_data[row].length && m_data[row][col+1] >= 0 && m_grid[row][col+1].isEnabled())
			{
				spread(row,col+1);
			}
			if(row+1 < m_data.length && m_data[row+1][col] >= 0 && m_grid[row+1][col].isEnabled())
			{
				spread(row+1,col);
			}
		} else if (CENTER == -2)
		{
			//Game Over (We hit a mine)
			m_gameover = true;
			m_smiley.setIcon(new ImageIcon("images/dead.png"));
		}
	}
	
	//Updates the button to correspond to the data inside
	public void updateButton(int r, int c)
	{
		switch(m_data[r][c])
		{
			//Mine
			case -2:
			if(m_grid[r][c].isEnabled())
			{
				m_grid[r][c].setText("");
				m_grid[r][c].setIcon(new ImageIcon("images/mine.png"));
				m_grid[r][c].setEnabled(false);
			}
			break;
			//Flag
			case -1:
			if(m_grid[r][c].isEnabled())
			{
				m_grid[r][c].setText("");
				m_grid[r][c].setIcon(new ImageIcon("images/flag.png"));
				m_grid[r][c].setEnabled(false);
			}
			break;
			//Empty space
			case 0:
			if(m_grid[r][c].isEnabled())
			{
				m_grid[r][c].setText("");
				m_grid[r][c].setEnabled(false);
			}
			break;
			///Number
			default:
			if(m_grid[r][c].isEnabled())
			{
				m_grid[r][c].setText(""+m_data[r][c]);
				m_grid[r][c].setEnabled(false);
			}
			break;
		}
	}
	
	//Check performed action
  	public void actionPerformed(ActionEvent evt)
  	{
  		if(!m_gameover && !m_win && !evt.getActionCommand().equals("smiley"))
  		{
  			//Normal button clicked, get the row and column by parsing the action command string which is in the format "#,#"
	  		StringTokenizer action = new StringTokenizer(evt.getActionCommand(),",");
	  		int row = Integer.parseInt(action.nextToken());
	  		int col = Integer.parseInt(action.nextToken());
	  		spread(row,col);
			checkWin();
  		} else if(evt.getActionCommand().equals("smiley"))
  		{
  			//Smiley button clicked, reset the board
  			for(int r = 0;r<m_data.length;r++)
	        {
	        	for(int c = 0;c<m_data.length;c++)
	       		{
	       			m_grid[r][c].setIcon(null);
	       			m_grid[r][c].setText(null);
	       			m_grid[r][c].setEnabled(true);
	       		}
	        }
  			generateBoard();
  			m_gameover = false;
  			m_win = false;
			m_smiley.setIcon(new ImageIcon("images/happy.png"));
  		}
  	}
  	
  	//Double buffering for smoother screen updates
	public void bufferPaint(Graphics gr)
	{
		//Replace the background
		this.setBackground(Color.gray);
	}
	public void paint(Graphics gr)
	{
  		//Buffer
		Image screen = createImage(this.getWidth(), this.getHeight());
  		bufferPaint(screen.getGraphics());
  		gr.drawImage(screen, 0, 0, this);
	}
	public void update(Graphics gr)
	{
  		paint(gr);
	}
	
	//Handle mouse events
	public void mouseClicked(MouseEvent e)
	{
		if(e.getButton() == 3 && !m_gameover && !m_win)
		{
			//Right clicked, set a flag on the current btton
			StringTokenizer action = new StringTokenizer(((JButton)e.getSource()).getActionCommand(),",");
	  		int row = Integer.parseInt(action.nextToken());
	  		int col = Integer.parseInt(action.nextToken());
	  		if(m_grid[row][col].isEnabled())
	  		{
	  			//Set flag
				m_data[row][col] = -1;
				updateButton(row, col);
				checkWin();
	  		} else if(m_data[row][col] == -1)
	  		{
	  			//Remove flag
				m_data[row][col] = m_data_original[row][col];
				m_grid[row][col].setIcon(null);
				m_grid[row][col].setEnabled(true);
				System.out.println("asdas");
	  		}
		} else
		{
				//Change the 
			if(m_gameover)
			{
				m_smiley.setIcon(new ImageIcon("images/dead.png"));
			} else if(m_win)
			{
				m_smiley.setIcon(new ImageIcon("images/win.png"));
			} else
			{
				m_smiley.setIcon(new ImageIcon("images/happy.png"));
			}
		}
	}
	
	public void mouseEntered(MouseEvent e)
	{
	}
	public void mouseExited(MouseEvent e)
	{
	} 
	public void mousePressed(MouseEvent e)
	{
		if(m_gameover)
		{
			m_smiley.setIcon(new ImageIcon("images/dead.png"));
		} else if(m_win)
		{
			m_smiley.setIcon(new ImageIcon("images/win.png"));
		}  else
		{
			m_smiley.setIcon(new ImageIcon("images/scared.png"));
		}
	}
	public void mouseReleased(MouseEvent e)
	{
		if(m_gameover)
		{
			m_smiley.setIcon(new ImageIcon("images/dead.png"));
		} else if(m_win)
		{
			m_smiley.setIcon(new ImageIcon("images/win.png"));
		}  else
		{
			m_smiley.setIcon(new ImageIcon("images/happy.png"));
		}
	}
	
	//Get the numerical data of the grid as a string
	public String toString()
	{
		 String s = "";
        for(int i = 0; i < m_data.length; i++)
        {
            s += "[";
            for(int j = 0; j < m_data.length; j++)
                s += m_data[i][j] + ",";

            s = s.substring(0, s.length() - 1) + "]\n";
        }

        s = s.substring(0, s.length() - 1);
        return s;
	}
}

//To catch the exiting event
class wQuit extends WindowAdapter {
	public void windowClosing(WindowEvent e) {
		System.exit(0);
	}
}