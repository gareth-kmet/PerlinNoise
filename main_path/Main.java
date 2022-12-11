package main_path;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

import perlin.PerlinNoise;
import perlin.PerlinNoise.PerlinReturn;
import perlin.Perlinification.PerlinInfluenceGenerator;
import util.Vectornf;

class Main {
    static final int CHUNK_SIZE=4, PIXEL_SIZE=256, MULTI=1, R=1;
    static PerlinReturn[][] pixs;
    static Frame f = new Frame();
    static {
    	f.setLayout(new GridLayout(R,R));
    }
    static long seed = new Random().nextInt();
    static {
    	System.out.println(seed);
    }
    static PerlinNoise p = new PerlinNoise(PIXEL_SIZE, Vectornf.genStandardVectors(3), new PerlinInfluenceGenerator() {
		@Override
		public Vectornf perlinMainInfluenceVector(long seed, int spiralIndex, int cx, int cy, int mask) {
			if((cy==0 && (mask == PerlinNoise.TL || mask==PerlinNoise.TR))) {
				return new Vectornf(0f,0f,0f);
			}
			return null;
		}
		
		@Override
		public Vectornf perlinOctInfluenceVector(long seed, int spiralIndex, int mask, PerlinNoise.PerlinOctaveChunkData octData) {
			boolean b = octData.forEach(t -> {return t.ry()==0;});
			if(b && (mask == PerlinNoise.TL || mask == PerlinNoise.TR)) {
				return new Vectornf(0f,0f,0f);
			}
			return null;
		}
	});
    
    static float max,min,mmax;
    
    static float per = 0.5f;
    static int oct = 2;
    static int lac = 1;
    
    public static void main(String[] args) {
    	
    	f.setSize(new Dimension(CHUNK_SIZE*PIXEL_SIZE*MULTI*R,CHUNK_SIZE*PIXEL_SIZE*MULTI*R+40));
        
        /*
		for(oct=1; oct<=3; oct++) {
			for(lac=1; lac<=3; lac++) {
				if(lac==3)lac=4;
				mainy();
			}
		}
		*/
    	mainy();
		f.setVisible(true);
        
        
        f.addWindowListener (new WindowAdapter() {    
            @Override
			public void windowClosing (WindowEvent e) {    
                f.dispose();    
            }    
        }); 
	}
 
    static public void mainy(){
    	p.setOctaves(oct, lac, per);
    	Main.perl();
        
    	Canvas c = new MainCanvas(mmax,pixs);
        f.add(c);
        
        
         
   }
   
   static void perl() {
	   pixs= new PerlinReturn[CHUNK_SIZE][CHUNK_SIZE];
	   max = Float.NEGATIVE_INFINITY;
	   min = Float.POSITIVE_INFINITY;
	   long time = System.currentTimeMillis();
	   for(int x=0; x<CHUNK_SIZE; x++) {for(int y=0; y<CHUNK_SIZE; y++) {
		   PerlinReturn pr = p.perlin(seed, x, y);
//		   System.out.println(pr.max()+"   "+pr.min());
		   max = Math.max(pr.max(), max);
		   min = Math.min(pr.min(), min);
	       pixs[x][y] = pr;
	   }}
	   mmax = Math.max(Math.abs(max), Math.abs(min));
	   System.out.println(max+" "+min+" "+(Math.sqrt(1/2)*(1f-Math.pow(per, oct))/(1f-per)));
	   long t = System.currentTimeMillis()-time;
	   System.out.println(t);
   }
}

class MainCanvas extends Canvas{
	
	static final float B=0.007f;
	
	PerlinReturn[][] pixs; float mmax;

	public MainCanvas(float mmax, PerlinReturn[][] pixs){
		this.mmax=mmax;this.pixs=pixs;
	}
    @Override
	public void paint(Graphics g){	
    	System.out.println("paint");
    	for(int cx=0; cx<Main.CHUNK_SIZE;cx++) {for(int cy=0;cy<Main.CHUNK_SIZE;cy++) {
    		for(int px=0; px<Main.PIXEL_SIZE; px++) {for(int py=0; py<Main.PIXEL_SIZE; py++) {
    			
    			if(px==0 || py==0 || px==255 || py==255) {
//    				continue;
    			}
    			
    			int x=Main.PIXEL_SIZE*cx+px;
    			int y=Main.PIXEL_SIZE*cy+py;
    			
    			Vectornf ct = pixs[cx][cy].values()[px][py];
    	
    			
    			float r = Math.abs(ct.get(0))/mmax;
    			float g_= Math.abs(ct.get(1))/mmax;
    			float b = Math.abs(ct.get(2))/mmax;
    			/*
    			
    			float r = ct.get(0) > 0 ? ct.get(0)/mmax : 0;
    			float g_= ct.get(1) > 0 ? ct.get(1)/mmax : 0;
    			float b = ct.get(2) > 0 ? ct.get(2)/mmax : 0;
    			*/
    			/*
    			float f = ct.get(0);
    			float r = f>B?f:0;
    			float g_=Math.abs(f)<B?1:0;
    			float b = f<-B?-f:0;
    			*/
    			/*
    			float r = Math.abs(ct.get(0));
    			float g_ = Math.abs(ct.get(1));
    			float b = Math.abs(ct.get(2));
    			*/
    			
//    			g.setColor(new Color(r,g_,b));
    			g.setColor(new Color(r,g_,b));
    			
    			g.fillRect(x*Main.MULTI, y*Main.MULTI, 1*Main.MULTI, 1*Main.MULTI);
    		}}
    	}}
    	
    	
    }
}