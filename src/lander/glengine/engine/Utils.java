package lander.glengine.engine;

import java.awt.Color;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

public class Utils {
	
	public static float[] getColorAsRGBA8FloatArray(Color color) {
		float r = (float) color.getRed() / 255;
		float g = (float) color.getGreen() / 255;
		float b = (float) color.getBlue() / 255;
		float a = (float) color.getAlpha() / 255;
		return new float[] {r, g, b, a};
	}
	
	public static String addLineNumbers(String str) {
		if (str == null) return null;
		String[] lines = str.split("\\r?\\n");
		for (int i = 0; i < lines.length; i++) {
			lines[i] = (i + 1) + ":	" + lines[i];
		}
		return String.join("\n", lines);
	}
	
	public static ByteBuffer byteBufferFromArray(byte[] bytes) {
		return BufferUtils.createByteBuffer(bytes.length).put(bytes).flip();
	}
	
}
