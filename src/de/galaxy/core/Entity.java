package de.galaxy.core;

import java.util.ArrayList;

import com.jogamp.opengl.GL4;

import de.galaxy.component.Component;
import de.galaxy.math.Matrix4;
import de.galaxy.math.Vector3;

public abstract class Entity 
{
	private ArrayList<Component> components;
	
	protected Vector3 transform;
	protected Matrix4 model;
	
	public Entity() 
	{
		components = new ArrayList<Component>();
		
		transform = new Vector3(0,0,0);
		model = new Matrix4(1.0f);
	}
	
	protected void AddComponent(Component c) 
	{
		if(!components.contains(c)) 
		{
			components.add(c);
		}
		else 
		{
			System.out.println("[Galaxy Warning]: Component already on Entity");
		}
	}
	
	//every component that needs to be renderd is render here
	public void render(GL4 gl) 
	{
		for(Component c : components)
			c.render(gl);
	}
	
	public void dispose() 
	{
		for(Component c : components)
			c.dispose();
	}
}
