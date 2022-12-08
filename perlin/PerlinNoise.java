package perlin;

import java.util.Random;

import util.Util;
import util.Vector2f;

/**
 * Built for static generation
 * <br>Adapted from <a href="https://www.youtube.com/watch?v=ZsEnnB2wrbI"> this video</a>
 * @author Gareth Kmet
 *
 */
public final class PerlinNoise {
	
	static final int MASKS=4,
					TL=0, TR=1,
					BL=2, BR=3;
	
	private final Random random;
	private final PerlinChunk chunk;
	private long currentSeed = 0;
	
	
	
	public PerlinNoise(int cx, int cy, int psize) {
		random = new Random();
		chunk = new PerlinChunk(cx,cy,psize);
	}
	
	public PerlinReturn perlin(long seed) {
		currentSeed=seed;
		genInfluenceVectors();
		
		
		float max = Float.NEGATIVE_INFINITY, min=Float.POSITIVE_INFINITY;
		float[][] pixs = Perlinification.perlinAChunk(chunk);
		
		for(float[] fs : pixs) {for(float f:fs) {
			max = Math.max(max, f);
			min = Math.min(min, f);
		}}
		
		return new PerlinReturn(pixs,max,min);
	}
	
	private void genInfluenceVectors() {
		int[] index = genInfluenceVectorIndecies();
		
		Vector2f[] vecs = new Vector2f[4];
		for(int i=0; i<4; i++) {
			float f = Util.getRandomFloatAtIndex(index[i], random, currentSeed, 2*(float)Math.PI);
			vecs[i] = Vector2f.fromPolar(1, f);
		}
		
		chunk.setInfluencevectors(vecs);
	}
	
	private int[] genInfluenceVectorIndecies() {
		
		int x = chunk.x, y=chunk.y;
		int[] i = new int[MASKS];
		i[TL] = Util.pointToSpiral(x  , y  );
		i[TR] = Util.pointToSpiral(x+1, y  );
		i[BL] = Util.pointToSpiral(x  , y+1);
		i[BR] = Util.pointToSpiral(x+1, y+1);
		
		return i;
		
	}
	
	
	public record PerlinReturn(float[][] values, float max, float min) {
		public float getNormalizedValue(int px, int py) {
			return getNormalizedValue(px,py,max,min);
		}
		public float getNormalizedValue(int px, int py, float max, float min) {
			return (values[px][py]-min)/(max-min);
		}
	};
	
}
