package metrics;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import simulator.DynamicHelper;
import simulator.Simulator;
import models.CustomRequest;
import utilities.RequestsUtil;
import utilities.NetworkTopology;

public class TransponderMetric{
	
	private static enum BANDWIDTH_DISTRIBUTION { RANDOM, GAUSSIAN, UNIFORM };
	private static enum EMBEDDING_METHOD { BACKUP, WO_BACKUP }; 
	
	private final String PROJECT_DIRECTORY =  new File(".").getCanonicalPath();
	private final String RESULTS_DIRECTORY = "/src/results/";
	
	public TransponderMetric()throws IOException{}
	
	//take in topology type, bandwidth distribution type, routing type, threshold, custom request file path
	public void start(BANDWIDTH_DISTRIBUTION distribution, String routingType, String dynSimConfirm, String dynSimMaxTime) throws IOException{
		//getResultsSimple(BANDWIDTH_DISTRIBUTION.RANDOM,EMBEDDING_METHOD.BACKUP);

		//getResultsNSFNET(BANDWIDTH_DISTRIBUTION.RANDOM,EMBEDDING_METHOD.BACKUP);
	//	getResults(BANDWIDTH_DISTRIBUTION.RANDOM,EMBEDDING_METHOD.BACKUP,NetworkTopology.RING8,30,false);

	//	getResults(BANDWIDTH_DISTRIBUTION.RANDOM,EMBEDDING_METHOD.BACKUP,NetworkTopology.HYPERCUBE8,40);
	//	getResults(BANDWIDTH_DISTRIBUTION.RANDOM,EMBEDDING_METHOD.BACKUP,NetworkTopology.MESH8,50);
	//	getResults(BANDWIDTH_DISTRIBUTION.RANDOM,EMBEDDING_METHOD.BACKUP,NetworkTopology.HYPERCUBE16,60);
	//	getResults(BANDWIDTH_DISTRIBUTION.RANDOM,EMBEDDING_METHOD.BACKUP,NetworkTopology.HYPERCUBE16,70);
	//	getResults(BANDWIDTH_DISTRIBUTION.RANDOM,EMBEDDING_METHOD.BACKUP,NetworkTopology.HYPERCUBE16,80);
	//	getResults(BANDWIDTH_DISTRIBUTION.RANDOM,EMBEDDING_METHOD.BACKUP,NetworkTopology.HYPERCUBE16,90);
	//	getResults(BANDWIDTH_DISTRIBUTION.RANDOM,EMBEDDING_METHOD.BACKUP,NetworkTopology.HYPERCUBE16,110);
		//getResultsHypercube16(BANDWIDTH_DISTRIBUTION.RANDOM,EMBEDDING_METHOD.BACKUP);		
	//	getResults(BANDWIDTH_DISTRIBUTION.RANDOM,EMBEDDING_METHOD.BACKUP, topology, threshold, false);
	//	 getResultsTestTwo(BANDWIDTH_DISTRIBUTION distributionType, EMBEDDING_METHOD method, NetworkTopology topology, String routingType, int maxBandwidth, int transponderCapacity, String dynSimConfirm, String dynSimMaxTime, int hybridThreshold, boolean customRequest)
		getResults(distribution, EMBEDDING_METHOD.BACKUP, NetworkTopology.MESH8, routingType, 200, 100, dynSimConfirm, dynSimMaxTime, 50, false);
	//	simulator.getTranspondersODU(100,i, distributionType.name().toLowerCase(), 0, (method.equals(EMBEDDING_METHOD.WO_BACKUP))?false:true, requests);

	}
	

