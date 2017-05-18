package at.gepa.androidlib;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

public class PhoneHelper {
	
	private static final String BATTERY_CAPACITY_SYSFS="/sys/class/power_supply/battery/capacity";

	public static String isPhonePluggedIn(Activity a) 
	{
		  boolean charging = false;
		  String result = "No";
		  final Intent batteryIntent = a.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		  
		  int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		  boolean batteryCharge = status == BatteryManager.BATTERY_STATUS_CHARGING;
		 
		  int chargePlug = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
		  boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
		  boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
		 
		  if (batteryCharge) charging = true;
		  if (usbCharge) charging = true;
		  if (acCharge) charging = true;
		 
		  if (charging)
		  {
			  result = "Yes";
		  }
		  return result;
	}
	private static String readSysFsFile(String a_File) throws Exception 
	{ 
		java.io.FileReader a_Reader = null; 
		int a_Char; 
		String a_Content = "";
		try
		{
			a_Reader = new java.io.FileReader(a_File);
			while((a_Char = a_Reader.read()) != -1) 
			{ 
				// Ignore \n 
				if (a_Char != 10) 
				{ 
					a_Content += Character.toString((char) a_Char); 
				} 
			}
		}
		catch(Exception ex )
		{
			throw ex;
		}
		finally
		{
			if( a_Reader != null )
				a_Reader.close();
		}
		return a_Content; 
	}
	
	public static String getBatteryStatus() 
	{ 
		try 
		{ 
			String a_Capacity = readSysFsFile(BATTERY_CAPACITY_SYSFS); 
			if (a_Capacity.length() == 0) 
			{ 
				a_Capacity = "Error"; 
			} 
			return a_Capacity; 
		} 
		catch(Exception a_Ex)
		{ 
			return "Error"; 
		} 
	}
	
	private static boolean isWifiConnected(Context context) {
		return isConnectedToInet(context, ConnectivityManager.TYPE_WIFI);
    }

	private static boolean isMobileConnected(Context context) {
		return isConnectedToInet(context, ConnectivityManager.TYPE_MOBILE);
    }
	public static boolean hasHispeedNetwork(Context context) {
		boolean ret = false;
		if( isAirplaneModeOn(context) )
			return ret;
		if( isWifiConnected( context ) )
			ret = true;
		else if( !getNetworkClass( context ).isEmpty() )
			ret = true;
	    return ret;
	}
	
	public static boolean isConnectedToInet( Context context, int type )
	{
		boolean ret = false;
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == type) 
            {
                // connected to wifi or mobile provider's data plan
            	ret = true;
            } 
        } 
        return ret;
	}
	
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public static boolean isAirplaneModeOn(Context context) 
	{        
	    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) 
	    {
	        return Settings.System.getInt(context.getContentResolver(), 
	                Settings.System.AIRPLANE_MODE_ON, 0) != 0;          
	    } else {
	        return Settings.Global.getInt(context.getContentResolver(), 
	                Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
	    }       
	}
	
	public static String getNetworkClass(Context context) {
	    TelephonyManager mTelephonyManager = (TelephonyManager)
	            context.getSystemService(Context.TELEPHONY_SERVICE);
	    int networkType = mTelephonyManager.getNetworkType();
	    switch (networkType) {
	        case TelephonyManager.NETWORK_TYPE_GPRS:
	        case TelephonyManager.NETWORK_TYPE_EDGE:
	        case TelephonyManager.NETWORK_TYPE_CDMA:
	        case TelephonyManager.NETWORK_TYPE_1xRTT:
	        case TelephonyManager.NETWORK_TYPE_IDEN:
	            return "2G";
	        case TelephonyManager.NETWORK_TYPE_UMTS:
	        case TelephonyManager.NETWORK_TYPE_EVDO_0:
	        case TelephonyManager.NETWORK_TYPE_EVDO_A:
	        case TelephonyManager.NETWORK_TYPE_HSDPA:
	        case TelephonyManager.NETWORK_TYPE_HSUPA:
	        case TelephonyManager.NETWORK_TYPE_HSPA:
	        case TelephonyManager.NETWORK_TYPE_EVDO_B:
	        case TelephonyManager.NETWORK_TYPE_EHRPD:
	        case TelephonyManager.NETWORK_TYPE_HSPAP:
	            return "3G";
	        case TelephonyManager.NETWORK_TYPE_LTE:
	            return "4G";
	        default:
	            return "";
	    }
	}
	
	public static String getNetworkConnectStatus(Context context) {
		return getNetworkType(context, "Connected via ");
	}
	
	
	public static final String NETWORK_WIFI = "WiFi";
	public static final String NETWORK_SMS = "SMS";
	public static final String NETWORK_NOTCONNECTED = "Not Connected!";

	public static final String OK = "OK";
	
	public static String getNetworkType(Context context, String suffix) {
		
		String ret = suffix; 
		String con = "";
		if( isWifiConnected(context)  )
			con = NETWORK_WIFI;
		else
			con = getNetworkClass( context );
		if( con.isEmpty() )
		{
        	if( isMobileConnected( context ) )
    			con = NETWORK_SMS;
        	else
        		ret = NETWORK_NOTCONNECTED;
		}
		ret = ret + con;
		return ret;
	}


	public static boolean hasNetwork(Context context) {
		if( hasHispeedNetwork(context) )
			return true;
		return isMobileConnected( context );
	}


	public static ArrayList<InetAddress> getIPList() {
		final ArrayList<InetAddress> list = new ArrayList<InetAddress>();
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
						NetworkInterface intf = en.nextElement();
						for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
							InetAddress inetAddress = enumIpAddr.nextElement();
							list.add( inetAddress );
						}
					}
				} catch (SocketException ignore) {}
			}
		});
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return list;
	}
	
}
