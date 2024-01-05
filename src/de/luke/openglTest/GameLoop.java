package de.luke.openglTest;

public class GameLoop extends Thread
{
	public boolean running = false;
	public boolean stopped = false;
	
	private long startTime;
	private long timeDelta;
	
	public GameLoop(int targetFps) 
	{
		running = true;
	}
	
	public void run() 
	{
		timeDelta = 1000000000/Engine.targetFps;
		startTime = System.nanoTime();
		while(running) 
		{
			long timeElapsed = System.nanoTime() - startTime;
			if(timeElapsed >= timeDelta) 
			{
				Engine.deltaTime = timeElapsed / 1000000000.0f;
				
				//step animations 
				//entity System
				
				Engine.window.Render();
				
				startTime = System.nanoTime();
			}
		}
		
		stopped = true;
		System.out.println("[Galaxy Message]: Gameloop stopped");
	}
	
	public void end() 
	{
		running = false;
	}
}
