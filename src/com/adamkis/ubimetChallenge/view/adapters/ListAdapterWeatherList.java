package com.adamkis.ubimetChallenge.view.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.adamkis.ubimetChallenge.model.ObjectUbimet;
import com.adamkis.ubimetChallenge.utils.UtilsUbimetChallenge;
import com.adamkis.ubimetChallenge.view.R;

public class ListAdapterWeatherList extends BaseAdapter {

    private LayoutInflater rowInflater = null;
    private ArrayList<ObjectUbimet> data = new ArrayList<ObjectUbimet>();


    public ListAdapterWeatherList(ArrayList<ObjectUbimet> data, Activity activity){
    	
    	rowInflater = LayoutInflater.from(activity);
    	this.data = data;

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
		TextView nameTextView;
		TextView parametersTextView;
	}
    
    public View getView(int pos, View convertView, ViewGroup p) {

    	// For the static environments

    	SearchViewHolder searchViewHolder;

    	if( convertView == null ){
    		
    		convertView = rowInflater.inflate(R.layout.pod_weather_list, p, false);
    		
    		searchViewHolder = new SearchViewHolder();
    		searchViewHolder.nameTextView = (TextView)convertView.findViewById(R.id.name);
    		searchViewHolder.parametersTextView = (TextView)convertView.findViewById(R.id.parameters);

    		convertView.setTag(searchViewHolder);
    	}
    	else{
    		searchViewHolder = (SearchViewHolder)convertView.getTag();
    	}

    	try{
    		
    		// Show name of the place
    		if( data.get(pos).getName() != null && data.get(pos).getName().length()>0 )
    			searchViewHolder.nameTextView.setText(data.get(pos).getName());
    		else
    			searchViewHolder.nameTextView.setText(data.get(pos).getPoi_ref());
    		
    		// Show temperature
    		searchViewHolder.parametersTextView.setText( UtilsUbimetChallenge
        			.kelvinToCelsiusReadable(
    					data.get(pos)
    		        		.getTemp(), 2 ) + " Celsius"
    		        		);
    		
//    		StringBuilder parametersToDisplay = new StringBuilder();
//    		HashMap<String, String> parameters = data.get(pos).getParameters();
//	        if( parameters != null && !parameters.isEmpty() ){
//		        Iterator<Entry<String, String>> it = parameters.entrySet().iterator();
//		        while (it.hasNext()) {
//		            Entry<String, String> pairs = (Entry<String, String>)it.next();
//		            parametersToDisplay.append(pairs.getKey() + ": " + pairs.getValue() + "\n");
//		        }
//	        }
//    		searchViewHolder.parametersTextView.setText(parametersToDisplay);


    	}
    	catch(Exception e){
    		Log.d("Toovia", "Faulty element in the ListAdapter");
    		e.printStackTrace();
    	}

        return convertView;
    }
    

    
    
    public void setAdapterData(ArrayList<ObjectUbimet> data) {
    	
    	this.data = data;

    }
    
    public void appendAdapterData(ArrayList<ObjectUbimet> data) {
    	

    	for (int i=0; i<data.size(); i++){
    		this.data.add(data.get(i));
        }
    	

    }
    

    
    
}
