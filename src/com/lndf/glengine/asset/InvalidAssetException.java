package com.lndf.glengine.asset;

public class InvalidAssetException extends RuntimeException {

	private static final long serialVersionUID = 6026721388748437357L;
	
	public InvalidAssetException(String error) {
		super(error);
	}

}
