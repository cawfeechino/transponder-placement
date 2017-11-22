package models;

import java.text.DecimalFormat;

public class MapNode {
	static int numberOfEntries = 0;
	private int id;
	private double latitude;
	private double longitude;
	
	private DecimalFormat format = new DecimalFormat("#0.0000");
	
	public MapNode(double latitude, double longitude) {
		super();
		
		this.latitude = Double.parseDouble(format.format(latitude));
		this.longitude = Double.parseDouble(format.format(longitude));
		this.id = ++numberOfEntries;
	}


	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double lattitude) {
		this.latitude = lattitude;
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
