package com.lndf.glengine.asset;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

import com.lndf.glengine.engine.Utils;

public class Asset {
	
	private static final HashMap<String, AssetSource> SOURCE_TYPES = new HashMap<String, AssetSource>();
	
	public static void registerAsssetSource(String name, AssetSource source) {
		Asset.SOURCE_TYPES.put(name, source);
	}
	
	public static void unregisterAssetSource(String name) {
		Asset.SOURCE_TYPES.remove(name);
	}
	
	static {
		Asset.registerAsssetSource("file", new AssetSourceFile());
		Asset.registerAsssetSource("resource", new AssetSourceResource());
	}
	
	private String path;
	private String sourceName;
	private AssetSource source;
	private String directory;
	
	public Asset(String path) {
		String[] parts = path.split(":");
		if (parts.length < 2) throw new InvalidAssetException("Incorrect asset format: " + path);
		this.sourceName = parts[0];
		this.path = String.join(":", Arrays.copyOfRange(parts, 1, parts.length));
		if (!Asset.SOURCE_TYPES.containsKey(sourceName)) throw new InvalidAssetException("Incorrect asset source: " + sourceName);
		this.source = Asset.SOURCE_TYPES.get(sourceName);
	}
	
	public String getRelativeString(String path) {
		return this.sourceName + ":" + this.getDirectory() + path;
	}
	
	public Asset getRelativeAsset(String path) {
		return new Asset(this.getRelativeString(path));
	}
	
	public InputStream getInputStream() throws FileNotFoundException {
		return this.source.getInputStream(path);
	}
	
	public OutputStream getOutputStream() throws FileNotFoundException {
		return this.source.getOutputStream(path);
	}
	
	public byte[] getBytes() throws IOException  {
		InputStream is = null;
		is = this.getInputStream();
		if (is == null) throw new FileNotFoundException(this.path);
		byte[] b = null;
		try {
			b = is.readAllBytes();
		} catch (IOException e) {
			is.close();
			throw e;
		}
		is.close();
		return b;
	}
	
	public ByteBuffer getByteBuffer() throws FileNotFoundException, IOException {
		return Utils.byteBufferFromArray(this.getBytes());
	}
	
	public String getString() throws FileNotFoundException, IOException {
		return new String(this.getBytes(), StandardCharsets.UTF_8);
	}

	public String getPath() {
		return path;
	}
	
	public String getDirectory() {
		if (this.directory == null)
			this.directory = this.path.substring(0, this.path.lastIndexOf('/') + 1);
		return this.directory;
		
	}
	
	@Override
	public String toString() {
		return this.sourceName + ":" + this.path;
	}
	
}
