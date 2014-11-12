package com.adamkis.ubimetChallenge.view;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.adamkis.ubimetChallenge.communication.GeoLocationMagager;
import com.adamkis.ubimetChallenge.communication.HttpCommunicationInterface;
import com.adamkis.ubimetChallenge.communication.HttpGetAsynchTask;
import com.adamkis.ubimetChallenge.communication.LocationHandlerInterface;
import com.adamkis.ubimetChallenge.model.ConstantsUbimet;
import com.adamkis.ubimetChallenge.model.ObjectUbimet;
import com.adamkis.ubimetChallenge.utils.UtilsUbimetChallenge;
import com.adamkis.ubimetChallenge.view.adapters.ListAdapterWeatherList;



public class WeatherListActivity extends ActionBarActivity implements HttpCommunicationInterface, LocationHandlerInterface {

	private GeoLocationMagager geoLocationManager;
	
	private android.widget.ListView listView;
	private WeatherListActivity weatherListActivity;
	private View loadingStatusView;
	private Animation fade_out;
	private AnimationListener animationListener;

	private View errorContainer;

	private TextView errorMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_list_activity);
		

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Weather List");
	    overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);

		weatherListActivity = this;

		listView = (ListView)findViewById(R.id.searchResultList);
 

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
	public void callBackPost(String response, String mode) {}

	@Override
	public void callBackGet(String response, String mode) {

		if ( mode.equals("search") ){
		
			Log.i("Ubimet", "Search response was:");
			UtilsUbimetChallenge.longLog(response);
			
	        try {		
	        	
	        	// Parsing the JSON
	        	JSONObject rawSearchResponseJSONObject;
				try {
					rawSearchResponseJSONObject = new JSONObject(response);
				} catch (JSONException e1) {
					showError(true, "Oops, something went wrong...");
					e1.printStackTrace();
					return;
				}

	        	ArrayList<ObjectUbimet> resultList = new ArrayList<ObjectUbimet>();

				JSONArray dataJSONArray = new JSONArray();
				try {
					dataJSONArray = rawSearchResponseJSONObject
							.optJSONArray("data");
				} catch (Exception e) {
					e.printStackTrace();
					showError(true, null);
					showProgress(false);
					return;
				}
				
				if( dataJSONArray.length() < 1 ){
					showError(true, "No matching results found...");
					showProgress(false);
					return;
				}
				else{
				
					for ( int i = 0; i < dataJSONArray.length(); i++  ){
						try {
							resultList.add( new ObjectUbimet( dataJSONArray.optJSONObject(i) ) );
						} catch (Exception e) {
							e.printStackTrace(); 
							Log.w("Toovia", "When parsing ISBN book results: book cannot be parsed. Index: " + i);
						}
					} 
		
					if( resultList.size() == 0 ){
						showError(true, "No matching results found...");
						showProgress(false);
						return;
					}
				
				}

	
				ListAdapterWeatherList adapter = new ListAdapterWeatherList(resultList, weatherListActivity);
				
				listView.setAdapter(adapter);	
				adapter.notifyDataSetChanged();
				
//				listView.setOnItemClickListener(new OnItemClickListener() {
//				
//					@Override
//					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//					
//						if ( ((ISBNBook)arg0.getItemAtPosition(arg2)).getTitle() != null ){
//						
//							Intent detailsPageIntent = new Intent(weatherListActivity, ProfilePageActivity.class);
//							detailsPageIntent.putExtra("ISBNBook",
//									((Serializable)(((ISBNBook)arg0.getItemAtPosition(arg2)))));
//							weatherListActivity.startActivity( detailsPageIntent );
//							return;
//						}
//						else{
//							
//						}
//					
//					}
//				});

				showProgress(false);
				
				
				
					
			} catch (Throwable e) {
				e.printStackTrace();
			}
        
		}
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
    	default:
    		return super.onOptionsItemSelected(item);
		}

	}
	
	@Override
	public void callBackLocationUpdated(Location location) {
		callForPinpointData( location );
		geoLocationManager.stopListening();
	}
	


	@Override
	public void showProgress(final boolean show) {

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

	
	public void showError(final boolean show, String message) {

		final TextView errorMessage = (TextView)findViewById(R.id.errorMessage);
		if( show ){
			errorMessage.setText(message);
			errorMessage.setVisibility( View.VISIBLE );
		}
		else{
			errorMessage.setVisibility( View.GONE );
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
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
    }


}
