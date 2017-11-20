package models;

import java.text.DecimalFormat;

public class MapNode {
	static int numberOfEntries = 0;
	private int id;
	private double lattitude;
	private double longitude;
	
	private DecimalFormat format = new DecimalFormat("#0.0000");
	
	public MapNode(double lattitude, double longitude) {
		super();
		
		this.lattitude = Double.parseDouble(format.format(lattitude));
		this.longitude = Double.parseDouble(format.format(longitude));
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
