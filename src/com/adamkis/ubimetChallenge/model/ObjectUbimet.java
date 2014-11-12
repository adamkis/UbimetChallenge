package com.adamkis.ubimetChallenge.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

public class ObjectUbimet implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String poi_ref = null;
	private HashMap<String, String> parameters;
	
	public ObjectUbimet( String poi_ref, JSONArray parametersJSONArray, ArrayList<String> parameterNames ){
		setParameters(new HashMap<String, String>());
		setPoi_ref(poi_ref);
		
		for ( int i = 0; i < parametersJSONArray.length(); i++  ){
			try {
				parameters.put(
						parameterNames.get(i),
						parametersJSONArray
							.getJSONArray(i)
							.getString(0)
						);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public HashMap<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(HashMap<String, String> parameters) {
		this.parameters = parameters;
	}

	public String getPoi_ref() {
		return poi_ref;
	}

	public void setPoi_ref(String poi_ref) {
		this.poi_ref = poi_ref;
	}

	

}
