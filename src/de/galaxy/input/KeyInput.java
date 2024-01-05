package de.galaxy.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyInput implements KeyListener
{

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) 
	{
		InputHandler.keys.put(e.getKeyCode(), true);
	}

	@Override
	public void keyReleased(KeyEvent e) 
	{
		InputHandler.keys.put(e.getKeyCode(), false);
	}

}
