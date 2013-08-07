package tri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class UCT {

	/**
	 * @param args
	 */

	public static void main(String[] args) {
		
		int C = 40; //-- balancing exploitation and exploration --//
		int randSeed = 1;
				
		System.out.println("Test UCT");
		
		float[] partcpRate = {0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.7f, 1.0f};
		int granularity = 8;
		
		UCTTree tree = new UCTTree(partcpRate, granularity, C, randSeed);
		
		float bestValue = -100;
		int[] bestPath = new int[partcpRate.length];
		
		for (int i=0;i<100;i++){
			int[] onePass= tree.makeOnePass();
			
			// evaluate one pass
			float evalValue = 0f;
			for (int j=0; j<onePass.length;j++){
				evalValue = evalValue - onePass[j]*j; 
			}
			
			if (evalValue>bestValue){
				bestValue = evalValue;
				System.arraycopy(onePass, 0, bestPath, 0, onePass.length);
			}
			
			System.out.println(i+": Chosen pass: "+Util.fastArrToStr(onePass)+", given value: "+evalValue);
			tree.propagateScore(onePass,evalValue);
			System.out.println();
		}
		System.out.println("bestValue="+bestValue);
		System.out.println("bestNode ="+Util.fastArrToStr(bestPath));
		
		/*		
		int MASK_STREAM = 0b00000001;
		int MASK_FILE = 0b00000010;
		
		int verbose = 0;
		
		if ( (verbose & MASK_STREAM) != 0 ) System.out.println("write into stream");
		if ( (verbose & MASK_FILE) != 0 ) System.out.println("write into file");
		*/
		
				
		
		/*
		HashMap<String,UCTData> stateMap = new HashMap<String,UCTData>();

		int[] state1 = {1,0,0,0}; 
		int[] state2 = {0,2,0,0}; 
		int[] state3 = {0,0,3,0}; 
		int[] state4 = {1,0,0,0};
		
		UCTData data1 = new UCTData(1,1);
		UCTData data2 = new UCTData(2,2);
		UCTData data3 = new UCTData(3,3);
		UCTData data4 = new UCTData(4,4);
		
		
		stateMap.put(Util.fastArrToStr(state1), data1);
		stateMap.put(Util.fastArrToStr(state2), data2);
		stateMap.put(Util.fastArrToStr(state3), data3);
		
		System.out.println(stateMap);
		
		if (stateMap.containsKey(Util.fastArrToStr(state4)) == true) {
			System.out.println("yes");
			UCTData data = stateMap.get(Util.fastArrToStr(state4));
			System.out.println(data.numVisit + "," + data.totalScore);
			stateMap.put(Util.fastArrToStr(state4), data4);
			data = stateMap.get(Util.fastArrToStr(state4));
			System.out.println(data.numVisit + "," + data.totalScore);
		} else {
			System.out.println("no");
		}
		System.out.println("1/4 = " + 1/4f); 
		*/
	}
	

//	public static String arrToStr(int[] Arr){
//		String result="";
//		for (int i=0; i<Arr.length; i++){
//			result = result + Arr[i] + ","; 
//		}
//		return result;
//	}

}

class UCTTree{
	
	private HashMap<String,UCTData> stateMap;
	private float[] partcpRate;
	private int granularity;
	private float cTune;
	private Random rand;
	
	/**
	 * Constructor
	 * @param PartcpRate
	 * @param Height
	 */
	public UCTTree(float[] PartcpRate, int Granularity, float C, int RandSeed){
		init(PartcpRate, Granularity, C, RandSeed);
	}
		
	private void init(float[] PartcpRate, int Granularity, float C, int RandSeed){
		stateMap = new HashMap<String,UCTData>();
		
		partcpRate = new float[PartcpRate.length];
		System.arraycopy(PartcpRate, 0, partcpRate, 0, PartcpRate.length);
		
		granularity = Granularity;
		
		cTune = C;
					
		rand = new Random(RandSeed);
	}

	public void propagateScore(int[] leafNode, float evalValue) {
		int[] newNode = new int[leafNode.length];
		System.arraycopy(leafNode, 0, newNode, 0, leafNode.length); 
		// update until root
		for (int i=leafNode.length-1; i>=-1; i--) {
			String strNode = Util.fastArrToStr(newNode);
			UCTData data = stateMap.get( strNode );
			if (data == null) {
				// if no visit before
				data = new UCTData(1,evalValue);
				
			} else {
				// update the visit
				data.numVisit ++;
				data.totalScore = data.totalScore + evalValue;
			}
//			if (i==leafNode.length-1) {
//				data.subTreeCompleted = true;
//			}
			stateMap.put( strNode , data);
			
			//..System.out.println("propagate: "+ strNode + ": ["+data.numVisit+","+data.totalScore+"]");
			// set backward node
			if (i>=0) newNode[i] = -1;
			
		}
		
	}

	
	private boolean isTerminal(int[] Node) {
		if (Util.arraySum(Node) == granularity)			
			return true;
		else {
			if (Util.arraySum(Node)>granularity) {
				System.err.println("[UCTTree::isTerminal] sum of the node > granularity: "+Util.arraySum(Node) + " > " + granularity);
				System.exit(1);
			} 
			return false;
		}
	}

