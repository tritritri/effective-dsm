package tri;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;




public class DRSimultv3v3{
	
	public static void main(String argv[]) throws ParserConfigurationException, SAXException, IOException  {
		
		/*
		int RandomSeed = 2;
		
		String CustTypeFile = "customers-constant.xml";
		String ApplsFile = "appliances.xml";
		
		// init basic building blocks
		//float[] partcpRate = {0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f};
		float[] partcpRate = {0.0f, 0.05f, 0.10f, 0.15f, 0.20f, 0.25f, 0.30f, 0.35f, 0.40f, 0.45f, 0.50f};
		float[] prDist = new float[partcpRate.length];

		int granularity = 10;
		int C=4;

		// init DR simulation engine
		DRSimulation sim = new DRSimulation(ApplsFile, CustTypeFile);
		
		// init UCT
		UCTTree tree = new UCTTree(partcpRate, granularity, C, RandomSeed);

		// init for capturing best value node
		float bestValue = -1000;
		int[] bestPath = new int[partcpRate.length];

		// start the UCT
		for (int i=0;i<100;i++){
			
			// generate a path
			int[] onePass= tree.makeOnePass();
			
			// evaluate one pass
			for (int j=0; j<partcpRate.length; j++){
				prDist[j] = (float) ((onePass[j]+0.0) / granularity);
			}
			
			// we want to minimize par
			float evalValue = -sim.run(partcpRate, prDist, RandomSeed);

			if (evalValue>bestValue){
				bestValue = evalValue;
				System.arraycopy(onePass, 0, bestPath, 0, onePass.length);
			}
			System.out.println(i+": Chosen pass: "+Util.fastArrToStr(onePass)+" given value: "+evalValue);
			
			// propagate the current valuation
			tree.propagateScore(onePass,evalValue);
			System.out.println();
		}

		// in the end...
		System.out.println("bestValue="+bestValue);
		System.out.println("bestNode ="+Util.fastArrToStr(bestPath));
		
		*/
		

		
		int RandomSeed = 1;

		String CustTypeFile = "cons100.xml";
		String ApplsFile = "appliances.xml";

		DRSimulation sim = new DRSimulation(ApplsFile, CustTypeFile);

		//float[] partcpRate = {0.00f, 0.025f, 0.050f, 0.075f, 0.100f, 0.125f, 0.150f, 0.175f, 0.200f, 0.225f, 0.250f};
		//float[] prDist     = {0.10f, 0.36f, 0.41f, 0.08f, 0.04f, 0.01f};

		//float[] partcpRate = {0.05f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f};
		//float[] prDist     = {1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
		
		//float[] partcpRate = {0.02f, 0.04f, 0.06f, 0.08f, 0.10f, 0.24f};
		//float[] prDist     = {0.10f, 0.36f, 0.41f, 0.08f, 0.04f, 0.01f};
		
		float[] partcpRate = {0.02f, 0.04f, 0.06f, 0.08f};
		float[] prDist     = {0.06f, 0.87f, 0.05f, 0.02f};
		
		//float[] partcpRate = {0.00f, 0.04f, 0.06f };
		//float[] prDist     = {0.0f, 0.4f, 0.6f};

		
		//float[] partcpRate = {0.0f, 0.3f, 0.5f, 0.7f, 0.9f};
		//float[] prDist     = {1.0f, 0.0f, 0.0f, 0.0f, 0.0f};

		//float[] partcpRate = {(float) 0.0, (float) 1.0};
		//float[] prDist = {(float) 0.0, (float) 1.0};

		//float[] partcpRate = {(float) 0.05};
		//float[] prDist = {(float) 1.0};

		//sim.setAppDeviation(0);
		//float[] par = sim.expRun(1, "test.txt", partcpRate, prDist, 1, 0, 200, 365, 1, 1);
		float[] par1 = sim.run(partcpRate, prDist, RandomSeed);
		//System.out.println(par1);
		//prDist[1] = 0.0f;
		//prDist[2] = 1.0f;
		//float par2 = sim.run(partcpRate, prDist);
		
		//prDist[2] = 0.0f;
		//prDist[3] = 1.0f;
		//float par3 = sim.run(partcpRate, prDist);

		//System.out.printf("End result: %.3f, %.3f, %.3f\n",par1,par2,par3);
		
		
	}
}

