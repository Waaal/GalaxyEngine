package de.galaxy.core;

import java.nio.IntBuffer;

import com.jogamp.opengl.GL4;

import de.luke.openglTest.Texture2D;

public class Vao 
{
	public Boolean elements = false;
	public Texture2D texture2Ds[];
	
	private int glDrawing;
	
	private IntBuffer vao;
	private Vbo vbo;
	private Ebo ebo;
	
	private int vec3Number;
	
	public Vao(GL4 gl, float vertices[], int drawingMethod, VertexAttribPointer[] attrib) 
	{
		glDrawing = drawingMethod;
		init(gl, vertices, null, attrib, null);
	}
	
	public Vao(GL4 gl, float vertices[], int[] indices, int drawingMethod, VertexAttribPointer[] attrib) 
	{
		glDrawing = drawingMethod;
		elements = true;
		
		init(gl, vertices, indices, attrib, null);
	}
	
	public Vao(GL4 gl, float vertices[], int drawingMethod, VertexAttribPointer[] attrib, Vbo vbo) 
	{
		glDrawing = drawingMethod;
		init(gl, vertices, null, attrib, vbo);
	}
	
	public void addVbo(GL4 gl, Vbo vbo, VertexAttribPointer[] attrib) 
	{
		gl.glBindVertexArray(vao.array()[0]);
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo.getID());
		
		for(int i = 0; i < attrib.length; i++) 
		{
			gl.glEnableVertexAttribArray(attrib[i].index);
			gl.glVertexAttribPointer(attrib[i].index, attrib[i].size, attrib[i].type, attrib[i].normalize, attrib[i].stride, attrib[i].pointer);	
			gl.glVertexAttribDivisor(attrib[i].index, attrib[i].divisor);
		}
		
		gl.glBindVertexArray(0);
	}
	
	private void init(GL4 gl, float vertices[], int[] indices, VertexAttribPointer[] attrib, Vbo vbo) 
	{			
		vao = IntBuffer.allocate(1);
		
		gl.glGenVertexArrays(1, vao);
		gl.glBindVertexArray(vao.array()[0]);
		
		if(vbo == null) 
			this.vbo = new Vbo(gl, vertices);
		else 
			gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo.getID());
		
		if(indices != null)
			this.ebo = new Ebo(gl, indices);
		
		for(int i = 0; i < attrib.length; i++) 
		{
			gl.glEnableVertexAttribArray(attrib[i].index);
			gl.glVertexAttribPointer(attrib[i].index, attrib[i].size, attrib[i].type, attrib[i].normalize, attrib[i].stride, attrib[i].pointer);	
			gl.glVertexAttribDivisor(attrib[i].index, attrib[i].divisor);
		}
		
		int divide = 0;
		switch(glDrawing) 
		{
			case GL4.GL_TRIANGLES:
				divide = 3;
				break;
			case GL4.GL_LINES:
				divide = 2;
				break;
		}
		
		if(!elements) 
			vec3Number = vertices.length / divide;
		else
			vec3Number = indices.length;
		
		//unbind
		gl.glBindVertexArray(0);
	}
	
	public int getVec3Number() 
	{
		return vec3Number;
	}
	
	public int getID() 
	{
		return vao.array()[0];
	}

	public Vbo getVbo() 
	{
		return vbo;
	}
	
	public int getDrawing() 
	{
		return glDrawing;
	}
	
	public void dispose(GL4 gl) 
	{
		gl.glDeleteVertexArrays(1, vao);
		
		if(vbo != null)
			gl.glDeleteBuffers(1, vbo.getBuffer());
		
		if(ebo != null)
			gl.glDeleteBuffers(1, ebo.getBuffer());
	}
}
