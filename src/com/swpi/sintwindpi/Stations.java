package com.swpi.sintwindpi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParserException;



import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Stations extends Activity {
	private static final String TAG = "swpi-stations";
	private ListView lv;
    private ItemAdapter adapter;
    private ArrayList<Station> stationlist = new ArrayList<Station>();
    
    
	private class ItemAdapter extends ArrayAdapter<Station> {

		private ArrayList<Station> objects;

		
		public ItemAdapter(Context context, int textViewResourceId, ArrayList<Station> objects) {
			super(context, textViewResourceId, objects);
			this.objects = objects;
		}

		public View getView(int position, View convertView, ViewGroup parent){

			View v = convertView;
			
			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.rowstationlayout, null);
			}

			Station i = objects.get(position);

			SharedPreferences settings  = getSharedPreferences("swpi_stations", 0);
			int currentID = settings.getInt("ID", 0);
			
			if (i != null) {

				TextView TextViewID = (TextView) v.findViewById(R.id.ID);
				TextView TextViewNANE = (TextView) v.findViewById(R.id.NAME);
					
				ImageView im  = ( ImageView) v.findViewById(R.id.imageViewCheckBox);
				
				
				if ( im != null )
				{
					im.setVisibility(View.INVISIBLE);
					if ( currentID == i.ID)
					{
						im.setVisibility(View.VISIBLE);
					}
				}
				
				if (TextViewID != null){
					TextViewID.setText(Integer.toString(i.ID));
				}
				if (TextViewNANE != null){
					TextViewNANE.setText(i.NAME);
				}
			}

			return v;

		}

	}
	
	private class DownloadXMLTask extends AsyncTask<String, Void, String> {
		ListView lv;
		public  DownloadXMLTask() {
		}

		protected String doInBackground(String... urls) {
		String xml = "";
			
		try {
	   		HttpClient httpClient = new DefaultHttpClient();
    		// Creating HTTP Post
    		//HttpPost httpPost = new HttpPost(url);
    		HttpGet httpPost = new HttpGet(urls[0]);
    		
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity httpEntity = response.getEntity();
            xml = EntityUtils.toString(httpEntity);
               
    		// writing response to log
    		Log.d(TAG, xml);
    		return xml;
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
        			
		    return "";
		  }

		  protected void onPostExecute(String result) {
				String xmlStations = result;
		        StationsXMLParser sp = new StationsXMLParser();
		        try {
					stationlist = sp.parsexml(xmlStations);
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        initWidgets();
		        adapter.notifyDataSetChanged();
		        
		  }
	}
	
	
	private void initWidgets() {
		
		lv = (ListView) findViewById(R.id.listView1);
        adapter = new ItemAdapter(this, R.layout.rowstationlayout, stationlist);
        lv.setAdapter(adapter);
        
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View v, int pos,
                    long arg3) 
            {
            	
            	Station st = stationlist.get(pos);
            	
            	 SharedPreferences settings  = getSharedPreferences("swpi_stations", 0);
            	 Editor edit = settings.edit();
            	 
            	 
            	 edit.putInt("ID",stationlist.get(pos).ID );
            	 edit.putString("NAME",stationlist.get(pos).NAME );
            	 edit.putFloat("LAT",stationlist.get(pos).LAT );
            	 edit.putFloat("LON",stationlist.get(pos).LON );
            	 edit.putString("URL",stationlist.get(pos).URL );
            	 edit.putString("WEBCAM",stationlist.get(pos).WEBCAM );
            	 edit.putString("TEL",stationlist.get(pos).TEL );
            	 edit.putString("NOTES",stationlist.get(pos).NOTES );
            	 edit.commit();
            	 
                 Toast.makeText(getApplicationContext(),stationlist.get(pos).NAME ,Toast.LENGTH_LONG).show();
                 
     			Intent intMain = new Intent(v.getContext(),MainActivity.class);
    			startActivity(intMain);


            }
        });
       
    }
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stations);
		
		new DownloadXMLTask().execute("http://www.vololiberomontecucco.it/jessica2/swpi_stations_new.php");
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stations, menu);
		return true;
	}

}
