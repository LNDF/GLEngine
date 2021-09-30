package lander.glengine.asset;

public enum AssetSourceType {
	RESOURCE("resource"), FILE("file");
	
	private String name;
	
	private AssetSourceType(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
