package com.syndarin.erdi.synchronizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.util.Log;

public class MD5Generator {
	
	private final String TAG=this.getClass().getSimpleName();
	
	public String getFileMD5(File file){
		InputStream is=null;
		try {
			is=new FileInputStream(file);
			int qBytes=is.available();
			byte[] fileBytes=new byte[qBytes];
			Log.i(TAG, "Total bytes "+qBytes);
			
			is.read(fileBytes, 0, qBytes);
			
			MessageDigest digest=MessageDigest.getInstance("MD5");
			digest.update(fileBytes);
			
			byte[] mDigest=digest.digest();
			
			StringBuilder hexString=new StringBuilder();
			for (int i = 0; i < mDigest.length; i++) {
                hexString.append(String.format("%02x", (0xFF & mDigest[i])));
            }
			Log.i(TAG, hexString.toString());
            return hexString.toString();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

}
