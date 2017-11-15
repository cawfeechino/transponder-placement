package models;

public class nodeEditor {
	static int numberOfEntries = 0;
	private int id;
	private double lattitude;
	private double longitude;
	
	public nodeEditor(double lattitude, double longitude) {
		super();
		this.lattitude = lattitude;
		this.longitude = longitude;
		this.id = ++numberOfEntries;
	}

	public double getLattitude() {
		return lattitude;
	}

	public void setLattitude(double lattitude) {
		this.lattitude = lattitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public int getId() {
		return id;
	}
}
