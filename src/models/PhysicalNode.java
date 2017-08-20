package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PhysicalNode {

	public static int nodeCount = 0;
	
	private int id;
	private int computationalAvailability;
	
	private boolean disabled = false;
	
	private int transmitterBandwidth = 0;
	private int receiverBandwidth = 0;
	
	private List<Integer> networkFunctions;
	private Map<Integer, Integer> adjacentNodes;
	
	private Map<Integer, Integer> removedAdjacentNodes;
	
	
	public PhysicalNode(int computationalAvailability) {
		this.computationalAvailability = computationalAvailability;
		this.networkFunctions = new ArrayList<>();
		this.adjacentNodes = new HashMap<>();
		this.removedAdjacentNodes = new HashMap<>();
		this.id = nodeCount++;
	}
	
	public void removeAdjacentNode(int id){
		removedAdjacentNodes.put(id, adjacentNodes.get(id));
		adjacentNodes.remove(id);
	}
	
	public void saveRemovedAdjacentNode(int id){
		removedAdjacentNodes.put(id, adjacentNodes.get(id));
	}
	
	public void resetAdjacentNodes(){
		adjacentNodes.putAll(removedAdjacentNodes);
		//removedAdjacentNodes.clear();
	}
	
	public void resetAdjacentNodesAlpha(){
		if(removedAdjacentNodes == null) removedAdjacentNodes = new HashMap<>();
		
		adjacentNodes.putAll(removedAdjacentNodes);
		removedAdjacentNodes.clear();
	}

	
	//------------------------------ MUTATORS ------------------------------//
	
	public int addNetworkFunction(int functionID) {
		networkFunctions.add(functionID);
		return functionID;
	}
	
	public void setID(int id){
		this.id = id;
	}
	
	public void setComputationAvailability(int computationalAvailability){
		this.computationalAvailability = computationalAvailability;
	}

	//------------------------------ ACCESSORS -----------------------------//
	
	public int getID() {
		return id;
	}

	public int getComputationalAvailability() {
		return computationalAvailability;
	}

	public Map<Integer, Integer> getAdjacentNodes() {
		return adjacentNodes;
	}
	
	public Map<Integer, Integer> getRemovedAdjacentNodes() {
		return removedAdjacentNodes;
	}
	
	public int getNetworkFunction(int i){
		return networkFunctions.get(i);
	}
	
	// ---------------------------- BOOLEAN ---------------------------------//
	
	public void disable(){
		disabled = true;
	}
	
	public void enable(){
		disabled = false;
	}
	
	public boolean isDisabled(){
		return disabled;
	}
	
	public String toString(){
		return "PHYSICAL NODE: " + id;
	}

	public void incrementTBC(int bandwidthConsumption) {
		transmitterBandwidth += bandwidthConsumption;
		
	}
	
	public void incrementRBC(int bandwidthConsumption) {
		receiverBandwidth += bandwidthConsumption;
		
	}

	public int getTransmissionBandwidth() {
		return transmitterBandwidth;
	}
	
	public int getReceivingBandwidth() {
		return receiverBandwidth;
	}
}