class DRSimulation {
	
	private int EXPNO;
	private String LOGFILENAME;
	private String CUSTTYPEFILE;
	PrintWriter pLog;
	
	private int TAKETURN;
	private int STOCHOPT;
	
	private int VERBOSE=1;
	
	private Random rnd;
	
	private int MAXTIME;
	private int NROUND;
	private int NSTOCH;
	private int DEVUSAGE;
	private float TIMEPRICECOEFF; 
	
	private int PERTURBED;
	private int NOISERANGE;
	private int EVOLUTION;
	private int NROUNDTOLEARN;
	
	private ArrayList<CustomerType> custTypeLst;
	private ArrayList<Appliance> appLst;
	
	private float[] PROBLEARNLABEL;
	private float[] PROBLEARNDIST;
	

	
	public DRSimulation(String AppliancesFile, String CustTypeFile) throws ParserConfigurationException, SAXException, IOException{
		
		EXPNO = 0;
		LOGFILENAME = "";
		CUSTTYPEFILE = CustTypeFile;
		
		TAKETURN = 0; // 0=NOTAKETURN, 1=TAKETURN 
		STOCHOPT = 0; // 0=STOCH, 1=OPT
		
		// TODO:
		// set this using XML: general-setting.xml	
		MAXTIME = 24;
		NROUND = 365;
		NSTOCH = 200;
		DEVUSAGE = 0; // additional percentage usage of app deviation (other than specified on the xml file)
		TIMEPRICECOEFF = 0.0f; // coeff for netbenefit, which is: coeff*timeU + priceU 
		
		PERTURBED = 0; // 0=without perturbation, 1=with perturbation
		NOISERANGE = 25; // set for perturbation
		
		// about Evolution
		EVOLUTION = 0;
		NROUNDTOLEARN = 20;
		
		// for stay (0), learning (the prop prob one) (1), mutate (select random strategy except the best one) (1)
		PROBLEARNLABEL = new float[3];
		PROBLEARNDIST  = new float[3];
		PROBLEARNLABEL[0] = 0; 		PROBLEARNDIST[0] = 0.00f;
		PROBLEARNLABEL[1] = 1;		PROBLEARNDIST[1] = 0.95f;
		PROBLEARNLABEL[2] = 2;		PROBLEARNDIST[2] = 0.05f;
				
		
		// TODO: put this in XML: general-setting.xml
		// read and parse customers file
		custTypeLst = readCustomersFile(CUSTTYPEFILE);

		// TODO: put this in XML: general-setting.xml
		// read and parse appliances file
		appLst = readAppliancesFile(AppliancesFile);
		
		if (VERBOSE == 1)
		System.out.println(appLst);			
		
 	}
	
	
	// run for experiment
	public float[] expRun(int ExpNo, String LogFileName, float[] partcpRate, float[] prDist, int TakeTurn, int StochOpt, int NStoch, int NRound, int Verbose, int RandomSeed) throws FileNotFoundException{
		EXPNO = ExpNo;
		LOGFILENAME = LogFileName;
		TAKETURN = TakeTurn;
		STOCHOPT = StochOpt;
		NSTOCH = NStoch;		
		NROUND = NRound;
		VERBOSE = Verbose;
		if (LogFileName.length()>0) 		
			pLog = new PrintWriter(new File(LOGFILENAME));
		return run(partcpRate,  prDist,  RandomSeed);
	}
	
	public void setAppDeviation(int Percentage){
		DEVUSAGE = Percentage;
	}
	
