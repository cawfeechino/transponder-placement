package simulator;

import models.Path;
import models.PathNode;

public class DynamicHelper extends Thread {
	private int start = 0;
	private int duration = 0;
	private int bandwidth = 0;
	private Path path;
	private int hops = 0;
	
	public DynamicHelper(Path path, int start, int duration, int bandwidth) {
		this.start= start;
		this.duration = duration;
		this.bandwidth = bandwidth;
		this.path = path;
	}
	
	public void run() {
		useTransponderBandwidth(path, bandwidth);
		System.out.println("starting at " + start);
		try {
            Thread.sleep(duration * 1000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
		int totalTime = start + duration;
		System.out.println("ending at " + totalTime);
		restoreTranponderBandwidth(path, bandwidth);
	}
	
	private int useTransponderBandwidth(Path path, int traffic) {
		PathNode current = path.getStart();
		PathNode next = current.next();
		int status = 1;
		while (next != null) {
			Simulator.getTopology().getLink(current.getNodeID(), next.getNodeID()).decreaseBandwidthAvail(traffic);
			Simulator.getTopology().getLink(current.getNodeID(), next.getNodeID()).incrementTBC(current.getNodeID(), next.getNodeID(),
					traffic);
			hops++;
			current = next;
			next = next.next();
		}
		return status;
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
	
	public int getHops() {
		return hops;
	}
	
	public int getStart() {
		return start;
	}
	//have this extend thread
	//copy updateTransponderBandwidth from simulator
}
