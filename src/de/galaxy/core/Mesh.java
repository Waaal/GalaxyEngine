package de.galaxy.core;

import com.jogamp.opengl.GL4;

import de.galaxy.math.Vector3;
import de.luke.openglTest.Texture2D;

public class Mesh 
{
	public String name;
	
	public Texture[] textures;
	
	private Vao vao;
	private Vector3 color;
	
	private VertexAttribPointer[] cubeAttrib = new VertexAttribPointer[] 
			{
					new VertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 8*4, 0),
					new VertexAttribPointer(1, 3, GL4.GL_FLOAT, false, 8*4, 3*4),
					new VertexAttribPointer(2, 2, GL4.GL_FLOAT, false, 8*4, 6*4)
			};
	
	public Mesh(GL4 gl, float[] vertices, Texture[] textures, String name, Vector3 meshColor, VertexAttribPointer[] vertexAttribPointer) 
	{
		this.name = name;
		this.color = meshColor;
		if(textures != null)
			this.textures = textures;
		//else give material
		
		if(vertexAttribPointer != null)
			cubeAttrib = vertexAttribPointer;
		
		this.vao = new Vao(gl, vertices, GL4.GL_TRIANGLES, cubeAttrib);
	}
	
	public Mesh(GL4 gl, float[] vertices, int[] indices, Texture[] textures, String name, Vector3 meshColor, VertexAttribPointer[] vertexAttribPointer) 
	{
		this.name = name;
		this.color = meshColor;
		if(textures != null)
			this.textures = textures;
		//else give material
		
		if(vertexAttribPointer != null)
			cubeAttrib = vertexAttribPointer;
		
		this.vao = new Vao(gl, vertices, indices, GL4.GL_TRIANGLES, cubeAttrib);
	}
	
	public void render(GL4 gl, ShaderProgram shader) 
	{
		shader.setVec3(gl, "meshColor", color);
		
		if(textures != null) 
		{
			for(Texture t : textures) 
			{
				t.bind(gl);	
				//dont save shininess in texture. Make own material class or something
				shader.setFloat(gl, "material.shininess", t.highlightFocus);
			}
		}
		
		gl.glBindVertexArray(vao.getID());
		
		if(vao.elements)
			gl.glDrawElements(vao.getDrawing(), vao.getVec3Number(), GL4.GL_UNSIGNED_INT, 0);
		else
			gl.glDrawArrays(vao.getDrawing(), 0, vao.getVec3Number());
		
		gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		gl.glBindVertexArray(0);
	}
	
	public void dispose(GL4 gl) 
	{
		if(textures != null) 
		{
			for(Texture t : textures)
				t.dispose(gl);
		}
		
		if(vao != null)
			vao.dispose(gl);
	}
}
