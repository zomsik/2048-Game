
package com.mycompany.game2048;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener {
	
	public boolean[] keys;
	public boolean up, down, left, right;
	
	public Keyboard(){
		keys = new boolean[256];

	}
        
        public void tick(){
                up = keys[KeyEvent.VK_W];
		down = keys[KeyEvent.VK_S];
		left = keys[KeyEvent.VK_A];
		right = keys[KeyEvent.VK_D];
        }
	
	@Override
	public void keyPressed(KeyEvent e) {

		keys[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

}