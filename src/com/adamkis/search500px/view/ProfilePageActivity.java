package com.adamkis.search500px.view;


import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.adamkis.search500px.view.R;
import com.adamkis.search500px.model.Object500px;
import com.squareup.picasso.Picasso;



public class ProfilePageActivity extends ActionBarActivity {

	private Object500px object500px = null;
	private ProfilePageActivity profilePageActivity = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_page_activity);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
		
		
		
		
		Bundle extras = getIntent().getExtras(); 
		if ( extras != null )
			object500px = (Object500px)extras.get("object500px");
		
		
		if( object500px != null ){
			
			getSupportActionBar().setTitle(object500px.getName());
			
			TextView titleTextView = (TextView)findViewById(R.id.title);
			TextView descriptionTextView = (TextView)findViewById(R.id.description);
			ImageView image = (ImageView)findViewById(R.id.image);
	
			titleTextView.setText(object500px.getName());
			descriptionTextView.setText(object500px.getDescription());

    		try{
	        	Picasso
	        	.with(profilePageActivity)
	      	  	.load(object500px.getImage_url_HD().toString())
	      	  	.placeholder(R.drawable.grey_rectangle_600_2_spot)
	      	  	.into(image);

    		}catch(Exception e){ e.printStackTrace(); }

			
		}
		else{
			showError(true);
		}


	}

	public void showError(final boolean show) {

		final TextView error_message = (TextView)findViewById(R.id.error_message);
		if( show ){
			error_message.setVisibility( View.VISIBLE );
		}
		else{
			error_message.setVisibility( View.GONE );
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
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
    }


}

