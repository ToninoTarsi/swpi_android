package com.swpi.sintwindpi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;



import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;
import android.widget.Toast;





public class MainActivity extends Activity {
	
	private static final int LAUNCH_SETTINGS = 1;
	
	private Station station = new Station();
	
	private String urlPage = "";
	private  WebView myWebView;
	private static final String TAG = "MainActivity";
	private int page;
	private SharedPreferences settings;
	private int width ;
	private int height ;
	private String strjson = "";
	private AudioManager audioManager;
	private Intent iMeteoService;
	private int n = 0;
	
	private BroadcastReceiver myServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	Bundle extras = intent.getExtras();
			//Address location = extras.get("LOCATION");
			
        	strjson = extras.getString("METEO");
        	myWebView.loadUrl("javascript:UpdateData('"+strjson+"')");
        	Log.d(TAG,"myServiceReceiver");
        	
        	
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			boolean bAudio = sharedPrefs.getBoolean("bAudio", false);
			int Audio_repetition_time = Integer.valueOf(sharedPrefs.getString("Audio_repetition_time","5"));
			
			if ( bAudio &&  ( n % Audio_repetition_time == 0 )  ) {
				try {
					playaudio(strjson);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			n++;
        }
	};
	
	public boolean isJSONValid(String test)
	{
	    boolean valid = false;
	    try {
	        new JSONObject(test);
	        valid = true;
	    }
	    catch(JSONException ex) { 
	        valid = false;
	    }
	    return valid;
	}
	
	public boolean haveNetworkConnection() {
	    boolean haveConnectedWifi = false;
	    boolean haveConnectedMobile = false;

	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	    for (NetworkInfo ni : netInfo) {
	        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
	            if (ni.isConnected())
	                haveConnectedWifi = true;
	        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
	            if (ni.isConnected())
	                haveConnectedMobile = true;
	    }
	    return haveConnectedWifi || haveConnectedMobile;
	}
	
	private class UpdateMeteoTask extends AsyncTask<String, Void, String> {
		public  UpdateMeteoTask() {
		}

		protected String doInBackground(String... urls) {
		    URL textUrl;
		    try {
			     textUrl = new URL(urls[0]);
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
		    	e.printStackTrace();
		     
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }
		    return "";
		}
		     
	  protected void onPostExecute(String result) {
		  UpdateData(result);
	  }
	}
	
	
	
	public void UpdateData(String strjson)
	{
    	if ( strjson.startsWith("{") && strjson.endsWith("}")) {
    		myWebView.loadUrl("javascript:UpdateData('"+strjson+"')");
    		//Log.d(TAG, "data updated " + strjson);
    	}
    	else {
    		Log.d(TAG, "Bad json read");
    	}
    	

	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		DisplayMetrics metrics = this.getResources().getDisplayMetrics();
		width = metrics.widthPixels;
		height = metrics.heightPixels;
		

		
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);	
//    	audioManager.setSpeakerphoneOn(true);
//		int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
//    	int sb2value = audioManager.getStreamMaxVolume(audioManager.STREAM_MUSIC);
//    	audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, sb2value,0);
    	

