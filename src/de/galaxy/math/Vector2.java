package de.galaxy.math;

public class Vector2 
{
	public static Vector2 ZERO = new Vector2(0,0);
	public static Vector2 X_ONE = new Vector2(1,0);
	public static Vector2 Y_ONE = new Vector2(0,1);
	public static Vector2 ONE = new Vector2(1,1);
	
	public float X,Y;
	
	public Vector2(float x, float y)
	{
		X = x;
		Y = y;
	}
	
	public Vector2 Add(Vector2 vec) 
	{
		return new Vector2(X+vec.X,Y+vec.Y);
	}
	
	public Vector2 Add(float f) 
	{
		return new Vector2(X+f,Y+f);
	}
	
	public Vector2 Sub(Vector2 vec) 
	{
		return new Vector2(X-vec.X,Y-vec.Y);
	}
	
	public Vector2 Sub(float f) 
	{
		return new Vector2(X-f,Y-f);
	}
	
	public Vector2 Mul(Vector2 vec) 
	{
		return new Vector2(X*vec.X,Y*vec.Y);
	}
	
	public Vector2 Mul(float f) 
	{
		return new Vector2(X*f,Y*f);
	}
	
	public Vector2 Div(Vector2 vec) 
	{
		return new Vector2(X/vec.X,Y/vec.Y);
	}
	
	public Vector2 Div(float f) 
	{
		return new Vector2(X/f,Y/f);
	}
	
	public Vector2 Normalize() 
	{
		float length = (float) Math.sqrt((X*X) + (Y*Y));
		return new Vector2(X/length, Y/length);
	}
	
	//Returns in radiant
	public float Dotproduct(Vector2 vec) 
	{
		float multiplied = (X*vec.X)+(Y*vec.Y);
		return (float)Math.acos(multiplied);
		
	}
	
	public String toString() 
	{
		return Math.round(X*100.0f) / 100.0f + "," + Math.round(Y*100.0f) / 100.0f; 
	}
}
