package at.gepa.androidlib.security;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

public class PasswordEncryptionDecryption {
	
	private String plainPassword;
	private byte[] ciphertext;
	private String encryptedPwd;

	public PasswordEncryptionDecryption(String pwd)
	{
		this.plainPassword = pwd;
		ciphertext = null;
	}
	public PasswordEncryptionDecryption( String encryptedPwd, boolean decrypt )
	{
		this.plainPassword = null;
		this.encryptedPwd = encryptedPwd;
		ciphertext = Base64.decode(encryptedPwd, Base64.DEFAULT);
	}
	
	public String encrypt() throws Exception 
	{
		if (plainPassword == null || plainPassword.length() == 0) throw new Exception("Empty string");

		SecretKeySpec sks = createSecretKey();

        // Encode the original data with AES
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, sks);
            ciphertext = c.doFinal(this.plainPassword.getBytes());
        } catch (Exception e) {
            throw e;
        }
        
		return new String(Base64.encodeToString( ciphertext, Base64.DEFAULT));
	}

	private SecretKeySpec createSecretKey() throws Exception {
		SecretKeySpec sks = null;
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG","Crypto");
        sr.setSeed( getClass().getName().getBytes() );
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(128, sr);
        sks = new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
		return sks;
	}
	public String decrypt() throws Exception 
	{
		if (encryptedPwd == null || encryptedPwd.length() == 0) throw new Exception("Empty string");

		byte [] decrypted = null;
		try {
            Cipher c = Cipher.getInstance("AES");
            
    		SecretKeySpec sks = createSecretKey();
            
            c.init(Cipher.DECRYPT_MODE, sks );
            decrypted = c.doFinal(ciphertext);
        } catch (Exception e) {
            throw e;
        }

		this.plainPassword = new String(decrypted, "UTF-8");// new String(Base64.encodeToString( decrypted, Base64.DEFAULT)); 
		return plainPassword;
	}
	public byte [] getBytes() {
		return ciphertext;
	}
	public void setBytes(byte[] bytes) {
		this.ciphertext = bytes;
	}
}
