import java.io.File;
import java.util.Scanner;

class Main {
	public static void main (String[] args){
		//Game g = new Game("CVC");
		
		try {
			new File("Scores.txt").createNewFile(); //pos is black win, neg is white win
		} catch (Exception e){e.printStackTrace();}
		
		for (int i = 0; i < 1000; i++){
			Game g = new Game("CVCS", "Scores.txt", 5, 45, 5, 45);
		}
		
		try {
			Scanner scores = new Scanner(new File("Scores.txt"));
			
			int sum = 0;
			int wins = 0;
			while(scores.hasNext()){
				String nl = scores.nextLine();
				if (!nl.equals("")){
					sum += Integer.parseInt(nl);
					if (Integer.parseInt(nl) > 0){wins++;}
					else if (Integer.parseInt(nl) < 0){wins--;}
				}
				
			}
			System.out.println(sum);
			System.out.println(wins);

			new File("Scores.txt").delete();
		} catch (Exception e){e.printStackTrace();}
		
		
		
	}
}