package de.galaxy.core;

import com.jogamp.opengl.GL4;

public abstract class Texture 
{
	protected float highlightFocus = 1.0f;
	
	public abstract void bind(GL4 gl);
	public abstract void dispose(GL4 gl);
}
