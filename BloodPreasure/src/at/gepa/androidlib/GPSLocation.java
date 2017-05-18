package at.gepa.androidlib;

import android.location.Location;
import android.location.LocationManager;

public class GPSLocation {
	
	private LocationManager locationManager;

	public GPSLocation(LocationManager lom)
	{
		this.locationManager = lom;
	}
	
	public Location getLastBestLocation() {
	    Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	    Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

	    long GPSLocationTime = 0;
	    if (null != locationGPS) 
	    { 
	    	GPSLocationTime = locationGPS.getTime(); 
	    }

	    long NetLocationTime = 0;

	    if (null != locationNet) 
	    {
	        NetLocationTime = locationNet.getTime();
	    }

	    if ( NetLocationTime - GPSLocationTime > 0 ) 
	    {
	        return locationNet;
	    }
        return locationGPS;
	}
	public static enum eGPSTyp
	{
		Sateliten,
		Netzwerk,
		No
	}

	public eGPSTyp getLastBestLocationDevice() {
		if( locationManager == null ) return eGPSTyp.No;
		
	    Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	    
	    long GPSLocationTime = 0;
	    if (null != locationGPS) 
	    { 
	    	GPSLocationTime = locationGPS.getTime(); 
	    }

	    Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	    long NetLocationTime = 0;

	    if (null != locationNet) 
	    {
	        NetLocationTime = locationNet.getTime();
	    }

	    if ( NetLocationTime - GPSLocationTime > 0 ) 
	    {
	        return eGPSTyp.Netzwerk;
	    }
	    if( GPSLocationTime > 0 )
	    	return eGPSTyp.Sateliten;
	    
		return eGPSTyp.No;
	}
}
