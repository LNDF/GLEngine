package com.lndf.glengine.asset;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

public interface AssetSource {
	
	public InputStream getInputStream(String path) throws FileNotFoundException;
	public OutputStream getOutputStream(String path) throws FileNotFoundException;
	
}
