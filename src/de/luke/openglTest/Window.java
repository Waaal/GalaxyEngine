package de.luke.openglTest;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;

import de.galaxy.input.KeyInput;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Window
{
	
	private JFrame frame;
	
	private GLProfile profile;
	private GLCapabilities capabilities;
	private GLCanvas glcanvas;
	
	private Renderer renderer;
	
	public Window() 
	{
		String title = Engine.title;
		if(title == null)
			title = "Galaxy Engine";
		
		profile = GLProfile.get(GLProfile.GL4);
		capabilities = new GLCapabilities(profile);
		
		capabilities.setHardwareAccelerated(true);
		capabilities.setDoubleBuffered(true);
		
		glcanvas = new GLCanvas(capabilities);
		
		renderer = new Renderer();
		glcanvas.addKeyListener(new KeyInput());
		glcanvas.addGLEventListener(renderer);
		glcanvas.setSize(Engine.width, Engine.height);
		
		glcanvas.setVisible(true);
			
		frame = new JFrame(title);
		
		frame.getContentPane().add(glcanvas);
		frame.setSize(frame.getContentPane().getPreferredSize());
		frame.setVisible(true);
		
		BufferedImage cursorImg = new BufferedImage(16,16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0,0), "blank cursor");
		frame.getContentPane().setCursor(blankCursor);
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) 
			{
				Engine.gameThread.end();
				while(!Engine.gameThread.stopped) 
				{
				}
				glcanvas.destroy();
				System.exit(0);
			}
		});
	}
	
	public void Render() 
	{
		glcanvas.display();
	}
}
