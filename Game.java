import java.util.Scanner;
import java.io.FileWriter;

class Game {
	Scanner kb = new Scanner(System.in);
	private char[][] board;
	private final int order[][] = {{-1,-1}, {0,-1}, {1,-1}, {1,0}, {1,1}, {0,1}, {-1,1}, {-1, 0}};
	private final double steepness1, cutoff1, steepness2, cutoff2;
	
	/*
	constructor, takes game type
	informs which players are computers
	first letter is white, second is black (White plays first)
	does nothing if no type is passed,
	CVCS will only work with the correct data passed (no not mess with)
	*/
	public Game (String gt){
		this(gt,"",5,45,5,45);
	}
	public Game(String gametype, String filePath, double s1, double c1,
			double s2, double c2) {
		steepness1 = s1;
		cutoff1 = c1;
		steepness2 = s2;
		cutoff2 = c2;
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
		
		if (gametype.toUpperCase().equals("PVP")){
			this.PVPLoop();
		} else if (gametype.toUpperCase().equals("PVC")){
			this.PVCLoop();
		} else if (gametype.toUpperCase().equals("CVP")){
			this.CVPLoop();
		} else if (gametype.toUpperCase().equals("CVC")){
			this.CVCLoop();
		} else if (gametype.toUpperCase().equals("CVCS")){
			int output = this.CVCSLoop();
			try {
				FileWriter fw = new FileWriter(filePath, true);
				fw.append("\n" + Integer.toString(output));
				fw.close();
			} catch (Exception e) {e.printStackTrace();}
		}
	}

	//loop for PVP games (see constructor)
	private void PVPLoop(){
		boolean exit;
		this.display();
		while (true){
			do {
				exit = blackPMove();
			} while (!exit);
			
			this.display();
			
			do {
				exit = whitePMove();
			} while (!exit);
			
			this.display();
			
			if (!(whiteCanPlay() || blackCanPlay())){
				break;
			}
		}
		System.out.println("White: " + this.whiteScore() + " Black: " + this.blackScore());
	}

	//loop for PVC games
	private void PVCLoop(){
		boolean exit;
		this.display();
		while (true){
			do {
				exit = blackPMove();
			} while (!exit);
			
			this.display();
			
			if (!(whiteCanPlay() || blackCanPlay())){
				break;
			} else if (whiteCanPlay()){
				CompWPlayer c = new CompWPlayer(this);
				this.display();
			}
		}
		System.out.println("White: " + this.whiteScore() + " Black: " + this.blackScore());
	}
	
	//loop for CVP games
	private void CVPLoop(){
		boolean exit;
		this.display();
		while (true){
			if (!(whiteCanPlay() || blackCanPlay())){
				break;
			} else if (blackCanPlay()){
				CompBPlayer c = new CompBPlayer(this);
				this.display();
			}
			
			do {
				exit = whitePMove();
			} while (!exit);
			
			this.display();
		}
		System.out.println("White: " + this.whiteScore() + " Black: " + this.blackScore());
	}
	
	//Comp v comp games, verbose, waits between move for enter key
	private void CVCLoop(){
		Scanner kb = new Scanner(System.in);
		this.display();
		String timer = ""; 
		while (true){
			if (!(whiteCanPlay() || blackCanPlay())){
				break;
			} else if (blackCanPlay()){
				CompBPlayer b = new CompBPlayer(this);
				this.display();
			}
			while (!timer.isEmpty()){
				timer = kb.nextLine();
			} 
			timer = "a";
			
			if (!(whiteCanPlay() || blackCanPlay())){
				break;
			} else if (whiteCanPlay()){
				CompWPlayer w = new CompWPlayer(this);
				this.display();
			}
			
			while (!timer.isEmpty()){
				timer = kb.nextLine();
			}
			timer = "a";
		}
		System.out.println("White: " + this.whiteScore() + " Black: " + this.blackScore());
	}
	
	//comp v comp, silent, returns 1 if black won, -1 if white won, and 0 if draw
	private int CVCSLoop(){
		while (true){
			if (!(whiteCanPlay() || blackCanPlay())){
				break;
			} else if (blackCanPlay()){
				CompBPlayer b = new CompBPlayer(this, steepness1, cutoff1);
			} 
			
			if (!(whiteCanPlay() || blackCanPlay())){
				break;
			} else if (whiteCanPlay()){
				CompWPlayer w = new CompWPlayer(this, steepness2, cutoff2);
			}
		}
		return this.blackScore()-this.whiteScore();
	}
	
