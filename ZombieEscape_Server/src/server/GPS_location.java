package server;

/**
 * Entspricht einer Position auf der Erde, ausgedrückt in Längengrad und
 * Breitengrad.
 * 
 * 
 */
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

	/**
	 * Ermittelt die Distanz in Kilometer zwischem <code>this</code> und dem
	 * Eingabewert <code>loc</code>.
	 * 
	 * @param loc
	 *            die zweite Position zum ermitteln der Distanz
	 * @return Distanz in km
	 */
	public double getDistanceTo_km(GPS_location loc) {
		if (loc == null) {
			return 0;
		}
		return haversine_km(this.latitude, this.longitude, loc.latitude, loc.longitude);
	}

	/**
	 * Ermittelt die Distanz in Kilometer zwischem dem Eingabewert
	 * <code>loc1</code> und dem Eingabewert <code>loc2</code>.
	 * 
	 * @param loc1
	 *            die erste Position zum ermitteln der Distanz
	 * @param loc2
	 *            die zweite Position zum ermitteln der Distanz
	 * @return Distanz in km
	 */
	public static double getDistanceFromTo_km(GPS_location loc1, GPS_location loc2) {
		return haversine_km(loc1.latitude, loc1.longitude, loc2.latitude, loc2.longitude);

	}

	/**
	 * Berechnung der Distanz zwischen 2 GPS-Koordinaten. Gefunden auf
	 * http://stackoverflow
	 * .com/questions/365826/calculate-distance-between-2-gps-coordinates
	 * (04.10.2012)
	 * 
	 * @param lat1 Breitengrad der ersten Position
	 * @param long1 Längengrad der ersten Position
	 * @param lat2 Breitengrad der zweiten Position
	 * @param long2 Längengrad der zweiten Position
	 * @return Distanz in km
	 */
	private static double haversine_km(double lat1, double long1, double lat2, double long2) {
		double toRad = 0.0174532925199433; // pi / 180
		double dlong = (long2 - long1) * toRad;
		double dlat = (lat2 - lat1) * toRad;
		double a = Math.pow(Math.sin(dlat / 2.0), 2) + Math.cos(lat1 * toRad) * Math.cos(lat2 * toRad) * Math.pow(Math.sin(dlong / 2.0), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = 6367 * c;

		return d;
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

	@Override
	public String toString() {
		return "GPS_location [latitude=" + latitude + ", longitude=" + longitude + "]";
	}

	/**
	 * Erstellt ein neues <code>GPS_location</code>-Objekt mit den gleichen
	 * Werten wie <code>this</code>.
	 * 
	 * @return ein neues <code>GPS_location</code>-Objekt
	 */
	public GPS_location copy() {
		return new GPS_location(this.latitude, this.longitude);
	}

}
