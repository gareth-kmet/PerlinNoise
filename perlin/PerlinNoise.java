package perlin;

import java.util.Random;

import util.Vector2f;

/**
 * Built for static generation
 * @author Gareth Kmet
 *
 */
public final class PerlinNoise {
	
	static final int MASKS=4,
					TL=0, TR=1,
					BL=2, BR=3;
	
	private final Random random;
	private Vector2f[][] influenceVectors;
	private PerlinChunk[][] chunks;
	
	private final boolean perlinOnGen;
	
	private final int csize, psize;
	
	public PerlinNoise(long seed, int csize, int psize, boolean perlinOnGen) {
		random = new Random(seed);
		this.perlinOnGen=perlinOnGen;
		this.csize=csize; this.psize=psize;
		this.genInfluenceVectors(random);
		this.genChunks();
	}
	
	private void genInfluenceVectors(Random random) {
		influenceVectors = new Vector2f[csize+1][csize+1];
		for (int i=0; i<influenceVectors.length; i++) {
			for(int j=0; j<influenceVectors.length; j++)
			influenceVectors[i][j]=Vector2f.fromPolar(1,random.nextFloat(2*(float)(Math.PI)));
		}
	}
	
	private void genChunks() {
		chunks = new PerlinChunk[csize][csize];
		for(int x=0; x<csize; x++) {
			for (int y=0; y<csize; y++) {
				Vector2f 
					tl = influenceVectors[x  ][y  ],
					tr = influenceVectors[x+1][y  ],
					bl = influenceVectors[x  ][y+1],
					br = influenceVectors[x+1][y+1];
				
				PerlinChunk.InfluenceVectors is = new PerlinChunk.InfluenceVectors(tl, tr, bl, br);
				chunks[x][y] = new PerlinChunk(x,y,is,psize);
				
				//TODO if perlin on gen
			}
		}
	}
	
	public PerlinReturn perlin() {
		float max = Float.NEGATIVE_INFINITY, min=Float.POSITIVE_INFINITY;
		
		float[][][][] pixs = new float[csize][csize][][];
		for(PerlinChunk[] cs : chunks) {for(PerlinChunk c:cs) {
			pixs[c.x][c.y] = Perlinification.perlinAChunk(c);
			
			for(float[] fs : pixs[c.x][c.y]) {for(float f:fs) {
				max = Math.max(max, f);
				min = Math.min(min, f);
			}}
			
		}}
		
		return new PerlinReturn(pixs,max,min);
	}
	
	public record PerlinReturn(float[/*cx*/][/*cy*/][/*px*/][/*py*/] values, float max, float min) {};
	
}
