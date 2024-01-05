package de.galaxy.math;

public class Vector3 
{
	//public static Vector3 ZERO = new Vector3(0,0,0);
	//public static Vector3 X_ONE = new Vector3(1,0,0);
	//public static Vector3 Y_ONE = new Vector3(0,1,0);
	//public static Vector3 Z_ONE = new Vector3(0,0,1);
	//public static Vector3 ONE = new Vector3(1,1,1);
	
	public float X,Y,Z;
	
	public Vector3(float x, float y, float z)
	{
		X = x;
		Y = y;
		Z = z;
	}
	
	public Vector3 Add(Vector3 vec) 
	{
		return new Vector3(X+vec.X,Y+vec.Y,Z+vec.Z);
	}
	
	public Vector3 Add(float f) 
	{
		return new Vector3(X+f,Y+f,Z+f);
	}
	
	public Vector3 Sub(Vector3 vec) 
	{
		return new Vector3(X-vec.X,Y-vec.Y,Z-vec.Z);
	}
	
	public Vector3 Sub(float f) 
	{
		return new Vector3(X-f,Y-f,Z-f);
	}
	
	public Vector3 Mul(Vector3 vec) 
	{
		return new Vector3(X*vec.X,Y*vec.Y,Z*vec.Z);
	}
	
	public Vector3 Mul(float f) 
	{
		return new Vector3(X*f,Y*f,Z*f);
	}
	
	public Vector3 Div(Vector3 vec) 
	{
		return new Vector3(X/vec.X,Y/vec.Y,Z/vec.Z);
	}
	
	public Vector3 Div(float f) 
	{
		return new Vector3(X/f,Y/f,Z/f);
	}
	
	public Vector3 Normalize() 
	{
		float length = (float) Math.sqrt((X*X) + (Y*Y) + (Z*Z));
		return new Vector3(X/length, Y/length, Z/length);
	}
	
	//Returns in radiant
	public float Dotproduct(Vector3 vec) 
	{
		float multiplied = (X*vec.X)+(Y*vec.Y)+(Z*vec.Z);
		return (float)Math.acos(multiplied);
		
	}
	
	public Vector3 Crossproduct(Vector3 vec) 
	{
		return new Vector3((Y*vec.Z)-(Z*vec.Y),(Z*vec.X)-(X*vec.Z),(X*vec.Y)-(Y*vec.X));
	}
	
	public float Length() 
	{
		return (float)Math.sqrt((double)((X * X)+(Y * Y)+(Z * Z)));
	}
	
	public String toString() 
	{
		return Math.round(X*100.0f) / 100.0f + "," + Math.round(Y*100.0f) / 100.0f + "," + Math.round(Z*100.0f) / 100.0f; 
	}
}
