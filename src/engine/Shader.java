package engine;

import org.lwjgl.opengl.ARBShaderObjects;

import misc.Util;

import static org.lwjgl.opengl.GL32.*;


public class Shader {
	
	private int shaderProgram;
	private boolean usable = false;
	
	public Shader(String vertShaderPath, String fragShaderPath) {
		String vertSource = Util.loadFile(vertShaderPath);
		String fragSource = Util.loadFile(fragShaderPath);
		
		if(vertSource.length() == 0) {
			System.err.println("Couldn't load vertex shader source " + vertShaderPath);
			return;
		}
		
		if(fragSource.length() == 0) {
			System.err.println("Couldn't load fragment shader source " + fragShaderPath);
			return;
		}
		
		int vertShader = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertShader, vertSource);
		glCompileShader(vertShader);
		
		if (ARBShaderObjects.glGetObjectParameteriARB(vertShader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL_FALSE) {
			 System.err.println("Error compiling vertex shader " + vertShaderPath);
			 System.err.println(Util.getShaderLogInfo(vertShader));
			 return;
		 }
		 
		
		int fragShader = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragShader, fragSource);
		glCompileShader(fragShader);
		
		 if (ARBShaderObjects.glGetObjectParameteriARB(fragShader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL_FALSE) {
			 System.err.println("Error compiling fragment shader " + fragShaderPath);
			 System.err.println(Util.getShaderLogInfo(fragShader));
			 return;
		 }
		
		shaderProgram = glCreateProgram();
		
		glAttachShader(shaderProgram, vertShader);
		glAttachShader(shaderProgram, fragShader);
		
		glBindFragDataLocation(shaderProgram, 0, "outColor");
		
		glLinkProgram(shaderProgram);
		
		if (ARBShaderObjects.glGetObjectParameteriARB(shaderProgram, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL_FALSE) {
			System.err.println("Error linking shader program " + vertShaderPath + " " + fragShaderPath);
			System.err.println(Util.getShaderLogInfo(shaderProgram));
            return;
        }
         
		glValidateProgram(shaderProgram);
		
		if (ARBShaderObjects.glGetObjectParameteriARB(shaderProgram, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL_FALSE) {
			System.err.println("Error validating shader program " + vertShaderPath + " " + fragShaderPath);
			System.err.println(Util.getShaderLogInfo(shaderProgram));
		}
		usable = true;
	}
	
	public int getShaderId() {
		return shaderProgram;
	}
	
	public boolean isUsable() {
		return usable;
	}
	
	public void use() {
		glUseProgram(this.shaderProgram);
	}
}
