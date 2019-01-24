/* TODO convention is degrees N and degrees E ... change to degrees E
 * TODO extend android Location
 */

package edu.calit2.ivl.campus;

import android.location.Location;


/**
 * Wrapper for GPS location. Includes static support for base latitude and
 * base longitude, which are the origin of a Cartesian plane
 * 
 */
public class GPSLocation {

	
	private double _latitude,_longitude; // radians N, radians W
	private double _distance, _bearing; // meters, radians E of N
	
	/**
	 * Location instantiated to origin by default
	 */
	public GPSLocation() {
		_latitude = BASE_LAT;
		_longitude = BASE_LONG;
		
		_distance = distance();
		_bearing = bearing();
	}

	/**
	 * @param latitude degrees N
	 * @param longitude degrees W
	 */
	public GPSLocation(double latitude, double longitude) {
		latitude(latitude);
		longitude(longitude);
		
		_distance = distance();
		_bearing = bearing();
	}
	
	/**
	 * 
	 * @return radians N
	 */
	
	double latitude() {
		return _latitude;
	}
	

	/**
	 * 
	 * @return radians W
	 */
	double longitude() {
		return _longitude;
	}
	
	/**
	 * 
	 * @param latitude degrees N
	 */
	void latitude(double latitude) {
		_latitude = Math.toRadians(latitude);
	}
	
	/**
	 * 
	 * @param longitude degrees W
	 */
	void longitude(double longitude) {
		_longitude = Math.toRadians(longitude);
	}
	
	/**
	 * 
	 * @return meters
	 */
	public double distance() {
		double dLat = _latitude - BASE_LAT;
		double dLong = _longitude - BASE_LONG;
		
		double A = Math.pow( Math.sin(dLat / 2), 2 ) + Math.cos(_latitude) * Math.cos(BASE_LAT) * Math.pow( Math.sin( dLong / 2), 2 );
		double C = 2 * Math.atan2( Math.sqrt(A), Math.sqrt(1-A) );
		
		return C * EARTH_RADIUS;
	}
	
	
	/**
	 * 
	 * @return radians E of N from reference point
	 */
	public double bearing() {
		
		double dLat = BASE_LAT - _latitude;
		double dLong = BASE_LONG - _longitude;
		
		double y = Math.sin( dLong )* Math.cos( _latitude );
		double x = Math.cos( BASE_LAT ) * Math.sin( _latitude ) - 
				   Math.sin( BASE_LAT ) * Math.cos( _latitude ) * Math.cos( (dLong) );
		
		double ans =  Math.atan2(y,x);
		ans += TWO_PI ;
		return ans > TWO_PI ? ans - TWO_PI : ans;
	}
	
	/**
	 * 
	 * @return meters
	 */
	public float positionX() {
		return (float) (_distance * Math.cos(_bearing));
	}
		
	/**
	 * 
	 * @return meters
	 */
	public float positionZ() {
		return (float) (_distance * Math.sin(_bearing));
	}
	
	
	public void update(Location location) {
		latitude(location.getLatitude());
		longitude(-location.getLongitude());
		_distance = distance();
		_bearing = bearing();
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////	
	////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// STATIC ////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////
	

	private static final int TWO_MINUTES = 1000 * 60 * 2; // ms
	private static final double TWO_PI = 2 * Math.PI; // radians
	private static double EARTH_RADIUS =  6378100; // meters
	static final double BASE_LAT = Administration.CAMINO_TRANQUILO_LAT, BASE_LONG = Administration.CAMINO_TRANQUILO_LONG; // camino tranquilo

	
	/**
	 * 
	 * @param degrees
	 * @param minutes
	 * @param seconds
	 * @return degrees
	 */
	public static double timeToDecimal(int degrees, int minutes, double seconds) {
		return degrees + minutes/60.0 + seconds/3600;
	}
	
	/** Determines whether one Location reading is better than the current Location fix
	 * @param location  The new Location that you want to evaluate
	 * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	 */
	static boolean isBetterLocation(Location location, Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private static boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}
	
	
	
	
	
	
	
//	/**
//	 * ??
//	 * @param latitude degrees N
//	 * @param longitude degrees W
//	 * @return meters
//	 */
//	public static double distance(double latitude, double longitude) {
//		double dLat = (Math.toRadians(latitude) - BASE_LAT);
//		double dLong = (Math.toRadians(longitude) - BASE_LONG);
//		
//		double A = Math.pow( Math.sin(dLat / 2), 2 ) + Math.cos((latitude)) * Math.cos((BASE_LAT)) * Math.pow( Math.sin( dLong / 2), 2 );
//		double C = 2 * Math.atan2( Math.sqrt(A), Math.sqrt(1-A) );
//		
//		return C * EARTH_RADIUS;
//	}
	
	
//	/**
//	 * ??
//	 * @param latitude degrees N
//	 * @param longitude degrees W
//	 * @return in radians angle east of north from reference point
//	 */
//	public static double bearing(double latitude, double longitude) {
//		
//		double dLat = BASE_LAT - latitude;
//		double dLong = BASE_LONG - longitude;
//		
//		double y = Math.sin( (dLong) )* Math.cos( latitude );
//		double x = Math.cos( (BASE_LAT) ) * Math.sin( latitude ) - 
//				   Math.sin( (BASE_LAT) ) * Math.cos( latitude ) * Math.cos( (dLong) );
//		
//		double ans =   Math.atan2(y,x );
//		ans += TWO_PI ;
//		return ans > TWO_PI ? ans - TWO_PI : ans;
//	}
	
}

