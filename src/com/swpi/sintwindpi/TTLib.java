package com.swpi.sintwindpi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.util.Log;

public class TTLib {
	
	
	public String getTxtStringFromUrl(String url) 
	{
	    URL textUrl;
	    try 
	    {
		     textUrl = new URL(url);
		     BufferedReader bufferReader = new BufferedReader(new InputStreamReader(textUrl.openStream()));
		     String StringBuffer;
		     String stringText = "";
		     while ((StringBuffer = bufferReader.readLine()) != null) 
		     {
		     	stringText += StringBuffer;
		     }
		     bufferReader.close();
		     return stringText;
		           
	    } catch (MalformedURLException e) {
	     // TODO Auto-generated catch block
	    	e.printStackTrace();
	     
	    } catch (IOException e) {
	     // TODO Auto-generated catch block
	    	e.printStackTrace();
		     
		 }
	    return "";
	
	}

	
	public String getXMLStringFromUrl(String url) {
        String xml = null;
        
        
        try 
        {
    		HttpClient httpClient = new DefaultHttpClient();
    		// Creating HTTP Post
    		//HttpPost httpPost = new HttpPost(url);
    		HttpGet httpPost = new HttpGet(url);
    		
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity httpEntity = response.getEntity();
            xml = EntityUtils.toString(httpEntity);
               
    		// writing response to log
            Log.d("Http Response:", xml);
    	} 
		catch (ClientProtocolException e) 
		{
			// writing exception to log
			e.printStackTrace();
		} 
        catch (IOException e) 
        	{
    			// writing exception to log
    			e.printStackTrace();
    		}
        
    	return xml;
	}
	
}
