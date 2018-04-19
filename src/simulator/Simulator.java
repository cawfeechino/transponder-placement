package simulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import models.CustomRequest;
import models.Link;
import models.Path;
import models.Pair;
import models.PathNode;
import models.PhysicalLink;
import models.PhysicalNode;
import models.Topology;
import models.VirtualLink;
import models.VirtualNode;
import models.VirtualRequest;
import utilities.DijkstraShortestPath;
import utilities.KShortestPath;
import utilities.NetworkTopology;
import utilities.TopologyUtil;

public class Simulator {

	private int numberOfRequests = 100; // Default value for number of virtual request generated by simulator
	private int maxNodes = 2; // Default value for max number of virtual nodes per virtual request
	
	//topology is originally not static
	private Topology topologyT;
	private static Topology topology; // Topology generated by adjacency matrix

	private List<VirtualRequest> requests; // List of virtual requests
	private int maxTime = -1;
	
	private static int maxBandwidth = 0;

	/**
	 * Constructor reads adjacency matrix for specified topology type
	 * 
	 * @param type
	 *            - Type of topology being used
	 * @throws IOException
	 */
	public Simulator(NetworkTopology type, int computationalAvailability, int bandwidthAvailability)
			throws IOException {
		topologyT = TopologyUtil.readAdjacencyMatrix(type, computationalAvailability, bandwidthAvailability);
		topology = topologyT;
		maxBandwidth = bandwidthAvailability;
	}

	// --------------------------------------- ACCESSORS
	// -------------------------------------- //

	/**
	 * Gets the number of virtual requests the simulator is set to generate
	 * 
	 * @return number of virtual request the simulator will generate
	 */
	public int getNumberOfRequests() {
		return numberOfRequests;
	}
	
	public int getMaxTime() {
		return maxTime;
	}
	
	public static int getMaxBandwidth() {
		return maxBandwidth;
	}

	/**
	 * Gets the max number of nodes allowed for each virtual requests generated by
	 * the simulator
	 * 
	 * @return max number of virtual nodes allowed for each virtual request
	 */
	public int getMaxNodes() {
		return maxNodes;
	}

	/**
	 * Gets the topology object generated by simulator
	 * 
	 * @return topology object
	 */
	public static Topology getTopology() {
		return topology;
	}

	// returns the total bandwidth used for 1 request (number of hops X traffic)
	public int getTotalRequestBandwidth(Path path, int traffic) {
		return path.getNumberOfHops() * traffic;
	}

	// ---------------------------------------- MUTATORS
	// ---------------------------------------- //

	/**
	 * Sets the number of virtual requests the simulator must create
	 * 
	 * @param numberOfRequests
	 */
	public void setNumberOfRequest(int numberOfRequests) {
		this.numberOfRequests = numberOfRequests;
	}

	public void setMaxTime(int maxTime) {
		this.maxTime = maxTime;
	}
	/**
	 * Sets the max number of virtual nodes a virtual request is allowed to have
	 * 
	 * @param maxNodes
	 */
	public void setMaxNodes(int maxNodes) {
		this.maxNodes = maxNodes;
	}

	// ---------------------------------------- OTHER METHODS
	// ----------------------------------- //

	/**
	 * Generates number of requests. Default value is 100 requests unless other wise
	 * specified.
	 */
	public void generateRequests() {
		if (requests == null)
			this.requests = new ArrayList<>();
		if(maxTime != -1) {
			for (int requestNumber = 0; requestNumber < numberOfRequests; requestNumber++) {
				VirtualRequest request = new VirtualRequest(maxNodes, topology.getType(), maxTime);
				requests.add(request);
			}
		} else {
			for (int requestNumber = 0; requestNumber < numberOfRequests; requestNumber++) {
				VirtualRequest request = new VirtualRequest(maxNodes, topology.getType());
				requests.add(request);
			}
		}
	}

