package com.adamkis.ubimetChallenge.view;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class MainActivity extends ActionBarActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
	    overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
	    
		 
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
}
