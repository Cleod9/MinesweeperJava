//Greg McLeod
//3.19.2008
//MineSweeper

class MineSweeper
{
	private Grid m_grid;
    
    //Creates a new game
    public MineSweeper(int size)
    {
        try
        {
            if(size <= 0)
                throw new Exception("Error, intitial size must be greater than zero");
                
            //Make a new MineSweeper Grid
            m_grid = new Grid("MineSweeper",size,45);
        }
        
        catch(Exception exception) { }
    }
    
    
   	//Prints out the data within the grid
    public String toString()
    {
		return m_grid.toString();
    }
}
