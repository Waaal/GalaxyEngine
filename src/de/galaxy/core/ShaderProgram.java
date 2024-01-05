package de.galaxy.core;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL4;

import de.galaxy.math.Matrix4;
import de.galaxy.math.Vector3;

public class ShaderProgram 
{
	private int ID;
	private List<Integer> shaderIds;
	
	private int shaderCount;
	private int shaderCompiled;
	
	public ShaderProgram() 
	{
		ID = 0;
		shaderIds = new ArrayList<Integer>();
		
		shaderCount = 0;
		shaderCompiled = 0;
	}
	
	public void Add(int shaderType, String file, GL4 gl) 
	{
		shaderCount++;
		
		String[] shaderSource = new String[] {AssetManager.LoadShaderFile(file)};
		if(shaderSource[0] != null) 
		{
			IntBuffer lengthBuffer = IntBuffer.allocate(1);
			lengthBuffer.array()[0] = shaderSource[0].length();
			
			int sh = gl.glCreateShader(shaderType);
			gl.glShaderSource(sh, 1, shaderSource, lengthBuffer);
			gl.glCompileShader(sh);
			
			if(!checkCompileShader(sh, gl)) 
				return;
			
			System.out.println("[Galaxy Log]: Shader added");
			shaderIds.add(sh);
			
			shaderCompiled++;
		}
		else 
		{
			System.out.println("[Galaxy Error]: Shader file not found");
		}
	}
	
	public boolean Create(GL4 gl) 
	{
		if(shaderIds.isEmpty()) 
		{
			System.out.println("[Galaxy Error]: No shader added to the program");
			return false;
		}
		
		if(shaderCount != shaderCompiled) 
		{
			System.out.println("[Galaxy Error]: Error compiling shaders");
			return false;
		}
		
		int id = gl.glCreateProgram();
		for(int s : shaderIds)
			gl.glAttachShader(id, s);
		
		gl.glLinkProgram(id);
		
		for(int s : shaderIds) 
			gl.glDeleteShader(s);
		
		if(!checkLink(id, gl)) 
		{
			System.out.println("[Galaxy Error]: Faild to create shader program");
			return false;
		}
		
		System.out.println("[Galaxy Log]: Shader program created");
		ID = id;
		return true;
	}
	
	public int getID() 
	{
		return ID;
	}
	
	public void setVec3(GL4 gl, String name, Vector3 vec3) 
	{
		gl.glUniform3f(gl.glGetUniformLocation(ID, name), vec3.X, vec3.Y, vec3.Z);
	}
	
	public void setFloat(GL4 gl, String name, float value) 
	{
		gl.glUniform1f(gl.glGetUniformLocation(ID, name), value);
	}
	
	public void setInt(GL4 gl, String name, int value) 
	{
		gl.glUniform1i(gl.glGetUniformLocation(ID, name), value);
	}
	
	public void setMat4(GL4 gl, String name, Matrix4 value) 
	{
		gl.glUniformMatrix4fv(gl.glGetUniformLocation(ID, name), 1, false, value.value_ptr(), 0);
	}
	
	private Boolean checkCompileShader(int shader, GL4 gl) 
	{
		IntBuffer success = IntBuffer.allocate(1);
		gl.glGetShaderiv(shader, GL4.GL_COMPILE_STATUS, success);
		if(success.get() == 0) 
		{
			ByteBuffer infoLog = ByteBuffer.allocate(512);
			gl.glGetShaderInfoLog(shader, 512, null, infoLog);
			
			System.out.println("[Galaxy Error]: ");
			for(Byte b : infoLog.array()) 
			{
				System.out.print((char) (b & 0xFF));
			}
			
			return false;
		}
		return true;
	}
	
	private Boolean checkLink(int shader, GL4 gl) 
	{
		IntBuffer success = IntBuffer.allocate(1);
		gl.glGetProgramiv(shader, GL4.GL_LINK_STATUS, success);
		if(success.get() == 0) 
		{
			ByteBuffer infoLog = ByteBuffer.allocate(512);
			gl.glGetProgramInfoLog(shader, 512, null, infoLog);
			
			System.out.println("[Galaxy Error]: ");
			for(Byte b : infoLog.array()) 
			{
				System.out.print((char) (b & 0xFF));
			}
			
			return false;
		}
		return true;
	}
}