	public void generateSpecificRequests() {
		int requestCount = 0;
		if (requests == null)
			this.requests = new ArrayList<>();
		for (int start = 0; start < topology.getNodes().size() - 1; start++) {
			for (int destination = start + 1; destination < topology.getNodes().size(); destination++) {
				VirtualRequest request = new VirtualRequest(maxNodes, topology.getType());
				request.getVirtualNodes().get(0).setMap(start);
				request.getVirtualNodes().get(request.getNumberOfVirtualNodes()).setMap(destination);
				requests.add(request);
				requestCount++;
			}
		}
		setNumberOfRequest(requestCount);
	}

	private int updateTransponderBandwidth(Path path, int traffic) {
		PathNode current = path.getStart();
		PathNode next = current.next();
		int hops = 0;
		while (next != null) {
			// if(current.getNodeID() == path.getStart().getNodeID()){
			// topology.getNodes().get(current.getNodeID()).incrementTBC(traffic);
			// }
			// else if(!current.hasNext()){
			// topology.getNodes().get(current.getNodeID()).incrementRBC(traffic);
			// }
			// else{
			// topology.getNodes().get(current.getNodeID()).incrementTBC(traffic);
			// topology.getNodes().get(current.getNodeID()).incrementRBC(traffic);
			// }
			hops++;
			topology.getLink(current.getNodeID(), next.getNodeID()).incrementTBC(current.getNodeID(), next.getNodeID(),
					traffic);
			topology.getLink(current.getNodeID(), next.getNodeID()).decreaseBandwidthAvail(traffic);
			current = next;
			next = next.next();
		}
		return hops;
	}

	public void setRequests() {
		int[][] leafNodes = new int[][] { { 0, 0, 0, 1, 1, 2 }, { 1, 2, 3, 2, 3, 3 } };

		for (int i = 0; i < requests.size(); i++) {
			requests.get(i).getVirtualNodes().get(0).setMap(leafNodes[0][i]);
			requests.get(i).getVirtualNodes().get(1).setMap(leafNodes[1][i]);
			// System.out.println(requests.get(i).getVirtualLinks().get(0).toString());
			// System.out.println(requests.get(i).getVirtualLinks().get(0).toString());
		}
	}

