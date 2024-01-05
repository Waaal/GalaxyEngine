package de.galaxy.math;

public class Matrix3 
{
	public float[][] mat;
	public Matrix3(Matrix4 mat4) 
	{
		mat = new float[3][3];
		for(int x = 0; x < 3; x++) 
		{
			for(int y = 0; y < 3; y++) 
			{
				mat[x][y] = mat4.mat[x][y];
			}
		}
	}
}
