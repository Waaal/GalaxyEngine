package de.galaxy.core;

import java.nio.IntBuffer;

import com.jogamp.opengl.GL4;

import de.luke.openglTest.Texture2D;

public class cubeMapTexture  extends Texture
{
	public IntBuffer ID;
	public boolean loadSuccessFull = true;
	
	public cubeMapTexture(GL4 gl, ShaderProgram shader, String[] paths) 
	{
		if(paths.length != 6) 
		{
			loadSuccessFull = false;
			System.out.println("[Galaxy Error]: Error creating cubeMapTexture. Paths must have the length of 6");
			return;
		}
		
		ID = IntBuffer.allocate(1);
		gl.glGenTextures(1, ID);
		gl.glActiveTexture(GL4.GL_TEXTURE0);
		gl.glBindTexture(GL4.GL_TEXTURE_CUBE_MAP, ID.array()[0]);
		
		shader.setInt(gl, "skybox", 0);
		
		for(int i = 0; i < paths.length; i++) 
		{
			Texture2D temp = new Texture2D(paths[i], false);
			if(temp.bytes != null) 
			{
				gl.glTexImage2D(GL4.GL_TEXTURE_CUBE_MAP_POSITIVE_X +i, 0, temp.type, temp.getWidth(), temp.getHeight(), 0, temp.type, GL4.GL_UNSIGNED_BYTE, temp.bytes);
			}
			else 
			{
				loadSuccessFull = false;
			}
		}
		
		gl.glTexParameteri(GL4.GL_TEXTURE_CUBE_MAP, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
		gl.glTexParameteri(GL4.GL_TEXTURE_CUBE_MAP, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR);
		
		gl.glTexParameteri(GL4.GL_TEXTURE_CUBE_MAP, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL4.GL_TEXTURE_CUBE_MAP, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL4.GL_TEXTURE_CUBE_MAP, GL4.GL_TEXTURE_WRAP_R, GL4.GL_CLAMP_TO_EDGE); 
	}
	
	@Override
	public void bind(GL4 gl) 
	{
		gl.glActiveTexture(GL4.GL_TEXTURE0);
		gl.glBindTexture(GL4.GL_TEXTURE_CUBE_MAP, ID.array()[0]);
	}
	
	@Override
	public void dispose(GL4 gl) 
	{
		gl.glDeleteTextures(1, ID);
	}
}
