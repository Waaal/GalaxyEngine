package de.galaxy.core;

import de.galaxy.math.Vector3;

public class GameObject extends Entity 
{
	public GameObject(Vector3 transform)
	{
		super();
		this.transform = transform;
		this.Create();
	}
	
	//Gets called on creation
	protected void Create() 
	{
		
	}
	
	//Gets called when added into to entity component system
	protected void Awake() 
	{
		
	}
	
	//gets called once per frame
	protected void Update() 
	{
		
	}
	
	//gets called before destroy
	protected void Destroy() 
	{
		
	}
}
