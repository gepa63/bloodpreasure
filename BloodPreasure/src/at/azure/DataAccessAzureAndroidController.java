package at.azure;

import at.gepa.net.DataAccessAzureController;


public class DataAccessAzureAndroidController extends DataAccessAzureController {

	public static final String DEFAULT_ACCOUNT = "bloodpreasure";
	public static final String DEFAULT_CONTAINER = "bpshare";

	/*
	private String account;
	private String key;
	private String container;
	public DataAccessAzureAndroidController() {
	}
*/
	public DataAccessAzureAndroidController(String account, String key, String filename, String container) {
		super(account, key, filename, container);
	}
	/*
	public String getAccount()
	{
		return account;
	}
	public String getKey()
	{
		return key;
	}

	public String getContainer() {
		return container;
	}

	@Override
	public boolean validate()  throws Exception
	{
		super.validate();
		if( container == null || container.isEmpty() ) throw new Exception("Container ist leer!");
		if( key == null || key.isEmpty() ) throw new Exception("Key ist leer!");
		if( account == null || account.isEmpty() ) throw new Exception("Account ist leer!");
		return true;
	}

	public String getStorageConnectionString() {
		String subKey = "vvoFt9hzq76Ufdc8L6+7zQcbTCgONDJGgsBQnjSVAFlm4u+u02mHJ/x0I97pJDTRV+yEXfPTW01g==";
		String _key = "";
		if( getKey().length() <= 10 )
			_key = getKey() + subKey;
		else
			_key = getKey();
		return String.format("DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s;EndpointSuffix=core.windows.net", 
				getAccount(), _key);
	}
	*/
}
