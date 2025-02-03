import java.util.HashMap;
import java.util.Map;

public class Driver {
	
	//Hashmap of all the tracks with names
	private static Map<String, Track> tracks;
	
	
    public static void main(String[] args) {
        Track tr;
        tracks = new HashMap<String, Track>();
        
        String [] trackNames = {"O", "R", "W", "L"};
        for (String string : trackNames) {
        	tr = new Track();
        	tr.loadTrack(string);
        	//tr.printTrack();
        	tracks.put(string, tr);
        	
		}
        
        
        VIteration vI = new VIteration(tracks);
        vI.learning(trackNames[3]);
        vI.testBestPolicy(trackNames[3]);
//        QLearning qL =new QLearning(tracks);
//        qL.learning(trackNames[0]);
//        qL.testQlearning(trackNames[0]);
//        SARSA sarsa = new SARSA(tracks);
//        sarsa.learning(trackNames[0]);
//        sarsa.testSARSAlearning(trackNames[0]);
    }
    
	
}
