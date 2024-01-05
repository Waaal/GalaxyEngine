package de.galaxy.core;

import java.nio.IntBuffer;

import com.jogamp.opengl.GL4;

public class Ebo 
{
	private IntBuffer ID;
	
	public Ebo(GL4 gl, int[] indices) 
	{
		ID = IntBuffer.allocate(1);
		
		gl.glGenBuffers(1, ID);
		gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, ID.array()[0]);
		
		IntBuffer temp = IntBuffer.allocate(indices.length);
		for(int i = 0; i < indices.length; i++)
			temp.array()[i] = indices[i];
		
		gl.glBufferData(GL4.GL_ELEMENT_ARRAY_BUFFER, 4*indices.length, temp, GL4.GL_STATIC_DRAW);
	}
	
	public int getID() 
	{
		return ID.array()[0];
	}
	
	public IntBuffer getBuffer() 
	{
		return ID;
	}
}
