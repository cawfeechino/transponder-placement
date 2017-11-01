package models;

public class NodeEditor {
	int id;
	double lattitude;
	double longitude;
	
	public NodeEditor(int id, double lattitude, double longitude) {
		this.id = id;
		this.lattitude = lattitude;
		this.longitude = longitude;
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
	
}