	public ArrayList<Integer> getTranspondersODU(int transponderCapacity, int maxBandwidth, String distribution, int pathMode,
			boolean backupPath, ArrayList<CustomRequest> customRequest) {
		for (PhysicalLink l : topology.getLinks()) {
			l.resetTransponderBandwidth();
			l.setBandwidthAvailability(topology.getBandwidthAvailability());
		}
		int hops = 0;
		ArrayList<DynamicHelper> threads = new ArrayList<DynamicHelper>();
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(0);
		if(requests.get(0).getStart() != -1) {
			if(backupPath) {
				scheduler = Executors.newScheduledThreadPool(requests.size() * 2);
			} else {
				scheduler = Executors.newScheduledThreadPool(requests.size());
			}
		}
		if (!customRequest.isEmpty()) {
			for (CustomRequest cr : customRequest) {
				int start = cr.getStart();
				int finish = cr.getDestination();
				int traffic = cr.getBandwidth();
				switch (pathMode) {
				case 0:
					DijkstraShortestPath dsp = new DijkstraShortestPath(topology, start, finish);
					hops += updateTransponderBandwidth(dsp.getShortestPath(), traffic);
					if (backupPath)
						hops += updateTransponderBandwidth(dsp.getDisjointShortestPath(), traffic);
					break;
				case 1:
					KShortestPath kspLUF = new KShortestPath(topology, start, finish);
					ArrayList<Path> pathsLUF = kspLUF.getShortestPath(3);
					Path path1 = getLUF(pathsLUF);
					hops += updateTransponderBandwidth(path1, traffic);
					if (backupPath)
						hops += updateTransponderBandwidth(
								DijkstraShortestPath.getDisjointShortestPath(topology, path1, start, finish), traffic);
					break;
				case 2:
					KShortestPath kspMUF = new KShortestPath(topology, start, finish);
					ArrayList<Path> pathsMUF = kspMUF.getShortestPath(3);
					Path path2 = getMUF(pathsMUF);
					hops += updateTransponderBandwidth(path2, traffic);
					if (backupPath)
						hops += updateTransponderBandwidth(
								DijkstraShortestPath.getDisjointShortestPath(topology, path2, start, finish), traffic);
					break;
				default:
					DijkstraShortestPath dspDef = new DijkstraShortestPath(topology, start, finish);
					hops = hops + updateTransponderBandwidth(dspDef.getShortestPath(), traffic);
					break;
				}
			}
		} else {
			
			for (VirtualRequest vr : requests) {
				List<VirtualNode> virtualNodes = vr.getVirtualNodes();
				int start = virtualNodes.get(0).getMapID();
				int finish = virtualNodes.get(virtualNodes.size() - 1).getMapID();

				int traffic = -1;
				while (traffic <= 0 || traffic > maxBandwidth) {
					traffic = (distribution.equals("uniform")) ? Link.generateRandomBandwidthUniform(maxBandwidth)
							: (distribution.equals("gaussian")) ? Link.generateRandomBandwidthGaussian(maxBandwidth)
									: Link.generateRandomBandwidth(maxBandwidth);
				}

				switch (pathMode) {
				case 0:
					DijkstraShortestPath dsp = new DijkstraShortestPath(topology, start, finish);
					if(vr.getStart() != -1) {
						threads.add(new DynamicHelper(dsp.getShortestPath(), vr.getStart(), vr.getDuration(), traffic));
						if(backupPath) {
							threads.add(new DynamicHelper(dsp.getDisjointShortestPath(), vr.getStart(), vr.getDuration(), traffic));
						}
					}
					else {
						hops += updateTransponderBandwidth(dsp.getShortestPath(), traffic);
						if (backupPath)
							hops += updateTransponderBandwidth(dsp.getDisjointShortestPath(), traffic);
					}
					break;
				case 1:
					KShortestPath kspLUF = new KShortestPath(topology, start, finish);
					ArrayList<Path> pathsLUF = kspLUF.getShortestPath(3);
					Path path1 = getLUF(pathsLUF);
					Path disjointPath = DijkstraShortestPath.getDisjointShortestPath(topology, path1, start, finish);
					if(vr.getStart() != -1) {
						threads.add(new DynamicHelper(path1, vr.getStart(), vr.getDuration(), traffic));
						if(backupPath) {
							threads.add(new DynamicHelper(disjointPath, vr.getStart(), vr.getDuration(), traffic));
						}
					}
					else {
						hops += updateTransponderBandwidth(path1, traffic);
						if (backupPath)
							hops += updateTransponderBandwidth(
									disjointPath, traffic);
					}
					break;
				case 2:
					KShortestPath kspMUF = new KShortestPath(topology, start, finish);
					ArrayList<Path> pathsMUF = kspMUF.getShortestPath(3);
					Path path2 = getMUF(pathsMUF);
					Path disjointPath2 = DijkstraShortestPath.getDisjointShortestPath(topology, path2, start, finish);
					if(vr.getStart() != -1) {
						threads.add(new DynamicHelper(path2, vr.getStart(), vr.getDuration(), traffic));
						if(backupPath) {
							threads.add(new DynamicHelper(disjointPath2, vr.getStart(), vr.getDuration(), traffic));
						}
					}
					else {
						hops += updateTransponderBandwidth(path2, traffic);
						if (backupPath)
							hops += updateTransponderBandwidth(
									disjointPath2, traffic);
					}
					break;
				default:
					DijkstraShortestPath dspDef = new DijkstraShortestPath(topology, start, finish);
					hops = hops + updateTransponderBandwidth(dspDef.getShortestPath(), traffic);
					break;
				}

			}
		}
		int totalTranspondersODU = 0;
		int totalBandwidth = 0;
		int totalDropped = 0;
		if(requests.get(0).getStart() != -1) {
		//	System.out.println(threads.size());
			for(int x=0; x<threads.size(); x++) {
				scheduler.schedule(threads.get(x), threads.get(x).getStart(), TimeUnit.MILLISECONDS);
			}
			scheduler.shutdown();
			try {
				int extraSecond = 30;
	        	Thread.sleep(extraSecond * 1000);
	        }
	        catch(InterruptedException ex) 
	        {
	            ex.printStackTrace();
	        }
		//	System.out.println(DynamicHelper.count);
			for(int y=0; y<threads.size(); y++) {
				hops += threads.get(y).getHops();
				if(threads.get(y).getStatus() == false) totalDropped++;
			}
		}
		for (PhysicalLink l : topology.getLinks()) {
			totalTranspondersODU += l.getTransponders(transponderCapacity);
			totalBandwidth += l.getBandwidthUsed();
		}
		ArrayList<Integer> results = new ArrayList<Integer>();
		// for(int i = 0; i < topology.getType().getNumberOfPhysicalNodes(); i++){
		// int transmittersNeeded =
		// ((topology.getNodes().get(i).getTransmissionBandwidth() /
		// transponderCapacity) +
		// ((topology.getNodes().get(i).getTransmissionBandwidth() % transponderCapacity
		// != 0)? 1:0));
		// int receiversNeeded = ((topology.getNodes().get(i).getReceivingBandwidth() /
		// transponderCapacity) + ((topology.getNodes().get(i).getReceivingBandwidth() %
		// transponderCapacity != 0)? 1:0));
		// totalTranspondersODU += (transmittersNeeded + receiversNeeded);
		// }
		Double dropRatio = (double) totalDropped / (numberOfRequests * 2) * 100;
		results.add(totalTranspondersODU);
		results.add(totalBandwidth);
		results.add(hops);
		results.add(totalDropped);
		results.add((int) Math.ceil(dropRatio));
		return results;
	}

