package de.luke.openglTest;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import com.jogamp.opengl.GL4;

import de.galaxy.core.ShaderProgram;
import de.galaxy.core.Texture;


public class Texture2D extends Texture
{
	public static final int TEXTURE_DIFFUSE = 0;
	public static final int TEXTURE_SPECULAR = 1;
	public static final int TEXTURE_EMISSION = 2;
	public static final int TEXTURE_NORMAL = 3;
	
	private static final String[] DIFFUSE_STRINGS = new String[] {"diffuse"};
	private static final String[] SPECULAR_STRINGS = new String[] {"specular"};
	private static final String[] EMISSION_STRINGS = new String[] {"emission"};
	private static final String[] NORMAL_STRINGS = new String[] {"normal"};
	
	public IntBuffer ID;
	
	public int width, height;
	private String path;
	public int type;
	
	public ByteBuffer bytes;
	private boolean loadSuccess = false;
	
	private int textType = -1;
	private int textureCount;
	
	private int diffuseCount = 0;
	private int specularCount = 0;
	private int emissionCount = 0;
	private int normalCount = 0;
	
	public Texture2D(String path, boolean flipImage) 
	{
		this.path = path;
		load(flipImage);
	}
	
	public Texture2D (String path, GL4 gl, ShaderProgram shader, int textureCount, int textureType, float highlightFocus) 
	{
		this.path = path;
		this.textType = textureType;
		this.textureCount = textureCount;
		this.highlightFocus = highlightFocus;
		
		load(true);
		loadIntoGraphicsCard(gl, shader, textureCount);
			
	} 
	
	private void load(boolean flipImage)  
	{
		if(path != null) 
		{
			File f = new File(path);
			if(!f.exists()) 
			{
				System.out.println("[Galaxy Error]: Texture " + path + "not found");
				return;
			}
			
			BufferedImage buf = null;
			try 
			{
				buf = ImageIO.read(f);
			} catch (Exception e) 
			{
				System.out.println("[Galaxy Error]: Texture "+ path + " :" + e.getMessage());
				return;
			} 
			
			width = buf.getWidth();
			height = buf.getHeight();
			
			byte pixels[] = ((DataBufferByte) buf.getRaster().getDataBuffer()).getData();
			bytes = ByteBuffer.allocate(pixels.length);
			
			int pixelsNum = 0;
			switch(buf.getType()) 
			{
			case BufferedImage.TYPE_3BYTE_BGR:
				pixelsNum = 3;
				type = 6407;
				break;
			case BufferedImage.TYPE_4BYTE_ABGR:
				pixelsNum = 4;
				type = 6408; 	
				break;
			default:
				System.out.println("[Galaxy Error]: Image type unknown");
				return;
			}
			
			//flip the image vertically and write it in ByteBuffer
			if(flipImage) 
			{
				int byteBufferCounter = 0;	
				
				for(int row = height; row >= 0; row--) 
				{
					byte[] pixelBuffer = new byte[pixelsNum];
					int pixelBufferCounter = 0;
					for(int i = Math.max(0, width*(row-1)*pixelsNum); i < (width*row*pixelsNum); i++) 
					{
						//bytes.array()[byteBufferCounter] = pixels[i];
						//byteBufferCounter++;		

						//PNG - Flip ABGR -> RGBA
						pixelBuffer[pixelBufferCounter] = pixels[i];
						pixelBufferCounter++;	
							
						if(pixelBufferCounter == pixelsNum) 
						{
							for(int j = pixelsNum-1; j >= 0; j--) 
							{
								bytes.array()[byteBufferCounter] = pixelBuffer[j];
								byteBufferCounter++;		
							}
							pixelBufferCounter = 0;
						}	
					}
				}		
			}
			else 
			{
				int byteBufferCounter = 0;	
				
				for(int row = 0; row <= height; row++) 
				{
					byte[] pixelBuffer = new byte[pixelsNum];
					int pixelBufferCounter = 0;
					for(int i = Math.max(0, width*(row-1)*pixelsNum); i < (width*row*pixelsNum); i++) 
					{
						//bytes.array()[byteBufferCounter] = pixels[i];
						//byteBufferCounter++;		

						//PNG - Flip ABGR -> RGBA
						pixelBuffer[pixelBufferCounter] = pixels[i];
						pixelBufferCounter++;	
							
						if(pixelBufferCounter == pixelsNum) 
						{
							for(int j = pixelsNum-1; j >= 0; j--) 
							{
								bytes.array()[byteBufferCounter] = pixelBuffer[j];
								byteBufferCounter++;		
							}
							pixelBufferCounter = 0;
						}	
					}
				}
			}
			loadSuccess = true;
		}
		else 
		{
			System.out.println("[Galaxy Error]: Faild to load texture, path was null");
		}
	}
	
	private void loadIntoGraphicsCard(GL4 gl, ShaderProgram shader, int textureCount) 
	{
		if(loadSuccess) 
		{
			ID = IntBuffer.allocate(1);
			
			String[] textNameArr = null;
			int arrayCount = 0;
			
			switch(textType) 
			{
			case Texture2D.TEXTURE_DIFFUSE:
				textNameArr = Texture2D.DIFFUSE_STRINGS;
				arrayCount = diffuseCount;
				diffuseCount++;
				break;
			case Texture2D.TEXTURE_EMISSION:
				textNameArr = Texture2D.EMISSION_STRINGS;
				arrayCount = emissionCount;
				emissionCount++;
				break;
			case Texture2D.TEXTURE_SPECULAR:
				textNameArr = Texture2D.SPECULAR_STRINGS;
				arrayCount = specularCount;
				specularCount++;
				break;
			case Texture2D.TEXTURE_NORMAL:
				textNameArr = Texture2D.NORMAL_STRINGS;
				arrayCount = normalCount;
				normalCount++;
				break;
			default:
				System.out.println("[Galaxy Error]: Uknown texture type");
				return;
			}
			
			gl.glGenTextures(1, ID);
			gl.glActiveTexture(GL4.GL_TEXTURE0+textureCount);
				
			gl.glBindTexture(GL4.GL_TEXTURE_2D, ID.array()[0]);
			gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, type, width, height, 0, type, GL4.GL_UNSIGNED_BYTE, bytes);
			gl.glGenerateMipmap(GL4.GL_TEXTURE_2D);
				
			gl.glUniform1i(gl.glGetUniformLocation(shader.getID(), "material."+textNameArr[arrayCount]), textureCount);
			
			gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_REPEAT);
			gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_REPEAT);
			
			gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR_MIPMAP_LINEAR);
			gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
			
			gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
			
			free();
			
			System.out.println("[Galaxy Log]: Loaded Texture " + path);

		}
		else 
		{
			this.ID = Engine.missingTexture.ID;
		}
	}
	
	@Override
	public void bind(GL4 gl) 
	{
		gl.glActiveTexture(GL4.GL_TEXTURE0+textureCount);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, ID.array()[0]);
	}
	
	@Override
	public void dispose(GL4 gl) 
	{
		gl.glDeleteTextures(1, ID);
	}
	
	public int getWidth() 
	{
		return width;
	}
	
	public int getHeight() 
	{
		return height;
	}
	
	public ByteBuffer getData() 
	{
		return bytes;
	}
	
	public int getType() 
	{
		return type;
	}
	
	public void free() 
	{
		bytes = null;
	}
}
