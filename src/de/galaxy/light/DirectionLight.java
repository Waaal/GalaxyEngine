package de.galaxy.light;

import com.jogamp.opengl.GL4;

import de.galaxy.core.ShaderProgram;
import de.galaxy.math.Vector3;

public class DirectionLight extends Light
{	
	public DirectionLight(Vector3 position, Vector3 direction) 
	{
		super(position, direction);
		type = Light.DIRECTION_LIGHT;
	}
	
	@Override
	public void fillShader(GL4 gl, ShaderProgram shader, int number) 
	{
		shader.setVec3(gl, "dirLight["+number+"].direction", direction);
		
		shader.setVec3(gl, "dirLight["+number+"].ambient", color.Mul(ambient));
		shader.setVec3(gl, "dirLight["+number+"].diffuse", color.Mul(diffuse));
		shader.setVec3(gl, "dirLight["+number+"].specular", color);
	}
}
