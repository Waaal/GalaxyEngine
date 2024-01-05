package de.galaxy.light;

import com.jogamp.opengl.GL4;

import de.galaxy.core.ShaderProgram;
import de.galaxy.math.Vector3;

public class PointLight extends Light
{
	public float linear = 0.14f;
	public float quadratic = 0.07f;
	
	public PointLight(Vector3 position) 
	{
		super(position);
		ambient = 0.0f;
		type = Light.POINT_LIGHT;
	} 
	
	public PointLight(Vector3 position, Vector3 direction) 
	{
		super(position, direction);
		ambient = 0.0f;
		type = Light.POINT_LIGHT;
	} 
	
	@Override
	public void fillShader(GL4 gl, ShaderProgram shader, int number) 
	{
		shader.setVec3(gl, "pointLights["+number+"].position", position);
		
		shader.setFloat(gl, "pointLights["+number+"].linear", linear);
		shader.setFloat(gl, "pointLights["+number+"].quadratic", quadratic);
		
		shader.setVec3(gl, "pointLights["+number+"].ambient", color.Mul(ambient));
		shader.setVec3(gl, "pointLights["+number+"].diffuse", color.Mul(diffuse));
		shader.setVec3(gl, "pointLights["+number+"].specular", color);
	}
}
