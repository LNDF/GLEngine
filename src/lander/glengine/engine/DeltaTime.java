package lander.glengine.engine;

public class DeltaTime {
	
	private static long deltaTime;
	private static long lastTime;
	
	public static void set() {
		long time = System.currentTimeMillis();
		DeltaTime.deltaTime = time - DeltaTime.lastTime;
		DeltaTime.lastTime = time;
	}
	
	public static double get() {
		return (double) DeltaTime.deltaTime / 1000.0;
	}
	
}
