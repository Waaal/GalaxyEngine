package de.galaxy.input;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.util.HashMap;

import de.luke.openglTest.Engine;

public class InputHandler 
{
	public static HashMap<Integer, Boolean> keys = new HashMap<Integer, Boolean>();
	
	public static final int KEY_W = 87;
	public static final int KEY_A = 65;
	public static final int KEY_S = 83;
	public static final int KEY_D = 68;
	public static final int KEY_M = 77;
	public static final int KEY_F = 70;
	
	private static float lastX = 300.0f, lastY = 300.0f;
	private static boolean firstMouse = true;
	
	private static Robot robot;
	private static boolean initRobot = false;
	private static boolean moveRobot = false;
	
	public static boolean getKeyPress(int keyCode) 
	{
		if(keys.containsKey(keyCode)) 
			return keys.get(keyCode);
		return false;
	}
	
	public static boolean getKeyJustPress(int keyCode) 
	{
		boolean ret = false;
		if(keys.containsKey(keyCode)) 
		{
			ret = keys.get(keyCode);
			keys.put(keyCode, false);
		}
		return ret;
	}
	
	public static Point getMouseMove() 
	{		
		if(!initRobot) 
		{
			try {
				robot = new Robot();
			} catch (AWTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			initRobot = true;
		}
		
		Point mouseP = MouseInfo.getPointerInfo().getLocation();
		
		if(firstMouse) 
		{
			lastX = mouseP.x;
			lastY = mouseP.y;
			
			firstMouse = false;
		}
		
		float xOffset = mouseP.x - lastX;
		float yOffset = mouseP.y - lastY;
		lastX = mouseP.x;
		lastY = mouseP.y;
		
		if(moveRobot) 
		{
			//was moved by robot, do nothing
			moveRobot = false;
			return new Point(0,0);
		}
		else 
		{
			if(Engine.lockMouse && ((mouseP.x < 10 || mouseP.x > 500) || (mouseP.y < 10 || mouseP.y > 500))) 
			{
				robot.mouseMove(300, 300);	
				moveRobot = true;
			}
			
			xOffset *= 0.1f;
			yOffset *= 0.1f;
			
			Point ret = new Point();
			ret.setLocation(xOffset, yOffset);
			return ret;
		}
	}
}
