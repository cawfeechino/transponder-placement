package models;

public class VirtualLink {

	private Pair<Integer,Integer> endNodes;
	private Path linkMapping;
	private int bandwidthDemand;
	
	public VirtualLink(Pair<Integer,Integer> endNodes, int bandwidthDemand) {
		this.endNodes = endNodes;
		this.bandwidthDemand = bandwidthDemand;
	}
	
	public int getOriginID(){
		return endNodes.first();
	}

	public int getDestinationID(){
		return endNodes.second();
	}
	
	public int getbandwidthDemand(){
		return bandwidthDemand;
	}
	
	public Path getLinkMapping(){
		return linkMapping;
	}
	
	public void setLinkMapping(Path linkMapping){
		this.linkMapping = linkMapping;
	}
	
	public String toString(){
		return String.format("Origin: %1$-2d , Destination: %2$-2d , Bandwidth Demand: %3$-4d ", getOriginID(), getDestinationID(), bandwidthDemand);

	}

}
