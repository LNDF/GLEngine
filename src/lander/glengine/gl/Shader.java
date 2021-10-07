package lander.glengine.gl;

import static org.lwjgl.opengl.GL33.*;

import java.io.IOException;
import java.util.HashMap;

import org.joml.Matrix4f;
import lander.glengine.asset.Asset;
import lander.glengine.engine.Task;
import lander.glengine.engine.Utils;
import lander.glengine.engine.Window;

public class Shader {
	
	public static String SHADER_VERSION = "#version 330 core";
	
	public enum ShaderType {
		VERTEX_SHADER(GL_VERTEX_SHADER),
		FRAGMENT_SHADER(GL_FRAGMENT_SHADER),
		GEOMETRY_SHADER(GL_GEOMETRY_SHADER);
		
		private int type;
		
		private ShaderType(int type) {
			this.type = type;
		}
		
		public int getType() {
			return this.type;
		}
	}
	
	private int id;
	private HashMap<String, Integer> uniformCache = new HashMap<String, Integer>();
	
	private boolean closed = false;
	
	protected static int boundShader = 0;
	
	public static int compileShader(int type, String src) {
		int shader = glCreateShader(type);
		glShaderSource(shader, src);
		glCompileShader(shader);
		if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
			String error = glGetShaderInfoLog(shader);
			System.err.println("Error in shader:\n" + Utils.addLineNumbers(src));
			System.err.println(error);
			glDeleteShader(shader);
			return 0;
		}
		return shader;
	}
	
	public static String readShader(Asset asset) {
		try {
			return asset.getString();
		} catch (IOException e) {
			System.out.println("ERROR: couldn't load shader " + asset.toString());
		}
		return null;
	}
	
	public Shader(String[] vertexSrcs, String[] fragmentSrcs, String[] geometrySrcs) {
		int vs, fs, gs = 0;
		String vertexSrc = String.join("\n", vertexSrcs);
		String fragmentSrc = String.join("\n", fragmentSrcs);
		this.id = glCreateProgram();
		vs = Shader.compileShader(ShaderType.VERTEX_SHADER.getType(), vertexSrc);
		fs = Shader.compileShader(ShaderType.FRAGMENT_SHADER.getType(), fragmentSrc);
		glAttachShader(this.id, vs);
		glAttachShader(this.id, fs);
		if (geometrySrcs != null) {
			String geometryScr = String.join("\n", geometrySrcs);
			gs = Shader.compileShader(ShaderType.GEOMETRY_SHADER.getType(), geometryScr);
			glAttachShader(this.id, gs);
		}
		glLinkProgram(this.id);
		int linked = glGetProgrami(this.id, GL_LINK_STATUS);
		if (linked == GL_FALSE) {
			String error = glGetProgramInfoLog(this.id);
			glDeleteProgram(this.id);
			System.err.println("Error linking program: " + error);
			this.id = 0;
		} else glValidateProgram(this.id);
		glDeleteShader(vs);
		glDeleteShader(fs);
		if (geometrySrcs != null) glDeleteShader(gs);
	}
	
	public void close() {
		if (this.closed) return;
		this.closed = true;
		Window.getWindow().addEndOfLoopTask(new Task() {
			@Override
			public void execute() {
				glDeleteProgram(Shader.this.id);
			}
		});
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setUniform(String name, int v1) {
		glUniform1i(this.getUniformLocation(name), v1);
	}
	
	public void setUniform(String name, float f1) {
		glUniform1f(this.getUniformLocation(name), f1);
	}
	
	public void setUniform(String name, float f1, float f2) {
		glUniform2f(this.getUniformLocation(name), f1, f2);
	}
	
	public void setUniform(String name, float f1, float f2, float f3) {
		glUniform3f(this.getUniformLocation(name), f1, f2, f3);
	}
	
	public void setUniform(String name, float f1, float f2, float f3, float f4) {
		glUniform4f(this.getUniformLocation(name), f1, f2, f3, f4);
	}
	
	public void setUniform(String name, Matrix4f v1) {
		float[] floats = new float[16];
		v1.get(floats);
		glUniformMatrix4fv(this.getUniformLocation(name), false, floats);
	}
	
	public void setUniform(String name, boolean v1) {
		glUniform1i(this.getUniformLocation(name), v1 ? 1 : 0);
	}
	
	public int getUniformLocation(String name) {
		this.bind();
		if (this.uniformCache.containsKey(name)) return this.uniformCache.get(name);
		int loc = glGetUniformLocation(this.id, name);
		if (loc == -1) {
			System.out.println("WARNING: Uniform " + name + " not used.");
		}
		this.uniformCache.put(name, loc);
		return loc;
	}
	
	public void bind() {
		if (Shader.boundShader == this.id) return;
		Shader.boundShader = this.id;
		glUseProgram(this.id);
	}
	
	public static void unbind() {
		if (Shader.boundShader == 0) return;
		Shader.boundShader = 0;
		glUseProgram(0);
	}
}
