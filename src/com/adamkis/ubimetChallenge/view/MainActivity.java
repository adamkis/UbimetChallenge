package com.adamkis.ubimetChallenge.view;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import com.adamkis.ubimetChallenge.communication.HttpCommunicationInterface;
import com.adamkis.ubimetChallenge.communication.HttpGetAsynchTask;
import com.adamkis.ubimetChallenge.communication.GeoLocationMagager;
import com.adamkis.ubimetChallenge.communication.LocationHandlerInterface;
import com.adamkis.ubimetChallenge.model.ConstantsUbimet;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity implements HttpCommunicationInterface, LocationHandlerInterface {

	private GeoLocationMagager geoLocationManager;
	private View loadingStatusView;
	private TextView currentLocationWeatherInfoTextView;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
	    overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
	    
	    currentLocationWeatherInfoTextView = (TextView)findViewById(R.id.currentLocationWeatherInfo);

    	showProgress(true);
	    getMyLocation();
	    
	}
	
    private void getMyLocation() {
		
        geoLocationManager = new GeoLocationMagager(this);
        Location location = geoLocationManager.getLastLocation();
        
        if ( location != null ){
    	    callForPinpointData( location );
        }
        else{
            geoLocationManager.startListening();
        }

		
	}

	private void callForPinpointData( Location location ) {
		
	    StringBuilder url = new StringBuilder();
	    url.append(ConstantsUbimet.UBIMET_HOST_URL);
	    url.append("/pinpoint-data?sets=basic_now");
	    try {
	    	url.append("&coordinates=");
//			url.append(URLEncoder.encode("19.04 47.49", "utf-8"));
	    	url.append(URLEncoder.encode(location.getLongitude() + " " + location.getLatitude(), "utf-8"));
	    	
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	    
	    HashMap<String, String> headers = new HashMap<String, String>();
	    headers.put("Authorization", ConstantsUbimet.UBIMET_PINPOINT_TOKEN);
	    headers.put("Accept", "application/json");
	    headers.put("Accept-Language", "en");
	    
	    HttpGetAsynchTask dlgg = new HttpGetAsynchTask(this, url.toString(), headers, "initial");
	    dlgg.execute();
		
	}

	@Override
    public void onResume() {
    	super.onResume();
  
	}

	
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
    }

	@Override
	public void callBackPost(String response, String mode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void callBackGet(String response, String mode) {
		
		Log.d("Ubimet", response);
		currentLocationWeatherInfoTextView.setText(response);
		showProgress(false);
		
	}

	@Override
	public void showProgress(boolean show) {

		if( loadingStatusView == null ){
			loadingStatusView = (View)findViewById(R.id.loading_status);
		}
		
		if( show ){
			loadingStatusView.setVisibility( View.VISIBLE );
		}
		else{
			loadingStatusView.setVisibility( View.GONE );
		}
		
	}

	@Override
	public void callBackLocationUpdated(Location location) {
		callForPinpointData( location );
		geoLocationManager.stopListening();
	}
	
}
