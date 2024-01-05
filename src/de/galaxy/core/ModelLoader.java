package de.galaxy.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import com.jogamp.opengl.GL4;

import de.galaxy.math.Vector2;
import de.galaxy.math.Vector3;
import de.luke.openglTest.Texture2D;

public class ModelLoader 
{
	private static ArrayList<Vector3> v;
	private static ArrayList<Vector3> vn;
	private static ArrayList<Vector2> vt;
	
	private static int trianglesCount;
	
	private static ArrayList<Float> vertices;
	private static ArrayList<Integer> indices;
	private static HashMap<String, Integer> faceMap;
	
	private static int indicesCount;
	
	private static ArrayList<Mesh> meshes;
	private static String currentName;
	
	private static HashMap<String, ArrayList<Texture2D>> materials;
	private static String libFile;
	private static String actMaterial;
	
	private static GL4 gl;
	
	private static Random rand;
	
	public static Model load(GL4 gl, String model, ShaderProgram shaderProgram, boolean uvMapping, boolean calcTangentSpace) 
	{
		System.out.println("[Galaxy Log]: Loading mode " + model);
		ModelLoader.gl = gl;
		rand = new Random();
		
		trianglesCount = 0;
		
		faceMap = new HashMap<String, Integer>();
		indices = new ArrayList<Integer>();
		
		Model m = null;
		File[] files = AssetManager.LoadModelFiles(model);
		
		HashMap<String, Integer> fileMap = new HashMap<String, Integer>();
		File objFile = null;
		ArrayList<File> mtlFile = new ArrayList<File>();
		
		materials = new HashMap<String, ArrayList<Texture2D>>();
		
		if(files == null)
			return null;
		
		v = new ArrayList<Vector3>();
		vn = new ArrayList<Vector3>();
		vt = new ArrayList<Vector2>();
		
		indicesCount = 0;
		
		meshes = new ArrayList<Mesh>();
		vertices = new ArrayList<Float>();
		
		libFile = "";
		actMaterial = "";
		
		//"order" files
		for(int i = 0; i < files.length; i++) 
		{
			String[] temp = files[i].getName().split("\\.");
			String ending = "";
			if(temp.length > 0)
				ending = temp[temp.length-1].toLowerCase();
			
			if(ending.equals("obj")) 
			{
				objFile = files[i];
			}
			else if(ending.equals("mtl")) 
			{
				mtlFile.add(files[i]);
			}
			else 
			{
				fileMap.put(files[i].getName(), i);
			}
		}
		
		for(File mtlF : mtlFile)
			readMtl(mtlF, shaderProgram);
		
		Scanner reader = null;
		try 
		{
			reader = new Scanner(objFile);
			while(reader.hasNextLine()) 
			{
				readLine(reader.nextLine(), uvMapping, calcTangentSpace);
			}
			reader.close();
			
			saveMesh();
			m = new Model(meshes.toArray(new Mesh[0]), model, trianglesCount);
			
			System.out.println("[Glaxy Log]: Finished loading model");
			
			//set everything to null
			v = null;
			vn = null;
			vt = null;
			meshes = null;
			vertices = null;
			faceMap = null;
			indices = null;
			materials = null;
		} 
		catch (Exception e) 
		{
			System.out.println("load" + e.getMessage());
		}
		
		return m;
	}
	
	private static void readLine(String line, boolean uvMapping, boolean calcTangentSpace) 
	{
		String[] arr = line.replaceAll("^ +| +$|( )+", "$1").split(" ");
		if(arr.length > 0) 
		{
			switch(arr[0]) 
			{
				case "o":
					saveMesh();
					currentName = arr[1];
					break;
				case "v":
					if(arr.length == 4) 
					{
						Vector3 temp = new Vector3(Float.parseFloat(arr[1]),Float.parseFloat(arr[2]),Float.parseFloat(arr[3]));
						v.add(temp);
					}
					break;
				case "vn":
					if(arr.length == 4) 
					{
						Vector3 temp = new Vector3(Float.parseFloat(arr[1]),Float.parseFloat(arr[2]),Float.parseFloat(arr[3]));
						vn.add(temp);
					}
					break;
				case "vt":
					if(arr.length > 2) 
					{
						Vector2 temp = new Vector2(Float.parseFloat(arr[1]),Float.parseFloat(arr[2]));
						vt.add(temp);
					}
					break;
				case "f":
					if(arr.length == 4) 
					{
						readFace(arr, uvMapping, calcTangentSpace);
						trianglesCount++;
					}
					break;
				case "mtllib":
					libFile = arr[1];
					break;
				case "usemtl":
					actMaterial = arr[1];
					break;
				default:
					break;
			}
		}
		return;
	}
	
