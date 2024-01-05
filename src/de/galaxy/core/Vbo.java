package de.galaxy.core;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL4;

public class Vbo 
{
	private IntBuffer ID;
	
	public Vbo(GL4 gl, float[] vertices) 
	{
		ID = IntBuffer.allocate(1);
		
		gl.glGenBuffers(1, ID);
		
		FloatBuffer verticesBuffer = FloatBuffer.allocate(vertices.length);
		for(int i = 0; i < vertices.length; i++)
			verticesBuffer.array()[i] = vertices[i];
					
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, ID.array()[0]);
		gl.glBufferData(GL4.GL_ARRAY_BUFFER, 4*vertices.length, verticesBuffer, GL4.GL_STATIC_DRAW);
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
