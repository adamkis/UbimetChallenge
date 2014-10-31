package com.adamkis.search500px.model;

import java.io.Serializable;

import org.json.JSONObject;

import android.util.Log;

public class Object500px implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String name = null;	
	private String image_url = null;	
	private String image_url_HD = null;
	private String description = null;


	public Object500px( JSONObject jsonObject ){
		setName(jsonObject.optString("name"));
		setImage_url(jsonObject.optString("image_url"));
		setImage_url_HD(getImage_url());
		setDescription(jsonObject.optString("description"));
	}

	public String getName() {
		return name;
	}

	public void setName(String title) {
		this.name = title;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
	
	public String getImage_url_HD() {
		return image_url_HD;
	}
	
	public void setImage_url_HD(String image_url){
		try {
			final String extention = ".jpg";
			String[] photo_url_partsStrings = image_url.split( extention );
			photo_url_partsStrings[0] = photo_url_partsStrings[0].substring(0, photo_url_partsStrings[0].length()-1) + "5";
			this.image_url_HD = photo_url_partsStrings[0] + extention + photo_url_partsStrings[1];
		} catch (Exception e) {
			Log.w("search500px", "Generating URL for HD picture failed");
//			e.printStackTrace();
			this.image_url_HD = image_url;
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	

}
