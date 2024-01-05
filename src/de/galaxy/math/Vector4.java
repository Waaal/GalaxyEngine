package de.galaxy.math;

public class Vector4 
{
	public static Vector4 ZERO = new Vector4(0,0,0,0);
	public static Vector4 ONE = new Vector4(1,1,1,1);
	
	public float X,Y,Z,W;
	
	public Vector4(float x, float y, float z, float w)
	{
		X = x;
		Y = y;
		Z = z;
		W = w;
	}
	
	public Vector4 Add(Vector4 vec) 
	{
		return new Vector4(X+vec.X,Y+vec.Y,Z+vec.Z,W+vec.W);
	}
	
	public Vector4 Add(float f) 
	{
		return new Vector4(X+f,Y+f,Z+f,W+f);
	}
	
	public Vector4 Sub(Vector4 vec) 
	{
		return new Vector4(X-vec.X,Y-vec.Y,Z-vec.Z,W-vec.W);
	}
	
	public Vector4 Sub(float f) 
	{
		return new Vector4(X-f,Y-f,Z-f,W-f);
	}
	
	public Vector4 Mul(Vector4 vec) 
	{
		return new Vector4(X*vec.X,Y*vec.Y,Z*vec.Z,W*vec.W);
	}
	
	public Vector4 Mul(float f) 
	{
		return new Vector4(X*f,Y*f,Z*f,W*f);
	}
	
	public Vector4 Div(Vector4 vec) 
	{
		return new Vector4(X/vec.X,Y/vec.Y,Z/vec.Z,W/vec.W);
	}
	
	public Vector4 Div(float f) 
	{
		return new Vector4(X/f,Y/f,Z/f,W/f);
	}
	
	public Vector4 Normalize() 
	{
		float length = (float) Math.sqrt((X*X) + (Y*Y) + (Z*Z)+ (W*W));
		return new Vector4(X/length, Y/length, Z/length, W/length);
	}
	
	//Returns in radiant
	public float Dotproduct(Vector4 vec) 
	{
		float multiplied = (X*vec.X)+(Y*vec.Y)+(Z*vec.Z)+(W*vec.W);
		return (float)Math.acos(multiplied);
		
	}
	
	public String toString() 
	{
		return Math.round(X*100.0f) / 100.0f + "," + Math.round(Y*100.0f) / 100.0f + "," + Math.round(Z*100.0f) / 100.0f + "," + Math.round(W*100.0f) / 100.0f; 
	}
}
