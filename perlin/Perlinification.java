package perlin;

import util.Vector2f;

class Perlinification {
	
	
	boolean perlined = false;
	
	static final class Masks{
		final float[][]
				TL, TR,
				BL, BR;
		
		final float[][][] m = new float[4][][];
		
		private Masks(int size){
			TL = new float[size][size]; m[PerlinNoise.TL]=TL;
			BL = new float[size][size]; m[PerlinNoise.BL]=BL;
			TR = new float[size][size]; m[PerlinNoise.TR]=TR;
			BR = new float[size][size]; m[PerlinNoise.BR]=BR;
		}
		
	}
	
	static float[][] perlinAChunk(PerlinChunk chunk) {
		Masks chunkMask = new Masks(chunk.pixelSize);
		for(int i=0; i<PerlinNoise.MASKS; i++) {
			perlinAMask(i, chunkMask, chunk);
		}
		
		float[][] mT = lerpMs(chunkMask.TL, chunkMask.TR, chunk.pixelSize, true);
		float[][] mB = lerpMs(chunkMask.BL, chunkMask.BR, chunk.pixelSize, true);
		float[][] mask = lerpMs(mT, mB, chunk.pixelSize, false);
		
		return mask;
		
	}
	
	private static float[][] lerpMs(float[][] m1, float[][] m2, int size, boolean lr){
		float psize = 1f/size;
		
		float[][] mask = new float[size][size];
		
		for(int x=0;x<size;x++) {
			for(int y=0;y<size;y++) {
				float aProp = (lr?x:y)*psize;
				mask[x][y] = lerp(m1[x][y],m2[x][y], aProp);
			}
		}
		
		return mask;
	}
	
	private static float lerp(float val1, float val2, float aProp) {
		float a = (float)(6*Math.pow(aProp, 5)-15*Math.pow(aProp, 4)+10*Math.pow(aProp, 3));
		return val1+a*(val2-val1);
	}
	
	private static void perlinAMask(int mask, Masks masks, PerlinChunk chunk) {
		for(int x=0; x<chunk.pixelSize; x++) {
			for(int y=0; y<chunk.pixelSize; y++) {
				PerlinPixel p = chunk.pixels[x][y];
				masks.m[mask][x][y] = Vector2f.dot(p.vecs[mask], chunk.invecs[mask]);
			}
		}
	}
}
