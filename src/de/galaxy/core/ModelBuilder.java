package de.galaxy.core;

import com.jogamp.opengl.GL4;

import de.galaxy.math.Vector2;
import de.galaxy.math.Vector3;
import de.luke.openglTest.Texture2D;

public class ModelBuilder 
{
	private static float blockVertices[] = new float[] 
		{
			    // positions          // normals           // texture coords
				//Back face
				-0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f,  0.0f,
		         0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  1.0f,  1.0f,
		         0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  1.0f,  0.0f,
		         0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  1.0f,  1.0f,
			    -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f,  0.0f,
		        -0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,  0.0f,  1.0f,
		        //Front face
		        -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  0.0f,  0.0f,
		         0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  1.0f,  0.0f,
		         0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,
		         0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,
		        -0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  0.0f,  1.0f,
		        -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  0.0f,  0.0f,
		        //Left face
		        -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  1.0f,  0.0f,
		        -0.5f,  0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  1.0f,  1.0f,
		        -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  0.0f,  1.0f,
		        -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  0.0f,  1.0f,
		        -0.5f, -0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  0.0f,  0.0f,
		        -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  1.0f,  0.0f,
		        //Right face
		         0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,
		         0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  0.0f,  1.0f,
		         0.5f,  0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  1.0f,  1.0f,
		         0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  0.0f,  1.0f,
		         0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  1.0f,  0.0f,
		         0.5f, -0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  0.0f,  0.0f,
		         //Bottom face
		        -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  0.0f,  1.0f,
		         0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  1.0f,  1.0f,
		         0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  1.0f,  0.0f,
		         0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  1.0f,  0.0f,
		        -0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  0.0f,  0.0f,
		        -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  0.0f,  1.0f,
		        //Top Face
		        -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,
		         0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  1.0f,  0.0f,
		         0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  1.0f,  1.0f,
		         0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  1.0f,  0.0f,
			    -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,
		        -0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  0.0f,  0.0f
			};
	private static float cubeMapVertices[] = {
		    // positions          
		    -1.0f,  1.0f, -1.0f,
		    -1.0f, -1.0f, -1.0f,
		     1.0f, -1.0f, -1.0f,
		     1.0f, -1.0f, -1.0f,
		     1.0f,  1.0f, -1.0f,
		    -1.0f,  1.0f, -1.0f,

		    -1.0f, -1.0f,  1.0f,
		    -1.0f, -1.0f, -1.0f,
		    -1.0f,  1.0f, -1.0f,
		    -1.0f,  1.0f, -1.0f,
		    -1.0f,  1.0f,  1.0f,
		    -1.0f, -1.0f,  1.0f,

		     1.0f, -1.0f, -1.0f,
		     1.0f, -1.0f,  1.0f,
		     1.0f,  1.0f,  1.0f,
		     1.0f,  1.0f,  1.0f,
		     1.0f,  1.0f, -1.0f,
		     1.0f, -1.0f, -1.0f,

		    -1.0f, -1.0f,  1.0f,
		    -1.0f,  1.0f,  1.0f,
		     1.0f,  1.0f,  1.0f,
		     1.0f,  1.0f,  1.0f,
		     1.0f, -1.0f,  1.0f,
		    -1.0f, -1.0f,  1.0f,

		    -1.0f,  1.0f, -1.0f,
		     1.0f,  1.0f, -1.0f,
		     1.0f,  1.0f,  1.0f,
		     1.0f,  1.0f,  1.0f,
		    -1.0f,  1.0f,  1.0f,
		    -1.0f,  1.0f, -1.0f,

		    -1.0f, -1.0f, -1.0f,
		    -1.0f, -1.0f,  1.0f,
		     1.0f, -1.0f, -1.0f,
		     1.0f, -1.0f, -1.0f,
		    -1.0f, -1.0f,  1.0f,
		     1.0f, -1.0f,  1.0f
		};
	
	private static float planeVertices[] = new float[] 
			{
				    // positions          // normals           // texture coords
			        -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  0.0f,  0.0f,
			         0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  1.0f,  0.0f,
			         0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,
			         0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  1.0f,  1.0f,
			        -0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  0.0f,  1.0f,
			        -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,  0.0f,  0.0f
			};
	
	public static Model CreateBlock(GL4 gl) 
	{
		Mesh blockMesh = new Mesh(gl, blockVertices, null, "blockMesh", new Vector3(0.7f, 0.2f, 0.4f), null);
		return new Model(new Mesh[] {blockMesh}, "blockModel", 12);
	}
	
	public static Model CreateBlock(GL4 gl, Texture2D[] textures) 
	{
		Mesh blockMesh = new Mesh(gl, blockVertices, textures, "blockMesh", new Vector3(0.7f, 0.2f, 0.4f), null);
		return new Model(new Mesh[] {blockMesh}, "blockModel", 12);
	}
	
