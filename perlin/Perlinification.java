package perlin;

import util.Vector2f;

class Perlinification {
	
	boolean perlined = false;
	
	Perlinification(){
	}
	
	static final class Masks{
		final float[][]
				TL, TR,
				BL, BR;
		
		final float[][][] m = new float[4][][];
		
		private Masks(PerlinChunk chunk){
			TL = new float[chunk.pixelSize][chunk.pixelSize]; m[PerlinNoise.TL]=TL;
			BL = new float[chunk.pixelSize][chunk.pixelSize]; m[PerlinNoise.BL]=BL;
			TR = new float[chunk.pixelSize][chunk.pixelSize]; m[PerlinNoise.TR]=TR;
			BR = new float[chunk.pixelSize][chunk.pixelSize]; m[PerlinNoise.BR]=BR;
		}
		
	}
	
	static float[][] perlinAChunk(PerlinChunk chunk) {
		Masks chunkMask = new Masks(chunk);
		for(int i=0; i<PerlinNoise.MASKS; i++) {
			perlinAMask(i, chunkMask, chunk);
		}
		
		return chunkMask.m[1];
		
		
	}
	
	private static void perlinAMask(int mask, Masks masks, PerlinChunk chunk) {
		for(int x=0; x<chunk.pixelSize; x++) {
			for(int y=0; y<chunk.pixelSize; y++) {
				PerlinPixel p = chunk.pixels[x][y];
				masks.m[mask][x][y] = Vector2f.dot(p.covecs.vecs[mask], chunk.invecs.vecs[mask]);
			}
		}
	}
}
