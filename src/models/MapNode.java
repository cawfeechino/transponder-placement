package models;

import java.text.DecimalFormat;

import com.lynden.gmapsfx.javascript.object.LatLong;


public class MapNode {
	static int numberOfEntries = 0;
	private int id;
	private double latitude;
	private double longitude;
	private LatLong start;
	private LatLong end;
	private double distance;
	
	private DecimalFormat format = new DecimalFormat("#0.0000");
	
	public MapNode(LatLong start, LatLong end, double distance) {
		super();
		this.start = start;
		this.end = end;
		this.distance = Double.parseDouble(format.format(distance/1000));
	}

	public MapNode(double latitude, double longitude) {
		super();
		
		this.latitude = Double.parseDouble(format.format(latitude));
		this.longitude = Double.parseDouble(format.format(longitude));
		this.id = ++numberOfEntries;
	}

	public LatLong getStart() {
		return start;
	}

	public LatLong getEnd() {
		return end;
	}

	public double getDistance() {
		return distance;
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

