import java.util.Scanner;

class Game {
	Scanner kb = new Scanner(System.in);
	private char[][] board;
	
	/*
	constructor, takes PVP, PVC, or CVP
	informs which players are computers
	first letter is white, second is black (White plays first)
	*/
	public Game(String gametype) {
		board = new char[8][8];
		for (int x = 0; x < 8; x++){
			for (int y = 0; y < 8; y++){
				board[x][y] = ' ';
			}
		}
		board[3][3] = 'W';
		board[4][4] = 'W';
		board[3][4] = 'B';
		board[4][3] = 'B';
		this.loop(gametype.toUpperCase());
	}

	//main loop, runs until game is over, takes game type (see constructor)
	public void loop (String type){
		boolean exit;
		this.display();
		
		while (whiteCanPlay() || blackCanPlay()){
			exit = false;
			do {
				if (!whiteCanPlay()){
					break;
				}
				switch (type){
					case ("PVP"):
						exit = whitePMove();
						break;
					case ("PVC"):
						exit = whitePMove();
						break;
					case ("CVP"):
						exit = whiteCMove();
						break;
				}
			} while (!exit);
			exit = false;
			
			do {
				if (!blackCanPlay()){
					break;
				}
				switch (type){
					case ("PVP"):
						exit = blackPMove();
						break;
					case ("PVC"):
						exit = blackCMove();
						break;
					case ("CVP"):
						exit = blackPMove();
						break;
				}
			} while (!exit);
			exit = false;
		}
		System.out.println("White: " + this.whiteScore() + " Black: " + this.blackScore());
	}

	//prints the board in a human readable manner to console
	public void display(){
		int i = 0;
		while (i < 3){
			System.out.println();
			i++;
		}
		System.out.println("Displaying the board");
		
		for (int y = -1; y < 8; y++){
			if (y == -1){
				System.out.println("#01234567");
			} else {
				for (int x = -1; x < 8; x++){
					if (x == -1){
						System.out.print(y);
					} else {
						System.out.print(board[x][y]);
					}
				}
			System.out.println();
			}
		}
	}
	
	//returns # of white and black pieces on the board, respectively
	public int whiteScore(){
		int s = 0;
		for (int x = 0; x < 8; x++){
			for (int y = 0; y < 8; y++){
				if (board[x][y] == 'W' ){
					s++;
				}
			}
		}
		return s;
	}
	public int blackScore(){
		int s = 0;
		for (int x = 0; x < 8; x++){
			for (int y = 0; y < 8; y++){
				if (board[x][y] == 'B' ){
					s++;
				}
			}
		}
		return s;
	}

	/*
	player moves, takes input from console, x enter, y enter
	from here, legalWMove and legalBMove are called
	only allows legal moves, otherwise prompts for another attempt
	*/
	public boolean whitePMove(){
		System.out.print("X Coordinate? ");
		int x = kb.nextInt();
		System.out.print("Y Coordinate? ");
		int y = kb.nextInt();
		if (x > 7 || x < 0 || y > 7 || y < 0){
			System.out.println("Should be 0-7");
			return false;
		}	
		
		if (legalWMove(x,y)){
			board[x][y] = 'W';
			flipToWhite(x,y);
			this.display();
			return true;
		} else {
			System.out.println("Ilegal: White move?");
			return false;
		}
		
	}
	public boolean blackPMove(){ 
		System.out.print("X Coordinate? ");
		int x = kb.nextInt();
		System.out.print("Y Coordinate? ");
		int y = kb.nextInt();
		if (x > 7 || x < 0 || y > 7 || y < 0){
			System.out.println("Should be 0-7");
			return false;
		}	
		
		if (legalBMove(x,y)){
			board[x][y] = 'B';
			flipToBlack(x,y);
			this.display();
			return true;
		} else {
			System.out.println("Ilegal: Black move?");
			return false;
		}
	}

	//TODO computer plays for one player
	public boolean whiteCMove(){
		//TODO
		return false;
	}
	public boolean blackCMove(){
		//TODO
		return false;
	}
	
	//checks if legal moves exist
	public boolean whiteCanPlay(){
		for (int x = 0; x < 8; x++){
			for (int y = 0; y < 8; y++){
				if (legalWMove(x,y)){
					return true;
				}
			}
		}
		return false;
	}
	public boolean blackCanPlay(){
		for (int x = 0; x < 8; x++){
			for (int y = 0; y < 8; y++){
				if (legalBMove(x,y)){
					return true;
				}
			}
		}
		return false;
	}