		boolean bNet = haveNetworkConnection() ;
		if ( ! bNet) {
			AlertDialog.Builder builder=new AlertDialog.Builder(this);
			builder.setTitle("Errore");
			builder.setMessage("Sint Wind PI ha bisogno di una connessione attiva per funzionare");
			builder.setCancelable(true);
			builder.setNegativeButton("Chiudi", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               // User cancelled the dialog
		           }
		       });

			builder.create();
			builder.show();

			
		}
		
		settings  = getSharedPreferences("swpi_stations", 0);
		station.ID = settings.getInt("ID", 0);
		
		if ( station.ID == 0)
		{
			Intent intStations = new Intent(this,Stations.class);
			startActivity(intStations);			
		}
		else
		{

			page = settings.getInt("PAGE", 0);
			station.NAME = settings.getString("NAME", "");
			station.LAT = settings.getFloat("LAT", 0); 
			station.LON = settings.getFloat("LON", 0); 	
			station.URL = settings.getString("URL", "");
			station.WEBCAM = settings.getString("WEBCAM", "");
			station.TEL = settings.getString("TEL", "");
			station.NOTES = settings.getString("NOTES", "");
			

			
//		    PAGE 0 - Dati
//		    PAGE 1 - WEB
//		    PAGE 2 = Wind
//		    PAGE 3 = LCD
//		    PAGE 4 = WEBCAM
		    switch(page)
		    {
		    	case 0:
		    		if ( width > 480 )
		    			urlPage = "file:///android_asset/data480.html";
		    		else
		    			urlPage = "file:///android_asset/data.html";
		    		break;
			    case 1:
			    	urlPage = station.URL +"/swpi_smartphone.html";
			        break;
			    case 2:
			    	if ( width > 480 )
			    		urlPage = "file:///android_asset/wind480.html";
			    	else
			    		urlPage = "file:///android_asset/wind.html";
			    	break;
//			    case 3:
//			    	urlPage = "file:///android_asset/lcd.html";
//			    	break;
			    case 4:
			        Intent intWebcam = new Intent(this,WebCamActivity.class);
			        startActivity(intWebcam); 
			    	return;

		    }
		    
		    setContentView(R.layout.activity_main);
			
			myWebView = (WebView) findViewById(R.id.webView1);
			myWebView.getSettings().setJavaScriptEnabled(true);


			myWebView.setWebChromeClient(new WebChromeClient() {
	            public void onProgressChanged(WebView view, int progress)
	            {

	                if(progress == 100 && ( page == 0  || page == 2 )){
	                	new UpdateMeteoTask().execute(station.URL+"/meteo.txt" );
	                	myWebView.refreshDrawableState();
	                }

	            }

	        });


			myWebView.loadUrl(urlPage);
			
			
			
			// use this to start and trigger a service
			iMeteoService= new Intent(this, MeteoService.class);
			// potentially add data to the intent
			iMeteoService.putExtra("METEO_FILE", station.URL+"/meteo.txt");
	        registerReceiver(myServiceReceiver, new IntentFilter("com.swpi.sintwindpi.INTENT_ACTION_METEOCHANGED"));
			startService(iMeteoService); 

			
			Thread thread = new Thread() {
			    @Override
			    public void run() {
			        try {
			        	int n = 0;
			        	String prevstrjson = "";
			        	TTLib t = new TTLib();
			        	boolean bSync = false;
			        	
			        	try { 
			        		String tmljson = t.getTxtStringFromUrl(station.URL+"/meteo.txt");
		            		prevstrjson = tmljson;
		            		JSONObject jObject = new JSONObject(tmljson);
		            		myWebView.loadUrl("javascript:UpdateData('"+strjson+"')");
			        	} catch(Exception e) {
		            		
		            	}	
			        	while ( ! bSync ) {
			            	try { 
			            		String tmljson = t.getTxtStringFromUrl(station.URL+"/meteo.txt");
			            		if ( !  prevstrjson.equals(tmljson) ) {
				            		Log.d(TAG, "Sincronized");
			            			strjson = tmljson;
			            			myWebView.loadUrl("javascript:UpdateData('"+strjson+"')");
			            			prevstrjson = strjson;
			            			bSync = true;
			            		}
			            		else {
				            		Log.d(TAG, "Sleeping 2000");
			            			sleep(2000);
			            		}
				        	} catch(Exception e) {
			            		
			            	}	
			        	}
			        	
			            while(true) {
		            		Log.d(TAG, "Sleeping 60");
			            	sleep(60000);
			            	String tmljson = t.getTxtStringFromUrl(station.URL+"/meteo.txt" );
			            	try { 
			            		JSONObject jObject = new JSONObject(tmljson);
			            		if (  ! prevstrjson.equals(tmljson )) {
			            			strjson = tmljson;
			            			myWebView.loadUrl("javascript:UpdateData('"+strjson+"')");
			            			prevstrjson = strjson;
			            			
									SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
									boolean bAudio = sharedPrefs.getBoolean("bAudio", false);
									int Audio_repetition_time = Integer.valueOf(sharedPrefs.getString("Audio_repetition_time","5"));
									
									if ( bAudio &&  ( n % Audio_repetition_time == 0 )  ) {
										playaudio(strjson);
									}
									n++;
			            			
			            		}
			            		
			            		
			            	} catch(Exception e) {
			            		
			            	}
			                
			            }
			        } catch (InterruptedException e) {
			            e.printStackTrace();
			        }
			    }
			};
			//thread.start();
			
			
			

			
			

			Thread threadAudio = new Thread() {
			    @Override
			    public void run() {
			    	
			    	while ( true) {
			    		
						SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
						boolean bAudio = sharedPrefs.getBoolean("bAudio", false);
						int Audio_repetition_time = Integer.valueOf(sharedPrefs.getString("Audio_repetition_time","5"));
						
						if ( bAudio ) {
							
							boolean sended = false;
							while ( ! sended ) {
								if ( strjson != "" ) {
									try {
										JSONObject jObject = new JSONObject(strjson);
										String last_measure_time = jObject.getString("last_measure_time");
										String pattern = "[dd/MM/yyyy-HH:mm:ss]";
							            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
							            Date date_current  = new Date();
						                Date date_meteo = sdf.parse(last_measure_time);
						                long dif = date_current.getTime() - date_meteo.getTime();
						                if ( dif < 180000 ) {
						                	playaudio(jObject);
						                	sended = true;
						                }
						                else {
						                	Thread.sleep(60*1000);
						                }
									} catch (Exception e) { 
									    // Oops
									}
								}
							}
						}
					
				    	try {
							Thread.sleep(Audio_repetition_time*60*1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
			    }
			};
			//threadAudio.start();
		}
		
	}

	private void playaudio(String  jsonString) throws JSONException  {
		
		JSONObject jObject = new JSONObject(jsonString);
		
		String wind_dir_code  = jObject.getString("wind_dir_code");
		String wind_ave  = jObject.getString("wind_ave");
		String wind_gust  = jObject.getString("wind_gust");

		String str;
		str = "winddirection.mp3";
		playmp3(str);
		str = wind_dir_code.toLowerCase()+".mp3";
		playmp3(str);
		str = "from.mp3";
		playmp3(str);
		str = String.format("n%d.mp3",Math.round(Double.parseDouble(wind_ave)));
		playmp3(str);
		str = "to.mp3";
		playmp3(str);
		str = String.format("n%d.mp3",Math.round(Double.parseDouble(wind_gust)));
		playmp3(str);

	}
	
	
	private void playaudio(JSONObject jObject) throws JSONException {
		
		String wind_dir_code  = jObject.getString("wind_dir_code");
		String wind_ave  = jObject.getString("wind_ave");
		String wind_gust  = jObject.getString("wind_gust");

		String str;
		str = "winddirection.mp3";
		playmp3(str);
		str = wind_dir_code.toLowerCase()+".mp3";
		playmp3(str);
		str = "from.mp3";
		playmp3(str);
		str = String.format("n%d.mp3",Math.round(Double.parseDouble(wind_ave)));
		playmp3(str);
		str = "to.mp3";
		playmp3(str);
		str = String.format("n%d.mp3",Math.round(Double.parseDouble(wind_gust)));
		playmp3(str);

	}
	
	public void playmp3(String strMp3) {
	    try {

	    	MediaPlayer m = new MediaPlayer();
	        AssetFileDescriptor descriptor = getAssets().openFd("audio/"+strMp3);
	        m.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
	        descriptor.close();

	        m.prepare();
	        m.setVolume(1f, 1f);
	        m.setLooping(false);
	        m.start();
	        while ( m.isPlaying() ) {
	        	Thread.sleep(10);
	        }
	        	
	    } catch (Exception e) {
	    }
	}
	
	
	
	
//    PAGE 0 - Dati
//    PAGE 1 - WEB
//    PAGE 2 = Wind
//    PAGE 3 = LCD
//    PAGE 4 = WEBCAM
	@Override
	public boolean onPrepareOptionsMenu (Menu menu) {
		int page = settings.getInt("PAGE", 0);
	    if (page == 0 )
	        menu.findItem(R.id.ViewMain).setEnabled(false);
	    else
	    	 menu.findItem(R.id.ViewMain).setEnabled(true);
	    if (page == 2 )
	        menu.findItem(R.id.ViewGauge1).setEnabled(false);	
	    else
	    	 menu.findItem(R.id.ViewGauge1).setEnabled(true);	
	    if (station.WEBCAM == "" )
	        menu.findItem(R.id.ViewWebcal).setEnabled(false);	
	    else
	    	menu.findItem(R.id.ViewWebcal).setEnabled(true);
	    if (station.TEL == "" )
	        menu.findItem(R.id.Tel).setEnabled(false);	
	    else
	    	menu.findItem(R.id.Tel).setEnabled(true);
	    
	    return true;
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        moveTaskToBack(true);
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
    	SharedPreferences settings  = getSharedPreferences("swpi_stations", 0);
	    switch(item.getItemId())
	    {
		    case R.id.action_settings:
		        Intent intStations = new Intent(this,Stations.class);
		        startActivity(intStations);
		        return true;      
		    case R.id.ViewWeb:
		    	page = 1;
		    	settings.edit().putInt("PAGE", 1).commit();   
		    	urlPage = station.URL +"/swpi_smartphone.html";
		    	myWebView.loadUrl(urlPage);
//		        Intent intWeb = new Intent(this,WebActivity.class);
//		        startActivity(intWeb);
		        return true;        
		    case R.id.ViewGauge1: 
		    	page = 2;
		    	settings.edit().putInt("PAGE", 2).commit();  
		    	if ( width > 480 )
		    		urlPage = "file:///android_asset/wind480.html";
		    	else
		    		urlPage = "file:///android_asset/wind.html";
		    	myWebView.loadUrl(urlPage);
		        return true;    
		    case R.id.ViewMain:
		    	page = 0;
		    	settings.edit().putInt("PAGE", 0).commit();           	 
	    		if ( width > 480 )
	    			urlPage = "file:///android_asset/data480.html";
	    		else
	    			urlPage = "file:///android_asset/data.html";
		    	myWebView.loadUrl(urlPage);
		        return true;      
//		    case R.id.ViewLCD:
//		    	settings.edit().putInt("PAGE", 3).commit();           	 
//		    	urlPage = "file:///android_asset/lcd.html";
//		    	myWebView.loadUrl(urlPage);
//		        return true;      		        
		    case R.id.ViewWebcal:
		    	settings.edit().putInt("PAGE", 4).commit();           	 
		        Intent intWebcam = new Intent(this,WebCamActivity.class);
		        startActivity(intWebcam);
		        return true;        		
		    case R.id.Tel:
		    	Intent callIntent = new Intent(Intent.ACTION_CALL);
		    	String ntel = "tel:" + station.TEL;
		    	callIntent.setData(Uri.parse(ntel));
		    	startActivity(callIntent);
		    	return true; 
		    case R.id.settings:
	            Intent i = new Intent(this, UserSettingActivity.class);
	            startActivityForResult(i, LAUNCH_SETTINGS);
//		        Intent intPref = new Intent(this,UserSettingActivity.class);
//		        startActivity(intPref);
		        return true;        		

		           		     
		            
	    }
	    return false;
	}
	
	@Override
    public void onDestroy()
    {
        super.onDestroy();
        stopService(iMeteoService);
    }
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		
	    // Check which request we're responding to
		//GetApplicationSettings();
	    if (requestCode == LAUNCH_SETTINGS) {
	        // Make sure the request was successful
	        if (resultCode == 0) {

	        }
	    }
	}
}