	//prints the board in a human readable manner to console
	public void display(){
		int i = 0;
		while (i < 3){
			System.out.println();
			i++;
		}
		System.out.println("Displaying the board");
		String temp;
		int color;
		
		for (int y = -1; y < 9; y++){
			if (y == -1 || y == 8){
				System.out.println("\u001B[34;42m#01234567#\u001B[0m");
			} else {
				for (int x = -1; x < 9; x++){
					color = ((y+x)%2 == 1)? 42:43;
					if (x == -1 || x == 8){
						System.out.print("\u001B[34;"+42+"m"+y+"\u001B[0m");
					} else {
						temp = String.valueOf(board[x][y]);
						if (temp.equals("W")){
							temp = "\u001B[97;"+color+"m" + temp + "\u001B[0m";
						} else if (temp.equals("B")){
							temp = "\u001B[30;"+color+"m" + temp + "\u001B[0m";
						} else {
							temp = "\u001B["+color+"m" + temp + "\u001B[0m";
						}
						System.out.print(temp);
					}
				}
			System.out.println();
			}
		}
	}
	
	public char[][] getBoard(){
		return this.board;
	} //unclear if this is needed
	
	/*
	returns value of logistic growth
	0 < cut < 60
	0 < x < 60
	steep = 5ish
	*/
	public static double logistic (double steep, double cut, int x){
		return 1/(1 + Math.exp(-1*steep*(x - cut)));
	}
	
	//a.copy(b) sets a to be identical to b
	public void copy(Game g){
		for (int x = 0; x < 8; x++){
			for (int y = 0; y < 8; y++){
				this.board[x][y] = g.board[x][y];
			}	
		}
	}
	
	//returns what turn was last played, should be 0 to 60, before and moves and after the last
	public int turnNum(){
		int num = -4;
		for (int x = 0; x < 8; x++){
			for (int y = 0; y < 8; y++){
				if (board[x][y] != ' '){
					num++;
				}
			}
		}
		return num;
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
		if (!whiteCanPlay()){
			return true;
		}
	
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
			return true;
		} else {
			System.out.println("Ilegal: White move?");
			return false;
		}
		
	}
	public boolean blackPMove(){ 
		if (!blackCanPlay()){
			return true;
		}
	
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
			return true;
		} else {
			System.out.println("Ilegal: Black move?");
			return false;
		}
	}

	//changed the board state, used externally, assumes move is legal
	public void externalMove(int x, int y, char color){
		if (color == 'W'){
			board[x][y] = 'W';
			this.flipToWhite(x,y);
		} else {
			board[x][y] = 'B';
			this.flipToBlack(x,y);
		}
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
	public boolean legalWMove (int x, int y){
		boolean isBlack[] = new boolean[8];
				
		if (board[x][y] != ' '){
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
	public boolean legalBMove (int x, int y){
		boolean isWhite[] = new boolean[8];
		
				
		if (board[x][y] != ' '){
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
		
		boolean lineToBeFlipped[] = new boolean[8];
		for (int i = 0; i < 8; i++){
			if (x + order[i][0] > 7 || x + order[i][0] < 0 || y + order[i][1] > 7 || y + order[i][1] < 0){
				lineToBeFlipped[i] = false;
			} else {
				lineToBeFlipped[i] = (board[x + order[i][0]][y + order[i][1]]) == 'B';
			}
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
		
		boolean lineToBeFlipped[] = new boolean[8];
		for (int i = 0; i < 8; i++){
			if (x + order[i][0] > 7 || x + order[i][0] < 0 || y + order[i][1] > 7 || y + order[i][1] < 0){
				lineToBeFlipped[i] = false;
			} else {
				lineToBeFlipped[i] = (board[x + order[i][0]][y + order[i][1]]) == 'W';
			}
		}
		for (int j = 0; j < 8; j++){
			if (lineToBeFlipped[j]){
				lineToBeFlipped[j] = validBLine(x + order[j][0], y + order[j][1], order[j]);
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