	public float[] run(float[] partcpRate, float[] prDist, int RandomSeed) throws FileNotFoundException{
		// if there is some exp going on
		
		if (EXPNO == 1){
			pLog.printf("// EXPNO=%d, TAKETURN=%d, STOCHOPT=%d, NSTOCH=%d, NRound=%d, RANDOMSEED=%d, DEVUSAGE=%d, PERTURBED=%d, EVOLUTION=%d, CUSTTYPEFILE=%s,  \n", EXPNO, TAKETURN, STOCHOPT, NSTOCH, NROUND, RandomSeed, DEVUSAGE, PERTURBED, EVOLUTION, CUSTTYPEFILE);			
			pLog.flush();
		}

		
		rnd = new Random(RandomSeed);
		
		ArrayList<Customer> custLst = createCustomers(partcpRate, prDist);
		
		// area under PAR = sum over all PAR
		float AUP = 0.0f;
		float initialSystemCost = 0.0f;
		float systemSaving = 0.0f;
		float systemCost = 0.0f;
		
		// now, initialize the load
		// loop over all customers
		for (int i=0; i<custLst.size(); i++){
			
			// fill up each customer load
			Customer currCust = custLst.get(i);
			
			// initialize the load
			currCust.loads = new int[MAXTIME];
			currCust.initSchedule = new ArrayList<ArrayList<Integer>>();
			currCust.prevSchedule = new ArrayList<ArrayList<Integer>>();
			currCust.currSchedule = new ArrayList<ArrayList<Integer>>();
			
			// loop over all appliances this customer has			
			for (int j=0; j<currCust.appUsages.size(); j++){
				
				// get appliances number j for customer i
				ArrayList<UsageGroup> currAppUsage = currCust.appUsages.get(j);

				// get power of the appliances
				int appIdx = currCust.appsIndex.get(j);					
				int appPower = appLst.get(appIdx).power;
				
				// loop over all usage group
				ArrayList<Integer> startsTime = new ArrayList<Integer>();
				
				for (int k=0; k<currAppUsage.size(); k++){
					UsageGroup currUsage = currAppUsage.get(k);
					//  get the starting time
					// TODO: must be fixed?
					//int timeslotPick = (int) (rnd.nextDouble() * currUsage.timeslots.size());
					int timeslotPick = 0;
					int startingTime = currUsage.timeslots.get(timeslotPick).start;
					if (startingTime<0) startingTime = 0;
					if (startingTime>=MAXTIME) startingTime = MAXTIME-1;
					startsTime.add(startingTime);
					
					// get the usage time
					float usageTime = (float)((rnd.nextDouble()*2) - 1) * currUsage.duration.devUsage + currUsage.duration.iDuration;
					
					// consume!
					Util.consumeElectricity(currCust.loads, startingTime, usageTime, appPower, MAXTIME);										
				
				}// end for all usage group
				
				currCust.initSchedule.add(startsTime);
				currCust.prevSchedule.add(startsTime);
				currCust.currSchedule.add(startsTime);
				
			}// end for all appliances

		} // end for all customer
		
		// now, get overall loads
		int[] initTotalLoads;
		int[] initTotalLoadsActual = null;
		if (PERTURBED == 0) {
			initTotalLoads = Util.getTotalLoad(custLst);			
		} else {			
			initTotalLoads = Util.getTotalLoadPerturbed(custLst, NOISERANGE, rnd);
			initTotalLoadsActual = Util.getTotalLoad(custLst);
		}

		if (VERBOSE == 1) {
			// for output purposes
			System.out.println("initTotalLoads="+Util.arrToStr(initTotalLoads));
			if (PERTURBED == 1) { 
				System.out.println("initTotalLoads="+Util.arrToStr(initTotalLoadsActual) + " (actual)" );		
			}
		}
		int[] totalLoads = new int[initTotalLoads.length];
		int[] totalLoadsActual = null;
		System.arraycopy(initTotalLoads, 0, totalLoads, 0, initTotalLoads.length);
		
		int initPeakLoad = Util.arrayMax(initTotalLoads);
		int initPeakLoadActual = 0;
		double initAvgLoad = Util.arrayAvg(initTotalLoads);
		double initAvgLoadActual = 0;
		
		if (PERTURBED == 1){
			initPeakLoadActual = Util.arrayMax(initTotalLoadsActual); 
			initAvgLoadActual = Util.arrayAvg(initTotalLoadsActual);			
		}
		
		float PAR = (float) ((initPeakLoad+0.0) / initAvgLoad);
		if (VERBOSE == 1){
			if (PERTURBED == 1){
				System.out.println("init: par="+ PAR + ", maxLoad="+ initPeakLoad + ", avgLoads=" + initAvgLoad + " (actual: " + ((initPeakLoadActual+0.0) / initAvgLoadActual) + ", " + initPeakLoadActual + ", " + initAvgLoadActual + ")");			
			} else {
				System.out.println("init: par=" + PAR + ", maxLoad="+ initPeakLoad + ", avgLoads=" + initAvgLoad);
			}
		}
		
		AUP = AUP + PAR; 
		initialSystemCost = Util.getTotalPrice(totalLoads);
		systemSaving = 0.0f;
		systemCost = initialSystemCost;
		
		if (EXPNO == 1){
			pLog.printf("Round,PAR,AUP,SystemSaving,MaxLoad,AvgLoad\n");
			pLog.printf("0,%.3f,%.3f,%.3f,%d,%.3f\n", PAR, AUP, systemSaving, initPeakLoad, initAvgLoad);
			pLog.flush();
		}
		
		
		// for output purposes: get price per Wh for all customers
		/*
		float[] pricePerWh = new float[custLst.size()];
		for (int i=0; i<custLst.size(); i++){
			pricePerWh[i] = Util.getPricePerWh(custLst.get(i).loads, totalLoads);
		}
		*/			
		// end of customers' initialization
		
		// netBenefit for evolution mode
		float[] netBenefit = new float[partcpRate.length];
		int[] countCust = new int[partcpRate.length];
		
		int MaxLoad = 0;
		float AvgLoad = 0;
		// do this games g times		
		for (int g=0;g<NROUND;g++){
			
			int[] prevTotalLoads = new int[MAXTIME];  
			System.arraycopy(totalLoads, 0, prevTotalLoads, 0, MAXTIME);		

			// learn evolution?
			if (EVOLUTION==1 && g>0 && g%NROUNDTOLEARN == 0 ){
				// get the cust count for each partcp rate group
				for (int i=0; i<partcpRate.length; i++){
					countCust[i] = 0;
					for (int j=0; j<custLst.size(); j++){				
						if (custLst.get(j).partcpRate == partcpRate[i]) {
							countCust[i]++;
						}
					}			
				}
				
				// normalize netBenefit (average per customer)
				for (int i=0; i<partcpRate.length; i++){
					if (countCust[i] == 0) {
						netBenefit[i] = 0;
						 
					} else 
						netBenefit[i] = netBenefit[i] / countCust[i];
				}
				
				int maxBenIdx = 0;
				float maxBenVal = netBenefit[0];
				for (int i=1; i<partcpRate.length; i++){
					if (netBenefit[i] > maxBenVal) {
						maxBenIdx = i;
						maxBenVal = netBenefit[i]; 
					}
				}
				
				
				// build the probLearnDist
				float totalNetBenefit = Util.arraySum(netBenefit);
				float[] probLearnDist = new float[partcpRate.length];
				for (int i=0; i<partcpRate.length;i++){
					if (totalNetBenefit==0){
						probLearnDist[i] = 0;
					} else 
					probLearnDist[i] = netBenefit[i] / totalNetBenefit;
				}
				
				if (VERBOSE == 1) {
					// print some information
					System.out.printf("netBenefit: %s\n",Util.arrToStr(netBenefit));
					System.out.printf("probDist: %s\n",Util.arrToStr(probLearnDist));
					System.out.printf("learn: the best strategy: %d, %.3f\n", maxBenIdx, maxBenVal);
				}

				// create distribution for 100 element
				int lenTab = 100;				
				
				// probLearnTab for decision level 1 (stay, learn, mutate)
				float[] probLearnTab1 = Util.createProbDistTable(PROBLEARNLABEL, PROBLEARNDIST, lenTab);
				// probLearnTab for decision level 2 (prop to stra netBenefit), if decide to learn (from decision1=1)
				float[] probLearnTab2 = Util.createProbDistTable(partcpRate, probLearnDist, lenTab);
				
				for (int i=0; i<custLst.size(); i++){
					// for each agent, do random decision based on PROBLEARNDIST
					float decision1 = probLearnTab1[(int) (rnd.nextDouble() * lenTab)];
					float decision2 = 0.0f;
					if (decision1 == 1) { // learn the best
						decision2 = probLearnTab2[(int) (rnd.nextDouble() * lenTab)];
						custLst.get(i).partcpRate = decision2;
					} else if (decision1 == 2){
						int RandPartcpRate = (int) (rnd.nextDouble() * partcpRate.length);
						//if (RandPartcpRate == maxBenIdx) RandPartcpRate ++;
						//if (RandPartcpRate >= partcpRate.length) RandPartcpRate = 0;
						custLst.get(i).partcpRate = partcpRate[RandPartcpRate];
					}
					//System.out.printf("cust #%d: [dec1: %.3f, dec2: %.3f, newPartcpRate: %.3f]\n",i,decision1, decision2, custLst.get(i).partcpRate);
				}
				
				if (VERBOSE == 1) {
					// print customer strategy distribution
					System.out.printf("Cust strategy count: %s\n",Util.arrToStr(Util.getStrategyCount(partcpRate, custLst)));
				}
				// renew netBenefit
				netBenefit = new float[netBenefit.length];				
				
			} // end of learn Evolution
			
			// TODO: this make everything so slow for the evolution mode. Optimize this!
			if (EVOLUTION==1){
				for (int i=0; i<partcpRate.length; i++){
					for (int j=0; j<custLst.size(); j++){				
						if (custLst.get(j).partcpRate == partcpRate[i]) {
							netBenefit[i] = netBenefit[i] + custLst.get(j).getCurrNetBenefit();
						}
					}
				}
			}

			
			
			
			// loop over all customer
			// i = index of each (current) customer
			for (int i=0; i<custLst.size(); i++){			
				
							
				// get the current customer
				Customer currCust = custLst.get(i);
				
				// per appliances, generate the load requirement for this iteration
				currCust.generateLoadReqs(rnd);

				
				int[] prevLoadMinus = new int[MAXTIME];
				int calculatedPrevLoadMinus = 0;
				// if rnd is under partcp rate 
				if ((rnd.nextDouble() > currCust.partcpRate) && (TAKETURN == 0) ){
					if (DEVUSAGE > 0){
						// get the prev day loads minus my own loads						
						for (int j=0; j<MAXTIME; j++){
							prevLoadMinus[j] = prevTotalLoads[j] - currCust.loads[j];
						}		
						calculatedPrevLoadMinus = 0;
						currCust.applyPrevSchedule(appLst, prevLoadMinus, MAXTIME, rnd);
					}
					continue;
				} 
				
				if (TAKETURN == 1) {	
					int custNo = g;
					if (custNo >= custLst.size()){
						custNo = custNo % custLst.size();
					}
					if (i!=custNo) {
						continue;
					}
				}
				
				// get the prev day loads minus my own loads
				
				if (calculatedPrevLoadMinus == 0){					
					for (int j=0; j<MAXTIME; j++){
						prevLoadMinus[j] = prevTotalLoads[j] - currCust.loads[j];
					}		
					
				}
				
				

				if (TAKETURN == 1 || STOCHOPT == 1){
					currCust.optBestSchedule(appLst, prevLoadMinus, MAXTIME);				
				}else 
				if (TAKETURN ==0 && STOCHOPT == 0){
					currCust.stochBestSchedule(NSTOCH, appLst, prevLoadMinus, MAXTIME, rnd);				
					
				}
				
								

				
			} // end play for all customer
			
			// now, get overall loads
			if (PERTURBED == 0) {
				totalLoads = Util.getTotalLoad(custLst);
			} else {
				totalLoads = Util.getTotalLoadPerturbed(custLst, NOISERANGE, rnd);
				totalLoadsActual = Util.getTotalLoad(custLst);
			}
			
			
			MaxLoad = Util.arrayMax(totalLoads);
			AvgLoad = (float) Util.arrayAvg(totalLoads);
			PAR = (float) ((MaxLoad +0.0) / AvgLoad);
			AUP = AUP + PAR;
			float currCost = Util.getTotalPrice(totalLoads);
			systemSaving = systemSaving + ( initialSystemCost - currCost ); 
			systemCost = systemCost + currCost;
			
			if (VERBOSE == 1){
				// for output purposes				
				if (PERTURBED == 1){
					System.out.printf("g=%3d, (reported: maxLoad=%5d, avgLoads= %.3f), (actual: par=%.3f, maxLoad=%d, avgLoads=%.3f) \n", (g+1), MaxLoad, AvgLoad, ((Util.arrayMax(totalLoadsActual)+0.0) / Util.arrayAvg(totalLoadsActual)), Util.arrayMax(totalLoadsActual), Util.arrayAvg(totalLoadsActual));
				} else {						
					System.out.printf("g=%3d, par=%.3f, aup=%.3f, maxLoad=%5d, avgLoads=%.3f\n", (g+1), PAR, AUP, MaxLoad, AvgLoad);
					//..System.out.println(Util.arrToStr(totalLoads));
				}
			}
			
			if (EXPNO == 1){
				pLog.printf("%d,%.3f,%.3f,%.3f,%.3f,%d,%.3f\n", (g+1), PAR, AUP, systemSaving, systemCost, MaxLoad, AvgLoad);
				pLog.flush();
			}

			// to calculate price per customer
			for (int i=0; i<custLst.size(); i++){
				custLst.get(i).addPriceUtility(Util.getPricePerWh(custLst.get(i).loads, totalLoads));
			}			
			
			// for output purposes: get price per Wh for all customers
			/*
			pricePerWh = new float[custLst.size()];
			for (int i=0; i<custLst.size(); i++){
				pricePerWh[i] = Util.getPricePerWh(custLst.get(i).loads, totalLoads);
				
				// utility calculation
				float timeUtility = custLst.get(i).calculateTimeUtility();
				float priceUtility = - Util.getPricePerWh(custLst.get(i).loads, totalLoads);
				custLst.get(i).addPriceUtility(priceUtility);
				custLst.get(i).addTimeUtility(timeUtility);
				
				// renew prevSchedule
				Collections.copy(custLst.get(i).prevSchedule, custLst.get(i).currSchedule);

			}			
			*/
			
		} // end for g=game
		
		if (VERBOSE == 1){
			// print count of strategy
			if (EVOLUTION == 1){
				System.out.printf("Cust strategy count: %s\n", Util.arrToStr(Util.getStrategyCount(partcpRate, custLst)));
			}
			System.out.println("Total Customers=" + custLst.size());
			if (PERTURBED == 0) {
				System.out.println("init: maxLoad=" + initPeakLoad + ", avgLoads=" + initAvgLoad);
				System.out.println(Util.arrToStr(initTotalLoads));
				System.out.println("end: maxLoad=" + Util.arrayMax(totalLoads)+ ", avgLoads="+ Util.arrayAvg(totalLoads));
				System.out.println(Util.arrToStr(totalLoads));
			} else {
				System.out.println("init: maxLoadActual=" + initPeakLoadActual + ", avgLoadsActual=" + initAvgLoadActual);
				System.out.println(Util.arrToStr(initTotalLoadsActual));
				System.out.println("end: maxLoadActual=" + Util.arrayMax(totalLoadsActual)+ ", avgLoadsActual="+ Util.arrayAvg(totalLoadsActual));
				System.out.println(Util.arrToStr(totalLoadsActual));				
			}
			// presents the utility summary
			System.out.printf("Utility summary: %s \n", Util.accUtilitySummary(partcpRate, custLst));
			
			// print price per customer
			for (int i=0; i<custLst.size(); i++){
				System.out.printf("cust %d: partcpRate: %.3f, avgPricePerWh: %.4f\n", i, custLst.get(i).partcpRate, custLst.get(i).accPriceUtility/NROUND);
			}

		}
		if (EXPNO == 1){
			pLog.printf("// print avg price per kwh for each customer (from 1 to %d)\n", custLst.size());
			pLog.printf("custId,partcpRate,avgPricePerKwh\n");
			for (int i=0; i<custLst.size(); i++){
				pLog.printf("%d,%.3f,%.4f\n", i, custLst.get(i).partcpRate, custLst.get(i).accPriceUtility/NROUND);
			}
			pLog.close();
		}				
		float[] results = {PAR, AUP, systemSaving, systemCost};
		if (VERBOSE == 1) System.out.printf("PAR: %.3f, AUP: %.3f, systemSaving: %.3f, systemCost: %.3f", PAR, AUP, systemSaving, systemCost);		
		return results; 
	}
	
	
	// TODO: make partcpRate, pRdist, DEVUSAGE, and TIMEPRICECOEFF tidy!
	private ArrayList<Customer> createCustomers(float[] partcpRate, float[] prDist){
		
		// create cust participation ratio
		int LPARTCPDIST = 100;
		float[] partcpRateDist = new float[LPARTCPDIST];
		int st = 0;
		for (int i=0; i<prDist.length-1; i++){
			// how many element shall we set now 
			int lNow = (int) (prDist[i] * LPARTCPDIST);
			for (int j=st; j<st+lNow; j++){
				partcpRateDist[j] = partcpRate[i];
			}
			st = st+lNow;
		}
		for (int j=st; j<LPARTCPDIST; j++){
			partcpRateDist[j] = partcpRate[partcpRate.length-1];
		}
		
		String userPartcpRateDist = Util.arrToStr(partcpRateDist);
		if (VERBOSE == 1)
			System.out.println("User partcp rate = " + userPartcpRateDist);
		if (EXPNO == 1) 
			pLog.printf("// User partcp rate dist = %s\n", userPartcpRateDist);
	
		
		// create data structure for customers
		ArrayList<Customer> custLst = new ArrayList<Customer>();
		
		// creating customers
		// loop over all customer type
		for (int i=0; i<custTypeLst.size(); i++){
			// TODO create accessor for this .type
			// get the customer type
			String type = custTypeLst.get(i).type;
			
			// how many customer we should create
			int count = custTypeLst.get(i).count;
			
			// get the list of apps
			ArrayList<ApplianceUsage> appUsage = custTypeLst.get(i).appliances;
			ArrayList<Integer> apps = new ArrayList<Integer>();
			for (int j=0; j<appUsage.size(); j++){
				String appId = appUsage.get(j).id; 
				// search for id in appLst
				int idx = 0;
				while (!appLst.get(idx).id.equalsIgnoreCase(appId)) {
					idx ++;
				}
				if ( appLst.get(idx).id.equalsIgnoreCase(appId) ) {
					apps.add(idx);
				}
			}
			
			// create customer for this type one by one
			for (int j=0; j<count; j++){
				// assign the type and participation rate				
				Customer cust = new Customer(type, partcpRateDist[(int) (rnd.nextDouble() * LPARTCPDIST)], TIMEPRICECOEFF);
				// define the list of apps owned by this customer
				cust.addApps(apps);		
				// now set the time for each apps
				cust.addAppUsages(custTypeLst.get(i).appliances, rnd, DEVUSAGE);
				// add to the customer list
				custLst.add(cust);								
			}
			
		} // end for all customer type
		
		// end of creating customers 
		// all customers data is now stored in custLst
		return custLst;

	}
	
