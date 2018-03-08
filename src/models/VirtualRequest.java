package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import utilities.NetworkTopology;

/**
 * VirtualRequest class represents a virtual request. A virtual request contains
 * a list of virtual nodes and virtual links. The user must specify the max
 * number of nodes a virtual request can have. The assumption for these virtual
 * request is that they follow a linear array topology meaning that the number
 * of virtual links is always going to be one less than the number of virtual
 * nodes. Each virtual node will have a bandwidth demand , and each virtual node
 * will have a computational demand and a requested function. If a request
 * cannot be mapped to a physical topology then the request is blocked.
 * 
 * @author Carlos M. Galdamez
 *
 */
public class VirtualRequest {

	private int numberOfVirtualNodes;
	private int numberOfVirtualLinks;
	private int start = -1;
	private int duration = -1;
	
	private boolean blocked = false;

	private List<VirtualNode> virtualNodes;
	private List<VirtualLink> virtualLinks;

	/**
	 * Constructor creates the virtual nodes and virtual links for the virtual
	 * request being created
	 * 
	 * @param maxNodes
	 *            - Max number of nodes the virtual request can have
	 * @param type
	 *            - Type of topology being used
	 */
	public VirtualRequest(int maxNodes, NetworkTopology type) {

		// Minimum number of nodes is 2, largest is 1 - max number of nodes
		this.numberOfVirtualNodes = 2 + (int) (Math.random() * maxNodes);
		// Linear array topology has 1 less link than the total number of nodes
		this.numberOfVirtualLinks = numberOfVirtualNodes - 1;

		virtualNodes = new ArrayList<>();
		virtualLinks = new ArrayList<>();

		// Create virtual nodes
		for (int vnID = 0; vnID < numberOfVirtualNodes; vnID++) {
			VirtualNode virtualNode = new VirtualNode(vnID, Node.generateRandomComputationalSpeed());
			virtualNode.setRequestedFunction(1 + (int) (Math.random() * 3));
			virtualNodes.add(virtualNode);
		}

		// ------------------------ Map source node and destination node
		// randomly ------------------------ //

		int originMapID = (int) (Math.random() * type.getNumberOfPhysicalNodes());
		int destinationMapID = originMapID;
		while (destinationMapID == originMapID)
			destinationMapID = (int) (Math.random() * type.getNumberOfPhysicalNodes());

		virtualNodes.get(0).setMap(originMapID);
		virtualNodes.get(numberOfVirtualNodes - 1).setMap(destinationMapID);

		// ----------------------------------- Create virtual links
		// ------------------------------------- //

		for (int virtualNode = 0; virtualNode < numberOfVirtualLinks; virtualNode++)
			virtualLinks.add(new VirtualLink(new Pair<Integer, Integer>(virtualNodes.get(virtualNode).getID(),
					virtualNodes.get(virtualNode + 1).getID()), Link.generateRandomBandwidth()));
	}
	
	public VirtualRequest(int maxNodes, NetworkTopology type, int maxSecond) {

		// Minimum number of nodes is 2, largest is 1 - max number of nodes
		this.numberOfVirtualNodes = 2 + (int) (Math.random() * maxNodes);
		// Linear array topology has 1 less link than the total number of nodes
		this.numberOfVirtualLinks = numberOfVirtualNodes - 1;

		virtualNodes = new ArrayList<>();
		virtualLinks = new ArrayList<>();

		// Create virtual nodes
		for (int vnID = 0; vnID < numberOfVirtualNodes; vnID++) {
			VirtualNode virtualNode = new VirtualNode(vnID, Node.generateRandomComputationalSpeed());
			virtualNode.setRequestedFunction(1 + (int) (Math.random() * 3));
			virtualNodes.add(virtualNode);
		}

		// ------------------------ Map source node and destination node
		// randomly ------------------------ //

		int originMapID = (int) (Math.random() * type.getNumberOfPhysicalNodes());
		int destinationMapID = originMapID;
		while (destinationMapID == originMapID)
			destinationMapID = (int) (Math.random() * type.getNumberOfPhysicalNodes());

		virtualNodes.get(0).setMap(originMapID);
		virtualNodes.get(numberOfVirtualNodes - 1).setMap(destinationMapID);

		// ----------------------------------- Create virtual links
		// ------------------------------------- //

		for (int virtualNode = 0; virtualNode < numberOfVirtualLinks; virtualNode++)
			virtualLinks.add(new VirtualLink(new Pair<Integer, Integer>(virtualNodes.get(virtualNode).getID(),
					virtualNodes.get(virtualNode + 1).getID()), Link.generateRandomBandwidth()));
		
		Random r = new Random();
		start = r.nextInt(maxSecond);
		int timeRemain = maxSecond - start;
		duration = r.nextInt((timeRemain - 1) + 1) + 1;
	}

	/**
	 * Gets the number of virtual nodes in this request.
	 * 
	 * @return integer representing the number of virtual nodes in the request
	 */
	public int getNumberOfVirtualNodes() {
		return numberOfVirtualNodes;
	}

	/**
	 * Gets the number of virtual links in the request.
	 * 
	 * @return integer representing the number of virtual links in the request
	 */
	public int getNumberOfVirtualLinks() {
		return numberOfVirtualLinks;
	}
	
	public int getStart() {
		return start;
	}
	
	public int getDuration() {
		return duration;
	}

	/**
	 * Get the list of virtual nodes in this virtual request.
	 * 
	 * @return list of virtual nodes
	 */
	public List<VirtualNode> getVirtualNodes() {
		return virtualNodes;
	}

	/**
	 * Get the list of virtual link in this virtual request.
	 * 
	 * @return list of virtual links
	 */
	public List<VirtualLink> getVirtualLinks() {
		return virtualLinks;
	}

	/**
	 * Blocks virtual request
	 */
	public void block() {
		blocked = true;
	}

	/**
	 * Unblocks virtual request
	 */
	public void unblock() {
		blocked = false;
	}

	/**
	 * Returns whether virtual request is blocked or not
	 * 
	 * @return true or false
	 */
	public boolean isBlocked() {
		return blocked;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("Virtual Node Request Information: \n");
		sb.append("--------------------------------- \n");

		for (VirtualNode vn : virtualNodes)
			sb.append(vn + "\n");

		sb.append("\nVirtual Link Request Information: \n");
		sb.append("--------------------------------- \n");

		Path p = new Path(virtualNodes.get(0).getMapID());
		for (VirtualLink vl : virtualLinks) {
			p.append(vl.getLinkMapping());
			sb.append(vl + "\n");
		}

		sb.append("\n" + p);

		return sb.toString();
	}
}