	public ArrayList<Integer> getTransponderOPT(int transponderCapacity, int maxBandwidth, String distribution, boolean backupPath) {
		
		ArrayList<Integer> results = new ArrayList<Integer>();
		int totalTranspondersOTN = 0;
		int hops = 0;
		int totalBandwidth = 0;
		for (int i = 0; i < numberOfRequests; i++) {
			int traffic = -1;
			while (traffic <= 0 || traffic > maxBandwidth) {
				traffic = (distribution.equals("uniform")) ? Link.generateRandomBandwidthUniform(maxBandwidth)
						: (distribution.equals("gaussian")) ? Link.generateRandomBandwidthGaussian(maxBandwidth)
								: Link.generateRandomBandwidth(maxBandwidth);
			}

			totalTranspondersOTN += ((traffic / transponderCapacity) + ((traffic % transponderCapacity != 0) ? 1 : 0))
					* 2;
			if (backupPath)
				totalTranspondersOTN += ((traffic / transponderCapacity)
						+ ((traffic % transponderCapacity != 0) ? 1 : 0)) * 2;
			totalBandwidth += (traffic * 4);
			hops += 2;
		}
		results.add(totalTranspondersOTN);
		results.add(totalBandwidth);
		results.add(hops);
		return results;
	}

	public ArrayList<Integer> getTransponderMUX(int transponderCapacity, int maxBandwidth, String distribution, boolean backupPath) {

		int totalTranspondersMUX = 0;
		int totalBandwidth = 0;
		int hops = 0;
		ArrayList<ArrayList<Integer>> trafficGroup = new ArrayList<ArrayList<Integer>>();
		Set<Pair<Integer, Integer>> paths = new HashSet<Pair<Integer, Integer>>();
		ArrayList<Pair<Integer, Integer>> tempArray = new ArrayList<Pair<Integer, Integer>>();

		for (VirtualRequest vr : requests) {

			List<VirtualNode> virtualNodes = vr.getVirtualNodes();
			int traffic = -1;
			while (traffic <= 0 || traffic > maxBandwidth) {
				traffic = (distribution.equals("uniform")) ? Link.generateRandomBandwidthUniform(maxBandwidth)
						: (distribution.equals("gaussian")) ? Link.generateRandomBandwidthGaussian(maxBandwidth)
								: Link.generateRandomBandwidth(maxBandwidth);
			}
			int start = virtualNodes.get(0).getMapID();
			int finish = virtualNodes.get(virtualNodes.size() - 1).getMapID();
			Pair<Integer, Integer> tempPair = new Pair<Integer, Integer>(start, finish);
			paths.add(tempPair);
			tempArray.addAll(paths);
			traffic = (int) (Math.ceil(traffic / 10) * 10);
			if (trafficGroup.size() == tempArray.size()) {
				trafficGroup.get(tempArray.indexOf(tempPair)).add(traffic);
				tempArray.clear();
				continue;
			}
			trafficGroup.add(new ArrayList<Integer>());
			trafficGroup.get(trafficGroup.size() - 1).add(traffic);
			tempArray.clear();

		}

		for (int x = 0; x < trafficGroup.size(); x++) {
			int totalTraffic = 0;
			for (int y = 0; y < trafficGroup.get(x).size(); y++) {
				totalTraffic += trafficGroup.get(x).get(y);
			}
			totalTranspondersMUX += ((totalTraffic / transponderCapacity)
					+ ((totalTraffic % transponderCapacity != 0) ? 1 : 0)) * 2;
			totalBandwidth += totalTraffic;
			if (backupPath)
				totalTranspondersMUX += ((totalTraffic / transponderCapacity)
						+ ((totalTraffic % transponderCapacity != 0) ? 1 : 0)) * 2;
			totalBandwidth += totalTraffic;
			hops += 2;
		}
		ArrayList<Integer> results = new ArrayList<Integer>();
		results.add(totalTranspondersMUX);
		results.add(totalBandwidth);
		results.add(hops);
		return results;
	}

