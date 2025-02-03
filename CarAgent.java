import java.util.Random;

public class CarAgent {
    int[] velocity = new int[2];//Current Carr Velocity x,y
    int[] Ovelocity = new int[2];
    int topSpeed = 5;
    int [] oPos = new int[2];
    int [] nPos = new int[2];
    //change this to change the crash scenario
    boolean crashNear;
    Track trk;
    private Random rand  = new Random();


    //constructor
    CarAgent(Track tr, Boolean Cr){
    	trk = tr;
    	crashNear = Cr;
    }

    public int[] getVelocity(){
        return this.velocity;
    }

    public int[] setVelocity(int[] newVelo){
        this.velocity = newVelo;
        return newVelo;
    }

    public boolean applyAcceleration(int[] acc ){
        Random rand = new Random();

        //80% chance that acceleration is being applied
        double nDcheck = rand.nextDouble();
        if (nDcheck <= 0.2){
        	//System.out.println("Acceleration not applied");
        	return false;
        }

        //applying velocity
        int[] newVelo = new int[2];
        newVelo[0] = acc[0] + velocity[0];
        newVelo[1] = acc[1] + velocity[1];
        if (newVelo[0] > topSpeed || newVelo[0] < -topSpeed || newVelo[1] > topSpeed || newVelo[1] < -topSpeed){
        	//System.out.println("Velocity exceeded the limit");
        	//System.out.println("Acceleration not applied");
        	return false;
        }
        else{
        	updateVelocity(newVelo);
        	return true;
        }

    }
    
    public boolean applyAccAlways(int[] acc ){

    	Ovelocity = velocity.clone();
        //applying velocity
        int[] newVelo = new int[2];
        newVelo[0] = acc[0] + velocity[0];
        newVelo[1] = acc[1] + velocity[1];
        if (newVelo[0] > topSpeed || newVelo[0] < -topSpeed || newVelo[1] > topSpeed || newVelo[1] < -topSpeed){
        	//System.out.println("Velocity exceeded the limit");
        	//System.out.println("Acceleration not applied");
        	return false;
        }
        else{
        	updateVelocity(newVelo);
        	return true;
        }

    }
    

    private void updateVelocity(int[] Newvelocity) {

    	this.velocity[0] = Newvelocity[0];
    	this.velocity[1] = Newvelocity[1];
    }
    
    
    //always call this function after applying acceleration, 
    //so old Position variable has the correct value
    public boolean updatePositionWithoutAcc(){
    	
    	int [] tempNewPosition = new int[2];
    	tempNewPosition[0] = oPos[0] + Ovelocity[0];
    	tempNewPosition[1] = oPos[1] + Ovelocity[1];

    	//check if new position hits wall or finish or is in track
    	if(trk.CarinTrack(tempNewPosition)) {
    		if(trk.isCrossFLine(oPos, tempNewPosition)){
    			//System.out.print("Car crossed the finished line");
    			nPos = tempNewPosition;
    			return true;
    		}
    	}
        //crash
        else if(! trk.CarinTrack(tempNewPosition)) {

        	//call crash scenario
        	if(crashNear) {
        		moveToNearest(tempNewPosition);
        	}else {
        		moveBacktoOrigin();
        	}
        	return false;
        }else {
        	nPos = tempNewPosition;
        }
        
        velocity[0] = Ovelocity[0];
        velocity[1] = Ovelocity[1];
        return false;
    }

    


    public boolean updatePosition(){

    	oPos = nPos;

    	int [] tempNewPosition = new int[2];
    	tempNewPosition[0] = nPos[0] + velocity[0];
    	tempNewPosition[1] = nPos[1] + velocity[1];

    	//check if new position hits wall or finish or is in track
    	if(trk.CarinTrack(tempNewPosition)) {
    		if(trk.isCrossFLine(oPos, tempNewPosition)){
    			//System.out.print("Car crossed the finished line");
    			nPos = tempNewPosition;
    			return true;
    		}
    	}
        //crash
        else if(! trk.CarinTrack(tempNewPosition)) {

        	//call crash scenario
        	if(crashNear) {
        		moveToNearest(tempNewPosition);
        	}else {
        		moveBacktoOrigin();
        	}
        	return false;
        }else {
        	nPos = tempNewPosition;
        }
        return false;
    }
    

    private void moveBacktoOrigin() {

    	//new position to one of the starting cells
    	nPos = minStartingPoint();
    	this.velocity[0] = 0;
    	this.velocity[1] = 0;
    }

    private void moveToNearest(int[] temp) {

    	nPos = getNearestTrack(temp);
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


}