	private static void readFace(String[] arr, boolean uvMapping, boolean calcTangentSpace) 
	{
		for(int i = 1; i < arr.length; i++) 
		{
			String[] data = arr[i].split("/");
			String key = arr[i];

				if(faceMap.containsKey(key) && !calcTangentSpace) 
				{
					indices.add(faceMap.get(key));
				}
				else 
				{
					int vIndex = Integer.parseInt(data[0])-1;
					int vtIndex = Integer.parseInt(data[1])-1;
					int vnIndex = Integer.parseInt(data[2])-1;
					
					//vertices
					vertices.add(v.get(vIndex).X);
					vertices.add(v.get(vIndex).Y);
					vertices.add(v.get(vIndex).Z);
					
					//normals
					vertices.add(vn.get(vnIndex).X);
					vertices.add(vn.get(vnIndex).Y);
					vertices.add(vn.get(vnIndex).Z);
					
					//texture coordinates
					vertices.add(vt.get(vtIndex).X);
					if(uvMapping)
						vertices.add(1.0f-vt.get(vtIndex).Y);
					else
						vertices.add(vt.get(vtIndex).Y);
					
					faceMap.put(key, indicesCount);
					indices.add(indicesCount);	
						
					indicesCount++;
				}
		}
	}
	
	private static void saveMesh()
	{
		if(ModelLoader.vertices.size() > 0)
		{
			float[] vertices = new float[ModelLoader.vertices.size()];
			for(int i = 0; i < vertices.length; i++)
				vertices[i] = ModelLoader.vertices.get(i);
			
			Texture2D[] textures = null;
			if(!libFile.equals("") && !actMaterial.equals("")) 
			{
				String fullMatName = libFile + "/" + actMaterial;
				if(materials.containsKey(fullMatName)) 
				{
					ArrayList<Texture2D> textureList = materials.get(fullMatName);
					textures = new Texture2D[textureList.size()];
					for(int i = 0; i < textures.length; i++) 
					{
						textures[i] = textureList.get(i);
					}
				}
			}
			
			int[] indices = new int[ModelLoader.indices.size()];
			for(int i = 0; i < indices.length; i++)
				indices[i] = ModelLoader.indices.get(i);
				
			meshes.add(new Mesh(gl, vertices, indices, textures, currentName, new Vector3(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()).Mul(0.5f), null));	

			ModelLoader.faceMap = new HashMap<String, Integer>();
			ModelLoader.indices = new ArrayList<Integer>();
			ModelLoader.vertices = new ArrayList<Float>();
			
			actMaterial = "";
			indicesCount = 0;
		}
	}
	
	private static void readMtl(File f, ShaderProgram shaderProgram) 
	{
		//save all materials not only one
		Scanner reader = null;
		try 
		{
			reader = new Scanner(f);
			
			String fileName = f.getName();
			
			String aktMaterial = "";
			
			Texture2D diffuse = null;
			Texture2D specular = null;
			Texture2D normal = null;
			
			int textureCount = 0;
			
			float highlightFocus = 32.0f;
			
			while(reader.hasNextLine()) 
			{
				String[] line = reader.nextLine().split(" ");
				if(line.length > 1) 
				{
					String path;
					switch(line[0]) 
					{
					case "newmtl":
						if(!aktMaterial.equals("")) 
						{
							ArrayList<Texture2D> temp = new ArrayList<Texture2D>();
							if(diffuse != null)
								temp.add(diffuse);
							if(specular != null)
								temp.add(specular);
							if(normal != null)
								temp.add(normal);
							
							materials.put(fileName + "/" + aktMaterial, temp);	
							
							diffuse = null;
							specular = null;
							normal = null;
						}
						aktMaterial = line[1];
						break;
					case "Ns":
						highlightFocus = Float.parseFloat(line[1]);
						break;
					case "map_Kd":
						path = f.getParent() + "\\" + line[1];
						diffuse = new Texture2D(path, ModelLoader.gl, shaderProgram, textureCount, Texture2D.TEXTURE_DIFFUSE, highlightFocus);
						textureCount++;
						break;
					case "map_Bump":
						break;
					case "map_Ks":
						path = f.getParent() + "\\" + line[1];
						specular = new Texture2D(path, ModelLoader.gl, shaderProgram, textureCount, Texture2D.TEXTURE_SPECULAR, highlightFocus);
						textureCount++;
						break;
					default:
						break;
					}
				}
			}
			reader.close();
			
			if(!aktMaterial.equals("")) 
			{
				ArrayList<Texture2D> temp = new ArrayList<Texture2D>();
				if(diffuse != null)
					temp.add(diffuse);
				if(specular != null)
					temp.add(specular);
				if(normal != null)
					temp.add(normal);
				
				materials.put(fileName + "/" + aktMaterial, temp);	
			}
		}
		catch(Exception e) 
		{
			System.out.println("readMtl" + e.getMessage());
		}
	}
}
