package models;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

public class Path {

	private int pathDistance;
	private int throughput;

	private ArrayList<PathLink> pathlinks;

	private PathNode start;

	public Path(int originNodeID) {
		start = new PathNode(originNodeID);
		this.pathlinks = new ArrayList<PathLink>();
	}

	public int getThroughput() {
		return throughput;
	}

	public void setThroughput(int throughput) {
		this.throughput = throughput;
	}

	public int getPathDistance() {
		return pathDistance;
	}

	public void setPathDistance(int pathDistance) {
		this.pathDistance = pathDistance;
	}

	public PathNode getStart() {
		return start;
	}

	public PathNode getNode(int i) {
		PathNode current = start;

		int index = 0;
		while (index != i) {
			current = current.next();
			index++;
		}

		return current;
	}

	public boolean equals(Path p) {
		PathNode current = start;
		PathNode currentCompare = p.getStart();

		if (this.getNumberOfHops() != p.getNumberOfHops())
			return false;

		while (current.hasNext() && currentCompare.hasNext()) {
			if (current.getNodeID() != currentCompare.getNodeID())
				return false;
			current = current.next();
			currentCompare = currentCompare.next();
		}

		return true;
	}

	public int getNumberOfHops() {
		PathNode current = start;

		int hop = 0;
		while (current.hasNext()) {
			current = current.next();
			hop++;
		}

		return hop;
	}

	public Path getNodes(int e) {
		Path subPath = new Path(start.getNodeID());
		if (e == 0)
			return subPath;

		PathNode subCurrent = subPath.getStart();
		PathNode current = start.next();

		int index = 1;
		while (index != e + 1) {
			subCurrent.setNext(new PathNode(current.getNodeID()));
			subCurrent = subCurrent.next();
			current = current.next();
			index++;
		}

		return subPath;

	}

	public void generateLinks() {
		PathNode current = start.next();
		pathlinks.add(new PathLink(start.getNodeID(), current.getNodeID()));
		while (current.hasNext()) {
			if (!current.hasNext())
				break;

			pathlinks.add(new PathLink(current.getNodeID(), current.next().getNodeID()));

			current = current.next();
		}
	}

	public ArrayList<PathLink> getLinks() {
		return pathlinks;
	}

	public void add(PathLink edge) {
		pathlinks.add(edge);
	}

	public ArrayList<PathLink> cloneTo(int i) {
		ArrayList<PathLink> pathlinks = new ArrayList<PathLink>();
		int l = this.pathlinks.size();
		if (i > l)
			i = l;

		// for (Edge edge : this.edges.subList(0,i)) {
		for (int j = 0; j < i; j++) {
			pathlinks.add(this.pathlinks.get(j));
		}

		return new ArrayList<PathLink>(pathlinks);
	}

	public void append(Path path) {
		PathNode current = start;

		while (current.hasNext())
			current = current.next();

		if (current.getNodeID() == path.getStart().getNodeID())
			current.setNext(path.getStart().next());
		else
			current.setNext(path.getStart());

		pathDistance += path.getPathDistance();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("PATH: ");

		PathNode current = start;

		do {
			sb.append(current.getNodeID() + ((current.hasNext()) ? " --> " : ""));
			current = current.next();
		} while (current != null);

		sb.append("\nPath Distance: " + pathDistance);
		// sb.append("\nThroughput: " + throughput + "\n");

		return sb.toString();
	}

}
