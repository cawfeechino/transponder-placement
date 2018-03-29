package simulator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import models.Path;
import models.PathNode;
import models.Topology;
import utilities.DijkstraShortestPath;
import utilities.KShortestPath;

public class DynamicHelper extends Thread {
	private int start = 0;
	private int duration = 0;
	private int bandwidth = 0;
	private Path path;
	private int hops = 0;
	private int status = 1;
	private int pathStart = 0;
	private int pathFinish = 0;
	private Path KSPPath;
	private Path KSPDisPath;
	private int mode;
	private static int counter;
	private ArrayList<Path> paths = new ArrayList<Path>();

	public DynamicHelper(Path path, int start, int duration, int bandwidth) {
		this.start = start;
		this.duration = duration;
		this.bandwidth = bandwidth;
		this.path = path;
	}
	
	public DynamicHelper(int start, int duration, int bandwidth, int pathStart, int pathFinish, int mode, ArrayList<Path> paths) {
		this.start = start;
		this.duration = duration;
		this.bandwidth = bandwidth;
		this.pathStart = pathStart;
		this.pathFinish = pathFinish;
		this.mode = mode;
		this.paths = paths;
	}

	/*public void run() {
		useTransponderBandwidth(path, bandwidth);
		if (status == 1) {
			System.out.println("starting at " + start);
			try {
				Thread.sleep(duration * 1000);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
			int totalTime = start + duration;
			System.out.println("ending at " + totalTime);
			restoreTransponderBandwidth(path, bandwidth);
		}
	}*/
	
	public void run() {
		switch(mode) {
		//LUF
		case 1:
			generatePathLUF();
			break;
		//MUF
		case 2:
			generatePathMUF();
			break;
		default:
			break;
		}
		if(KSPPath != null) {
			useTransponderBandwidth(KSPPath, bandwidth);
			useTransponderBandwidth(KSPDisPath, bandwidth);
		}
		if(KSPPath == null) {
			useTransponderBandwidth(path, bandwidth);
		}
		if (status == 1) {
		//	System.out.println("starting at " + start);
			try {
				Thread.sleep(duration * 1000);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
			int totalTime = start + duration;
		//	System.out.println("ending at " + totalTime);
			if(KSPPath != null) {
				restoreTransponderBandwidth(KSPPath, bandwidth);
				restoreTransponderBandwidth(KSPDisPath, bandwidth);
			}
			if(KSPPath == null) {
				restoreTransponderBandwidth(path, bandwidth);
			}
		}
	}

	private int useTransponderBandwidth(Path path, int traffic) {
		PathNode current = path.getStart();
		PathNode next = current.next();
		status = 1;
		while (next != null) {
			// only do if available bandwidth is greatest or equal to the traffic needed,
			// otherwise skips
			// do we need to do something to the dropped ones though
			if (Simulator.getTopology().getLink(current.getNodeID(), next.getNodeID())
					.getbandwidthAvailability() >= traffic) {
				Simulator.getTopology().getLink(current.getNodeID(), next.getNodeID()).decreaseBandwidthAvail(traffic);
				Simulator.getTopology().getLink(current.getNodeID(), next.getNodeID()).incrementTBC(current.getNodeID(),
						next.getNodeID(), traffic);
				hops++;
				current = next;
				next = next.next();
			} else {
				status = 0;
				break;
			}
		}
		return status;
	}
	
	private void generatePathLUF() {
	//	System.out.println(paths.get(1).toString());
		KSPPath = Simulator.getLUF(paths);
	//	System.out.println(KSPPath.toString());
		System.out.println("start: " + pathStart + " finish: " + pathFinish);
		try {
			KSPDisPath = Simulator.getDisjointPath(KSPPath, pathStart, pathFinish);
		} catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("test");
		
	}
	
	private void generatePathMUF() {
		KSPPath = Simulator.getMUF(paths);
		KSPDisPath = getDisjointShortestPath(Simulator.getTopology(), KSPPath, pathStart, pathFinish);
	}

	private void restoreTransponderBandwidth(Path path, int traffic) {
		PathNode current = path.getStart();
		PathNode next = current.next();
		while (next != null) {
			Simulator.getTopology().getLink(current.getNodeID(), next.getNodeID()).increaseBandwidthAvail(traffic);
			current = next;
			next = next.next();
		}
	}
	
	public Path getDisjointShortestPath(Topology topology, Path path, int originID, int destinationID){
		//if(path.getNumberOfHops() == 0) getShortestPath();

		
		PathNode current = path.getStart().next();
		while(current.hasNext()){
			if(!current.hasNext()) break;
			try {
			for (Map.Entry<Integer,Integer> entry : topology.getNodes().get(current.getNodeID()).getAdjacentNodes().entrySet()) { 
		//		System.out.println("aaa " + current.getNodeID());
				topology.getNodes().get(entry.getKey()).removeAdjacentNode(current.getNodeID());
				
			}
			} catch(Exception e) {
				e.printStackTrace();
			}
			topology.removeNode(current.getNodeID());
			
			current = current.next();
		}
		
		DijkstraShortestPath dsp = new DijkstraShortestPath(topology, originID, destinationID);
		Path disjointPath = dsp.getShortestPath();
		topology.resetNodes();
		
		current = path.getStart().next();
		while(current.hasNext()){
			if(!current.hasNext()) break;
			for (Iterator<Map.Entry<Integer,Integer>> it = topology.getNodes().get(current.getNodeID()).getAdjacentNodes().entrySet().iterator();it.hasNext();) {
				Entry<Integer, Integer> curr = it.next();

				topology.getNodes().get(curr.getKey()).resetAdjacentNodes();
			}
			current = current.next();
		}
		return disjointPath;
	}

	public int getHops() {
		return hops;
	}

	public int getStart() {
		return start;
	}

	public int getStatus() {
		return status;
	}
	// have this extend thread
	// copy updateTransponderBandwidth from simulator
}
