package main_path;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import perlin.PerlinNoise;
import perlin.PerlinNoise.PerlinReturn;

class Main {
    static final int CHUNK_SIZE=5, PIXEL_SIZE=10;
    static PerlinReturn pixs;
 
    static public void main(String[] args){
    	
    	PerlinNoise p = new PerlinNoise(12314, CHUNK_SIZE, PIXEL_SIZE, false);
        pixs = p.perlin();
        
        Frame f = new Frame( "paint Example" );
        f.add("Center", new MainCanvas());
        f.setSize(new Dimension(CHUNK_SIZE*PIXEL_SIZE*10+22,CHUNK_SIZE*PIXEL_SIZE*10+22));
        f.setVisible(true);
        
        f.addWindowListener (new WindowAdapter() {    
            @Override
			public void windowClosing (WindowEvent e) {    
                f.dispose();    
            }    
        }); 
        
        
   }
}
class MainCanvas extends Canvas
{
    @Override
	public void paint(Graphics g)
    {
    	for(int cx=0; cx<Main.CHUNK_SIZE;cx++) {for(int cy=0;cy<Main.CHUNK_SIZE;cy++) {
    		for(int px=0; px<Main.PIXEL_SIZE; px++) {for(int py=0; py<Main.PIXEL_SIZE; py++) {
    			int x=Main.PIXEL_SIZE*cx+px;
    			int y=Main.PIXEL_SIZE*cy+py;
    			
    			float f = Main.pixs.values()[cx][cy][px][py]; 
    			f = (f-Main.pixs.min())/(Main.pixs.max()-Main.pixs.min());
   
    			
    			//g.setColor(new Color((float)x/(Main.PIXEL_SIZE*Main.CHUNK_SIZE), (float)y/(Main.PIXEL_SIZE*Main.CHUNK_SIZE),1));
    			g.setColor(new Color(f,f,f));
    			g.fillRect(x*10, y*10, 1*10, 1*10);
    		}}
    	}}
    }
}