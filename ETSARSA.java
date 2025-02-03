import java.util.Map;
import java.util.Random;

public class ETSARSA {
	private static Map<String, Track> tracks;
	private static int [][] actions ={ {0,0}, {0, 1}, {0,-1}, {1,0}, 
			{1,1}, {1,-1}, {-1,0}, {-1,1}, {-1,-1} };
	private Random rand  = new Random();
	private Car car;
	private Double [][][][][] q_table;
	private int [][][][][] et_table;
	
    int Cost = -1;
    Double discount = 0.95;
    Double epsilon = 0.5;
    Double learning_rate = 0.75;
    Double decay = 0.9999;
    
	
	public ETSARSA(Map<String, Track> Alltracks){
		tracks = Alltracks;
	}
	
	
	public void learning(String trackname) {
	    Track trk = tracks.get(trackname);
	    int row = trk.getTotalRow();
	    int col = trk.getTotalCol();

	    car = new Car(trk, true);
	    initializeQT(row, col);


	    int totalIteration = 50000;
	    int totalSteps = 1000;

	    int count = 0;
	    while (count != totalIteration) {
	        // initializing the car from one of the starting position
	        int[] tempArray = car.randomStartingPoint();
	        int i = tempArray[0];
	        int j = tempArray[1];
	        int k = 0;
	        int l = 0;
	        car.nPos[0] = i;
	        car.nPos[1] = j;
	        car.velocity[0] = k;
	        car.velocity[1] = l;
	        int ni, nj, nk, nl;


	        int steps = 0;
	        boolean is_finished = false;
	        while (!is_finished && steps != totalSteps) {
	            // get all values of action for this state
	            Double[] qValues = q_table[i][j][k + 5][l + 5];

	            // select the action either the action with max value
	            // or randomly selecting the action
	            int AccIndex = actionSelection(qValues, epsilon);
	            // current q value of this acceleration
	            Double current_q_value = qValues[AccIndex];

	            boolean acc = car.applyAcceleration(actions[AccIndex]);
	            boolean pos = car.updatePosition();

	            if (pos) {
	                System.out.println("Car crossed the Finished line");
	                is_finished = true;
	                break;
	            } else {
	                // set i, j, k, and l (state to new state)
	                ni = car.nPos[0];
	                nj = car.nPos[1];
	                nk = car.velocity[0];
	                nl = car.velocity[1];

	                // calculate new qValue for this acceleration with new state
	                Double[] newQValues = q_table[ni][nj][nk + 5][nl + 5];
	                int index = maxQValueforAcc(newQValues);
	                Double new_q_value = newQValues[index];

	                q_table[i][j][k+5][l+5][AccIndex]+= (learning_rate * (Cost + discount * new_q_value - current_q_value ));
	               
	                // Update eligibility traces for all state-action pairs
	                et_table[i][j][k][l][AccIndex] += 1;

	                //update Qvalue and Eligibility trace 
	                updateQTandET(row, col, new_q_value, current_q_value);
	                i = ni;
	                j = nj;
	                k = nk;
	                l = nl;
	            }
	            steps++;
	        }

	        // anealing epsilon and learning rate through the process
	        epsilon *= decay;
	        if (learning_rate > 0.01) {
	            learning_rate *= decay;
	        }
	        System.out.println("Count of iterations: " + count);
	        count++;
	    }
	}
	
	private void updateQTandET(int row, int col, Double newQValue, Double currentQValue) {
        
        for (int x = 0; x < row; x++) {
            for (int y = 0; y < col; y++) {
                for (int v = 0; v < 11; v++) {
                    for (int w = 0; w < 11; w++) {
                        for (int a = 0; a < actions.length; a++) {
                            q_table[x][y][v][w][a] += learning_rate *
                                    et_table[x][y][v][w][a] * (Cost + discount * newQValue - currentQValue);
                            // Decay eligibility traces
                            et_table[x][y][v][w][a] *= discount * decay;
                        }
                    }
                }
            }
        }
	}

	
	//return the index of actions array
	private int actionSelection(Double[] qValues, Double epsilon) {
		
		Double random = rand.nextDouble();
		
		if( random < epsilon) {
			//exploration
			//choose the action randomly
			int r = rand.nextInt(0,actions.length);
			return r;
		}else {
			//get the max value index from qValues
			//exploitation
			return maxQValueforAcc(qValues);
		}
	}
	
	
	//return index
	private int maxQValueforAcc(Double[] array) {
		int index = 0; 
		Double maxValue = -100000.0;
		
		for(int i = 0; i < array.length; i++ ) {
			
			if(array[i] > maxValue) {
				maxValue = array[i];
				index = i;
			}
		}
		return index;	
	}
	
	
	private void initializeQT(int row, int col) {
		
		q_table = new Double[row][col][11][11][actions.length];
		
		for(int i = 0; i < row; i++) {
			for(int j = 0; j < col; j++) {
				for(int k = 0; k < 11; k++ ) {
					for(int l = 0; l < 11; l++) {
						for(int m = 0; m < actions.length; m++) {
							q_table[i][j][k][l][m] = rand.nextDouble();
							
						}
						
					}
				}
					
			}
		}
		
	}
	
	
	private void initializeET(int row, int col) {
		
		et_table = new int[row][col][11][11][9];
		
		for(int i = 0; i < row; i++) {
			for(int j = 0; j < col; j++) {
				for(int k = 0; k < 11; k++ ) {
					for(int l = 0; l < 11; l++) {
						for(int m = 0; m < actions.length; m++) {
							et_table[i][j][k][l][m] = 0;
							
						}
						
					}
				}
					
			}
		}
		
	}
	
	public void testSARSAlearning(String trackName) {
		
		Track trk = tracks.get(trackName);
        Car car = new Car(trk, true);
        
        int[] temp = car.randomStartingPoint(); 
        car.nPos[0] = temp[0];
        car.nPos[1] = temp[1];
        car.velocity[0] = 0; 
        car.velocity[1] = 0;
        int i,j,k,l, policyIndex;
        boolean acc, pos;
        int steps = 0;
        Double[] qValues;

        boolean is_finished = false;
        while (! is_finished && steps != 3000) {
        	
        	i = car.nPos[0];
        	j = car.nPos[1];
        	k = car.velocity[0] + 5;
        	l = car.velocity[1] + 5;
        	
        	qValues = q_table[i][j][k][l];
        	
        	//apply the acceleration with highest q value
        	policyIndex = actionSelection(qValues, 0.5);
        	acc = car.applyAcceleration(actions[policyIndex]);
        	pos = car.updatePosition();
        	
        	if(pos) {
        		System.out.println("Car crossed the Finished line in Test");
        		System.out.println("Steps car took to finish the race " + steps);
        		is_finished = true;
        	}
        	steps++;
        }
        if(! is_finished)
        System.out.println("Car couldn't finish the race even in " + steps + " steps");
	}
	


		
	
	

}
