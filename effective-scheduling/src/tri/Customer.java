package tri;

import java.util.ArrayList;
import java.util.Random;

//working data structure for each customer
//used for day-to-day simulation
public class Customer{
	
	// customer type
	String type;
	
	// list of appliance owned, based on index on appsLst 
	ArrayList<Integer> appsIndex;
	
	// list of usageGroup of each appliances correspond to apps index
	// the difference with timeslots in customerType is that
	// in here, we processed 'dev' and fixed the 'start' and 'end'
	ArrayList<ArrayList<UsageGroup>> appUsages;
	
	// to store initial schedule
	ArrayList<ArrayList<Integer>> initSchedule;

	// to store previous day schedule
	ArrayList<ArrayList<Integer>> prevSchedule;

	// to store best schedule so far
	ArrayList<ArrayList<Integer>> currSchedule;
	
	// total loads (wh) per hour 
	// this is int[24]
	ArrayList<ArrayList<Float>> loadReqs;

	int[] loads;
	
	float partcpRate;
	
	float accTimeUtility;
	float currTimeUtility;
	
	float accPriceUtility;
	float currPriceUtility;
	
	float timePriceCoeff;
	
	public Customer(String Type, float PartcpRate, float TimePriceCoeff){
		type = Type;
		currSchedule = new ArrayList<ArrayList<Integer>>();
		partcpRate = PartcpRate;
		accTimeUtility = 0;		
		currTimeUtility = 0;		
		accPriceUtility = 0;
		currPriceUtility = 0;
		timePriceCoeff = TimePriceCoeff;
	}	
	
	public void addTimeUtility(float TimeUtility){
		//System.out.printf("TimeU=%.3f\n", TimeUtility);
		currTimeUtility = TimeUtility;
		accTimeUtility = accTimeUtility + currTimeUtility;
	}
	
	public void addPriceUtility(float PriceUtility){
		//System.out.printf("PriceU=%.3f\n", PriceUtility);
		currPriceUtility = PriceUtility;
		accPriceUtility = accPriceUtility + currPriceUtility;
	}

	public void addApps(ArrayList<Integer> Apps){
		appsIndex = new ArrayList<Integer>();
		for (int i=0; i<Apps.size(); i++){
			appsIndex.add(Apps.get(i));
		}		
	}
	
	public float getCurrNetBenefit(){
		return timePriceCoeff*currTimeUtility + currPriceUtility;
	}
	
	// this should be the same order as in time
	public void addAppUsages(ArrayList<ApplianceUsage> appliances, Random rnd, int DEVUSAGE) {
		
		appUsages = new ArrayList<ArrayList<UsageGroup>>();		
		// loop for all appliances
		for (int i=0; i<appliances.size(); i++){
			
			// loop for all timeslots available
			ArrayList<UsageGroup> newUsages = new ArrayList<UsageGroup>();
			
			for (int j=0; j<appliances.get(i).usages.size(); j++){
				
				UsageGroup newUsage = new UsageGroup();
				UsageGroup Usage = appliances.get(i).usages.get(j);
				
				// fix the timeslot
				ArrayList<Timeslot> Timeslots = Usage.timeslots;
				ArrayList<Timeslot> newTimeslots = new ArrayList<Timeslot>(); 
				for (int k=0; k<Timeslots.size(); k++){					
					int start = (int) (Timeslots.get(k).start + ((rnd.nextDouble()*2-1)*Timeslots.get(k).dev));
					int end = (int) (Timeslots.get(k).end + (rnd.nextDouble()*Timeslots.get(k).dev));
					Timeslot t = new Timeslot(start, end);
					newTimeslots.add(t);
				}				
				newUsage.timeslots = newTimeslots;
				
				// TODO NOW fix the duration
				Duration newDuration = new Duration();
				//  add deviation from usage-duration
				
				float devAdd = (float) (Usage.duration.iDuration * ((DEVUSAGE+0.0)/100.0));				
				newDuration.devUsage = (float) Usage.duration.devUsage + devAdd;
				//..System.out.println(newDuration.devUsage);
				// random between -dev and +dev
				float dev = (float) (rnd.nextDouble() * Usage.duration.dev * 2);				
				dev = dev - Usage.duration.dev;
				newDuration.iDuration = Usage.duration.iDuration + dev;

				// set the duration
				newUsage.duration = newDuration;
				
				// add to the list
				newUsages.add(newUsage);
			}
			
			appUsages.add(newUsages);
		}
	}
	
