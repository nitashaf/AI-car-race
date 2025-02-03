import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Car {
    int[] velocity = new int[2];//Current Carr Velocity x,y
    int topSpeed = 5;
    int xPos = 0;
    int yPos = 0;
    char[][] track;

    //addition
    int[] Ovelocity = new int[2];
    int [] TempPos = new int[2];
    boolean crashNear;
    Track trk;
    private Random rand  = new Random();
    
    //Actions: {0,0}{0,1}{1,0}{1,1}{0,-1}{-1,0}{-1,-1}{1,-1}{-1,1}
    
    
    //change
    public Car(Track tr){
    	this.trk = tr;
        this.track = tr.getTrack();
    }
    
    
    public int[] getVelocity(){
        return this.velocity;
    }

    public String getState(){
        //build string for hash key
        StringBuilder string = new StringBuilder();
        string.append(xPos);
        string.append(",");
        string.append(yPos);
        string.append(",");
        string.append(velocity[0]);
        string.append(",");
        string.append(velocity[1]);
        return string.toString();
    }

    public int[] setVelocity(int[] newVelo){
        this.velocity = newVelo;
        return newVelo;
    }
    
    
    public int[] applyAcceleration(int[] acc ){
        Random rand = new Random();
        double nDcheck = rand.nextDouble(1);
        if (nDcheck <= 0.2){
            //Random Failure
//            System.out.println("Random Failure. Acceleration not performed.");
            return velocity;
        }

        int[] newVelo = new int[2];
        newVelo[0] = acc[0] + velocity[0];
        newVelo[1] = acc[1] + velocity[1];
        //Check speed cap ---- already done in alg
        if (newVelo[0] > topSpeed || newVelo[0] < -topSpeed || newVelo[1] > topSpeed || newVelo[1] < -topSpeed){
            //System.out.println("Already at max speed in a certain axis");
            return velocity;
        }
        else{
            this.velocity = newVelo;
            return newVelo;
        }
    }
    
    public int[] initializePosition(char[][] track){
        //Set position to random start square
        List<Point> startLine = new ArrayList<>();
        for (int i = 0; i < track.length; i++){
            for (int j = 0; j < track[0].length; j++){
                if (track[i][j] == 'S'){
                    Point sP = new Point(i, j);
                    startLine.add(sP);
                }
            }
        }
        Random rand = new Random();
        int randIndex = rand.nextInt(startLine.size());
        Point posPT = startLine.get(randIndex);
        int[] pos = new int[2];
        pos[0] = posPT.x;
        pos[1] = posPT.y;
        this.xPos = posPT.x;
        this.yPos = posPT.y;
        return pos;
    }
    
    
    private List<Point> getLinePoints(int x0, int y0, int x1, int y1){
        //Bresenham's line drawing alg
        List<Point> linePoints = new ArrayList<>();

        int xDif = Math.abs(x0 - x1);
        int yDif = Math.abs(y0 - y1);

        int xDirection = x0 < x1 ? 1:-1;
        int yDirection = y0 < y1 ? 1:-1;

        int error = xDif - yDif;
        int eNext;
        while(true){
            //Add points to a list that are intersected by line drawn
            linePoints.add(new Point(x0, y0));
            if (x0 == x1 && y0 == y1){
                break;
            }
            eNext = 2 * error;
            if (eNext > -yDif){
                error -= yDif;
                x0 += xDirection;
            }
            if (eNext < xDif){
                error += xDif;
                y0 += yDirection;
            }
        }
        return linePoints;
    }
    
    
    private Point checkCollision(int x0, int y0, int x1, int y1, String crashType){
        //Iterate through line list and check if the car has passed through a wall
        List<Point> linePoints = getLinePoints(x0, y0, x1, y1);
        Point prev = linePoints.get(0);
        for (Point point : linePoints){
            if (this.track[point.x][point.y] == '#') {
                return prev;
            }
                prev = point;
        }
        return null;
    }
    
     
    private boolean checkVictory(int x0, int y0, int x1, int y1){
        List<Point> linePoints = getLinePoints(x0, y0, x1, y1);
        for (Point point : linePoints){
            if (this.track[point.x][point.y] == 'F'){
                return true;
            }
        }
        return false;
    }
    
    public boolean updatePosition(String crashType){
    	
        int newXPos = xPos;
        int newYPos = yPos;
        newXPos += velocity[0];
        newYPos += velocity[1];
        if (checkCollision(xPos, yPos, newXPos, newYPos, crashType) == null){
            if (checkVictory(xPos, yPos, newXPos, newYPos)){
                return false;
            }
            this.xPos = newXPos;
            this.yPos = newYPos;
        }
        else {
            //if the car collides with a wall, the velocity is reset to zero
            Point lastBeforeCollision = checkCollision(xPos, yPos, newXPos, newYPos, crashType);
            velocity[0] = 0;
            velocity[1] = 0;
            //Position set to the first wall collided with

            this.xPos = lastBeforeCollision.x;
            this.yPos = lastBeforeCollision.y;

            if (track[xPos][yPos] == 'F'){
                return false;
            }
            if (crashType.equals("reset")){
                //If the car resets after a crash, just re-initialize position with a velocity of zero
                initializePosition(track);
            }
//            System.out.println("Wall collision detected, velocity reset");
        }
        return true;
    }

    
    ////////////////////////////////////Functions Added//////////////
    
    public boolean updatePositionV(String crashType){
    	
    	
    			
        int newXPos = xPos;
        int newYPos = yPos;
        newXPos += velocity[0];
        newYPos += velocity[1];
        if (checkCollision(xPos, yPos, newXPos, newYPos, crashType) == null){
            if (checkVictory(xPos, yPos, newXPos, newYPos)){
                return true;
            }
            this.xPos = newXPos;
            this.yPos = newYPos;
        }
        else {
            //if the car collides with a wall, the velocity is reset to zero
            Point lastBeforeCollision = checkCollision(xPos, yPos, newXPos, newYPos, crashType);
            velocity[0] = 0;
            velocity[1] = 0;
            //Position set to the first wall collided with

            this.xPos = lastBeforeCollision.x;
            this.yPos = lastBeforeCollision.y;

            if (track[xPos][yPos] == 'F'){
                return true;
            }
            if (crashType.equals("reset")){
                //If the car resets after a crash, just re-initialize position with a velocity of zero
                moveBacktoOrigin();
            }
//            System.out.println("Wall collision detected, velocity reset");
        }
        return false;
    }
    
    
    
    
    public boolean updatePositionVN(String crashType){
        
    	
    	int newXPos = xPos;
        int newYPos = yPos;
        newXPos += velocity[0];
        newYPos += velocity[1];
        if (checkCollision(xPos, yPos, newXPos, newYPos, crashType) == null){
            if (checkVictory(xPos, yPos, newXPos, newYPos)){
                return false;
            }
            this.xPos = newXPos;
            this.yPos = newYPos;
        }
        else {
            //if the car collides with a wall, the velocity is reset to zero
            Point lastBeforeCollision = checkCollision(xPos, yPos, newXPos, newYPos, crashType);
            velocity[0] = 0;
            velocity[1] = 0;
            //Position set to the first wall collided with

            this.xPos = lastBeforeCollision.x;
            this.yPos = lastBeforeCollision.y;

            if (track[xPos][yPos] == 'F'){
                return false;
            }
            if (crashType.equals("reset")){
                //If the car resets after a crash, just re-initialize position with a velocity of zero
                moveBacktoOrigin();
            }
//            System.out.println("Wall collision detected, velocity reset");
        }
        return true;
    }
    
    
    
    public boolean applyAccelerationAlways(int[] acc ){

    	Ovelocity = this.velocity;
        int[] newVelo = new int[2];
        newVelo[0] = acc[0] + velocity[0];
        newVelo[1] = acc[1] + velocity[1];
        //Check speed cap ---- already done in alg
        if (newVelo[0] > topSpeed || newVelo[0] < -topSpeed || newVelo[1] > topSpeed || newVelo[1] < -topSpeed){
            //System.out.println("Already at max speed in a certain axis");
            return false;
        }
        else{
            this.velocity = newVelo;
            return true;
        }
    }
    
	//select one starting point randomly    
    public int[] randomStartingPoint() {
    	
    	//System.out.println(startPoints.size());
    	int sInd = rand.nextInt(0,this.trk.getStartPoints().size());
    	return this.trk.getStartPoints().get(sInd);
    }
    
    public int[] minStartingPoint() {
    	
    	int minSum = Integer.MAX_VALUE;
    	int startingP[] = trk.getStartPoints().get(0);
    	
    	for (int[] sp : trk.getStartPoints()) {
			
    		int sum = sp[0] + sp[1];
    		if(minSum < sum) {
    			minSum = sum;
    			startingP = sp; 
    		}
		}
    	
    	return startingP;
    }
    
    private void moveBacktoOrigin() {

    	//new position to one of the starting cells
    	int[] temp  = minStartingPoint();
    	xPos = temp[0];
    	yPos = temp[1];
    	
    	this.velocity[0] = 0;
    	this.velocity[1] = 0;
    }

    private void moveToNearest(int[] temp) {

    	int[] nPos = getNearestTrack(temp);
    	xPos = nPos[0];
    	yPos = nPos[1];
    	
    	this.velocity[0] = 0;
    	this.velocity[1] = 0;
    }
    
    private Double calculateDistance(int[] position, int[] trackPoint) {
        Double distance = 0.0;
        
        distance = Math.pow((position[0] - trackPoint[0]), 2) +
        		Math.pow((position[1] - trackPoint[1]), 2);
      
        return Math.sqrt(distance);
    }

    //function that finds the nearest track point of the crash
    private int[] getNearestTrack(int[] position ) {
    	
    	//System.out.println("Position " + position[0]+ " " + position[1]);
    	Double minDis = Double.MAX_VALUE;
    	Double Dis;
    	int[] location = null;
    	
    	//calculating the distance with all track cells to see which one is nearest
    	for (int[] trkL : this.trk.getTrackPoints()) {
    		//System.out.println(trkL[0] +" "+ trkL[1]);
			Dis = calculateDistance(position, trkL);
			
			if(Dis < minDis) {
				minDis = Dis;
				location = trkL;
			}
		}
    	return location;
    }
    
    
    
    public static void main(String[] args) {
        Track tr = new Track();
        tr.loadTrack("W");
        char[][] track = tr.getTrack();
        Car car = new Car(tr);
        car.initializePosition(track);
        System.out.println(car.xPos);
        System.out.println(car.yPos);
     //   tr.applyCarPos(car.xPos, car.yPos);
        tr.printTrack();
        System.out.println();
        System.out.println(Arrays.toString(car.getVelocity()));
        int[] acc = {0, 1};
        car.applyAcceleration(acc);
       // tr.applyCarPos(car.xPos, car.yPos);
        tr.printTrack();
        System.out.println();
        System.out.println(Arrays.toString(car.getVelocity()));
        System.out.println(car.getState());
    }

}
