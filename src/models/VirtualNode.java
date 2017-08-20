package models;

public class VirtualNode {

	private int id;
	private int computationalDemand;
	private int mapID = -1;
	private int requestedFunctionID = -1;
	
	
	public VirtualNode(int id, int computationalDemand) {
		this.id = id;
		this.computationalDemand = computationalDemand;
	}

	public void setMap(int mapID) {
		this.mapID = mapID;	
	}

	public void setID(int id) {
		this.id = id;
	}

	public void setRequestedFunction(int requestedFunctionID) {
		this.requestedFunctionID = requestedFunctionID;
		
	}

	public int getID() {
		return id;
	}
	
	public int getComputationalDemand(){
		return computationalDemand;
	}
	
	public int getMapID(){
		return mapID;
	}
	
	public int getRequestedFunctionID(){
		return requestedFunctionID;
	}
	
	public String toString(){
		
		return String.format("%1$-2d , Map ID: %2$-3d , Computational Demand: %3$-3d , Requested Function: %4$-2d", id, mapID, computationalDemand, requestedFunctionID);
	}

}