	public static void getResults(BANDWIDTH_DISTRIBUTION distributionType, EMBEDDING_METHOD method, NetworkTopology topology, String routingType, int maxBandwidth, int transponderCapacity, String dynSimConfirm, String dynSimMaxTime, int hybridThreshold, boolean customRequest) throws IOException{
		String PROJECT_DIRECTORY =  new File(".").getCanonicalPath();
		String RESULTS_DIRECTORY = "/src/results/";
		String fileName = "_transponder_new_" + distributionType.name().toLowerCase() + "_" + routingType.toLowerCase() + "_";
		File file = new File(PROJECT_DIRECTORY + RESULTS_DIRECTORY + hybridThreshold + fileName + distributionType.name().toLowerCase() + "_" + method.name().toLowerCase() + ".csv");
		File fileUtil = new File(PROJECT_DIRECTORY + RESULTS_DIRECTORY + hybridThreshold + fileName + distributionType.name().toLowerCase() + "_" + method.name().toLowerCase() + "_utilization" + ".csv");
		File fileDropSecond = new File(PROJECT_DIRECTORY + RESULTS_DIRECTORY + hybridThreshold + fileName + distributionType.name().toLowerCase() + "_" + method.name().toLowerCase() + "_dropSecond" + ".csv");
		PrintWriter pw = new PrintWriter(file);
		PrintWriter pwUtil = new PrintWriter(fileUtil);
		PrintWriter pwDropSecond = new PrintWriter(fileDropSecond);
		
		pw.println("Max_Bandwidth,Transponders,Bandwidth,Hops,Drop,Drop% ");
		
		ArrayList<CustomRequest> requests = new ArrayList<CustomRequest>();
		if(customRequest) {
			Scanner sc = new Scanner(System.in);
			System.out.println("Enter location of file.");
			requests = RequestsUtil.createCustomRequests(sc.nextLine());
			sc.close();
		}
		
		//default 500
		for(int i = 100; i <= maxBandwidth; i+=20){
			System.out.println("Starting Transponder Metric with max bandwidth: " + i);

			pwUtil.println(i);
			pwUtil.println("Second, Utilization ");
			
			pwDropSecond.println(i);
			pwDropSecond.println("Second, DropRate ");
			
			int sum = 0;
			int sum1 = 0;
			int sum2 = 0;
			int sum3 = 0;
			int sum4 = 0;
			
			/*for(int j = 0; j < 1000; j++){
				Simulator simulator = new Simulator(topology,Integer.MAX_VALUE, 100000);
				simulator.setMaxNodes(0);// setting requests with only two nodes.
				simulator.setNumberOfRequest(5); //originally 500
				simulator.generateRequests();
				
				ArrayList<Integer> results = getTranspondersByType(routingType, simulator, transponderCapacity, i, distributionType.name().toLowerCase(), (method.equals(EMBEDDING_METHOD.WO_BACKUP))?false:true, requests, hybridThreshold);
				
				//tranponders, bandwidth, hops
				sum += results.get(0);
				sum1 += results.get(1);
				sum2 += results.get(2);
				
			}				
			pw.println(i + "," + sum/1000 + "," + sum1/1000 + "," + sum2/1000);*/
			int maxLinkCapacity = 2000; //2000
			Simulator simulator = new Simulator(topology,Integer.MAX_VALUE, maxLinkCapacity);
		//	int maxTime = 10;
		//	DynamicHelper.utilInit(maxTime);
		//	simulator.setMaxTime(maxTime);
			int maxTime = dynSimConfirm.equals("yes") ? Integer.parseInt(dynSimMaxTime) : -1;
			if(maxTime != -1) {
				DynamicHelper.utilInit(maxTime);
				simulator.setMaxTime(maxTime);
			}
			int requestsCount = 20000;  //20000
			simulator.setMaxNodes(0);// setting requests with only two nodes.
			simulator.setNumberOfRequest(requestsCount); //originally 500
			
			simulator.generateRequests();
			
			ArrayList<Integer> results = getTranspondersByType(routingType, simulator, transponderCapacity, i, distributionType.name().toLowerCase(), (method.equals(EMBEDDING_METHOD.WO_BACKUP))?false:true, requests, hybridThreshold);
			
			//tranponders, bandwidth, hops, dropped
			sum += results.get(0);
			sum1 += results.get(1);
			sum2 += results.get(2);
			sum3 += results.get(3);
			sum4 += results.get(4);
			
			ArrayList<Integer> utilization = DynamicHelper.getUtilization();
			ArrayList<Integer> dropsPSecond = DynamicHelper.getDropSecond();
			
			pw.println(i + "," + sum + "," + sum1 + "," + sum2 + "," + sum3 + "," + sum4);
			
			for(int x = 0; x < utilization.size(); x++) {
		//		System.out.println(utilization.get(x).intValue() + " " + Simulator.getTopology().getLinks().size());
				
				Double utilAverage = (double) utilization.get(x).intValue() / (Simulator.getTopology().getLinks().size() * maxLinkCapacity) * 100; 
				Double dropCalc = (double) dropsPSecond.get(x).intValue() / (requestsCount * 2) * 100;
		//		Double utilPercent =  (double) utilAverage / maxLinkCapacity * 100;
				pwUtil.println(x + "," + utilAverage);
				pwDropSecond.println(x + "," + dropCalc);
			}
		}
		
		pw.close();
		pwUtil.close();
		pwDropSecond.close();
		System.out.println("********************** done *******************");
	}
	
	public static ArrayList<Integer> getTranspondersByType(String routingType, Simulator simulator, int transponderCapacity, int maxBandwidth, String distribution,
			boolean backupPath, ArrayList<CustomRequest> customRequest, int hybridThreshold) {
		ArrayList<Integer> results = new ArrayList<Integer>();
		switch(routingType) {
		case "SPF":
			results = simulator.getTranspondersODU(transponderCapacity, maxBandwidth, distribution, 0, backupPath, customRequest);
			break;
		case "LUF":
			results = simulator.getTranspondersODU(transponderCapacity, maxBandwidth, distribution, 1, backupPath, customRequest);
			break;
		case "MUF":
			results = simulator.getTranspondersODU(transponderCapacity, maxBandwidth, distribution, 2, backupPath, customRequest);
			break;
		case "OPT":
			results = simulator.getTransponderOPT(transponderCapacity, maxBandwidth, distribution, backupPath);
			break;
		case "Hybrid":
			results = simulator.getTranspondersHybrid(transponderCapacity, maxBandwidth, distribution, backupPath, hybridThreshold);
			break;
		case "MUX":
			results = simulator.getTransponderMUX(transponderCapacity, maxBandwidth, distribution, backupPath);
			break;
		default:
			break;
		}
		return results;
	}
	
	public static void main(String args[]) throws IOException {
		try {
			new TransponderMetric().start(BANDWIDTH_DISTRIBUTION.valueOf(args[0]), args[1], args[2], args[3]);
		} catch (Exception e){
			System.out.println(e);
		}
		
	//	new TransponderMetric().start(BANDWIDTH_DISTRIBUTION.RANDOM, "SPF", "yes", "1000");
	}
}
