package com.adamkis.ubimetChallenge.view;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;

import com.adamkis.ubimetChallenge.communication.HttpCommunicationInterface;
import com.adamkis.ubimetChallenge.communication.HttpGetAsynchTask;
import com.adamkis.ubimetChallenge.model.ConstantsUbimet;
import com.adamkis.ubimetChallenge.model.ObjectUbimet;
import com.adamkis.ubimetChallenge.utils.UtilsUbimetChallenge;
import com.adamkis.ubimetChallenge.view.adapters.ListAdapterWeatherList;



public class WeatherListActivity extends ActionBarActivity implements HttpCommunicationInterface {

	private android.widget.ListView listView;
	private WeatherListActivity weatherListActivity;
	private View loadingStatusView;
	private Animation fade_out;
	private AnimationListener animationListener;

	private View errorContainer;

	private TextView errorMessage;
	
	private ArrayList<ObjectUbimet> weatherDataList;

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
    	
    	callForWeatherList();
	    
	}

	
	private void callForWeatherList() {
		
	    StringBuilder url = new StringBuilder();
	    url.append(ConstantsUbimet.UBIMET_HOST_URL);
	    url.append("/poi-data?sets=basic_now");

	    HashMap<String, String> headers = new HashMap<String, String>();
	    headers.put("Authorization", ConstantsUbimet.UBIMET_POI_TOKEN);
	    headers.put("Accept", "application/json");
	    headers.put("Accept-Language", "en");
	    
	    HttpGetAsynchTask dlgg = new HttpGetAsynchTask(this, url.toString(), headers, "get_weather_list");
	    dlgg.execute();
		
	}

	@Override
	public void callBackPost(String response, String mode) {}

	@Override
	public void callBackGet(String response, String mode) {

		/////////////////////////////////////////
		// First call for the weather list
		/////////////////////////////////////////
		if ( mode.equals("get_weather_list") ){
		
			Log.i("Ubimet", "Get_weather_list response was:");
			UtilsUbimetChallenge.longLog(response);
			
	        try {		
	        	
	        	// Parsing the JSON
	        	JSONObject rawSearchResponseJSONObject;
				try {
					rawSearchResponseJSONObject = new JSONObject(UtilsUbimetChallenge.correctUbiMetJSONresponse(response));
				} catch (JSONException e1) {
					showError(true, null);
					e1.printStackTrace();
					return;
				}

	        	weatherDataList = new ArrayList<ObjectUbimet>();
	        	ArrayList<String> parameterNames = new ArrayList<String>();

	        	// Getting data and the parameter names
				JSONArray dataJSONArray = new JSONArray();
				JSONArray paramNamesJSONArray = new JSONArray();
				JSONArray poi_refsJSONArray = new JSONArray();
				
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
					
					poi_refsJSONArray = rawSearchResponseJSONObject
							.getJSONObject("spatial_features")
							.getJSONArray("poi_refs");
					
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
					showError(true, null);
					showProgress(false);
					return;
				}
				
				if( dataJSONArray.length() < 1 || parameterNames.size() < 1 || poi_refsJSONArray.length() < 1 ){
					showError(true, null);
					showProgress(false);
					return;
				}
				else{

					// Filling up the result set
					for ( int i = 0; i < dataJSONArray.length(); i++  ){
						try {
							ObjectUbimet objectUbimet = new ObjectUbimet( poi_refsJSONArray.getString(i), dataJSONArray.getJSONArray(i), parameterNames );
							weatherDataList.add( objectUbimet );
						} catch (Exception e) {
							e.printStackTrace(); 
							Log.w("Toovia", "When parsing Ubimet Object results: object cannot be parsed. Index: " + i);
						}
					} 
		
					if( weatherDataList.size() == 0 ){
						showError(true, null);
						showProgress(false);
						return;
					}
				
				}
				
				
				
			    StringBuilder url = new StringBuilder();
			    url.append(ConstantsUbimet.UBIMET_HOST_URL);
			    url.append("/poi-info?sets=basic_now");

			    HashMap<String, String> headers = new HashMap<String, String>();
			    headers.put("Authorization", ConstantsUbimet.UBIMET_POI_TOKEN);
			    headers.put("Accept", "application/json");
			    headers.put("Accept-Language", "en");
			    
			    
			    ////////////////////////////////////////////////////////////
			    // After getting the POI info, let's make another call to resolve the location names given the POI_IDs
			    ////////////////////////////////////////////////////////////
			    HttpGetAsynchTask dlgg = new HttpGetAsynchTask(this, url.toString(), headers, "get_poi_info");
			    dlgg.execute();

				
				
				
					
			} catch (Throwable e) {
				showError(true, null);
				e.printStackTrace();
			}
        
		}
		
		/////////////////////////////////////////
		// Second call to add additional info to the objects (name for now)
		/////////////////////////////////////////		
		if ( mode.equals("get_poi_info") ){
			
			Log.i("Ubimet", "get_poi_info response was:");
			UtilsUbimetChallenge.longLog(response);
			
	        try {		
	        	
	        	// Parsing the JSON
	        	JSONObject rawSearchResponseJSONObject;
				try {
					rawSearchResponseJSONObject = new JSONObject(UtilsUbimetChallenge.correctUbiMetJSONresponse(response));
				} catch (JSONException e1) {
					showError(true, null);
					e1.printStackTrace();
					return;
				}

				JSONArray poi_infoJSONArray = new JSONArray();
				try {
					poi_infoJSONArray = rawSearchResponseJSONObject
							.getJSONArray("spatial_features");

					// Matching objects with the extra info and if they match upon the POI_ID, then add the name of it
					for ( int i = 0; i < poi_infoJSONArray.length(); i++  ){
						try{
							for( ObjectUbimet objectUbimet : weatherDataList ){
								if( poi_infoJSONArray
										.getJSONObject(i)
											.getString("id")
												.equals(objectUbimet.getPoi_ref()) ){
									objectUbimet.setName(poi_infoJSONArray
										.getJSONObject(i)
											.getString("name"));
								}
							}
						} catch (Exception e) {
							e.printStackTrace(); 
							Log.w("Toovia", "Couldn't add name to the UbimeTobject. Index: " + i);
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					showError(true, null);
					showProgress(false);
					return;
				}

				// Showing the data in the adapter
				ListAdapterWeatherList adapter = new ListAdapterWeatherList(weatherDataList, weatherListActivity);
				listView.setAdapter(adapter);	
				adapter.notifyDataSetChanged();
				showProgress(false);
				
				
				
					
			} catch (Throwable e) {
				showError(true, null);
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
