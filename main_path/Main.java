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
import util.Vectornf;

class Main {
    static final int CHUNK_SIZE=4, PIXEL_SIZE=256, MULTI=1;
    static PerlinReturn[][] pixs = new PerlinReturn[CHUNK_SIZE][CHUNK_SIZE];
    static Frame f;
    static Vectornf[] ps = {Vectornf.color(Color.red), Vectornf.color(Color.green), Vectornf.color(Color.blue)};
    static PerlinNoise p = new PerlinNoise(PIXEL_SIZE, ps);
    static float max,min;
    
    static int octaves = 3;
 
    static public void main(String[] args){
    	p.setOctaves(3, 2);
    	Main.perl();
        f = new Frame( "paint Example" );
        f.add("Center", new MainCanvas());
        f.setSize(new Dimension(CHUNK_SIZE*PIXEL_SIZE*Main.MULTI,CHUNK_SIZE*PIXEL_SIZE*Main.MULTI+40));
        f.setVisible(true);
        
        f.addWindowListener (new WindowAdapter() {    
            @Override
			public void windowClosing (WindowEvent e) {    
                f.dispose();    
            }    
        }); 
        
   }
    
   static void perl() {
	   int seed = 6465161;//new Random().nextInt();
	   max = Float.NEGATIVE_INFINITY;
	   min = Float.POSITIVE_INFINITY;
	   long time = System.currentTimeMillis();
	   for(int x=0; x<CHUNK_SIZE; x++) {for(int y=0; y<CHUNK_SIZE; y++) {
		   PerlinReturn pr = p.perlin(seed, x, y);
		   max = Math.max(pr.max(), max);
		   min = Math.min(pr.min(), min);
	       pixs[x][y] = pr;
	   }}
	   long t = System.currentTimeMillis()-time;
	   System.out.println(t);
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
    			
    			Color f = Main.pixs[cx][cy].getNormalizedValue(px,py, Main.max, Main.min).toColor();
   
    			
    			//g.setColor(new Color((float)x/(Main.PIXEL_SIZE*Main.CHUNK_SIZE), (float)y/(Main.PIXEL_SIZE*Main.CHUNK_SIZE),1));
    			g.setColor(f);
    			g.fillRect(x*Main.MULTI, y*Main.MULTI, 1*Main.MULTI, 1*Main.MULTI);
    		}}
    	}}
    }
}