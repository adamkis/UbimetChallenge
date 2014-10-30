package com.adamkis.search500px.controller;

public interface DataHandlerInterface {

	public void callBackPost( String response, String mode );
	
	public void callBackGet( String response, String mode );	
	
	public void showProgress( boolean show );


}