	private ArrayList<Appliance> readAppliancesFile(String filename) throws ParserConfigurationException, SAXException, IOException {

		// assign some string constants
		String appString = "appliance";
		String idString = "id";
		String nameString = "name";
		String powerString = "power";
		
		// create the data structure
		ArrayList<Appliance> apps = new ArrayList<Appliance>();

		// open and read the file
		File file = new File(filename);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);
		doc.getDocumentElement().normalize();			
		NodeList nodeLst = doc.getElementsByTagName(appString);		
		
		for (int i = 0; i < nodeLst.getLength(); i++) {
			Element appElmnt = (Element) nodeLst.item(i);
			String idValue = appElmnt.getAttribute(idString);
			
			NodeList nameElmntLst = appElmnt.getElementsByTagName(nameString);
			Element nameElmnt = (Element) nameElmntLst.item(0);
			String nameValue = nameElmnt.getFirstChild().getNodeValue();
			
			NodeList powerElmntLst = appElmnt.getElementsByTagName(powerString);
			Element powerElmnt = (Element) powerElmntLst.item(0);
			String powerValue = powerElmnt.getFirstChild().getNodeValue();
			
			Appliance app = new Appliance(idValue, nameValue, Integer.parseInt(powerValue));
			apps.add(app);
		}
		