	//check to see if x,y move is legal for the relevant color
	private boolean legalWMove (int x, int y){
		boolean isBlack[] = new boolean[8];
		int order[][] = {{-1,-1}, {0,-1}, {1,-1}, {1,0}, {1,-1}, {0,1}, {-1,1}, {-1, 0}};
				
		if (board[x][y] != ' '){
			System.out.println("That tile is filled");
			return false;
		}	
			
		for (int i = 0; i < 8; i++){
		
			int move[] = order[i];
			int xtest = x+move[0];
			int ytest = y+move[1];
			if (xtest > 7 || xtest < 0 || ytest > 7 || ytest < 0){
				isBlack[i] = false;
				continue;
			}	
			isBlack[i] = (board[xtest][ytest]) == 'B';		

		}
		
		boolean exit = false;
		for (int j = 0; j < 8; j++){
			if (isBlack[j]){
				exit = validWLine(x + order[j][0], y+ order[j][1], order[j]);

			}
			if (exit){
				return true;
			}
		}
		return false;
	}
	private boolean legalBMove (int x, int y){
		boolean isWhite[] = new boolean[8];
		int order[][] = {{-1,-1}, {0,-1}, {1,-1}, {1,0}, {1,-1}, {0,1}, {-1,1}, {-1, 0}};
				
		if (board[x][y] != ' '){
			System.out.println("That tile is filled");
			return false;
		}	
			
		for (int i = 0; i < 8; i++){
		
			int move[] = order[i];
			int xtest = x+move[0];
			int ytest = y+move[1];
			if (xtest > 7 || xtest < 0 || ytest > 7 || ytest < 0){
				isWhite[i] = false;
				continue;
			}	
			isWhite[i] = (board[xtest][ytest]) == 'W';		

		}
		
		boolean exit = false;
		for (int j = 0; j < 8; j++){
			if (isWhite[j]){
				exit = validBLine(x + order[j][0], y+ order[j][1], order[j]);

			}
			if (exit){
				return true;
			}
		}
		return false;
	}
	
	/*
	called by legalWMove/legalBMove
	checks for a line followed by a dot of the oposite color
	validWline is a line that can be played on by white
	so VWL looks for BBBW, if given
	the x and y are not the coords of the empty end,
		but the coords of the first black piece in the line
	hacky and gross, do not touch or call.
	*/
	private boolean validWLine(int x, int y, int move[]){
			int xtest = x+move[0];
			int ytest = y+move[1];

			
			while (true){
				if (xtest > 7 || xtest < 0 || ytest > 7 || ytest < 0){

					return false; //reached edge
				}
				if (board[xtest][ytest] == 'W'){
					return true;
				} else if (board[xtest][ytest] == ' '){
					return false; //catches end of line with blank space
				}
				xtest += move[0];
				ytest += move[1];
			}		
	}
	private boolean validBLine(int x, int y, int move[]){
		int xtest = x+move[0];
		int ytest = y+move[1];

		
		while (true){
			if (xtest > 7 || xtest < 0 || ytest > 7 || ytest < 0){

				return false; //reached edge
			}
			if (board[xtest][ytest] == 'B'){
				return true;
			} else if (board[xtest][ytest] == ' '){
				return false; //catches end of line with blank space
			}
			xtest += move[0];
			ytest += move[1];
		}
	}
	
	//takes x and y of new placement
	private void flipToWhite(int x, int y){
		int order[][] = {{-1,-1}, {0,-1}, {1,-1}, {1,0}, {1,-1}, {0,1}, {-1,1}, {-1, 0}};
		boolean lineToBeFlipped[] = new boolean[8];
		for (int i = 0; i < 8; i++){
			lineToBeFlipped[i] = (board[x + order[i][0]][y + order[i][1]]) == 'B';
		}
		for (int j = 0; j < 8; j++){
			if (lineToBeFlipped[j]){
				lineToBeFlipped[j] = validWLine(x + order[j][0], y + order[j][1], order[j]);
			}
		}
		int move[];
		for (int k = 0; k < 8; k++){
			move = order[k];
			int xtest = x + move[0];
			int ytest = y + move[1];
			if (lineToBeFlipped[k]){
				while (true){
					if (board[xtest][ytest] == 'B'){
						board[xtest][ytest] = 'W';
					} else {
						break;
					}
					xtest += move[0];
					ytest += move[1];
				}
			}
		}
	}
	private void flipToBlack(int x, int y){
		int order[][] = {{-1,-1}, {0,-1}, {1,-1}, {1,0}, {1,-1}, {0,1}, {-1,1}, {-1, 0}};
		boolean lineToBeFlipped[] = new boolean[8];
		for (int i = 0; i < 8; i++){
			lineToBeFlipped[i] = (board[x + order[i][0]][y + order[i][1]]) == 'W';
		}
		for (int j = 0; j < 8; j++){
			if (lineToBeFlipped[j]){
				lineToBeFlipped[j] = validWLine(x + order[j][0], y + order[j][1], order[j]);
			}
		}
		int move[];
		for (int k = 0; k < 8; k++){
			move = order[k];
			int xtest = x + move[0];
			int ytest = y + move[1];
			if (lineToBeFlipped[k]){
				while (true){
					if (board[xtest][ytest] == 'W'){
						board[xtest][ytest] = 'B';
					} else {
						break;
					}
					xtest += move[0];
					ytest += move[1];
				}
			}
		}
	}

}