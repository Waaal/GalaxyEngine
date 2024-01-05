package de.galaxy.light;

import com.jogamp.opengl.GL4;

import de.galaxy.core.ShaderProgram;
import de.galaxy.math.Vector3;

public abstract class Light 
{
	public static final int DIRECTION_LIGHT = 0;
	public static final int POINT_LIGHT = 1;
	public static final int SPOT_LIGHT = 2;
	
	public Vector3 position;
	public Vector3 direction;
	public Vector3 color;
	
	public float diffuse = 0.5f;
	public float ambient = 0.1f;
	
	protected int type;
	
	public Light(Vector3 position) 
	{
		this.position = position;
		
		direction = new Vector3(0,0,0);
		color = new Vector3(1.0f, 1.0f, 1.0f);
	}
	
	public Light(Vector3 position, Vector3 direction) 
	{
		this.position = position;
		this.direction = direction;
		
		color = new Vector3(1.0f, 1.0f, 1.0f);
	}
	
	public void fillShader(GL4 gl, ShaderProgram shader, int number) 
	{
		
	}
	
	public int getType() 
	{
		return type;
	}

}
