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
import perlin.Perlinification.PerlinGenMainInfluenceVectorPosibilityIndex;
import perlin.Perlinification.PerlinGenOctInfluenceVectorPosibilityIndex;
import util.Vectornf;

class Main {
    static final int CHUNK_SIZE=4, PIXEL_SIZE=256, MULTI=1;
    static PerlinReturn[][] pixs = new PerlinReturn[CHUNK_SIZE][CHUNK_SIZE];
    static Frame f;
    static Vectornf[] ps = {new Vectornf(1f,0f,0f), new Vectornf(0f,1f,0f), new Vectornf(0f,0f,1f),
    						new Vectornf(1f,0f,1f), new Vectornf(1f,1f,0f), new Vectornf(0f,1f,1f),
    						new Vectornf(1f,1f,1f), new Vectornf(0f,0f,0f)};
//    static Vectornf[] ps = {new Vectornf(0f,0f,0f), new Vectornf(1f, 0f, 0f), new Vectornf(0f, 1f, 0f), new Vectornf(0f,0f,1f)};
//    static PerlinNoise p = new PerlinNoise(PIXEL_SIZE,ps, new PerlinGenMainInfluenceVectorPosibilityIndex() {}, new PerlinGenOctInfluenceVectorPosibilityIndex() {});
    static PerlinNoise p = new PerlinNoise(PIXEL_SIZE, ps, new PerlinGenMainInfluenceVectorPosibilityIndex() {

		@Override
		public int perlinGenMainInfluenceVectorPosibilityIndex(long seed, int mask, int spiralIndex, int cx, int cy) {
			if((cy==1 && (mask == PerlinNoise.TL || mask == PerlinNoise.TR))||cy==0) return 0;
			return -1;
		}
	}, new PerlinGenOctInfluenceVectorPosibilityIndex() {

		@Override
		public int perlinGenOctInfluenceVectorPosibilityIndex(long seed, int mask, int spiralIndex, int cx, int cy) {
			if((cy==1 && (mask == PerlinNoise.TL || mask == PerlinNoise.TR))||cy==0) return 0;
			return -1;
		}
    	
    });
    
    static float max,min;
    
    static int octaves = 3;
    
    static float m = 0.5f;
 
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
	   int seed = 8415;//new Random().nextInt();
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
	public void paint(Graphics g){	
    	System.out.println("paint");
    	for(int cx=0; cx<Main.CHUNK_SIZE;cx++) {for(int cy=0;cy<Main.CHUNK_SIZE;cy++) {
    		for(int px=0; px<Main.PIXEL_SIZE; px++) {for(int py=0; py<Main.PIXEL_SIZE; py++) {
    			int x=Main.PIXEL_SIZE*cx+px;
    			int y=Main.PIXEL_SIZE*cy+py;
    			
    			Vectornf c = Main.pixs[cx][cy].getNormalizedValue(px,py, Main.max, Main.min);
    			
    			float r = c.get(0) > Main.m ? c.get(0) : 0;
    			float g_= c.get(1) > Main.m ? c.get(1) : 0;
    			float b = c.get(2) > Main.m ? c.get(2) : 0;
    			
    			float fr = c.get(0)+c.get(1)+c.get(2)<0.5 ||b>0.5? 1:0;
    			
//    			Color f = new Color(r,g_,b);
//    			Color f = new Color(r,g_,b);
    			Color f = new Color(c.get(0), c.get(1), c.get(2));
   
    			
    			//g.setColor(new Color((float)x/(Main.PIXEL_SIZE*Main.CHUNK_SIZE), (float)y/(Main.PIXEL_SIZE*Main.CHUNK_SIZE),1));
    			g.setColor(f);
    			g.fillRect(x*Main.MULTI, y*Main.MULTI, 1*Main.MULTI, 1*Main.MULTI);
    		}}
    	}}
    	
    	
    }
}