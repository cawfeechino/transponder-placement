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

	public DynamicHelper(Path path, int start, int duration, int bandwidth) {
		this.start = start;
		this.duration = duration;
		this.bandwidth = bandwidth;
		this.path = path;
	}

	public void run() {
		try {
			useTransponderBandwidth(path, bandwidth);
		} catch (Exception e) {
			e.printStackTrace();
		}

	//	System.out.println("starting at " + start);
		try {
			Thread.sleep(duration * 1000);
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
			} else
				status = false;
		}
		if (status == true) {
			for (int x = start; x <= start + duration; x++) {
				utilization.set(x, utilization.get(x) + traffic);
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
		for (int x = 0; x <= totalTime; x++) {
			utilization.add(0);
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

	public boolean getStatus() {
		return status;
	}
	// have this extend thread
	// copy updateTransponderBandwidth from simulator
}