		return apps;
		
	}
	
	private ArrayList<CustomerType> readCustomersFile(String filename) throws ParserConfigurationException, SAXException, IOException {
		String typeString = "type";
		String countString = "count";
		String appsString = "appliances";
		String appString = "appliance";
		String appidString = "appid";
		String usageString = "usagegroup";
		String timeslotString = "timeslot";
		String startString = "start";
		String endString = "end";
		String devString = "dev";
		String devUsageString = "devUsage";
		String durationString = "duration";
		
		// create the data structure for the customer types
		ArrayList<CustomerType> custTypeLst = new ArrayList<CustomerType>();
				
		File file = new File(filename);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);
		doc.getDocumentElement().normalize();
		NodeList nodeLst = doc.getElementsByTagName("customer");
	 		
		for (int i = 0; i < nodeLst.getLength(); i++) {
			Node custNode = nodeLst.item(i);
			if (custNode.getNodeType() == Node.ELEMENT_NODE) {
					
				Element custElmnt = (Element) custNode;	
				
				// get 'type'
				NodeList typeElmntLst = custElmnt.getElementsByTagName(typeString);
				if (typeElmntLst.getLength()==0 ){
					System.err.println("[main::readCustomersFile] Error: Element '"+ typeString +"' not found in customer #" + (i+1));
					System.exit(1);
				}
				Element typeElmnt = (Element) typeElmntLst.item(0);
				String typeValue = typeElmnt.getFirstChild().getNodeValue();								
				
				// get 'count'
				NodeList countElmntLst = custElmnt.getElementsByTagName(countString);
				if (countElmntLst.getLength()==0 ){
					System.err.println("[main::readCustomersFile] Error: Element '"+ countString +"' not found in customer #" + (i+1));
					System.exit(1);
				}
				Element countElmnt = (Element) countElmntLst.item(0);
				String countValue = countElmnt.getFirstChild().getNodeValue();						
				
				// create CustomerType data structure
				CustomerType custType = new CustomerType(typeValue, Integer.parseInt(countValue));

				// get 'appliances'
				NodeList appsElmntLst = custElmnt.getElementsByTagName(appsString);
				if (countElmntLst.getLength()==0 ){
					System.err.println("[main::readCustomersFile] Error: Element '" + appsString + "' not found in customer #" + (i+1));
					System.exit(1);
				}
				Element appsElmnt = (Element) appsElmntLst.item(0);
				
				//  get all 'appliance'
				NodeList appElmntLst = appsElmnt.getElementsByTagName(appString);					
				for (int j=0;j<appElmntLst.getLength();j++ ){
					Element appElmnt = (Element) appElmntLst.item(j);
					
					// get 'appid'
					NodeList appidElmntLst = appElmnt.getElementsByTagName(appidString);
					Element appidElmnt = (Element) appidElmntLst.item(0);
					String appid = appidElmnt.getFirstChild().getNodeValue();
					
					// create data structure for each appliance
					ApplianceUsage appliance = new ApplianceUsage(appid);
					
					// get all 'usage'
					NodeList usageElmntLst = appElmnt.getElementsByTagName(usageString);
					for (int k=0; k<usageElmntLst.getLength(); k++){
						Element usageElmnt = (Element) usageElmntLst.item(k);
						
						// create a data structure for a UsageGroup
						UsageGroup usageGroup = new UsageGroup();
						
						// get 'timeslot'
						NodeList timeslotElmntLst = usageElmnt.getElementsByTagName(timeslotString);
						for (int m=0; m<timeslotElmntLst.getLength(); m++){								
							Element timeslotElmnt = (Element) timeslotElmntLst.item(m);
							String startValue = timeslotElmnt.getAttribute(startString);
							String endValue = timeslotElmnt.getAttribute(endString);
							String devValue = timeslotElmnt.getAttribute(devString);
							
							usageGroup.addTimeslot(startValue, endValue, devValue);
						}
						
						// get 'duration'
						NodeList durationElmntLst = usageElmnt.getElementsByTagName(durationString);
						Element durationElmnt = (Element) durationElmntLst.item(0);
						String durationValue = durationElmnt.getFirstChild().getNodeValue();
						String devValue = durationElmnt.getAttribute(devString);
						String devUsageValue = durationElmnt.getAttribute(devUsageString);
						usageGroup.setDuration(devValue, devUsageValue, durationValue);
						appliance.addUsageGroup(usageGroup);
						
					} // end for usageGroup iteration
					
					custType.addAppliance(appliance);
					
				} // end for appliance iteration
				
				custTypeLst.add(custType);
				
			} // end if
			
		} // end for iterate over all customer Type
		
		return custTypeLst;
			
	}
}


