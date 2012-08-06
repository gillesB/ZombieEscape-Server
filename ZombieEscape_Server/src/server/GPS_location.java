package server;

public class GPS_location {	
	
	public GPS_location() {
		super();
	}
	public GPS_location(double longitude, double latitude) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
	}
	public double longitude = 0;
	public double latitude = 0;
	
	public double getDistance(GPS_location loc){
		//distance between two points: sqrt((x2-x1)^2 + (y2-y1)^2 )
		double x2 = Math.pow(loc.longitude - this.longitude, 2);
		double y2 = Math.pow(loc.latitude - this.latitude, 2);
		return Math.sqrt(x2 + y2);		
	}

}
