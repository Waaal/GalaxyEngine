package de.galaxy.core;

import java.util.Random;

import com.jogamp.opengl.GL4;

import de.galaxy.math.Matrix4;
import de.galaxy.math.Vector3;

public class Instanciator 
{
	private Vao vao;
	private int count;
	private Matrix4 model;
	
	private float[] offsets;
	
	private VertexAttribPointer[] attrib = new VertexAttribPointer[] 
	{
			new VertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 3*4, 0)
	};
	
	private VertexAttribPointer[] attrib2 = new VertexAttribPointer[] 
	{
			new VertexAttribPointer(1, 3, GL4.GL_FLOAT, false, 6*4, 0, 1),
			new VertexAttribPointer(2, 3, GL4.GL_FLOAT, false, 6*4, 3*4, 1)
	};
	
	public Instanciator(GL4 gl, float[] vertices, int count) 
	{
		this.count = count;
		model = new Matrix4(1.0f);
		
		offsets = new float[count*6];
		
		float yCounter = 0.0f;
		float colorCounter = 0.0f;
		for(int i = 0; i < count*6; i+=6) 
		{
			offsets[i] = 0.0f;
			offsets[i+1] = yCounter+1.0f;
			offsets[i+2] = 0.0f;
			
			offsets[i+3] = colorCounter;
			offsets[i+4] = colorCounter;
			offsets[i+5] = colorCounter;
			
			colorCounter += 0.01f;
			
			yCounter+=1.0f;
		}
		
		vao = new Vao(gl, vertices, GL4.GL_TRIANGLES, attrib);
		
		Vbo tempVbo = new Vbo(gl, offsets);
		vao.addVbo(gl, tempVbo, attrib2);
	}
	
	public void render(GL4 gl, ShaderProgram shader) 
	{
		shader.setMat4(gl, "model", model); 
		
		gl.glBindVertexArray(vao.getID());
		
		gl.glDrawArraysInstanced(vao.getDrawing(),  0, vao.getVec3Number(), count);
		
		gl.glBindVertexArray(0);
	}
}
