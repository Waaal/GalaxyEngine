package de.luke.openglTest;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import de.galaxy.math.*;
import de.galaxy.core.Camera;
import de.galaxy.core.Instanciator;
import de.galaxy.core.Model;
import de.galaxy.core.ModelBuilder;
import de.galaxy.core.ModelLoader;
import de.galaxy.core.ShaderProgram;
import de.galaxy.core.Vao;
import de.galaxy.core.VertexAttribPointer;
import de.galaxy.core.cubeMapTexture;
import de.galaxy.input.InputHandler;
import de.galaxy.light.Light;
import de.galaxy.light.DirectionLight;
import de.galaxy.light.PointLight;
import de.galaxy.light.SpotLight;

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
	
	Matrix4 model = new Matrix4(1.0f);
	Matrix4 projection = new Matrix4(1.0f);
	
	ArrayList<Light> lightList;
	
	Instanciator instances;
	
	Vector3[] pointLightPositions = new Vector3[]{
			new Vector3( 0.7f,  -1.3f,  2.0f),
			new Vector3( 2.3f, -3.3f, -4.0f),
			new Vector3(-4.0f,  2.0f, -12.0f),
			new Vector3(0.0f,  5.0f, -2.0f),
			new Vector3( 0.0f,  0.0f, -3.0f)
		}; 
	
	Vector3[] windowPositions = new Vector3[] 
			{
					new Vector3(1.0f, 1.0f, 3.0f),
					new Vector3(1.0f, 1.0f, 4.0f),
					new Vector3(1.0f, 1.0f, 5.0f),
					new Vector3(1.0f, 1.0f, 6.0f),
					new Vector3(1.0f, 1.0f, 7.0f)
			};
	
	Vector3 spotLightPos = new Vector3(0f,3f,-0.2f);
	Vector3 spotLightDirection = new Vector3(-1,-1,-1);
	
	Camera cam;
	
	ArrayList<ShaderProgram> shaderList = new ArrayList<ShaderProgram>();
	
	Model myModel;
	Model windowModel;
	Model testblock;
	
	Model cUwUbe;
	cubeMapTexture cUwUbeTexture;
	
	Model PointLightModel;
	
	Model brickWall;
	
	@Override
	public void display(GLAutoDrawable drawable)
	{
		if(ok) 
		{
			GL4 gl = drawable.getGL().getGL4();			
			//write scene in frameBuffer
			
			//gl.glPolygonMode(GL4.GL_FRONT_AND_BACK, GL4.GL_LINE);
			
			gl.glViewport(0, 0, Engine.width, Engine.height);
			
			gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo.array()[0]);	
			gl.glEnable(GL4.GL_DEPTH_TEST);
			
			gl.glClearColor(0, 0, 0, 1.0f);
			gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);
			
			calcLights(gl, true);
			
			//skybox
			gl.glUseProgram(shaderList.get(6).getID());	
			gl.glDepthMask(false);
			
			cUwUbe.position = cam.getPos();
			cUwUbe.scale = new Vector3(5,5,5);
			cUwUbe.render(gl, shaderList.get(6));
			
			gl.glDepthMask(true);
			
			if(InputHandler.getKeyJustPress(InputHandler.KEY_M)) 
				Engine.lockMouse = !Engine.lockMouse;	
			
			cam.Update();
			
			gl.glBindBuffer(GL4.GL_UNIFORM_BUFFER, uboMatrices.array()[0]);
			gl.glBufferSubData(GL4.GL_UNIFORM_BUFFER, 4*4*4, 4*4*4, cam.getBuffer());
			gl.glBindBuffer(GL4.GL_UNIFORM_BUFFER, 0);
			
			renderScene(gl, null);		
			gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
			
			//gl.glPolygonMode(GL4.GL_FRONT_AND_BACK, GL4.GL_FILL);
			
			gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
			gl.glClear(GL4.GL_COLOR_BUFFER_BIT);
			gl.glDisable(GL4.GL_DEPTH_TEST);			
			
			gl.glUseProgram(shaderList.get(5).getID());
			
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
				
				PointLightModel.position = l.position;			
				PointLightModel.render(gl, selectedShader);
			}
		}
		
		//write all lights in shader
		selectedShader = shaderList.get(0);
		gl.glUseProgram(selectedShader.getID());
		
		selectedShader.setVec3(gl, "viewPos", cam.getPos());

		int pointCounter = 0;
		for(int k = 0; k < lightList.size(); k++) 
		{
			//use uniform buffer object
			if(lightList.get(k).getType() == Light.DIRECTION_LIGHT) 
			{
				lightList.get(k).fillShader(gl, selectedShader, 0);	
			}
			else if(lightList.get(k).getType() == Light.POINT_LIGHT) 
			{
				lightList.get(k).fillShader(gl, selectedShader, pointCounter);	
				pointCounter++;
			}
			else if(lightList.get(k).getType() == Light.SPOT_LIGHT) 
			{
				lightList.get(k).fillShader(gl, selectedShader, 0);	
			}
		}
	}
	
	private void renderScene(GL4 gl, ShaderProgram givenShader) 
	{
		ShaderProgram selectedShader = null;
		
		boolean hasShader = false;
		if(givenShader != null) 
		{
			selectedShader = givenShader;
			gl.glUseProgram(selectedShader.getID());
			hasShader = true;
		}
			
		if(!hasShader) 
		{
			selectedShader = shaderList.get(0);
			gl.glUseProgram(selectedShader.getID());			
		}
		
		brickWall.position = new Vector3(0 ,5f,-3.5f);
		brickWall.render(gl, selectedShader);
		
		gl.glEnable(GL4.GL_CULL_FACE);
		gl.glCullFace(GL4.GL_BACK);
		gl.glFrontFace(GL4.GL_CCW); 
		
		//model render
		myModel.render(gl, selectedShader);
		testblock.render(gl, selectedShader);
		
		gl.glDisable(GL4.GL_CULL_FACE);
		
		if(!hasShader) 
		{
			selectedShader = shaderList.get(7);
			gl.glUseProgram(selectedShader.getID());
			instances.render(gl, selectedShader);	
		}
		
		if(!hasShader) 
		{
			selectedShader = shaderList.get(3);
			gl.glUseProgram(selectedShader.getID());	
		}
		
		Map<Float, Vector3> map = new TreeMap<Float, Vector3>();
		for(int i = 0; i < windowPositions.length; i++) 
		{
			float distance = (cam.getPos().Sub(windowPositions[i])).Length();
			map.put(distance, windowPositions[i]);
		}
		
		ArrayList<Float> keys = new ArrayList<Float>(map.keySet());
		for(int i = keys.size()-1; i >= 0; i--)
		{
			windowModel.position = map.get(keys.get(i));
			windowModel.render(gl, selectedShader);	
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
		
		myModel.dispose(gl);
		windowModel.dispose(gl);
		PointLightModel.dispose(gl);
		testblock.dispose(gl);
		cUwUbe.dispose(gl);
		
		brickWall.dispose(gl);
		
		System.out.println("DISPOSED");
	}

	@Override
	public void init(GLAutoDrawable drawable) 
	{
		GL4 gl = drawable.getGL().getGL4();
		
		gl.glEnable(GL4.GL_DEPTH_TEST);
		gl.glDepthFunc(GL4.GL_LEQUAL);
		
		gl.glEnable(GL4.GL_BLEND);
		gl.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);
		
		//gl.glEnable(GL4.GL_MULTISAMPLE);
		
		ShaderProgram program = new ShaderProgram();
		ShaderProgram lightSourceProgram = new ShaderProgram();
		ShaderProgram NLNTProgram = new ShaderProgram();
		ShaderProgram OTProgram = new ShaderProgram();
		ShaderProgram VDBProgram = new ShaderProgram();
		ShaderProgram SFBProgram = new ShaderProgram();
		ShaderProgram CMProgram = new ShaderProgram();
		ShaderProgram IProgram = new ShaderProgram();
		ShaderProgram SDProgram = new ShaderProgram();
		
		program.Add(GL4.GL_VERTEX_SHADER, "defaultShader/VertexShaderDefault.GLSL", gl);
		program.Add(GL4.GL_FRAGMENT_SHADER, "defaultShader/FragmentShaderDefault.GLSL", gl);
		if(!program.Create(gl))
			return;
		
		lightSourceProgram.Add(GL4.GL_VERTEX_SHADER, "lightSourceShader/VertexShaderLightSource.GLSL", gl);
		lightSourceProgram.Add(GL4.GL_FRAGMENT_SHADER, "lightSourceShader/FragmentShaderLightSource.GLSL", gl);
		
		if(!lightSourceProgram.Create(gl)) 
			return;
		
		NLNTProgram.Add(GL4.GL_VERTEX_SHADER, "NoLightNoTexture/VertexShaderNLNT.GLSL", gl);
		NLNTProgram.Add(GL4.GL_FRAGMENT_SHADER, "NoLightNoTexture/FragmentShaderNLNT.GLSL", gl);
		
		if(!NLNTProgram.Create(gl))
			return;
		
		OTProgram.Add(GL4.GL_VERTEX_SHADER, "OnlyTexture/VertexShaderOT.GLSL", gl);
		OTProgram.Add(GL4.GL_FRAGMENT_SHADER, "OnlyTexture/FragmentShaderOT.GLSL", gl);
		
		if(!OTProgram.Create(gl))
			return;
		
		VDBProgram.Add(GL4.GL_VERTEX_SHADER, "VisualizeDepthBuffer/VertexShaderVDB.GLSL", gl);
		VDBProgram.Add(GL4.GL_FRAGMENT_SHADER, "VisualizeDepthBuffer/FragmentShaderVDB.GLSL", gl);
		
		if(!VDBProgram.Create(gl))
			return;
		
		SFBProgram.Add(GL4.GL_VERTEX_SHADER, "ScreenFrameBuffer/VertexShaderSFB.GLSL", gl);
		SFBProgram.Add(GL4.GL_FRAGMENT_SHADER, "ScreenFrameBuffer/FragmentShaderSFB.GLSL", gl);
		
		if(!SFBProgram.Create(gl))
			return;
		
		CMProgram.Add(GL4.GL_VERTEX_SHADER, "cubeMapShader/VertexShaderCM.GLSL", gl);
		CMProgram.Add(GL4.GL_FRAGMENT_SHADER, "cubeMapShader/FragmentShaderCM.GLSL", gl);
		
		if(!CMProgram.Create(gl))
			return;
		
		IProgram.Add(GL4.GL_VERTEX_SHADER, "InstanciateShader/VertexShaderI.GLSL", gl);
		IProgram.Add(GL4.GL_FRAGMENT_SHADER, "InstanciateShader/FragmentShaderI.GLSL", gl);
		
		if(!IProgram.Create(gl))
			return;
		
		SDProgram.Add(GL4.GL_VERTEX_SHADER, "simpleDepthShader/VertexShaderSD.GLSL", gl);
		SDProgram.Add(GL4.GL_FRAGMENT_SHADER, "simpleDepthShader/FragmentShaderSD.GLSL", gl);
		
		if(!SDProgram.Create(gl))
			return;
		
		shaderList.add(program);
		shaderList.add(lightSourceProgram);
		shaderList.add(NLNTProgram);
		shaderList.add(OTProgram);
		shaderList.add(VDBProgram);
		shaderList.add(SFBProgram);
		shaderList.add(CMProgram);
		shaderList.add(IProgram);
		shaderList.add(SDProgram);
		
		uboMatrices = IntBuffer.allocate(1);
		gl.glGenBuffers(1, uboMatrices);
		
		gl.glBindBuffer(GL4.GL_UNIFORM_BUFFER, uboMatrices.array()[0]);
		gl.glBufferData(GL4.GL_UNIFORM_BUFFER, 2*(4*4*4), null, GL4.GL_STATIC_DRAW);
		gl.glBindBuffer(GL4.GL_UNIFORM_BUFFER, 0);
		
		gl.glBindBufferRange(GL4.GL_UNIFORM_BUFFER, 0, uboMatrices.array()[0], 0, 2*(4*4*4));
		
		gl.glUseProgram(shaderList.get(5).getID());
		if(!createFrameBuffer(gl, shaderList.get(5))) 
		{
			System.out.println("[Galaxy Error]: Error while creating the FrameBuffer");
			return;
		}
		
		gl.glUseProgram(shaderList.get(0).getID());	
		Engine.missingTexture = new Texture2D("assets/textures/missing.png", gl, shaderList.get(0), 0, Texture2D.TEXTURE_DIFFUSE, 0.0f);	
		
		//shader needs to be used before he can set uniform variable
		
		lightList = new ArrayList<Light>();
		
		lightList.add(new DirectionLight(new Vector3(1.2f, 1.0f, 2.0f), new Vector3(-0.2f, -1.0f, -0.3f)));
		for(int i = 0; i < pointLightPositions.length; i++) 
		{
			PointLight temp = new PointLight(pointLightPositions[i]);
			
			temp.color = new Vector3(1.0f, 1.0f, 1.0f);
			temp.linear = 0.22f;
			temp.quadratic = 0.20f;
			
			lightList.add(temp);
		}
		
		SpotLight spot = new SpotLight(spotLightPos, spotLightDirection);
		lightList.add(spot);
		
		//WireFrame mode
		//gl.glPolygonMode(GL4.GL_FRONT_AND_BACK, GL4.GL_LINE);
		gl.glPolygonMode(GL4.GL_FRONT_AND_BACK, GL4.GL_FILL);
		
		cam = new Camera(new Vector3(0.0f, 0.0f, 3.0f));
		
		int biggerNumber = Engine.width;
		int smallerNumber = Engine.height;
		if(Engine.width < Engine.height) 
		{
			biggerNumber = Engine.height;
			smallerNumber = Engine.width;
		}
		projection.Perspective(GalacticMath.DegreeToRadiant(45.0f), biggerNumber/smallerNumber, 0.1f, 100.0f);
		
		gl.glBindBuffer(GL4.GL_UNIFORM_BUFFER, uboMatrices.array()[0]);
		gl.glBufferSubData(GL4.GL_UNIFORM_BUFFER, 0, 4*4*4, projection.getBuffer());
		gl.glBindBuffer(GL4.GL_UNIFORM_BUFFER, 0);
		
		//lightList.get(0).color = new Vector3(0.2f, 0.2f, 1.0f);
		lightList.get(0).color = new Vector3(0.0441f, 0.630f, 0.103f);
		
		gl.glUseProgram(shaderList.get(0).getID());
		
		myModel = ModelLoader.load(gl, "backback", shaderList.get(0), true, true);
		
		Texture2D brickWallDiffuse = new Texture2D("assets/textures/brickwall.jpg", gl, shaderList.get(0), 0, Texture2D.TEXTURE_DIFFUSE, 32.0f);
		Texture2D brickWallNormal = new Texture2D("assets/textures/brickwall_normal.jpg", gl, shaderList.get(0), 1, Texture2D.TEXTURE_NORMAL, 32.0f);
		
		brickWall = ModelBuilder.CreatePlane(gl, new Texture2D[] {brickWallDiffuse, brickWallNormal}, true);
		
		Texture2D testBlockDiffuse = new Texture2D("assets/textures/container2.png", gl, shaderList.get(0), 0, Texture2D.TEXTURE_DIFFUSE, 32.0f);
		Texture2D testBlockSpecular = new Texture2D("assets/textures/container2_specular.png", gl, shaderList.get(0), 1, Texture2D.TEXTURE_SPECULAR, 32.0f);
		testblock = ModelBuilder.CreateBlock(gl, new Texture2D[] {testBlockDiffuse, testBlockSpecular});
		testblock.position = new Vector3(2,2,-2);
		
		gl.glUseProgram(shaderList.get(3).getID());
		Texture2D grassTexture = new Texture2D("assets/textures/blending_transparent_window.png", gl, shaderList.get(3), 0, Texture2D.TEXTURE_DIFFUSE, 32.0f);
		windowModel = ModelBuilder.CreatePlane(gl, new Texture2D[] {grassTexture}, false);
		
		Texture2D pointLightTexture = new Texture2D("assets/textures/core/pointLight.png", gl, shaderList.get(3), 0, Texture2D.TEXTURE_DIFFUSE, 32.0f);
		PointLightModel = ModelBuilder.CreatePlane(gl, new Texture2D[] {pointLightTexture}, false);
		
		gl.glUseProgram(shaderList.get(6).getID());
		cUwUbeTexture = new cubeMapTexture(gl, shaderList.get(6) ,new String[] {
				"assets/textures/galaxy/right.png",
				"assets/textures/galaxy/left.png",
				"assets/textures/galaxy/top.png",
				"assets/textures/galaxy/bottom.png",
				"assets/textures/galaxy/front.png",
				"assets/textures/galaxy/back.png"
		});
		
		cUwUbe = ModelBuilder.CreateCubeMapBlock(gl, cUwUbeTexture);
		
		float temptest[] = {
				//Back face
				-0.5f, -0.5f, -0.5f,
		         0.5f,  0.5f, -0.5f,
		         0.5f, -0.5f, -0.5f,
		         0.5f,  0.5f, -0.5f,
			    -0.5f, -0.5f, -0.5f,
		        -0.5f,  0.5f, -0.5f,
		        //Front face
		        -0.5f, -0.5f,  0.5f,
		         0.5f, -0.5f,  0.5f,
		         0.5f,  0.5f,  0.5f,
		         0.5f,  0.5f,  0.5f,
		        -0.5f,  0.5f,  0.5f,
		        -0.5f, -0.5f,  0.5f,
		        //Left face
		        -0.5f,  0.5f,  0.5f,
		        -0.5f,  0.5f, -0.5f,
		        -0.5f, -0.5f, -0.5f,
		        -0.5f, -0.5f, -0.5f,
		        -0.5f, -0.5f,  0.5f,
		        -0.5f,  0.5f,  0.5f,
		        //Right face
		         0.5f,  0.5f,  0.5f,
		         0.5f, -0.5f, -0.5f,
		         0.5f,  0.5f, -0.5f,
		         0.5f, -0.5f, -0.5f,
		         0.5f,  0.5f,  0.5f,
		         0.5f, -0.5f,  0.5f,
		         //Bottom face
		        -0.5f, -0.5f, -0.5f,
		         0.5f, -0.5f, -0.5f,
		         0.5f, -0.5f,  0.5f,
		         0.5f, -0.5f,  0.5f,
		        -0.5f, -0.5f,  0.5f,
		        -0.5f, -0.5f, -0.5f,
		        //Top Face
		        -0.5f,  0.5f, -0.5f,
		         0.5f,  0.5f,  0.5f,
		         0.5f,  0.5f, -0.5f,
		         0.5f,  0.5f,  0.5f,
			    -0.5f,  0.5f, -0.5f,
		        -0.5f,  0.5f,  0.5f  		
			};  
		
		instances = new Instanciator(gl, temptest, 100);
		
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
