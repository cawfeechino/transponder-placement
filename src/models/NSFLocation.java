package models;

import com.lynden.gmapsfx.javascript.object.LatLong;

public class NSFLocation {

    private int id;
    private LatLong  location;
    private LatLong latitude;
    private double distance;

    public NSFLocation(String locationName){ this.id = id;

    }
    public int getId(){
        return id;
    }
    public LatLong getLocation(){
        return location;
    }
//    public void setDistance(){
//        int r = 6371; // average radius of the earth in km
//        double dLat = Math.toRadians(lat2 - latitude);
//        double dLon = Math.toRadians(long2 - longitude);
//        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
//                Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(lat2))
//                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//        double d = r * c;
//        distance = d;
//    }
    public double getDistance(){
        return distance;
    }
}

