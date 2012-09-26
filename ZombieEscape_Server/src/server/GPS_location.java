package server;

public class GPS_location {

	public GPS_location() {
		super();
	}

	public GPS_location(double latitude, double longitude) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public double longitude = 0;
	public double latitude = 0;

	public double getDistanceTo_km(GPS_location loc) {
		// distance between two points
		return haversine_km(this.latitude, this.longitude, loc.latitude, loc.longitude);
	}
	
	public static double getDistanceFromTo_km(GPS_location loc1, GPS_location loc2){
		return haversine_km(loc1.latitude, loc1.longitude, loc2.latitude, loc2.longitude);
		
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof GPS_location) {
			GPS_location loc = (GPS_location) obj;
			if (this.latitude == loc.latitude && this.longitude == loc.longitude) {
				return true;
			}
		} else {
			return false;
		}
		return super.equals(obj);
	}

	private static double haversine_km(double lat1, double long1, double lat2, double long2) {
		double toRad = 0.0174532925199433; // pi / 180
		double dlong = (long2 - long1) * toRad;
		double dlat = (lat2 - lat1) * toRad;
		double a = Math.pow(Math.sin(dlat / 2.0), 2) + Math.cos(lat1 * toRad) * Math.cos(lat2 * toRad)
				* Math.pow(Math.sin(dlong / 2.0), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = 6367 * c;

		return d;
	}

}
