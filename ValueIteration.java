import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class ValueIteration {
	
	private static Map<String, Track> tracks;
	private static int [][] actions ={ {0,0}, {0, 1}, {0,-1}, {1,0}, 
			{1,1}, {1,-1}, {-1,0}, {-1,1}, {-1,-1} };
	private Random rand ;
	private Car car;
	private Double [][][][] best_v_table; 
	private Double [][][][][] v_table;
	private int [][][][] p_table;
	
	
	ValueIteration(Map<String, Track> Alltracks){
		tracks = Alltracks;
		
	}
	
	public void learning(String trackname){
		
		Track trk = tracks.get(trackname);
		int row = trk.getTotalRow();
		int col = trk.getTotalCol();
		
		boolean []rowVisited = new boolean[row];
		boolean []colVisited = new boolean[col];
		
		car = new Car(trk);
		initializeBVT(row, col);
		initializeVT(row, col);
		initializePT(row, col);
		
		//hyper parameters, needs to be tuned
        Double cost = -1.0;
        Double threshold = 0.0;
        Double discount = 0.95;
        int totalIteration = 10000;
        
        //temp variables
        boolean done = false;
        int count = 0;
        int locationVisited = 0;
        Double [][][][] old_v_table; 
        int[] tempArray = new int[2];
        Double new_s_v = 0.0;
        Double newN_s_v = 0.0;
        Double value;
        Double old_value;
        Double max_Value = -1000.0;
        Double delta;
        Double max_delta = 0.0;
        int bestPolicy = 0;
        boolean allrowVisited = false;
        boolean allcolVisited = false;
        
        while ((! done) && (count != totalIteration)) {
        	        
            old_v_table = best_v_table.clone();
            max_delta = 0.0;
            bestPolicy = 0;
            locationVisited = 0;
            
            int[] stPoint = car.minStartingPoint();
            int i = stPoint[0];
            int j = stPoint[1];
            
            allrowVisited = false;
            resetRowArray(rowVisited);
            //for each state which is the x y location and velocity v_x, v_y            
            while(! allrowVisited ) {
            	
            	j = stPoint[1];
            	allcolVisited = false;
            	resetColArray(colVisited);
            	
    			while(! allcolVisited) {
    				
					//check if the position i, j is the valid position ?
					if(trk.isValid(i, j)) {			
    				
						for(int k = -5; k <= 5; k++ ) {
    					for(int l = -5; l <= 5; l++) {
    						

    							   						
    						max_Value = -1000.0;
    						    						
    						//for each action
    						for (int m = 0; m < actions.length; m++) {
    							
//    							if(trk.isFinished(i,j)) {
//    								//System.out.println("Points are on Finish Line" +i + " " + j );
//    								cost = 0.0;
//    							}else {
//    								cost = -1.0;
//    							}
    							
    							//set the car position and velocity and then apply acceleration 
    							car.nPos[0] = i;
    							car.nPos[1] = j;

    							car.velocity[0] = k;
    							car.velocity[1] = l;
    							
    							//System.out.println(i + " " + j + " "+ k + " " + l + " " +m);

    							//apply acceleration (action) and update position
    							boolean acc = car.applyAccAlways(actions[m]);
    							boolean pos = car.updatePosition();
    							

    							//get the car's updated state value from the best_value table
    							
            					if(pos && locationVisited >= (trk.getTrackPoints().size()/10)) {
            						
            						cost = 0.0;
            						System.out.println("Car crossed the Finished line. Now the cost is "+ cost);
            						new_s_v = 0.0;
            						
            					}else {
             					//get the value of the new state after taking action
            					//car velocity is from -5 to 5, but index is from 0 to 11  
            					cost = -1.0;	
            					new_s_v = old_v_table[car.nPos[0]][car.nPos[1]][car.velocity[0]+5][car.velocity[1]+5];   					
            					}
            					//get the value of the new state without taking action
            					pos = car.updatePositionWithoutAcc();
            					if(pos && locationVisited >= (trk.getTrackPoints().size()/10)) {        						
            						cost = 0.0;
            						System.out.println("Car crossed the Finished line. Now the cost is "+ cost);
            						newN_s_v = 0.0;
            					}else {
            					cost = -1.0;	
            					newN_s_v  = old_v_table[car.nPos[0]][car.nPos[1]][car.velocity[0]+5][car.velocity[1]+5];
            					}
            					value = cost + discount*((0.8 * new_s_v) + (0.2 * newN_s_v));
    							
            					//store it in the value table
    							v_table[i][j][k+5][l+5][m] = value;
    							
    							if (value > max_Value) {
    								max_Value = value;
    								bestPolicy = m;
    							}
    							
    						
    						}//end of loop of action
    						
    						//we can save the acceleration of max value as well, that will make the policy
    						//now we have the max value out of all the accelerations for this state
    						old_value = best_v_table[i][j][k+5][l+5];
    						best_v_table[i][j][k+5][l+5] = max_Value;
    						p_table[i][j][k+5][l+5] = bestPolicy;
    						
    						
    						delta = old_value - max_Value;
                            if(delta > max_delta) {                            	
                            	max_delta = delta;
                            }
    					
    						
    						
    					} 
    				}//end of velocity loop
    				}//if valid
					locationVisited++;
    				colVisited[j] = true;
    				if(allColVisited(colVisited)) {
    					allcolVisited = true;
    				}else {
    				j++;
    				
    				if(j >= col) {
    					//j == cols, but all columns are not visited 
    					if(! allColVisited(colVisited)) {
    						j = 0;
    					}
    					else {
    						allcolVisited = true;
    					}
    				}
    				}
            	}
    			
    			
				rowVisited[i] = true;
				if(allRowVisited(rowVisited)) {
					allrowVisited = true;
				}else {
				
				i++;
				if(i >= row) {
					//i == row, but all rows are not visited 
					if(! allRowVisited(rowVisited)) {
						i = 0;
					}
					else {
						allrowVisited = true;
					}
				}
				}
    			   			
            }
    		//end for loop of state
    		if(max_delta < threshold) {
    			System.out.print("Algorithim converged."+ max_delta);
    			done = true;
    			break;
    		}
    		System.out.println("Count of Iterations: "+ count);
    		count++;
        }//end of while loop
	}
        
	
	private void resetRowArray(boolean[] rowVisited ) {
		
		for(int i = 0; i < rowVisited.length; i ++) {
			rowVisited[i] = false;
		}
	}
	
	private void resetColArray(boolean [] colVisited) {
		
		for(int i = 0; i < colVisited.length; i ++) {
			colVisited[i] = false;
		}
	}
	
	private boolean allRowVisited(boolean[] rowVisited) {
		
		for (boolean b : rowVisited) {
			
			if(!b) {
				return false;
			}
		}
		return true;
	}
	
	private boolean allColVisited(boolean [] colVisited) {
	
		for (boolean b : colVisited) {
			
			if(!b) {
				return false;
			}
		}
		return true;
	}
	

	//Policy table will save the policy that is what acc should be applied to each state 
	private void initializePT(int row, int col) {
		
		p_table = new int[row][col][11][11];
		
		
		//setting all values of v table to 0
		for(int i = 0; i < row; i++) {
			for(int j = 0; j < col; j++) {
				for(int k = 0; k < 11; k++ ) {
					for(int l = 0; l < 11; l++) {
						
						p_table[i][j][k][l] = 0;
					}
				}
					
			}
		}
		
	}
	
	
	//table V(s) that will store the optimal Q-value for each state
	private void initializeBVT(int row, int col) {
		
		best_v_table = new Double[row][col][11][11];
		
		
		//setting all values of v table to 0
		for(int i = 0; i < row; i++) {
			for(int j = 0; j < col; j++) {
				for(int k = 0; k < 11; k++ ) {
					for(int l = 0; l < 11; l++) {
						
						best_v_table[i][j][k][l] = 0.0;
					}
				}
					
			}
		}
		
	}
	
	private void initializeVT(int row, int col) {
		
		v_table = new Double[row][col][11][11][9];
		
		for(int i = 0; i < row; i++) {
			for(int j = 0; j < col; j++) {
				for(int k = 0; k < 11; k++ ) {
					for(int l = 0; l < 11; l++) {
						for(int m = 0; m < actions.length; m++) {
							v_table[i][j][k][l][m] = 0.0;
							
						}
						
					}
				}
					
			}
		}
		
	}
	
	
	public void testBestPolicy(String trackName) {
		
		Track trk = tracks.get(trackName);
        Car car = new Car(trk, true);
        
        int[] temp = car.randomStartingPoint(); 
        car.nPos[0] = temp[0];
        car.nPos[1] = temp[1];
        car.velocity[0] = 0; 
        car.velocity[1] = 0;
        int i,j,k,l,policy ;
        boolean acc, pos;
        int count = 1;

        boolean is_finished = false;
        while (! is_finished && count != 1000) {
        	
        	i = car.nPos[0];
        	j = car.nPos[1];
        	k = car.velocity[0] + 5;
        	l = car.velocity[1] + 5;
        	
        	System.out.println("Position of car is: "+i+ " " + j );
        	policy = p_table[i][j][k][l];
        	acc = car.applyAcceleration(actions[policy]);
        	pos = car.updatePosition();
        	
        	if(pos) {
        		System.out.println("Car crossed the Finished line in Test");
        		System.out.println("Steps car took to finish the race " + count);
        		is_finished = true;
        		break;
        	}
        	count++;
        }
        if(!is_finished) 
        	System.out.println("Car couldn't finish the race even in " + count + " steps");
	}

	public static void main(String[] args) {
		
	}
}
