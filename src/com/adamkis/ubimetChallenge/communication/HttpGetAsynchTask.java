package com.adamkis.ubimetChallenge.communication;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.adamkis.ubimetChallenge.utils.UtilsUbimetChallenge;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

public class HttpGetAsynchTask extends AsyncTask<String, Void, String > {

	private CookieStore cookieStore;
	private String mode = null;
	private String url = null;
	private HttpCommunicationInterface dataHandlerInterface = null;
	private HashMap<String, String> headers;

	
	public HttpGetAsynchTask( HttpCommunicationInterface dataHandlerInterface, String url, HashMap<String, String> headers, String mode ){
		this.dataHandlerInterface = dataHandlerInterface;
		this.mode = mode;
		this.url = url;
		this.headers = headers;
	}

	@Override
	protected void onPreExecute(){}
	
    @Override
    protected void onPostExecute(String response) { 

    	if( dataHandlerInterface != null ){
    		dataHandlerInterface.callBackGet(response, mode);
    	}
    	else{
    		Log.w("UbiMet", "The caller of the data load asynch task became null");
    	}

    }

	
	
	@Override
	protected String doInBackground(String... params) {

        StringBuffer response = new StringBuffer();

    	try {

	    	HttpClient httpclient = new DefaultHttpClient();
	    	
	    	UtilsUbimetChallenge tu = new UtilsUbimetChallenge();
	    	httpclient = tu.sslClient(httpclient);
	    	
	        HttpContext localContext = new BasicHttpContext();
	        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

	        HttpGet httpGet = new HttpGet( url ); 
	        

			Log.i("Ubimet", "Mode: " + mode + " The URL called for Generic GET call: " + url );
	        if( headers != null && !headers.isEmpty() ){
		        Iterator<Entry<String, String>> it = headers.entrySet().iterator();
		        while (it.hasNext()) {
		            Entry<String, String> pairs = (Entry<String, String>)it.next();
			        httpGet.setHeader(pairs.getKey(), pairs.getValue());
			        Log.i("Ubimet", "Header: Key:" + pairs.getKey() + " Value: " + pairs.getValue() );
		            it.remove();
		        }
	        }
	        
			HttpResponse httpResponse = httpclient.execute(httpGet, localContext);
			HttpEntity responseEntity = httpResponse.getEntity();

			InputStream inputStream = AndroidHttpClient.getUngzippedContent( responseEntity );
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

			String s = null;
        	while((s = bufferedReader.readLine()) != null){
        		response.append(s); 
        	}
			
//			response.append( bufferedReader.readLine() );

			// Try to close
			try {
				responseEntity.consumeContent();
			} catch (Exception e) {e.printStackTrace();}		


		} 
    	catch (UnknownHostException e) {
			e.printStackTrace();
			response.delete(0, response.length());
		}
    	catch (Exception e) {
			e.printStackTrace();
			response.delete(0, response.length());
		}
    	catch (Throwable e) {
			e.printStackTrace();
			response.delete(0, response.length());
			System.gc();
		}
		
    	return response.toString();


	}

}
