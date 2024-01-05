package de.galaxy.core;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import de.galaxy.input.InputHandler;
import de.galaxy.light.DirectionLight;
import de.galaxy.light.Light;
import de.galaxy.light.PointLight;
import de.galaxy.light.SpotLight;
import de.galaxy.math.GalacticMath;
import de.galaxy.math.Matrix4;
import de.galaxy.math.Vector3;
import de.luke.openglTest.Engine;
import de.luke.openglTest.Texture2D;

import java.nio.IntBuffer;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Renderer implements GLEventListener 
{
	private Boolean ok = false;
	
	float screenQuadVertices[] = {
		    // positions   // texCoords
		    -1.0f,  1.0f,  0.0f, 1.0f,
		    -1.0f, -1.0f,  0.0f, 0.0f,
		     1.0f, -1.0f,  1.0f, 0.0f,

		    -1.0f,  1.0f,  0.0f, 1.0f,
		     1.0f, -1.0f,  1.0f, 0.0f,
		     1.0f,  1.0f,  1.0f, 1.0f
	    };
	
	private VertexAttribPointer[] screenAttrib = new VertexAttribPointer[] 
			{
					new VertexAttribPointer(0, 2, GL4.GL_FLOAT, false, 4*4, 0),
					new VertexAttribPointer(1, 2, GL4.GL_FLOAT, false, 4*4, 2*4)
			};
	
	IntBuffer fbo;
	IntBuffer textureColorBuffer;
	IntBuffer rbo;
	Vao screenVao;

	IntBuffer uboMatrices;

	ShaderProgram screenShader;

	Matrix4 projection = new Matrix4(1.0f);
	Camera cam;

	ArrayList<ShaderProgram> shaderList = new ArrayList<ShaderProgram>();
	ArrayList<Light> lightList;

	ArrayList<HashMap<Integer, ArrayList<Model>>> renderLayer = new ArrayList<HashMap<Integer, ArrayList<Model>>>();

	Vector3 lightsTypeCount;

	@Override
	public void display(GLAutoDrawable drawable)
	{
		if(ok) 
		{
			GL4 gl = drawable.getGL().getGL4();			
			//write scene in frameBuffer

			gl.glViewport(0, 0, Engine.width, Engine.height);
			
			gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo.array()[0]);	
			gl.glEnable(GL4.GL_DEPTH_TEST);
			
			gl.glClearColor(0, 0, 0, 1.0f);
			gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);
			
			calcLights(gl, false);
			
			if(InputHandler.getKeyJustPress(InputHandler.KEY_M)) 
				Engine.lockMouse = !Engine.lockMouse;	
			
			cam.Update();
			
			gl.glBindBuffer(GL4.GL_UNIFORM_BUFFER, uboMatrices.array()[0]);
			gl.glBufferSubData(GL4.GL_UNIFORM_BUFFER, 4*4*4, 4*4*4, cam.getBuffer());
			gl.glBindBuffer(GL4.GL_UNIFORM_BUFFER, 0);
			
			renderScene(gl, null);		
			gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);

			gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
			gl.glClear(GL4.GL_COLOR_BUFFER_BIT);
			gl.glDisable(GL4.GL_DEPTH_TEST);			
			
			gl.glUseProgram(screenShader.getID());
			
			gl.glBindVertexArray(screenVao.getID());
			
			gl.glActiveTexture(GL4.GL_TEXTURE0);
			gl.glBindTexture(GL4.GL_TEXTURE_2D, textureColorBuffer.array()[0]);
			
			gl.glDrawArrays(screenVao.getDrawing(), 0, screenVao.getVec3Number());
			
			gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		}
	}
	
	private void calcLights(GL4 gl, boolean showDebugLights) 
	{
		ShaderProgram selectedShader;
		
		if(showDebugLights) 
		{
			//draw all lights
			selectedShader = shaderList.get(3);
			gl.glUseProgram(selectedShader.getID());

			for(Light l : lightList) 
			{
				if(l.getType() == Light.DIRECTION_LIGHT) 
					continue;
				
				//PointLightModel.position = l.position;
				//PointLightModel.render(gl, selectedShader);
			}
		}
		
		//write all lights in shader
		selectedShader = shaderList.get(0);
		gl.glUseProgram(selectedShader.getID());
		
		selectedShader.setVec3(gl, "viewPos", cam.getPos());

		selectedShader.setInt(gl, "NR_DIR_LIGHTS", (int)lightsTypeCount.X);
		selectedShader.setInt(gl, "NR_POINT_LIGHTS", (int)lightsTypeCount.Y);
		selectedShader.setInt(gl, "NR_SPOT_LIGHTS", (int)lightsTypeCount.Z);

		int pointCounter = 0, dirCounter = 0, spotCounter = 0;
		for(int k = 0; k < lightList.size(); k++) 
		{
			//use uniform buffer object
			if(lightList.get(k).getType() == Light.DIRECTION_LIGHT) 
			{
				lightList.get(k).fillShader(gl, selectedShader, dirCounter);
				dirCounter++;
			}
			else if(lightList.get(k).getType() == Light.POINT_LIGHT) 
			{
				lightList.get(k).fillShader(gl, selectedShader, pointCounter);	
				pointCounter++;
			}
			else if(lightList.get(k).getType() == Light.SPOT_LIGHT) 
			{
				lightList.get(k).fillShader(gl, selectedShader, spotCounter);
				spotCounter++;
			}
		}
	}
	
	private void renderScene(GL4 gl, ShaderProgram givenShader) 
	{
		ShaderProgram selectedShader = null;
		for(HashMap<Integer, ArrayList<Model>> currentLayer : renderLayer)
		{
			for(Integer shaderKey: currentLayer.keySet())
			{
				selectedShader = shaderList.get(shaderKey);
				gl.glUseProgram(selectedShader.getID());
				for(Model currentModel : currentLayer.get(shaderKey))
				{
					currentModel.render(gl, selectedShader);
				}
			}
		}
	}
	
	@Override
	public void dispose(GLAutoDrawable drawable) 
	{
		GL4 gl = drawable.getGL().getGL4();
		
		for(int i = 0; i < shaderList.size(); i++)
			gl.glDeleteProgram(shaderList.get(i).getID());
		
		if(fbo != null)
			gl.glDeleteFramebuffers(1, fbo);
		
		if(rbo != null)
			gl.glDeleteRenderbuffers(1, rbo);
		
		if(textureColorBuffer != null)
			gl.glDeleteTextures(1, textureColorBuffer);
		
		if(screenVao != null)
			screenVao.dispose(gl);

		for(HashMap<Integer, ArrayList<Model>> layer : renderLayer)
			for(ArrayList<Model> modelList: layer.values())
				for(Model m : modelList)
					m.dispose(gl);
	}

	@Override
	public void init(GLAutoDrawable drawable) 
	{
		GL4 gl = drawable.getGL().getGL4();

		//Default settings
		gl.glEnable(GL4.GL_DEPTH_TEST);
		gl.glDepthFunc(GL4.GL_LEQUAL);
		
		gl.glEnable(GL4.GL_BLEND);
		gl.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);

		screenShader = new ShaderProgram();
		screenShader.Add(GL4.GL_VERTEX_SHADER, "ScreenFrameBuffer/VertexShaderSFB.GLSL", gl);
		screenShader.Add(GL4.GL_FRAGMENT_SHADER, "ScreenFrameBuffer/FragmentShaderSFB.GLSL", gl);
		if(!screenShader.Create(gl))
		{
			System.out.println("[Galaxy Error]: Error while creating the FrameBuffer");
			return;
		}

		gl.glUseProgram(screenShader.getID());
		if(!createFrameBuffer(gl, screenShader))
		{
			System.out.println("[Galaxy Error]: Error while creating the FrameBuffer");
			return;
		}

		// = = = = = LOAD SCENE = = = = =

		Scene currentScene = new Scene("assets/scenes/EngineTestScene.scene", true, gl);

		projection = currentScene.getProjection();
		cam = currentScene.getCam();
		shaderList = currentScene.getShaders();
		lightList = currentScene.getLightList();
		renderLayer = currentScene.getRenderLayer();
		lightsTypeCount = currentScene.getLightTypesCount();

		uboMatrices = IntBuffer.allocate(1);
		gl.glGenBuffers(1, uboMatrices);

		gl.glBindBuffer(GL4.GL_UNIFORM_BUFFER, uboMatrices.array()[0]);
		gl.glBufferData(GL4.GL_UNIFORM_BUFFER, 2*(4*4*4), null, GL4.GL_STATIC_DRAW);
		gl.glBindBuffer(GL4.GL_UNIFORM_BUFFER, 0);

		gl.glBindBufferRange(GL4.GL_UNIFORM_BUFFER, 0, uboMatrices.array()[0], 0, 2*(4*4*4));

		gl.glBindBuffer(GL4.GL_UNIFORM_BUFFER, uboMatrices.array()[0]);
		gl.glBufferSubData(GL4.GL_UNIFORM_BUFFER, 0, 4*4*4, projection.getBuffer());
		gl.glBindBuffer(GL4.GL_UNIFORM_BUFFER, 0);

		System.out.println("INIT COMPLETE");
		ok = true;
	}
	
	private Boolean createFrameBuffer(GL4 gl, ShaderProgram selectedShader) 
	{
		screenVao = new Vao(gl, screenQuadVertices, GL4.GL_TRIANGLES, screenAttrib);
		
		fbo = IntBuffer.allocate(1);
		
		gl.glGenFramebuffers(1, fbo);
		gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo.array()[0]);
		
		//generate texture	
		textureColorBuffer = IntBuffer.allocate(1);
		gl.glGenTextures(1, textureColorBuffer);
		gl.glActiveTexture(GL4.GL_TEXTURE0);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, textureColorBuffer.array()[0]);
		gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGB, Engine.width-20, Engine.height-40, 0, GL4.GL_RGB, GL4.GL_UNSIGNED_BYTE, null);
		
		//attach texture to current bound frameBuffer object
		gl.glFramebufferTexture2D(GL4.GL_FRAMEBUFFER, GL4.GL_COLOR_ATTACHMENT0, GL4.GL_TEXTURE_2D, textureColorBuffer.array()[0], 0);
		
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_REPEAT);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_REPEAT);
		
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
		
		selectedShader.setInt(gl, "screenTexture", 0);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		
		//generate renderBufferObject for Depth and Stencil testing
		rbo = IntBuffer.allocate(1);
		gl.glGenRenderbuffers(1, rbo);
		gl.glBindRenderbuffer(GL4.GL_RENDERBUFFER, rbo.array()[0]);
		gl.glRenderbufferStorage(GL4.GL_RENDERBUFFER, GL4.GL_DEPTH24_STENCIL8, Engine.width-20, Engine.height-40);
		
		//attach rebderBufferObject to current bound frameBuffer object
		gl.glFramebufferRenderbuffer(GL4.GL_FRAMEBUFFER, GL4.GL_DEPTH_STENCIL_ATTACHMENT, GL4.GL_RENDERBUFFER, rbo.array()[0]);
		
		gl.glBindRenderbuffer(GL4.GL_RENDERBUFFER, 0);
		
		gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
		if(gl.glCheckFramebufferStatus(GL4.GL_FRAMEBUFFER) == GL4.GL_FRAMEBUFFER_COMPLETE) 
		{
			return true;	
		}
		
		return false;
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		
	}
}
