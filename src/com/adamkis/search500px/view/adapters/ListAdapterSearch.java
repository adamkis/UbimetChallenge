package com.adamkis.search500px.view.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.adamkis.search500px.view.R;
import com.adamkis.search500px.model.Object500px;
import com.squareup.picasso.Picasso;

public class ListAdapterSearch extends BaseAdapter {

    private LayoutInflater rowInflater = null;
    private ArrayList<Object500px> data = new ArrayList<Object500px>();
    private Activity activity;


    public ListAdapterSearch(ArrayList<Object500px> data, Activity activity){
    	
    	rowInflater = LayoutInflater.from(activity);
    	this.data = data;
    	this.activity = activity;

    }
    
    public int getCount() { 
    	if( data == null )
    		return 0;
    	return data.size(); 
    }
    
    public Object getItem(int pos) {
    	return data.get(pos); 
    }
    public long getItemId(int pos) { return pos; }

	static class SearchViewHolder{
		TextView titleTextView;
		ImageView image;
	}
    
    public View getView(int pos, View convertView, ViewGroup p) {

    	// For the static environments

    	SearchViewHolder searchViewHolder;

    	if( convertView == null ){
    		
    		convertView = rowInflater.inflate(R.layout.pod_search_result, p, false);
    		
    		searchViewHolder = new SearchViewHolder();
    		searchViewHolder.titleTextView = (TextView)convertView.findViewById(R.id.title);
    		searchViewHolder.image = (ImageView)convertView.findViewById(R.id.image);

    		convertView.setTag(searchViewHolder);
    	}
    	else{
    		searchViewHolder = (SearchViewHolder)convertView.getTag();
    	}

    	try{
    		searchViewHolder.titleTextView.setText(data.get(pos).getName());

    		try{
	        	Picasso
	        	.with(activity)
	      	  	.load(data.get(pos).getImage_url().toString())
	      	  	.placeholder(R.drawable.grey_rectangle_600_2_spot)
//	      	  	.noFade()
	      	  	.into(searchViewHolder.image);

    		}catch(Exception e){ e.printStackTrace(); }



    	}
    	catch(Exception e){
    		Log.d("Toovia", "Faulty element in the ListAdapter");
    		e.printStackTrace();
    	}

        return convertView;
    }
    

    
    
    public void setAdapterData(ArrayList<Object500px> data) {
    	
    	this.data = data;

    }
    
    public void appendAdapterData(ArrayList<Object500px> data) {
    	

    	for (int i=0; i<data.size(); i++){
    		this.data.add(data.get(i));
        }
    	

    }
    

    
    
}
