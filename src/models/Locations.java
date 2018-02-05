package models;

import java.util.ArrayList;

import com.lynden.gmapsfx.javascript.object.LatLong;

public class Locations {
	public ArrayList<LatLong> locations() {
		LatLong sd = new LatLong(32.7157, -117.1611);
		LatLong palo = new LatLong(37.4419, -122.143);
		LatLong seattle = new LatLong(47.6062, -122.3321);
		LatLong slc = new LatLong(40.7608, -111.8910);
		LatLong boulder = new LatLong(40.0150, -105.2705);
		LatLong houston = new LatLong(29.7604, -95.3698);
		LatLong lincoln = new LatLong(40.8258, -96.6852);
		LatLong champaign = new LatLong(40.1164, -88.2434);
		LatLong atlanta = new LatLong(33.749, -84.388);
		LatLong pittsburgh = new LatLong(40.4406, -79.9959);
		LatLong annArbor = new LatLong(42.2808, -83.7430);
		LatLong ithaca = new LatLong(43.444, -76.5019);
		LatLong collegePk = new LatLong(38.9897, -76.9378);
		LatLong princeton = new LatLong(40.3487, -74.659);
		
		ArrayList<LatLong> locations = new ArrayList<>();
		locations.add(sd);
		locations.add(palo);
		locations.add(seattle);
		locations.add(slc);
		locations.add(boulder);
		locations.add(houston);
		locations.add(lincoln);
		locations.add(champaign);
		locations.add(atlanta);
		locations.add(pittsburgh);
		locations.add(annArbor);
		locations.add(ithaca);
		locations.add(collegePk);
		locations.add(princeton);
		return locations;
	}
}