	public float calculateTimeUtility(){
		float absCost = 0; 
		float changeCost = 0;
		for (int i=0; i<initSchedule.size(); i++){
			for (int j=0; j<initSchedule.get(i).size(); j++){
				absCost = absCost + Math.abs(initSchedule.get(i).get(j) - currSchedule.get(i).get(j) );
				//changeCost = (float) (changeCost + Math.pow(Math.abs(prevSchedule.get(i).get(j) - currSchedule.get(i).get(j)),2) );
			}
		}
		//return -absCost - changeCostCoeff*changeCost;
		//return  - changeCost;
		return  1/(absCost+1);
		
	}

	
	public String toString(){
		return "Customer type=" + type + ", appsIndex=" + appsIndex + ", appUsage=" + appUsages; 
	}
	
	
	public void stochBestSchedule(int NSTOCH, ArrayList<Appliance> appLst, int[] prevLoadMinus, int MAXTIME, Random rnd){
		// first try previous schedule
		int[] currWh = new int[MAXTIME];
		// j = index of appliances
		for (int j=0; j<appUsages.size(); j++){
			
			// get power of the appliances
			int appIdx = appsIndex.get(j);					
			int appPower = appLst.get(appIdx).power;
			
			ArrayList<UsageGroup> currApp = appUsages.get(j);
			// k = index of usage group
			for (int k=0; k<currApp.size();k++){
				Util.consumeElectricity(currWh, currSchedule.get(j).get(k), loadReqs.get(j).get(k), appPower, MAXTIME);
			}			
		}

		// put the minCost as this initial price
		float minCost=Util.getPriceFromLoadMinus(currWh, prevLoadMinus);
		// put the minLoads as this initial loads
		int[] minLoads = new int[MAXTIME];
		ArrayList<ArrayList<Integer>> minSched = currSchedule; 
		System.arraycopy(currWh, 0, minLoads, 0, MAXTIME);
		// end of calculation using prev schedule
				
		
		// begin stochastic process several times (let's try: 50 times)
		for (int r=0; r<NSTOCH; r++){
			currWh = new int[MAXTIME];
			ArrayList<ArrayList<Integer>> schedule = new ArrayList<ArrayList<Integer>>();
			
			// j = index of appliances
			for (int j=0; j<appUsages.size(); j++){

				// get power of the appliances
				int appIdx = appsIndex.get(j);					
				int appPower = appLst.get(appIdx).power;

				ArrayList<UsageGroup> currApp = appUsages.get(j);
				ArrayList<Integer> startsTime = new ArrayList<Integer>();
				
				// k = index of usage group
				for (int k=0; k<currApp.size();k++){
					// get the starting time
					// for better time slot picks 
					ArrayList<Integer> accTimeSlot = new ArrayList<Integer>();
					for (int t=0; t<currApp.get(k).timeslots.size();t++){						
						for (int s=currApp.get(k).timeslots.get(t).start; s<=currApp.get(k).timeslots.get(t).end;s++)
						accTimeSlot.add(t);
					}
					int timeslotPick = accTimeSlot.get( (int) (rnd.nextDouble() * accTimeSlot.size()) );
					//int timeslotPick = (int) (rnd.nextDouble() * currApp.get(k).timeslots.size());
					int startingTime = currApp.get(k).timeslots.get(timeslotPick).start;
					startingTime = startingTime + (int) (rnd.nextDouble() * (currApp.get(k).timeslots.get(timeslotPick).end - currApp.get(k).timeslots.get(timeslotPick).start));
					if (startingTime<0) startingTime = 0;
					if (startingTime>=MAXTIME) startingTime = MAXTIME - 1;
					startsTime.add(startingTime);
					
					// consume the electricity
					Util.consumeElectricity(currWh, startingTime, loadReqs.get(j).get(k), appPower, MAXTIME);
					
				}
				schedule.add(startsTime);
			}
								
			// calculate price
			float price = Util.getPriceFromLoadMinus(currWh, prevLoadMinus);
			
			// see if we get lower price
			if (price < minCost ) {
				minCost = price;												
				System.arraycopy(currWh, 0, minLoads, 0, MAXTIME);
				minSched = schedule;
			}										
		}						

		// change with probability
		// if (rnd.nextDouble() < NIMPERFECT) {
		//if (rnd.nextDouble() < currCust.partcpRate) {
		System.arraycopy(minLoads, 0, loads , 0, MAXTIME);
		currSchedule = minSched;
		//} 

		// end of customer calculating schedule
	}
	
