package de.galaxy.core;

import com.jogamp.opengl.GL4;

import de.galaxy.math.Matrix4;
import de.galaxy.math.Vector3;

public class Model 
{
	public Vector3 position;
	public Vector3 scale;
	
	public Mesh[] meshes;
	
	private String name;
	private int trianglesCount;
	private int memorySize;
	
	private Vector3 rotationAxis = new Vector3(1,1,1);
	private float rotationRadiant = 0f;
	Boolean hasRotation = false;
	
	//make Model hold textures. Not single meshes
	
	public Model(Mesh[] meshes, String name, int trianglesCount) 
	{
		this.name = name;
		this.trianglesCount = trianglesCount;
		
		this.meshes = meshes;
		
		memorySize = (trianglesCount * 96)/1024;
		
		scale = new Vector3(1,1,1);
		position = new Vector3(0,5,0);
	}
	
	public void Rotate(float radiant, Vector3 axis) 
	{
		hasRotation = true;
		rotationAxis = axis;
		rotationRadiant = radiant;
	}
	
	public void render(GL4 gl, ShaderProgram shader) 
	{
		Matrix4 model = new Matrix4(1.0f);
		model.Translate(position);
		model.Scale(scale);
		//rotation
		
		int modelLocation = gl.glGetUniformLocation(shader.getID(), "model");
		gl.glUniformMatrix4fv(modelLocation, 1, false, model.value_ptr(), 0);
		
		for(Mesh mesh : meshes)
			mesh.render(gl, shader);
	}
	
	public void dispose(GL4 gl) 
	{
		for(Mesh mesh : meshes)
			mesh.dispose(gl);
	}
	
	@Override
	public String toString() 
	{
		return "----------\nModel: " + name + "\nMeshes: " + meshes.length + "\nTriangles: " + trianglesCount + "\nMemorySize: " + memorySize+"Kb\n----------";
	}
	
}
