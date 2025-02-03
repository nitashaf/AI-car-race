import java.util.Map;
import java.util.Random;

public class VIteration {

	private static Map<String, Track> tracks;
	private static int [][] actions ={ {0,0}, {0, 1}, {0,-1}, {1,0}, 
			{1,1}, {1,-1}, {-1,0}, {-1,1}, {-1,-1} };
	private Car car;
	private Double [][][][] best_v_table; 
	private Double [][][][][] v_table;
	private int [][][][] p_table;
	
	
	VIteration(Map<String, Track> Alltracks){
		tracks = Alltracks;
		
	}
	
	public void learning(String trackname){
		
		Track trk = tracks.get(trackname);
		int row = trk.getTotalRow();
		int col = trk.getTotalCol();

		
		car = new Car(trk);
		initializeBVT(row, col);
		initializeVT(row, col);
		initializePT(row, col);
		
		//hyper parameters, needs to be tuned
        Double cost = -1.0;
        Double threshold = 0.0001;
        Double discount = 0.95;
        int totalIteration = 5000;
        
       // Double[] DiscountArray = {0.2,0.4,0.6,0.8,1.0,1.2};
        boolean done = false;
        int count = 0;
        Double [][][][] old_v_table;  
        Double new_s_v = 0.0;
        Double newN_s_v = 0.0;
        Double value;
        Double old_value;
        Double max_Value = -1000.0;
        Double delta;
        Double max_delta = 0.0;
        int bestPolicy = 0;

        
       //This code I did to gather data for different discount rate //     
       // for(int d = 0; d < DiscountArray.length; d++) {
       // 	discount = DiscountArray[d];
       // 	done = false;
       // 	count =0;
        
        while ((! done) && (count != totalIteration)){
        	
        	//System.out.println("Count of Iterations for discount : "+ discount);        
            old_v_table = best_v_table.clone();
            max_delta = 0.0;
            bestPolicy = 0;
            
            for(int i =0; i < row; i++ ) {
            	for(int j = 0; j < col; j++) {
					//check if the position i, j is the valid position ?
					if(trk.isValid(i, j)) {
						
						for(int k = -5; k <= 5; k++ ) {
							for(int l = -5; l <= 5; l++) {
    						    							   						
    						max_Value = -1000.0;
    						    						
    						//for each action
    						for (int m = 0; m < actions.length; m++) {
    							

    							car.xPos = i;
    							car.yPos = j;

    							car.velocity[0] = k;
    							car.velocity[1] = l;
    							
    							

    							//apply acceleration (action) and update position
    							boolean acc = car.applyAccelerationAlways(actions[m]);
    							boolean pos1 = car.updatePositionV("");
    							

    							//get the car's updated state value from the best_value table
    							
            					if(pos1) {
            						
            						cost = -0.5;
            						//System.out.println("Car crossed the Finished line. Now the cost is "+ cost);
            						new_s_v = 0.0;
            						
            					}else {
             					//get the value of the new state after taking action
            					//car velocity is from -5 to 5, but index is from 0 to 11  
            					cost = -1.0;	
            					new_s_v = old_v_table[car.xPos][car.yPos][car.velocity[0]+5][car.velocity[1]+5];   					
            					}
            					
            					
    							car.xPos = i;
    							car.yPos = j;

    							car.velocity[0] = k;
    							car.velocity[1] = l;
            					//get the car's updated state value from the best_value table
            					boolean pos2 = car.updatePositionV("");
            					
            					if(pos2) {
            						
            						cost = -0.5;
            						//System.out.println("Car crossed the Finished line. Now the cost is "+ cost);
            						newN_s_v = 0.0;
            						
            					}else {
             					//get the value of the new state without taking action
            					//car velocity is from -5 to 5, but index is from 0 to 11  
            					cost = -1.0;	
            					newN_s_v = old_v_table[car.xPos][car.yPos][car.velocity[0]+5][car.velocity[1]+5];   					
            					}
            					
            					if(pos1 && pos2) {
            						cost = 0.0;
            					}
            						
            					//to make code deterministic, instead of having acceleration in the 
            					//acceleration function, I used this equation
            					value = cost + discount* (0.8*new_s_v + 0.2*newN_s_v);
    							
            					//store it in the value table
    							v_table[i][j][k+5][l+5][m] = value;
    							
    							if (value > max_Value) {
    								max_Value = value;
    								bestPolicy = m;
    							}
    							
    						
    						}//end of loop of action
    						
    						//we can save the acceleration of max value as well, that will make the policy
    						//now we have the max value out of all the accelerations for this state
    						
    						//
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
				   }//end of y pos
            	}//end of x pos
            
			
    			   			
            
    		//end for loop of state
    		if(max_delta < threshold) {
    			System.out.print("Algorithim converged."+ max_delta);
    			done = true;
    			break;
    		}
    		System.out.println("Count of Iterations: "+ count);
    		count++;
	       }//end of while loop
 
        //This was just being done to collect data of different discount factor       
        //testBestPolicy(trackname);
        //}//discount loop
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
	
	
	//table V(s) that will store the optimal Q-value for each state action
	//this will store the best value of each state out of all actions
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
	
	//storing all the values for each action 
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
	
	
	//function to test the best policy
	public void testBestPolicy(String trackName) {
		
		Track trk = tracks.get(trackName);
        Car car = new Car(trk);
        
        int[] temp = car.randomStartingPoint(); 
        car.xPos = temp[0];
        car.yPos = temp[1];
        car.velocity[0] = 0; 
        car.velocity[1] = 0;
        int i,j,k,l,policy ;
        boolean acc, pos;
        int count = 1;

        boolean is_finished = false;
        while (! is_finished && count != 1000) {
        	
        	i = car.xPos;
        	j = car.yPos;
        	k = car.velocity[0] + 5;
        	l = car.velocity[1] + 5;
        	
        	//System.out.println("Position of car is: "+i+ " " + j );
        	//trk.printTrackwithCar(i, j);
        	policy = p_table[i][j][k][l];
        	acc = car.applyAccelerationAlways(actions[policy]);
        	pos = car.updatePositionV("");
        	
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
