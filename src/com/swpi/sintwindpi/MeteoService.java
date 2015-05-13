package com.swpi.sintwindpi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetFileDescriptor;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class MeteoService extends Service{

	private static final int NOTIFICATION = 1;
	public static final String CLOSE_ACTION = "com.swpi.sintwindpi.close";
	
	private NotificationManager mNotificationManager;
    private static final int NOTIFICATION_ID = 2;
    private final NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this);
    
	private long lastAudioTime = 0;
	private String strjson;
	private static final String TAG = "MeteoService";
	String meteo_file;
	private boolean isPlayingAudio = false;
	
	private class AsynGetMeteo extends AsyncTask<String, Void, String> {
		public  AsynGetMeteo() {
		}
		
		
		private int n = 0;
		protected String doInBackground(String... urls) {
			String ret;
			try {
	        	TTLib t = new TTLib();
	        	String tmljson = t.getTxtStringFromUrl(meteo_file);
				ret = tmljson;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ret = "0";
			}
	    	return ret;	
		}
		
		
		protected void onPostExecute(String result) {
    		try {
				JSONObject jObject = new JSONObject(result);
				strjson = result;
	    		SendMeteo(strjson);
	    		
	    		String last_measure_time = jObject.getString("last_measure_time");
	    		//Log.d("AUDIO",last_measure_time);
	    		
	    		SimpleDateFormat  format = new SimpleDateFormat("[dd/MM/yyyy-HH:mm:ss]");  
	    		Date theMeteoDate = null;
	    		try {  
	    			theMeteoDate =  format.parse(last_measure_time);  
 
	    		} catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//Log.d("AUDIO","ERROR");
					return;
				}
	    		
	    		//Log.d("MeteoService",theMeteoDate.toString());
	    		
	    		Date theNowDate = new Date();

	    		//Log.d("MeteoService",theNowDate.toString());
	    				
	    		long diff = ( theNowDate.getTime() - theMeteoDate.getTime() ) / (1000 * 60);
	    		
	    		
	    		//Log.d("MeteoService",Long.toString(diff));
	    		

	    		if ( diff > 10 ) 
	    			return;
	    		
	    		
	    		final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	    		final boolean bAudio = sharedPrefs.getBoolean("bAudio", false);
	    		final int Audio_repetition_time = Integer.valueOf(sharedPrefs.getString("Audio_repetition_time","5"));
	    		if ( bAudio ) {
	    			//Log.d("AUDIO","audio");
					Thread thread = new Thread() {
					    @Override
					    public void run() {
							//SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
							//boolean bAudio = sharedPrefs.getBoolean("bAudio", false);
							//int Audio_repetition_time = Integer.valueOf(sharedPrefs.getString("Audio_repetition_time","5"));
							long audioTime = System.currentTimeMillis();
							//Log.d("AUDIO",Long.toString( audioTime- lastAudioTime));
							if ( bAudio &&  ( (audioTime -  lastAudioTime) + 30 >  Audio_repetition_time*60*1000 )  ) {
								lastAudioTime = audioTime;
								try {
									playaudio(strjson);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
							n++; 
					    }
					};

					thread.start();
	    		}
				

//				long audioTime = System.currentTimeMillis();
//				if ( bAudio &&  ( audioTime -  lastAudioTime >  Audio_repetition_time*1000 )  ) {
//					lastAudioTime = audioTime;
//					try {
//						playaudio(strjson);
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//				}
//				n++;
//				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}
	}


	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onDestroy()
	{
        Log.d("MainActivity", "ssSTOP");


        
        mNotificationManager.cancelAll();

	}
	
	
	@Override
	public void onCreate() 
	{			
		//Log.d(TAG,"onCreate");
    	setupNotifications();
    	showNotification();
	}

	private void setupNotifications() { //called in onCreate()
	    if (mNotificationManager == null) {
	        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	    }
	    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
	            new Intent(this, MainActivity.class)
	                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
	    
//	    Intent intent = new Intent("action_close_app");
//	    PendingIntent pendingCloseIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//	    
//	    PendingIntent pendingCloseIntent=PendingIntent.getActivity(this, 0, intent,	0);
	    
	    
	    
	    PendingIntent pendingCloseIntent = PendingIntent.getActivity(this, 0,
	            new Intent(this, MainActivity.class)
	                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP )
	                    .setAction(CLOSE_ACTION),  0);
	      

	    
	    
	    mNotificationBuilder
	            .setSmallIcon(R.drawable.ic_launcher)
//	            .setCategory(NotificationCompat.CATEGORY_SERVICE)
//	            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
	            .setContentTitle(getText(R.string.app_name))
//	            .setWhen(System.currentTimeMillis())
	            .setContentIntent(pendingIntent)
//	            .setAutoCancel(true)
//	            .addAction(android.R.drawable.ic_menu_close_clear_cancel,"Close", pendingCloseIntent)
	            .setOngoing(true);
	    
	    //Yes intent
	    Intent yesReceive = new Intent();  
	    yesReceive.setAction(CLOSE_ACTION);
//	    Bundle yesBundle = new Bundle();            
//	    yesBundle.putInt("userAnswer", 1);//This is the value I want to pass
//	    yesReceive.putExtras(yesBundle);
	    PendingIntent pendingIntentYes = PendingIntent.getBroadcast(this, 12345, yesReceive, PendingIntent.FLAG_UPDATE_CURRENT);
	    mNotificationBuilder.addAction(android.R.drawable.ic_menu_close_clear_cancel, "Close", pendingIntentYes);	    	    
	    
	    
	}

	private void showNotification() {
	    mNotificationBuilder
	            .setTicker(getText(R.string.app_name))
	            .setContentText("Running..");
	    if (mNotificationManager != null) {
	        mNotificationManager.notify(NOTIFICATION, mNotificationBuilder.build());
	    }
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Bundle extras = intent.getExtras();
		meteo_file = (String) extras.get("METEO_FILE");
		
		new AsynGetMeteo().execute();
		Thread thread1 = new Thread() {
		    @Override
		    public void run() {
				while (true) {
					try {
						Calendar c = Calendar.getInstance(); 
						int seconds = c.get(Calendar.SECOND);
						sleep(60000-seconds*1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        	Log.d("MeteoService","Start AsynGetMeteo");
					new AsynGetMeteo().execute();
				}
		    }
		};
		thread1.start();
		
//		Thread thread = new Thread() {
//		    @Override
//		    public void run() {
//		    	
//		        try {
//		        	int n = 0;
//		        	String prevstrjson = "";
//		        	TTLib t = new TTLib();
//		        	boolean bSync = false;
//		        	
//		        	try { 
//		        		String tmljson = t.getTxtStringFromUrl(meteo_file);
//	            		prevstrjson = tmljson;
//	            		JSONObject jObject = new JSONObject(tmljson);
//	            		SendMeteo(strjson);
//		        	} catch(Exception e) {
//	            		
//	            	}	
//		        	
//		        	while ( ! bSync ) {
//		            	try { 
//		            		String tmljson = t.getTxtStringFromUrl(meteo_file);
//		            		if ( !  prevstrjson.equals(tmljson) ) {
//			            		Log.d(TAG, "Sincronized");
//		            			strjson = tmljson;
//		            			SendMeteo(strjson);
//		            			prevstrjson = strjson;
//		            			bSync = true;
//		            		}
//		            		else {
//			            		Log.d(TAG, "Sleeping 2000");
//		            			sleep(2000);
//		            		}
//			        	} catch(Exception e) {
//		            		
//		            	}	
//		        	}
//		        	
//		            while(true) {
//	            		Log.d(TAG, "Sleeping 60");
//		            	sleep(60000);
//		            	String tmljson = t.getTxtStringFromUrl(meteo_file );
//		            	try { 
//		            		JSONObject jObject = new JSONObject(tmljson);
//		            		if (  ! prevstrjson.equals(tmljson )) {
//		            			strjson = tmljson;
//		            			SendMeteo(strjson);
//		            			prevstrjson = strjson;
//		            			
//								SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//								boolean bAudio = sharedPrefs.getBoolean("bAudio", false);
//								int Audio_repetition_time = Integer.valueOf(sharedPrefs.getString("Audio_repetition_time","5"));
//								
//								if ( bAudio &&  ( n % Audio_repetition_time == 0 )  ) {
//									playaudio(strjson);
//								}
//								n++;
//		            			
//		            		}
//		            		
//		            		
//		            	} catch(Exception e) {
//		            		
//		            	}
//		                
//		            }
//		        } catch (InterruptedException e) {
//		            e.printStackTrace();
//		        }
//		    }
//		};
//		thread.start();
		
		
 
		
		return Service.START_STICKY;
		//return Service.START_NOT_STICKY;
		
	}

	
	
	private void playaudio(String  jsonString) throws JSONException  {
		
    	//Log.d("MeteoService","playaudio");

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
	
	
//	private void playaudio(JSONObject jObject) throws JSONException {
//		
//		String wind_dir_code  = jObject.getString("wind_dir_code");
//		String wind_ave  = jObject.getString("wind_ave");
//		String wind_gust  = jObject.getString("wind_gust");
//
//		String str;
//		str = "winddirection.mp3";
//		playmp3(str);
//		str = wind_dir_code.toLowerCase()+".mp3";
//		playmp3(str);
//		str = "from.mp3";
//		playmp3(str);
//		str = String.format("n%d.mp3",Math.round(Double.parseDouble(wind_ave)));
//		playmp3(str);
//		str = "to.mp3";
//		playmp3(str);
//		str = String.format("n%d.mp3",Math.round(Double.parseDouble(wind_gust)));
//		playmp3(str);
//
//	}
	
	public void playmp3(String strMp3) {
	    try {
	    	if ( isPlayingAudio )
	    		return;
	    	isPlayingAudio = true;
	    	MediaPlayer m = new MediaPlayer();
	        AssetFileDescriptor descriptor = getAssets().openFd("audio/"+strMp3);
	        m.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
	        descriptor.close();

	        m.prepare();
	        m.setVolume(1f, 1f);
	        m.setLooping(false);
	        m.setAudioStreamType(AudioManager.STREAM_MUSIC);
	        m.start();

	        while ( m.isPlaying() ) {
	        	Thread.sleep(100);
	        }
	        Thread.sleep(200);
	        m.release();	
	        m = null;
	        isPlayingAudio = false;
	    } catch (Exception e) {
	    	isPlayingAudio = false;
	    }
	}
	
	
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
	
	
	private void SendMeteo(String strMeteo) {
		Intent BroadcastIntent = new Intent();
		BroadcastIntent.setAction("com.swpi.sintwindpi.INTENT_ACTION_METEOCHANGED");
		
		BroadcastIntent.putExtra("METEO",strMeteo);


		sendBroadcast(BroadcastIntent);
	}
}
