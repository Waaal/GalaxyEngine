package de.galaxy.math;

import java.nio.FloatBuffer;

public class Matrix4 
{
	public float[][] mat;
	public Matrix4() 
	{
		mat = new float[4][4];
	}
	
	public Matrix4(float f) 
	{
		mat = new float[4][4];	
		for(int i = 0; i < 4; i++) 
		{
			if(i != 3)
				mat[i][i] = f;	
			else
				mat[i][i] = 1;
		}
	}
	
	public Matrix4(Matrix3 mat3) 
	{
		mat = new float[4][4];
		for(int x = 0; x < 4; x++) 
		{
			for(int y = 0; y < 4; y++) 
			{
				if(x > 2) 
				{
					mat[x][y] = 0f;
				}
				else 
				{
					if(y > 2) 
					{
						mat[x][y] = 0f;
					}
					else 
					{
						mat[x][y] = mat3.mat[x][y];	
					}
				}
			}
		}
	}
	
	public void Perspective(float fov, float aspect, float nearDist, float farDist) 
	{
		float[][] temp = new float[4][4];
		
	    final float top    =  (float)Math.tan(fov/2f) * nearDist;
	    final float bottom =  -1.0f * top; 
	    final float left   = aspect * bottom;
	    final float right  = aspect * top;
	      
	    float[] m = makeFrustum(left, right, bottom, top, nearDist, farDist);
		int counter = 0;
		for(int x = 0; x < 4; x++) 
		{
			for(int y = 0; y < 4; y++) 
			{
				temp[x][y] = m[counter];
				counter++;
			}
		}
	    
	    
		this.Mul(temp);
	}
	
	public void Orthographic(float left, float right, float bottom, float top, float nearDist, float farDist) 
	{
		float[][] temp = new float[4][4];
		
        final float dx=right-left;
        final float dy=top-bottom;
        final float dz=farDist-nearDist;
        final float tx=-1.0f*(right+left)/dx;
        final float ty=-1.0f*(top+bottom)/dy;
        final float tz=-1.0f*(farDist+nearDist)/dz;
        
        float[] m = new float[16];
        
        m[0] = 2.0f/dx;
        m[5] = 2.0f/dy;
        m[10] = -2.0f/dz;
        
        m[12] = tx;
        m[13] = ty;
        m[14] = tz;
        m[15] = 1.0f;    
        
		int counter = 0;
		for(int x = 0; x < 4; x++) 
		{
			for(int y = 0; y < 4; y++) 
			{
				temp[x][y] = m[counter];
				counter++;
			}
		}
		
		this.Mul(temp);
	}
	
	private float[] makeFrustum(float left, float right, float bottom, float top, float nearDist, float farDist) 
	{
		float[] m = new float[16];
		
	      final float zNear2 = 2.0f*nearDist;
	      final float dx=right-left;
	      final float dy=top-bottom;
	      final float dz=farDist-nearDist;
	      final float A=(right+left)/dx;
	      final float B=(top+bottom)/dy;
	      final float C=-1.0f*(farDist+nearDist)/dz;
	      final float D=-2.0f*(farDist*nearDist)/dz;
	      
	      m[0+4*0] = zNear2/dx;

	      m[1+4*1] = zNear2/dy;

	      m[0+4*2] = A;
	      m[1+4*2] = B;
	      m[2+4*2] = C;
	      m[3+4*2] = -1.0f;

	      m[2+4*3] = D;
	      m[3+4*3] = 0f;
	      
	      return m;
	}
	
	private void Mul(float[][] mat) 
	{
		float[][] tempMat = new float[4][4];
		
		int rc = 0, cc = 0;
		for(int y = 0; y < 4; y++) 
		{
			for(int x = 0; x < 4; x++) 
			{
				tempMat[x][y] = (this.mat[0][rc]*mat[cc][0])+(this.mat[1][rc]*mat[cc][1])+(this.mat[2][rc]*mat[cc][2])+(this.mat[3][rc]*mat[cc][3]);
				cc++;
			}
			cc = 0;
			rc++;
		}
		
		this.mat = tempMat;
	}
	
	public void Mul(Matrix4 matrix) 
	{
		Mul(matrix.mat);
	}
	
	public void Mul(Vector3 vec) 
	{
		float[][] temp = new float[4][4];
		temp[0][0] = 1.0f*vec.X;
		temp[1][1] = 1.0f*vec.Y;
		temp[2][2] = 1.0f*vec.Z;
		temp[3][3] = 1;
		
		this.Mul(temp);
	}
	
