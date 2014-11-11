package com.adamkis.ubimetChallenge.communication;

import com.adamkis.ubimetChallenge.utils.UtilsUbimetChallenge;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;


public class GeoLocationMagager {

	private LocationManager locationManager;
	private Criteria criteria; 
	private boolean isListening = false;
	
    private boolean gps_enabled;
    private boolean network_enabled;

	private LocationListener gpsLocationListener;
	private LocationListener networkLocationListener;
	private Location networkLocation;
	private Location gpsLocation;
	
    private Location lastLocation = null;
	
	public <T extends Context & LocationHandlerInterface> GeoLocationMagager( final T caller ){

        criteria = new Criteria();
        locationManager = (LocationManager) caller.getSystemService(Context.LOCATION_SERVICE);
        
        try{gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){ ex.printStackTrace(); }
        try{network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){ ex.printStackTrace(); }
        
        gpsLocationListener = new LocationListener() {
        	
	        public void onLocationChanged(Location location) {
	        	gpsLocation = location;
	        	
	        	if ( caller != null ){
	        		caller.callBackLocationUpdated(location);
	        	}
	        	
	        	Log.d("UbiMet", "Location from GPS onLocationChanged: " + location.getLatitude() + "," + location.getLongitude() );
	        }
	        public void onProviderEnabled(String p) {}
	        public void onProviderDisabled(String p) {}
	        public void onStatusChanged(String p, int status, Bundle extras) {}
	        
	    };
	    
        networkLocationListener = new LocationListener() {
        	
	        public void onLocationChanged(Location location) {
	        	networkLocation = location;
	        	
	        	if ( caller != null ){
	        		caller.callBackLocationUpdated(location);
	        	}
	        	
	        	Log.d("UbiMet", "Location from Network onLocationChanged: " + location.getLatitude() + "," + location.getLongitude() );
	        }
	        public void onProviderEnabled(String p) {}
	        public void onProviderDisabled(String p) {}
	        public void onStatusChanged(String p, int status, Bundle extras) {}
	        
	    };
	}
	
	public void startListening(){
		if (!isListening) {
			if (gps_enabled) {
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 0, 0,
						gpsLocationListener);
			}
			if (network_enabled) {
				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 0, 0,
						networkLocationListener);
			}
			isListening = true;
		}
	}
	
	public void stopListening(){
		if( isListening ){
			locationManager.removeUpdates(gpsLocationListener);
			locationManager.removeUpdates(networkLocationListener);
			isListening = false;
		}
	}
	
	public Location getLastLocation(){
		if (lastLocation != null)
			return lastLocation;
		else {
			return getLocation();
		}
	}
	
	private Location getLocation(){


    	networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    	gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

    	if( gpsLocation != null ){
    		if( networkLocation != null && UtilsUbimetChallenge.haversine(gpsLocation.getLatitude(), gpsLocation.getLongitude(), networkLocation.getLatitude(), networkLocation.getLongitude()) > 1 ){
    			lastLocation = networkLocation;
    			return networkLocation;
    		}
    		else{
	    		lastLocation = gpsLocation;
	    		gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	        	Log.d("UbiMet", "Location return from GPS: " + gpsLocation.getLatitude() + "," + gpsLocation.getLongitude() );
	    		return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    		}
    	}
    	else if( networkLocation != null ){
        	Log.d("UbiMet", "Location return Network: " + networkLocation.getLatitude() + "," + networkLocation.getLongitude() );
    		lastLocation = networkLocation;
    		return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    	}
	    else{
        	Log.d("UbiMet", "Location return else branch" );
		    String bestProvider = locationManager.getBestProvider(criteria, false);
		    Location location = locationManager.getLastKnownLocation(bestProvider);
		    lastLocation = location;
		    
		    // This can be null
		    return location;
	    }


	}
	
	
}
