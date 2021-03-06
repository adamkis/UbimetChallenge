package com.adamkis.ubimetChallenge.view;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.adamkis.ubimetChallenge.communication.HttpCommunicationInterface;
import com.adamkis.ubimetChallenge.communication.HttpGetAsynchTask;
import com.adamkis.ubimetChallenge.communication.GeoLocationMagager;
import com.adamkis.ubimetChallenge.communication.LocationHandlerInterface;
import com.adamkis.ubimetChallenge.model.ConstantsUbimet;
import com.adamkis.ubimetChallenge.model.ObjectUbimet;
import com.adamkis.ubimetChallenge.utils.UtilsUbimetChallenge;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity implements HttpCommunicationInterface, LocationHandlerInterface {

	private MainActivity mainActivity = this;
	
	private GeoLocationMagager geoLocationManager;
	
	private View loadingStatusView;
	private View errorContainer;
	private TextView errorMessage;
	
	private TextView timezoneTextView;
	private TextView parametersTextView;
	
	private Animation fade_out;
	private AnimationListener animationListener;
	
	private Button go_to_weather_list_button;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
	    overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
	    
	    timezoneTextView = (TextView)findViewById(R.id.timezone);
	    parametersTextView = (TextView)findViewById(R.id.parameters);
	    
	    go_to_weather_list_button = (Button)findViewById(R.id.go_to_weather_list_button);
	    go_to_weather_list_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(mainActivity, WeatherListActivity.class));
				
			}
		});

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
	public void callBackPost(String response, String mode) {}

	@Override
	public void callBackGet(String response, String mode) {
		
		Log.d("Ubimet", response);
		
		if ( response == null || response.isEmpty() ){
			showErrorMessage(true, null);
			return;
		}
		
		try {
			

        	// Parsing the JSON
        	JSONObject rawSearchResponseJSONObject;
			try {
				rawSearchResponseJSONObject = new JSONObject(UtilsUbimetChallenge.correctUbiMetJSONresponse(response));
			} catch (JSONException e1) {
				showErrorMessage(true, null);
				e1.printStackTrace();
				return;
			}

        	ArrayList<String> parameterNames = new ArrayList<String>();

        	// Getting data and the parameter names
			JSONArray dataJSONArray = new JSONArray();
			JSONArray paramNamesJSONArray = new JSONArray();
			
			try {
				dataJSONArray = rawSearchResponseJSONObject
						.getJSONArray("met_sets")
						.getJSONObject(0)
						.getJSONArray("parameter_timesets")
						.getJSONObject(0)
						.getJSONArray("data");
				
				paramNamesJSONArray = rawSearchResponseJSONObject
						.getJSONArray("met_sets")
						.getJSONObject(0)
						.getJSONArray("parameter_timesets")
						.getJSONObject(0)
						.getJSONArray("params");

				
				// Filling up the param names
				for ( int i = 0; i < paramNamesJSONArray.length(); i++  ){
					try{
						parameterNames.add(paramNamesJSONArray
								.getString(i));
					} catch (JSONException e) {
						e.printStackTrace(); 
						Log.w("Toovia", "Couldn't add param name to parameter names. Index: " + i);
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				showErrorMessage(true, null);
				showProgress(false);
				return;
			}
			
			if( dataJSONArray.length() < 1 || parameterNames.size() < 1 ){
				showErrorMessage(true, null);
				showProgress(false);
				return;
			}
			else{

				
				// Show time zone
				timezoneTextView.setText(getResources().getString(R.string.timezone) + ": " + rawSearchResponseJSONObject.getString("timezone"));
				
				// Show temperature in Celsius with two decimals
				ObjectUbimet objectUbimet = new ObjectUbimet( null, dataJSONArray.getJSONArray(0), parameterNames );
				parametersTextView.setText( UtilsUbimetChallenge
	        			.kelvinToCelsiusReadable(
						objectUbimet
	    		        		.getTemp(), 2 ) + " Celsius"
	    		        		);
			}

		} catch (JSONException e) {
			Log.e("Ubimet", "The response could not be parsed");
			showErrorMessage(true, null); 
			e.printStackTrace();
		} catch (Exception e) { 
			e.printStackTrace();
		}
		
		showProgress(false);
		
	}

	
	
	
	
	
	
	
	
	@Override
	public void showProgress(boolean show) {

		// init if not inited
		if( loadingStatusView == null )
			loadingStatusView = (View)findViewById(R.id.loading_status);
		if( fade_out == null )
			fade_out = AnimationUtils.loadAnimation(this, R.anim.fade_out );
		if( animationListener == null ){
			animationListener = new Animation.AnimationListener(){
	    	    @Override
	    	    public void onAnimationStart(Animation arg0) {
	    	    }           
	    	    @Override
	    	    public void onAnimationRepeat(Animation arg0) {
	    	    }           
	    	    @Override
	    	    public void onAnimationEnd(Animation arg0) {
	    	    	loadingStatusView.setVisibility(View.GONE);
	    	    }
    		};
		}
		
		// handling visibility
		if( show ){
			loadingStatusView.setVisibility( View.VISIBLE );
		}
		else{
			fade_out.setAnimationListener(animationListener);
			loadingStatusView.startAnimation( fade_out );
		}
		
    	
		
	}
	
	public void showErrorMessage(boolean show, String message) {

		// init if not inited
		if( errorContainer == null )
			errorContainer = (View)findViewById(R.id.errorContainer);

		// Set message
		if( message != null  ){
			if( errorMessage == null ){
				errorMessage = (TextView)findViewById(R.id.errorMessage);
			}
			errorMessage.setText(message);
		}
			
		// handling visibility
		if( show ){
			errorContainer.setVisibility( View.VISIBLE );
		}
		else{
			errorContainer.setVisibility( View.GONE );
		}
		
    	
		
	}

	@Override
	public void callBackLocationUpdated(Location location) {
		callForPinpointData( location );
		geoLocationManager.stopListening();
	}
	
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
    }

	
}
