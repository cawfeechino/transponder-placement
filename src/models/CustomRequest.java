package models;

public class CustomRequest {
	private int start;
	private int destination;
	private int bandwidth;

	public CustomRequest(int start, int destination, int bandwidth) {
		this.start = start;
		this.destination = destination;
		this.bandwidth = bandwidth;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getDestination() {
		return destination;
	}

	public void setDestination(int destination) {
		this.destination = destination;
	}

	public int getBandwidth() {
		return bandwidth;
	}

	public void setBandwidth(int bandwidth) {
		this.bandwidth = bandwidth;
	}
}
