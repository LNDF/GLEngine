package com.lndf.glengine.engine;

import java.awt.Color;
import java.nio.ByteBuffer;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.AIQuaternion;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;
import org.lwjgl.system.MemoryStack;

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
	
	public static void decomposeAssimpMatrix4x4(AIMatrix4x4 matrix, Vector3f position, Vector3f scale, Quaternionf rotation) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			AIVector3D assimpPos = AIVector3D.callocStack(stack);
			AIVector3D assimpScale = AIVector3D.callocStack(stack);
			AIQuaternion assimpRot = AIQuaternion.callocStack(stack);
			Assimp.aiDecomposeMatrix(matrix, assimpScale, assimpRot, assimpPos);
			position.set(assimpPos.x(), assimpPos.y(), assimpPos.z());
			scale.set(assimpScale.x(), assimpScale.y(), assimpScale.z());
			rotation.set(assimpRot.x(), assimpRot.y(), assimpRot.z(), assimpRot.w());
		}
	}
	
}
