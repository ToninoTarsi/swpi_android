package com.swpi.sintwindpi;

public class Station {
	
	public Station(int tID,String tNAME,float tLAT,float tLON,String tURL,String tWEBCAM,String tNOTES,String tTEL)
	{
		ID = tID;
		NAME = tNAME;
		LAT = tLAT;
		LON=tLON;
		URL = tURL;
		WEBCAM = tWEBCAM;
		NOTES = tNOTES;
		TEL = tTEL;
		
	}
	
	public Station()
	{
		ID = 0;
		NAME = null;
		LAT = 0;
		LON=0;
		URL = null;
		WEBCAM = null;
		NOTES = null;
		TEL = null;
		
	}
	
	
	public int ID;
	public String NAME;
	public float LAT;
	public float LON;
	public String URL;
	public String WEBCAM;
	public String NOTES;
	public String TEL;

}
