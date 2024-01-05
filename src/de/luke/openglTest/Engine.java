package de.luke.openglTest;

public class Engine 
{
	public static Window window;
	
	public static String title = null;
	
	public static int targetFps;
	public static int width;
	public static int height;
	
	private static boolean initComplete = false;
	
	public static GameLoop gameThread;
	
	public static boolean lockMouse = true;
	
	public static float deltaTime = 0.0f;
	
	public static Texture2D missingTexture;
	
	static void init(int width, int height, int targetFps) 
	{
		Engine.width = width;
		Engine.height = height;
		Engine.targetFps = targetFps;
		
		gameThread = new GameLoop(targetFps);
		window = new Window();
		
		Engine.initComplete = true;
	}
	
	static void run() 
	{
		if(!initComplete) 
		{
			init(600, 600, 30);
			System.out.println("[Galaxy Warning]: Engine given no init parameter. Init was done with default parameter");
		}
		
		gameThread.start();
		System.out.println("[Galaxy Message]: Game thread started");
	}
}
