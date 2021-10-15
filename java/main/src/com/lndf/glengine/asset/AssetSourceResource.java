package com.lndf.glengine.asset;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

public class AssetSourceResource implements AssetSource {

	@Override
	public InputStream getInputStream(String path) throws FileNotFoundException {
		return AssetSourceResource.class.getResourceAsStream(path);
	}

	@Override
	public OutputStream getOutputStream(String path) throws FileNotFoundException {
		throw new UnsupportedOperationException("Resources are read-only");
	}

}
