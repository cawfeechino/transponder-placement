package utilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import models.Pair;
import models.Path;
import models.PathLink;
import models.PathNode;
import models.PhysicalLink;
import models.Topology;

public class KShortestPath {

	private int originID;
	private int destinationID;
	private Topology topology;

	

	public KShortestPath(Topology topology, int originID, int destinationID) {
		this.originID = originID;
		this.destinationID = destinationID;
		this.topology = topology;

	}
	public ArrayList<Path> getShortestPath(int K){
		ArrayList<Path> ksp = new ArrayList<Path>();
		ArrayList<Pair<ArrayList<PathLink>, Integer>> candidates = new ArrayList<Pair<ArrayList<PathLink>, Integer>>();
	//	try{
			DijkstraShortestPath dsp = new DijkstraShortestPath(topology,originID,destinationID);
			Pair<ArrayList<PathLink>, Integer> kthPath = new Pair<ArrayList<PathLink>, Integer>(null, null);
			Path pTest = dsp.getShortestPath();
			
			
			ksp.add(pTest);
			for(int k=1; k<K; k++){
				
			//	System.out.println("current k: " + k);
			//	System.out.println("ksp before starting: " + ksp.toString());
				
				Path previousPath = ksp.get(k-1);
				
			//	System.out.println("previous path: " + previousPath.toString());
				
				if(previousPath.getLinks().isEmpty()) previousPath.generateLinks();
				
				for(int i=0; i<previousPath.getLinks().size(); i++){
					ArrayList<PathLink> removedEdges = new ArrayList<PathLink>();
					ArrayList<Integer> removedNodes = new ArrayList<Integer>();
					Integer spurNode = previousPath.getLinks().get(i).getFromID();
					ArrayList<PathLink> rootPath = previousPath.cloneTo(i);
					for(Path p : ksp){
						ArrayList<PathLink> stub = p.cloneTo(i);
						
				//		System.out.println("Does " + rootPath.toString() + " equal " + stub.toString() + " ?");
				//		System.out.println(rootPath.equals(stub));
					
						if(rootPath.equals(stub)){
							PathLink re = p.getLinks().get(i);
							removedEdges.add(re);
						}
					}
					
					Set<PathLink> temp = new HashSet<>();
					temp.addAll(removedEdges);
					removedEdges.clear();
					removedEdges.addAll(temp);
					
					removeAdjacentNodes(removedEdges);
					for(PathLink rootPathEdge : rootPath){
						Integer rn = rootPathEdge.getFromID();
						if(rn != spurNode){
							removedNodes.add(rn);
						}
					}
					removeNodes(removedNodes);
					
				//	System.out.println("removed Edges: " + removedEdges.toString());
				//	System.out.println("removed Nodes: " + removedNodes.toString());
				//	System.out.println("rootPath: " + rootPath.toString());
					if(topology.getNodes().get(spurNode).getAdjacentNodes().isEmpty()){
						restoreNodes(removedNodes);
						restoreAdjacentNodes();
					//	System.out.println("triggered");
						continue;
					}
					
					DijkstraShortestPath dspT = new DijkstraShortestPath(topology, spurNode, destinationID);
					Path spurPath = dspT.getShortestPath();
					if(spurPath.getLinks().isEmpty()) spurPath.generateLinks();
					if(!spurPath.getLinks().isEmpty()){
						ArrayList<PathLink> totalPath = new ArrayList<PathLink>();
						for(int p=0; p<rootPath.size(); p++){
							totalPath.add(rootPath.get(p));
						}
						
					//	System.out.println("rootPath: " + rootPath.toString());
					//	System.out.println("spurPath: " + spurPath.toString());
					//	System.out.println("spurPath Links: " + spurPath.getLinks());
						
						totalPath.addAll(spurPath.getLinks());
						
					//	System.out.println("after totalPath allAll: " + totalPath);
						
						boolean candidateExists = false;
						for(Pair<ArrayList<PathLink>, Integer> c : candidates){
							if(c.first().equals(totalPath)){
								candidateExists = true;
								break;
							}
						}
						if(!candidateExists){
							
						//	System.out.println("totalPath before add: " + totalPath);
							
							candidates.add(new Pair<ArrayList<PathLink>, Integer>(totalPath, getDistance(totalPath)));
						}
					}
					restoreNodes(removedNodes);
					restoreAdjacentNodes();
				}
				
			//	System.out.println("candidates: " + candidates.toString());
				
				if(candidates.isEmpty()) break;
				candidates = sortCandidates(candidates);
				boolean isNewPath;
				do{
					if(candidates.isEmpty()){
						kthPath = null;
						break;
					}
					kthPath = candidates.get(0);
					candidates.remove(0);
					isNewPath = true;
					if(kthPath != null){
						for(Path p : ksp){
							if(p.getLinks().equals(kthPath.first())){
								isNewPath = false;
								break;
							}
						}
					}
				}while(!isNewPath);
				
				
			//	System.out.println(kthPath == null);
				
				if(kthPath == null) break;
				
			//	System.out.println("kthPath: " + kthPath.toString());
				
				Path tempPath = createPath(kthPath.first(),kthPath.second());
				
			//	System.out.println("tempPath: " + tempPath.toString());
				
				ksp.add(tempPath);
				
			//	System.out.println("ksp after adding new path: " + ksp.toString());
				
			}
	//	} catch(Exception e){
	//		System.out.println(e);
	//		e.printStackTrace();
	//	}
		return ksp;
	}
	
