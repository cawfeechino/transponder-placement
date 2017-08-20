package utilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import models.Pair;
import models.Path;
import models.PathLink;
import models.PathNode;
import models.PhysicalNode;
import models.Topology;

public class DijkstraShortestPath {

	private Topology topology;
	
	private Integer originID;
	private Integer destinationID;
	
	private boolean[] done;
	private List<Pair<Integer,Integer>> iterationTracker;
	
	private Path path;
	
	public DijkstraShortestPath(Topology topology, int originID, int destinationID){
		
		this.topology = topology;
		this.originID = originID;
		this.destinationID = destinationID;
		
		done = new boolean[this.topology.getType().getNumberOfPhysicalNodes()];
		iterationTracker = new ArrayList<>();
		
		path = new Path(this.originID);
	}
	
	public Path getShortestPath(){
		init();
		
		findShortestPath(originID);
		
		for(Entry<Integer, PhysicalNode> entry : topology.getNodes().entrySet()){
			entry.getValue().enable();
		}
		path.generateLinks();
		return path;
	}
	
	public Path getDisjointShortestPath(){
		//if(path.getNumberOfHops() == 0) getShortestPath();
		
		PathNode current = path.getStart().next();
		while(current.hasNext()){
			if(!current.hasNext()) break;
			
			for (Map.Entry<Integer,Integer> entry : topology.getNodes().get(current.getNodeID()).getAdjacentNodes().entrySet()) 
				topology.getNodes().get(entry.getKey()).removeAdjacentNode(current.getNodeID());
			
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
	
	public static Path getDisjointShortestPath(Topology topology, Path path, int originID, int destinationID){
		//if(path.getNumberOfHops() == 0) getShortestPath();

		PathNode current = path.getStart().next();
		while(current.hasNext()){
			if(!current.hasNext()) break;
			
			for (Map.Entry<Integer,Integer> entry : topology.getNodes().get(current.getNodeID()).getAdjacentNodes().entrySet()) 
				topology.getNodes().get(entry.getKey()).removeAdjacentNode(current.getNodeID());
			
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
	
	private void init(){
		for (int i = 0; i < topology.getType().getNumberOfPhysicalNodes(); i++)
			iterationTracker.add((i == originID)? new Pair<Integer,Integer>(0,0) : new Pair<Integer, Integer>(null, null));

	}
	
	private void findShortestPath(int currentNodeID){
		if(currentNodeID == -1){
			setPath();
			return;
		}
		
	//	System.out.println("currentNodeID: " + currentNodeID);
	//	System.out.println("ajacentNodesByID: " + topology.getNodes().get(currentNodeID).getAdjacentNodes());
		
		Set<Map.Entry<Integer,Integer>> adjacentNodes = topology.getNodes().get(currentNodeID).getAdjacentNodes().entrySet();
	//	Set<Map.Entry<Integer,Integer>> removedAdjacentNodes = topology.getNodes().get(currentNodeID).getRemovedAdjacentNodes().entrySet();
				
		int currentAdjacentNodeID;
		
	//	System.out.println("adjacentNodes: " + adjacentNodes.toString());
	//	System.out.println("removedAdjacentNodes: " + removedAdjacentNodes.toString());
		
		for(Map.Entry<Integer, Integer> entry : adjacentNodes){
			currentAdjacentNodeID = entry.getKey();
			
		//	System.out.println("currentAdjacentNodeID: " + currentAdjacentNodeID);
			
			if(done[currentAdjacentNodeID]) continue;
		//	if(entry.getValue() == null) continue;
			update(currentNodeID, entry);

		}
		
		done[currentNodeID] = true;
		
		findShortestPath(getNext());
		
	}
	
	private int getNext() {
		int min = Integer.MAX_VALUE;
		int id = -1;
		
		for (int i = 0; i < iterationTracker.size();i++){
			Pair<Integer,Integer> current = iterationTracker.get(i); 
			if(current.second() == null) continue;
			
			if(current.second() < min && !done[i]){
				min = current.second();
				id = i;
			}
		}
		
		return id;
	}

	private void update(int currentNodeID, Entry<Integer, Integer> entry) {
		
		Integer currentNodeDistance = iterationTracker.get(currentNodeID).second();
		Integer adjacentNodeDistance = entry.getValue();
		
	//	System.out.println("currentNodeDistance: " + currentNodeDistance);
	//	System.out.println("adjacentNodeDistance: " + adjacentNodeDistance);
		
		if (iterationTracker.get(entry.getKey()).second() != null){
			if(adjacentNodeDistance + currentNodeDistance < iterationTracker.get(entry.getKey()).second())
				iterationTracker.set(entry.getKey(), new Pair<Integer,Integer>(currentNodeID, currentNodeDistance + adjacentNodeDistance));
		}
		else{
			iterationTracker.set(entry.getKey(), new Pair<Integer,Integer>(currentNodeID, currentNodeDistance + adjacentNodeDistance));
		}
	}

	private void setPath(){
		
		Integer nextID;
		PathNode next = null;
		PathNode current = new PathNode(destinationID);
		
		// Moving backwards
		while(true){
			current.setNext(next);
			next = current;
			
			nextID = iterationTracker.get(current.getNodeID()).first();
		//	if(nextID == null) continue; 
			
		//	System.out.println("iT: " + iterationTracker.toString());
		//	System.out.println("nextID: " + nextID);
		//	System.out.println("current iT: " + iterationTracker.get(current.getNodeID()));
			
			if(nextID == originID){
				path.getStart().setNext(next);
				break;
			}
			
			current = new PathNode(nextID);
		}
		
		// Set path throughput
//		int throughput = Integer.MAX_VALUE;
//		
//		current = path.getStart();
//		next = current.next();
//		
//		while(next.hasNext()){
//			PhysicalLink link = topology.getLink(current.getNodeID(), next.getNodeID());
//			if(link.getbandwidthAvailability() < throughput)
//				throughput = link.getbandwidthAvailability();
//			current = next;
//			next = current.next();
//		}
		
		path.setPathDistance(iterationTracker.get(destinationID).second());
//		path.setThroughput(throughput);
	}
	
}
