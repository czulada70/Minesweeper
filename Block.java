//Aaron Czulada
//This is the block class for Minesweeper game

public class Block {
		
	private boolean isBomb, marked, flagged;
	private int blockValue;
	
		
	public Block(boolean isBomb2) {
		isBomb = isBomb2;
		blockValue = 0;
		marked = false;
		flagged = false;
	}
	
	public boolean checkIsBomb() {
		return isBomb; 
	}
	
	public int checkBlockValue() {
		return blockValue;
	}
	
	public boolean checkMark() {
		return marked;
	}
	
	public boolean checkFlag() {
		return flagged;
	}
	
	public void setMarked(boolean marked2) {
		marked = marked2;
	}
	public void setBomb(boolean isBomb2) {
		isBomb = isBomb2;
	}
	
	public void setFlag(boolean flagged2) {
		flagged = flagged2;
	}
	
	public void setBlockValue(int blockValue2) {
		blockValue = blockValue2;
	}
	
	public String toString() {
		if(isBomb)
			return "# ";
		else if(blockValue > 0)
			return blockValue + " ";
		else 
			return "0 ";
	}
	
}