	public static Model CreateCubeMapBlock(GL4 gl, cubeMapTexture texture) 
	{
		Mesh blockMesh = new Mesh(gl, cubeMapVertices, new Texture[] {texture}, "blockMesh", new Vector3(0.7f, 0.2f, 0.4f), new VertexAttribPointer[] {
				new VertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 3*4, 0)
		});
		return new Model(new Mesh[] {blockMesh}, "blockModel", 12);
	}
	
	public static Model CreatePlane(GL4 gl) 
	{
		Mesh planeMesh = new Mesh(gl, planeVertices, null, "planeMesh", new Vector3(0.7f, 0.2f, 0.4f), null);
		return new Model(new Mesh[] {planeMesh}, "planeModel", 2);
	}
	
	public static Model CreatePlane(GL4 gl, Texture2D[] textures, boolean generateTangentSpace) 
	{
		Mesh planeMesh = null;
		if(generateTangentSpace) 
		{
			float[] temp = calcTangentSpace(planeVertices);
			
			planeMesh = new Mesh(gl, temp, textures, "planeMesh", new Vector3(0.7f, 0.2f, 0.4f), new VertexAttribPointer[] {
					new VertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 11*4, 0),
					new VertexAttribPointer(1, 3, GL4.GL_FLOAT, false, 11*4, 3*4),
					new VertexAttribPointer(2, 2, GL4.GL_FLOAT, false, 11*4, 6*4),
					new VertexAttribPointer(3, 3, GL4.GL_FLOAT, false, 11*4, 8*4)
			});
		}
		else 
		{
			planeMesh = new Mesh(gl, planeVertices, textures, "planeMesh", new Vector3(0.7f, 0.2f, 0.4f), null);
		}
		return new Model(new Mesh[] {planeMesh}, "planeModel", 2);
	}

	private static float[] calcTangentSpace(float[] vertices)
	{
		float[] tangentSpace = new float[((vertices.length/8)/3)*3];
		int verticesCount = 0;
		int tangentSpaceMul = 0;

		Vector3[] positions = new Vector3[] {new Vector3(0,0,0), new Vector3(0,0,0), new Vector3(0,0,0)};
		Vector2[] textCoords = new Vector2[] {new Vector2(0,0), new Vector2(0,0), new Vector2(0,0)};
		Vector3[] normals = new Vector3[] {new Vector3(0,0,0), new Vector3(0,0,0), new Vector3(0,0,0)};

		int vertexCount = -1;
		for(int i = 0; i < vertices.length; i++)
		{
			vertexCount++;
			if(vertexCount == 0)
			{
				positions[verticesCount].X = vertices[i];
			}
			else if(vertexCount == 1)
			{
				positions[verticesCount].Y = vertices[i];
			}
			else if(vertexCount == 2)
			{
				positions[verticesCount].Z = vertices[i];
			}
			else if(vertexCount == 3)
			{
				normals[verticesCount].X = vertices[i];
			}
			else if(vertexCount == 4)
			{
				normals[verticesCount].Y = vertices[i];
			}
			else if(vertexCount == 5)
			{
				normals[verticesCount].Z = vertices[i];
			}
			else if(vertexCount == 6)
			{
				textCoords[verticesCount].X = vertices[i];
			}
			else if(vertexCount == 7)
			{
				textCoords[verticesCount].Y = vertices[i];

				verticesCount++;
				if(verticesCount == 3)
				{
					Vector3 edge1 = positions[1].Sub(positions[0]);
					Vector3 edge2 = positions[2].Sub(positions[0]);

					Vector2 deltaUV1 = textCoords[1].Sub(textCoords[0]);
					Vector2 deltaUV2 = textCoords[2].Sub(textCoords[0]);

					float f = 1.0f / (deltaUV1.X * deltaUV2.Y - deltaUV2.X * deltaUV1.Y);
					if(Float.isInfinite(f))
						f = 0.0f;

					tangentSpace[tangentSpaceMul] = f * (deltaUV2.Y * edge1.X - deltaUV1.Y * edge2.X);
					tangentSpace[tangentSpaceMul+1] = f * (deltaUV2.Y * edge1.Y - deltaUV1.Y * edge2.Y);
					tangentSpace[tangentSpaceMul+2] = f * (deltaUV2.Y * edge1.Z - deltaUV1.Y * edge2.Z);

					tangentSpaceMul+=3;
					verticesCount = 0;
				}

				vertexCount = -1;
			}
		}

		float[] temp = new float[vertices.length+(tangentSpace.length*3)];
		int triangles = (vertices.length/8)/3;

		int tangentSpaceCounter = 0;
		int tempCounter = 0;
		for(int i = 0; i < triangles; i++)
		{
			for(int k = i*3; k < (i+1)*3; k++)
			{
				for(int j = k*8; j < (k+1)*8; j++)
				{
					temp[tempCounter] = vertices[j];
					tempCounter++;
				}

				for(int j = tangentSpaceCounter*3; j < (tangentSpaceCounter+1)*3; j++)
				{
					temp[tempCounter] = tangentSpace[j];
					tempCounter++;
				}

			}

			tangentSpaceCounter++;
		}

		return temp;
	}
}
