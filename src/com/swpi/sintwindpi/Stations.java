package com.swpi.sintwindpi;

import java.io.BufferedReader;
import java.io.IOException;
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
import android.os.Bundle;
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
	private ListView lv;
    private ItemAdapter adapter;
    private ArrayList<Station> stationlist = new ArrayList<Station>();
    
    
	public class ItemAdapter extends ArrayAdapter<Station> {

		// declaring our ArrayList of items
		private ArrayList<Station> objects;

		/* here we must override the constructor for ArrayAdapter
		* the only variable we care about now is ArrayList<Station> objects,
		* because it is the list of objects we want to display.
		*/
		public ItemAdapter(Context context, int textViewResourceId, ArrayList<Station> objects) {
			super(context, textViewResourceId, objects);
			this.objects = objects;
		}

		/*
		 * we are overriding the getView method here - this is what defines how each
		 * list Station will look.
		 */
		public View getView(int position, View convertView, ViewGroup parent){

			// assign the view we are converting to a local variable
			View v = convertView;

			// first check to see if the view is null. if so, we have to inflate it.
			// to inflate it basically means to render, or show, the view.
			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.rowstationlayout, null);
			}

			/*
			 * Recall that the variable position is sent in as an argument to this method.
			 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
			 * iterates through the list we sent it)
			 * 
			 * Therefore, i refers to the current Station object.
			 */
			Station i = objects.get(position);

			SharedPreferences settings  = getSharedPreferences("swpi_stations", 0);
			int currentID = settings.getInt("ID", 0);
			
			if (i != null) {

				// This is how you obtain a reference to the TextViews.
				// These TextViews are created in the XML files we defined.

				TextView TextViewID = (TextView) v.findViewById(R.id.ID);
				TextView TextViewNANE = (TextView) v.findViewById(R.id.NAME);
				
				

				// check to see if each individual textview is null.
				// if not, assign some text!
				
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

			// the view must be returned to our activity
			return v;

		}

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stations);
		
		TTLib t = new TTLib();
		
		String xmlStations = t.getXMLStringFromUrl("http://www.vololiberomontecucco.it/jessica2/swpi_stations.php");
 
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stations, menu);
		return true;
	}

}
