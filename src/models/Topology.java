package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import utilities.NetworkTopology;

/**
 * Topology represents a physical topology. In order to get the topology from
 * a given file you must use the TopologyUtil class to read the adjacency matrix
 * from the file and generate a topology object.
 * @author Carlos M. Galdamez
 */
public class Topology {
	
	private NetworkTopology type;									// Type of topology being used (US_MESH, NSFNET or SIMPLE)
	
	private int computationalAvailability;							// Computational units available for mapping in each node
	private int bandwidthAvailability;								// Bandwidth units available for mapping in each link
	
	private Map<Integer, PhysicalNode> nodes;						// Map of nodes in the physical topology
	private List<List<PhysicalNode>> subsets;       				// Nodes are separated into subsets based on their network function
	
	private List<PhysicalLink> links;								// List of links in the physical topology

	private Map<Integer, PhysicalNode> removedNodes;				// Any removed nodes will be saved in this map, to ensure that nodes can be rest if necessary
		
	private Map<Integer,Map<Integer,Path>> pathDictionary;			// Used only for algorithm 3
	
	private int requestsMapped = 0;									// Virtual requests successfully mapped to this topology
	
	/**
	 * Constructor sets the topology type of the physical topology.
	 * @param type - Type of topology being used (US_MESH, NSFNET or SIMPLE)
	 */
	public Topology(NetworkTopology type){
		this.type = type;
	}
	
	// --------------------------------------------- ACCESSORS ---------------------------------------------//
	
	/**
	 * Gets the type of topology being used.
	 * @return - type of topology being used
	 */
	public NetworkTopology getType() {
		return type;
	}
	
	/**
	 * Gets the number of requests that were mapped to this physical topology.
	 * @return - integer that represents number of requests mapped
	 */
	public int getRequestsMapped(){
		return requestsMapped;
	}
	
	/**
	 * Gets the max computational availability of nodes in topology. 
	 * @return - integer representing max computational availability of nodes.
	 */
	public int getComputationalAvailability(){
		return computationalAvailability;
	}
	
	/**
	 * Gets the max bandwidth availability of links in topology.
	 * @return - integer representing max bandwidth availability of links.
	 */
	public int getBandwidthAvailability(){
		return bandwidthAvailability;
	}
	
	/**
	 * Gets map of all nodes in the topology.
	 * @return - reference to map of nodes in topology.
	 */
	public Map<Integer, PhysicalNode> getNodes() {
		return nodes;
	}
	
	/**
	 * Gets list of subsets of nodes.
	 * @return - reference to list of nodes separated into subsets based on network function.
	 */
	public List<List<PhysicalNode>> getFunctionSubsets(){
		return subsets;
	}

	/**
	 * Gets list of links in the topology.
	 * @return - reference to list of links in topology.
	 */
	public List<PhysicalLink> getLinks(){
		return links;
	}
	
	/**
	 * Gets list of all shortest paths between nodes. Used for algorithm 3.
	 * @return - reference to list of shortest paths.
	 */
	public Map<Integer,Map<Integer,Path>> getPathDictionary() {
		if(pathDictionary == null) {
			pathDictionary = new HashMap<>();
			for(int i = 0; i < type.getNumberOfPhysicalNodes();i++){
				pathDictionary.put(i,new HashMap<>());
			}
		}
		
		return pathDictionary;
	}
	
	// --------------------------------------------- MUTATORS ---------------------------------------------//

	/**
	 * Set the amount of requests mapped
	 * @param requestsMapped - number of requests that were mapped successfully
	 */
	public void setRequestsMapped(int requestsMapped){
		this.requestsMapped = requestsMapped;
	}
	
	/**
	 * Set max computational availability of nodes in this topology. 
	 * @param computationalAvailability - max computational availability.
	 */
	public void setComputationalAvailability(int computationalAvailability){
		this.computationalAvailability = computationalAvailability;
	}
	
	/**
	 * Set max bandwidth availability of the links in this topology.
	 * @param bandwidthAvailability - max bandwidth availability.
	 */
	public void setBandwidthAvailability(int bandwidthAvailability){
		this.bandwidthAvailability = bandwidthAvailability;
	}
	
	/**
	 * Set nodes to a list of nodes.
	 * @param nodes - list of nodes.
	 */
	public void setNodes(Map<Integer, PhysicalNode> nodes) {
		this.nodes = nodes;
	}
	
	/**
	 * Set links to a list of links.
	 * @param links -list of links.
	 */
	public void setLinks(List<PhysicalLink> links) {
		this.links = links;
	}
	
	/**
	 * Set path dictionary.
	 * @param pathDictionary - set path dictionary.
	 */
	public void setPathDictionary(Map<Integer,Map<Integer,Path>> pathDictionary) {
		this.pathDictionary = pathDictionary;
	}
	
	// ---------------------------------------- OTHER METHODS ------------------------------------------//

	/**
	 * Remove node with the specified id.
	 * @param id - id of node to be removed.
	 */
	public void removeNode(int id){
		if(removedNodes == null) removedNodes = new HashMap<>();
		
		// Store removed node in map of removed nodes
		removedNodes.put(id, nodes.get(id));
		nodes.remove(id);
	}
	
	/**
	 * Restores all removed nodes.
	 */
	public void resetNodes(){
		if(removedNodes == null) removedNodes = new HashMap<>();
		
		nodes.putAll(removedNodes);
		removedNodes.clear();
	}
	
	/**
	 * Increases requests mapped by 1.
	 */
	public void increaseRequestsMapped(){
		requestsMapped++;
	}
	
	/**
	 * Gets a link object with the specified origin and destination
	 * @param origin
	 * @param destination
	 * @return - physical link object
	 */
	public PhysicalLink getLink(int origin, int destination){
		for(PhysicalLink link: links){
			Pair<Integer,Integer> endNodes = link.getEndNodes();
			if((endNodes.first() == origin && endNodes.second() == destination) || (endNodes.second() == origin && endNodes.first() == destination))
				return link;
		}
		return null;
	}
	
	/**
	 * Creates a list of lists of physical nodes. The list are subsets of physical nodes
	 * separated by network function.
	 */
	public void setFunctionSubsets(){
		if(subsets == null) subsets = new ArrayList<>();

		
		subsets.add(new ArrayList<>());
		subsets.add(new ArrayList<>());
		subsets.add(new ArrayList<>());
		
		for(Entry<Integer, PhysicalNode> entry : nodes.entrySet()){
			subsets.get(entry.getValue().getNetworkFunction(0) - 1).add(entry.getValue());
			subsets.get(entry.getValue().getNetworkFunction(1) - 1).add(entry.getValue());
		}
	}

	/**
	 * Calculates the total bandwidth consumption of the whole topology.
	 * @return - integer representing the bandwidth consumption of the whole topology.
	 */
	public int getTotalBandwidthConsumption(){
		int sum = 0;
		
		for(PhysicalLink link: links){
			sum += (bandwidthAvailability - link.getbandwidthAvailability());
		}
		
		return sum;
	}

}
