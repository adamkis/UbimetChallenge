package com.adamkis.ubimetChallenge.view;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import com.adamkis.ubimetChallenge.communication.DataHandlerInterface;
import com.adamkis.ubimetChallenge.communication.DataLoaderGenericGet;
import com.adamkis.ubimetChallenge.model.ConstantsUbimet;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;


public class MainActivity extends ActionBarActivity implements DataHandlerInterface {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
	    overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
	    
	    StringBuilder url = new StringBuilder();
	    url.append(ConstantsUbimet.UBIMET_HOST_URL);
	    url.append("/pinpoint-data?sets=basic_now");
	    try {
	    	url.append("&coordinates=");
			url.append(URLEncoder.encode("19.04 47.49", "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	    
	    HashMap<String, String> headers = new HashMap<String, String>();
	    headers.put("Authorization", ConstantsUbimet.UBIMET_PINPOINT_TOKEN);
	    headers.put("Accept-Language", "en");
//	    headers.put("Coordinates", "19.04 47.49");
	    
	    DataLoaderGenericGet dlgg = new DataLoaderGenericGet(this, url.toString(), headers, "initial");
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
		
	}

	@Override
	public void showProgress(boolean show) {
		// TODO Auto-generated method stub
		
	}
}