	public void clearStateMap(){
		stateMap.clear();
	}
	
	
	/**
	 * To traverse the tree, one pass
	 */
	public int[] makeOnePass(){
		int[] root = new int[partcpRate.length];
		for (int i=0;i<root.length;i++){
			root[i]=-1;
		}
		return traverseNode(root,0);
	}
	
	
	
	
	/**
	 * Helper method to traverse the entire tree from one node
	 * Used by method traverseOnePass();
	 * Result: a path traversed
	 * @param Node
	 */
	
	private int[] traverseNode(int[] Node, int currRound){
		
		int currSum = 0;
		for (int i=0; i<currRound; i++){
			currSum = currSum + Node[i];
		}
		
		int restSum = granularity - currSum;
		
		
		// if all population has been assigned
		
		if (currSum == granularity)	{
			for (int i=currRound; i<Node.length; i++){
				Node[i] = 0;
			}
			return Node;
		}
		
		
		// if we are in the last round, make sure that the sum is equal to "granularity"
		if ( currRound == Node.length-1 ) {
			Node[Node.length-1] = restSum;
			return Node;
		} 
				
		
		// if not terminal, then:
		
		// check the number of visit of current node 
		int nodeVisit = 0;
		UCTData dataNode = stateMap.get(Util.fastArrToStr(Node));
		if (dataNode != null){
			nodeVisit = dataNode.numVisit;
		}
		// +1 for now
		nodeVisit++;
		
		
		// create a new node for the child exploration
		int[] newNode = new int[partcpRate.length];
		System.arraycopy(Node, 0, newNode, 0, Node.length);
				
		float maxScore = Integer.MIN_VALUE;
		int bestAction = 0; 	
				
		float currScore = 0;
		ArrayList<Integer> unVisited = new ArrayList<Integer>();
		// OPT
		//int numChildFinished = 0;
		
		// all possible assignment
		for (int i=0; i<=restSum; i++){
			// create the new node
			newNode[currRound] = i;			
			UCTData dataChild = stateMap.get(Util.fastArrToStr(newNode));
			
			// so, this new Child has been visited before
			if (dataChild!=null){
				
				// OPT
//				if (dataChild.subTreeCompleted){
//					continue;
//				}
				
				// this is exploration and exploitation score
				float firstTerm = dataChild.totalScore / dataChild.numVisit;
				float secondTerm = (float) ( Math.sqrt(Math.log(nodeVisit)/dataChild.numVisit) );
				currScore = (float) ( firstTerm+ cTune*secondTerm);
				//VERBOSE
				//System.out.println("::"+Util.fastArrToStr(newNode)+"::["+firstTerm+"+"+secondTerm+"="+ currScore+"], ["+nodeVisit+","+dataChild.numVisit+"]");
				
				if (currScore > maxScore) {
					maxScore = currScore;
					bestAction = i;
				}	
				
				//OPT
				// if (dataChild.subTreeCompleted == true) numChildFinished ++; 
			} else {
				unVisited.add(i);
			}			
		}
		
		//OPT
//		if (numChildFinished == restSum+1) {
//			dataNode.subTreeCompleted = true;
//		}
		
		// if there is some unVisited node
		if (unVisited.size()>0){
			bestAction = unVisited.get( (int) (rand.nextDouble()*unVisited.size()) );
		}
		newNode[currRound] = bestAction;
		
		return traverseNode(newNode, currRound+1);
	}
	
	// OPT
	/*
	private int getNumChild(int[] Node) {
		
		if (isTerminal(Node)){
			return 0;
		}
		
		int currSum = 0;
		int i = 0;
		while ( i<Node.length && Node[i]!=-1 ){
			currSum += Node[i];
			i++;
		}		
		return granularity - currSum + 1;
	}
	*/
	
}

class UCTData{
	
	int numVisit;
	float totalScore;
	//boolean subTreeCompleted;
	
	public UCTData(int NumVisit, float TotalScore){
		numVisit = NumVisit;
		totalScore = TotalScore;
		//subTreeCompleted = false;
	}
		
}
