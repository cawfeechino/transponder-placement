package models;


public class NSFLocation {

    private String locationName;
    private double [] location;
    private double longitude;
    private double latitude;
    private double distance;

    public NSFLocation(String locationName){
        this.locationName = locationName;
    }
    public String getLocationName(){
        return locationName;
    }
    public double [] getLocation(){
        return location;
    }
    public double getLongitude() {
        return longitude;
    }
    public double getLatitude(){
        return latitude;
    }
    public void setDistance(double longitude, double latitude, double long2, double lat2){
        int r = 6371; // average radius of the earth in km
        double dLat = Math.toRadians(lat2 - latitude);
        double dLon = Math.toRadians(long2 - longitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = r * c;
        distance = d;
    }
    public double getDistance(){
        return distance;
    }
}

