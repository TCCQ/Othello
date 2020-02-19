import java.util.ArrayList;

class CompBPlayer {
	private byte[][] valueMap1 = new byte[][]{
		{10,-5,0,0,0,0,-5,10},
		{-5,-5,0,0,0,0,-5,-5},
		{0,0,8,8,8,8,0,0},
		{0,0,8,9,9,8,0,0},
		{0,0,8,9,9,8,0,0},
		{0,0,8,8,8,8,0,0},
		{-5,-5,0,0,0,0,-5,-5},
		{10,-5,0,0,0,0,-5,10}
		}; //cannot do inside constructor
	private byte[][] valueMap2 = new byte[][]{
		{10,-3,8,8,8,8,-3,10},
		{-3,-3,8,0,0,8,-3,-3},
		{8,8,9,0,0,9,8,8},
		{8,0,0,7,7,0,0,8},
		{8,0,0,7,7,0,0,8},
		{8,8,9,0,0,9,8,8},
		{-3,-3,8,0,0,8,-3,-3},
		{10,-3,8,8,8,8,-3,10}
		};
	private byte[][] valueMap3 = new byte[][]{
		{10,8,8,8,8,8,8,10},
		{8,7,7,1,1,7,7,8},
		{8,7,1,1,1,1,7,8},
		{8,1,1,1,1,1,1,8},
		{8,1,1,1,1,1,1,8},
		{8,7,1,1,1,1,7,8},
		{8,7,7,1,1,7,7,8},
		{10,8,8,8,8,8,8,10}
		};

	//when to switch which vMap is being referred to
	int vMapSwap1 = 10; 
	int vMapSwap2 = 40;

	public Game game;
	
	public CompBPlayer (Game g){
		game = g;
		
		int[] toMove = this.bestMove(this.availableMoves());
		game.externalMove(toMove[0], toMove[1], 'B');
		
	}
	
	//returns arraylist of possible moves if form [x,y, score of move]
	private ArrayList<Integer[]> availableMoves(){
		ArrayList<Integer[]> moves= new ArrayList<Integer[]>();
		//size 3, x,y,val
		for (int x = 0; x < 8; x++){
			for (int y = 0; y < 8; y++){
				if (game.legalBMove(x,y)){
					moves.add(new Integer[]{x,y,calcVal(x,y)});
				}
			}
		}
		return moves;
	}
	
	//returns an [x,y] array of the best possible move
	private int[] bestMove(ArrayList<Integer[]> l){
		int maxIndex = 0;
		for (int i = 1; i < l.size(); i++){
			if (l.get(i)[2] > l.get(maxIndex)[2]){
				maxIndex = i;
			}
		}
		int out[] = {l.get(maxIndex)[0], l.get(maxIndex)[1]};
		return out;
	}
	
	private int calcVal(int x, int y){
		Game temp = new Game("OTHER"); //starts with no loop
		temp.copy(game);
		temp.externalMove(x,y,'B');
		int score = temp.blackScore() - game.blackScore(); 
		if (game.turnNum() < vMapSwap1){
			score += valueMap1[x][y];
		} else if (game.turnNum() < vMapSwap2){
			score += valueMap2[x][y];
		} else {
			score += valueMap3[x][y];
		}
		return score;
	}
	
}