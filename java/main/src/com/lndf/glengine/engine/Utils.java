package com.lndf.glengine.engine;

import java.awt.Color;
import java.nio.ByteBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AIMatrix4x4;

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
	
	public static Matrix4f fromAssimpMatrix4x4(AIMatrix4x4 input) {
		return new Matrix4f(
				input.a1(), input.b1(), input.c1(), input.d1(),
				input.a2(), input.b2(), input.c2(), input.d2(),
				input.a3(), input.b3(), input.c3(), input.d3(),
				input.a4(), input.b4(), input.c4(), input.d4()
		);
	}
	
}
