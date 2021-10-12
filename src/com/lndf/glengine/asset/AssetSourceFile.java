package com.lndf.glengine.asset;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class AssetSourceFile implements AssetSource {

	@Override
	public InputStream getInputStream(String path) throws FileNotFoundException {
		return new FileInputStream(path);
	}

	@Override
	public OutputStream getOutputStream(String path) throws FileNotFoundException {
		return new FileOutputStream(path);
	}
	
}
