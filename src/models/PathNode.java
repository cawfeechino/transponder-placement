package models;

public class PathNode {

	private int nodeID;
	private PathNode next = null;
	
	public PathNode(int nodeID){
		this.nodeID = nodeID;
	}
	
	public void setNext(PathNode next){
		this.next = next;
	}
	
	public int getNodeID(){
		return nodeID;
	}
	
	public PathNode next(){
		return next;
	}
	
	public boolean hasNext(){
		if(next == null) return false;
		else return true;
	}
	
}
