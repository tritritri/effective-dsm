package tri;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class Exp4 {

	/**
	 * 
	 * This is for p=0.02
	 * 
	 * @param args
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		String ApplsFile = "appliances.xml";
		String CustTypeFile = "cons100.xml";
		
		//float[] partcpRate1 = { 0.0f, 0.1f,   0.2f,   0.3f,   0.4f,   0.5f,   0.6f,   0.7f,   0.8f,   0.9f,   1.0f};
		//float[] partcpRate2 = {0.00f, 0.05f,  0.10f,  0.15f,  0.20f,  0.25f,  0.30f,  0.35f,  0.40f,  0.45f,  0.50f};
		//float[] partcpRate3 = {0.00f, 0.025f, 0.050f, 0.075f, 0.100f, 0.125f, 0.150f, 0.175f, 0.200f, 0.225f, 0.250f};
		//float[] partcpRate4 = {0.00f, 0.02f,  0.04f,  0.06f,  0.08f,  0.10f,  0.12f,  0.14f,  0.16f,  0.18f,  0.20f};
		//float[] partcpRate5 = {0.00f, 0.01f,  0.02f,  0.03f,  0.04f,  0.10f,  0.12f,  0.14f,  0.16f,  0.18f,  0.20f};
		int partcpRate6Count = 51;
		float partcpRate6Gran = 0.02f;
		float[] partcpRate6 = new float[partcpRate6Count];
		for (int i=0; i<partcpRate6Count; i++){
			partcpRate6[i] = i*partcpRate6Gran;
		}
		
		
		ArrayList<float[]> arrPartcpRate = new ArrayList<float[]>();
		
		//arrPartcpRate.add(partcpRate1);
		//arrPartcpRate.add(partcpRate2);
		//arrPartcpRate.add(partcpRate3);
		//arrPartcpRate.add(partcpRate4);
		arrPartcpRate.add(partcpRate6);
		
		int[] beta = {0};

		float[] prDist = new float[partcpRate6.length];

		float[] C = {500};
		
		
		// for writing the result
		PrintWriter pLog; 
		PrintWriter pLogDets; // for detailed file log 
		int granularity = 10;
		int UCTiter = 100;
		
		int nRound = 365;

		// let's start with 1 random seed
		DRSimulation sim = new DRSimulation(ApplsFile, CustTypeFile);
		for (int p=0; p<arrPartcpRate.size(); p++){

			float[] partcpRate = arrPartcpRate.get(p);
				
			int obj = 1;
			String dir = "Exp4-0.02//"; 
				
			for (int c=0; c<C.length; c++){

				for (int b=0; b<beta.length; b++){
				
					String fileName = dir + "Exp4-UCT-" + CustTypeFile + "-p"+ partcpRate[1] + "-C" + C[c] + "-i"+ UCTiter + "-g"+ granularity + "-b" + beta[b] + ".txt";
					
					pLog = new PrintWriter (new File(fileName));
					pLog.printf("// %s\n", Util.arrToStr(partcpRate));
					pLog.printf("r,bestVal,bestSeq\n");
					
					// for robust experiment, try 10 random seed 
					// for trial only, use 1 random seed is ok
					for (int r=1;r<=1;r++){
	
						// initialization for UCT
						float bestEvalValue = -100000;
						float bestPreEvalValue = -100000;
						float bestBetaReg = -100000;
						
						int[] bestPath = new int[partcpRate.length];
						UCTTree tree = new UCTTree(partcpRate, granularity, C[c], r);
						
						String fileDetName = dir+"Exp4-UCT-det-p"+ partcpRate[1] + "-" + CustTypeFile + "-C" + C[c] + "-i"+ UCTiter + "-g"+ granularity + "-b" + beta[b] +"-r" + r + ".txt";
						pLogDets = new PrintWriter (new File(fileDetName));
						pLogDets.printf("// %sC=%.2f, beta=%d\n", Util.arrToStr(partcpRate),C[c],beta[b]);
						pLogDets.printf("iter,bestVal,preEval,reg-penalty,currVal,currSeq\n");
						System.out.println("processed: Exp4-UCT-det-p"+ partcpRate[1] + "-" + CustTypeFile + "-C" + C[c] + "-i"+ UCTiter + "-g"+ granularity + "-b" + beta[b] +"-r" + r + ".txt");
						for (int i=0; i<UCTiter; i++){
							
							// generate a path
							int[] onePass= tree.makeOnePass();
							
							// evaluate one pass
							for (int j=0; j<partcpRate.length; j++){
								prDist[j] = (float) ((onePass[j]+0.0) / granularity);
							}
							
							// expRun(ExpNo, LogFileName, float[] partcpRate,  prDist, TakeTurn, StochOpt, NStoch, NRound, Verbose, RandomSeed) 
		
							float[] evalValues = sim.expRun(2, "", partcpRate, prDist, 0, 0, 200, nRound, 0, r); 
							float preEvalValue = -evalValues[obj]; // this is for AUP
		
							// do the regularization:
							float reg = 0.0f;
							for (int j=0; j<onePass.length; j++){
								reg = reg + (onePass[j] * partcpRate[j]);
							}
							
							float evalValue = preEvalValue - beta[b]*reg;
							
							if (evalValue>bestEvalValue){
								bestEvalValue = evalValue;
								bestPreEvalValue = preEvalValue;
								bestBetaReg = beta[b]*reg;
								System.arraycopy(onePass, 0, bestPath, 0, onePass.length);
							}
							
							String seq = Util.fastArrToStr(onePass);
							System.out.println(i+": Chosen pass: "+ seq +" given value: "+evalValue);					
							// propagate the current valuation
							tree.propagateScore(onePass,evalValue);
							pLogDets.printf("%d,%.3f,%.3f,%.3f,%3f,%s\n",i, -bestEvalValue, -preEvalValue, beta[b]*reg, -evalValue , seq); 
							pLogDets.flush();
						}
						pLogDets.close();
						
						pLog.printf("%d,%3f,%.3f,%3f,%s\n", r, -bestEvalValue, -bestPreEvalValue, bestBetaReg,  Util.fastArrToStr(bestPath));								
						pLog.flush();
					}	
					pLog.close();
				}
		
					
			}
		}


	}

}
