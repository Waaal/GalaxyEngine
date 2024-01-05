package de.galaxy.core;

import de.galaxy.math.*;
import de.luke.openglTest.Engine;

import java.awt.Point;
import java.nio.FloatBuffer;

import de.galaxy.input.InputHandler;

public class Camera 
{
	private Vector3 pos;
	private Vector3 front;
	
	private Matrix4 view;
	
	private float cameraSpeed = 4.0f;
	
	private float yaw = -90.0f;
	private float pitch = 0.0f;
	
	public Camera(Vector3 pos) 
	{
		this.pos = pos;
		
		front = new Vector3(0.0f, 0.0f, -1.0f);
		
		view = new Matrix4(1.0f);
		view.LookAt(pos, pos.Add(front), new Vector3(0,1,0));
	}
	
	public Matrix4 getMat() 
	{
		return view;
	}
	
	public void Update() 
	{
		if(Engine.lockMouse) 
		{
			float speed = cameraSpeed * Engine.deltaTime;
			
			Point p = InputHandler.getMouseMove();
			
			yaw += p.x;
			pitch += p.y;
			
			if(pitch > 89.99f)
				  pitch =  89.99f;
			if(pitch < -89.99f)
				  pitch = -89.99f;
			
			Vector3 direction = new Vector3(0.0f, 0.0f, 0.0f);
			direction.X = (float)Math.cos(GalacticMath.DegreeToRadiant(yaw)) * (float)Math.cos(GalacticMath.DegreeToRadiant(pitch));
			direction.Y = -1 * (float)Math.sin(GalacticMath.DegreeToRadiant(pitch));
			direction.Z = (float)Math.sin(GalacticMath.DegreeToRadiant(yaw)) * (float)Math.cos(GalacticMath.DegreeToRadiant(pitch));
			
			front = direction.Normalize();
			
			if(InputHandler.getKeyPress(InputHandler.KEY_W)) 
				pos = pos.Add(front.Mul(speed));
			if(InputHandler.getKeyPress(InputHandler.KEY_S)) 
				pos = pos.Sub(front.Mul(speed));
			if(InputHandler.getKeyPress(InputHandler.KEY_A)) 
				pos = pos.Sub((front.Crossproduct(new Vector3(0,1,0))).Normalize().Mul(speed));
			if(InputHandler.getKeyPress(InputHandler.KEY_D)) 
				pos = pos.Add((front.Crossproduct(new Vector3(0,1,0))).Normalize().Mul(speed));
			
			view.LookAt(pos, pos.Add(front), new Vector3(0,1,0));	
		}
	}
	
	public float[] getView() 
	{
		return view.value_ptr();
	}
	
	public FloatBuffer getBuffer() 
	{
		return view.getBuffer();
	}
	
	public Vector3 getPos() 
	{
		return pos;
	}
	
	public Vector3 getFront() 
	{
		return front;
	}
}