	public ArrayList<Integer> getTranspondersHybrid(int transponderCapacity, int maxBandwidth, String distribution, boolean backupPath,
			int threshold) {
		int totalTransponders = 0;
		int hops = 0;
		int totalBandwidth = 0;
		for (PhysicalLink l : topology.getLinks()) {
			l.resetTransponderBandwidth();
		}
		for (VirtualRequest vr : requests) {
			List<VirtualNode> virtualNodes = vr.getVirtualNodes();
			int start = virtualNodes.get(0).getMapID();
			int finish = virtualNodes.get(virtualNodes.size() - 1).getMapID();

			int traffic = -1;
			while (traffic <= 0 || traffic > maxBandwidth) {
				traffic = (distribution.equals("uniform")) ? Link.generateRandomBandwidthUniform(maxBandwidth)
						: (distribution.equals("gaussian")) ? Link.generateRandomBandwidthGaussian(maxBandwidth)
								: Link.generateRandomBandwidth(maxBandwidth);
			}

			// System.out.println("start: " + start + " finish: " + finish);

			if (traffic <= threshold) {
				// run ODU
				DijkstraShortestPath dsp = new DijkstraShortestPath(topology, start, finish);
				// System.out.println(start + " " + finish);
				hops += updateTransponderBandwidth(dsp.getShortestPath(), traffic);

				if (backupPath)
					hops += updateTransponderBandwidth(dsp.getDisjointShortestPath(), traffic);
			} else {
				// run OPT
				totalTransponders += ((traffic / transponderCapacity) + ((traffic % transponderCapacity != 0) ? 1 : 0))
						* 2;
				if (backupPath)
					totalTransponders += ((traffic / transponderCapacity)
							+ ((traffic % transponderCapacity != 0) ? 1 : 0)) * 2;
				totalBandwidth += (traffic * 4);
			}
		}

		for (PhysicalLink l : topology.getLinks()) {
			totalTransponders += l.getTransponders(transponderCapacity);
			totalBandwidth += l.getBandwidthUsed();
		}
		// for(int i = 0; i < topology.getType().getNumberOfPhysicalNodes(); i++){
		// int transmittersNeeded =
		// ((topology.getNodes().get(i).getTransmissionBandwidth() /
		// transponderCapacity) +
		// ((topology.getNodes().get(i).getTransmissionBandwidth() % transponderCapacity
		// != 0)? 1:0));
		// int receiversNeeded = ((topology.getNodes().get(i).getReceivingBandwidth() /
		// transponderCapacity) + ((topology.getNodes().get(i).getReceivingBandwidth() %
		// transponderCapacity != 0)? 1:0));
		// totalTranspondersODU += (transmittersNeeded + receiversNeeded);
		// }
		ArrayList<Integer> results = new ArrayList<Integer>();
		results.add(totalTransponders);
		results.add(totalBandwidth);
		results.add(hops);
		return results;
	}

