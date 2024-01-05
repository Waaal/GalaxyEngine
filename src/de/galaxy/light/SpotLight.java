package de.galaxy.light;

import com.jogamp.opengl.GL4;

import de.galaxy.core.ShaderProgram;
import de.galaxy.math.GalacticMath;
import de.galaxy.math.Vector3;

public class SpotLight extends PointLight
{
	public SpotLight(Vector3 position, Vector3 direction) 
	{
		super(position, direction);
		type = Light.SPOT_LIGHT;
	}

	public float cutOff = (float)Math.cos(GalacticMath.DegreeToRadiant(12.5f));
	public float outerCutOff = (float)Math.cos(GalacticMath.DegreeToRadiant(17.5f));

	public void setCutOff(float degree)
	{
		cutOff = (float)Math.cos(GalacticMath.DegreeToRadiant(degree));
	}

	public void setOuterCutOff(float degree)
	{
		outerCutOff = (float)Math.cos(GalacticMath.DegreeToRadiant(degree));
	}
	
	@Override
	public void fillShader(GL4 gl, ShaderProgram shader, int number) 
	{
		shader.setVec3(gl, "spotLight["+number+"].position", position);
		shader.setVec3(gl, "spotLight["+number+"].direction", direction);
		
		shader.setFloat(gl, "spotLight["+number+"].linear", linear);
		shader.setFloat(gl, "spotLight["+number+"].quadratic", quadratic);
		
		shader.setFloat(gl, "spotLight["+number+"].cutOff", cutOff);
		shader.setFloat(gl, "spotLight["+number+"].outerCutOff", outerCutOff);
		
		shader.setVec3(gl, "spotLight["+number+"].ambient", color.Mul(ambient));
		shader.setVec3(gl, "spotLight["+number+"].diffuse", color.Mul(diffuse));
		shader.setVec3(gl, "spotLight["+number+"].specular", color);
	}
}
