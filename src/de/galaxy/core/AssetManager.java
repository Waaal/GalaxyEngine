package de.galaxy.core;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class AssetManager 
{
	/*
	 * Main class for interacting with assets (textures, shader, animations etc)
	 */
	
	public static final String shaderFilesPath = "assets/shader/";
	public static final String modelFilePath = "assets/models/";
	
	public static String LoadShaderFile(String file) 
	{
		try 
		{
			System.out.println("[Galaxy Log]: Load shader file " + file);
			return new String(Files.readAllBytes(Paths.get(shaderFilesPath, file)), StandardCharsets.UTF_8);
		}
		catch(Exception e) 
		{
			return null;	
		}
	}
	
	public static File[] LoadModelFiles(String file) 
	{
		ArrayList<File> filesList = new ArrayList<File>();
		
		boolean obj = false;
		
		try 
		{
			File dir = new File(modelFilePath + file);
			if(dir.isDirectory()) 
			{
				for(File f : dir.listFiles()) 
				{
					String[] temp = f.getName().toLowerCase().split("\\.");
					if(temp.length > 0) 
					{
						if(temp[temp.length-1].equals("obj"))
							obj = true;	
					}
						
					filesList.add(f);
				}
			}
			else 
			{
				System.out.println("[Galaxy Error]: " + file + " is not a directory");
			}
		}
		catch(Exception e) 
		{
			System.out.println(e.getMessage());
			return null;
		}
		
		if(!obj) 
		{
			System.out.println("[Galaxy Error]: At model " + file + " no .obj file was found");
			return null;
		}
		
		return filesList.toArray(new File[0]);
	}
}
