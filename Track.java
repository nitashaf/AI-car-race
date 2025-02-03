
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Track {
	
    private char[][] track;
    private int totalRow;
    private int totalCol;
    private String trackName;
    private Random rand  = new Random();

    private ArrayList<int[]> startPoints = new ArrayList<int[]>();
    private ArrayList<int[]> finishPoints= new ArrayList<int[]>();
    private ArrayList<int[]> trackPoints= new ArrayList<int[]>();
    private ArrayList<int[]> wallPoints = new ArrayList<int[]>();
    int[] tempArray = new int[2];
    
    int[] finishLine = new int[5];
    
    public ArrayList<int[]> getStartPoints() {
		return startPoints;
	}
    
    public ArrayList<int[]> getFinishPoints() {
		return finishPoints;
	}
    
    public ArrayList<int[]> getTrackPoints() {
		return trackPoints;
	}
    
    public ArrayList<int[]> getWallPoints() {
		return wallPoints;
	}
    
    
    //read track into the system
    public void loadTrack(String trackName){
    	    	    	
        this.trackName = trackName;
        try{
        	
            File file = new File("C:\\Users\\nitas\\Downloads\\RaceTrackReader\\RaceTrackReader\\Tracks\\" + trackName +  "-track.txt");
            Scanner read = new Scanner(file);
            String[] line1 = read.nextLine().split(",");
            this.totalRow= Integer.parseInt(line1[0]);
            this.totalCol = Integer.parseInt(line1[1]);
            this.track = new char[totalRow][totalCol];


            int rowNum = 0;

            while(read.hasNextLine()){
                String x = read.nextLine();
                char[] line = x.toCharArray();
                for (int c = 0; c < totalCol; c++){
                    track[rowNum][c] = line[c];
                    updateCells(rowNum, c, line[c]);                    
                }
                rowNum++;
            }
           
            //drawFLine();   
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    
    
    //getters
    public int getTotalRow() {
		return this.totalRow;
	}
    
    public int getTotalCol() {
		return this.totalCol;
	}
    
    
    
    public char[][] getTrack(){
        return this.track;
    }
    
    
    public boolean isValid(int x, int y) {
    	if(track[x][y] == '#') {
    		return false;
    	}
    	return true;
    }
    
    public boolean isFinished(int x, int y) {
    	if(track[x][y] == 'F') {
    		return true;
    	}
    	return false;
    }
    
    
    //segregating starting points, finishing points, walls and track
    private void updateCells(int row, int col, char c) {
    	
    	tempArray = new int[2];  
    	tempArray[0] = row;
    	tempArray[1] = col;
    	
        if (c == '#') {
        	wallPoints.add(tempArray);         
        } else if(c == '.'){
        	
        	trackPoints.add(tempArray);  
        }
        else if(c == 'S'){
        	startPoints.add(tempArray);
        }
        else if (c == 'F'){
           finishPoints.add(tempArray);
        }    	      
    } 
    


    //check if the car is in track


    //print function
    public void printTrack(){
        for (int x = 0; x < totalRow; x++){
            System.out.print("\n");
            for(int y =0; y < totalCol; y++){
                System.out.print(track[x][y]);
            }
        }        
    }
    
    public void printTrackwithCar(int a, int b){
        
    	this.track[a][b] = '@';
    	for (int x = 0; x < totalRow; x++){
            System.out.print("\n");
            for(int y =0; y < totalCol; y++){
                System.out.print(track[x][y]);
            }
        }

    	
    }

    public static void main(String[] args) {
        Track tr = new Track();
        tr.loadTrack("O");
       // tr.drawFLine();
 //       tr.printTrack();
 //       tr.randomStartingPoint();
        

    }

}