	private int getDistance(ArrayList<PathLink> pathlinks) {
		int distance = 0;
		for (int y = 0; y < pathlinks.size(); y++) {
			
			Pair<Integer,Integer> tempLink = new Pair<Integer,Integer>(pathlinks.get(y).getFromID(),pathlinks.get(y).getToID());
			Pair<Integer,Integer> tempLinkFlip = new Pair<Integer,Integer>(pathlinks.get(y).getToID(),pathlinks.get(y).getFromID()); 

			for (PhysicalLink l : topology.getLinks()) {
				if(l.getEndNodes().equals(tempLink) || l.getEndNodes().equals(tempLinkFlip)) distance += l.getLinkDistance();
			}
		}
		return distance;
	}
	
	private ArrayList<Pair<ArrayList<PathLink>, Integer>> sortCandidates(ArrayList<Pair<ArrayList<PathLink>, Integer>> candidates){
		Pair<ArrayList<PathLink>, Integer> temp  = new Pair<ArrayList<PathLink>, Integer>(null, null);
		for(int x = 1; x < candidates.size(); x++){
			temp = candidates.get(x);
			int y;
			for(y = x-1; (y >= 0) && candidates.get(y).second() > temp.second(); y--){
				candidates.set(y+1, candidates.get(y));
			}
			candidates.set(y+1, temp);
		}
		return candidates;
	}

	private Path createPath(ArrayList<PathLink> pathlinks, int distance) {

		Path tempPath = new Path(originID);
		int nextID;
		PathNode next = null;
		PathNode current = new PathNode(destinationID);

		for (int x = pathlinks.size(); x > 0; x--) {
			current.setNext(next);
			next = current;
			nextID = pathlinks.get(x - 1).getFromID();

			if (nextID == originID) {
				tempPath.getStart().setNext(next);
				break;
			}

			current = new PathNode(nextID);
		}

		// Moving backwards

		tempPath.setPathDistance(distance);
		return tempPath;
	}
	
	
	private void removeAdjacentNodes(ArrayList<PathLink> removedLinks){
		for(int x = 0; x < removedLinks.size(); x++){
			topology.getNodes().get(removedLinks.get(x).getFromID()).removeAdjacentNode(removedLinks.get(x).getToID());
		}
	}
	
	private void removeNodes(ArrayList<Integer> removedNodes){
		for(int x = 0; x < removedNodes.size(); x++){
			
			for (Map.Entry<Integer,Integer> entry : topology.getNodes().get(removedNodes.get(x)).getAdjacentNodes().entrySet()){ 
				topology.getNodes().get(entry.getKey()).removeAdjacentNode(removedNodes.get(x));
			}
			topology.removeNode(removedNodes.get(x));
		}
	}
	
	/*private void restoreAdjacentNodes(ArrayList<PathLink> removedLinks){
		for(int x = 0; x < removedLinks.size(); x++){
			topology.getNodes().get(removedLinks.get(x).getFromID()).resetAdjacentNodes();
			topology.getNodes().get(removedLinks.get(x).getToID()).resetAdjacentNodes();
		}
		
	}*/
	
	private void restoreAdjacentNodes(){
		
	//	System.out.println("retoreAdjacentNodes nodes: " + topology.getNodes().toString());
	
		for(int i = 0; i < topology.getType().getNumberOfPhysicalNodes(); i++){
			
		//	System.out.println("Node Adj. Node: " + i + " " + topology.getNodes().get(i).getAdjacentNodes().toString());
		//	System.out.println("Node Rm. Adj. Node: " + i + " " + topology.getNodes().get(i).getRemovedAdjacentNodes().toString());
			
			topology.getNodes().get(i).resetAdjacentNodesAlpha();
		}
	}
	
	private void restoreNodes(ArrayList<Integer> removedNodes){
		topology.resetNodes();
	}
	
}
