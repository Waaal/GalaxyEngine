package de.galaxy.core;

public class VertexAttribPointer 
{
	public int index;
	public int size;
	public int type;
	public boolean normalize;
	public int stride;
	public int pointer;
	public int divisor = 0;
	
	public VertexAttribPointer(int index, int size, int type, boolean normalize, int stride, int pointer) 
	{
		this.index = index;
		this.size = size;
		this.type = type;
		this.normalize = normalize;
		this.stride = stride;
		this.pointer = pointer;
	}
	
	public VertexAttribPointer(int index, int size, int type, boolean normalize, int stride, int pointer, int divisor) 
	{
		this.index = index;
		this.size = size;
		this.type = type;
		this.normalize = normalize;
		this.stride = stride;
		this.pointer = pointer;
		this.divisor = divisor;
	}
}
