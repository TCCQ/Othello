import java.util.ArrayList;

class CompWPlayer {
	private byte[][] valueMap1 = new byte[][]{
		{30,-20,5,5,5,5,-20,30},
		{-20,-20,3,3,3,3,-20,-20},
		{5,3,5,5,5,5,3,5},
		{5,3,5,5,5,5,3,5},
		{5,3,5,5,5,5,3,5},
		{5,3,5,5,5,5,3,5},
		{-20,-20,3,3,3,3,-20,-20},
		{30,-20,5,5,5,5,-20,30}
		}; //cannot do inside constructor
	private byte[][] valueMap2 = new byte[][]{
		{30,-20,5,5,5,5,-10,30},
		{-20,-20,5,3,3,5,-10,-10},
		{5,5,5,3,3,5,5,5},
		{5,3,3,7,7,3,3,5},
		{5,3,3,7,7,3,3,5},
		{5,5,5,3,3,5,5,5},
		{-20,-20,5,3,3,5,-20,-20},
		{30,-20,5,5,5,5,-20,30}
		};
	private byte[][] valueMap3 = new byte[][]{
		{10,0,8,8,8,8,0,10},
		{0,0,7,1,1,7,0,0},
		{8,7,1,1,1,1,7,8},
		{8,1,1,1,1,1,1,8},
		{8,1,1,1,1,1,1,8},
		{8,7,1,1,1,1,7,8},
		{0,0,7,1,1,7,0,0},
		{10,0,8,8,8,8,0,10}
		};

	//when to switch which vMap is being referred to
	private int vMapSwap1 = 10; 
	private int vMapSwap2 = 40;

	public Game game;
	
	private double steepness, cutoff;
	
	public CompWPlayer (Game g){
		this(g, 5, 45);
	}
	public CompWPlayer (Game g, double s, double c){
		game = g;
		steepness = s;
		cutoff = c;
		
		int[] toMove = this.bestMove(this.availableMoves());
		game.externalMove(toMove[0], toMove[1], 'W');
		
	}
	
	//returns arraylist of possible moves if form [x,y, score of move]
	private ArrayList<Integer[]> availableMoves(){
		ArrayList<Integer[]> moves= new ArrayList<Integer[]>();
		//size 3, x,y,val
		for (int x = 0; x < 8; x++){
			for (int y = 0; y < 8; y++){
				if (game.legalWMove(x,y)){
					moves.add(new Integer[]{x,y,calcVal(x,y)});
				}
			}
		}
		return moves;
	}
	
	//returns an [x,y] array of the best possible move
	private int[] bestMove(ArrayList<Integer[]> l){
		Integer[] temp;
		int j;
		for (int i = 1; i < l.size(); i++){ //sort best first
			j = i;
			while(l.get(j)[2].intValue() > l.get(j-1)[2].intValue()){
				temp = l.get(j-1);
				l.set(j-1,l.get(j));
				l.set(j,temp);
				j--;
				if (j == 0){
					break;
				}
			}
		}
		
		double rand = Math.random();
		int index;
		if (l.size() == 1){
			index = 0;
		} else {
			index = (rand > 0.25)? 0 : 1;
		}
		
		int out[] = {l.get(index)[0].intValue(), l.get(index)[1].intValue()};
		return out; 
	}
	
	private int calcVal(int x, int y){
		Game temp = new Game("OTHER"); //starts with no loop
		temp.copy(game);
		temp.externalMove(x,y,'W');
		
		double sRatio = Game.logistic(steepness, cutoff, game.turnNum());
		double score = (temp.whiteScore() - game.whiteScore()) * sRatio; 
		
		if (game.turnNum() < vMapSwap1){
			for (int a = 0; a < 8; a++){
				for (int b = 0; b < 8; b++){
					if (temp.getBoard()[a][b] == 'W' && 
						game.getBoard()[a][b] != 'W'){
						score += valueMap1[a][b] * (1-sRatio);
					}
				}
			}
		
		} else if (game.turnNum() < vMapSwap2){
			for (int a = 0; a < 8; a++){
				for (int b = 0; b < 8; b++){
					if (temp.getBoard()[a][b] == 'W' && 
						game.getBoard()[a][b] != 'W'){
						score += valueMap2[a][b] * (1-sRatio);	
					}
				}
			}
		} else {
			for (int a = 0; a < 8; a++){
				for (int b = 0; b < 8; b++){
					if (temp.getBoard()[a][b] == 'W' && 
						game.getBoard()[a][b] != 'W'){
						score += valueMap3[a][b] * (1-sRatio);
					}
				}
			}
		}
		return (int)(score);
	}
	
	
}