	public void Translate(Vector3 vec) 
	{
		float[][] temp = new float[4][4];
		for(int i = 0; i < 4; i++) 
				temp[i][i] = 1.0f;	
		temp[3][0] = vec.X;
		temp[3][1] = vec.Y;
		temp[3][2] = vec.Z;
		temp[3][3] = 1.0f;
		
		this.Mul(temp);
	}
	
	public void Scale(Vector3 vec) 
	{
		float[][] temp = new float[4][4];
		temp[0][0] = vec.X;
		temp[1][1] = vec.Y;
		temp[2][2] = vec.Z;
		temp[3][3] = 1.0f;
		this.Mul(temp);
	}
	
	public void Rotate(float radiant, Vector3 axis) 
	{
		float[][] temp = new float[4][4];
	
		axis = axis.Normalize();
		
		float x = axis.X;
		float y = axis.Y;
		float z = axis.Z;
		
		float c = (float)Math.cos(radiant);
		float ic = 1.0f - c;
		float s = (float)Math.sin(radiant);
		
		float xy = x*y;
		float xz = x*z;
		float xs = x*s;
		float ys = y*s;
		float yz = y*z;
		float zs = z*s;
		
		temp[0][0] = x*x*ic+c;
		temp[1][0] = xy*ic-zs;
		temp[2][0] = xz*ic+ys;
		temp[3][0] = 0.0f;
		
		temp[0][1] = xy*ic+zs;
		temp[1][1] = y*y*ic+c;
		temp[2][1] = yz*ic-xs;
		temp[3][1] = 0.0f;
		
		temp[0][2] = xz*ic-ys;
		temp[1][2] = yz*ic+xs;
		temp[2][2] = z*z*ic+c;
		temp[3][2] = 0.0f;
		
		temp[0][3] = 0.0f;
		temp[1][3] = 0.0f;
		temp[2][3] = 0.0f;
		temp[3][3] = 1.0f;
		
		this.Mul(temp);
	}
	
	public void LookAt(Vector3 position, Vector3 target, Vector3 up) 
	{
		float[][] temp = new float[4][4];
		Vector3 cameraDirection = (position.Sub(target)).Normalize();
		Vector3 cameraRight = (up.Crossproduct(cameraDirection)).Normalize();
		Vector3 cameraUp = (cameraDirection.Crossproduct(cameraRight)).Normalize();
		
		temp[0][0] = cameraRight.X;
		temp[1][0] = cameraRight.Y;
		temp[2][0] = cameraRight.Z;
		temp[3][0] = 0.0f;
		
		temp[0][1] = cameraUp.X;
		temp[1][1] = cameraUp.Y;
		temp[2][1] = cameraUp.Z;
		temp[3][1] = 0.0f;
		
		temp[0][2] = cameraDirection.X;
		temp[1][2] = cameraDirection.Y;
		temp[2][2] = cameraDirection.Z;
		temp[3][2] = 0.0f;
		
		temp[0][3] = 0.0f;
		temp[1][3] = 0.0f;
		temp[2][3] = 0.0f;
		temp[3][3] = 1.0f;
		
		this.mat = temp;
		
		temp = new float[4][4];
		temp[0][0] = 1.0f;
		temp[1][1] = 1.0f;
		temp[2][2] = 1.0f;
		
		temp[3][0] = -position.X;
		temp[3][1] = -position.Y;
		temp[3][2] = -position.Z;
		temp[3][3] = 1.0f;
		this.Mul(temp);
	}
	
	public float[] value_ptr() 
	{
		float[] ret = new float[16];
		
		int counter = 0;
		for(int x = 0; x < 4; x++) 
		{
			for(int y = 0; y < 4; y++) 
			{
				ret[counter] = mat[x][y];
				counter++;
			}
		}
		
		return ret;
	}
	
	public FloatBuffer getBuffer() 
	{
		FloatBuffer temp = FloatBuffer.allocate(16);
		System.arraycopy(value_ptr(), 0, temp.array(), 0, 16);
		return temp;
	}
	
	
	public int getLength() 
	{
		return 16;
	}
	
	public String toString() 
	{
		String ret = "";
		
		for(int y = 0; y < 4; y++) 
		{
			for(int x = 0; x < 4; x++) 
				ret +=Math.round(mat[x][y]*100.0f) / 100.0f + " ";
			ret +="\n";
		}
		
		return ret;
	}
}
