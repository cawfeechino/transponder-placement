package models;

public class PhysicalLink {

	private int linkDistance;
	private int bandwidthAvailability;
	private Pair<Integer,Integer> endNodes;
	
	private int transponderBandwidth1 = 0;
	private int transponderBandwidth2 = 0;

	
	public PhysicalLink(int linkDistance, int bandwidthAvailability, Pair<Integer,Integer> endNodes) {
		
		this.linkDistance = linkDistance;
		this.bandwidthAvailability = bandwidthAvailability;
		this.endNodes = endNodes;
	}

	public void incrementTBC(int origin, int destination, int bandwidth){
		if(endNodes.first() == origin)
			transponderBandwidth1 += bandwidth;
		else
			transponderBandwidth2 += bandwidth;
	}
	
	public void decreaseBandwidthAvail(int bandwidth){
		bandwidthAvailability -= bandwidth;
	}
	
	public void resetTransponderBandwidth(){
		//System.out.println("Before: " + transponderBandwidth1 + " " + transponderBandwidth2);
		transponderBandwidth1 = 0;
		transponderBandwidth2 = 0;
		//System.out.println("After: " + transponderBandwidth1 + " " + transponderBandwidth2);
	}
	
	public int getTransponders(int capacity){
		return (((transponderBandwidth1 / capacity) + ((transponderBandwidth1 % capacity != 0)? 1:0)) * 2)
			 + (((transponderBandwidth2 / capacity) + ((transponderBandwidth2 % capacity != 0)? 1:0)) * 2);
	}
	
	public int getLinkDistance(){
		return linkDistance;
	}
	
	public int getbandwidthAvailability(){
		return bandwidthAvailability;
	}
	
	public Pair<Integer, Integer> getEndNodes(){
		return endNodes;
	}
	
	public void setBandwidthAvailability(int bandwidthAvailability){
		this.bandwidthAvailability = bandwidthAvailability;
	}
	
	public String toString(){
		return "PHYSICAL LINK: " + endNodes.first() + " <---> " + endNodes.second() + " ; BANDWIDTH: " + bandwidthAvailability;
	}
}
