package at.gepa.androidlib;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;


public class IpUtils 
{
	public static final String _255 = "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
	public static final Pattern pattern = Pattern.compile("^(?:" + _255 + "\\.){3}" + _255 + "$");

	public static String longToIpV4(long longIp) 
	{
	    int octet3 = (int) ((longIp >> 24) % 256);
	    int octet2 = (int) ((longIp >> 16) % 256);
	    int octet1 = (int) ((longIp >> 8) % 256);
	    int octet0 = (int) ((longIp) % 256);
	
	    return octet3 + "." + octet2 + "." + octet1 + "." + octet0;
	}
	
	public static long ipV4ToLong(String ip) {
	    String[] octets = ip.split("\\.");
	    return (Long.parseLong(octets[0]) << 24) + (Integer.parseInt(octets[1]) << 16)
	            + (Integer.parseInt(octets[2]) << 8) + Integer.parseInt(octets[3]);
	}

	public static boolean isIPv4Private(String ip) {
		long longIp = ipV4ToLong(ip);
		return (longIp >= ipV4ToLong("10.0.0.0") && longIp <= ipV4ToLong("10.255.255.255"))
            || (longIp >= ipV4ToLong("172.16.0.0") && longIp <= ipV4ToLong("172.31.255.255"))
            || longIp >= ipV4ToLong("192.168.0.0") && longIp <= ipV4ToLong("192.168.255.255");
	}

	public static boolean isIPv4Valid(String ip) 
	{
		return pattern.matcher(ip).matches();
	}

	public static boolean isLoopback(String ip) 
	{
		return ip.equals("127.0.0.1");
	}

	
	
	
	
	private static Pattern VALID_IPV4_PATTERN = null;
	private static Pattern VALID_IPV6_PATTERN = null;
	private static final String ipv4Pattern = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
	private static final String ipv6Pattern = "([0-9a-f]{1,4}:){7}([0-9a-f]){1,4}";

	static {
		try {
			VALID_IPV4_PATTERN = Pattern.compile(ipv4Pattern, Pattern.CASE_INSENSITIVE);
			VALID_IPV6_PATTERN = Pattern.compile(ipv6Pattern, Pattern.CASE_INSENSITIVE);
		} catch (PatternSyntaxException e) {
			//logger.severe("Unable to compile pattern", e);
		}
	}

	/**
	 * Determine if the given string is a valid IPv4 or IPv6 address.  This method
	 * uses pattern matching to see if the given string could be a valid IP address.
	 *
	 * @param ipAddress A string that is to be examined to verify whether or not
	 *  it could be a valid IP address.
	 * @return <code>true</code> if the string is a value that is a valid IP address,
	 *  <code>false</code> otherwise.
	 */
	public static boolean isIpAddress(String ipAddress) {
		Matcher m1 = VALID_IPV4_PATTERN.matcher(ipAddress);
		if (m1.matches()) {
			return true;
		}
		Matcher m2 = VALID_IPV6_PATTERN.matcher(ipAddress);
		return m2.matches();
	}

	public static boolean isIpv4Address(String ipAddress) {
		Matcher m1 = VALID_IPV4_PATTERN.matcher(ipAddress);
		return m1.matches();
	}

	public static boolean isIpv6Address(String ipAddress) {
		Matcher m1 = VALID_IPV6_PATTERN.matcher(ipAddress);
		return m1.matches();
	}
	
	
	
	public static String getLocalIpAddress(final boolean removeIPv6) 
	{
		final ArrayList<String> list = new ArrayList<String>();
		final ArrayList<String> ret = new ArrayList<String>();
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
						NetworkInterface intf = en.nextElement();
						for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
							InetAddress inetAddress = enumIpAddr.nextElement();
							//list.add( inetAddress.getCanonicalHostName() + ": "+ inetAddress.getHostAddress().toString() );
							
							if (inetAddress.isSiteLocalAddress() &&
									!inetAddress.isAnyLocalAddress() &&
									(!removeIPv6 || isIpv4Address(inetAddress.getHostAddress().toString())) ) {
								ret.add( inetAddress.getHostAddress().toString() );
							}
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
		
		if( ret.size() == 0 )
			return "";
		return ret.get(0);
	}	

	public static String getIPAddress(Context context)
	{
		String ip = null;
		if( context == null )
		{
			return ip;
		}
		WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		if( wifiManager != null )
		{
			WifiInfo info = wifiManager.getConnectionInfo();
	    	if (info!=null && info.getNetworkId()>-1) 
	    	{
		    	int i = info.getIpAddress();
		        ip = String.format(Locale.ENGLISH,"%d.%d.%d.%d", i & 0xff, i >> 8 & 0xff,i >> 16 & 0xff,i >> 24 & 0xff);
	    	}
		}
		if( ip == null )
		{
			ip = getLocalIpAddress(true);
    	}
		
		return ip;
	}

	public static String getLocalIpAddress4Server() throws Exception 
	{
	    String resultIpv6 = "";
	    String resultIpv4 = "";
	      
	      for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); 
	        en.hasMoreElements();) {

	        NetworkInterface intf = en.nextElement();
	        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); 
	            enumIpAddr.hasMoreElements();) {

	            InetAddress inetAddress = enumIpAddr.nextElement();
	            if(!inetAddress.isLoopbackAddress()){
	              if (inetAddress instanceof Inet4Address) {
	                resultIpv4 = inetAddress.getHostAddress().toString();
	                } else if (inetAddress instanceof Inet6Address) {
	                  resultIpv6 = inetAddress.getHostAddress().toString();
	                }
	            }
	        }
	    }
	    return ((resultIpv4.length() > 0) ? resultIpv4 : resultIpv6);
	}	

}
