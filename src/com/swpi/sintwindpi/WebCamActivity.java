package com.swpi.sintwindpi;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class WebCamActivity extends Activity {
	public Station station = new Station();
	public String urlPage = "";
	public  WebView myWebView;
	private static final String TAG = "WebCamActivity";
	SharedPreferences settings;
	
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		  ScaleImageView bmImage;

		  public DownloadImageTask(ScaleImageView bmImage) {
		      this.bmImage = bmImage;
		  }

		  protected Bitmap doInBackground(String... urls) {
		      String urldisplay = urls[0];
		      Bitmap mIcon11 = null;
		      try {
		        InputStream in = new java.net.URL(urldisplay).openStream();
		        mIcon11 = BitmapFactory.decodeStream(in);
		      } catch (Exception e) {
		          Log.e("Error", e.getMessage());
		          e.printStackTrace();
		      }
		      return mIcon11;
		  }

		  protected void onPostExecute(Bitmap result) {
		      bmImage.setImageBitmap(result);
		  }
		}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_cam);
		
		settings  = getSharedPreferences("swpi_stations", 0);
		station.ID = settings.getInt("ID", 0);
		
		if ( station.ID == 0)
		{
			Intent intStations = new Intent(this,Stations.class);
			startActivity(intStations);
			//setContentView(R.layout.activity_stations);
			
		}
		
		station.NAME = settings.getString("NAME", "");
		station.LAT = settings.getFloat("LAT", 0); 
		station.LON = settings.getFloat("LON", 0); 	
		station.URL = settings.getString("URL", "");
		station.WEBCAM = settings.getString("WEBCAM", "");
		station.TEL = settings.getString("TEL", "");
		station.NOTES = settings.getString("NOTES", "");

		
//		myWebView = (WebView) findViewById(R.id.webView1);
//		myWebView.getSettings().setJavaScriptEnabled(true);
//		myWebView.getSettings().setSupportZoom(true);  
//		myWebView.getSettings().setBuiltInZoomControls(true);
//		//myWebView.setWebChromeClient (new WebChromeClient  ());
//		String html = "<html><body><img src=\""+station.WEBCAM+"\" width=\"100%\"  /></body></html>";
//		myWebView.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
		
		
		new DownloadImageTask((ScaleImageView) findViewById(R.id.imageView1)).execute(station.WEBCAM);
		
//		ImageView imageView = (ImageView) findViewById(R.id.imageView1);
//		URL url = null;
//		Bitmap bmp = null;
//		try {
//			url = new URL(station.WEBCAM);
//			bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		imageView.setImageBitmap(bmp);


		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//	    if (keyCode == KeyEvent.KEYCODE_BACK) {
//	        moveTaskToBack(true);
//	        return true;
//	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web_cam, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
    	settings  = getSharedPreferences("swpi_stations", 0);
	    switch(item.getItemId())
	    {
		    case R.id.action_settings:
		        Intent intStations = new Intent(this,Stations.class);
		        startActivity(intStations);
		        return true;      
		    case R.id.ViewWeb:
		    	settings.edit().putInt("PAGE", 1).commit();           	 
		        Intent intWeb = new Intent(this,MainActivity.class);
		        startActivity(intWeb);
		        return true;        
		    case R.id.ViewGauge1:
		    	settings.edit().putInt("PAGE", 2).commit();  
		        Intent intGauge1 = new Intent(this,MainActivity.class);
		        startActivity(intGauge1);
		        return true;    
		    case R.id.ViewMain:
		    	settings.edit().putInt("PAGE", 0).commit();           	 
		        Intent intMain = new Intent(this,MainActivity.class);
		        startActivity(intMain);
		        return true;      
		    case R.id.Tel:
		    	Intent callIntent = new Intent(Intent.ACTION_CALL);
		    	String ntel = "tel:" + station.TEL;
		    	callIntent.setData(Uri.parse(ntel));
		    	startActivity(callIntent);
//		    case R.id.ViewLCD:
//		    	settings.edit().putInt("PAGE", 3).commit();           	 
//		        Intent inLCD = new Intent(this,MainActivity.class);
//		        startActivity(inLCD);
//		        return true;      		        
	        
	    }
	    return false;
	}
	
}