	public void applyPrevSchedule(ArrayList<Appliance> appLst, int[] prevLoadMinus, int MAXTIME, Random rnd){

		// previous schedule
		int[] currWh = new int[MAXTIME];
		// j = index of appliances
		for (int j=0; j<appUsages.size(); j++){
			
			// get power of the appliances
			int appIdx = appsIndex.get(j);					
			int appPower = appLst.get(appIdx).power;
			
			ArrayList<UsageGroup> currApp = appUsages.get(j);
			// k = index of usage group
			for (int k=0; k<currApp.size();k++){
				Util.consumeElectricity(currWh, currSchedule.get(j).get(k), loadReqs.get(j).get(k), appPower, MAXTIME);
			}			
		}

		System.arraycopy(currWh, 0, loads , 0, MAXTIME);
		// end of calculation using prev schedule
	}

	public void generateLoadReqs(Random rnd){
		// per appliances, generate the load requirement for this iteration
		loadReqs = new ArrayList<ArrayList<Float>>();						
		for (int j=0; j<appUsages.size(); j++){
			// j = index of appliances
			ArrayList<UsageGroup> currApp = appUsages.get(j);
			ArrayList<Float> appReq = new ArrayList<Float>(); 
			for (int k=0; k<currApp.size();k++){
				// k = index of usage group
				float usageTime = (float)((rnd.nextDouble()*2) - 1) * currApp.get(k).duration.devUsage + currApp.get(k).duration.iDuration;
				appReq.add(usageTime);
			}
			loadReqs.add(appReq);
		}
		
	}
	
	private void recurseUsageGroup(ArrayList<Appliance> appLst, int[] prevLoadMinus, int appNo, int usageGroupNo, int[] CurrWh, int MAXTIME, float[] minCost, int[] minLoads){
		
		if (appNo<appUsages.size()){
			// put some load here
			int[] currWh = new int[CurrWh.length];
			// copy the currWh
			
			
			ArrayList<Timeslot> timeslots = appUsages.get(appNo).get(usageGroupNo).timeslots; 
			for (int i=0; i<timeslots.size(); i++){
				int starts = timeslots.get(i).start;
				int ends = timeslots.get(i).end;
				for (int j=starts; j<=ends; j++){
					//..System.out.printf("appNo: %d, usageGroup: %d, timeslot: %d, hour: %d \n",appNo,usageGroupNo,i,j);
					int appIdx = appsIndex.get(appNo);
					int appPower = appLst.get(appIdx).power;
					// always return to original CurrWh
					System.arraycopy(CurrWh, 0, currWh, 0, CurrWh.length);
					int startingTime = j;
					if (startingTime>=MAXTIME) startingTime = startingTime - MAXTIME;  
					Util.consumeElectricity(currWh, startingTime, loadReqs.get(appNo).get(usageGroupNo), appPower, MAXTIME);					
					// we still have next usage group? call
					if (usageGroupNo < appUsages.get(appNo).size()-1){
						recurseUsageGroup(appLst, prevLoadMinus, appNo, usageGroupNo+1, currWh, MAXTIME, minCost, minLoads);
					} else 
					// we are the last usage group. Then, call another appliances
					if (appNo < appUsages.size()-1){						
						recurseUsageGroup(appLst, prevLoadMinus, appNo+1, 0, currWh, MAXTIME, minCost, minLoads);
					} else
					// we are the last usage group, the last user
					{
						// calculate the price
						//System.out.println("currWh: "+Util.fastArrToStr(currWh));
						//System.out.println("prevLoadMinus: "+Util.fastArrToStr(prevLoadMinus));
						
						float price = Util.getPriceFromLoadMinus(currWh, prevLoadMinus);
						//..System.out.printf("minCost: %.3f, price: %.3f \n", minCost[0], price);
						// see if we get lower price
						if (price < minCost[0] ) {
							minCost[0] = price;												
							System.arraycopy(currWh, 0, minLoads, 0, MAXTIME);
						}
					}
				}
			}
			
		}
		
		
			
	}
	
	
	public void optBestSchedule(ArrayList<Appliance> appLst, int[] prevLoadMinus, int MAXTIME){
		
		float[] minCost = {100000};
		int[] minLoads = new int[MAXTIME];
		int[] currWh = new int[MAXTIME];
		
		recurseUsageGroup(appLst, prevLoadMinus, 0, 0,currWh, MAXTIME, minCost, minLoads);

		System.arraycopy(minLoads, 0, loads , 0, MAXTIME);
		
		// end of customer calculating schedule
	}

	
	
}