	/**
	 * Removes all mappings for all virtual requests and returns all resources to
	 * physical links and physical nodes
	 */
	public void resetAllResources() {

		topology.setRequestsMapped(0);

		for (VirtualRequest vr : requests) {
			if (vr.isBlocked())
				vr.unblock();
			for (int i = 1; i < vr.getVirtualNodes().size() - 1; i++)
				vr.getVirtualNodes().get(i).setMap(-1);
			for (VirtualLink vl : vr.getVirtualLinks())
				vl.setLinkMapping(null);
		}

		for (Map.Entry<Integer, PhysicalNode> entry : topology.getNodes().entrySet())
			entry.getValue().setComputationAvailability(topology.getComputationalAvailability());

		for (PhysicalLink pl : topology.getLinks())
			pl.setBandwidthAvailability(topology.getBandwidthAvailability());
	}

	public Path getLUF(ArrayList<Path> paths) {
		Path returnPath = null;
		for (int x = 0; x < paths.size(); x++) {
			int currMax = getGreatestBandwidthFromPath(paths.get(x));
			if (returnPath == null) {
				returnPath = paths.get(x);
				continue;
			}
			if (currMax > getGreatestBandwidthFromPath(returnPath))
				returnPath = paths.get(x);
		}
		return returnPath;
	}

	public int getGreatestBandwidthFromPath(Path path) {
		int max = 0;
		PathNode current = path.getStart();
		PathNode next = current.next();
		while (next != null) {
			int availBandwidth = topology.getLink(current.getNodeID(), next.getNodeID()).getbandwidthAvailability();
			if (availBandwidth > max)
				max = availBandwidth;
			current = next;
			next = next.next();
		}
		return max;
	}

	public Path getMUF(ArrayList<Path> paths) {
		Path returnPath = null;
		for (int x = 0; x < paths.size(); x++) {
			int currLow = getLeastBandwidthFromPath(paths.get(x));
			if (returnPath == null) {
				returnPath = paths.get(x);
				continue;
			}
			if (currLow < getLeastBandwidthFromPath(returnPath))
				returnPath = paths.get(x);
		}
		return returnPath;
	}

	public int getLeastBandwidthFromPath(Path path) {
		int low = Integer.MAX_VALUE;
		PathNode current = path.getStart();
		PathNode next = current.next();
		while (next != null) {
			int availBandwidth = topology.getLink(current.getNodeID(), next.getNodeID()).getbandwidthAvailability();
			if (availBandwidth < low)
				low = availBandwidth;
			current = next;
			next = next.next();
		}
		return low;
	}
}
