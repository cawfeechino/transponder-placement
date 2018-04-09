package simulator;

import java.util.ArrayList;

import models.Path;
import models.PathNode;

public class DynamicHelper extends Thread {
	private int start = 0;
	private int duration = 0;
	private int bandwidth = 0;
	private Path path;
	private int hops = 0;
	private boolean status = true;
	private static ArrayList<Integer> utilization = new ArrayList<Integer>();
	private static ArrayList<Integer> dropSecond = new ArrayList<Integer>();
	static int count = 0;
	
	public DynamicHelper(Path path, int start, int duration, int bandwidth) {
		this.start = start;
		this.duration = duration;
		this.bandwidth = bandwidth;
		this.path = path;
	}

	public void run() {
	//	System.out.println(count++ + " " + start);
		
		try {
			useTransponderBandwidth(path, bandwidth);
		} catch (Exception e) {
			e.printStackTrace();
		}

	//	System.out.println("starting at " + start);
		try {
			Thread.sleep(duration);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		int totalTime = start + duration;
	//	System.out.println("ending at " + totalTime);
		restoreTranponderBandwidth(path, bandwidth);
	}

	private void useTransponderBandwidth(Path path, int traffic) {
		PathNode current = path.getStart();
		PathNode next = current.next();
		for (int x = start; x < start + duration; x++) {
			if((utilization.get(x) + traffic) > (Simulator.getTopology().getLinks().size() * Simulator.getMaxBandwidth())) {
				
				status = false;
				break;
			}
		}
		while (next != null && status == true) {
			// only do if available bandwidth is greatest or equal to the traffic needed,
			// otherwise skips
			// do we need to do something to the dropped ones though
		//	System.out.println(Simulator.getTopology().getLink(current.getNodeID(), next.getNodeID())
		//			.getbandwidthAvailability() + " " + traffic);
			if (Simulator.getTopology().getLink(current.getNodeID(), next.getNodeID())
					.getbandwidthAvailability() >= traffic) {
				Simulator.getTopology().getLink(current.getNodeID(), next.getNodeID()).decreaseBandwidthAvail(traffic);
				Simulator.getTopology().getLink(current.getNodeID(), next.getNodeID()).incrementTBC(current.getNodeID(),
						next.getNodeID(), traffic);
				hops++;
				current = next;
				next = next.next();
			} else
				status = false;
		}
		if (status == true) {
			for (int x = start; x < start + duration; x++) {
				utilization.set(x, utilization.get(x) + traffic);
			//	if(utilization.get(x) >= 42000) System.out.println(utilization.get(x) + " " + start);
			}
		}
		if (status == false) {
		//	System.out.println(++count);
			for (int x = start; x < start + duration; x++) {
				dropSecond.set(x, dropSecond.get(x) + 1);
			}
		}
	}

	private void restoreTranponderBandwidth(Path path, int traffic) {
		PathNode current = path.getStart();
		PathNode next = current.next();
		while (next != null) {
			Simulator.getTopology().getLink(current.getNodeID(), next.getNodeID()).increaseBandwidthAvail(traffic);
			current = next;
			next = next.next();
		}
	}

	public static void utilInit(int totalTime) {
		utilization.clear();
		dropSecond.clear();
		for (int x = 0; x <= totalTime; x++) {
			utilization.add(0);
			dropSecond.add(0);
		}
	}

	public int getHops() {
		return hops;
	}

	public int getStart() {
		return start;
	}

	public static ArrayList<Integer> getUtilization() {
		return utilization;
	}
	
	public static ArrayList<Integer> getDropSecond(){
		return dropSecond;
	}

	public boolean getStatus() {
		return status;
	}
	// have this extend thread
	// copy updateTransponderBandwidth from simulator
}
